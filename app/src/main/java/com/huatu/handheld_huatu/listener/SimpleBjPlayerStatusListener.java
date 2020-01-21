package com.huatu.handheld_huatu.listener;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.OnPlayerViewListener;
import com.baijiahulian.player.bean.SectionItem;
import com.baijiahulian.player.bean.VideoItem;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.music.player.IBgPlayerStatusListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\5\16 0016.
 */

public abstract class SimpleBjPlayerStatusListener implements OnPlayerViewListener {

    private IBgPlayerStatusListener mPlayerStatusListeners;

    public void setOnBgPlayerStatusListener(IBgPlayerStatusListener playerStatusListener){
        mPlayerStatusListeners=playerStatusListener;
    }

    @Override
    public void onCaton(BJPlayerView bjPlayerView) {   }

    @Override
    public void onVideoInfoInitialized(BJPlayerView playerView, HttpException exception) {
        //TODO: 视频信息初始化结束
        if (exception != null) {
            // 视频信息初始化成功
            VideoItem videoItem = playerView.getVideoItem();
            LogUtils.e("onVideoInfoInitialized", "onVideoInfoInitialized");
        }
    }

    @Override
    public void onPause(BJPlayerView playerView) {
        //TODO: video暂停
        LogUtils.e("onPause", "onPause");
    }

    @Override
    public void onPlay(BJPlayerView playerView) {
        //TODO: 开始播放
        LogUtils.e("onPlay", "onPlay");
    }

    @Override
    public void onError(BJPlayerView playerView, int code) {
        //TODO: 播放出错
        LogUtils.e("onError", "onError");
    }

    @Override
    public void onUpdatePosition(BJPlayerView playerView, int position) {
        //TODO: 播放过程中更新播放位置
        //   Log.e("onUpdatePosition", "onUpdatePosition");
    }

    @Override
    public void onSeekComplete(BJPlayerView playerView, int position) {
        //TODO: 拖动进度条
        LogUtils.e("onSeekComplete", "onSeekComplete");
    }

    @Override
    public void onSpeedUp(BJPlayerView playerView, float speedUp) {
        //TODO: 设置倍速播放
        LogUtils.e("onSpeedUp", "onSpeedUp");
    }

    @Override
    public void onVideoDefinition(BJPlayerView playerView, int definition) {
        //TODO: 设置清晰度完成
        LogUtils.e("onVideoDefinition", "onVideoDefinition");
    }

    @Override
    public void onPlayCompleted(BJPlayerView playerView, VideoItem item, SectionItem nextSection) {
        //TODO: 当前视频播放完成 [nextSection已被废弃，请勿使用]
    /*    if (livelession.get(currenttTitlePosition).getIsComment() == 0) {
            if (!sharedPreferences.getBoolean(livelession.get(currenttTitlePosition).rid + "judge", false)) {
                CourseJudgeActivity.newInstance(BJYMediaPlayActivity.this,
                        classid, livelession.get(currenttTitlePosition).rid);
            }

        }

        Log.e("onPlayCompleted", "onPlayCompleted");*/
    }

    @Override
    public void onVideoPrepared(BJPlayerView playerView) {

        LogUtils.e("onVideoPrepared", "onVideoPrepared");
        //TODO: 准备好了，马上要播放
    /*    // 可以在这时获取视频时长
        playerView.getDuration();
        isPrepared = true;
        Log.e("isPrepared", isPrepared + "`");*/
    }
}


/*

    @Override
    public void onCaton(BJPlayerView bjPlayerView) {   }

    @Override
    public void onVideoInfoInitialized(BJPlayerView playerView, HttpException exception) {
        //TODO: 视频信息初始化结束
        if (exception != null) {
            // 视频信息初始化成功
            VideoItem videoItem = playerView.getVideoItem();
            Log.e("onVideoInfoInitialized", "onVideoInfoInitialized");
        }
    }

    @Override
    public void onPause(BJPlayerView playerView) {
        //TODO: video暂停
        Log.e("onPause", "onPause");
    }

    @Override
    public void onPlay(BJPlayerView playerView) {
        //TODO: 开始播放
        Log.e("onPlay", "onPlay");
    }

    @Override
    public void onError(BJPlayerView playerView, int code) {
        //TODO: 播放出错
        Log.e("onError", "onError");
    }

    @Override
    public void onUpdatePosition(BJPlayerView playerView, int position) {
        //TODO: 播放过程中更新播放位置
        //   Log.e("onUpdatePosition", "onUpdatePosition");
    }

    @Override
    public void onSeekComplete(BJPlayerView playerView, int position) {
        //TODO: 拖动进度条
        Log.e("onSeekComplete", "onSeekComplete");
    }

    @Override
    public void onSpeedUp(BJPlayerView playerView, float speedUp) {
        //TODO: 设置倍速播放
        Log.e("onSpeedUp", "onSpeedUp");
    }

    @Override
    public void onVideoDefinition(BJPlayerView playerView, int definition) {
        //TODO: 设置清晰度完成
        Log.e("onVideoDefinition", "onVideoDefinition");
    }

    @Override
    public void onPlayCompleted(BJPlayerView playerView, VideoItem item, SectionItem nextSection) {
        //TODO: 当前视频播放完成 [nextSection已被废弃，请勿使用]
        Log.e("onPlayCompleted", "onPlayCompleted");
    }

    @Override
    public void onVideoPrepared(BJPlayerView playerView) {
        //TODO: 准备好了，马上要播放
        // 可以在这时获取视频时长
        playerView.getDuration();
    }*/
