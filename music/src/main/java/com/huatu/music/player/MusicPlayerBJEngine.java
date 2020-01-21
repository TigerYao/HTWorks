package com.huatu.music.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.CDNInfo;
import com.baijiahulian.player.bean.PlayItem;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.mediaplayer.IJKMediaPlayer;
import com.baijiahulian.player.mediaplayer.IMediaPlayer;
import com.baijiahulian.player.playerview.CenterViewStatus;
import com.baijiahulian.player.playerview.PlayerInfoLoader;
import com.baijiahulian.player.utils.BJLog;
import com.baijiahulian.player.utils.Utils;
import com.baijiayun.log.BJFileLog;
import com.google.gson.Gson;
import com.huatu.music.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.huatu.music.player.MusicPlayerService.CATON_PRERPARED;
import static com.huatu.music.player.MusicPlayerService.CATON_PRERPAREING;
import static com.huatu.music.player.MusicPlayerService.PLAYER_PREPARED;
import static com.huatu.music.player.MusicPlayerService.PREPARE_ASYNC_UPDATE;
import static com.huatu.music.player.MusicPlayerService.RELEASE_WAKELOCK;
import static com.huatu.music.player.MusicPlayerService.TRACK_PLAY_ENDED;
import static com.huatu.music.player.MusicPlayerService.TRACK_PLAY_ERROR;
import static com.huatu.music.player.MusicPlayerService.TRACK_WENT_TO_NEXT;

/**
 * Created by cjx on 2019\8\21 0021.
 */

public class MusicPlayerBJEngine implements IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnPreparedListener,IMediaPlayer.OnUpdatePlayPositionListener,IMediaPlayer.OnSeekCompleteListener {

    private String TAG = "MusicPlayerEngine";

    private final WeakReference<MusicPlayerService> mService;

   // private PowerManager.WakeLock mWakeLock = null;

    private IJKMediaPlayer mCurrentMediaPlayer = new IJKMediaPlayer();

    private Handler mHandler;

    private boolean  mIsSeekComplement = false;//当前的音乐是否播放完

    private int       mSeekPosition ;
    private boolean   mIsSeeking = false;

    private Subscription mObservableOfCaton;
    //通过此方法判断是否是卡顿
    private void unSubscribeCatonTimer() {
        if(this.mObservableOfCaton != null && !this.mObservableOfCaton.isUnsubscribed()) {
            this.mObservableOfCaton.unsubscribe();
        }
        this.mObservableOfCaton = null;
    }

    //是否已经初始化
    private boolean mIsInitialized = false;
    //是否已经初始化
    private boolean mIsPrepared = false;

    MusicPlayerBJEngine(final MusicPlayerService service) {
        mService = new WeakReference<>(service);
        // setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);  底层已实现
    }

    IMediaPlayer.OnUpdatePlayPositionListener mPlayPostionListener;
    public void setPlayPostionListener(IMediaPlayer.OnUpdatePlayPositionListener playPositionListener){
        mPlayPostionListener=playPositionListener;
    }

    private float mPlayRate=1f;
    public void setPlayRate(float playRate) {

        // BJFileLog.i(BJPlayerView.class, "BJPlayerView", "setVideoRate " + playRate + ", c=" + this.mPlayerPresenter.getVideoRate());
        if((double)playRate >= 0.5D && (double)playRate <= 2.0D &&(playRate!= mPlayRate)) {
            mPlayRate=playRate;
            LogUtil.e("setPlayRate",mPlayRate+"");

            this.mCurrentMediaPlayer.setSpeedUp(playRate);
        }
    }

    public float getPlayRate(){  return mPlayRate;  }

    public void setWakeMode(Context context, int mode) {
       /* boolean washeld = false;

        *//* Disable persistant wakelocks in media player based on property *//*
       *//*  if (SystemProperties.getBoolean("audio.offload.ignore_setawake", false) == true) {
            Log.w(TAG, "IGNORING setWakeMode " + mode);
            return;
        } *//*

        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                washeld = true;
                mWakeLock.release();
            }
            mWakeLock = null;
        }

        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(mode|PowerManager.ON_AFTER_RELEASE, MusicPlayerBJEngine.class.getName());
        mWakeLock.setReferenceCounted(false);
        if (washeld) {
            mWakeLock.acquire();
        }*/
    }

