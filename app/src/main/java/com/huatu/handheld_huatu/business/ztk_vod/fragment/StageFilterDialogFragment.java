package com.huatu.handheld_huatu.business.ztk_vod.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CatalogStageAdapter;
import com.huatu.handheld_huatu.base.AbsDialogFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.listener.DetachableDialogDismissListener;
import com.huatu.handheld_huatu.listener.DetachableDialogShowListener;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.CourseStageBean;
import com.huatu.handheld_huatu.mvpmodel.SyllabusClassesBean;
import com.huatu.handheld_huatu.mvpmodel.TeacherBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;


/**
 * Created by cjx on 2018\7\19 0019.
 */


public class StageFilterDialogFragment extends AbsDialogFragment implements DialogInterface.OnShowListener,OnRecItemClickListener {

    CommloadingView mCommloadingView;
    private String mCourseId;
    private int    mSelectNodeId;
    private int    mCurrentType;
    private int    mTeacherId;
    private String mStageId;



    public static StageFilterDialogFragment getInstance(String courseId,int selNodeId,String stageId,String teacherId,int type) {//0 阶段 ,1 课程目录 ，2 老师
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putInt(ArgConstant.TYPE,type);
        args.putInt(ArgConstant.KEY_ID,selNodeId);
        args.putString(ArgConstant.TYPE_ID,stageId);
        args.putInt(ArgConstant.TITLE,StringUtils.parseInt(teacherId));
        StageFilterDialogFragment tmpFragment = new StageFilterDialogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }
    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }
    DetachableDialogShowListener mShowListener;


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=mShowListener){
            mShowListener.clear();
            mShowListener=null;
        }
         RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }



    CatalogStageAdapter mListAdapter;
    protected void parserParams(Bundle args) {
        mCourseId = args.getString(ArgConstant.COURSE_ID);
        mSelectNodeId = args.getInt(ArgConstant.KEY_ID,0);
        mCurrentType=args.getInt(ArgConstant.TYPE,0);
        mStageId=args.getString(ArgConstant.TYPE_ID);
        mTeacherId=args.getInt(ArgConstant.TITLE,0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parserParams(getArguments());
        }
    }

