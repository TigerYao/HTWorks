package com.huatu.handheld_huatu.business.me.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.course.CourseCollectlistAdapter;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.OnSwitchListener;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.CourseCollectBean;
import com.huatu.handheld_huatu.mvpmodel.WarpListResponse;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\8\6 0006.
 */

public class CourseCollectListFragment extends ABaseListFragment<WarpListResponse<CourseCollectBean>> implements OnRecItemClickListener, OnSwitchListener ,AStripTabsFragment.IStripTabInitData{
    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView mWorksListView;


    @BindView(R.id.deleteAll_btn)
    TextView mDelBtn;


    @BindView(R.id.selAll_btn)
    TextView mSelAllBtn;

    @BindView(R.id.bottom_action_layout)
    LinearLayout mAction_layout;

    CustomConfirmDialog mConfirmDialog;

    int mSelectNum = 0;
    public int getContentView() {
        return R.layout.course_mycollect_list_layout;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return mWorksListView.getRefreshableView();
    }

    CourseCollectlistAdapter mListAdapter;

    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    private String mCourseId = "";
    private int mCourseType=0;


    public static void lanuch(Context context) {

       /* Bundle arg = new Bundle();
        arg.putString(ArgConstant.COURSE_ID, courseId);
        arg.putInt(ArgConstant.TYPE,courseType);*/
        UIJumpHelper.jumpFragment(context, CourseCollectListFragment.class);
    }



    @Override
    public int isEditMode() {
        if (mListAdapter.getItemCount() <= 0) return 2;
        return mListAdapter.isEditMode() ? 1 : 0;
    }

    @Override
    public void switchMode() {

        if(mListAdapter.isEditMode()) {//当前删除模式->normal
            mSelectNum=0;
            mSelAllBtn.setText("全选");
            mDelBtn.setText("删除");
            //mRightMenu.setText(R.string.edit);
            for (CourseCollectBean curLession : mListAdapter.getAllItems()) {
                curLession.setSelect(false);
            }
            mListAdapter.setActionMode(false);
            mAction_layout.setVisibility(View.GONE);

            RelativeLayout.LayoutParams tmpParam=   (RelativeLayout.LayoutParams)mWorksListView.getLayoutParams();
            tmpParam.bottomMargin=0;
            mWorksListView.setLayoutParams(tmpParam);
            if(mOnSwipeItemTouchListener!=null)
                mOnSwipeItemTouchListener.setCanSwipe(true);
        }
        else {//当前normal->删除模式
           // mRightMenu.setText(R.string.pickerview_cancel);
            mAction_layout.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams tmpParam=   (RelativeLayout.LayoutParams)mWorksListView.getLayoutParams();
            tmpParam.bottomMargin=DensityUtils.dp2px(getContext(),45);
            mWorksListView.setLayoutParams(tmpParam);
            mListAdapter.setActionMode(true);

            if(mOnSwipeItemTouchListener!=null)
                mOnSwipeItemTouchListener.setCanSwipe(false);
        }
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
       // setHasOptionsMenu(true);
       // setHomeAsUpEnabled(true);
       // setTitle("我的收藏");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListResponse = new WarpListResponse<CourseCollectBean>();
        mListResponse.mAdapterList = new ArrayList<>();
        mListAdapter = new CourseCollectlistAdapter(getContext(), mListResponse.mAdapterList);
        mListAdapter.setOnViewItemClickListener(this);
    }

/*    MenuItem mRightMenu;
    public MenuItem getRightMenu() {
        return mRightMenu;
    }

    @Override
    public void onCreateTitleBarMenu(TitleBar titleBar, ViewGroup container) {
        super.onCreateTitleBarMenu(titleBar, container);
        mRightMenu = new MenuItem(getContext());
        mRightMenu.setText(R.string.edit);
        mRightMenu.setId(android.R.id.button1);
        mRightMenu.setVisibility(View.GONE);
        titleBar.add(mRightMenu);
    }*/

    /*@Override
    public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
        super.onMenuClicked(titleBar, menuItem);
        if (menuItem.getId() == android.R.id.button1) {
            if(mListAdapter.getItemCount()<=0){
                ToastUtils.showShortToast(getContext(),"暂无内容~");
                return;
            }

            if(mListAdapter.isEditMode()) {//当前删除模式->normal
                mSelectNum=0;
                mSelAllBtn.setText("全选");
                mDelBtn.setText("删除");
                mRightMenu.setText(R.string.edit);
                for (CourseCollectBean curLession : mListAdapter.getAllItems()) {
                    curLession.setSelect(false);
                }
                mListAdapter.setActionMode(false);
                mAction_layout.setVisibility(View.GONE);

                RelativeLayout.LayoutParams tmpParam=   (RelativeLayout.LayoutParams)mWorksListView.getLayoutParams();
                tmpParam.bottomMargin=0;
                mWorksListView.setLayoutParams(tmpParam);
                if(mOnSwipeItemTouchListener!=null)
                    mOnSwipeItemTouchListener.setCanSwipe(true);
            }
            else {//当前normal->删除模式
                mRightMenu.setText(R.string.pickerview_cancel);
                mAction_layout.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams tmpParam=   (RelativeLayout.LayoutParams)mWorksListView.getLayoutParams();
                tmpParam.bottomMargin=DensityUtils.dp2px(getContext(),45);
                mWorksListView.setLayoutParams(tmpParam);
                mListAdapter.setActionMode(true);

                if(mOnSwipeItemTouchListener!=null)
                    mOnSwipeItemTouchListener.setCanSwipe(false);
            }
        }
    }*/
    @Override
    public void onStripTabRequestData(){
        //实时查询，去除不一致标记
        PrefStore.putSettingInt("user_collection_count_change",0);
        onFirstLoad();
    }

/*    @Override
    public void requestData() {
        super.requestData();
        //实时查询，去除不一致标记
        PrefStore.putSettingInt("user_collection_count_change",0);
        onFirstLoad();
    }*/


