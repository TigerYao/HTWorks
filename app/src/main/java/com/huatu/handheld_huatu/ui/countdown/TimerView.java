/*
 * Copyright (c) 2014, 青岛司通科技有限公司 All rights reserved.
 * File Name：RushBuyCountDownTimerView.java
 * Version：V1.0
 * Author：zhaokaiqiang
 * Date：2014-9-26
 */
package com.huatu.handheld_huatu.ui.countdown;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;



import java.lang.ref.WeakReference;

@SuppressLint("HandlerLeak")
public class TimerView extends LinearLayout {
    private TextView tv_status;
    // 天
    private TextView tv_day;
    private TextView tvDay;
    // 小时
    private TextView tv_hour;
    // 分钟
    private TextView tv_minute;
    // 秒
    private TextView tv_second;


    private Context context;


    // 计时器
    private CustomTimer timer;

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      /*  View view = inflater.inflate(R.layout.layout_timer_view, this);

        tv_status = (TextView) view.findViewById(R.id.status);
        tv_day = (TextView) view.findViewById(R.id.tv_day);
        tvDay = (TextView) view.findViewById(R.id.day);
        tv_hour = (TextView) view.findViewById(R.id.tv_hour);
        tv_minute = (TextView) view.findViewById(R.id.tv_minute);
        tv_second = (TextView) view.findViewById(R.id.tv_second);*/
    }

    /**
     * 开始倒计时
     */
    public void start() {
        if (timer == null || timer.isRunning()) return;
        timer.doStart();
    }

    /**
     * 停止倒计时
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setTime(int status, long time) {

        if (null != timer && timer.isRunning()) return;
        int remainDay = (int) (time / 86400);
        int remainHour = (int) (time % 86400 / 3600);
        int remainMin = (int) (time % 3600 / 60);
        int remainSec = (int) (time % 60);
        switch (status) {
            case 0:
                tv_status.setText("距开始");
                break;
            case 1:
                tv_status.setText("距结束");
                break;
            case 2:
                tv_status.setText("已结束");
                break;
            case 3:
                tv_status.setText("");
                break;
        }

        setTimeValue(remainDay, remainHour, remainMin, remainSec);
        timer = new CustomTimer(time, 1000, this);
        start();
    }

    public void setTimeValue(int day, int hour, int minute, int second) {
        if (day < 1) {
            tv_day.setVisibility(GONE);
            tvDay.setVisibility(GONE);
        }
        tv_day.setText(day < 10 ? "0" + day : day + "");
        tv_hour.setText(hour < 10 ? "0" + hour : hour + "");
        tv_minute.setText(minute < 10 ? "0" + minute : minute + "");
        tv_second.setText(second < 10 ? "0" + second : second + "");
    }

    static class CustomTimer extends CountDownTimer {

        private boolean mIsRunning = false;
        private int day;
        private int mHour;
        private int mMinute;
        private int mSecond;

        private WeakReference<TimerView> mCountTimerView;

        public CustomTimer(long millisInFuture, long countDownInterval, TimerView countTimeView) {
            super(millisInFuture * 1000, countDownInterval);

            day = (int) (millisInFuture / 86400);
            mHour = (int) (millisInFuture % 86400 / 3600);
            mMinute = (int) (millisInFuture % 3600 / 60);
            mSecond = (int) (millisInFuture % 60);

            mCountTimerView = new WeakReference<TimerView>(countTimeView);
        }

        public void doStart() {
            mIsRunning = true;
            super.start();
        }

        public boolean isRunning() {
            return mIsRunning;
        }

        @Override
        public void onFinish() {
            mIsRunning = false;
            TimerView view = mCountTimerView.get();
            if (view != null) view.setTimeValue(0, 0, 0, 0);
        }

        @Override
        public void onTick(long millisUntilFinished) {
                mSecond--;
                if (mSecond < 0) {
                    mMinute--;
                    mSecond = 59;
                    if (mMinute < 0) {
                        mMinute = 59;
                        mHour--;
                        if (mHour < 0) {
                            mHour = 23;
					        day--;
                            if (day < 0) {
                                this.cancel();
                                return;
                            }
                        }
                    }
                }
            TimerView view = mCountTimerView.get();
            if (view != null) view.setTimeValue(day, mHour, mMinute, mSecond);
        }
    }
}
