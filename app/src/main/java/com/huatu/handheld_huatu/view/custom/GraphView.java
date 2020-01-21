package com.huatu.handheld_huatu.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;

/*
 * Created by ljzyuhenda on 16/5/7.
 */
public class GraphView extends View {
    private int[] verticalCoordinate = {100, 80, 60, 40, 20, 0};
    private int horizontalSize = 6;
    private String[] horizontalCoordinate = {"00日"};
    private float textSizeVertical;
    private float spaceVertical;
    private float textSizeHorizontal;
    private float spaceHorizontal;
    private Paint paint;
    private Path path;
    private float[] mScores = {20f, 5f, 30f, 20f, 50f};
    private float[] mScoresAverage = {10f, 23f, 35f, 100f};
    private float startPointX;
    private float startPointY;
    private int strokeWidth;
    private int circleRedius;
    private String textInfo = "---- 用户平均分";
    private int percent = 20;

    public GraphView(Context context) {
        this(context, null);
    }

    public GraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GraphView);
        textSizeHorizontal = typedArray.getDimension(R.styleable.GraphView_textSizeHorizontal, 0);
        spaceHorizontal = typedArray.getDimension(R.styleable.GraphView_spaceHorizontal, 0);
        textSizeVertical = typedArray.getDimension(R.styleable.GraphView_textSizeVertical, 0);
        spaceVertical = typedArray.getDimension(R.styleable.GraphView_spaceVertical, 0);

        initDatas();
    }

    private void initDatas() {
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        paint = new Paint();
        paint.setAntiAlias(true);

        path = new Path();

        strokeWidth = DisplayUtil.dp2px(1f);
        circleRedius = DisplayUtil.dp2px(5.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //测量水平轴坐标字体大小
        paint.setTextSize(textSizeHorizontal);
        //调查一
        float horizonTextWidth1 = paint.measureText(horizontalCoordinate[0]);
        float horizonTextHeight1 = textSizeHorizontal;
        Paint.FontMetrics metrics1 = paint.getFontMetrics();

        int width = getWidth() - getPaddingLeft() * 2;
        float height = getHeight() - spaceVertical - horizonTextHeight1;

        //画垂直坐标轴各个坐标
        paint.setTextSize(textSizeVertical);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.black001));

        float textWidth = paint.measureText("分数");
        float textHeight = textSizeHorizontal;

        //垂直坐标轴每个坐标之间间距
        float verticalInterval = (height - textHeight / 2) / verticalCoordinate.length;

        //垂直坐标轴顶点x值
        float startPointXForVerticalCoordinate = textWidth + spaceVertical;
        //垂直坐标轴终Y值
        float endPointYForVerticalCoordinate = height - textHeight / 2;

        Paint.FontMetrics fm = paint.getFontMetrics();
        //绘制Y轴坐标点
        for (int index = 1; index < verticalCoordinate.length; index++) {
            float currentTextWidth = paint.measureText(String.valueOf(verticalCoordinate[5 - index]));
            float horizontalLineY = endPointYForVerticalCoordinate - index * verticalInterval;
            float startYforVerticalText = horizontalLineY - (fm.bottom + fm.top) / 2;

            float startX = textWidth - currentTextWidth;
            canvas.drawText(String.valueOf(verticalCoordinate[5 - index]), startX, startYforVerticalText, paint);
        }

        canvas.drawText("分数", 0, -fm.top, paint);

        //绘制Y轴箭头
        Bitmap drawable = BitmapFactory.decodeResource(getResources(), R.drawable.icon_arrow_up);
        canvas.drawBitmap(drawable, startPointXForVerticalCoordinate - drawable.getWidth() / 2, 0, paint);

        //绘制Y轴
        path.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.common_arena_gray_color));
        paint.setStrokeWidth(2);
        path.moveTo(startPointXForVerticalCoordinate, 0);
        path.lineTo(startPointXForVerticalCoordinate, endPointYForVerticalCoordinate);
        canvas.drawPath(path, paint);

        //绘制Y轴坐标点短线
        path.reset();
        paint.setColor(getResources().getColor(R.color.common_arena_gray_color));
        for (int index = 1; index < verticalCoordinate.length; index++) {
            float startX = textWidth + spaceVertical;
            float startY = endPointYForVerticalCoordinate - index * verticalInterval;

            float endX = textWidth + spaceVertical / 2;
            float endY = startY;

            path.moveTo(startX, startY);
            path.lineTo(endX, endY);
            canvas.drawPath(path, paint);
        }

        //水平坐标轴起点x值
        float startPointXForHorizontalCoordinate = startPointXForVerticalCoordinate;
        //水平坐标轴起点y值
        float startPointYForHorizontalCoordinate = endPointYForVerticalCoordinate;
        //水平坐标轴长度
        float lengthForHorizontalCoordiante = width - startPointXForVerticalCoordinate;

        //绘制X轴
        path.reset();
        paint.setPathEffect(null);
        path.moveTo(startPointXForHorizontalCoordinate, startPointYForHorizontalCoordinate - 0 * verticalInterval);
        path.lineTo(startPointXForHorizontalCoordinate + lengthForHorizontalCoordiante, startPointYForHorizontalCoordinate - 0 * verticalInterval);

        canvas.drawPath(path, paint);

        //水平坐标间距
        float intervalForHorizonTal = lengthForHorizontalCoordiante / horizontalSize;

        startPointX = startPointXForHorizontalCoordinate;
        startPointY = startPointYForHorizontalCoordinate;

        //绘制曲线
        path.reset();
        paint.setPathEffect(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.gray012));
        paint.setStrokeWidth(1);
        float endPointX;
        float endPointY;
        for (int index = 0; index < mScores.length - 1; index++) {
            startPointX = startPointXForHorizontalCoordinate + intervalForHorizonTal * (index + 1);
            startPointY = verticalInterval * (verticalCoordinate.length - mScores[index] / percent);

            endPointX = startPointX + intervalForHorizonTal;
            endPointY = verticalInterval * (verticalCoordinate.length - mScores[index + 1] / percent);

            path.moveTo(startPointX, startPointY);
            path.lineTo(endPointX, endPointY);
            canvas.drawPath(path, paint);
        }

        //绘制平均分
        path.reset();
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 15));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.gray012));
        paint.setStrokeWidth(1);
        for (int index = 0; index < mScoresAverage.length - 1; index++) {
            startPointX = startPointXForHorizontalCoordinate + intervalForHorizonTal * (index + 1);
            startPointY = verticalInterval * (verticalCoordinate.length - mScoresAverage[index] / percent);

            endPointX = startPointX + intervalForHorizonTal;
            endPointY = verticalInterval * (verticalCoordinate.length - mScoresAverage[index + 1] / percent);

            path.moveTo(startPointX, startPointY);
            path.lineTo(endPointX, endPointY);
            canvas.drawPath(path, paint);
        }

        //绘制X轴坐标字
        path.reset();
        paint.setTextSize(textSizeHorizontal);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.black001));

        //第一个text开始位置
        float startXForHorizonText = startPointXForHorizontalCoordinate + intervalForHorizonTal - horizonTextWidth1 / 2;
        float startYForHorizonText = startPointYForHorizontalCoordinate + spaceHorizontal - metrics1.top;

        for (int index = 0; index < horizontalCoordinate.length - 1; index++) {
            canvas.drawText(horizontalCoordinate[index + 1], startXForHorizonText + index * intervalForHorizonTal, startYForHorizonText, paint);
        }

        //绘制Y轴箭头
        Bitmap drawableRight = BitmapFactory.decodeResource(getResources(), R.drawable.icon_arrow_right);
        canvas.drawBitmap(drawableRight, startPointXForHorizontalCoordinate + lengthForHorizontalCoordiante - drawableRight.getWidth(), startPointYForHorizontalCoordinate - drawableRight.getHeight() / 2, paint);

        //绘制x轴短线
        path.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.gray006));
        paint.setStrokeWidth(2);
        for (int index = 1; index < horizontalCoordinate.length; index++) {
            float startX = startPointXForHorizontalCoordinate + index * intervalForHorizonTal;
            float startY = startPointYForHorizontalCoordinate - 0 * verticalInterval;

            float endX = startX;
            float endY = startY - spaceHorizontal / 2;

            path.moveTo(startX, startY);
            path.lineTo(endX, endY);
            canvas.drawPath(path, paint);
        }

        //绘制圆点
        startPointX = startPointXForHorizontalCoordinate;
        startPointY = startPointYForHorizontalCoordinate;
        for (int index = 0; index < mScores.length; index++) {
            path.reset();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(R.color.common_arena_gray_color));
            paint.setStrokeWidth(strokeWidth);
            paint.setPathEffect(null);

            startPointX = startPointXForHorizontalCoordinate + intervalForHorizonTal * (index + 1);
            startPointY = verticalInterval * (verticalCoordinate.length - mScores[index] / percent);
            canvas.drawCircle(startPointX, startPointY, circleRedius, paint);

            path.reset();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.white));
            canvas.drawCircle(startPointX, startPointY, circleRedius - strokeWidth / 2, paint);
        }

        paint.setTextSize(textSizeVertical);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.black001));
        float heightForInfo = textSizeVertical;
        float widthForInfo = paint.measureText(textInfo);
        Paint.FontMetrics metricsInfo = paint.getFontMetrics();
        float centerY = startPointYForHorizontalCoordinate - spaceHorizontal - heightForInfo / 2;
        float startYforTextinfo = centerY - (metricsInfo.bottom + metricsInfo.top) / 2;
        canvas.drawText(textInfo, startPointXForHorizontalCoordinate + lengthForHorizontalCoordiante - widthForInfo, startYforTextinfo, paint);
    }

    public void setDatas(float[] scores, float[] scoresAverage, String[] date) {
        mScores = scores;
        mScoresAverage = scoresAverage;
        if (date != null) {
            horizontalCoordinate = date;
        }

        invalidate();
    }
}
