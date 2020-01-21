package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by saiyuan on 2017/10/31.
 */

public class GradViewForScroll extends GridView {
    public GradViewForScroll(Context context) {
        super(context);
    }

    public GradViewForScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradViewForScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expect = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expect);
    }
}
