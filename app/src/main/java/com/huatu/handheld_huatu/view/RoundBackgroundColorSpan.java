package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import com.baijia.player.playback.util.DisplayUtils;


public class RoundBackgroundColorSpan extends ReplacementSpan {
    private int bgColor;
    private int textColor;
    private Context context;


    public RoundBackgroundColorSpan(Context context, int bgColor, int textColor) {
        super();
        this.context = context;
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return ((int) paint.measureText(text, start, end) + DisplayUtils.px2dip(context, 76));
    }


    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color1 = paint.getColor();
        //设置背景颜色
        paint.setColor(this.bgColor);
        canvas.drawRoundRect(new RectF(x + DisplayUtils.dip2px(context, 2), top, x + ((int) paint.measureText(text, start, end)) + DisplayUtils.dip2px(context, 6), bottom), 5, 5, paint);
        //设置字体颜色
        paint.setColor(this.textColor);
        canvas.drawText(text, start, end, x + DisplayUtils.dip2px(context, 4), y, paint);
        paint.setColor(color1);
    }
}