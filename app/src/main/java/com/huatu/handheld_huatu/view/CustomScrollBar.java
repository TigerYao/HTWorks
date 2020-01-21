package com.huatu.handheld_huatu.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

public class CustomScrollBar extends View {

    private Context mContext;

    private int mWidth;                         // 此View的宽度
    private int mHeight;                        // 此View的高度

    private RecyclerView recyclerView;

    private Paint mPaint;

    private int allWidth;                       // RecycleView总长度
    private int showWidth;                      // RecycleView的显示长度

    private int leftX;                          // RecycleView左边划过x的距离

    public CustomScrollBar(Context context) {
        this(context, null);
    }

    public CustomScrollBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#E1E1E1"));
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
//                leftX += dx;
            leftX = getScrollDistance();
            invalidate();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    /**
     * 关联RecycleView
     */
    public void setRecycleView(RecyclerView recycleView) {

        if (this.recyclerView != null) {
            this.recyclerView.removeOnScrollListener(scrollListener);
        }

        this.recyclerView = recycleView;

        recyclerView.addOnScrollListener(scrollListener);

        if (mContext != null && onGlobalLayoutListener != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ((Activity) mContext).getWindow()
                        .getDecorView()
                        .getViewTreeObserver()
                        .removeOnGlobalLayoutListener(onGlobalLayoutListener);
            } else {
                ((Activity) mContext).getWindow()
                        .getDecorView()
                        .getViewTreeObserver()
                        .removeGlobalOnLayoutListener(onGlobalLayoutListener);
            }
        }

        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager == null) {
                    return;
                }

                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter == null) {
                    return;
                }

                View childAt = layoutManager.getChildAt(0);
                if (childAt == null) {
                    return;
                }

                int measuredWidth = childAt.getMeasuredWidth();
                if (measuredWidth <= 0) {
                    return;
                }

                int childCount = adapter.getItemCount();
                if (childCount <= 0) {
                    return;
                }
                CustomScrollBar.this.allWidth = measuredWidth * childCount;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ((Activity) mContext).getWindow()
                            .getDecorView()
                            .getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    ((Activity) mContext).getWindow()
                            .getDecorView()
                            .getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }

                invalidate();
            }
        };

        // 当RecycleView挂载的时候，执行获取RecycleView的总宽度和显示宽度
        ((Activity) mContext).getWindow()
                .getDecorView()
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        showWidth = recyclerView.getMeasuredWidth();

        if (allWidth <= 0 || showWidth <= 0) {
            return;
        }
        float leftP = (float) leftX / (float) allWidth;                         // 左边百分比
        float left = mWidth * leftP;                                            // 左边x坐标

        float rightP = (float) (leftX + showWidth) / (float) allWidth;
        float right = mWidth * rightP;                                          // 右边x坐标

        float r = (float) mHeight / 2;                                          // 圆半径

        // 画左边的圆
        canvas.drawCircle(left + r, r, r, mPaint);

        // 画中间的矩形
        canvas.drawRect(left + r, 0, right - r, mHeight, mPaint);

        // 画右边的半圆
        canvas.drawCircle(right - r, r, r, mPaint);
    }

    public int getScrollDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibilityChildView = layoutManager.findViewByPosition(position);
        int itemWidth = firstVisibilityChildView.getWidth();
        return (position) * itemWidth - firstVisibilityChildView.getLeft();
    }
}
