package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjx on 2018\11\30 0030.
 */

public class MultTagView extends View {
    protected int mWidth;
    protected int mHeight;
    private String[] mTextArray;

    private float mPadding=0;
    private float mMarginleft=0;
    private float mRaduis=0;
    Paint mTxtPaint,mBgPaint;

    protected float mHollowCircleStroke;//空心圆粗细
    protected List<RectF> mRectList;//点击用的矩形集合
    public MultTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHollowCircleStroke=DensityUtils.dp2floatpx(context,0.6f);
        int mainColor=Color.parseColor("#FF3F47");
        mTxtPaint =  getPaint(mainColor,DensityUtils.sp2px(context,10)) ;

        mBgPaint=generatePaint(mainColor,Paint.Style.STROKE,mHollowCircleStroke);


        mPadding=DensityUtils.dp2floatpx(context,6);
        mMarginleft=DensityUtils.dp2floatpx(context,5);
        mRaduis=DensityUtils.dp2floatpx(context,8);
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

    private Paint generatePaint(int color,Paint.Style style,float width)
    {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(style);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(width);
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
       // mHeight = getHeight();
        mHeight = (int) (getHeight() - DensityUtils.dp2px(getContext(), 2));
        if(ArrayUtils.isEmpty(mTextArray)) return;


        Rect rect = new Rect();
        float tmpWidthLeft=mHollowCircleStroke;

        mRectList.clear();
        for(int i=0;i<mTextArray.length;i++){
            rect.setEmpty();
            mTxtPaint.getTextBounds(mTextArray[i],0,mTextArray[i].length(), rect);
            float adjustDistance=rect.width()+(2*mPadding)+mMarginleft;
            if((tmpWidthLeft+adjustDistance)>mWidth){
                 break;
            }
            RectF tmpRect = new RectF(tmpWidthLeft, mHollowCircleStroke, tmpWidthLeft+rect.width()+(2*mPadding), mHeight+mHollowCircleStroke);
            mRectList.add(tmpRect);
            tmpWidthLeft += adjustDistance;
        }

        float mhasLeft=mWidth-tmpWidthLeft;
        for(int k=0;k<mRectList.size();k++){

            RectF curRect=mRectList.get(k);
            curRect.offset(mhasLeft,0);
            Paint.FontMetricsInt fontMetrics = mTxtPaint.getFontMetricsInt();
            int baseline = (int)(curRect.bottom + curRect.top - fontMetrics.bottom - fontMetrics.top) / 2;

            canvas.drawRoundRect(curRect, mRaduis, mRaduis, mBgPaint);


            canvas.drawText(mTextArray[k], curRect.centerX(), baseline, mTxtPaint);

        }

       /* Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(), rect);
        width = rect.width()

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

        }*/
    }
}
