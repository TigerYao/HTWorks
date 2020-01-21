package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ProportionRelativeLayout extends RelativeLayout {
    public ProportionRelativeLayout(Context context) {
        super(context);
    }

    public ProportionRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProportionRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private double proportion = 0.24;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.makeMeasureSpec((int) (specSize * proportion), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, height);
    }
}
