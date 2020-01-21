package com.huatu.handheld_huatu.listener;

import android.os.Handler;
import android.os.Message;

import com.huatu.handheld_huatu.utils.Constant;

import java.lang.ref.WeakReference;


/**
 * @author zhaodongdong
 */
public abstract class AdvancedDownTimer {
    private long mDownTimeInterval;
    private long mTotalTime;
    private long mRemainTime;
    private boolean isPause;
    private long mStep = 1000;
    private Handler mDownTimeHandler =null;
    public void setStep(long step) {
        mStep = step;
    }

    /**
     * @return 倒计时是否暂停, true为暂停中
     */
    public boolean isPause() {
        return isPause;
    }

    private static class MyHandler extends Handler {
        WeakReference<AdvancedDownTimer> mHandlerActivityRef;

        public MyHandler(AdvancedDownTimer obj) {
            mHandlerActivityRef = new WeakReference<AdvancedDownTimer>(
                    obj);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mHandlerActivityRef == null || mHandlerActivityRef.get() == null) {
                return;
            }
            AdvancedDownTimer mAdvancedDownTimer = mHandlerActivityRef.get();
            if (mAdvancedDownTimer!=null) {
                switch (msg.what) {
                    case Constant.MSG_DOWNTIME_START:
                        mAdvancedDownTimer.mRemainTime = mAdvancedDownTimer.mRemainTime - mAdvancedDownTimer
                                .mDownTimeInterval;
                        if (mAdvancedDownTimer.mRemainTime < 0) {
                            mAdvancedDownTimer.onFinish();
                        } else {
                            mAdvancedDownTimer.onTick(mAdvancedDownTimer.mRemainTime, mAdvancedDownTimer.mTotalTime >
                                    0 ? Long.valueOf(100 *
                                    (mAdvancedDownTimer.mTotalTime - mAdvancedDownTimer.mRemainTime) /
                                    mAdvancedDownTimer.mTotalTime).intValue() : 0);
                            sendMessageDelayed(obtainMessage(Constant.MSG_DOWNTIME_START), mAdvancedDownTimer.mStep);
                        }
                        break;
                    case Constant.MSG_DOWNTIME_PAUSE:
                        mAdvancedDownTimer.onPause();
                        break;
                }
            }
        }

    }

    public AdvancedDownTimer(long mDownTimeInterval, long mTotalTime, long mRemainTime) {
        mDownTimeHandler=new MyHandler(this);
        this.mDownTimeInterval = mDownTimeInterval;
        this.mTotalTime = mTotalTime;
        this.mRemainTime = mRemainTime;
    }

    /**
     * 获取剩余时间
     *
     * @return 剩余时间
     */
    public long getmRemainTime() {
        return mRemainTime;
    }

    public final void seek(int value) {
        synchronized (AdvancedDownTimer.this) {
            mRemainTime = ((100 - value) * mTotalTime) / 100;
        }
    }

    /**
     * 重置时间步长
     *
     * @param timeInterval time step
     */
    public final void resetTimeInterval(long timeInterval) {
        mDownTimeInterval = timeInterval;
    }

    /**
     * 倒计时开启
     */
    public synchronized final AdvancedDownTimer start() {
        if(mDownTimeHandler==null){
            return this;
        }
        if (mRemainTime<= 0){
            onFinish();
            return this;
        }
        isPause = false;
        mDownTimeHandler.sendMessage(mDownTimeHandler.obtainMessage(Constant.MSG_DOWNTIME_START));
        return this;
    }

    /**
     * 倒计时暂停
     */
    public final void pause() {
        if(mDownTimeHandler==null){
            return;
        }
        isPause = true;
        mDownTimeHandler.removeMessages(Constant.MSG_DOWNTIME_START);
        mDownTimeHandler.sendMessageAtFrontOfQueue(mDownTimeHandler.obtainMessage(Constant.MSG_DOWNTIME_PAUSE));
    }

    /**
     * 倒计时恢复
     */
    public final void resume() {
        if(mDownTimeHandler==null){
            return;
        }
        isPause = false;
        mDownTimeHandler.removeMessages(Constant.MSG_DOWNTIME_PAUSE);
        mDownTimeHandler.sendMessageAtFrontOfQueue(mDownTimeHandler.obtainMessage(Constant.MSG_DOWNTIME_START));
    }

    /**
     * 倒计时取消
     */
    public final void cancel() {
        if(mDownTimeHandler==null){
            return;
        }
        mDownTimeHandler.removeMessages(Constant.MSG_DOWNTIME_START);
        mDownTimeHandler.removeMessages(Constant.MSG_DOWNTIME_PAUSE);
    }

    public abstract void onFinish();

    public abstract void onPause();

    public abstract void onTick(long millisUnFinished, int percent);
}
