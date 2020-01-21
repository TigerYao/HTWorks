package com.huatu.handheld_huatu.business.ztk_zhibo.listener;

/**
 * Created by DongDong on 2016/4/1.
 */
public interface OnDLHandoutListener {


    void onDLError(String s, int errorCode);

    void onDLFinished(String downID);


}