    private void superOnRefresh(){
        super.onRefresh();
    }

    SwipeItemLayout.OnSwipeItemTouchListener mOnSwipeItemTouchListener;

    @Override
    protected void onPrepare() {
        super.onPrepare();
     /*   getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_related_organize);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);*/

        //this.findViewById(R.id.right_btn).setBackground(ResourceUtils.getDrawable(R.drawable.collect_confirm_bg));

        getEmptyLayout().setEmptyImg(R.mipmap.course_no_data_icon);//course_no_cache_icon
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text,R.string.xs_none_collectcourse);
        getEmptyLayout().setTipText(R.string.xs_choose_collectcourse);

        getEmptyLayout().findViewById(R.id.xi_tv_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainTabActivity.class);
                intent.putExtra("require_index",1);
                startActivity(intent);
            }
        });

        mOnSwipeItemTouchListener=new SwipeItemLayout.OnSwipeItemTouchListener(getContext());
        mWorksListView.getRefreshableView().addOnItemTouchListener(mOnSwipeItemTouchListener);

        mWorksListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
        mWorksListView.getRefreshableView().showForcePageEnd();
        mWorksListView.getRefreshableView().setPagesize(getLimit());
        mWorksListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorksListView.getRefreshableView().setRecyclerAdapter(mListAdapter);

    }


    @Override
    public void onSuccess(WarpListResponse<CourseCollectBean> response)   {
        super.onSuccess(response);
       /* if(isCurrentReMode()){
            if(null!=mRightMenu){
                mRightMenu.setVisibility(ArrayUtils.isEmpty(response.getListResponse())?View.GONE:View.VISIBLE);
            }
            *//*if(mTotalTxtView!=null) {
                mTotalTxtView.setText("("+response.data.total+")");
                mHeadView.setVisibility(View.VISIBLE);
            }*//*
        }*/
    }

    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            if(mListAdapter.getItemCount()<=0){
                super.onError(throwable, type);
                // initNotify("网络加载出错~");
            }
            else {
                hideEmptyLayout();
                onRefreshCompleted();
                ToastUtils.showShortToast(UniApplicationContext.getContext(),"网络加载出错~");
            }
        }
    }

    @Override
    public void showEmpty() {
        if (isCurrentReMode()) {
            mListAdapter.clearAndRefresh();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        }else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }
    @Override
    protected void onRefreshCompleted(){
        if(null!=mWorksListView) mWorksListView.onRefreshComplete();
    }

    @Override
    protected void setListener() {
        mWorksListView.getRefreshableView().setOnLoadMoreListener(this);
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        CourseApiService.getApi().getMycollectionList(offset,limit).enqueue(getCallback());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000){
            if(PrefStore.getSettingInt("user_collection_count_change",0)==1){
                PrefStore.putSettingInt("user_collection_count_change",0);
                if(null!=mWorksListView){
                    mWorksListView.getRefreshableView().scrollToPosition(0);
                    mWorksListView.setRefreshing(true);//会触发onRefresh事件
                }
            }
        }
    }

    @Override
    public void onItemClick(int position, View view, int type) {

        CourseCollectBean curLession=mListAdapter.getItem(position);
        if(curLession==null) return;
        if(mListAdapter.isEditMode()){

            boolean oldSelect=curLession.isSelect();
            curLession.setSelect(!oldSelect);
            if(view instanceof CheckBox){
                ((CheckBox)view).setChecked(!oldSelect);
            }
            mSelectNum=mSelectNum+(oldSelect? -1:1);
            setDownloadBtnState();
            return;
        }
        switch (type){
            case EventConstant.EVENT_ALL:

                if(curLession.isBuy==0&&curLession.offLine){
                    ToastUtils.showShort("课程已下架");
                    return;
                }
                if(curLession.isTermined==1){
                    DialogUtils.createSysDialog(getActivity(), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {}
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), MainTabActivity.class);
                                    intent.putExtra("require_index",1);
                                    startActivity(intent);
                                }
                            }, null,
                            "该课程已过期，是否去看看其他课程？", "留在此页", "去看看");
                    return;
                }

                Intent intent = new Intent(getContext(), BaseIntroActivity.class);
                intent.putExtra("NetClassId",String.valueOf( curLession.classId));//lesson.NetClassId
                intent.putExtra("course_type", curLession.videoType);
                intent.putExtra("price", String.valueOf(curLession.actualPrice));
                intent.putExtra("originalprice",String.valueOf( curLession.price));
                intent.putExtra("saleout", curLession.isSaleOut);
                intent.putExtra("rushout", curLession.isRushOut);
                intent.putExtra("daishou", curLession.isTermined);
                intent.putExtra("from", "app我的收藏");
                if(!TextUtils.isEmpty(curLession.collageActivityId))
                    intent.putExtra("collageActiveId",StringUtils.parseInt(curLession.collageActivityId) );
                 startActivityForResult(intent,2000);

                break;
            case EventConstant.EVENT_DELETE:

                final  CourseCollectBean delLession=curLession;
                final  int               delPostion=position;

                DialogUtils.createSysDialog(getActivity(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<CourseCollectBean> tmpList=new ArrayList<>();
                                tmpList.add(delLession);
                                cancelCollection(tmpList,delPostion);
                            }
                        }, null,
                        "真的要取消收藏吗？", "取消", " 确定");
                //showDeleteDlg(mDownCourseAdapter.getCurrentItem(position),position);
                break;
        }
    }

    @OnClick({R.id.selAll_btn,R.id.deleteAll_btn})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.selAll_btn:
                if(mSelectNum==mListAdapter.getItemCount()){
                    mSelectNum=0;
                    for (CourseCollectBean curLession : mListAdapter.getAllItems()) {
                        curLession.setSelect(false);
                    }
                    setDownloadBtnState();
                    mListAdapter.notifyDataSetChanged();
                }else {
                    mSelectNum=mListAdapter.getItemCount();
                    for (CourseCollectBean curLession : mListAdapter.getAllItems()) {
                        curLession.setSelect(true);
                    }
                    setDownloadBtnState();
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.deleteAll_btn:
                if(mSelectNum==0){
                    ToastUtils.showShort("请先选择课程");
                    return;
                }
                 DialogUtils.createSysDialog(getActivity(), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    List<CourseCollectBean> downIDs = new ArrayList<>();
                                    for (CourseCollectBean curLession:mListAdapter.getAllItems()) {
                                        if (curLession.isSelect()) {
                                            downIDs.add(curLession);
                                        }
                                    }
                                    cancelCollection(downIDs,-1);
                                }
                            }, null,
                            "真的要取消收藏吗？", "取消", " 确定");
                break;
            default:
                break;
        }
    }


    List<CourseCollectBean> mDelCourseList;
    private int mDelPostion=-1;
    private void cancelCollection(List<CourseCollectBean> courseIds,int postion){
        mDelCourseList=courseIds;
        if(ArrayUtils.isEmpty(courseIds)){
            ToastUtils.showShort("请先选择课程");
            return;
        }
        mDelPostion=postion;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < courseIds.size(); i++) {
            if (i == courseIds.size() - 1) {
                sb.append(courseIds.get(i).classId);
            } else {
                sb.append(courseIds.get(i).classId + ",");
            }
        }

        ServiceExProvider.visitSimple(getSubscription(), CourseApiService.getApi().cancelCollectionCourse(sb.toString()), new NetObjResponse<String>() {
            @Override
            public void onSuccess(BaseResponseModel<String> model) {
                if(mDelPostion==-1){
                    mListAdapter.getAllItems().removeAll(mDelCourseList) ;
                    if(null!=mDelBtn){
                        mSelectNum=0;
                        mDelBtn.setText("删除");
                    }
                    mListAdapter.notifyDataSetChanged();
                }
                else {
                    mListAdapter.removeAt(mDelPostion,mDelCourseList.get(0));
                }
                if (mListAdapter.getItemCount() <= 0) {
                    Intent data = new Intent();
                    setResult(Activity.RESULT_OK, data);
                    CourseCollectListFragment.this.finish();
                }
            }

            @Override
            public void onError(String message, int type) {
                ToastUtils.showShort("取消收藏失败");
            }
        });
    }

    private void setDownloadBtnState() {
        if(mSelectNum>0){
           /* mDelTipTxt.setVisibility(View.VISIBLE);
            mDelTipTxt.setText(String.valueOf(mSelectNum));
            AnimUtils.scaleView(mDelTipTxt);*/
            mDelBtn.setText(String.format("确认删除 (%d)",mSelectNum));
        }else{
           // mDelTipTxt.setVisibility(View.GONE);
            mDelBtn.setText("删除");
        }
        if (mListAdapter.getItemCount() == (mSelectNum)) {
            mSelAllBtn.setText("取消全选");
        }else {
            mSelAllBtn.setText("全选");
        }
    }
}
