package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.utils.DensityUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by cjx on 2018\7\21 0021.
 */

public class PieProgressView extends View{
    Paint mPaint;
    Rect  mBound;
    float   mProgressValue=0;
    int   mDiameter=20;

    @IntDef({NORMAL, ONLIVE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PieProgressStatus{};

    public static final int NORMAL=0;
    public static final int ONLIVE=1;

    private final int bgColor=Color.parseColor("#E1E1E1");
    private final int progressColor=Color.parseColor("#1CE28D");
    private final int onliveColor=Color.parseColor("#FF6D73");

    private int mCurrentStatus= NORMAL;
    private float outStrokeWidth=0.0f;
    public PieProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PieProgressView(Context context, AttributeSet attrs, int defStyle)  {
        super(context, attrs, defStyle);
        /**
         * 获得我们所定义的自定义样式属性
         */
      /*  TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PieProgressView, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.PieProgressView_backColor:
                    // 默认背景颜色设置为黑色
                    mBackColor = a.getColor(attr, Color.BLACK);
                    Log.i("log","mBackColor="+Integer.toHexString(mBackColor));
                    break;
                case R.styleable.PieProgressView_frontColor:
                    // 默认前景颜色设置为蓝色
                    mFrontColor = a.getColor(attr, Color.BLUE);
                    Log.i("log","mFrontColor="+Integer.toHexString(mFrontColor));
                    break;
                case R.styleable.PieProgressView_diameter:
                    // 默认设置为40dp
                    mDiameter = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 40, getResources().getDisplayMetrics()));
                    break;
            }

        }
        a.recycle();*/

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mBound = new Rect();
        mProgressValue=0;
        outStrokeWidth= DensityUtils.dp2floatpx(getContext(), 2);

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
       /** * 设置宽度   */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode)  {
            case MeasureSpec.EXACTLY:// 明确指定了
                width = getPaddingLeft() + getPaddingRight() + specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                width = getPaddingLeft() + getPaddingRight() + mBound.width();
                break;
        }

        /**  * 设置高度  */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode)
        {
            case MeasureSpec.EXACTLY:// 明确指定了
                height = getPaddingTop() + getPaddingBottom() + specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                height = getPaddingTop() + getPaddingBottom() + mBound.height();
                break;
        }

        //设置直径的最小值
/*        if(mDiameter<=40){
            mDiameter=40;
        }
        height=mDiameter;
        width=mDiameter;*/

        LogUtils.i("log","w="+width+" h="+height);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas)  {
        //mPaint.setColor(Color.YELLOW);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        RectF rect = new RectF(0, 0, width, height);//200, 200);
//      Log.i("log","w="+width+" h="+height);

        if(mCurrentStatus==NORMAL){
            mPaint.setColor(bgColor);
            canvas.drawArc(rect, 0, 360, true, mPaint);

            mPaint.setColor(progressColor);
            canvas.drawArc(rect, 270, mProgressValue*360/100, true, mPaint);
        }else {
            mPaint.setColor(onliveColor);
            canvas.drawArc(rect, 270, mProgressValue*360/100, true, mPaint);

            mPaint.setStyle(Paint.Style.STROKE);
            // 设置画出的线的 粗细程度
            mPaint.setStrokeWidth(outStrokeWidth/2);
            mPaint.setColor(Color.parseColor("#FBC5C7"));
            canvas.drawCircle(width/2, height/2, width/2-outStrokeWidth, mPaint);
            mPaint.setStyle(Paint.Style.FILL);
         }
    }

    public void setProgressValue(float progressValue){
        this.mProgressValue = progressValue;
        this.invalidate();
    }

    public PieProgressView setStatus(@PieProgressView.PieProgressStatus int status){
        mCurrentStatus=status;
        return this;
    }


}
