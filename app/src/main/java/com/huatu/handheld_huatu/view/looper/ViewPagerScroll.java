package com.huatu.handheld_huatu.view.looper;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * desc:ViewPagerScroll Controller
 *
 * @author zhaodongdong
 *         QQ: 676362303
 *         email: androidmdeveloper@163.com
 */
public class ViewPagerScroll extends Scroller {
    //滑动速度,值越大,滑动越慢,滑动越快回导致3d效果不明显
    private int mScrollDuration = 800;
    private boolean zero;

    public int getScrollDuration() {
        return mScrollDuration;
    }

    public void setScrollDuration(int scrollDuration) {
        mScrollDuration = scrollDuration;
    }

    public ViewPagerScroll(Context context) {
        super(context);
    }

    public ViewPagerScroll(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public ViewPagerScroll(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, zero ? 0 : mScrollDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, zero ? 0 : mScrollDuration);
    }

    public boolean isZero() {
        return zero;
    }

    public void setZero(boolean zero) {
        this.zero = zero;
    }
}
