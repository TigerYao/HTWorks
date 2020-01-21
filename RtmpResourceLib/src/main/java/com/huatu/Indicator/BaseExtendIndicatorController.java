package com.huatu.Indicator;

import android.animation.Animator;

import java.util.List;

/**
 * Created by cjx on 2018\10\24 0024.
 */

public abstract class BaseExtendIndicatorController  extends BaseIndicatorController{
    private List<Animator> mAnimators;
    /**
     * create animation or animations
     */
    public abstract List<Animator> createExAnimation();

    public void initOrStartAnimation() {
        if(mAnimators==null){
            mAnimators = createExAnimation();
        }else {
            setAnimationStatus(AnimStatus.START);
        }
    }

    /**
     * make animation to start or end when target
     * view was be Visible or Gone or Invisible.
     * make animation to cancel when target view
     * be onDetachedFromWindow.
     *
     * @param animStatus
     */
    public void setAnimationStatus(AnimStatus animStatus) {
        if (mAnimators == null) {
            return;
        }
        int count = mAnimators.size();
        for (int i = 0; i < count; i++) {
            Animator animator = mAnimators.get(i);
            boolean isRunning = animator.isRunning();
            switch (animStatus) {
                case START:
                    if (!isRunning) {
                        animator.start();
                    }
                    break;
                case END:
                    if (isRunning) {
                        animator.end();
                    }
                    break;
                case CANCEL:
                    if (isRunning) {
                        animator.cancel();
                    }
                    break;
            }
        }
    }


    public enum AnimStatus {
        START, END, CANCEL
    }
}
