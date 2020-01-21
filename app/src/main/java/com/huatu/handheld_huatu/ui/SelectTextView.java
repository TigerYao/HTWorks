package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by cjx on 2018\11\19 0019.
 */

public class SelectTextView extends AppCompatTextView {
    private  Paint mPaint ;
    private boolean mIsSelected,mHasBackground;

    private int mNorColor=Color.parseColor("#f7f7f7");
    private int mSelColor=Color.parseColor("#FF3F47");


    public SelectTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SelectTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs,int def){
      //  fontWidth= DensityUtils.dp2px(getContext(), 86);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public void setSelected(boolean selected){
        mIsSelected=selected;
    }

    public void setHasBackground(boolean hasBg){
        mHasBackground=hasBg;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(true||mHasBackground){
            mPaint.setColor(mIsSelected? mSelColor:mNorColor);
            RectF rectF = new RectF(0f, 0f, getWidth(), getHeight());
            canvas.drawRoundRect(rectF, getHeight()/2, getHeight()/2, mPaint);
        }
        super.onDraw(canvas);
   }
}

