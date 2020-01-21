package com.huatu.handheld_huatu.business.me;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 *
 */
public class SerciveDialog extends Dialog {
    public View mContentView;

    public SerciveDialog(Context context, int resid) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mContentView = View.inflate(context, resid, null);

        setContentView(mContentView);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }
}
