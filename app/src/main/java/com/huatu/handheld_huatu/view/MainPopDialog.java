package com.huatu.handheld_huatu.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.utils.DensityUtils;

/**
 * Created by ljzyuhenda on 16/7/13.
 *<item name="android:backgroundDimAmount">0.6</item>
 * https://zhidao.baidu.com/question/2203283308302553148.html
 */
public class MainPopDialog extends Dialog {
    public View mContentView;
    public ImageView mCoverImg;


    public MainPopDialog(Context context, @LayoutRes int resid) {
        super(context,R.style.fullScreenDialog);

       // requestWindowFeature(Window.FEATURE_NO_TITLE);
      //  getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mContentView = View.inflate(context, resid, null);


        setContentView(mContentView);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mContentView!=null){
            mCoverImg=mContentView.findViewById(R.id.main_img_adv_bg);
            mCoverImg.setTranslationY(400f);
            mCoverImg.setAlpha(0f);

            ImageView main_img_close = (ImageView) mContentView.findViewById(R.id.main_img_close);
            main_img_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainPopDialog.this.dismiss();

                }
            });
        }
    }

    public void reSize(AdvertiseConfig config,Context context,ImageView advImage){
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) advImage.getLayoutParams();

         int tmpWidth=DisplayUtil.dp2px(config.params.width / 2);
         if(tmpWidth>=DisplayUtil.getScreenWidth()){
             tmpWidth=DisplayUtil.getScreenWidth()-DisplayUtil.dp2px(60);
             lp.width=tmpWidth;
             lp.height=(int)(((float)tmpWidth)*config.params.height)/config.params.width;
         }else {
             lp.width =tmpWidth;
             lp.height = DisplayUtil.dp2px(config.params.height / 2);
         }

        advImage.setLayoutParams(lp);
       // advImage.requestLayout();
        advImage.setVisibility(View.VISIBLE);

        ImageLoad.displaynoCacheImage(context,R.drawable.icon_default, config.params.image, advImage);
    }

    public void showAnim(){
        super.show();

        if(mCoverImg==null) return;
        // top logo by left
        //mLeftLogoImg = (ImageView) findViewById(R.id.left_logo_imageview);
        final SpringAnimation leftLogoAnimY = new SpringAnimation(mCoverImg, SpringAnimation.TRANSLATION_Y, 0);
        leftLogoAnimY.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);
        leftLogoAnimY.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
        leftLogoAnimY.setStartVelocity(-2000);

        final ValueAnimator logoAlphaAnim = ObjectAnimator.ofFloat(0f, 1f);
        logoAlphaAnim.setDuration(600);
        logoAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCoverImg.setAlpha((Float) valueAnimator.getAnimatedValue());
                //mRightLogoImg.setAlpha((Float) valueAnimator.getAnimatedValue());
            }
        });

        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                leftLogoAnimY.start();
                logoAlphaAnim.start();
            }
        }, 1000);
    }
}
