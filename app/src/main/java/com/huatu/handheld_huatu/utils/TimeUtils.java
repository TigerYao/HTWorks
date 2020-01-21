package com.huatu.handheld_huatu.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 *
 */
public class TimeUtils {

    public static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void delayTask(Runnable r, long delayMillis) {
        mHandler.postDelayed(r, delayMillis);
    }

    @NonNull
    public static String getSecond2MinTime(int timeSecond) {
        if (timeSecond < 0) {
            return "";
        }
        int min = timeSecond / 60;
        int sec = timeSecond % 60;
        String strSec = String.valueOf(sec);
//        if (sec < 10) {
//            strSec = "0" + sec;
//        }
        String time = null;
        if (min > 0) {
            time = min + "′" + strSec + "″";
        } else {
            time = strSec + "″";
        }
        return time;
    }

    @NonNull
    public static String getSecond2OnlyMinTime(int timeSecond) {
        if (timeSecond < 0) {
            return "";
        }
        int min = timeSecond / 60;
        int sec = timeSecond % 60;
        String strSec = String.valueOf(sec);
//        if (sec < 10) {
//            strSec = "0" + sec;
//        }
        String time = null;
//        if (min > 0) {
//            time = min + "." + strSec;
//        } else {
//            time = "0."+strSec;
//        }
        if (sec > 0) {
            min += 1;
        }
        time = String.valueOf(min);
        return time;
    }

    @NonNull
    public static String getSecond22MinTime(int timeSecond) {
        if (timeSecond < 0) {
            return "00:00";
        }
        String min = String.valueOf(timeSecond / 60);
        String sec = String.valueOf(timeSecond % 60);
        if (min.length() == 1) {
            min = "0" + min;
        }
        if (sec.length() == 1) {
            sec = "0" + sec;
        }
        return String.format("%s:%s", min, sec);
    }

    @NonNull
    public static String getSecond22HourMinTime(int timeSecond) {
        if (timeSecond < 0) {
            return "00:00:00";
        }
        int hour = timeSecond / 3600;
        int min = timeSecond % 3600 / 60;
        int sec = timeSecond % 60;
        String hourStr = hour > 0 ? (hour + ":") : "";
        String minStr = min < 10 ? ("0" + min + ":") : (min + ":");
        String secStr = sec < 10 ? ("0" + sec) : (sec + "");
        return hourStr + minStr + secStr;
    }

    @NonNull
    public static String getSecond2HourMinTimeOther(int timeSecond) {
        if (timeSecond <= 0) {
            return "0秒";
        }
        int hour = timeSecond / 3600;
        int min = timeSecond % 3600 / 60;
        int sec = timeSecond % 60;
        String hourStr = hour > 0 ? (hour + "小时") : "";
        String minStr = (hour == 0 && min == 0) ? "" : ((min >= 10 || hour == 0) ? (min + "分钟") : ("0" + min + "分钟"));
        String secStr = ((hour == 0 && min == 0) || sec >= 10) ? (sec + "秒") : ("0" + sec + "秒");
        return hourStr + minStr + secStr;
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getFormatData(String format, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(time);
        return currentTime;
    }

    public static String getSCDayFormatData(long time) {
        return getFormatData("yyyy年MM月dd日", time);
    }

    public static String getFormatData(long time) {
        return getFormatData("yyyy-MM-dd", time);
    }

    public static String getSCHourFormatData(long time) {
        return getFormatData("HH:mm", time);
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    public static String getTime(int time) {
        time = time / 1000;
        return String.format("%02d", time / 60) + ":" +
                String.format("%02d", time % 60);
    }


}
