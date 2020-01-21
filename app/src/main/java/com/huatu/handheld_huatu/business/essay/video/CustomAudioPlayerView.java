package com.huatu.handheld_huatu.business.essay.video;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.player.bean.VideoItem;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;

import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.essay.net.SimplePlayerInfoLoader;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomAudioPlayerView extends FrameLayout implements CustomAudioPlayerListener {

    public static final int STATE_IDLE = -1;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_PREPARED = 3;
    public static final int STATE_PLAYING = 4;
    public static final int STATE_PAUSE = 5;
    public static final int STATE_AUTO_COMPLETE = 6;
    public static final int STATE_ERROR = 7;
    public int state = -1;

    @BindView(R.id.start_play_btn)
    public ImageView startButton;
    @BindView(R.id.timeline)
    public SeekBar progressBar;
    @BindView(R.id.current_time_tv)
    public TextView currentTimeTV;
    @BindView(R.id.duration_tv)
    public TextView totalTimeTV;
    @BindView(R.id.audio_loading_bar)
    public ProgressBar mLoadingBar;
    @BindView(R.id.time_layout)
    public View mTimeLayout;

    private int startTime;
    private String audioUrl;
    private long videoId;
    private String token;
    private CustomAudioInterface mMediaPlayer;
    private long mCurrentPosition;
    SimplePlayerInfoLoader mPlayInfoLoader;//

    private AudioManager mAudioManager;
    boolean isPauseFromUser = false;

    public interface onStartPlayListener {
        void onAudioPlayClick();
    }

    private onStartPlayListener mOnStartPlayListener;

    public void setOnStartPlayListener(onStartPlayListener startPlayListener) {
        this.mOnStartPlayListener = startPlayListener;
    }


    public CustomAudioPlayerView(Context context) {
        this(context, null);
    }

    public CustomAudioPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomAudioPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context ctx) {
        View.inflate(ctx, R.layout.custom_music_player_layout, this);
        ButterKnife.bind(this);
        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isEmptyOrNull(audioUrl) && Utils.isEmptyOrNull(token)) {
                    ToastUtils.showMessage("老师正在忙碌，稍后奉上~");
                    return;
                }
                isPauseFromUser = false;
                if (state == -1) {
                    if (null != mOnStartPlayListener) {
                        mOnStartPlayListener.onAudioPlayClick();
                    }
                    startVideo();
                } else if (state == STATE_PLAYING) {
                    isPauseFromUser = true;
                    cancelProgressTimer();
                    mMediaPlayer.pause();
                    startButton.setImageResource(R.mipmap.music_player_start_btn);
                    state = STATE_PAUSE;
                    setKeepScreenOn(false);
                } else if (state == STATE_PAUSE) {
                    if (null != mOnStartPlayListener) {
                        mOnStartPlayListener.onAudioPlayClick();
                    }
                    mMediaPlayer.start();
                    startProgressTimer();
                    startButton.setImageResource(R.mipmap.music_player_pause_btn);
                    state = STATE_PLAYING;
                    setKeepScreenOn(true);
                } else if (state == STATE_AUTO_COMPLETE) {
                    if (null != mOnStartPlayListener) {
                        mOnStartPlayListener.onAudioPlayClick();
                    }
                    progressBar.setProgress(0);
                    mMediaPlayer.seekTo(0);
                    startButton.setImageResource(R.mipmap.music_player_pause_btn);
                    setKeepScreenOn(false);
                }
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                cancelProgressTimer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (state != STATE_PLAYING &&
                        state != STATE_PAUSE) return;
                long time = seekBar.getProgress() * getDuration() / 100;
                mMediaPlayer.seekTo(time);
            }
        });

        progressBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return state == STATE_IDLE;
            }
        });
    }


    //系统播放器播放百家云
    public void reSetAudioData(long videoId, String token) {
        mLoadingBar.setVisibility(View.VISIBLE);
        if (null == mPlayInfoLoader) {
            mPlayInfoLoader = new SimplePlayerInfoLoader() {
                @Override
                public void onFailure(HttpException var1) {
                    if (null != mPlayInfoLoader) {
                        mLoadingBar.setVisibility(View.GONE);
                        ToastUtil.showToast("发生错误，稍后重试(" + var1.getCode() + ")");
                        state = STATE_ERROR;
                    }
                }

                @Override
                public void onSuccess(VideoItem var1) {
                    //LogUtils.e("onSuccess", GsonUtil.toJsonStr(var1));
                    if (null != mPlayInfoLoader) {
                        reSetAudioData(var1.audioUrl, 0, (int) var1.duration);
                    }
                }
            };
            // mBjPlayerInfoLoader.setVideoId(16703992,"jCR11q89lq5MyA0LsBVeTK_voKv1q4zf5ZuGcnHCnTrob3Lg8GDvmjTSEzZrILF4");
        }
        mPlayInfoLoader.setVideoId(videoId, token);
        mPlayInfoLoader.getVideoInfo();
    }


    //系统播放器
    public void reSetAudioData(String url, int startTime, int duration) {
        this.startTime = startTime;
        audioUrl = url;

        mLoadingBar.setVisibility(View.VISIBLE);
        mAudioManager = (AudioManager) UniApplicationContext.getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        setKeepScreenOn(true);
        resetProgressAndTime();
        state = STATE_PREPARING;
        if (null == mMediaPlayer) {
            mMediaPlayer = new CustomAudioPlayer(getContext(), audioUrl);
            ((CustomAudioPlayer) mMediaPlayer).setMusticPlayerListener(this);
            mMediaPlayer.start();//initmediaPlay  + prepareAysnc
        } else {
            if (mMediaPlayer instanceof CustomAudioPlayer) {
                ((CustomAudioPlayer) mMediaPlayer).resetDataSource(url, 0, "");
            }
        }
    }


    //百家云播放器来播音频
    public void reSetBjAudioData(long videoId, String token, int startTime, int duration) {
        this.startTime = startTime;
        this.videoId = videoId;
        this.token = token;

        mLoadingBar.setVisibility(View.VISIBLE);
        mAudioManager = (AudioManager) UniApplicationContext.getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        setKeepScreenOn(true);
        resetProgressAndTime();
        state = STATE_PREPARING;
        if (null == mMediaPlayer) {
            mMediaPlayer = new CustomAudioPlayer(getContext(), videoId, token);
            ((CustomAudioPlayer) mMediaPlayer).setMusticPlayerListener(this);
            mMediaPlayer.start();//initmediaPlay  + prepareAysnc
        } else {
            if (mMediaPlayer instanceof CustomAudioPlayer) {
                ((CustomAudioPlayer) mMediaPlayer).resetDataSource("", videoId, token);
            }
        }
    }

    public boolean isPlaying() {
        if(null==mMediaPlayer) return false;
        return (mMediaPlayer).isPlaying();
    }

    //仅设置数据，不播放
    public void setAudioData(String audioUrl, long videoId, String token) {
        //  releasePlayer();
        this.audioUrl = audioUrl;
        this.videoId = videoId;
        this.token = token;
        state = STATE_IDLE;
    }

    public void resumePlay() {
        if (isPauseFromUser)
            return;
        if (mMediaPlayer != null && state == STATE_PAUSE) {
            state = STATE_PLAYING;
            mMediaPlayer.start();
            startProgressTimer();
            setKeepScreenOn(true);
            startButton.setImageResource(R.mipmap.music_player_pause_btn);
        } else if (mMediaPlayer != null && state == STATE_PREPARING) {
            state = STATE_PREPARING;
            mMediaPlayer.prepare();
            setKeepScreenOn(true);
        }
    }

    public void pausePlay() {
        if (isPauseFromUser)
            return;
        if (mMediaPlayer != null && state != STATE_IDLE) {
            state = STATE_PAUSE;
            mMediaPlayer.pause();
            cancelProgressTimer();
            setKeepScreenOn(false);
            startButton.setImageResource(R.mipmap.music_player_start_btn);
            mLoadingBar.setVisibility(View.GONE);
        }
    }


    public void forcePausePlay(){
        isPauseFromUser = true;

        if(mMediaPlayer != null){
            cancelProgressTimer();
            mMediaPlayer.pause();
            startButton.setImageResource(R.mipmap.music_player_start_btn);
            state = STATE_PAUSE;
            setKeepScreenOn(false);
        }
    }

    public void releasePlayer() {
        if (mTimeLayout.getVisibility() == View.VISIBLE)
            mTimeLayout.setVisibility(View.INVISIBLE);
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.INVISIBLE);
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        audioUrl = null;
        token = null;
        mPlayInfoLoader = null;
        cancelProgressTimer();
        if (mAudioManager != null)
            mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        startButton.setImageResource(R.mipmap.music_player_start_btn);
        onLoading(false);
    }

    @Override
    public void onPrepared() {
        startButton.setImageResource(R.mipmap.music_player_pause_btn);
        if (mMediaPlayer.getDuration() != 0) {
            totalTimeTV.setText(TimeUtils.getTime((int) mMediaPlayer.getDuration()));
        }
        currentTimeTV.setText(TimeUtils.getTime((int) mMediaPlayer.getCurrentPosition()));
        progressBar.setMax(100);
        state = STATE_PREPARED;
        if (startTime > 0) {
            mMediaPlayer.seekTo(startTime);
            startTime = 0;
        }
        mMediaPlayer.start();
        state = STATE_PLAYING;
        startProgressTimer();
        mLoadingBar.setVisibility(View.GONE);
        if (mTimeLayout.getVisibility() != View.VISIBLE)
            mTimeLayout.setVisibility(View.VISIBLE);
        if (progressBar.getVisibility() != View.VISIBLE)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCompletion() {
        startButton.setImageResource(R.mipmap.music_player_start_btn);
        cancelProgressTimer();
        progressBar.setProgress(100);
        state = STATE_AUTO_COMPLETE;
        currentTimeTV.setText(TimeUtils.getTime((int) getDuration()));
        if (mLoadingBar.getVisibility() == View.VISIBLE)
            mLoadingBar.setVisibility(View.GONE);
    }

    @Override
    public void updateBufferProgress(int percent) {
        setBufferProgress(percent);
    }

    @Override
    public void onSeekComplete() {
        startProgressTimer();
        if (state == STATE_AUTO_COMPLETE) {
            mMediaPlayer.start();
            state = STATE_PLAYING;
        } else if (state == STATE_LOADING)
            onLoading(false);
    }

    @Override
    public void onError(int code, String msg) {
        ToastUtil.showToast("发生错误，稍后重试(" + msg + ")");
        state = STATE_ERROR;
//        releasePlayer();
//        state = STATE_IDLE;
    }

    @Override
    public void onLoading(boolean isLoading) {
        if (state == STATE_PLAYING || state == STATE_LOADING) {
            state = isLoading ? STATE_LOADING : STATE_PLAYING;
            mLoadingBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    //点击播放时初始化播放器
    private void startVideo() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showMessage("网络未连接,连网后重试");
            return;
        }
        mLoadingBar.setVisibility(View.VISIBLE);

        mMediaPlayer = !Utils.isEmptyOrNull(audioUrl) ? new CustomAudioPlayer(getContext(), audioUrl) : new CustomAudioPlayer(getContext(), videoId, token);
        ((CustomAudioPlayer) mMediaPlayer).setMusticPlayerListener(this);
        mAudioManager = (AudioManager) UniApplicationContext.getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        setKeepScreenOn(true);
        resetProgressAndTime();
        state = STATE_PREPARING;
        mMediaPlayer.start();//初始化播放器
    }

    private void startProgressTimer() {
        if (mMediaPlayer != null && mMediaPlayer.mMainHandler != null) {
            mMediaPlayer.mMainHandler.removeCallbacks(mProgressTimerTask);
            mMediaPlayer.mMainHandler.postDelayed(mProgressTimerTask, 1000);
        }
    }

    private void cancelProgressTimer() {
        if (mMediaPlayer != null && mMediaPlayer.mMainHandler != null)
            mMediaPlayer.mMainHandler.removeCallbacks(mProgressTimerTask);
    }

    @Override
    public void updatePostion(int duration, int position) {
        final int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
        onProgress(progress, position, duration);
    }

    private void onProgress(int progress, long position, long duration) {
        if (mCurrentPosition == position)
            return;
        if (state == STATE_LOADING)
            onLoading(false);
        if (progress != 0) progressBar.setProgress(progress);
        if (position != 0) currentTimeTV.setText(TimeUtils.getTime((int) position));
        totalTimeTV.setText(TimeUtils.getTime((int) duration));
        mCurrentPosition = position;
    }

    private void setBufferProgress(int bufferProgress) {
        if (bufferProgress != 0) progressBar.setSecondaryProgress(bufferProgress);
    }

    private long getCurrentPositionWhenPlaying() {
        long position = 0;
        if (state == STATE_PLAYING ||
                state == STATE_PAUSE) {
            try {
                position = mMediaPlayer.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }

    private long getDuration() {
        long duration = 0;
        try {
            duration = mMediaPlayer.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

    private void resetProgressAndTime() {
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        currentTimeTV.setText("00:00");
        totalTimeTV.setText("00:00");
    }

    private Runnable mProgressTimerTask = new Runnable() {
        @Override
        public void run() {
            if (state == STATE_PLAYING || state == STATE_PAUSE) {
                final long position = getCurrentPositionWhenPlaying();
                final long duration = getDuration();
                final int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                onProgress(progress, position, duration);
                if (mMediaPlayer != null && mMediaPlayer.mMainHandler != null)
                    mMediaPlayer.mMainHandler.postDelayed(this, 1000);
            }
        }
    };

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {//是否新建个class，代码更规矩，并且变量的位置也很尴尬
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    pausePlay();
//                    releaseAllVideos();
//                    Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    resumePlay();
//                    try {
//                        if (player != null && player.state == Jzvd.STATE_PLAYING) {
//                            player.startButton.performClick();
//                        }
//                    } catch (IllegalStateException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };
}
