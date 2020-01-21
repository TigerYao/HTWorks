package com.huatu.handheld_huatu.business.arena.setting;

public interface TextSizeSwitchInterface {
    /**
     * 字体大小切换接口，需要切换的页面集成此接口，在ArenaViewSettingManager中注册 && 解除注册，然后设置字体大小会回调此接口
     */
    void sizeSwitch();
}
