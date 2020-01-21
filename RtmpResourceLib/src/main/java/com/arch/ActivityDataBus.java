package com.arch;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Administrator on 2020\1\8 0008.
 *
 * https://blog.csdn.net/qq_22744433/article/details/78195155
 */

public class ActivityDataBus {

    public static <T extends ActivityShareData> T getData(Context context, Class<T> tClass) {
        return getData(checkContext(context),tClass);
    }

    public static <T extends ActivityShareData> T getData(Activity context, Class<T> tClass) {
        return getData(checkContext(context),tClass);
    }

    public static <T extends ActivityShareData> T getData(FragmentActivity context, Class<T> tClass) {
        return ViewModelProviders.with(context).get(tClass);
    }

    private static FragmentActivity checkContext(Context context) {
        if(context instanceof FragmentActivity) return (FragmentActivity) context;
        throw new IllegalContextException();
    }

    public static class ActivityShareData extends ViewModel {}

    public static class IllegalContextException extends RuntimeException {
        public IllegalContextException() {
            super("ActivityDataBus 需要FragmentActivity作为上下文！");
        }
    }

}
