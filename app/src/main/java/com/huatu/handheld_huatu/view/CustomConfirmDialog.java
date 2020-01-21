package com.huatu.handheld_huatu.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;

/**
 * Created by saiyuan on 2016/10/25.
 */
public class CustomConfirmDialog extends Dialog {
    private Context mContext;
    private LayoutInflater mInflater;
    private View mDialogView;
    private TextView mTitleView;
    private TextView mMessageView;
    private TextView mCancelBtn;
    private TextView mOkBtn;
    private View mBtnDivider;
    private View mDivider;

    private String strTitle;
    private String strMessage;
    private int cancelColor;
    private int okColor;

    private android.view.View.OnClickListener mNegativeBtnClickListener;
    private android.view.View.OnClickListener mPositiveBtnClickListener;
    private SpannableStringBuilder bTitle;
    private int mLayoutId = -1;

    public CustomConfirmDialog(Context context, int theme) {
        this(context, theme, -1);

    }

    public CustomConfirmDialog(Context context, int theme, int layoutId) {
        super(context, theme);
        this.mLayoutId = layoutId;
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initView(mInflater);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mDialogView);
    }

    private void initView(LayoutInflater inflater) {
        mDialogView = inflater.inflate(mLayoutId == -1 ? R.layout.layout_custom_confirm_dialog : mLayoutId, null);

        mTitleView = mDialogView.findViewById(R.id.custom_dialog_title_view);
        mMessageView = mDialogView.findViewById(R.id.custom_dialog_message_view);
        mCancelBtn = mDialogView.findViewById(R.id.custom_dialog_cancel_btn);
        mOkBtn = mDialogView.findViewById(R.id.custom_dialog_confirm_btn);

        mBtnDivider = mDialogView.findViewById(R.id.custom_dialog_btn_divider);
        mDivider = mDialogView.findViewById(R.id.custom_dialog_divider);

        mCancelBtn.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (mNegativeBtnClickListener != null) {
                    mNegativeBtnClickListener.onClick(v);
                }
            }
        });

        mOkBtn.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (mPositiveBtnClickListener != null) {
                    mPositiveBtnClickListener.onClick(v);
                }
            }
        });
    }

    public void setCancelBtnVisibility(boolean visible) {
        if (visible) {
            mCancelBtn.setVisibility(View.VISIBLE);
        } else {
            mCancelBtn.setVisibility(View.GONE);
        }
        setBtnDividerVisibility(isNeedShowBtnDivider());
    }

    /**
     * 当只显示ok或只显示cancel时不需要显示中间的divider
     */
    public boolean isNeedShowBtnDivider() {
        if (mCancelBtn.getVisibility() == View.VISIBLE
                && mOkBtn.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    public void setContentGravity(int gravity) {
        mMessageView.setGravity(gravity);
    }

    public void setNegativeColor(int color) {
        cancelColor = color;
        if (cancelColor != 0) {
            mCancelBtn.setTextColor(cancelColor);
        }
    }

    public void setPositiveColor(int color) {
        okColor = color;
        if (okColor != 0) {
            mOkBtn.setTextColor(okColor);
        }
    }

    /**
     * 设置两按钮中间的divider的可见性
     */
    public void setBtnDividerVisibility(boolean visible) {
        if (visible) {
            mBtnDivider.setVisibility(View.VISIBLE);
        } else {
            mBtnDivider.setVisibility(View.GONE);
        }
    }

    public void setOkBtnConfig(int marginLeftRight, int marginTopBottom, int resId) {
        ((ViewGroup.MarginLayoutParams) mOkBtn.getLayoutParams()).setMargins(marginLeftRight, marginTopBottom, marginLeftRight, marginTopBottom);
        mOkBtn.setBackgroundResource(resId);
        mDivider.setVisibility(View.GONE);
    }

    public void setCancleBtnConfig(int marginLeftRight, int marginTopBottom, int resId) {
        ((ViewGroup.MarginLayoutParams) mCancelBtn.getLayoutParams()).setMargins(marginLeftRight, marginTopBottom, marginLeftRight, marginTopBottom);
        mCancelBtn.setBackgroundResource(resId);
        mDivider.setVisibility(View.GONE);
    }

    public final void setOkBtnVisibility(boolean visible) {
        if (visible) {
            mOkBtn.setVisibility(View.VISIBLE);
        } else {
            mOkBtn.setVisibility(View.GONE);
        }
        setBtnDividerVisibility(isNeedShowBtnDivider());
    }

    public void setTitleColor(int color) {
        if(color != 0) {
            mTitleView.setTextColor(color);
            setItemVisible();
        }
    }
    public void setTitle(String title) {
        strTitle = title;
        mTitleView.setText(strTitle);
        setItemVisible();
    }

    public void setTitle(SpannableStringBuilder title) {
        bTitle = title;
        mTitleView.setText(bTitle);
        setItemVisible();
    }

    public void setTitleSize(int size){
        mTitleView.setTextSize(size);
    }

    public void setTitleBold() {
        mTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }


    public View getContentView() {
        return mDialogView;
    }

    //    public void setTextView(int resId, String title) {
//        View view = mDialogView.findViewById(resId);
//        if(view == null || !(view instanceof TextView)) {
//            return;
//        }
//        view.setVisibility(View.VISIBLE);
//        if (title != null) {
//            ((TextView)view).setText(title);
//        }
//    }
    public void setMessage(String message) {
        setMessage(message, 0, 0);
    }

    public void setMessage(String message, int size) {
        setMessage(message, size, 0);
    }

    public void setMessage(String message, int size, int color) {
        if (message != null) {
            strMessage = message;
            mMessageView.setText(message);
        }
        if (size != 0) {
            mMessageView.setTextSize(size);
        }
        if (color != 0) {
            mMessageView.setTextColor(color);
        }
        setItemVisible();
    }

    public void setMessage(SpannableStringBuilder sb){
       if (sb != null) {
            strMessage = "sb";
            mMessageView.setText(sb);
            mMessageView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        setItemVisible();
    }

    public void setMessageBold() {
        mMessageView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    private void setItemVisible() {
        if (TextUtils.isEmpty(strMessage)) {
            mMessageView.setVisibility(View.GONE);
        } else {
            mMessageView.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(strTitle) && TextUtils.isEmpty(bTitle)) {
            mTitleView.setVisibility(View.GONE);
        } else {
            mTitleView.setVisibility(View.VISIBLE);
        }
    }

    public void setNegativeButton(String text, android.view.View.OnClickListener onClickListener) {
        setNegativeButton(text, false, onClickListener);
    }

    public void setNegativeButton(String text, boolean isBold, android.view.View.OnClickListener onClickListener) {
        if (!TextUtils.isEmpty(text)) {
            mCancelBtn.setText(text);
        }
        if (isBold) {
            TextPaint tp = mCancelBtn.getPaint();
            tp.setFakeBoldText(true);
            mCancelBtn.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
        }
        mNegativeBtnClickListener = onClickListener;
    }

    public void setPositiveButton(String text, android.view.View.OnClickListener onClickListener) {
        mOkBtn.setVisibility(View.VISIBLE);
        mBtnDivider.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(text)) {
            mOkBtn.setText(text);
        }
        mPositiveBtnClickListener = onClickListener;
    }

    public void adjustPositiveWidth(){

       LinearLayout.LayoutParams tmpParams= (LinearLayout.LayoutParams)mOkBtn.getLayoutParams();
       tmpParams.width= DisplayUtil.dp2px( 150);
        mOkBtn.setLayoutParams(tmpParams);
    }

    public void setPositiveButtonClickListener(android.view.View.OnClickListener onClickListener) {
        mPositiveBtnClickListener = onClickListener;
    }




    public static class Builder {
        private Context mContext;
        private String mTitleString;
        private String mMessageString;
        private int mMessageSize;
        private int mMessageColor;
        private String mNegativeBtnTextString;
        private android.view.View.OnClickListener mNegativeClickListener;
        private String mPositiveBtnTextString;
        private android.view.View.OnClickListener mPositiveClickListener;
        private OnCancelListener mOnCancelListener;
        private boolean isCanceledOnTouchOutside = true;
        private int okColor;
        private int cancelColor;
        private int mLayoutId = -1;



        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTitle(int resId) {
            mTitleString = mContext.getResources().getString(resId);
            return this;
        }

        public Builder setLayoutId(int resId) {
            mLayoutId = resId;
            return this;
        }

        public Builder setTitle(String title) {
            mTitleString = title;
            return this;
        }

        public Builder setMessage(int resId) {
            mMessageString = mContext.getResources().getString(resId);
            return this;
        }

        public Builder setMessage(String message) {
            mMessageString = message;
            return this;
        }

        public Builder setMessage(String message, int size, int color) {
            mMessageString = message;
            mMessageSize = size;
            mMessageColor = color;
            return this;
        }

        public Builder setNegativeColor(int color) {
            cancelColor = color;
            return this;
        }

        public Builder setPositiveColor(int color) {
            okColor = color;
            return this;
        }

        public Builder setNegativeButton(int stringId, android.view.View.OnClickListener onClickListener) {
            mNegativeBtnTextString = mContext.getResources().getString(stringId);
            mNegativeClickListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(String text, android.view.View.OnClickListener onClickListener) {
            mNegativeBtnTextString = text;
            mNegativeClickListener = onClickListener;
            return this;
        }

        public Builder setPositiveButton(int stringId, android.view.View.OnClickListener onClickListener) {
            mPositiveBtnTextString = mContext.getResources().getString(stringId);
            mPositiveClickListener = onClickListener;
            return this;
        }

        public Builder setPositiveButton(String text, android.view.View.OnClickListener onClickListener) {
            mPositiveBtnTextString = text;
            mPositiveClickListener = onClickListener;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean cancel) {
            this.isCanceledOnTouchOutside = cancel;
            return this;
        }

        public CustomConfirmDialog create() {
            CustomConfirmDialog dialog = mLayoutId == -1 ? new CustomConfirmDialog(mContext, R.style.CustomProgressDialog)
                    : new CustomConfirmDialog(mContext, R.style.CustomProgressDialog, mLayoutId);

            if (!TextUtils.isEmpty(mTitleString)) {
                dialog.setTitle(mTitleString);
            }

            if (!TextUtils.isEmpty(mMessageString) || mMessageSize > 0 || mMessageColor > 0) {
                dialog.setMessage(mMessageString, mMessageSize, mMessageColor);
            }

            if (!TextUtils.isEmpty(mNegativeBtnTextString)
                    || mNegativeClickListener != null) {
                dialog.setNegativeButton(mNegativeBtnTextString, mNegativeClickListener);
            }

            if (!TextUtils.isEmpty(mPositiveBtnTextString)
                    || mPositiveClickListener != null) {
                dialog.setPositiveButton(mPositiveBtnTextString, mPositiveClickListener);
            }

            if (mOnCancelListener != null) {
                dialog.setOnCancelListener(mOnCancelListener);
            }

            if (cancelColor > 0) {
                dialog.setNegativeColor(cancelColor);
            }

            if (okColor > 0) {
                dialog.setPositiveColor(okColor);
            }

            dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);

            return dialog;
        }
    }

    public static class SpannableBuilder extends Builder{
        SpannableStringBuilder mContentStringBuilder;

        public SpannableBuilder(Context context) {
            super(context);
        }


        public SpannableBuilder setContentStringBuilder(SpannableStringBuilder stringBuilder) {
            mContentStringBuilder = stringBuilder;
            return this;
        }

        public CustomConfirmDialog create() {

            CustomConfirmDialog dialog=  super.create();
            dialog.setMessage(mContentStringBuilder);
            return    dialog;
        }

    }
}
