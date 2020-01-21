package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.course.MyBuyCourselistAdapter;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_vod.highmianshou.HuaTuXieYiActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.CourseCalenderFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.BaseBooleanBean;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseBean;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.CourseTypeEnum;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.StickyItemDecoration;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\6\29 0029.
 */

public  class MySingleTypeCourseFragment extends ABaseListFragment<BuyCourseListResponse> implements OnRecItemClickListener,AStripTabsFragment.IStripTabInitData {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView myPeopleListView;


    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    MyBuyCourselistAdapter mListAdapter;

   // View mRecyleTextView;
    protected CompositeSubscription mCompositeSubscription = null;
    protected int mCourseType;

    public boolean isRecylerView(){
        return false;
    }

/*    @Override
    protected int getLimit() {  return 10; }*/

    public static MySingleTypeCourseFragment getInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(ArgConstant.TYPE, type);
        MySingleTypeCourseFragment tmpFragment = new MySingleTypeCourseFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    @Override
    protected void parserParams(Bundle arg) {
        super.parserParams(arg);
        mCourseType = arg.getInt(ArgConstant.TYPE);
    }

    @Override
    public int getContentView() {
        return R.layout.comm_ptrlist_layout2;
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


        mListResponse = new BuyCourseListResponse();

        mListResponse.mCourselist = new ArrayList<>();
        mListAdapter = new MyBuyCourselistAdapter(getContext(), mListResponse.mCourselist,false);
        mListAdapter.setOnRecyclerViewItemClickListener(this);
    }

    private void superOnRefresh(){
        super.onRefresh();
    }

    @Override
    public void setListener() {
        if(!isRecylerView()){
            myPeopleListView.setAdjustDistance(DensityUtils.dp2px(getContext(),38));
        }
        myPeopleListView.getRefreshableView().setPagesize(getLimit());
        myPeopleListView.getRefreshableView().setOnLoadMoreListener(this);
        myPeopleListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
    }

 /*   @Override
    public void onCreateTitleBarMenu(TitleBar titleBar, ViewGroup container) {
        super.onCreateTitleBarMenu(titleBar, container);
        if(!isRecylerView()){
            titleBar.addIcon(R.mipmap.course_search_icon, android.R.id.button1);
            titleBar.addIcon(R.mipmap.course_recyle_icon, android.R.id.button2);
            titleBar.addIcon(R.mipmap.course_calendar_icon, android.R.id.button3);
        }
    }
*/

    @Override
    public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
        super.onMenuClicked(titleBar, menuItem);
        if(isRecylerView()) return;
        if (menuItem.getId() == android.R.id.button1) {
          /*  Bundle bundle = new Bundle();
            bundle.putInt("course_type", 0);
            BaseFrgContainerActivity.newInstance(UniApplicationContext.getContext(),
                    CourseSearchMineFragment.class.getName(), bundle);*/

            MyCourseSearchFragment.lanuch(getContext(),0,"","");
        }
        else if (menuItem.getId() == android.R.id.button2) {

           // MyAllRecyleCourseFragment.lanuch(getContext(),viewPager.getCurrentItem());
              UIJumpHelper.jumpFragment(getContext(),MyRecylerCourseFragment.class);
        }
        else if (menuItem.getId() == android.R.id.button3) {
            //  CourseCalenderFragment calenderFragment = new CourseCalenderFragment();
            //  startFragmentForResult(calenderFragment);
            if(!NetUtil.isConnected()){
                ToastUtils.showShort("当前网络不可用");
                return;
            }
            Bundle bundle = new Bundle();
            BaseFrgContainerActivity.newInstance(UniApplicationContext.getContext(),
                    CourseCalenderFragment.class.getName(), bundle);
        }
    }


    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_none_course);
        getEmptyLayout().setTipText( R.string.xs_my_empty);
        getEmptyLayout().setEmptyImg(R.mipmap.course_no_data_icon);

        myPeopleListView.getRefreshableView().addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));
        myPeopleListView.getRefreshableView().setRecyclerAdapter(mListAdapter);
        myPeopleListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
       // myPeopleListView.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 10)));

         if(!isRecylerView()){
            StickyItemDecoration mDecoration = new StickyItemDecoration(mListAdapter);
            myPeopleListView.getRefreshableView().addItemDecoration(mDecoration);

        /*    mRecyleTextView=View.inflate(getContext(),R.layout.course_recycle_text,null);
            FrameLayout.LayoutParams tmpparams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
            tmpparams.gravity=Gravity.RIGHT;

            myPeopleListView.getRefreshContainer().addView(mRecyleTextView,tmpparams);
            mRecyleTextView.setVisibility(View.GONE);
            mRecyleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIJumpHelper.jumpFragment(getContext(),MyRecylerCourseFragment.class);
                }
            });*/
        }
    }

    @Override
    public void requestData() {
        super.requestData();
        onFirstLoad();
    }

    @Override
    public void onStripTabRequestData(){
        onFirstLoad();
    }

    @Override
    protected void onLoadData(int offset, int limit) {
       // CourseApiService.getApi().getMyCourseList(0,offset).enqueue(getCallback());
        CourseApiService.getApi().getMyCourses("",offset,limit).enqueue(getCallback());
    }

