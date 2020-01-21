package com.huatu.test;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2019\7\4 0004.
 */

public class UniApplicationContext {

    private static Application application;
    private static Context mContext;



    public static void setApplication(Application app) {
        application = app;
    }

    public static void setContext(Context con) {
        mContext = con;
    }

    public static Application getApplication() {
        return application;
    }

    public static Context getContext() {
        return mContext;
    }
}
