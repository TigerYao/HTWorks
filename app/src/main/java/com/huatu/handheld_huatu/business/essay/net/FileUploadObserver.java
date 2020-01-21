package com.huatu.handheld_huatu.business.essay.net;

/**
 * 上传文件的RxJava2回调
 */

public abstract class FileUploadObserver {

    //监听进度的改变
    public void onProgressChange(long bytesWritten, long contentLength) {
        onProgress((int) (bytesWritten * 100 / contentLength));
    }

    //上传进度回调
    public abstract void onProgress(int progress);

}
