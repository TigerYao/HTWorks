/**
 * <pre>
 * Copyright (C) 2015  Soulwolf xiaodaow3.0-branch
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
package com.huatu.handheld_huatu.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.huatu.handheld_huatu.UniApplicationContext;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 磁盘存储工具类
 * https://blog.csdn.net/zhangbuzhangbu/article/details/23257873
 * <p/>

 */
public class StorageUtils {

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String INDIVIDUAL_DIR_NAME = "uil-images";
    private static final String TEMP_DIRECTORY = "temp";

    private static final String TEMP_U3D_DIRECTORY = "huatutempv2";
    private static final String DIRECTORY = "huatu";
    private static final String DIRECTORY_CAMERA = "华图在线";
    static final String CAMERA_FILE = "temp-camera-file";

    public static int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);

                    }
                    if (child.lastModified() < numDays) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }


    /**
     * 删除文件
     */
    public static void removeFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                for (File f : children) {
                    removeFile(f);
                }
            } else {
                boolean delete = file.delete();
                LogUtils.e("StorageUtils", "removeFile:" + delete);
                // XLog.d("StorageUtils","removeFile:%s",delete);
            }
        }
    }

    /**
     * 检查SD卡是否存在
     */
    public static boolean checkSdCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取文件大小
     */
    public static long getDirSize(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children) {
                    size += getDirSize(f);
                }
                return size;
            } else {
                return file.length();
            }
        } else {
            return 0;
        }
    }

    /**
     * 获取一个拍照的保存路径
     */
    public static File getCameraFile(Context context, boolean isDelete) {
        return getTempFile(context, CAMERA_FILE, "jpg");
    }

    /**
     * 获取一个临时文件 /data/data/com.xiaodao360.xiaodaow/cache/xxx.temp
     */
    public static File getTempFile(Context context, Object fileName) {
        return getTempFile(context, fileName, "temp");
    }

    /**
     * 获取一个临时文件 /data/data/com.xiaodao360.xiaodaow/cache/xxx.xx
     */
    public static File getTempFile(Context context, Object fileName, Object suffix) {
        File directory = getTempDirectory(context);
        return new File(directory, String.format("%s-%s.%s", "huatu", fileName, suffix));
    }


    //filename   8_1.unity3d
    public static File getHuatuTempFile(Context context, Object fileName) {
        File directory = getHuatuTempDirectory(context);
        return new File(directory, String.format("%s",  fileName));
    }

    public static File getHuatuTempDirectory(Context context) {
        return getCacheDirectory(context, TEMP_U3D_DIRECTORY);
    }

    /**
     * 获取一个临时文件 /data/data/com.xiaodao360.xiaodaow/cache/xxx.temp
     */
    public static File getTempFile(Context context) {
        return getTempFile(context, System.currentTimeMillis());
    }

    public static File getTempDirectory(Context context) {
        return getCacheDirectory(context, TEMP_DIRECTORY);
    }


    public static File getCacheDirectory(Context context, String dir) {
        File file = new File(getCacheDirectory(context), dir);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            LogUtils.i("StorageUtils", "removeFile:%s -- > %s", mkdirs, file);
        }
        return file;
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
     * Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}.<br />
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> (if card is mounted and app has appropriate permission) or
     * on device's file system depending incoming parameters.
     *
     * @param context        Application context
     * @param preferExternal Whether prefer external location for cache
     * @return Cache {@link File directory}.<br />
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            //L.w("Can't define system cache directory! '%s' will be used.", cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
     * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted and app has
     * appropriate permission. Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}
     */
    public static File getIndividualCacheDirectory(Context context) {
        return getIndividualCacheDirectory(context, INDIVIDUAL_DIR_NAME);
    }

    /**
     * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
     * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted and app has
     * appropriate permission. Else - Android defines cache directory on device's file system.
     *
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getIndividualCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = appCacheDir;
            }
        }
        return individualCacheDir;
    }

    /**
     * Returns specified application cache directory. Cache directory will be created on SD card by defined path if card
     * is mounted and app has appropriate permission. Else - Android defines cache directory on device's file system.
     *
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    /**
     * Returns specified application cache directory. Cache directory will be created on SD card by defined path if card
     * is mounted and app has appropriate permission. Else - Android defines cache directory on device's file system.
     *
     * @param context  Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir, boolean preferExternal) {
        File appCacheDir = null;
        if (preferExternal && MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                //L.w("Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                //L.i("Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取图片存储路径
     */
/*
    public static File getGalleryPath() {
   */
/*     String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        if (!MEDIA_MOUNTED.equals(externalStorageState)) {
            return null;
        }*//*


        File rootDir=getCacheDirectory(UniApplicationContext.getContext());
        File dir = new File(rootDir+ File.separator + DIRECTORY + File.separator);
        if (!dir.exists())
            dir.mkdirs();
        //图片目录
        File camera = new File(dir.getAbsolutePath() + File.separator + DIRECTORY_CAMERA + File.separator);
        if (!camera.exists())
            camera.mkdirs();
        return camera;
    }
*/
    /**
     * 获取图片存储路径
     */
    public static File getGalleryPath() {
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        if (!MEDIA_MOUNTED.equals(externalStorageState)) {
            return null;
        }
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + DIRECTORY + File.separator);
        if (!dir.exists())
            dir.mkdirs();
        //图片目录
        File camera = new File(dir.getAbsolutePath() + File.separator + DIRECTORY_CAMERA + File.separator);
        if (!camera.exists())
            camera.mkdirs();
        return camera;
    }

}
