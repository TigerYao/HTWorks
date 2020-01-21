package com.huatu.handheld_huatu.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

/**
 * Created by guangju on 2018/5/28.
 */

public class EditextUtils {
    private static EditextFilter[] filters;
    private static class EditextFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if(source.equals(" "))
                return "";
            else
                return null;
        }

    }

    public static EditextFilter[] getEditextFilters(){
        filters= new EditextFilter[]{new EditextFilter()};
        return filters;
    }
}
