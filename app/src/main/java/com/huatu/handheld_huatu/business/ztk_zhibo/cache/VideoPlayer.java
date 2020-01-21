package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baijiahulian.livecore.models.imodels.IPreviousSurveyModel;
import com.baijiahulian.livecore.ppt.LPPPTFragment;
import com.baijiahulian.player.BJPlayerView;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.ChatMessageListWrap;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.LiveChatExpressBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.BaiJiaVideoPlayerImpl;

import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2017/9/18.
 */

public abstract class VideoPlayer {
    protected FragmentManager fragmentManager;
    protected FrameLayout mPptViewLayout;
    protected FrameLayout mVideoFatherLayout;
    protected FrameLayout mVideoViewLayout;
   // protected ImageView btnVideoViewClose;
   // protected GSDocViewGx mGlDocView;
   // protected GSVideoView mGSVideoView;

//    protected LPPPTFragment lppptFragment;
    protected VideoPlayer.OnVideoPlayListener onVideoPlayListener;

    protected int pptViewContainerId;
    protected String chatText;
    protected String richText;
    protected boolean isLive = true;
    protected boolean isPortrait = true;
    protected String offlineFilePath;
    protected String offSignalFilePath;
    protected boolean isRootMuted = false;
    protected boolean isSendEnable = true;
 //   protected final List<UserInfo> userInfoList = new ArrayList();
    protected boolean hasVideo = true;//直播视频是否打开
    protected boolean isPlaying;
   // protected List<ChatMessageListWrap> chatMsgList = new ArrayList<>();
    protected ChatMessageListWrap mChatMsgList=new ChatMessageListWrap();

    public boolean isVideoPlay = false;//是否视频在ppt显示区域
    public int currentPlayPosition = 0;
    protected boolean isPptFrgmAdded = false;

    public VideoPlayer(boolean isLiveType, FragmentManager fm,
                       VideoPlayer.OnVideoPlayListener listener) {
        isLive = isLiveType;
        fragmentManager = fm;
        onVideoPlayListener = listener;
    }

    protected abstract void createPlayer();

    public boolean isLivePlay(){   return isLive; }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void initPlayerParams(final CourseWareInfo liveCourseInfo) {
        currentPlayPosition = 0;
        mChatMsgList.clear();
        offlineFilePath = liveCourseInfo.targetPath;
        offSignalFilePath = liveCourseInfo.offSignalFilePath;
        LogUtils.e("initPlayerParams__"+offlineFilePath);
        LogUtils.e("initPlayerParams__"+offSignalFilePath);
        hasVideo = true;
    }

    public abstract ChatMessageListWrap getMsgList();

    protected boolean isOfflinePathValid()  {
        if(TextUtils.isEmpty(offlineFilePath)) {
            return false;
        }
        File file = new File(offlineFilePath);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public abstract void pause();

    public abstract void resume();

    public abstract void stop();

    public abstract void replay();//完成之后重新播放

    public abstract void refreshPlay();//播放出错尝试再播

    public abstract void releasePlayer();

    public abstract void seekTo(int time);

    public abstract void setSpeed(int speed);

    public abstract void setOnlyTeacherMessage(boolean isOnlyTeacher);

    public void setQuality(int index){}

    public void initPlayerViews(FrameLayout pptLayout, FrameLayout videoFather) {
        createPlayer();
        mPptViewLayout = pptLayout;
        mVideoFatherLayout = videoFather;
        mVideoFatherLayout.setVisibility(View.VISIBLE);
        mVideoViewLayout = videoFather.findViewById(
                R.id.live_video_view_container_layout);
//        mPptViewLayout.setIntercept(true);
//        mVideoViewLayout.setIntercept(true);
        ImageView btnVideoViewClose = (ImageView) videoFather.findViewById(R.id.live_video_view_close_iv);
        btnVideoViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(mVideoFatherLayout!=null)
                   mVideoFatherLayout.setVisibility(View.INVISIBLE);
            }
        });
        initViews();
        isVideoPlay = false;
        setVideoMode(isVideoPlay);
        mVideoFatherLayout.setVisibility(View.VISIBLE);
    }

    protected abstract void initViews();

    public void setPortraitMode(boolean isPort) {
        isPortrait = isPort;
      //  setVideoMode(isVideoPlay);
    }

    public boolean getVideoMode() {
        return isVideoPlay;
    }

    public void changeVideoMode() {
        setVideoMode(!isVideoPlay);
    }

