package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;


import com.huatu.handheld_huatu.mvpmodel.CalendarLiveBean;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Created by cjx on.  http://www.bubuko.com/infodetail-1207943.html
 * View 的关键生命周期为 [改变可见性] --> 构造View --> onFinishInflate --> onAttachedToWindow --> onMeasure --> onSizeChanged --> onLayout --> onDraw --> onDetackedFromWindow
 *
 */

public class WeekView extends CalendarView {
    /**
     * 周起始：周日
     */
      final int WEEK_START_WITH_SUN = 1;

    /**
     * 周起始：周一
     */
      final int WEEK_START_WITH_MON = 2;

    /**
     * 周起始：周六
     */
      final int WEEK_START_WITH_SAT = 7;

    private OnClickWeekViewListener mOnClickWeekViewListener;
    private OnViewInitListener mOnViewInitListener;
    private boolean mHasInit=false;

    public void setOnViewInitListener(OnViewInitListener viewInitListener){
          this.mOnViewInitListener=viewInitListener;
    }

    public interface OnViewInitListener{
        void onViewInit(WeekDay date);
    }
    private List<String> lunarList;
 /*   private void drawNormalRing(Canvas canvas) {
        Paint ringNormalPaint = new Paint();
        ringNormalPaint.setStyle(Paint.Style.STROKE);
        ringNormalPaint.setStrokeWidth(mHollowCircleStroke);
        ringNormalPaint.setColor(Color.WHITE);//圆环默认颜色为灰色
        canvas.drawArc(mRectF, 360, 360, false, ringNormalPaint);
        Path path = new Path();
        path.addCircle
    }*/


    public void refreshView(List<CalendarLiveBean> liveBeans){
        for (WeekDay bean : dates) {
            for (CalendarLiveBean livebean : liveBeans) {
                if (bean.day.equals(livebean.date)) {
                    bean.liveStatus = (livebean.isHaveLive + 1);
                    if(!ArrayUtils.isEmpty(livebean.id)){
                        bean.lessionIds= StringUtils.arrayString(livebean.id);
                    }
                    break;
                }
            }
        }
        this.invalidate();
    }

    public WeekDay getTodayWeekDay() {
        for (WeekDay bean : dates) {
             if(null!=mSelectDate){
                 if (bean.day.equals(mSelectDate.day)) {
                     return bean;
                 }
             }else {
               if (bean.day.equals(mInitialDate.day)) {
                    return bean;
               }
            }
         }
        return null;
    }


    private   int getMonthViewStartDiff(int week, int weekStart) {
        if (weekStart == WEEK_START_WITH_SUN) {
            return week - 1;
        }
        if (weekStart == WEEK_START_WITH_MON) {
            return week == 1 ? 6 : week - weekStart;
        }
        return week == WEEK_START_WITH_SAT ? 0 : week;
    }

    private  List<WeekDay> getWeekDay() {

        Calendar calendar = Calendar.getInstance();
        WeekDay weekDay2 = new WeekDay();
        // 获取星期的显示名称，例如：周一、星期一、Monday等等
        weekDay2.week = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.CHINESE);
        weekDay2.day = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        weekDay2.year=calendar.get(Calendar.YEAR);
        weekDay2.month=calendar.get(Calendar.MONTH)+1;
        weekDay2.dayof=calendar.get(Calendar.DAY_OF_MONTH);
        this.mInitialDate = weekDay2;

       // int firstDayOfWeek = calendar.getFirstDayOfWeek();

        int week = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        int distance=getMonthViewStartDiff(week,WEEK_START_WITH_MON);
        List<WeekDay> list = new ArrayList<>();
        if(distance!=0){
            calendar.add( Calendar. DATE, -distance);
        }

