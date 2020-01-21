package com.huatu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.gensee.rtmpresourcelib.R;
import com.huatu.utils.DensityUtils;


/**
 * Created by cjx

 */
public class MessageImageView extends ImageView {


    Paint myPaint ;
    float marginRight = 0;
    float marginTop = 0;
    private float mPointRadius = 0.0f;

    private float outStrokeWidth=0.0f;
    private int  mMessageCount = 1;
    public MessageImageView(Context context) {
        super(context);

        init(context);
    }

    public MessageImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        //init();
    }

    public MessageImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndexImage);
        if (a != null) {
            marginTop = a.getDimension(R.styleable.IndexImage_PointMarginTop, 0);
            marginRight = a.getDimension(R.styleable.IndexImage_PointMarginRight, 0);
            a.recycle();
        }
        init(context);
    }

    private void init(Context context){
        myPaint  =new Paint();
        myPaint.setAntiAlias(true);

        myPaint.setTextSize(DensityUtils.sp2px(context,8));
        myPaint.setTextAlign(Paint.Align.CENTER);
       /* myPaint.setColor(Color.rgb(255, 62, 62));
        */

        mPointRadius =  this.getContext().getResources().getDimension(R.dimen.circle_point_radius);
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
        if(mMessageCount>0){

          // 让画出的图形是实心的
            myPaint.setStyle(Paint.Style.FILL);
            //myPaint.setStrokeWidth(1);
            //myPaint.setColor(Color.rgb(254, 0, 0));
            myPaint.setColor(Color.parseColor("#FF221B"));
            canvas.drawCircle(width-x, y, mPointRadius, myPaint);

        /*    myPaint.setStyle(Paint.Style.STROKE);
            // 设置画出的线的 粗细程度
            myPaint.setStrokeWidth(outStrokeWidth);
            myPaint.setColor(Color.WHITE);
            canvas.drawCircle(width-x, y, mPointRadius, myPaint);*/

            Rect rect = new Rect((int)(width-marginRight-2*mPointRadius), (int)marginTop, (int)(width-marginRight), (int)(marginTop+2*mPointRadius));
            Paint.FontMetricsInt fontMetrics = myPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            myPaint.setColor(Color.WHITE);

            canvas.drawText(mMessageCount>99?"99+":String.valueOf(mMessageCount), rect.centerX(), baseline, myPaint);
         }
    }

    public void setMessageNum(int messageNum){
        this.mMessageCount = messageNum;
        this.invalidate();
    }

}
