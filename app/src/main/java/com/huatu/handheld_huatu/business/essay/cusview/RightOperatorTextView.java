package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.utils.DisplayUtil;


public class RightOperatorTextView extends android.support.v7.widget.AppCompatTextView {

    private static String TAG = "RightOperatorTextView";

    private Context mContext;
    private OnCusViewCallBack mOnCusViewCallBack;

    private int l, t, r, b;

    public interface OnCusViewCallBack {
        boolean isActionUp(boolean isOpen);
    }

    public RightOperatorTextView(Context context) {
        super(context);
        initView(context);
    }

    public RightOperatorTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RightOperatorTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        setMovementMethod(ScrollingMovementMethod.getInstance());
        setOnTouchListener(shopCarSettleTouch);
    }

    public void setOnCusViewCallBack(OnCusViewCallBack l) {
        this.mOnCusViewCallBack = l;
    }

    public void resetPosition() {

        postDelayed(new Runnable() {
            @Override
            public void run() {
                int screenWidth = ((View) getParent()).getMeasuredWidth();
                int screenHeight = ((View) getParent()).getMeasuredHeight();

                if (screenWidth == 0 || screenHeight == 0) {
                    screenWidth = DisplayUtil.getScreenWidth();
                    screenHeight = DisplayUtil.getScreenHeight();
                }

                int measuredWidth = DisplayUtil.dp2px(50);
                int measuredHeight = DisplayUtil.dp2px(50);

                if (l == 0 && t == 0 && r == 0 && b == 0) {
                    t = b = (screenHeight - measuredHeight) / 2;
                    l = screenWidth - measuredWidth;
                    r = 0;
                } else {

                    if (r == 0) {
                        l = screenWidth - measuredWidth;
                    } else if (l == 0) {
                        r = screenWidth - measuredWidth;
                    } else {
                        float p = (float) l / (float) (l + r);
                        l = (int) ((screenWidth - measuredWidth) * p);
                        r = (int) ((screenWidth - measuredWidth) * (1 - p));
                    }

                    if (b == 0) {
                        t = screenHeight - measuredHeight;
                    } else if (t == 0) {
                        b = screenHeight - measuredHeight;
                    } else {
                        float p = (float) t / (float) (t + b);
                        t = (int) ((screenHeight - measuredHeight) * p);
                        b = (int) ((screenHeight - measuredHeight) * (1 - p));
                    }
                }

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
                lp.setMargins(l, t, 0, 0);
                setLayoutParams(lp);

                setVisibility(VISIBLE);
            }
        }, 100);

    }

    private View.OnTouchListener shopCarSettleTouch = new View.OnTouchListener() {

        float startX, startY;
        int lastX, lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int ea = event.getAction();
            switch (ea) {
                case MotionEvent.ACTION_DOWN:

                    startX = event.getRawX();//得到相对应屏幕左上角的坐标
                    startY = event.getRawY();

                    lastX = (int) event.getRawX();//获取触摸事件触摸位置的原始X坐标
                    lastY = (int) event.getRawY();

                case MotionEvent.ACTION_MOVE:
                    //event.getRawX();获得移动的位置
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    int screenWidth = ((View) getParent()).getMeasuredWidth();
                    int screenHeight = ((View) getParent()).getMeasuredHeight();

                    l = v.getLeft() + dx;
                    t = v.getTop() + dy;
                    r = screenWidth - v.getLeft() - v.getMeasuredWidth() - dx;
                    b = screenHeight - v.getTop() - v.getMeasuredHeight() - dy;

                    if (l < 0 || r < 0) {
                        l = v.getLeft();
                        r = screenWidth - v.getLeft() - v.getMeasuredWidth();
                    }
                    if (t < 0 || b < 0) {
                        t = v.getTop();
                        b = screenHeight - v.getTop() - v.getMeasuredHeight();
                    }

                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
                    lp.setMargins(l, t, 0, 0);
                    v.setLayoutParams(lp);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    v.postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(lastX - startX) < 10 && Math.abs(lastY - startY) < 10) {
                        if (mOnCusViewCallBack != null) {
                            mOnCusViewCallBack.isActionUp(true);
                        }
                    }
                    break;
            }
            return true;
        }
    };
}
