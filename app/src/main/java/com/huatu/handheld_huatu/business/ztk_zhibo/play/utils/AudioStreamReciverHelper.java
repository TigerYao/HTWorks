package com.huatu.handheld_huatu.business.ztk_zhibo.play.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.baijia.baijiashilian.liveplayer.tools.RtcPhoneListener;
import com.huatu.handheld_huatu.UniApplication;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.utils.LogUtils;

/**
 * 监听Android的系统广播AudioManager.ACTION_AUDIO_BECOMING_NOISY， 这个广播只是针对有线耳机，或者无线耳机的手机断开连接的事件，监听不到有线耳机和蓝牙耳机的接入
 */
public class AudioStreamReciverHelper {

    private AudioStreamReceiver mAudioReceiver;
    private Context mCtx;
    private boolean mIsRegister = false;
    private AudioStreamChangeListener mListener;
    private TelephonyManager mTelephonyManager;
    private RtcPhoneListener mPhoneListener;
    /**
     * 用AudioManager获取音频焦点避免音视频声音并发问题
     */
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;
    private AudioFocusRequest mAudioRequest;

    public interface AudioStreamChangeListener {
        void handleHeadsetDisconnected();

        void onPhoneStateChanged();
    }

    public AudioStreamReciverHelper(Context ctx) {
        this.mCtx = ctx;
        mTelephonyManager = (TelephonyManager) mCtx.getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneListener = new RtcPhoneListener(UniApplicationContext.getContext(), mMyPhoneListener);
        mTelephonyManager.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }



    //zxzhong 请求音频焦点 设置监听
    public int requestTheAudioFocus(final AudioListener audioListener) {
        if (Build.VERSION.SDK_INT < 8) {//Android 2.2开始(API8)才有音频焦点机制
            return 0;
        }
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) UniApplicationContext.getContext().getSystemService(Context.AUDIO_SERVICE);
        }
        if (mAudioFocusChangeListener == null) {
            mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {//监听器
                @Override
                public void onAudioFocusChange(int focusChange) {
                    LogUtils.d("audioFocusChange...." + focusChange);
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                            //播放操作
                            audioListener.start();
                            break;
                        case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                        case AudioManager.AUDIOFOCUS_LOSS:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            //暂停操作
                            audioListener.pause();
                            break;
                        default:
                            break;
                    }
                }
            };
        }
        int requestFocusResult = requestAudioFocus();

        return requestFocusResult;
    }

    public int requestAudioFocus() {
        int requestFocusResult;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAudioRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                            .build()).setWillPauseWhenDucked(true)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(mAudioFocusChangeListener)
                    .build();
            requestFocusResult =  mAudioManager.requestAudioFocus(mAudioRequest);

        }else {
            requestFocusResult = mAudioManager.requestAudioFocus(mAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC | AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
        return requestFocusResult;
    }

    public interface AudioListener {
        void start();

        void pause();
    }

    public void setListener(AudioStreamChangeListener listener) {
        this.mListener = listener;
    }

    public void register() {
        if (mCtx != null) {
            mAudioReceiver = new AudioStreamReceiver();
            IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);//监听耳机（有线/无线）拔出
            mCtx.registerReceiver(mAudioReceiver, intentFilter);
            mIsRegister = true;
        }
    }

    public void unRegister() {
        unfocuAudio();
        if (mIsRegister && mAudioReceiver != null && mCtx != null) {
            mCtx.unregisterReceiver(mAudioReceiver);
            mTelephonyManager.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
            mIsRegister = false;
            mCtx = null;
            mAudioReceiver = null;
            mTelephonyManager = null;
            mPhoneListener = null;
            mListener = null;
        }
    }

    public void unfocuAudio(){
        if(mAudioManager != null){
            if(mAudioRequest != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mAudioManager.abandonAudioFocusRequest(mAudioRequest);
            }
            mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
        }
    }

    class AudioStreamReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                if (mListener != null)
                    mListener.handleHeadsetDisconnected();
            }
        }
    }


    private  RtcPhoneListener.PhoneStateListener mMyPhoneListener = new  RtcPhoneListener.PhoneStateListener() {
        @Override
        public void onPhoneStateChanged(RtcPhoneListener.PHONESTATE var1) {
            if (mListener != null)
                mListener.onPhoneStateChanged();
        }
//
    };
}
