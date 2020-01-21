package com.huatu.Indicator;

/**
 * Created by Administrator on 2018\10\22 0022.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by walkingMen on 2016/4/27.
 */
public class SLoadingIndicatorView extends View {
    private static final String TAG = SLoadingIndicatorView.class.getSimpleName();
    public static final int DefaultScale = 0;
    public static final int LineScale = 1;


    //Sizes (with defaults in DP)
    public static final int DEFAULT_SIZE = 45;

    //attrs
    int mIndicatorId;
    int mIndicatorColor=Color.parseColor("#888888");

    Paint mPaint;
    BaseExtendIndicatorController mIndicatorController;

    private boolean mHasAnimation;

    public SLoadingIndicatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public SLoadingIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SLoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SLoadingIndicatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mIndicatorId=DefaultScale;
        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        applyIndicator();
    }

    private void applyIndicator() {
        switch (mIndicatorId) {
            case DefaultScale:
                mIndicatorController = new LineScaleWaveIndicator(new int[]{});
                break;
            case LineScale:
                mIndicatorController = new LineScaleWaveIndicator(new int[]{});
                break;

          /*  case LineScalePulseOutWave:
                int[] waveFloats = new int[4];

                int w = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                int h = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                this.measure(w, h);
                int height = this.getMeasuredHeight();
                for (int i = 0; i < 4; i++) {
                    int intRandom = (int) (1 + Math.random() * (4 - 1 + 1));
                    int v = intRandom * 100;
                    waveFloats[i] = v;
                    Log.i(TAG, "applyIndicator: " + waveFloats[i]);
                }
                mIndicatorController = new LineScalePulseOutWaveIndicator(waveFloats);
                bringToFront();
                break;*/
        }
        mIndicatorController.setTarget(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation) {
            mHasAnimation = true;
            //applyAnimation();
        }
    }

    boolean mIsRunning=false;
    private boolean isRunning(){
        return mIsRunning;
    }

    private boolean mAutoRun=true;
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

    void start() {
        mIsRunning=true;
        mIndicatorController.initOrStartAnimation();
    }

    private  void stopAnimation() {
        if(null!=mIndicatorController){
            mIsRunning=false;
            mIndicatorController.stopAnimation();
            // mIndicatorController=null;
        }
    }

   private void changeRunStateByVisibility(int visibility) {
        if (mIndicatorController == null) {
            return;
        }
        if (visibility == VISIBLE) {
            if (mNeedRun) {
                start();
            }
        } else {
            if (isRunning()) {
                mNeedRun = true;
                stopAnimation();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAutoRun) {
            if (getVisibility() == VISIBLE)
                start();
            else
                mNeedRun = true;
        }
    }

    /**
     * onAttachedToWindow是在第一次onDraw前调用的。也就是我们写的View在没有绘制出来时调用的，但只会调用一次。
     * onDetachedFromWindow相反
     */


    /**
     * This is called when the view is detached from a window. At this point it no longer has a surface for drawing.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIndicatorController.setAnimationStatus(BaseExtendIndicatorController.AnimStatus.CANCEL);
    }

    void drawIndicator(Canvas canvas) {
        mIndicatorController.draw(canvas, mPaint);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
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



    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }
}

