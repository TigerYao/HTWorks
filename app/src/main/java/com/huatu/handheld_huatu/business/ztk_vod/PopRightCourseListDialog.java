package com.huatu.handheld_huatu.business.ztk_vod;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.PlayLessionAdapter;
import com.huatu.handheld_huatu.base.BaseRefreshlist;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.business.ztk_vod.bean.VodCoursePlayBean;

import com.huatu.handheld_huatu.listener.DetachableDialogShowListener;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.WarpListResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommSmallLoadingView;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.library.PullToRefreshBase;
import com.huatu.library.PullVerRefreshRecyclerView;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\7\19 0019.
 */

public class PopRightCourseListDialog extends BaseRefreshlist<WarpListResponse<CourseWareInfo>>
        implements OnRecItemClickListener ,PullToRefreshBase.OnRefreshListener<RecyclerView>,DialogInterface.OnShowListener {

    CompositeSubscription mCompositeSubScrip;

    protected PullVerRefreshRecyclerView mRecyclerView;
    protected CommSmallLoadingView mLoadingView;
    private Dialog mDialog;
    private View mRootView;

    private Context mContext;
    private long curRid;

    @Override
    protected   CommSmallLoadingView getLoadingView(){
        return mLoadingView;
    }

    private PlayLessionAdapter mLessionAdapter;
    private OnCoursePlaylistener onSubItemClickListener;

    DetachableDialogShowListener mShowListener;

    private int mDelaySelectId=-1;

    public void setOnSubItemClickListener(OnCoursePlaylistener onSubItemClickListener) {
        this.onSubItemClickListener = onSubItemClickListener;
    }

    public PopRightCourseListDialog(Context context, long rid,CompositeSubscription compositeSubscrip) {
       // super(context, R.style.ActionhorizontalDialogStyle);
        this.mContext = context;
        this.curRid = rid;
        this.mCompositeSubScrip=compositeSubscrip;
        mListResponse =new WarpListResponse<CourseWareInfo>();
        mListResponse.mAdapterList=new ArrayList<CourseWareInfo>();
        mLessionAdapter=new PlayLessionAdapter(mContext,mListResponse.mAdapterList);
    }

    public void setCourseWarelist(List<CourseWareInfo> courseWarelist){
        mLessionAdapter.refresh(courseWarelist);
        if(ArrayUtils.size(courseWarelist)<getLimit()) mRecyclerView.setCanPull(false);
    }

    public PopRightCourseListDialog builder() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.play_course_list_layout, null);
        //view.setMinimumWidth(DensityUtils.dp2px(mContext,165));
        mRootView.setMinimumHeight(DisplayUtil.getScreenWidth()+DensityUtils.getStatusHeight(mContext));

        mRecyclerView = (PullVerRefreshRecyclerView) mRootView.findViewById(R.id.lessionListView);
        mRecyclerView.getRefreshableView().setPadding(0,DensityUtils.dp2px(mContext,40),0,0);
        mRecyclerView.getRefreshableView().setClipToPadding(false);
        mRecyclerView.setOnRefreshListener(this);
        // mMenuTypeView = (LinearLayoutListView)view.findViewById(R.id.xi_comm_menu_type_list);
        mLoadingView = (CommSmallLoadingView)mRootView.findViewById(R.id.xi_layout_loading);
        mLoadingView.setOnRefreshClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetry();
            }
        });

        mDialog = new Dialog(mContext, R.style.ActionhorizontalDialogStyle);
        mDialog.setContentView(mRootView);
        mShowListener=DetachableDialogShowListener.wrap(this);
        mDialog.setOnShowListener(mShowListener);

        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =0;// 0;
        dialogWindow.setAttributes(lp);
        mRecyclerView.setAdapter(mLessionAdapter);
        mLessionAdapter.setOnViewItemClickListener(this);
        return this;
    }

    public PopRightCourseListDialog setCancelable(boolean cancel) {
        mDialog.setCancelable(cancel);
        return this;
    }

    public PopRightCourseListDialog setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public void show() {
        mDialog.show();
    }

    public void checkHasClose(){
        if(null!=mDialog&&(mDialog.isShowing())){
           mDialog.dismiss();
        }
    }

    public void destory(){
         if(null!=mShowListener) {
             mShowListener.clear();
             mShowListener=null;
         }
    }

    @Override
    protected   void notifyDataSetChanged(){
        mLessionAdapter.notifyDataSetChanged();   }

    @Override
    protected   void checkMoreAndHideLoad(int size){
        mRecyclerView.onRefreshComplete();
        if(size<getLimit()) {
            mRecyclerView.setCanPull(false);
        }
    }

    @Override
    public  boolean isFragmentFinished(){
        if(mContext==null) return true;
        if(mContext instanceof Activity){
            return ((Activity)mContext).isFinishing();
        }
        return false;
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        //new GetDataTask().execute();
        OnLoadMoreEvent();
    }/**/

    @Override
    protected   void  onLoadData(int pageIndex, int limit){
         CourseApiService.getApi().getAllCourseWarebyPage(curRid,pageIndex,limit).enqueue(getCallback());
    }

    @Override
    public void onSuccess(WarpListResponse<CourseWareInfo> response){
        super.onSuccess(response);
        if(!isCurrentRefreshMode()){
            if(isFragmentFinished()) return;
            if(onSubItemClickListener!=null){
                onSubItemClickListener.getRecordList().addAll(response.getListResponse());
               // LogUtils.e("onSuccess", GsonUtil.GsonString(((BJRecordPlayActivity)mContext).getRecordList()));
            }
        }
    }

    @Override
    public  void onItemClick(int position,View view,int type){
       if(onSubItemClickListener!=null)
           onSubItemClickListener.onSelectPlayClick(mLessionAdapter.getItem(position),false);
    }

    public void setCurrentSelectId(int id){
        mLessionAdapter.setSelectId(id);
    }

    @Override
    public void onShow(DialogInterface dialog){
        if(mDelaySelectId!=-1){
            setCurrentSelectId(mDelaySelectId);
            mDelaySelectId=-1;
        }
    }

    public void setDelaySelectId(int id){
        mDelaySelectId=id;
    }
}




 /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.play_course_list_layout, null);
        rootView.setMinimumHeight(DisplayUtil.getScreenWidth()+DensityUtils.getStatusHeight(mContext));
        setContentView(rootView);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
        // dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =0;//DensityUtils.getStatusHeight(mContext);// 0;
        dialogWindow.setAttributes(lp);

        //dialogWindow.setWindowAnimations(R.style.popup_anim_bottom2);
        lessionListView=(RecyclerView) this.findViewById(R.id.lessionListView);
        lessionListView.setLayoutManager(new LinearLayoutManager(mContext));

        lessionListView.setAdapter(mLessionAdapter);
        mLessionAdapter.setOnViewItemClickListener(this);

    }*/