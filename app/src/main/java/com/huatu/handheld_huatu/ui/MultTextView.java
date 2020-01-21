package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjx on 2018\11\30 0030.
 */

public class MultTextView extends View {
    protected int mWidth;
    protected int mHeight;
    private String[] mTextArray;
    private final  int SPLITNUM=3;
    Paint mPaint;
    protected List<Rect> mRectList;//点击用的矩形集合
    public MultTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint =  getPaint(Color.parseColor("#4A4A4A"),DensityUtils.sp2px(context,10)) ;
        mRectList = new ArrayList<>();
    }

    private Paint getPaint(int paintColor, float paintSize) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setTextSize(paintSize);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    public void setNameList(String[] nameArray){
        mTextArray=nameArray;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        if(ArrayUtils.isEmpty(mTextArray)) return;
        //为了与月日历保持一致，往上压缩一下,5倍的关系
       // mHeight = (int) (getHeight() - DensityUtils.dp2px(getContext(), 2));
        if(ArrayUtils.isEmpty(mRectList)){
             for(int k=0;k<SPLITNUM;k++){
                Rect rect = new Rect(k * mWidth / SPLITNUM, 0, k * mWidth / SPLITNUM + mWidth / SPLITNUM, mHeight);
                mRectList.add(rect);
            }
        }

        int length=Math.min(SPLITNUM,ArrayUtils.size(mTextArray));
        for (int i = 0; i < length; i++) {
            Rect rect =mRectList.get(i);
             String date = mTextArray[i];;
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            canvas.drawText(date, rect.centerX(), baseline, mPaint);

        }
    }
}
