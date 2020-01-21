package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2018\7\3 0003.
 */

public class CoverTextView extends TextView {
    String myText;
    public CoverTextView(Context context) {
        super(context);

    }

    public CoverTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setText(String tmpStr){
        myText=tmpStr;
        this.invalidate();
    }

   /* @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if(!TextUtils.isEmpty(myText)){

            Paint paint = new Paint();

            //canvas.drawColor(Color.parseColor("#4c000000"));
            paint.setColor(Color.parseColor("#4c000000"));
            float radius=DensityUtils.dp2floatpx(getContext(), 3);
            RectF rectF = new RectF(0f, 0f, getWidth(), getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);

            paint.setColor(0xffffffff);
            paint.setTextSize(sp2px(this.getContext(), 14.50f));

            paint.setAntiAlias(true);

            Paint.FontMetrics fontMetrics=paint.getFontMetrics();
            // float fontHeight=Math.abs(fontMetrics.ascent);//fontMetrics.ascent;//fontMetrics.bottom-fontMetrics.top;


            int width=this.getWidth();
            int heigth=this.getHeight();

            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(myText + "", width / 2, (heigth - fontMetrics.bottom - fontMetrics.top) / 2, paint);

            canvas.restore();

        }

    }*/

}
