package com.huatu.handheld_huatu.business.essay.cusview.drawimpl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayHelper;
import com.huatu.handheld_huatu.business.essay.cusview.bean.TxtModel;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 实现了 CusAlignText.DrawLine 划线接口，所以 CusAlignText 的下划线绘制会交给这里
 * 材料页划线
 */
public class CusDrawUnderLineEx implements CusAlignText.DrawLine {

    public static final String TAG = "CusAlignText";

    public Context mContext;
    private ExerciseTextView mExerciseTextView;
    private int mViewWidth;
    private Paint localPaint;
    private Path localPath = new Path();

    private ArrayList<Integer> indexListU;                          // 已划波浪线线的区域
    public ArrayList<Integer> indexList = new ArrayList<>();
    private ArrayList<String> uContentls = new ArrayList<>();       // <u> </u> 标签的内容，画黑线
    private ArrayList<Integer> indexListT = new ArrayList<>();      // <u>标签的区域
    private TxtModel txtModel = new TxtModel();

    float dx = 10.0F;
    float dx2 = 20.0F;
    float yheight = 9;
    int wspace = 40;
    private int yOffset = 6;
    private int type;               // 0、是画波浪线 1、画直线
    private boolean isScale;

    public CusDrawUnderLineEx(CusAlignText cCusAlignText) {
        if (cCusAlignText != null) {
            initData(cCusAlignText.mExerciseTextView);
            cCusAlignText.setDrawLine(this);
        }
    }

    public void setUnderLine(ArrayList<Integer> indexList, ArrayList<String> uContentls) {
        this.indexListU = indexList;
        this.uContentls = uContentls;
        if (mExerciseTextView != null) {
            mExerciseTextView.invalidate();
        }
    }

    public void initData(ExerciseTextView textView) {
        if (textView != null) {
            mContext = textView.getContext();
            mExerciseTextView = textView;
            initlocalPaint(mContext);
        }
    }

