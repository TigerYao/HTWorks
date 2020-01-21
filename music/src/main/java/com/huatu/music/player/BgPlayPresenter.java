package com.huatu.music.player;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.huatu.music.bean.Music;
import com.huatu.music.utils.LogUtil;

import java.lang.ref.WeakReference;

import static android.content.Context.POWER_SERVICE;
import static com.huatu.music.player.MusicPlayerService.ACTION_PLAY_PAUSE;
import static com.huatu.music.player.MusicPlayerService.ACTION_SERVICE;
import static com.huatu.music.player.MusicPlayerService.AUDIO_FOCUS_CHANGE;
import static com.huatu.music.player.MusicPlayerService.RELEASE_WAKELOCK;
import static com.huatu.music.player.MusicPlayerService.TRACK_PLAY_ENDED;
import static com.huatu.music.player.MusicPlayerService.TRACK_PLAY_ERROR;
import static com.huatu.music.player.MusicPlayerService.TRACK_WENT_TO_NEXT;
import static com.huatu.music.player.MusicPlayerService.VOLUME_FADE_DOWN;
import static com.huatu.music.player.MusicPlayerService.VOLUME_FADE_UP;

/**
 * Created by cjx on 2019\11\28 0028.
 */

public class BgPlayPresenter implements BgPlayContract.BasePresenter<BgPlayContract.BaseView>,IPlayServiceCallback {

    protected BgPlayContract.BaseView mView;
    // protected List<Disposable> disposables;
    private   HeadsetReceiver mHeadsetReceiver;
    IntentFilter intentFilter;

    private PowerManager.WakeLock mWakeLock;
  //  private PowerManager powerManager;
    private TelephonyManager mTelephonyManager;
    private ServicePhoneStateListener mPhoneStateLisener;
    private BgAudioAndFocusManager mAudioAndFocusManager;

    private MusicPlayerHandler mHandler;
    //暂时失去焦点，会再次回去音频焦点
    private boolean mPausedByTransientLossOfFocus = false;

    private BgMediaSessionManager mMediaSessionManager;


    private boolean isRunningForeground = false;
    private boolean isMusicPlaying = false;


    @Override
    public void attachView(BgPlayContract.BaseView view) {
        this.mView = view;
        // disposables = new ArrayList<>();

      //  initReceiver();
        initConfig();
        initTelephony();
       // requestAudioFocus();
    }

    public  void onVideoPrepared(){
        isMusicPlaying = true;
        mMediaSessionManager.updatePlaybackState();
    }

    public void onPause(){
       if (isPlaying()){
            isMusicPlaying = false;
            mMediaSessionManager.updatePlaybackState();
       }
    }

    public void onPlay(){
        isMusicPlaying = true;
        mMediaSessionManager.updatePlaybackState();
        mAudioAndFocusManager.requestAudioFocus();
        mMediaSessionManager.canSessionActive(true);
    }

    public void onPlayEnd(){
        onPause();
    }

    public void start(Music music){
        isMusicPlaying = false;
        mMediaSessionManager.updatePlaybackState();
        mAudioAndFocusManager.requestAudioFocus();
        mMediaSessionManager.canSessionActive(true);
        mMediaSessionManager.updateMetaData(music);
    }

    @Override
    public long getCurrentPosition() {
          return mView.getCurrentPosition();
    }

    @Override
    public boolean isPlaying(){
        return isMusicPlaying;
    }

    @Override
    public void playPause(){
        if (isPlaying()) {
            isMusicPlaying = false;
            mMediaSessionManager.updatePlaybackState();
            mView.doPauseAction();
        } else {
            isMusicPlaying = true;
            mMediaSessionManager.updatePlaybackState();
            mAudioAndFocusManager.requestAudioFocus();
            mMediaSessionManager.canSessionActive(true);;
            mView.doResumeAction();
        }
    }

