package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.view.View;

import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.CenterViewStatus;
import com.baijiahulian.player.playerview.IPlayerBottomContact;
import com.baijiahulian.player.playerview.IPlayerCenterContact;
import com.baijiahulian.player.playerview.IPlayerTopContact;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.VideoPlayer;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

/**
 * Created by saiyuan on 2017/11/30.
 */

public class BaiJiaPlayBackPresenter implements IPlayerTopContact.TopView,
        IPlayerCenterContact.CenterView, IPlayerBottomContact.BottomView{
    private BJPlayerView mPlayerView;
    private VideoPlayer.OnVideoPlayListener mPlayListener;
    private boolean mIsCacheIng=false;

    public BaiJiaPlayBackPresenter(BJPlayerView vp,VideoPlayer.OnVideoPlayListener listener) {
        mPlayerView = vp;
        mPlayListener=listener;
    }

    @Override
    public void onBind(IPlayerBottomContact.IPlayer iPlayer) {

    }

    @Override
    public CenterViewStatus getStatus() {
        return null;
    }

    @Override
    public void setDuration(int i) {

    }

    @Override
    public void setCurrentPosition(int i) {

    }

    @Override
    public void setIsPlaying(boolean b) {
        LogUtils.e("setIsPlaying:" + b);
        if(b) {
            if(mPlayListener != null) {
                mPlayListener.onPlayResume();
            }
        } else {
            if(mPlayListener != null) {
                mPlayListener.onPlayPause(false);
            }
        }
    }

    @Override
    public void onBufferingUpdate(int i) {

    }

    @Override
    public void setSeekBarDraggable(boolean b) {

    }

    @Override
    public void onBind(IPlayerCenterContact.IPlayer iPlayer) {

    }

    @Override
    public boolean onBackTouch() {
        return false;
    }

    @Override
    public void showProgressSlide(int i) {   }

    @Override
    public void showLoading(String s) {
       LogUtils.e("BaiJiaPlayBackPresenter",s+"_showLoading");
        mIsCacheIng=true;
        if(mPlayListener!=null) mPlayListener.onCaching(true);
    }

    @Override
    public void dismissLoading() {
        if(mIsCacheIng){
            mIsCacheIng=false;
            LogUtils.e("BaiJiaPlayBackPresenter","dismissLoading2");
            if(mPlayListener!=null) mPlayListener.onCaching(false);
        }
    }

    @Override
    public void showVolumeSlide(int i, int i1) {

    }

    @Override
    public void showBrightnessSlide(int i) {

    }

    @Override
    public void showError(int what, int extra) {
        LogUtils.i("errorCode: " + what + ", errorMsgCode:" + extra);
    }

    @Override
    public void showError(final int code, String message) {
        LogUtils.i("errorCode: " + code + ", errorMsg:" + message);
        if (mPlayListener != null){
            mPlayListener.onCaching(false);
            mPlayListener.onPlayPause(true);
            mPlayListener.onPlayError(message + ":" + code, code);
        }
    }

    @Override
    public void showWarning(String s) {
        ToastUtils.showShort(s);
        LogUtils.i("showWarning:" + s);
    }

    @Override
    public void onShow() {
        LogUtils.i("onShow");
    }

    @Override
    public void onHide() {
        LogUtils.i("onHide");
    }

    @Override
    public void onVideoInfoLoaded(VideoItem videoItem) {
        LogUtils.i("onVideoInfoLoaded");
    }

    @Override
    public boolean isDialogShowing() {
        return false;
    }

    @Override
    public void updateDefinition() {

    }

    @Override
    public void onBind(IPlayerTopContact.IPlayer iPlayer) {

    }

    @Override
    public void setTitle(String s) {

    }

    @Override
    public void setOrientation(int i) {

    }

    @Override
    public void setOnBackClickListener(View.OnClickListener onClickListener) {

    }

    public void startVideo() {
        LogUtils.i("startVideo");
        if(mPlayerView != null && mPlayerView.getVideoItem() != null) {
            mPlayerView.playVideo();
        }
    }

    public void startVideo(int position) {
        LogUtils.i("startVideo: " + position);
        if(mPlayerView != null ) {
            if(position == 0) {
                mPlayerView.playVideo();
            } else {
                mPlayerView.playVideo(position);
            }
        }
    }

    public void stopVideo() {
        LogUtils.i("stopVideo");
        if(mPlayerView != null) {
            mPlayerView.pauseVideo();
        }

    }

    public void setSpeed(float speed) {
        if(mPlayerView != null) {
            mPlayerView.setVideoRate(speed);
        }
    }

    public void setVideoDefinition(int index){
        if (mPlayerView != null)
            mPlayerView.setVideoDefinition(index);
    }

    public void release(){
        if(mPlayerView != null) {
//            mPlayerView.pauseVideo();
            mPlayerView.onDestroy();
         }
        mPlayListener=null;
   }
}
