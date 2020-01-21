package com.huatu.handheld_huatu.ui;

import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

/**
 * Created by Administrator on 2018\5\29 0029.
 */

public class LiveLoadingLayout extends FrameLayout implements View.OnClickListener{
    ImageView mProgressBar,mBottomLoadView;
    LinearLayout mErrorLayout;

    TextView mErrorTxt,mRetryBtn;
    View.OnClickListener mOnClickListener;
    public boolean mIsLocal = false;

    AnimatorSet mAnimatorSet;

    public void setOnClickListener(View.OnClickListener onClickListener){
        mOnClickListener=onClickListener;
     }

    public LiveLoadingLayout(Context context) {
        super(context);
    }

    public LiveLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        mProgressBar= (ImageView) this.findViewById(R.id.iv_loading);
        mErrorLayout=(LinearLayout)this.findViewById(R.id.video_play_error_layout);
        mErrorTxt=(TextView)this.findViewById(R.id.error_tip_txt);
        mRetryBtn=(TextView)this.findViewById(R.id.error_retry_btn);
        mRetryBtn.setOnClickListener(this);
//        Glide.with(getContext()).load(R.drawable.player_loading).asGif()
//                .diskCacheStrategy(DiskCacheStrategy.ALL).into(mProgressBar);
        mProgressBar.setImageResource(R.drawable.loading_swing_luobo);
        mBottomLoadView=this.findViewById(R.id.iv_loading_bg);
        mAnimatorSet=AnimUtils.loadingAnim(mProgressBar, mBottomLoadView);
    }

    public void showLoading(){
        this.setVisibility(VISIBLE);
        mProgressBar.setVisibility(VISIBLE);
        mBottomLoadView.setVisibility(VISIBLE);
        if((null!=mAnimatorSet)&&(!mAnimatorSet.isRunning())){
            mAnimatorSet.start();
        }
        mErrorLayout.setVisibility(GONE);
    }

    public void hide(){
        this.setVisibility(GONE);
        if((null!=mAnimatorSet)){
            mAnimatorSet.cancel();
        }
    }

    public void showErrorTip(String des){
        this.setVisibility(VISIBLE);
        mErrorLayout.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mBottomLoadView.setVisibility(GONE);
        if((null!=mAnimatorSet)&&(mAnimatorSet.isRunning())){
            mAnimatorSet.cancel();
        }
        mErrorTxt.setText(String.valueOf(des));
    }

    @Override
    public void onClick(View v) {

        if(!NetUtil.isConnected() && !mIsLocal) {
            ToastUtils.showShort("当前无网络，请检查网络!");
            return;
        }

        showLoading();
        if(mOnClickListener!=null) mOnClickListener.onClick(v);
    }

    public boolean isShowError(){
        return getVisibility() == VISIBLE && mErrorLayout.getVisibility() == View.VISIBLE;
    }
    
}
