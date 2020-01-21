package com.huatu.handheld_huatu.business.arena.textselect.impl;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.huatu.handheld_huatu.business.arena.textselect.abstracts.AbsDrawStyle;
import com.huatu.handheld_huatu.business.arena.textselect.abstracts.MarkInfo;

/**
 * 背景实现
 */
public class BackgroundDrawStyle extends AbsDrawStyle {

    private Paint paint;

    public BackgroundDrawStyle() {
        style = 0;
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void drawStyle(Canvas canvas, float left, float top, float right, float bottom, MarkInfo markInfo) {
        paint.setColor(markInfo.getColor());
        canvas.drawRect(left, top, right, bottom, paint);
    }
}
