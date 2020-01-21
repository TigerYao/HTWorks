package com.huatu.handheld_huatu.business.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.course.MyStudyCourselistAdapter;
import com.huatu.handheld_huatu.base.AbsBaseHeader;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.lessons.MyCourseSearchFragment;
import com.huatu.handheld_huatu.business.lessons.MyRecylerCourseFragment;
import com.huatu.handheld_huatu.business.lessons.OneToOnCourseInfoFragment;
import com.huatu.handheld_huatu.business.lessons.TeacherOneToOneCourseInfoFragment;
import com.huatu.handheld_huatu.business.me.MyScanActivity;
import com.huatu.handheld_huatu.business.message.MessageGroupListFragment;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.BaseBooleanBean;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseBean;
import com.huatu.handheld_huatu.mvpmodel.CourseTypeEnum;
import com.huatu.handheld_huatu.mvpmodel.StudyBgBean;
import com.huatu.handheld_huatu.mvpmodel.StudyCourseListResponse;
import com.huatu.handheld_huatu.mvppresenter.me.MeArenaContentImpl;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullNestRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewStateUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ServerTimeUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.library.PullToRefreshBase;
import com.huatu.popup.QuickListAction;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.widget.MessageImageView;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2018\11\28 0028.
 */

public class StudyCourseFragment extends ABaseListFragment<StudyCourseListResponse> implements OnRecItemClickListener,AbsBaseHeader.onHeaderLoadListener {

    @BindView(R.id.xi_comm_page_list)
    PullNestRefreshRecyclerView myPeopleListView;

    @BindView(R.id.calendarbg_view)
    ImageView mCalendarBgView;

    @BindView(R.id.head_layout)
    FrameLayout mSearchHeadView;

    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    @BindView(R.id.search_bar)
    TextView mSearchBar;

    @BindView(R.id.rl_scan_btn)
    ImageView mScanView;

    @BindView(R.id.iv_message)
    MessageImageView mMessageView;
    private long mRefreshBgTime=0;

    int totalHeight=0;

    private int mUserCatalogId;

    private String mExamStatus,mPriceStatus,mStudyStatus,mTeacherId;
    private int   mRecentStatus=0;

    MyStudyCourselistAdapter mListAdapter;

    StudyFilterActionDialog  mFilterActionDialog;

    // View mRecyleTextView;
    protected CompositeSubscription mCompositeSubscription = null;
    protected int mCourseType;
    private int tabId;


    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    StudyCourseHeader mHeader;
    private boolean mHasAdBannerbg;//是否有广告背景

/*    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent event) {
        if (event == null) {
            return;
        }
        if (event.typeExObject instanceof MeMsgMessageEvent) {
            if (event.type == MeMsgMessageEvent
                    .MMM_MSG_ME_MESSAGE_HAS) {
                refreshMsgNum();
            } else if (event.type == MeMsgMessageEvent
                    .MMM_MSG_ME_MESSAGE_NO) {
                refreshMsgNum();
            }
        }
    }*/

    private void refreshMsgNum() {
        if (mMessageView != null && MeArenaContentImpl.data != null && MeArenaContentImpl.data.unreadMsgCount > 0) {
            mMessageView.setMessageNum(MeArenaContentImpl.data.unreadMsgCount);
        } else {
            if (mMessageView != null) {
                mMessageView.setMessageNum(0);
            }
        }
    }

    @Override
    protected void parserParams(Bundle arg) {
        super.parserParams(arg);
        mCourseType = arg.getInt(ArgConstant.TYPE);
    }

    @Override
    public int getContentView() {
        return R.layout.course_study_list_layout;
    }

    @Override
    protected RecyclerViewEx getListView()  {
        if(myPeopleListView==null) return null;
        return myPeopleListView.getRefreshableView();
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(  inflater,   savedInstanceSate);
    /*    setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);

        getTitleBar().setShadowVisibility(View.GONE);
        setTitle("我的课程");*/
    /*    if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }*/
        mUserCatalogId=SpUtils.getUserCatgory();
        totalHeight= DensityUtils.dp2px(getContext(), 220);
        mListResponse = new StudyCourseListResponse();
        mListResponse.mCourselist = new ArrayList<>();
        mListAdapter = new MyStudyCourselistAdapter(getContext(), mListResponse.mCourselist);
        mListAdapter.setOnRecyclerViewItemClickListener(this);
    }

