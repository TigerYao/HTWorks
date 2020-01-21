package com.huatu.calculate;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.gensee.rtmpresourcelib.R;

/**
 * Created by Liming on 2016/5/30.
 */
public class FixedRatioFrameLayout extends FrameLayout {


    public static final float defaultRatio = 1.0f;
    private float mRatio = defaultRatio;

    public void setAutoMeasure(boolean canAuto){

    }

    public FixedRatioFrameLayout(Context context) {
        super(context);
    }

    public FixedRatioFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public void setRatia(float ratio) {
        this.mRatio = ratio;
    }

    public FixedRatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FixedRatioImageView);
        if(null!=typedArray){
            mRatio = typedArray.getFloat(R.styleable.FixedRatioImageView_ratio, defaultRatio);
            typedArray.recycle();
         }
     }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mRatio != 0) {
            float height = width * mRatio;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

   /* protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FixedRatioImageView);
        if(a!=null){
            ratio = a.getFloat(R.styleable.FixedRatioImageView_ratio, ratio);
            a.recycle();
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * ratio);
        setMeasuredDimension(width, height);
    }*/
}
