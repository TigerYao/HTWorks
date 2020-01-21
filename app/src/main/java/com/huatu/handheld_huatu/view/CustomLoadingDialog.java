package com.huatu.handheld_huatu.view;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.LogUtils;

/**
 * Created by saiyuan on 2016/10/25.
 */
public class CustomLoadingDialog extends Dialog implements DialogInterface.OnShowListener,DialogInterface.OnDismissListener {
  //  private Activity mContext;
    private TextView mTipView;
    private View mCustomView;
  //  private ImageView iv_loading,mBottomloadView;

    AnimatorSet mAnimatorSet;

    public CustomLoadingDialog(Activity context) {
        super(context, R.style.CustomProgressDialog);
     //   mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mCustomView = inflater.inflate(R.layout.layout_custom_progress_dialog, null);
//        mTipView = (TextView) mCustomView.findViewById(R.id.custom_progress_tips);
        mCustomView = inflater.inflate(R.layout.iv_loading, null);
      //  iv_loading= (ImageView) mCustomView.findViewById(R.id.iv_loading);
      //  mBottomloadView=(ImageView)mCustomView.findViewById(R.id.iv_loading_bg);
/*        Glide.with(mContext).load(R.drawable.icon_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(iv_loading);*/
//        setIsMessageShow(true);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
      //  this.setOnShowListener(this);
        //this.setOnDismissListener(this);
    }

    @Override
    public void onShow(DialogInterface dialog){
/*        if(null==mAnimatorSet){
            mAnimatorSet= AnimUtils.loadingAnim(iv_loading,mBottomloadView);
        }
        if(null!=mAnimatorSet)
           mAnimatorSet.start();*/
    }

    @Override
    public void onDismiss(DialogInterface dialog){
      /*  if(null!=mAnimatorSet){
            LogUtils.e("dismiss","cancle");
            mAnimatorSet.cancel();

            if(iv_loading!=null)
                iv_loading.clearAnimation();

            if(mBottomloadView!=null)
                mBottomloadView.clearAnimation();

            mAnimatorSet=null;
        }*/
    }

    @Override
    public void dismiss(){
     /*   if(null!=mAnimatorSet){
            LogUtils.e("dismiss","cancle");
            mAnimatorSet.cancel();

            if(iv_loading!=null)
                iv_loading.clearAnimation();

            if(mBottomloadView!=null)
                mBottomloadView.clearAnimation();

            mAnimatorSet=null;
        }*/
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mCustomView);
    }

//    public void setMessage(String msg) {
//        mTipView.setText(msg);
//        mTipView.setVisibility(View.VISIBLE);
//    }
//
//    public void setMessage(int resId) {
//        mTipView.setText(resId);
//        mTipView.setVisibility(View.VISIBLE);
//    }

//    public void setIsMessageShow(boolean flag) {
//        if(flag) {
//            mTipView.setVisibility(View.VISIBLE);
//        } else {
//            mTipView.setVisibility(View.GONE);
//        }
//    }
}