    private void superOnRefresh(){
        this.onRefresh();
    }

    @Override
    public void setListener() {
    /*   if(!CommonUtils.isPad(getContext())){
           if(null!=mCalendarBgView){
                mCalendarBgView.setLayoutParams(new RelativeLayout.LayoutParams(DisplayUtil.getScreenWidth(),RelativeLayout.LayoutParams.WRAP_CONTENT));
           }
       }*/
       this.findViewById(R.id.search_bar).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              MyCourseSearchFragment.lanuch(getContext(),0,"","");
          }
       });

       this.findViewById(R.id.iv_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UIJumpHelper.jumpFragment(getContext(),MessageListFragment.class);
                BaseFrgContainerActivity.newInstance(getActivity(),
                        MessageGroupListFragment.class.getName(),
                        null);
                //CourseCalendarActivity.show(getContext());
           }
        });
       this.findViewById(R.id.rl_scan_btn).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(), MyScanActivity.class);
               startActivity(intent);
           }
       });
        myPeopleListView.getRefreshableView().setPagesize(getLimit());
        myPeopleListView.getRefreshableView().setOnLoadMoreListener(this);
        myPeopleListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        if(mCalendarBgView!=null&&(CommonUtils.isPad(getContext()))){
            mCalendarBgView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_none_course);
        getEmptyLayout().setTipText( R.string.xs_my_empty);
        getEmptyLayout().setEmptyImg(R.mipmap.course_no_data_icon);

         myPeopleListView.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(getActivity()));
        myPeopleListView.getRefreshableView().addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));
        myPeopleListView.getRefreshableView().setRecyclerAdapter(mListAdapter);
        myPeopleListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));


        if(null!=MeArenaContentImpl.data&&(MeArenaContentImpl.data.unreadMsgCount>0)){

            mMessageView.setMessageNum(MeArenaContentImpl.data.unreadMsgCount);
        }
       // mMessageView.setMessageNum(1);
        mHeader = new StudyCourseHeader(this,myPeopleListView.getRefreshableView(), this);
        mHeader.setCompositeSubscription(getSubscription());
        mHeader.setOnHeaderLoadListener(this);
        RecyclerViewStateUtils.setHeaderView(myPeopleListView.getRefreshableView(), mHeader.getContentView());

        myPeopleListView.getRefreshableView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(null==myPeopleListView) return;
                RecyclerView.LayoutManager mLayoutManager = myPeopleListView.getRefreshableView().getLayoutManager();
                if (null == mListAdapter || mListAdapter.getItemCount() == 0) {
                    return;
                } else if (null == mLayoutManager || mLayoutManager.getItemCount() == 0) {
                    return;
                } else {

                    int firstVisiblePosition = ((RecyclerView.LayoutParams) mLayoutManager.getChildAt(0)
                            .getLayoutParams()).getViewAdapterPosition();
                    if (firstVisiblePosition == 0) {
                        final View firstVisibleChild = myPeopleListView.getRefreshableView().getChildAt(0);
                        if (firstVisibleChild != null) {
                            onNewScroll(-firstVisibleChild.getTop());
                            //return firstVisibleChild.getTop() >= mWrapperView.getTop();
                        }else {
                            onNewScroll(totalHeight);
                        }
                    }else
                        onNewScroll(totalHeight);

               /*     if (null != goTop)
                        goTop.setVisibility(firstVisiblePosition >= 10 ? View.VISIBLE : View.GONE);*/
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       tabId=((MainTabActivity)context).getCurrentIndex();
        Log.i("acaige","AllCourseFragment-----"+tabId+"------");
    }


    @Override
    public void requestData() {
        super.requestData();
        if(null!=mCommloadingView) mCommloadingView.showLoadingStatus();
        if(null!=mHeader) mHeader.onLoadData();
    }

    @Override
    public void onRefresh() {
        if(null!=mHeader) mHeader.onLoadData();
        showDefaultBg();
    }

    @Override
    public void onHeaderSuccess(){
        if (!isFirstLoad())
            super.onRefresh();
        else
            onFirstLoad();
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        // CourseApiService.getApi().getMyCourseList(0,offset).enqueue(getCallback());
        //CourseApiService.getApi().getMyCourses("",offset,limit).enqueue(getCallback());

        CourseApiService.getApi().getMyStudyCourses(0,mExamStatus,mPriceStatus,mRecentStatus,mStudyStatus,mTeacherId,offset,limit)
                .enqueue(getCallback());
    }

    @Override
    public void onSuccess(StudyCourseListResponse response){
        if(isCurrentReMode()){
            showbg();
//            if (tabId==2&&isFragmentFinished()){
//                showPop();
//            }
            if(response!=null&&response.data != null&&response.data.listObject!=null){
                int onTopcount=0;
                for( BuyCourseBean.Study bean:response.data.listObject){
                    if(bean.isTop) onTopcount++;
                    else {
                        break;
                    }
                }
                mListAdapter.mOnTopCount= onTopcount;
            }
         }
        super.onSuccess(response);
    }


    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            showbg();
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
            mListAdapter.clear();
            //手动的填充两种格式数据
            BuyCourseBean.Study tmpDto = new BuyCourseBean.Study();
            tmpDto.holdType = 1;
            mListResponse.mCourselist.add(tmpDto);

            BuyCourseBean.Study tmpDto2 = new BuyCourseBean.Study();
            tmpDto2.holdType = 2;

            mListResponse.mCourselist.add(tmpDto2);
            mListAdapter.notifyDataSetChanged();
            // mListAdapter.notifyDataSetChanged();
            getListView().resetAll();
            //getListView().hideloading();
            hideEmptyLayout();
        }else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }

    @Override
    protected void onRefreshCompleted(){
        if(null!=myPeopleListView) myPeopleListView.onRefreshComplete();
    }

    private void setOnTop(long rid,String orderId,final boolean isSetOnTop){

        ServiceExProvider.visit(getSubscription(), CourseApiService.setCourseOnTop(rid, orderId, mCourseType, isSetOnTop),
                new NetObjResponse<BaseBooleanBean>() {
                   @Override
                    public void onError(String message, int type) {
                        ToastUtils.showShort(isSetOnTop ? "置顶出错" : "取消置顶出错");
                    }

                    @Override
                    public void onSuccess(BaseResponseModel<BaseBooleanBean> model) {
                        if (model.data.status) {
                            ToastUtils.showShort(isSetOnTop ? "置顶成功" : "取消置顶成功");
                            if (null != myPeopleListView) {
                                myPeopleListView.getRefreshableView().scrollToPosition(0);
                                myPeopleListView.setRefreshing(true);//会触发onRefresh事件
                            }

                        } else
                            ToastUtils.showShort(isSetOnTop ? "置顶出错" : "取消置顶出错");
                    }
                });
    }

    private CustomConfirmDialog mConfirmDialog;
    private BuyCourseBean.Data  mDelCourseInfo;
    private int mDelPostion;
    private void deleteConfrim(BuyCourseBean.Data mineItem, int postion){
        mDelCourseInfo=mineItem;
        mDelPostion=postion;
        if(mConfirmDialog == null) {
            mConfirmDialog = DialogUtils.createDialog(getActivity(),
                    "提示", "即将删除所选课程");
            mConfirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDelCourseInfo!=null){
                        deleteCourse(mDelCourseInfo.rid,mDelCourseInfo.orderId,mDelPostion);
                    }
                }
            });
        }
        if(!mConfirmDialog.isShowing()) {
            mConfirmDialog.show();
        }
    }

    private void deleteCourse(long rid,String orderId,final int postion){

        ServiceExProvider.visit(getSubscription(),CourseApiService.getApi().deleteCourse(rid,orderId),new NetObjResponse<BaseBooleanBean>(){

            @Override
            public void onError(String message,int type){
                ToastUtils.showShort("删除出错");
            }

            @Override
            public void onSuccess(BaseResponseModel<BaseBooleanBean> model){
                if(model.data.status){
                    ToastUtils.showShort("删除成功");
                    if((postion>=0)&&(null!=mListAdapter)&&(mListAdapter.getItemCount()>1)){
                        boolean hasTop=mListAdapter.getItem(postion).isTop;
                        mListAdapter.removeAt(postion);
                        mDelPostion=-1;
                        if(hasTop){
                            mListAdapter.mOnTopCount--;
                            mListAdapter.notifyItemRangeChanged(postion,mListAdapter.getItemCount()-postion-1); //强制刷新列表状态
                        }
                    }
                    else  if(null!=myPeopleListView){
                        myPeopleListView.getRefreshableView().scrollToPosition(0);
                        myPeopleListView.setRefreshing(true);//会触发onRefresh事件
                    }
                }else
                    ToastUtils.showShort("删除出错");
            }
        });
    }

    private boolean checkNoCourse(){
        if(isCurrentReMode()&&(!mListAdapter.isFromFilter())){
            if(mListAdapter.getItemCount()<=1){
                ToastUtils.showShort("没有课程,无法筛选呢");
                return true;
            }
            if(mListAdapter.getItemCount()<=2){
                if(mListAdapter.getItem(1).holdType==2){
                    ToastUtils.showShort("没有课程,无法筛选呢");
                    return true;
                }
                 return false;
            }
        }
        return false;
    }

    QuickListAction shareActons;
    private void showFilterTypeWindow(View anchor) {
       if(checkNoCourse()){
           return;
       }
        if (shareActons == null) {
            shareActons = new QuickListAction(getContext(), R.layout.pop_study_filter_views, R.id.root);
            shareActons.setForceOnBottom();
            shareActons.getRootView().findViewById(R.id.pop_menu_upreport).setSelected(true);
            shareActons.setDistance(DensityUtils.dp2px(getContext(),120));
            shareActons.setAnimStyle(R.style.Animations_PopDownMenu_Center);
            shareActons.setOnViewItemClickListener(new QuickListAction.onItemViewClickListener() {
                @Override
                public void onItemViewClick(int position, View view) {
                    if(!view.isSelected()){//  没有切换
                        shareActons.dismiss();
                        return;
                    }else {

                       ViewGroup containerView= shareActons.getRootView().findViewById(R.id.root);
                       int resetIndex=position==0? 1:0;
                        view.setSelected(false);
                        containerView.getChildAt(resetIndex).setSelected(true);

                        shareActons.dismiss();
                        mListAdapter.refreshOrderType(position);

                        if(null!=myPeopleListView){
                            mRecentStatus=position;
                            mListResponse.mRecentStatus=mRecentStatus;
                            myPeopleListView.getRefreshableView().scrollToPosition(0);
                            myPeopleListView.setRefreshing(true);//会触发onRefresh事件
                        }
                    }
                 }
            });
            shareActons.show(anchor);
        } else
            shareActons.Reshow(anchor);
    }

    private void showFilterAction(View v){
        if (mFilterActionDialog == null) {
            mFilterActionDialog = new StudyFilterActionDialog(v.getContext(), "","");
            mFilterActionDialog.setCanceledOnTouchOutside(true);
            mFilterActionDialog.setCancelable(true);
            mFilterActionDialog.setCompositeSubscription(getSubscription());
            mFilterActionDialog.setOnSubItemClickListener(new StudyFilterActionDialog.OnSubItemClickListener() {
                @Override
                public void onShareBtnClick(String examStatus, String priceStatus, String studyStatus, String teacherId) {

                    if(TextUtils.isEmpty(examStatus)&&TextUtils.isEmpty(priceStatus)&&TextUtils.isEmpty(studyStatus)&&TextUtils.isEmpty(teacherId))  {
                        mListAdapter.setFromFilter(false);
                    }else
                        mListAdapter.setFromFilter(true);
                    mExamStatus=examStatus;
                    mPriceStatus=priceStatus;
                    mStudyStatus=studyStatus;
                    mTeacherId=teacherId;
                    if(null!=myPeopleListView){
                        myPeopleListView.getRefreshableView().scrollToPosition(0);
                        myPeopleListView.setRefreshing(true);//会触发onRefresh事件
                    }
                }
            });
        }
         mFilterActionDialog.show();
      //  mFilterActionDialog.showDialog(getActivity());
    }

    private void showDefaultBg(){
        int delayMin=(int)((System.currentTimeMillis() - mRefreshBgTime) / (1000 * 60 * 5));//
        if(delayMin>=5&&(!mHasAdBannerbg)){
            mRefreshBgTime=System.currentTimeMillis();
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int time= calendar.get(java.util.Calendar.HOUR_OF_DAY);

            boolean isDay=(time>=6)&&(time<18);
            if(null!=mCalendarBgView){
                ImageLoad.displayImageDrawableResource(getContext() ,mCalendarBgView,(isDay ? R.mipmap.study_calendar_day_bg2
                        :R.mipmap.study_calendar_night_bg2),R.mipmap.study_calendar_day_bg2);
            }
        }
    }

    private void showbg(){
          if(null!=mSearchHeadView){
            if("1".equals(mSearchHeadView.getTag())){
                LogUtils.e("showbag","showbg");
                mSearchHeadView.setTag("0");
                mSearchBar.setTextColor(Color.parseColor("#C0DDF8"));
                mSearchBar.setBackgroundResource(R.drawable.drawable_rectangle_transe3e3e3);

                mSearchBar.setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getDrawable(R.mipmap.calendar_search_write_icon),null,null,null);
                mSearchHeadView.setBackgroundColor(Color.argb(0, 255, 255, 255));

                String bgJson=IoExUtils.getJsonString(Constant.APP_STUDY_BG);
                List<StudyBgBean> bgInfo=null;

                java.util.Calendar calendar = java.util.Calendar.getInstance();
                int time= calendar.get(java.util.Calendar.HOUR_OF_DAY);

                boolean isDay=(time>=6)&&(time<18);
                int placeholderId=(isDay ? R.mipmap.study_calendar_day_bg2
                                        :R.mipmap.study_calendar_night_bg2);

                LogUtils.e("showbag",time+"");
                mRefreshBgTime=System.currentTimeMillis();
                boolean hasBg=TextUtils.isEmpty(bgJson)&&(null!=(bgInfo= GsonUtil.jsonToList(bgJson,new GsonUtil.TypeToken<List<StudyBgBean>>() {}.getType())));
                if(hasBg&& (!ArrayUtils.isEmpty(bgInfo))){
                     ImageLoad.displaynoCacheImage(getContext(),placeholderId,bgInfo.get(0).image,mCalendarBgView);
                }
                else {
                     ImageLoad.displayImageDrawableResource(getContext(),mCalendarBgView,placeholderId,R.mipmap.study_calendar_day_bg2);
                }
                if(null!=mScanView) mScanView.setImageLevel(1);
                if(null!=mMessageView) mMessageView.setImageLevel(1);
                checkBg();
            }
        }
    }

    private void checkBg(){
        ServiceExProvider.visitList(getSubscription(), CourseApiService.getApi().getStudyBgDetail(SpUtils.getUserCatgory())
                        .map(new Func1<BaseListResponseModel<StudyBgBean>, BaseListResponseModel<StudyBgBean>>() {
                    @Override
                    public BaseListResponseModel<StudyBgBean> call(BaseListResponseModel<StudyBgBean> studyBgResponse) {
                        BaseListResponseModel<StudyBgBean> tmpResponse=new BaseListResponseModel<>();
                        tmpResponse.code=studyBgResponse.code;
                        tmpResponse.message=studyBgResponse.message; //过滤合适的时间广告
                        if(!ArrayUtils.isEmpty(studyBgResponse.data)){
                            tmpResponse.data=new ArrayList<>();
                            long serverTime=  ServerTimeUtil.newInstance().getServerTime();
                            for(StudyBgBean bgBean:studyBgResponse.data){
                               if((bgBean.onLineTime<=serverTime)&&(bgBean.offLineTime>serverTime)){
                                    tmpResponse.data.add(bgBean);
                               }
                            }
                        }
                        return tmpResponse;
                    }
                }),
                new NetListResponse<StudyBgBean>() {
            @Override
            public void onSuccess(BaseListResponseModel<StudyBgBean> model) {
                IoExUtils.saveJsonFile(GsonUtil.toJsonStr(model.data), Constant.APP_STUDY_BG);
                mHasAdBannerbg=true;
                if(!Method.isActivityFinished(getActivity())){
                    ImageLoad.displaynoCacheImage(getContext(),R.drawable.trans_bg,model.data.get(0).image,mCalendarBgView);
                }
            }

            @Override
            public void onError(String message, int type) {
                if(type==0){
                    IoExUtils.saveJsonFile("", Constant.APP_STUDY_BG);
                    mRefreshBgTime=0;
                    mHasAdBannerbg=false;
                    showDefaultBg();
                }
           }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            LogUtils.e("showbag","onHiddenChanged");
            checkBgStatus();
        }
    }

    private void checkBgStatus() {
        if (mUserCatalogId !=SpUtils.getUserCatgory()) {
            // 登录状态与非登录状态切换时强刷新分类
            mUserCatalogId =SpUtils.getUserCatgory();
            checkBg();
        }
    }

    int currentHeaderHeight=0;
    protected void onNewScroll(int scrollPosition) {

        LogUtils.e("onNewScroll",scrollPosition+","+totalHeight);
        scrollPosition= Math.min(scrollPosition,totalHeight);
        if(currentHeaderHeight==scrollPosition) return;
        currentHeaderHeight=scrollPosition;

        if(null!=mSearchHeadView){
            float  curAlpha=  ((float)scrollPosition)/totalHeight ;
            mCalendarBgView.setAlpha(1-curAlpha);
            mCalendarBgView.setTranslationY(-scrollPosition);
            mSearchHeadView.setBackgroundColor(Color.argb((int)(curAlpha*255), 255, 255, 255));
            if(curAlpha>0.5){
                mSearchBar.setBackgroundResource(R.drawable.drawable_rectangle_e3e3e3);
                mSearchBar.setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getDrawable(R.mipmap.calendar_search_grey_icon),null,null,null);

                mSearchBar.setTextColor(Color.parseColor("#9B9B9B"));
                if(null!=mScanView) mScanView.setImageLevel(0);
                if(null!=mMessageView) mMessageView.setImageLevel(0);
                //mMsgHitView.setImageLevel(1);
                //mTopBarLayout.setTouchable(true);
            }
            else  {
                mSearchBar.setBackgroundResource(R.drawable.drawable_rectangle_transe3e3e3);
                mSearchBar.setTextColor(Color.parseColor("#C0DDF8"));
                mSearchBar.setCompoundDrawablesWithIntrinsicBounds(ResourceUtils.getDrawable(R.mipmap.calendar_search_write_icon),null,null,null);
                if(null!=mScanView) mScanView.setImageLevel(1);
                if(null!=mMessageView) mMessageView.setImageLevel(1);
               // mMsgHitView.setImageLevel(0);
                //mTopBarLayout.setTouchable(false);
            }
        }
    }


    @Override
    public void onDestroyView() {
        if(null!=myPeopleListView){
            myPeopleListView.getRefreshableView().clearOnScrollListeners();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       /* if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }*/
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult( requestCode,  resultCode,  data);
        if((resultCode== Activity.RESULT_OK)&&(requestCode==11200||requestCode==11201||requestCode==21001)){
            if(null!=myPeopleListView){
                myPeopleListView.getRefreshableView().scrollToPosition(0);
                myPeopleListView.setRefreshing(true);//会触发onRefresh事件
            }
        }else if((resultCode==Activity.RESULT_OK)&&(requestCode==10001)){
            if(null!=mOnetoOneLession)
                mOnetoOneLession.oneToOne=2;
        }
    }

    @Override
    public void onItemClick(int position, View view, int type) {
        BuyCourseBean.Data mineItem = mListAdapter.getCurrentItem(position-1);


        switch (type){
            case EventConstant.EVENT_CONCERN:
                UIJumpHelper.jumpFragment(getActivity(),21001, MyRecylerCourseFragment.class,null);
                break;
            case EventConstant.EVENT_MORE:
                if (mineItem.oneToOne == 1) {
                    showOneToOneDlg(mineItem);
                    return;
                }else if (mineItem.oneToOne ==2) {
                    if (mineItem.NetClassCategoryId==12||mineItem.NetClassCategoryId==19||mineItem.NetClassCategoryId==21){
                        if (mineItem.newTeacherOneToOne==0){
                            //旧的教师一对一
                            startOneToOne(mineItem, false);
                        }else {
                            startTeacherOneToOne(mineItem, false);
                        }
                    }else {
                        startOneToOne(mineItem, false);
                    }
                    return;
                }
                break;
            case EventConstant.EVENT_SET_DEFAULT:
                showFilterTypeWindow(view);
                break;
            case EventConstant.EVENT_CHANGE:
                if(checkNoCourse()){
                    return;
                }
                showFilterAction(view);
                break;
            case EventConstant.EVENT_ALL:
              /*  if (!TextUtils.isEmpty(mineItem.protocolUrl)) {
                    HuaTuXieYiActivity.newIntent(getActivity().getApplicationContext()
                            , mineItem.protocolUrl);
                    return;
                }*/
                if (mineItem.oneToOne == 1) {
                    showOneToOneDlg(mineItem);
                    return;
                }/* else if (mineItem.oneToOne == 2) {
                    startOneToOne(mineItem, false);
                    return;
                }*/
                if(mineItem.isExpired){
                    alertOutDateDialog();
                    return;
                }
                if(mineItem.classType == CourseTypeEnum.RECORDING.getValue()){
                    Intent intent2 = new Intent(getActivity(), BJRecordPlayActivity.class);
                    intent2.putExtra("current", -1);
                    intent2.putExtra("classid", String.valueOf(mineItem.rid));
                    intent2.putExtra(ArgConstant.FOR_RESUTL, true);

                    startActivityForResult(intent2,11200);
                }else {

                    Intent intent = new Intent();
                    intent.putExtra("classid", String.valueOf(mineItem.rid));
                    intent.setClass(getActivity(), BJRecordPlayActivity.class);
                    intent.putExtra(ArgConstant.TYPE, 1);
                    intent.putExtra(ArgConstant.FOR_RESUTL, true);
                    startActivityForResult(intent,11201);
                }
                break;
            case EventConstant.EVENT_LIKE:

                if(mListAdapter.mOnTopCount>=10&&(!mineItem.isTop)){
                    ToastUtils.showShort("置顶名额已满，删除一个再试试");
                    return;
                }
                setOnTop(mineItem.rid,mineItem.orderId,mineItem.isTop?false:true);
                break;
            case EventConstant.EVENT_DELETE:
                deleteConfrim(mineItem,position-1);
                //deleteCourse(mineItem.rid,mineItem.orderId);
                break;
        }
    }

    protected void alertOutDateDialog() {
    /*    final QMUITipDialog  tipDialog = new QMUITipDialog.CustomBuilder(getContext())
                .setContent( R.layout.course_outdate_popwin)
                .create(); */
        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                .setTipWord("课程已过期，学习一下其他课程吧")
                .create();
        tipDialog.show();
        myPeopleListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 2000);
    }

    CustomConfirmDialog confirmDialog;
    private void showOneToOneDlg(final BuyCourseBean.Data item) {
        if (item == null) {
            return;
        }
        if (confirmDialog == null) {
            confirmDialog = DialogUtils.createExitConfirmDialog(getActivity(), null,
                    "此课程包含1对1内容，请尽快填写学员信息。学员可通过填写信息卡预约上课时间，" +
                            "上课老师等。若90天未填写学员信息卡，课程将失效且无法退款。", "取消", "去填写");
            confirmDialog.setContentGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
        confirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.NetClassCategoryId==12||item.NetClassCategoryId==19||item.NetClassCategoryId==21){
                    startTeacherOneToOne(item, true);
                }else {
                    startOneToOne(item, true);
                }
            }
        });
        if(confirmDialog != null) {
            confirmDialog.show();
        }
    }


    private BuyCourseBean.Data mOnetoOneLession;
    private void startOneToOne(final BuyCourseBean.Data item, boolean isEdit) {
        //OneToOnCourseInfoFragment fragment = new OneToOnCourseInfoFragment();
        Bundle arg = new Bundle();
        arg.putBoolean("is_edit", isEdit);
        arg.putString("course_id", item.classId);
        arg.putString("course_name", item.title);
        arg.putString("order_number", item.orderNum);
        arg.putInt("NetClassCategoryId", item.NetClassCategoryId);
        mOnetoOneLession=item;
        // fragment.setArguments(bundle);
        // startFragmentForResult(fragment);

        BaseFrgContainerActivity.newInstance(getContext(),
                OneToOnCourseInfoFragment.class.getName(), arg);
    }
    private void startTeacherOneToOne(final BuyCourseBean.Data item, boolean isEdit) {
        //OneToOnCourseInfoFragment fragment = new OneToOnCourseInfoFragment();
        Bundle arg = new Bundle();
        arg.putBoolean("is_edit", isEdit);
        arg.putString("course_id", item.classId);
        arg.putString("course_name", item.title);
        arg.putString("order_number", item.orderNum);
        arg.putInt("NetClassCategoryId", item.NetClassCategoryId);
        mOnetoOneLession=item;
        // fragment.setArguments(bundle);
        // startFragmentForResult(fragment);

        BaseFrgContainerActivity.newInstance(getContext(),
                TeacherOneToOneCourseInfoFragment.class.getName(), arg);
    }
}

