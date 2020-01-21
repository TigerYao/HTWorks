package com.huatu.handheld_huatu.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ljzyuhenda on 16/7/13.
 */
public class CustomDialog extends Dialog {
    public View mContentView;

    public CustomDialog(Context context, @LayoutRes int resid) {
        super(context, com.gensee.rtmpresourcelib.R.style.CustomProgressDialog2);

     /*   requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));*/
        Window window = this.getWindow(); // 得到对话框
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mContentView = View.inflate(context, resid, null);

        setContentView(mContentView);
        //getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setCanceledOnTouchOutside(false);
    }
}
