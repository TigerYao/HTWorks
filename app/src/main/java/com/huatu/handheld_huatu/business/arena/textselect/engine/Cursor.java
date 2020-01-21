package com.huatu.handheld_huatu.business.arena.textselect.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ContentFrameLayout;
import android.text.Layout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huatu.handheld_huatu.business.arena.textselect.util.TextLayoutUtil;
import com.huatu.handheld_huatu.utils.DisplayUtil;

/**
 * 选中的共用光标类，是个PopupWindow
 */
public class Cursor extends View {

    private PopupWindow mPopupWindow;
    private Paint mPaint;

    private int mCircleRadius;
    private int mWidth;
    private int mHeight;

    public boolean isLeft;

    private Point mCursorOffset;

    private TextView mTextView;
    private SelectHelper selectHelper;
    private MenuWindow menuWindow;

    public Cursor(Context context, boolean isLeft) {
        super(context);
        this.isLeft = isLeft;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xFF1379D6);
        mPaint.setStrokeWidth((float) 4.0);
        mPopupWindow = new PopupWindow(this);
        mPopupWindow.setClippingEnabled(false);
        mWidth = mHeight = TextLayoutUtil.dp2px(context, 50);
        mCircleRadius = mWidth / 4;
        mPopupWindow.setWidth(mWidth);
        mPopupWindow.setHeight(mHeight);

