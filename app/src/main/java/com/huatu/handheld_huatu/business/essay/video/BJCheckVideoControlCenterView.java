package com.huatu.handheld_huatu.business.essay.video;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.CenterViewStatus;
import com.baijiahulian.player.playerview.IPlayerCenterContact;
import com.baijiahulian.player.utils.Utils;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;

import java.lang.ref.WeakReference;

public class BJCheckVideoControlCenterView implements IPlayerCenterContact.CenterView, View.OnClickListener {

    private Context mContext;
    private View rootView;
    private CenterHandler handler;                                  // 用户延迟隐藏View

    private CheckBJPlayerView mPlayerView;
    private IPlayerCenterContact.IPlayer iPlayer;

    private LinearLayout llLoading;                                 // 正在加载布局

    private TextView tvTimeSlide;                                   // 手势滑动，时间快进快退
    private LinearLayout llVolume;                                  // 声音大小，亮度调节显示
    private ImageView ivVolume;
    private TextView tvVolume;

    private TextView tvSpeed;                                       // 倍速按钮

    private RelativeLayout rlSpeedLayout;                           // 倍速选择


    private CheckBJPlayerView.OnSpeedShowListener mOnSpeedShowListener;

    public void setOnSpeedShowListener(CheckBJPlayerView.OnSpeedShowListener speedShowListener) {
        mOnSpeedShowListener = speedShowListener;
    }

    BJCheckVideoControlCenterView(View rootView, CheckBJPlayerView mPlayerView) {
        this.mContext = rootView.getContext();
        this.rootView = rootView;
        this.mPlayerView = mPlayerView;

        initView();
        initClick();
        initAnim();
    }

    private void initView() {
        handler = new CenterHandler(this);
        llLoading = rootView.findViewById(R.id.ll_loading);

        tvTimeSlide = rootView.findViewById(R.id.tv_time_slide);
        llVolume = rootView.findViewById(R.id.ll_volume);
        ivVolume = rootView.findViewById(R.id.iv_volume);
        tvVolume = rootView.findViewById(R.id.tv_volume);

        tvSpeed = rootView.findViewById(R.id.tv_speed_btn);

        rlSpeedLayout = rootView.findViewById(R.id.rl_speed);

        initSpeedLayout();
    }

