package com.huatu.handheld_huatu.business.matchsmall.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.ExplandListAdapter;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.adapter.ArenaStagePointTreeAdapter;
import com.huatu.handheld_huatu.business.arena.customview.ArenaAnswerCardViewNew;
import com.huatu.handheld_huatu.business.arena.setting.ArenaViewSettingManager;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.LUtils;
import com.huatu.handheld_huatu.business.matchsmall.adapter.ExpandableCardAdapter;
import com.huatu.handheld_huatu.business.matchsmall.adapter.StageRankExplandAdapter;
import com.huatu.handheld_huatu.business.matchsmall.customview.ExpandableCardView;
import com.huatu.handheld_huatu.business.matchsmall.customview.ViewCircleBar;
import com.huatu.handheld_huatu.business.matchsmall.customview.ViewSmoothLine;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.CourseWorkReportBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaDetailBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.PaperBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.StageReportBean;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.StageReportOtherBean;
import com.huatu.handheld_huatu.mvpmodel.me.ShareInfoBean;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaExamPresenterImpl;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamMainView;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.ListViewForScroll;
import com.huatu.handheld_huatu.view.NoScrollListView;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 阶段测评报告页
 */
public class StageReportActivity extends BaseActivity implements NightSwitchInterface, ArenaExamMainView {

    @BindView(R.id.tv_commit_time)
    TextView tvCommitTime;                      // 提交时间
    @BindView(R.id.view_circle_bar)
    ViewCircleBar circleBar;                    // 圆进度条
    @BindView(R.id.ll_no_show_all)
    LinearLayout llNoShowAll;                   // 不全显示报告的tip
    @BindView(R.id.tv_score)
    TextView tvScore;                           // 我的得分
    @BindView(R.id.tv_all_score)
    TextView tvAllScore;                        // 总分
    @BindView(R.id.tv_time)
    TextView tvTime;                            // 总用时
    @BindView(R.id.tv_top_score)
    TextView tvTopScore;                        // 最高得分
    @BindView(R.id.tv_ave_score)
    TextView tvAveScore;                        // 平均得分
    @BindView(R.id.tv_update_time)
    TextView tvUpdateTime;                      // 更新时间

    @BindView(R.id.ll_remark)
    LinearLayout llRemark;                      // 评价布局，如果没有按时交卷，这部分隐藏
    @BindView(R.id.iv_header)
    ImageView ivHeader;                         // 我的头像
    @BindView(R.id.tv_nick)
    TextView tvNick;                            // 我的昵称
    @BindView(R.id.tv_remark)
    TextView tvRemark;                          // 老师评语
    @BindView(R.id.tv_teacher_name)
    TextView tvTeacherName;                     // 老师名称
    @BindView(R.id.sl_statistics)
    ViewSmoothLine smoothLine;                  // 成绩分布统计控件
    @BindView(R.id.tv_my_rank)
    TextView tvMyRank;                          // 我的排名
    @BindView(R.id.rank_list)
    NoScrollListView rankListView;              // 排名信息，通过adapter可伸缩
    @BindView(R.id.tv_rank_more)
    TextView tvMore;                            // 排名点击更多
    @BindView(R.id.tv_survey_content)
    TextView tvSurveyContent;                   // 考试情况
    @BindView(R.id.point_list)
    ListViewForScroll pointListView;            // 知识点展开树
    @BindView(R.id.ll_attention)
    LinearLayout llAttention;                   // 重点关注布局，如果没按时交卷，隐藏
    @BindView(R.id.e_card_view_overtime)
    ExpandableCardView eCardViewOverTime;       // 做题时长超过50s，可展开的答题卡
    @BindView(R.id.e_card_view_hard)
    ExpandableCardView eCardViewHard;           // 题难度超过0.5
    @BindView(R.id.e_card_view_no)
    ExpandableCardView eCardViewNo;             // 未做的题
    @BindView(R.id.card_view)
    ArenaAnswerCardViewNew cardView;            // 答题卡
    @BindView(R.id.view_err)
    CommonErrorView errorView;

