package com.huatu.widget;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by cjx on 2019\1\4 0004.
 */

public class ExpandCollapseAnimation extends Animation {
    private final View mTargetView;
    private final int mStartHeight;
    private final int mEndHeight;
    private static final int DEFAULT_ANIM_DURATION = 300;
    public ExpandCollapseAnimation(View view, int startHeight, int endHeight) {
        mTargetView = view;
        mStartHeight = startHeight;
        mEndHeight = endHeight;
        setDuration(DEFAULT_ANIM_DURATION);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final int newHeight = (int) ((mEndHeight - mStartHeight) * interpolatedTime + mStartHeight);
        //mTv.setMaxHeight(newHeight - mMarginBetweenTxtAndBottom);
         /*   if (Float.compare(mAnimAlphaStart, 1.0f) != 0) {
                applyAlphaAnimation(mTv, mAnimAlphaStart + interpolatedTime * (1.0f - mAnimAlphaStart));
            }*/
        mTargetView.getLayoutParams().height = newHeight;
        mTargetView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
