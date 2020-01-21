package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.utils.DensityUtils;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

/**
 * Created by cjx on 2018\7\27 0027.
 */

public class RoundbgView extends View {
    Paint myPaint ;
    private float mPointRadius = 0.0f;

    private float outStrokeWidth=0.0f;
    private boolean isDotsShow = false;
    public RoundbgView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RoundbgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        QMUIRoundButtonDrawable bg = QMUIRoundButtonDrawable.fromAttributeSet(context, attrs, defStyleAttr);
        QMUIViewHelper.setBackgroundKeepingPadding(this, bg);
        myPaint  =new Paint();
        myPaint.setAntiAlias(true);
       /* myPaint.setColor(Color.rgb(255, 62, 62));
        *///#5163F1

        mPointRadius =DensityUtils.dp2px(getContext(),18);// this.getContext().getResources().getDimension(com.gensee.rtmpresourcelib.R.dimen.circle_point_radius);
        outStrokeWidth= DensityUtils.dp2floatpx(getContext(), 2);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();


        //x = width / 2;
        //y =0;// mTopLineLen;
        if(isDotsShow){

            // 让画出的图形是实心的
     /*       myPaint.setStyle(Paint.Style.FILL);
            myPaint.setStrokeWidth(1);
            //myPaint.setColor(Color.rgb(254, 0, 0));
            myPaint.setColor(Color.parseColor("#5163F1"));
            canvas.drawCircle(width/2, width/2, mPointRadius, myPaint);*/

            myPaint.setStyle(Paint.Style.STROKE);
            // 设置画出的线的 粗细程度
            myPaint.setStrokeWidth(outStrokeWidth);
            myPaint.setColor(Color.parseColor("#5163F1"));
            canvas.drawCircle(width/2, width/2, mPointRadius, myPaint);
        }

    }

    public void setDotsVisibility(boolean isShow){
        if(isDotsShow==isShow) return;
        isDotsShow = isShow;
        invalidate();
    }
}
