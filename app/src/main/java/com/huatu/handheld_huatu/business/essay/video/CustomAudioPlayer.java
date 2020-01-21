package com.huatu.handheld_huatu.business.essay.video;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.LayoutInflater;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.OnPlayerViewListener;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.BaiJiaPlayBackPresenter;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.LogUtils;

public class CustomAudioPlayer extends CustomAudioInterface implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener {

    private final int IDLE_CODE = 0;
    private final int PREPAREING_CODE = 1;
    private final int PREPAREED_CODE = 2;
    private final int LOADING_CODE = 3;
    private final int START_CODE = 4;
    private final int PAUSE_CODE = 5;
    private final int STOP_CODE = 6;
    private final int ERROR_CODE = 7;
    private final int REALEASE_CODE = 8;

    private int current_state = IDLE_CODE;
    private CustomAudioPlayerListener mMusticPlayerListener;
    private MediaPlayer mMediaPlayer;
    private BJPlayerView mBjPlayerView;
    private Context mCtx;
    private String mUrl;
    private long videoId;
    private String token;
    private int type;

    public CustomAudioPlayer(Context context, String url) {
        mCtx = context;
        mUrl = url;
        type = 0;
        initPlayer();
    }

    public CustomAudioPlayer(Context context, long videoId, String token) {
        mCtx = context;
        this.videoId = videoId;
        this.token = token;
        type = 1;
        initPlayer();
    }

