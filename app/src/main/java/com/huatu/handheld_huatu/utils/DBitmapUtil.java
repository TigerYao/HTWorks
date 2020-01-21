package com.huatu.handheld_huatu.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;


/**
 * Created by michael on 17/9/4.
 */

public class DBitmapUtil {

    public static Drawable getDrawable(Context cxt, int rid) {
        if (cxt != null) {
            Drawable drawable = ContextCompat.getDrawable(cxt, rid);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            return drawable;
        }
        return new BitmapDrawable();
    }

    public static void setText(Context cxt, TextView tv, String text, int start, int end, int color) {
        if (!TextUtils.isEmpty(text) && cxt != null && start < end && end < text.length()) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            ForegroundColorSpan yellowSpan = new ForegroundColorSpan(ContextCompat.getColor(cxt, color));
            builder.setSpan(yellowSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (tv != null) {
                tv.setText(builder);
            }
        }
    }

    public static void setText(Context cxt, TextView tv, String text, int start, int end) {
        setText(cxt, tv, text, start, end, R.color.common_style_text_color);
    }

    public static void setText(Context cxt, TextView tv, String var, String svar) {
        if (svar != null && var.contains(svar)) {
            int start = var.indexOf(svar);
            int end = start + svar.length();
            setText(cxt, tv, var, start, end, R.color.common_style_text_color);
        }
    }


}
