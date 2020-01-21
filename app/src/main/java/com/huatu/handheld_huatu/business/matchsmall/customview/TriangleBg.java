package com.huatu.handheld_huatu.business.matchsmall.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.utils.SpUtils;

public class TriangleBg extends View {

    private Paint mPaint;
    private Path path;

    public TriangleBg(Context context) {
        this(context, null);
    }

    public TriangleBg(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleBg(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
        if (SpUtils.getDayNightMode() == 0) {
            mPaint.setColor(Color.parseColor("#F8F8F8"));
        } else {
            mPaint.setColor(Color.parseColor("#1C1C1C"));
        }
        path.reset();
        path.moveTo(0, 0);
        path.lineTo(mWidth, mHeight);
        path.lineTo(0, mHeight);
        path.close();
        canvas.drawPath(path, mPaint);
    }
}
