package com.huatu.Indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by Jack on 2015/10/15.
 */
public abstract class BaseIndicatorController {

    private WeakReference<View> mTarget;


    public void setTarget(View target){
        this.mTarget=new WeakReference<View>(target);
    }

    public View getTarget(){
        if(mTarget==null) return null;
        return mTarget.get();
    }


    public int getWidth(){
        if(mTarget==null) return 0;
        return mTarget.get().getWidth();
    }

    public int getHeight(){
        if(mTarget==null) return 0;
        return mTarget.get().getHeight();
    }

    public void postInvalidate(){

        if(mTarget==null||mTarget.get()==null)return;
        mTarget.get().postInvalidate();
    }

    /**
     * draw indicator what ever
     * you want to draw
     * @param canvas
     * @param paint
     */
    public abstract void draw(Canvas canvas,Paint paint);

    /**
     * create animation or animations
     * ,and add to your indicator.
     */
    public abstract void createAnimation();

    public abstract void stopAnimation();
}
