package com.huatu.widget.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gensee.rtmpresourcelib.R;
import com.huatu.utils.DensityUtils;


/**
 * Created by Administrator on 2017/10/16.
 */
public class CirclePointTextView extends TextView{


    Paint myPaint ;
    float marginRight = 0;
    float marginTop = 0;
    private float mPointRadius = 0.0f;


    public void setMarginRight(float rightMargin,float topMargin){
        this.marginRight=rightMargin;
        this.marginTop=topMargin;
    }
    private float outStrokeWidth=0.0f;
    private boolean isDotsShow = true;
    public CirclePointTextView(Context context) {
        super(context);
        init();
    }

    public CirclePointTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public CirclePointTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
         TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndexImage);
        if (a != null) {
            marginTop = a.getDimension(R.styleable.IndexImage_PointMarginTop, 0);
            marginRight = a.getDimension(R.styleable.IndexImage_PointMarginRight, 0);
            a.recycle();
        }
        init();
    }


    private void init(){
        myPaint  =new Paint();
        myPaint.setAntiAlias(true);
       /* myPaint.setColor(Color.rgb(255, 62, 62));
        */

        mPointRadius =DensityUtils.dp2floatpx(getContext(),3) ;
        outStrokeWidth= DensityUtils.dp2floatpx(getContext(), 1);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        float x = mPointRadius+marginRight;
        float y = mPointRadius+marginTop;
        //x = width / 2;
        //y =0;// mTopLineLen;
        if(isDotsShow){

            // 让画出的图形是实心的
            myPaint.setStyle(Paint.Style.FILL);
            myPaint.setStrokeWidth(1);
            //myPaint.setColor(Color.rgb(254, 0, 0));
            myPaint.setColor(Color.parseColor("#ec9182"));
            canvas.drawCircle(width - x, y, mPointRadius, myPaint);

            myPaint.setStyle(Paint.Style.STROKE);
            // 设置画出的线的 粗细程度
            myPaint.setStrokeWidth(outStrokeWidth);
            myPaint.setColor(Color.WHITE);
            canvas.drawCircle(width - x, y, mPointRadius, myPaint);
        }

    }

    public void setDotsVisibility(boolean isShow){
        isDotsShow = isShow;
    }

    public void hideDots(){
        isDotsShow = false;
        invalidate();
    }

    public void setShowDots(boolean isShow){
        isDotsShow = isShow;
        invalidate();
    }
}
