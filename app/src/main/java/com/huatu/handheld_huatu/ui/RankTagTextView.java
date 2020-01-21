package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.EventLog;
import android.widget.TextView;

import com.huatu.utils.DensityUtils;

import static android.graphics.Paint.Style.FILL;

/**
 * Created by Administrator on 2018\7\3 0003.
 */

public class RankTagTextView extends TextView {
    String myText;
    Paint mPaint;
    float moffset=0;
    float mArrowWidth=0;
    public RankTagTextView(Context context) {
        super(context);
        mPaint= getPaint(0xFFFF3F47);
        moffset= DensityUtils.dp2floatpx(context,3);
        mArrowWidth=DensityUtils.dp2floatpx(context,6);
    }

    public RankTagTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint= getPaint(0xFFFF3F47);
        moffset= DensityUtils.dp2floatpx(context,6);
        mArrowWidth=DensityUtils.dp2floatpx(context,6);
    }


/*    public void setText(String tmpStr){
        myText=tmpStr;
        this.invalidate();
    }*/

    private int mArrowType=1;//0 ,1 up,2 down

    public void setArrowType(int arrowType){
        mArrowType=arrowType;
    }

    private Paint getPaint(int paintColor) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
      //  paint.setTextSize(paintSize);
        paint.setAntiAlias(true);
        paint.setStyle(FILL);
      //  paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }
     @Override
     protected void onDraw(Canvas canvas) {
         super.onDraw(canvas);

         if(mArrowType==-1) return;
         float height = getHeight();
         float width = getWidth();

         float yoffset=moffset/3;
         if (mArrowType == 1) {
             //实例化路径
             Path path = new Path();
             path.moveTo(width - mArrowWidth, height / 2 + yoffset);
             path.lineTo(width - mArrowWidth / 2, height / 2 + yoffset - mArrowWidth / 2);
             path.lineTo(width, height / 2 + yoffset);
             path.close(); // 使这些点构成封闭的多边形
             mPaint.setColor(0xFFFF3F47);
             canvas.drawPath(path, mPaint);
          } else if (mArrowType == 2) {
             //实例化路径
             Path path = new Path();
             path.moveTo(width - mArrowWidth, height / 2 );
             path.lineTo(width - mArrowWidth / 2, height / 2  + mArrowWidth / 2);
             path.lineTo(width, height / 2 );
             path.close(); // 使这些点构成封闭的多边形
             mPaint.setColor(0xFF41C200);
             canvas.drawPath(path, mPaint);
         } else {
             mPaint.setColor(0xFFF5A623);
             canvas.drawRect(width - mArrowWidth, height / 2, width, height / 2 + moffset/3 , mPaint);// 正方形
         }
     }

}
