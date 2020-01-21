package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.view.looper.holder.ShapeHolder;
import com.huatu.handheld_huatu.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaodongdong
 */
public class CircleIndicator extends View {
    private List<ShapeHolder> tabItems;
    private ShapeHolder movingItem;
    private ViewPager mViewPager;
    private int mCurItemPosition;
    private float mCurItemPositionOffset;
    //config list
    private float mIndicatorRadius;
    private float mIndicatorMargin;
    private int mIndicatorBackground;
    private int mIndicatorBackgroundSelected;
    private Gravity mIndicatorGravity;
    private Mode mIndicatorMode;
    private BgMode mBgMode;

    //current view pager position
    int mCurrentViewPositon;

    //default value
    private final int DEFAULT_INDICATOR_RADIUS = DisplayUtil.dp2px( 5);
    private final int DEFAULT_INDICATOR_MARGIN = DisplayUtil.dp2px( 20);
    private final int DEFAULT_INDICATOR_BACKGROUND = Color.BLUE;
    private final int DEFAULT_INDICATOR_BACKGROUND_SELECTED = Color.RED;
    private final int DEFAULT_INDICATOR_GRAVITY = Gravity.CENTER.ordinal();
    private final int DEFAULT_INDICATOR_MODE = Mode.SOLO.ordinal();
    private final int DEFAULT_BG_MODE = BgMode.NORMAL.ordinal();

    private enum Gravity {
        LEFT,
        CENTER,
        RIGHT
    }

    private enum Mode {
        INSIDE,
        OUTSIDE,
        SOLO
    }

    private enum BgMode {
        NORMAL,
        BG_MODE,
        SELECTED_MODE
    }

