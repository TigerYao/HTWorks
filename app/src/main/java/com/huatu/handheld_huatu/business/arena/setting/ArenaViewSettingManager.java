package com.huatu.handheld_huatu.business.arena.setting;

import java.util.LinkedList;

/**
 * 新行测做题日夜间模式、字体设置管理单例
 * 需要设置的页面集成接口NightSwitchInterface、TextSizeSwitchInterface，然后在此单例中注册 && 解除注册
 * 然后设置的时候，会回调接口，逻辑自己页面进行处理
 */
public class ArenaViewSettingManager implements NightSwitchInterface, TextSizeSwitchInterface {

    private static ArenaViewSettingManager instance;

    public static ArenaViewSettingManager getInstance() {
        if (instance == null) {
            synchronized (ArenaViewSettingManager.class) {
                if (instance == null) {
                    instance = new ArenaViewSettingManager();
                }
            }
        }
        return instance;
    }

    private ArenaViewSettingManager() {
    }

    // 注册的页面
    private LinkedList<NightSwitchInterface> nightSwitchInterfaceList = new LinkedList<>();
    private LinkedList<TextSizeSwitchInterface> textSizeSwitchInterfacesList = new LinkedList<>();

    // 日夜间、字体大小页面 注册和解除注册
    public void registerNightSwitcher(NightSwitchInterface switcher) {
        nightSwitchInterfaceList.add(switcher);
    }

    public void unRegisterNightSwitcher(NightSwitchInterface switcher) {
        if (nightSwitchInterfaceList.size() > 0) {
            nightSwitchInterfaceList.remove(switcher);
        }
    }

    public void registerTextSizeSwitcher(TextSizeSwitchInterface switcher) {
        textSizeSwitchInterfacesList.add(switcher);
    }

    public void unRegisterTextSizeSwitcher(TextSizeSwitchInterface switcher) {
        if (textSizeSwitchInterfacesList.size() > 0) {
            textSizeSwitchInterfacesList.remove(switcher);
        }
    }

    // 日夜间模式，字体大小设置遍历回调，切换
    @Override
    public void nightSwitch() {
        if (nightSwitchInterfaceList != null && nightSwitchInterfaceList.size() > 0) {
            for (NightSwitchInterface nightSwitchInterface : nightSwitchInterfaceList) {
                nightSwitchInterface.nightSwitch();
            }
        }
    }

    @Override
    public void sizeSwitch() {
        if (textSizeSwitchInterfacesList != null && textSizeSwitchInterfacesList.size() > 0) {
            for (TextSizeSwitchInterface textSizeSwitchInterface : textSizeSwitchInterfacesList) {
                textSizeSwitchInterface.sizeSwitch();
            }
        }
    }
}
