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
public class CountDownTimerView extends LinearLayout {
    private TextView tv_status;
    // 天
//	private TextView tv_day;
    // 小时
    private TextView tv_hour;
    // 分钟
    private TextView tv_minute;
    // 秒
    private TextView tv_second;

    private Context context;


    // 计时器
    private CustomTimer timer;

    public CountDownTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   /*     View view = inflater.inflate(R.layout.layout_rush_buy_count_down_view, this);

        tv_status = (TextView) view.findViewById(R.id.status);
//		tv_day = (TextView) view.findViewById(R.id.tv_day);
        tv_hour = (TextView) view.findViewById(R.id.tv_hour);
        tv_minute = (TextView) view.findViewById(R.id.tv_minute);
        tv_second = (TextView) view.findViewById(R.id.tv_second);*/
    }

    /**
     * @param
     * @return void
     * @throws
     * @Description: 开始计时
     */
    public void start() {
        if (timer == null || timer.isRunning()) return;
        timer.doStart();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Description: 停止计时
     */
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * @param
     * @return void
     * @throws Exception
     * @throws
     * @Description: 设置倒计时的时长
     */
    /*public void setTime(int day, int hour, int min, int sec) {

		if (hour >= 60 || min >= 60 || sec >= 60 || day < 0 || hour < 0 || min < 0
				|| sec < 0) {
			throw new RuntimeException("Time format is error,please check out your code");
		}

		this.day = day;
		this.hour = hour;
		this.minute = min;
		this.second = sec;

		tv_day.setText(day < 10 ? "0" + day : day + "");
		tv_hour.setText(hour < 10 ? "0" + hour : hour + "");
		tv_minute.setText(minute < 10 ? "0" + minute : minute + "");
		tv_second.setText(second < 10 ? "0" + second : second + "");

	}*/
    public void setTime(int status, long time) {

        if(null!=timer&&timer.isRunning()) return;
//		int remainDay = (int) (time / 86400);
        int remainHour = (int) (time / 3600);
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
        }

//		tv_day.setText(day < 10 ? "0" + day : day + "");

        setTimeValue(remainHour,remainMin,remainSec);
        timer = new CustomTimer(time , 1000,this);
        start();
    }

    public void setTimeValue(int hour,int minute,int second){
        tv_hour.setText(hour < 10 ? "0" + hour : hour + "");
        tv_minute.setText(minute < 10 ? "0" + minute : minute + "");
        tv_second.setText(second < 10 ? "0" + second : second + "");
    }


/*    @Override
    public void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        stop();
    }*/

   static class  CustomTimer extends CountDownTimer {

        private boolean mIsRunning = false;
        //	private int day;
        private int mHour;
        private int mMinute;
        private int mSecond;

        private WeakReference<CountDownTimerView>  mCountTimerView;
        public CustomTimer(long millisInFuture, long countDownInterval,CountDownTimerView countTimeView) {
            super(millisInFuture*1000, countDownInterval);

            mHour = (int) (millisInFuture / 3600);
            mMinute = (int) (millisInFuture % 3600 / 60);
            mSecond = (int) (millisInFuture % 60);

            mCountTimerView=new WeakReference<CountDownTimerView>(countTimeView);
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
            CountDownTimerView view = mCountTimerView.get();
            if(view!=null) view.setTimeValue(0,0,0);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            //countDown();
            mSecond--;
            if (mSecond < 0) {
                mMinute--;
                mSecond = 59;
                if (mMinute < 0) {
                    mMinute = 59;
                    mHour--;
                    if (mHour < 0) {
                        //stop();
                        this.cancel();
                        return;
//					hour = 23;
//					day--;

					/*if (day < 0) {
						Toast.makeText(context, "时间到了", Toast.LENGTH_SHORT).show();
						stop();
						return;
					}*/
                    }
                }
            }
            CountDownTimerView view = mCountTimerView.get();
            if(view!=null) view.setTimeValue(mHour,mMinute,mSecond);
        }
    }
}
