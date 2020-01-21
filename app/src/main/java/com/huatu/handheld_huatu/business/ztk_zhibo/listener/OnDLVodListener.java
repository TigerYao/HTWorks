package com.huatu.handheld_huatu.business.ztk_zhibo.listener;

/**
 * Created by DongDong on 2016/4/1.
 */
public interface OnDLVodListener {
    void onDLProgress(String s, int percent,long speed);

    void onDLError(String s, int errorCode);

    void onDLFinished(String downID);

    void onDLPrepare(String downID);

    void onDLStop(String key,boolean keepWaiting);

    void onDLFileStorage(String key, long space);
}
