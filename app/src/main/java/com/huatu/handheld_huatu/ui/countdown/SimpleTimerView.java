package com.huatu.handheld_huatu.ui.countdown;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;



public class SimpleTimerView extends LinearLayout {
    private TextView tv_status;
    // 天
    private TextView tv_day;
    // 小时
    private TextView tv_hour;
    // 分钟
    private TextView tv_minute;
    // 秒
    private TextView tv_second;

    private Context context;

    public SimpleTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
/*        View view = inflater.inflate(R.layout.layout_simple_timer_view, this);

        tv_status = (TextView) view.findViewById(R.id.status);
        tv_day = (TextView) view.findViewById(R.id.tv_day);
        tv_hour = (TextView) view.findViewById(R.id.tv_hour);
        tv_minute = (TextView) view.findViewById(R.id.tv_minute);
        tv_second = (TextView) view.findViewById(R.id.tv_second);*/
    }

    public void setTime(int status, long millisUntilFinished) {
        long time = millisUntilFinished/1000 ;
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
        }
         setTimeValue(remainDay, remainHour, remainMin, remainSec);
    }

    public void setTimeValue(int day, int hour, int minute, int second) {
        tv_day.setText(day < 10 ? "0" + day : day + "");
        tv_hour.setText(hour < 10 ? "0" + hour : hour + "");
        tv_minute.setText(minute < 10 ? "0" + minute : minute + "");
        tv_second.setText(second < 10 ? "0" + second : second + "");
    }

    public void doOnTick(long millisUntilFinished) {
        long millisInFuture = millisUntilFinished/1000 ;
        int day = (int) (millisInFuture / 86400);
        int hour = (int) (millisInFuture % 86400 / 3600);
        int minute = (int) (millisInFuture % 3600 / 60);
        int second = (int) (millisInFuture % 60);

        setTimeValue(day, hour, minute, second);
    }


  /*  public enum SimpleTimerViewStyle {
        DEFALUT_STYLE(1, R.drawable.bg_rush_sale_count_down_view),
        ROSEGOLD_STYLE(2, R.drawable.address_deault_shape);

        SimpleTimerViewStyle(int type, int resourceId) {
        }

        public static int getStyle(int type) {
            switch (type) {
                case 1:
                    return R.drawable.bg_rush_sale_count_down_view;

                case 2:
                    return R.drawable.address_deault_shape;

                default:
                    return R.drawable.bg_rush_sale_count_down_view;
            }
        }
    }

    *//**
     *  设置当前样式(1为黑色背景 2为玫瑰金)
     * @param styleType
     *//*
    public void setStyle(int styleType) {
        tv_day.setBackgroundResource(SimpleTimerViewStyle.getStyle(styleType));
        tv_hour.setBackgroundResource(SimpleTimerViewStyle.getStyle(styleType));
        tv_minute.setBackgroundResource(SimpleTimerViewStyle.getStyle(styleType));
        tv_second.setBackgroundResource(SimpleTimerViewStyle.getStyle(styleType));
    }*/
}
