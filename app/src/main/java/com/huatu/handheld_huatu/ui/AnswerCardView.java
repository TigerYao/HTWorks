package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjx on 2019\2\18 0018.
 */

public class AnswerCardView extends View {

    protected int mWidth;


    protected int mSolarTextColor;//公历字体颜色

    protected float mSolarTextSize;

    protected Paint mSorlarPaint;

    protected float mSelectCircleRadius;//选中圆的半径
    protected int mSelectCircleColor;//选中圆的颜色


    protected int mHolidayColor;
    protected int mWorkdayColor;

    private final int SPLITNUM=5;
    private Paint mPathPaint;
    protected int mHollowCircleColor;//空心圆颜色
    protected float mHollowCircleStroke;//空心圆粗细

    /**
     * 日历的行数
     */
    private int mLineCount;

    /**
     * 日历高度
     */
    private int mHeight;

    /**
     * 每一项的宽度
     */
    protected int mItemWidth;

    /**
     * 每一项的高度
     */
    protected int mItemHeight;
    /**
     * Text的基线
     */
    protected float mTextBaseLine;

    public void setItemHeight(int itemHeight) {
        this.mItemHeight = itemHeight;
    }

    OnClickCardViewListener mOnClickWeekViewListener ;
    public interface OnClickCardViewListener {
         void onClickCurrentCard(CardAnswer data);

    }
    protected List<CardAnswer> mPointList;

    public void setDateList(List<CardAnswer> pointList){
        mPointList=pointList;
        int size=ArrayUtils.size(mPointList);
        boolean hasMore=size%SPLITNUM==0? false:true;
        mLineCount=size/SPLITNUM+(hasMore? 1:0);
        mHeight=(mLineCount)*mItemHeight;

        requestLayout();
       // invalidate();
    }

    public AnswerCardView(Context context, AttributeSet var2) {
        super(context,var2);
        init(context);
    }

    public AnswerCardView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){

        mItemHeight=DensityUtils.dp2px(context, 50);
        mSolarTextColor = Color.parseColor("#ffffff");//#4A4A4A

        mSolarTextSize = DensityUtils.sp2px(context,16);


        mSelectCircleRadius = DensityUtils.dp2px(context,17);
        mSelectCircleColor = Color.parseColor("#49CF9E");//5D9AFF


        mHollowCircleColor = Color.parseColor("#FF4A4A4A");
        mHollowCircleStroke = DensityUtils.dp2px(context, 1) ;


        mHolidayColor =Color.YELLOW;
        mWorkdayColor = Color.BLUE;


        mSorlarPaint = getPaint(mSolarTextColor, mSolarTextSize);

        Paint.FontMetrics metrics = mSorlarPaint.getFontMetrics();
        mTextBaseLine = mItemHeight / 2 - metrics.descent + (metrics.bottom - metrics.top) / 2;

        mPathPaint = new Paint();
        mPathPaint.setColor(mHollowCircleColor);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setAntiAlias(true);
        mPathPaint.setStrokeWidth(mHollowCircleStroke);

    }

    private Paint getPaint(int paintColor, float paintSize) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setTextSize(paintSize);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    /**
     * 点击的x、y坐标
     */
    private float mX, mY;
    private GestureDetector mGestureDetector ;

    public void setOnClickCardViewListener(OnClickCardViewListener onClickWeekViewListener){
        mOnClickWeekViewListener = onClickWeekViewListener;
        if(null!=mOnClickWeekViewListener){
            mGestureDetector= new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                    mX = e.getX();
                    mY = e.getY();
                    int indexX = (int) mX / mItemWidth;
                    if (indexX >= SPLITNUM) {
                        indexX = SPLITNUM-1;
                    }
                    int indexY = (int) mY / mItemHeight;
                    int position = indexY * SPLITNUM + indexX;// 选择项

                    int count =  ArrayUtils.size(mPointList);;
                    if (position >= 0 && position < count){
                        CardAnswer selectDate = mPointList.get(position);

                        if(null!=mOnClickWeekViewListener){
                            mOnClickWeekViewListener.onClickCurrentCard(selectDate);
                        }
                    }
                    return true;
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mLineCount != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private void draw(Canvas canvas, CardAnswer calendar, int i, int j, int d) {
        int x = j * mItemWidth ;
        int y = i * mItemHeight;

        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2+ y;

        //画圆
        if(calendar.status==0){

            //初始化Path
            Path path = new Path();
            //Path.Direction.CW顺时针绘制圆 Path.Direction.CCW逆时针绘制圆
            path.addCircle( cx,cy, mSelectCircleRadius - mHollowCircleStroke, Path.Direction.CW);

            canvas.drawPath(path, mPathPaint);

            mSorlarPaint.setColor(Color.BLACK);
           // canvas.drawText(calendar.index, rect.centerX(), baseline, mSorlarPaint);

            canvas.drawText(calendar.index,  cx,baselineY,mSorlarPaint);
        }else if (calendar.status==1) {//正确

            mSorlarPaint.setColor(mSelectCircleColor);

            canvas.drawCircle(cx, cy, mSelectCircleRadius, mSorlarPaint);

            mSorlarPaint.setColor(Color.WHITE);
            canvas.drawText(calendar.index,  cx,baselineY,mSorlarPaint);
        } else {
            mSorlarPaint.setColor(0XFFFF3F47);
            canvas.drawCircle(cx, cy, mSelectCircleRadius, mSorlarPaint);
            mSorlarPaint.setColor(Color.WHITE);
            canvas.drawText(calendar.index,  cx,baselineY,mSorlarPaint);

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLineCount == 0)
            return;
        mItemWidth = (getWidth() ) / SPLITNUM;
        int count =  ArrayUtils.size(mPointList);;

        int d = 0;
        for (int i = 0; i < mLineCount; i++) {
            for (int j = 0; j < SPLITNUM; j++) {
                if(d>=count) return;
                CardAnswer calendar = mPointList.get(d);
                draw(canvas, calendar, i, j, d);
                ++d;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null!=mOnClickWeekViewListener&&(null!=mGestureDetector))
            return mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }





    public static class CardAnswer {
        /** 星期的显示名称*/
        public String index;
        /** 对应的日期*/
        public String day;

        public int status;



        @Override
        public boolean equals(Object var1) {
            if(this == var1) {
                return true;
            } else {
                if(var1 instanceof CardAnswer) {
                    CardAnswer var2 = (CardAnswer)var1;
                    return var2.status==this.status&&(var2.index.equals(this.index))&&(var2.day.equals(this.day));
                }

                return super.equals(var1);
            }
        }
    }
}
