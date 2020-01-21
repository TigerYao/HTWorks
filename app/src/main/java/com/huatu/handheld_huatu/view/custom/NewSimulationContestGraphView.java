package com.huatu.handheld_huatu.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.LUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;

/**
 * Created by chq on 2019/1/25.
 */

public class NewSimulationContestGraphView extends View {
    //模考得分
    private float[] mScores = {0f};
    //全站平均分
    private float[] mScoresAverage = {0f};
    //y轴分值
    private int[] verticalCoordinate = {0, 20, 40, 60, 80, 100};
    private int[] verticalCoordinate200 = {0, 40, 80, 120, 160, 200};
    //x轴日期
    private String[] horizontalCoordinate = {"7/0"};
    private Paint mPaint;
    private Path mPath;

    private float xSpace = 0;
    private float xSpace2 = 0;
    private float ySpace;
    private float xTxtSize;
    private float yTxtSize;
    private float inter = 0.0f;
    private float xStep, yStep;
    private float sLength = 0;
    private float sydisTxt = 0;
    private float sxdisTxt = 0;
    private int percent = 20;
    private int circleRedius = 0;
    boolean isStart;
    private int boardOffset = 30;
    private int colorBlack333;
    private int colorBlack666;
    private int colorScoreLine;
    private int colorScoreAverage;
    private int scoreOfy = 0;
    private int xOffsety = 0;
    private int dianWH = 15;
    private int colorScoreChangeStart;
    private int colorScoreChangeEnd;


    public NewSimulationContestGraphView(Context context) {
        this(context, null);
    }

    public NewSimulationContestGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewSimulationContestGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ty = context.obtainStyledAttributes(attrs, R.styleable.GraphView);
        xSpace = ty.getDimension(R.styleable.GraphView_spaceHorizontal, 0);
        ySpace = ty.getDimension(R.styleable.GraphView_spaceVertical, 0);
        xTxtSize = ty.getDimension(R.styleable.GraphView_textSizeHorizontal, 0);
        yTxtSize = ty.getDimension(R.styleable.GraphView_textSizeVertical, 0);

