package com.huatu.handheld_huatu.business.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huatu.AppContextProvider;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CourseAdapterNew;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.fragment.ABaseLazyListFragment;
import com.huatu.handheld_huatu.business.faceteach.FaceMainWebViewFragment;
import com.huatu.handheld_huatu.business.lessons.CourseCategorySelectFragment;
import com.huatu.handheld_huatu.business.lessons.bean.AllCourseData;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.business.me.ExamTargetAreaActivity;
import com.huatu.handheld_huatu.business.message.MessageListFragment;
import com.huatu.handheld_huatu.business.other.OnLiveSearchActivity;
import com.huatu.handheld_huatu.helper.LoginTrace;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.AllCourseListResponse;
import com.huatu.handheld_huatu.mvppresenter.me.MeArenaContentImpl;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.countdown.CountDownTask;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.SpaceItemDecoration;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ServerTimeUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.viewpagerindicator.PagerSlidingArrayTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\6\13 0013.
 * 首页课程列表
 */

public class AllCourseFragment extends ABaseLazyListFragment<AllCourseListResponse> implements OnRecItemClickListener, PagerSlidingArrayTabStrip.onSelectTabListener {
    private static final String TAG = "AllCourseFragment";
    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView myPeopleListView;

    @BindView(R.id.xi_activity_type_tab_strip)
    PagerSlidingArrayTabStrip mSlidingArrayTabStrip;

    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;
    private int showCategoryId;

    public CommloadingView getCommLoadingView() {
        return mCommloadingView;
    }

    private ArrayList<CourseCategoryBean> mSelCategoryList = new ArrayList<>();

    private CourseAdapterNew mListAdapter;

    private int mCategoryId = 0;                             // 最终考试Id
    private String mSubjectId = "";                           // 最终科目Id

    private int mCurrentItem = 0;
    private final int FACETEACHTYPE = 1017;    //面授 1017

    private CompositeSubscription mCompositeSubscription;

    protected String keyword = "";

    protected int mOrderId = 0;
    protected int mPriceId = 1000;
    private int tabId;
    private boolean mCanReadCache = true;
    private int mUserId;

