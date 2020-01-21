package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.util.AttributeSet;

public class ArenaProportionImageView extends android.support.v7.widget.AppCompatImageView {

    public ArenaProportionImageView(Context context) {
        this(context, null);
    }

    public ArenaProportionImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArenaProportionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private double proportion = 0.487;

    public void setProportion(double proportion) {
        this.proportion = proportion;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.makeMeasureSpec((int) (specSize * proportion), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, height);
    }
}
