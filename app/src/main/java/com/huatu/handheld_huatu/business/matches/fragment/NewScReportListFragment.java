package com.huatu.handheld_huatu.business.matches.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.business.matches.activity.ScReportActivity;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScHistory;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScHistoryTag;
import com.huatu.handheld_huatu.mvppresenter.simulation.SimulationContestImpl;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.PopWindowUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CommonErrorViewExsc;
import com.huatu.handheld_huatu.view.custom.NewScDesTipGraphView;
import com.huatu.handheld_huatu.view.custom.NewSimulationContestGraphView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by chq on 2019/1/25.
 * 新的模考报告页面
 */

public class NewScReportListFragment extends BaseFragment {

    @BindView(R.id.rl_top_bar)
    RelativeLayout rl_top_bar;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.iv_has_more)
    ImageView iv_has_more;
    @BindView(R.id.tv_sc_tag)
    TextView tv_sc_tag;
    @BindView(R.id.lv_sc_report)
    ListView lv_sc_report;
    @BindView(R.id.view_no_data)
    CommonErrorViewExsc viewNoData;

    private SimulationContestImpl mPresenter;

    // 顶部的折线图
    private View mScRecordReportHeadView;
    private NewSimulationContestGraphView mSimulationContestGraphView;
    private NewScDesTipGraphView mScDesTipGraphView;
    private HorizontalScrollView mHorizontalScrollView;

    //    private int typeFrom = 0;                                               // 0、行测 1、申论
    private int subjectId;                                                  // 当前是从哪个科目进来的

    private List<SimulationScHistoryTag> mSimulationScHistoryTags;          // 右上角 年份/分类 Tag数据

    private int curPosId = 0;                                               // 当前选中的Tag Index
    private int curTagId;                                                   // 当前选中的Tag Id
    private int curSubjectId;                                               // 当前选中的Tag 科目Id

    private PopupWindow mPopupWindow;                                       // Tag弹窗
    private boolean toOpenPopWindow;
    private final int POP_WINDOW_WIDTH_DP = 150;
    private final int POP_WINDOW_ITEM_HEIGHT_DP = 45;
    private int POP_WINDOW_HEIGHT_DP = 145;

    // 申论报告
    private SimulationScHistory mSimulationScHistory;

    // 行测和其他报告
    private ArrayList<SimulationScHistory.ListEntity> dataList = new ArrayList<>();
    private CommonAdapter<SimulationScHistory.ListEntity> mAdapter;
    private boolean isAddHeadView = false;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent<SimulationContestMessageEvent> event) {
        if (event == null || event.typeExObject == null || mPresenter == null) {
            return;
        }
        if (event.type == SimulationContestMessageEvent.NEW_BASE_EVENT_TYPE_SC_EXAM_HISTORY_TAG_DATA) {
            // 获取标题PopWindow内容
            refreshView(curPosId);
        } else if (event.type == SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_essay_getEssayScHistoryList) {
            // 我的申论报告
            mSimulationScHistory = mPresenter.simulationScHistory;
            refreshListViews();
        } else if (event.type == SimulationContestMessageEvent.NEW_BASE_EVENT_TYPE_SC_EXAM_REPORT_DATA) {
            // 行测和其他报告
            mSimulationScHistory = mPresenter.getNewSimulationHistoryData();
            refreshListViews();
        }

    }

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_new_sc_report_list;
    }

    @Override
    protected void onInitView() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        mPresenter = new SimulationContestImpl(compositeSubscription);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (args != null) {
            subjectId = args.getInt("subjectId", 1);
        }
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

    /**
     * 弹窗点击
     */
    private void initListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        viewNoData.setOnReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击刷新数据
                mSimulationScHistoryTags = mPresenter.mNewScHistoryTags;
                if (mSimulationScHistoryTags == null || mSimulationScHistoryTags.size() == 0) {
                    mPresenter.getNewScHistoryTag(subjectId);
                } else {
                    //  获取报告
                    refreshView(curPosId);
                }
            }
        });
        //顶部标题
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
                    PopWindowUtil.showPop_showAtLocation_match(mActivity, rl_top_bar, xoffdp, 20, R.layout.layout_pop_sc_history_paper_title, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, new PopWindowUtil.PopViewCall() {

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
                                            curSubjectId = item.getSubject();
                                            curPosId = position;
                                            // 刷新页面
                                            refreshView(curPosId);
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

    /**
     * 我的模考报告adapter
     */
    public void initAdapter() {
        mAdapter = new CommonAdapter<SimulationScHistory.ListEntity>(mActivity.getApplicationContext(), dataList, R.layout.new_list_item_sc_record_layout) {
            @Override
            public void convert(ViewHolder holder, final SimulationScHistory.ListEntity item, final int position) {
                if (position == 0) {
                    holder.setViewVisibility(R.id.item_sc_record_head_view, View.VISIBLE);
                } else {
                    holder.setViewVisibility(R.id.item_sc_record_head_view, View.GONE);
                }
                holder.setText(R.id.item_sc_record_title_tv, item.getName());
                holder.setText(R.id.item_sc_record_sign_up_time_tv, TimeUtils.getFormatData(item.getStartTime()));
                holder.setText(R.id.item_sc_record_sign_up_num_tv, "共" + item.getTotal() + "人参加 ");
                holder.setViewOnClickListener(R.id.item_sc_record_enter_sc_report_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isFastDoubleClick()) return;
                        Intent intent = new Intent(mActivity, ScReportActivity.class);
                        if (getFlag(curTagId) ==1) {
                            intent.putExtra("tag", 2); //报告详情页的2表示申论
                        } else {
                            intent.putExtra("tag", 1); //报告详情页的2表示申论，1表示其他，故加1
                        }
                        Bundle arg = new Bundle();
                        arg.putLong("practice_id", item.paperId);
                        arg.putBoolean("is_report_finished", true);
                        arg.putLong("essay_paperId", item.essayPaperId);
                        intent.putExtra("arg", arg);
                        mActivity.startActivity(intent);
                    }
                });
            }
        };
        lv_sc_report.setAdapter(mAdapter);
    }

    private void refreshView(int pos) {
        mSimulationScHistoryTags = mPresenter.mNewScHistoryTags;
        if (mSimulationScHistoryTags == null || mSimulationScHistoryTags.size() == 0) {
            showEmptyView(2);
            return;
        }
        refreshTitle(pos);
        refreshCurTagId(pos);
        int flag = getFlag(curTagId);
        if (flag == 1) {                                            // 我的申论报告
            if (mPresenter != null) {
                mPresenter.getEssayScHistoryList(curTagId);
            }
        } else {                                                    // 我的行测报告
            if (mPresenter != null) {
                mPresenter.getNewScReport(curTagId, curSubjectId);
            }
        }
    }

    private void refreshTitle(int pos) {
        if (tv_sc_tag != null && mSimulationScHistoryTags != null) {
            SimulationScHistoryTag var = mSimulationScHistoryTags.get(pos);
            tv_sc_tag.setText(var.getName());
        }
        if (mSimulationScHistoryTags == null || mSimulationScHistoryTags.size() < 2) {
            iv_has_more.setVisibility(View.GONE);
        } else {
            iv_has_more.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 根据选择的第几个item，得到item id
     */
    private int refreshCurTagId(int pos) {
        if (tv_sc_tag != null && mSimulationScHistoryTags != null) {
            SimulationScHistoryTag var = mSimulationScHistoryTags.get(pos);
            if (var != null) {
                curTagId = var.getId();
                curSubjectId = var.getSubject();
                return curTagId;
            }
        }
        return 0;
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
     * 刷新我的模考列表
     */
    private void refreshListViews() {
        if (mSimulationScHistory != null) {
            onSuccess(mSimulationScHistory.getList(), true);
            SimulationScHistory.LineEntity var1 = mSimulationScHistory.getLine();
            recordReportHeadView();
            if (var1 != null && mSimulationContestGraphView != null && mScDesTipGraphView != null) {
                List<String> categories = var1.getCategories();
                if (categories != null && categories.size() > 1) {
                    if (mSimulationScHistoryTags != null
                            && curPosId < mSimulationScHistoryTags.size()
                            && mSimulationScHistoryTags.get(curPosId).getFlag() != 1) {
                        mScDesTipGraphView.setShowType(1);
                    } else {
                        mScDesTipGraphView.setShowType(0);
                    }
                    mScDesTipGraphView.setTextDes(categories.get(0), categories.get(1));
                }
                List<SimulationScHistory.LineEntity.SeriesEntity> series = var1.getSeries();
                if (series != null) {
                    int size = series.size();
                    float[] scores = new float[size + 1];
                    float[] scoresAverage = new float[size + 1];
                    String[] date = new String[size + 1];
                    scores[0] = 0;
                    scoresAverage[0] = 0;
                    date[0] = "";
                    for (int i = 0; i < series.size(); i++) {
                        SimulationScHistory.LineEntity.SeriesEntity var = series.get(i);
                        if (var != null) {
                            String data = var.getName();
                            if (data != null) {
                                date[i + 1] = data;
                            }
                            List<Float> values = var.getData();
                            if (values != null && values.size() == 2) {
                                scoresAverage[i + 1] = values.get(0);
                                scores[i + 1] = values.get(1);
                            }
                        }
                    }
                    mSimulationContestGraphView.setDatas(scores, scoresAverage, date, 1);
                    if (mHorizontalScrollView != null) {
                        mHorizontalScrollView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mHorizontalScrollView.fullScroll(View.FOCUS_RIGHT);
                            }
                        }, 300);
                    }
                }
            }
        } else {
            dataList.clear();
            recordReportHeadView();
            onSuccess(dataList, true);
            showEmptyView(2);
        }
    }

    public void onSuccess(List list, boolean isRefresh) {
        if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
            return;
        }
        if (isRefresh) {
            dataList.clear();
            dataList.addAll(list);
            lv_sc_report.setSelection(0);
        } else {
            dataList.addAll(list);
        }
        if (dataList.isEmpty()) {
            showEmptyView(2);
        } else {
            viewNoData.setVisibility(View.GONE);
        }
        mAdapter.setDataAndNotify(dataList);
    }

    /**
     * 添加我的模考列表头（折线图）
     */
    private void recordReportHeadView() {
        if (!dataList.isEmpty()) {
            if (mScRecordReportHeadView == null) {
                initReportHeadView();
            }
            if (!isAddHeadView) {
                isAddHeadView = true;
                lv_sc_report.addHeaderView(mScRecordReportHeadView);
            }
        } else {
            if (mScRecordReportHeadView != null) {
                isAddHeadView = false;
                lv_sc_report.removeHeaderView(mScRecordReportHeadView);
            }
        }
    }

    /**
     * 初始化折线图
     */
    private void initReportHeadView() {
        if (lv_sc_report != null) {
            mScRecordReportHeadView = LayoutInflater.from(mActivity).inflate(R.layout.new_list_item_sc_record_report_graph_layout, null);
            mSimulationContestGraphView = (NewSimulationContestGraphView) mScRecordReportHeadView.findViewById(R.id.record_sc_report_graphView);
            mScDesTipGraphView = (NewScDesTipGraphView) mScRecordReportHeadView.findViewById(R.id.record_sc_report_graphView_des);
            mHorizontalScrollView = (HorizontalScrollView) mScRecordReportHeadView.findViewById(R.id.record_sc_report_graphView_scrollview);
        }
    }

    /**
     * 无数据
     * 1、网络不好，点击重试！
     * 2、我的模考报告：还没参加过模考？\n快去报名吧！
     * 3、往期模考试卷：暂无试卷
     */
    private void showEmptyView(int type) {
        viewNoData.setVisibility(View.VISIBLE);
        switch (type) {
            case 1:
                viewNoData.setErrorText("网络不好，点击重试！");
                break;
            case 2:
                viewNoData.setErrorText("还没参加过模考？\n快去报名吧！");
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
