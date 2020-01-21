package com.huatu.handheld_huatu.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @author ZDD
 */
public class PathConfigure {
    public static PathConfigure conf = null;
    private static String ROOT_PATH = "";

    private PathConfigure(Context context) {
        if (PrefStore.getStorageState() == 1 &&
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + "Android" + File.separator + "data" + File.separator + context.getPackageName();
        } else {
            if (null != context.getExternalFilesDir("")) {
                ROOT_PATH = context.getExternalFilesDir("").getAbsolutePath();
            } else {
                ROOT_PATH = context.getFilesDir().getAbsolutePath();
            }
        }
    }

    public String getRootPath() {
        return ROOT_PATH;
    }

    public static PathConfigure getInstance(Context context) {
        if (conf == null) {
            conf = new PathConfigure(context);
        }
        return conf;
    }

    public String getImgCachePath() {
        return ROOT_PATH + File.separator + "imageCache";
    }

    public String getDownloadPath() {
        return ROOT_PATH + File.separator + "download";
    }

    public String getCrashPath() {
        return ROOT_PATH + File.separator + "crash_log";
    }
}
