package com.huatu.test.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * 支持解析html的TextView
 * 目前本TextView会与父控件的点击事件冲突，需要两者同时设置相同的点击事件
 * Created by KaelLi on 2016/7/4.
 * <p>
 * 如果接口变量 mCusDraw 不为空，就把所有的绘制交给 此接口实现类 CusAlignText
 */
public class ExerciseTextView extends android.support.v7.widget.AppCompatTextView {

    protected Context mContext;
    private String htmlSource;
    private int type;
    private Spanned mSpanned;

    public CusDraw mCusDraw;

    public interface CusDraw {
        void onDraw(Canvas canvas, ExerciseTextView textView);
    }

    public void setmCusDraw(CusDraw mCusDraw) {
        this.mCusDraw = mCusDraw;
    }

    public ExerciseTextView(Context context) {
        super(context);
        this.mContext = context;
    }

    public void initDefaultStyle(){
        this.setTextColor(0xff4a4a4a);
        this.setTextSize(15);
        this.setLineSpacing(DensityUtils.dp2px(mContext,5),1);
    }

    public ExerciseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public ExerciseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

     public void setHtmlSource(String source) {
        if (TextUtils.isEmpty(source)) {
            return;
        }
        this.htmlSource = source;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mSpanned = Html.fromHtml(htmlSource, Html.FROM_HTML_MODE_COMPACT,
                    new GlideImageV4Getter(htmlSource, ExerciseTextView.this,getContext()),null);
        } else {
            mSpanned = Html.fromHtml(htmlSource,
                    new GlideImageV4Getter(htmlSource, ExerciseTextView.this,getContext()),
                    null);
        }
        ExerciseTextView.this.setText(mSpanned);
        this.setMovementMethod(LinkMovementMethod.getInstance());
        if (type != 1) {
            setHighlightColor(getResources().getColor(android.R.color.transparent));//方法重新设置文字背景为透明色。
        }
    }

   /* public void setHtmlSource(float widthdp, String source) {
        if (TextUtils.isEmpty(source)) {
            return;
        }
        this.htmlSource = source;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mSpanned = Html.fromHtml(htmlSource, Html.FROM_HTML_MODE_COMPACT,
                    new GlideImageV4Getter(widthdp, htmlSource, ExerciseTextView.this,getContext()), new ImgTagHandler(true));
        } else {
            mSpanned = Html.fromHtml(htmlSource,
                    new GlideImageV4Getter(widthdp, htmlSource, ExerciseTextView.this,getContext()), new ImgTagHandler(true));
        }
        ExerciseTextView.this.setText(mSpanned);
        this.setMovementMethod(LinkMovementMethod.getInstance());
        if (type != 1) {
            setHighlightColor(getResources().getColor(android.R.color.transparent));//方法重新设置文字背景为透明色。
        }
    }*/

    public void setType(int type) {
        this.type = type;
    }

    boolean isContainImg = false;

    public void setTextImgTagEssay(String input) {
        if (input != null) {
            isContainImg = input.contains("<img") || input.contains("< img src");
        }
    }


    private boolean isCenterImg;        // 是否支持图片和文字上下居中显示

    /**
     * 是否支持文字和图片上下居中
     * 注意：必须要设置背景颜色，要不然会使用默认白色覆盖原文字和图片的位置
     */
    public void setCenterImg(boolean centerImg) {
        isCenterImg = centerImg;
    }

    ArrayList<String> starts = new ArrayList<>(Arrays.asList("(单选题)", "(多选题)", "(不定项选择)", "(判断题)", "(复合题)", "(主观题)"));

    ArrayList<Point> imageSpans;
    ArrayList<TextInfo> styleSpans;
    HashSet<TextInfo> reDrawText;

    @Override
    protected void onDraw(Canvas canvas) {

        if (!isCenterImg) {                         // 不支持文字图片上下居中，就直接绘制
            if (mCusDraw == null) {
                super.onDraw(canvas);
            } else {
                if (!isContainImg) {
                    mCusDraw.onDraw(canvas, this);
                } else {
                    super.onDraw(canvas);
                }
            }
            return;
        }

        Layout layout = getLayout();

        if (imageSpans == null) {
            imageSpans = new ArrayList<>();            // p.x 记录ImageSpan的index，p.y 记录的是line
        } else {
            imageSpans.clear();
        }
        if (styleSpans == null) {
            styleSpans = new ArrayList<>();
        } else {
            styleSpans.clear();
        }
        if (reDrawText == null) {
            reDrawText = new HashSet<>();
        } else {
            reDrawText.clear();
        }

        if (mSpanned != null) {

            int lineCount = layout.getLineCount();

            // 记录ImageSpan position
            if (mSpanned != null) {
                for (int i = 0; i < mSpanned.length(); i++) {
                    ReplacementSpan[] spans = mSpanned.getSpans(i, i + 1, ReplacementSpan.class);
                    if (spans.length > 0) {
                        if (spans[0] instanceof ImageSpan) {
                            imageSpans.add(new Point(i, 0));
                            ImageSpan imageSpan = (ImageSpan) spans[0];
                            Drawable drawable = imageSpan.getDrawable();
                            Rect bounds = drawable.getBounds();
                        }
                    } else {
                        StyleSpan[] styls = mSpanned.getSpans(i, i + 1, StyleSpan.class);
                        if (styls.length > 0) {
                            int style = styls[0].getStyle();
                            if (style == Typeface.BOLD) {        // 是StyleSpan，是粗体
                                TextInfo info = new TextInfo();
                                info.start = i;
                                info.end = i + 1;
                                info.content = mSpanned.subSequence(i, i + 1).toString();
                                styleSpans.add(info);
                            }
                        }
                    }
                }
            }

            // 记录需要重绘的文字，存放在 HashSet<TextInfo> reDrawText 中
            for (int j = 0; j < lineCount; j++) {

                int start = layout.getLineStart(j);
                int end = layout.getLineEnd(j);

                for (Point p : imageSpans) {
                    int index = p.x;
                    if (start <= index && index < end) {
                        p.y = j;
                        int stringStart = start;
                        for (int i = start; i < end; i++) {

                            ReplacementSpan[] spans = mSpanned.getSpans(i, i + 1, ReplacementSpan.class);

                            if (spans.length > 0 || i == end - 1) {

                                TextInfo info = new TextInfo();
                                info.start = stringStart;
                                info.end = i;
                                if (spans.length > 0) {
                                    info.content = mSpanned.subSequence(stringStart, i).toString();
                                } else {
                                    info.content = mSpanned.subSequence(stringStart, i + 1).toString();
                                }
                                info.line = j;
                                reDrawText.add(info);

                                stringStart = i + 1;
                            }
                        }
                    }
                }

                for (TextInfo styleSpan : styleSpans) {
                    int index = styleSpan.start;
                    if (start <= index && index < end) {
                        styleSpan.line = j;
                    }
                }
            }
        }

        if (mCusDraw == null) {
            super.onDraw(canvas);
        } else {
            if (!isContainImg) {
                mCusDraw.onDraw(canvas, this);
            } else {
                super.onDraw(canvas);
            }
        }

        TextPaint paint = getPaint();

        int color = paint.getColor();
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();

        // 绘制纯色，覆盖原文字
        Drawable drawable = getBackground();
        ColorDrawable dra = (ColorDrawable) drawable;

        // 重绘不居中的文字
        if (mSpanned != null) {

            for (TextInfo info : reDrawText) {

                int top = layout.getLineBottom(info.line - 1);
                int bottom = layout.getLineBottom(info.line);

                float textSize = paint.getTextSize();
                if (dra != null) {
                    paint.setColor(dra.getColor());
                } else {
                    paint.setColor(Color.parseColor("#FFFFFF"));
                }

                float startX = layout.getPrimaryHorizontal(info.start) + getPaddingLeft();
                float startY = bottom - ((bottom - top) - textSize) / 2 + getPaddingTop();

                RectF rectF = new RectF(startX, top + getPaddingTop(), startX + paint.measureText(info.content), bottom + getPaddingTop());

                canvas.drawRect(rectF, paint);

                int index = -5;

                for (int i = 0; i < starts.size(); i++) {
                    String s = starts.get(i);
                    if (info.content.startsWith(s)) {
                        index = i;
                        break;
                    }
                }

                if (index == -5) {
                    paint.setColor(color);
                    canvas.drawText(info.content, startX, startY - fm.descent / 2, paint);
                } else {
                    int nightMode = 1;//SpUtils.getDayNightMode();
                    String colorS;
                    if (nightMode == 1) {
                        colorS = "#421B29";
                    } else {
                        colorS = "#D3688F";
                    }
                    paint.setColor(Color.parseColor(colorS));
                    canvas.drawText(info.content.substring(0, starts.get(index).length()), startX, startY - fm.descent / 2, paint);
                    float v = paint.measureText(starts.get(index));
                    paint.setColor(color);
                    canvas.drawText(info.content.substring(starts.get(index).length()), startX + v, startY - fm.descent / 2, paint);
                }

                paint.setColor(color);
            }
        }

        for (Point p : imageSpans) {

            Typeface typeface = paint.getTypeface();
            // 重绘图片位置
            ImageSpan[] spans = mSpanned.getSpans(p.x, p.x + 1, ImageSpan.class);

            int top = layout.getLineBottom(p.y - 1) + getPaddingTop();
            int bottom = layout.getLineBottom(p.y) + getPaddingTop();

            ImageSpan imageSpan = spans[0];
            Rect bounds = imageSpan.getDrawable().getBounds();
            if (bounds.height() == 0) {
                continue;
            } else {

                // 绘制纯色，覆盖原文字
                if (dra != null) {
                    paint.setColor(dra.getColor());
                } else {
                    paint.setColor(Color.parseColor("#FFFFFF"));
                }

                float startX = layout.getPrimaryHorizontal(p.x) + getPaddingLeft();
                int bottomx = bottom - (bottom - top - bounds.height()) / 2;

                RectF rectF = new RectF(startX, top, startX + bounds.width(), bottom);

                canvas.drawRect(rectF, paint);

                float x = layout.getPrimaryHorizontal(p.x) + getPaddingLeft();

                imageSpan.draw(canvas, "", 0, 0, x, 0, 0, bottomx, paint);

                paint.setColor(color);
            }

            // 绘制和图片在同一行的粗体文字
            for (TextInfo styleSpan : styleSpans) {
                if (styleSpan.line == p.y) {

                    float textSize = paint.getTextSize();

                    paint.setColor(color);
                    Typeface font = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                    paint.setTypeface(font);

                    float startX = layout.getPrimaryHorizontal(styleSpan.start) + getPaddingLeft();
                    float startY = bottom - ((bottom - top) - textSize) / 2;

                    canvas.drawText(styleSpan.content, startX, startY - fm.descent / 2, paint);

                }
            }
            paint.setTypeface(typeface);
        }
    }

  /*  public SelectableTextHelper mSelectableTextHelper;

    public void openCopy() {
        mSelectableTextHelper = new SelectableTextHelper.Builder(this)
                .setSelectedColor(ContextCompat.getColor(getContext(), R.color.essay_sel_color))
                .setCursorHandleSizeInDp(10).setShowType(SelectableTextHelper.SHOW_TYPE_ONLY_COPY)
                .setCursorHandleColor(ContextCompat.getColor(getContext(), R.color.main_color))
                .build();
    }

    public void clearView() {
        if (mSelectableTextHelper != null) {
            mSelectableTextHelper.clearView();
        }
    }*/

    static class TextInfo {
        int start;
        int end;
        String content;
        int line;

        @Override
        public String toString() {
            return "TextInfo{" +
                    "start=" + start +
                    ", end=" + end +
                    ", content=" + content +
                    ", line=" + line +
                    '}';
        }

        @Override
        public int hashCode() {
            return start;
        }

        @Override
        public boolean equals(Object obj) {
            return toString().equals(obj.toString());
        }
    }
}
