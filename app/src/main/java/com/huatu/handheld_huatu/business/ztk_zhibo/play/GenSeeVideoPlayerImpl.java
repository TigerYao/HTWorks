package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/*import com.gensee.callback.IChatCallBack;
import com.gensee.common.GenseeConfig;
import com.gensee.common.RoleType;
import com.gensee.common.ServiceType;
import com.gensee.entity.ChatMsg;
import com.gensee.entity.DocInfo;
import com.gensee.entity.InitParam;
import com.gensee.entity.QAMsg;
import com.gensee.entity.VodObject;
import com.gensee.media.GSOLPlayer;
import com.gensee.media.PlaySpeed;
import com.gensee.media.VODPlayer;
import com.gensee.net.RtComp;
import com.gensee.player.OnChatListener;
import com.gensee.room.RtSimpleImpl;
import com.gensee.routine.IRTEvent;
import com.gensee.routine.State;
import com.gensee.taskret.OnTaskRet;
import com.gensee.view.GSDocViewGx;
import com.gensee.view.GSVideoView;
import com.gensee.vod.VodSite;*/
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.ChatMessage;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.ChatMessageListWrap;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.VideoPlayer;
import com.huatu.handheld_huatu.business.ztk_zhibo.view.InterceptFrameLayout;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.VideoBean;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by saiyuan on 2017/12/19.
 */

public abstract class GenSeeVideoPlayerImpl extends VideoPlayer {

    public GenSeeVideoPlayerImpl(boolean isLiveType, FragmentManager fm,
                       VideoPlayer.OnVideoPlayListener listener) {
        super(isLiveType, fm, listener);
    }

