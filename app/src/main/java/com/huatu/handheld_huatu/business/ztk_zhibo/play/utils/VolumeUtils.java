package com.huatu.handheld_huatu.business.ztk_zhibo.play.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

public class VolumeUtils {

    public static final int MAX_UI_VALUE_NORMAL = 100;
    public static final int MAX_UI_VALUE_LOUD = 150;

    public static int getMediaVolume(boolean isLive, Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(isLive ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC);
    }

    public static void setMediaVolume(Context context, int volume) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        } catch (Exception e) {
        }
    }

    public static int getMediaMaxVolume(boolean isLive, Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamMaxVolume(isLive ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC);
    }

    public static int getMaxUiValue() {
        return Build.VERSION.SDK_INT > 9 ? MAX_UI_VALUE_LOUD : MAX_UI_VALUE_NORMAL;
    }

    public static void setMediaVolume(Context context, boolean isLiveVideo, int minVolume){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(isLiveVideo ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC, minVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
}
