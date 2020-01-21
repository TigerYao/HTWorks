package com.huatu.viewpagerindicator;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by cjx on 2018\7\30 0030.
 */

public class BoldTextView extends AppCompatTextView {


    private boolean isBold=false;
    public BoldTextView(Context context) {
        super(context);
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBold(boolean canBold){

        if(canBold) {
            TextPaint tp = this.getPaint();
            tp.setFakeBoldText(true);
        }else {
            TextPaint tp = this.getPaint();
            tp.setFakeBoldText(false);
        }
        this.invalidate();
    }


}
