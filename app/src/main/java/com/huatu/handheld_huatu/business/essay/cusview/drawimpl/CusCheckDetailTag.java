package com.huatu.handheld_huatu.business.essay.cusview.drawimpl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.cusview.bean.TxtModel;
import com.huatu.handheld_huatu.business.essay.cusview.bean.UnderLine;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 实现了 CusAlignText.DrawLine 划线接口，所以 CusAlignText 的下划线、底纹绘制会交给这里
 * 批改报告，画得分、画波浪线、画红黄底纹
 */
public class CusCheckDetailTag implements CusAlignText.DrawLine {

    public static final String TAG = "CusAlignText";

    public Context mContext;
    private ExerciseTextView mExerciseTextView;
    private int mLineY;
    private int mViewWidth;                                                      // TextView 的宽度
    protected Paint localPaint;
    protected Path localPath = new Path();
    private ArrayList<Integer> indexList = new ArrayList<>();
    private TxtModel txtModel = new TxtModel();

    int wspace = 60;                                                            // 最小波浪线的宽度
    float dx = 15.0F;                                                           // 波浪线 x坐标 半波长的偏移量
    float dx2 = 30.0F;                                                          // 波浪线 x坐标 一个波长的偏移量
    float yheight = 11;                                                         // 波浪线 y 的偏移量
    boolean isSingle;
    boolean isScale;
    protected int yOffset = 7;
    private int xOffset = 15;
    protected int yOffsetz = 10;

    ArrayList<UnderLine> singleLines = new ArrayList<>();
    ArrayList<UnderLine> multLines = new ArrayList<>();

    ArrayList<Integer> singleChar = new ArrayList<>();
    ArrayList<Integer> multChar = new ArrayList<>();

    int singlePos;
    int multPos;
    public String text;                                                         // TextView 的内容
    private int lineheight = 0;
    protected Typeface fromAsset;

    public CusCheckDetailTag(CusAlignText cCusAlignText) {
        if (cCusAlignText != null) {
            initData(cCusAlignText.mExerciseTextView);
            cCusAlignText.setDrawLine(this);
        }
    }

    public void setUnderLine(ArrayList<UnderLine> singleLines, ArrayList<UnderLine> multLines, ArrayList<Integer> singleChar, ArrayList<Integer> multChar) {
        this.singleLines = singleLines;
        this.multLines = multLines;
        this.singleChar = singleChar;
        this.multChar = multChar;
        if (mExerciseTextView != null) {
            mExerciseTextView.invalidate();
        }
    }

    public void initData(ExerciseTextView textView) {
        if (textView != null) {
            mContext = textView.getContext();
            mExerciseTextView = textView;
            initlocalPaint(mContext);
            fromAsset = Typeface.createFromAsset(mContext.getAssets(), "font/Heavy.ttf");
        }
    }

    private void initlocalPaint(Context cxt) {
        if (cxt != null && localPaint == null) {
            localPaint = new Paint();
            localPaint.setStyle(Paint.Style.STROKE);
            localPaint.setAntiAlias(true);
            localPaint.setStrokeWidth(2);
            localPaint.setColor(ContextCompat.getColor(cxt, R.color.common_style_text_color));
            localPaint.setDither(true);
            localPaint.setTextSize(DisplayUtil.dp2px(8));
        }
    }

    public Context getContext() {
        return mContext;
    }

    TextPaint mTextPaint;

    public TextPaint getPaint() {
        if (mTextPaint == null) {
            if (mExerciseTextView != null) {
                mTextPaint = mExerciseTextView.getPaint();
            }
        }
        return mTextPaint;
    }

    class Data {
        float xstart;
        float ystart;

        float xend;
        float yend;

        public String score;
        public int seq;

        @Override
        public String toString() {
            return "Data{" +
                    "xstart=" + xstart +
                    ", ystart=" + ystart +
                    ", xend=" + xend +
                    ", yend=" + yend +
                    ", score='" + score + '\'' +
                    '}';
        }
    }


    List<Integer> st_ed_ls = new ArrayList<>();     // 记录 TextView 每行的 开始、结束 位置 使用连续的两个数
    // 由于支持屏幕旋转，所以把记录信息的东西去掉每次重绘，就重新计算
//    List<Data> dSingleBg = new ArrayList<>();       // 记录黄底纹的位置 xstart ystart xend yend
//    List<Data> dMultBg = new ArrayList<>();         // 记录红底纹的位置
//    List<Data> dHeiLine = new ArrayList<>();        // 记录波浪线的位置

    float TextSize;                                 // TextView 的默认字大小 px

