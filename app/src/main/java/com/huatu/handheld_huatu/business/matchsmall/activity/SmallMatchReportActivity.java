package com.huatu.handheld_huatu.business.matchsmall.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.customview.ArenaAnswerCardViewNew;
import com.huatu.handheld_huatu.business.arena.setting.ArenaViewSettingManager;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.matchsmall.adapter.PointAdapter;
import com.huatu.handheld_huatu.business.matchsmall.customview.ViewCircleBar;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaDetailBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.SmallMatchReportBean;
import com.huatu.handheld_huatu.mvpmodel.me.ShareInfoBean;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaExamPresenterImpl;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamMainView;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

public class SmallMatchReportActivity extends BaseActivity implements NightSwitchInterface, ArenaExamMainView {

    @BindView(R.id.tv_type)
    TextView tvType;                            // 考试类型
    @BindView(R.id.tv_commit_time)
    TextView tvCommitTime;                      // 提交时间
    @BindView(R.id.view_circle_bar)
    ViewCircleBar circleBar;                    // 圆进度条
    @BindView(R.id.tv_right_num)
    TextView tvRightNum;                        // 正确数量
    @BindView(R.id.tv_count)
    TextView tvCount;                           // 总题数
    @BindView(R.id.tv_rank_complete)
    TextView tvRankComplete;                    // 第几名完成
    @BindView(R.id.tv_rank)
    TextView tvRank;                            // 排名
    @BindView(R.id.tv_count_p)
    TextView tvCountP;                          // 总参加人数
    @BindView(R.id.tv_right)
    TextView tvRight;                           // 正确道数
    @BindView(R.id.tv_ave)
    TextView tvAve;                             // 平均答对数量
    @BindView(R.id.tv_beat)
    TextView tvBeat;                            // 打败百分比
    @BindView(R.id.tv_update_time)
    TextView tvUpdateTime;                      // 更新时间
    @BindView(R.id.tv_survey_content)
    TextView tvSurveyContent;                   // 考试情况
    @BindView(R.id.rl_point)
    RecyclerView rlPoint;                       // 知识点
    @BindView(R.id.card_view)
    ArenaAnswerCardViewNew cardViewNew;         // 答题卡

    @BindView(R.id.view_err)
    CommonErrorView errorView;                  // 错误、空白页面

    private ArenaExamPresenterImpl presenter;

    private long practiceId;                    // 答题卡Id

    private SmallMatchReportBean reportBean;    // 报告数据

