package com.huatu.handheld_huatu.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.Html;


import com.huatu.handheld_huatu.UniApplicationContext;


/**
 * Resource工具类
  */
public class ResourceUtils {

    public static Context getContext() {
        return UniApplicationContext.getContext();
    }

    public static Resources getResources() {
        if (getContext() != null) {
            return getContext().getResources();
        }
        return null;
    }

    public static int getColor(@ColorRes int colorRes) {
        if (getResources() != null) {
            return getResources().getColor(colorRes);
        }
        return -1;
    }

    public static int getColor(int a, int r, int g, int b) {
        return Color.argb(a, r, g, b);
    }

    public static String getString(@StringRes int stringRes) {
        if (getResources() != null) {
            return getResources().getString(stringRes);
        }
        return null;
    }

    public static String getString(@StringRes int id, Object... formatArgs) {
        if (getResources() != null) {
            return getResources().getString(id, formatArgs);
        }
        return null;
    }

    public static CharSequence getText(@StringRes int stringRes) {
        if (getResources() != null) {
            return getResources().getText(stringRes);
        }
        return null;
    }

    public static String[] getArrayString(@ArrayRes int arrayRes) {
        if (getResources() != null) {
            return getResources().getStringArray(arrayRes);
        }
        return null;
    }

    public static boolean getBoolean(@BoolRes int resId, boolean defValue) {
        if (getResources() != null) {
            return getResources().getBoolean(resId);
        }
        return defValue;
    }

    public static boolean getBoolean(@BoolRes int resId) {
        return getBoolean(resId, false);
    }

    public static int getInt(@IntegerRes int resId) {
        return getInt(resId, 0);
    }

    public static int getInt(@IntegerRes int resId, int defValue) {
        if (getResources() != null) {
            return getResources().getInteger(resId);
        }
        return defValue;
    }

    public static Integer getInteger(@IntegerRes int resId) {
        return getInteger(resId, null);
    }

    public static Integer getInteger(@IntegerRes int resId, Integer defValue) {
        if (getResources() != null) {
            return getInteger(resId);
        }
        return defValue;
    }

    public static ColorStateList getColorStateList(@ColorRes int resId) {
        if (getResources() != null) {
            return getResources().getColorStateList(resId);
        }
        return null;
    }

    public static int getDimension(@DimenRes int resId) {
        if (getResources() != null) {
            return (int) getResources().getDimension(resId);
        }
        return 0;
    }

    public static float getDimensionFloat(@DimenRes int resId) {
        if (getResources() != null) {
            return getResources().getDimension(resId);
        }
        return 0;
    }

    public static int getDimensionPixelOffset(@DimenRes int resId) {
        if (getResources() != null) {
            return getResources().getDimensionPixelOffset(resId);
        }
        return 0;
    }

    public static int getDimensionPixelSize(@DimenRes int resId) {
        if (getResources() != null) {
            return getResources().getDimensionPixelSize(resId);
        }
        return 0;
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        if (getResources() != null) {
            return ContextCompat.getDrawable(getContext(), resId);
        }
        return null;
    }

    public static int getDrawable(String format, Object... args) {
        if (getResources() != null) {
            return getResources().getIdentifier(String.format(format, args), "mipmap",
                    UniApplicationContext.getContext().getPackageName());
        }
        return 0;

    }

    public static CharSequence[] getTextArray(@ArrayRes int resId) {
        if (getResources() != null) {
            return getResources().getTextArray(resId);
        }
        return null;
    }

    public static int[] getIntArray(@ArrayRes int id) {
        if (getResources() != null) {
            return getResources().getIntArray(id);
        }
        return null;
    }


    /**
     * 获取Color的String表现形式
     *
     * @param colorRes
     * @return
     */
    public static String getColorStr(@ColorRes int colorRes) {
        String color = Integer.toHexString(getColor(colorRes));
        return String.format("%s%s", "#", color.substring(2));
    }

    /**
     * 将String 进行 Html格式化
     *
     * @param str 要格式化的String
     * @return 返回格式化后的结果
     */
    public static CharSequence getStringForHtml(String str) {
        return Html.fromHtml(str);
    }

    /**
     * 将String 进行 Html格式化
     *
     * @param strRes  要格式化的String
     * @param objects 参数
     * @return 返回格式化后的结果
     */
    public static CharSequence getStringForHtml(@StringRes int strRes, Object... objects) {
        return Html.fromHtml(getString(strRes, objects));
    }

    public static CharSequence getStringForHtml(@StringRes int strRes, Html.ImageGetter imageGetter, Object... objects) {
        return Html.fromHtml(getString(strRes, objects), imageGetter, null);
    }


    /**
     * 根据 expression决定返回数值
     *
     * @param expression 判断依据
     * @param strRes1    如果expression为true,则返回该参数
     * @param strRes2    如果expression为false,则返回该参数
     * @return 返回结果
     */
    public static CharSequence getStringOr(boolean expression, @StringRes int strRes1, @StringRes int strRes2) {
        if (getResources() != null) {
            return getResources().getString(expression ? strRes1 : strRes2);
        }
        return null;
    }

    /**
     * 根据 expression决定返回数值
     *
     * @param expression 判断依据
     * @param str1       如果expression为true,则返回该参数
     * @param strRes2    如果expression为false,则返回该参数
     * @return 返回结果
     */
    public static String getStringOr(boolean expression, String str1, @StringRes int strRes2) {
        if (getResources() != null) {
            String string = getResources().getString(strRes2);
            return expression ? str1 : string;
        }
        return null;
    }
}
