package com.huatu.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.gensee.rtmpresourcelib.R;

/**
 * Created by Administrator on 2017/8/30.
 */
public class IncreaseProgressBar extends ProgressBar {
    ObjectAnimator mAnimator;
    Interpolator mInterpolator;
    public IncreaseProgressBar(Context context) {
        super(context);
        mInterpolator= AnimationUtils.loadInterpolator(getContext(),
                R.interpolator.msf_interpolator);
    }

    public IncreaseProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInterpolator= AnimationUtils.loadInterpolator(getContext(),
                R.interpolator.msf_interpolator);
    }

    public void setFastOutInterpoator(){
        mInterpolator=new FastOutSlowInInterpolator();
    }

    public void setCurProgress(final int progress) {
        if(null!=mAnimator) {
            if(mAnimator.isRunning()||mAnimator.isStarted()) mAnimator.cancel();
        }
        if(progress<=0) {
            super.setProgress(0);
            return;
        }
        //super.setProgress(0);
        long time=  Math.abs(progress-this.getProgress())*1500/100;
        mAnimator= ObjectAnimator.ofInt(this, "progress", progress).setDuration(time);
        mAnimator.setStartDelay(200);

        // mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        mAnimator.setInterpolator(mInterpolator);
        mAnimator.start();
    }

    public void setCurProgress2(final int progress) {
        if(null!=mAnimator) {
            if(mAnimator.isRunning()||mAnimator.isStarted()) mAnimator.cancel();
        }
        if(progress<=0) {
            super.setProgress(0);
            return;
        }
        super.setProgress(0);
        long time=  Math.abs(progress-this.getProgress())*1000/100;
        mAnimator= ObjectAnimator.ofInt(this, "progress", progress).setDuration(time);
        mAnimator.setStartDelay(200);

       // mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setInterpolator(mInterpolator);
        mAnimator.start();
    }
}
