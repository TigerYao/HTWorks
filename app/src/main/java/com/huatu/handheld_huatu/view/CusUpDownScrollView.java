package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by dongd on 2016/10/20.
 */

public class CusUpDownScrollView extends ScrollView {
    public CusUpDownScrollView(Context context) {
        super(context);
        init();
    }

    public CusUpDownScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CusUpDownScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setScrollBarSize(0);
    }

    private ScrollListener mListener;

    public static interface ScrollListener {
        public void scrollOritention(int oritention);
    }

    public static final int SCROLL_UP = 0x01;

    /**
     * ScrollView正在向下滑动
     */
    public static final int SCROLL_DOWN = 0x10;

    /**
     * 最小的滑动距离
     */
    private static final int SCROLLLIMIT = 40;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mListener != null) {
            if (oldt > t && oldt - t > SCROLLLIMIT) {// 向下
                if (mListener != null)
                    mListener.scrollOritention(SCROLL_DOWN);
            } else if (oldt < t && t - oldt > SCROLLLIMIT) {// 向上
                if (mListener != null)
                    mListener.scrollOritention(SCROLL_UP);
            }
        }
    }

    public void setScrollListener(ScrollListener l) {
        this.mListener = l;
    }
}
