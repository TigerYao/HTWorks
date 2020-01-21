package com.huatu.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.huatu.utils.DensityUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;


/**
 * Created by Administrator on 2016/11/14.
 */
public class KitkatProfileFramLayout extends FrameLayout {

    public boolean enablePadding(){
        return true;
    }
    public KitkatProfileFramLayout(Context context) {
        super(context);

        setInit();
    }

    public KitkatProfileFramLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setInit();
    }

    public KitkatProfileFramLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setInit();
    }

    private void setInit() {
        if (QMUIStatusBarHelper.supportTranslucent()) {

            setPadding(getPaddingLeft(),
                  getPaddingTop()+ DensityUtils.getStatusHeight(getContext()),

                    getPaddingRight(),
                    getPaddingBottom() );

//            setBackgroundColor(Utils.resolveColor(getContext(), R.attr.colorPrimary, Color.BLACK));
        }
    }
    private boolean mTouchable = true;

    public boolean isTouchable() {
        return mTouchable;
    }

    public void setTouchable(boolean touchable) {
        this.mTouchable = touchable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mTouchable) return super.onInterceptTouchEvent(ev);
        else return true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mTouchable) return super.onTouchEvent(ev);
        else return true;
    }

}
