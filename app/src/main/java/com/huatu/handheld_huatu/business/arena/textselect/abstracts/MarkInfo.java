package com.huatu.handheld_huatu.business.arena.textselect.abstracts;

/**
 * 标记样式绘制位置的类
 */
public class MarkInfo {

    public int seq;             // 方便删除（数据库主键）

    public int start, end;      // 标记起始位置
    public String content;      // 内容
    public int style;           // 样式
    public int color;           // 颜色

    public int getColor() {
        return color == 0 ? 0X30F44336 : color;
    }

    public MarkInfo() {
    }

    public MarkInfo(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public MarkInfo(int start, int end, String content, int style, int color) {
        this.start = start;
        this.end = end;
        this.content = content;
        this.style = style;
        this.color = color;
    }
}
