package com.huatu.handheld_huatu.view;

import android.animation.AnimatorSet;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.LogUtils;

/**
 * Created by Administrator on 2019\8\2 0002.
 */

public class LoadingFrameLayout extends FrameLayout {

    public LoadingFrameLayout(@NonNull Context context) {
        super(context);
    }

    public LoadingFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                       @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    ImageView mLoadingView,mBottomView;
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mLoadingView = (ImageView) getChildAt(1);
        mBottomView = (ImageView) getChildAt(0);

    }

    private boolean mAutoRun=true;
    private boolean mNeedRun;

    boolean mIsRunning=false;
    private boolean isRunning(){
        return mIsRunning;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        changeRunStateByVisibility(visibility);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        changeRunStateByVisibility(visibility);
    }

    private void changeRunStateByVisibility(int visibility) {
         if (visibility == VISIBLE) {
            if (mNeedRun) {
                //Log.e("AVLoadingIndicatorView","start");
                start();
            }
        } else {
            if (isRunning()) {
                mNeedRun = true;
                stopAnimation();;
                // Log.e("AVLoadingIndicatorView", "stop");
            }
        }
    }


    AnimatorSet mAnimatorSet;
    private void start(){
        mIsRunning=true;
        LogUtils.e("dismiss","start");
        if(null==mAnimatorSet){
            mAnimatorSet= AnimUtils.loadingAnim(mLoadingView,mBottomView);
        }
        if(null!=mAnimatorSet)
            mAnimatorSet.start();
    }

    private void stopAnimation(){

        if(null!=mAnimatorSet){
            LogUtils.e("dismiss","cancle");
            mAnimatorSet.cancel();

            if(mLoadingView!=null)
                mLoadingView.clearAnimation();
             if(mBottomView!=null)
                mBottomView.clearAnimation();
            mIsRunning=false;
            mAnimatorSet=null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAutoRun ) {
            if (getVisibility() == VISIBLE)
                start();
            else
                mNeedRun = true;
        }
    }

    @Override
    protected void onDetachedFromWindow(){
        super.onDetachedFromWindow();
        stopAnimation();

    }

}
