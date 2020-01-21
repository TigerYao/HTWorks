package com.qmuiteam.qmui.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.gensee.rtmpresourcelib.R;

/**
 * Created by cjx on 2018\7\26 0026.
 */

public class QMUIDialog extends Dialog {
    boolean mCancelable = true;
    boolean mShowInCenter=false;
    boolean mFullScreen=true;

    private boolean mCanceledOnTouchOutside = true;
    private boolean mCanceledOnTouchOutsideSet;
    private Context mBaseContext;

    public void setCanFullScreen(boolean canFullScreen){
        mFullScreen=canFullScreen;
    }

  /*  public QMUIDialog(Context context) {
        this(context, R.style.QMUI_Dialog);
    }*/
    public QMUIDialog(Context context, int styleRes,boolean showinCenter){
        this(context,styleRes);
        this.mShowInCenter=showinCenter;
    }

    public QMUIDialog(Context context, int styleRes) {
        super(context, styleRes);
        mBaseContext = context;
        init();
    }

    private void init() {
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialog();

    }


    private void applyCompat() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    private void initDialog() {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        if(mFullScreen)
            applyCompat();
        WindowManager.LayoutParams wmlp = window.getAttributes();
        wmlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wmlp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        if(mShowInCenter) {
            wmlp.gravity = Gravity.CENTER;
        } else {
            wmlp.gravity = Gravity.BOTTOM;
        }
        window.setAttributes(wmlp);
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
        mCancelable = cancelable;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
        if (cancel && !mCancelable) {
            mCancelable = true;
        }
        mCanceledOnTouchOutside = cancel;
        mCanceledOnTouchOutsideSet = true;
    }

    boolean shouldWindowCloseOnTouchOutside() {
        if (!mCanceledOnTouchOutsideSet) {
            if (Build.VERSION.SDK_INT < 11) {
                mCanceledOnTouchOutside = true;
            } else {
                TypedArray a = getContext().obtainStyledAttributes(
                        new int[]{android.R.attr.windowCloseOnTouchOutside});
                mCanceledOnTouchOutside = a.getBoolean(0, true);
                a.recycle();
            }
            mCanceledOnTouchOutsideSet = true;
        }
        return mCanceledOnTouchOutside;
    }

    void cancelOutSide() {
        if (mCancelable && isShowing() && shouldWindowCloseOnTouchOutside()) {
            cancel();
        }
    }

    public void showWithImmersiveCheck(Activity activity) {
        // http://stackoverflow.com/questions/22794049/how-to-maintain-the-immersive-mode-in-dialogs
        Window window = getWindow();
        if (window == null) {
            return;
        }

        Window activityWindow = activity.getWindow();
        int activitySystemUi = activityWindow.getDecorView().getSystemUiVisibility();
        if ((activitySystemUi & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN ||
                (activitySystemUi & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            window.getDecorView().setSystemUiVisibility(
                    activity.getWindow().getDecorView().getSystemUiVisibility());
            super.show();
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
            super.show();
        }
    }

    public void showWithImmersiveCheck() {
        if (!(mBaseContext instanceof Activity)) {
            super.show();
            return;
        }
        Activity activity = (Activity) mBaseContext;
        showWithImmersiveCheck(activity);
    }

    @Override
    public void show() {
        super.show();
    }
}