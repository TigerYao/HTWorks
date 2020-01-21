package com.huatu.test.drawimpl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;


import com.huatu.test.DisplayUtil;
import com.huatu.test.R;
import com.huatu.test.bean.TxtModel;
import com.huatu.test.custom.ExerciseTextView;

import java.util.ArrayList;
import java.util.HashMap;


public class CusDrawUnderLine implements CusAlignText.DrawLine {

    public static final String TAG = "CusAlignText";

    public Context mContext;
    public ExerciseTextView mExerciseTextView;
    public int mViewWidth;
    public Paint localPaint;
    public Path localPath = new Path();

    public ArrayList<Integer> indexList;
    public TxtModel txtModel = new TxtModel();


    float dx = 15.0F;
    float dx2 = 30.0F;
    float yheight = 11;
    int wspace = 60;

    public CusDrawUnderLine(CusAlignText cCusAlignText) {
        if (cCusAlignText != null) {
            initData(cCusAlignText.mExerciseTextView);
        }
    }

    public void setUnderLine(ArrayList<Integer> indexList) {
        this.indexList = indexList;
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

    public void initlocalPaint(Context cxt) {
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
    public void drawCusHeiLine(CusAlignText cCusAlignText, Canvas canvas, int mLineY,int lineStart, int lineEnd, String line,
                               float width, boolean isScale) {
        drawUnderLine(canvas,mLineY, lineStart, lineEnd, line, width, isScale);
    }

    public void drawUnderLine(Canvas canvas,int mLineY, int lineStart, int lineEnd, String line, float lineWidth, boolean
            isScale) {
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
                    if (startIndex != 0 && Math.abs(startIndex) % 2 != 0 && Math.abs(endIndex) % 2 != 0) {
                        if (localPath.isEmpty()) {
                            localPath.moveTo(0, mLineY + DisplayUtil.dp2px(4));
                        }
                        localPath.quadTo(0, mLineY + DisplayUtil.dp2px(4), right, mLineY + DisplayUtil.dp2px(4));
                    } else if (Math.abs(startIndex) % 2 != 0 && Math.abs(endIndex) % 2 == 0) {
                        int startIa = Math.abs(startIndex) - 1;
                        if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                            int mStart = indexList.get(startIa);
                            int mEnd = indexList.get(startIa + 1);
                            if (localPath.isEmpty()) {
                                localPath.moveTo(0, mLineY + DisplayUtil.dp2px(4));
                            }
                            localPath.quadTo(0, mLineY + DisplayUtil.dp2px(4), getIndex(mLineY,mEnd, canvas, lineStart, line,
                                    lineWidth), mLineY + DisplayUtil.dp2px(4));
                        }

                    } else if (Math.abs(startIndex) % 2 == 0 && Math.abs(endIndex) % 2 != 0) {
                        int startIa = Math.abs(endIndex) - 1;
                        if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                            int mStart = indexList.get(startIa);
                            int mEnd = indexList.get(startIa + 1);
                            if (localPath.isEmpty()) {
                                localPath.moveTo(getIndex(mLineY,mStart, canvas, lineStart, line, lineWidth), mLineY +
                                        DisplayUtil.dp2px(4));
                            }
                            localPath.quadTo(getIndex(mLineY,mStart, canvas, lineStart, line, lineWidth), mLineY +
                                    DisplayUtil.dp2px(4), right, mLineY + DisplayUtil.dp2px(4));
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
                                    int i4 = (int) getIndex(mLineY,mStart, canvas, lineStart, line, lineWidth);
                                    int i5 = (int) getIndex(mLineY,mEnd, canvas, lineStart, line, lineWidth);
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
                                    localPath.moveTo(i4, mLineY + DisplayUtil.dp2px(4));
                                    i++;
                                } else {
                                    i4 = (int) getIndex(mLineY,mStart, canvas, lineStart, line, lineWidth);
                                    localPath.moveTo(i4, mLineY + DisplayUtil.dp2px(4));
                                    i++;
                                    i++;
                                }
                                int i5 = (int) getIndex(mLineY,mEnd, canvas, lineStart, line, lineWidth);
                                localPath.quadTo(i4, mLineY + DisplayUtil.dp2px(4), i5, mLineY + DisplayUtil.dp2px(4));
//                                int i6 = i5 - i4;
//                                int i7 = i6 / wspace;
//                                if (i7 == 0) {
//                                    i7 = 1;
//                                }
//                                for (int i9 = 0; i9 < i7; i9++) {
//                                    localPath.rQuadTo(dx, yheight, dx2, 0.0F);
//                                    localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
//                                }
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

    public float getIndex(int mLineY,int index, Canvas canvas, int lineStart, String line, float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(lineStart, line)) {
            String blanks = "  ";
            canvas.drawText(blanks, x, mLineY, getPaint());
            float bw = getBwfloat(blanks, getPaint());
            x += bw;
            line = line.substring(3);
        }
        float d = (mViewWidth - lineWidth) / line.length() - 1;
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

    public boolean needScale(String line) {
        if (line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }

    public static float getDesiredWidth(CharSequence c, TextPaint paint) {
        return StaticLayout.getDesiredWidth(c, paint);
    }

    static HashMap StrMap = new HashMap();

    public static float getDesiredWidth(int type, String text, int lineStart, int lineEnd, TextPaint paint) {
        return StaticLayout.getDesiredWidth(text, lineStart, lineEnd, paint);
    }

}