    @Override
    public void drawCusHeiLine(CusAlignText cCusAlignText, Canvas canvas, ExerciseTextView textView, Layout layout) {

        st_ed_ls.clear();           // 为了旋转屏幕重绘，所以把记录的数据清除掉
        for (int i = 0; i < layout.getLineCount(); i++) {
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            st_ed_ls.add(lineStart);                                    // 记录 TextView 每行的 开始、结束 字的index位置 使用连续的两个数
            st_ed_ls.add(lineEnd);
        }

        if (textView != null) {
            mViewWidth = textView.getMeasuredWidth();                   // 得到TextView的宽度
        }

        if (text == null) {
            if (cCusAlignText != null) {
                text = cCusAlignText.text;                                  // TextView 的内容
            }
        }

        if (lineheight == 0 && textView != null) {
//            lineheight = textView.getLineHeight();                          // TextView 的标准行高   px
            lineheight = textView.getMeasuredHeight() / layout.getLineCount();       // 如果修改了系统字体，行高会变，内容绘制就会超出控件高度。所以由控件的高度/行数，反算行高。减30是因为还要留出最后一行画波浪线
            TextSize = textView.getTextSize();                              // TextView 的默认字大小 px
        }

        if (st_ed_ls.size() == 0 || TextUtils.isEmpty(text)) {
            return;
        }

        drawBg(0, canvas, singleChar);                       // 画黄底纹
        drawBg(1, canvas, multChar);                           // 画红底纹

        drawLine(1, canvas, singleLines);                     // 划线

        getPaint().setColor(ContextCompat.getColor(getContext(), R.color.black));
        getPaint().setAlpha(255);
    }

    /**
     * 画文字底纹
     */
    private void drawBg(int type, Canvas canvas, ArrayList<Integer> singleChar) {
        getPaint().setAlpha(85);
        if (type == 0) {
            getPaint().setColor(ContextCompat.getColor(getContext(), R.color.essay_height_light_color));
        } else {
            getPaint().setColor(ContextCompat.getColor(getContext(), R.color.essay_sel_color));
        }

        if (singleChar != null && singleChar.size() > 0) {
            for (int i = 0; i < singleChar.size() - 1; ) {
                int start = singleChar.get(i);
                int end = singleChar.get(i + 1);
                List<Data> varAry = getDrawData(0, canvas, start, end, null,0);
                i++;
                i++;
            }
        }
        getPaint().setAlpha(255);
    }

    /**
     * 画文字的底纹
     */
    private void drawCus_dSingleBg(Canvas canvas, List<Data> dSingleBg) {
        if (canvas != null) {
            for (Data data : dSingleBg) {
                if (data != null) {
                    canvas.drawRect(new RectF(data.xstart, data.ystart - DisplayUtil.dp2px(15), data.xend, data.yend + DisplayUtil.dp2px(4)), getPaint());
                }
            }
        }
    }

    /**
     * 划线
     */
    protected void drawLine(int type, Canvas canvas, ArrayList<UnderLine> singleLines) {
        if (localPaint != null) {
            localPaint.setColor(ContextCompat.getColor(getContext(), R.color.common_style_text_color));
        }
        if (singleLines != null && singleLines.size() > 0) {
            for (int i = 0; i < singleLines.size(); ) {
                int start = 0;
                int end = 0;
                UnderLine lineData = singleLines.get(i);
                if (lineData != null) {
//                        Log.d("UnderLine", lineData.toString());
                    start = lineData.start;
                    end = lineData.end;
                    List<Data> varAry = getDrawData(1, canvas, start, end, lineData.score,lineData.seq);
                }
                i++;
            }
        }
    }

    /**
     * 划线
     */
    private void drawCus_dHeiLine(Canvas canvas, List<Data> dSingleBg) {
        if (canvas != null) {
            for (Data data : dSingleBg) {
                if (data != null) {
                    drawCus_dHeiLine(canvas, data,false);
                }
            }
        }
    }

    /**
     * 根据文字的始末位置，返回需要画的底纹或线的位置的List<Data>，并划线或者底纹
     */
    protected List<Data> getDrawData(int type, Canvas canvas, int start, int end, String ex,int seq) {
        if (start >= 0 && end >= 0) {
            mLineY = 0;
            mLineY += TextSize;
            List<Data> varList = new ArrayList<>();
            for (int i = 0; i < st_ed_ls.size() - 1; ) {                                            // 循环每一行
                int startLine = st_ed_ls.get(i);
                int endLine = st_ed_ls.get(i + 1);
                if (startLine > end) {                                                              // 如果第一行第一个字在 重点词结束位置之后 直接跳出
                    break;
                }
                if (startLine <= start && endLine >= end) {                                         // 如果 重点词 都在这一行内
                    Data d = new Data();
                    d.xstart = getX_Sl(mViewWidth, start, text, startLine, endLine);
                    d.ystart = mLineY;
                    d.xend = getX_Sl(mViewWidth, end, text, startLine, endLine);
                    d.yend = mLineY;
                    d.seq=seq;
                    if (ex != null) {
                        d.score = ex;
                    }
                    varList.add(d);
                    if (type == 0) {
                        drawCus_dSingleBg(canvas, d);
                    } else {
                        drawCus_dHeiLine(canvas, d,true);
                    }
                } else if (startLine <= start && start <= endLine) {                                // 如果 重点词 第一个字在此行
                    Data d = new Data();
                    d.seq=seq;
                    d.xstart = getX_Sl(mViewWidth, start, text, startLine, endLine);
                    d.ystart = mLineY;
                    d.xend = getX_Sl(mViewWidth, endLine, text, startLine, endLine);
                    d.yend = mLineY;
                    varList.add(d);
                    if (type == 0) {
                        drawCus_dSingleBg(canvas, d);
                    } else {
                        drawCus_dHeiLine(canvas, d,false);
                    }
                } else if (startLine <= end && end <= endLine) {                                    // 如果 重点词 最后一个字在此行
                    Data d = new Data();
                    d.seq=seq;
                    d.xstart = getX_Sl(mViewWidth, startLine, text, startLine, endLine);
                    d.ystart = mLineY;
                    d.xend = getX_Sl(mViewWidth, end, text, startLine, endLine);
                    d.yend = mLineY;
                    if (ex != null) {
                        d.score = ex;
                    }
                    varList.add(d);
                    if (type == 0) {
                        drawCus_dSingleBg(canvas, d);
                    } else {
                        drawCus_dHeiLine(canvas, d,true);
                    }
                } else if (startLine >= start && end >= endLine) {                                  // 如果此行全在 重点词 中
                    Data d = new Data();
                    d.seq=seq;
                    d.xstart = getX_Sl(mViewWidth, startLine, text, startLine, endLine);
                    d.ystart = mLineY;
                    d.xend = getX_Sl(mViewWidth, endLine, text, startLine, endLine);
                    d.yend = mLineY;
                    varList.add(d);
                    if (type == 0) {
                        drawCus_dSingleBg(canvas, d);
                    } else {
                        drawCus_dHeiLine(canvas, d,false);
                    }
                }
                mLineY += lineheight;
                i++;
                i++;
            }
            return varList;
        }
        return null;
    }

