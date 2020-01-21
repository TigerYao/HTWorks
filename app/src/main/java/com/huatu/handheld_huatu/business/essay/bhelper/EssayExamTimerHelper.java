package com.huatu.handheld_huatu.business.essay.bhelper;

import android.os.Bundle;

import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 申论计时系统
 * 模考计时（计时不暂停，倒计时）：
 * 其他的计时（计时可暂停，正计时）：
 * 模考计时是在EssayExamMaterials中操作，其他计时是在EssayExamEditAnswer。
 */
public class EssayExamTimerHelper {

    public static volatile EssayExamTimerHelper instance = null;

    public static EssayExamTimerHelper getInstance() {
        synchronized (EssayExamTimerHelper.class) {
            if (instance == null) {
                instance = new EssayExamTimerHelper();
            }
        }
        return instance;
    }

    private EssayExamTimerHelper() {
    }

    private boolean isEnableExam = false;                       // 是否开始考试

    public boolean isEnableExam() {
        if (requestType == EssayExamActivity.ESSAY_EXAM_SC) {
            return isEnableExam;
        }
        return true;
    }

    public void setEnableExam(boolean enableExam) {
        isEnableExam = enableExam;
    }

    /**
     * 申论模考大赛进入材料页会首先判断是否开始考试
     * 如果没有开始考试就显示开始考试前的倒计时
     * 如果开始考试就s etEnableExam(true)
     */
    private Subscription fiveTimeSubscription;      // 开始考试前倒计时
    private int requestType;                        // 做题类型

