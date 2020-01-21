package com.huatu.handheld_huatu.business.arena.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.adapter.ArenaPointTreeAdapter;
import com.huatu.handheld_huatu.business.arena.customview.ArenaAnswerCardViewNew;
import com.huatu.handheld_huatu.business.arena.setting.ArenaViewSettingManager;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaDetailBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.me.ShareInfoBean;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaExamPresenterImpl;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamMainView;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ShareUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.ListViewForScroll;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 新的报告页
 * <p>
 * 专项练习
 * 每日特训（没有再来一套）
 * 智能刷题
 * 错题重练
 * 错题导出
 * <p>
 * 功能：
 * <p>
 * 显示报告页，
 * 查看解析，
 * 跳转解析指定题号，
 * 夜间模式，
 * 分享，
 * 跳转再来一套
 */

public class ArenaExamReportExActivity extends BaseActivity implements ArenaExamMainView, NightSwitchInterface {

    @BindView(R.id.arena_exam_answer_report_type_tv)
    TextView tvSubjectType;
    @BindView(R.id.arena_exam_answer_report_complete_time_tv)
    TextView tvCompleteTime;
    @BindView(R.id.arena_exam_answer_report_correct_tv)
    TextView tvCorrectNumber;
    @BindView(R.id.arena_exam_answer_report_practise_again_tv)
    TextView tvAgain;
    @BindView(R.id.arena_exam_answer_report_total_tv)
    TextView tvTotalNumber;
    @BindView(R.id.arena_exam_answer_report_used_time_tv)
    TextView tvUsedTime;
    @BindView(R.id.arena_exam_answer_report_answer_card_layout_id)
    ArenaAnswerCardViewNew answerCardView;
    @BindView(R.id.arena_exam_answer_report_knowledge_list_view)
    ListViewForScroll lvKnowledge;
    @BindView(R.id.arena_exam_main_error_layout)
    CommonErrorView errorView;

    ArenaPointTreeAdapter pointTreeAdapter;

    private ArenaExamPresenterImpl mPresenter;

    private int requestType;
    private boolean mToHome;
    private Bundle extraArgs;

    private RealExamBeans.RealExamBean realExamBean;                        // 答题卡，试卷