        invalidate();
    }

    public void setSelect(TextView textView, SelectHelper selectHelper, MenuWindow menuWindow){
        this.mTextView = textView;
        this.selectHelper = selectHelper;
        this.menuWindow = menuWindow;
    }

    // 根据左右绘制光标，先画线，还是先画圈
    @Override
    protected void onDraw(Canvas canvas) {
        if (isLeft) {
            canvas.drawCircle(mCircleRadius, mHeight - mCircleRadius, mCircleRadius, mPaint);
            canvas.drawRect(mCircleRadius, mHeight - mCircleRadius * 2, mCircleRadius * 2, mHeight - mCircleRadius, mPaint);
        } else {
            canvas.drawCircle(mWidth - mCircleRadius, mHeight - mCircleRadius, mCircleRadius, mPaint);
            canvas.drawRect(mCircleRadius * 2 + 2, mHeight - mCircleRadius * 2, mWidth - mCircleRadius, mHeight - mCircleRadius, mPaint);
        }
    }

    private int mAdjustX;
    private int mAdjustY;

    private int mBeforeDragStart;
    private int mBeforeDragEnd;
    private int thisOldIndex;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mBeforeDragStart = selectHelper.mSelectInfo.mStart;
                mBeforeDragEnd = selectHelper.mSelectInfo.mEnd;
                if (isLeft) {
                    thisOldIndex = selectHelper.mSelectInfo.mStart;
                } else {
                    thisOldIndex = selectHelper.mSelectInfo.mEnd;
                }
                mAdjustX = (int) event.getX();
                mAdjustY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                menuWindow.dismiss();
                int rawX = (int) event.getRawX();
                int rawY = (int) event.getRawY();
                update(rawX - mAdjustX, rawY - mAdjustY);
                break;
            case MotionEvent.ACTION_UP:
                selectHelper.showCursorHandle(Cursor.this);
            case MotionEvent.ACTION_CANCEL:
                menuWindow.show();
                break;
        }
        return true;
    }

    private void changeDirection() {
        isLeft = !isLeft;
        invalidate();
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    private int[] mTempCoors = new int[2];

    public void update(int x, int y) {

        mTextView.getLocationInWindow(mTempCoors);
        int oldOffset;
        if (isLeft) {
            oldOffset = selectHelper.mSelectInfo.mStart;
        } else {
            oldOffset = selectHelper.mSelectInfo.mEnd;
        }

        y -= mTempCoors[1];

        // 根据和光标初始位置的坐标差获取到当前光标应该显示的位置
        int offset = TextLayoutUtil.getHysteresisOffset(mTextView, x, y, oldOffset);

        if (offset != oldOffset) {
            selectHelper.resetSelectionInfo();
            if (isLeft) {       // 左光标
                if (offset > mBeforeDragEnd) {      // 新位置大于右光标位置，交换左右光标属性
                    Cursor handle = selectHelper.getCursorHandle(false);
                    changeDirection();
                    handle.changeDirection();
                    mBeforeDragStart = mBeforeDragEnd;
                    selectHelper.selectText(mBeforeDragEnd, offset);
                    handle.updateCursorHandle();
                } else {                            // 新位置还是左位置
                    selectHelper.selectText(offset, -1);
                }
                updateCursorHandle();
            } else {
                if (offset < mBeforeDragStart) {
                    Cursor handle = selectHelper.getCursorHandle(true);
                    handle.changeDirection();
                    changeDirection();
                    mBeforeDragEnd = mBeforeDragStart;
                    selectHelper.selectText(offset, mBeforeDragStart);
                    handle.updateCursorHandle();
                } else {
                    selectHelper.selectText(mBeforeDragStart, offset);
                }
                updateCursorHandle();
            }
        }

        if (mTextView != null) {
            mTextView.invalidate();
        }
    }

    public void updateCursorHandle() {

        Point myOffset = new Point();
        if (mCursorOffset != null) {
            myOffset.x = mCursorOffset.x;
            myOffset.y = mCursorOffset.y;
        }

        mTextView.getLocationInWindow(mTempCoors);
        Layout layout = mTextView.getLayout();
        if (isLeft) {
            mPopupWindow.update((int) layout.getPrimaryHorizontal(selectHelper.mSelectInfo.mStart) + getExtraX() + myOffset.x,
                    layout.getLineBottom(layout.getLineForOffset(selectHelper.mSelectInfo.mStart)) + getExtraY() + myOffset.y,
                    -1, -1);
        } else {
            // 修改右边光标的显示位置
            // 本来是取最 最后一个字的 右边的字 的左边坐标
            // 改为最后一个字的右边的坐标，这样就不会出现在行首位
            float rightX = layout.getSecondaryHorizontal(selectHelper.mSelectInfo.mEnd - 1) + getExtraX() + mTextView.getPaint().measureText("我");
            // 右光标不要超过屏幕
            if (rightX > DisplayUtil.getScreenWidth() - 50) {
                rightX = DisplayUtil.getScreenWidth() - 50;
            }
            mPopupWindow.update((int) rightX + myOffset.x,
                    layout.getLineBottom(layout.getLineForOffset(selectHelper.mSelectInfo.mEnd - 1)) + getExtraY() + myOffset.y,
                    -1, -1);
        }
    }

    public void show(int x, int y) {
        mTextView.getLocationInWindow(mTempCoors);

        int Y = y + getExtraY();

        // 如果光标位置超出ScrollView显示位置，就不显示。
        ViewParent parent = mTextView.getParent();

        while ((!(parent instanceof ScrollView)) && (!(parent instanceof NestedScrollView))) {
            parent = parent.getParent();
            if (parent instanceof ContentFrameLayout) {
                break;
            }
        }

        if (parent instanceof ScrollView || parent instanceof NestedScrollView) {
            ViewGroup scrollView = (ViewGroup) parent;

            int[] location = new int[2];
            scrollView.getLocationInWindow(location);

            int height = scrollView.getHeight();

            if (Y < (location[1] - 40) || Y > (location[1] + height - 40)) {
                Cursor.this.dismiss();
                return;
            }
        }

        Point myOffset = new Point();
        if (mCursorOffset != null) {
            myOffset.x = mCursorOffset.x;
            myOffset.y = mCursorOffset.y;
        }

        mPopupWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, x + getExtraX(), Y);
    }

    public int getExtraX() {
        return mTempCoors[0] + mTextView.getPaddingLeft() - mWidth / 2;
    }

    public int getExtraY() {
        return mTempCoors[1] + mTextView.getPaddingTop() - mHeight / 2;
    }
}
