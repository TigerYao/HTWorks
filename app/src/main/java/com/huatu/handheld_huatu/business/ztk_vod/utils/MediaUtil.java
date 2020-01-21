package com.huatu.handheld_huatu.business.ztk_vod.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;

public class MediaUtil {

    public final static String PCM_FILE_SUFFIX = ".pcm";
    //    public final static String MP4_FILE_SUFFIX = ".mp4";
    public final static String MP4_FILE_SUFFIX = ".pcm";

    /**
     * 截取视频第一帧
     *
     * @param context
     * @param uri
     * @return
     */
    public static Bitmap getVideoFirstFrame(Context context, Uri uri) {
        Bitmap bitmap = null;
        String className = "android.media.MediaMetadataRetriever";
        Object objectMediaMetadataRetriever = null;
        Method release = null;
        try {
            objectMediaMetadataRetriever = Class.forName(className).newInstance();
            Method setDataSourceMethod = Class.forName(className).getMethod("setDataSource", Context.class, Uri.class);
            setDataSourceMethod.invoke(objectMediaMetadataRetriever, context, uri);
            Method getFrameAtTimeMethod = Class.forName(className).getMethod("getFrameAtTime");
            bitmap = (Bitmap) getFrameAtTimeMethod.invoke(objectMediaMetadataRetriever);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (release != null) {
                    release.invoke(objectMediaMetadataRetriever);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static File createFile(String title, String suffix) {
        File file = null;
        // 判断sd卡是否存在
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            File dir = new File(sdDir + ConfigUtil.DOWNLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String path = dir + "/" + title.concat(suffix);
            file = new File(path);
        }

        return file;
    }

    public static void deleteFile(String title, String suffix) {
        File file = null;
        // 判断sd卡是否存在
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStorageDirectory() + ConfigUtil.DOWNLOAD_DIR + "/" + title + suffix);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static boolean hasFile(String title, String suffix) {
        File file = null;
        // 判断sd卡是否存在
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStorageDirectory() + ConfigUtil.DOWNLOAD_DIR + "/" + title + suffix);
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }

    public static File createMD5File(String title, String suffix) {
        File file = null;
        // 判断sd卡是否存在
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            File dir = new File(sdDir + ConfigUtil.DOWNLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName=MD5.digest(title);
            fileName= TextUtils.isEmpty(fileName) ? System.currentTimeMillis()+"":fileName;
            String path = dir + "/" + fileName.concat(suffix);
            file = new File(path);
        }

        return file;
    }
}
