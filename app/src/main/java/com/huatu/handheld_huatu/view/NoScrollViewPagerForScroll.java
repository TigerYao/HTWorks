package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by saiyuan on 2017/9/6.
 */

public class NoScrollViewPagerForScroll extends NoScrollViewPager {
    private boolean isForScrollFlag = true;

    public NoScrollViewPagerForScroll(Context context) {
        super(context);
    }

    public NoScrollViewPagerForScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setForScrollFlag(boolean flag) {
        isForScrollFlag = flag;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expect = heightMeasureSpec;
        if(isForScrollFlag) {
            expect = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, expect);
    }
}