    /* implements OnTaskRet, IChatCallBack,
        GSOLPlayer.OnOLPlayListener, VodSite.OnVodListener {
    private RtSimpleImpl mLivePlayer;
    private RtComp comp;
    private VODPlayer mCachePlayer;
    protected PlaySpeed[] playSpeedLists = {PlaySpeed.SPEED_NORMAL,
            PlaySpeed.SPEED_125, PlaySpeed.SPEED_150, PlaySpeed.SPEED_200};
    protected VodSite vodSite;

    public GenSeeVideoPlayerImpl(boolean type, FragmentManager fm,
                       VideoPlayer.OnVideoPlayListener listener) {
        super(type, fm, listener);
        mChatMsgList.geeChatMsg=new ArrayList<>();
        mChatMsgList.playerType= PlayerTypeEnum.Gensee.getValue();
        GenseeConfig.isNeedChatMsg=true;
        createPlayer();

    }
    public  void setOnlyTeacherMessage(boolean isOnlyTeacher){}

    *//* 这个字段用来标记只看老师，此处强制为true
    *  方便OnliveChatPresenter
    * *//*
    @Override
    public boolean isLivePlay(){
        return true;
    }

    @Override
    protected void createPlayer() {
        if(isLive) {
            if(mLivePlayer == null) {
                mLivePlayer = new RtSimpleImpl() {
                    @Override
                    protected void onVideoStart() {
                        LogUtils.i("onVideoBegin:");//有时候不走，
                        isPlaying = true;
                        if(onVideoPlayListener != null) {
                            onVideoPlayListener.onVideoBegin();
                            onVideoPlayListener.onCaching(false);
                        }
                    }

                    @Override
                    protected void onVideoEnd() {
                        LogUtils.i("onVideoEnd:");
                        isPlaying = false;
                        ToastUtils.showShort("视频已关闭");
                    }

                    @Override
                    public Context onGetContext() {
                        return UniApplicationContext.getContext();
                    }

                    //直播间加入响应
                    @Override
                    public void onRoomJoin(int result, com.gensee.routine.UserInfo self, boolean svrFailover) {
                        onErr(result,true);
                    }

                    *//**
                     * 直播状态 s.getValue()   0 默认直播未开始 1、直播中， 2、直播停止，3、直播暂停
                     *//*
                    @Override
                    public void onRoomPublish(State s) {
                        super.onRoomPublish(s);
                    }

                    @Override
                    public void onJoin(boolean result) {
                        LogUtils.i("result:" + result);
                        String msg = null;
                        isPlaying = false;
                        if(result) {
                            isPlaying = true;
                            if(onVideoPlayListener != null) {
                                onVideoPlayListener.onJoinSuccess();
                            }
                        } else {
                            if(onVideoPlayListener != null) {
                                onVideoPlayListener.onJoinFailed("进入直播间出错",true);
                            }
                        }
                        ToastUtils.showShort(msg);
                    }

                    //退出完成 关闭界面
                    @Override
                    protected void onRelease(final int reason) {
                        //reason 退出原因
                        String msg = "已退出";
                        switch (reason) {
                            //用户自行退出  正常退出
                            case IRTEvent.IRoomEvent.LeaveReason.LR_NORMAL:
                                msg = "";
                                break;
                            //LR_EJECTED = LR_NORMAL + 1; //被踢出
                            case IRTEvent.IRoomEvent.LeaveReason.LR_EJECTED:
                                msg = "您已被踢出";
                                break;
                            //LR_TIMESUP = LR_NORMAL + 2; //时间到
                            case IRTEvent.IRoomEvent.LeaveReason.LR_TIMESUP:
                                msg = "时间已过";
                                break;
                            //LR_CLOSED = LR_NORMAL + 3; //直播（课堂）已经结束（被组织者结束）
                            case IRTEvent.IRoomEvent.LeaveReason.LR_CLOSED:
                                msg = "直播间已经被关闭";
                                if(onVideoPlayListener != null) {
                                    onVideoPlayListener.onVideoEnd();
                                }
                                break;
                            default:
                                break;
                        }
                       if(!TextUtils.isEmpty(msg))   ToastUtils.showShort(msg);
                    }
                };
                mLivePlayer.getRtSdk().setChatCallback(this);
            }
        } else {
            if(mCachePlayer == null) {
                mCachePlayer = new VODPlayer();
            }
            if(vodSite == null) {
                vodSite = new VodSite(UniApplicationContext.getContext());
                vodSite.setVodListener(this);
            }
        }
    }
    @Override
    public void initPlayerParams(CourseWareInfo liveCourseInfo) {
        super.initPlayerParams(liveCourseInfo);
        createPlayer();
        InitParam initParam = new InitParam();
    *//*    initParam.setDomain(liveCourseInfo.url);
        initParam.setLiveId(liveCourseInfo.JoinCode);*//*
        initParam.setDomain(liveCourseInfo.bjyRoomId);
        initParam.setLiveId(liveCourseInfo.bjySessionId);
        initParam.setLoginAccount("");
        initParam.setLoginPwd("");
        if (!TextUtils.isEmpty(SpUtils.getNick())) {
            initParam.setNickName(SpUtils.getNick());
        } else {
            initParam.setNickName(SpUtils.getUname());
        }

        initParam.setServiceType(ServiceType.ST_CASTLINE);
        if (isLive) {
           // initParam.setJoinPwd(liveCourseInfo.password);
            initParam.setJoinPwd(liveCourseInfo.joinCode);
            comp = new RtComp(UniApplicationContext.getContext(), new RtComp.Callback() {
                @Override
                public void onInited(String s) {
                    LogUtils.i("onInited: " + s);
                    mLivePlayer.joinWithParam("", s);
                }

                @Override
                public void onErr(int i) {
                    GenSeeVideoPlayerImpl.this.onErr(i,true);
                }
            });
            comp.initWithGensee(initParam);
        } else {
            //initParam.setVodPwd(liveCourseInfo.password);
            initParam.setJoinPwd(liveCourseInfo.joinCode);
            if (!isOfflinePathValid()) {
                vodSite.getVodObject(initParam);
            } else if (mCachePlayer != null) {
                mCachePlayer.play(offlineFilePath, this, "", false);
                if (onVideoPlayListener != null) {
                    onVideoPlayListener.onPlayResume();
                }
            } else {
                LogUtils.i("Using offline player, but mCachePlayer == null");
            }
        }
    }

    @Override
    public void initPlayerViews(FrameLayout pptLayout, FrameLayout videoFather) {
        super.initPlayerViews(pptLayout, videoFather);
        if (isLive) {
            mLivePlayer.setVideoView(mGSVideoView);
            mLivePlayer.setGSDocViewGx(mGlDocView);
        } else {
            mCachePlayer.setGSVideoView(mGSVideoView);
            mCachePlayer.setGSDocViewGx(mGlDocView);
        }
        // PublicChatManager.getIns().clearAll();
    }

    @Override
    protected void initViews() {
        if(mGlDocView == null) {
            mGlDocView = new GSDocViewGx(UniApplicationContext.getContext());
        }
        if(mGSVideoView == null) {
            mGSVideoView = new GSVideoView(UniApplicationContext.getContext());
        }
        mGlDocView.showAdaptView();
        mGlDocView.setTouchforbidden(true);
        mGSVideoView.setClickable(false);
        if (mGSVideoView != null) {
            mGSVideoView.renderDefault();
        }
    }

    //IChatCallBack start
    @Override
    public void onChatJoinConfirm(boolean b) { }

    @Override
    public void onChatMessage(ChatMsg chatMsg) {
        LogUtils.i("onChatMessage:" + chatMsg.toString());
     *//*   ChatMessage message = new ChatMessage();
        message.setSendUserId(String.valueOf(chatMsg.getSenderId()));
        message.setSendUserName(chatMsg.getSender());
        message.role = chatMsg.getSenderRole();
        message.playerType = 0;
        message.setText(chatMsg.getContent());
        message.setRich(chatMsg.getRichText());
        message.setTime(Calendar.getInstance().getTimeInMillis());
        PublicChatManager.getIns().addMsg(message);*//*

        mChatMsgList.geeChatMsg.add(chatMsg);
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onChatWithPublic();
        }
    }

    @Override
    public void onChatEnable(boolean b) {
        LogUtils.i("onChatEnable:" + b);
        isSendEnable = b;
        if(!b) {
            ToastUtils.showShort("您已被禁聊，请联系管理员");
        } else {
            ToastUtils.showShort("禁言已解除");
        }
    }
   //聊天审核删除通知
    @Override
    public void onChatCensor(long l, String s) {  }

    //IChatCallBack end



    @Override
    public ChatMessageListWrap getMsgList() {   return mChatMsgList;  }

    @Override
    public void pause() {
        isPlaying = false;
       if(mLivePlayer != null) {
           //关闭声音 直播不能暂停
            mLivePlayer.getRtSdk().audioCloseSpeaker(null);
            //mLivePlayer.audioSet(true);
            //mLivePlayer.videoSet(true);
       }
        if(mCachePlayer != null) {
            mCachePlayer.pause();
        }
    }

    @Override
    public void resume() {
         if(mLivePlayer != null) {
             //打开声音
             mLivePlayer.getRtSdk().audioOpenSpeaker(null);
            // mLivePlayer.audioSet(false);
           //  mLivePlayer.videoSet(false);
         }
        if(mCachePlayer != null) {
             mCachePlayer.resume();
        }
    }

    //
    @Override
    public void  refreshPlay(){    }

    @Override
    public  void replay(){
         if(!isLive&&mCachePlayer != null) {
            mCachePlayer.play(offlineFilePath, this, "", false);

        }
    }

    @Override
    public void stop() {
        isPlaying = false;
        if(!isLive && mCachePlayer != null) {
            mCachePlayer.stop();
        }
    }

    @Override
    public void releasePlayer() {
        this.onVideoPlayListener=null;
        GenseeConfig.isNeedChatMsg=false;
        //PublicChatManager.getIns().clearAll();
        isPlaying = false;
        if(mLivePlayer != null) {
            leaveCast();
            mLivePlayer = null;
        }
        if(this.mCachePlayer != null) {
            stop();
            mCachePlayer.setGSDocViewGx(null);
            mCachePlayer.setGSVideoView(null);
            vodSite.setVodListener(null);
            mCachePlayer.release();
            mCachePlayer = null;
        }
        this.mPptViewLayout.removeAllViews();
        this.mVideoViewLayout.removeAllViews();

        this.mVideoViewLayout=null;
        this.mPptViewLayout=null;
        this.mVideoFatherLayout=null;
    }

    *//**
     * 退出的时候请调用
     *//*
    private void leaveCast(){
        if(comp!= null){
            comp.setCallback(null);
            comp = null;
        }
        mLivePlayer.setGSDocViewGx(null);
        mLivePlayer.setVideoView(null);
        mLivePlayer.leave(false);
    }

    @Override
    public void seekTo(int time) {
        LogUtils.i("seekTo: " + time);
        if(mCachePlayer != null) {
            mCachePlayer.seekTo(time);
           // mCachePlayer.resume();
            if(onVideoPlayListener != null) {
                onVideoPlayListener.onCaching(true);
            }
        }
    }

    @Override
    public void setSpeed(int speed) {
        if(mCachePlayer != null) {
            PlaySpeed strSpeed = playSpeedLists[speed];
            mCachePlayer.setSpeed(strSpeed, null);
        }
    }

    @Override
    public void onChatCensor(String s, String s1) {  }

    @Override
    public boolean sendChatMsg(String cText, String rText, boolean isEmoji) {
        boolean flag = super.sendChatMsg(cText, rText, isEmoji);
        if(flag) {
            if(mLivePlayer != null && (!TextUtils.isEmpty(cText) || !TextUtils.isEmpty(rText))) {
                chatText = cText;
                richText = rText;
                ChatMsg msg = new ChatMsg(chatText, richText, ChatMsg.CHAT_MSG_TYPE_PANELIST,
                        String.valueOf(UserInfoUtil.userId));
                mLivePlayer.getRtSdk().chatWithPublic(msg, this);
            } else {
                ToastUtils.showShort("消息不能为空");
            }
        }
        return flag;
    }

//    protected int getUserIndex(UserInfo userInfo) {
//        if(userInfo == null) {
//            return -1;
//        } else {
//            int index = -1;
//            for(int i = 0; i < userInfoList.size(); ++i) {
//                if(userInfo.getUserId() == userInfoList.get(i).getUserId()) {
//                    index = i;
//                    break;
//                }
//            }
//
//            return index;
//        }
//    }

//    public void onUserLeave(UserInfo userInfo) {
//        LogUtils.i("onUserLeave:" + userInfo.toString());
//        if(userInfo != null) {
//            int index = getUserIndex(userInfo);
//            if(index >= 0) {
//                userInfoList.remove(index);
//            }
//        }
//    }
//
//    public void onUserUpdate(UserInfo userInfo) {
//        LogUtils.i("onUserUpdate:" + userInfo.toString());
//        if(userInfo != null) {
//            int index = getUserIndex(userInfo);
//            if(index < 0) {
//                userInfoList.add(userInfo);
//            } else {
//                userInfoList.set(index, userInfo);
//            }
//        }
//    }

    public void onReconnecting() {
        LogUtils.i("onReconnecting:");
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onReconnecting();
        }

    }



    public void onErr(int errCode,boolean isLive) {
        LogUtils.i("onErr:" + errCode);
        isPlaying = false;
        String msg = "";
        if(errCode == IRTEvent.IRoomEvent.JoinResult.JR_OK) {
            LogUtils.i("已加入");
            if(onVideoPlayListener != null) {
                onVideoPlayListener.onJoinSuccess();
                onVideoPlayListener.onCaching(false);
            }
            return;
        }
        switch(errCode) {
            case IRTEvent.IRoomEvent.JoinResult.JR_ERROR:
                msg = "加入失败，重试或联系管理员";
                break;
            //课堂被锁定
            case IRTEvent.IRoomEvent.JoinResult.JR_ERROR_LOCKED:
                msg = "直播间已被锁定";
                break;
            //老师（组织者已经加入）
            case IRTEvent.IRoomEvent.JoinResult.JR_ERROR_HOST:
                msg = "老师已经加入，请以其他身份加入";
                break;
            //加入人数已满
            case IRTEvent.IRoomEvent.JoinResult.JR_ERROR_LICENSE:
                msg = "人数已满，联系管理员";
                break;
            //音视频编码不匹配
            case IRTEvent.IRoomEvent.JoinResult.JR_ERROR_CODEC:
                msg = "编码不匹配";
                break;
            //超时
            case IRTEvent.IRoomEvent.JoinResult.JR_ERROR_TIMESUP:
                msg = "已经超过直播结束时间";
                break;

            // int ERR_DOMAIN = -100; // ip(domain)不正确
            // int ERR_TIME_OUT = -101; // 超时
            // int ERR_UNKNOWN = -102; // 未知错误
            // int ERR_SITE_UNUSED = -103; // 站点不可用
            // int ERR_UN_NET = -104; // 无网络
            // int ERR_DATA_TIMEOUT = -105; // 数据过期
            // int ERR_SERVICE = -106; // 服务不正确
            // int ERR_PARAM = -107; // 参数不正确
            // int ERR_THIRD_CERTIFICATION_AUTHORITY = -108 //第三方认证失败
            // int ERR_NUMBER_UNEXIST = 0; // 直播间不存在
            // int ERR_TOKEN = 4; // 口令错误
            // int ERR_LOGIN = 5; // 用户名或密码错误
            // int ERR_WEBCAST_UNSTART = 6; // 直播未开始
            // int ERR_ISONLY_WEB = 7; // 只支持web
            // int ERR_ROOM_UNEABLE = 8; 直播间不可用
            // int ERR_INVALID_ADDRESS = 10; // 无效地址
            // int ERR_ROOM_OVERDUE = 11; // 过期
            // int ERR_AUTHORIZATION_NOT_ENOUGH = 12;授权不够
            // int ERR_UNTIMELY = 13; // 太早
            default:
                msg = "播放异常：" + errCode ;
                break;
        }
        if(!TextUtils.isEmpty(msg)) {
            ToastUtils.showShort(msg);
        }
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onJoinFailed(msg,isLive);
        }
    }

    public void onAudioLevel(int i) {

    }

    public void onPublish(boolean isPlay) {
        LogUtils.i("onPublish:" + isPlay);
        isPlaying = isPlay;
    }

    //播放过程中文档翻页及分辨率
    public void onPageSize(int pos, int width, int height) {
        LogUtils.i("onPageSize:[" + pos + "," + width + "], flag:" + height);
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onCaching(false);
        }
    }

    public void onTaskRet(boolean arg0, int i, String s) {
        LogUtils.i("onTaskRet:" + arg0 + ", " + i + ", " + s);
        if(arg0) {
            ChatMsg msg = new ChatMsg();
            msg.setSenderId(UserInfoUtil.userId);
            String name;
            if(!TextUtils.isEmpty(SpUtils.getNick())) {
                name = SpUtils.getNick();
            } else {
                name = UserInfoUtil.userName;
            }
            msg.setSender(name);
            msg.setContent(chatText);
            msg.setRichText(richText);
            msg.setSenderRole(RoleType.ROLE_ATTENDEE);
            this.onChatMessage(msg);
        }
    }

    public void onReconnection() {
        LogUtils.i("onReconnection:");
    }



    //播放状态回调onOlPlayListener  start
    @Override
    public void onInit(int result, boolean haveVideo, int duration, List<DocInfo> docInfos) {
        LogUtils.i("onInit: hasVideo:" + haveVideo + ", duration:" + duration
                + ", docInfoSize:" + (docInfos != null?docInfos.size():0));
        hasVideo = haveVideo;
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onRecordVideoInit(result, haveVideo, duration);
        }
    }

    //播放停止o
    @Override
    public void onPlayStop() {
        isPlaying = false;
        LogUtils.i("onPlayStop:");
         if(onVideoPlayListener != null) {
            onVideoPlayListener.onVideoEnd();
        }
    }

    @Override
    public void onPlayPause() {
        isPlaying = false;
        LogUtils.i("onPlayPause:");
        if(onVideoPlayListener != null) {
             onVideoPlayListener.onPlayPause(false);
        }
    }

   // 播放已恢复o
   @Override
    public void onPlayResume() {
        isPlaying = true;
        LogUtils.i("onPlayResume:");
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onPlayResume();
        }

    }

    //播放进度
    @Override
    public void onPosition(int position) {
        isPlaying = true;
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onSeek(position);
        }
    }

    //视频分辨率上报
    @Override
    public void onVideoSize(int i, int i1, int i2) {   }


   // 进度更改完成
   @Override
    public void onSeek(int position) {
        LogUtils.i("onSeek: " + position);
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onSeek(position);
        }
    }
    @Override
    public void onVideoStart() {
        isPlaying = true;
        LogUtils.i("onVideoStart: ");
    }

    //在线播放缓冲通知
    @Override
    public void onCaching(boolean isCaching) {
        LogUtils.i("onCaching:" + isCaching);//        ToastUtils.showShort(isCaching?"正在缓冲":"缓冲完成");
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onCaching(isCaching);
        }

    }

    //播放中聊天消息回调
    @Override
    public void onChat(List<ChatMsg> list) {
        if(onVideoPlayListener != null && list != null) {
             mChatMsgList.geeChatMsg.addAll(list);
            onVideoPlayListener.onChatWithPublic();
        }
    }

    public void onDocInfo(List<DocInfo> list) {  }

    //播放出错onError
    @Override
    public void onError(int errorCode) {
        LogUtils.e("Gensee_"+errorCode);
        isPlaying = false;
        String message = "";
        switch(errorCode) {
            case ERR_PAUSE:
                break;
            case ERR_PLAY:
                message = "播放失败";
                break;
//            case ERR_RESUME:
//                message = "恢复失败";
//                break;
            case ERR_STOP:
                message = "停止失败";
                break;
//            case ERR_SEEK:
//                message = "进度变化失败";
//                break;
            default:
                break;
        }

        ToastUtils.showShort(message);
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onCaching(false);
            onVideoPlayListener.onPlayPause(true);
        }
    }
  //  播放状态回调onOlPlayListener  end


    //点播信息回调OnVnVodListener start
    public void onChatHistory(String s, List<ChatMsg> list, int i, boolean b) {
    }

    public void onQaHistory(String s, List<QAMsg> list, int i, boolean b) {
    }

    public void onVodErr(int i) {
        onErr(i,false);
    }

    public void onVodObject(String s) {
        LogUtils.i("onVodObject: " + s);
        if(TextUtils.isEmpty(offlineFilePath)) {
            offlineFilePath = s;
        }
        if(mCachePlayer != null) {
            mCachePlayer.play(offlineFilePath, this, "", false);
        }
        if(onVideoPlayListener != null) {
            onVideoPlayListener.onPlayResume();
        }

    }

    public void onVodDetail(VodObject vodObject) {
        long storage = vodObject.getStorage();
        int duration = (int)vodObject.getDuration();
        LogUtils.i("onVodObject: storage:" + storage + ", duration:" + duration);
//        if(onVideoPlayListener != null) {
//            onVideoPlayListener.onVodDetail(storage, duration);
//        }
    }

    //onVodListener end


    @Override
    public void setVideoMode(boolean isVideo) {
        super.setVideoMode(isVideo);
        if(isPortrait) {
            if(isVideoPlay) {
                mVideoViewLayout.addView(mGlDocView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mPptViewLayout.addView(mGSVideoView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                mPptViewLayout.addView(mGlDocView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mVideoViewLayout.addView(mGSVideoView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            if(isVideoPlay) {
                mPptViewLayout.addView(mGSVideoView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                mPptViewLayout.addView(mGlDocView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    @Override
    public void mute() {

    }

    @Override
    public void unMute() {

    }*/
}
