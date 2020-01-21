package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.util.AttributeSet;

public class EssayProportionImageView extends android.support.v7.widget.AppCompatImageView {

    public EssayProportionImageView(Context context) {
        this(context, null);
    }

    public EssayProportionImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EssayProportionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.makeMeasureSpec((int) (specSize * 1.34), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, height);
    }
}
