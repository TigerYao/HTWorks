package com.huatu.handheld_huatu.business.matchsmall.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;

public class ViewCircleBar extends View {

    private Context mContext;
    private Paint mPaint;

    private int colorBg = Color.parseColor("#45000000");    // 背景

    private float pWidth = DisplayUtil.dp2px(15);             // 线宽

    private float mWidth, mHeight;

    private float data = 0f;

    public ViewCircleBar(Context context) {
        this(context, null);
    }

    public ViewCircleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewCircleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(pWidth);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        RectF rectF = new RectF(pWidth, pWidth, mWidth - pWidth, mHeight - pWidth);

        mPaint.setColor(colorBg);
        canvas.drawArc(rectF, 0, 360, false, mPaint);

        mPaint.setColor(mContext.getResources().getColor(R.color.small_common_white_text));
        canvas.drawArc(rectF, -90, 360 * data, false, mPaint);
    }

    public void setColorBg(float pWidth, int colorBg) {
        this.pWidth = pWidth;
        mPaint.setStrokeWidth(pWidth);
        this.colorBg = colorBg;
        invalidate();
    }

    public void setData(float data) {
        if (data >= 0) {
            this.data = data;
            invalidate();
        }
    }
}
