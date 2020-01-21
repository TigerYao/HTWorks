package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.utils.DensityUtils;

/**
 * Created by cjx on 2018\7\21 0021.
 */

public class ProgressTextView extends View {

    private int mWidth;
    private int mHeight;
    private int mProgress=0;
    private String mText="";
    Paint mPaint;


    public ProgressTextView(Context context) {
        super(context);
        setup();
       // setup(context, null);
    }

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
        //setup(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        //configShape();
        setMeasuredDimension(mWidth, mHeight);
    }

    private void setup(){
         mPaint = new Paint();
         mPaint.setAntiAlias(true);
         mPaint.setTextSize(DensityUtils.sp2px(getContext(),10f));
         mPaint.setColor(Color.parseColor("#FF3F47"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (TextUtils.isEmpty(mText)) {
           return;
        }
        Rect rect = new Rect();
        this.mPaint.getTextBounds(this.mText, 0, this.mText.length(), rect);
       // int x = (getWidth() / 2) - rect.centerX();

        int x=0;
        int curLeft=mProgress*mWidth/100;
        if(curLeft<rect.width()) x=0;
        else if((curLeft+rect.width())>=mWidth){
            x=mWidth-rect.width()-DensityUtils.dp2px(getContext(),1);
        }else {
            x=curLeft-rect.width()/2;
        }

        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(this.mText, x, y, this.mPaint);
    }

    public void setTextProgress(String text,int progress){
        this.mText=text;
        this.mProgress=progress;
        this.invalidate();
    }
}
