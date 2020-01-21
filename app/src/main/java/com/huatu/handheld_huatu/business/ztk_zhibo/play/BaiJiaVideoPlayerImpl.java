package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

//import com.baijia.baijiashilian.liveplayer.ViESurfaceViewRenderer;
import com.baijia.baijiashilian.liveplayer.render.ViESurfaceViewRenderer;
import com.baijia.player.playback.LivePlaybackSDK;
import com.baijia.player.playback.PBRoom;
import com.baijia.player.playback.mocklive.OnPlayerListener;
import com.baijiahulian.common.networkv2.HttpException;

import com.baijiahulian.livecore.LiveSDK;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.context.OnLiveRoomListener;
import com.baijiahulian.livecore.launch.LPLaunchListener;
import com.baijiahulian.livecore.models.LPKVModel;
import com.baijiahulian.livecore.models.imodels.IExpressionModel;
import com.baijiahulian.livecore.models.imodels.ILoginConflictModel;
import com.baijiahulian.livecore.models.imodels.IMediaModel;
import com.baijiahulian.livecore.models.imodels.IMessageModel;
import com.baijiahulian.livecore.models.imodels.IUserModel;
import com.baijiahulian.livecore.ppt.LPPPTFragment;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
import com.baijiahulian.livecore.viewmodels.ChatVM;
import com.baijiahulian.livecore.viewmodels.impl.LPDocListViewModel;
import com.baijiahulian.livecore.wrapper.LPPlayer;
import com.baijiahulian.livecore.wrapper.listener.LPPlayerListener;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.SimpleOnPlayerViewListener;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiayun.log.BJFileLog;
import com.baijiayun.ppt.PPTView;
import com.google.gson.JsonObject;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.ChatMessageListWrap;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.LiveChatExpressBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.VideoPlayer;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.LiveUserInfo;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by saiyuan on 2017/12/19.
 */

public class BaiJiaVideoPlayerImpl extends VideoPlayer {
    public List<Subscription> mSubscriptions;

    protected SurfaceView mBaiJiaLiveVideoView;
    protected BJPlayerView mBaiJiaBackPlayerView;

    protected LPPlayerListener onLPPlayerListener;
    protected LPPlayer mBaiJiaLivePlayer; // player用于播放远程音视频流，用于直播
    protected float[] bjPlaySpeedLists = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f};
    protected BaiJiaPlayBackPresenter mBjPlayBackPresenter;
    protected LiveRoom mBjyPlayRoom;
    private boolean isLanchSuccess = false;
    private String curLiveTeacherId = "";