    private VideoItem mCurrentVideoItem;//
    private String mToken;
    private long   mVideoId;
    public void setVideoInfo(long videoId,String token,final int startPos){
        mVideoId=videoId;
        mToken=token;
        if(null!=mService&&(mService.get()!=null)){
            SimplePlayerInfoLoader.getInstance(mService.get())
                    .setVideoId(videoId,token).getVideoInfo(new PlayerInfoLoader.a<VideoItem>() {
                @Override
                public void onFailure(HttpException e) {
                    if(null!=mService&&(mService.get()!=null))
                        Toast.makeText(mService.get(),"音频获取失败",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(VideoItem videoItem) {
                    PlayItem playInfo=getVideoItem(videoItem);
                    mCurrentVideoItem=videoItem;
                    if(playInfo!=null){
                        setDataSource(playInfo.cdnList[0].url,startPos);
                        LogUtil.d("PlayItem", playInfo.definition+"  <  " +playInfo.cdnList[0]);
                    }
                }
            });
        }
    }

    public PlayItem getVideoItem(VideoItem videoItem) {
        if(null==videoItem) return null;
        PlayItem var1 = null;
        int defaultDef   = Utils.getVideoDefinitionFromString(videoItem.vodDefaultDefinition);
        LogUtil.d("PlayItem",defaultDef+"");
        if(videoItem != null && videoItem.playInfo != null) {
            if(defaultDef== 5) {
                var1 = videoItem.playInfo.audio;
            } else if(defaultDef == 0) {
                var1 = videoItem.playInfo.low;
            } else if(defaultDef == 1) {
                var1 = videoItem.playInfo.high;
            } else if(defaultDef == 2) {
                var1 = videoItem.playInfo.superHD;
            } else if(defaultDef == 3) {
                var1 = videoItem.playInfo._720p;
            } else if(defaultDef == 4) {
                var1 = videoItem.playInfo._1080p;
            }

            if(var1 == null) {
                var1 = videoItem.playInfo.low;
                if(var1 == null) {
                    var1 = videoItem.playInfo.high;
                    if(var1 == null) {
                        var1 = videoItem.playInfo.superHD;
                        if(var1 == null) {
                            var1 = videoItem.playInfo._720p;
                            if(var1 == null) {
                                var1 = videoItem.playInfo._1080p;
                            }
                        }
                    }
                }
            }

            return var1;
        } else {
            return null;
        }
    }

    public void setDataSource(final String path,int startPos) {
        resetCdnIndex();
        this.mIsSeeking = false;

        mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path,startPos);
    }

    public void setDataSource(final String path) {
        resetCdnIndex();
        this.mIsSeeking = false;
        mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path,0);
    }


    private boolean setDataSourceImpl(final IJKMediaPlayer player, final String path,int startPos) {
        if (path == null) return false;
        try {
            if (player.isPlaying()) player.stop();
            mIsPrepared = false;
            player.reset();
          /*  if (path.startsWith("content://")) {
                player.setDataSource(mService.get(), Uri.parse(path));
            } else*/ {
                player.setDataSource(path,startPos);
            }
            player.prepareAsync();
            player.setOnPreparedListener(this);
            player.setOnUpdatePlayPositionListener(this);
            player.setOnBufferingUpdateListener(this);
            player.setOnErrorListener(this);
            player.setOnCompletionListener(this);
            player.setOnSeekCompleteListener(this);
        } catch (Exception todo) {
            return false;
        }
        return true;
    }

    public void setHandler(final Handler handler) {
        mHandler = handler;
    }


    public boolean isInitialized() {
        return mIsInitialized;
    }

    public boolean isPrepared() {
        return mIsPrepared;
    }

    public void start() {
        mCurrentMediaPlayer.start();
    }


