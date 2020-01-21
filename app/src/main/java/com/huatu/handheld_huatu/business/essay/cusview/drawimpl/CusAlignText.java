package com.huatu.handheld_huatu.business.essay.cusview.drawimpl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.SelectionInfo;
import com.huatu.handheld_huatu.business.essay.cusview.bean.TxtModel;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;

/**
 * 实现了 ExerciseTextView.CusDraw 接口，所以 ExerciseTextView 的绘制全交给这里
 * <p>
 * 把绘制下划线通过接口 DrawLine 交给 CusDrawUnderLineEx
 */
public class CusAlignText implements ExerciseTextView.CusDraw {

    public static final String TAG = "CusAlignText";

    public Context mContext;
    ExerciseTextView mExerciseTextView;
    private int mLineY;                                     // 当前行的底边坐标
    private int mViewWidth;                                 // TextView 的宽度
    private SelectionInfo mSelectionInfo;                   // 文字选中后，选中的信息，开始、结束、内容

    private TxtModel txtModel = new TxtModel();
    private int lineCount = 0;                              // TextView 的总行数
    private int lineHeight = 0;                             // 行高

    boolean isHasDraw;

    public String text;

    protected DrawLine mDrawLine;

    public void setDrawLine(DrawLine mDrawLine) {
        this.mDrawLine = mDrawLine;
    }

    public void setTxtModel() {
        this.txtModel = new TxtModel();
    }

    public void setSelectableTextHelper(SelectionInfo mSelectionInfo) {
        this.mSelectionInfo = mSelectionInfo;
    }

    public void invalidate() {
        if (mExerciseTextView != null) {
            mExerciseTextView.invalidate();
        }
    }

    public CusAlignText(ExerciseTextView mExerciseTextView) {
        if (mExerciseTextView != null) {
            this.mContext = mExerciseTextView.getContext();
            this.mExerciseTextView = mExerciseTextView;
            initData(mExerciseTextView);
        }
    }

    public void initData(ExerciseTextView textView) {
        if (textView != null) {
            mContext = textView.getContext();
            mExerciseTextView = textView;
        }
    }

    public Context getContext() {
        return mContext;
    }

    public TextPaint getPaint() {
        if (mExerciseTextView != null) {
            return mExerciseTextView.getPaint();
        }
        return null;
    }

    boolean isDrawLineFront = false;                    // true、是先画bg，false、先画文字

    public void setFront(boolean var) {
        isDrawLineFront = var;
    }