/*    protected void removePptFragment() {
        if(lppptFragment == null || !isPptFrgmAdded || !lppptFragment.isAdded()) {
            return;
        }
        LogUtils.i("isPptFrgmAdded:" + isPptFrgmAdded + ", Added:" + lppptFragment.isAdded()
                + ", Detached:" + lppptFragment.isDetached()
                + ", Hide:" + lppptFragment.isHidden() + ", Resumed:" + lppptFragment.isResumed()
                + ", Removing:" + lppptFragment.isRemoving() + ", Visible:" + lppptFragment.isVisible());
        try {
            fragmentManager.popBackStackImmediate();
            isPptFrgmAdded = false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    protected void removePptFragment() {
//        if(lppptFragment == null || !isPptFrgmAdded || !lppptFragment.isAdded()) {
//            return;
//        }
//        LogUtils.i("isPptFrgmAdded:" + isPptFrgmAdded + ", Added:" + lppptFragment.isAdded()
//                + ", Detached:" + lppptFragment.isDetached()
//                + ", Hide:" + lppptFragment.isHidden() + ", Resumed:" + lppptFragment.isResumed()
//                + ", Removing:" + lppptFragment.isRemoving() + ", Visible:" + lppptFragment.isVisible());
//        try {
//            //fragmentManager.popBackStackImmediate();
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.remove(lppptFragment);
//            transaction.commitAllowingStateLoss();
//            isPptFrgmAdded = false;
//            lppptFragment=null;
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    protected void addPptFragment(){}

    public void setVideoMode(boolean isVideo) {
        if(isVideoPlay == isVideo)
            return;
        isVideoPlay = isVideo;
        removePptFragment();
        mPptViewLayout.removeAllViews();
        mVideoViewLayout.removeAllViews();
        mVideoFatherLayout.setVisibility(View.VISIBLE);
    }

    public boolean sendChatMsg(String cText, String rText, boolean isEmoji) {
        if(!isLive) {
            ToastUtils.showShort("回放课程不能发消息");
            return false;
        }
        if(isRootMuted) {
            ToastUtils.showShort("房间已被禁聊");
            return false;
        }
        if(!isSendEnable) {
            ToastUtils.showShort("您已被禁聊，请联系管理员");
            return false;
        }
        return true;
    }

    public void setIsLive(boolean isLive){
        this.isLive = isLive;
    }

    public static VideoPlayer getPlayerInstance(int playerType, boolean isLiveVideo,
                                         FragmentManager fm, VideoPlayer.OnVideoPlayListener l) {
        VideoPlayer mVideoPlayer= new BaiJiaVideoPlayerImpl(isLiveVideo, fm, l);;
      /*  if(playerType == 1) {
            mVideoPlayer = new BaiJiaVideoPlayerImpl(isLiveVideo, fm, l);
        } else {
            mVideoPlayer = new GenSeeVideoPlayerImpl(isLiveVideo, fm, l);
        }*/
        return mVideoPlayer;
    }

    public abstract void mute();
    public abstract void unMute();

    public interface OnVideoPlayListener {
        void onExpressUpdate(String playerTypeKey, List<LiveChatExpressBean> list);

        void onJoinSuccess();

        void onJoinFailed(String error,boolean isLive);

        void onReconnecting();

        void onChatWithPublic();

        void onRecordVideoInit(int var1, boolean var2, int var3);

        void onVideoPrepared(int starttime, int videoduration);

        void onVideoEnd();

        void onPlayResume();

        void onPlayPause(boolean isError);//isError 是否出错引起的播放暂停

        void onPlayError(String error, int code);

        void onCaching(boolean var1);

        void onSeek(int var1);

        boolean getActivityRunningFront();

        void onCaton(BJPlayerView view);//卡顿

        void onPPtPageChange(int currentPage, int totalPage);

        void onTeacherNameChange(String teacherName);

        void onTestOptions(String url, boolean isEnd);

        void onPlayErrorInfo(String error, int code);

//        void onErr(int var1);

//        void onVodDetail(long storage, int duration);
    }

    public interface BaseView{
        VideoPlayer getPlayer();

    }
}
