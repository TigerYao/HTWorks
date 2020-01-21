package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;

public class LiveVideoDragView extends FrameLayout {

    private float moveX;
    private float moveY;
    private float width, height;
    public int mMarginTop, mMarginLeft;
    private Rect mParentRect;
    private boolean isCanMove = true;
    private Path mPath;
    private float mRatio = 0.75f;

    public LiveVideoDragView(Context context) {
        this(context, null);
    }

    public LiveVideoDragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveVideoDragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setParentRect(Rect rec, float ratio) {
        mParentRect = rec;
        if (height == 0) {
            height = getHeight() == 0 ? getResources().getDimensionPixelOffset(R.dimen.common_dimens_108dp) : getHeight();
        }
        width = ratio != 0.75f ? getResources().getDimensionPixelOffset(R.dimen.common_dimens_192dp) : height / ratio;
        ViewGroup.MarginLayoutParams oldParams = (MarginLayoutParams) getLayoutParams();
        mMarginTop = oldParams.topMargin;
        mMarginLeft = oldParams.leftMargin;
        LogUtils.e(mParentRect.toShortString());
    }

    public void setCanMove(boolean move) {
        isCanMove = move;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getChildCount() > 1 && getChildAt(1).getVisibility() == View.VISIBLE) {
            Rect rect = new Rect();
            getChildAt(1).getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getRawX(), (int) ev.getRawY()))
                return super.onInterceptTouchEvent(ev);
        }
        return isCanMove && mParentRect != null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveX = event.getX();
                moveY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //计算移动的距离
                float offX = event.getX() - moveX;
                float offY = event.getY() - moveY;
                ViewGroup.MarginLayoutParams mlp =
                        (MarginLayoutParams) getLayoutParams();
                mlp.leftMargin = (int) (getLeft() + offX);
                mlp.topMargin = (int) (getTop() + offY) - mMarginTop;
                if (getBottom() >= mParentRect.bottom) {
                    mlp.bottomMargin = (int) (getBottom() - offY);
                } else if (mlp.bottomMargin != 0)
                    mlp.bottomMargin = 0;
                if (getRight() >= mParentRect.right) {
                    mlp.rightMargin = (int) (getRight() - offX);
                } else if (mlp.rightMargin != 0)
                    mlp.rightMargin = 0;
                mlp.topMargin += mParentRect.top;
                setLayoutParams(mlp);
                LogUtils.d(getTop() + "...." + mlp.topMargin + "。。。。。。" + event.getY() + "...." + offY);
                break;
            case MotionEvent.ACTION_UP:
                resetLayoutParams();
                break;
            case MotionEvent.ACTION_CANCEL:
                LogUtils.d("ACTION_CANCEL");
                break;
        }
        return true;
    }

    private boolean resetLayoutParams() {
        ViewGroup.MarginLayoutParams mlp =
                (MarginLayoutParams) getLayoutParams();
        boolean isChanged = false;
        if (mlp.leftMargin < mParentRect.left || getLeft() < 0) {
            mlp.leftMargin = mParentRect.left;
            isChanged = true;
        }
        if (mlp.topMargin < mParentRect.top || getTop() < 0) {
            mlp.topMargin = mParentRect.top;
            isChanged = true;
        }

        if (mlp.rightMargin != 0) {
            mlp.rightMargin = 0;
            mlp.leftMargin = mParentRect.right - getWidth();
            isChanged = true;
        }

        if (mlp.bottomMargin != 0) {
            mlp.bottomMargin = 0;
            mlp.topMargin = mParentRect.bottom - getHeight();
            isChanged = true;
        }

        if (isChanged) {
            mlp.rightMargin = 0;
            mlp.bottomMargin = 0;
            setLayoutParams(mlp);
        }
        LogUtils.d("ACTION_UP..." + isChanged);
        return true;
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(height != 0) {
            int widthRound = getResources().getDimensionPixelOffset(R.dimen.common_5dp);
            if (mPath == null) mPath = new Path();
            mPath.reset();
            mPath.addRoundRect(new RectF(0, 0, width, height), widthRound, widthRound, Path.Direction.CW);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.clipPath(mPath, Region.Op.REPLACE);
        }
        super.dispatchDraw(canvas);
//        canvas.restore();
    }
}