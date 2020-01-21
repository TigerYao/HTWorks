package com.huatu.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by cjx on 2018\9\12 0012.
 */

public class WebProgressBar extends ProgressBar {


    public WebProgressBar(Context context) {
        super(context);

    }

    public WebProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    private android.os.Handler tmpHandler=new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    showDelayAnim();
                    break;
            }
        }
    };

    private void showDelayAnim(){
        if (null != mDelayAnim) {
            if (mDelayAnim.isRunning() || mDelayAnim.isStarted()) mDelayAnim.cancel();
        }
        int currentProgress=this.getProgress();

        if(currentProgress>=95) return;
        int finalProgress=(currentProgress+5);
        finalProgress=finalProgress>95?95:finalProgress;

        long time=  Math.abs(finalProgress-currentProgress)*1500/100;  //1500
        mDelayAnim = ObjectAnimator.ofInt(this, "progress", currentProgress, finalProgress);
        mDelayAnim.setDuration(time);
        mDelayAnim.setInterpolator(new DecelerateInterpolator());
        mDelayAnim.start();
        tmpHandler.sendEmptyMessageDelayed(1,time+2000);
    }

    int currentProgress;
    boolean isAnimStart;
    ObjectAnimator mRunningAnim;
    ObjectAnimator mFinishAnim;

    ObjectAnimator mDelayAnim;
    private boolean isDetached=false;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isDetached=true;
        if(null!=mRunningAnim) mRunningAnim.cancel();
        if(null!=mFinishAnim) mFinishAnim.cancel();
        tmpHandler.removeMessages(1);
        if(null!=mDelayAnim) mDelayAnim.cancel();

    }

    public void onLoadingStart() {
        this.setVisibility(View.VISIBLE);
        this.setAlpha(1.0f);
        if (null != mRunningAnim) {
            if (mRunningAnim.isRunning() || mRunningAnim.isStarted()) mRunningAnim.cancel();
        }
        this.setProgress(0);

    }

    public void onProgressFinished(int newProgress) {
        //XLog.e("onProgressFinished", newProgress + "");
        currentProgress = this.getProgress();
        if (newProgress >= 100 && !isAnimStart) {
            // 防止调用多次动画
            isAnimStart = true;
            this.setProgress(newProgress);
            // 开启属性动画让进度条平滑消失
            startDismissAnimation(this.getProgress());
        } else {
            // 开启属性动画让进度条平滑递增
            startProgressAnimation(newProgress);
        }
    }

    private void startProgressAnimation(int newProgress) {
        if(null!=mRunningAnim) {
            if(mRunningAnim.isRunning()||mRunningAnim.isStarted()) mRunningAnim.cancel();
        }
        if (null != mDelayAnim) {
            if (mDelayAnim.isRunning() || mDelayAnim.isStarted()) mDelayAnim.cancel();
        }
        tmpHandler.removeMessages(1);
        long time=  Math.abs(newProgress-currentProgress)*1500/100;
        mRunningAnim = ObjectAnimator.ofInt(this, "progress", currentProgress, newProgress);
        mRunningAnim.setDuration(time);
        mRunningAnim.setInterpolator(new LinearInterpolator());
        mRunningAnim.start();
        if(newProgress<95)
         tmpHandler.sendEmptyMessageDelayed(1,time+1000);
    }

    private void startDismissAnimation(final int progress) {
        if(null!=mRunningAnim) {
            if(mRunningAnim.isRunning()||mRunningAnim.isStarted()) mRunningAnim.cancel();
        }
        if (null != mDelayAnim) {
            if (mDelayAnim.isRunning() || mDelayAnim.isStarted()) mDelayAnim.cancel();
        }
        tmpHandler.removeMessages(1);
        mFinishAnim = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f);
        mFinishAnim.setDuration(1500);  // 动画时长
        mFinishAnim.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        mFinishAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                int offset = 100 - progress;
                if(isDetached) return;
                WebProgressBar.this.setProgress((int) (progress + offset * fraction));
            }
        });
        mFinishAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                if(isDetached) return;
                WebProgressBar.this.setProgress(0);
                WebProgressBar.this.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        mFinishAnim.start();

    }
}
