package com.huatu.handheld_huatu.business.arena.textselect.interfaces;

import android.graphics.Canvas;

import com.huatu.handheld_huatu.business.arena.textselect.TextSelectManager;

/**
 * TextView 的样式绘制交给这里
 */
public interface ITextStyleDraw {

    /**
     * 绘制样式
     *
     * @param canvas 画布
     */
    void onDraw(TextSelectManager.SelectTools selectTools, Canvas canvas);
}
