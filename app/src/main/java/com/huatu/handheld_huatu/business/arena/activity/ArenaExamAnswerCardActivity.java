package com.huatu.handheld_huatu.business.arena.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.customview.ArenaAnswerCardViewNew;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.arena.fragment.ArenaExamTitleFragmentNew;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matchsmall.activity.SmallMatchReportActivity;
import com.huatu.handheld_huatu.business.matchsmall.activity.StageReportActivity;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseAfterWorkReportFragment;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.event.SectionalExaminationEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.helper.db.KnowPointInfoDao;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaExamAnswerCardPresenterImpl;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamAnswerCardView;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 新的行测答题答题卡卡页面
 * <p>
 * 功能：
 * 查看答题卡情况、点击题号跳转对应试题、交卷
 * <p>
 * 注意交卷的时候进行数据处理，如果一道题的答题时间是0，改为1
 */
public class ArenaExamAnswerCardActivity extends BaseActivity implements ArenaExamAnswerCardView {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.card_view)
    ArenaAnswerCardViewNew cardView;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;

    private ArenaExamAnswerCardPresenterImpl mPresenter;
    private boolean startTimer;                                             // 考试中开始计时了
    private Subscription startExamTimeSubscription;                         // 考试中计时

    private int requestType = 0;
    private boolean isAutoSubmit;                                           // 自动提交
    private RealExamBeans.RealExamBean realExamBean;                        // 试卷
    private Bundle extraArgs;
    private boolean toHome;

    private boolean isSubmit;                                               // 是否是交卷，如果是交卷退出，就不重写退出方法

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_arena_exam_answer_card;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {
        if (event == null || event.type <= 0) {
            return;
        }
        // 跳转题号
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION) {
            if (event.extraBundle != null) {
                if ("ArenaAnswerCardViewNew".equals(event.tag)) {   // 答题卡跳指定的题
                    finish();
                }
            }
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

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        compositeSubscription = new CompositeSubscription();
        mPresenter = new ArenaExamAnswerCardPresenterImpl(compositeSubscription, this);

        if (originIntent != null) {
            requestType = originIntent.getIntExtra("request_type", 0);
            extraArgs = originIntent.getBundleExtra("extra_args");
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            isAutoSubmit = extraArgs.getBoolean("auto_submit");
            realExamBean = ArenaDataCache.getInstance().realExamBean;
            toHome = extraArgs.getBoolean("toHome", false);
        }

        if (realExamBean != null && realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_COURSE_EXERICE) {
            tvSubmit.setText("提交课后作业");
        }

        setColor();
    }

    @Override
    protected void onLoadData() {
        setData();
    }

    @OnClick(R.id.iv_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.tv_submit)
    public void onClickSubmit() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showMessage("无网络连接！");
            return;
        }
        if (CommonUtils.isFastDoubleClick()) {
            return;
        }
        List<ArenaExamQuestionBean> questionBeanList = realExamBean.paper.questionBeanList;
        int doneCount = ArenaHelper.getDoneCount(realExamBean);                                   // 作过了多少
        if (doneCount == 0) {                  // 一道没做
            DialogUtils.onShowRedConfirmDialog(this, null, null, " ", "您还没有答题，\n请答题后交卷。", null, "确定");
        } else if (doneCount == questionBeanList.size()) {          // 做完
            submit();
        } else {          // 做一部分
            doneCount = questionBeanList.size() - doneCount;
            String tipContent = null;
            tipContent = String.format("您还有%d题未作答，\n确定交卷吗?", doneCount);
            DialogUtils.onShowRedConfirmDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submit();
                }
            }, null, " ", tipContent, "取消", "确定");
        }
    }

    private void setData() {
        if (realExamBean == null || realExamBean.paper == null) {
            finish();
            return;
        }

        if (isAutoSubmit) {         // 如果是自动提交，就直接提交就行了
            submit();
        } else {                    // 否则就要初始化，开始、显示计时系统
            initTime();
        }

        if (requestType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE) {
            tvSubmit.setVisibility(View.VISIBLE);
        } else {
            tvSubmit.setVisibility(View.GONE);
        }

        cardView.setData(realExamBean, requestType, realExamBean.type, "ArenaAnswerCardViewNew");
    }

    private void submit() {
        if (realExamBean == null) return;
        isSubmit = true;
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {             // 新模考大赛交卷
            mPresenter.submitScCard(realExamBean);
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN) {          // 专项模考，精准估分交卷
            mPresenter.submitMoKao(realExamBean);
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST) {              // 阶段考试交卷
            mPresenter.submitStageAnswerCard(extraArgs.getLong("syllabusId"), realExamBean);
        } else {
            mPresenter.submitAnswerCard(realExamBean);                                          // 其他交卷
        }
    }


    /**
     * 初始化计时系统
     * (realExamBean.type >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL)，解析不需要计时
     * 模考大赛：是否到了做题时间，五分钟开始倒计时开始做题，不可作答。然后倒计时结束后，开始做题倒计时。然后做题倒计时后自动交卷。
     * 真题演练、往期模考，进来就倒计时做题，到时间交卷
     * 其他做题，只有正计时，没有自动交卷
     */
    private void initTime() {
        if (realExamBean == null) return;
        if (realExamBean.type >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
                || realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE) {             // 解析、背题不需要计时
            llTime.setVisibility(View.GONE);
            return;
        }
        llTime.setVisibility(View.VISIBLE);
        // 是模考已经开始考试，或者其他考试做题过程中
        if (!ArenaHelper.isPaperSCType(realExamBean) || realExamBean.paper.startTime <= System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime) {                                                                        // 开始考试
            startTime();
        }
    }


    /**
     * 开始计时
     */
    private void startTime() {
        if (realExamBean == null || startTimer) return;

        startTimer = true;

        // 模考大赛单独计算剩余时间
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {
            int remain = (int) ((realExamBean.endTime - (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime)) / 1000);
            int all = (int) ((realExamBean.endTime - realExamBean.startTime) / 1000);

            int min = remain < all ? remain : all;

            realExamBean.remainingTime = min > 0 ? min : 0;
        } else if (realExamBean.backGroundTime > 0) {
            if (ArenaHelper.isCountDown(realExamBean.type)) {       // 是倒计时
                realExamBean.remainingTime -= (System.currentTimeMillis() - realExamBean.backGroundTime) / 1000;
            } else {
                realExamBean.expendTime += (System.currentTimeMillis() - realExamBean.backGroundTime) / 1000;
            }
        }
        realExamBean.backGroundTime = ArenaExamTitleFragmentNew.CARD_NOT_RECORD_BG_TIME;

        setTimeText();

        startExamTimeSubscription = Observable.interval(1, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (ArenaHelper.isCountDown(realExamBean.type)) {                                   // 是倒计时
                            realExamBean.remainingTime--;
                            if (realExamBean.remainingTime == 10 * 60 && ArenaHelper.isPaperSCType(realExamBean)) {         // 模考倒计时十分钟
                                DialogUtils.createTipsDialog(ArenaExamAnswerCardActivity.this, "考试还剩10分钟,\n结束时将自动交卷!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                });
                            }
                            if (realExamBean.remainingTime <= 0) {                                                          // 时间到，交卷
                                CommonUtils.showToast("时间到，交卷啦!");
                                finishTimer();
                                submit();
                            }
                        } else {
                            realExamBean.expendTime++;
                        }
                        setTimeText();
                    }
                });

    }

    /**
     * 设置显示时间
     */
    private void setTimeText() {
        int time;
        if (ArenaHelper.isCountDown(realExamBean.type)) {   // 是倒计时
            time = realExamBean.remainingTime;                  // 剩余时间
        } else {
            time = realExamBean.expendTime;                     // 花费的时间
        }
        tvTime.setText(TimeUtils.getSecond22MinTime(time));
    }

    private void finishTimer() {
        startTimer = false;
        if (startExamTimeSubscription != null && !startExamTimeSubscription.isUnsubscribed()) {
            startExamTimeSubscription.unsubscribe();
        }
        // 模考大赛开始后 || 小模考
        // 得记录后台时间（如果是去答题卡，就不用记录，因为答题卡会对同一个对象进行及时）
        if (ArenaHelper.isRecordBackgroundTime(realExamBean)) {
            realExamBean.backGroundTime = System.currentTimeMillis();               // 记录当前时间
        }
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

    //--------------------------------------------------------------------------
    // 交卷回调
    @Override
    public void onSubmitAnswerResult(RealExamBeans.RealExamBean bean) {

        // 删除数据库中的高亮
        KnowPointInfoDao.getInstance().clearPaper(bean.id);

        // 交卷成功发送消息
        EventBus.getDefault().post(new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_COMMIT_PAPER_SUCCESS));
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI) {
            // 专项练习 为了保持树形展开结构，所以获取全部树形数据，进行数据对比。
            EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_MSG_TYPE_TREE_DATA_UPDATE_VIEW_REFRESH_ALL));
        }
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {         // 专项练习 错题练习 背题模式，交完卷，只提示，不做任何操作
            // 关掉页面
            finishThis();
            ToastUtil.showToast("背题已交卷成功，可在答题记录里再次查看");
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI              // 专项练习
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN                    // 每日特训（没有再来一套）
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE                    // 智能刷题
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI                   // 错题重练
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ERROR_EXPORT) {                // 错题导出
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            extraArgs.putBoolean("ArenaDataCache", true);
            ArenaDataCache.getInstance().realExamBean = bean;
            // 关掉页面
            finishThis();
            ArenaExamReportExActivity.show(this, requestType, extraArgs);
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH) {                 // 小模考跳转报告页
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            extraArgs.putLong("practice_id", bean.id);          // 把答题卡id传过去，到那边请求报告
            // 关掉页面
            finishThis();
            // 小模考报告
            SmallMatchReportActivity.show(ArenaExamAnswerCardActivity.this, requestType, extraArgs);
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST) {                  // 阶段测试报告
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            extraArgs.putLong("practice_id", realExamBean.id);          // 把答题卡id传过去，到那边请求报告


            // 1未开始2开始考试5继续考试6查看报告
            PrefStore.putSettingInt(ArgConstant.KEY_ID + extraArgs.getLong("syllabusId", 0), 6);
            // 关掉页面
            finishThis();
            // 阶段测试报告页面
            StageReportActivity.show(ArenaExamAnswerCardActivity.this, requestType, extraArgs);
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_COURSE_EXERICE) {              // 课后练习报告

            // 交卷要通知课后作业列表刷新
            int position = extraArgs.getInt("position", -1);
            if (position != -1) {
                // position: 传过去的值，传回来
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                EventBus.getDefault().post(new SectionalExaminationEvent(bundle));
            }
            // 关掉页面
            finishThis();

            CourseAfterWorkReportFragment.lanuch(ArenaExamAnswerCardActivity.this, realExamBean.id, extraArgs);
        } else {                                                                                    // 真题演练、往期模考、专项模考、精准估分报告页
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            extraArgs.putBoolean("ArenaDataCache", true);
            ArenaDataCache.getInstance().realExamBean = bean;
            finishThis();
            ArenaExamReportActivity.show(this, requestType, extraArgs);
        }
    }

    // 新模考大赛、专项模考、精准估分 交卷回调（不一定有报告）
    @Override
    public void onSubmitMokaoAnswerResult(boolean isReport, RealExamBeans.RealExamBean bean) {

        // 删除数据库中的高亮
        KnowPointInfoDao.getInstance().clearPaper(bean.id);

        // 交卷成功发送消息
        EventBus.getDefault().post(new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_COMMIT_PAPER_SUCCESS));

        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {     // 新模考大赛交卷成功
            // 新模考大赛交卷成功，通知模考大赛列表刷新
            EventBus.getDefault().post(new BaseMessageEvent<>(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_EXAM_COMP_NOT_REPORT_DATA, new SimulationContestMessageEvent()));
            // 模考大赛提交成功
            Toast.makeText(this, "交卷成功，请在模考结束30分钟后查看模考报告", Toast.LENGTH_LONG).show();
            // 关掉页面
            finishThis();
            // 是否跳主页
            if (toHome) {
                MainTabActivity.newIntent(this);
            }
        } else {                                                                        // 专项模考，精准估分
            if (isReport) {                                                                  // 有报告
                if (extraArgs == null) {
                    extraArgs = new Bundle();
                }
                extraArgs.putBoolean("ArenaDataCache", true);
                ArenaDataCache.getInstance().realExamBean = bean;
                extraArgs.putLong("practice_id", bean.id);                                          // 把答题卡Id放进去，为了礼包中刷新报告。如果是继续做题就会有practice_id，如果是新作题没有practice_id
                finishThis();
                ArenaExamReportActivity.show(this, requestType, extraArgs);
            } else {                                                                         // 无报告
                Toast.makeText(this, "交卷成功，请稍后查看报告", Toast.LENGTH_LONG).show();
                // 关掉页面
                finishThis();
                // 是否跳主页
                if (toHome) {
                    MainTabActivity.newIntent(this);
                }

            }
        }
    }

    /**
     * 交卷之后的Finish
     */
    private void finishThis() {
        tvTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 交卷成功，关掉本页面，关闭做题页面
                Intent intent = new Intent();
                intent.putExtra("commit_success", true);
                setResult(ArenaExamActivityNew.ANSWER_CARD_RESULT_CODE, intent);
                finish();

                // 再次关闭做题页面，保证交卷后，关闭做题
                Activity activity = ActivityStack.getInstance().getActivity(ArenaExamActivityNew.class);
                if (activity != null) {
                    activity.finish();
                }
            }
        }, 200);
    }

    @Override
    public void onSubmitMokaoAnswerError() {

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
    //--------------------------------------------------------------------------

    private void setColor() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {
            QMUIStatusBarHelper.setStatusBarLightMode(ArenaExamAnswerCardActivity.this);
        } else {
            QMUIStatusBarHelper.setStatusBarDarkMode(ArenaExamAnswerCardActivity.this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initTime();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishTimer();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        realExamBean = ArenaDataCache.getInstance().realExamBean;
        setData();
    }

    @Override
    public void finish() {
        super.finish();
        if (!isSubmit) {
            overridePendingTransition(R.anim.enter_left_30, R.anim.exit_right_100);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        finishTimer();
    }
}
