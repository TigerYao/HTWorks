package com.huatu.handheld_huatu.ui;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;

public class ShadowDrawable extends Drawable {

    private Paint mShadowPaint;
    private Paint mBgPaint;
    private int mShadowRadius;
    private int mShape;
    private int mShapeType;
    private int mShapeRadius;
    private int mOffsetX;
    private int mOffsetY;
    private int mBgColor[];
    private RectF mRect;

    public final static int SHAPE_NONE = 0;
    public final static int SHAPE_ROUND = 1;
    public final static int SHAPE_CIRCLE = 2;


    public final static int SHAPE_ROUND_TOP = 3;
    public final static int SHAPE_ROUND_BOTTOM = 4;
    public final static int SHAPE_ROUND_LEFT = 5;
    public final static int SHAPE_ROUND_RIGHT = 6;
    public final static int SHAPE_ROUND_TOP_RIGHT = 7;
    public final static int SHAPE_ROUND_TOP_LEFT = 8;
    public final static int SHAPE_TOP = 4;

    private ShadowDrawable(int shape, int shapeType, int[] bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        this.mShape = shape;
        this.mShapeType = shapeType;
        this.mBgColor = bgColor;
        this.mShapeRadius = shapeRadius;
        this.mShadowRadius = shadowRadius;
        this.mOffsetX = offsetX;
        this.mOffsetY = offsetY;

        mShadowPaint = new Paint();
        mShadowPaint.setColor(Color.TRANSPARENT);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setShadowLayer(shadowRadius, offsetX, offsetY, shadowColor);
        mShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        int roundLeft = left + mShadowRadius - mOffsetX;
        int roundRight = right - mShadowRadius - mOffsetX;
        int roundTop = top + mShadowRadius - mOffsetY;
        int roundBottom = bottom - mShadowRadius - mOffsetY;
        switch (mShapeType) {
            case SHAPE_ROUND_TOP:
                roundLeft = left - mOffsetX;
                roundRight = right - mOffsetX;
                roundBottom = bottom + mShadowRadius;
                break;
            case SHAPE_ROUND_TOP_RIGHT:
                roundLeft = left - mShadowRadius;
                roundRight = right - mOffsetX;
                roundBottom = bottom + mShadowRadius;
                break;
            case SHAPE_ROUND_TOP_LEFT:
                roundLeft = left - mOffsetX;
                roundRight = right + mShadowRadius;
                roundBottom = bottom + mShadowRadius;
                break;
            case SHAPE_TOP:
                roundLeft = -mOffsetX- mShadowRadius;
                roundRight = right + mShadowRadius;
                roundBottom = bottom + mShadowRadius;
                break;
        }
        mRect = new RectF(roundLeft, roundTop, roundRight, roundBottom);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mBgColor != null) {
            if (mBgColor.length == 1) {
                mBgPaint.setColor(mBgColor[0]);
            } else {
                mBgPaint.setShader(new LinearGradient(mRect.left, mRect.height() / 2, mRect.right,
                        mRect.height() / 2, mBgColor, null, Shader.TileMode.CLAMP));
            }
        }

        if (mShape == SHAPE_ROUND) {
            canvas.drawRoundRect(mRect, mShapeRadius, mShapeRadius, mShadowPaint);
            canvas.drawRoundRect(mRect, mShapeRadius, mShapeRadius, mBgPaint);
        } else if (mShape == SHAPE_CIRCLE) {
            canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.width(), mRect.height()) / 2, mShadowPaint);
            canvas.drawCircle(mRect.centerX(), mRect.centerY(), Math.min(mRect.width(), mRect.height()) / 2, mBgPaint);
        } else {
            canvas.drawRect(mRect, mShadowPaint);
            canvas.drawRect(mRect, mBgPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mShadowPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mShadowPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public static void setShadowDrawable(View view, Drawable drawable) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY, int shapeType) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setShapeType(shapeType)
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int shape, int shapeType, int bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setShape(shape)
                .setShapeType(shapeType)
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static void setShadowDrawable(View view, int[] bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetX, int offsetY) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetX(offsetX)
                .setOffsetY(offsetY)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static class Builder {
        private int mShape;
        private int mShapeType;
        private int mShapeRadius;
        private int mShadowColor;
        private int mShadowRadius;
        private int mOffsetX = 0;
        private int mOffsetY = 0;
        private int[] mBgColor;

        public Builder() {
            mShape = ShadowDrawable.SHAPE_ROUND;
            mShapeType = ShadowDrawable.SHAPE_ROUND_TOP;
            mShapeRadius = 12;
            mShadowColor = Color.parseColor("#4d000000");
            mShadowRadius = 18;
            mOffsetX = 0;
            mOffsetY = 0;
            mBgColor = new int[1];
            mBgColor[0] = Color.TRANSPARENT;
        }

        public Builder setShape(int mShape) {
            this.mShape = mShape;
            return this;
        }

        public Builder setShapeType(int mShapeType) {
            this.mShapeType = mShapeType;
            return this;
        }

        public Builder setShapeRadius(int ShapeRadius) {
            this.mShapeRadius = ShapeRadius;
            return this;
        }

        public Builder setShadowColor(int shadowColor) {
            this.mShadowColor = shadowColor;
            return this;
        }

        public Builder setShadowRadius(int shadowRadius) {
            this.mShadowRadius = shadowRadius;
            return this;
        }

        public Builder setOffsetX(int OffsetX) {
            this.mOffsetX = OffsetX;
            return this;
        }

        public Builder setOffsetY(int OffsetY) {
            this.mOffsetY = OffsetY;
            return this;
        }

        public Builder setBgColor(int BgColor) {
            this.mBgColor[0] = BgColor;
            return this;
        }

        public Builder setBgColor(int[] BgColor) {
            this.mBgColor = BgColor;
            return this;
        }

        public ShadowDrawable builder() {
            return new ShadowDrawable(mShape, mShapeType, mBgColor, mShapeRadius, mShadowColor, mShadowRadius, mOffsetX, mOffsetY);
        }
    }
}