/*    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //复写子类的侦听
        Dialog dialog= getDialog();
        if(null!=dialog){
            mDismissListener=DetachableDialogDismissListener.wrap(this);
           // dialog.setOnCancelListener(null);
            dialog.setOnDismissListener(mDismissListener);
        }
     }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    RecyclerViewEx mLettersLayout;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.play_filter_list_layout, null);
        Dialog dialog = new Dialog(getActivity(), R.style.DimThemeDialogPopup );

        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        mLettersLayout = (RecyclerViewEx) view.findViewById(R.id.xi_comm_page_list);

        mShowListener= DetachableDialogShowListener.wrap(this);
        dialog.setOnShowListener(mShowListener);


        TextView titleView= view.findViewById(R.id.filter_title_txt);
        if(mCurrentType==0){
            titleView.setText("阶段筛选");
        }else if(mCurrentType==1){
            titleView.setText("课程筛选");
        }
        else if(mCurrentType==2){
            titleView.setText("老师筛选");
        }

        view.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mCommloadingView=view.findViewById(R.id.xi_layout_loading);

        mCommloadingView.setEmptyImg(R.drawable.no_data_bg);//course_no_cache_icon
        mCommloadingView.setStatusStringId(R.string.xs_loading_text,R.string.xs_error_message);
        mCommloadingView.setTipText(R.string.xs_retry_message);
       mCommloadingView.disableDetachedFromWindow();

        mCommloadingView.findViewById(R.id.xi_tv_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(GONE);
                onShow(null);
                //loadData(mPageIndex);
            }
        });

        if(mCurrentType==2){
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),4);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(mListAdapter.getItemCount()>1)
                      return position==0?4:1;
                    return 1;
                }
            });
            mLettersLayout.setLayoutManager(layoutManager);

        }else {
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            mLettersLayout.setLayoutManager(manager);
        }

        mLettersLayout.setPagesize(100000);

        mListAdapter = new CatalogStageAdapter(getActivity(), new ArrayList<SyllabusClassesBean>(),mCurrentType==2);
         mListAdapter.setOnViewItemClickListener(this);
        mLettersLayout.setRecyclerAdapter(mListAdapter);
        //dialogWindow.setWindowAnimations(R.style.popup_anim_bottom2);
      /*  WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =DensityUtils.dp2px(mContext,60);// 0;
        dialogWindow.setAttributes(lp);*/
        return dialog;
    }

    @Override
    public void onItemClick(int position,View view,int type){

        SyllabusClassesBean curItem= mListAdapter.getItem(position);
        if(null==curItem) return;


        if(getActivity() instanceof BJRecordPlayActivity){
            if(mCurrentType==1||mCurrentType==0){
                String strClassNodeIds=curItem.nodeId>0 ? curItem.nodeId+"_"+curItem.name:"";
                ((BJRecordPlayActivity)getActivity()).onCatalogSelect("","",strClassNodeIds,mCurrentType);
            }
            else if(mCurrentType==2){//2 老师
                ((BJRecordPlayActivity)getActivity()).onCatalogSelect(curItem.nodeId>0?String.valueOf(curItem.nodeId):"",curItem.name,"",mCurrentType);
            }
        }
        dismiss();
    }


    private void showAllTeacher(){
        mCommloadingView.showLoadingStatus();
        ServiceExProvider.visitList(getSubscription(), CourseApiService.getApi().getSyllabusTeachers(StringUtils.parseInt(mCourseId),mSelectNodeId<=0?"":(mSelectNodeId+""),mStageId),//StringUtils.parseInt(mCourseId)
                new NetListResponse<TeacherBean>() {
                    @Override
                    public void onSuccess(BaseListResponseModel<TeacherBean> model) {
                        if(null!=mCommloadingView) {
                            mCommloadingView.hide();
                        }
                        List<SyllabusClassesBean> tmpList=new ArrayList<>();
                        if(ArrayUtils.size(model.data)>1){
                            SyllabusClassesBean tmpBean=new SyllabusClassesBean();
                            tmpBean.name="全部老师";
                            tmpList.add(0,tmpBean);
                        }
                        for(TeacherBean bean:model.data){
                            SyllabusClassesBean tmpBean=new SyllabusClassesBean();
                            tmpBean.nodeId=bean.teacherId;
                            tmpBean.name=bean.teacherName;
                            tmpList.add(tmpBean);

                        }
                        mListAdapter.addAll(tmpList);
                        setSelectNode(mTeacherId);
                    }

                    @Override
                    public void onError(String message, int type) {
                        if(null!=mCommloadingView) {
                            mCommloadingView.showEmptyStatus();
                        }
                    }
                });
     }


    private void setSelectNode(int selectNodeId){
        if(selectNodeId>0){
            int postion=-1;
            for(int i=0;i<mListAdapter.getAllItems().size();i++){
                if(mListAdapter.getItem(i).nodeId==selectNodeId){
                    postion=i;
                    mListAdapter.setSelectPostion(postion);
                    mListAdapter.notifyItemChanged(postion);
                    break;
                }
            }
            if(postion!=-1&&(mLettersLayout!=null)){
                LinearLayoutManager mLayoutManager = (LinearLayoutManager) mLettersLayout.getLayoutManager();
                int absPostion=0;
                if(postion>4){
                    absPostion=Math.min(mListAdapter.getItemCount()-postion,postion);
                }
                mLayoutManager.scrollToPositionWithOffset(postion,absPostion>4? DensityUtils.dp2px(getContext(), 60):0);
            }
        }
    }

    @Override
    public void onShow(DialogInterface dialog){
        if(mCurrentType==2){
            showAllTeacher();
            return;
        }
        boolean canShow=(mCurrentType==0&&(!TextUtils.isEmpty(mStageId)))||(mCurrentType==1&&TextUtils.isEmpty(mStageId));

        if(canShow&&(getActivity() instanceof BJRecordPlayActivity)){
            mCommloadingView.hide();
            List<SyllabusClassesBean> tmplist= ((BJRecordPlayActivity)getActivity()).mFilterSyllabusClass;
         /*   if(mCurrentType!=0&&(ArrayUtils.size(tmplist)>1)){
                SyllabusClassesBean tmpBean=new SyllabusClassesBean();
                tmpBean.name=mCurrentType==1? "全部课程":"全部老师";
                tmplist.add(0,tmpBean);
            }*/
            mListAdapter.addAll(tmplist);
            setSelectNode(mCurrentType==0?StringUtils.parseInt(mStageId):mSelectNodeId);
            return;

        }
        mCommloadingView.showLoadingStatus();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getSyllabusClasses(mCourseId,mStageId),//StringUtils.parseInt(mCourseId)
                new NetObjResponse<CourseStageBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<CourseStageBean> model) {
                        if(null!=mCommloadingView) {
                            mCommloadingView.hide();
                        }
                        if(mCurrentType!=0&&(ArrayUtils.size(model.data.list)>1)){
                            SyllabusClassesBean tmpBean=new SyllabusClassesBean();
                            tmpBean.name=mCurrentType==1? "全部课程":"全部老师";
                            model.data.list.add(0,tmpBean);
                        }
                        mListAdapter.addAll(model.data.list);
                        setSelectNode(mSelectNodeId);
                     }

                    @Override
                    public void onError(String message, int type) {
                        if(null!=mCommloadingView) {
                            mCommloadingView.showEmptyStatus();
                        }
                    }
                });

    }

}
