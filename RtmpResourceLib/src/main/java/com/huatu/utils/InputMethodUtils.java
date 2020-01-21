/**
 * <pre>
 * Copyright (C) 2015  Soulwolf xiaodaow3.0-branch
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
package com.huatu.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 输入法软键盘的工具类
 * <p/>
 * author : Soulwolf Create by 2015/8/12 14:12
 * email  : ToakerQin@gmail.com.
 */
public final class InputMethodUtils {

    public static final long TIME = 100;

    /**
     * 打开软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void closeKeybord(EditText mEditText, Context mContext) {
        mEditText.clearFocus();
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }


    /**
     * 隐藏软键盘
     */
    public static void hideMethod(Context context, View view) {
        if (view != null && view instanceof EditText && view.isFocused()) {
            view.clearFocus();
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 延迟显示软键盘
     */
    public static void showMethodDelayed(final Context context, final EditText mSearchKeyword, final long time) {
        mSearchKeyword.setFocusableInTouchMode(true);
        mSearchKeyword.requestFocus();
        // 延迟弹出输入法
        mSearchKeyword.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodUtils.openKeybord(mSearchKeyword, context);
            }
        }, time);
    }

    public static void showMethodDelayed(final Context context, final EditText mSearchKeyword,Runnable runnable, final long time) {
        mSearchKeyword.setFocusableInTouchMode(true);
        mSearchKeyword.requestFocus();
        // 延迟弹出输入法
        mSearchKeyword.postDelayed(runnable, time);
    }
}
