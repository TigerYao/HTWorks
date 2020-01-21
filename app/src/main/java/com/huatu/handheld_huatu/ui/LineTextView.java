package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.huatu.utils.DensityUtils;
import com.huatu.viewpagerindicator.BoldTextView;

/**
 * Created by cjx on 2018\8\20 0020.
 */

public class LineTextView extends BoldTextView {

    Paint rectPaint;
    private   int indicatorMarginBottom=0;
    private   int indicatorHeight=0;
    private boolean mIsSelected=false;

    public LineTextView(Context context) {
        super(context);
        init(context);
    }

    public LineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        indicatorMarginBottom= DensityUtils.dp2px(context,11);
        indicatorHeight= DensityUtils.dp2px(context,2);
       // mPaint.setTextSize(DensityUtils.sp2px(getContext(),10f));
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(Color.parseColor("#FF3F47"));
    }

    @Override
    protected void onDraw(Canvas canvas)  {
        super.onDraw(canvas);

        if(mIsSelected){
            float lineLeft = 0;
            float lineRight = 0;
            float lineOffset=0;

            int height=getHeight();
            canvas.drawRect(0, height - indicatorHeight-indicatorMarginBottom, getWidth(), height-indicatorMarginBottom, rectPaint);
         }
     }

    public void setSelected(boolean isSelected){
        mIsSelected=isSelected;
        setBold(mIsSelected);
    }
}
