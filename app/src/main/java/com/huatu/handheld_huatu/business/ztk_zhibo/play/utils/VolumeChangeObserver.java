package com.huatu.handheld_huatu.business.ztk_zhibo.play.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.huatu.handheld_huatu.UniApplicationContext;

import java.lang.ref.WeakReference;

public class VolumeChangeObserver {
    private static final String VOLUME_CHANGE_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    public final static int LIVE_PHONTHE_TYPE = 1;
    public final static int MEDIA_PLAY_TYPE = 2;

    public interface VolumeChangeListener {
        void onMediaVolumeChanged(int volume);

        void onPhoneVolumeChanged(int volume);
    }

    private VolumeChangeListener mVolumeChangeListenr;
    private VolumeBroadcaseReceiver mVolumeBroadcaseReceiver;
    private Context mContext;
    private AudioManager mAudioManager;
    private boolean mRegistered = false;
    private int type = MEDIA_PLAY_TYPE;

    public VolumeChangeObserver(Context context) {
        this.mContext = context;
        mAudioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    public void setType(int type) {
        this.type = type;
        if(type  == LIVE_PHONTHE_TYPE && mAudioManager != null)//直播时调节听筒音量
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        else if(mAudioManager != null)
            mAudioManager.setMode(AudioManager.MODE_NORMAL);

    }

    public int getCurrentMusicVolume() {
        return mAudioManager != null ? mAudioManager.getStreamVolume(type == LIVE_PHONTHE_TYPE ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC) : -1;
    }

    public int getMaxMusicVolume() {
        return mAudioManager != null ? mAudioManager.getStreamMaxVolume(type == LIVE_PHONTHE_TYPE ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC) : type == LIVE_PHONTHE_TYPE  ? 5 : 15;
    }

    public void setMusicVolume(int volume) {
        mAudioManager.setStreamVolume(type == LIVE_PHONTHE_TYPE ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    public VolumeChangeListener getVolumeChangeListener() {
        return mVolumeChangeListenr;
    }

    public void ajustVoluem(boolean raise){
        try {
            mAudioManager.adjustStreamVolume(
                    type == LIVE_PHONTHE_TYPE ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC,
                    raise ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
        }catch (Exception e){}
    }

    public void setVolumeChangeListener(VolumeChangeListener volumeChangeListener) {
        this.mVolumeChangeListenr = volumeChangeListener;
    }

    public void registerReceiver() {
        mVolumeBroadcaseReceiver = new VolumeBroadcaseReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(VOLUME_CHANGE_ACTION);
        mContext.registerReceiver(mVolumeBroadcaseReceiver, filter);
        mRegistered = true;
    }

    public void unregisterReceiver() {
        if (mRegistered) {
            try {
                mContext.unregisterReceiver(mVolumeBroadcaseReceiver);
                mVolumeBroadcaseReceiver = null;
                mRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class VolumeBroadcaseReceiver extends BroadcastReceiver {
        private WeakReference<VolumeChangeObserver> mObserverWeakReference;

        public VolumeBroadcaseReceiver(VolumeChangeObserver volumeChangeObserver) {
            this.mObserverWeakReference = new WeakReference<>(volumeChangeObserver);

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!VOLUME_CHANGE_ACTION.equals(intent.getAction()) || mObserverWeakReference == null)
                return;
            VolumeChangeObserver observer = mObserverWeakReference.get();
            if (observer == null || observer.getVolumeChangeListener() == null)
                return;
            int streamType = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1);
            if ((streamType == AudioManager.STREAM_VOICE_CALL || streamType >= 9) && observer.type == LIVE_PHONTHE_TYPE) {
                int volume = observer.getCurrentMusicVolume();
                if (streamType >= 9)
                    volume = observer.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                observer.getVolumeChangeListener().onPhoneVolumeChanged(volume);
            } else if (streamType == AudioManager.STREAM_MUSIC && observer.type == MEDIA_PLAY_TYPE) {
                int volume = observer.getCurrentMusicVolume();
                observer.getVolumeChangeListener().onMediaVolumeChanged(volume);
            }
        }
    }


}