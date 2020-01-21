package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.huatu.handheld_huatu.business.arena.utils.LUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;

import java.util.ArrayList;

public class ViewHistogram extends View {

    private String TAG = "ViewHistogram";

    private Context mContext;

    private Paint mPaintBg;             // 背景画笔、及格分画笔
    private Paint mPaintText;           // 文字画笔
    private Paint mPaintWord;           // 左边文字画笔
    private Paint mPaintScore;          // 得分柱状图画笔

    private int mCount = 5;             // 几组条形图
    private int max = 120;              // 最大值

    private String[] names = {"语言理解与表达", "数量关系", "判断推理", "资料分析", "常识判断"};     // 画的周围的文字

    private float wordsPercent = 0.3f;              // 图形左边宽度百分比
    private int paddingRight = 50;                  // 右边padding
    private int spice = 15;                         // 字和图形之间的空隙

    private ArrayList<PointF> data = new ArrayList<>();         // 数据list，point.x是我的得分，point.y是及格得分
    private float measureWidth;
    private float measureHeight;
    private float wordsWidth;
    private float bottomTextHeight;
    private float leftWordSize;

    public ViewHistogram(Context context) {
        this(context, null);
    }

    public ViewHistogram(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewHistogram(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        // 初始化画笔
        mPaintBg = new Paint();
        mPaintBg.setColor(Color.parseColor("#D1D1D1"));
        mPaintBg.setStyle(Paint.Style.FILL);
        mPaintBg.setStrokeWidth(3);

        mPaintText = new Paint();
        mPaintText.setColor(Color.parseColor("#282828"));
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setStrokeWidth(5);

        // 数据画笔
        mPaintWord = new Paint();
        mPaintWord.setColor(Color.parseColor("#000000"));
        mPaintWord.setStyle(Paint.Style.FILL);
        mPaintWord.setStrokeWidth(5);
        Typeface font = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD);
        mPaintWord.setTypeface(font);

        mPaintScore = new Paint();
        mPaintScore.setStyle(Paint.Style.FILL);

        // TODO: 2018/11/20 测试数据
        data.add(new PointF(80, 60));
        data.add(new PointF(40, 40));
        data.add(new PointF(90, 70));
        data.add(new PointF(50, 30));
        data.add(new PointF(100, 60));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.makeMeasureSpec((int) (specSize * (1 - wordsPercent)), specMode);
        super.onMeasure(widthMeasureSpec, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        measureSize();

        drawBg(canvas);

        drawData(canvas);
    }

    private void drawData(Canvas canvas) {
        int size = data.size();

        if (size == 0) {
            return;
        }

        float pointHeight = (measureHeight - bottomTextHeight * 2) / size;      // 一组数据的高度
        float hisHeight = pointHeight / 5;                                      // 每个柱状图的高度
        float dYmyScore = hisHeight;                                            // 自己得分柱状图的偏移量
        float dYdefScore = hisHeight * 3;                                       // 默认得分偏移量

        mPaintText.setTextSize(hisHeight + 5);                                  // 设置后挂字高

        for (int i = 0; i < data.size(); i++) {

            PointF pointF = data.get(i);

            // 画我的数据
            float startX = wordsWidth;
            float startY = i * pointHeight + dYmyScore;
            float endX = pointF.x / max * measureWidth + wordsWidth;
            float endY = startY + hisHeight;
            // 渐变色
            LinearGradient backGradient = new LinearGradient(startX, startY, endX, endY, new int[]{Color.parseColor("#F5A623"), Color.parseColor("#FBD249")}, null, Shader.TileMode.CLAMP);

            mPaintScore.setShader(backGradient);

            canvas.drawRect(startX, startY, endX, endY, mPaintScore);

            canvas.drawText(LUtils.formatPoint(pointF.x), endX + 15, endY, mPaintText);

            // 画默认数据
            float startXL = wordsWidth;
            float startYL = i * pointHeight + dYdefScore;
            float endXL = pointF.y / max * measureWidth + wordsWidth;
            float endYL = startYL + hisHeight;

            canvas.drawRect(startXL, startYL, endXL, endYL, mPaintBg);

            canvas.drawText(LUtils.formatPoint(pointF.y), endXL + 15, endYL, mPaintText);

            mPaintWord.setTextSize(leftWordSize);
            String name = names[i];
            float textWidth = mPaintWord.measureText(name);
            startXL -= (textWidth + spice);
            startYL += (leftWordSize - hisHeight) / 2 - 5;

            canvas.drawText(name, startXL, startYL, mPaintWord);
        }

    }

    /**
     * 根据max和mCount画背景
     */
    private void drawBg(Canvas canvas) {

        mPaintText.setTextSize(bottomTextHeight);

        float everWidth = measureWidth / mCount;
        float everSize = max / mCount;

        for (int i = 0; i <= mCount; i++) {
            // 画线
            canvas.drawLine(everWidth * i + wordsWidth, 0, everWidth * i + wordsWidth, measureHeight - bottomTextHeight * 2, mPaintBg);
            // 画字
            String text = String.valueOf(everSize * i).substring(0, String.valueOf(everSize * i).indexOf('.'));
            float textWidth = mPaintText.measureText(text);
            canvas.drawText(text, everWidth * i + wordsWidth - textWidth / 2, measureHeight - 20, mPaintText);
        }
    }

    /**
     * 测量各种数据
     */
    private void measureSize() {
        measureWidth = getMeasuredWidth() * (1 - wordsPercent) - paddingRight;
        measureHeight = getMeasuredHeight();

        wordsWidth = getMeasuredWidth() * wordsPercent;

        bottomTextHeight = measureHeight / (names.length * 5 + 2);

        int count = 0;
        for (int i = 0; i < names.length; i++) {
            if (names[i].length() > count) {
                count = names[i].length();
            }
        }

        leftWordSize = (wordsWidth - spice * 2) / count;

        int text13 = DisplayUtil.dp2px(13);
        leftWordSize = leftWordSize > text13 ? text13 : leftWordSize;
    }

    /**
     * 设置数据，数据是Point集合，
     */
    public void setData(ArrayList<PointF> data, String[] names) {
        if (data == null || names == null || data.size() != names.length) {
            Log.e(TAG, "数据格式不正确");
            return;
        }
        this.data = data;
        this.names = names;
        float max = 0f;
        for (PointF datum : data) {
            if (datum.x > max) {
                max = datum.x;
            }
            if (datum.y > max) {
                max = datum.y;
            }
        }
        this.max = (((int) (max + 5) / 10 + 2) * 10);
        invalidate();
    }
}
