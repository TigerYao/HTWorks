package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;

/**
 * @author zhaodongdong
 */
public class RoundProgressBar extends ProgressBar {
    private int mRoundColor = 0xFFFC00D1;
    private int mRoundProgressColor = 0xFFD3D6DA;
    private int mRoundWidth = DisplayUtil.dp2px( 2);
    private int mRoundProgressWidth = (int) (mRoundWidth * 2.5f);
    private int mTextColor = mRoundProgressColor;
    private int mTextSize = DisplayUtil.sp2px(10);
    private int mRadius = DisplayUtil.dp2px( 30);
    private String mText;
    private boolean showProgress = false;

    private Paint mPaint;
    private int mMaxPaintWidth;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyleAttrs(attrs);
        mMaxPaintWidth = Math.max(mRoundWidth, mRoundProgressWidth);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(mTextSize);
        if (TextUtils.isEmpty(mText)) {
            showProgress = true;
        }
    }

    private void obtainStyleAttrs(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        mRoundColor = array.getColor(R.styleable.RoundProgressBar_roundColor, mRoundColor);
        mRoundProgressColor = array.getColor(R.styleable.RoundProgressBar_roundProgressColor, mRoundProgressColor);
        mRoundWidth = (int) array.getDimension(R.styleable.RoundProgressBar_roundWidth, mRoundWidth);
        mRoundProgressWidth = (int) array.getDimension(R.styleable.RoundProgressBar_roundProgressWidth, mRoundProgressWidth);
        mTextColor = array.getColor(R.styleable.RoundProgressBar_textColor, mTextColor);
        mTextSize = (int) array.getDimension(R.styleable.RoundProgressBar_textSize, mTextSize);
        mRadius = (int) array.getDimension(R.styleable.RoundProgressBar_radius, mRadius);
        mText = array.getString(R.styleable.RoundProgressBar_text);
        array.recycle();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expect = mRadius * 2 + getPaddingLeft() + getPaddingRight() + mMaxPaintWidth;//默认四方向的padding相同
        int width = resolveSize(expect, widthMeasureSpec);
        int height = resolveSize(expect, heightMeasureSpec);
        int realWith = Math.min(width, height);
        mRadius = (realWith - getPaddingLeft() - getPaddingRight() - mMaxPaintWidth) / 2;
        setMeasuredDimension(realWith, realWith);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (showProgress) {
            mText = getProgress() + "%";
        }
        float textWidth = mPaint.measureText(mText);
        float textHeight = mPaint.descent() + mPaint.ascent();//居中
        canvas.save();
        canvas.translate(getPaddingLeft() + mMaxPaintWidth / 2, getPaddingTop() + mMaxPaintWidth / 2);
        mPaint.setStyle(Paint.Style.STROKE);
        //draw round bar
        mPaint.setColor(mRoundColor);
        mPaint.setStrokeWidth(mRoundWidth);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        //draw roundProgress bar
        mPaint.setColor(mRoundProgressColor);
        mPaint.setStrokeWidth(mRoundProgressWidth);
        float sweepAngle = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), -90, sweepAngle, false, mPaint);
        //draw text
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mTextColor);
        canvas.drawText(mText, mRadius - textWidth / 2, mRadius - textHeight / 2, mPaint);
        canvas.restore();

    }
}
