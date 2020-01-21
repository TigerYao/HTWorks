package com.huatu.music.player;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.huatu.music.bean.Music;
import com.huatu.music.utils.CoverLoader;
import com.huatu.music.utils.LogUtil;

/**
 * Created by Administrator on 2019\12\4 0004.
 */

public class BgMediaSessionManager {

    private static final String TAG = "MediaSessionManager";

    //指定可以接收的来自锁屏页面的按键信息
/*    private static final long MEDIA_SESSION_ACTIONS =
            PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_PLAY_PAUSE
                    | PlaybackStateCompat.ACTION_STOP;*/


    private static final long MEDIA_SESSION_ACTIONS =
            PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_PAUSE
                    | PlaybackStateCompat.ACTION_PLAY_PAUSE
                    | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    | PlaybackStateCompat.ACTION_STOP
                    | PlaybackStateCompat.ACTION_SEEK_TO;

    private  IPlayServiceCallback control;
    private  Context context;
    private MediaSessionCompat mMediaSession;
    private Handler mHandler;
    private String mLastLockCoverUrl;//最近加的封面图

    private boolean mHasRelease=false;//是否已经释放

    public BgMediaSessionManager(IPlayServiceCallback control, Context context, Handler mHandler,MediaSessionCompat mediaSessionCompat) {
        this.control = control;
        this.context = context;
        this.mHandler = mHandler;
        mMediaSession=mediaSessionCompat;
        setupMediaSession();
    }

    /**
     * 初始化并激活 MediaSession
     */
    private void setupMediaSession() {

      /*   ComponentName mediaButtonReceiverComponent = new ComponentName(context.getPackageName(),
                MediaButtonIntentReceiver.class.getName());*/

      //  mMediaSession = new MediaSessionCompat(context, TAG,mediaButtonReceiverComponent,null);
//        第二个参数 tag: 这个是用于调试用的,随便填写即可
        //mMediaSession = new MediaSessionCompat(context, TAG);
        //指明支持的按键信息类型
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );
        mMediaSession.setCallback(callback, mHandler);
        mMediaSession.setActive(true);
    }

    /**
     * 更新播放状态， 播放／暂停／拖动进度条时调用
     */
    public void updatePlaybackState() {
        int state = isPlaying() ? PlaybackStateCompat.STATE_PLAYING :
                PlaybackStateCompat.STATE_PAUSED;

        mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setActions(MEDIA_SESSION_ACTIONS)
                .setState(state, getCurrentPosition(), 1)
                .build());
    }

    public void canSessionActive(boolean isActive){
        if(mMediaSession!=null){
            if(isActive!=mMediaSession.isActive()){
                mMediaSession.setActive(isActive);
            }
        }
    }

    private long getCurrentPosition() {
        try {
            return control.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 是否在播放
     *
     * @return
     */
    protected boolean isPlaying() {
        try {
            return control.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新正在播放的音乐信息，切换歌曲时调用
     */
    public void updateMetaData(Music songInfo) {
        if (songInfo == null) {
            mMediaSession.setMetadata(null);
            return;
        }

        LogUtil.e("updateMetaData",songInfo.toString());
        final   MediaMetadataCompat.Builder metaDta = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songInfo.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songInfo.artist)//.getArtist()
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songInfo.album)//.getAlbum()
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, songInfo.artist)//.getArtist()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, songInfo.duration);//.getDuration()

      /*   CoverLoader.loadBigImageView(context, songInfo, bitmap -> {
            metaDta.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
            mMediaSession.setMetadata(metaDta.build());
            return null;
        });*/
        CoverLoader.loadBigImageView(context, songInfo, new CoverLoader.CustomSimpleTarget(new CoverLoader.Callback<Bitmap>() {
            @Override
            public void invoke(Bitmap bitmap) {
                metaDta.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
                mMediaSession.setMetadata(metaDta.build());
            }
        }));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            metaDta.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1);
        }
        mMediaSession.setMetadata(metaDta.build());

    }


    /**
     * 释放MediaSession，退出播放器时调用
     */
    public void release() {
        if(!mHasRelease){
            mHasRelease=true;
            context=null;
            control=null;
            mMediaSession.setCallback(null);
            mMediaSession.setActive(false);
            mMediaSession.release();
        }
    }


    /**
     * API 21 以上 耳机多媒体按钮监听 MediaSessionCompat.Callback
     */
    private MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {

//        接收到监听事件，可以有选择的进行重写相关方法

        @Override
        public void onPlay() {
            try {
                control.playPause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPause() {
            try {
                control.playPause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSkipToNext() {

        }

        @Override
        public void onSkipToPrevious() {

        }

        @Override
        public void onStop() {
            try {
                control.playPause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSeekTo(long pos) {

        }
    };
}
