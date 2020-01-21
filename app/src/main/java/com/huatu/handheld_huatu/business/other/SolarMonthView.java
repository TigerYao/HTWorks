package com.huatu.handheld_huatu.business.other;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;
import com.huatu.handheld_huatu.utils.LogUtils;

import java.util.List;

/**
 * 精致星系月视图，利用三角函数计算坐标
 * solar MonthView ,using trigonometric function
 * Created by huanghaibin on 2018/2/8.
 */

public class SolarMonthView extends MonthView {


    private Paint mPointPaint = new Paint();

    private int mRadius;
    private int mPointRadius;

    public SolarMonthView(Context context) {
        super(context);


        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mSchemePaint.setStyle(Paint.Style.STROKE);
        mSchemePaint.setStrokeWidth(dipToPx(context, 1f));
        mSchemePaint.setColor(0xFFFFFFFF);
        mPointRadius = dipToPx(context, 2f);
        mPointPaint.setColor(Color.RED);

        //兼容硬件加速无效的代码
        //setLayerType(View.LAYER_TYPE_SOFTWARE, mPointPaint);
        //4.0以上硬件加速会导致无效
       // mPointPaint.setMaskFilter(new BlurMaskFilter(28, BlurMaskFilter.Blur.SOLID));

        setLayerType(View.LAYER_TYPE_SOFTWARE, mSchemePaint);
        mSchemePaint.setMaskFilter(new BlurMaskFilter(28, BlurMaskFilter.Blur.SOLID));
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
       // canvas.drawCircle(cx, cy, mRadius, mSchemePaint);

        List<Calendar.Scheme> schemes = calendar.getSchemes();

        //mPointPaint.setColor(schemes.get(0).getShcemeColor());//You can also use three fixed Paint 你也可以使用三个Paint对象
      /*  int rightTopX = (int) (cx + mRadius * Math.cos(-10 * Math.PI / 180));
        int rightTopY = (int) (cy + mRadius * Math.sin(-10 * Math.PI / 180));
        canvas.drawCircle(rightTopX, rightTopY, mPointRadius, mPointPaint);

         mPointPaint.setColor(schemes.get(1).getShcemeColor());
        int leftTopX = (int) (cx + mRadius * Math.cos(-140 * Math.PI / 180));
        int leftTopY = (int) (cy + mRadius * Math.sin(-140 * Math.PI / 180));
        canvas.drawCircle(leftTopX, leftTopY, mPointRadius, mPointPaint);*/

        //LogUtils.e("onDrawScheme",calendar.getScheme()+"");

        if("0".equals(calendar.getScheme())){
            mPointPaint.setColor(0xFFB3B7BF);  //Color.parseColor("#B3B7BF")
        }else
            mPointPaint.setColor(Color.RED);

        int bottomX = (int) (cx + mRadius * Math.cos(90 * Math.PI / 180));
        int bottomY = (int) (cy + mRadius * Math.sin(130 * Math.PI / 180));
        canvas.drawCircle(bottomX, bottomY, mPointRadius, mPointPaint);

    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;
        //画圆
        if((!isSelected)&&calendar.isCurrentDay()){

            int cy = mItemHeight / 2+ y;
            //初始化Path
            Path path = new Path();
            //Path.Direction.CW顺时针绘制圆 Path.Direction.CCW逆时针绘制圆
            path.addCircle( cx,cy, mRadius, Path.Direction.CW);
            mSchemePaint.setColor(0xFFDDDCDC);//#DDDCDC
            canvas.drawPath(path, mSchemePaint);
        }
        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.isCurrentDay()? "今":calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.isCurrentDay()? "今":calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {
            canvas.drawText(String.valueOf(calendar.isCurrentDay()? "今":calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
