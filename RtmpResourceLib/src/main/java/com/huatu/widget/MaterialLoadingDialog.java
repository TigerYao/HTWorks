/**
 * <pre>
 * Copyright 2015 Soulwolf Ching
 * Copyright 2015 The Android Open Source Project for xiaodaow3.0-branch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </pre>
 */
package com.huatu.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gensee.rtmpresourcelib.R;


/**
 * author: Soulwolf Created on 2015/8/25 23:02.
 * email : Ching.Soulwolf@gmail.com
 */
public class MaterialLoadingDialog extends Dialog {
    public final static int CALLBACK_ACTION_NONE = 0;
    public final static int CALLBACK_ACTION_PREFIX = 1;
    public final static int CALLBACK_ACTION_POSTFIX = 2;
    public final static int CALLBACK_ACTION_BOTH = 3;

    private long times = System.currentTimeMillis();

    private Handler mHandler = null;

    ProgressWheel mProgressWheel;
    TextView mShowText;

    private OnDismissCallback onDismissCallback = null;

    public MaterialLoadingDialog(Context context) {
        this(context, R.style.CustomProgressDialog2);
    }

    public MaterialLoadingDialog(Context context, int theme) {
        super(context, theme);
        initialize();
    }

    public static MaterialLoadingDialog newInstance(Context context){
        return new MaterialLoadingDialog(context);
    }

    private void initialize() {
        setCanceledOnTouchOutside(false);
        Window window = this.getWindow(); // 得到对话框
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.library_dialog_loading);
        mProgressWheel = (ProgressWheel) findViewById(R.id.xi_progress_loading);
        mShowText = (TextView)findViewById(R.id.xi_rotate_text);

        mHandler = new Handler(Looper.getMainLooper());
    }

    public void dismissDelay(final int callback_action_type) {
        if (onDismissCallback != null && (callback_action_type == CALLBACK_ACTION_BOTH
                || callback_action_type == CALLBACK_ACTION_PREFIX)) {
            onDismissCallback.onPreAction();
        }
        long time_now = System.currentTimeMillis();
        long delayd = Math.max(500, time_now - times);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
                if (onDismissCallback != null && (callback_action_type == CALLBACK_ACTION_BOTH
                        || callback_action_type == CALLBACK_ACTION_POSTFIX)) {
                    onDismissCallback.onPostAction();
                }
            }
        }, delayd);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!mProgressWheel.isSpinning()){
            mProgressWheel.spin();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mProgressWheel.isSpinning()){
            mProgressWheel.stopSpinning();
        }
    }

    public void setText(String text) {
        if(text != null)
            mShowText.setText(text);
    }

    public void showText(String text) {
        if(text != null && text.length() > 0) {
            mProgressWheel.setVisibility(View.GONE);
            mShowText.setVisibility(View.VISIBLE);
            mShowText.setText(text);
        } else {
            mProgressWheel.setVisibility(View.VISIBLE);
            mShowText.setVisibility(View.GONE);
        }
        show();
    }

    public void showTextWithLoading(String text) {
        mShowText.setVisibility(View.VISIBLE);
        mShowText.setVisibility(View.VISIBLE);
        if(text != null)
            mShowText.setText(text);
        show();
    }

    @Override
    public void show() {
        times = System.currentTimeMillis();
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void setOnDismissCallback(OnDismissCallback onDismissCallback) {
        this.onDismissCallback = onDismissCallback;
    }

    public interface OnDismissCallback {
        /**
         * 前置动作
         */
        void onPreAction();

        /**
         * 后置动作
         */
        void onPostAction();
    }
}
