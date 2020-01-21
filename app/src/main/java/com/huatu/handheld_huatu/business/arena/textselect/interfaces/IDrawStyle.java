package com.huatu.handheld_huatu.business.arena.textselect.interfaces;

import android.graphics.Canvas;

import com.huatu.handheld_huatu.business.arena.textselect.abstracts.MarkInfo;

/**
 * 样式绘制器
 * 背景，下划线，等等
 * 因为从MarkInfo找到画背景的框方法是一样的，所以放到
 */
public interface IDrawStyle<T extends MarkInfo> {
    /**
     * 进行样式绘制
     *
     * @param canvas    画布
     * @param markInfo  样式
     */
    void drawStyle(Canvas canvas, float left, float top, float right, float bottom, T markInfo);
}