    /**
     * 画底纹
     */
    private void drawCus_dSingleBg(Canvas canvas, Data data) {
        if (canvas != null) {
            if (data != null) {
                canvas.drawRect(new RectF(data.xstart, data.ystart - DisplayUtil.dp2px(15), data.xend, data.yend + DisplayUtil.dp2px(4)), getPaint());
            }
        }
    }

    /**
     * 划线
     * i4 是开始x位置
     * i5 是结束x位置
     * i6 是需要划线的总长度
     * i7 是划线的波长 整数
     */
    protected void drawCus_dHeiLine(Canvas canvas, Data data,boolean islineEnd) {
        if (canvas != null) {
            if (data != null) {
                localPaint.setColor(Color.parseColor("#FF6D73"));
                localPaint.setStyle(Paint.Style.STROKE);
//                Log.d("dSingleBg", data.toString());
                localPaint.setStrokeWidth(4);
                int i4 = (int) data.xstart;                                                         // 开始处 x坐标
                int i5 = (int) data.xend;                                                           // 结束处 x坐标
                int i6 = (int) (i5 - i4);                                                                   // 划线总宽度
                if (i6 != 0) {
                    localPath.reset();
                    localPath.moveTo(i4, data.ystart + DisplayUtil.dp2px(yOffset));              // 路径挪到开始 y + 偏移量 的位置
                    int i7 = ((i6) / wspace);                                                       // 总宽度/固定值（不知道是什么） 固定值可能是波浪线的最小循环长度 一个波长 i7 一共有几个整数波长
                    int sm = i6 % wspace;                                                           // 总宽度/固定值 的余数      sm 是整数波长之后的余数
//                    if (i7 == 0) {                                                                  // 如果需要划线的长度不够一个波长，就让它等于 1
//                        i7 = 1;
//                    }
                    for (int i9 = 0; i9 < i7 + 1; i9++) {
                        // quadTo 用于绘制圆滑曲线，即贝塞尔曲线。mPath.quadTo(x1, y1, x2, y2) (x1,y1) 为控制点，(x2,y2)为结束点。
                        localPath.rQuadTo(dx, yheight, dx2, 0.0F);                             // 往下凹的半波长
//                        if (i9 == (i7 - 1)) {
//                            if (data.score == null || i7 < 3 || sm > 15) {                          // 最后一个半波长
//                                localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
//                            }
//                        } else {
                        localPath.rQuadTo(dx, -yheight, dx2, 0.0F);                        // 往上凸的半波长
//                        }
                    }
//                    if (data.score == null && i7 > 1) {
//                        if (sm > 30) {
//                            localPath.rQuadTo(dx, yheight, dx2, 0.0F);
//                        }
//                    }
                    if (!localPath.isEmpty() && localPaint != null) {
                        canvas.drawPath(localPath, localPaint);
                    }

                    // 画个白的背景，遮挡 分数后的波浪线 或者 遮挡多余的线
                    localPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
                    localPaint.setStyle(Paint.Style.FILL);
                    float left;
                    float top = data.ystart + DisplayUtil.dp2px(yOffset) - yheight;
                    float right = data.xend + wspace;
                    float bottom = data.ystart + DisplayUtil.dp2px(yOffset) + yheight;
                    if (data.score != null) {
                        left = data.xend - DisplayUtil.dp2px(5);
                    } else {
                        left = data.xend;
                    }
                    RectF rectF = new RectF(left, top, right, bottom);
                    canvas.drawRect(rectF, localPaint);
                    localPaint.setColor(ContextCompat.getColor(getContext(), R.color.common_style_text_color));

                    Typeface oldType = localPaint.getTypeface();
                    float oldSize = localPaint.getTextSize();
                    // 字体
                    localPaint.setTypeface(fromAsset);
                    localPaint.setTextSize(DisplayUtil.dp2px(8));

                    localPaint.setStrokeWidth(1);

                    if (data.score != null) {
                        // 最后的画分数
//                        canvas.drawText(data.score, i5 - DisplayUtil.dp2px(xOffset), data.ystart + DisplayUtil.dp2px(yOffsetz), localPaint);
                        canvas.drawText(data.score, left + 1, data.ystart + DisplayUtil.dp2px(yOffsetz), localPaint);
                    }

                    localPaint.setStyle(Paint.Style.STROKE);
                    localPaint.setTypeface(oldType);
                    localPaint.setTextSize(oldSize);

                    localPaint.setStrokeWidth(2);
                }
            }
        }
    }