    public void stop() {
        try {
            unSubscribeCatonTimer();
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
            mIsPrepared = false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    public void release() {
        unSubscribeCatonTimer();
        mPlayPostionListener=null;
        mCurrentMediaPlayer.release();
    }


    public void pause() {
        unSubscribeCatonTimer();
        mCurrentMediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mCurrentMediaPlayer.isPlaying();
    }


    /**
     * getDuration 只能在prepared之后才能调用，不然会报-38错误
     *
     * @return
     */
    public long duration() {
        if (mIsPrepared) {
            return mCurrentMediaPlayer.getDuration();
        } else return 0;
    }


    public long position() {
        try {
            return mCurrentMediaPlayer.getCurrentPosition();
        } catch (IllegalStateException e) {
            return -1;
        }
    }

  /*  public void seek(final long whereto) {
        mCurrentMediaPlayer.seekTo((int) whereto);
    }*/

    private long mLastSeekTimestamp;
    public void seek(final long whereto) {
        if(System.currentTimeMillis() - this.mLastSeekTimestamp > 500L) {
            this.mLastSeekTimestamp = System.currentTimeMillis();
            if(this.mCurrentMediaPlayer != null) {
                if(isPlaying()) {
                   // BJLog.w("BJPlayerView", "BJPlayerView seekVideo " + var1);
                   // BJFileLog.i(BJPlayerView.class, "BJPlayerView", "seekVideo " + var1);
                   // this.mPlayerPresenter.onSeekTo(var1, this.mVideoView.getCurrentPosition());
                    //this.mVideoView.seekTo(var1);
                    mCurrentMediaPlayer.seekTo((int) whereto);
                   // this.showLoading();
                    mHandler.sendEmptyMessage(CATON_PRERPAREING);
                    this.mSeekPosition = (int) whereto;
                    this.mIsSeeking = true;
                }
            }
        }
    }

    public void setVolume(final float vol) {
        LogUtil.e("Volume", "vol = " + vol);
        try {
            mCurrentMediaPlayer.setVolume(vol, vol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAudioSessionId() {
        return mCurrentMediaPlayer.getAudioSessionId();
    }

    @Override
    public boolean onError(final IMediaPlayer mp, final int what, final int extra) {
        LogUtil.e(TAG, "Music Server Error what: " + what + " extra: " + extra);
        this.unSubscribeCatonTimer();
        if(handleRetry(what)){
            return true;
        }
        switch (what) {
            case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
            case IMediaPlayer.MEDIA_ERROR_UNKNOWN:
                final MusicPlayerService service = mService.get();
                final TrackErrorInfo errorInfo = new TrackErrorInfo(service.getAudioId(),
                        service.getTitle());
                mIsInitialized = false;
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = new IJKMediaPlayer();
                setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);
                Message msg = mHandler.obtainMessage(TRACK_PLAY_ERROR, errorInfo);
                mHandler.sendMessageDelayed(msg, 2000);
                return true;
            default:
                break;
        }
        return true;
    }
    private int mCdnIndex;
    public String switchCDNInfinity() {
        if(this.mCurrentVideoItem != null ) {//&& !this.isPlayLocalVideo()
            PlayItem var1 = this.getVideoItem(mCurrentVideoItem);
            if(var1 != null && var1.cdnList != null && var1.cdnList.length != 0) {
                this.mCdnIndex = (this.mCdnIndex + 1) % var1.cdnList.length;
               // this.bi.onSwitchCDN(var1.cdnList[this.ba].cdn);
                return var1.cdnList[this.mCdnIndex].url;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
    public boolean switchCDN() {
        if(this.mCurrentVideoItem == null) {
            return false;
        } else {
            PlayItem var1 = this.getVideoItem(mCurrentVideoItem);
            if(var1 != null && var1.cdnList != null && var1.cdnList.length != 0) {
                this.mCdnIndex = (this.mCdnIndex + 1) % var1.cdnList.length;
                if(this.mCdnIndex == 0) {
                    return false;
                } else {
                   // this.bi.onSwitchCDN(var1.cdnList[this.ba].cdn);
                   // this.C(); ping address
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public boolean reStartPlayVideo(String var1) {
        this.mCurrentVideoItem = null;
      /*  if(!TextUtils.isEmpty(var1)) {
            this.be = var1;
        }
        this.playVideo();*/

        if(mVideoId>0&&(!TextUtils.isEmpty(mToken))){
            setVideoInfo(mVideoId,mToken,0);
            return true;
        }
        return false;
    }

    private void resetCdnIndex() {
        this.mCdnIndex = 0;
    }

    public String getSelectedVideoUrl() {
        if(this.mCurrentVideoItem == null) {
            return null;
        } else {
            PlayItem var1 = this.getVideoItem(mCurrentVideoItem);
            if(var1 != null && var1.cdnList != null) {
                CDNInfo var2 = var1.cdnList[this.mCdnIndex % var1.cdnList.length];
                 return var2 == null?null:var2.url;
            } else {
                return null;
            }
        }
    }

    private Runnable mDelayRestUrl=new Runnable() {
        @Override
        public void run() {
            String var1 =  switchCDNInfinity();
            if(!TextUtils.isEmpty(var1)) {
                BJLog.d("bjy", "onError what=" + -10000 + ", retry url=" + var1);
                BJFileLog.d(BJPlayerView.class, "BJPlayerView", "onError what=" + -10000 + ", retry url=" + var1);
                setDataSource(var1);
            }
        }
    };

    private boolean handleRetry( int var2){
        if(var2 == -88888) {
            return this.reStartPlayVideo("");
        } else if((var2 == 1 || var2 == 100 || var2 == 200 || var2 == -1004 || var2 == -1007 || var2 == -1010 || var2 == -110) && this.switchCDN()) {
            String  var5 = this.getSelectedVideoUrl();
            if(var5 != null) {
                BJLog.d("bjy", "onError what=" + var2 + ", retry url=" + var5);
                BJFileLog.d(BJPlayerView.class, "BJPlayerView", "onError what=" + var2 + ", retry url=" + var5);
                setDataSource(var5);
                return true;
            }
        } else if(var2 == -10000) {
            if(this.mHandler != null&&(mCurrentVideoItem!=null)) {
                this.mHandler.removeCallbacks(mDelayRestUrl);
                this.mHandler.postDelayed(mDelayRestUrl,1000L);

              /*  this.handler.removeCallbacksAndMessages((Object)null);
                this.handler.postDelayed(new Runnable() {
                    public void run() {
                        String var1 =  switchCDNInfinity();
                        if(!TextUtils.isEmpty(var1)) {
                            BJLog.d("bjy", "onError what=" + var2 + ", retry url=" + var1);
                            BJFileLog.d(BJPlayerView.class, "BJPlayerView", "onError what=" + var2 + ", retry url=" + var1);
                            BJPlayerView.this.mVideoView.setVideoPath(var1);
                            BJPlayerView.this.playVideo();
                        }

                    }
                }, 1000L);*/

                return true;
            }

          /* if(!this.isIJKMediaDefaultErrorOccurred) {
                if(this.mICenterView != null) {
                    this.mICenterView.showError(var2, var3);
                }

                this.showControllers(true);
                this.mOnPlayerViewListener.onError(this, var2);
            }

            this.isIJKMediaDefaultErrorOccurred = true;*/

        }
        resetCdnIndex();
        return false;
    }




    @Override
    public void onCompletion(final IMediaPlayer mp) {
        LogUtil.e(TAG, "onCompletion");
        this.unSubscribeCatonTimer();
        if (mp == mCurrentMediaPlayer) {
            mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
        } else {
            mService.get().mWakeLock.acquire(30000);
            mHandler.sendEmptyMessage(TRACK_PLAY_ENDED);
            mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        LogUtil.e(TAG, "onBufferingUpdate" + percent);
        Message message = mHandler.obtainMessage(PREPARE_ASYNC_UPDATE, percent);
        mHandler.sendMessage(message);
    }

    private boolean isCaton;
    private int mLastUpdatePosition;


    @Override
    public  void onSeekComplete(IMediaPlayer var1){
        this.mIsSeekComplement = true;
        this.mIsSeeking = false;
    }
    //Info (701,0)容易停住
    @Override
    public void onUpdatePlayPosition(IMediaPlayer var1, long var2){
         if(isPlaying()){

            int var4 = (int)(var2 / 1000L);
            if(var4 != this.mLastUpdatePosition){
                // LogUtil.e(TAG,  "onUpdatePlayPosition_"+System.currentTimeMillis()+"");

                this.mLastUpdatePosition = var4;
                if(mPlayPostionListener!=null){
                    mPlayPostionListener.onUpdatePlayPosition(var1,var2);
                }

                this.unSubscribeCatonTimer();
                if(!this.mIsSeekComplement) {
                    this.mObservableOfCaton = Observable.timer(3000L, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new com.baijiahulian.player.b.a<Long>() {
                        @Override
                        public void call(Long var1) {
                            // BJPlayerView.this.mPlayerPresenter.onIjkCaton();
                            // BJLog.i("BJPlayerView", "caton");
                            // BJFileLog.w(BJPlayerView.class, "BJPlayerView", "caton currentPos=" + BJPlayerView.this.mCurrentPlayPosition);
                            if(isPlaying()) {
                                mHandler.sendEmptyMessage(CATON_PRERPAREING);
                                isCaton = true;
                            }
                            // BJPlayerView.this.mOnPlayerViewListener.onCaton(BJPlayerView.this);
                        }
                    });
                }
            }
            if(this.mIsSeekComplement && var2 != (long)(this.mSeekPosition * 1000)) {
               /* if(this.mICenterView != null) {
                    this.mICenterView.dismissLoading();
                }*/
                mHandler.sendEmptyMessage(CATON_PRERPARED);
                this.mIsSeekComplement = false;
            }

            if(this.isCaton) {
                 mHandler.sendEmptyMessage(CATON_PRERPARED);
            }
            this.isCaton = false;
        }
    }

    private void resetVideoRate() {
        if(1f!=this.mPlayRate){
            this.mCurrentMediaPlayer.setSpeedUp(this.mPlayRate);
        }
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        mp.start();
        if (!mIsPrepared) {
            mIsPrepared = true;
            Message message = mHandler.obtainMessage(PLAYER_PREPARED);
            mHandler.sendMessage(message);
        }

        this.resetVideoRate();
    }

    private class TrackErrorInfo {
        private String audioId;
        private String trackName;

        public TrackErrorInfo(String audioId, String trackName) {
            this.audioId = audioId;
            this.trackName = trackName;
        }

        public String getAudioId() {
            return audioId;
        }

        public void setAudioId(String audioId) {
            this.audioId = audioId;
        }

        public String getTrackName() {
            return trackName;
        }

        public void setTrackName(String trackName) {
            this.trackName = trackName;
        }
    }
}