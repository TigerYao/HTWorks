/**
 * <pre>
 * Copyright (C) 2015  Soulwolf XiaoDaoW3.0
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </pre>
 */
package com.huatu.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间戳格式化处理工具类
 * <p/>
 * author : Soulwolf Create by 2015/6/16 14:41
 * email  : ToakerQin@gmail.com.
 */
public class TimerUtils {

    public static final String FORMAT_MM_DD = "MM-dd";

    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

    public static final String FORMAT_YYYY$POINT$MM = "yyyy.MM";

    public static final String FORMAT_MM_DD$BLANK$HH$COLON$MM = "MM-dd HH:mm";

    public static final String FORMAT_HH$COLON$MM = "HH:mm";

    public static final String FORMAT_HH$COLON$MM$SS = "HH:mm:ss";

    public static final String FORMAT_MM_DD$BLANK$WEEKLY$HH$COLON$MM = "MM-dd 每周(期) HH:mm";

    public static final String FORMAT_YYYY_MM_DD$BLANK$WEEKLY$HH$COLON$MM = "yyyy-MM-dd 每周(期) 开始 至 HH:mm";

    public static final String FORMAT_YYYY_MM_DD$BLANK$HH$COLON$MM = "yyyy-MM-dd HH:mm";

    /**
     * From php timestamp to java timestamp!
     *
     * @param time php timestamp
     * @return java timestamp
     */
    public static long php2Java(long time) {
        return time * 1000;
    }

    /**
     * From php timestamp to java timestamp!
     *
     * @param time php timestamp
     * @return java timestamp
     */
    public static long php2Java(String time) {
        try {
            long t = Long.parseLong(time);
            return t != 0 ? php2Java(t) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * From java timestamp to php timestamp!
     *
     * @param time java timestamp
     * @return php  timestamp
     */
    public static long java2Php(long time) {
        return time / 1000;
    }

    /**
     * 时间戳格式化为指定 格式
     */
    public static String timestampFormat(long timestamp, String format) {
        if (timestamp == 0) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return dateFormat.format(new Date(timestamp));
    }

    public static String timestampFormat(String timestamp, String format) {
        if (TextUtils.isEmpty(timestamp)) return "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
            return dateFormat.format(new Date(Long.parseLong(timestamp)));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 时间戳格式化为指定 格式
     */
    public static String timestampFormat(long timestamp, String format, Object... args) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        return String.format(dateFormat.format(new Date(timestamp)).replace("期", "%s"), args);
    }

    /**
     * 周期性活动：开始日期到结束日期，每周几的时间段，例如2015-09-02 至 2015-09-27 每周日 01:30 至 04:00
     * yyyy-MM-dd 每周(期) HH:mm
     */
    public static String activityTimeFormat(long begin, long end, Object args) {
        String beginTime = new SimpleDateFormat(FORMAT_HH$COLON$MM, Locale.CANADA).format(new Date(begin));
        String endTime = new SimpleDateFormat(FORMAT_YYYY_MM_DD$BLANK$WEEKLY$HH$COLON$MM,
                Locale.CANADA).format(new Date(end));
        return String.format(endTime.replace("期", "%s").replace("开始", "%s"), args, beginTime);
    }

    /**
     * 检测两个时间是否为同一天
     */
    public static boolean equalsDay(long time1, long time2) {
        String t1 = timestampFormat(time1, FORMAT_YYYY_MM_DD);
        String t2 = timestampFormat(time2, FORMAT_YYYY_MM_DD);
        return TextUtils.equals(t1, t2);
    }

}
