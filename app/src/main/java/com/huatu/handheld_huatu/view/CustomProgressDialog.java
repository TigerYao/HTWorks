package com.huatu.handheld_huatu.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.CommonUtils;

/**
 * Created by ljzyuhenda on 15/3/27.
 */
public class CustomProgressDialog {
    private AlertDialog alertDialog;
    private ProgressBar pb_update;
    private TextView tv_update_percent;
    private TextView tv_update_info;
    private long total;
    private final int MESSAGE_PROGRESS = 0;
    private long progress;
    private long lastUpdateTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_PROGRESS:
                    updateLabel();
                    break;
            }
        }
    };
    private TextView id_tv_loadingmsg;

    public CustomProgressDialog(Context context) {
        alertDialog = new AlertDialog.Builder(context).create();
        initDlg();
    }

    private void initDlg() {
        alertDialog.show();
        Window window = alertDialog.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,item_mainactivity_dialog.xml文件中定义view内容
        window.setContentView(R.layout.dialog_showprogress);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pb_update = (ProgressBar) window.findViewById(R.id.pb_update);
        tv_update_percent = (TextView) window.findViewById(R.id.tv_update_percent);
        tv_update_info = (TextView) window.findViewById(R.id.tv_update_info);
        id_tv_loadingmsg = (TextView) window.findViewById(R.id.id_tv_loadingmsg);
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            id_tv_loadingmsg.setText(title);
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;

        pb_update.setProgress(progress);
        long currentTime = SystemClock.uptimeMillis();
        if (currentTime - lastUpdateTime >= 500) {
            lastUpdateTime = currentTime;
            handler.sendEmptyMessage(MESSAGE_PROGRESS);
        }
    }

    private void updateLabel() {
        if (CustomProgressDialog.this.total != 0) {
            tv_update_percent.setText(CustomProgressDialog.this.progress * 100 / CustomProgressDialog.this.total + "%");
            tv_update_info.setText(CommonUtils.FormatFileSize(CustomProgressDialog.this.progress) + "/" + CommonUtils.FormatFileSize(CustomProgressDialog.this.total));
        } else {
            tv_update_percent.setText("0%");
            tv_update_info.setText(0 + "/" + 0);
        }
    }

    public void setMax(int length) {
        this.total = length;

        pb_update.setMax(length);
    }

    public void setCancelable(boolean isCancelable) {
        alertDialog.setCancelable(isCancelable);
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}
