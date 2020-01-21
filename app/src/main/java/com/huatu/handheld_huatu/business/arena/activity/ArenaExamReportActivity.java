package com.huatu.handheld_huatu.business.arena.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.GiftWebViewActivity;
import com.huatu.handheld_huatu.business.arena.adapter.ArenaPointTreeAdapter;
import com.huatu.handheld_huatu.business.arena.customview.ArenaAnswerCardViewNew;
import com.huatu.handheld_huatu.business.arena.setting.ArenaViewSettingManager;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matches.customview.ShakeImageView;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.event.match.MatchEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaDetailBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.me.ShareInfoBean;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaExamPresenterImpl;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamMainView;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PopWindowUtil;
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
 * 成绩报告页面新（正式试卷页）
 * 真题演练
 * 往期模考
 * 专项模考
 * 精准估分
 */
public class ArenaExamReportActivity extends BaseActivity implements NightSwitchInterface, ArenaExamMainView {

    @BindView(R.id.arena_exam_answer_report_title_back)
    ImageView btnBack;
    @BindView(R.id.sc_view)
    NestedScrollView scView;                                         // ScrollView
    @BindView(R.id.arena_exam_answer_report_type_tv)
    TextView tvSubjectType;
    @BindView(R.id.arena_exam_answer_report_complete_time_tv)
    TextView tvCompleteTime;
    @BindView(R.id.arena_exam_answer_report_used_time_tv)
    TextView tvUsedTime;
    @BindView(R.id.arena_exam_answer_report_correct_des)
    TextView tvCorrectNumberDes;
    @BindView(R.id.arena_exam_answer_report_correct_tv)
    TextView tvCorrectNumber;
    @BindView(R.id.arena_exam_answer_report_total_tv)
    TextView tvTotalNumber;
    @BindView(R.id.arena_exam_answer_report_compare_layout)
    LinearLayout layoutCompare;
    @BindView(R.id.arena_exam_answer_report_score_tv)
    TextView tvScore;
    @BindView(R.id.arena_exam_answer_report_score_number_tv)
    TextView tvScoreNumber;
    @BindView(R.id.arena_exam_answer_report_average_score)
    TextView tvAverageScore;
    @BindView(R.id.arena_exam_answer_report_number_tv)
    TextView tvCompareNumber;
    @BindView(R.id.arena_exam_answer_report_knowledge_list_view)
    ListViewForScroll lvKnowledge;
    @BindView(R.id.arena_exam_answer_report_answer_card_layout_id)
    ArenaAnswerCardViewNew answerCardView;

    @BindView(R.id.arena_exam_main_error_layout)
    CommonErrorView errorView;

    @BindView(R.id.rl_gift)
    RelativeLayout rlGift;
    @BindView(R.id.iv_gift)
    ShakeImageView ivGift;

    private ArenaPointTreeAdapter pointTreeAdapter;

    private ArenaExamPresenterImpl mPresenter;

    private int requestType;
    private boolean mToHome;
    private Bundle extraArgs = null;

    private RealExamBeans.RealExamBean realExamBean;                        // 答题卡，试卷