    @Override
    public void onDraw(Canvas canvas, ExerciseTextView textView) {
        LogUtils.d(TAG, "draw sta System.currentTimeMillis():" + System.currentTimeMillis());
        if (textView != null && canvas != null) {

            isHasDraw = true;                                           // 正在绘制
            TextPaint paint = textView.getPaint();                      // 得到 ExerciseTextView 的 画笔Paint
            paint.setColor(textView.getCurrentTextColor());             // 设置画笔颜色
            paint.drawableState = textView.getDrawableState();
            mViewWidth = textView.getMeasuredWidth();                   // TextView 的宽度
            if (text == null) {
                text = textView.getText().toString();                   // TextView 的内容
            }

            if (TextUtils.isEmpty(text)) {
                return;
            }

            mLineY = 0;                                                 // 当前的 Y 位置 为 0
            mLineY += textView.getTextSize();                           // 当前的 Y 位置 为 字高 * 1.5
            Layout layout = textView.getLayout();                       // Layout 是 TextView 的一些显示属性

            // 由于切换横屏，所以每次重绘就进行数据计算或者获取当前数据
//            if (txtModel.lineCount > 0) {                               // TextModel 记录一些 Text 的属性 不需要频繁的从layout中取（可能）
//                lineCount = txtModel.lineCount;                         // TextView 的总行数
//            } else {
            lineCount = layout.getLineCount();
            txtModel.lineCount = lineCount;
//            }

//            if (txtModel.lineHeight > 0) {                              // 行高
//                lineHeight = txtModel.lineHeight;
//            } else {
//                lineHeight = textView.getLineHeight();
            lineHeight = textView.getMeasuredHeight() / lineCount;  // 如果修改了系统字体，行高会变，内容绘制就会超出控件高度。所以由控件的高度/行数，反算行高。减30是因为还要留出最后一行画波浪线
            txtModel.lineHeight = lineHeight;
//            }

            if (mDrawLine != null) {                               //波浪线在此处绘制
                mDrawLine.drawCusHeiLine(this, canvas, textView, layout);
            }

//            txtModel.lineModels.clear();

            // 遍历每一行
            TxtModel.LineModel lineModel = null;
            for (int i = 0; i < lineCount; i++) {
                int lineStart = 0;
                int lineEnd = 0;
                String line = null;
                float width = 0;
                boolean isScale = false;

//                if (i < txtModel.lineModels.size()) {
//                    lineModel = txtModel.lineModels.get(i);
//                    lineStart = lineModel.lineStart;
//                    lineEnd = lineModel.lineEnd;
//                    line = text.substring(lineStart, lineEnd);
//                    width = lineModel.lineWidth;
//                    isScale = lineModel.isScale;
//                } else {
                lineModel = new TxtModel.LineModel();
//                    txtModel.lineModels.add(lineModel);

                lineStart = layout.getLineStart(i);
                lineEnd = layout.getLineEnd(i);
                line = text.substring(lineStart, lineEnd);
                width = getDesiredWidth(0, text, lineStart, lineEnd, getPaint());
                isScale = needScale(line, lineEnd);

                lineModel.lineStart = lineStart;
                lineModel.lineEnd = lineEnd;
                lineModel.lineWidth = width;
                lineModel.isScale = isScale;
//                }

                if (isDrawLineFront) {                          // 申论，材料为true，先画选中的背景
                    if (mSelectionInfo != null) {                   // 画选中的背景
                        drawSelBg(canvas, lineStart, lineEnd, line, width, isScale);
                    }
                    if (mDrawLine != null) {                        // 画下划线
                        mDrawLine.drawCusHeiLine(this, canvas, mLineY, lineStart, lineEnd, line, width, isScale);
                    }
                } else {
                    if (mDrawLine != null) {
                        mDrawLine.drawCusHeiLine(this, canvas, mLineY, lineStart, lineEnd, line, width, isScale);
                    }
                    if (mSelectionInfo != null) {
                        drawSelBg(canvas, lineStart, lineEnd, line, width, isScale);
                    }
                }

                // 画文字
                if (isScale && i < lineCount - 1) {
                    drawScaledText(canvas, lineStart, line, width, lineModel);
                } else {
                    canvas.drawText(line, 0, mLineY, paint);
                }

                mLineY += lineHeight;
            }

        }
//        }
        LogUtils.d(TAG, "draw end System.currentTimeMillis():" + System.currentTimeMillis());
    }

    /**
     * 画文字被选中后的背景
     *
     * @param canvas    画布
     * @param lineStart 开始
     * @param lineEnd   结束
     * @param line      内容
     * @param lineWidth 行宽
     * @param isScale   是否需要缩放(行两端对齐)
     */
    private void drawSelBg(Canvas canvas, int lineStart, int lineEnd, String line, float lineWidth, boolean isScale) {

        if (mSelectionInfo != null) {
            // 设置被选中的背景色
            getPaint().setColor(ContextCompat.getColor(getContext(), R.color.essay_sel_color));

            if (mSelectionInfo.mStart <= lineStart && lineEnd <= mSelectionInfo.mEnd) {             // 如果是全行被选中
                canvas.drawRect(new RectF(0,
                                mLineY - DisplayUtil.dp2px(15),
                                mViewWidth,
                                mLineY + DisplayUtil.dp2px(4)),
                        getPaint());
            } else if (lineStart <= mSelectionInfo.mStart && mSelectionInfo.mEnd <= lineEnd) {      // 如果在行内被选中
                canvas.drawRect(new RectF(getIndex(mSelectionInfo.mStart, canvas, lineStart, line, lineWidth),
                                mLineY - DisplayUtil.dp2px(15),
                                getIndex(mSelectionInfo.mEnd, canvas, lineStart, line, lineWidth),
                                mLineY + DisplayUtil.dp2px(4)),
                        getPaint());
            } else if (lineStart <= mSelectionInfo.mStart && mSelectionInfo.mStart <= lineEnd
                    && mSelectionInfo.mEnd >= lineEnd) {                                            // 如果从中间到最后被选中
                canvas.drawRect(new RectF(
                                getIndex(mSelectionInfo.mStart, canvas, lineStart, line, lineWidth),
                                mLineY - DisplayUtil.dp2px(15),
                                mViewWidth,
                                mLineY + DisplayUtil.dp2px(4)),
                        getPaint());
            } else if (mSelectionInfo.mStart <= lineStart && mSelectionInfo.mEnd <= lineEnd
                    && mSelectionInfo.mEnd >= lineStart) {                                          // 如果开始到中间被选中
                canvas.drawRect(new RectF(
                                0,
                                mLineY - DisplayUtil.dp2px(15),
                                getIndex(mSelectionInfo.mEnd, canvas, lineStart, line, lineWidth),
                                mLineY + DisplayUtil.dp2px(4)),
                        getPaint());
            }

            // 画笔设置成黑色
            getPaint().setColor(ContextCompat.getColor(getContext(), R.color.black));
        }
    }

