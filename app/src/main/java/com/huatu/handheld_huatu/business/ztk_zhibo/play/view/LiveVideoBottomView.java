package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.TimeUtils;

/**
 * Created by yaohu on 2018/8/7.
 */

public class LiveVideoBottomView extends FrameLayout {
    private TextView btnTitleSchedule;
    private TextView btnTitleSpeed;
    private ImageView btnFullScreen;
    private TextView tvTotalTime;
    private SeekBar seekBar;
    private ImageView btnStartBtn;
    private TextView liveVideoTime;
    private OnClickListener mClickListener;
    private SeekBar.OnSeekBarChangeListener mBarChangeListener;
    private TextView mLiveQuality;
    private TextView mPptPageView;
    private View mFloatViewLayout;

    private int playState = 0;//0 为开始，1，播放，2 ，暂停
    private boolean mIsFullScreen;
    private int mTotalDuration;
    private int mCurrentTime;
    public boolean mShouldSeek = true;
    private String mSpeed;
    private String mQualityName;
    private boolean mIsLocalVideo;

    public LiveVideoBottomView(@NonNull Context context) {
        this(context, null);
    }

    public LiveVideoBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveVideoBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(false);
        initView(context);
    }

    public void showFullScreen(boolean fullScreen) {
        if (fullScreen && !mIsFullScreen) {
            inflateView(true);
            initView(getContext());
            onVideoStateChange(playState);
        } else if (!fullScreen && mIsFullScreen) {
            inflateView(false);
            initView(getContext());
            onVideoStateChange(playState);
        }
    }

    public void updateTime(int currentTime, int totalTime) {
        tvTotalTime.setText(TimeUtils.getTime(totalTime));
        if (mIsFullScreen)
            liveVideoTime.setText(TimeUtils.getTime(currentTime) + "/");
        else
            liveVideoTime.setText(TimeUtils.getTime(currentTime));
        seekBar.setProgress(currentTime);
        seekBar.setMax(totalTime);
        mTotalDuration = totalTime;
        mCurrentTime = currentTime;
    }


    public void isLive(boolean isLive) {
        if (btnTitleSpeed != null)
            btnTitleSpeed.setVisibility(isLive ? View.GONE : View.VISIBLE);
    }

    public void isLocalVideo(boolean isLocalVideo) {
        if (mLiveQuality != null)
            mLiveQuality.setVisibility(isLocalVideo ? GONE : VISIBLE);
        mIsLocalVideo = isLocalVideo;
    }

    private void initView(Context ctx) {
        btnTitleSpeed = (TextView) findViewById(R.id.live_title_speed_btn);
        btnTitleSchedule = (TextView) findViewById(R.id.live_title_schedule_btn);
        btnFullScreen = (ImageView) findViewById(R.id.live_video_full_screen_btn);
        tvTotalTime = (TextView) findViewById(R.id.live_video_duration_tv);
        seekBar = (SeekBar) findViewById(R.id.live_video_seek_bar);
        btnStartBtn = (ImageView) findViewById(R.id.live_video_pause_btn);
        btnStartBtn.setImageResource(R.drawable.btn_video_small_play);
        liveVideoTime = (TextView) findViewById(R.id.live_video_time);
        mLiveQuality = (TextView) findViewById(R.id.live_bottom_quality);
        mPptPageView = findViewById(R.id.ppt_page);

        if (btnFullScreen != null)
            btnFullScreen.setOnClickListener(mOnclickListener);
        if (btnStartBtn != null)
            btnStartBtn.setOnClickListener(mOnclickListener);
        if (btnTitleSchedule != null)
            btnTitleSchedule.setOnClickListener(mOnclickListener);
        if (btnTitleSpeed != null)
            btnTitleSpeed.setOnClickListener(mOnclickListener);
        if (mLiveQuality != null)
            mLiveQuality.setOnClickListener(mOnclickListener);
        if (seekBar != null) {
            if (mBarChangeListener != null) {
                seekBar.setOnSeekBarChangeListener(mBarChangeListener);
                seekBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (playState == 0) return true;
                        return false;
                    }
                });
            }
        }

        updateTime(mCurrentTime, mTotalDuration);
        updateSpeed(mSpeed);
        updateQuality(mQualityName);
        isLocalVideo(mIsLocalVideo);
        if (mIsLive) {
            setLiveStyle();
        }
//        if (mIsFullScreen) {
//            int page = currentPage;
//            currentPage = 0;
//            pptPageChange(page, totalPages);
//        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mClickListener = l;
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener seekbarChangeListener) {
        this.mBarChangeListener = seekbarChangeListener;
        seekBar.setOnSeekBarChangeListener(mBarChangeListener);
    }

    public void onVideoStateChange(int playState) {
        this.playState = playState;
        switch (playState) {
            case 0://开始
                btnStartBtn.setImageResource(R.drawable.btn_video_small_play);
                break;
            case 1://播放
                btnStartBtn.setImageResource(R.drawable.btn_video_small_pause);
                break;
            case 2://暂停
                btnStartBtn.setImageResource(R.drawable.btn_video_small_play);
                break;
            case 3://完成
                btnStartBtn.setImageResource(R.drawable.btn_video_small_play);
                break;
            case 4://stop
                btnStartBtn.setImageResource(R.drawable.btn_video_small_play);
                break;

        }
    }

    private boolean mIsLive;

    // 设置直播样式
    public void setLiveStyle() {
        mIsLive = true;
        btnStartBtn.setVisibility(View.INVISIBLE);
        liveVideoTime.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);
        tvTotalTime.setVisibility(View.INVISIBLE);
    }

    private OnClickListener mOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onClick(view);
        }
    };

    private void inflateView(boolean isFullScreen) {
        this.removeAllViews();
        mIsFullScreen = isFullScreen;
        setBackgroundResource(R.drawable.bg_live_video_bottom);
        View view = View.inflate(getContext(), isFullScreen ? R.layout.layout_live_video_bottom_land : R.layout.layout_live_video_bottom, this);
        if (!mIsLive && isFullScreen) {
            ViewStub operationView = view.findViewById(R.id.reback_bottom_viewstub);
            mFloatViewLayout = operationView.inflate();
        }
    }

    public View getFloatViewLayout() {
        return mFloatViewLayout;
    }

    public void updateSpeed(String s) {
        if (TextUtils.isEmpty(s))
            return;
        if (btnTitleSpeed != null)
            btnTitleSpeed.setText(s);
        mSpeed = s;
    }

    public void updateQuality(String quality) {
        if (TextUtils.isEmpty(quality))
            return;
        if (mLiveQuality != null)
            mLiveQuality.setText(quality);
        mQualityName = quality;
    }

//    int currentPage, totalPages;

    public void pptPageChange(int currentPage, int totalPages) {
//        if (currentPage > 0 && this.currentPage == currentPage)
//            return;
//        this.currentPage = currentPage;
//        this.totalPages = totalPages;
//        if (totalPages > 0 && mPptPageView != null) {
//            String value = currentPage == 0 ? "白板" : currentPage + "/" + (totalPages - 1);
//            mPptPageView.setText(value);
//            if (mPptPageView.getVisibility() == View.GONE)
//                mPptPageView.setVisibility(VISIBLE);
//        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }
}
