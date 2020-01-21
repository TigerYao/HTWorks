package com.huatu.handheld_huatu.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;

/**
 * Created by chq on 2019/1/25.
 */

public class NewScDesTipGraphView extends View {
    private int[] verticalCoordinate = {0, 20, 40, 60, 80, 100};
    private int[] verticalCoordinate200 = {0, 40, 80, 120, 160, 200};

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
    private int dianWH = 10;
    private String averageTiptxt = "全站正确率";
    private String scScoreTiptxt = "模考正确率";

    private int showType = 0;                       // 纵轴类型 0、分数 1、正确率
    private String verticalAxis = "（分数）";

    public NewScDesTipGraphView(Context context) {
        this(context, null);
    }

    public NewScDesTipGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewScDesTipGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        sxdisTxt = DisplayUtil.dp2px(1);
        circleRedius = DisplayUtil.dp2px(3.3f);
        boardOffset = DisplayUtil.dp2px(1);
        dianWH = DisplayUtil.dp2px(4f);
        xOffsety = -DisplayUtil.dp2px(9.9f);
        xStep = DisplayUtil.getScreenWidth() / 5;
    }

    private void initData(Context context) {
//        this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(DisplayUtil.dp2px(13));
        colorBlack333 = ContextCompat.getColor(getContext(), R.color.arena_report_item_text_color);
        colorBlack666 = ContextCompat.getColor(getContext(), R.color.blackF4);
        colorScoreLine = ContextCompat.getColor(getContext(), R.color.redF3);
        colorScoreAverage = ContextCompat.getColor(getContext(), R.color.sc_report_average);
        mPaint.setColor(colorBlack666);
        mPath = new Path();
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth() - getPaddingLeft() * 2;
        float height = getHeight() - getPaddingTop() * 2;

        verticalAxis = showType == 0 ? "(分数)" : "(正确率)";

        mPaint.setColor(colorBlack666);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        // 绘制 边框
//        mPath.reset();
//        mPath.moveTo(0 + xSpace2, 0);
//        mPath.lineTo(width - boardOffset - xSpace2, 0);
//        canvas.drawPath(mPath, mPaint);
//
//        mPath.reset();
//        mPath.moveTo(0 + xSpace2, 0);
//        mPath.lineTo(0 + xSpace2, height);
//        canvas.drawPath(mPath, mPaint);
//
//        mPath.reset();
//        mPath.moveTo(width - boardOffset - xSpace2, 0);
//        mPath.lineTo(width - boardOffset - xSpace2, height);
//        canvas.drawPath(mPath, mPaint);
//
//        mPath.reset();
//        mPath.moveTo(0 + xSpace2, height);
//        mPath.lineTo(width - boardOffset - xSpace2, height);
//        canvas.drawPath(mPath, mPaint);


        yStep = (height - 2 * ySpace) / (verticalCoordinate.length);

        // 画行
        for (int i = 0; i < verticalCoordinate.length; i++) {
            mPath.reset();
            mPath.moveTo(xSpace - xOffsety, height - ySpace - yStep * i);
            mPath.lineTo(width - xSpace - xOffsety, height - ySpace - yStep * i);
            canvas.drawPath(mPath, mPaint);
        }

        // 左字
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(6);
        mPaint.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < verticalCoordinate.length; i++) {
            float textWidth = mPaint.measureText(String.valueOf(verticalCoordinate[i]));
            Paint.FontMetrics fm = mPaint.getFontMetrics();
            float textHeight = (fm.bottom + fm.top) / 2;
            canvas.drawText(String.valueOf(verticalCoordinate[i]), -sLength + xSpace - textWidth - sydisTxt -
                    xOffsety, height - ySpace - yStep * i + Math.abs(textHeight), mPaint);
            if (i == verticalCoordinate.length - 1) {
                canvas.drawText(verticalAxis, -sLength + xSpace - textWidth - sydisTxt - sydisTxt - xOffsety,
                        height - ySpace - yStep * i + Math.abs(textHeight) - scoreOfy, mPaint);
            }
        }


        mPaint.setStrokeWidth(6);
        //画 平均分图例
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(colorScoreAverage);
//        canvas.drawRect(new RectF(width - 36* dianWH - dianWH, 9 * dianWH + dianWH, width - 36 * dianWH + dianWH, 11 * dianWH + dianWH), mPaint);
        canvas.drawRect(new RectF(width - 12 * dianWH - dianWH, 9 * dianWH + dianWH, width - 12 * dianWH + dianWH, 11 * dianWH + dianWH), mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.reset();
//        mPath.moveTo(getStartX(width - 40 * dianWH), 11 * dianWH);
//        mPath.lineTo(getStartX(width - 32 * dianWH), 11 * dianWH);
        mPath.moveTo(width - 15 * dianWH, 11 * dianWH);
        mPath.lineTo(width - xSpace - xOffsety, 11 * dianWH);
        canvas.drawPath(mPath, mPaint);

        //画 模考得分图例
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(colorScoreLine);
//        canvas.drawCircle(width - 36 * dianWH, 6 * dianWH, dianWH, mPaint);
        canvas.drawCircle(width - 12 * dianWH, 6 * dianWH, dianWH, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.reset();
//        mPath.moveTo(getStartX(width - 40 * dianWH), 6 * dianWH);
//        mPath.lineTo(getStartX(width - 32 * dianWH), 6 * dianWH);
        mPath.moveTo(width - 15 * dianWH, 6 * dianWH);
        mPath.lineTo(width - xSpace - xOffsety, 6 * dianWH);
        canvas.drawPath(mPath, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(colorBlack666);
        mPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(averageTiptxt), width - 17 * dianWH, 7 * dianWH + 5 * dianWH, mPaint);
        canvas.drawText(String.valueOf(scScoreTiptxt), width - 17 * dianWH, 7 * dianWH, mPaint);

    }

    private float getStartX(float x) {
        return x;
    }

    public void setTextDes(String s, String s1) {
        if (TextUtils.isEmpty(s) || TextUtils.isEmpty(s1)) {
            return;
        }
        averageTiptxt = s;
        scScoreTiptxt = s1;
        invalidate();
    }

    public void setTextDes(String s, String s1, int type) {
        if (TextUtils.isEmpty(s) || TextUtils.isEmpty(s1)) {
            return;
        }
        averageTiptxt = s;
        scScoreTiptxt = s1;
        if (type == 200) {
            percent = 40;
            verticalCoordinate = verticalCoordinate200;
        }
        invalidate();
    }
}