    /**
     * 如果此行要伸缩，就再这里画
     *
     * @param canvas    画布
     * @param lineStart 行首
     * @param line      内容
     * @param lineWidth 行宽
     * @param lineModel model
     */
    private void drawScaledText(Canvas canvas, int lineStart, String line, float lineWidth, TxtModel.LineModel lineModel) {

        float x = 0;
        float d = 0;

        if (lineModel != null && lineModel.d > 0) {
            d = lineModel.d;
        } else {
            d = (mViewWidth - lineWidth) / line.length() - 1;
            lineModel.d = d;
        }

        TxtModel.CharModel charModel = null;
        for (int i = 0; i < line.length(); i++) {
            if (i < lineModel.charModels.size()) {
                charModel = lineModel.charModels.get(i);
                char[] c = {line.charAt(i)};
                canvas.drawText(c, 0, 1, x, mLineY, getPaint());
                x = charModel.x;
            } else {
                charModel = new TxtModel.CharModel();
                lineModel.charModels.add(charModel);
                char[] c = {line.charAt(i)};
                float cw = getDesiredWidth(String.valueOf(c), getPaint());
                canvas.drawText(c, 0, 1, x, mLineY, getPaint());

                x += cw + d;

                charModel.x = x;
            }
        }
    }

    //计算选中文字开始字符的左偏量
    private float getIndex(int index, Canvas canvas, int lineStart, String line, float lineWidth) {
        float x = 0;

        float d = (mViewWidth - lineWidth) / line.length() - 1;

        if ((mViewWidth - lineWidth) > DisplayUtil.dp2px(100)) {
            d = 0;
        }
        index = index - lineStart;

        for (int i = 0; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = getDesiredWidth(c, getPaint());
            if (index == i) {
                break;
            }
            x += cw + d;
        }
        return x;
    }

    private boolean needScale(String line, int lineEnd) {
        if (line != null) {
            if (line.length() == 0 || (text != null && lineEnd == text.length())) {
                return false;
            } else {
                return line.charAt(line.length() - 1) != '\n';
            }
        }
        return false;
    }

    private static float getDesiredWidth(CharSequence c, TextPaint paint) {
        return StaticLayout.getDesiredWidth(c, paint);
    }


    private static float getDesiredWidth(int type, String text, int lineStart, int lineEnd, TextPaint paint) {
        return StaticLayout.getDesiredWidth(text, lineStart, lineEnd, paint);
    }


    /**
     * 绘制下划线线接口
     */
    public interface DrawLine {
        void drawCusHeiLine(CusAlignText cCusAlignText, Canvas canvas, int mLineY, int lineStart, int lineEnd, String line, float lineWidth, boolean isScale);

        void drawCusHeiLine(CusAlignText cCusAlignText, Canvas canvas, ExerciseTextView textView, Layout layout);
    }
}
