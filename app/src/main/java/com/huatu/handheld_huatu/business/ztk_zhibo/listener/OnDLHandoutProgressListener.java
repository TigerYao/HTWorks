package com.huatu.handheld_huatu.business.ztk_zhibo.listener;

/**
 * Created by cjx on
 */
public interface OnDLHandoutProgressListener {

    //downId   -1为正在下载的缓存讲义的   ，-10000为当前的讲义下载完成
    void onDLHandoutProgress(String downID,float speed,String subjectName,String fileSize);

}
