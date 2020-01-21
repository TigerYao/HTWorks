package com.huatu.handheld_huatu.business.matchsmall.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;

public class ViewSmoothLine extends View {

    private Context mContext;

    private Paint mPaintLine;
    private Paint mPaintText;
    private Paint mPaintData;
    private Paint mPaintMyData;

    private float mPadding;                 // 周围写字空间
    private float mTextSize;                // 文字大小
    private float mWidth, mHeight;          // View的宽高

    private ArrayList<PointF> originalData; // 原始数据
    private ArrayList<PointF> data;         // 总体数据 x、得分 y、高度比例（此分数人数/做多人得分的人数）
    private int index = 6;                  // 我的数据
    private String beatStr, myScore;        // 击败比例，我的得分

    public ViewSmoothLine(Context context) {
        this(context, null);
    }

    public ViewSmoothLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewSmoothLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mPadding = DisplayUtil.dp2px(30);
        mTextSize = DisplayUtil.dp2px(12);

        data = new ArrayList<>();

        // 背景线
        mPaintLine = new Paint();
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(DisplayUtil.dp2px(1));

        // 文字画笔
        mPaintText = new Paint();
        mPaintText.setTextSize(mTextSize);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setStrokeWidth(DisplayUtil.dp2px(1));
        Typeface font = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD);
        mPaintText.setTypeface(font);

        // 总数据
        mPaintData = new Paint();
        mPaintData.setStyle(Paint.Style.FILL);
        mPaintData.setStrokeWidth(DisplayUtil.dp2px(1));
        mPaintData.setColor(Color.parseColor("#FFF6F3"));

        // 我的数据
        mPaintMyData = new Paint();
        mPaintMyData.setStyle(Paint.Style.FILL);
        mPaintMyData.setStrokeWidth(DisplayUtil.dp2px(1));
        mPaintMyData.setColor(Color.parseColor("#FFF6F3"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.makeMeasureSpec((int) (specSize * 0.6f), specMode);
        super.onMeasure(widthMeasureSpec, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();

        canvas.translate(mPadding, mHeight - mPadding);

        drawBg(canvas);

        if (originalData == null || originalData.size() <= 1) return;

        drawData(canvas);
    }

    private void drawBg(Canvas canvas) {

        mPaintLine.setColor(Color.parseColor("#4A4A4A"));
        canvas.drawLine(0, 0, 0, -(mHeight - mPadding * 2), mPaintLine);
        canvas.drawLine(0, 0, mWidth - mPadding * 2, 0, mPaintLine);

        mPaintText.setColor(Color.parseColor("#4A4A4A"));
        canvas.drawText("（人数）", 0, mPadding * 2 - (mPadding - mTextSize) / 2 - mHeight, mPaintText);
        canvas.drawText("微模考成绩", mWidth - mPadding * 2 - mPaintText.measureText("微模考成绩"), mPadding * 2 - (mPadding - mTextSize) / 2 - mHeight, mPaintText);
        canvas.drawText("0分", -mPaintText.measureText("0分") / 2, mPadding - (mPadding - mTextSize) / 2, mPaintText);
        canvas.drawText("100分", mWidth - mPadding * 2 - mPaintText.measureText("100分") / 2, mPadding - (mPadding - mTextSize) / 2, mPaintText);
    }

    private void drawData(Canvas canvas) {

        // 处理数据，计算贝塞尔曲线的控制点
        calculateControlPoint();

        // 绘制正态分布
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(data.get(0).x, data.get(0).y);
        for (int i = 0; i < data.size() - 1; i++) {
            PointF point_01 = mControlPointList.get(i * 2);
            PointF point_02 = mControlPointList.get(i * 2 + 1);
            PointF nextPoint = data.get(i + 1);
            path.cubicTo(point_01.x, point_01.y, point_02.x, point_02.y, nextPoint.x, nextPoint.y);
        }
        path.lineTo(data.get(data.size() - 1).x, 0);
        mPaintData.setColor(Color.parseColor("#40FFE9CF"));
        canvas.drawPath(path, mPaintData);

        if (index > data.size() - 1 || index == 0 || StringUtils.isEmpty(beatStr) || StringUtils.isEmpty(myScore))
            return;
        // 绘制我的数据渐变色
        path.reset();
        path.moveTo(0, 0);
        path.lineTo(data.get(0).x, data.get(0).y);
        for (int i = 0; i < index; i++) {
            PointF point_01 = mControlPointList.get(i * 2);
            PointF point_02 = mControlPointList.get(i * 2 + 1);
            PointF nextPoint = data.get(i + 1);
            path.cubicTo(point_01.x, point_01.y, point_02.x, point_02.y, nextPoint.x, nextPoint.y);

            if (i == index - 1) {
                path.lineTo(nextPoint.x, 0);
            }
        }

        // 渐变色
        LinearGradient backGradient = new LinearGradient(0, 0, data.get(index).x, 0,
                new int[]{Color.parseColor("#FF999D"), Color.parseColor("#FFE9CF")}, null, Shader.TileMode.CLAMP);
        mPaintMyData.setShader(backGradient);

        canvas.drawPath(path, mPaintMyData);

        // 绘制我的分数段
        PointF myPoint = data.get(index);
        mPaintText.setColor(Color.parseColor("#FF3F47"));
        canvas.drawCircle(myPoint.x, myPoint.y, DisplayUtil.dp2px(4), mPaintText);
        canvas.drawLine(myPoint.x, myPoint.y, myPoint.x, 0, mPaintText);
        canvas.drawText(beatStr, myPoint.x - mPaintText.measureText(beatStr) / 2, myPoint.y - mPadding / 2, mPaintText);
        canvas.drawText(myScore, myPoint.x - mPaintText.measureText(myScore) / 2, mPadding - (mPadding - mTextSize) / 2, mPaintText);
    }

    private float SMOOTHNESS = 0.3f;
    ArrayList<PointF> mControlPointList = new ArrayList<>();

    /**
     * 计算控制点
     */
    private void calculateControlPoint() {
        // 处理原始数据
        data.clear();
        for (PointF datum : originalData) {
            data.add(new PointF(datum.x, -datum.y));
        }
        for (PointF datum : data) {
            datum.x = datum.x * ((mWidth - mPadding * 2) / 100);
            datum.y = datum.y * (mHeight - mPadding * 2);
        }

        // 计算中间点
        mControlPointList.clear();
        for (int i = 0; i < data.size(); i++) {
            PointF point = data.get(i);
            if (i > 0 && i < data.size() - 1) {
                PointF prePoint = data.get(i - 1);
                PointF nextPoint = data.get(i + 1);
                float k = (nextPoint.y - prePoint.y) / (nextPoint.x - prePoint.x);
                float b = point.y - k * point.x;
                //添加前控制点
                float lastControlX = point.x - (point.x - prePoint.x) * SMOOTHNESS;
                float lastControlY = k * lastControlX + b;
                mControlPointList.add(new PointF(lastControlX, lastControlY));
                //添加后控制点
                float nextControlX = point.x + (nextPoint.x - point.x) * SMOOTHNESS;
                float nextControlY = k * nextControlX + b;
                mControlPointList.add(new PointF(nextControlX, nextControlY));
            } else if (i == 0) {
                PointF nextPoint = data.get(i + 1);
                float controlX = point.x + (nextPoint.x - point.x) * SMOOTHNESS;
                float controlY = point.y;
                mControlPointList.add(new PointF(controlX, controlY));
            } else if (i == data.size() - 1) {
                //添加前控制点
                PointF prePoint = data.get(i - 1);
                float controlX = point.x - (point.x - prePoint.x) * SMOOTHNESS;
                float controlY = point.y;
                mControlPointList.add(new PointF(controlX, controlY));
            }
        }
    }

    public void setData(ArrayList<PointF> data, int index, String beatStr, String myScore) {
        if (data == null || data.size() == 0) return;
        this.originalData = data;
        this.index = index;
        this.beatStr = beatStr;
        this.myScore = myScore;
        invalidate();
    }
}
