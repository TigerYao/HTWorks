package com.huatu.handheld_huatu.business.ztk_zhibo.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by saiyuan on 2017/11/29.
 */

public class InterceptFrameLayout extends FrameLayout {
    boolean isIntercept = false;
    OnTouchListener onTouchListener;

    public InterceptFrameLayout(@NonNull Context context) {
        super(context);
    }

    public InterceptFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public InterceptFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                       @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIntercept(boolean isInter) {
        isIntercept = isInter;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isIntercept) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        onTouchListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(onTouchListener != null) {
            boolean isTouch = onTouchListener.onTouch(this, event);
            if(isTouch) {
                return isTouch;
            }
        }
        return super.onTouchEvent(event);
    }
}