    private ArenaExamPresenterImpl presenter;

    public StageReportBean stageReportBean;     // 答题卡基础数据
    public StageReportOtherBean stageReportOtherBean;   // 报告其他数据

    private long practiceId;                    // 答题卡Id
    private long syllabusId;                    // 大纲，答题卡Id

    private boolean isClickCard;                // 是否点击了答题卡
    private int choiceIndex;                    // 点击了第几个

    private boolean oneOk, twoOk;               // 两个网络访问是否都回来了
    private Bundle extraArgs;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_stage_report;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {

        if (event == null || event.type <= 0) {
            return;
        }

        // 跳转题号
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION) {
            if (event.extraBundle != null) {
                if ("STAGE_REPORT".equals(event.tag)) {   // 答题卡跳指定的题
                    isClickCard = true;
                    choiceIndex = event.extraBundle.getInt("request_index", -1);
                    if (stageReportBean.paper != null && stageReportBean.paper.questionBeanList != null && stageReportBean.paper.questionBeanList.size() > 0
                            && stageReportBean.paper.questionBeanList.get(0).questionOptions != null &&
                            !StringUtils.isEmpty(stageReportBean.paper.questionBeanList.get(0).questionOptions.get(0).optionDes)) {
                        goArenaExamActivityNew(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int dayNightMode = SpUtils.getDayNightMode();
        if (dayNightMode == 0) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarDarkMode(StageReportActivity.this);

        ArenaViewSettingManager.getInstance().registerNightSwitcher(this);
        EventBus.getDefault().register(this);

        presenter = new ArenaExamPresenterImpl(compositeSubscription, this);

        extraArgs = originIntent.getBundleExtra("extra_args");
        if (extraArgs == null) {
            extraArgs = new Bundle();
        }
        practiceId = extraArgs.getLong("practice_id", 0);
        syllabusId = extraArgs.getLong("syllabusId", 0);

        Typeface serif = Typeface.createFromAsset(getAssets(), "font/SourceHanSerifCN-Regular.ttf");
        tvNick.setTypeface(serif);
        tvRemark.setTypeface(serif);
        Typeface heavy = Typeface.createFromAsset(getAssets(), "font/Heavy.ttf");
        tvTeacherName.setTypeface(heavy);

    }

    @Override
    protected void onLoadData() {

        if (!NetUtil.isConnected()) {
            ToastUtils.showMessage("无网络连接");
            showError(0);
            return;
        }
        showProgress();

        oneOk = false;
        // 获取基础答题卡信息
        ServiceProvider.getStageReport(compositeSubscription, practiceId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                oneOk = true;
                showError(1);
                if (e instanceof ApiException) {
                    ToastUtils.showMessage(((ApiException) e).getErrorMsg());
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                hideError();
                stageReportBean = (StageReportBean) model.data;
                stageReportBean.id = stageReportBean.practiceId;
                stageReportBean.ucount = stageReportBean.unum;
                stageReportBean.points = stageReportBean.questionPointTrees;
                stageReportBean.rcount = stageReportBean.rnum;
                stageReportBean.wcount = stageReportBean.wnum;
                presenter.getPractice(stageReportBean);

                // 阶段测试报告 埋点
                String course_id = extraArgs.getString("course_id");
                if (!StringUtils.isEmpty(course_id)) {
                    String course_title = extraArgs.getString("course_title");
                    String class_id = extraArgs.getString("class_id");
                    String class_title = extraArgs.getString("class_title");
                    StudyCourseStatistic.stageReport(course_id, course_title, class_id, class_title,
                            stageReportBean.rcount, stageReportBean.qcount - stageReportBean.ucount, stageReportBean.expendTime, stageReportBean.qcount);
                }
            }
        });

        twoOk = false;
        // 获取报告其他信息
        ServiceProvider.getStageOtherReport(compositeSubscription, practiceId, syllabusId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                twoOk = true;
                showError(1);
                if (e instanceof ApiException) {
                    ToastUtils.showMessage(((ApiException) e).getErrorMsg());
                } else {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                twoOk = true;
                hideError();
                stageReportOtherBean = (StageReportOtherBean) model.data;
                handleData();
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.ll_no_show_all, R.id.view_err})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_no_show_all:
                llNoShowAll.setVisibility(View.GONE);
                break;
            case R.id.view_err:
                onLoadData();
                break;
        }
    }

    // 全部解析
    @OnClick(R.id.rl_all)
    public void onClickAnalysisAll() {
        goArenaExamActivityNew(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL);
    }

    // 错题解析
    @OnClick(R.id.rl_wrong)
    public void onClickAnalysisWrong() {
        if (stageReportBean == null || stageReportBean.paper == null || stageReportBean.paper.wrongQuestionBeanList == null || stageReportBean.paper.wrongQuestionBeanList.size() <= 0) {
            ToastUtils.showMessage("没有错题！");
            return;
        }
        goArenaExamActivityNew(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG);
    }

    // 阶段性测试解析
    @OnClick(R.id.iv_explain)
    public void onClickExplain() {                            // 弹窗介绍
        String content = "阶段测评最高分、平均分会随着参加考的学员人数变化而变化，请以考试结束后的最终数据为准";
        final CustomConfirmDialog dialog = DialogUtils.createDialog(StageReportActivity.this, "提示", content);
        dialog.setBtnDividerVisibility(false);
        dialog.setCancelBtnVisibility(false);
        dialog.setMessage(content, 13);
        dialog.setOkBtnConfig(200, 50, R.drawable.eva_explain_btn_bg);
        dialog.setContentGravity(Gravity.START);
        dialog.setPositiveColor(Color.parseColor("#FFFFFF"));
        dialog.setPositiveButton("我知道啦", null);
        dialog.setTitleBold();
        View contentView = dialog.getContentView();
        LinearLayout llBtn = contentView.findViewById(R.id.ll_btn);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llBtn.getLayoutParams();
        layoutParams.height = DisplayUtil.dp2px(66);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 处理数据，并显示
     */
    private void handleData() {
        if (!oneOk || !twoOk) return;

        if (stageReportBean != null && stageReportOtherBean != null && stageReportBean.isViewAllReport) {       // 两个网络访问都回来了，并且显示所有报告
            if (stageReportBean.paper == null || stageReportBean.paper.questionBeanList == null || stageReportBean.paper.questionBeanList.size() == 0) {
                return;
            }

            // 过滤三种类型的试题 超时，难度，没做
            stageReportOtherBean.questionListOverTime = new ArrayList<>();
            stageReportOtherBean.questionListHard = new ArrayList<>();
            stageReportOtherBean.questionListNoDone = new ArrayList<>();

            List<ArenaExamQuestionBean> questionBeanList = stageReportBean.paper.questionBeanList;
            ArrayList<StageReportOtherBean.Attention> focusList = stageReportOtherBean.focusList;
            if (focusList != null) {
                for (StageReportOtherBean.Attention attention : focusList) {
                    List<ArenaExamQuestionBean> questionList;
                    if (attention.typeValue.contains("EXPENDPASS50")) {
                        questionList = stageReportOtherBean.questionListOverTime;
                    } else if (attention.typeValue.contains("DIFFCULTYPASS50")) {
                        questionList = stageReportOtherBean.questionListHard;
                    } else {    // if (attention.typeValue.contains("UNDO"))
                        questionList = stageReportOtherBean.questionListNoDone;
                    }

                    if (attention.questions != null && attention.questions.size() > 0) {
                        if (attention.questions.get(0) != null) {
                            for (Integer index : attention.questions.get(0).questionIndexs) {
                                if (index < questionBeanList.size()) {
                                    questionList.add(questionBeanList.get(index));
                                }
                            }
                        }
                    }

                    Collections.sort(questionList, new Comparator<ArenaExamQuestionBean>() {
                        @Override
                        public int compare(ArenaExamQuestionBean o1, ArenaExamQuestionBean o2) {
                            return o1.index - o2.index;
                        }
                    });
                }
            }
        }
        showData();
    }

    private void showData() {
        if (stageReportBean != null && stageReportOtherBean != null && stageReportBean.isViewAllReport) {
            if (stageReportBean.paper == null || stageReportBean.paper.questionBeanList == null || stageReportBean.paper.questionBeanList.size() == 0) {
                return;
            }
            showOneData();
            showTwoData();
            if (stageReportBean.startTimeIsEffective == 0) {
                llNoShowAll.setVisibility(View.GONE);
            } else {
                llNoShowAll.setVisibility(View.VISIBLE);
            }
        } else if (stageReportBean != null && !stageReportBean.isViewAllReport) {
            showOneData();
            llRemark.setVisibility(View.GONE);
            llAttention.setVisibility(View.GONE);
            if (stageReportBean.startTimeIsEffective == 0) {
                llNoShowAll.setVisibility(View.GONE);
            } else {
                llNoShowAll.setVisibility(View.VISIBLE);
            }
        } else {
            showError(2);
        }
    }

    /**
     * 显示基础数据
     */
    private void showOneData() {

        if (stageReportBean == null) {
            showError(2);
            return;
        }
        if (stageReportBean.submitTime <= 0) {
            tvCommitTime.setVisibility(View.GONE);
        } else {
            tvCommitTime.setText("交卷时间：" + DateUtils.getFormatData("yyyy-MM-dd  HH:mm", stageReportBean.submitTime));
        }
        circleBar.setData(stageReportBean.userScore / (float) stageReportBean.score);
        tvScore.setText(LUtils.formatPoint(stageReportBean.userScore) + " ");
        tvAllScore.setText("满分" + LUtils.formatPoint(stageReportBean.score) + "分");
        tvTime.setText((stageReportBean.expendTime < 60 ? 1 : Math.round(((float) stageReportBean.expendTime) / 60)) + "");
        tvTopScore.setText(LUtils.formatPoint(stageReportBean.maxScore) + " ");
        tvAveScore.setText(LUtils.formatPoint(stageReportBean.averageScore) + " ");
        tvUpdateTime.setText("(统计数据更新于" + TimeUtils.getFormatData("yyyy-MM-dd  HH:mm", stageReportBean.reportTime) + ")");

        String color;
        int dayNightMode = SpUtils.getDayNightMode();
        if (dayNightMode == 0) {
            color = "#EC74A0";
        } else {
            color = "#6B3132";
        }

        String content = "共" + getFont(color, stageReportBean.qcount + "")
                + "题，答对" + getFont(color, stageReportBean.rcount + "")
                + "题，答错" + getFont(color, stageReportBean.wcount + "")
                + "题，未答" + getFont(color, stageReportBean.ucount + "")
                + "题；总计用时" + (stageReportBean.expendTime > 60 ? (getFont(color, stageReportBean.expendTime / 60 + "") + "分") : "")
                + getFont(color, stageReportBean.expendTime % 60 + "")
                + "秒，平均每题用时" + getFont(color, stageReportBean.averageTime + "")
                + "秒，正确率" + getFont(color, LUtils.formatPoint(stageReportBean.accuracy) + "%");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvSurveyContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvSurveyContent.setText(Html.fromHtml(content));
        }

        // 知识点
        ArenaStagePointTreeAdapter pointTreeAdapter = new ArenaStagePointTreeAdapter(getApplicationContext(), stageReportBean.points);
        pointListView.setAdapter(pointTreeAdapter);

        // 答题卡
        cardView.setTitleVisible(View.GONE);
        cardView.setShowModule(false);
        cardView.setData(stageReportBean, ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL, stageReportBean.type, "STAGE_REPORT");
    }

    private String getFont(String color, String s) {
        return StringUtils.asStrong(StringUtils.asItalic(StringUtils.asStrong(StringUtils.fontColor(color, s))));
    }

    /**
     * 显示其他数据
     */
    private void showTwoData() {
        ImageLoad.displayUserAvater(StageReportActivity.this, SpUtils.getAvatar(), ivHeader, R.mipmap.me_default_avater);
        tvNick.setText(getNickname() + "同学");

        setRemarkData();

        setSmoothLineData();

        if (stageReportOtherBean.scoreTop != null && stageReportOtherBean.scoreTop.comprehensiveRank != null) {
            ArrayList<StageReportOtherBean.RankInfo> scoreTop = stageReportOtherBean.scoreTop.comprehensiveRank;
            StageReportOtherBean.RankInfo self = stageReportOtherBean.scoreTop.self;
            if (scoreTop.size() > 0) {
                ArrayList<CourseWorkReportBean.ScoreRank> data = new ArrayList<>();
                for (int i = 0; i < scoreTop.size(); i++) {
                    StageReportOtherBean.RankInfo rankInfo = scoreTop.get(i);
                    data.add(getScoreRank(rankInfo));
                }
                if (self != null) {
                    tvMyRank.setText("我的排名 第" + self.rank + "名");
                }
                final StageRankExplandAdapter adapter = new StageRankExplandAdapter(StageReportActivity.this, data);
                rankListView.setAdapter(adapter);

                if (data.size() <= 3) {
                    tvMore.setText("没有更多了");
                    tvMore.setClickable(false);
                } else {
                    tvMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (adapter.getCount() == ExplandListAdapter.ExplandCount) {
                                adapter.setItemNum(adapter.getRealCount());
                                ((TextView) v).setText("收起");
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter.setItemNum(ExplandListAdapter.ExplandCount);
                                ((TextView) v).setText("查看更多");
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        }

        final RealExamBeans.RealExamBean realExamBeanCache = new RealExamBeans.RealExamBean();
        realExamBeanCache.type = stageReportBean.type;
        realExamBeanCache.name = stageReportBean.name;
        realExamBeanCache.paper = new PaperBean();
        realExamBeanCache.paper.name = stageReportBean.paper.name;
        realExamBeanCache.paper.type = stageReportBean.paper.type;

        eCardViewOverTime.setQuestions(0, stageReportOtherBean.questionListOverTime, new ExpandableCardAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                isClickCard = true;
                choiceIndex = position;
                realExamBeanCache.paper.questionBeanList = stageReportOtherBean.questionListOverTime;
                goArenaExamActivityNewForSpecial(realExamBeanCache);
            }
        });

        eCardViewHard.setQuestions(1, stageReportOtherBean.questionListHard, new ExpandableCardAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                isClickCard = true;
                choiceIndex = position;
                realExamBeanCache.paper.questionBeanList = stageReportOtherBean.questionListHard;
                goArenaExamActivityNewForSpecial(realExamBeanCache);
            }
        });

        eCardViewNo.setQuestions(2, stageReportOtherBean.questionListNoDone, new ExpandableCardAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                isClickCard = true;
                choiceIndex = position;
                realExamBeanCache.paper.questionBeanList = stageReportOtherBean.questionListNoDone;
                goArenaExamActivityNewForSpecial(realExamBeanCache);
            }
        });
    }

    private CourseWorkReportBean.ScoreRank getScoreRank(StageReportOtherBean.RankInfo rankInfo) {
        CourseWorkReportBean.ScoreRank info = new CourseWorkReportBean.ScoreRank();
        info.avatar = rankInfo.icon;
        info.expendTime = rankInfo.expendTime < 60 ? 1 : Math.round(((float) rankInfo.expendTime) / 60);
        info.rank = rankInfo.rank;
        info.submitTimeDes = DateUtils.getFormatData("MM-dd-HH:mm", rankInfo.submitTime);
        info.uname = rankInfo.userName;
        info.rcount = rankInfo.score + "";
        return info;
    }

    // 老师评语
    private void setRemarkData() {
        StageReportBean.TeacherRemark remark = stageReportBean.teacherRemark;
        if (remark == null) return;
        String content;
        if (remark.knowCount > 0 || remark.understandCount > 0 || remark.knowWellCount > 0 || remark.elasticCount > 0) {
            content = "本次测验考察了<font color=\"#EC74A0\">" + remark.pointCount + "</font>个知识点。<br/>"
                    + (remark.knowCount > 0 ? "达到<font color=\"#FF3F47\">了解</font>程度的<font color=\"#EC74A0\">" + remark.knowCount + "</font>个（" + remark.knowName + "），需要再听一遍课再做一次题。<br/>" : "")
                    + (remark.understandCount > 0 ? "<font color=\"#F5A623\">理解</font>程度的<font color=\"#EC74A0\">" + remark.understandCount + "</font>个（" + remark.understandName + "），需要再做一次题。 <br/>" : "")
                    + (remark.knowWellCount > 0 ? "<font color=\"#5D9AFF\">掌握</font>程度的<font color=\"#EC74A0\">" + remark.knowWellCount + "</font>个（" + remark.knowWellName + "），有时间的条件下可以再做一次题。 <br/>" : "")
                    + (remark.elasticCount > 0 ? "<font color=\"#1DD86C\">灵活运用</font>程度的<font color=\"#EC74A0\">" + remark.elasticCount + "</font>个（" + remark.elasticName + "），已掌握，隔断时间再来巩固下。" : "")
                    + "整体效果不错，继续加油哦！";
        } else {
            content = "本次测验考察了<font color=\"#EC74A0\">" + remark.pointCount + "</font>个知识点。<br/>然而每个知识点的正确率都成功必过了合格线，同学要加油呀！";
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvRemark.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvRemark.setText(Html.fromHtml(content));
        }
        tvTeacherName.setText(remark.teacherName);
    }

    // 成绩分布
    private void setSmoothLineData() {
        ArrayList<StageReportOtherBean.SocreDistribution> scoreDistribution = stageReportOtherBean.socreDistribution;
        if (scoreDistribution == null || scoreDistribution.size() == 0) return;

        Collections.sort(scoreDistribution, new Comparator<StageReportOtherBean.SocreDistribution>() {
            @Override
            public int compare(StageReportOtherBean.SocreDistribution o1, StageReportOtherBean.SocreDistribution o2) {
                return o1.score - o2.score;
            }
        });

        float maxCount = 0f;
        for (StageReportOtherBean.SocreDistribution distribution : scoreDistribution) {
            if (maxCount < distribution.count) {
                maxCount = distribution.count;
            }
        }
        maxCount *= 1.2;

        ArrayList<PointF> data = new ArrayList<>();
        int index = 0;
        String beatStr = "", myScore = "";
        for (int i = 0; i < scoreDistribution.size(); i++) {
            StageReportOtherBean.SocreDistribution distribution = scoreDistribution.get(i);
            data.add(new PointF(distribution.score, (float) distribution.count / maxCount));
            if (distribution.isSelf) {
                beatStr = "击败" + distribution.beatRatio + "%";
                myScore = distribution.score + "分";
                index = i;
            }
        }

        smoothLine.setData(data, index, beatStr, myScore);
    }

    private String getFormatNum(String color, int num) {
        return "<font color=\"" + color + "\">" + num + "</font>";
    }

    //获取昵称
    private String getNickname() {
        String nick = SpUtils.getNick();
        String mobile = SpUtils.getMobile();
        String uname = SpUtils.getUname();
        String email = SpUtils.getEmail();
        if (!TextUtils.isEmpty(nick)) {
            return nick;
        }
        if (!TextUtils.isEmpty(mobile)) {
            return mobile;
        }
        if (!TextUtils.isEmpty(uname)) {
            return uname;
        }
        if (!TextUtils.isEmpty(email)) {
            return email;
        }
        return "";
    }

    /**
     * 点击超时、难度系数，没做的题，进行跳转
     */
    private void goArenaExamActivityNewForSpecial(RealExamBeans.RealExamBean realExamBean) {
        if (stageReportBean == null) return;
        Bundle bundle = new Bundle();
        ArenaDataCache.getInstance().realExamBean = realExamBean;
        if (isClickCard) {
            isClickCard = false;
            bundle.putInt("showIndex", choiceIndex);
        } else {
            bundle.putInt("showIndex", 0);
        }
        ArenaExamActivityNew.show(this, ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL, bundle);
    }

    /**
     * 错题解析、全部解析
     * 去答题/看题页面
     */
    private void goArenaExamActivityNew(int requestType) {
        if (stageReportBean == null) return;
        Bundle bundle = new Bundle();
        ArenaDataCache.getInstance().realExamBean = stageReportBean;
        if (isClickCard) {
            isClickCard = false;
            bundle.putInt("showIndex", choiceIndex);
        } else {
            bundle.putInt("showIndex", 0);
        }
        ArenaExamActivityNew.show(this, requestType, bundle);
    }


    private void hideError() {
        hideProgress();
        errorView.setVisibility(View.GONE);
    }

    private void showError(int type) {
        hideProgress();
        errorView.setVisibility(View.VISIBLE);
        errorView.setErrorImageVisible(true);
        switch (type) {
            case 0:                         // 无网络
                errorView.setErrorImage(R.drawable.no_server_service);
                errorView.setErrorText("无网络，点击重试");
                break;
            case 1:                         // 无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("获取数据是失败，点击重试");
                break;
            case 2:                         // 无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("无数据");
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (stageReportBean != null) {
            outState.putSerializable("reportBean", stageReportBean);
        }
        if (stageReportOtherBean != null) {
            outState.putSerializable("stageReportOtherBean", stageReportOtherBean);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stageReportBean = (StageReportBean) savedInstanceState.getSerializable("reportBean");
        stageReportOtherBean = (StageReportOtherBean) savedInstanceState.getSerializable("stageReportOtherBean");
        if (stageReportBean != null && stageReportOtherBean != null) {
            oneOk = true;
            twoOk = true;
            showData();
        } else {
            onLoadData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ArenaViewSettingManager.getInstance().unRegisterNightSwitcher(this);
    }

    @Override
    public void nightSwitch() {
        recreate();
    }

    public static void show(Activity context, int from, Bundle args) {
        Intent intent = new Intent(context, StageReportActivity.class);
        intent.putExtra("request_type", from);
        intent.putExtra("extra_args", args);
        context.startActivity(intent);
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    //------------------------------------
    @Override
    public void onSetPagerDatas(RealExamBeans beans) {

    }

    @Override
    public void onGetPractiseData(RealExamBeans.RealExamBean beans) {
        oneOk = true;
        // 获取到基础数据
        stageReportBean = (StageReportBean) beans;
        for (ArenaExamQuestionBean arenaExamQuestionBean : stageReportBean.paper.questionBeanList) {
            arenaExamQuestionBean.categoryName = arenaExamQuestionBean.name;
        }
        handleData();
    }

    @Override
    public void onCollectionCanceled(int questionId) {

    }

    @Override
    public void onCollectionSuccess(int questionId) {

    }

    @Override
    public void onGetShareContent(ShareInfoBean shareInfoBean) {

    }

    @Override
    public void onArenaInfoSuccess(ArenaDetailBean bean) {

    }

    @Override
    public void onGetPaperDataFailed(long errorCode) {

    }

    @Override
    public void onLoadDataFailed(int flag) {
        oneOk = true;
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void dismissProgressBar() {

    }

    @Override
    public void onSetData(Object respData) {

    }

    @Override
    public void onLoadDataFailed() {

    }
    //--------------------------------

}