    public CircleIndicator(Context context) {
        this(context, null);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tabItems = new ArrayList<>();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicator);
        mIndicatorRadius = array.getDimension(R.styleable.CircleIndicator_ci_radius, DEFAULT_INDICATOR_RADIUS);
        mIndicatorMargin = array.getDimension(R.styleable.CircleIndicator_ci_margin, DEFAULT_INDICATOR_MARGIN);
        mIndicatorBackground = array.getColor(R.styleable.CircleIndicator_ci_background, DEFAULT_INDICATOR_BACKGROUND);
        mIndicatorBackgroundSelected = array.getColor(R.styleable.CircleIndicator_ci_background_selected, DEFAULT_INDICATOR_BACKGROUND_SELECTED);
        int gravity = array.getInt(R.styleable.CircleIndicator_ci_gravity, DEFAULT_INDICATOR_GRAVITY);
        mIndicatorGravity = Gravity.values()[gravity];
        int mode = array.getInt(R.styleable.CircleIndicator_ci_mode, DEFAULT_INDICATOR_MODE);
        mIndicatorMode = Mode.values()[mode];
        int bg_mode = array.getInt(R.styleable.CircleIndicator_bg_mode, DEFAULT_BG_MODE);
        mBgMode = BgMode.values()[bg_mode];
        array.recycle();
        if (mBgMode == BgMode.BG_MODE && mIndicatorMode == Mode.INSIDE) {
            mIndicatorMode = Mode.SOLO;
        }
    }

    /**
     * 设置viewpager
     * 初始化工作
     *
     * @param viewPager viewpager
     */
    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        createTabItems();
        createMovingItem();
        setUpListener();
    }

    private void setUpListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (mIndicatorMode != Mode.SOLO) {
                    trigger(position, positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (mIndicatorMode == Mode.SOLO) {
                    trigger(position, 0);
                }
                mCurrentViewPositon=position;
            }
        });
    }

    /**
     * trigger to redraw the indicator when the ViewPager's selected item changed!
     *
     * @param position       position
     * @param positionOffset positionOffset
     */
    private void trigger(int position, float positionOffset) {
        mCurItemPosition = position;
        mCurItemPositionOffset = positionOffset;
        requestLayout();
        invalidate();
    }

    private void createMovingItem() {
        OvalShape circle = new OvalShape();
        ShapeDrawable drawable = new ShapeDrawable(circle);
        movingItem = new ShapeHolder(drawable);
        Paint paint = drawable.getPaint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setColor(mIndicatorBackgroundSelected);
        if (mBgMode == BgMode.SELECTED_MODE) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
        }
        switch (mIndicatorMode) {
            case INSIDE:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                break;
            case OUTSIDE:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
                break;
            case SOLO:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                break;
        }
        movingItem.setPaint(paint);
    }

    private void createTabItems() {
        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
            OvalShape circle = new OvalShape();
            ShapeDrawable drawable = new ShapeDrawable(circle);
            ShapeHolder shapeHolder = new ShapeHolder(drawable);
            Paint paint = drawable.getPaint();
            paint.setColor(mIndicatorBackground);
            paint.setAntiAlias(true);
            paint.setDither(true);
            if (mBgMode == BgMode.BG_MODE) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
            }
            shapeHolder.setPaint(paint);
            tabItems.add(shapeHolder);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        layoutTabItems(width, height);
        layoutMovingItem(mCurItemPosition, mCurItemPositionOffset);
    }

    private void layoutMovingItem(int curItemPosition, float curItemPositionOffset) {
        if (movingItem == null) {
            throw new IllegalStateException("forget to create movingItem?");
        }
        if (tabItems.size() == 0) {
            return;
        }
        ShapeHolder item = tabItems.get(curItemPosition);
        movingItem.resizeShape(item.getWidth(), item.getHeight());
        float x = item.getX() + (mIndicatorMargin + mIndicatorRadius * 2) * curItemPositionOffset;
        movingItem.setX(x);
        movingItem.setY(item.getY());
    }

    private void layoutTabItems(int width, int height) {
        if (tabItems == null) {
            throw new IllegalStateException("forgot to create tabItems");
        }
        float yCoordinate = height * 0.5f;
        float startPosition = startDrawPosition(width);
        for (int i = 0; i < tabItems.size(); i++) {
            ShapeHolder item = tabItems.get(i);
            item.resizeShape(2 * mIndicatorRadius, 2 * mIndicatorRadius);
            item.setY(yCoordinate - mIndicatorRadius);
            float x = startPosition + (mIndicatorMargin + mIndicatorRadius * 2) * i;
            item.setX(x);
        }
    }

    private float startDrawPosition(int width) {
        if (mIndicatorGravity == Gravity.LEFT) {
            return 0;
        }
        float tabItemLength = tabItems.size() * (2 * mIndicatorRadius + mIndicatorMargin) - mIndicatorMargin;
        if (width < tabItemLength) {
            return 0;
        }
        if (mIndicatorGravity == Gravity.CENTER) {
            return (width - tabItemLength) / 2;
        }
        return width - tabItemLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        for (ShapeHolder item : tabItems) {
            drawItem(canvas, item);
        }
        if (movingItem != null) {
            drawItem(canvas, movingItem);
        }
        canvas.restoreToCount(sc);
    }

    private void drawItem(Canvas canvas, ShapeHolder item) {
        canvas.save();
        canvas.translate(item.getX(), item.getY());
        item.getShape().draw(canvas);
        canvas.restore();
    }

    public void setIndicatorRadius(float indicatorRadius) {
        mIndicatorRadius = indicatorRadius;
    }

    public void setIndicatorMargin(float indicatorMargin) {
        mIndicatorMargin = indicatorMargin;
    }

    public void setIndicatorBackground(int indicatorBackground) {
        mIndicatorBackground = indicatorBackground;
    }

    public void setIndicatorBackgroundSelected(int indicatorBackgroundSelected) {
        mIndicatorBackgroundSelected = indicatorBackgroundSelected;
    }

    public void setIndicatorGravity(Gravity indicatorGravity) {
        mIndicatorGravity = indicatorGravity;
    }

    public void setIndicatorMode(Mode indicatorMode) {
        mIndicatorMode = indicatorMode;
    }

    public int getCurrentViewPositon(){
        return mCurrentViewPositon;
    }
}
