package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by cjx on 2018\8\10 0010.
 *
 * https://www.cnblogs.com/linux007/p/5798656.html
 */

public class StatusbgTextView extends RoundbgTextView {

    @IntDef({END, WAITING, ONLIVEING,PLAYBACK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BgStatus{};

    public static final int END=0;
    public static final int WAITING=1;
    public static final int ONLIVEING=2;
    public static final int PLAYBACK=3;

    private int mCurrentStatus=PLAYBACK;
    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }

    public StatusbgTextView(Context context) {
        super(context);

    }

    public StatusbgTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    public ShapeDrawable getDefaultBackground(int radius,int color,boolean isAllRound) {

        int r = dipToPixels(radius);
        float[] outerR = new float[] {r, r,isAllRound?r: 0,isAllRound?r: 0,isAllRound?r: 0,isAllRound?r: 0, r, r};//// 前2个 左上角， 3 4 ， 右上角， 56， 右下， 78 ，左下，如果没弧度的话，传入null即可。

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(color);

        return drawable;

    }

    public void setStatus(@BgStatus int  bgStatus){
        setStatus(bgStatus,false);
    }

     public void setStatus(@BgStatus int  bgStatus,boolean isAllRound){
         if(mCurrentStatus==bgStatus) return;
         mCurrentStatus=bgStatus;

        ShapeDrawable drawable;
        if(bgStatus==ONLIVEING){
            drawable=getDefaultBackground(14,0XFFFD4312,isAllRound);
        }else if(bgStatus==END){
            drawable=getDefaultBackground(14,0X80DDDCDC,isAllRound);
        }else if(bgStatus==PLAYBACK){
            drawable=getDefaultBackground(14,0XFFFF3F47,isAllRound);//#FF3F47
        }else if(bgStatus==WAITING){
            drawable=getDefaultBackground(14,0XFFFEAE99,isAllRound);
        }else {
            drawable=getDefaultBackground(14,0XFFFF3F47,isAllRound);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackground(drawable);
        } else {
            this.setBackgroundDrawable(drawable);
        }
    }
}
