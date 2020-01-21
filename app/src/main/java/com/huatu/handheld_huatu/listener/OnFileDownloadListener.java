package com.huatu.handheld_huatu.listener;

import com.huatu.handheld_huatu.utils.DownloadBaseInfo;

/**
 * Created by saiyuan on 2016/11/17.
 */

public interface OnFileDownloadListener {
    void onStart(DownloadBaseInfo baseInfo);
    /*
    percent:已下载文件进度，百分比整数
    byteCount:已下载文件字节数，用于以字节方式显示下载进度
     */
    void onProgress(DownloadBaseInfo baseInfo, final int percent, final int byteCount,final int totalCount);
    void onCancel(DownloadBaseInfo baseInfo);
    void onSuccess(DownloadBaseInfo baseInfo, final String mFileSavePath);
    void onFailed(DownloadBaseInfo baseInfo);
}
