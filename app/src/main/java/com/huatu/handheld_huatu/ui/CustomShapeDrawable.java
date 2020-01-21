package com.huatu.handheld_huatu.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.utils.DensityUtils;

/**
 * https://www.cnblogs.com/phj981805903/p/3325182.html
 * Created by cjx on 2019\5\17 0017.
 */

public class CustomShapeDrawable extends ShapeDrawable {
    private final Paint fillpaint, strokepaint;

    public CustomShapeDrawable(Shape s, int fillColor, int strokeColor, float strokeWidth) {
        super(s);
        fillpaint = new Paint(this.getPaint());
        fillpaint.setColor(fillColor);
        strokepaint = new Paint(fillpaint);
        strokepaint.setStyle(Paint.Style.STROKE);
        strokepaint.setStrokeWidth(strokeWidth);
        strokepaint.setColor(strokeColor);
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        shape.draw(canvas, fillpaint);
        shape.draw(canvas, strokepaint);
    }


    //有边框
    public static ShapeDrawable build(int radius, int fillColor, int strokeColor, float strokeWidth) {

        int r = DensityUtils.dp2px(UniApplicationContext.getContext(), radius);
        boolean isAllRound = true;
        float[] outerR = new float[]{r, r, r, r, isAllRound ? r : 0, isAllRound ? r : 0, r, r};//// 前2个 左上角， 3 4 ， 右上角， 56， 右下， 78 ，左下，如果没弧度的话，传入null即可。

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        CustomShapeDrawable drawable = new CustomShapeDrawable(rr, fillColor, strokeColor, DensityUtils.dp2px(UniApplicationContext.getContext(), strokeWidth));
        drawable.getPaint().setColor(fillColor);
        return drawable;
    }


    public static ShapeDrawable buildV2(int radius, int fillColor, int strokeColor, float strokeWidth) {


        float storke = (strokeWidth * DisplayUtil.getDensity() + 0.5f);
        ;
        float r = DisplayUtil.dp2px(radius);
        //(dpValue * getDensity() + 0.5f);

        float[] outerR = new float[]{r, r, r, r, r, r, r, r};//// 前2个 左上角， 3 4 ， 右上角， 56， 右下， 78 ，左下，如果没弧度的话，传入null即可。

        float totalDistance = storke;
        // 内部矩形与外部矩形的距离
        RectF inset = new RectF(totalDistance, totalDistance, totalDistance, totalDistance);

        float inRadii = r - 2 * storke;
        // 内部矩形弧度
        float[] innerRadii = new float[]{inRadii, inRadii, inRadii, inRadii, inRadii, inRadii, inRadii, inRadii};

        RoundRectShape rr = new RoundRectShape(outerR, inset, innerRadii);
        CustomShapeDrawable drawable = new CustomShapeDrawable(rr, fillColor, strokeColor, storke);
        drawable.getPaint().setColor(fillColor);
        return drawable;
    }


    public static ShapeDrawable build(int radius, int strokeColor, float strokeWid) {


        int r = DensityUtils.dp2px(UniApplicationContext.getContext(), radius);
        int strokeWidth = DensityUtils.dp2px(UniApplicationContext.getContext(), strokeWid);
        ;

        float totalDistance = strokeWidth * 2;
        // 外部矩形弧度
        float[] outerR = new float[]{r, r, r, r, r, r, r, r};
        // 内部矩形与外部矩形的距离
        RectF inset = new RectF(totalDistance, totalDistance, totalDistance, totalDistance);

        // 内部矩形弧度

        float inRadii = r - strokeWidth;
        // 内部矩形弧度
        float[] innerRadii = new float[]{inRadii, inRadii, inRadii, inRadii, inRadii, inRadii, inRadii, inRadii};
        // float[] innerRadii = new float[] { 20, 20, 20, 20, 20, 20, 20, 20 };

        RoundRectShape rr = new RoundRectShape(outerR, inset, innerRadii);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        //指定填充颜色
        drawable.getPaint().setColor(strokeColor);
        // 指定填充模式
        drawable.getPaint().setStyle(Paint.Style.FILL);
        return drawable;
    }

    //无边框
    public static ShapeDrawable buildRoundBackground(int radius, int color) {

        int r = DensityUtils.dp2px(UniApplicationContext.getContext(), radius);
        float[] outerR = new float[]{r, r, r, r, r, r, r, r};//// 前2个 左上角， 3 4 ， 右上角， 56， 右下， 78 ，左下，如果没弧度的话，传入null即可。

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(color);
        return drawable;
    }

    //https://blog.csdn.net/lovexjyong/article/details/45966179
       /*  //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
                gradientDrawable.setCornerRadii(new float[] { topLeftRadius,
      topLeftRadius, topRightRadius, topRightRadius,
      bottomRightRadius, bottomRightRadius, bottomLeftRadius,
      bottomLeftRadius });*/

    //带渐变的圆角
    public static GradientDrawable buildGradientBackgroud(int startColor,int endColor,float[] radius) {

        GradientDrawable gradientDrawable = new GradientDrawable();

        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradientDrawable.setColors(new int[]{startColor, endColor});
        gradientDrawable.setCornerRadii(radius);
        return gradientDrawable;
  }

}

  /*  private ShapeDrawable getDefaultBackground(int radius, int color, boolean isAllRound) {

        int r = DensityUtils.dp2px(mContext,radius);
        float[] outerR = new float[] {r, r,  0,  0,isAllRound?r: 0,isAllRound?r: 0, 0, 0};//// 前2个 左上角， 3 4 ， 右上角， 56， 右下， 78 ，左下，如果没弧度的话，传入null即可。

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(color);
        return drawable;
    }*/
