package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.LUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewRadarMap extends View {

    private String TAG = "RadarMap";

    private Context mContext;

    private Paint mPaintBg;
    private Paint mPaintLine;
    private Paint mPaintData;
    private Paint mPaintText;

    private int radarColorBg = Color.parseColor("#F7F7F7");             // 网格的背景的颜色
    private int radarColor = Color.parseColor("#CCD4DF");               // 背景网格的颜色
    private int textColor = Color.parseColor("#000000");                // 文字

    // 星星们
    private int[] stars = {R.mipmap.radar_star_full, R.mipmap.radar_star_half, R.mipmap.radar_star_empty};
    private Bitmap bFull;
    private Bitmap bHalf;
    private Bitmap bEmpty;

    private boolean isShowStars = true;                         // 周围数据是显示星星，还是显示文字，默认显示星星

    private float radarPercent = 1f / 2f;                       // 雷达图占整个控件的大小

    private int alpha = 100;                                                                            // 数据颜色的透明度，255不透明

    private int mSize = 5;                                      // 几边型
    private float radarWidth = 2;                            // 网格线的宽度
    private float[] roCount = {0.2f, 0.4f, 0.6f, 0.8f};         // 背景默认要画几个网格
    private double[] degrees = new double[mSize];               // 几个边的角度

    private float starSize = 40;                                // 星星的大小
    private float starSpace = 5f;                               // 星星之间的间距

    private float lineSpace = 15;                               // 文字行间距

    private String[] names = {"语言理解与表达", "数量关系", "判断推理", "资料分析", "常识判断"};     // 画的周围的文字
    private Double[] outData;
    private Map<Double[], String[]> contentData = new HashMap<>();                                       // 数据

    private String[][] defColor = {
            {"#EC74A0", "#FF00A5"},
            {"#C86DD7", "#3023AE"},
    };

    public ViewRadarMap(Context context) {
        this(context, null);
    }

    public ViewRadarMap(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewRadarMap(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        // 初始化画笔
        // 蜘蛛网内的背景
        mPaintBg = new Paint();
        mPaintBg.setStyle(Paint.Style.FILL);
        mPaintBg.setColor(radarColorBg);
        mPaintBg.setStrokeWidth(radarWidth);

        // 背景线
        mPaintLine = new Paint();
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setColor(radarColor);
        mPaintLine.setStrokeWidth(radarWidth);

        // 数据画笔
        mPaintData = new Paint();
        mPaintData.setColor(radarColor);
        mPaintData.setAlpha(alpha);
        mPaintData.setStyle(Paint.Style.FILL);
        mPaintData.setStrokeWidth(radarWidth);

        // 数据画笔
        mPaintText = new Paint();
        mPaintText.setColor(textColor);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setStrokeWidth(radarWidth);
        Typeface font = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD);
        mPaintText.setTypeface(font);

        bFull = BitmapFactory.decodeResource(mContext.getResources(), stars[0]);
        bHalf = BitmapFactory.decodeResource(mContext.getResources(), stars[1]);
        bEmpty = BitmapFactory.decodeResource(mContext.getResources(), stars[2]);

        // 初始化几个角度
        for (int i = 0; i < mSize; i++) {
            degrees[i] = 2 * Math.PI / mSize * i - Math.PI / 2;
        }

        // TODO: 2018/11/20 测试数据
        outData = new Double[]{0.5d, 0.5d, 0.5d, 0.5d, 0.5d};
        contentData.put(new Double[]{0.6d, 0.6d, 0.6d, 0.6d, 0.6d}, new String[]{"#EC74A0", "#FF00A5"});
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 高和宽一样
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width == 0 || height == 0) return;

        // 移动到画布中间
        canvas.translate((float) width / 2, (float) height / 2);

        // 画网格背景
        drawRadar(canvas);

        // 画数据
        drawRadarContent(canvas);

        if (isShowStars) {
            // 画文字和星星
            drawRadarTextAndStars(canvas);
        } else {
            drawRadarTextAndData(canvas);
        }
    }

    /**
     * 周围显示文字和数字
     */
    private void drawRadarTextAndData(Canvas canvas) {
        float textSize = getMeasuredWidth() * (1 - radarPercent) / 2 - lineSpace * 2;
        // 得到字号
        int count = 0;
        for (String name : names) {
            if (name.length() > count) {
                count = name.length();
            }
        }
        textSize = textSize / count;
        int text13 = DisplayUtil.dp2px(13);
        textSize = textSize > text13 ? text13 : textSize;
        mPaintText.setTextSize(textSize);

        for (int i = 0; i < mSize; i++) {

            String data = LUtils.doubleToPercent(outData[i]) + "%";

            String name = names[i];

            PointF point = getPoint(i, 1);
            PointF pointTextUp = new PointF(point.x, point.y);
            PointF pointTextDown = new PointF(point.x, point.y);

            float upWidth = mPaintText.measureText(data);
            float downWidth = mPaintText.measureText(name);
            boolean upBigger = upWidth > downWidth;
            float width = upBigger ? upWidth : downWidth;

            if (pointTextUp.x < 1 && pointTextUp.x > -1) {     // 中间的字
                pointTextUp.x -= (upWidth / 2);
                pointTextDown.x -= (downWidth / 2);
                if (pointTextUp.y < 0) {                                // 中上的字
                    pointTextUp.y -= (textSize + lineSpace * 2);
                    pointTextDown.y -= lineSpace;
                } else {                                                // 中下的字
                    pointTextUp.y += (textSize + lineSpace);
                    pointTextDown.y += (lineSpace * 2 + textSize * 2);
                }
            } else if (pointTextUp.x < 0) {                   // 左边的字
                if (upBigger) {                               // 字更宽
                    pointTextUp.x -= (lineSpace + width);
                    pointTextDown.x -= (lineSpace + width - (upWidth - downWidth) / 2);
                } else {                                        // 星星更宽
                    pointTextUp.x -= (lineSpace + width - (downWidth - upWidth) / 2);
                    pointTextDown.x -= (lineSpace + width);
                }
                pointTextUp.y -= lineSpace / 2;
                pointTextDown.y += lineSpace / 2 + textSize;
            } else {                                         // 右边的字
                if (upBigger) {                               // 字更宽
                    pointTextUp.x += lineSpace;
                    pointTextDown.x += (lineSpace + (upWidth - downWidth) / 2);
                } else {                                        // 星星更宽
                    pointTextUp.x += (lineSpace + (downWidth - upWidth) / 2);
                    pointTextDown.x += lineSpace;
                }
                pointTextUp.y -= lineSpace / 2;
                pointTextDown.y += lineSpace / 2 + textSize;
            }
            canvas.drawText(data, pointTextUp.x, pointTextUp.y, mPaintText);
            canvas.drawText(name, pointTextDown.x, pointTextDown.y, mPaintText);
        }
    }

    /**
     * 画文字和星星
     */
    private void drawRadarTextAndStars(Canvas canvas) {
        float textSize = getMeasuredWidth() * (1 - radarPercent) / 2 - lineSpace * 2;
        // 计算星星的大小，星星的间距。星星和间距比例是 5：1
        starSize = textSize * 5 / 41;
        starSpace = textSize / 41;
        // 得到字号
        int count = 0;
        for (String name : names) {
            if (name.length() > count) {
                count = name.length();
            }
        }
        textSize = textSize / count;
        int text13 = DisplayUtil.dp2px(13);
        textSize = textSize > text13 ? text13 : textSize;
        mPaintText.setTextSize(textSize);

        for (int i = 0; i < mSize; i++) {

            String name = names[i];

            PointF point = getPoint(i, 1);
            PointF pointText = new PointF(point.x, point.y);
            PointF pointStar = new PointF(point.x, point.y);

            float textWidth = mPaintText.measureText(name);
            float starWidth = starSize * 5 + starSpace * 4;
            boolean textBigger = textWidth > starWidth;
            float width = textBigger ? textWidth : starWidth;

            if (pointText.x < 1 && pointText.x > -1) {     // 中间的字
                pointText.x -= (textWidth / 2);
                pointStar.x -= (starWidth / 2);
                if (pointText.y < 0) {                              // 中上的字
                    pointText.y -= (starSize + lineSpace * 2);
                    pointStar.y -= (lineSpace + starSize);
                } else {                                            // 中下的字
                    pointText.y += (textSize + lineSpace);
                    pointStar.y += (lineSpace * 2 + textSize);
                }
            } else if (pointText.x < 0) {                   // 左边的字
                if (textBigger) {                               // 字更宽
                    pointText.x -= (lineSpace + width);
                    pointStar.x -= (lineSpace + width - (textWidth - starWidth) / 2);
                } else {                                        // 星星更宽
                    pointText.x -= (lineSpace + width - (starWidth - textWidth) / 2);
                    pointStar.x -= (lineSpace + width);
                }
                pointText.y -= lineSpace / 2;
                pointStar.y += lineSpace / 2;
            } else {                                         // 右边的字
                if (textBigger) {                               // 字更宽
                    pointText.x += lineSpace;
                    pointStar.x += (lineSpace + (textWidth - starWidth) / 2);
                } else {                                        // 星星更宽
                    pointText.x += (lineSpace + (starWidth - textWidth) / 2);
                    pointStar.x += lineSpace;
                }
                pointText.y -= lineSpace / 2;
                pointStar.y += lineSpace / 2;
            }
            canvas.drawText(name, pointText.x, pointText.y, mPaintText);
            drawStar(canvas, pointStar, i);
        }
    }

    /**
     * 画星星
     */
    private void drawStar(Canvas canvas, PointF pointStar, int index) {
        Double outDatum = outData[index];
        int full = 0;
        int half = 0;
        int empty = 0;
        full = (int) (outDatum * 10) / 2;
        int halfx = (int) (outDatum * 10) % 2;
        if (halfx == 0) {
            half = 1;
        } else {
            full += 1;
        }
        empty = 5 - full - half;

        RectF dst = new RectF();
        dst.left = pointStar.x;
        dst.top = pointStar.y;
        dst.bottom = dst.top + starSize;
        dst.right = dst.left + starSize;

        for (int i = 0; i < 5; i++) {
            if (i < full) {
                canvas.drawBitmap(bFull, null, dst, mPaintText);
            } else if (i < full + half) {
                canvas.drawBitmap(bHalf, null, dst, mPaintText);
            } else {
                canvas.drawBitmap(bEmpty, null, dst, mPaintText);
            }
            dst.left += (starSize + starSpace);
            dst.right += (starSize + starSpace);
        }

    }

    /**
     * 画数据多边形，数据key为多边形，value为多边形颜色
     */
    private void drawRadarContent(Canvas canvas) {
        if (contentData.size() == 0) {
            return;
        }
        for (Double[] floats : contentData.keySet()) {
            String[] strings = contentData.get(floats);
            initContentPaint(strings);

            Path path = new Path();
            PointF point = getPoint(0, floats[0]);
            path.moveTo(point.x, point.y);
            for (int j = 1; j < mSize; j++) {
                PointF pointA = getPoint(j, floats[j]);
                path.lineTo(pointA.x, pointA.y);
            }
            path.close();
            canvas.drawPath(path, mPaintData);
        }
    }

    /**
     * 画背景
     */
    private void drawRadar(Canvas canvas) {

        // 绘制五边形背景
        Path path = new Path();
        PointF point = getPoint(0, 1f);
        path.moveTo(point.x, point.y);
        for (int j = 1; j < mSize; j++) {
            PointF pointA = getPoint(j, 1f);
            path.lineTo(pointA.x, pointA.y);
        }
        path.close();
        canvas.drawPath(path, mPaintBg);

        // 画五边形网格线
        for (int i = 0; i < mSize; i++) {
            PointF p = getPoint(i, 1f);
            canvas.drawLine(0, 0, p.x, p.y, mPaintLine);
        }

        // 画几边型
        for (float v : roCount) {
            Path pathA = new Path();
            PointF pointA = getPoint(0, v);
            pathA.moveTo(pointA.x, pointA.y);
            for (int j = 1; j < mSize; j++) {
                PointF pointB = getPoint(j, v);
                pathA.lineTo(pointB.x, pointB.y);
            }
            pathA.close();
            canvas.drawPath(pathA, mPaintLine);
        }
    }

    /**
     * 根据哪条线，和百分比，获取x, y坐标
     */
    private PointF getPoint(int index, double percent) {
        if (index < 0 || index > degrees.length - 1) {
            index = 0;
        }
        if (degrees == null) {
            return new PointF(0f, 0f);
        }
        float r = getMeasuredWidth() * radarPercent / 2f;
        PointF p = new PointF();
        p.x = (float) (r * Math.cos(degrees[index]) * percent);
        p.y = (float) (r * Math.sin(degrees[index]) * percent);
        return p;
    }

    /**
     * 设置内数据图形的颜色
     */
    private void initContentPaint(String[] colors) {
        PointF point = getPoint(degrees.length - 1, 1);
        PointF pointx = getPoint(degrees.length / 2, 1);

        // 渐变色
        LinearGradient backGradient = new LinearGradient(point.x, point.y, pointx.x, pointx.y,
                new int[]{Color.parseColor(colors[0]), Color.parseColor(colors[1])}, null, Shader.TileMode.CLAMP);

        mPaintData.setShader(backGradient);
    }

    /**
     * 使用默认颜色，设置数据。
     */
    public void setData(ArrayList<Double[]> datas, String[] names, boolean isShowStars) {

        if (datas == null || datas.size() == 0 || names == null || names.length == 0) {
            return;
        }

        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).length != names.length) {
                Log.e(TAG, "数据量和名称量不一样");
                return;
            }
        }

        this.contentData.clear();
        for (int i = 0; i < datas.size(); i++) {
            contentData.put(datas.get(i), defColor[i % defColor.length]);
        }
        this.names = names;
        outData = datas.get(0);         // 默认显示数据为第一层数据
        mSize = names.length;
        this.isShowStars = isShowStars;

        degrees = new double[mSize];

        // 初始化几个角度
        for (int i = 0; i < mSize; i++) {
            degrees[i] = 2 * Math.PI / mSize * i - Math.PI / 2;
        }

        invalidate();
    }

    /**
     * 是显示星星，还是显示数字
     */
    public void setIsShowStars(boolean isShowStars) {
        this.isShowStars = isShowStars;
        invalidate();
    }
}

