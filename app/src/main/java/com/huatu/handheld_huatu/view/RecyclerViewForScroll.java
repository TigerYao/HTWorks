package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.utils.LogUtils;

/**
 */
public class RecyclerViewForScroll extends RecyclerView {

    private int requestType;
    public RecyclerViewForScroll(Context context) {
        super(context);
    }

    public RecyclerViewForScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewForScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expect = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expect);
    }

    public void setRequestType(int requestType) {
       this.requestType=requestType;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtils.d("RecyclerViewForScroll", "dispatchTouchEvent "+requestType);
        if(isParentIntercept()){
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isParentIntercept() {
        return requestType== ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL ||
                requestType== ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_SINGLE ||
                requestType== ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG ||
                requestType== ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE ;
    }
}