    private boolean isClickCard;                                            // 是否点击答题卡
    private int choiceIndex = 0;                                            // 点击答题卡的题号

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_arena_exam_report;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {
        if (event == null || event.type <= 0) {
            return;
        }
        // 跳转题号
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION) {               // 点击答题卡，跳转题号
            if (event.extraBundle != null) {
                if ("ArenaExamReportActivity".equals(event.tag)) {
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
        } else if (event.type == ArenaExamMessageEvent.REFRESH_REPORT) {                            // 如果是精准估分/行测模考大赛，需要刷新报告
            mPresenter.getPractiseDetails(extraArgs);
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
            QMUIStatusBarHelper.setStatusBarDarkMode(ArenaExamReportActivity.this);
        } else {
            QMUIStatusBarHelper.setStatusBarLightMode(ArenaExamReportActivity.this);
        }

        mPresenter = new ArenaExamPresenterImpl(compositeSubscription, this);

        if (originIntent != null) {
            requestType = originIntent.getIntExtra("request_type", 0);
            extraArgs = originIntent.getBundleExtra("extra_args");
            if (extraArgs != null) {
                mToHome = extraArgs.getBoolean("toHome", false);
            }
        }

        switch (requestType) {
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN:
                tvSubjectType.setText("练习类型：真题演练");
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_PAST_MOKAO:
                tvSubjectType.setText("练习类型：往期模考");
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN:
                tvSubjectType.setText("练习类型：专项模考");
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN:
                tvSubjectType.setText("练习类型：精准估分");
                break;
        }

        answerCardView.setTitleVisible(View.GONE);

        scView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
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
        this.finish();
    }

    @OnClick(R.id.arena_exam_answer_report_title_share)
    public void onClickShare() {
        if (realExamBean == null) return;
        mPresenter.getShareContents(realExamBean.id, 2, 1);
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

    //领取大礼包
    @OnClick(R.id.rl_gift)
    public void onClickBigGift() {
        if (realExamBean.hasGetBigGift == 0) {
            //没领过大礼包
            showBigGift();
        } else {
            // TODO已领过大礼包，跳H5
            GiftWebViewActivity.newInstance(this, realExamBean.giftHtmlUrl);
        }
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

    //---------------------------------------------------------------------------------------------------------
    @Override
    public void onSetPagerDatas(RealExamBeans beans) {

    }

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
        tvCompleteTime.setText(dateFormat.format(realExamBean.createTime));
        int seconds = realExamBean.expendTime;
        if (realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN
                || realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN
                || realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN
        ) {
            seconds = realExamBean.paper.time - realExamBean.remainingTime;
            if (seconds < 0) {
                seconds = realExamBean.expendTime;
            }
        }

        tvUsedTime.setText("答题用时：" + TimeUtils.getSecond2HourMinTimeOther(seconds));

        if ((realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN
                || realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN
                || realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_PAST_MOKAO
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_PAST_MOKAO
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN)
                && realExamBean.cardUserMeta != null) { // 真题演练和模考估分 才显示compare部分
            tvCorrectNumberDes.setText("得分");
            tvCorrectNumber.setText(realExamBean.scoreStr);
            tvTotalNumber.setText("分");
            tvTotalNumber.setGravity(Gravity.LEFT);
            layoutCompare.setVisibility(View.VISIBLE);
            tvScore.setText(String.valueOf(realExamBean.rcount));
            tvScoreNumber.setText("/" + realExamBean.paper.qcount + "道");
            tvAverageScore.setText(realExamBean.cardUserMeta.averageStr);
            tvCompareNumber.setText(realExamBean.cardUserMeta.beatRate + "%");
        } else {
            layoutCompare.setVisibility(View.GONE);
            tvCorrectNumber.setText(String.valueOf(realExamBean.rcount));
            tvTotalNumber.setText("/ " + realExamBean.paper.qcount + "道");
        }
        pointTreeAdapter = new ArenaPointTreeAdapter(getApplicationContext(), realExamBean.points);
        lvKnowledge.setAdapter(pointTreeAdapter);
        answerCardView.setData(realExamBean, ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL, realExamBean.type,
                "ArenaExamReportActivity");
        if (realExamBean.hasGift == 1 && realExamBean.rightImgUrl != null) {
            rlGift.setVisibility(View.VISIBLE);
            // Glide.with(ArenaExamReportActivity.this).load(realExamBean.rightImgUrl).diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivGift);

            ImageLoad.load(ArenaExamReportActivity.this, realExamBean.rightImgUrl, ivGift);
            ivGift.startAnim();
        } else {
            rlGift.setVisibility(View.GONE);
        }
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
    //---------------------------------------------------------------------------------------------------------

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

    private void showBigGift() {
        PopWindowUtil.showPopInCenter(this, btnBack, 0, 0, R.layout.layout_biggift_open_pop,
                DisplayUtil.px2dp(DisplayUtil.getScreenWidth()), DisplayUtil.px2dp(DisplayUtil.getScreenHeight()), new PopWindowUtil.PopViewCall() {
                    @Override
                    public void popViewCall(View contentView, final PopupWindow popWindow) {


                        final ImageView iv_bag = (ImageView) contentView.findViewById(R.id.iv_bag);                // 去发红包

                        ImageView ivClose = (ImageView) contentView.findViewById(R.id.iv_close);                    // 关闭按钮

                        TextView tvOpen = (TextView) contentView.findViewById(R.id.tv_open);
                        if (realExamBean.giftImgUrl != null) {
                            ImageLoad.load(ArenaExamReportActivity.this, realExamBean.giftImgUrl, iv_bag, DiskCacheStrategy.AUTOMATIC);
                            //Glide.with(ArenaExamReportActivity.this).load(realExamBean.giftImgUrl).diskCacheStrategy(DiskCacheStrategy.NONE).into(iv_bag);
                        }

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
                                GiftWebViewActivity.newInstance(ArenaExamReportActivity.this, realExamBean.giftHtmlUrl);
                                popWindow.dismiss();
                            }
                        });

                    }

                    @Override
                    public void popViewDismiss() {
                    }
                });
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
            default:
                module = "";
        }
        return module;
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
        Intent intent = new Intent(context, ArenaExamReportActivity.class);
        intent.putExtra("request_type", from);
        intent.putExtra("extra_args", args);
        context.startActivity(intent);
    }
}
