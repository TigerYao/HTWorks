package com.huatu.handheld_huatu.business.ztk_vod.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by Yuren on 2016/11/22.
 */
public class DashViewDrawable extends Drawable {
    private int mWidth, mHeight;
    private float dashWidth;
    private Paint mPaint;

    public DashViewDrawable(int mWidth, int mHeight, float dashWidth) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
        this.dashWidth = dashWidth;
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mHeight);
        PathEffect effects = new DashPathEffect(new float[]{dashWidth - 5, 5}, 0);
        Path path = new Path();
        path.moveTo(0, mHeight / 2);
        path.lineTo(mWidth, mHeight / 2);
        mPaint.setPathEffect(effects);
        canvas.drawPath(path, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
