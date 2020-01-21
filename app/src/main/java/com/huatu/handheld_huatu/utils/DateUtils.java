package com.huatu.handheld_huatu.utils;

import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {



    public static String getStringTime(int time) {
        time = time / 1000;
        return String.format("%02d", time / 60) + "分钟" +
                String.format("%02d", time % 60) + "秒";
    }

    public static String getTime(int time) {
        time = time / 1000;
        return String.format("%02d", time / 60) + ":" +
                String.format("%02d", time % 60);
    }

    public static String formatMs(long longdate) {
        Date date = new Date(longdate);
        SimpleDateFormat sdf = new SimpleDateFormat("M月dd日");
        return sdf.format(date);
    }

    public static String formatAMs(long longdate) {
        Date date = new Date(longdate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月dd日");
        return sdf.format(date);
    }

    public static String changeFormatYMD(String stringDate) {
        Date date = new Date(stringDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
        return sdf.format(date);
    }

    public static String getStrTime(String beginTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M月d日");
        String format = simpleDateFormat.format(new Date());
        if (!TextUtils.isEmpty(beginTime) && beginTime.contains(format)) {
            String newTime = beginTime.replace(format, "今日");
            return newTime;
        }
        return beginTime;
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

    public static String getFormatData(SimpleDateFormat sdf, long time) {

        String currentTime = sdf.format(time);
        return currentTime;
    }

    public static String getSCDayFormatData(long time) {
        return getFormatData("yyyy年MM月dd日", time);
    }

    public static String getSCHourFormatData(long time) {
        return getFormatData("HH:mm", time);
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    public static String getCurrentTime2() {
        return getCurrentTime("yyyy年MM月dd日  HH:mm");
    }

    public static String getCurrentYear() {
        return getCurrentTime("yyyy");
    }


    public static SpannableStringBuilder formatTime(long mss) {
        long days = mss / (60 * 60 * 24);
        long hours = (mss % (60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % (60 * 60)) / 60;
        long seconds = mss % 60;
        String strDay = days < 10 ? "0" + days : "" + days;
        String strHour = hours < 10 ? "0" + hours : "" + hours;
        String strMinute = minutes < 10 ? "0" + minutes : "" + minutes;
        String strSecond = seconds < 10 ? "0" + seconds : "" + seconds;
        String time = days > 0 ? strDay + "" : strHour + ":" + strMinute + ":" + strSecond;
        SpannableStringBuilder newString = new SpannableStringBuilder(time);
        newString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(UniApplicationContext.getContext(),
                R.color.main_color)), 0, newString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return days > 0 ? newString.append(" 天") : newString;
    }


    public static String getCaculteTime(long currentTime) {
        long day = currentTime / (60 * 60 * 24);
        long hour = (currentTime % (60 * 60 * 24)) / (60 * 60);
        long minuteTime = day > 0 ? (currentTime + 60) : currentTime;
        long minute = (minuteTime % (60 * 60)) / 60;
        long second = (currentTime % 60);
        String time = (hour >= 10 ? hour : "0" + hour) + ":" + (minute >= 10 ? minute : "0" + minute) + ":" + (second >= 10 ? second : "0" + second);
        if (day > 0) {
            time = day + "天" + (hour >= 10 ? hour : "0" + hour) + "小时" + (minute >= 10 ? minute : "0" + minute) + "分";
        }
        return time;
    }


    public static String getCourseTime(long currentTime) {
        long day = currentTime / (60 * 60 * 24);
        long hour = (currentTime % (60 * 60 * 24)) / (60 * 60);
//        long minuteTime =  day > 0 ? (currentTime + 60) : currentTime;
        long minute = (currentTime % (60 * 60)) / 60;
        long second = (currentTime % 60);
        String time = (hour >= 10 ? hour : "0" + hour) + "小时" + (minute >= 10 ? minute : "0" + minute) + "分" + (second >= 10 ? second : "0" + second) + "秒";
        if (day > 0) {
            time = day + "天" + (hour >= 10 ? hour : "0" + hour) + "小时" + (minute >= 10 ? minute : "0" + minute) + "分" + (second >= 10 ? second : "0" + second) + "秒";
        }
        return time;
    }

    public static String formatStrTime(long mss) {
        long days = mss / (60 * 60 * 24);
        String strDay = days < 10 ? "0" + days : "" + days;
        if (days > 0) return strDay + " 天";

        long hours = (mss % (60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % (60 * 60)) / 60;
        long seconds = mss % 60;

        String strHour = hours < 10 ? "0" + hours : "" + hours;
        String strMinute = minutes < 10 ? "0" + minutes : "" + minutes;
        String strSecond = seconds < 10 ? "0" + seconds : "" + seconds;
        return strHour + ":" + strMinute + ":" + strSecond;
    }

    public static SpannableStringBuilder formatMyTime(long mss) {
//        long days = mss / (60 * 60 * 24);
        long hours = mss / (60 * 60);
        long minutes = (mss % (60 * 60)) / 60;
        long seconds = mss % 60;
        String strHour = hours < 10 ? "0" + hours : "" + hours;
        String strMinute = minutes < 10 ? "0" + minutes : "" + minutes;
        String strSecond = seconds < 10 ? "0" + seconds : "" + seconds;
        String time;
        if (!strHour.equals("00")) {
            time = strHour + "小时" + strMinute + "分钟";
        } else {
            time = strMinute + "分钟";
        }
        SpannableStringBuilder newString = new SpannableStringBuilder(time);
//        newString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(UniApplicationContext.getContext(),
//                R.color.main_color)), 0, newString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return newString;
    }

    /**
     * 把剩余的毫秒数转换为 10:20:30.5
     */
    public static String millToTime(long milliSeconds) {
        if (milliSeconds < 0) {
            return "00:00:00.0";
        }
        long hours = milliSeconds / (1000 * 60 * 60);
        long minutes = milliSeconds % (1000 * 60 * 60) / (1000 * 60);
        long seconds = milliSeconds % (1000 * 60) / 1000;
        long mills = milliSeconds % 1000 / 10;
        return getStr(hours) + ":" +
                getStr(minutes) + ":" +
                getStr(seconds) + "." +
                getStr(mills);
    }

    /**
     * 获取指定日期的时间戳
     */
    public static long getStamp(String format, String dateStr) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            Date date = df.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            return -1;
        }
    }

    private static String getStr(long l) {
        return l < 10 ? "0" + l : "" + l;
    }
}
