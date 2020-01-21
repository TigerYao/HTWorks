package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import com.huatu.handheld_huatu.business.ztk_zhibo.cache.VideoPlayer;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.BrightnessHelper;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.VolumeUtils;

public class GestureState {
    public static final int GESTURE_TYPE_ONE_TAP = 0;
    public static final int GESTURE_TYPE_DOUBLE_TAP = 1;
    public static final int GESTURE_TYPE_VOLUME = 2;
    public static final int GESTURE_TYPE_BRIGHTNESS = 3;
    public static final int GESTURE_TYPE_PROGRESS = 4;
    public static final int GESTURE_TYPE_ZOOM = 5;

    public int type = -1;
    public int offset;
    public float zoomOffset;

    public int maxVolumeProgress;
    public int maxVolume = VolumeUtils.getMaxUiValue();
    public int maxBrightness = 255;
    public int duration;

    public int initVolume;
    public int initBrightness = -1;
    public int initPosition;

    public int seekedPosition;
    public VideoPlayer.BaseView mView;

    public GestureState(VideoPlayer.BaseView view) {
        this.mView = view;
    }

    public void reset() {
        type = -1;
        offset = 0;

        maxVolume = 0;
        maxBrightness = 0;
        duration = 0;

        initVolume = 0;
        initBrightness = -1;
        initPosition = 0;
    }
}