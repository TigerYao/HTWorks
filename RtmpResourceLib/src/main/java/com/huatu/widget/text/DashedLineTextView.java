package com.huatu.widget.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.huatu.utils.DensityUtils;


/**
 * Created by Administrator on 2016/11/14.
 */
public class DashedLineTextView extends TextView {

    float fontWidth=10;
    Paint paint ;
    public DashedLineTextView(Context context) {
        super(context);
        init();
    }

    public DashedLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init(){
        fontWidth= DensityUtils.dp2px(getContext(), 92);
        paint = new Paint();
        paint.setStrokeWidth(DensityUtils.dp2floatpx(getContext(),0.7f));//线条宽度
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#FFC4C4C4"));
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        //float fontWidth= DensityUtils.dp2px(getContext(), 86);

        float width=getWidth();
        float height=getHeight();



        Path path = new Path();
        path.moveTo(0, height/2);
        path.lineTo((width-fontWidth)/2, height/2);

        path.moveTo((width+fontWidth)/2, height/2);
        path.lineTo(getWidth(),  height/2);

      //  PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
       // paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }
}
