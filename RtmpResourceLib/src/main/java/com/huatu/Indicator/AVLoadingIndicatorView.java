package com.huatu.Indicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import com.gensee.rtmpresourcelib.R;


/**
 * Created by Jack on 2015/10/15
 *
 .BallPulse,
 .BallGridPulse,
 .BallClipRotate,
 .BallClipRotatePulse,
 .SquareSpin,
 .BallClipRotateMultiple,
 .BallPulseRise,
 .BallRotate,
 .CubeTransition,
 .BallZigZag,
 .BallZigZagDeflect,
 .BallTrianglePath,
 .BallScale,
 .LineScale,
 .LineScaleParty,
 .BallScaleMultiple,
 .BallPulseSync,
 .BallBeat,
 .LineScalePulseOut,
 .LineScalePulseOutRapid,
 .BallScaleRipple,
 .BallScaleRippleMultiple,
 .BallSpinFadeLoader,
 .LineSpinFadeLoader,
 .TriangleSkewSpin,
 .Pacman,
 .BallGridBeat,
 .SemiCircleSpin
 *
 */
public class AVLoadingIndicatorView extends View{


    private boolean mCanDestory=true;

    public void setCanDetachedFromWindow(boolean canDetachedFromWindow){
        mCanDestory=canDetachedFromWindow;
    }
    //indicators
    public static final int BallPulse=0;



    @IntDef(flag = true,
            value = {
                    BallPulse,

            })
    public @interface Indicator{}

    //Sizes (with defaults in DP)
    public static final int DEFAULT_SIZE=45;

    //attrs
    int mIndicatorId;
    int mIndicatorColor;

    Paint mPaint;

    BaseIndicatorController mIndicatorController;

    private boolean mHasAnimation;


    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }

    public AVLoadingIndicatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public AVLoadingIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AVLoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AVLoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AVLoadingIndicatorView);
        mIndicatorId=a.getInt(R.styleable.AVLoadingIndicatorView_indicator, BallPulse);
        mIndicatorColor=a.getColor(R.styleable.AVLoadingIndicatorView_indicator_color, Color.WHITE);
        a.recycle();
        mPaint=new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        applyIndicator();
    }

    private void applyIndicator(){
        switch (mIndicatorId){
            case BallPulse:
                mIndicatorController=new BallPulseIndicator();
                break;

        }
        mIndicatorController.setTarget(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width  = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int defaultSize,int measureSpec){
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation){
            mHasAnimation=true;
          //  applyAnimation();
        }
    }

    void drawIndicator(Canvas canvas){
        if(null!=mIndicatorController)
        mIndicatorController.draw(canvas,mPaint);
    }

    boolean mIsRunning=false;
    private boolean isRunning(){
        return mIsRunning;
    }

    private   void applyAnimation(){
        mIsRunning=true;
        mIndicatorController.createAnimation();
    }

    private  void stopAnimation() {
        if(null!=mIndicatorController){
            mIsRunning=false;
            mIndicatorController.stopAnimation();
           // mIndicatorController=null;
        }
    }


    /**
     * Start the loading animation
     */
    public void start() {
         applyAnimation();
        mNeedRun = false;
    }

    /**
     * Stop the loading animation
     */
    public void stop() {
         stopAnimation();
        mNeedRun = false;
    }

    private boolean mAutoRun=false;
    private boolean mNeedRun;
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        changeRunStateByVisibility(visibility);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        changeRunStateByVisibility(visibility);
    }

    private void changeRunStateByVisibility(int visibility) {
        if (mIndicatorController == null) {
            return;
        }
        if (visibility == VISIBLE) {
            if (mNeedRun) {
                //Log.e("AVLoadingIndicatorView","start");
                start();
            }
        } else {
               if (isRunning()) {
                    mNeedRun = true;
                    stopAnimation();;
                  // Log.e("AVLoadingIndicatorView", "stop");
               }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAutoRun ) {
            if (getVisibility() == VISIBLE)
                 start();
            else
                mNeedRun = true;
        }
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        stopAnimation();
        if(mCanDestory)
           mIndicatorController=null;
    }

   /* @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }*/
}