    private void initClick() {
        tvSpeed.setOnClickListener(this);
        rlSpeedLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_speed_btn:
                if (rlSpeedLayout.getVisibility() == View.GONE) {
                    showSpeedLayout();
                } else {
                    hideSpeedLayout();
                }
                break;
            case R.id.rl_speed:
                hideSpeedLayout();
                break;
            default:            // 取出倍速并设置
                String tag = (String) v.getTag();
                mPlayerView.setVideoRate(Float.valueOf(tag));
                tvSpeed.setText(tag + "x");
                hideSpeedLayout();
        }

    }

    @Override
    public void onBind(IPlayerCenterContact.IPlayer iPlayer) {
        this.iPlayer = iPlayer;
    }

    @Override
    public boolean onBackTouch() {
        return false;
    }

    @Override
    public void setOrientation(int i) {
        if (mPlayerView.isFullScreen()) {
            ViewGroup.LayoutParams layoutParams = tvSpeed.getLayoutParams();
            layoutParams.height = DisplayUtil.dp2px(44);
            layoutParams.width = DisplayUtil.dp2px(44);
            tvSpeed.setLayoutParams(layoutParams);
            tvSpeed.setTextSize(16);
        } else {
            ViewGroup.LayoutParams layoutParams = tvSpeed.getLayoutParams();
            layoutParams.height = DisplayUtil.dp2px(30);
            layoutParams.width = DisplayUtil.dp2px(30);
            tvSpeed.setLayoutParams(layoutParams);
            tvSpeed.setTextSize(12);
            rlSpeedLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void showProgressSlide(int i) {
        // 左右滑动手势，改变播放位置
        tvTimeSlide.setVisibility(View.VISIBLE);
        llVolume.setVisibility(View.GONE);
        int allTime = iPlayer.getDuration();
        String all = Utils.formatDuration(allTime);
        String position = Utils.formatDuration(iPlayer.getCurrentPosition() + i, iPlayer.getDuration() >= 3600);
        tvTimeSlide.setText(position + "/" + all);
        if (i > 0) {
            tvTimeSlide.setEnabled(false);
        } else {
            tvTimeSlide.setEnabled(true);
        }
        handler.sendMsgDismiss();
    }

    @Override
    public void showLoading(String s) {
        llLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoading() {
        llLoading.setVisibility(View.GONE);
    }

    @Override
    public void showVolumeSlide(int volume, int maxVolume) {
        // 滑动手势，改变声音大小
        tvTimeSlide.setVisibility(View.GONE);
        llVolume.setVisibility(View.VISIBLE);
        int value = volume * 100 / maxVolume;
        if (value == 0) {
            ivVolume.setImageResource(R.drawable.bjplayer_ic_volume_off_white);
            tvVolume.setText("off");
        } else {
            ivVolume.setImageResource(R.drawable.bjplayer_ic_volume_up_white);
            tvVolume.setText(value + "%");
        }
        handler.sendMsgDismissDelay();
    }

    @Override
    public void showBrightnessSlide(int brightness) {
        // 滑动手势，改变亮度
        tvTimeSlide.setVisibility(View.GONE);
        llVolume.setVisibility(View.VISIBLE);
        ivVolume.setImageResource(R.drawable.bjplayer_ic_brightness);
        tvVolume.setText(brightness + "%");
        handler.sendMsgDismissDelay();
    }

    @Override
    public void showError(int i, int i1) {

    }

    @Override
    public void showError(int i, String s) {

    }

    @Override
    public void showWarning(String s) {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    @Override
    public void onVideoInfoLoaded(VideoItem videoItem) {

    }

    @Override
    public boolean isDialogShowing() {
        return false;
    }

    @Override
    public void updateDefinition() {

    }

    @Override
    public CenterViewStatus getStatus() {
        return null;
    }

    private void initSpeedLayout() {
        rootView.findViewById(R.id.speed_05).setOnClickListener(this);
        rootView.findViewById(R.id.speed_075).setOnClickListener(this);
        rootView.findViewById(R.id.speed_1).setOnClickListener(this);
        rootView.findViewById(R.id.speed_125).setOnClickListener(this);
        rootView.findViewById(R.id.speed_15).setOnClickListener(this);
        rootView.findViewById(R.id.speed_175).setOnClickListener(this);
        rootView.findViewById(R.id.speed_2).setOnClickListener(this);
    }

    // 显示隐藏倍速选择列表
    private Animation showAnim;
    private Animation hideAnim;

    private void initAnim() {
        if (showAnim == null) {
            showAnim = new TranslateAnimation(DisplayUtil.dp2px(150), 0, 0, 0);
            showAnim.setDuration(200);
            showAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mPlayerView.hideTopAndBottom();
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        if (hideAnim == null) {
            hideAnim = new TranslateAnimation(0, DisplayUtil.dp2px(150), 0, 0);
            hideAnim.setDuration(200);
            hideAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mPlayerView.showTopAndBottom();

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rlSpeedLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private void showSpeedLayout() {
        if (DisplayUtil.px2dp(mPlayerView.getMeasuredHeight()) > 380 ||
                (!CommonUtils.isPad(mContext) && mPlayerView.isFullScreen())) {       // 如果
            rlSpeedLayout.setVisibility(View.VISIBLE);
            rlSpeedLayout.startAnimation(showAnim);
        } else {
            if (mPlayerView != null) {
                if (null != mOnSpeedShowListener) {
                    mOnSpeedShowListener.onPlayRateClick();
                    return;
                }
                float playRate = mPlayerView != null ? mPlayerView.getVideoRateInFloat() : 1f;
                CheckPlayRateDialogFragment ratefragment = CheckPlayRateDialogFragment.getInstance(playRate);
                ratefragment.show(((BaseActivity) mContext).getSupportFragmentManager(), "rateplay");
            }
        }
    }

    private void hideSpeedLayout() {
        rlSpeedLayout.startAnimation(hideAnim);
    }

    private void dismissDialog() {
        tvTimeSlide.setVisibility(View.GONE);
        llVolume.setVisibility(View.GONE);
    }

    public void setSpeed(float curRate) {
        tvSpeed.setText(curRate + "x");
    }

    // 延迟隐藏的Handler
    private static class CenterHandler extends Handler {

        private WeakReference<BJCheckVideoControlCenterView> centerView;

        private CenterHandler(BJCheckVideoControlCenterView centerView) {
            this.centerView = new WeakReference<>(centerView);
        }

        private void sendMsgDismiss() {
            removeMessages(3);
            Message msg = obtainMessage(3);
            sendMessageDelayed(msg, 100);
        }

        private void sendMsgDismissDelay() {
            removeMessages(3);
            Message msg = obtainMessage(3);
            sendMessageDelayed(msg, 2000);
        }

        @Override
        public void handleMessage(Message msg) {
            if (centerView.get() == null) return;
            centerView.get().dismissDialog();
        }
    }
}