    private void refreshMsgNum() {
        TextView mMsgNumTxt = this.findViewById(R.id.tv_message_num);
        if (mMsgNumTxt != null && MeArenaContentImpl.data != null && MeArenaContentImpl.data.unreadMsgCount > 0) {
            if (MeArenaContentImpl.data.unreadMsgCount > 99) {
                mMsgNumTxt.setText("99+");
            } else {
                mMsgNumTxt.setText(MeArenaContentImpl.data.unreadMsgCount + "");
            }
            mMsgNumTxt.setVisibility(View.VISIBLE);
        } else {
            if (mMsgNumTxt != null) {
                mMsgNumTxt.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected int getLimit() {
        return 1000;
    }

    @Override
    public int getContentView() {
        return R.layout.main_vodcourse_layout;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return myPeopleListView.getRefreshableView();
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        mListResponse = new AllCourseListResponse();
        mListResponse.mLessionlist = new ArrayList<>();
        mListAdapter = new CourseAdapterNew(getContext(), mListResponse.mLessionlist);
        mUserId = UserInfoUtil.userId;
    }

    @Override
    public void onRefresh() {
        if (ArrayUtils.isEmpty(mSelCategoryList)) {
            getCategoryList();
        } else {
            superOnRefresh();
        }
    }

    private void superOnRefresh() {
        if (mCategoryId == FACETEACHTYPE && (mDetailWebView != null)) {
            mDetailWebView.refreshUI();
            return;
        }
        super.onRefresh();
    }

    private void refreshCurrent(boolean isFromUser) {
        if (ArrayUtils.isEmpty(mSelCategoryList)) {
            getCategoryList();
        } else {
            mCanReadCache = !isFromUser;
            superOnRefresh();
        }
    }

    @Override
    public void setListener() {
        this.findViewById(R.id.iv_change_subject).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showShort("网络未连接，请检查您的网络设置");
                    return;
                }
                StudyCourseStatistic.clickStatistic("课程", "页面第一模块左上角", "设置的考试类型");
                if (!CommonUtils.checkLogin(getActivity())) {
                    return;
                }
                ExamTargetAreaActivity.newIntent(getActivity(), null);
            }
        });
        this.findViewById(R.id.iv_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                UIJumpHelper.jumpFragment(getActivity(), MessageListFragment.class);

            }
        });
        this.findViewById(R.id.search_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudyCourseStatistic.clickSearchBar("课程");
                OnLiveSearchActivity.newIntent(getActivity(), mCategoryId, mSubjectId, String.valueOf(mOrderId), mSelCategoryList);
            }
        });
        mSlidingArrayTabStrip.setOnSelectTabListener(this);

        mSlidingArrayTabStrip.setOnTabChangeIntercept(new PagerSlidingArrayTabStrip.onTabChangeIntercept() {
            @Override
            public boolean OnBeforeTabIntercept(int index) {

                if (ArrayUtils.isInArrayRange(mSelCategoryList, index)) {
                    boolean isFlag = FACETEACHTYPE == mSelCategoryList.get(index).cateId;
                    if (isFlag && (!CommonUtils.checkLogin(getContext()))) {
                        return true;
                    }
                }
                return false;
            }
        });

        myPeopleListView.getRefreshableView().setOnLoadMoreListener(this);
        myPeopleListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                refreshCurrent(true);
            }
        });
        this.findViewById(R.id.shopping_category_setting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            @LoginTrace(type = 0)
            public void onClick(View v) {
                CourseCategorySelectFragment.newInstance(getActivity(), 10034);
            }
        });
    }

    @Override
    public int getCurrentItem() {
        return mCurrentItem;
    }

    @Override
    public void setCurrentItem(int position, boolean isAnimal) {
        try {
            if (mCurrentItem == position) return;
            mCurrentItem = position;

            mCategoryId = mSelCategoryList.get(position).cateId;
            mSubjectId = mSelCategoryList.get(position).subjectId;
            StudyCourseStatistic.clickStatistic("课程", "页面第二模块滚动条", mSelCategoryList.get(position).name);
            SpUtils.setSelectedLiveCategory(mCategoryId);
            SpUtils.setSelectedCategoryName(mSelCategoryList.get(position).name);
            myPeopleListView.getRefreshableView().scrollToPosition(0);

            if (mCategoryId == FACETEACHTYPE) {
                showFaceTeachInfo(true);
            } else {
                showFaceTeachInfo(false);
                ((BaseActivity) getActivity()).showProgress();
                superOnRefresh();
            }

            // myPeopleListView.getRefreshableView().scrollToPosition(0);
            // myPeopleListView.setRefreshing(true);//会触发onRefresh事件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    FaceMainWebViewFragment mDetailWebView;

    private void showFaceTeachInfo(boolean needShow) {
        if (needShow) {
            if (null == mDetailWebView) {
                mDetailWebView = new FaceMainWebViewFragment();
                this.getChildFragmentManager().beginTransaction()
                        .add(R.id.webview_container, mDetailWebView).commitAllowingStateLoss();

            } else {
                mDetailWebView.resumeUrl();
                this.getChildFragmentManager().beginTransaction()
                        .show(mDetailWebView).commitAllowingStateLoss();
            }
        } else {
            if (null != mDetailWebView) {
                this.getChildFragmentManager().beginTransaction()
                        .hide(mDetailWebView).commitAllowingStateLoss();
                mDetailWebView.clearContent();
            }
        }
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_date);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);
        myPeopleListView.getRefreshableView().setPagesize(getLimit());

        myPeopleListView.getRefreshableView().showForcePageEnd();
        myPeopleListView.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(getActivity()));
        myPeopleListView.getRefreshableView().setRecyclerAdapter(mListAdapter);
        myPeopleListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        myPeopleListView.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 0.6f)));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            if (AppContextProvider.hasFlag(AppContextProvider.BANNRCOURSETYPE)) {
                AppContextProvider.removeFlag(AppContextProvider.BANNRCOURSETYPE);
                requestData();
            } else if (mUserId != UserInfoUtil.userId) {
                //登录状态与非登录状态切换时强刷新分类
                mUserId = UserInfoUtil.userId;
                requestData();
            }
        }
    }


    @Override
    public void requestData() {
        super.requestData();
        String value = SpUtils.getLiveCategoryList();
        //  LogUtils.v(TAG, "requestData:"+value);
        boolean canJump = true;
        int localVersion = PrefStore.getUserSettingInt(Constant.APP_COURSETYPE_VERSION, 0);
        int newVersion = PrefStore.getSettingInt(Constant.APP_COURSETYPE_REMOTE_VERSION, 0);

        LogUtils.v(TAG, "requestData:"+localVersion+","+newVersion);
        if (newVersion > localVersion) {
            PrefStore.putUserSettingInt(Constant.APP_COURSETYPE_VERSION, newVersion);
            canJump = false;
        }
        if ((canJump) && !TextUtils.isEmpty(value)) {
            List<CourseCategoryBean> allType = GsonUtil.jsonToList(value, new GsonUtil.TypeToken<List<CourseCategoryBean>>() {
            }.getType());
            if (ArrayUtils.isEmpty(allType)) {
                getCategoryList();
            } else {
                showCategory(allType);
            }
            return;
        }
        getCategoryList();
    }

    private void showCategory(List<CourseCategoryBean> titleList) {
        mSelCategoryList.clear();
        int vodCourseExam = SpUtils.getSelectedLiveCategory();
        for (CourseCategoryBean item : titleList) {
            if (vodCourseExam == item.cateId) {
                if (!item.checked) {
                    item.checked = true;
                }
            }
            if (item.checked) {
                mSelCategoryList.add(item);
            }
        }
        int size = ArrayUtils.size(mSelCategoryList);
        if (size > 0) {
            mCurrentItem = 0;
            for (int i = 0; i < mSelCategoryList.size(); i++) {
                if (vodCourseExam == mSelCategoryList.get(i).cateId) {
                    mCurrentItem = i;
                    break;
                }
            }
            mCategoryId = mSelCategoryList.get(mCurrentItem).cateId;
            mSubjectId = mSelCategoryList.get(mCurrentItem).subjectId;
            SpUtils.setSelectedLiveCategory(mCategoryId);

            String[] tmpArr = new String[size];
            for (int i = 0; i < ArrayUtils.size(mSelCategoryList); i++)
                tmpArr[i] = mSelCategoryList.get(i).name;
            mSlidingArrayTabStrip.setTabArray(tmpArr);

            if (mCategoryId == FACETEACHTYPE) {
                showFirstLoading();
                showFaceTeachInfo(true);
            } else {
                showFaceTeachInfo(false);
                if (isFirstLoad()) onFirstLoad();
                else {

                    myPeopleListView.getRefreshableView().scrollToPosition(0);
                    myPeopleListView.setRefreshing(true);//会触发onRefresh事件
                }
            }
        } else {
            ToastUtils.makeText(getContext(), "加载分类出错").show();
            if (null != mCommloadingView) mCommloadingView.showServerError();
            if (null != myPeopleListView) myPeopleListView.onRefreshComplete();
        }

    }

    private void getCategoryList() {
        ServiceProvider.getCourseCategoryList(mCompositeSubscription, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                ToastUtils.makeText(getContext(), "加载分类出错").show();
                if (null != mCommloadingView) {
                    if (NetUtil.isConnected())
                        mCommloadingView.showServerError();
                    else
                        mCommloadingView.showNetworkTip();
                }
                if (null != myPeopleListView) myPeopleListView.onRefreshComplete();
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                List<CourseCategoryBean> titleList = model.data;
                if (!ArrayUtils.isEmpty(titleList)) {
                    for (CourseCategoryBean item : titleList) {  //全部打开
                        if (!item.checked) {
                            item.checked = true;
                        }
                    }
                    IoExUtils.saveJsonFile(GsonUtil.toJsonStr(titleList), Constant.ALL_COUSRE_TYPE);
                    SpUtils.setLiveCategoryList(GsonUtil.GsonString(titleList));
                }
                showCategory(titleList);
            }
        });
    }

    @Override
    public void showEmpty() {
        if (isCurrentReMode()) {
            mListAdapter.clear();
            mListAdapter.notifyDataSetChanged();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        } else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10034 && resultCode == Activity.RESULT_OK) {
            //getCategoryList();
            requestData();
        } else if (requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            refreshCurrent(true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tabId = ((MainTabActivity) context).getCurrentIndex();
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        if (!mCanReadCache) {
            mCanReadCache = true;
            CourseApiService.getApi().getAllCourses(mCategoryId).enqueue(getCallback());
        } else {
            CourseApiService.getApi().getAllCacheCourses(mCategoryId).enqueue(getCallback());
        }
    }

    @Override
    public void onSuccess(AllCourseListResponse response) {
        if (isCurrentReMode()) {
            if (null != mListAdapter)
                mListAdapter.clearCountDownTask();
        }
        if (!ArrayUtils.isEmpty(response.getListResponse())) {
            long beginTime = CountDownTask.elapsedRealtime();//毫秒级
            for (AllCourseData curLesson : response.getListResponse()) {
                for (int i = 0; i < curLesson.data.size(); i++) {
                    // 根据服务器时间，计算剩余时间（这里服务器上cdn后，改成用开始时间，而不是剩余时间了）
                    if (curLesson.data.get(i).startTimeStamp != 0) {
                        long startR = (curLesson.data.get(i).startTimeStamp * 1000 - ServerTimeUtil.newInstance().getServerTime()) / 1000;
                        curLesson.data.get(i).lSaleStart = startR > 0 ? startR : 0;
                    } else {
                        curLesson.data.get(i).lSaleStart = Method.parseLong(curLesson.data.get(i).saleStart);
                    }
                    if (curLesson.data.get(i).stopTimeStamp != 0) {
                        long endR = (curLesson.data.get(i).stopTimeStamp * 1000 - ServerTimeUtil.newInstance().getServerTime()) / 1000;
                        curLesson.data.get(i).lSaleEnd = endR > 0 ? endR : 0;
                    } else {
                        curLesson.data.get(i).lSaleEnd = Method.parseLong(curLesson.data.get(i).saleEnd);
                    }

                    if (curLesson.data.get(i).lSaleStart > 0) {
                        curLesson.data.get(i).lSaleStart = beginTime + curLesson.data.get(i).lSaleStart * 1000;
                    }
                    if (curLesson.data.get(i).lSaleEnd > 0) {
                        curLesson.data.get(i).lSaleEnd = beginTime + curLesson.data.get(i).lSaleEnd * 1000;
                    }
                }
            }
        }
        super.onSuccess(response);
    }

    @Override
    protected void notifyDataSetChanged(int positionStart, int itemSize) {
        mListAdapter.updateItemCount();
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            super.onError(throwable, type);
        }
    }

    @Override
    protected void onRefreshCompleted() {
        if (null != myPeopleListView) myPeopleListView.onRefreshComplete();
        ((BaseActivity) getActivity()).hideProgress();
    }

    //by chq
    @Override
    public void onItemClick(int position, View view, int type) {
    }

    @Override
    public void onUserVisible() {
        super.onUserVisible();
        if (null != mListAdapter) {
            mListAdapter.checkPauseStatus();
        }
    }

    @Override
    public void onUserInvisible() {
        super.onUserInvisible();
        if (null != mListAdapter) {
            mListAdapter.pauseTicks();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mListAdapter)
            mListAdapter.clearCountDownTask();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        SpUtils.setSelectedLiveCategory(0);

    }

}
