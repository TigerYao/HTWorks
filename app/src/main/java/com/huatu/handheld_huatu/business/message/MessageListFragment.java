package com.huatu.handheld_huatu.business.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.MessageListAdapter;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.arena.activity.ManualCheckReportActivity;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamCheckDetailV2;
import com.huatu.handheld_huatu.business.essay.mainfragment.CheckCorrectEssay;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matches.activity.ScReportActivity;
import com.huatu.handheld_huatu.business.matches.activity.SimulationContestActivityNew;
import com.huatu.handheld_huatu.business.matches.cache.MatchCacheData;
import com.huatu.handheld_huatu.business.me.order.LogisticDetailActivity;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.UserMessageBean;
import com.huatu.handheld_huatu.mvpmodel.WarpListResponse;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.util.ArrayList;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\11\6 0006.
 */


public class MessageListFragment extends ABaseListFragment<WarpListResponse<UserMessageBean>> implements OnRecItemClickListener, MessageListAdapter.OnDeleteListener {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView myPeopleListView;


    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    MessageListAdapter mListAdapter;


    // View mRecyleTextView;

    protected int mCourseType;
    private String view;
    private String from;
    private boolean from_push = false;
    private String msgType;

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
    protected RecyclerViewEx getListView() {
        if (myPeopleListView == null) return null;
        return myPeopleListView.getRefreshableView();
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);
        setTitle("消息");

        mListResponse = new WarpListResponse();
        mListResponse.mAdapterList = new ArrayList<>();
        mListAdapter = new MessageListAdapter(getContext(), mListResponse.mAdapterList, this);
        mListAdapter.setOnViewItemClickListener(this);
    }

    private void superOnRefresh() {
        super.onRefresh();
    }

    @Override
    public void setListener() {
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
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_none_message);
        getEmptyLayout().setTipText(R.string.xs_my_empty);
        getEmptyLayout().setEmptyImg(R.mipmap.course_no_cache_icon);


        myPeopleListView.getRefreshableView().addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getActivity()));
        myPeopleListView.getRefreshableView().setRecyclerAdapter(mListAdapter);
        myPeopleListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        //myPeopleListView.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 0.6f),true));

        if (getArguments() != null) {
            view = getArguments().getString("view");
            from_push = getArguments().getBoolean("from_push");
        }
        if (view.equals("course")) {
            msgType = "课程通知";
        } else if (view.equals("feedBack")) {
            msgType = "反馈通知";
        } else if (view.equals("logistics")) {
            msgType = "物流通知";
        } else if (view.equals("platForm")) {
            msgType = "平台通知";
        } else {
            msgType = "其他";
        }
        if ("platForm".equals(view)) {
            myPeopleListView.getRefreshableView().showForcePageEnd();
        }
        if (from_push) {
            from = "消息推送";
        } else {
            from = "消息列表";
        }
    }

    @Override
    public void requestData() {
        super.requestData();
        onFirstLoad();
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        CourseApiService.getApi().getMessagelist(view, offset, limit).enqueue(getCallback());
    }

    @Override
    public void onSuccess(WarpListResponse<UserMessageBean> response) {
        if (!ArrayUtils.isEmpty(response.getListResponse())) {
            for (UserMessageBean bean : response.getListResponse()) {
                if (bean.type.equals("course")) {//跳转课程
                    bean.holdType = MessageListAdapter.ONLINE;
                } else if (bean.type.equals("mock")) {//跳转模考
                    bean.holdType = MessageListAdapter.MOCK;
                } else if (bean.type.equals("order")) {
                    bean.holdType = MessageListAdapter.LOGISTIC;
                } else if (bean.type.equals("feedback")) {
                    bean.holdType = MessageListAdapter.CORRECTION;
                } else if (bean.type.equals("correct")) {
                    bean.holdType = MessageListAdapter.ESSAYREPORT;
                } else if (bean.type.equals("correctCourseWork")) {
                    bean.holdType = MessageListAdapter.COURSEHOMEWORK;
                }
            }
        }
        super.onSuccess(response);
    }

