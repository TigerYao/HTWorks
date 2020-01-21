package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.utils.DisplayUtil;

/**
 * 行测材料ScrollView，当上滑到底部的时候继续向上滑，减小材料ScrollView的高度，增加问题View的高度。
 */
public class ArenaDragMaterialScrollView extends NestedScrollView {

    public ArenaDragMaterialScrollView(Context context) {
        super(context);
    }

    public ArenaDragMaterialScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArenaDragMaterialScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    int Y;
    boolean isSwitch;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Y = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 向上滑 并且 滑到底了，就改变自身的高度
                int nextY = (int) ev.getRawY();
                if (nextY < Y) {                                        // 向上滑
                    int height = getHeight();
                    int scrollY = getScrollY();
                    View child = getChildAt(0);
                    if (child == null) break;
                    int childHeight = child.getMeasuredHeight();
                    if (height + scrollY > childHeight - 1 && Y > -1) {                  // 滑到底了，就拦截处理，自己改变高度
                        int dy = Math.abs(nextY - Y);
                        Y = nextY;
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        if (layoutParams.height - dy < DisplayUtil.dp2px(70)) {
                            layoutParams.height = DisplayUtil.dp2px(70);
                        } else {
                            if (isSwitch) {
                                isSwitch = false;
                                layoutParams.height -= dy * 2;
                            } else {
                                layoutParams.height -= dy;
                            }
                        }
                        // 更改了自身的高度，内容不会跟着画上去，所以会与内容交替上滑
                        // 所以会造成卡顿感，和视差感觉，所以记录交替isSwitch，让 dy * 2 消除视差。但是解决不了卡顿感。因为这里调用scrollTo不管用
                        setLayoutParams(layoutParams);
                        return true;
                    }
                }
                isSwitch = true;
                Y = nextY;
                break;
            case MotionEvent.ACTION_UP:
                isSwitch = false;
                Y = -1;
                break;
        }

        return super.onTouchEvent(ev);
    }
}
