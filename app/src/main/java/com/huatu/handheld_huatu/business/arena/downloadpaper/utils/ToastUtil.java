package com.huatu.handheld_huatu.business.arena.downloadpaper.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.UniApplicationLike;

import java.util.Timer;
import java.util.TimerTask;


public class ToastUtil {

    private static Toast toast;
    private static View view;

    private ToastUtil() {

    }

    @SuppressLint("ShowToast")
    private static void getToast(Context context) {
        if (toast == null) {
            toast = new Toast(context);
        }
        if (view == null) {
            view = Toast.makeText(context, "", Toast.LENGTH_SHORT).getView();
        }
        toast.setView(view);
    }

    public static void showToast(CharSequence msg) {
        showToast(msg, 2000);
    }

    private static Timer cancelTimer;
    private static Timer timer;

    public static void showToast(final CharSequence msg, final int duration) {
        UniApplicationLike.getApplicationHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (cancelTimer != null) {
                        cancelTimer.cancel();
                    }
                    if (timer != null) {
                        timer.cancel();
                    }

                    getToast(UniApplicationContext.getContext());

                    toast.setText(msg);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            toast.show();
                        }
                    }, 0, 2000);

                    cancelTimer = new Timer();
                    cancelTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            toast.cancel();
                            timer.cancel();
                            cancelTimer = null;
                        }
                    }, duration);
                } catch (Exception e) {

                }
            }
        });
    }
}
