package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by cjx on 2018\9\3 0003.
 */

public class BJPlayerTouchView extends com.baijiahulian.player.BJPlayerView {

    private boolean mInterceptTouch=false;
    public interface OnInterceptListener {
        void onInterceptClick(View view);
    }

    OnInterceptListener mOnInterceptListener;
    public void setInterceptTouch(OnInterceptListener interceptTouch){
        this.mOnInterceptListener=interceptTouch;
    }

    public void setInterceptTouch(boolean interceptTouch){
       this.mInterceptTouch=interceptTouch;
    }

    public BJPlayerTouchView(Context var1) {
        this(var1, (AttributeSet)null);
    }

    public BJPlayerTouchView(Context var1, AttributeSet var2) {
        this(var1, var2, 0);
    }

    public BJPlayerTouchView(Context var1, AttributeSet var2, int var3){
        super(var1,var2,var3);
    }


    public boolean isPlayStateNormal(){
        return  this.getVideoView().getMediaPlayer() != null && this.getVideoView().getMediaPlayer().getPlayState() >= 1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent var1) {
        if(this.getVideoView().getMediaPlayer() != null && this.getVideoView().getMediaPlayer().getPlayState() >= 1) {
            if(mInterceptTouch){
                 if(null!=mOnInterceptListener) mOnInterceptListener.onInterceptClick(this);
                 return true;
            }
            else
                return super.onTouchEvent(var1);
        } else {
            return false;
        }
    }

    private  boolean isInBackground;
    public boolean isFromBackground(){
        return isInBackground;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!this.isInBackground){
            isInBackground=true;
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        if(this.isInBackground) {
            this.isInBackground = false;
        }
    }
}
