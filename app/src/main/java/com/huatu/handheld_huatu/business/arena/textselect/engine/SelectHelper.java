package com.huatu.handheld_huatu.business.arena.textselect.engine;

import android.content.Context;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.huatu.handheld_huatu.business.arena.textselect.TextSelectManager;
import com.huatu.handheld_huatu.business.arena.textselect.interfaces.IMenu;
import com.huatu.handheld_huatu.business.arena.textselect.util.TextLayoutUtil;
import com.huatu.handheld_huatu.utils.DisplayUtil;

/**
 * TextView辅助选择类，控制弹窗，光标的显示隐藏
 */
public class SelectHelper {

    private final static int DEFAULT_SELECTION_LENGTH = 1;              // 默认选择个数
    private static final int DEFAULT_SHOW_DURATION = 100;               // 选线延迟弹出的时间

    private MenuWindow mOperateWindow;                              // 悬浮按钮
    private IMenu optionWindow;                                 // 存储文字信息，处理弹窗显示
    private Cursor mStartHandle;                                 // 开始光标
    private Cursor mEndHandle;                                   // 结束光标

    public SelectInfo mSelectInfo = new SelectInfo();          // 选择内容，开始 结束 内容

    private Context mContext;
    public TextView mTextView;

    private int mTouchX;                                                // 记录触摸Text的位置
    private int mTouchY;

    private boolean isHideWhenScroll;                                   // 滑动的时候是否隐藏
    private boolean isHide = true;

    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    public SelectHelper(Context context, TextView tv, IMenu optionWindow, MenuWindow mOperateWindow, Cursor mStartHandle, Cursor mEndHandle) {
        this.mContext = context;
        this.mTextView = tv;
        this.optionWindow = optionWindow;
        this.mOperateWindow = mOperateWindow;
        this.mStartHandle = mStartHandle;
        this.mEndHandle = mEndHandle;

        init();
    }

    /**
     * 初始化，添加各种监听
     */
    private void init() {
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        // 长按监听
        mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSelectViewByPosition(mTouchX, mTouchY);                               // 长按文字发生动作
                return true;
            }
        });

        // 触摸监听，获取触摸坐标
        mTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTouchX = (int) event.getX() - mTextView.getPaddingLeft();
                mTouchY = (int) event.getY() - mTextView.getPaddingTop();
                return false;
            }
        });

        // 点击监听
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectInfo selArea = isSelectArea(mTouchX, mTouchY);
                if (selArea != null) {
                    showSelectViewByIndex(selArea.mStart, selArea.mEnd);
                } else {
                    TextSelectManager.newInstance().clearOthers(null);
                }
            }
        });

        // View attach 和 detach 到 window 上监听
        // 如果TextView onViewDetachedFromWindow，就执行destory，关闭监听等东西
