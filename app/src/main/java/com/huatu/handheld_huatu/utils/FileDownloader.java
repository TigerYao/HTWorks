package com.huatu.handheld_huatu.utils;

import android.text.TextUtils;

import com.huatu.handheld_huatu.listener.OnFileDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by saiyuan on 2016/11/16.
 */

public class FileDownloader {
    public static boolean download(DownloadBaseInfo info, String savePath, OnFileDownloadListener listener) {
        return download(info, savePath, listener, false);
    }

    /*
    isMd5Check:是否校验md5
    默认不覆盖旧文件
     */
    public static boolean download(DownloadBaseInfo info, String savePath, OnFileDownloadListener listener,
                                   boolean isMd5Check) {
        return download(info, savePath, listener, isMd5Check, false);
    }

    /*
    isMd5Check:是否校验md5
    isOverride:是否覆盖旧文件
     */
    public static boolean download(DownloadBaseInfo info, String savePath, OnFileDownloadListener listener,
                                   boolean isMd5Check, boolean isOverride) {
        if (info == null || TextUtils.isEmpty(info.downloadUrl) || TextUtils.isEmpty(savePath)) {
            LogUtils.i("Input parameter is null");
            return false;
        }
        boolean isSuccess = false;
        LogUtils.v("FileDownloader(), url:" + info.downloadUrl);
        if (!isOverride) {
            File oldFile = new File(savePath);
            if (oldFile != null && oldFile.exists()) {
                if (isMd5Check) {
                    String md5 = Md5Util.getFileMD5String(oldFile);
                    if (!TextUtils.isEmpty(md5) && md5.equals(info.md5)) {
                        return true;
                    }
                } else if (oldFile.length() == info.fileSize) {
                    return true;
                }
            }
        }
        FileUtil.createFile(savePath);
        File downloadFile = new File(savePath);
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL httpUrl = new URL(info.downloadUrl);
            conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20000);
            if (conn.getResponseCode() == 200) {
                int total = conn.getContentLength();
                is = conn.getInputStream();
                fos = new FileOutputStream(downloadFile);
                byte[] buffer = new byte[1024];
                int len = 0;
                int process = 0;
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                    process += len;
                    if (listener != null) {
                        listener.onProgress(info, (int) (process * 100.0f / total), process, total);
                    }
                    fos.flush();
                }
                if (isMd5Check) {
                    String md5 = Md5Util.getFileMD5String(downloadFile);
                    if (!TextUtils.isEmpty(md5) && md5.equals(info.md5)) {
                        isSuccess = true;
                    }
                } else {
                    isSuccess = true;
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }
        return isSuccess;
    }

    public static boolean download(String url, String path) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
            return false;
        }
        boolean isSuccess = false;
        FileUtil.createFile(path);
        File downloadFile = new File(path);
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL httpUrl = new URL(url);
            conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20000);
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                fos = new FileOutputStream(downloadFile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                isSuccess = true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
            try {
                if(downloadFile!=null && downloadFile.exists()){
                    downloadFile.delete();
                }
            } catch (Exception e1) {
                LogUtils.e(e);
                e1.printStackTrace();
            } finally {
            }
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }

        return isSuccess;
    }
}
