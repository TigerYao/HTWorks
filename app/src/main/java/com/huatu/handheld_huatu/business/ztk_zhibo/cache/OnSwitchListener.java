package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

/**
 * Created by Administrator on 2018\12\12 0012.
 */

public interface OnSwitchListener {

    int isEditMode();//0 normal ,1 编辑模式  ,2 不可编辑

    void switchMode();
}
