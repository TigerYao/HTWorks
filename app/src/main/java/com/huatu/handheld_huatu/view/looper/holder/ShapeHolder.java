package com.huatu.handheld_huatu.view.looper.holder;

import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

/**
 * @author zhaodongdong
 */
public class ShapeHolder {
    private float x = 0, y = 0;
    private ShapeDrawable shape;
    private int color;
    private float alpha = 1f;
    private Paint paint;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public ShapeDrawable getShape() {
        return shape;
    }

    public void setShape(ShapeDrawable shape) {
        this.shape = shape;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        shape.getPaint().setColor(color);
        this.color = color;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        shape.setAlpha((int) (alpha * 255f + 0.5f));//0~255 整数
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public float getWidth() {
        return shape.getShape().getWidth();
    }

    public void setWidth(float width) {
        Shape s = this.shape.getShape();
        s.resize(width, s.getHeight());
    }

    public float getHeight() {
        return shape.getShape().getHeight();
    }

    public void setHeight(float height) {
        Shape s = this.shape.getShape();
        s.resize(s.getWidth(), height);
    }

    public void resizeShape(float width, float height) {
        shape.getShape().resize(width, height);
    }

    public ShapeHolder(ShapeDrawable shape) {
        this.shape = shape;
    }
}