        initVar();
        initData(context);
    }

    private void initVar() {
        scoreOfy = DisplayUtil.dp2px(25);
        xSpace2 = DisplayUtil.dp2px(7);
        sLength = DisplayUtil.dp2px(3.3f);
        sydisTxt = DisplayUtil.dp2px(3.3f);
        sxdisTxt = DisplayUtil.dp2px(10);
        circleRedius = DisplayUtil.dp2px(3.3f);
        boardOffset = DisplayUtil.dp2px(1);
        dianWH = DisplayUtil.dp2px(4f);
        xOffsety = -DisplayUtil.dp2px(9.9f);
        xStep = DisplayUtil.getScreenWidth() / 8;
    }

    private void initData(Context context) {
//        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setTextSize(DisplayUtil.dp2px(13));
        colorBlack333 = ContextCompat.getColor(getContext(), R.color.arena_report_item_text_color);
        colorBlack666 = ContextCompat.getColor(getContext(), R.color.blackF4);
        colorScoreLine = ContextCompat.getColor(getContext(), R.color.redF3);
        colorScoreChangeStart = ContextCompat.getColor(getContext(), R.color.sc_report_change_start);
        colorScoreChangeEnd = ContextCompat.getColor(getContext(), R.color.sc_report_change_end);
        colorScoreAverage = ContextCompat.getColor(getContext(), R.color.sc_report_average);
        mPaint.setColor(colorBlack666);
        mPath = new Path();
    }

    public void setDatas(float[] scores, float[] scoresAverage, String[] date) {
        if (scores == null || scoresAverage == null || date == null) {
            return;
        }
        this.mScores = scores;
        this.mScoresAverage = scoresAverage;
        this.horizontalCoordinate = date;
        requestLayout();
        invalidate();
    }

    private int type;
    private int offSetX = DisplayUtil.dp2px(10);

    public void setDatas(float[] scores, float[] scoresAverage, String[] date, int type) {
        if (scores == null || scoresAverage == null || date == null) {
            return;
        }
        this.mScores = scores;
        this.mScoresAverage = scoresAverage;
        this.horizontalCoordinate = date;
        this.type = type;
        if (type == 200) {
            percent = 40;
            verticalCoordinate = verticalCoordinate200;
        }
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (type == 1) {
            offSetX = DisplayUtil.dp2px(10);
        } else {
            offSetX = DisplayUtil.dp2px(0);
        }
        if (horizontalCoordinate.length < 4) {
            //参加过的模考数量少于4场，就设置图标的宽为屏幕宽
            setMeasuredDimension(DisplayUtil.getScreenWidth() + offSetX, height);
        } else {
            //参加过的模考数量大于等于4场，示图的宽为（屏幕的八分之一乘以模考数量再加上个偏移量）；
            setMeasuredDimension((int) xStep * (horizontalCoordinate.length) + offSetX + DisplayUtil.dp2px(30), height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (horizontalCoordinate.length < 1) {
            return;
        }

        int width = getWidth() - getPaddingLeft() * 2;
        float height = getHeight() - getPaddingTop() * 2;

        mPaint.setColor(colorBlack666);
        mPaint.setStyle(Paint.Style.STROKE);


        float xStep = this.xStep;
        if (mScoresAverage.length < 4) {
            xStep = width / (mScoresAverage.length + 1);
        }

        yStep = (height - 2 * ySpace) / (verticalCoordinate.length);


//        // x间隔刻度
//        mPaint.setColor(colorBlack666);
//        mPaint.setStyle(Paint.Style.STROKE);
//        for (int i = 1; i < horizontalCoordinate.length; i++) {
//            mPath.reset();
//            //移动画笔的起点
//            mPath.moveTo(xSpace + xStep * i - xOffsety, height - ySpace);
//            mPath.lineTo(xSpace + xStep * i - xOffsety, height - ySpace - sLength);
//            canvas.drawPath(mPath, mPaint);
//        }

        // x轴 日期
        if (horizontalCoordinate[0].length() > 3) {
            mPaint.setTextSize(DisplayUtil.dp2px(12));
        }

        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 1; i < horizontalCoordinate.length; i++) {
            float textWidth = mPaint.measureText(String.valueOf(horizontalCoordinate[i]));
            Paint.FontMetrics fm = mPaint.getFontMetrics();
            float textHeight = (fm.bottom + fm.top);
            canvas.drawText(String.valueOf(horizontalCoordinate[i]), xSpace + xStep * i - textWidth / 2 - xOffsety, height -
                    ySpace + sLength + 2 * sxdisTxt, mPaint);
        }
        if (horizontalCoordinate[0].length() > 3) {
            mPaint.setTextSize(DisplayUtil.dp2px(13));
        }
        // 画 分数线填充渐变色
        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(colorScoreLine);
        Shader mShader = new LinearGradient(xSpace + xStep, 0, xSpace + xStep * (mScores.length - 1), 0,
                new int[]{colorScoreChangeStart, colorScoreChangeEnd},
                null, Shader.TileMode.CLAMP);
        mPaint.setShader(mShader);
        for (int i = 1; i < mScores.length - 1; i++) {
            float sY = height - ySpace - mScores[i] / percent * yStep;
            float sX = xSpace + xStep * (i);
            float eY = height - ySpace - mScores[(i + 1)] / percent * yStep;
            float eX = xSpace + xStep * (i + 1);
            mPath.reset();
            mPath.moveTo(getStartX(sX) - xOffsety, sY);
            mPath.lineTo(getStartX(eX) - xOffsety, eY);
            mPath.lineTo(getStartX(eX) - xOffsety, height - ySpace);
            mPath.lineTo(getStartX(sX) - xOffsety, height - ySpace);
            canvas.drawPath(mPath, mPaint);
        }

        // 画 分数线
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(colorScoreLine);
        mPaint.setShader(null);
        for (int i = 1; i < mScores.length - 1; i++) {
            float sY = height - ySpace - mScores[i] / percent * yStep;
            float sX = xSpace + xStep * (i);
            float eY = height - ySpace - mScores[(i + 1)] / percent * yStep;
            float eX = xSpace + xStep * (i + 1);
            mPath.reset();
            mPath.moveTo(getStartX(sX) - xOffsety, sY);
            mPath.lineTo(getStartX(eX) - xOffsety, eY);
            canvas.drawPath(mPath, mPaint);
        }
        // 画 分数线点
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(null);
        for (int i = 1; i < mScores.length; i++) {
            float sY = height - ySpace - mScores[i] / percent * yStep;
            float sX = xSpace + xStep * (i);
            mPaint.setColor(colorScoreLine);
            // 画红方块
//            canvas.drawRect(new RectF(sX - dianWH - xOffsety, sY - dianWH, sX + dianWH - xOffsety, sY + dianWH), mPaint);
            // 画小圆圈
            canvas.drawCircle(sX - xOffsety, sY, dianWH, mPaint);
            float yoff = mScores[i] - mScoresAverage[i];
            if (Math.abs(yoff) > dianWH) {
                yoff = 0;
            } else {
                if (yoff < 0) {
                    if (mScores[i] < 10) {
                        yoff = (5 + mScores[i] / 2) * dianWH;
                    } else {
                        yoff = 6 * dianWH;
                    }
                } else {
                    yoff = -dianWH;
                }
            }
            mPaint.setColor(colorBlack333);
            canvas.drawText(LUtils.formatPoint(mScores[i]), sX - 2 * dianWH - xOffsety, sY - 2 * dianWH + yoff, mPaint);
        }

        // 画 平均分数线
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(colorScoreAverage);
        for (int i = 1; i < mScoresAverage.length - 1; i++) {
            float sY = height - ySpace - mScoresAverage[i] / percent * yStep;
            float sX = xSpace + xStep * (i);
            float eY = height - ySpace - mScoresAverage[(i + 1)] / percent * yStep;
            float eX = xSpace + xStep * (i + 1);
            mPath.reset();
            mPath.moveTo(getStartX(sX) - xOffsety, sY);
            mPath.lineTo(getStartX(eX) - xOffsety, eY);
            canvas.drawPath(mPath, mPaint);
        }
        // 画 平均分数线点
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 1; i < mScoresAverage.length; i++) {
            mPaint.setColor(colorScoreAverage);
            float sY = height - ySpace - mScoresAverage[i] / percent * yStep;
            float sX = xSpace + xStep * (i);
            canvas.drawRect(new RectF(sX - dianWH - xOffsety, sY - dianWH, sX + dianWH - xOffsety, sY + dianWH), mPaint);
            float yoff = mScores[i] - mScoresAverage[i];
            if (Math.abs(yoff) > dianWH) {
                yoff = 0;
            } else {
                if (yoff < 0) {
                    yoff = -dianWH;
                } else {
                    if (mScoresAverage[i] < 10) {
                        yoff = (5 + mScoresAverage[i] / 2) * dianWH;
                    } else {
                        yoff = 6 * dianWH;
                    }
                }
            }
            mPaint.setColor(colorBlack333);
            canvas.drawText(LUtils.formatPoint(mScoresAverage[i]), sX - 2 * dianWH - xOffsety, sY - 2 * dianWH + yoff, mPaint);
        }

    }

    private String getNoo(String mScore) {
        if (mScore != null) {
            if (mScore.endsWith(".0")) {
                int end = mScore.length() - 2;
                if (end < mScore.length()) {
                    mScore = mScore.substring(0, end);
                    return mScore;
                }
            }
        }
        return mScore;
    }

    private float getStartX(float x) {
        return x;
    }
}
