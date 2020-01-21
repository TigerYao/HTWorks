package com.huatu.handheld_huatu.business.arena.textselect.engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout;
import android.widget.TextView;

import com.huatu.handheld_huatu.business.arena.textselect.TextSelectManager;
import com.huatu.handheld_huatu.business.arena.textselect.abstracts.MarkInfo;
import com.huatu.handheld_huatu.business.arena.textselect.interfaces.IDrawStyle;
import com.huatu.handheld_huatu.business.arena.textselect.interfaces.ITextStyleDraw;
import com.huatu.handheld_huatu.utils.DisplayUtil;

import java.util.ArrayList;

/**
 * 绘制底纹实现
 */
public class TextStyleDraw<T extends MarkInfo> implements ITextStyleDraw {

    private MarkInfo selectMarkInfo = new MarkInfo();

    @Override
    public void onDraw(TextSelectManager.SelectTools selectTools, Canvas canvas) {
        if (selectTools.markMenu != null && TextSelectManager.newInstance().drawStyleArray.size() > 0) {
            ArrayList<T> markInfoList = selectTools.markMenu.getMarkInfoList();
            if (markInfoList != null && markInfoList.size() > 0) {
                for (T markInfo : markInfoList) {
                    drawMark(selectTools.textView, canvas, markInfo);
                }
            }

            SelectInfo selectionInfo = selectTools.selectInfo;
            if (selectionInfo != null && selectionInfo.mSelectionContent != null) {
                if (selectionInfo.mStart != selectionInfo.mEnd) {
                    selectMarkInfo.style = 0;
                    selectMarkInfo.start = selectionInfo.mStart;
                    selectMarkInfo.end = selectionInfo.mEnd;
                    selectMarkInfo.color = Color.parseColor("#5003A9F4");
                    selectMarkInfo.content = selectionInfo.mSelectionContent;
                    drawMark(selectTools.textView, canvas, selectMarkInfo);
                }
            }
        }
    }

    /**
     * 绘制背景
     *
     * @param canvas   TextView的画布
     * @param markInfo 需要绘制的位置及颜色信息
     */
    private void drawMark(TextView textView, Canvas canvas, MarkInfo markInfo) {

        // 位置不能超过TextView的总长度
        if (markInfo.end > textView.getText().length()) {
            markInfo.end = textView.getText().length();
            if (markInfo.start >= markInfo.end) {
                return;
            }
        }

        IDrawStyle iDrawStyle = TextSelectManager.newInstance().drawStyleArray.get(markInfo.style);
        if (iDrawStyle == null) return;

        Layout layout = textView.getLayout();
        int lineCount = layout.getLineCount();

        float lineSpacingExtra = textView.getLineSpacingExtra();

        int startLine = layout.getLineForOffset(markInfo.start);
        int endLine = layout.getLineForOffset(markInfo.end);

        if (startLine == endLine) {      // 选中的同一行
            float left = layout.getPrimaryHorizontal(markInfo.start);
            float right = layout.getPrimaryHorizontal(markInfo.end);
            int top = layout.getLineTop(startLine);
            int bottom = layout.getLineBottom(startLine);
            iDrawStyle.drawStyle(canvas,
                    left + textView.getPaddingLeft(),
                    top + textView.getPaddingTop(),
                    right + textView.getPaddingLeft(),
                    bottom + textView.getPaddingTop() - lineSpacingExtra,
                    markInfo);
        } else {                        // 占两行及以上
            for (int i = startLine; i <= endLine; i++) {

                float left, right, top, bottom;

                top = layout.getLineTop(i);
                if (i == lineCount - 1) {   // 因为系统最后一行的绘制总是缺点儿，所以这里修正一下
                    bottom = layout.getLineBottom(i) + DisplayUtil.dp2px(4);
                } else {
                    bottom = layout.getLineBottom(i);
                }

                if (i == startLine) {            // 第一行
                    left = layout.getPrimaryHorizontal(markInfo.start);
                    right = layout.getLineRight(i);
                } else if (i == endLine) {        // 最后一行
                    left = layout.getLineLeft(i);
                    right = layout.getPrimaryHorizontal(markInfo.end);
                } else {                         // 中间行
                    left = layout.getLineLeft(i);
                    right = layout.getLineRight(i);
                }

                iDrawStyle.drawStyle(canvas,
                        left + textView.getPaddingLeft(),
                        top + textView.getPaddingTop(),
                        right + textView.getPaddingLeft(),
                        bottom + textView.getPaddingTop() - lineSpacingExtra,
                        markInfo);
            }
        }
    }
}
