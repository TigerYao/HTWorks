package com.huatu.handheld_huatu.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by saiyuan on 2017/6/23.
 */

public class RadarMView extends View {
    private int         axisWidth;
    private int         chartColor;
    private int         chartWidth;

    private final LinkedHashMap<String, Integer> axis = new LinkedHashMap<>();
    private final Rect                         rect = new Rect();
    private final Path                         path = new Path();
    private TextPaint textPaint;
    private Paint                        paint;
    private       int                          centerX;
    private       int                          centerY;
    private       Ring[]                       rings;
    private       float[]                      vertices;
    private       float[]                      textVertices;
    private       float                        ratio;
    private       float                        axisMaxInternal = 230; //轴长度
    private       float                        axisTickInternal;
    private float intervalMore = 20;  //轴超出部分长度
    private int intervalNum = 5;
    private int[] paintColor;
    private float textSize;
    private int strokeWidth = 4;//线条宽度

    public RadarMView(Context context) {
        this(context, null);
        init();
    }

    public RadarMView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public RadarMView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paintColor = new int[]{getResources().getColor(R.color.red002),
                getResources().getColor(R.color.orange002),
                getResources().getColor(R.color.green003),
                getResources().getColor(R.color.purple),
                getResources().getColor(R.color.blue002),
                getResources().getColor(R.color.black),
                getResources().getColor(R.color.green002)
                };
        textSize = DisplayUtil.sp2px(12);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.density = DisplayUtil.getDensity();
        paint = createPaint(Color.BLACK);
        axisMaxInternal = DisplayUtil.getScreenWidth() * 0.3194f;
        intervalMore = DisplayUtil.dp2px(1);
        ratio = (axisMaxInternal - intervalMore) / 150.0f;
        calcAxisTickInternal();
    }

    private Paint createPaint(int color) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.white));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        return paint;
    }

    public final void setAxis(Map<String, Integer> axis) {
        this.axis.clear();
        this.axis.putAll(axis);
        onAxisChanged();
    }

    public final void remove(String axisName) {
        axis.remove(axisName);
        onAxisChanged();
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        calculateCenter();
        calcAxisTickInternal();
        buildRings();
    }

    @Override protected void onDraw(Canvas canvas) {
        final int count = axis.size();
        if(count < 3) {
            return;
        }
        drawPolygons(canvas, count);
        drawValues(canvas, count);
        drawAxis(canvas);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int size = MeasureSpec.makeMeasureSpec(Math.min(width, height), MeasureSpec.EXACTLY);
        super.onMeasure(size, size);
    }

    private void buildRings() {
        calcAxisTickInternal();
        rings = new Ring[intervalNum];
        for (int i = 0; i < intervalNum; i++) {
            rings[i] = new Ring(axisTickInternal * (i + 1), axisTickInternal);
        }
        buildVertices();
    }

    private float[] createPoint(float radius, double alpha, float x0, float y0) {
        final float[] point = new float[2];
        point[0] = (float) (radius * Math.cos(alpha) + x0);
        point[1] = (float) (radius * Math.sin(alpha) + y0);
        return point;
    }

    private float[] createPoints(int count, float radius, float x0, float y0) {
        final int length = count + count;
        final float[] points = new float[length];
        final double angle = 2 * Math.PI / count;
        int j = 0;
        for (int i = 0; i < length; i += 2) {
            final double alpha = angle * j++ - Math.PI / 2;
            final float[] point = createPoint(radius, alpha, x0, y0);
            points[i] = point[0];
            points[i + 1] = point[1];
        }
        return points;
    }

    private void buildVertices() {
        final int count = axis.size();
        for (Ring ring : rings) {
            ring.vertices = createPoints(count, ring.fixedRadius, centerX, centerY);
        }
        vertices = createPoints(count, axisMaxInternal, centerX, centerY);
        textVertices = createPoints(count, axisMaxInternal + 15, centerX, centerY);
    }

    private void calcAxisTickInternal() {
        axisTickInternal = (axisMaxInternal - intervalMore) / intervalNum;//每段间隔长度
    }

    private void calculateCenter() {
        centerX = (getMeasuredWidth() >> 1) + getPaddingLeft() - getPaddingRight();
        centerY = (getMeasuredHeight() >> 1) + getPaddingTop() - getPaddingBottom();
    }

    private void mutatePaint(Paint paint, float strokeWidth, Paint.Style style) {
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
    }

    private void drawAxis(Canvas canvas) {
        final Iterator<String> axisNames = axis.keySet().iterator();
        final Iterator<Integer> axisValues = axis.values().iterator();
        mutatePaint(paint, axisWidth, Paint.Style.STROKE);
        final int length = vertices.length;
        for (int i = 0; i < length; i += 2) {
            path.reset();
            path.moveTo(centerX, centerY);
            float pointX = vertices[i];
            float pointY = vertices[i + 1];
            path.lineTo(pointX, pointY);
            path.close();
            paint.setColor(paintColor[i / 2]);
            canvas.drawPath(path, paint);

            pointX = textVertices[i];
            pointY = textVertices[i + 1];
            String axisName = axisNames.next();
            textPaint.getTextBounds(axisName, 0, axisName.length(), rect);
            int nameWidth = rect.width();
            float x = pointX > centerX ? pointX : pointX - rect.width();
            float y = pointY > centerY ? pointY : pointY + rect.height() + 10 ;
            if(i == 0 || (axis.size() % 2 == 0) && (i == axis.size())) {
                x = pointX - rect.width() / 2;
                if(i == 0) {
                    y = pointY;
                } else {
                    y = pointY + rect.height();
                }
            }
            textPaint.setColor(paintColor[i / 2]);
            canvas.drawText(axisName, x, y, textPaint);

            axisName = String.valueOf(axisValues.next());
            textPaint.getTextBounds(axisName, 0, axisName.length(), rect);
            int dx = (nameWidth - rect.width()) / 2;
            x = pointX > centerX ? (pointX + dx): (pointX - rect.width() - dx);
            y = pointY > centerY ? (y + rect.height() + 10) : (y - rect.height() - 10);
            if(i == 0 || (axis.size() % 2 == 0) && (i == axis.size())) {
                x = pointX - rect.width() / 2;
            }
            textPaint.setColor(paintColor[i / 2]);
            canvas.drawText(axisName, x, y, textPaint);
        }
    }

    private void drawPolygons(Canvas canvas, int count) {
        paint.setColor(getResources().getColor(R.color.gray011));
        paint.setStyle(Paint.Style.STROKE);
        for (final Ring ring : rings) {
            final float[] points = ring.vertices;
            final float startX = points[0];
            final float startY = points[1];

            path.reset();
            path.moveTo(startX, startY);
            path.setLastPoint(startX, startY);
            for (int j = 2; j < count + count; j += 2) {
                path.lineTo(points[j], points[j + 1]);
            }
            path.close();

            mutatePaint(paint, strokeWidth, Paint.Style.STROKE);
            canvas.drawPath(path, paint);
        }
    }

    private void drawValues(Canvas canvas, int count) {
        Integer[] values = new Integer[count];
        values = axis.values().toArray(values);
        final float[] first = createPoint(values[0] * ratio, - Math.PI / 2, centerX, centerY);
        final float firstX = first[0];
        final float firstY = first[1];
        path.reset();
        path.setLastPoint(firstX, firstY);

        path.moveTo(firstX, firstY);
        for (int i = 1; i < count; i++) {
            final float[] point = createPoint(values[i] * ratio, (2 * Math.PI / count) * i - Math.PI / 2, centerX, centerY);
            path.lineTo(point[0], point[1]);
        }
        path.close();
        mutatePaint(paint, chartWidth, Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.red003));
        canvas.drawPath(path, paint);
    }

    private void onAxisChanged() {
        buildRings();
        buildVertices();
        invalidate();
    }

    private static class Ring {
        final float width;
        final float radius;
        final float fixedRadius;
        float[] vertices;

        Ring(float radius, float width) {
            this.radius = radius;
            this.width = width;
            fixedRadius = radius - width / 2;
        }

        @Override public String toString() {
            return "Ring{" +
                    "radius=" + radius +
                    ", width=" + width +
                    ", fixedRadius=" + fixedRadius +
                    '}';
        }
    }
}