package com.huatu.handheld_huatu.business.arena.setting;

public interface NightSwitchInterface {
    /**
     * 日夜间切换接口，需要切换的页面集成此接口，在ArenaViewSettingManager中注册 && 解除注册，然后设置会日夜间模式会回调此接口
     */
    void nightSwitch();
}
