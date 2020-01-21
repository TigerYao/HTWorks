package com.huatu.handheld_huatu.business.ztk_zhibo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.InputMethodUtils;

/**
 * Created by liyj on 2018/8/20.
 *
 * @email liyj@huatu.com
 * @detail
 */
public abstract class LiveChatEditDialog extends Dialog {
    private View mRootView;
    private EditText mEtTextView;
    private Context mContext;
    private View mSendMsgBtn;
    private TextView mLimitTips;

    private int mMaxChars = 25;

    public LiveChatEditDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        this.mContext = context;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        setLayout();
        initEditListener();
        mSendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mEtTextView.getText())){
                    ToastUtils.showMessage("消息不能为空");
                    return;
                }
                String chatText = mEtTextView.getText().toString();
//                String richText = mEtTextView.getRichText();
                if (TextUtils.isEmpty(chatText)) {
                    ToastUtils.showMessage("消息不能为空");
                    return;
                }
                sendChatMsg(chatText, null, false);
                dismiss();
            }
        });

    }

    //弹出软键盘
    public void showKeyboard() {
        //其中editText为dialog中的输入框的 EditText
        if (mEtTextView != null) {
            //设置可获得焦点
            mEtTextView.setFocusable(true);
            mEtTextView.setFocusableInTouchMode(true);
            //请求获得焦点
            mEtTextView.requestFocus();
            InputMethodUtils.openKeybord(mEtTextView, getContext());
        }
    }

    private void initEditListener() {
        mEtTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) //发送按键action
                {
                    Method.hideKeyboard(mEtTextView);
                    String mSearchContent = mEtTextView.getText().toString();
                    if (TextUtils.isEmpty(mSearchContent)) {
                        return true;
                    }

                    mSendMsgBtn.performClick();

                }
                return false;
            }
        });

        mEtTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dismiss();
            }
        });
        mRootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > 200)) {
                    isShow = true;
                } else if (isShow && oldBottom != 0 && bottom != 0 && (bottom - oldBottom > 200)) {
                    isShow = false;
                    mEtTextView.setText("");
                    LiveChatEditDialog.super.dismiss();
                }
            }
        });
        mEtTextView.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                selectionStart = mEtTextView.getSelectionStart();
                selectionEnd = mEtTextView.getSelectionEnd();
                // 先去掉监听器，否则会出现栈溢出
                mEtTextView.removeTextChangedListener(this);
                int length = temp.length();
                if (length > mMaxChars) {
                    temp = editable.subSequence(0, mMaxChars);
                    mEtTextView.setText(temp);
                    if (selectionEnd > mMaxChars)
                        selectionEnd = mMaxChars;
                    mEtTextView.setSelection(selectionEnd);
                    length = mMaxChars;
                }
                mLimitTips.setText(length + "/25");
                mEtTextView.addTextChangedListener(this);
            }
        });
    }

    public void showDialog() {
        show();
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }


    @Override
    public void dismiss() {
        if (isShow)
            InputMethodUtils.hideMethod(getContext(), mEtTextView);
        else {
            mEtTextView.setText("");
            super.dismiss();
        }
    }

    public boolean isShow = false;

    public abstract void sendChatMsg(String chat, String richMes, boolean isEmoji);

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    private void initView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.land_video_chat_layout, null);
        mSendMsgBtn = mRootView.findViewById(R.id.tv_send_msg);
        mEtTextView = (EditText) mRootView.findViewById(R.id.et_comment);
        mLimitTips = (TextView) mRootView.findViewById(R.id.dialog_limit_textsize);
        mEtTextView.requestFocus();
        setContentView(mRootView);
    }

    private void setLayout() {
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(p);
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }


}
