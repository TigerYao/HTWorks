package com.huatu.handheld_huatu.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.view.CustomHeadView;

/**
 * Created by saiyuan on 2016/10/27.
 */
public class WaveView extends CustomHeadView {
    private Paint maskPaint;
    private int mProgress;
    private int mCorrectProgress;
    private int maxProgress = 10;
    private TextPaint mTextPaint;
    private float textHeight;
    private Paint fillPaint;
    private Path clipPath;

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        maskPaint = new Paint();
        maskPaint.setStrokeWidth(1);
        maskPaint.setColor(Color.parseColor("#41ffffff"));
        maskPaint.setStyle(Paint.Style.FILL);
        mTextPaint = new TextPaint();
        float scale = mContext.getResources().getDisplayMetrics().density;
        mTextPaint.setTextSize(DisplayUtil.dp2px(22));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setColor(Color.parseColor("#5151d4"));
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        textHeight = Math.abs(fontMetrics.ascent);//number only use ascent
        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#99438c44"));
        clipPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mRadius > 0) {
            if(mProgress >= maxProgress) {
                canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius - 1, fillPaint);
            } else if(mProgress > 0) {
                float curPercent = mProgress /((float) maxProgress);
                RectF rectF = new RectF((float) mWidth / 2 - mRadius, (float) mHeight / 2 - mRadius, (float) mWidth / 2 + mRadius, (float) mHeight / 2 + mRadius);
                RectF rectF1 = new RectF(rectF);
                rectF1.top = rectF.bottom - (mRadius * 2 * curPercent);
                canvas.save();
                clipPath.reset();
                clipPath.addCircle(centerX, centerY, mRadius, Path.Direction.CCW);
                canvas.clipPath(clipPath);
                canvas.drawRect(rectF1, fillPaint);
                canvas.restore();
            } else {

            }
            canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius - 1, maskPaint);

            String str = String.valueOf(mCorrectProgress);
            float txtLength = mTextPaint.measureText(str);
            canvas.drawText(str, mWidth / 2 - txtLength / 2, (mHeight + textHeight)/ 2, mTextPaint);
        }
    }

    public void setMaxProgress(int p) {
        maxProgress = p;
    }

    public void setProgress(int percent){
        if(percent > maxProgress) {
            return;
        }
        mProgress = percent;
        this.invalidate();
    }

    public void setCorrectProgress(int percent){
        if(percent > maxProgress) {
            return;
        }
        mCorrectProgress = percent;
        this.invalidate();
    }
}
