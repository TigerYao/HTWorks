package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by saiyuan on 2016/10/25.
 */
public class NoScrollViewPager extends ViewPager {
    private boolean isCanScroll = true;
    private boolean isScroll = true;
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollEnable(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y){
        if (isScroll){
            super.scrollTo(x, y);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!isCanScroll) {
                    isScroll = false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(!isCanScroll) {
                    isScroll = true;
                }
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isCanScroll) {
            return true;
        }
        return super.onTouchEvent(event);
    }
}