    /**
     * 初始化广播
     */
    private void initReceiver() {
        //实例化过滤器，设置广播
        intentFilter = new IntentFilter(ACTION_SERVICE);

        mHeadsetReceiver = new HeadsetReceiver();
        // mStandardWidget = new StandardWidget();

        intentFilter.addAction(ACTION_PLAY_PAUSE);

        //注册广播
        //registerReceiver(mServiceReceiver, intentFilter);
        mView.getContext().registerReceiver(mHeadsetReceiver, intentFilter);
       // registerReceiver(mHeadsetPlugInReceiver, intentFilter);
        // registerReceiver(mStandardWidget, intentFilter);
    }

    private void initConfig() {

        //电源键
        PowerManager powerManager = (PowerManager) mView.getContext().getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PlayerWakelockTag");

        // mFloatLyricViewManager = new FloatLyricViewManager(this);
        mHandler = new MusicPlayerHandler(this,Looper.getMainLooper());
        //初始化和设置MediaSessionCompat
       // mMediaSessionManager = new MediaSessionManager(mBindStub, this, mMainHandler);
        mAudioAndFocusManager = new BgAudioAndFocusManager(mView.getContext(), mHandler);

        mMediaSessionManager = new BgMediaSessionManager(this, mView.getContext(), mHandler,mAudioAndFocusManager.getMediaSession());
    }

    /**
     * 初始化电话监听服务
     */
    private void initTelephony() {
        TelephonyManager telephonyManager = (TelephonyManager)mView.getContext().getSystemService(Context.TELEPHONY_SERVICE);// 获取电话通讯服务
        mTelephonyManager=telephonyManager;
        mPhoneStateLisener=new ServicePhoneStateListener() ;
        telephonyManager.listen(mPhoneStateLisener,
                PhoneStateListener.LISTEN_CALL_STATE);// 创建一个监听对象，监听电话状态改变事件
    }


    public class MusicPlayerHandler extends Handler {
        private final WeakReference<BgPlayPresenter> mService;
        private float mCurrentVolume = 1.0f;

        public MusicPlayerHandler(final BgPlayPresenter service, final Looper looper) {
            super(looper);
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final BgPlayPresenter service = mService.get();
            synchronized (mService) {
                switch (msg.what) {
                   /* case VOLUME_FADE_DOWN:
                        mCurrentVolume -= 0.05f;
                        if (mCurrentVolume > 0.2f) {
                            sendEmptyMessageDelayed(VOLUME_FADE_DOWN, 10);
                        } else {
                            mCurrentVolume = 0.2f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case VOLUME_FADE_UP:
                        mCurrentVolume += 0.01f;
                        if (mCurrentVolume < 1.0f) {
                            sendEmptyMessageDelayed(VOLUME_FADE_UP, 10);
                        } else {
                            mCurrentVolume = 1.0f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;*/
                    case TRACK_WENT_TO_NEXT: //mplayer播放完毕切换到下一首

                        break;
                    case TRACK_PLAY_ENDED://mPlayer播放完毕且暂时没有下一首

                        break;
                    case TRACK_PLAY_ERROR://mPlayer播放错误
                       /* LogUtil.e(TAG, "TRACK_PLAY_ERROR " + msg.obj + "---");
                        playErrorTimes++;
                        if (playErrorTimes < MAX_ERROR_TIMES) {
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    service.play();
                                }
                            });
                        } else {
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    service.pause();
                                }
                            });
                        }*/
                        break;
                    case RELEASE_WAKELOCK://释放电源锁
                        service.mWakeLock.release();
                        break;


                    case AUDIO_FOCUS_CHANGE:
                        switch (msg.arg1) {
                            case AudioManager.AUDIOFOCUS_LOSS://失去音频焦点
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://暂时失去焦点
                                if (service.isPlaying()) {
                                    mPausedByTransientLossOfFocus =
                                            msg.arg1 == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
                                }
                                service.isMusicPlaying=false;
                                LogUtil.e("AUDIO_FOCUS_CHANGE","false_"+mPausedByTransientLossOfFocus);
                                service.mView.doPauseAction();
                               /* mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        service.pause();
                                    }
                                });*///service::pause
                                break;
                          /*  case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                removeMessages(VOLUME_FADE_UP);
                                sendEmptyMessage(VOLUME_FADE_DOWN);
                                break;*/
                            case AudioManager.AUDIOFOCUS_GAIN://重新获取焦点
                                //重新获得焦点，且符合播放条件，开始播放
                                if (!service.isPlaying()
                                        && mPausedByTransientLossOfFocus) {
                                    mPausedByTransientLossOfFocus = false;

                                    LogUtil.e("AUDIO_FOCUS_CHANGE","true_"+mPausedByTransientLossOfFocus);
                                    mCurrentVolume = 0f;
                                    service.isMusicPlaying=true;
                                    service.mView.doResumeAction();
                                  //  service.mPlayer.setVolume(mCurrentVolume);
                                   /* mMainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            service.play();
                                        }
                                    });*///service::play
                                } else {
                                   // removeMessages(VOLUME_FADE_DOWN);
                                    //sendEmptyMessage(VOLUME_FADE_UP);
                                }
                                break;
                            default:
                        }
                        break;
                }
            }
        }
    }


