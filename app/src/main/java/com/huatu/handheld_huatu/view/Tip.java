package com.huatu.handheld_huatu.view;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.huatu.handheld_huatu.R;
import com.huatu.widget.ProgressHelper;
import com.huatu.widget.ProgressWheel;


public class Tip {

    private Context _context;
    private Dialog _mDialog;
    private View _contentView;
    private ImageView _loadingImg;

    public View getContentView() {
        return _contentView;
    }

    protected Dialog getDialog() {
        return _mDialog;
    }

    protected Context getContext() {
        return _context;
    }

    protected ImageView getloadingImg() {
        return _loadingImg;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return (T) getContentView().findViewById(id);
    }

    ProgressHelper mProgressHelper;

    public Tip(Context context, int dialogThemeId) {
        _context = context;
        _mDialog = new Dialog(context, dialogThemeId); // new AlertDialog.Builder(context).Create();
        Window window = _mDialog.getWindow();//.Window;

        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 20;
        window.setAttributes(wl);//  window.SetFlags(WindowManagerFlags.Fullscreen, WindowManagerFlags.ForceNotFullscreen);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        _mDialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 255);
        _mDialog.setOnKeyListener(keylistener);
        _mDialog.setCanceledOnTouchOutside(false);//设置区域外点击消失
    }

    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                Dismiss();
                return true;
            } else {
                return false;
            }
        }
    };

    public void SetGravity(int flags) {
        Window window = _mDialog.getWindow();
        window.setGravity(flags);
    }

    public void SetContentView(View root) {
        _contentView = root;
        _mDialog.setContentView(root);
    }

    public void SetContentView(int layoutResId) {
        LayoutInflater inflator =
                (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //.LAYOUT_INFLATER_SERVICE);
        SetContentView(inflator.inflate(layoutResId, null));
    }

    public void Show(String showtxt) {
        ((TextView) _contentView.findViewById(R.id.LoadingTxt)).setText(showtxt, BufferType.NORMAL);
        if (mProgressHelper == null) {
            mProgressHelper = new ProgressHelper(_context);
            mProgressHelper.setProgressWheel((ProgressWheel) _contentView.findViewById(R.id.progressWheel));
        } else {
           if(!_mDialog.isShowing()){
                mProgressHelper.spin();
                _mDialog.show();
            }
         }
    }

    public void Dismiss() {
        if (mProgressHelper != null) mProgressHelper.stopSpinning();
        _mDialog.dismiss();
    }

    public void SimpleShow() {
        if (_mDialog != null&&(!_mDialog.isShowing())) _mDialog.show();
    }

    public boolean isShow() {
        return _mDialog.isShowing();//.isShowing;
    }
}


