package com.huatu.handheld_huatu.business.arena.textselect.engine;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ContentFrameLayout;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.textselect.interfaces.IMenu;
import com.huatu.handheld_huatu.business.arena.textselect.util.TextLayoutUtil;
import com.huatu.handheld_huatu.utils.DisplayUtil;

/**
 * 共用弹窗类，是个PopupWindow
 */
public class MenuWindow {

    private Context mContext;

    private IMenu optionWindow;

    private SelectInfo mSelectInfo;
    private SelectHelper mSelectHelper;

    private PopupWindow mWindow;

    private int[] mTempCoors = new int[2];

    private int mWidth;
    private int mHeight;

    public void setContentView(IMenu optionWindow) {

        // 如果 optionWindow 为空，就只显示 复制 & 全选 功能
        View contentView;
        if (optionWindow != null) {
            this.optionWindow = optionWindow;
            contentView = optionWindow.getRootView();
        } else {
            // 默认的 复制/全选功能
            contentView = LayoutInflater.from(mContext).inflate(R.layout.select_option_copy, null);
        }
        contentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mWidth = contentView.getMeasuredWidth();
        mHeight = contentView.getMeasuredHeight();

        mWindow.setContentView(contentView);

        // 点击复制
        View copyBtn = contentView.findViewById(R.id.tv_copy);
        if (copyBtn != null) {
            copyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy();
                }
            });
        }
        // 点击全选
        View selectAllBtn = contentView.findViewById(R.id.tv_select_all);
        if (selectAllBtn != null) {
            selectAllBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelect();
                }
            });
        }
    }

    public void setHelper(SelectHelper mSelectHelper, SelectInfo mSelectInfo) {
        this.mSelectHelper = mSelectHelper;
        this.mSelectInfo = mSelectInfo;
    }

    public MenuWindow(final Context context) {
        mContext = context;
        mWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mWindow.setClippingEnabled(false);
    }

    private void setSelect() {
        if (mSelectHelper == null) return;
        mSelectHelper.setSelectText(0, mSelectHelper.mTextView.getText().toString().length(), false);
    }

    private void copy() {
        if (mSelectHelper == null || mSelectInfo == null) return;
        ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setPrimaryClip(ClipData.newPlainText(mSelectInfo.mSelectionContent, mSelectInfo.mSelectionContent));
        mSelectHelper.resetSelectionInfo();
        mSelectHelper.hideSelectView();
    }

    public void show() {
        if (mSelectHelper == null || mSelectInfo == null) return;
        if (mSelectInfo.mStart == mSelectInfo.mEnd) {
            if (mSelectInfo.mEnd < mSelectHelper.mTextView.getText().length()) {
                mSelectInfo.mEnd++;
            } else if (mSelectInfo.mStart > 0) {
                mSelectInfo.mStart--;
            }
            if (mSelectInfo.mStart != mSelectInfo.mEnd) {
                mSelectHelper.setSelectText(mSelectInfo.mStart, mSelectInfo.mEnd, true);
                return;
            }
        }
        if (this.optionWindow != null) {
            // 处理应该显示的选项信息
            // 在特殊处理情况下，返回应该选中的开始结束的位置，然后重新选择，更新Option位置
            SelectInfo selectInfo = optionWindow.dealBtnStyle(mSelectInfo.mStart, mSelectInfo.mEnd);
//                if (selectTools != null) {
//                    if (mSelectInfo.mStart != selectTools.mStart || mSelectInfo.mEnd != selectTools.mEnd) {
//                        setSelectText(selectTools.mStart, selectTools.mEnd, true);
//                        return;
//                    }
//                }
        }

        mSelectHelper.mTextView.getLocationInWindow(mTempCoors);
        Layout layout = mSelectHelper.mTextView.getLayout();
        int posX = (int) layout.getPrimaryHorizontal(mSelectInfo.mStart) + mTempCoors[0] + mSelectHelper.mTextView.getPaddingLeft();
        int posY = layout.getLineTop(layout.getLineForOffset(mSelectInfo.mStart)) + mTempCoors[1] - mHeight - 16 + mSelectHelper.mTextView.getPaddingTop();
        if (posX <= 0) posX = 16;

        ViewParent parent = mSelectHelper.mTextView.getParent();

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

            if (posY < (location[1] - DisplayUtil.dp2px(65))) {
                posY = (location[1] - DisplayUtil.dp2px(65));
            }
            if (posY > (location[1] + height - DisplayUtil.dp2px(65)))
                posY = (location[1] + height - DisplayUtil.dp2px(65));
        } else {
            if (posY < DisplayUtil.dp2px(70)) posY = DisplayUtil.dp2px(70);
            if (posY > (DisplayUtil.getScreenHeight() - DisplayUtil.dp2px(65)))
                posY = (DisplayUtil.getScreenHeight() - DisplayUtil.dp2px(65));
        }

        if (posX + mWidth > TextLayoutUtil.getScreenWidth(mContext)) {
            posX = TextLayoutUtil.getScreenWidth(mContext) - mWidth - 16;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWindow.setElevation(8f);
        }
        mWindow.showAtLocation(mSelectHelper.mTextView, Gravity.NO_GRAVITY, posX, posY);
    }

    public void dismiss() {
        mWindow.dismiss();
    }

    public boolean isShowing() {
        return mWindow.isShowing();
    }
}
