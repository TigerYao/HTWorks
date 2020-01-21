package com.huatu.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by cjx on 2018\11\5 0005.
 *  绘制贝塞尔来绘制波浪形
 */

public class ReverseWaveView extends View {

    //private int waveHeight=30;
    private int headHeight;
    private Path path;
    private Paint paint;
    private int mOffsetX = -1;

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
        path = new Path();
        paint = new Paint();
        //paint.setColor(0xff1F2426);
         paint.setColor(0xffFE3848);
       // paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);

       // waveHeight=(int) (15 * getDensity(context) + 0.5f);
        headHeight=0;
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
        paint.setColor(color);
    }

    public int getHeadHeight() {
        return headHeight;
    }

    public void setHeadHeight(int headHeight) {
        this.headHeight = headHeight;
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
        final int width = getWidth();
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
        canvas.drawPath(path, paint);
    }

    public void setWaveOffsetX(int offset) {
        mOffsetX = offset;
    }
}