//    private final String mPPtFragmentFlag = "baijialpptfragement_flag";
    private boolean mIsFinished = true;
    private LPConstants.LPUserType mLiveUserType;
    private boolean mMustCodePlay = false;
    private int errorCount = 0;
    private ViewGroup mCurrentPPtViewLayout;
    private PPTView mPPtView;
    private boolean isLocalVideo = false;
    private boolean teacherVideoOn, teacherAudioOn;
    private Subscription msubscriptionOfTeacherMedia;
    private CourseWareInfo mLiveCourseInfo;
    private int catonTimes = -1;
    private long catonTipTime;
    private boolean isShowVideo = true;
    private LPLaunchListener lpLaunchListener = new LPLaunchListener() {
        @Override
        public void onLaunchSteps(int step, int totalStep) {
            LogUtils.i("init step:" + step + "/" + totalStep);
        }

        @Override
        public void onLaunchError(LPError error) {
            if (null == error) {
                return;
            }
            LogUtils.e("error", error.getCode() + " " + error.getMessage());

            isLanchSuccess = false;
            isPlaying = false;
            int code = (int) error.getCode();
            String errMsg = error.getMessage();
            if (code == -1) errMsg = "当前网络不可用";
            if (errorCount <= 3 && (code == 5101 || code == 5102 || code == 5103) && onVideoPlayListener != null) {
                errorCount++;
                onVideoPlayListener.onPlayErrorInfo(errMsg, code);
                return;
            }
            errorCount = 0;
            if (!isLive) {
                //doReEnterRoom();
                onVideoPlayListener.onJoinFailed(errMsg + ":" + code, true);
                return;
            }
            switch (code) {
                case LPError.CODE_WARNING_PLAYER_LAG:
                    if (mOnPlayerViewListener != null)
                        mOnPlayerViewListener.onCaton(null);
                    break;
                case LPError.CODE_ERROR_ROOMSERVER_LOSE_CONNECTION:
                    if (onVideoPlayListener != null) {
                        boolean isFront = onVideoPlayListener.getActivityRunningFront();
                        if (isFront) refreshPlay();
                        else
                            onVideoPlayListener.onJoinFailed(errMsg + ":" + code, true);
                    }
                    break;
                case LPError.CODE_ERROR_UNKNOWN:
                case LPError.CODE_ERROR_NETWORK_FAILURE:
                case LPError.CODE_ERROR_LOGIN_CONFLICT:
                case LPError.CODE_ERROR_ROOMSERVER_FAILED:
                case LPError.CODE_ERROR_ENTER_ROOM_FORBIDDEN:
                case LPError.CODE_ERROR_PERMISSION_DENY:
                    if (onVideoPlayListener != null)
                        onVideoPlayListener.onJoinFailed(errMsg + ":" + code, true);
                    break;
                case 1002:
                    if (!mMustCodePlay) {
                        mMustCodePlay = true;
                        refreshPlay();
                    } else if (onVideoPlayListener != null)
                        onVideoPlayListener.onJoinFailed(errMsg + ":" + code, true);
                default:
                    if (onVideoPlayListener != null) {
                        try {
                            if (!TextUtils.isEmpty(error.getMessage()))
                                ToastUtils.showShort(error.getMessage() + ":" + code);
                        } catch (Exception e) {
                        }
                    }
                    break;
            }
            if (onVideoPlayListener != null)
                onVideoPlayListener.onPlayErrorInfo(errMsg, code);
        }

        @Override
        public void onLaunchSuccess(LiveRoom lRoom) {
            mBjyPlayRoom = lRoom;
            isLanchSuccess = true;
            if (null == onVideoPlayListener) return;//添加判断，表示ui层已释放
            LogUtils.d("onLaunchSuccess.....start");
            mPPtView = new MyPPtView(mPptViewLayout.getContext());
            if (isLive) {
                onBaiJiaLiveInitSuccess();
            } else {
                onBaiJiaVodInitSuccess();
            }
            addPptFragment();
            if (onVideoPlayListener != null) {
                onVideoPlayListener.onJoinSuccess();
            }
            LogUtils.d("onLaunchSuccess.....end");
            errorCount = 0;
        }
    };
    private OnPlayerListener mPlayerListener = new OnPlayerListener() {
        @Override
        public void onPause(BJPlayerView bjPlayerView) {
            LogUtils.d("onPause");
            if (onVideoPlayListener != null)
                onVideoPlayListener.onPlayPause(false);
        }

        @Override
        public void onPlay(BJPlayerView bjPlayerView) {
            mIsFinished = false;
            isPlaying = true;
            LogUtils.d("onPlay");
            if (onVideoPlayListener != null)
                onVideoPlayListener.onPlayResume();

        }

        @Override
        public void onVideoInfoInitialized(BJPlayerView playerView, long duration, HttpException exception) {
            LogUtils.e("wduration", String.valueOf(duration));
            isPlaying = true;
            if (onVideoPlayListener != null) {
                onVideoPlayListener.onRecordVideoInit(0, true, (int) duration * 1000);
            }
        }

        @Override
        public void onError(BJPlayerView playerView, int code) {
            LogUtils.i("onError", String.valueOf(code));
            if (code == -2) return;   //移动网络
            isPlaying = false;
            String errMsg = "";
            switch (code) {
                case -1:
                    if (isLocalVideo)
                        return;
                    errMsg = "无网络连接!";
                    break;
                case 403:
                    errMsg = "视频地址过期!";
                    break;
                case 5101:
                case 5102:
                case 5103:
                    errMsg = "视频数据出错!";
                    break;
                case 500:
                    errMsg = "视频出错，请退出重进";
                    break;
                default:
                    errMsg = "播放异常：" + code;
                    break;
            }

            if (onVideoPlayListener != null) {
                onVideoPlayListener.onCaching(false);
//                onVideoPlayListener.onPlayPause(true);
                onVideoPlayListener.onPlayError(errMsg, code);
                onVideoPlayListener.onPlayErrorInfo(errMsg, code);
            }
        }

        @Override
        public void onUpdatePosition(BJPlayerView playerView, int position) {
            // LogUtils.i("onUpdatePosition", String.valueOf(position));
            currentPlayPosition = position;
            isPlaying = true;
            if (onVideoPlayListener != null) {
                onVideoPlayListener.onSeek(position * 1000);
            }
        }

        @Override
        public void onSeekComplete(BJPlayerView playerView, int position) {
            LogUtils.i("onSeekComplete", String.valueOf(position));
            currentPlayPosition = position;
            isPlaying = playerView.isPlaying();
            if (onVideoPlayListener != null) {
                onVideoPlayListener.onSeek(position * 1000);
            }
        }

        @Override
        public void onSpeedUp(BJPlayerView playerView, float speedUp) {
            LogUtils.i("onSpeedUp", String.valueOf(speedUp));
        }

        @Override
        public void onVideoDefinition(BJPlayerView playerView, int definition) {
            LogUtils.i("onVideoDefinition", String.valueOf(definition));
        }

        @Override
        public void onPlayCompleted(BJPlayerView playerView,
                                    VideoItem item, SectionItem nextSection) {
            LogUtils.i("onPlayCompleted..." + BJFileLog.getLogDirPath());
            isPlaying = false;
            mIsFinished = true;
            if (onVideoPlayListener != null) {
                onVideoPlayListener.onVideoEnd();
            }
            if (mChatMsgList != null) {
                mChatMsgList.mIsNewMessage = false;
            }
        }

        @Override
        public void onVideoPrepared(BJPlayerView playerView) {
            LogUtils.i("onVideoPrepared==" + playerView.getCurrentPosition());
            if (onVideoPlayListener != null)
                onVideoPlayListener.onVideoPrepared(playerView.getCurrentPosition(), playerView.getDuration());
            mIsFinished = false;
        }

    };

    private SimpleOnPlayerViewListener mOnPlayerViewListener = new SimpleOnPlayerViewListener() {
        @Override
        public void onCaton(BJPlayerView bjPlayerView) {
            super.onCaton(bjPlayerView);
            long gapTime = (isLive || (bjPlayerView != null && bjPlayerView.getDuration() < 20 * 60 * 1000) ? 10 : 20) * 60 * 1000;
            if (catonTimes >= 2 && (catonTipTime == 0 || SystemClock.currentThreadTimeMillis() - catonTipTime > gapTime)) {
                if (onVideoPlayListener != null)
                    onVideoPlayListener.onCaton(bjPlayerView);
                catonTimes = 0;
                catonTipTime = SystemClock.currentThreadTimeMillis();
            }
            catonTimes++;
        }
    };

    public BaiJiaVideoPlayerImpl(boolean isLiveType, FragmentManager fm,
                                 VideoPlayer.OnVideoPlayListener listener) {
        super(isLiveType, fm, listener);
        mChatMsgList.baijiaMsg = new ArrayList<>();
        mChatMsgList.playerType = PlayerTypeEnum.BaiJia.getValue();
        mSubscriptions = new ArrayList<>();
        // createPlayer();
    }

    @Override
    public boolean isLivePlay() {
        if (isOfflinePathValid()) return false;
        return isLive;
    }

    @Override
    protected void createPlayer() {
        if (isLive) {
            if (mBjyPlayRoom == null) {
                return;
            }
            if (mBaiJiaLivePlayer == null) {
                mBaiJiaLivePlayer = mBjyPlayRoom.getPlayer();
            }
        }
    }

    @Override
    public void initPlayerViews(FrameLayout pptLayout, FrameLayout videoFather) {
        LogUtils.d("initPlayerViews");
        createPlayer();
        mCurrentPPtViewLayout = mPptViewLayout = pptLayout;
        mVideoFatherLayout = videoFather;
        mVideoViewLayout = videoFather.findViewById(R.id.live_video_view_container_layout);
        initViews();
        isVideoPlay = false;
        setVideoMode(isVideoPlay);
        mVideoViewLayout.setVisibility(View.VISIBLE);
        LogUtils.d("initPlayerViews.....end");
    }

    @Override
    protected void initViews() {
        if (isLive) {
            if (mBaiJiaLiveVideoView == null) {
                mBaiJiaLiveVideoView = ViESurfaceViewRenderer.CreateRenderer(UniApplicationContext.getContext(), true);
                mBaiJiaLiveVideoView.setZOrderMediaOverlay(false);
                mBaiJiaLiveVideoView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            }
        } else {
            if (mBaiJiaBackPlayerView == null) {
                mBaiJiaBackPlayerView = (BJPlayerView) LayoutInflater.from(
                        UniApplicationContext.getContext()).inflate(
                        R.layout.live_video_bai_jia_paly_back_view_layout, null, false);
                //setSurfaceZOrderMediaOverlay(mBaiJiaBackPlayerView.getVideoView(),false);
                mBaiJiaBackPlayerView.enableBrightnessGesture(false);
                mBaiJiaBackPlayerView.enableSeekGesture(false);
                mBaiJiaBackPlayerView.enableVolumeGesture(false);
                mBaiJiaBackPlayerView.setVideoEdgePaddingColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public void initPlayerParams(CourseWareInfo liveCourseInfo) {
        LogUtils.d("initPlayerParams.....start");
        super.initPlayerParams(liveCourseInfo);
        mLiveCourseInfo = liveCourseInfo;
        curLiveTeacherId = "";
        isLanchSuccess = false;
        String name = SpUtils.getNick();
        if (TextUtils.isEmpty(name)) {
            name = SpUtils.getUname();
        }
        if (TextUtils.isEmpty(name)) {
            name = "学员" + System.currentTimeMillis();
        }

        if (onVideoPlayListener != null) {
            onVideoPlayListener.onCaching(true);
        }
        isLocalVideo = false;
        if (isLive) {
            hasVideo = false;
            String sign = liveCourseInfo.sign;
            String roomId = liveCourseInfo.bjyRoomId;
            if (mMustCodePlay || TextUtils.isEmpty(sign) || TextUtils.isEmpty(roomId) || !TextUtils.isDigitsOnly(roomId) || UserInfoUtil.getLiveUserInfo() == null)
                mBjyPlayRoom = LiveSDK.enterRoom(UniApplicationContext.getContext(), liveCourseInfo.joinCode, name, lpLaunchListener);
            else {
                enterLiveRoomBySign(Long.parseLong(roomId), sign, liveCourseInfo.groupId);
            }
        } else {
            hasVideo = true;
            if (mBjPlayBackPresenter == null) {
                mBjPlayBackPresenter = new BaiJiaPlayBackPresenter(mBaiJiaBackPlayerView, onVideoPlayListener);
            }
            mBaiJiaBackPlayerView.setMemoryPlayEnable(true);
            mBaiJiaBackPlayerView.setEnableNetWatcher(false);
            mBaiJiaBackPlayerView.setTopPresenter(mBjPlayBackPresenter);
            mBaiJiaBackPlayerView.setCenterPresenter(mBjPlayBackPresenter);
            mBaiJiaBackPlayerView.setBottomPresenter(mBjPlayBackPresenter);
            mBaiJiaBackPlayerView.setOnPlayerViewListener(mOnPlayerViewListener);
            LogUtils.d("initPlayerParams.....start...enterroom");
            if (!isOfflinePathValid() || TextUtils.isEmpty(liveCourseInfo.offSignalFilePath)) {
                if (TextUtils.isEmpty(liveCourseInfo.bjySessionId)) {
                    mBjyPlayRoom = LivePlaybackSDK.newPlayBackRoom(UniApplicationContext.getContext(),
                            Method.parseLong(liveCourseInfo.bjyRoomId), liveCourseInfo.token);
                } else {
                    //长期房间
                    mBjyPlayRoom = LivePlaybackSDK.newPlayBackRoom(UniApplicationContext.getContext(),
                            Method.parseLong(liveCourseInfo.bjyRoomId),
                            Method.parseLong(liveCourseInfo.bjySessionId), liveCourseInfo.token);
                }

                LogUtils.d("initPlayerParams.....start...enterroom.....online");
            } else {
                isLocalVideo = true;
                mBjyPlayRoom = LivePlaybackSDK.newPlayBackRoom(UniApplicationContext.getContext(), Method.parseLong(liveCourseInfo.bjyRoomId),
                        TextUtils.isEmpty(liveCourseInfo.bjySessionId) ? 0 : Method.parseLong(liveCourseInfo.bjySessionId),
                        offlineFilePath, liveCourseInfo.offSignalFilePath);
                LogUtils.d("initPlayerParams.....start...enterroom.....local");
            }
            ((PBRoom) mBjyPlayRoom).setOnPlayerListener(mPlayerListener);
            ((PBRoom) mBjyPlayRoom).enterRoom(lpLaunchListener);
            ((PBRoom) mBjyPlayRoom).bindPlayerView(mBaiJiaBackPlayerView);
            mBjyPlayRoom.setOnLiveRoomListener(getRoomListener());
//            mBaiJiaBackPlayerView.playVideo(currentPlayPosition/1000);
            LogUtils.d("initPlayerParams.....end");
        }
    }

    @Override
    public ChatMessageListWrap getMsgList() {
        return mChatMsgList;
    }

    @Override
    public void setOnlyTeacherMessage(boolean isOnlyTeacher) {
        if (isLive) return;
        if (mBjyPlayRoom != null) ((PBRoom) mBjyPlayRoom).setMessageModeTAOnly(isOnlyTeacher);

    }

    @Override
    public void refreshPlay() {
        ToastUtils.showShortToast(null, "正在重新连接中..");
        LogUtils.e("refreshPlay", "refreshPlay");
        if (mChatMsgList != null)
            mChatMsgList.mIsNewMessage = false;
        if (onVideoPlayListener != null) onVideoPlayListener.onCaching(true);
        if (isLanchSuccess && mPPtView != null) {
            removePptFragment();
            mBjyPlayRoom.quitRoom();
        }
        initPlayerParams(mLiveCourseInfo);
    }

    @Override
    public void mute() {
        try {
            if (mBjyPlayRoom != null && mBaiJiaLivePlayer != null) {
                mBaiJiaLivePlayer.mute();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void unMute() {
        try {
            if (mBjyPlayRoom != null && mBaiJiaLivePlayer != null) {
                mBaiJiaLivePlayer.unMute();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void pause() {
        isPlaying = false;
        if (mBaiJiaLivePlayer != null && !TextUtils.isEmpty(curLiveTeacherId)
                && mBaiJiaLivePlayer.isVideoPlaying(curLiveTeacherId)) {
            mBaiJiaLivePlayer.playAVClose(curLiveTeacherId);
        }
        if (mBjPlayBackPresenter != null) {
            mBjPlayBackPresenter.stopVideo();
        }
    }

    @Override
    public void resume() {
        if (mBaiJiaLivePlayer != null && !TextUtils.isEmpty(curLiveTeacherId)
                && !mBaiJiaLivePlayer.isVideoPlaying(curLiveTeacherId)
                && mBaiJiaLiveVideoView != null) {
            mBaiJiaLivePlayer.playVideo(curLiveTeacherId, mBaiJiaLiveVideoView);
        }
        if (mBjPlayBackPresenter != null) {
            mBjPlayBackPresenter.startVideo();
            // if(!isLive) setSurfaceZOrderMediaOverlay(); mBaiJiaLiveVideoView
        }
    }

    @Override
    public void replay() {
        if (!isLive && mBjPlayBackPresenter != null) {
            if (mIsFinished)
                currentPlayPosition = 0;
            mBjPlayBackPresenter.startVideo();
        }
    }

    @Override
    public void stop() {
        isPlaying = false;
        if (mBaiJiaLivePlayer != null && !TextUtils.isEmpty(curLiveTeacherId)
                && mBaiJiaLivePlayer.isVideoPlaying(curLiveTeacherId)) {
            mBaiJiaLivePlayer.playAVClose(curLiveTeacherId);
        }
        if (mBjPlayBackPresenter != null) {
            mBjPlayBackPresenter.stopVideo();
        }
    }

    @Override
    public void releasePlayer() {
        RxUtils.unsubscribeIfNotNull(msubscriptionOfTeacherMedia);
        for (Subscription subscription : mSubscriptions) {
            if (subscription != null && !subscription.isUnsubscribed())
                subscription.unsubscribe();
        }
        // UniApplicationLike.getApplicationHandler().removeCallbacks(pptRunnable);
        onVideoPlayListener = null;
        isPlaying = false;
        if (mBaiJiaLivePlayer != null) {
            mBaiJiaLivePlayer.removePlayerListener(onLPPlayerListener);
        }

        if (mBjyPlayRoom != null) {
            mBjyPlayRoom.setOnLiveRoomListener(null);
            mBjyPlayRoom.quitRoom();
            LogUtils.d("quitRoom");
            mBjyPlayRoom = null;
        }

        if (mBjPlayBackPresenter != null) {
            mBjPlayBackPresenter.release();
        }
        removePptFragment();

//        this.mPptViewLayout.removeAllViews();
//        this.mVideoViewLayout.removeAllViews();
//
//        this.mVideoViewLayout = null;
//        this.mPptViewLayout = null;
//        this.mVideoFatherLayout = null;
    }

    @Override
    public void seekTo(int time) {
        LogUtils.i("seekTo: " + time);
        if (mBaiJiaBackPlayerView != null) {
            if (mIsFinished)
                mBaiJiaBackPlayerView.playVideo(time / 1000);
            else
                mBaiJiaBackPlayerView.seekVideo(time / 1000);
        }
        if (mChatMsgList != null && mChatMsgList.baijiaMsg != null && mChatMsgList.baijiaMsg.size() > 0)
            mChatMsgList.mIsNewMessage = false;
    }

    @Override
    public void setSpeed(int speed) {
        if (mBjPlayBackPresenter != null) {
            mBjPlayBackPresenter.setSpeed(bjPlaySpeedLists[speed]);
        }
    }

    @Override
    public boolean sendChatMsg(String cText, String rText, boolean isEmoji) {
        boolean flag = super.sendChatMsg(cText, rText, isEmoji);
        if (flag) {
            if (TextUtils.isEmpty(cText)) {
                ToastUtils.showShort("消息不能为空");
                return false;
            }
            try {
                ChatVM chatVM = mBjyPlayRoom.getChatVM();
                if (chatVM != null && isLanchSuccess) {
                    if (isEmoji) {
                        chatVM.sendEmojiMessage("[" + cText + "]");
                    } else {
                        chatVM.sendMessage(cText);
                    }
                }
            } catch (Exception e) {
            }
        }
        return true;
    }

    @Override
    public void setPortraitMode(boolean isPort) {
//        super.setPortraitMode(isPort);   isPortrait = isPort;   setVideoMode(isVideoPlay);
        isPortrait = isPort;
      /*  if(isPort&&lppptFragment!=null&&isVideoPlay){
            UniApplicationLike.getApplicationHandler().removeCallbacks(pptRunnable);
            UniApplicationLike.getApplicationHandler().postDelayed(pptRunnable, 2000);
        }*/
    }

    //isVideoPlay 是否视频在ppt显示区域
    @Override
    public void changeVideoMode() {  //setVideoMode(!isVideoPlay);

        boolean isVideo = !isVideoPlay;
        if (!hasVideo && !isVideoPlay && isVideo) {
            return;
        }

        isVideoPlay = isVideo;
        //removePptFragment();
        switchPPTAndVideo();
    }

    @Override
    public void setVideoMode(boolean isVideo) {

        LogUtils.i("hasVideo:" + hasVideo + ", isVideoPlay:" + isVideoPlay + ", isVideo:" + isVideo
                + ", mVideoFatherLayoutVISIBLE:" + (mVideoFatherLayout.getVisibility() == View.VISIBLE));
        if (!hasVideo && !isVideoPlay && isVideo) {
            return;
        }

        isVideoPlay = isVideo;
        removePptFragment();
        mPptViewLayout.removeAllViews();
        mVideoViewLayout.removeAllViews();
        mCurrentPPtViewLayout = mPptViewLayout;
        pptViewContainerId = mPptViewLayout.getId();
        mVideoViewLayout.addView(isLive ? mBaiJiaLiveVideoView : mBaiJiaBackPlayerView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void removePptFragment() {
//        if (isLive) {
            if (mPPtView != null) {
                mPPtView.onDestroy();
                mPPtView.removeAllViews();
                mPPtView = null;
            }
//        } else
//            super.removePptFragment();
    }

    @Override
    public void setQuality(int index) {
        if (mBjPlayBackPresenter != null)
            mBjPlayBackPresenter.setVideoDefinition(index);
    }

    @Override
    public void addPptFragment() {
//        if (isLive) {
            if (mPPtView == null || mBjyPlayRoom == null || mPPtView.getParent() != null)
                return;
            mPPtView.attachLiveRoom(mBjyPlayRoom);
            mCurrentPPtViewLayout.addView(mPPtView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mPPtView.setAnimPPTEnable(true);
            mPPtView.setFlingEnable(true);
            mPPtView.changeTouchAble(false);
            mPptViewLayout.setBackgroundColor(Color.WHITE);
            mPPtView.hidePageView();
            mPPtView.setPPTShowWay(LPConstants.LPPPTShowWay.SHOW_COVERED);
//        } else {
//            if (lppptFragment == null || isPptFrgmAdded || lppptFragment.isAdded())
//                return;
//            LogUtils.i("isPptFrgmAdded:" + isPptFrgmAdded + ", Added:" + lppptFragment.isAdded()
//                    + ", Detached:" + lppptFragment.isDetached()
//                    + ", Hide:" + lppptFragment.isHidden() + ", Resumed:" + lppptFragment.isResumed()
//                    + ", Removing:" + lppptFragment.isRemoving() + ", Visible:" + lppptFragment.isVisible());
//            try {
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.add(pptViewContainerId, lppptFragment, mPPtFragmentFlag)
//                        // .addToBackStack(lppptFragment.getClass().getSimpleName())
//                        .commitAllowingStateLoss();
//                isPptFrgmAdded = true;
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
    }

    public void onBaiJiaLiveInitSuccess() {
        List<IExpressionModel> expressionModels = mBjyPlayRoom.getChatVM().getExpressions();
        List<LiveChatExpressBean> expressList = new ArrayList<>();
        if (!Method.isListEmpty(expressionModels)) {
            for (int i = 0; i < expressionModels.size(); i++) {
                LiveChatExpressBean bean = new LiveChatExpressBean();
                bean.playerType = 1;
                bean.key = expressionModels.get(i).getKey();
                bean.name = expressionModels.get(i).getName();
                bean.nameEn = expressionModels.get(i).getNameEn();
                bean.imgUrl = expressionModels.get(i).getUrl();
                expressList.add(bean);
            }
        }
        // LogUtils.e("onBaiJiaLiveInitSuccess"+ GsonUtil.GsonString(expressList));
        if (onVideoPlayListener != null) {
            onVideoPlayListener.onExpressUpdate(String.valueOf(1), expressList);
        }
        mBaiJiaLivePlayer = mBjyPlayRoom.getPlayer();
        mPPtView = new MyPPtView(mPptViewLayout.getContext());
        // lppptFragment.setLiveRoom(mBjyPlayRoom);

        isPlaying = true;
        if (onVideoPlayListener != null) {
            onVideoPlayListener.onCaching(false);
        }
        if (onLPPlayerListener == null) {
            onLPPlayerListener = new LPPlayerListener() {
                @Override
                public void onReadyToPlay(int i) {
                    LogUtils.i("onReadyToPlay: " + i);
                    if (onVideoPlayListener != null) {
                        onVideoPlayListener.onCaching(false);
                    }
                }

                @Override
                public void onPlayAudioSuccess(int i) {
                    LogUtils.i("onPlayAudioSuccess: " + i);
                }

                @Override
                public void onPlayVideoSuccess(int i) {
                    LogUtils.i("onPlayVideoSuccess: " + i);
                }

                @Override
                public void onPlayClose(int i) {
                    LogUtils.i("onPlayClose: " + i);
                }
            };
        }
        mBaiJiaLivePlayer.addPlayerListener(onLPPlayerListener);
        Subscription loginSub = mBjyPlayRoom.getObservableOfLoginConflict().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ILoginConflictModel>() {
                    @Override
                    public void call(ILoginConflictModel iLoginConflictModel) {
                        ToastUtils.showShort("您已经被踢下线");
                        isPlaying = false;
                        stop();
                        if (onVideoPlayListener != null) {
                            onVideoPlayListener.onJoinFailed("您已经被踢下线", true);
                        }
                    }
                });
        mSubscriptions.add(loginSub);
        if (mBjyPlayRoom.getSpeakQueueVM() != null) {
            mBjyPlayRoom.getSpeakQueueVM().requestActiveUsers();
            // 进入房间首次获取发言队列
            Subscription speakActiveUser = mBjyPlayRoom.getSpeakQueueVM().getObservableOfActiveUsers()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<IMediaModel>>() {
                        @Override
                        public void call(List<IMediaModel> iMediaModels) {
                            if (iMediaModels != null) {
                                LogUtils.i("IMediaModel call: " + iMediaModels.size());
                                if (ArrayUtils.isEmpty(iMediaModels)) {
                                    setLiveVideoViewState(false, null);
                                    return;
                                }
                                //添加助教的暖场判断
                                if (iMediaModels.size() == 1 && iMediaModels.get(0).getUser().getType() == LPConstants.LPUserType.Assistant) {
                                    IMediaModel tmpMedia = iMediaModels.get(0);
                                    // if (tmpMedia.isVideoOn()) {
                                    setLiveVideoViewState(tmpMedia.isVideoOn(), tmpMedia);
                                    return;
                                    // }
                                }
                                for (IMediaModel model : iMediaModels) {
                                    if (model != null && model.getUser() != null) {
                                        LogUtils.i("IMediaModel: " + model.getUser().getUserId()
                                                + ", " + model.getUser().getName()
                                                + ", isVideoOn:" + model.isVideoOn());
                                        if (model.getUser().getType() == LPConstants.LPUserType.Teacher) {
                                            setLiveVideoViewState(model.isVideoOn(), model);
                                            if (mBaiJiaLivePlayer != null) {
                                                if (!CommonUtils.isEmpty(curLiveTeacherId)) {
                                                    mBaiJiaLivePlayer.playAVClose(curLiveTeacherId);
                                                    mBaiJiaLivePlayer.playAudio(curLiveTeacherId);
                                                }
                                                if (CommonUtils.isEmpty(curLiveTeacherId))
                                                    curLiveTeacherId = model.getUser().getUserId();
                                                mBaiJiaLivePlayer.playVideo(curLiveTeacherId, mBaiJiaLiveVideoView);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    });
            mSubscriptions.add(speakActiveUser);

            Subscription speakPresent = mBjyPlayRoom.getSpeakQueueVM().getObservableOfPresenterChange().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    if (mBjyPlayRoom == null)
                        return;
                    if (mBjyPlayRoom.getPresenterUser() != null)
                        mLiveUserType = mBjyPlayRoom.getPresenterUser().getType();
                    if (CommonUtils.isEmpty(s) || TextUtils.equals(s, curLiveTeacherId)) return;
                    if (!CommonUtils.isEmpty(curLiveTeacherId)) {
                        mBaiJiaLivePlayer.playAVClose(curLiveTeacherId);
                        mBaiJiaLivePlayer.playAudio(curLiveTeacherId);
                    }
                    curLiveTeacherId = s;
                    mBaiJiaLivePlayer.playVideo(s, mBaiJiaLiveVideoView);
                }
            });
            mSubscriptions.add(speakPresent);
        }
        // 收到聊天消息8dwjdf
        if (mBjyPlayRoom.getChatVM() != null) {
            Subscription messageListSub = mBjyPlayRoom.getChatVM().getObservableOfNotifyDataChange().onBackpressureBuffer(1000)
                    .subscribe(new Action1<List<IMessageModel>>() {
                        @Override
                        public void call(List<IMessageModel> msgList) {
                            LogUtils.i("get chat message list: " + (msgList == null ? 0 : msgList.size()));
                            LogUtils.i("聊天消息改变");
                            if (null == mBjyPlayRoom || mChatMsgList.mIsClearAll) return;
                            ChatVM chatVM = mBjyPlayRoom.getChatVM();
                            if (chatVM == null || mChatMsgList.mIsNewMessage) {
                                return;
                            }
//                            mChatMsgList.addTeacherMsgs(msgList);
                            mChatMsgList.baijiaMsg.clear();
                            mChatMsgList.baijiaMsg.addAll(msgList);
                            if (onVideoPlayListener != null) {
                                onVideoPlayListener.onChatWithPublic();
                            }
                            mChatMsgList.mIsNewMessage = true;
                        }
                    });
            mSubscriptions.add(messageListSub);
            Subscription chatSub = mBjyPlayRoom.getChatVM().getObservableOfReceiveMessage().onBackpressureBuffer(1000).subscribe(new Action1<IMessageModel>() {
                @Override
                public void call(IMessageModel iMessageModel) {
                    LogUtils.i("聊天消息改变:::");
                    if (iMessageModel != null)
//                        mChatMsgList.addTeacherMsg(iMessageModel);
                        if (mChatMsgList.baijiaMsg != null && (mChatMsgList.mIsClearAll || mChatMsgList.mIsNewMessage) && !mChatMsgList.isContains(iMessageModel)) {
                            mChatMsgList.baijiaMsg.add(iMessageModel);
                            mChatMsgList.mIsNewMessage = true;
                            if (onVideoPlayListener != null) {
                                onVideoPlayListener.onChatWithPublic();
                            }
                        }
                }
            });
            mSubscriptions.add(chatSub);
        }

        // 全体禁言
        Subscription forbidAllChatSub = mBjyPlayRoom.getObservableOfForbidAllChatStatus().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                LogUtils.i("getObservableOfForbidAllChatStatus: " + aBoolean);
                isRootMuted = aBoolean;
            }
        });
        mSubscriptions.add(forbidAllChatSub);
        Subscription selfChatForbid = mBjyPlayRoom.getObservableOfIsSelfChatForbid().subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isChatForbid) {
                LogUtils.i("getObservableOfIsSelfChatForbid: " + isChatForbid);
                isSendEnable = !isChatForbid;
            }
        });
        mSubscriptions.add(selfChatForbid);
        Subscription classStartSub = mBjyPlayRoom.getObservableOfClassStart().subscribe(new LPErrorPrintSubscriber<Void>() {
            @Override
            public void call(Void aVoid) {
                LogUtils.i("上课了");
                isPlaying = true;
            }
        });
        mSubscriptions.add(classStartSub);
        Subscription classEndSub = mBjyPlayRoom.getObservableOfClassEnd().subscribe(new LPErrorPrintSubscriber<Void>() {
            @Override
            public void call(Void aVoid) {
                LogUtils.i("下课了");
                ToastUtils.showShort("直播已结束，等待回放上传");
                isPlaying = false;
                mBaiJiaLivePlayer.playAVClose(curLiveTeacherId);
                hasVideo = false;
                if (onVideoPlayListener != null) {
                    onVideoPlayListener.onVideoEnd();
                }
            }
        });
        mSubscriptions.add(classEndSub);
        //ToastUtils.showShort("您已经被踢下线");
        // error 回调
        mBjyPlayRoom.setOnLiveRoomListener(getRoomListener());

        //参考BJLiveUi  GlobalPresenter.java
        if (mBjyPlayRoom.getCurrentUser().getType() != LPConstants.LPUserType.Teacher) {
            // 学生监听老师音视频状态
            msubscriptionOfTeacherMedia = mBjyPlayRoom.getSpeakQueueVM().getObservableOfMediaNew()
                    .mergeWith(mBjyPlayRoom.getSpeakQueueVM().getObservableOfMediaChange())
                    .mergeWith(mBjyPlayRoom.getSpeakQueueVM().getObservableOfMediaClose())
                    .filter(new Func1<IMediaModel, Boolean>() {
                        @Override
                        public Boolean call(IMediaModel iMediaModel) {
                            LogUtils.i("IMediaModel1: " + iMediaModel.getUser().getUserId()
                                    + ", " + iMediaModel.getUser().getName()
                                    + ", isVideoOn:" + iMediaModel.isVideoOn());
                            return !isTeacherOrAssistant() && ((mLiveUserType == null && iMediaModel.getUser().getType() != LPConstants.LPUserType.Student) || iMediaModel.getUser().getType() == mLiveUserType);
                        }
                    })
                    .throttleLast(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<IMediaModel>() {
                        @Override
                        public void call(IMediaModel iMediaModel) {

                            LogUtils.i("IMediaModel2: " + iMediaModel.getUser().getUserId()
                                    + ", " + iMediaModel.getUser().getName()
                                    + ", isVideoOn:" + iMediaModel.isVideoOn());
                            if (!mBjyPlayRoom.isClassStarted()) {
                                return;
                            }
                            LogUtils.i("IMediaModel3: " + iMediaModel.getUser().getUserId()
                                    + ", " + iMediaModel.getUser().getName()
                                    + ", isVideoOn:" + iMediaModel.isVideoOn());

                            if (iMediaModel.isVideoOn() && iMediaModel.isAudioOn()) {
                                if (!teacherVideoOn) {
                                    //routerListener.showMessageTeacherOpenAV();
                                    ToastUtils.showShort("老师打开了摄像头");
                                } else
                                    ToastUtils.showShort("老师打开了麦克风");
                            } else if (iMediaModel.isVideoOn()) {
                                if (teacherAudioOn && teacherVideoOn) {
                                    ToastUtils.showShort("老师关闭了麦克风");
                                    // routerListener.showMessageTeacherCloseAudio();
                                } else if (!teacherVideoOn) {
                                    ToastUtils.showShort("老师打开了摄像头");
                                    // routerListener.showMessageTeacherOpenVideo();
                                    //mVideoViewLayout.addView(isLive ? mBaiJiaLiveVideoView : mBaiJiaBackPlayerView , new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                }
                            } else if (iMediaModel.isAudioOn()) {
                                if (teacherAudioOn && teacherVideoOn) {
                                    ToastUtils.showShort("老师关闭了摄像头");
                                    // mVideoViewLayout.removeAllViews();
                                    // routerListener.showMessageTeacherCloseVideo();
                                } else if (!teacherAudioOn) {
                                    ToastUtils.showShort("老师打开了麦克风");
                                    //routerListener.showMessageTeacherOpenAudio();

                                }
                            } else {
                                //routerListener.showMessageTeacherCloseAV();
                                ToastUtils.showShort("老师关闭了麦克风和摄像头");

                            }
                            if (mBjyPlayRoom.getPresenterUser() != null)
                                mLiveUserType = mBjyPlayRoom.getPresenterUser().getType();
                            setLiveVideoViewState(iMediaModel.isVideoOn(), iMediaModel);
                            //setTeacherMedia(iMediaModel);
                            teacherVideoOn = hasVideo = iMediaModel.isVideoOn();
                            teacherAudioOn = iMediaModel.isAudioOn();
                        }
                    });
        }

        AudioManager mAudioManager = (AudioManager) UniApplicationContext.getContext().getSystemService(Context.AUDIO_SERVICE);
        int current;
        if (mAudioManager != null) {
            int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            int curMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);//得到听筒模式的最大值MaxVolume=
            mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, -1, 0);
            int minVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currentVolume, 0);
            current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (current <= minVolume) {
                int divide = curMaxVolume > 10 ? 10 : 5;
                int tmpVolume = curMaxVolume * 2 / divide;
                LogUtils.e("AudioManager", tmpVolume + "," + curMaxVolume);
                if (tmpVolume > 0)
                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, tmpVolume, 0);
                else mBaiJiaLivePlayer.mute();//静音

            }
        }
        getBjyBraodcast();
    }

    public void setCourseInfo(CourseWareInfo courseInfo) {
        this.mLiveCourseInfo = courseInfo;
    }

    /**
     * 学生发送答案
     *
     * @param answer [A, B ,C] 数组元素是 option 下 key
     */
    public void sendAnswer(List<String> answer) {
        if (mBjyPlayRoom != null)
            mBjyPlayRoom.submitAnswerSheet(answer);
    }

    public void showVideo(boolean showVideo) {
        View view = isLive ? mBaiJiaLiveVideoView : mBaiJiaBackPlayerView;
        if (mVideoViewLayout == null || view == null)
            return;
        LogUtils.d("videoshow", "showVideo==" + showVideo + "===hasVideo===" + hasVideo);
        if (isVideoPlay) {
            View pptView = mPPtView;//isLive ? mPPtView : lppptFragment == null ? null : lppptFragment.getView();
            if (pptView == null) return;
            if (!showVideo)
                mVideoViewLayout.removeView(pptView);
            else if (!isShowVideo || mVideoViewLayout.indexOfChild(pptView) == -1) {
//                if (isLive) {
                    addPptView(pptView, mVideoViewLayout);
//                } else {
//                    lppptFragment.onStop();
//                    addPptView(pptView, mVideoViewLayout);
//                    lppptFragment.onStart();
//                }
            }
        }

        ViewGroup parentView = isVideoPlay ? mPptViewLayout : mVideoViewLayout;
        if (!isVideoPlay && (!showVideo || !hasVideo))
            parentView.removeView(view);
        else if (hasVideo) {
            boolean isAdd = false;
            if (view.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                isAdd = viewGroup.indexOfChild(view) != -1;
            }
            if (!isAdd)
                parentView.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }

        isShowVideo = showVideo;
    }

    //替换父控件
    public void setVideoContainer(FrameLayout videoContainer) {
        try {
            ViewGroup viewGroup = (ViewGroup) mVideoViewLayout.getParent();
            if (videoContainer == null)
                return;
            if (viewGroup != null && (viewGroup.getId() == videoContainer.getId()))
                return;
            if (viewGroup != null) viewGroup.removeView(mVideoViewLayout);
            FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            videoContainer.addView(mVideoViewLayout, 0, videoParams);
            showVideo(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enterLiveRoomBySign(final long roomId, String sign, int groupId) {
        LPConstants.LPUserType userType = LPConstants.LPUserType.Student;
        LiveUserInfo userInfo = UserInfoUtil.getLiveUserInfo();
        if (null != userInfo)
            mBjyPlayRoom = LiveSDK.enterRoom(UniApplicationContext.getContext(), roomId, groupId, userInfo.userNumber, userInfo.userNick, userType, userInfo.userAvatar, sign, lpLaunchListener);
        else {
            if (onVideoPlayListener != null)
                onVideoPlayListener.onPlayErrorInfo("签名出错roomId:" + roomId, 1020);
        }

    }

    private void onBaiJiaVodInitSuccess() {
        mPPtView = new MyPPtView(mPptViewLayout.getContext());
        Subscription subscriptionVideoOn = ((PBRoom) mBjyPlayRoom).getObservableOfVideoStatus().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Boolean>() {
                    @Override
                    public void call(Boolean isVideoOn) {
                        LogUtils.i(isVideoOn ? "视频开启" : "视频关闭");
                        hasVideo = isVideoOn;
                        showVideo(isShowVideo);
                    }
                });
        mSubscriptions.add(subscriptionVideoOn);
        Subscription subscriptionChatList = mBjyPlayRoom.getChatVM().getObservableOfNotifyDataChange()
                .onBackpressureBuffer(1000)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<IMessageModel>>() {
                    @Override
                    public void call(List<IMessageModel> iMessageModels) {
                        if (null == mBjyPlayRoom) return;
                        ChatVM chatVM = mBjyPlayRoom.getChatVM();
                        if (chatVM == null || iMessageModels == null || mChatMsgList.mIsNewMessage) {
                            return;
                        }
                        List<IMessageModel> models = new ArrayList<>(iMessageModels);
                        mChatMsgList.baijiaMsg.clear();
                        mChatMsgList.baijiaMsg.addAll(models);
//                        mChatMsgList.addTeacherMsgs(models);
                        if (onVideoPlayListener != null) {
                            onVideoPlayListener.onChatWithPublic();
                        }
                        if (models.size() > 1)
                            mChatMsgList.mIsNewMessage = true;
                    }

                });
        mSubscriptions.add(subscriptionChatList);
        Subscription subscriptionChat = mBjyPlayRoom.getChatVM().getObservableOfReceiveMessage()
                .onBackpressureBuffer(1000)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IMessageModel>() {
                    @Override
                    public void call(IMessageModel iMessageModel) {
                        if (iMessageModel != null) {
//                            mChatMsgList.addTeacherMsg(iMessageModel);
                            if (mChatMsgList.baijiaMsg != null && !mChatMsgList.isContains(iMessageModel) && mChatMsgList.mIsNewMessage) {
                                mChatMsgList.baijiaMsg.add(iMessageModel);
                                mChatMsgList.mIsNewMessage = true;
                                if (onVideoPlayListener != null) {
                                    onVideoPlayListener.onChatWithPublic();
                                }
                            }
                        }
                    }
                });
        mSubscriptions.add(subscriptionChat);
    }

    private boolean isTeacherOrAssistant() {
        if (mBjyPlayRoom == null) return false;
        return mBjyPlayRoom.getCurrentUser().getType() == LPConstants.LPUserType.Teacher ||
                mBjyPlayRoom.getCurrentUser().getType() == LPConstants.LPUserType.Assistant;
    }

    private OnLiveRoomListener getRoomListener() {
        return new OnLiveRoomListener() {
            @Override
            public void onError(LPError error) {
                if (null == error) {
                    return;
                }
                LogUtils.e("error", error.getCode() + "," + error.getMessage());
                int code = (int) error.getCode();
                String errMsg = error.getMessage();
                switch (code) {
                    case LPError.CODE_WARNING_PLAYER_LAG:
                        if (mOnPlayerViewListener != null)
                            mOnPlayerViewListener.onCaton(null);
                        break;
                    case LPError.CODE_ERROR_ROOMSERVER_LOSE_CONNECTION:
                        if (onVideoPlayListener != null) {
                            boolean isFront = onVideoPlayListener.getActivityRunningFront();
                            if (isFront) refreshPlay();
                            else
                                onVideoPlayListener.onJoinFailed(errMsg + ":" + code, true);
                        }
                        break;
                    case LPError.CODE_ERROR_NETWORK_FAILURE:
                        if (isLocalVideo) return;
                    case LPError.CODE_ERROR_LOGIN_CONFLICT:
                    case LPError.CODE_ERROR_ROOMSERVER_FAILED:
                    case LPError.CODE_ERROR_ENTER_ROOM_FORBIDDEN:
                    case LPError.CODE_ERROR_PERMISSION_DENY:
                        if (onVideoPlayListener != null)
                            onVideoPlayListener.onJoinFailed(errMsg + ":" + code, true);
                        break;
                    case 1002:
                        if (!mMustCodePlay) {
                            mMustCodePlay = true;
                            refreshPlay();
                        } else if (onVideoPlayListener != null)
                            onVideoPlayListener.onJoinFailed(errMsg + ":" + code, true);
                        break;
                    default:
                        try {
                            if (!TextUtils.isEmpty(error.getMessage()))
                                ToastUtils.showShort(error.getMessage() + ":" + code);
                        } catch (Exception e) {
                        }
                        break;
                }
                if (onVideoPlayListener != null)
                    onVideoPlayListener.onPlayErrorInfo(errMsg, code);
            }
        };
    }

    private String getAsString(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            return jsonObject.get(key).getAsString();
        }
        return "";
    }

    private void getBjyBraodcast() {
        if (mBjyPlayRoom == null || mBjyPlayRoom.getObservableOfBroadcast() == null)
            return;
        final String params = "&class_id=" + mBjyPlayRoom.getRoomId();// + "&user_number=" + UserInfoUtil.liveUserInfo.userNumber + "&user_name=" + UserInfoUtil.liveUserInfo.userNick;
        Subscription broadcast = mBjyPlayRoom.getObservableOfBroadcast().subscribe(new Action1<LPKVModel>() {
            @Override
            public void call(LPKVModel lpkvModel) {
                try {
                    if (!TextUtils.equals("custom_webpage", lpkvModel.key))
                        return;
                    JsonObject jsonObject = (JsonObject) lpkvModel.value;
                    String action = getAsString(jsonObject, "action");
                    String value = getAsString(jsonObject, "url");
                    if (onVideoPlayListener != null) {
                        if (TextUtils.equals("student_close_webpage", action))
                            onVideoPlayListener.onTestOptions(value + params, true);
                        else if (TextUtils.equals("student_open_webpage", action))
                            onVideoPlayListener.onTestOptions(value + params, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mSubscriptions.add(broadcast);
        if (mBjyPlayRoom.getObservableOfBroadcastCache() == null)
            return;
    }

    /**
     * 只有助教进入，presenteruser可能为null
     *
     * @param isVideoOn
     * @param model
     */
    private void setLiveVideoViewState(boolean isVideoOn, IMediaModel model) {
        if (isVideoOn && model != null) {
            if (CommonUtils.isEmpty(curLiveTeacherId)) {
                IUserModel userModel = mBjyPlayRoom.getPresenterUser() == null ? model.getUser() : mBjyPlayRoom.getPresenterUser();
                curLiveTeacherId = userModel.getUserId();
                mLiveUserType = userModel.getType();
            }
            mBaiJiaLivePlayer.playVideo(curLiveTeacherId, mBaiJiaLiveVideoView);
        }
        hasVideo = isVideoOn;
        showVideo(isShowVideo);
    }

    //    private void clearOldFragment() {
//        if (isLive)
//            return;
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        boolean doCommit = false;
//        Fragment oldfragment = fragmentManager.findFragmentByTag(mPPtFragmentFlag);
//        if (oldfragment != null) {
//            transaction.remove(oldfragment);
//            doCommit = true;
//        }
//        try {
//            if (doCommit) transaction.commitAllowingStateLoss();//commitNow();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void setSurfaceZOrderMediaOverlay(View view, boolean isZOrder) {
//        if (view instanceof SurfaceView) {
//            ((SurfaceView) view).setZOrderOnTop(isZOrder);
//            ((SurfaceView) view).getHolder().setFormat(PixelFormat.TRANSLUCENT);
////            ((SurfaceView) view).setZOrderMediaOverlay(isZOrder);
////            view.setBackgroundColor(Color.WHITE);
//        } else if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                setSurfaceZOrderMediaOverlay(((ViewGroup) view).getChildAt(i), isZOrder);
//            }
//        }
//    }

    private void addPptView(View pptView, FrameLayout parentView) {
        if (pptView == null || parentView == null || parentView.indexOfChild(pptView) != -1)
            return;
        if (pptView.getParent() != null && parentView.getParent() instanceof ViewGroup) {
            ((ViewGroup) pptView.getParent()).removeView(pptView);
        }
        parentView.addView(pptView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

    }

    private void switchPPTAndVideo() {
//        if (!isLive && (null == lppptFragment || lppptFragment.isDetached())) return;
        if (isLive && mPPtView == null) return;
        View pptView = mPPtView; //isLive ? mPPtView : lppptFragment.getView();
        if (pptView == null) return;
//        if (null != lppptFragment) lppptFragment.onStop();
        mPptViewLayout.removeAllViews();
        mVideoViewLayout.removeAllViews();
        mVideoFatherLayout.setVisibility(View.VISIBLE);
        mCurrentPPtViewLayout = isVideoPlay ? mVideoViewLayout : mPptViewLayout;
        pptViewContainerId = mCurrentPPtViewLayout.getId();
        addPptView(pptView, isVideoPlay ? mVideoViewLayout : mPptViewLayout);
//        if (null != lppptFragment) lppptFragment.onStart();
        if (hasVideo && isShowVideo) {
            ViewGroup parentView = isVideoPlay ? mPptViewLayout : mVideoViewLayout;
            parentView.addView(isLive ? mBaiJiaLiveVideoView : mBaiJiaBackPlayerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
        }
//        setSurfaceZOrderMediaOverlay(pptView, true);
    }

    //    public static class MyLPPPTFragment extends LPPPTFragment {
//        boolean isFirstSet = true;
//        private int pptSize = 0;
//        private int currentPPtPage = 0;
//        private OnVideoPlayListener videoPlayListener;
//        boolean isLive = false;
//
//        public MyLPPPTFragment() {
//            super();
//            LogUtils.d("BaiJiaVideoPlayerImpl", "==initPPt==");
//        }
//
//        public void setIsLive(boolean islive) {
//            this.isLive = islive;
//        }
//
//        @Override
//        public void onStart() {
//            super.onStart();
////            if (isFirstSet && !isLive) {
////                isFirstSet = false;
////            mWhiteBoardView.setZOrderMediaOverlay(isVideoPlay);
////                LogUtils.d("BaiJiaVideoPlayerImpl", "==onStart==");
////            }
////            if (!isLive)
////                mWhiteBoardView.setBackgroundColor(Color.WHITE);
//
//        }
//
//        @Override
//        public void initDocList(List list) {
//            super.initDocList(list);
//            pptSize = list == null ? 0 : list.size();
//            if (videoPlayListener != null)
//                videoPlayListener.onPPtPageChange(currentPPtPage, pptSize);
//        }
//
//        @Override
//        public void updatePage(int i, boolean b) {
//            super.updatePage(i, b);
//            if (currentPPtPage != i && videoPlayListener != null) {
//                currentPPtPage = i;
//                videoPlayListener.onPPtPageChange(currentPPtPage, pptSize);
//                LogUtils.d("updatePage", pptSize + "==updatePage==" + i);
//            }
//        }
//    }

    class MyPPtView extends PPTView {
        int totalPages = 0;
        private int currentPPtPage = 0;
        public MyPPtView(Context context) {
            super(context);
            setBackgroundColor(Color.WHITE);
        }

        @Override
        public void updatePage(int i, boolean b) {
            super.updatePage(i, b);
            if (currentPPtPage != i && onVideoPlayListener != null) {
                currentPPtPage = i;
                onVideoPlayListener.onPPtPageChange(currentPPtPage, totalPages);
                LogUtils.d("updatePage", totalPages + "==updatePage==" + i);
            }
        }

        @Override
        public void initDocList(List<LPDocListViewModel.DocModel> list) {
            super.initDocList(list);
            if(list != null) totalPages = list.size();
            if (onVideoPlayListener != null)
                onVideoPlayListener.onPPtPageChange(currentPPtPage, totalPages);
        }
    }

}