    public void startScExam(int requestType, final PaperQuestionDetailBean paperQuestionDetailBean) {
        this.requestType = requestType;
        if (notStartExam(paperQuestionDetailBean.essayPaper.startTime)) {                            // 还没到开始时间
            CommonUtils.showToast("正在等待开始考试");
            setEnableExam(false);      // 还没有开始考试
            fiveTimeSubscription = Observable.interval(1, TimeUnit.SECONDS)
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
                            updateWaitExamStatus(paperQuestionDetailBean);                      // 每秒更新考试等待时间
                        }
                    });
            updateWaitExamStatus(paperQuestionDetailBean);                      // 每秒更新考试等待时间
        } else {                    // 到了开始时间
            setEnableExam(true);
            initTimeSubscription(false, requestType, null, paperQuestionDetailBean);
            EventBus.getDefault().post(new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_START_EXAM));
        }
    }

    // 模考大赛考试前，每秒检查是否开始考试
    private void updateWaitExamStatus(PaperQuestionDetailBean paperQuestionDetailBean) {
        if (notStartExam(paperQuestionDetailBean.essayPaper.startTime)) {                            // 如果还没到考试时间
            setEnableExam(false);
            ArenaExamMessageEvent scStartCountDown = new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_NOT_START_EXAM);
            scStartCountDown.extraBundle = new Bundle();
            scStartCountDown.extraBundle.putLong("countDown", paperQuestionDetailBean.essayPaper.startTime - (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime));
            EventBus.getDefault().post(scStartCountDown);
        } else {                                                    // 到了考试时间
            CommonUtils.showToast("开始考试");
            setEnableExam(true);
            initTimeSubscription(false, requestType, null, paperQuestionDetailBean);
            EventBus.getDefault().post(new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_START_EXAM));
            if (fiveTimeSubscription != null && !fiveTimeSubscription.isUnsubscribed()) {
                fiveTimeSubscription.unsubscribe();                 // 取消定时发送
                fiveTimeSubscription = null;
            }
        }
    }

    // 不到开始时间
    private boolean notStartExam(long scStartExamTime) {
        return scStartExamTime > (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime);
    }


    /**
     * 这是申论的计时器
     */
    private boolean isTimerStart;                       // 是否开始考试计时
    private Subscription startExamTimeSubscription;     // 考试过程中计时

    public void initTimeSubscription(final boolean isSingle,
                                     final int requestType,
                                     final SingleQuestionDetailBean singleQuestionDetailBean,
                                     final PaperQuestionDetailBean paperQuestionDetailBean) {
        this.requestType = requestType;
        if (isSingle) {
            if (singleQuestionDetailBean == null) {
                return;
            }
        } else {
            if (paperQuestionDetailBean == null) {
                return;
            }
        }

        if (isTimerStart) {
            return;
        }
        if (isEnableExam()) {
            isTimerStart = true;
            setEnableExam(true);
            if (startExamTimeSubscription == null) {
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
                                updateExamTime(isSingle, singleQuestionDetailBean, paperQuestionDetailBean);
                            }
                        });
            }
        }
    }

    public long lastSaveTime = 0;           // 模考最后一次保存时间

    // 每秒更新时间
    private void updateExamTime(boolean isSingle, SingleQuestionDetailBean singleQuestionDetailBean, PaperQuestionDetailBean paperQuestionDetailBean) {
        if (requestType == EssayExamActivity.ESSAY_EXAM_SC) {      // 模考大赛
            if (paperQuestionDetailBean != null && paperQuestionDetailBean.essayPaper != null) {
                paperQuestionDetailBean.spendTime++;
                paperQuestionDetailBean.essayPaper.remainTime--;
                if (curSingleQuestionDetailBean != null) {
                    curSingleQuestionDetailBean.spendTime++;
                }
                long curTime = System.currentTimeMillis();
                if (curTime - lastSaveTime > (5 * 60 * 1000 + (int) (Math.random() * 5 * 60 * 1000))) {
                    lastSaveTime = curTime;
                    EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_MOCK_AUTO_SAVE_PAPER));
                }
                if (paperQuestionDetailBean.essayPaper.remainTime <= 0) {
                    ToastUtils.showEssayToast("考试结束，自动交卷中");
                    pauseExamTime();
                    EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_AUTO_COMMIT_PAPER));
                } else if (paperQuestionDetailBean.essayPaper.remainTime == 10 * 60) {
                    ToastUtils.showEssayToast("考试剩余10分钟\n结束时自动交卷");
                }
            }
        } else {                                                    // 非模考大赛
            if (isSingle) {
                if (singleQuestionDetailBean != null) {
                    singleQuestionDetailBean.spendTime++;
                }
            } else {
                if (paperQuestionDetailBean != null) {
                    paperQuestionDetailBean.spendTime++;
                    if (curSingleQuestionDetailBean != null) {
                        curSingleQuestionDetailBean.spendTime++;
                    }
                }
            }
        }
        EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_time_heartbeat));
    }

    // 为了单题时间计时
    private SingleQuestionDetailBean curSingleQuestionDetailBean;

    public void setCurSingleQuestionDetailBean(SingleQuestionDetailBean var) {
        curSingleQuestionDetailBean = var;
    }


    // 暂停时间
    private long pauseExamTime;

    public boolean needRecordBgTime = false;                // 是否要记录后台时间（人工 & 选择图片，需要记录）

    /**
     * 是否需要记录后台时间。模考需要记录，人工选择图片需要记录。
     */
    public void pauseExamTime() {
        if (requestType != EssayExamActivity.ESSAY_EXAM_SC && needRecordBgTime && isEnableExam()) {
            pauseExamTime = System.currentTimeMillis();
        }
        if (startExamTimeSubscription != null && !startExamTimeSubscription.isUnsubscribed()) {
            startExamTimeSubscription.unsubscribe();
            startExamTimeSubscription = null;
        }
        if (fiveTimeSubscription != null && !fiveTimeSubscription.isUnsubscribed()) {
            fiveTimeSubscription.unsubscribe();
            fiveTimeSubscription = null;
        }
        isTimerStart = false;
    }

    // 重新进入页面，检查是否要要加上backgroundTime
    public void resumeExamTime(int requestType, boolean isSingle, SingleQuestionDetailBean singleQuestionDetailBean, PaperQuestionDetailBean paperQuestionDetailBean) {
        this.requestType = requestType;
        if (requestType == EssayExamActivity.ESSAY_EXAM_SC) {
            if (paperQuestionDetailBean != null && paperQuestionDetailBean.essayPaper != null) {
                int remainTime = (int) ((paperQuestionDetailBean.essayPaper.endTime - (System.currentTimeMillis() + SignUpTypeDataCache.getInstance().dTime)) / 1000);
                int allTime = (int) ((paperQuestionDetailBean.essayPaper.endTime - paperQuestionDetailBean.essayPaper.startTime) / 1000);

                if (remainTime > allTime) {
                    paperQuestionDetailBean.essayPaper.remainTime = allTime;
                } else {
                    paperQuestionDetailBean.essayPaper.remainTime = remainTime;
                }
            }
        } else if (needRecordBgTime && isEnableExam()) {
            if (pauseExamTime > 0) {
                int bgTime = (int) ((System.currentTimeMillis() - pauseExamTime) / 1000);
                if (bgTime > 0) {
                    if (isSingle) {
                        if (singleQuestionDetailBean != null) {
                            singleQuestionDetailBean.spendTime += bgTime;
                        }
                    } else {
                        if (paperQuestionDetailBean != null) {
                            if (paperQuestionDetailBean.essayPaper != null) {
                                paperQuestionDetailBean.spendTime += bgTime;
                                paperQuestionDetailBean.essayPaper.remainTime -= bgTime;
                                if (curSingleQuestionDetailBean != null) {
                                    curSingleQuestionDetailBean.spendTime += bgTime;
                                }
                            }
                        }
                    }
                }
            }
        }
        needRecordBgTime = false;
        pauseExamTime = 0;
        EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_time_heartbeat));
    }

    // Activity退出，取消定时任务
    public void onDestroy() {
        isEnableExam = false;
        needRecordBgTime = false;
        pauseExamTime = 0;
        isTimerStart = false;
        // 各种定时器 五分钟倒计时、考试计时器
        if (fiveTimeSubscription != null && !fiveTimeSubscription.isUnsubscribed()) {
            fiveTimeSubscription.unsubscribe();
            fiveTimeSubscription = null;
        }
        if (startExamTimeSubscription != null && !startExamTimeSubscription.isUnsubscribed()) {
            startExamTimeSubscription.unsubscribe();
            startExamTimeSubscription = null;
        }
    }
}
