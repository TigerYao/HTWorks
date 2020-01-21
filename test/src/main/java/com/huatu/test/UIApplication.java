package com.huatu.test;

import android.app.Application;
import android.util.Log;

/**
 * Created by Administrator on 2019\7\4 0004.
 */

public class UIApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("UniApplicationLike", "UniApplicationLike onCreate start");
        UniApplicationContext.setContext(getApplicationContext());
    }
}
