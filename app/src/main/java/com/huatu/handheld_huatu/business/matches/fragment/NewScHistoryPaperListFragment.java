package com.huatu.handheld_huatu.business.matches.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseResult;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.matches.adapter.NewScArenaPastAdapter;
import com.huatu.handheld_huatu.business.matches.adapter.NewScEssayPastAdapter;
import com.huatu.handheld_huatu.business.matches.bean.ScHistoryPaperListData;
import com.huatu.handheld_huatu.business.matches.bean.ScHistoryPaperListResult;
import com.huatu.handheld_huatu.business.matches.bean.ScHistoryPaperTopData;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationEssayPastPaperData;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationEssayPastPaperResult;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScHistoryTag;
import com.huatu.handheld_huatu.mvppresenter.simulation.SimulationContestImpl;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PopWindowUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CommonErrorViewExsc;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.library.PullToRefreshBase;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by chq on 2019/1/21.
 */

public class NewScHistoryPaperListFragment extends BaseFragment implements NewScEssayPastAdapter.OnEsItemClickListener {

    @BindView(R.id.rlv_past_paper)
    PullRefreshRecyclerView rlv_past_paper;
    @BindView(R.id.rl_top_bar)
    RelativeLayout rl_top_bar;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_has_more)
    ImageView iv_has_more;
    @BindView(R.id.iv_top_course)
    ImageView iv_top_course;
    @BindView(R.id.tv_sc_tag)
    TextView tv_sc_tag;

    @BindView(R.id.view_no_data)
    CommonErrorViewExsc viewNoData;                         // 没有数据

    //    private EssayCheckImpl mEssayCheckImpl;
    private SimulationContestImpl mPresenter;

    private PopupWindow mPopupWindow;
    private boolean toOpenPopWindow = false;                    // 是否打开了PopWindow
    private final int POP_WINDOW_WIDTH_DP = 150;
    private final int POP_WINDOW_ITEM_HEIGHT_DP = 45;
    private int POP_WINDOW_HEIGHT_DP = 145;
    private final int TITLE_MAX_WIDTH_DP = 200;

    private CustomConfirmDialog dialog;

    //    private int typeFrom = -1;                                  // 0、行测 1、申论
    private int subjectId = 1;                                  // 当前科目

    private int curPosId = 0;
    private int curTagId;
    private int curSubject = -1;                                // 当前选择的科目

    private int pageSize = 20;                                  // 默认加载20条
    private int page = 1;

    // 模考历史PopWindow列表
    private List<SimulationScHistoryTag> mSimulationScHistoryTags;

    // 往期行测模考试卷列表数据
    private ArrayList<ScHistoryPaperListResult> mArenaData = new ArrayList<>();
    // 往期申论模考试卷列表数据
    private ArrayList<SimulationEssayPastPaperResult> mEssayData = new ArrayList<>();

    private NewScArenaPastAdapter mArAdapter;                                  // 往期行测试卷
    private NewScEssayPastAdapter mEsAdapter;                                  // 往期申论试卷
    private SimulationEssayPastPaperData mEssay;
    private ScHistoryPaperListData mAdministrative;
    private ScHistoryPaperTopData mTopCourseData;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent<SimulationContestMessageEvent> event) {
        if (event == null || event.typeExObject == null || mPresenter == null) {
            return;
        }
        if (event.type == SimulationContestMessageEvent.NEW_BASE_EVENT_TYPE_SC_EXAM_HISTORY_TAG_DATA) {
            refreshView(curPosId);                             // 获取PopWindow内容
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {
        if (event == null || event.type <= 0) {
            return;
        }
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_COMMIT_PAPER_SUCCESS) {
            // 非申论试卷交卷成功
            page = 1;
            rlv_past_paper.setVisibility(View.VISIBLE);
            loadPastPaper(curTagId);
        } else if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_SAVE_PAPER_SUCCESS) {
            // 非申论试卷交卷成功保存成功
            page = 1;
            rlv_past_paper.setVisibility(View.VISIBLE);
            loadPastPaper(curTagId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit) {
            //申论交卷成功
            if (rlv_past_paper.getRefreshableView() != null) {
                rlv_past_paper.getRefreshableView().reset();
            }
            page = 1;
            rlv_past_paper.setVisibility(View.VISIBLE);
            loadPastPaper(curTagId);
//            if (mEssayCheckImpl != null) {
//                mEssayCheckImpl.checkCountVerify(1);
//            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit_fail) {
            //申论交卷失败
            page = 1;
            rlv_past_paper.setVisibility(View.VISIBLE);
            loadPastPaper(curTagId);
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave) {
            //申论保存成功
            page = 1;
            rlv_past_paper.setVisibility(View.VISIBLE);
            loadPastPaper(curTagId);
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave_fail) {
            //申论保存失败
            page = 1;
            rlv_past_paper.setVisibility(View.VISIBLE);
            loadPastPaper(curTagId);
        }
        return true;
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_sc_hisytory_paper_list;
    }

    @Override
    protected void onInitView() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        mPresenter = new SimulationContestImpl(compositeSubscription);
//        mEssayCheckImpl = new EssayCheckImpl(compositeSubscription);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
//        mEssayCheckImpl.checkCountVerify(1);

        if (args != null) {
            subjectId = args.getInt("subjectId", 0);
        }

        initRecyclerView();
        initListener();
        initAdapter();
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        if (mPresenter != null) {
            mPresenter.getNewScHistoryTag(subjectId);
        }
    }

    private void onRefresh() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            rlv_past_paper.onRefreshComplete();
            return;
        }
        try {
            if (rlv_past_paper.getRefreshableView() != null) {
                rlv_past_paper.getRefreshableView().reset();
            } else {
                return;
            }
        } catch (Exception e) {
            return;
        }

        viewNoData.setVisibility(View.GONE);
        page = 1;
        int flag = getFlag(curTagId);
        if (flag == 1) {
            mEssayData.clear();
            loadEssayPastPaper(true, curTagId);
        } else {
            mArenaData.clear();
            loadArenaPastPaper(curTagId, true);
        }
    }

    private void onLoadMore() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            rlv_past_paper.onRefreshComplete();
            return;
        }
        page++;
        int flag = getFlag(curTagId);
        if (flag == 1) {
            loadEssayPastPaper(true, curTagId);
        } else {
            loadArenaPastPaper(curTagId, true);
        }
    }

    private void initRecyclerView() {
        // 设置每页加载的条数，判断是否是最后一页
        rlv_past_paper.getRefreshableView().setPagesize(pageSize);
        // 侧滑的实现，是配合com.nalan.swipeitem.recyclerview.SwipeItemLayout使用
        rlv_past_paper.getRefreshableView().addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(mActivity));
        rlv_past_paper.getRefreshableView().setLayoutManager(new LinearLayoutManager(mActivity));
        // 自动加载更多的回调
        rlv_past_paper.getRefreshableView().setOnLoadMoreListener(new IonLoadMoreListener() {
            @Override
            public void OnLoadMoreEvent(boolean isRetry) {
                NewScHistoryPaperListFragment.this.onLoadMore();
            }
        });
        // 加载过程中是否可以滑动
        rlv_past_paper.setPullToRefreshOverScrollEnabled(true);
        // 下拉刷新的回调
        rlv_past_paper.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                NewScHistoryPaperListFragment.this.onRefresh();
            }
        });
    }

    private void initListener() {
        iv_top_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTopCourseData != null && mTopCourseData.collectionCourseId != 0) {
                    BaseIntroActivity.newIntent(mActivity, mTopCourseData.collectionCourseId + "");
                }

            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        viewNoData.setOnReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       // 点击刷新数据
                mSimulationScHistoryTags = mPresenter.mNewScHistoryTags;
                if (mSimulationScHistoryTags == null || mSimulationScHistoryTags.size() == 0) {
                    mPresenter.getNewScHistoryTag(subjectId);
                } else {
                    // 往期考卷
                    loadPastPaper(curTagId);
                }
            }
        });

        // 顶部标题
        tv_sc_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSimulationScHistoryTags == null || mSimulationScHistoryTags.size() == 1) {
                    return;
                }
                toOpenPopWindow = true;
                refreshTitle(curPosId);
                if (toOpenPopWindow) {
                    if (mSimulationScHistoryTags == null || mSimulationScHistoryTags.size() == 0) {
                        return;
                    }
                    tv_sc_tag.measure(0, 0);
                    POP_WINDOW_HEIGHT_DP = mSimulationScHistoryTags.size() * POP_WINDOW_ITEM_HEIGHT_DP + DisplayUtil.px2dp(10);
                    int xoffdp = (DisplayUtil.px2dp(tv_sc_tag.getMeasuredWidth()) - POP_WINDOW_WIDTH_DP) / 2;
                    PopWindowUtil.showPop_showAtLocation_match(mActivity, rlv_past_paper, xoffdp, 20, R.layout.layout_pop_sc_history_paper_title, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, new PopWindowUtil.PopViewCall() {

                        @Override
                        public void popViewCall(View contentView, PopupWindow popWindow) {
                            mPopupWindow = popWindow;
                            contentView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mPopupWindow != null) {
                                        mPopupWindow.dismiss();
                                    }
                                }
                            });
                            ListView pop_sc_title_list_view_id = (ListView) contentView.findViewById(R.id
                                    .pop_sc_title_list_view_id);
                            CommonAdapter scTitlePopAdapter = new CommonAdapter<SimulationScHistoryTag>(mActivity.getApplicationContext(),
                                    mSimulationScHistoryTags, R.layout.list_item_sc_record_pop_title_layout) {
                                @Override
                                public void convert(ViewHolder holder, final SimulationScHistoryTag item, final int position) {
                                    holder.setText(R.id.item_sc_record_pop_title_tv, item.getName());
                                    holder.setBoldFaceType(R.id.item_sc_record_pop_title_tv, Typeface.NORMAL);
                                    if (position == curPosId) {
                                        holder.setTextColor(R.id.item_sc_record_pop_title_tv, ContextCompat.getColor(mActivity, R.color.black));
                                        holder.setViewBackgroundColor(R.id.item_sc_record_pop_title_tv, R.color.teacher_detail_margin_color);
                                    } else {
                                        holder.setTextColor(R.id.item_sc_record_pop_title_tv, ContextCompat.getColor(mActivity, R.color.black250));
                                        holder.setViewBackgroundColor(R.id.item_sc_record_pop_title_tv, R.color.white);
                                    }
                                    holder.setViewOnClickListener(R.id.item_sc_record_pop_title_tv, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            LogUtils.d(TAG, item);
                                            toOpenPopWindow = false;
                                            curTagId = item.getId();
                                            curSubject = item.getSubject();
                                            mArAdapter.setCurSubject(curSubject);       // 为了试卷下载传入科目信息
                                            curPosId = position;
                                            loadPastPaper(curTagId);
                                            if (mPopupWindow != null) {
                                                mPopupWindow.dismiss();
                                            }
                                        }
                                    });
                                }
                            };
                            pop_sc_title_list_view_id.setAdapter(scTitlePopAdapter);
                        }

                        @Override
                        public void popViewDismiss() {
                            toOpenPopWindow = false;
                            mPopupWindow = null;
                            refreshTitle(curPosId);
                        }
                    });
                } else {
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
            }
        });
    }

    private void initAdapter() {
        mArAdapter = new NewScArenaPastAdapter(mActivity, mArenaData, compositeSubscription, subjectId);
        mEsAdapter = new NewScEssayPastAdapter(mActivity, mEssayData, this);
    }

    // 获取到Tag信息
    private void refreshView(int pos) {
        mSimulationScHistoryTags = mPresenter.mNewScHistoryTags;
        if (mSimulationScHistoryTags == null || mSimulationScHistoryTags.size() == 0) {
            showEmptyView(3);
            return;
        }
        refreshTitle(pos);
        if (tv_sc_tag != null && mSimulationScHistoryTags != null) {
            SimulationScHistoryTag var = mSimulationScHistoryTags.get(pos);
            if (var != null) {
                curTagId = var.getId();
                curSubject = var.getSubject();
            }
        }
        loadPastPaper(curTagId);

    }

    // 刷新标题
    private void refreshTitle(int pos) {
        if (tv_sc_tag != null && mSimulationScHistoryTags != null) {
            SimulationScHistoryTag var = mSimulationScHistoryTags.get(pos);
            if (var != null && var.getName() != null) {
                tv_sc_tag.setText(var.getName());
            }
        }
        if (mSimulationScHistoryTags == null || mSimulationScHistoryTags.size() < 2) {
            iv_has_more.setVisibility(View.GONE);
        } else {
            iv_has_more.setVisibility(View.VISIBLE);
        }
    }

    private int getFlag(int tagId) {
        mSimulationScHistoryTags = mPresenter.mNewScHistoryTags;
        if (mSimulationScHistoryTags == null || mSimulationScHistoryTags.size() == 0) {
            return 0;
        }
        for (SimulationScHistoryTag mSimulationScHistoryTag : mSimulationScHistoryTags) {
            if (mSimulationScHistoryTag.getId() == tagId) {
                return mSimulationScHistoryTag.getFlag();
            }
        }
        return 0;
    }

    /**
     * 获取往期模考试卷
     */
    private void loadPastPaper(int tagId) {
        rlv_past_paper.getRefreshableView().reset();
        mEssayData.clear();
        mArenaData.clear();
        page = 1;
        int flag = getFlag(tagId);
        loadTopCourse(curSubject, tagId);
        if (flag == 1) {                            // 历史申论模考试卷
            if (mEsAdapter != null) {
                rlv_past_paper.getRefreshableView().setRecyclerAdapter(mEsAdapter);
            }
            loadEssayPastPaper(false, tagId);
        } else {                                    // 历史行测模考试卷
            if (mArAdapter != null) {
                rlv_past_paper.getRefreshableView().setRecyclerAdapter(mArAdapter);
            }
            loadArenaPastPaper(tagId, false);
        }
    }

    // 获取申论之外的往期模考试卷
    private void loadArenaPastPaper(int tagId, boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        }
        if (!isRefresh) {
            mActivity.showProgress();
        }
        ServiceProvider.getNewScHistoryPaper(compositeSubscription, tagId, page, curSubject, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                rlv_past_paper.onRefreshComplete();
                if (model.data != null) {
                    mAdministrative = (ScHistoryPaperListData) model.data;
                }
                if (mAdministrative != null) {

                    mArenaData.addAll(mAdministrative.result);
                }

                if (mArenaData.size() == 0) {
                    showEmptyView(3);
                } else {
                    viewNoData.setVisibility(View.GONE);
                }
                if (mAdministrative.next != 1) {
                    // 判断是不是最后一页
                    rlv_past_paper.getRefreshableView().checkloadMore(0);
                }
                // 隐藏加载动画
                rlv_past_paper.getRefreshableView().hideloading();
                mArAdapter.notifyDataSetChanged();
                if (page == 1) {
                    // 下拉刷新后显示第一行
                    rlv_past_paper.getRefreshableView().scrollToPosition(0);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
                rlv_past_paper.onRefreshComplete();
                ToastUtils.showEssayToast("网络连接错误，请稍后重试");
            }
        });
    }

    // 获取申论模考往期试卷
    private void loadEssayPastPaper(boolean isRefresh, int mTagId) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        }
        if (!isRefresh) {
            mActivity.showProgress();
        }
        ServiceProvider.getEssayPastMatchList(compositeSubscription, page, mTagId, 14, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                rlv_past_paper.onRefreshComplete();
                if (model.data != null) {
                    mEssay = (SimulationEssayPastPaperData) model.data;
                }
                if (mEssay != null) {
                    mEssayData.addAll(mEssay.result);
                }
                if (mEssayData == null || mEssayData.size() == 0) {
                    showEmptyView(3);
                } else {
                    viewNoData.setVisibility(View.GONE);
                }
                if (mEssay.next != 1) {
                    // 判断是不是最后一页
                    rlv_past_paper.getRefreshableView().checkloadMore(0);
                }
                // 隐藏加载动画
                rlv_past_paper.getRefreshableView().hideloading();
                mEsAdapter.notifyDataSetChanged();
                if (page == 1) {
                    // 下拉刷新后显示第一行
                    rlv_past_paper.getRefreshableView().scrollToPosition(0);
                }

//                if (mEssayCheckImpl != null && EssayCheckDataCache.getInstance().maxCorrectTimes == -1) {
//                    if (mEssayData != null) {
//                        if (mEssayData.size() > 0) {
//                            mEssayCheckImpl.getCheckCountNum(1, mEssayData.get(0).paperId);
//                        }
//                    }
//                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
                rlv_past_paper.onRefreshComplete();
                ToastUtils.showEssayToast("暂无数据，请稍后重试");
            }
        });
    }

    // 顶部的图片信息
    private void loadTopCourse(int curSubject, int tagId) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        }

        ServiceProvider.getNewScHistoryPaperTopCourse(compositeSubscription, curSubject, tagId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (model != null) {
                    mTopCourseData = (ScHistoryPaperTopData) model.data;
                }
                if (mTopCourseData != null) {
                    if (mTopCourseData.advertUrl != null && iv_top_course != null) {
                        iv_top_course.setVisibility(View.VISIBLE);
                        // GlideApp.with(mActivity).load(mTopCourseData.advertUrl).into(iv_top_course);
                        ImageLoad.load(mActivity, mTopCourseData.advertUrl, iv_top_course);

                    } else {
                        if (iv_top_course != null) {
                            iv_top_course.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (iv_top_course != null) {
                    iv_top_course.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onEsItemClick(int position) {
        // 点击申论往期模考
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        }
        final SimulationEssayPastPaperResult item = mEssayData.get(position);
        if (position - 1 >= mEssayData.size()) {
            return;
        }

        MultiExerciseResult multiExerciseResult = new MultiExerciseResult();
        multiExerciseResult.paperName = item.paperName;
        multiExerciseResult.paperId = item.paperId;
        multiExerciseResult.lastType = 1;

        Bundle bundle = new Bundle();
        bundle.putInt("staticCorrectMode", 1);      // 模考实智能是智能答题

        EssayRoute.navigateToAnswer(mActivity, compositeSubscription, false, null, multiExerciseResult, bundle);
    }

    /**
     * 无数据
     * 1、网络不好，点击重试！
     * 3、往期模考试卷：暂无试卷
     */
    private void showEmptyView(int type) {
        viewNoData.setVisibility(View.VISIBLE);
        switch (type) {
            case 1:
                viewNoData.setErrorText("网络不好，点击重试！");
                break;
            case 3:
                viewNoData.setErrorText("暂无试卷！\n去看看其他栏目吧！");
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }
}