    /**
     * 得到需要画背景的 重点词 的背景的 左上角的 x坐标
     */
    private float getX_Sl(int viewWidth, int index, String text, int lineStart, int lineEnd) {
        float x = 0;
        float d = 0;
        if (text != null && lineStart < text.length() && lineEnd <= text.length()) {
            String line = text.substring(lineStart, lineEnd);
            if (line != null && line.length() > 0) {
                if (needScale(line, lineEnd)) {                                                     // 本行最后一个字符不是换行
                    d = (viewWidth - getDesiredWidth(0, text, lineStart, lineEnd, getPaint())) / line.length() - 1;
                } else {
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
            }
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

    static HashMap StrMap = new HashMap();

    private static float getDesiredWidth(int type, String text, int lineStart, int lineEnd, TextPaint paint) {
        return StaticLayout.getDesiredWidth(text, lineStart, lineEnd, paint);
    }

    // ---------------------------------------------------------------------一下代码都没用
    boolean open;

    @Override
    public void drawCusHeiLine(CusAlignText cCusAlignText, Canvas canvas, int mLineY, int lineStart, int lineEnd, String line, float width, boolean isScale) {

        /**
         *  singleChar  multChar  没有执行open没有赋值的地方
         */

        if (!open) {
            return;
        }

        this.mLineY = mLineY;
        this.isScale = isScale;
//        LogUtils.d(TAG, "  ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//        LogUtils.d(TAG, "lineStart:  " + lineStart+"    lineEnd:  " + lineEnd);
//        LogUtils.d(TAG," onDraw－－－－－－－line" + " line  "+line);

        dx = 15.0F;
        dx2 = 30.0F;
        yheight = 11;
        wspace = 60;
        isSingle = true;

        if (indexList != null && singleChar != null && singleChar.size() > 0) {
            indexList.clear();
            indexList.addAll(singleChar);
            drawHeightBg(canvas, lineStart, lineEnd, line, width, isScale);
        }


        isSingle = false;
        dx = 10.0F;
        dx2 = 20.0F;
        yheight = 13;
        wspace = 40;

        if (indexList != null && multChar != null && multChar.size() > 0) {
            indexList.clear();
            indexList.addAll(multChar);
            drawHeightBg(canvas, lineStart, lineEnd, line, width, isScale);
        }


        /**
         * drawUnderLine
         */


        this.mLineY = mLineY;
        dx = 15.0F;
        dx2 = 30.0F;
        yheight = 11;
        wspace = 60;
        isSingle = true;
        yOffset = 7;
        yOffsetz = 9;

        if (indexList != null && singleLines != null && singleLines.size() > 0) {
            indexList.clear();
            int size = singleLines.size();
            for (int i = 0; i < size; i++) {
                UnderLine underLine = singleLines.get(i);
                indexList.add(underLine.start);
                indexList.add(underLine.end);
            }
            drawUnderLine(canvas, lineStart, lineEnd, line, width, isScale);
        }


        isSingle = false;
        dx = 10.0F;
        dx2 = 20.0F;
        yheight = 0;
        wspace = 40;
        yOffset = 7;
        yOffsetz = 9;

        if (indexList != null && multLines != null && multLines.size() > 0) {
            indexList.clear();
            int size = multLines.size();
            for (int i = 0; i < size; i++) {
                UnderLine underLine = multLines.get(i);
                indexList.add(underLine.start);
                indexList.add(underLine.end);
            }
            drawUnderLine(canvas, lineStart, lineEnd, line, width, isScale);
        }

    }

    private void drawHeightBg(Canvas canvas, int lineStart, int lineEnd, String line, float lineWidth, boolean isScale) {
        if (indexList != null) {
            if (indexList.size() > 0) {
                localPath.reset();
                int startIndex = indexedBinarySearch(indexList, lineStart);
                int endIndex = indexedBinarySearch(indexList, lineEnd);
                if (mViewWidth == 0) {
                    if (mExerciseTextView != null) {
                        mViewWidth = mExerciseTextView.getMeasuredWidth();
                    }
                }
                getPaint().setAlpha(85);
                if (isSingle) {
                    getPaint().setColor(ContextCompat.getColor(getContext(), R.color.essay_height_light_color));
                } else {
                    getPaint().setColor(ContextCompat.getColor(getContext(), R.color.essay_sel_color));
                }
                float right = 0;
                if (!isScale) {
                    right = lineWidth;
                } else {
                    right = mViewWidth - DisplayUtil.dp2px(8);
                }
                if (Math.abs(endIndex) - Math.abs(startIndex) <= 1) {
                    if (startIndex != 0 && Math.abs(startIndex) % 2 != 0 && Math.abs(endIndex) % 2 != 0) {
//                        if (localPath.isEmpty()) {
//                            localPath.moveTo(0, mLineY + DisplayUtil.dp2px(4));
//                        }
                        int i6 = (int) (-0 + right);

                        if (endIndex > 0) {
                            int startIa = Math.abs(startIndex) - 1;
                            if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                                int mStart = indexList.get(startIa);
                                int mEnd = indexList.get(startIa + 1);
                                i6 = (int) lineWidth;
                            }
                        } else {
                            i6 = (int) (-0 + right);
                        }
                        int i7 = 0;
                        if (!isScale) {
                            i7 = i6 / wspace;
                        } else {
                            i7 = i6 / wspace + 1;
                        }
                        canvas.drawRect(new RectF(0, mLineY - DisplayUtil.dp2px(15), i6, mLineY + DisplayUtil.dp2px(4)), getPaint());

//                        for (int i9 = 0; i9 < i7; i9++)
//                        {
//                            localPath.rQuadTo(dx, yheight, dx2, 0.0F);
//                            localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
//                        }
                    } else if (Math.abs(startIndex) % 2 != 0 && Math.abs(endIndex) % 2 == 0) {
                        int startIa = Math.abs(startIndex) - 1;
                        if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                            int mStart = indexList.get(startIa);
                            int mEnd = indexList.get(startIa + 1);
//                            if (localPath.isEmpty()) {
//                                localPath.moveTo(0, mLineY + DisplayUtil.dp2px(4));
//                            }
                            int i6 = (int) (-0 + getIndex(mEnd, canvas, lineStart, line, lineWidth));
                            int i7 = (i6 / wspace);
//                            for (int i9 = 0; i9 < i7; i9++) {
//                                localPath.rQuadTo(dx, yheight, dx2, 0.0F);
//                                localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
//                            }

                            canvas.drawRect(new RectF(0, mLineY - DisplayUtil.dp2px(15), i6, mLineY + DisplayUtil.dp2px(4)), getPaint());

                        }

                    } else if (Math.abs(startIndex) % 2 == 0 && Math.abs(endIndex) % 2 != 0) {
                        int startIa = Math.abs(endIndex) - 1;
                        if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                            int mStart = indexList.get(startIa);
                            int mEnd = indexList.get(startIa + 1);
//                            if (localPath.isEmpty()) {
//                                localPath.moveTo(getIndex(mStart, canvas, lineStart, line, lineWidth), mLineY +
//                                        DisplayUtil.dp2px(4));
//                            }
                            int i6 = (int) (right - getIndex(mStart, canvas, lineStart, line, lineWidth));
                            int i7 = i6 / wspace;
//                            for (int i9 = 0; i9 < i7; i9++) {
//                                localPath.rQuadTo(dx, yheight, dx2, 0.0F);
//                                localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
//                            }

                            canvas.drawRect(new RectF(getIndex(mStart, canvas, lineStart, line, lineWidth), mLineY - DisplayUtil.dp2px(15), right, mLineY + DisplayUtil.dp2px(4)), getPaint());
                        }

                    } else {
                        if (Math.abs(endIndex) - Math.abs(startIndex) > 1 || (endIndex == startIndex)) {
                            int startIa = Math.abs(startIndex);
                            if (startIa < 0) {
                                startIa = 0;
                            }
                            if (startIa > indexList.size() - 1) {
                                startIa = indexList.size() - 1;
                            }
                            int endIa = Math.abs(endIndex);
                            if (endIa > indexList.size() - 1) {
                                endIa = indexList.size() - 1;
                            }
                            if (startIa >= 0 && startIa < indexList.size() && (endIa) < indexList.size()) {
                                for (int i = startIa; i < endIa; ) {
                                    int mStart = indexList.get(i);
                                    int mEnd = indexList.get(i + 1);
                                    int i4 = (int) getIndex(mStart, canvas, lineStart, line, lineWidth);
                                    int i5 = (int) getIndex(mEnd, canvas, lineStart, line, lineWidth);
                                    localPath.moveTo(i4, mLineY + DisplayUtil.dp2px(4));
                                    int i6 = i5 - i4;
                                    int i7 = i6 / wspace;
                                    if (i7 == 0) {
                                        i7 = 1;
                                    }
                                    for (int i9 = 0; i9 < i7; i9++) {
                                        localPath.rQuadTo(dx, yheight, dx2, 0.0F);
                                        localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
                                    }
                                    i++;
                                    i++;
                                }

                            }
                        }
                    }
                } else {
                    if (Math.abs(endIndex) - Math.abs(startIndex) > 1) {
                        int startIa = Math.abs(startIndex);
                        if (startIa < 0) {
                            startIa = 0;
                        }
                        int endIa = Math.abs(endIndex);
                        if (endIa > indexList.size() - 1) {
                            endIa = indexList.size() - 1;
                        }
                        if (startIa >= 0 && startIa < indexList.size() && (endIa) < indexList.size()) {
                            for (int i = startIa; i < endIa; ) {
                                int mStart = 0;
                                int mEnd = 0;
                                if (i == startIa && startIa % 2 == 1) {
                                    mEnd = indexList.get(i);
                                } else {
                                    mStart = indexList.get(i);
                                    mEnd = indexList.get(i + 1);
                                }
                                int i4 = 0;
                                if (i == startIa && startIa % 2 == 1) {
                                    i4 = 0;
//                                    localPath.moveTo(i4, mLineY + DisplayUtil.dp2px(4));
                                    i++;
                                } else {
                                    i4 = (int) getIndex(mStart, canvas, lineStart, line, lineWidth);
//                                    localPath.moveTo(i4, mLineY + DisplayUtil.dp2px(4));
                                    i++;
                                    i++;
                                }
                                int i5 = (int) getIndex(mEnd, canvas, lineStart, line, lineWidth);
                                int i6 = i5 - i4;
                                int i7 = i6 / wspace;
                                if (i7 == 0) {
                                    i7 = 1;
                                }
//                                for (int i9 = 0; i9 < i7; i9++) {
//                                    localPath.rQuadTo(dx, yheight, dx2, 0.0F);
//                                    localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
//                                }

                                canvas.drawRect(new RectF(i4, mLineY - DisplayUtil.dp2px(15), i5, mLineY + DisplayUtil.dp2px(4)), getPaint());

                            }
                        }
                    }
                }
            }
            getPaint().setAlpha(255);
            getPaint().setColor(ContextCompat.getColor(getContext(), R.color.black));
        }
    }