    private boolean isClickCard;                                            // 是否点击答题卡
    private int choiceIndex = 0;                                            // 点击答题卡的题号

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_arena_exam_report_ex;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {
        if (event == null || event.type <= 0) {
            return;
        }
        // 跳转题号
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION) {               // 点击答题卡，跳转题号
            if (event.extraBundle != null) {
                if ("ArenaExamReportExActivity".equals(event.tag)) {
                    isClickCard = true;
                    choiceIndex = event.extraBundle.getInt("request_index", -1);
                    if (realExamBean.paper != null && realExamBean.paper.questionBeanList != null && realExamBean.paper.questionBeanList.size() > 0
                            && realExamBean.paper.questionBeanList.get(0).questionOptions != null &&
                            !StringUtils.isEmpty(realExamBean.paper.questionBeanList.get(0).questionOptions.get(0).optionDes)) {
                        goArenaExamActivityNew(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL);
                    }
                }
            }
        } else if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_SHARE_WAY) {              // 分享渠道回传
            String shareWay = event.extraBundle.getString("share_way");
            if (realExamBean != null)
                StudyCourseStatistic.simulatedShareResult(getModuleName(), Type.getSubject(realExamBean.subject), Type.getCategory(realExamBean.catgory),
                        String.valueOf(realExamBean.paper.id), realExamBean.paper.name, realExamBean.score, shareWay);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 1) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onInitView() {
        EventBus.getDefault().register(this);
        ArenaViewSettingManager.getInstance().registerNightSwitcher(this);
        //实现状态栏图标和文字颜色为暗色
        if (SpUtils.getDayNightMode() == 1) {
            QMUIStatusBarHelper.setStatusBarDarkMode(ArenaExamReportExActivity.this);
        } else {
            QMUIStatusBarHelper.setStatusBarLightMode(ArenaExamReportExActivity.this);
        }

        mPresenter = new ArenaExamPresenterImpl(compositeSubscription, this);
        requestType = originIntent.getIntExtra("request_type", 0);
        extraArgs = originIntent.getBundleExtra("extra_args");
        if (extraArgs != null) {
            mToHome = extraArgs.getBoolean("toHome", false);
        }

        switch (requestType) {
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI:
                tvSubjectType.setText("练习类型：专项练习");
                tvAgain.setVisibility(View.VISIBLE);
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI:
                tvSubjectType.setText("练习类型：错题重练");
                tvAgain.setVisibility(View.VISIBLE);
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE:
                tvSubjectType.setText("练习类型：智能刷题");
                tvAgain.setVisibility(View.VISIBLE);
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN:
                tvSubjectType.setText("练习类型：每日特训");
                tvAgain.setVisibility(View.GONE);
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ERROR_EXPORT:
                tvSubjectType.setText("练习类型：错题导出");
                tvAgain.setVisibility(View.GONE);
                break;
        }


    }

    @Override
    protected void onLoadData() {
        if (extraArgs == null) {
            ToastUtils.showShort("数据错误");
            return;
        }
        if (extraArgs.containsKey("ArenaDataCache")) {
            realExamBean = ArenaDataCache.getInstance().realExamBean;
            onGetPractiseData(realExamBean);
        } else {
            mPresenter.getPractiseDetails(extraArgs);
        }
    }


    @OnClick(R.id.arena_exam_answer_report_title_back)
    public void onClickBack() {
        if (mToHome) {
            MainTabActivity.newIntent(this);
        }
        finish();
    }

    // 点击分享
    @OnClick(R.id.arena_exam_answer_report_title_share)
    public void onClickShare() {
        if (realExamBean == null) return;
        mPresenter.getShareContents(realExamBean.id, 2, 2);
    }

    // 再来一套
    @OnClick(R.id.arena_exam_answer_report_practise_again_tv)
    public void onClickAgain() {
        // 再来一套，所以要初始化各种数据
        // 是否显示报告、自动提交、试题、继续做题、继续做题id、
        extraArgs.remove("show_statistic");
        extraArgs.remove("auto_submit");
        extraArgs.remove("realExamBean");
        extraArgs.remove("continue_answer");
        extraArgs.remove("practice_id");
        ArenaExamActivityNew.show(this, requestType, extraArgs);
        finish();
    }

    // 全部解析
    @OnClick(R.id.arena_exam_answer_report_analysis_all)
    public void onClickAnalysisAll() {
        goArenaExamActivityNew(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL);
    }

    // 错题解析
    @OnClick(R.id.arena_exam_answer_report_analysis_wrong)
    public void onClickAnalysisWrong() {
        if (realExamBean == null || realExamBean.paper == null || realExamBean.paper.wrongQuestionBeanList == null || realExamBean.paper.wrongQuestionBeanList.size() <= 0) {
            ToastUtils.showMessage("没有错题！");
            return;
        }
        goArenaExamActivityNew(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG);
    }

    /**
     * 错题解析、全部解析
     * 去答题/看题页面
     */
    private void goArenaExamActivityNew(int requestType) {
        if (realExamBean == null) return;
        Bundle bundle = new Bundle();
        if (isClickCard) {
            isClickCard = false;
            bundle.putInt("showIndex", choiceIndex);
        } else {
            bundle.putInt("showIndex", 0);
        }
        ArenaExamActivityNew.show(this, requestType, bundle);
    }

    //---------------------------------------------------------------------------------------
    @Override
    public void onSetPagerDatas(RealExamBeans beans) {

    }

    // 报告数据返回
    @Override
    public void onGetPractiseData(RealExamBeans.RealExamBean beans) {
        this.realExamBean = beans;
        ArenaDataCache.getInstance().realExamBean = realExamBean;
        if (realExamBean == null || realExamBean.paper == null) {
            LogUtils.e("realExamBean is null, close this page");
            finish();
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        tvCompleteTime.setText("交卷时间: " + dateFormat.format(realExamBean.createTime));

        tvUsedTime.setText(TimeUtils.getSecond2HourMinTimeOther(realExamBean.expendTime));

        tvCorrectNumber.setText(String.valueOf(realExamBean.rcount));
        tvTotalNumber.setText("道/ " + realExamBean.paper.qcount + "道");
        pointTreeAdapter = new ArenaPointTreeAdapter(this, realExamBean.points);
        lvKnowledge.setAdapter(pointTreeAdapter);
        answerCardView.setData(realExamBean, ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL, realExamBean.type, "ArenaExamReportExActivity");
    }

    @Override
    public void onCollectionCanceled(int questionId) {

    }

    @Override
    public void onCollectionSuccess(int questionId) {

    }

    @Override
    public void onGetShareContent(ShareInfoBean shareInfoBean) {
        String desc = shareInfoBean.desc;
        String title = shareInfoBean.title;
        String url = shareInfoBean.url;
        String id = shareInfoBean.id;
        ShareUtil.test(this, id, desc, title, url);
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
        hideProgress();
    }

    @Override
    public void onSetData(Object respData) {

    }

    @Override
    public void onLoadDataFailed() {

    }
    //---------------------------------------------------------------------------------------


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
     * 得到模块名称
     */
    private String getModuleName() {
        String module;
        switch (requestType) {
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE:
                module = "智能刷题";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI:
                module = "专项练习";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN:
                module = "真题演练";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_SIMULATION:
                module = "智能模考";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI:
                module = "错题重练";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN:
                module = "每日特训";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_COLLECT_PRACTICE:
                module = "收藏练习";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN:
                module = "专项模考";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ZC_LIANXI:
                module = "砖超联赛";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_WEIXIN_LIANXI:
                module = "微信练习";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST:
                module = "模考大赛";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN:
                module = "精准估分";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_PAST_MOKAO:
                module = "往期模考";
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ERROR_EXPORT:
                module = "错题导出";
                break;
            default:
                module = "";
        }
        return module;
    }

    private void showError(int type) {
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ArenaViewSettingManager.getInstance().unRegisterNightSwitcher(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void nightSwitch() {
        recreate();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        realExamBean = ArenaDataCache.getInstance().realExamBean;
        if (realExamBean != null) {
            onGetPractiseData(realExamBean);
        } else {
            onLoadData();
        }
    }

    public static void show(Activity context, int from, Bundle args) {
        Intent intent = new Intent(context, ArenaExamReportExActivity.class);
        intent.putExtra("request_type", from);
        intent.putExtra("extra_args", args);
        context.startActivity(intent);
    }
}
