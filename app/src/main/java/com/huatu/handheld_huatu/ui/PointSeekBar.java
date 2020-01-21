package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;

import java.util.List;

/**
 * Created by cjx on 2018\7\24 0024.
 */

public class PointSeekBar extends AppCompatSeekBar {

    public static class Point{

        public float X;
        public int Y;
        public long id;
        public String title;
        public String userAnswer;

        public int correct;
        public int time;

        public Point(float percentX,int pointY,long pointId,String pointTitle){
            X=percentX;
            Y=pointY;
            id=pointId;
            title=pointTitle;
        }
    }

    private boolean isDotsShow = false;
    private float outStrokeWidth=0.0f;
    private float mPointRadius = 0.0f;

/*    private int mWidth;
    private int mHeight;*/

    Paint myPaint ;
    List<Point> mSeekPoint;

    public List<Point> getAllPoint(){
        return mSeekPoint;
    }

    private  int mTouchSlop;
    private  onPopupListener mOnPopUplistener;
    public void setOnPopUplistener(onPopupListener onpopupListener){
        this.mOnPopUplistener=onpopupListener;
    }

    public interface onPopupListener{
         void onViewPopUp(View view,Point value);
    }

    public PointSeekBar(Context context) {
        this(context, null);
        init();
    }

    public PointSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PointSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getAvailableWidth(){
        int width = getWidth();

        return width - this.getPaddingLeft() - this.getPaddingRight();
    }

    private boolean isHandTouch=false;
    private PointSeekBar.Point mTouchPoint;
    private float   mTouchStartX=0;

    private float mAdjustDistance=0;

    public float getAdjustDistance(){
        return mAdjustDistance;
    }
    private void init(){
        myPaint  =new Paint();
        myPaint.setAntiAlias(true);

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
       /* myPaint.setColor(Color.rgb(255, 62, 62));
        */
        mAdjustDistance=DensityUtils.dp2floatpx(getContext(),4);
        mPointRadius = this.getContext().getResources().getDimension(R.dimen.seekbar_point_radius);
        outStrokeWidth= DensityUtils.dp2floatpx(getContext(), 1);
        this.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if(ArrayUtils.isEmpty(mSeekPoint)) return false;

                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                           isHandTouch=false;
                           int availableWidth = getWidth() - PointSeekBar.this.getPaddingLeft() - PointSeekBar.this.getPaddingRight();
                           for(PointSeekBar.Point point:mSeekPoint){

                                if(point.X>=0.99&&(Math.abs( event.getX()-point.X*getWidth()+PointSeekBar.this.getPaddingRight()+mPointRadius)<=mTouchSlop)){
                                    isHandTouch=true;
                                    mTouchStartX=event.getX();
                                    mTouchPoint=point;
                                    break;

                                }
                                else if(Math.abs( event.getX()-point.X*availableWidth-PointSeekBar.this.getPaddingLeft()-mAdjustDistance)<=mTouchSlop){
                                    isHandTouch=true;
                                    mTouchStartX=event.getX();
                                    mTouchPoint=point;
                                    break;
                                }
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(isHandTouch){
                                if(Math.abs(event.getX()-mTouchStartX)>mTouchSlop){
                                    isHandTouch=false;
                                }
                            }
                              break;
                        case MotionEvent.ACTION_CANCEL:
                            isHandTouch=false;
                            break;
                        case MotionEvent.ACTION_UP:
                            LogUtils.e("OnTouchListener",isHandTouch+"");
                            if(isHandTouch){
                                isHandTouch=false;
                              //  ToastUtils.showShort("test");
                                if(mOnPopUplistener!=null)
                                    mOnPopUplistener.onViewPopUp(v,mTouchPoint);


                                //如果需要当前进度不跳转，改为true即可
                                return true;
                            }
                            break;

                    }
                    return isHandTouch;
                   // return !isSeekBarDraggable;
                }

        });

     }

     @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
       /* mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
         //configShape();
        setMeasuredDimension(mWidth, mHeight);*/

       //LogUtils.e("onMeasure",getPaddingLeft()+""+getLeft());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int availableWidth = width - this.getPaddingLeft() - this.getPaddingRight();
        //x = width / 2;
        //y =0;// mTopLineLen;
        if(isDotsShow){
            LogUtils.e("onDraw",this.getPaddingLeft()+"");
            if(!ArrayUtils.isEmpty(mSeekPoint)){

                for(Point point:mSeekPoint){
                    // 让画出的图形是实心的
                    myPaint.setStyle(Paint.Style.FILL);
                    myPaint.setStrokeWidth(1);
                    //myPaint.setColor(Color.rgb(254, 0, 0));
                    myPaint.setColor(Color.parseColor("#5163F1"));
                    float postionX=0;

                    if(point.X>=0.99){
                        postionX=point.X*width-mPointRadius-this.getPaddingRight();
                    }
                    else
                        postionX=point.X*availableWidth+this.getPaddingLeft()+mAdjustDistance;

                    LogUtils.e("onDraw",postionX+","+width+","+getLeft());
                    canvas.drawCircle(postionX, point.Y, mPointRadius, myPaint);

                    myPaint.setStyle(Paint.Style.STROKE);
                    // 设置画出的线的 粗细程度
                    myPaint.setStrokeWidth(outStrokeWidth);
                    myPaint.setColor(Color.parseColor("#8C99FF"));
                    canvas.drawCircle(postionX, point.Y, mPointRadius, myPaint);

                }
            }
        }

    }

    public void setDotsVisibility(boolean isShow, List<Point> listPoint){
        isDotsShow = isShow;
        mSeekPoint=listPoint;
        invalidate();
    }

}
