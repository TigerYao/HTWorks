package com.huatu.handheld_huatu.utils;

import android.text.TextUtils;

import com.huatu.handheld_huatu.listener.OnFileDownloadListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by saiyuan on 2016/11/17.
 */
public class DownloadManager {
    private static DownloadManager instance = null;

    private List<DownloadTask> tasklist = null;
    private ExecutorService downloadExecutor = null;

    private DownloadManager() {
        downloadExecutor = Executors.newSingleThreadExecutor();
        tasklist = Collections.synchronizedList(new ArrayList<DownloadTask>());
    }

    public static DownloadManager getInstance() {
        if (instance == null)
            instance = new DownloadManager();
        return instance;
    }

    /**
     * 是否正在下载
     *
     * @param mInfo
     * @return
     */
    public boolean isDownloading(DownloadBaseInfo mInfo) {
        if (mInfo == null || TextUtils.isEmpty(mInfo.downloadUrl)) {
            LogUtils.i("downloadInfo is null or fileName is null");
            return false;
        }
        for (DownloadTask task : tasklist) {
            if (mInfo.downloadUrl.equals(task.mBaseInfo.downloadUrl)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 新建一个下载任务并添加到下载队列。
     *
     * @param mInfo
     * @param filePath
     * @return
     */
    public boolean addDownloadTask(DownloadBaseInfo mInfo, String filePath,
                                   OnFileDownloadListener listener) {
        return addDownloadTask(mInfo, filePath, listener, false);
    }

    public boolean addDownloadTask(DownloadBaseInfo mInfo, String filePath,
                                   OnFileDownloadListener listener, boolean checkMd5) {
        return addDownloadTask(mInfo, filePath, listener, false, false);
    }

    public boolean addDownloadTask(DownloadBaseInfo mInfo, String filePath,
                                   OnFileDownloadListener listener, boolean checkMd5, boolean isOverWrite) {
        if (mInfo == null || TextUtils.isEmpty(filePath) || TextUtils.isEmpty(mInfo.downloadUrl)) {
            LogUtils.i("Input parameter is null");
            return false;
        }
        if(isDownloading(mInfo)) {
            LogUtils.i("Downloading task exists. " + filePath);
            return false;
        }
        LogUtils.i("addDownloadTask success, url is " + mInfo.downloadUrl);
        DownloadTask newTask = new DownloadTask(mInfo, filePath, listener, checkMd5, isOverWrite);
        tasklist.add(newTask);
        downloadExecutor.execute(newTask);
        if(listener != null) {
            listener.onStart(mInfo);
        }
        LogUtils.i("adding Downloading-task success " + filePath);
        return true;
    }

    /**
     * 删除下载任务
     *
     * @param mInfo
     * @return
     */
    public boolean cancelDownloadTask(DownloadBaseInfo mInfo) {
        if (mInfo == null || TextUtils.isEmpty(mInfo.downloadUrl)) {
            LogUtils.i("Input parameter is null");
            return false;
        }
        for (DownloadTask task : tasklist) {
            if (mInfo.downloadUrl.equals(task.mBaseInfo.downloadUrl)) {
                task.cancel();
                tasklist.remove(task);
                //触发取消下载回调方法
                if(task.mListener != null)
                    task.mListener.onCancel(mInfo);
                task = null;
                LogUtils.i("cancel Downloading-task success. " + mInfo.downloadUrl);
                return true;
            }
        }
        LogUtils.i("Downloading task un-exist " + mInfo.downloadUrl);
        return false;
    }

    /** 删除下载任务 */
    public void removeTask(DownloadTask task) {
        tasklist.remove(task);
    }

    public static class DownloadTask implements Runnable {
        // 下载信息
        public DownloadBaseInfo mBaseInfo;

        /** 下载模式控制 true ==下载 false== 取消下载 */
        public boolean downloadControl = true;
        private String mSaveFilePath = "";
        private OnFileDownloadListener mListener;
        private boolean mCheckMd5;
        private boolean isOverWrite;

        /**
         * 构造函数，创建一个下载任务
         *
         * @param baseInfo
         * @param filePath 文件保存的路径
         * @param listener
         *            下载监听器
         */
        public DownloadTask(DownloadBaseInfo baseInfo, String filePath,
                            OnFileDownloadListener listener, boolean checkMd5, boolean over) {
            mBaseInfo = baseInfo;
            mSaveFilePath = filePath;
            mListener = listener;
            mCheckMd5 = checkMd5;
            isOverWrite = over;
        }

        public void cancel() {
            downloadControl = false;
        }

        @Override
        public void run() {
            LogUtils.i("Start download for: " + mSaveFilePath);
            if(mListener != null) {
                mListener.onStart(mBaseInfo);
            }
            boolean isSuccess = FileDownloader.download(mBaseInfo, mSaveFilePath, mListener, mCheckMd5, isOverWrite);
            if(isSuccess) {
                if(mListener != null) {
                    mListener.onSuccess(mBaseInfo, mSaveFilePath);
                }
            } else if(mListener != null) {
                mListener.onFailed(mBaseInfo);
            }
            DownloadManager.getInstance().removeTask(DownloadTask.this);
        }
    }
}
