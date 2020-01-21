package com.huatu.handheld_huatu.business.matches.customview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class ShakeImageView extends android.support.v7.widget.AppCompatImageView {

    private ObjectAnimator shakeAnim;
    private ValueAnimator scaleAnim;

    public ShakeImageView(Context context) {
        this(context, null);
    }

    public ShakeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShakeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 摇晃的动画
        shakeAnim = ObjectAnimator.ofFloat(ShakeImageView.this, "rotation", 0, -10, 0, 10, 0);
        shakeAnim.setInterpolator(new LinearInterpolator());                         // 设置线性差值器
        shakeAnim.setRepeatCount(ValueAnimator.INFINITE);                            // 设置重复
        shakeAnim.setDuration(600);

        // 放大缩小的动画
        scaleAnim = ValueAnimator.ofFloat(1f, 1.1f, 1.01f, 0.95f, 1f, 1f, 1f, 1f, 1f);
        scaleAnim.setInterpolator(new LinearInterpolator());                         // 设置线性差值器
        scaleAnim.setRepeatCount(ValueAnimator.INFINITE);                            // 设置重复
        scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {     // 设置持续事件
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                ShakeImageView.this.setScaleX(values);
                ShakeImageView.this.setScaleY(values);
            }
        });
        scaleAnim.setDuration(900);
    }

//    @Override
//    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
//        super.onVisibilityChanged(changedView, visibility);
//        showThis(visibility);
//    }
//
//    private void showThis(int visibility) {
//        if (visibility == VISIBLE) {
//            if (shakeAnim != null) {
////                shakeAnim.start();
//            }
//            if (scaleAnim != null) {
//                scaleAnim.start();
//            }
//        } else {
//            if (shakeAnim != null) {
////                shakeAnim.cancel();
//            }
//            if (scaleAnim != null) {
//                scaleAnim.cancel();
//            }
//        }
//    }

    public void startAnim() {
        if (scaleAnim != null) {
            scaleAnim.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (shakeAnim != null) {
            shakeAnim.cancel();
        }
        if (scaleAnim != null) {
            scaleAnim.cancel();
        }
    }
}