        for(int i=0;i<7;i++){
            WeekDay weekDay = new WeekDay();
            // 获取星期的显示名称，例如：周一、星期一、Monday等等
            weekDay.week = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.CHINESE);
            weekDay.day = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            weekDay.year=calendar.get(Calendar.YEAR);
            weekDay.month=calendar.get(Calendar.MONTH)+1;
            weekDay.dayof=calendar.get(Calendar.DAY_OF_MONTH);

            list.add(weekDay);
            calendar.add( Calendar.DATE, 1);

        }
        return list;
      /*  // 获取本周的第一天
        int firstDayOfWeek = calendar.getFirstDayOfWeek();


        List<WeekDay> list = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + i);
            WeekDay weekDay = new WeekDay();
            // 获取星期的显示名称，例如：周一、星期一、Monday等等
            weekDay.week = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.CHINESE);
            weekDay.day = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            weekDay.year=calendar.get(Calendar.YEAR);
            weekDay.month=calendar.get(Calendar.MONTH)+1;
            weekDay.dayof=calendar.get(Calendar.DAY_OF_MONTH);

            list.add(weekDay);
        }
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        WeekDay weekDay = new WeekDay();
        weekDay.week = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.CHINESE);
        weekDay.day = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        weekDay.year=calendar.get(Calendar.YEAR);
        weekDay.month=calendar.get(Calendar.MONTH)+1;
        weekDay.dayof=calendar.get(Calendar.DAY_OF_MONTH);


        list.add(weekDay);*/

    }

    private float mBottomDistance=0;
    private Paint mPathPaint;
    private final int SPLITNUM=7;
    public WeekView(Context var1, AttributeSet var2) {
        super(var1, var2);
        dates =  getWeekDay();
        mBottomDistance=DensityUtils.dp2floatpx(var1,3);
        mPathPaint = new Paint();
        mPathPaint.setColor(Color.WHITE);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStrokeWidth(mHollowCircleStroke);
    }

    public void setOnClickWeekViewListener(OnClickWeekViewListener onClickWeekViewListener){
        mOnClickWeekViewListener = onClickWeekViewListener;
    }

    public WeekView(Context context,  OnClickWeekViewListener onClickWeekViewListener) {
        super(context);
        dates =  getWeekDay();
        mOnClickWeekViewListener = onClickWeekViewListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mWidth!=0){//上一次的宽度    可能发生屏幕旋转
            if(mWidth!=getWidth()){
                mRectList.clear();
            }
        }
        mWidth = getWidth();
        //mHeight = getHeight();
        //为了与月日历保持一致，往上压缩一下,5倍的关系
        mHeight = (int) (getHeight() - DensityUtils.dp2px(getContext(), 2));

        if(ArrayUtils.isEmpty(mRectList)){
            for(int k=0;k<SPLITNUM;k++){
                Rect rect = new Rect(k * mWidth / SPLITNUM, 0, k * mWidth / SPLITNUM + mWidth / SPLITNUM, mHeight);
                mRectList.add(rect);
            }
        }

        for (int i = 0; i < 7; i++) {
            /*Rect rect = new Rect(i * mWidth / 7, 0, i * mWidth / 7 + mWidth / 7, mHeight);
            mRectList.add(rect);*/
            Rect rect=mRectList.get(i);
            WeekDay date = dates.get(i);
            Paint.FontMetricsInt fontMetrics = mSorlarPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;

            if (date.equals(mInitialDate)) {

                //初始化Path
                Path path = new Path();
                //Path.Direction.CW顺时针绘制圆 Path.Direction.CCW逆时针绘制圆
                path.addCircle(rect.centerX(), rect.centerY(), mSelectCircleRadius - mHollowCircleStroke, Path.Direction.CW);

                if(mSelectDate!=null){
                    if(mSelectDate.equals(mInitialDate)){
                        canvas.drawPath(path, mPathPaint);
                    }else {
                        mPathPaint.setColor(0X80FFFFFF);
                        canvas.drawPath(path, mPathPaint);
                        mPathPaint.setColor(Color.WHITE);
                    }
                }
                else
                    canvas.drawPath(path, mPathPaint);

               // mSorlarPaint.setColor(mHollowCircleColor);

               // canvas.drawCircle(rect.centerX(), rect.centerY(), mSelectCircleRadius, mSorlarPaint);
               // mSorlarPaint.setColor(Color.WHITE);
               // canvas.drawCircle(rect.centerX(), rect.centerY(), mSelectCircleRadius - mHollowCircleStroke, mSorlarPaint);
                mSorlarPaint.setColor(mSolarTextColor);
                canvas.drawText("今", rect.centerX(), baseline, mSorlarPaint);


            } else if (mSelectDate != null && date.equals(mSelectDate)) {
                mSorlarPaint.setColor(mSelectCircleColor);
                canvas.drawCircle(rect.centerX(), rect.centerY(), mSelectCircleRadius, mSorlarPaint);
                mSorlarPaint.setColor(Color.WHITE);
                canvas.drawText(   date.dayof + "", rect.centerX(), baseline, mSorlarPaint);
            } else {
                mSorlarPaint.setColor(mSolarTextColor);
                canvas.drawText(date.dayof + "", rect.centerX(), baseline, mSorlarPaint);
                //绘制农历
                //drawLunar(canvas, rect, baseline,i);
                //绘制节假日
                //drawHolidays(canvas, rect, date, baseline);
                //绘制圆点
                //drawPoint(canvas, rect, date, baseline);

            }

            if(date.liveStatus>0){
                mSorlarPaint.setColor(date.liveStatus==2? Color.WHITE:mPointColor);
                canvas.drawCircle(rect.centerX(), mHeight-mBottomDistance, mPointSize, mSorlarPaint);
            }

        }
    }

    private void drawLunar(Canvas canvas, Rect rect, int baseline, int i) {
        if (isShowLunar) {
            mLunarPaint.setColor(mLunarTextColor);
            String lunar = lunarList.get(i);
            canvas.drawText(lunar, rect.centerX(), baseline + getHeight() / 4, mLunarPaint);
        }
    }


    private void drawHolidays(Canvas canvas, Rect rect, WeekDay date, int baseline) {
        if (isShowHoliday) {
        /*    if (holidayList.contains(date.toString())) {
                mLunarPaint.setColor(mHolidayColor);
                canvas.drawText("休", rect.centerX() + rect.width() / 4, baseline - getHeight() / 4, mLunarPaint);

            } else if (workdayList.contains(date.toString())) {
                mLunarPaint.setColor(mWorkdayColor);
                canvas.drawText("班", rect.centerX() + rect.width() / 4, baseline - getHeight() / 4, mLunarPaint);
            }*/
        }
    }

    public void drawPoint(Canvas canvas, Rect rect, WeekDay date, int baseline) {
        if (pointList != null && pointList.contains(date.toString())) {
            mLunarPaint.setColor(mPointColor);
            canvas.drawCircle(rect.centerX(), baseline - getHeight() / 3, mPointSize, mLunarPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            for (int i = 0; i < mRectList.size(); i++) {
                Rect rect = mRectList.get(i);
                if (rect.contains((int) e.getX(), (int) e.getY())) {
                    WeekDay selectDate = dates.get(i);

                    if(selectDate.equals(getSelectDate())) {
                        return true ;
                    }
                    setSelectDate(selectDate);
                    if(null!=mOnClickWeekViewListener){
                        mOnClickWeekViewListener.onClickCurrentWeek(selectDate);
                    }
                     break;
                }
            }
            return true;
        }
    });


    public boolean contains(WeekDay date) {
        return dates.contains(date);
    }

    public interface OnClickWeekViewListener {

        void onClickCurrentWeek(WeekDay date);

    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed,left,top,right,bottom);
        if(!mHasInit){
            mHasInit=true;
            if(null!=mOnViewInitListener)
                mOnViewInitListener.onViewInit(mInitialDate);
        }
    }
}
