package com.huatu.handheld_huatu.business.essay.video;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baijiahulian.player.playerview.IPlayerBottomContact;
import com.baijiahulian.player.utils.Utils;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.examfragment.OnSwitchListener;

public class BJCheckVideoControlV2BottomView implements IPlayerBottomContact.BottomView, View.OnClickListener {

    private Context mContext;
    private View rootView;
    private CheckBJPlayerView mPlayerView;
    private IPlayerBottomContact.IPlayer iPlayer;
/*
    private Button btnPlayCenter;                           // 中间的播放按钮
    private ImageView ivCover;                              // 遮罩图*/

    private ImageButton btnPlay;                            // 底部播放按钮
    private TextView tvCurrentTimeBig;                      // 大当前播放时间
    private TextView tvAllTimeBig;                          // 大总时间
    private TextView tvCurrentTime;                         // 小当前播放时间
    private TextView tvAllTime;                             // 小总时间
    private SeekBar seekBar;
    private ImageView ivRotate;                             // 全屏按钮

    private boolean isSeek = false;                         // 是否手动拖拽进度条

    private int position = 0;                               // 查看pdf回来，继续播放的位置
    private boolean isRecord;                               // 是否查看pdf回来点击播放

    private OnSwitchListener onSwitchListener;

    private CheckBJPlayerV2View.onStartPlayListener mOnStartPlayListener;

    public void setOnStartPlayListener(CheckBJPlayerV2View.onStartPlayListener startPlayListener) {
        this.mOnStartPlayListener = startPlayListener;
    }

    BJCheckVideoControlV2BottomView(View rootView, CheckBJPlayerView mPlayerView) {
        this.mContext = rootView.getContext();
        this.rootView = rootView;
        this.mPlayerView = mPlayerView;

        initView();
        initClick();
    }

    private void initView() {
        // btnPlayCenter = rootView.findViewById(R.id.btn_play_center);
        // ivCover = rootView.findViewById(R.id.iv_cover);

        btnPlay = rootView.findViewById(R.id.btn_play);
        tvCurrentTimeBig = rootView.findViewById(R.id.tv_current_big);
        tvAllTimeBig = rootView.findViewById(R.id.tv_all_big);
        tvCurrentTime = rootView.findViewById(R.id.tv_current);
        tvAllTime = rootView.findViewById(R.id.tv_all);
        seekBar = rootView.findViewById(R.id.seek_bar);
        ivRotate = rootView.findViewById(R.id.iv_rotate);
    }

    private void initClick() {
        // btnPlayCenter.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        ivRotate.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 进度条拖动结束，设置播放时间
                isSeek = false;
                iPlayer.seekVideo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 播放
            // case R.id.btn_play_center:
            case R.id.btn_play:
                if (null != mOnStartPlayListener) {
                    mOnStartPlayListener.onVideoPlayClick();
                }
                if (iPlayer.isPlaying()) {
                    iPlayer.pauseVideo();
                } else {
                    iPlayer.playVideo();
                    if (isRecord) {
                        iPlayer.seekVideo(position);
                        isRecord = false;
                    }
                    // btnPlayCenter.setVisibility(View.GONE);
                    // ivCover.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_rotate:                        // 全屏播放跳转新的Activity
                onSwitchListener.onSwitch();
                break;
        }
    }

    @Override
    public void onBind(IPlayerBottomContact.IPlayer iPlayer) {
        this.iPlayer = iPlayer;
    }

    @Override
    public void setDuration(int duration) {
        // 总时间
        String durationText = Utils.formatDuration(duration);
        tvAllTime.setText(durationText);
        tvAllTimeBig.setText(durationText);
        seekBar.setMax(duration);
    }

    @Override
    public void setCurrentPosition(int currentPosition) {
        // 当前时间
        String durationText = Utils.formatDuration(currentPosition);
        tvCurrentTime.setText(durationText);
        tvCurrentTimeBig.setText(durationText);
        if (!isSeek) {
            seekBar.setProgress(currentPosition);
        }
    }

    @Override
    public void setIsPlaying(boolean isPlaying) {
        // 是否在播放
        if (isPlaying) {
            btnPlay.setImageResource(R.mipmap.vod_pause_icon);
            //btnPlayCenter.setVisibility(View.GONE);
            //ivCover.setVisibility(View.GONE);
        } else {
            btnPlay.setImageResource(R.mipmap.vod_play_icon);
        }
    }

    @Override
    public void setOrientation(int orientation) {
        // 设置方向
        if (mPlayerView.isFullScreen()) {
            ivRotate.setVisibility(View.GONE);
            tvCurrentTime.setVisibility(View.GONE);
            tvAllTime.setVisibility(View.GONE);
            tvCurrentTimeBig.setVisibility(View.VISIBLE);
            tvAllTimeBig.setVisibility(View.VISIBLE);
        } else {
            ivRotate.setVisibility(View.VISIBLE);
            tvCurrentTime.setVisibility(View.VISIBLE);
            tvAllTime.setVisibility(View.VISIBLE);
            tvCurrentTimeBig.setVisibility(View.GONE);
            tvAllTimeBig.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBufferingUpdate(int i) {

    }

    @Override
    public void setSeekBarDraggable(boolean b) {

    }

    public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
        this.onSwitchListener = onSwitchListener;
    }

    public void setPosition(int currentPosition) {
        this.position = currentPosition;
        this.isRecord = true;
    }
}
