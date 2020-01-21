package com.huatu.handheld_huatu.business.arena.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.customview.ArenaPadQuestionView;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.arena.fragment.ArenaDraftFragment;
import com.huatu.handheld_huatu.business.arena.fragment.ArenaExamTitleFragmentNew;
import com.huatu.handheld_huatu.business.arena.setting.ArenaViewSettingManager;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.setting.TextSizeSwitchInterface;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.event.SectionalExaminationEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaExamAnswerCardPresenterImpl;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaExamPresenterNewImpl;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamAnswerCardView;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamMainNewView;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 2019.1.9 新的做题页面，此页面只负责模考做题、模考错题查看（以后可能会实现所有的做题，看题，背题）
 * 2019.10.25 新的做题页面，此页面已经全部启用，所有做题，看题，背题。（除了旧模考，旧模考基本确定全部废除）
 */
public class ArenaExamActivityPad extends BaseActivity implements ArenaExamMainNewView, NightSwitchInterface, TextSizeSwitchInterface {

    public static final int ANSWER_CARD_REQUEST_CODE = 10012;       // 跳答题卡，如果交卷，需要finish本页面，这里是回传数据
    public static final int ANSWER_CARD_RESULT_CODE = 10013;

    @BindView(R.id.view_statue)
    View viewStatue;
    @BindView(R.id.fl_title)
    FrameLayout flTitle;
    @BindView(R.id.vp_content)
    ViewPager viewPager;

    @BindView(R.id.ll_start_five_tip)
    LinearLayout llFiveTip;                             // 模考大赛，提前进入，五分钟倒计时
    @BindView(R.id.tv_five_tip)
    TextView tvFiveTip;

    @BindView(R.id.error_view)
    CommonErrorView errorView;

    @BindView(R.id.view_tip_bg)
    View tipBg;
    @BindView(R.id.iv_doubt)
    ImageView ivDoubt;
    @BindView(R.id.iv_card)
    ImageView ivCard;
    @BindView(R.id.iv_long_press)
    ImageView ivLongPress;
    @BindView(R.id.iv_del)
    ImageView ivDel;

    private int requestType = -1;                       // 做题类型
    private int fromType = -1;                          // 从哪里过来的（现在应用于背题模式直接解析，需要知道是哪里过来，然后设置name）
    private Bundle extraArgs;                           // 额外数据
    private boolean mToHome;                            // 是否要Back到Home

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private ArenaExamPresenterNewImpl mPresenter;                       // 网络访问

    private RealExamBeans.RealExamBean realExamBean;                    // 试卷
    private List<ArenaExamQuestionBean> questionBeans;                  // 问题内容
    private ArrayList<ArenaPadQuestionView> questionViews;              // QuestionView
    private LinkedList<ArenaPadQuestionView> questionViewRecycle;       // QuestionView回收栈

    private HashSet<ArenaExamQuestionBean> saveQuestionBeans;           // 五道题保存答案

    private ArenaExamTitleFragmentNew titleFragmentNew;                 // 计时栏
    private QuestionPagerAdapter viewPagerAdapter;

    private int questionCount = 0;                                      // 收藏模式下的总题量

    public static int doStyle = 0;                                      // 做题模式 0、通屏 1、分屏

