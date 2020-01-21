package com.huatu.handheld_huatu.business.essay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamTimerHelper;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamCheckDetailV2;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamEditAnswer;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamMaterials;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamRightAns;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamRobotCheckReport;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

/**
 * 答题页
 * 包含材料页，答题页，正确答案页，报告页，批改页
 */

public class EssayExamActivity extends BaseActivity {

    // 做题类型
    public static final int ESSAY_EXAM_NORMAL = 1;                                  // 一般做题
    public static final int ESSAY_EXAM_HOMEWORK = ESSAY_EXAM_NORMAL + 1;            // 课后作业
    public static final int ESSAY_EXAM_SC = ESSAY_EXAM_HOMEWORK + 1;                // 模考大赛做题，时间倒计时

    protected int requestType;                                                      // 做题类型
    protected Bundle extraArgs = null;                                              // 上层传递Bundle参数

    private int areaId;                     // 地区id
    private int curPos;                     // 当前是第几个问题

    private int staticCorrectMode = 0;      // 指定批改方式，如果指定了，就不能切换 0、不限 1、智能 2、人工

    private boolean isSingle;               // 是套题还是单题

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_essay_exam_layout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 如果不是被回收，就清理数据，必须在super之前调用
        if (savedInstanceState == null) {
            EssayExamDataCache.getInstance().clearData();
            EssayExamTimerHelper.getInstance().onDestroy();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(BaseMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_ON_BACKFINISH) {                 // 关闭本页
            finish();
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_ON_BACKPRESS) {           // Fragment回退
            onBackPressed();
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_SHOW_PROGRESS_BAR) {      // 显示进度条
            showProgress();
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_DISMISS_PROGRESS_BAR) {   // 隐藏进度条
            hideProgress();
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_ONLOAD_DATA_FAILED) {     // 获取数据失败
//            onLoadDataFailed();
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate2(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_start_EssayExamEditAnswer) {              // 开始答题
            if (event.extraBundle != null) {
                areaId = event.extraBundle.getInt("areaId");
                curPos = event.extraBundle.getInt("curPos");
            }
            showEssayEditFragment();                                                                // 显示答题页
        } else if (event.type == EssayExamMessageEvent.EssayExam_start_EssayExamEditAnswer1) {      // 答案页，返回到材料页
            if (event.extraBundle != null) {
                areaId = event.extraBundle.getInt("areaId");
            }
            showMaterialFragment(3);                                                          // 显示材料页
        } else if (event.type == EssayExamMessageEvent.EssayExam_start_EssayExamMaterials1) {       // 显示材料页
            showMaterialFragment(1);
        } else if (event.type == EssayExamMessageEvent.EssayExam_start_EssayExamAnswerDetail) {     // 显示答案页
            if (event.extraBundle != null) {
                curPos = event.extraBundle.getInt("curPos");
            }
            showEssayAnswerFragment();                                                 // 显示答案页
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit) {                 // 交卷成功

            if (requestType == ESSAY_EXAM_SC) {
                //申论模考交卷成功
                ArenaHelper.showGoldExperience(ArenaHelper.MATCH_ENTER);
                finish();
            } else {
                if (isSingle && SpUtils.getSingleCheckReward()) {
                    ToastUtils.showRewardToast("SL_CORR_SINGLE");
                    SpUtils.setSingleCheckReward(false);
                } else if (!isSingle && SpUtils.getMultiCheckReward()) {
                    ToastUtils.showRewardToast("SL_CORR_SET");
                    SpUtils.setMultiCheckReward(false);
                }

                // 人工批改，交卷后，提示 & finish，
                if (EssayExamDataCache.getInstance().correctMode == 2) {
                    finish();
                } else if (isSingle) {
                    showEssayCheckFragment();                                  // 单题交卷成功，直接显示批改页
                } else {
                    showEssayCheckReport();                                    // 套题交卷成功，显示报告页
                }
            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit_fail) {
            ToastUtils.showEssayToast("交卷失败");
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave) {                   // 保存试卷
            finish();
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave_fail) {              // 保存失败
            finish();
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_AUTO_COMMIT_PAPER) {               // 模考大赛，自动交卷，显示答题页
            PaperQuestionDetailBean paperQuestionDetailBean = EssayExamDataCache.getInstance().cachePaperQuestionDetailBean;
            // 如果试题为空，就直接finish
            if (paperQuestionDetailBean == null || paperQuestionDetailBean.essayQuestions == null || paperQuestionDetailBean.essayPaper == null) {
                ToastUtils.showEssayToast("考试已结束，您仍未作答");
                EssayExamActivity.this.finish();
                return true;
            }
            int unDoneCount = EssayExamImpl.getUnfinishedCount(isSingle, null, paperQuestionDetailBean);
            // 如果一道题都没做，就直接finish
            if (unDoneCount == paperQuestionDetailBean.essayQuestions.size()) {
                ToastUtils.showEssayToast("考试已结束，您仍未作答");
                EssayExamActivity.this.finish();
                return true;
            }
            // 如果不存在做题页，就跳转交卷；如果存在，就跳转，做题页会自动接收event并自动交卷。
            showEssayEditFragment(true);
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {                                        // 显示考试时间
        if (event == null || event.type <= 0) {
            return;
        }
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_START_EXAM) {
            goneExamFiveTip();          // 隐藏倒计时考试时间
        } else if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_NOT_START_EXAM) {
            showExamFiveTip(event);          // 还没到考试时间，就显示提示
        }
    }

    private void goneExamFiveTip() {
        if (arenaScStartFiveTip != null) {
            arenaScStartFiveTip.setVisibility(View.GONE);
        }
    }

    // 模考大赛开始考试前倒计时
    private View arenaScStartFiveTip;
    private TextView arenaScStartFiveTiptv;

    private void showExamFiveTip(ArenaExamMessageEvent event) {
        if (arenaScStartFiveTiptv == null || arenaScStartFiveTip == null) {
            ViewStub stubScStartFive = rootView.findViewById(R.id.viewstub_id_sc_start_five);
            arenaScStartFiveTip = stubScStartFive.inflate();
            arenaScStartFiveTiptv = rootView.findViewById(R.id.arena_sc_start_five_tip_tv);
            arenaScStartFiveTip.setVisibility(View.VISIBLE);
        }
        if (arenaScStartFiveTiptv != null) {
            String timeInfo = TimeUtils.getSecond22HourMinTime((int) (event.extraBundle.getLong("countDown") / 1000));
            arenaScStartFiveTiptv.setText(timeInfo);
        }
    }

    @Override
    public boolean setSupportFragment() {
        return true;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (originIntent != null) {
            requestType = originIntent.getIntExtra("request_type", 1);             // 显示类型
            extraArgs = originIntent.getBundleExtra("extra_args");                             // 上层传递的Bundle参数
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            isSingle = extraArgs.getBoolean("isSingle");                                        // 是否是单题
            staticCorrectMode = extraArgs.getInt("staticCorrectMode", 0);

            if (EssayExamDataCache.getInstance().correctMode == 0) {
                EssayExamDataCache.getInstance().correctMode = extraArgs.getInt("correctMode", 0);

                if (requestType == ESSAY_EXAM_SC) {                 // 模考是智能答题
                    EssayExamDataCache.getInstance().correctMode = 1;
                } else if (requestType == ESSAY_EXAM_HOMEWORK) {    // 课后作业是人工答题
                    EssayExamDataCache.getInstance().correctMode = 2;
                } else if (staticCorrectMode != 0) {                // 如果指定，就是指定的方式
                    EssayExamDataCache.getInstance().correctMode = staticCorrectMode;
                }
            }
        }
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    // 由于是否是文章写作 从前页面获取 改为 材料页获取问题的的是返回判断 所以材料页获取了之后，要回来告诉Activity里的 extraArgs 其他页面还需要使用
    public void setIsArgue(boolean isFromArgue) {
        if (extraArgs == null) {
            extraArgs = new Bundle();
        }
        extraArgs.putBoolean("isFromArgue", isFromArgue);
    }

    @Override
    protected void onLoadData() {
        // 首次显示对应的Fragment
        // 注：如果系统回收，就不会走onLoadData，就不会重新创建各个Fragment
        if (requestType == ESSAY_EXAM_SC) {                             // 模考大赛改变考试状态
            EssayExamTimerHelper.getInstance().setEnableExam(false);
        }
        showMaterialFragment(0);
    }

    /**
     * 显示材料页
     *
     * @param type 从哪跳到哪，用于隐藏当前Fragment，显示目标Fragment
     * 0：EssayExamMaterials --> EssayExamMaterials     第一次显示材料页
     * 1：EssayExamEditAnswer --> EssayExamMaterials    答题页 到 材料页
     * 2：EssayExamCheckDetail --> EssayExamMaterials   批改页 到 材料页   （已弃用）
     * 3：EssayExamRightAns  --> EssayExamMaterials     答案页 到 材料页
     */

    EssayExamMaterials fragmentEssayExamMaterials;              // 材料页

    private void showMaterialFragment(int type) {
        QMUIStatusBarHelper.setStatusBarLightMode(EssayExamActivity.this);
        initMaterialFragment();
        if (type == 0) {
            addFragment(EssayExamMaterials.class.getSimpleName(), fragmentEssayExamMaterials, R.id.essay_exam_container, false, false, true);
        } else if (type == 1) {
            addFragment(EssayExamEditAnswer.class.getSimpleName(), fragmentEssayExamMaterials, R.id.essay_exam_container, false, true, true);
        } else if (type == 3) {
            addFragment(EssayExamRightAns.class.getSimpleName(), fragmentEssayExamMaterials, R.id.essay_exam_container, false, true, true);
        }
    }

    // 初始化材料页
    private void initMaterialFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(EssayExamMaterials.class.getSimpleName());
        if (fragment != null) {
            fragmentEssayExamMaterials = (EssayExamMaterials) fragment;
        } else {
            Bundle arg = new Bundle();
            arg.putInt("request_type", requestType);
            arg.putBundle("extra_args", extraArgs);
            fragmentEssayExamMaterials = EssayExamMaterials.newInstance(arg);
        }
    }

    /**
     * 显示答案页
     */
    EssayExamRightAns essayExamRightAns;                    // 答案页

    private void showEssayAnswerFragment() {
        QMUIStatusBarHelper.setStatusBarLightMode(EssayExamActivity.this);
        if (essayExamRightAns == null) {
            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = manager.findFragmentByTag(EssayExamRightAns.class.getSimpleName());
            if (fragment != null) {
                essayExamRightAns = (EssayExamRightAns) fragment;
            } else {
                Bundle arg = new Bundle();
                arg.putInt("request_type", requestType);
                arg.putBundle("extra_args", extraArgs);
                arg.putInt("curPos", curPos);
                essayExamRightAns = EssayExamRightAns.newInstance(arg);
            }
        } else {
            if (curPos != essayExamRightAns.getCurrentPosition()) {//减少EventBus事件传递
                essayExamRightAns.setSelectPosition(curPos);
            }
        }
        addFragment(EssayExamMaterials.class.getSimpleName(), essayExamRightAns, R.id.essay_exam_container, true, true, true);
    }

    /**
     * 显示答题页
     *
     * @param type 从哪跳到哪，用于隐藏当前Fragment，显示目标Fragment
     * 0： EssayExamMaterials --> EssayExamEditAnswer 从 材料页 到 答题页
     */
    public EssayExamEditAnswer fragmentEssayExamEditAnswer;        // 答题页

    private void showEssayEditFragment() {
        QMUIStatusBarHelper.setStatusBarDarkMode(EssayExamActivity.this);
        showEssayEditFragment(false);
    }

    private void showEssayEditFragment(boolean autoSubmit) {
        if (fragmentEssayExamEditAnswer == null) {
            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = manager.findFragmentByTag(EssayExamEditAnswer.class.getSimpleName());
            if (fragment != null) {
                fragmentEssayExamEditAnswer = (EssayExamEditAnswer) fragment;
            } else {
                Bundle arg = new Bundle();
                arg.putInt("request_type", requestType);
                if (extraArgs != null) {
                    extraArgs.putInt("areaId", areaId);
                    extraArgs.putBoolean("auto_submit", autoSubmit);
                    extraArgs.putInt("curPos", curPos);
                    extraArgs.putInt("correctMode", EssayExamDataCache.getInstance().correctMode);
                }
                arg.putBundle("extra_args", extraArgs);
                fragmentEssayExamEditAnswer = EssayExamEditAnswer.newInstance(arg);
            }
        }
        addFragment(EssayExamMaterials.class.getSimpleName(), fragmentEssayExamEditAnswer, R.id.essay_exam_container, true, true, true);
    }

    /**
     * 套题答完题，显示批改报告页
     */
    private void showEssayCheckReport() {
        if (extraArgs == null) {
            extraArgs = new Bundle();
        }
        if (extraArgs.getLong("answerId") <= 0) {
            if (fragmentEssayExamEditAnswer != null) {
                extraArgs.putLong("answerId", fragmentEssayExamEditAnswer.answerCardId);
            }
            if (fragmentEssayExamMaterials != null) {
                extraArgs.putString("areaName", fragmentEssayExamMaterials.areaName);
            }
        }
        extraArgs.putInt("correctMode", 1);

        EssayExamRobotCheckReport.lanuch(this, extraArgs);
        this.finish();
    }

    /**
     * 单题交卷成功，显示批改页
     */
    private void showEssayCheckFragment() {
        if (extraArgs == null) {
            extraArgs = new Bundle();
        }
        if (extraArgs.getLong("answerId") <= 0) {
            if (fragmentEssayExamEditAnswer != null) {
                extraArgs.putLong("answerId", fragmentEssayExamEditAnswer.answerCardId);
            }
            if (fragmentEssayExamMaterials != null) {
                extraArgs.putString("areaName", fragmentEssayExamMaterials.areaName);
            }
        }
        extraArgs.putInt("correctMode", EssayExamDataCache.getInstance().correctMode);
        EssayExamCheckDetailV2.lanuch(this, extraArgs);
        this.finish();
    }

    protected int getFragmentContainerId(int clickId) {
        return R.id.essay_exam_container;
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
     * 根据材料上下滑动，开启动画，下方字体、收藏栏的隐现
     *
     * @param open 隐现
     */
    public void startAnim(boolean open) {
        if (fragmentEssayExamMaterials != null) {
            if (fragmentEssayExamMaterials.isVisible() && fragmentEssayExamMaterials.isAdded()) {
                fragmentEssayExamMaterials.startAnim(open);
            }
        }
    }

    /**
     * 开启本Activity
     */
    public static void show(Activity context, int from, Bundle args) {
        Intent intent = new Intent(context, EssayExamActivity.class);
        intent.putExtra("request_type", from);
        intent.putExtra("extra_args", args);
        context.startActivity(intent);
    }

    public static void show(Context context, int from, Bundle args) {
        Intent intent = new Intent(context, EssayExamActivity.class);
        intent.putExtra("request_type", from);
        intent.putExtra("extra_args", args);
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("areaId", areaId);
        outState.putInt("curPos", curPos);
        outState.putInt("correctMode", EssayExamDataCache.getInstance().correctMode);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        areaId = savedInstanceState.getInt("areaId");
        curPos = savedInstanceState.getInt("curPos");
        EssayExamDataCache.getInstance().correctMode = savedInstanceState.getInt("correctMode");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