    private void drawUnderLine(Canvas canvas, int lineStart, int lineEnd, String line, float lineWidth, boolean isScale) {
        if (indexList != null) {
            if (indexList.size() > 0) {
                localPath.reset();
                int startIndex = indexedBinarySearch(indexList, lineStart);
                int endIndex = indexedBinarySearch(indexList, lineEnd);
                if (mViewWidth == 0) {
                    if (mExerciseTextView != null) {
                        mViewWidth = mExerciseTextView.getMeasuredWidth();
                    }
                }
                float right = 0;
                if (!isScale) {
                    right = lineWidth;
                } else {
                    right = mViewWidth - DisplayUtil.dp2px(8);
                }
                if (Math.abs(endIndex) - Math.abs(startIndex) <= 1) {
//                    LogUtils.d(TAG, "mLineY 1:  " + mLineY +" startIndex: "+startIndex+" endIndex: "+endIndex);
                    if (startIndex != 0 && Math.abs(startIndex) % 2 != 0 && Math.abs(endIndex) % 2 != 0) {
                        if (localPath.isEmpty()) {
                            localPath.moveTo(0, mLineY + DisplayUtil.dp2px(yOffset));
                        }
                        int i6 = (int) (-0 + right);
                        if (endIndex > 0) {
                            int startIa = Math.abs(startIndex) - 1;
                            if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                                int mStart = indexList.get(startIa);
                                int mEnd = indexList.get(startIa + 1);
                                i6 = (int) lineWidth;
                            }
                            setcolor(isSingle, localPaint);
                            if (isSingle) {
                                if (singlePos < singleLines.size()) {
                                    UnderLine var = singleLines.get(singlePos);
//                                    LogUtils.d(TAG, "only singlePos :" + singlePos  + " var.score : "+ var.score +" mLineY:" + mLineY+" i6: " + i6);
                                    if (var != null) {
                                        if (!TextUtils.isEmpty(var.score)) {
                                            singlePos++;
                                            if (singlePos == singleLines.size()) {
                                                singlePos = 0;
                                            }
                                            canvas.drawText(var.score, i6 - DisplayUtil.dp2px(7), mLineY + DisplayUtil.dp2px(yOffsetz), localPaint);
                                        }
                                    }
                                }
                            } else {
                                if (multPos < multLines.size()) {
                                    UnderLine var = multLines.get(multPos);
                                    if (var != null) {
                                        if (!TextUtils.isEmpty(var.score)) {
                                            multPos++;
                                            if (multPos == multLines.size()) {
                                                multPos = 0;
                                            }
                                            canvas.drawText(var.score, i6 - DisplayUtil.dp2px(7), mLineY + DisplayUtil.dp2px(yOffsetz), localPaint);
                                        }
                                    }
                                }
                            }
                        } else {
                            i6 = (int) (-0 + right);
                        }

                        int i7 = i6 / wspace;
//                        if (!isScale) {
//                            i7 = i6 / wspace;
//                        } else {
//                            i7 = i6 / wspace + 1;
//                        }
                        for (int i9 = 0; i9 < i7; i9++) {
                            localPath.rQuadTo(dx, yheight, dx2, 0.0F);
                            localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
                        }
                    } else if (Math.abs(startIndex) % 2 != 0 && Math.abs(endIndex) % 2 == 0) {
//                        LogUtils.d(TAG, "mLineY 2:  " + mLineY+" startIndex: "+startIndex+" endIndex: "+endIndex);
                        int startIa = Math.abs(startIndex) - 1;
                        if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                            int mStart = indexList.get(startIa);
                            int mEnd = indexList.get(startIa + 1);
                            if (localPath.isEmpty()) {
                                localPath.moveTo(0, mLineY + DisplayUtil.dp2px(yOffset));
                            }
                            int i6 = (int) (-0 + getIndex(mEnd, canvas, lineStart, line, lineWidth)) + DisplayUtil.dp2px(8);
                            int i7 = (i6 / wspace);
//                            if (!isScale) {
//                                i7 = i6 / wspace;
//                            } else {
//                                i7 = i6 / wspace + 1;
//                            }
                            for (int i9 = 0; i9 < i7; i9++) {
                                localPath.rQuadTo(dx, yheight, dx2, 0.0F);
                                localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
                            }
                            setcolor(isSingle, localPaint);
                            if (isSingle) {
                                if (singlePos < singleLines.size()) {
                                    UnderLine var = singleLines.get(singlePos);
//                                    LogUtils.d(TAG, "only singlePos :" + singlePos  + " var.score : "+ var.score +" mLineY:" + mLineY+" i6: " + i6);
                                    if (var != null) {
                                        if (!TextUtils.isEmpty(var.score)) {
                                            singlePos++;
                                            if (singlePos == singleLines.size()) {
                                                singlePos = 0;
                                            }
                                            canvas.drawText(var.score, i6 - DisplayUtil.dp2px(7), mLineY + DisplayUtil.dp2px(yOffsetz), localPaint);
                                        }
                                    }
                                }
                            } else {
                                if (multPos < multLines.size()) {
                                    UnderLine var = multLines.get(multPos);
                                    if (var != null) {
                                        if (!TextUtils.isEmpty(var.score)) {
                                            multPos++;
                                            if (multPos == multLines.size()) {
                                                multPos = 0;
                                            }
                                            canvas.drawText(var.score, i6 - DisplayUtil.dp2px(7), mLineY + DisplayUtil.dp2px(yOffsetz), localPaint);
                                        }
                                    }
                                }
                            }
                        }

                    } else if (Math.abs(startIndex) % 2 == 0 && Math.abs(endIndex) % 2 != 0) {
//                        LogUtils.d(TAG, "mLineY 3:  " + mLineY+" startIndex: "+startIndex+" endIndex: "+endIndex);
                        int startIa = Math.abs(endIndex) - 1;
                        if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                            int mStart = indexList.get(startIa);
                            int mEnd = indexList.get(startIa + 1);
                            if (localPath.isEmpty()) {
                                localPath.moveTo(getIndex(mStart, canvas, lineStart, line, lineWidth), mLineY +
                                        DisplayUtil.dp2px(yOffset));
                            }
                            int i6 = (int) (right - getIndex(mStart, canvas, lineStart, line, lineWidth)) + DisplayUtil.dp2px(8);
                            int i7 = i6 / wspace;
//                            if (!isScale) {
//                                i7 = i6 / wspace;
//                            } else {
//                                i7 = i6 / wspace + 1;
//                            }
                            for (int i9 = 0; i9 < i7; i9++) {
                                localPath.rQuadTo(dx, yheight, dx2, 0.0F);
                                localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
                            }
                        }

                    } else {
                        if (Math.abs(endIndex) - Math.abs(startIndex) > 1 || (endIndex == startIndex)) {
                            int startIa = Math.abs(startIndex);
                            if (startIa < 0) {
                                startIa = 0;
                            }
                            if (startIa > indexList.size() - 1) {
                                startIa = indexList.size() - 1;
                            }
                            int endIa = Math.abs(endIndex);
                            if (endIa > indexList.size() - 1) {
                                endIa = indexList.size() - 1;
                            }
                            if (startIa >= 0 && startIa < indexList.size() && (endIa) < indexList.size()) {
                                for (int i = startIa; i < endIa; ) {
                                    int mStart = indexList.get(i);
                                    int mEnd = indexList.get(i + 1);
                                    int i4 = (int) getIndex(mStart, canvas, lineStart, line, lineWidth);
                                    int i5 = (int) getIndex(mEnd, canvas, lineStart, line, lineWidth);
                                    localPath.moveTo(i4, mLineY + DisplayUtil.dp2px(yOffset));
                                    int i6 = i5 - i4;
                                    int i7 = i6 / wspace;
                                    if (i7 == 0) {
                                        i7 = 1;
                                    }
                                    for (int i9 = 0; i9 < i7; i9++) {
                                        localPath.rQuadTo(dx, yheight, dx2, 0.0F);
                                        localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
                                    }
                                    i++;
                                    i++;
                                }

                            }
                        }
                    }
                } else {
                    if (Math.abs(endIndex) - Math.abs(startIndex) > 1) {
//                        LogUtils.d(TAG, "mLineY 4:  " + mLineY+" startIndex: "+startIndex+" endIndex: "+endIndex);
                        int startIa = Math.abs(startIndex);
                        if (startIa < 0) {
                            startIa = 0;
                        }
                        int endIa = Math.abs(endIndex);
                        if (endIa > indexList.size() - 1) {
                            endIa = indexList.size() - 1;
                        }
                        if (startIa >= 0 && startIa < indexList.size() && (endIa) < indexList.size()) {
                            for (int i = startIa; i < endIa; ) {
                                int mStart = 0;
                                int mEnd = 0;
                                if (i == startIa && startIa % 2 == 1) {
                                    mEnd = indexList.get(i);
                                } else {
                                    mStart = indexList.get(i);
                                    mEnd = indexList.get(i + 1);
                                }

                                int i4 = 0;
                                if (i == startIa && startIa % 2 == 1) {
                                    i4 = 0;
                                    localPath.moveTo(i4, mLineY + DisplayUtil.dp2px(yOffset));
                                    i++;
                                } else {
                                    i4 = (int) getIndex(mStart, canvas, lineStart, line, lineWidth);
                                    if (mEnd - mStart > 5) {
                                        i4 += DisplayUtil.dp2px(5);
                                    }
                                    localPath.moveTo(i4, mLineY + DisplayUtil.dp2px(yOffset));
                                    i++;
                                    i++;
                                }
                                int i5 = (int) getIndex(mEnd, canvas, lineStart, line, lineWidth);
                                int i6 = i5 - i4;
                                int i7 = i6 / wspace;
//                                if (!isScale) {
//                                    i7 = i6 / wspace;
//                                } else {
//                                    i7 = i6 / wspace + 1;
//                                }
                                if (i7 == 0) {
                                    i7 = 1;
                                }
                                for (int i9 = 0; i9 < i7; i9++) {
                                    localPath.rQuadTo(dx, yheight, dx2, 0.0F);
                                    localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
                                }

                                if ((startIa % 2 == 1 || i % 2 == 0)) {
                                    if (mEnd <= lineEnd) {
                                        setcolor(isSingle, localPaint);
                                        if (isSingle) {
                                            if (singlePos < singleLines.size()) {
                                                UnderLine var = singleLines.get(singlePos);
//                                                LogUtils.d(TAG + "1", "mult singlePos :" + singlePos + " var.score : " + var.score + " mLineY:" + mLineY + " i6: " + i6);
                                                if (var != null) {
                                                    if (!TextUtils.isEmpty(var.score)) {
                                                        singlePos++;
                                                        if (singlePos == singleLines.size()) {
                                                            singlePos = 0;
                                                        }
                                                        canvas.drawText(var.score, i5 - DisplayUtil.dp2px(7), mLineY
                                                                + DisplayUtil.dp2px(yOffsetz), localPaint);
                                                    }
                                                }
                                            }
                                        } else {
                                            if (multPos < multLines.size()) {
                                                UnderLine var = multLines.get(multPos);
                                                if (var != null) {
                                                    if (!TextUtils.isEmpty(var.score)) {
                                                        multPos++;
                                                        if (multPos == multLines.size()) {
                                                            multPos = 0;
                                                        }
                                                        canvas.drawText(var.score, i5 - DisplayUtil.dp2px(7), mLineY
                                                                + DisplayUtil.dp2px(yOffsetz), localPaint);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (localPaint != null) {
                    localPaint.setColor(ContextCompat.getColor(getContext(), R.color.common_style_text_color));
                    if (!localPath.isEmpty()) {
                        canvas.drawPath(localPath, localPaint);
                    }
                }
            }
        }
    }

    public void setcolor(boolean isSingle, Paint localPaint) {
        if (localPaint != null) {
            if (isSingle) {
                localPaint.setColor(ContextCompat.getColor(getContext(), R.color.common_style_text_color));
            } else {
                localPaint.setColor(ContextCompat.getColor(getContext(), R.color.essay_sel_color_txt));
            }
        }
    }

    int indexedBinarySearch(ArrayList list, int key) {
        int low = 0;
        int high = list.size() - 1;
        int mid = 0;
        while (low <= high) {
            mid = (low + high) >>> 1;
            Comparable midVal = (Comparable) list.get(mid);
            int cmp = midVal.compareTo(key);
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low); // key found
    }

    private float getIndex(int index, Canvas canvas, int lineStart, String line, float lineWidth) {
        float x = 0;
//        if (isFirstLineOfParagraph(lineStart, line)) {
//            String blanks = "  ";
//            canvas.drawText(blanks, x, mLineY, getPaint());
//            float bw = getBwfloat(blanks, getPaint());
//            x += bw;
//            line = line.substring(4);
//        }
        float d = 0;
        if (isScale) {
            d = (mViewWidth - lineWidth) / line.length() - 1;
        } else {
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

    public float getBwfloat(String blanks, TextPaint paint) {
        float bw = 0;
        if (txtModel != null && txtModel.blanks_w > 0) {
            bw = txtModel.blanks_w;
        } else {
            bw = getDesiredWidth(blanks, getPaint());
            txtModel.blanks_w = bw;
        }
        return bw;
    }

    public boolean isFirstLineOfParagraph(int lineStart, String line) {
        return line.length() > 3 && line.charAt(0) == ' ' && line.charAt(1) == ' ';
    }
}