    @Override
    public void detachView() {
         //取消电话侦听
        if(null!=mTelephonyManager){
            if(null!=mPhoneStateLisener){
                mTelephonyManager.listen(mPhoneStateLisener,  PhoneStateListener.LISTEN_NONE);
                mPhoneStateLisener=null;
            }
            mTelephonyManager=null;
        }
        if(null!=mAudioAndFocusManager){
            mAudioAndFocusManager.abandonAudioFocus();
            mAudioAndFocusManager.release();
        }
        if(null!=mMediaSessionManager){
             mMediaSessionManager.release();
        }

        // 释放Handler资源
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mWakeLock.isHeld())
            mWakeLock.release();

       // mView.getContext().unregisterReceiver(mHeadsetReceiver);
        if (mView != null) {
            mView = null;
        }
      /*  for (Disposable dis : disposables) {
            dis.dispose();
        }
        disposables.clear();*/
    }



    /**
     * 电话监听
     */
    private class ServicePhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            LogUtil.d("BgPlayPresenter", "TelephonyManager state=" + state + ",incomingNumber = " + incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:   //接听状态
                case TelephonyManager.CALL_STATE_RINGING:   //响铃状态
                    if(null!=mView){
                        mView.doPauseAction();//pause();
                    }
                    break;
            }
        }
    }

    //有线耳机
    private class HeadsetReceiver extends BroadcastReceiver {

        final BluetoothAdapter bluetoothAdapter;

        public HeadsetReceiver() {
            intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY); //有线耳机拔出变化
            intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED); //蓝牙耳机连接变化

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        @Override
        //  @SuppressWarnings("Missing")
        public void onReceive(Context context, Intent intent) {
           /* if (isRunningForeground) {
                //当前是正在运行的时候才能通过媒体按键来操作音频
                switch (intent.getAction()) {
                    case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
                        LogUtil.e("蓝牙耳机插拔状态改变");
                        if (bluetoothAdapter != null &&
                                BluetoothProfile.STATE_DISCONNECTED == bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET) &&
                                isPlaying()) {
                            //蓝牙耳机断开连接 同时当前音乐正在播放 则将其暂停
                            mView.doPauseAction();//pause();
                        }
                        break;
                    case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                        LogUtil.e("有线耳机插拔状态改变");
                        if (isPlaying()) {
                            //有线耳机断开连接 同时当前音乐正在播放 则将其暂停
                            mView.doPauseAction();// pause();
                        }
                        break;

                }
            }*/
        }

    }
}
