package com.huatu.handheld_huatu.business.matches.fragment;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.GiftWebViewActivity;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamCheckDetailV2;
import com.huatu.handheld_huatu.business.matches.customview.ShakeImageView;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.event.match.MatchEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.exercise.ScMatchMetaBean;
import com.huatu.handheld_huatu.mvpmodel.matchs.EssayScReportBean;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.PopWindowUtil;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.ListViewForScroll;
import com.huatu.handheld_huatu.view.custom.NewScDesTipGraphView;
import com.huatu.handheld_huatu.view.custom.NewSimulationContestGraphView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * sc_report essay 申论模考报告
 * 申论模考报告
 * 数据内容是从这里自己请求的
 */
public class ScReportEssayFragment extends AbsSimulationContestFragment {

    @BindView(R.id.sc_answer_report_type_tv)
    TextView scAnswerReportTypeTv;
    @BindView(R.id.sc_answer_report_self_score_tv)
    TextView scAnswerReportSelfScoreTv;
    @BindView(R.id.sc_answer_report_ranking_samejob_self_tv)
    TextView scAnswerReportRankingSamejobSelfTv;
    @BindView(R.id.sc_answer_report_ranking_samejob_allnum_tv)
    TextView scAnswerReportRankingSamejobAllnumTv;
    @BindView(R.id.sc_answer_report_ranking_total)
    TextView scAnswerReportRankingTotal;
    @BindView(R.id.sc_answer_report_total_num)
    TextView scAnswerReportRankingTotalNum;
    @BindView(R.id.sc_answer_report_max_score_tv)
    TextView scAnswerReportMaxScoreTv;

    @BindView(R.id.tv_count)
    TextView tvCount;               // 总题数
    @BindView(R.id.tv_no_ans)
    TextView tvNoAns;               // 未答题数
    @BindView(R.id.tv_score)
    TextView tvScore;               // 得分数
    @BindView(R.id.tv_all_score)
    TextView tvAllScore;            // 总分数
    @BindView(R.id.tv_time)
    TextView tvTime;                // 用时

    @BindView(R.id.sc_answer_report_knowledge_list_view)
    ListViewForScroll scAnswerReportKnowledgeListView;
    @BindView(R.id.simulation_contest_main_error_layout)
    CommonErrorView layoutErrorView;
    @BindView(R.id.sc_report_graphView)
    NewSimulationContestGraphView scReportGraphView;
    @BindView(R.id.sc_report_graphView_des)
    NewScDesTipGraphView scReportGraphViewDes;
    @BindView(R.id.sc_report_scrollv)
    NestedScrollView scReportScrollv;
    @BindView(R.id.sc_report_graphView_scrollView)
    HorizontalScrollView scReportGraphViewScrollView;

    @BindView(R.id.rl_gift)
    RelativeLayout rlGift;                          // 大礼包布局
    @BindView(R.id.iv_gift)
    ShakeImageView ivGift;                          // 礼包图片

    private int requestType;
    private boolean isReportFinished = true;

