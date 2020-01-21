package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.utils.DisplayUtil;

import java.util.ArrayList;

public class ViewPieChart extends View {

    private Paint mPaint;

    private int mPadding = 100;
    private int mWidth = 100;

    private ArrayList<Integer> data = new ArrayList<>();
    private ArrayList<Float> dataDegree = new ArrayList<>();

    private int[] colors = {
            Color.parseColor("#FFC900"),
            Color.parseColor("#A6D601"),
            Color.parseColor("#00A5F5"),
            Color.parseColor("#A21CDB"),
            Color.parseColor("#DD1037"),
            Color.parseColor("#FF7C00")
    };

    public ViewPieChart(Context context) {
        this(context, null);
    }

    public ViewPieChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {

        mPadding = DisplayUtil.dp2px(20);
        mWidth = DisplayUtil.dp2px(30);

        // 初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#ff8080"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mWidth);
        mPaint.setAntiAlias(true);

        // TODO: 2018/11/20 测试数据
        data.add(3);
        data.add(3);
        data.add(3);
        data.add(3);
        data.add(3);
        data.add(3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth() - mPadding;
        int height = getMeasuredHeight() - mPadding;

        RectF rectF = new RectF(mPadding, mPadding, width, height);

        if (checkData(canvas, rectF)) return;

        handleDegree();

        drawPie(canvas, rectF);

        setRotation(-95);
    }

    /**
     * 检查数据，如果科目量是0，或者各科做题总量是0，就不计算绘制 & 绘制底色
     */
    private boolean checkData(Canvas canvas, RectF rectF) {
        if (data.size() == 0) {
            canvas.drawArc(rectF, 0, 360, false, mPaint);
            return true;
        }
        float all = 0f;
        for (int i = 0; i < data.size(); i++) {
            all += data.get(i);
        }
        if (all == 0) {
            canvas.drawArc(rectF, 0, 360, false, mPaint);
            return true;
        }
        return false;
    }

    /**
     * 画饼
     */
    private void drawPie(Canvas canvas, RectF rectF) {

        float startDegree = 0;
        for (int i = 0; i < dataDegree.size(); i++) {
            Float aFloat = dataDegree.get(i);
            mPaint.setColor(colors[i % colors.length]);
            canvas.drawArc(rectF, startDegree, aFloat + 5, false, mPaint);
            startDegree += aFloat;
        }
    }

    private void handleDegree() {
        float all = 0f;
        for (int i = 0; i < data.size(); i++) {
            all += data.get(i);
        }
        dataDegree.clear();
        for (int i = 0; i < data.size(); i++) {
            dataDegree.add((float) data.get(i) / all * 360);
        }
    }

    /**
     * 设置padding和线宽
     */
    public void setSize(int padding, int width) {
        if (this.mPadding != padding && padding > 0) {
            this.mPadding = padding;
        }
        if (this.mWidth != width && width > 0) {
            this.mWidth = width;
        }
        invalidate();
    }

    /**
     * 设置饼图的数据，只要是一串数字就可以，自己计算比例
     */
    public void setData(ArrayList<Integer> data) {
        this.data.clear();
        this.data.addAll(data);
        invalidate();
    }

    public int getColor(int i) {
        return colors[i % colors.length];
    }
}
