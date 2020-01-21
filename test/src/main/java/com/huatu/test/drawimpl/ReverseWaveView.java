package com.huatu.test.drawimpl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by cjx on 2018\11\5 0005.
 *  绘制贝塞尔来绘制波浪形
 */

public class ReverseWaveView extends View {

    //private int waveHeight=30;

    private Path mPath;
    private Paint mPaint;
    private int mOffsetX = -1;
    protected int direction=1;

    protected int mArcHeight;// 圆弧高度
    public ReverseWaveView(Context context) {
        this(context, null, 0);
    }

    public ReverseWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReverseWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private static float density = 0;
    private void initView(Context context) {
        mPath = new Path();
        mPaint = new Paint();
        //paint.setColor(0xff1F2426);
        mPaint.setColor(0xffFE3848);
       // paint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);

       // waveHeight=(int) (15 * getDensity(context) + 0.5f);

        mArcHeight=  (int) (15 * getDensity(context) );
    }


    public static float getDensity(Context context) {
        if(density <= 0) {
            density = context.getResources().getDisplayMetrics().density;
        }
        return density;
    }



/*    *//*
    * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    *//*
    public static int dp2px(float dpValue) {
        return (int) (dpValue * getDensity() + 0.5f);
    }*/

  /*  @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }*/

    public void setWaveColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    public int getHeadHeight() {
        return mArcHeight;
    }

    public void setHeadHeight(int headHeight) {
        this.mArcHeight = headHeight;
    }

    /*public int getWaveHeight() {
        return waveHeight;
    }

    public void setWaveHeight(int waveHeight) {
        this.waveHeight = waveHeight;
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       /* final int width = getWidth();
        int waveHeight=getMeasuredHeight();
        //重置画笔
        path.reset();
        //绘制贝塞尔曲线
        path.lineTo(0, headHeight);
        path.quadTo(mOffsetX >= 0 ? (mOffsetX) : width / 2, headHeight + waveHeight, width, headHeight);
        //path.lineTo(width, 0);
        path.lineTo(width, headHeight + waveHeight);
        path.lineTo(0, headHeight + waveHeight);
        path.lineTo(0, headHeight );
        canvas.drawPath(path, paint);*/

        calPath();
        canvas.drawPath(mPath, mPaint);
    }

    public void setWaveOffsetX(int offset) {
        mOffsetX = offset;
    }

    public static final int DIRECTION_DOWN_OUT_SIDE = 0;
    public static final int DIRECTION_DOWN_IN_SIDE = 1;

    @IntDef({DIRECTION_DOWN_OUT_SIDE, DIRECTION_DOWN_IN_SIDE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }
   /* private void calPath() {
        int w = getWidth();
        int h = getHeight();
        path.reset();
        path.moveTo(0, 0);
        switch (direction) {
            case DIRECTION_DOWN_OUT_SIDE:
                path.lineTo(0, h - arcHeight);
                path.quadTo(w / 2.0f, h + arcHeight, w, h - arcHeight);
                path.lineTo(w, 0);
                break;
            case DIRECTION_DOWN_IN_SIDE:
                path.lineTo(0, h);
                path.quadTo(w / 2.0f, h - arcHeight * 2, w, h);
                path.lineTo(w, 0);
                break;
        }
        path.close();
    }*/

    private void calPath() {
        int w = getWidth();
        int h = getHeight();
        mPath.reset();
        mPath.moveTo(0, h - mArcHeight);
        switch (direction) {
            case DIRECTION_DOWN_IN_SIDE:
                //path.lineTo(0, h - arcHeight);
                mPath.quadTo(w / 2.0f, h + mArcHeight, w, h - mArcHeight);
                mPath.lineTo(w, h);
                mPath.lineTo(0, h);
                break;
            case DIRECTION_DOWN_OUT_SIDE:
                mPath.lineTo(0, h);
                mPath.quadTo(w / 2.0f, h - mArcHeight * 2, w, h);
                mPath.lineTo(w, 0);
                break;
        }
        mPath.close();
    }

}
