package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.R;

public class SimpleLabelsLayout extends ViewGroup {
    protected int mWordMargin;
    protected int mLineMargin;
    protected int position = -1;
    protected boolean mIsSingleLine = false;
    private int mContentHeight;
    private OnLayoutSizeChange mOnLayoutSizeChange;

    public SimpleLabelsLayout(Context context) {
        super(context);
    }

    public SimpleLabelsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleLabelsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleLabelsLayout);
            mLineMargin = typedArray.getDimensionPixelOffset(R.styleable.SimpleLabelsLayout_lineMargin, 0);
            mWordMargin = typedArray.getDimensionPixelOffset(R.styleable.SimpleLabelsLayout_wordMargin, 0);
            mIsSingleLine = typedArray.getBoolean(R.styleable.SimpleLabelsLayout_singleLine, false);
            typedArray.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();

        int contentHeight = 0; //记录内容的高度
        int lineWidth = 0; //记录行的宽度
        int maxLineWidth = 0; //记录最宽的行宽
        int maxItemHeight = 0; //记录一行中item高度最大的高度
        boolean begin = true; //是否是行的开头

        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);

            if (!begin) {
                lineWidth += mWordMargin;
            } else {
                begin = false;
            }

            if (maxWidth <= lineWidth + view.getMeasuredWidth() || i == position || mIsSingleLine) {
                contentHeight += mLineMargin;
                contentHeight += maxItemHeight;
                maxItemHeight = 0;
                maxLineWidth = Math.max(maxLineWidth, lineWidth);
                lineWidth = 0;
                begin = true;
            }
            maxItemHeight = Math.max(maxItemHeight, view.getMeasuredHeight());

            lineWidth += view.getMeasuredWidth();
        }

        mContentHeight = contentHeight += maxItemHeight;
        maxLineWidth = Math.max(maxLineWidth, lineWidth);

        setMeasuredDimension(measureWidth(widthMeasureSpec, maxLineWidth),
                measureHeight(heightMeasureSpec, contentHeight));
        if (mOnLayoutSizeChange != null)
            mOnLayoutSizeChange.onSizeChange(maxLineWidth, mContentHeight);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int x = getPaddingLeft();
        int y = getPaddingTop();

        int contentWidth = right - left;
        int maxItemHeight = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);

            if (contentWidth < x + view.getMeasuredWidth() + getPaddingRight() || i == position || mIsSingleLine) {
                x = getPaddingLeft();
                y += mLineMargin;
                y += maxItemHeight;
                maxItemHeight = 0;
            }
            view.layout(x, y, x + view.getMeasuredWidth(), y + view.getMeasuredHeight());
            x += view.getMeasuredWidth();
            x += mWordMargin;
            maxItemHeight = Math.max(maxItemHeight, view.getMeasuredHeight());
        }
    }

    public void setOnLayoutSizeChange(OnLayoutSizeChange onLayoutSizeChange) {
        this.mOnLayoutSizeChange = onLayoutSizeChange;
    }

    public void setIsSingleLine(boolean singleLine) {
        this.mIsSingleLine = singleLine;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int measureWidth(int measureSpec, int contentWidth) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = contentWidth + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        result = Math.max(result, getSuggestedMinimumWidth());
        return result;
    }

    private int measureHeight(int measureSpec, int contentHeight) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = contentHeight + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        result = Math.max(result, getSuggestedMinimumHeight());
        return result;
    }

    public interface  OnLayoutSizeChange{
        void onSizeChange(int width, int height);
    }
}
