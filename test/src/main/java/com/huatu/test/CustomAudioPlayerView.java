package com.huatu.test;

import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import com.huatu.test.utils.NetUtil;
import com.huatu.test.utils.TimeUtils;

import java.util.Timer;
import java.util.TimerTask;

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

  //  @BindView(R.id.start_play_btn)
    public ImageView startButton;

  //  @BindView(R.id.timeline)
    public SeekBar progressBar;

   // @BindView(R.id.current_time_tv)
    public TextView currentTimeTV;

    //@BindView(R.id.duration_tv)
    public TextView totalTimeTV;

    //@BindView(R.id.audio_loading_bar)
    public ProgressBar mLoadingBar;

   // @BindView(R.id.time_layout)
    public View mTimeLayout;

    private int startTime;
    private String audioUrl;
    private long videoId;
    private String token;
    private CustomAudioInterface mMediaPlayer;
    private long mCurrentPosition;
    private ProgressTimerTask mProgressTimerTask;

    protected Timer UPDATE_PROGRESS_TIMER;
    private AudioManager mAudioManager;
    boolean isPauseFromUser = false;

    public interface onStartPlayListener{
        void onAudioPlayClick();
    }
    private onStartPlayListener mOnStartPlayListener;
    public void setOnStartPlayListener(onStartPlayListener startPlayListener){
        this.mOnStartPlayListener=startPlayListener;
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
        startButton=this.findViewById(R.id.start_play_btn);
        progressBar=this.findViewById(R.id.timeline);
        currentTimeTV=this.findViewById(R.id.current_time_tv);

        totalTimeTV=this.findViewById(R.id.duration_tv);
        mLoadingBar=this.findViewById(R.id.audio_loading_bar);

        mTimeLayout=this.findViewById(R.id.time_layout);

        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isPauseFromUser = false;
                if (state == -1) {
                    startVideo();
                } else if (state == STATE_PLAYING) {
                    isPauseFromUser = true;
                    mMediaPlayer.pause();
                    startButton.setImageResource(R.mipmap.music_player_start_btn);
                    state = STATE_PAUSE;
                    setKeepScreenOn(false);
                } else if (state == STATE_PAUSE) {
                    mMediaPlayer.start();
                    startButton.setImageResource(R.mipmap.music_player_pause_btn);
                    state = STATE_PLAYING;
                    setKeepScreenOn(true);
                } else if (state == STATE_AUTO_COMPLETE) {
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

    //用系统播放器播放
    public void setAudioData(String url, int startTime, int duration) {
        releasePlayer();
        this.startTime = startTime;
        audioUrl = url;
//        currentTimeTV.setText(TimeUtils.getTime(startTime));
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        if (duration == 0 && !Utils.isEmptyOrNull(audioUrl)) {
//            try {
//                HashMap<String, String> headers = null;
//                if (headers == null) {
//                    headers = new HashMap<>();
//                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
//                }
//                retriever.setDataSource(audioUrl, headers);
//                String durationMs = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
//                duration = Integer.parseInt(durationMs);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }finally {
//                retriever.release();
//                retriever = null;
//            }
//        }
//        totalTimeTV.setText(TimeUtils.getTime(duration));

    }


    public void reSetAudioData(String url, int startTime, int duration){
        this.startTime = startTime;
        audioUrl = url;


        mLoadingBar.setVisibility(View.VISIBLE);
        mAudioManager = (AudioManager) UniApplicationContext.getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        setKeepScreenOn(true);
        resetProgressAndTime();
        state = STATE_PREPARING;
        if(null==mMediaPlayer){
             mMediaPlayer =   new CustomAudioPlayer(getContext(), audioUrl) ;
            ((CustomAudioPlayer) mMediaPlayer).setMusticPlayerListener(this);
            mMediaPlayer.start();
        }else {

            if(mMediaPlayer instanceof CustomAudioPlayer){
              ((CustomAudioPlayer)mMediaPlayer).resetDataSource(url);
            }
        }
     }

    //百家云播放器播放
    public void setAudioData(long videoId, String token) {
        releasePlayer();
       this.videoId = videoId;
       this.token = token;
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
        }else if (mMediaPlayer != null && state == STATE_PREPARING) {
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
        }
    }

    public void releasePlayer(){
        if (mTimeLayout.getVisibility() == View.VISIBLE)
            mTimeLayout.setVisibility(View.INVISIBLE);
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.INVISIBLE);
        if (mMediaPlayer != null)
            mMediaPlayer.release();
        audioUrl = null;
        token = null;
        cancelProgressTimer();
        AudioManager mAudioManager = (AudioManager) UniApplicationContext.getContext().getSystemService(Context.AUDIO_SERVICE);
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
        currentTimeTV.setText(TimeUtils.getTime((int)getDuration()));
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
        }else if(state == STATE_LOADING)
            onLoading(false);
    }

    @Override
    public void onError(int code, String msg) {
       // ToastUtil.showToast("发生错误，稍后重试(" + msg+")");
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
            //ToastUtils.showMessage("网络未连接,连网后重试");
            return;
        }
        mLoadingBar.setVisibility(View.VISIBLE);

        mMediaPlayer = !TextUtils.isEmpty(audioUrl) ? new CustomAudioPlayer(getContext(), audioUrl) : new CustomAudioPlayer(getContext(), videoId, token);
        ((CustomAudioPlayer) mMediaPlayer).setMusticPlayerListener(this);
        mAudioManager = (AudioManager) UniApplicationContext.getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        setKeepScreenOn(true);
        resetProgressAndTime();
        state = STATE_PREPARING;
        mMediaPlayer.start();
    }

    private void startProgressTimer() {
        cancelProgressTimer();
        UPDATE_PROGRESS_TIMER = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 800);
    }

    private void cancelProgressTimer() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }
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

    public class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (state == STATE_PLAYING || state == STATE_PAUSE) {
                final long position = getCurrentPositionWhenPlaying();
                final long duration = getDuration();
                final int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                mMediaPlayer.mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onProgress(progress, position, duration);
                    }
                });
            }
        }
    }

    private  AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {//是否新建个class，代码更规矩，并且变量的位置也很尴尬
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