/*    @Override
    public void onSuccess(BuyCourseListResponse response){
        if(isCurrentReMode()){
           if(response!=null&&response.data != null&&response.data.listObject!=null)
            mListAdapter.mOnTopCount= ArrayUtils.size(response.data.listObject.top);
        }
        super.onSuccess(response);
    }*/

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
            mListAdapter.clear();
            mListAdapter.notifyDataSetChanged();
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
        if(null!=myPeopleListView) myPeopleListView.onRefreshComplete();
    }

    private void setOnTop(long rid,String orderId,final boolean isSetOnTop){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        ServiceExProvider.visit(mCompositeSubscription,CourseApiService.setCourseOnTop(rid,orderId,mCourseType,isSetOnTop),new NetObjResponse<BaseBooleanBean>(){

            @Override
            public void onError(String message,int type){
                ToastUtils.showShort(isSetOnTop ?"置顶出错":"取消置顶出错");
            }
            @Override
            public void onSuccess(BaseResponseModel<BaseBooleanBean> model){
                if(model.data.status){
                    ToastUtils.showShort(isSetOnTop?"置顶成功":"取消置顶成功");
                    if(null!=myPeopleListView){
                        myPeopleListView.getRefreshableView().scrollToPosition(0);
                        myPeopleListView.setRefreshing(true);//会触发onRefresh事件
                    }
                }else
                    ToastUtils.showShort(isSetOnTop ?"置顶出错":"取消置顶出错");
             }
        });
     }

    private CustomConfirmDialog mConfirmDialog;
    private BuyCourseBean.Data  mDelCourseInfo;
    private void deleteConfrim(BuyCourseBean.Data mineItem){
        mDelCourseInfo=mineItem;
        if(mConfirmDialog == null) {
            mConfirmDialog = DialogUtils.createDialog(getActivity(),
                    "提示", "即将删除所选课程");
            mConfirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDelCourseInfo!=null){
                        deleteCourse(mDelCourseInfo.rid,mDelCourseInfo.orderId);
                    }
                }
            });
        }

        if(!mConfirmDialog.isShowing()) {
            mConfirmDialog.show();
        }
    }

    private void deleteCourse(long rid,String orderId){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        ServiceExProvider.visit(mCompositeSubscription,CourseApiService.getApi().deleteCourse(rid,orderId),new NetObjResponse<BaseBooleanBean>(){

            @Override
            public void onError(String message,int type){
                ToastUtils.showShort("删除出错");
            }

            @Override
            public void onSuccess(BaseResponseModel<BaseBooleanBean> model){
                if(model.data.status){
                    ToastUtils.showShort("删除成功");
                    if(null!=myPeopleListView){
                        myPeopleListView.getRefreshableView().scrollToPosition(0);
                        myPeopleListView.setRefreshing(true);//会触发onRefresh事件
                    }
                }else
                    ToastUtils.showShort("删除出错");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        //  EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult( requestCode,  resultCode,  data);
        if((resultCode== Activity.RESULT_OK)&&(requestCode==11200||requestCode==11201)){
            if(null!=myPeopleListView){
                myPeopleListView.getRefreshableView().scrollToPosition(0);
                myPeopleListView.setRefreshing(true);//会触发onRefresh事件
            }
        }else if((resultCode==Activity.RESULT_OK)&&(requestCode==10001)){
           if(null!=mOnetoOneLession)
               mOnetoOneLession.oneToOne=0;
        }
    }

    @Override
    public void onItemClick(int position, View view, int type) {
        BuyCourseBean.Data mineItem = mListAdapter.getCurrentItem(position);
        switch (type){
            case EventConstant.EVENT_ALL:
                if (!TextUtils.isEmpty(mineItem.protocolUrl)) {
                    HuaTuXieYiActivity.newIntent(getActivity().getApplicationContext()
                            , mineItem.protocolUrl);
                    return;
                }
                if (mineItem.oneToOne == 1) {
                    showOneToOneDlg(mineItem);
                    return;
                }/* else if (mineItem.oneToOne == 2) {
                    startOneToOne(mineItem, false);
                    return;
                }*/
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
                setOnTop(mineItem.rid,mineItem.orderId,mineItem.isTop?false:true);
                break;
            case EventConstant.EVENT_DELETE:
                deleteConfrim(mineItem);
                //deleteCourse(mineItem.rid,mineItem.orderId);
                break;
        }
    }

    protected void alertOutDateDialog() {
    /*    final QMUITipDialog  tipDialog = new QMUITipDialog.CustomBuilder(getContext())
                .setContent( R.layout.course_outdate_popwin)
                .create(); */
        final QMUITipDialog  tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                .setTipWord("过期没看够?再看要重买哦~")
                .create();
        tipDialog.show();
        myPeopleListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                tipDialog.dismiss();
            }
        }, 3000);
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
                startOneToOne(item, true);
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
        mOnetoOneLession=item;
       // fragment.setArguments(bundle);
       // startFragmentForResult(fragment);

        BaseFrgContainerActivity.newInstance(getContext(),
                OneToOnCourseInfoFragment.class.getName(), arg);
    }
}