    private long essay_paperId;
    private EssayScReportBean essayScReportBean;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_essay_sc_report_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent<SimulationContestMessageEvent> event) {
        if (event == null || !(event.typeExObject instanceof SimulationContestMessageEvent) || mPresenter == null) {
            return;
        }
        if (event.type == SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_essay_getEssayReport) {

            if (mPresenter != null) {
                essayScReportBean = mPresenter.essayScReportBean;
            }
            refreshView(essayScReportBean);
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate");
    }

    int typehSc;                        // 要分享哪个模考报告 1、行测 2、申论 3、全部

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {
        if (event == null) {
            return;
        }
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_SHARE) {                       // 分享
            if (event.extraBundle != null) {
                int type = event.extraBundle.getInt("share_type", 0);
                if (type == 3) {                                                                  // 模考报告页面中的分享
                    typehSc = event.extraBundle.getInt("typeSc");
                }
            }
        } else if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_SHARE_WAY) {            // 分享渠道回传
            if (typehSc == 2) {    // 分享报告
                String shareWay = event.extraBundle.getString("share_way");
                if (essayScReportBean != null)
                    StudyCourseStatistic.simulatedShareResult("模考大赛", "申论", "公务员",
                            String.valueOf(essay_paperId), essayScReportBean.name, essayScReportBean.score, shareWay);
            }
        }
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        if (args != null) {
            requestType = args.getInt("request_type");
            essay_paperId = args.getLong("essay_paperId");
            isReportFinished = args.getBoolean("is_report_finished", true);
            LogUtils.d("ScReportArenaFragment", "requestType  " + requestType);
            LogUtils.d("ScReportArenaFragment", "isReportFinished  " + isReportFinished);
        }
        if (isReportFinished) {
            layoutErrorView.setVisibility(View.GONE);
        } else {
            layoutErrorView.setVisibility(View.VISIBLE);
            layoutErrorView.setErrorImage(R.mipmap.report_wait);
            layoutErrorView.setErrorText("该考试时间未结束，暂无成绩统计。\n"
                    + "考试结束后再来查看吧。");
            layoutErrorView.setback_finishVis(View.GONE);
        }

        scReportScrollv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY - oldScrollY > 10) {
                    EventBus.getDefault().post(new MatchEvent(MatchEvent.GIFT_SCROLL_UP));
                } else if (scrollY - oldScrollY < -10) {
                    EventBus.getDefault().post(new MatchEvent(MatchEvent.GIFT_SCROLL_DOWN));
                }
            }
        });
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        if (essay_paperId > 0) {
            if (mPresenter != null) {
                mPresenter.getEssayReport(essay_paperId);
            }
        }
    }

    private void refreshView(EssayScReportBean essayScReportBean) {
        if (essayScReportBean == null || Method.isActivityFinished(mActivity)) {
            return;
        }

        StudyCourseStatistic.simulatedReport("申论", "公务员",
                String.valueOf(essay_paperId), essayScReportBean.name, essayScReportBean.score, essayScReportBean.rcount, essayScReportBean.expendTime);

        scAnswerReportTypeTv.setText(essayScReportBean.name);
        scAnswerReportSelfScoreTv.setText(ArenaHelper.setNoZero(String.valueOf((essayScReportBean.score))) + " ");

        if (essayScReportBean.matchMeta != null) {
            scAnswerReportRankingSamejobSelfTv.setText(
                    essayScReportBean.matchMeta.getPositionRank() + "");
            scAnswerReportRankingSamejobAllnumTv.setText(
                    "/" + essayScReportBean.matchMeta.getPositionCount() + "");
        }

        if (essayScReportBean.cardUserMeta != null) {
            scAnswerReportRankingTotal.setText(essayScReportBean.cardUserMeta.rank + "");
            scAnswerReportRankingTotalNum.setText("/" + essayScReportBean.cardUserMeta.total + "");
            scAnswerReportMaxScoreTv.setText(ArenaHelper.setNoZero(essayScReportBean.cardUserMeta.max + "")+ " ");
        }

        if (essayScReportBean.matchMeta != null) {
            ScMatchMetaBean.ScoreLineEntity scoreLineEntity = essayScReportBean.matchMeta.getScoreLine();
            if (scoreLineEntity != null) {
                List<String> categories = scoreLineEntity.getCategories();
                if (categories != null && categories.size() > 1) {
                    scReportGraphViewDes.setTextDes(categories.get(0), categories.get(1));
                }
                List<ScMatchMetaBean.ScoreLineEntity.SeriesEntity> series = scoreLineEntity.getSeries();
                if (series != null) {
                    int size = series.size();
                    float[] scores = new float[size + 1];
                    float[] scoresAverage = new float[size + 1];
                    String[] date = new String[size + 1];
                    scores[0] = 0;
                    scoresAverage[0] = 0;
                    date[0] = "";
                    for (int i = 0; i < series.size(); i++) {
                        ScMatchMetaBean.ScoreLineEntity.SeriesEntity var = series.get(i);
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
                    scReportGraphView.setDatas(scores, scoresAverage, date);
                    if (scReportGraphViewScrollView != null) {
                        scReportGraphViewScrollView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scReportGraphViewScrollView.fullScroll(View.FOCUS_RIGHT);
                            }
                        }, 500);
                    }
                }
            }
        }

        int seconds = essayScReportBean.expendTime;
        int min = seconds / 60;
        int sec = seconds % 60;
        String strSec = String.valueOf(sec);
        if (sec < 10) {
            strSec = "0" + sec;
        }

        tvCount.setText(essayScReportBean.questionCount + " ");
        tvNoAns.setText(essayScReportBean.ucount + " ");
        tvScore.setText(ArenaHelper.setNoZero(essayScReportBean.score + "") + " ");
        tvAllScore.setText("/" + ArenaHelper.setNoZero(essayScReportBean.totalScore + "") + "，总计用时");
        //大礼包
        if (essayScReportBean.hasGift == 1 && essayScReportBean.rightImgUrl != null) {
            rlGift.setVisibility(View.VISIBLE);
            //Glide.with(mActivity).load(essayScReportBean.rightImgUrl).diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivGift);
            ImageLoad.load(mActivity, essayScReportBean.rightImgUrl, ivGift, DiskCacheStrategy.AUTOMATIC);
            ivGift.startAnim();
        } else {
            rlGift.setVisibility(View.GONE);
        }


        if (min > 0) {
            tvTime.setText(min + "′" + strSec + "″ ");
        } else {
            tvTime.setText(strSec + "″ ");
        }

        scAnswerReportKnowledgeListView.setAdapter(new CommonAdapter<EssayScReportBean.QuestionListEntity>(
                mActivity, essayScReportBean.questionList, R.layout.sc_essay_report_item_layout) {
            @Override
            public void convert(ViewHolder holder, EssayScReportBean.QuestionListEntity item, int position) {
                if (item.type == 1) {
                    holder.setText(R.id.type_name, "问题" + item.sort + "－概括归纳题");
                } else if (item.type == 2) {
                    holder.setText(R.id.type_name, "问题" + item.sort + "－综合分析题");
                } else if (item.type == 3) {
                    holder.setText(R.id.type_name, "问题" + item.sort + "－解决问题题");
                } else if (item.type == 4) {
                    holder.setText(R.id.type_name, "问题" + item.sort + "－应用写作");
                } else if (item.type == 5) {
                    holder.setText(R.id.type_name, "问题" + item.sort + "－文章写作");
                }
                holder.setText(R.id.sc_essay_content, "得分" + ArenaHelper.setNoZero(item.getExamScore() + "")
                        + "/" + ArenaHelper.setNoZero(item.score + "") + "，用时"
                        + TimeUtils.getSecond2MinTime(item.spendTime) + "，" + item.getInputWordNum() + "字");
            }
        });

        if (scReportScrollv != null) {
            scReportScrollv.scrollTo(0, 0);
        }
        if (scAnswerReportTypeTv != null) {
            scAnswerReportTypeTv.setFocusableInTouchMode(true);
            scAnswerReportTypeTv.requestFocus();
        }
    }

    @OnClick({R.id.sc_answer_report_analysis_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sc_answer_report_analysis_all:
                onClickAnalysisAll();
                break;
        }
    }

    @OnClick(R.id.rl_gift)
    public void onClickBigGift() {
        if (essayScReportBean.hasGetBigGift == 0) {
            //没领过大礼包
            showBigGift();
        } else {
            // 2018/10/24 已经领过大礼包了 跳转H5页面
            GiftWebViewActivity.newInstance(getContext(), essayScReportBean.giftHtmlUrl);
        }
    }

    private void showBigGift() {
        PopWindowUtil.showPopInCenter(mActivity, scReportScrollv, 0, 0, R.layout.layout_biggift_open_pop,
                DisplayUtil.px2dp(DisplayUtil.getScreenWidth()), DisplayUtil.px2dp(DisplayUtil.getScreenHeight()), new PopWindowUtil.PopViewCall() {
                    @Override
                    public void popViewCall(View contentView, final PopupWindow popWindow) {


                        final ImageView iv_bag = (ImageView) contentView.findViewById(R.id.iv_bag);                // 去发红包

                        ImageView ivClose = (ImageView) contentView.findViewById(R.id.iv_close);                    // 关闭按钮

                        TextView tvOpen = (TextView) contentView.findViewById(R.id.tv_open);
                        if (essayScReportBean.giftImgUrl != null) {
                            //  Glide.with(mActivity).load(essayScReportBean.giftImgUrl).diskCacheStrategy(DiskCacheStrategy.RESULT).into(iv_bag);
                            ImageLoad.load(mActivity, essayScReportBean.giftImgUrl, iv_bag, DiskCacheStrategy.AUTOMATIC);
                        }
                        // 规则按钮
                        final ValueAnimator animatorBagIn = ValueAnimator.ofFloat(0.2f, 1f);
                        animatorBagIn.setInterpolator(new AnticipateOvershootInterpolator());
                        animatorBagIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float values = (float) animation.getAnimatedValue();
                                iv_bag.setScaleX(values);
                                iv_bag.setScaleY(values);
                            }
                        });
                        animatorBagIn.setDuration(900);
                        animatorBagIn.start();

                        ivClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popWindow.dismiss();
                            }
                        });

                        tvOpen.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GiftWebViewActivity.newInstance(getContext(), essayScReportBean.giftHtmlUrl);
                                popWindow.dismiss();
                            }
                        });

                    }

                    @Override
                    public void popViewDismiss() {
                    }
                });
    }

    public void onClickBack() {
        mActivity.finish();
    }

    public void onClickAnalysisAll() {
        Bundle mEssayBundle = new Bundle();
        if (essayScReportBean != null) {
            mEssayBundle.putString("titleView", essayScReportBean.name);
        }
        mEssayBundle.putBoolean("isSingle", false);
        mEssayBundle.putBoolean("isFromArgue", false);
        mEssayBundle.putLong("paperId", essay_paperId);
        if (essayScReportBean != null) {
            mEssayBundle.putLong("answerId", essayScReportBean.id);
        }
        mEssayBundle.putInt("type", 1);
        mEssayBundle.putInt("correctMode", 1);

        // 批改详情
        EssayExamCheckDetailV2.lanuch(mActivity, mEssayBundle);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        essayScReportBean = (EssayScReportBean) savedInstanceState.getSerializable("essayScReportBean");
        if (essayScReportBean != null) {
            refreshView(essayScReportBean);
        } else {
            onLoadData();
        }
    }

    @Override
    protected void onSaveState(Bundle savedInstanceState) {
        super.onSaveState(savedInstanceState);
        savedInstanceState.putSerializable("essayScReportBean", essayScReportBean);
    }
}
