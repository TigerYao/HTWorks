package com.huatu.test.drawimpl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;


import com.huatu.test.DisplayUtil;
import com.huatu.test.R;
import com.huatu.test.custom.DensityUtils;
import com.huatu.test.custom.ExerciseTextView;
import com.huatu.test.custom.ResourceUtils;

import java.util.List;

/**
 * Created by cjx on 2019\6\27 0027.
 */

public class CusNumAlignText extends CusAlignText {
    private Paint mSorlarPaint;
    private Paint mPathPaint;
    private float mSelectCircleRadius;//选中圆的半径
    protected float mHollowCircleStroke;//空心圆粗细
    private Path mPath = new Path();
    private float mCircleXOffset=0;  //圆的左偏移
    RectF mFontRect=new RectF();
    public CusNumAlignText(ExerciseTextView exerciseTextView) {
        super(exerciseTextView);

        mPathPaint = new Paint();

        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setAntiAlias(true);
        mHollowCircleStroke= DensityUtils.dp2px(mContext, 1f);
        mPathPaint.setStrokeWidth(mHollowCircleStroke);
        mSelectCircleRadius=DensityUtils.dp2px(mContext,8);
        mCircleXOffset=DensityUtils.dp2px(mContext, 0.3f);
        mSorlarPaint = getPaint(0XFFFF6D73, DensityUtils.sp2px(mContext,11));
    }

    private Paint getPaint(int paintColor, float paintSize) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setTextSize(paintSize);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    @Override
    public void onDraw(Canvas canvas, ExerciseTextView textView) {
        super.onDraw(canvas,textView);
        if(mDrawLine!=null&&(mDrawLine instanceof CusNumCheckDetailTag)){
            List<CusCheckDetailTag.Data> scorePoints= ((CusNumCheckDetailTag)mDrawLine).mLineListInfos;

            int adjustX=  DensityUtils.dp2px(mContext,5);
            int i=1;
            int scorefontcolor= ResourceUtils.getColor( R.color.common_style_text_color);
            for(CusCheckDetailTag.Data data:scorePoints){

                if (data.score != null) {//放在这里可以盖住后面的波浪线
                    CusNumCheckDetailTag curDrawLine=(CusNumCheckDetailTag)mDrawLine;
                    Paint localPaint=curDrawLine.localPaint;

                    localPaint.setColor(Color.WHITE);
                    localPaint.setStyle(Paint.Style.FILL);

                    float yheight = curDrawLine.yheight;
                    int yOffset=curDrawLine.yOffset;
                    int yOffsetz=curDrawLine.yOffsetz;
                    int wspace = curDrawLine.wspace;
                    float left;
                    float top = data.ystart + DisplayUtil.dp2px(yOffset) - yheight;
                    float right = data.xend + wspace;
                    float bottom = data.ystart + DisplayUtil.dp2px(yOffset) + yheight;
                    if (data.score != null) {
                        left = data.xend - adjustX;//DisplayUtil.dp2px(5);
                    } else {
                        left = data.xend;
                    }
                   // RectF rectF = new RectF(left, top, right, bottom);
                   /* mFontRect.set(left, top, right, bottom);
                    canvas.drawRect(mFontRect, localPaint);
                    localPaint.setColor(scorefontcolor);

                    Typeface oldType = localPaint.getTypeface();
                    float oldSize = localPaint.getTextSize();
                    // 字体

                    Typeface fromAsset=curDrawLine.fromAsset;
                    localPaint.setTypeface(fromAsset);
                    localPaint.setTextSize(DisplayUtil.dp2px(8));

                    localPaint.setStrokeWidth(1);
                     // 最后的画分数
                     // canvas.drawText(data.score, i5 - DisplayUtil.dp2px(xOffset), data.ystart + DisplayUtil.dp2px(yOffsetz), localPaint);
                    canvas.drawText(data.score, mSelectCircleRadius+left + 1, data.ystart + DisplayUtil.dp2px(yOffsetz), localPaint);*/
                    //重置
                    localPaint.setStyle(Paint.Style.STROKE);
                   // localPaint.setTypeface(oldType);
                  //  localPaint.setTextSize(oldSize);
                    localPaint.setStrokeWidth(2);


                    mPathPaint.setStyle(Paint.Style.FILL);
                    mPathPaint.setColor(Color.WHITE);

                   // float  left = data.xend -adjustX;
                    float cx= left -  -mCircleXOffset;
                    float cy=data.ystart+mSelectCircleRadius/2+ mHollowCircleStroke-mCircleXOffset;
                    canvas.drawCircle(cx, cy, mSelectCircleRadius, mPathPaint);
                    //初始化Path
                    mPath.reset();
                    mPathPaint.setColor(0xFFFF6D73);
                    mPathPaint.setStyle(Paint.Style.STROKE);
                    //Path.Direction.CW顺时针绘制圆 Path.Direction.CCW逆时针绘制圆
                    mPath.addCircle(cx, cy, mSelectCircleRadius - mHollowCircleStroke, Path.Direction.CW);
                    canvas.drawPath(mPath, mPathPaint);

                    Paint.FontMetricsInt fontMetrics = mSorlarPaint.getFontMetricsInt();
                    mFontRect.set(cx-mSelectCircleRadius,cy-mSelectCircleRadius,cx+mSelectCircleRadius,cy+mSelectCircleRadius);
                    float baseline = (mFontRect.bottom + mFontRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                   // float baseline = (2*mSelectCircleRadius - fontMetrics.bottom - fontMetrics.top) / 2;
                    canvas.drawText(String.valueOf(i), cx, baseline, mSorlarPaint);
                    i++;
/*
                    // 最后的画分数
//                        canvas.drawText(data.score, i5 - DisplayUtil.dp2px(xOffset), data.ystart + DisplayUtil.dp2px(yOffsetz), localPaint);
                    canvas.drawText(data.score, mSelectCircleRadius+left + 1, data.ystart + DisplayUtil.dp2px(yOffsetz), localPaint);*/


                }
            }

        }
    }
}
