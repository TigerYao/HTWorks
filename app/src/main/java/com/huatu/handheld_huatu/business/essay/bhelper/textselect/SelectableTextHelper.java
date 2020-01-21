package com.huatu.handheld_huatu.business.essay.bhelper.textselect;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ContentFrameLayout;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.cusview.UnderlineSpanRed;
import com.huatu.handheld_huatu.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SelectableTextHelper implements View.OnAttachStateChangeListener {

    private final static int DEFAULT_SELECTION_LENGTH = 1;
    public static final int DEFAULT_SHOW_DURATION = 100;

    protected CursorHandle mStartHandle;                                  // 开始光标
    protected CursorHandle mEndHandle;                                    // 结束光标
    protected OperateWindow mOperateWindow;                               // 悬浮按钮
    public SelectionInfo mSelectionInfo = new SelectionInfo();          // 选择内容，开始 结束 内容
    private OnSelectListener mSelectListener;                           // 选中监听

    private Context mContext;
    protected TextView mTextView;                                         // 有选中功能的TextView
    private Spannable mSpannable;                                       // TextView的内容

    private int mTouchX;                                                // 记录触摸Text的位置
    private int mTouchY;

    private int mSelectedColor;                                         // 选中的颜色
    private int mCursorHandleColor;                                     // 光标颜色
    private int mCursorHandleSize;
    private BackgroundColorSpan mSpan;                                  // 背景颜色Span
    protected boolean isHideWhenScroll;                                   // 滑动的时候是否隐藏
    private boolean isHide = true;
    private int showType;                                               // 显示类型
    public static final int SHOW_TYPE_ONLY_COPY = 11;

    ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    public void setSelectListener(OnSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    OnMenuClickListener mOnMenuClickListener;
    //弹出菜单点击
    public interface OnMenuClickListener{
         void onMenuClick(int postion,boolean isCancel);
    }

    public void setOnMenuClickListener(OnMenuClickListener clickListener) {
        mOnMenuClickListener = clickListener;
    }

    public SelectableTextHelper(Builder builder) {
        mTextView = builder.mTextView;
        showType = builder.showType;
        mContext = mTextView.getContext();
        mSelectedColor = builder.mSelectedColor;
        mCursorHandleColor = builder.mCursorHandleColor;
        mCursorHandleSize = TextLayoutUtil.dp2px(mContext, builder.mCursorHandleSizeInDp);
        init();
    }


    @Override
    public void onViewAttachedToWindow(View v) {  }

    @Override
    public void onViewDetachedFromWindow(View v) {
        destroy(true);
    }

    /**
     * 初始化，添加各种监听
     */
    protected void init() {
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        // 长按监听
        mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showSelectView(mTouchX, mTouchY);                               // 长按文字发生动作
                return true;
            }
        });

        // 触摸监听，获取触摸坐标
        mTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                return false;
            }
        });

        // 点击监听
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doViewClick(mTouchX,mTouchY);
//                EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_essExMaterialsContent_operator_oc));
            }
        });

        // View attach 和 detach 到 window 上监听
        mTextView.addOnAttachStateChangeListener(this);

        // 这里实现滑动的时候 隐藏光标和按钮窗，停止的时候显示。每次重绘都回调都延迟100ms，
        // 此方法在视图绘制前会被调用，测量结束，客户获取到一些数据。再计算一些动态宽高时可以使用。(调用一次后需要注销这个监听，否则会阻塞ui线程。)
        mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isHideWhenScroll) {
                    Log.i("TAG", "-onPreDraw-- " + isHideWhenScroll);
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
                doScrollChanged();
            }
        };
        mTextView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);

        mOperateWindow = new OperateWindow(mContext);
    }

    public void doViewClick(int touchX,int touchY){
        if (isSelArea(touchX, touchY)) {
            showSelectView(touchX, touchY);
        } else {
            resetSelectionInfo();
            hideSelectView();
        }
    }

    protected void doScrollChanged(){
        if (!isHideWhenScroll && !isHide) {
            Log.i("TAG", "-onScrollChanged-- " + isHideWhenScroll);
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

    // 延迟显示光标和弹窗，用于在滑动的过程中
    protected void postShowSelectView(int duration) {
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

    // 隐藏光标、弹窗
    private void hideSelectView() {
        isHide = true;
        if (mStartHandle != null) {
            mStartHandle.dismiss();
        }
        if (mEndHandle != null) {
            mEndHandle.dismiss();
        }
        if (mOperateWindow != null) {
            mOperateWindow.dismiss();
            if (mSelectListener != null) {
                mSelectListener.updateView(0);
            }
        }
    }

    // 清除选择
    public void clearView() {
        resetSelectionInfo();
        hideSelectView();
    }
    // 初始化选择
    private void resetSelectionInfo() {
        if (mSelectionInfo != null) {
            mSelectionInfo.mSelectionContent = null;
        }
        if (mSpannable != null && mSpan != null) {
            mSpannable.removeSpan(mSpan);
            mSpan = null;
        }
    }

    // 是否是划线（标记）区域
    private boolean isSelArea(int x, int y) {
        int startOffset = TextLayoutUtil.getPreciseOffset(mTextView, x, y);
        int endOffset = startOffset + DEFAULT_SELECTION_LENGTH;

        int startIndex = indexedBinarySearch(indexList, startOffset);
        int endIndex = indexedBinarySearch(indexList, endOffset);

        return startIndex != 0 && Math.abs(startIndex) % 2 != 0 && Math.abs(endIndex) % 2 != 0;
    }

    // 显示光标，弹窗
    public void showSelectView(int x, int y) {
        hideSelectView();                                                           // 隐藏 光标 悬浮按钮
        resetSelectionInfo();                                                       // 重置选择
        isHide = false;
        if (mStartHandle == null) mStartHandle = new CursorHandle(true);
        if (mEndHandle == null) mEndHandle = new CursorHandle(false);

        int startOffset = TextLayoutUtil.getPreciseOffset(mTextView, x, y);         // 根据坐标获取点击的是 TextView 第几个字作为开始选择的字
        int endOffset = startOffset + DEFAULT_SELECTION_LENGTH;                     // 默认选择一个作为结束
        if (mTextView.getText() instanceof Spannable) {
            mSpannable = (Spannable) mTextView.getText();
        }
        if (mSpannable == null || startOffset >= mTextView.getText().length()) {
            return;
        }
        selectText(startOffset, endOffset);
        showCursorHandle(mStartHandle);
        showCursorHandle(mEndHandle);
        mOperateWindow.show();
    }

    // 显示选择光标
    private void showCursorHandle(CursorHandle cursorHandle) {
//        Layout layout = mTextView.getLayout();
//        int offset = cursorHandle.isLeft ? mSelectionInfo.mStart : mSelectionInfo.mEnd;
//        cursorHandle.show((int) layout.getPrimaryHorizontal(offset), layout.getLineBottom(layout.getLineForOffset(offset)));

        // 修改右边光标的显示位置
        // 本来是取最 最后一个字的 右边的字 的左边坐标
        // 改为最后一个字的右边的坐标，这样就不会出现在行首位
        Layout layout = mTextView.getLayout();
        if (cursorHandle.isLeft) {
            int offset = mSelectionInfo.mStart;
            int primaryHorizontal = (int) layout.getPrimaryHorizontal(offset);
            int lineBottom = layout.getLineBottom(layout.getLineForOffset(offset));
            cursorHandle.show(primaryHorizontal, lineBottom);
        } else {
            int offset = mSelectionInfo.mEnd - 1;
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

    /**
     * 根据选择的开始结束点，画背景，选择
     */
    private void selectText(int startPos, int endPos) {
        if (startPos != -1) {
            mSelectionInfo.mStart = startPos;
        }
        if (endPos != -1) {
            mSelectionInfo.mEnd = endPos;
        }
        if (mSelectionInfo.mStart > mSelectionInfo.mEnd) {
            int temp = mSelectionInfo.mStart;
            mSelectionInfo.mStart = mSelectionInfo.mEnd;
            mSelectionInfo.mEnd = temp;
        }

        if (mSpannable != null) {
            if (mSpan == null) {
                mSpan = new BackgroundColorSpan(mSelectedColor);                                                                    // 绘制选择文字的背景颜色
            }
            mSelectionInfo.mSelectionContent = mSpannable.subSequence(mSelectionInfo.mStart, mSelectionInfo.mEnd).toString();       // 截取选择的内容
            mSpannable.setSpan(mSpan, mSelectionInfo.mStart, mSelectionInfo.mEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);                // 设置背景色
            if (mSelectListener != null) {
                mSelectListener.onTextSelected(mSelectionInfo.mSelectionContent);                                                   // 选择的回调
            }
            if (mSelectListener != null) {
                mSelectListener.updateView(-1);
            }
        }
    }

    // 把添加的划线的index放进来，以判断是否点击了划线处
    private void addData(boolean isAdd, SelectionInfo v, UnderlineSpanRed u) {
        if (isAdd) {
            indexList.add(v.mStart);
            indexList.add(v.mEnd);
        }
        Collections.sort(indexList);
        map.put(v.mStart + "_" + v.mEnd, u);
        Log.d("SelectableTextHelper", "map.size():" + map.size());
    }

    // 去掉添加线的记录
    private void removeData(int startIndex, int endIndex) {
        int mStart = indexList.get(startIndex);
        int mEnd = indexList.get(endIndex);
        indexList.remove(startIndex);
        indexList.remove(startIndex);
        Collections.sort(indexList);
        map.remove(mStart + "_" + mEnd);
        Log.d("SelectableTextHelper", "map.size():" + map.size());
    }

    private boolean needUpdatSpan(int startIndex, int endIndex) {
        if (startIndex != endIndex) {
            return true;
        }
        return false;
    }

    /**
     * 二分查找
     *
     * 如果包含，就返回对应的index
     * 如果不包含，就返回大于key的第一个value的index的负数
     */
    private int indexedBinarySearch(ArrayList list, int key) {
        int low = 0;
        int high = list.size() - 1;
        int mid = 0;
        while (low <= high) {
            mid = (low + high) >>> 1;
            Comparable midVal = (Comparable) list.get(mid);
            int cmp = midVal.compareTo(key);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low); // key found
    }

    HashMap<String, UnderlineSpanRed> map = new HashMap();

    public ArrayList<Integer> indexList = new ArrayList<>();

    private void setIndexList(ArrayList<Integer> indexList) {
        this.indexList = indexList;
    }

    private CursorHandle getCursorHandle(boolean isLeft) {
        if (mStartHandle.isLeft == isLeft) {
            return mStartHandle;
        } else {
            return mEndHandle;
        }
    }

    public void destroy(boolean fullDestory) {
        mTextView.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        mTextView.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);
        resetSelectionInfo();
        hideSelectView();

        if(fullDestory){
            mStartHandle = null;
            mEndHandle = null;
            mOperateWindow = null;
        }

    }

    /**
     * hideSelectView();
     * selectText(0, mTextView.getText().length());
     * isHide = false;
     * showCursorHandle(mStartHandle);
     * showCursorHandle(mEndHandle);
     * mOperateWindow.show();
     * Operate windows : copy, select all
     * <p>
     * 添加下划线，去掉下划线选项悬浮按钮
     */
     class OperateWindow {

        private PopupWindow mWindow;
        private int[] mTempCoors = new int[2];
        private View contentView;
        private int type;                   // -1、0 显示"下划线" 1、显示取消下划线
        private int mWidth;
        private int mHeight;
        private TextView tv_select_line;
        private View div;
        private LinearLayout ln_copy_line;
        private int startIndex;
        private int endIndex;

        public OperateWindow(final Context context) {
            contentView = LayoutInflater.from(context).inflate(R.layout.layout_operate_windows, null);
            contentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mWidth = contentView.getMeasuredWidth();
            mHeight = contentView.getMeasuredHeight();
            mWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
            mWindow.setClippingEnabled(false);

            div = contentView.findViewById(R.id.div);
            // 下划线
            tv_select_line = contentView.findViewById(R.id.tv_select_line);
            // 整个布局
            ln_copy_line = contentView.findViewById(R.id.ln_copy_line);

            // 点击复制
            contentView.findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setPrimaryClip(ClipData.newPlainText(mSelectionInfo.mSelectionContent, mSelectionInfo.mSelectionContent));
                    if (mSelectListener != null) {
                        mSelectListener.onTextSelected(mSelectionInfo.mSelectionContent);
                    }
                    SelectableTextHelper.this.resetSelectionInfo();
                    SelectableTextHelper.this.hideSelectView();
                }
            });
            // 点击全选
            contentView.findViewById(R.id.tv_select_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectableTextHelper.this.selectText(0, SelectableTextHelper.this.mTextView.getText().toString().length());
                    mStartHandle.dismiss();
                    mEndHandle.dismiss();
                    showCursorHandle(mStartHandle);
                    showCursorHandle(mEndHandle);
                }
            });
            // 点击下划线
            contentView.findViewById(R.id.tv_select_line).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 0) {
                        final UnderlineSpanRed mSpan2 = new UnderlineSpanRed();
                        mSpannable.removeSpan(mSpan);
                        if (needUpdatSpan(startIndex, endIndex)) {
                            int startIa = Math.abs(startIndex);
                            int mStart = 0;
                            if (startIa < 0) {
                                startIa = 0;
                            }
                            if (startIa >= indexList.size()) {
                                startIa--;
                            }
                            mStart = indexList.get(startIa);

                            int endIa = Math.abs(endIndex);
                            int mEnd = 0;
                            if (endIa < 0) {
                                endIa = 0;
                            }

                            if (endIa >= indexList.size()) {
                                endIa--;
                            }
                            mEnd = indexList.get(endIa);
                            Log.d("OperateWindow", "Math.abs(startIa - endIa):" + Math.abs(startIa - endIa));


                            for (int i = startIa; i < endIa; ) {
                                int start = indexList.get(i);
                                int end = indexList.get(i + 1);
                                UnderlineSpanRed span = map.get(start + "_" + end);
                                Log.d("OperateWindow", "map.size():" + map.size());
                                if (span != null) {
                                    mSpannable.removeSpan(span);
                                    map.remove(start + "_" + end);
                                }
                                Log.d("OperateWindow", "map.size():" + map.size());
                                i++;
                                i++;
                            }

                            if (startIa >= 0 && startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                                mStart = indexList.get(startIa + 1);
                                if (mSelectionInfo.mStart != mStart) {
                                    indexList.set(startIa + 1, mSelectionInfo.mStart);
                                }
                            }

                            if (endIa > 0 && endIa < indexList.size()) {
                                mEnd = indexList.get(endIa - 1);
                                if (mSelectionInfo.mEnd != mEnd) {
                                    indexList.set(endIa - 1, mSelectionInfo.mEnd);
                                }
                            }

                            for (int i = startIa + 1; i < endIa - 1; ) {
                                Log.d("OperateWindow", "map.size():" + map.size());
                                Log.d("OperateWindow", "indexList.size():" + indexList.size());
                                indexList.remove(startIa + 1);
                                indexList.remove(startIa + 1);
                                Log.d("OperateWindow", "indexList.size():" + indexList.size());
                                i++;
                                i++;
                            }
                            if (Math.abs(startIa - endIa) <= 2) {
                                mSpannable.setSpan(mSpan2, mSelectionInfo.mStart, mSelectionInfo.mEnd, Spanned
                                        .SPAN_INCLUSIVE_EXCLUSIVE);
                                addData(false, mSelectionInfo, mSpan2);
                            } else {

                                Log.d("OperateWindow", "indexList:" + indexList);

                                Collections.sort(indexList);
                                for (int i = 0; i < indexList.size() - 1; ) {
                                    mSpannable.setSpan(mSpan2, indexList.get(i), indexList.get(i + 1), Spanned
                                            .SPAN_INCLUSIVE_EXCLUSIVE);
                                    map.put(indexList.get(i) + "_" + indexList.get(i + 1), mSpan2);
                                    i++;
                                    i++;
                                }
                            }

                        } else {
                            mSpannable.setSpan(mSpan2, mSelectionInfo.mStart, mSelectionInfo.mEnd, Spanned
                                    .SPAN_INCLUSIVE_EXCLUSIVE);
                            addData(true, mSelectionInfo, mSpan2);
                        }
                        mTextView.setText(mSpannable);
                        SelectableTextHelper.this.resetSelectionInfo();
                        SelectableTextHelper.this.hideSelectView();
                        if (mSelectListener != null) {
                            mSelectListener.updateView(0);
                        }
                    } else if (type == 1) {
                        mSpannable.removeSpan(mSpan);
                        if (startIndex < indexList.size() && endIndex < indexList.size()) {
                            int mStart = indexList.get(startIndex);
                            int mEnd = indexList.get(endIndex);
                            UnderlineSpanRed span = map.get(mStart + "_" + mEnd);
                            if (span != null) {
                                mSpannable.removeSpan(span);
                            }
                            removeData(startIndex, endIndex);
                            mTextView.setText(mSpannable);
                        }
                        SelectableTextHelper.this.resetSelectionInfo();
                        SelectableTextHelper.this.hideSelectView();
                        if (mSelectListener != null) {
                            mSelectListener.updateView(1);
                        }
                    }

                    if(null!=mOnMenuClickListener&&(null!=tv_select_line)){
                        mOnMenuClickListener.onMenuClick(2,!"下划线".equals(tv_select_line.getText()));
                    }
                }
            });
        }

        public void show() {
            // 选择的开始等于结束  ？？？ 不知道什么时候回发生这种情况
            if (mSelectionInfo.mStart == mSelectionInfo.mEnd) {
                show(-1, 0, 0);
            } else {
                int startIndex = indexedBinarySearch(indexList, mSelectionInfo.mStart);
                int endIndex = indexedBinarySearch(indexList, mSelectionInfo.mEnd);
                //
                if ((startIndex > 0 || (startIndex == 0 && indexList.size() == 0)) && endIndex > 0) {
                    show(1, startIndex, endIndex);
                } else {
                    if (startIndex != 0 && Math.abs(startIndex) % 2 != 0 && Math.abs(endIndex) % 2 != 0) {
                        int startIa = Math.abs(startIndex) - 1;
                        if (startIa < indexList.size() && (startIa + 1) < indexList.size()) {
                            int mStart = indexList.get(startIa);
                            int mEnd = indexList.get(startIa + 1);
                            selectText(mStart, mEnd);
                            getCursorHandle(true).updateCursorHandle();
                            getCursorHandle(false).updateCursorHandle();
                            show(1, startIa, startIa + 1);
                        }
                    } else {
                        show(0, startIndex, endIndex);
                    }
                }
            }
        }

        public void show(int type, int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.type = type;
            if (type == 0) {
                tv_select_line.setText("下划线");
                ln_copy_line.setBackground(ContextCompat.getDrawable(mContext, R.drawable.essay_opera_copy_bg));
            } else if (type == 1) {
                tv_select_line.setText("取消划线");
                ln_copy_line.setBackground(ContextCompat.getDrawable(mContext, R.drawable.essay_opera_copy_bg));
            } else if (type == -1) {
                tv_select_line.setText("下划线");
                ln_copy_line.setBackground(ContextCompat.getDrawable(mContext, R.drawable.essay_opera_copy_bg));

            }
            if (showType == SHOW_TYPE_ONLY_COPY) {
                if (div != null) {
                    div.setVisibility(View.GONE);
                }
                tv_select_line.setVisibility(View.GONE);
                ln_copy_line.setBackground(ContextCompat.getDrawable(mContext, R.drawable.essay_opera_copy_bg));
            }
            mTextView.getLocationInWindow(mTempCoors);
            Layout layout = mTextView.getLayout();
            int posX = (int) layout.getPrimaryHorizontal(mSelectionInfo.mStart) + mTempCoors[0];
            int posY = layout.getLineTop(layout.getLineForOffset(mSelectionInfo.mStart)) + mTempCoors[1] - mHeight - 16;
            if (posX <= 0) posX = 16;

            ViewParent parent = mTextView.getParent();

            // 循环寻找父类，如果父类是ScrollView，就停止，获得ScrollView的位置信息
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

                if (posY < (location[1] - 80)) posY = (location[1] - 80);
                if (posY > (location[1] + height - 80)) posY = (location[1] + height - 80);
            } else {
                if (posY < 300) posY = 300;
                if (posY > (DisplayUtil.getScreenHeight() - 300))
                    posY = (DisplayUtil.getScreenHeight() - 300);
            }

            if (posX + mWidth > TextLayoutUtil.getScreenWidth(mContext)) {
                posX = TextLayoutUtil.getScreenWidth(mContext) - mWidth - 16;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mWindow.setElevation(8f);
            }
            mWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, posX, posY);
        }

        public void dismiss() {
            mWindow.dismiss();
        }

        public boolean isShowing() {
            return mWindow.isShowing();
        }
    }

    /**
     * 光标类
     */
    private class CursorHandle extends View {

        private PopupWindow mPopupWindow;
        private Paint mPaint;

        private int mCircleRadius = mCursorHandleSize / 2;
        private int mWidth = mCircleRadius * 2;
        private int mHeight = mCircleRadius * 2;
        private int mPadding = 25;
        private boolean isLeft;

        public CursorHandle(boolean isLeft) {
            super(mContext);
            this.isLeft = isLeft;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(mCursorHandleColor);
            mPaint.setStrokeWidth((float) 4.0);
            mPopupWindow = new PopupWindow(this);
            mPopupWindow.setClippingEnabled(false);
            mPopupWindow.setWidth(mWidth + mPadding * 2);
            mPopupWindow.setHeight(mHeight + mPadding / 2 + DisplayUtil.dp2px(18));
            invalidate();
        }

        // 根据左右绘制光标，先画线，还是先画圈
        @Override
        protected void onDraw(Canvas canvas) {
            if (isLeft) {
                canvas.drawCircle(mCircleRadius + mPadding, mCircleRadius, mCircleRadius, mPaint);
                canvas.drawLine(mCircleRadius + mPadding, 0, mCircleRadius + mPadding, mCircleRadius * 2 + DisplayUtil.dp2px(15), mPaint);
            } else {
                canvas.drawCircle(mCircleRadius + mPadding, mCircleRadius * 2 + DisplayUtil.dp2px(15), mCircleRadius, mPaint);
                canvas.drawLine(mCircleRadius + mPadding, mCircleRadius - DisplayUtil.dp2px(1), mCircleRadius + mPadding, mCircleRadius * 2 + DisplayUtil.dp2px(15), mPaint);
            }
        }

        private int mAdjustX;
        private int mAdjustY;

        private int mBeforeDragStart;
        private int mBeforeDragEnd;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mBeforeDragStart = mSelectionInfo.mStart;
                    mBeforeDragEnd = mSelectionInfo.mEnd;
                    mAdjustX = (int) event.getX();
                    mAdjustY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    showCursorHandle(CursorHandle.this);
                case MotionEvent.ACTION_CANCEL:
                    mOperateWindow.show();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mOperateWindow.dismiss();
                    int rawX = (int) event.getRawX();
                    int rawY = (int) event.getRawY();
                    update(rawX + mAdjustX - mWidth, rawY + mAdjustY - mHeight);
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
                oldOffset = mSelectionInfo.mStart;
            } else {
                oldOffset = mSelectionInfo.mEnd;
            }

            y -= mTempCoors[1];

            int offset = TextLayoutUtil.getHysteresisOffset(mTextView, x, y, oldOffset);

            if (offset != oldOffset) {
                resetSelectionInfo();
                if (isLeft) {
                    if (offset > mBeforeDragEnd) {
                        CursorHandle handle = getCursorHandle(false);
                        changeDirection();
                        handle.changeDirection();
                        mBeforeDragStart = mBeforeDragEnd;
                        selectText(mBeforeDragEnd, offset);
                        handle.updateCursorHandle();
                    } else {
                        selectText(offset, -1);
                    }
                    updateCursorHandle();
                } else {
                    if (offset < mBeforeDragStart) {
                        CursorHandle handle = getCursorHandle(true);
                        handle.changeDirection();
                        changeDirection();
                        mBeforeDragEnd = mBeforeDragStart;
                        selectText(offset, mBeforeDragStart);
                        handle.updateCursorHandle();
                    } else {
                        selectText(mBeforeDragStart, offset);
                    }
                    updateCursorHandle();
                }
            }

            if (mSelectListener != null) {
                mSelectListener.updateView(-1);
            }
        }

        public void updateCursorHandle() {
            mTextView.getLocationInWindow(mTempCoors);
            Layout layout = mTextView.getLayout();
            if (isLeft) {
                mPopupWindow.update((int) layout.getPrimaryHorizontal(mSelectionInfo.mStart) - mWidth + getExtraX(),
                        layout.getLineBottom(layout.getLineForOffset(mSelectionInfo.mStart)) + getExtraY(), -1, -1);
            } else {
                // 修改右边光标的显示位置
                // 本来是取最 最后一个字的 右边的字 的左边坐标
                // 改为最后一个字的右边的坐标，这样就不会出现在行首位
                float rightX = layout.getSecondaryHorizontal(mSelectionInfo.mEnd - 1) + getExtraX() + mTextView.getPaint().measureText("我");
                // 右光标不要超过屏幕
                if (rightX > DisplayUtil.getScreenWidth() - 50) {
                    rightX = DisplayUtil.getScreenWidth() - 50;
                }
                mPopupWindow.update((int) rightX, layout.getLineBottom(layout.getLineForOffset(mSelectionInfo.mEnd - 1)) + getExtraY(), -1, -1);
            }
        }

        public void show(int x, int y) {
            mTextView.getLocationInWindow(mTempCoors);
            int offset = isLeft ? mWidth : 0;
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
                    CursorHandle.this.dismiss();
                    return;
                }
            }

            mPopupWindow.showAtLocation(mTextView, Gravity.NO_GRAVITY, x - offset + getExtraX(), Y);
        }

        public int getExtraX() {
            return mTempCoors[0] - mPadding + mTextView.getPaddingLeft();
        }

        public int getExtraY() {
            return mTempCoors[1] + mTextView.getPaddingTop() - DisplayUtil.dp2px(25);
        }
    }

    public static class Builder {
        private TextView mTextView;
        private int mCursorHandleColor = 0xFF1379D6;
        private int mSelectedColor = 0xFFAFE1F4;
        private float mCursorHandleSizeInDp = 14;
        private int showType;

        public Builder(TextView textView) {
            mTextView = textView;
        }

        public Builder setCursorHandleColor(@ColorInt int cursorHandleColor) {
            mCursorHandleColor = cursorHandleColor;
            return this;
        }

        public Builder setCursorHandleSizeInDp(float cursorHandleSizeInDp) {
            mCursorHandleSizeInDp = cursorHandleSizeInDp;
            return this;
        }

        public Builder setSelectedColor(@ColorInt int selectedBgColor) {
            mSelectedColor = selectedBgColor;
            return this;
        }

        public Builder setShowType(@ColorInt int sType) {
            showType = sType;
            return this;
        }

        public SelectableTextHelper build() {
            return new SelectableTextHelper(this);
        }
        public SelectableMultTextHelper buildV2() {
            return new SelectableMultTextHelper(this);
        }

        public SelectableMultTextV2Helper buildV3() {
            return new SelectableMultTextV2Helper(this);
        }
    }

    //*************************************/

    public void setSel(String id) {
        if (mTextView != null) {
            HashMap<String, ArrayList<Integer>> var = EssayExamDataCache.getInstance().materials_sel;
            ArrayList<Integer> var2 = var.get(id);
            if (var2 == null) {
                var2 = new ArrayList<>();
                var.put(id, var2);
            }
            setIndexList(var2);
        }
    }
}


