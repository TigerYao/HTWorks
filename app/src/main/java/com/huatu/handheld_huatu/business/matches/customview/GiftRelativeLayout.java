package com.huatu.handheld_huatu.business.matches.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.event.match.MatchEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 悬浮的礼物Layout，可以根据接收事件，进行弹出/消失
 */
public class GiftRelativeLayout extends RelativeLayout {

    private int STATE_OUT = 0;                      // 显示着
    private int STATE_IN = 1;                       // 隐藏着
    private int STATE_MOVING_TO_OUT = 2;            // 动画正在显示
    private int STATE_MOVING_TO_IN = 3;             // 动画正在隐藏

    private ObjectAnimator showAnim;                // 显示的动画
    private ObjectAnimator hideAnim;                // 隐藏的动画

    private int state = STATE_OUT;                  // 初始状态

    public GiftRelativeLayout(Context context) {
        this(context, null);
    }

    public GiftRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(MatchEvent event) {
        int type = event.type;
        switch (type) {
            case MatchEvent.GIFT_SCROLL_DOWN:                 // 向下滑动，显示ivGift
                showIvGift();
                break;
            case MatchEvent.GIFT_SCROLL_UP:                 // 向上滑动，隐藏ivGift
                hideIvGift();
                break;
        }
    }

    /**
     * 显示ivGift
     */
    private void showIvGift() {
        if (showAnim == null) {
            float halfWidth = (float) getMeasuredWidth() / 2 == 0 ? 50 : (float) getMeasuredWidth() / 2;
            // 显示的动画
            showAnim = ObjectAnimator.ofFloat(GiftRelativeLayout.this, "translationX", halfWidth, 0);
            showAnim.setDuration(300);
            showAnim.setInterpolator(new LinearInterpolator());
            showAnim.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    state = STATE_MOVING_TO_OUT;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    state = STATE_OUT;
                }
            });
        }
        if (state == STATE_IN || state == STATE_MOVING_TO_IN) {
            showAnim.start();
        }
    }

    /**
     * 隐藏ivGift
     */
    private void hideIvGift() {
        if (hideAnim == null) {
            float halfWidth = (float) getMeasuredWidth() / 2 == 0 ? 50 : (float) getMeasuredWidth() / 2;
            // 隐藏的动画
            hideAnim = ObjectAnimator.ofFloat(GiftRelativeLayout.this, "translationX", 0, halfWidth);
            hideAnim.setDuration(300);
            hideAnim.setInterpolator(new LinearInterpolator());
            hideAnim.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    state = STATE_MOVING_TO_IN;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    state = STATE_IN;
                }
            });
        }
        if (state == STATE_OUT || state == STATE_MOVING_TO_OUT) {
            hideAnim.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (showAnim != null) {
            showAnim.cancel();
        }
        if (hideAnim != null) {
            hideAnim.cancel();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