    private boolean isClickCard;                                            // 是否点击答题卡
    private int choiceIndex = 0;                                            // 点击答题卡的题号

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_small_match_report;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {

        if (event == null || event.type <= 0) {
            return;
        }

        // 跳转题号
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION) {
            if (event.extraBundle != null) {
                if ("SMALL_MATCH".equals(event.tag)) {   // 答题卡跳指定的题
                    isClickCard = true;
                    choiceIndex = event.extraBundle.getInt("request_index", -1);
                    if (reportBean.paper != null && reportBean.paper.questionBeanList != null && reportBean.paper.questionBeanList.size() > 0
                            && reportBean.paper.questionBeanList.get(0).questionOptions != null &&
                            !StringUtils.isEmpty(reportBean.paper.questionBeanList.get(0).questionOptions.get(0).optionDes)) {
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
        QMUIStatusBarHelper.setStatusBarDarkMode(SmallMatchReportActivity.this);

        ArenaViewSettingManager.getInstance().registerNightSwitcher(this);
        EventBus.getDefault().register(this);

        presenter = new ArenaExamPresenterImpl(compositeSubscription, this);

        rlPoint.setLayoutManager(new LinearLayoutManager(SmallMatchReportActivity.this));
        rlPoint.setNestedScrollingEnabled(false);

        Bundle extraArgs = originIntent.getBundleExtra("extra_args");
        if (extraArgs == null) {
            extraArgs = new Bundle();
        }
        practiceId = extraArgs.getLong("practice_id", 0);
    }

    @Override
    protected void onLoadData() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showMessage("无网络连接");
            showError(0);
            return;
        }
        showProgress();
        ServiceProvider.getSmallMatchReport(compositeSubscription, practiceId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                showError(1);
                if (e instanceof ApiException) {
                    ToastUtils.showMessage(((ApiException) e).getErrorMsg());
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                hideError();
                reportBean = (SmallMatchReportBean) model.data;
                presenter.getPractice(reportBean);

                StudyCourseStatistic.smallMatchReport(Long.toString(reportBean.paper.id), reportBean.name,
                        reportBean.rcount, reportBean.paper.qcount - reportBean.ucount, reportBean.expendTime, reportBean.paper.qcount);
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.view_err})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
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
        if (reportBean == null || reportBean.paper == null || reportBean.paper.wrongQuestionBeanList == null || reportBean.paper.wrongQuestionBeanList.size() <= 0) {
            ToastUtils.showMessage("没有错题！");
            return;
        }
        goArenaExamActivityNew(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG);
    }

    // 小模考解析
    @OnClick(R.id.iv_explain)
    public void onClickExplain() {                            // 弹窗介绍
        String content = "小模考的成绩数据会随着参加学员的状况动态变化，请以模考结束后的数据统计为准。";
        final CustomConfirmDialog dialog = DialogUtils.createDialog(SmallMatchReportActivity.this, "提示", content);
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

    private void setData() {
        if (reportBean == null) {
            showError(2);
            return;
        }
        tvType.setText("练习类型：小模考 " + DateUtils.getFormatData("yyyy-MM-dd", reportBean.paper.startTime));
        tvCommitTime.setText("交卷时间：" + DateUtils.getFormatData("yyyy-MM-dd  HH:mm", reportBean.createTime));
        circleBar.setData((float) reportBean.rcount / (float) reportBean.paper.qcount);
        tvRightNum.setText(reportBean.rcount + " ");
        tvCount.setText("共" + reportBean.paper.qcount + "题");
        if (reportBean.cardUserMeta != null) {
            tvRankComplete.setText(" " + reportBean.cardUserMeta.submitRank + " ");
            tvRank.setText(" " + reportBean.cardUserMeta.rank + " ");
            tvCountP.setText(" " + reportBean.cardUserMeta.total + " ");
            tvRight.setText(reportBean.rcount + " ");
            tvAve.setText(reportBean.cardUserMeta.rnumAverage + " ");
            tvBeat.setText(reportBean.cardUserMeta.beatRate + " %");
            tvUpdateTime.setText("(统计数据更新于" + TimeUtils.getFormatData("yyyy-MM-dd  HH:mm", reportBean.cardUserMeta.reportTime) + ")");
        }
        String color;
        int dayNightMode = SpUtils.getDayNightMode();
        if (dayNightMode == 0) {
            color = "#EC74A0";
        } else {
            color = "#6B3132";
        }

        String content = "共" + StringUtils.asStrong(StringUtils.asItalic(StringUtils.asStrong(StringUtils.fontColor(color, reportBean.paper.qcount + ""))))
                + "道，答对" + StringUtils.asStrong(StringUtils.asItalic(StringUtils.asStrong(StringUtils.fontColor(color, reportBean.rcount + ""))))
                + "道，答错" + StringUtils.asStrong(StringUtils.asItalic(StringUtils.asStrong(StringUtils.fontColor(color, reportBean.wcount + ""))))
                + "道，未答" + StringUtils.asStrong(StringUtils.asItalic(StringUtils.asStrong(StringUtils.fontColor(color, reportBean.ucount + ""))))
                + "道，总计用时"
                + (reportBean.expendTime > 60 ? (StringUtils.asStrong(StringUtils.asItalic(StringUtils.asStrong(StringUtils.fontColor(color, reportBean.expendTime / 60 + "")))) + "分") : "")
                + StringUtils.asStrong(StringUtils.asItalic(StringUtils.asStrong(StringUtils.fontColor(color, reportBean.expendTime % 60 + "")))) + "秒";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvSurveyContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvSurveyContent.setText(Html.fromHtml(content));
        }

        // 知识点
        PointAdapter adapter = new PointAdapter(reportBean.points, SmallMatchReportActivity.this);
        rlPoint.setAdapter(adapter);

        // 答题卡
        cardViewNew.setTitleVisible(View.GONE);
        cardViewNew.setShowModule(false);
        cardViewNew.setData(reportBean, ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL, reportBean.type, "SMALL_MATCH");
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

    /**
     * 错题解析、全部解析
     * 去答题/看题页面
     */
    private void goArenaExamActivityNew(int requestType) {
        if (reportBean == null) return;
        Bundle bundle = new Bundle();
        ArenaDataCache.getInstance().realExamBean = reportBean;
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
        if (reportBean != null) {
            outState.putSerializable("reportBean", reportBean);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        reportBean = (SmallMatchReportBean) savedInstanceState.getSerializable("reportBean");
        if (reportBean != null) {
            setData();
        } else {
            onLoadData();
        }
    }

    @Override
    public void nightSwitch() {
        recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ArenaViewSettingManager.getInstance().unRegisterNightSwitcher(this);
    }

    public static void show(Activity context, int from, Bundle args) {
        Intent intent = new Intent(context, SmallMatchReportActivity.class);
        intent.putExtra("request_type", from);
        intent.putExtra("extra_args", args);
        context.startActivity(intent);
    }

    //-----------------------view
    @Override
    public void onSetPagerDatas(RealExamBeans beans) {

    }

    @Override
    public void onGetPractiseData(RealExamBeans.RealExamBean beans) {
        reportBean = (SmallMatchReportBean) beans;
        // 看题，错题...不显示类型，只显示试卷名称
        for (ArenaExamQuestionBean arenaExamQuestionBean : reportBean.paper.questionBeanList) {
            arenaExamQuestionBean.categoryName = arenaExamQuestionBean.name;
        }
        setData();
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
        showError(flag);
    }

    @Override
    public void showProgressBar() {
        showProgress();
    }

    @Override
    public void dismissProgressBar() {
        hideError();
    }

    @Override
    public void onSetData(Object respData) {

    }

    @Override
    public void onLoadDataFailed() {
        showError(2);
    }
    //-----------------------view

}
