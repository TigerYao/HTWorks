package com.huatu.handheld_huatu.business.arena.utils;

import java.util.ArrayList;

public class LUtils {

    /**
     * 四舍五入保留小数点后一位
     * 如果是0，就不保留小数
     * 9.85 -> 9.9
     * 9.95 -> 10
     */
    public static String formatPoint(Double d) {
        return formatPoint(d.floatValue());
    }

    public static String formatPoint(Float d) {
        String s = String.valueOf(d + 0.05);
        if (s.contains(".")) {
            return s.substring(0, s.indexOf(".") + 2).replace(".0", "");
        }
        return s;
    }

    /**
     * 四舍五入不保留小数
     * 9.5 -> 10
     * 9.4 -> 9
     */
    public static String removePoint(Double d) {
        return removePoint(d.floatValue());
    }

    public static String removePoint(Float d) {
        return String.valueOf((int) (d + 0.5));
    }

    public static String doubleToPercent(double d) {
        d *= 100;
        return formatPoint(d);
    }

    // list 转为数组
    public static Double[] dobuleListToArray(ArrayList<Double> list) {
        if (list != null && list.size() > 0) {
            Double[] arr = new Double[list.size()];
            for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i);
            }
            return arr;
        }
        return null;
    }

    public static String[] stringListToArray(ArrayList<String> list) {
        if (list != null && list.size() > 0) {
            String[] arr = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i);
            }
            return arr;
        }
        return null;
    }

}
