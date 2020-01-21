package com.huatu.handheld_huatu.business.play.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ListableScrollView extends NestedScrollView {
    private float yDistance;
    private float xDistance;
    private float xLast;
    private float yLast;
    private ScrollViewListener mscrollViewListener = null;

    public ListableScrollView(Context context) {
        super(context);
    }

    public ListableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.mscrollViewListener = scrollViewListener;
    }

    public interface ScrollViewListener {
        void onScrollChanged(int x, int y, int oldx, int oldy);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mscrollViewListener != null) {
            mscrollViewListener.onScrollChanged(x, y, oldx, oldy);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(ev);
    }
}