/*    @Override
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
    }*/

    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            if (mListAdapter.getItemCount() <= 0) {
                super.onError(throwable, type);
                // initNotify("网络加载出错~");
            } else {
                hideEmptyLayout();
                onRefreshCompleted();
                ToastUtils.showShortToast(UniApplicationContext.getContext(), "网络加载出错~");
            }
        }
    }

    @Override
    protected void onRefreshCompleted() {
        if (null != myPeopleListView) myPeopleListView.onRefreshComplete();
    }

    private CompositeSubscription mCompositeSubscription = null;

    protected CompositeSubscription getSubscription() {
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        //  EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void setReadStatus(String noticeId) {
        ServiceExProvider.visitSimple(getSubscription(), CourseApiService.getApi().setMessageRead(StringUtils.parseLong(noticeId)), null);
    }

    @Override
    public void onItemClick(int position, View view, int type) {
        UserMessageBean mineItem = mListAdapter.getItem(position);
        if (null == mineItem) return;
        if (mineItem.isRead == 0) {
            view.setVisibility(View.INVISIBLE);
            mineItem.isRead = 1;
            setReadStatus(mineItem.noticeId);
        }
        switch (type) {
            case EventConstant.EVENT_ALL:
                if (mineItem.holdType == MessageListAdapter.ONLINE) {
                    //跳转课程 售后
                    try {
                        Intent intent2 = new Intent(getActivity(), BJRecordPlayActivity.class);
                        intent2.putExtra(ArgConstant.TYPE, 1);
                        intent2.putExtra("classid", String.valueOf(mineItem.payload.custom.businessId));
                        startActivity(intent2);
                    } catch (Exception e) {

                    }
                } else if (mineItem.holdType == MessageListAdapter.COURSEHOMEWORK) {
                    //跳转课程 售后大纲课后作业，带定位
                    try {
                        Intent intent = new Intent(getActivity(), BJRecordPlayActivity.class);
                        intent.putExtra(ArgConstant.TYPE, mineItem.payload.custom.isLive);
                        intent.putExtra("classid", mineItem.payload.custom.netClassId);
                        intent.putExtra(ArgConstant.LESSION_ID, mineItem.payload.custom.lession_id);
                        intent.putExtra(ArgConstant.FROM_ACTION, mineItem.payload.custom.syllabusId);
                        startActivity(intent);
                    } catch (Exception e) {

                    }
                } else if (mineItem.holdType == MessageListAdapter.MOCK) {
                    if (mineItem.detailType.equals("ready") || mineItem.detailType.equals("online")) {
                        MatchCacheData.getInstance().matchPageFrom = "app消息";
                        final Context context = getContext();
                        if (context instanceof BaseActivity) {
                            Intent intent = new Intent(context, SimulationContestActivityNew.class);
                            intent.putExtra("subject", SignUpTypeDataCache.getInstance().getCurSubject());
                            startActivity(intent);
                            finish();
                        }
                    } else {

                        try {
                            // 模考大赛报告  自已的报告
                            final Bundle bundle = new Bundle();
                            bundle.putLong("practice_id", StringUtils.parseLong(mineItem.payload.custom.businessId));
                            bundle.putInt("tag", 1);

                            final Context context = getContext();
                            if (context instanceof BaseActivity) {
                                Intent intent = new Intent(context, ScReportActivity.class);
                                intent.putExtra("tag", 1);
                                intent.putExtra("arg", bundle);
                                startActivity(intent);
                                finish();
                            }

                        } catch (Exception e) {

                        }
                    }
                } else if (mineItem.holdType == MessageListAdapter.LOGISTIC) {
                    try {
                        LogisticDetailActivity.newInstance(getActivity(), mineItem.payload.custom.businessId);
                    } catch (Exception e) {
                        LogUtils.e(e.getMessage());
                    }
                } else if (mineItem.holdType == MessageListAdapter.CORRECTION) {
                    UserMessageBean.CustomBean customBean = mineItem.payload.custom;
                    if (customBean == null)
                        return;

                    EssayExamCheckDetailV2.lanuchForMsg(getActivity(), customBean.topicType, customBean.answerCardId, customBean.questionBaseId, customBean.paperId, customBean.areaName, customBean.paperName);

                } else if (mineItem.holdType == MessageListAdapter.ESSAYREPORT) {
                    String detaiType = mineItem.detailType;
                    UserMessageBean.CustomBean customBean = mineItem.payload.custom;
                    if (Utils.isEmptyOrNull(detaiType) || customBean == null)
                        return;

                    if (TextUtils.equals(detaiType, "manualReport")) { //跳转到报告页
                        ManualCheckReportActivity.startForMsgCheckReport(getActivity(), customBean.topicType, customBean.answerCardId, customBean.questionBaseId, customBean.paperId);
                    } else if (TextUtils.equals(detaiType, "correctList")) { //跳转到列表页
                        Bundle bundle = new Bundle();
                        bundle.putInt("topicType", customBean.topicType);
                        BaseFrgContainerActivity.newInstance(getContext(), CheckCorrectEssay.class.getName(), bundle);
                    }
                }
                break;

        }
        StudyCourseStatistic.checkMessage(from, msgType, mineItem.payload.text);
    }


    @Override
    public void onDeleteClick(final int position) {
        DialogUtils.onShowConfirmDialog(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMessage(position);
            }
        }, null, "真的要删除吗？", "取消", "确定");

    }

    private void deleteMessage(int position) {
        UserMessageBean item = mListAdapter.getItem(position);
        //删除单条消息
        ServiceProvider.deleteMessage(getSubscription(), item.noticeId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                superOnRefresh();
            }
        });
    }

    @Override
    protected void onGoBack() {
        if (from_push&& !ActivityStack.getInstance().hasRootActivity()) {
            MainTabActivity.newIntent(getActivity());
        }
        super.onGoBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (from_push&& !ActivityStack.getInstance().hasRootActivity()) {
                MainTabActivity.newIntent(getActivity());
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}