    private void initPlayer() {
        mMainHandler = new Handler();
        mPlayerHandlerThread = new HandlerThread("music_player");
        mPlayerHandlerThread.start();
        mPlayerHandler = new Handler(mPlayerHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case PREPAREING_CODE:
                        doPrepare();
                        break;
                    case START_CODE:
                    case PREPAREED_CODE:
                        doStart();
                        break;
                    case PAUSE_CODE:
                        doPause();
                        break;
                    case STOP_CODE:
                        doStop();
                        break;
                    case REALEASE_CODE:
                        doRelease();
                        break;
                }
                current_state = msg.what;
            }
        };
    }

    public void setMusticPlayerListener(CustomAudioPlayerListener musticPlayerListener) {
        this.mMusticPlayerListener = musticPlayerListener;
    }


    @Override
    public void prepare() {
        //  release();
        if (mMusticPlayerListener != null)
            mMusticPlayerListener.onLoading(true);
        mPlayerHandler.sendEmptyMessage(PREPAREING_CODE);
    }

    private void doPrepare() {
        if (type == 0) {
            if (null == mMediaPlayer)
                initMediaPlayer();
           /*else {
               mMediaPlayer.prepareAsync();
           }*/
        } else {
            if (null == mBjPlayerView) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initBjPlayerView();
                    }
                });
            }
        }
    }

    public void resetDataSource(String url, long videoId, String token) {
        if (type == 0) {
            if (null != mMediaPlayer) {
                try {
                    doStop();
                    mMediaPlayer.setDataSource(mCtx, Uri.parse(url));
                    mMediaPlayer.prepareAsync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (null != mBjPlayerView) {
                mBjPlayerView.pauseVideo();
                mBjPlayerView.setVideoId(videoId, token);
                mBjPlayerView.playVideo();
            }
        }
    }

    private void initMediaPlayer() {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mCtx, Uri.parse(mUrl));
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBjPlayerView() {
        mBjPlayerView = (BJPlayerView) LayoutInflater.from(
                UniApplicationContext.getContext()).inflate(
                R.layout.live_video_bai_jia_paly_back_view_layout, null, false);
        ;
        BaiJiaPlayBackPresenter mBjPlayBackPresenter = new BaiJiaPlayBackPresenter(mBjPlayerView, null);

        mBjPlayerView.resetStatus();
        mBjPlayerView.setMemoryPlayEnable(false);
        mBjPlayerView.setEnableNetWatcher(false);
        mBjPlayerView.setTopPresenter(mBjPlayBackPresenter);
        mBjPlayerView.setCenterPresenter(mBjPlayBackPresenter);
        mBjPlayerView.setBottomPresenter(mBjPlayBackPresenter);
        mBjPlayerView.initPartner(Constant.BAIJIAYNN_PARTNER_KEY);
        mBjPlayerView.setVideoId(videoId, token);
        mBjPlayerView.playVideo();
        mBjPlayerView.setAudioFirstWhenEnable(true);
        mBjPlayerView.setOnPlayerViewListener(new OnPlayerViewListener() {
            @Override
            public void onVideoInfoInitialized(BJPlayerView bjPlayerView, HttpException e) {

            }

            @Override
            public void onPause(BJPlayerView bjPlayerView) {
                current_state = PAUSE_CODE;
            }

            @Override
            public void onPlay(BJPlayerView bjPlayerView) {
                current_state = START_CODE;
            }

            @Override
            public void onError(BJPlayerView bjPlayerView, int i) {
                if (mMusticPlayerListener != null)
                    mMusticPlayerListener.onError(i, "发生错误");
            }

            @Override
            public void onUpdatePosition(BJPlayerView bjPlayerView, int i) {
            }

            @Override
            public void onSeekComplete(BJPlayerView bjPlayerView, int i) {
                CustomAudioPlayer.this.onSeekComplete(null);
            }

            @Override
            public void onSpeedUp(BJPlayerView bjPlayerView, float v) {

            }

            @Override
            public void onVideoDefinition(BJPlayerView bjPlayerView, int i) {

            }

            @Override
            public void onPlayCompleted(BJPlayerView bjPlayerView, VideoItem videoItem, SectionItem sectionItem) {
                onCompletion(null);
            }

            @Override
            public void onVideoPrepared(BJPlayerView bjPlayerView) {
                onPrepared(null);
            }

            @Override
            public void onCaton(BJPlayerView bjPlayerView) {

            }

            @Override
            public String getVideoTokenWhenInvalid() {
                if (mMusticPlayerListener != null)
                    mMusticPlayerListener.onError(-1, "token 错误");
                return null;
            }
        });
    }

    @Override
    public void start() {
        if (current_state > PREPAREED_CODE && !isPlaying()) {
            mPlayerHandler.sendEmptyMessage(START_CODE);
        } else if (current_state == IDLE_CODE)
            mPlayerHandler.sendEmptyMessage(PREPAREING_CODE);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMusticPlayerListener != null)
                    mMusticPlayerListener.onPrepared();
            }
        });

        current_state = START_CODE;
    }

    private void doStart() {
        try {
            if (mMediaPlayer != null) mMediaPlayer.start();
            else if (mBjPlayerView != null) mBjPlayerView.playVideo();
        } catch (Exception e) {
        }
    }

    @Override
    public void pause() {
        if (current_state == START_CODE && isPlaying())
            mPlayerHandler.sendEmptyMessage(PAUSE_CODE);
        else if (current_state < START_CODE || current_state == ERROR_CODE)
            mPlayerHandler.sendEmptyMessage(STOP_CODE);
    }

    private void doPause() {
        try {
            if (mMediaPlayer != null) mMediaPlayer.pause();
            else if (mBjPlayerView != null) mBjPlayerView.pauseVideo();
        } catch (Exception e) {
        }
    }

    private void doStop() {
        try {

            current_state = IDLE_CODE;
            if (mMediaPlayer != null) mMediaPlayer.reset();
            else if (mBjPlayerView != null) mBjPlayerView.resetStatus();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean isPlaying() {
        try {
            return mMediaPlayer != null ? mMediaPlayer.isPlaying() : mBjPlayerView.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void seekTo(long time) {
        try {
            if (mMediaPlayer != null && current_state > PREPAREED_CODE)
                mMediaPlayer.seekTo((int) time);
            else if (mBjPlayerView != null && current_state > PREPAREED_CODE)
                mBjPlayerView.seekVideo((int) (time / 1000));
        } catch (Exception e) {
        }
    }

    @Override
    public void release() {
        doRelease();
    }

    private void doRelease() {
        if (mPlayerHandler != null && mPlayerHandlerThread != null) {//不知道有没有妖孽
            HandlerThread tmpHandlerThread = mPlayerHandlerThread;
            if (mMediaPlayer != null) {
                MediaPlayer tmpMediaPlayer = mMediaPlayer;

                tmpMediaPlayer.stop();
                tmpMediaPlayer.release();
            } else if (mBjPlayerView != null)
                mBjPlayerView.onDestroy();
            tmpHandlerThread.quit();
            mMediaPlayer = null;
            mBjPlayerView = null;
            mPlayerHandler.removeCallbacksAndMessages(null);
            mMainHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public long getCurrentPosition() {
        try {
            if (mMediaPlayer != null)
                return mMediaPlayer.getCurrentPosition();
            else if (mBjPlayerView != null)
                return mBjPlayerView.getCurrentPosition() * 1000;
        } catch (Exception e) {
        }
        return 0;
    }

    @Override
    public long getDuration() {
        try {
            if (mMediaPlayer != null)
                return mMediaPlayer.getDuration();
            else if (mBjPlayerView != null)
                return mBjPlayerView.getDuration() * 1000;
        } catch (Exception e) {
        }
        return 0;
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        try {
            if (mMediaPlayer != null)
                mMediaPlayer.setVolume(leftVolume, rightVolume);
        } catch (Exception e) {
        }
    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int progress) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMusticPlayerListener != null)
                    mMusticPlayerListener.onCompletion();
            }
        });

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, final int what, int extra) {
        LogUtils.d("onError===what ==" + what + "==extra==" + extra);
        String errorMsg = null;
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_IO:
                errorMsg = "网络异常，稍后重试";
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                errorMsg = "MEDIA_ERROR_MALFORMED";
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                errorMsg = "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK";
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                errorMsg = "MEDIA_ERROR_SERVER_DIED";
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                errorMsg = "MEDIA_ERROR_TIMED_OUT";
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                errorMsg = "MEDIA_ERROR_UNKNOWN";
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                errorMsg = "MEDIA_ERROR_UNSUPPORTED";
                break;
        }
        if (Utils.isEmptyOrNull(errorMsg))
            return false;
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            final String messge = errorMsg;
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mMusticPlayerListener != null)
                        mMusticPlayerListener.onError(what, messge);
                }
            });

        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, final int what, int extra) {
        LogUtils.d("onInfo===what ==" + what + "==extra==" + extra);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (MediaPlayer.MEDIA_INFO_BUFFERING_END == what) {
                    mMusticPlayerListener.onLoading(false);
                } else if (MediaPlayer.MEDIA_INFO_BUFFERING_START == what) {
                    mMusticPlayerListener.onLoading(true);
                }
            }
        });

        return false;
    }


    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMusticPlayerListener != null)
                    mMusticPlayerListener.onSeekComplete();
            }
        });
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {

    }
}