//        mTextView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//            @Override
//            public void onViewAttachedToWindow(View v) {
//
//            }
//
//            @Override
//            public void onViewDetachedFromWindow(View v) {
//                destroy();
//            }
//        });

        // 这里实现滑动的时候 隐藏光标和按钮窗，停止的时候显示。每次重绘都回调都延迟100ms，
        // 此方法在视图绘制前会被调用，测量结束，客户获取到一些数据。再计算一些动态宽高时可以使用。(调用一次后需要注销这个监听，否则会阻塞ui线程。)
        mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isHideWhenScroll) {
                    isHideWhenScroll = false;
                    // 延迟显示 光标和按钮窗
                    postShowSelectView(DEFAULT_SHOW_DURATION);
                }
                return true;
            }
        };

        mTextView.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);

        // 滑动隐藏 光标和按钮窗
        mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (!isHideWhenScroll && !isHide) {
                    isHideWhenScroll = true;
                    if (mOperateWindow != null) {
                        mOperateWindow.dismiss();
                    }
                    if (mStartHandle != null) {
                        mStartHandle.dismiss();
                    }
                    if (mEndHandle != null) {
                        mEndHandle.dismiss();
                    }
                }
            }
        };
        mTextView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
    }

    // 延迟显示光标和弹窗，用于在滑动的过程中
    private void postShowSelectView(int duration) {
        mTextView.removeCallbacks(mShowSelectViewRunnable);
        if (duration <= 0) {
            mShowSelectViewRunnable.run();
        } else {
            mTextView.postDelayed(mShowSelectViewRunnable, duration);
        }
    }

    // 显示光标和弹窗的Runnable
    private final Runnable mShowSelectViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (isHide) return;
            if (mOperateWindow != null) {
                mOperateWindow.show();
            }
            if (mStartHandle != null) {
                showCursorHandle(mStartHandle);
            }
            if (mEndHandle != null) {
                showCursorHandle(mEndHandle);
            }
        }
    };

    // 清除选择
    public void clearView() {
        if (isHide) return;
        resetSelectionInfo();
        hideSelectView();
    }

    // 初始化选择
    public void resetSelectionInfo() {
        if (mSelectInfo != null) {
            mSelectInfo.mSelectionContent = null;
        }
    }

    // 隐藏光标、弹窗
    public void hideSelectView() {
        isHide = true;
        if (mStartHandle != null) {
            mStartHandle.dismiss();
        }
        if (mEndHandle != null) {
            mEndHandle.dismiss();
        }
        if (mOperateWindow != null) {
            mOperateWindow.dismiss();
            if (mTextView != null) {
                mTextView.invalidate();
            }
        }
    }

    // 是否是有数据的区域
    private SelectInfo isSelectArea(int x, int y) {
        if (optionWindow != null) {
            int startOffset = TextLayoutUtil.getPreciseOffset(mTextView, x, y);
            int endOffset = startOffset + DEFAULT_SELECTION_LENGTH;
            return optionWindow.dealBtnStyle(startOffset, endOffset);
        }
        return null;
    }

    // 显示根据坐标显示 Cursor & OptionWindow
    private void showSelectViewByPosition(int x, int y) {
        int startOffset = TextLayoutUtil.getPreciseOffset(mTextView, x, y);         // 根据坐标获取点击的是 TextView 第几个字作为开始选择的字
        int endOffset = startOffset + DEFAULT_SELECTION_LENGTH;                     // 默认选择一个作为结束

        showSelectViewByIndex(startOffset, endOffset);
    }

    // 根据字符串起始位置 显示选择区域
    private void showSelectViewByIndex(int startOffset, int endOffset) {
        hideSelectView();                                                           // 隐藏 光标 悬浮按钮
        resetSelectionInfo();                                                       // 重置选择
        isHide = false;
        selectText(startOffset, endOffset);
        TextSelectManager.newInstance().clearOthers(SelectHelper.this);
        showCursorHandle(mStartHandle);
        showCursorHandle(mEndHandle);
        mOperateWindow.setContentView(optionWindow);
        mOperateWindow.setHelper(SelectHelper.this, mSelectInfo);
        mOperateWindow.show();
    }

    // 根据起始位置改变 TextView 的 Spannable 的 BackgroundColorSpan 来显示选中区域背景
    public void selectText(int startPos, int endPos) {
        if (startPos != -1) {
            mSelectInfo.mStart = startPos;
        }
        if (endPos != -1) {
            mSelectInfo.mEnd = endPos;
        }
        if (mSelectInfo.mStart > mSelectInfo.mEnd) {
            int temp = mSelectInfo.mStart;
            mSelectInfo.mStart = mSelectInfo.mEnd;
            mSelectInfo.mEnd = temp;
        }

        mSelectInfo.mSelectionContent = mTextView.getText().subSequence(mSelectInfo.mStart, mSelectInfo.mEnd).toString();
        if (mTextView != null) {
            mTextView.invalidate();
        }
    }

    /**
     * 重新显示选中区域
     *
     * @param updateOptionPosition 是否更新 OptionWindow 的位置，全选不需要更新位置
     */
    public void setSelectText(int start, int end, boolean updateOptionPosition) {
        SelectHelper.this.selectText(start, end);
        mStartHandle.dismiss();
        mEndHandle.dismiss();
        showCursorHandle(mStartHandle);
        showCursorHandle(mEndHandle);
        if (updateOptionPosition && mOperateWindow != null) {
            mOperateWindow.dismiss();
            mOperateWindow.show();
        }
    }

    // 显示选择光标
    public void showCursorHandle(Cursor cursorHandle) {
        cursorHandle.setSelect(mTextView, this, mOperateWindow);
        // 修改右边光标的显示位置
        // 本来是取最 最后一个字的 右边的字 的左边坐标
        // 改为最后一个字的右边的坐标，这样就不会出现在行首位
        Layout layout = mTextView.getLayout();
        if (cursorHandle.isLeft) {
            int offset = mSelectInfo.mStart;
            int primaryHorizontal = (int) layout.getPrimaryHorizontal(offset);
            int lineBottom = layout.getLineBottom(layout.getLineForOffset(offset));
            cursorHandle.show(primaryHorizontal, lineBottom);
        } else {
            int offset = mSelectInfo.mEnd - 1;
            float rightX = layout.getSecondaryHorizontal(offset) + mTextView.getPaint().measureText("我");
            // 右光标不要超过屏幕
            if (rightX > DisplayUtil.getScreenWidth() - 50) {
                rightX = DisplayUtil.getScreenWidth() - 50;
            }
            int secondaryHorizontal = (int) rightX;
            int lineBottom = layout.getLineBottom(layout.getLineForOffset(offset));
            cursorHandle.show(secondaryHorizontal, lineBottom);
        }
    }

    public Cursor getCursorHandle(boolean isLeft) {
        if (mStartHandle.isLeft == isLeft) {
            return mStartHandle;
        } else {
            return mEndHandle;
        }
    }

    public void destroy() {
        mTextView.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        mTextView.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);
        resetSelectionInfo();
        hideSelectView();
        mStartHandle = null;
        mEndHandle = null;
        mOperateWindow = null;
    }
}
