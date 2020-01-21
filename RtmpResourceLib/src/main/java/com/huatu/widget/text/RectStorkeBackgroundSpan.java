package com.huatu.widget.text;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import com.huatu.utils.DensityUtils;

/**
 * Created by pengyuntao on 2016/12/5.
 */

public class RectStorkeBackgroundSpan extends ReplacementSpan {

    private int mSize;
    private int mColor;
    private int mTextColor;
    private int mRadius;
    private float mTextSize;
    private float mStrokeWidth;


    /**
     * @param color  背景颜色
     * @param radius 圆角半径
     */
    public RectStorkeBackgroundSpan(int color, int radius, float textSize,float strokeWidth) {
        mColor = color;
        mRadius = radius;
        mTextColor=Color.parseColor("#9B9B9B");// Color.WHITE;
        mTextSize=textSize;
        mStrokeWidth=strokeWidth;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        //mSize = (int) (paint.measureText(text, start, end) + 2 * mRadius);
        mSize = (int) (paint.measureText(text, start, end) + 2 * mRadius);
        //mSize就是span的宽度，span有多宽，开发者可以在这里随便定义规则
        //我的规则：这里text传入的是SpannableString，start，end对应setSpan方法相关参数
        //可以根据传入起始截至位置获得截取文字的宽度，最后加上左右两个圆角的半径得到span宽度
        return mSize;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color = paint.getColor();//保存文字颜色

        paint.setStrokeWidth(2.5f);
        paint.setColor(mColor);//设置背景颜色
        paint.setStyle(Paint.Style.STROKE);
       // Paint.setStrokeWidth(3f);
        paint.setAntiAlias(true);// 设置画笔的锯齿效果
        float oldSize=  paint.getTextSize();
        paint.setTextSize(mTextSize);
        //Log.i("pyt", y + "");
        RectF oval = new RectF(x, y + paint.ascent()-2, x + mSize-0, y + paint.descent()-2);
        //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
        canvas.drawRoundRect(oval, 0, 0, paint);//绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
        paint.setColor(mTextColor);//恢复画笔的文字颜色

        paint.setStrokeWidth(0);
        canvas.drawText(text, start, end, x + mRadius*3/2, y-2, paint);//绘制文字
        paint.setColor(color);//恢复画笔的文字颜色
        paint.setTextSize(oldSize);
    }
}
