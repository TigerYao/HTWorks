package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


import com.huatu.utils.DensityUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjx on .
 *
 */

public abstract class CalendarView extends View {



    protected WeekDay mSelectDate;//被选中的date
    protected WeekDay mInitialDate;//初始传入的date
    protected int mWidth;
    protected int mHeight;
    protected List<WeekDay> dates;


    protected int mSolarTextColor;//公历字体颜色
    protected int mLunarTextColor;//农历字体颜色
    protected int mHintColor;//不是当月的颜色
    protected float mSolarTextSize;
    protected float mLunarTextSize;
    protected Paint mSorlarPaint;
    protected Paint mLunarPaint;
    protected float mSelectCircleRadius;//选中圆的半径
    protected int mSelectCircleColor;//选中圆的颜色
    protected boolean isShowLunar;//是否显示农历

    protected int mHolidayColor;
    protected int mWorkdayColor;

    protected List<Rect> mRectList;//点击用的矩形集合
    protected int mPointColor;//圆点颜色
    protected float mPointSize;//圆点大小

    protected int mHollowCircleColor;//空心圆颜色
    protected float mHollowCircleStroke;//空心圆粗细

    protected boolean isShowHoliday;//是否显示节假日

    protected List<String> pointList;

    public CalendarView(Context context,AttributeSet var2) {
        super(context,var2);
        init(context);
    }

    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        mSolarTextColor = Color.parseColor("#ffffff");//#4A4A4A
        mLunarTextColor =Color.BLACK;
        mHintColor = Color.BLACK;
        mSolarTextSize = DensityUtils.sp2px(context,14);
        mLunarTextSize =DensityUtils.sp2px(context,10);
        mSelectCircleRadius = DensityUtils.dp2px(context,14);
        mSelectCircleColor = Color.parseColor("#33ffffff");//5D9AFF
        isShowLunar = false;

        mPointSize = DensityUtils.dp2px(context,2);
        mPointColor =Color.parseColor("#4c000000");// Color.RED;
        mHollowCircleColor = Color.parseColor("#DDDCDC");
        mHollowCircleStroke = DensityUtils.dp2px(context, 1) ;

        isShowHoliday = false;
        mHolidayColor =Color.YELLOW;
        mWorkdayColor = Color.BLUE;

        mRectList = new ArrayList<>();
        mSorlarPaint = getPaint(mSolarTextColor, mSolarTextSize);
        mLunarPaint = getPaint(mLunarTextColor, mLunarTextSize);
    }

    private Paint getPaint(int paintColor, float paintSize) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setTextSize(paintSize);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    public WeekDay getInitialDate() {
        return mInitialDate;
    }

    public WeekDay getSelectDate() {
        return mSelectDate;
    }

    public void setSelectDate(WeekDay date) {
        this.mSelectDate = date;
        invalidate();
    }

    public void setDateAndPoint(WeekDay date, List<String> pointList) {
        this.mSelectDate = date;
        this.pointList = pointList;
        invalidate();
    }

    public void clear() {
        this.mSelectDate = null;
        invalidate();
    }

    public void setPointList(List<String> pointList) {
        this.pointList = pointList;
        invalidate();
    }


    public static class WeekDay {
        /** 星期的显示名称*/
        public String week;
        /** 对应的日期*/
        public String day;

        public int year;
        public int month;
        public int dayof;
        public int liveStatus;
        public String lessionIds;

        @Override
        public String toString() {
            return "WeekDay{" +
                    "week='" + week + '\'' +
                    ", day='" + day + '\'' +

                    ", dayof='" +year+"_"+month+"_"+ dayof + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object var1) {
            if(this == var1) {
                return true;
            } else {
                if(var1 instanceof WeekDay) {
                    WeekDay var2 = (WeekDay)var1;
                    return var2.year==this.year&&(var2.month==this.month)&&(var2.dayof==this.dayof);
                }

                return super.equals(var1);
            }
        }
    }
}
