package com.huatu.handheld_huatu.business.ztk_zhibo.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Created by saiyuan on 2017/9/7.
 */

public class NoScrollWrapViewPager extends ViewPager {
    private boolean isCanScroll = true;
    private boolean isScroll = true;
    private List<View> views;
    private int mustBeHeight = 0;

    public NoScrollWrapViewPager(Context context) {
        super(context);
    }

    public NoScrollWrapViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                requestLayout();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setScrollEnable(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (isScroll) {
            super.scrollTo(x, y);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isCanScroll) {
                    isScroll = false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isCanScroll) {
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
        if (!isCanScroll) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        // find the current child view
        if (views == null || views.size() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        View view = views.get(getCurrentItem());
        if (view != null) {
            // measure the current child view with the specified measure spec
            view.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height = view.getMeasuredHeight();
        }
        if (mustBeHeight != 0 && mustBeHeight > height) {
            height = mustBeHeight;
        } else {
//            mustBeHeight = height;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setViews(List<View> pagers) {
        this.views = pagers;
    }

    public void setHeight(int height) {
        this.mustBeHeight = height;
    }
}