    private void initlocalPaint(Context cxt) {
        if (cxt != null && localPaint == null) {
            localPaint = new Paint();
            localPaint.setStyle(Paint.Style.STROKE);
            localPaint.setAntiAlias(true);
            localPaint.setStrokeWidth(2);
            localPaint.setColor(ContextCompat.getColor(cxt, R.color.common_style_text_color));
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

    @Override
    public void drawCusHeiLine(CusAlignText cCusAlignText, Canvas canvas, ExerciseTextView textView, Layout layout) {

    }

    @Override
    public void drawCusHeiLine(CusAlignText cCusAlignText, Canvas canvas,
                               int mLineY, int lineStart, int lineEnd, String line, float width, boolean isScale) {
        this.isScale = isScale;
        // 波浪线
        if (indexListU != null && indexListU.size() > 0) {
            if (indexListT != null && indexListT.size() > 0) {
                type = 0;       // 画波浪线
                yOffset = 6;
                if (mContext != null) {
                    localPaint.setColor(ContextCompat.getColor(mContext, R.color.common_style_text_color));
                }
            }
            drawUnderLine(indexListU, canvas, mLineY, lineStart, lineEnd, line, width, isScale);
        }

        // 解析<u>标签的区域，把String对应成index
        if (indexListT != null && indexListT.size() == 0 && cCusAlignText != null) {
            EssayHelper.uContentls2Indexls(cCusAlignText.text, uContentls, indexListT);
        }

        // 画<u>标签，黑线
        if (indexListT != null && indexListT.size() > 0) {
            type = 1;           // 画直线
            yOffset = 3;
            if (mContext != null) {
                localPaint.setColor(ContextCompat.getColor(mContext, R.color.black));
            }
            drawUnderLine(indexListT, canvas, mLineY, lineStart, lineEnd, line, width, isScale);
        }
    }

    /**
     * 画下划线 黑直线/波浪线
     * 使用 Path 划线
     */
    private void drawUnderLine(ArrayList<Integer> indexList, Canvas canvas, int mLineY,
                               int lineStart, int lineEnd, String line, float lineWidth, boolean isScale) {
        if (indexList != null) {
            if (indexList.size() > 0) {
                localPath.reset();
                // 划线indexList的下标，indexList存储需要划线的字符串的 开始位置 和 结束位置 两个连续的数字，所以划线字符串开始位置的index是偶数，结束index是奇数
                int startIndex = indexedBinarySearch(indexList, lineStart);     // 找的是 等于key 或 比key大最接近key的index
                int endIndex = indexedBinarySearch(indexList, lineEnd);
                if (mExerciseTextView != null) {
                    mViewWidth = mExerciseTextView.getMeasuredWidth();
                }
                float right = 0;
                if (!isScale) {
                    right = lineWidth;
                } else {
                    right = mViewWidth - DisplayUtil.dp2px(8);
                }
                if (Math.abs(endIndex) - Math.abs(startIndex) <= 1) {       // 如果两个下标连续或相等
                    if (startIndex != 0 && Math.abs(startIndex) % 2 != 0 && Math.abs(endIndex) % 2 != 0) {
                        // endIndex == startIndex 划线的句末在本行中
                        // 因为 Math.abs(startIndex) % 2 != 0 说明 startIndex 是下划线 结束位置index，同理Math.abs(endIndex) % 2 != 0
                        // 又因为 Math.abs(endIndex) - Math.abs(startIndex) <= 1 是连续的，或是同一个，那必须就是同一个位置且是划线句末，
                        // 因为此位置在句末之后，所以此行都划线
                        if (localPath.isEmpty()) {
                            localPath.moveTo(0, mLineY + DisplayUtil.dp2px(yOffset));
                        }
                        quadTo(0, mLineY + DisplayUtil.dp2px(yOffset), right, mLineY + DisplayUtil.dp2px(yOffset));
                    } else if (Math.abs(startIndex) % 2 != 0 && Math.abs(endIndex) % 2 == 0) {
                        // startIndex是句末 endIndex是句首（句首在行末后边）
                        // 只画句首到startIndex
                        int startIa = Math.abs(startIndex) - 1;
                        if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                            int mStart = indexList.get(startIa);
                            int mEnd = indexList.get(startIa + 1);
                            if (localPath.isEmpty()) {
                                localPath.moveTo(0, mLineY + DisplayUtil.dp2px(yOffset));
                            }
                            float index = getIndex(mLineY, mEnd, canvas, lineStart, line, lineWidth);
                            quadTo(0, mLineY + DisplayUtil.dp2px(yOffset), index, mLineY + DisplayUtil.dp2px(yOffset));
                        }

                    } else if (Math.abs(startIndex) % 2 == 0 && Math.abs(endIndex) % 2 != 0) {
                        // startIndex是句首 endIndex是句末
                        // 所以从句末的前一个位置画到句末
                        int startIa = Math.abs(endIndex) - 1;
                        if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                            int mStart = indexList.get(startIa);
                            int mEnd = indexList.get(startIa + 1);
                            if (localPath.isEmpty()) {
                                localPath.moveTo(getIndex(mLineY, mStart, canvas, lineStart, line, lineWidth), mLineY +
                                        DisplayUtil.dp2px(yOffset));
                            }

                            quadTo(getIndex(mLineY, mStart, canvas, lineStart, line, lineWidth), mLineY +
                                    DisplayUtil.dp2px(yOffset), right, mLineY + DisplayUtil.dp2px(yOffset));

                        }

                    } else {
                        // 这里貌似不可能发生
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
                                    int i4 = (int) getIndex(mLineY, mStart, canvas, lineStart, line, lineWidth);
                                    int i5 = (int) getIndex(mLineY, mEnd, canvas, lineStart, line, lineWidth);
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
                    if (Math.abs(endIndex) - Math.abs(startIndex) > 1) {    // 如果两个下标不连续，说明本行文字中好几段下划线
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
                                    // moveTo是移动到线开始
                                    localPath.moveTo(i4, mLineY + DisplayUtil.dp2px(yOffset));
                                    i++;
                                } else {
                                    i4 = (int) getIndex(mLineY, mStart, canvas, lineStart, line, lineWidth);
                                    localPath.moveTo(i4, mLineY + DisplayUtil.dp2px(yOffset));
                                    i++;
                                    i++;
                                }
                                int i5 = (int) getIndex(mLineY, mEnd, canvas, lineStart, line, lineWidth);
                                // quaTo是划线
                                quadTo(i4, mLineY + DisplayUtil.dp2px(yOffset), i5, mLineY + DisplayUtil.dp2px(yOffset));
                            }
                        }
                    }
                }
                if (!localPath.isEmpty()) {
                    canvas.drawPath(localPath, localPaint);
                }
            }
        }
    }

    private void quadTo(float x1, float y1, float x2, float y2) {
        float i6 = x2 - x1;
        if (type == 0) {        // 画波浪线
            if (i6 > 0) {
                int i7 = (int) i6 / wspace;
                if (i7 == 0) {
                    i7 = 1;
                }
                if (localPath != null) {
                    for (int i9 = 0; i9 < i7; i9++) {
                        localPath.rQuadTo(dx, yheight, dx2, 0.0F);
                        localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
                    }
                    localPath.rQuadTo(10.0F, yheight, 20.0F, 0.0F);
                }
            }
        } else if (type == 1) { // 画直线
            if (i6 > 0) {
                localPath.quadTo(x1, y1, x2, y2);
            }
        }
    }

    /**
     * 1 8 10 15
     * <p>
     * 3->1
     * <p>
     * 9->10
     * <p>
     * 找的是 等于key 或 比key大最接近key的index
     */
    private int indexedBinarySearch(ArrayList list, int key) {
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

    /**
     * 计算带下划线的字的 起始位置 或者 结束位置。
     *
     * @param mLineY    字的Y位置
     * @param index     划线的字的开始位置 或者 划线字之前的字的起始位置
     * @param canvas
     * @param lineStart 需要划线字的开始位置
     * @param line      划线的字内容
     * @param lineWidth 本行的宽度
     * @return
     */
    private float getIndex(int mLineY, int index, Canvas canvas, int lineStart, String line, float lineWidth) {
        float x = 0;
        // 是不是第一行，第一行的话得多加两个空格
        if (isFirstLineOfParagraph(lineStart, line)) {
            String blanks = "  ";
            canvas.drawText(blanks, x, mLineY, getPaint());
            float bw = getBwfloat(blanks, getPaint());
            x += bw;
            line = line.substring(3);
        }
        // 计算字之间的间隔
        float d = (mViewWidth - lineWidth) / (line.length() - 1);
        // 如果是半行 或者 不缩放 的话 字的间隔重置为 0
        if ((mViewWidth - lineWidth) > DisplayUtil.dp2px(20) && !isScale) {
            d = 0;
        }
        // 计算划线之前字的个数 或者 划线的字的个数
        index = index - lineStart;
        for (int i = 0; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = getDesiredWidth(c, getPaint());
            if (index == i) {
                break;
            }
            // 每计算一个字，长度就加上一个 间隔 和 一个字的宽度
            x += (cw + d);
        }
        return x;
    }

    private float getBwfloat(String blanks, TextPaint paint) {
        float bw = 0;
        if (txtModel != null && txtModel.blanks_w > 0) {
            bw = txtModel.blanks_w;
        } else {
            bw = getDesiredWidth(blanks, getPaint());
            txtModel.blanks_w = bw;
        }
        return bw;
    }

    private boolean isFirstLineOfParagraph(int lineStart, String line) {
        return line.length() > 3 && line.charAt(0) == ' ' && line.charAt(1) == ' ';
    }

    public boolean needScale(String line) {
        if (line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }

    private static float getDesiredWidth(CharSequence c, TextPaint paint) {
        return StaticLayout.getDesiredWidth(c, paint);
    }

    static HashMap StrMap = new HashMap();

    public static float getDesiredWidth(int type, String text, int lineStart, int lineEnd, TextPaint paint) {
        return StaticLayout.getDesiredWidth(text, lineStart, lineEnd, paint);
    }

}