    private SparseIntArray oldNewIndex = new SparseIntArray();

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_arena_exam_pad;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {

        if (event == null || event.type <= 0) {
            return;
        }

        // 跳转题号
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION) {
            if (event.extraBundle != null) {
                if ("ArenaExamQuestionAdapter".equals(event.tag)) {             // 下一题
                    int index = event.extraBundle.getInt("index");
                    if (index < realExamBean.paper.questionBeanList.size() - 1) {
                        viewPager.setCurrentItem(oldNewIndex.get(index + 1), true);
                    } else if (oldNewIndex.get(index) == questionBeans.size() - 1) {
                        saveAnswer(1);
                        goAnswerCar(false);
                    }
                } else if ("ArenaAnswerCardViewNew".equals(event.tag)) {        // 答题卡跳指定的题
                    int index = event.extraBundle.getInt("request_index", -1);
                    if (oldNewIndex.get(index) >= 0 && oldNewIndex.get(index) < questionBeans.size())
                        viewPager.setCurrentItem(oldNewIndex.get(index), true);
                } else if ("ShowAnswerCardView".equals(event.tag)) {            // 显示答题卡
                    boolean autoSubmit = event.extraBundle.getBoolean("autoSubmit", false);
                    goAnswerCar(autoSubmit);
                }
            }
        } else if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_FINISH) {            // 智能练习，tips页面点击返回，关闭页面
            if (mToHome) {
                MainTabActivity.newIntent(ArenaExamActivityPad.this);
            }
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            ArenaDataCache.getInstance().clearCacheErrorData();
        }
        super.onCreate(savedInstanceState);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ArenaExamActivityNew");
        wakeLock.acquire();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        doStyle = SpUtils.getArenaPadDoStyle();
    }

    // 记录ViewPager滑动位置
    private int lastX = -1;

    @Override
    protected void onInitView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        // 日夜间、字体大小控制注册
        ArenaViewSettingManager.getInstance().registerNightSwitcher(this);
        ArenaViewSettingManager.getInstance().registerTextSizeSwitcher(this);

        requestType = originIntent.getIntExtra("request_from", 0);
        extraArgs = originIntent.getBundleExtra("extra_args");
        if (extraArgs == null) {
            extraArgs = new Bundle();
        }
        fromType = extraArgs.getInt("fromType", 0);
        mToHome = extraArgs.getBoolean("toHome", false);

        mPresenter = new ArenaExamPresenterNewImpl(compositeSubscription, this);

        saveQuestionBeans = new HashSet<>();

        addTitleFragment();

        initQuestionViewList();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (questionBeans != null && i < questionBeans.size()) {
                    ArenaExamQuestionBean arenaExamQuestionBean = questionBeans.get(i);
                    // 这里保证如果是收藏解析，加载更多后，显示的是刚才显示的那个题
                    if (realExamBean != null) {
                        realExamBean.lastIndex = i;
                    }
                    if (titleFragmentNew != null)
                        titleFragmentNew.setQuestionBean(arenaExamQuestionBean);

                    addAndCheckQuestionToFive(arenaExamQuestionBean);
                }

                if (ArenaHelper.isRecyView(requestType)) {      // 是否需要分页继续加载
                    // 这里实现分页加载。二十条。
                    // 是否还有更多去加载
                    if (ArenaHelper.isNeedPartErrorIdLoad(ArenaDataCache.getInstance().deleteErrorCount + i)) {
                        mPresenter.getData(requestType, extraArgs);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 收藏解析模式下，如果状态静止了，查看是否需要加载更多
                if (state == ViewPager.SCROLL_STATE_IDLE && requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE) {
                    mPresenter.checkAndRefreshCollectionPre();
                }
            }
        });

        View.OnTouchListener onVpTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int currentX = (int) event.getX();
                        if (lastX <= 0) {
                            lastX = currentX;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        int upX = (int) event.getX();
                        if (viewPager.getCurrentItem() == questionBeans.size() - 1) {
                            if (lastX - upX >= DisplayUtil.dp2px(20)) {
                                saveAnswer(1);
                                if (questionBeans != null) {
                                    viewPager.setCurrentItem(questionBeans.size());
                                }
                                goAnswerCar(false);
                                return true;
                            }
                        }
                        lastX = -1;
                        break;
                    default:
                        break;
                }
                return false;

            }
        };

        if (requestType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {
            viewPager.setOnTouchListener(onVpTouchListener);                                        // 做题模式下，最后一道题左滑跳转答题卡
        }

        setColor();

        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE) {                        // 智能刷题，显示Tips
            showAiPracticeTipView();
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE) {              // 收藏解析，这里需要知道体量
            String ids = extraArgs.getString("exerciseIdList");
            if (!StringUtils.isEmpty(ids)) {
                String[] split = ids.split(",");
                questionCount = split.length;
            }
        }

        if (requestType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {                           // 做题或背题，显示做题引导
            showTips();
        }
    }

    @Override
    protected void onLoadData() {
        mPresenter.getData(requestType, extraArgs);
    }

    @OnClick(R.id.error_view)
    public void onClickError() {
        onLoadData();
    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {
        if (realExamBean == null) return;
        switch (clickId) {
            case ArenaExamTitleFragmentNew.UPDATE_FIVE_TIME:                // 模考考试开始前倒计时五分钟
                long remainTime = bundle.getLong("remainTime");
                showFiveTime(remainTime);
                break;
            case R.id.iv_draft:
                //草稿纸
                ArenaDraftFragment draftFragment = ArenaDraftFragment.newInstance(realExamBean.lastIndex);
                addFragment(ArenaDraftFragment.class.getSimpleName(), draftFragment, clickId, true, false);
                break;
            case R.id.tv_switch_style:
                SpUtils.setArenaPadDoStyle(doStyle = (doStyle == 0 ? 1 : 0));
                for (ArenaPadQuestionView questionView : questionViews) {
                    questionView.switchViewStyle();
                }
                break;
        }
    }

    /**
     * 添加TitleFragment，放在LoadData中，因为系统回收后不会重建添加
     */
    private void addTitleFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag("titleFragmentNew");
        if (fragment != null) {
            titleFragmentNew = (ArenaExamTitleFragmentNew) fragment;
            transaction.show(titleFragmentNew);
        } else {
            titleFragmentNew = new ArenaExamTitleFragmentNew();
            extraArgs.putInt("request_from", requestType);
            titleFragmentNew.setArguments(extraArgs);
            transaction.add(R.id.fl_title, titleFragmentNew, "titleFragmentNew");
        }
        transaction.commit();
    }

    /**
     * 初始化问题View
     */
    private void initQuestionViewList() {
        questionViews = new ArrayList<>();
        questionViewRecycle = new LinkedList<>();

        //   不设PageLimit只有3个view同时存在，再一个view可以循环使用
        for (int i = 0; i < 5; i++) {
            // ArenaQuestionViewNew questionView = new ArenaQuestionViewNew(UniApplicationContext.getContext());

            ArenaPadQuestionView questionView = new ArenaPadQuestionView(ArenaExamActivityPad.this);
            questionViews.add(questionView);
            questionViewRecycle.add(questionView);
        }
    }

    /**
     * 考试开始前倒计时
     */
    private void showFiveTime(long remainTime) {
        if (remainTime > 0) {
            llFiveTip.setVisibility(View.VISIBLE);
            String timeInfo = TimeUtils.getSecond22MinTime((int) remainTime);
            tvFiveTip.setText(timeInfo);
        } else {
            llFiveTip.setVisibility(View.GONE);
        }
    }

    /**
     * 去答题卡页面
     */
    private void goAnswerCar(boolean autoSubmit) {
        Activity activity = ActivityStack.getInstance().getActivity(ArenaExamAnswerCardActivity.class);
        if (activity != null) return;
        realExamBean.backGroundTime = ArenaExamTitleFragmentNew.CARD_NOT_RECORD_BG_TIME;             // 如果是 -5 就不记录backGroundTime时间
        // 跳答题卡页面
        Intent intent = new Intent(this, ArenaExamAnswerCardActivity.class);
        intent.putExtra("request_type", requestType);
        extraArgs.putBoolean("auto_submit", autoSubmit);
        ArenaDataCache.getInstance().realExamBean = realExamBean;
        intent.putExtra("extra_args", extraArgs);
        // 为了在答题卡页面交卷成功，关闭本页面，需要页面通信
        startActivityForResult(intent, ANSWER_CARD_REQUEST_CODE);
        overridePendingTransition(R.anim.enter_right_100, R.anim.exit_left_30);
    }

    /**
     * 五道题一保存答案，把当前题放入set中
     */
    private void addAndCheckQuestionToFive(ArenaExamQuestionBean questionBean) {
        if (realExamBean.type >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {            // 解析不需要保存试题
            return;
        }

        if (!ArenaDataCache.getInstance().isEnableExam()) {                                 // 还没开始考试，就不保存
            return;
        }

        if (saveQuestionBeans.size() >= 5) {
            if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {
                mPresenter.saveAnswerCard(1, realExamBean.id, saveQuestionBeans);
            } else {
                mPresenter.saveAnswerCardOld(1, requestType, realExamBean.id, saveQuestionBeans);
            }
            saveQuestionBeans.clear();
        }

        if ((requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI)
                && questionBean.userAnswer != 0) {                                          // 新背题 且 用户已经有答案 就不需要再保存
            return;
        }

        saveQuestionBeans.add(questionBean);
    }

    /**
     * 按返回键保存答案
     */
    private void saveAnswer(int type) {
        if (realExamBean == null || realExamBean.id <= 0 || realExamBean.paper == null || realExamBean.paper.questionBeanList == null || realExamBean.paper.questionBeanList.size() == 0)
            return;
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {
            mPresenter.saveAnswerCard(type, realExamBean.id, realExamBean.paper.questionBeanList);
        } else {
            mPresenter.saveAnswerCardOld(type, requestType, realExamBean.id, realExamBean.paper.questionBeanList);
        }
    }

    /**
     * ai练习提示
     */
    private void showAiPracticeTipView() {
        if (SpUtils.getArenaAiPracticeTipsCanShow() && !ArenaDataCache.getInstance().isShowedAiTips) {
            ArenaDataCache.getInstance().isShowedAiTips = true;
            ViewStub aiPracticeTip = rootView.findViewById(R.id.vs_ai_practice_tips);
            if (aiPracticeTip != null) {
                ArenaDataCache.getInstance().isShowedAiTipsIng = true;
                View aiPracticeTipView = aiPracticeTip.inflate();
                aiPracticeTipView.setVisibility(View.VISIBLE);
            } else {
                LogUtils.e(TAG, "null viewstub_id_ai_practice_tips");
            }
        }
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return R.id.fl_draft_content;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    //--------------------------------------------------------------------------------------------------------------

    @Override
    public void onGetPractiseData(RealExamBeans.RealExamBean beans, List<ArenaExamQuestionBean> questions) {
        errorView.setVisibility(View.GONE);
        this.realExamBean = beans;
        this.questionBeans = new ArrayList<>();

        // 这里进行数据处理，把数据改成pad格式的
        for (int i = 0; i < questions.size(); i++) {

            ArenaExamQuestionBean bean = questions.get(i);
            bean.questions = new ArrayList<>();
            bean.questions.add(bean);

            // 是不是材料题
            if (!TextUtils.isEmpty(bean.material) || (!Method.isListEmpty(bean.materials) && TextUtils.isEmpty(bean.materials.get(0))) || !TextUtils.isEmpty(bean.require)) {
                bean.isMaterial = true;
            }

            // 如果是材料题，并且材料题和上一道材料相同，就放到上一道题里
            if (bean.isMaterial && i > 0
                    && questionBeans.get(questionBeans.size() - 1).isMaterial
                    && questionBeans.get(questionBeans.size() - 1).material.equals(bean.material)) {
                questionBeans.get(questionBeans.size() - 1).questions.add(bean);
            } else {
                questionBeans.add(bean);
            }
            oldNewIndex.put(i, questionBeans.size() - 1);
        }

        ArenaDataCache.getInstance().realExamBean = realExamBean;
        if (titleFragmentNew != null) {
            titleFragmentNew.setExamBeans(realExamBean);
            if (questions != null && questions.size() > 0 && realExamBean.lastIndex < questions.size()) {
                titleFragmentNew.setQuestionBean(questions.get(realExamBean.lastIndex));
                // 第一个题也要放到保存列表里
                addAndCheckQuestionToFive(questions.get(realExamBean.lastIndex));
            }
        }
        viewPagerAdapter = new QuestionPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        if (realExamBean.lastIndex > 0 && realExamBean.lastIndex < questions.size()) {
            viewPager.setCurrentItem(oldNewIndex.get(realExamBean.lastIndex), false);
        }
    }

    @Override
    public void onSavedAnswerCardSuccess(int saveType) {
        if (saveType == 0) {
            // 点击返回，保存成功发送消息
            EventBus.getDefault().post(new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_SAVE_PAPER_SUCCESS));
            ToastUtils.showShort("保存成功");
            if (mToHome) {
                MainTabActivity.newIntent(this);
            }
            finish();
            if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI
                    || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI) { // 如果是专项练习 做题 背题，就要发通知，首页刷新数据集
                EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_MSG_TYPE_TREE_DATA_UPDATE_VIEW_REFRESH_ALL));
            } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {       // 模考大赛保存成功，通知模考列表刷新
                EventBus.getDefault().post(new BaseMessageEvent<>(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_ARENA_SAVE_SUCCESS, new SimulationContestMessageEvent()));
            }
        }
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
    //--------------------------------------------------------------------------------------------------------------

    private void showTips() {
        tipBg.setVisibility(View.GONE);
        ivLongPress.setVisibility(View.GONE);
        ivDoubt.setVisibility(View.GONE);
        ivCard.setVisibility(View.GONE);
        ivDel.setVisibility(View.GONE);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipBg.setVisibility(View.GONE);
                ivLongPress.setVisibility(View.GONE);
                ivDoubt.setVisibility(View.GONE);
                ivCard.setVisibility(View.GONE);
                ivDel.setVisibility(View.GONE);
            }
        };
        if (SpUtils.getDeleteTipsShowX() != AppUtils.getVersionCode()) {            // 显示长安删除
            tipBg.setVisibility(View.VISIBLE);
            ivLongPress.setVisibility(View.VISIBLE);
            SpUtils.setDeleteTipsShowX();
            if (SpUtils.getDoubtTipsShowX() != AppUtils.getVersionCode()) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTips();
                    }
                };
            }
        } else if (SpUtils.getDoubtTipsShowX() != AppUtils.getVersionCode()
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE) {  // 显示疑问(背题模式是没有疑问按钮的)
            tipBg.setVisibility(View.VISIBLE);
            ivDoubt.setVisibility(View.VISIBLE);
            SpUtils.setDoubtTipsShowX();
            if (SpUtils.getChooseQueSeqShowX() != AppUtils.getVersionCode()) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTips();
                    }
                };
            }
        } else if (SpUtils.getChooseQueSeqShowX() != AppUtils.getVersionCode()) {   // 显示答题卡
            tipBg.setVisibility(View.VISIBLE);
            ivCard.setVisibility(View.VISIBLE);
            SpUtils.setChooseQueSeqShowX();
            if (SpUtils.getTipErrDelX() != AppUtils.getVersionCode() && requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTips();
                    }
                };
            }
        } else if (SpUtils.getTipErrDelX() != AppUtils.getVersionCode() && requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {
            tipBg.setVisibility(View.VISIBLE);
            ivDel.setVisibility(View.VISIBLE);
            SpUtils.setTipErrDelX();
        }
        tipBg.setOnClickListener(listener);
    }

    private void setColor() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {
            viewStatue.setBackgroundResource(R.color.arena_top_title_bg);
            flTitle.setBackgroundResource(R.color.arena_top_title_bg);
            viewPager.setBackgroundResource(R.color.arena_top_title_bg);
            QMUIStatusBarHelper.setStatusBarLightMode(ArenaExamActivityPad.this);
        } else {
            viewStatue.setBackgroundResource(R.color.arena_top_title_bg_night);
            flTitle.setBackgroundResource(R.color.arena_top_title_bg_night);
            viewPager.setBackgroundResource(R.color.arena_top_title_bg_night);
            QMUIStatusBarHelper.setStatusBarDarkMode(ArenaExamActivityPad.this);
        }
    }

    @Override
    public void nightSwitch() {
        setColor();
        if (questionViews != null)
            for (ArenaPadQuestionView questionViewNew : questionViews) {
                questionViewNew.nightSwitch();
            }
    }

    @Override
    public void sizeSwitch() {
        if (questionViews != null)
            for (ArenaPadQuestionView questionViewNew : questionViews) {
                questionViewNew.sizeSwitch();
            }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("a", "a");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        realExamBean = ArenaDataCache.getInstance().realExamBean;
        if (realExamBean != null) {
            questionBeans = ArenaExamPresenterNewImpl.getQuestionsFromExamBean(requestType, realExamBean);
            onGetPractiseData(realExamBean, questionBeans);
        } else {
            onLoadData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ArenaViewSettingManager.getInstance().unRegisterNightSwitcher(this);
        ArenaViewSettingManager.getInstance().unRegisterTextSizeSwitcher(this);
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 1) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 交卷成功，关闭页面
        if (requestCode == ANSWER_CARD_REQUEST_CODE && resultCode == ANSWER_CARD_RESULT_CODE) {
            if (data != null) {
                boolean commitSuccess = data.getBooleanExtra("commit_success", false);
                if (commitSuccess) {
                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (realExamBean == null || realExamBean.type >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
                || realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE) {             // 解析不需要保存试题
            if (mToHome) {
                MainTabActivity.newIntent(this);
            }
            finish();
            return;
        }

        // 如果是模考，还没开始考试，就直接finish
        if (ArenaHelper.isPaperSCType(realExamBean) && !ArenaDataCache.getInstance().isEnableExam()) {
            if (mToHome) {
                MainTabActivity.newIntent(this);
            }
            // 模考大赛在没有开始的时候，点击返回，需要刷新一下模考列表
            EventBus.getDefault().post(new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_SAVE_PAPER_SUCCESS));
            finish();
            return;
        }

        // 智能刷题，判断提示是否是正在显示提示，如果正在显示，就是直接finish
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE) {
            if (SpUtils.getArenaAiPracticeTipsCanShow() && ArenaDataCache.getInstance().isShowedAiTipsIng) {
                if (mToHome) {
                    MainTabActivity.newIntent(ArenaExamActivityPad.this);
                }
                finish();
                return;
            }
        }

        CustomConfirmDialog confirmDialog;
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN) {            // 每日练习 不可保存退出，只能交卷退出
            confirmDialog = DialogUtils.createExitConfirmDialog(ArenaExamActivityPad.this, null, "是否确认交卷？", "取消", "确定");
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH) {     // 小模考不可保存退出，只能交卷退出
            confirmDialog = DialogUtils.createExitConfirmDialog(ArenaExamActivityPad.this, null, getBackTip(), "取消", "确定");
        } else if ((requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI)
                && getUnDoCount() == 0) {                                               // 专项练习、错题重练 的背题模式就检查是否全部做完，如果做完，就交卷，否则弹出保存弹窗
            reciteAutoCommit();
            return;
        } else {                                                                        // 其他，退出提示保存
            confirmDialog = DialogUtils.createExitConfirmDialog(ArenaExamActivityPad.this, null, getBackTip(), "取消", "确定");
        }
        confirmDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected()) {
                    if (!Method.isActivityFinished(ArenaExamActivityPad.this)) {
                        ArenaExamActivityPad.this.finish();
                        CommonUtils.showToast("无网络，请检查网络连接");
                    }
                }
            }
        });
        confirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ArenaHelper.isPaperSCType(realExamBean)) {
                    StudyCourseStatistic.simulatedOperation("退出", Type.getSubject(realExamBean.subject), Type.getCategory(realExamBean.catgory), String.valueOf(realExamBean.paper.id), realExamBean.paper.name);
                }
                if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH) {
                    goAnswerCar(true);
                } else {
                    saveAnswer(0);
                }

                int doneCount = ArenaHelper.getDoneCount(realExamBean);
                int position = extraArgs.getInt("position", -1);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putInt("answerCount", doneCount);
                EventBus.getDefault().post(new SectionalExaminationEvent(bundle));
            }
        });
        if (!confirmDialog.isShowing()) {
            confirmDialog.show();
        }
    }

    // 背题模式全部完成，点击返回自动提交
    private void reciteAutoCommit() {
        if (!NetUtil.isConnected()) {
            ToastUtil.showToast("无网络连接");
            return;
        }
//            goAnswerCar(true);
        // 交卷
        new ArenaExamAnswerCardPresenterImpl(compositeSubscription, new ArenaExamAnswerCardView.SimpleAnswerCardView() {
            @Override
            public void onSubmitAnswerResult(RealExamBeans.RealExamBean bean) {
                hideProgress();
                // 专项练习 为了保持树形展开结构，所以获取全部树形数据，进行数据对比。
                if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI
                        || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI) {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_MSG_TYPE_TREE_DATA_UPDATE_VIEW_REFRESH_ALL));
                }
                ToastUtil.showToast("背题已交卷成功，可在答题记录里再次查看");
                finish();
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
            public void onLoadDataFailed() {
                finish();
            }
        }).submitAnswerCard(realExamBean);
    }

    // 未作答的题数
    private int getUnDoCount() {
        int unCount = 0;
        if (questionBeans != null && questionBeans.size() > 0) {
            for (ArenaExamQuestionBean questionBean : questionBeans) {
                if (questionBean.userAnswer == 0 && questionBean.type != 105) {
                    unCount++;
                }
            }
        }
        return unCount;
    }

    // 退出提示
    private String getBackTip() {
        switch (requestType) {
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST:     // 模考大赛
                return "是否保存本次考试并退出？退出后在考试结束前还可进入答题";
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN:             // 专项模考
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN:         // 真题演练
                return "是否保存本次考试并退出？退出后未完成的练习会保存在答题记录中";
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI:      // 专项练习
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE:            // 智能练习
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI:           // 错题重练
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN:            // 每日特训
                return "确定退出练习？退出后未完成的练习会保存在答题记录中";

            case ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH:            // 小模考
                return "本小模考只有一次作答机会，中途退出系统将为你自动交卷，请谨慎操作";

            default:
                return "是否保存本次练习并退出？";
        }
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
            case 2:                         // 无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("无试题数据");
                break;
        }
    }

    class QuestionPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ArenaPadQuestionView rView = (ArenaPadQuestionView) object;
            cleanView(rView);
            container.removeView(rView);
            questionViewRecycle.add(rView);
        }

        private void cleanView(View rView) {
            ViewGroup mPView = (ViewGroup) rView.getParent();
            if (mPView != null) {
                mPView.removeView(rView);
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ArenaPadQuestionView view = getRecycleView(container);
            // 查看收藏，由于分页加赞，所以总题量手动指定
            if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE && questionCount > 0) {
                view.setQuestionBean(fromType, questionBeans.get(position), questionCount, position);
            } else {
                view.setQuestionBean(fromType, questionBeans.get(position), getCount(), position);
            }
            container.addView(view);
            return view;
        }

        private ArenaPadQuestionView getRecycleView(ViewGroup container) {
            if (questionViewRecycle.size() > 0) {
                return questionViewRecycle.removeFirst();
            } else {
                return new ArenaPadQuestionView(container.getContext());
            }
        }

        @Override
        public int getCount() {
            return questionBeans != null ? questionBeans.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }
}
