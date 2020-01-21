package com.huatu.handheld_huatu.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;


import com.huatu.animation.BaseAnimatorSet;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.helper.SimpleAnimListener;
import com.huatu.handheld_huatu.helper.SimpleAnimationListener;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.ViewAnimHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Terry
 * Date : 2016/3/30 0030.
 * Email: terry@xiaodao360.com
 */
public class AnimUtils {

    public static class SlideBottomExit extends BaseAnimatorSet {

        public SlideBottomExit(Context context){
            interpolator(AnimationUtils.loadInterpolator(context, R.interpolator.msf_interpolator));

        }

        //	ObjectAnimator.ofFloat(view, "translationY", -250 * dm.density, 30, -10, 0));
        @Override
        public void setAnimation(View view) {
            DisplayMetrics dm = view.getContext().getResources().getDisplayMetrics();
            animatorSet.playTogether(//
                    ObjectAnimator.ofFloat(view, "translationY", 0,40,-10, -350 * dm.density), //
                    ObjectAnimator.ofFloat(view, "alpha", 1, 0.1f));
        }
    }

    public static void scaleView(final View firstView){
        ObjectAnimator fViewScaleXAnim = ObjectAnimator.ofFloat(firstView, "scaleX",1.0f, 0.5f, 1.1f, 1.0f);
        fViewScaleXAnim.setDuration(400);

        ObjectAnimator fViewScaleYAnim = ObjectAnimator.ofFloat(firstView, "scaleY",1.0f, 0.5f, 1.1f, 1.0f);
        fViewScaleYAnim.setDuration(400);

 /*       ObjectAnimator fViewAlphaAnim = ObjectAnimator.ofFloat(firstView, "alpha",1.0f, 0.6f, 1.0f, 1.0f);
        fViewAlphaAnim.setDuration(400);*/
        AnimatorSet showAnim = new AnimatorSet();
        Interpolator interpolator = AnimationUtils.loadInterpolator(firstView.getContext(),
                R.interpolator.msf_interpolator);
        showAnim.setInterpolator(interpolator);
        showAnim.playTogether(fViewScaleXAnim,  fViewScaleYAnim);//fViewAlphaAnim,

        showAnim.start();
    }

    public static void swingView(final View firstView){
        AnimatorSet showAnim = new AnimatorSet();
        ObjectAnimator tmpAnim=ObjectAnimator.ofFloat(firstView, "rotation", 0, 10, -10, 6, -6, 3, -3, 0);
        tmpAnim.setDuration(1500);
        showAnim.playTogether(tmpAnim);
        showAnim.start();
    }

    public static void translateShowView(final View firstView) {

        firstView.setVisibility(View.VISIBLE);
        TranslateAnimation mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 100f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        mAnimation.setDuration(400);

        Interpolator interpolator = AnimationUtils.loadInterpolator(firstView.getContext(),
                R.interpolator.msf_interpolator);
        //mAnimation.setFillAfter(true);
        mAnimation.setInterpolator(interpolator);

        firstView.startAnimation(mAnimation);
    }

    public static void fastShowIcon(final View firstView) {
        firstView.setVisibility(View.VISIBLE);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(90);
        scaleAnimation.setInterpolator(new DecelerateInterpolator());
        // scaleAnimation.setFillAfter(true);
        //scaleAnimation.start();
        scaleAnimation.setStartOffset(90);
        firstView.startAnimation(scaleAnimation);
    }

    public static void fastShowbtn(final View firstView) {
        firstView.setVisibility(View.VISIBLE);

        ObjectAnimator fViewScaleXAnim = ObjectAnimator.ofFloat(firstView, "scaleX", 0.8f, 1.0f);
        fViewScaleXAnim.setDuration(90);

        ObjectAnimator fViewScaleYAnim = ObjectAnimator.ofFloat(firstView, "scaleY", 0.8f, 1.0f);
        fViewScaleYAnim.setDuration(90);

        ObjectAnimator fViewAlphaAnim = ObjectAnimator.ofFloat(firstView, "alpha", 0.9f, 1.0f);
        fViewAlphaAnim.setDuration(90);
        AnimatorSet showAnim = new AnimatorSet();
        showAnim.setInterpolator(new DecelerateInterpolator());
        showAnim.playTogether(fViewScaleXAnim, fViewAlphaAnim, fViewScaleYAnim);

        showAnim.start();
    }



    public static void showNotify(final View target){
        ViewAnimHelper.setTranslationY(target, 0);
        target.setVisibility(View.VISIBLE);

        AnimatorSet showAnim = new AnimatorSet();
        showAnim.playTogether(ObjectAnimator.ofFloat(target, "alpha", 0.8f, 1, 1, 1),
                ObjectAnimator.ofFloat(target, "scaleX", 0.8f, 1.03f, 0.95f, 1));
        showAnim.setDuration(300);
        showAnim.start();
    }

    public static void hideNotify(final View target){
    /*    AnimatorSet showAnim = new AnimatorSet();
        showAnim.playTogether(ObjectAnimator.ofFloat(target, "alpha", 1, 0));
        showAnim.setDuration(300);
        showAnim.start();
        showAnim.addListener(new SimpleAnimListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                target.setVisibility(View.GONE);
            }
        });*/
        int distance = DensityUtils.dp2px(target.getContext(), 32);
        String propertyName = "translationY";
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, propertyName, 0, -distance);
        animator.setDuration(200);
        animator.start();
    }


    public static void fastHidebtn(final View firstView) {
        // firstView.setVisibility(View.VISIBLE);
        ObjectAnimator fViewScaleXAnim = ObjectAnimator.ofFloat(firstView, "scaleX", 1.0f, 0.8f);
        fViewScaleXAnim.setDuration(90);

        ObjectAnimator fViewScaleYAnim = ObjectAnimator.ofFloat(firstView, "scaleY", 1.0f, 0.8f);
        fViewScaleYAnim.setDuration(90);

        ObjectAnimator fViewAlphaAnim = ObjectAnimator.ofFloat(firstView, "alpha", 1.0f, 0.9f);
        fViewAlphaAnim.setDuration(90);
        AnimatorSet showAnim = new AnimatorSet();
        showAnim.setInterpolator(new DecelerateInterpolator());
        showAnim.playTogether(fViewScaleXAnim, fViewAlphaAnim, fViewScaleYAnim);
        showAnim.addListener(new SimpleAnimListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                firstView.setVisibility(View.GONE);
            }
        });
        showAnim.start();
    }

    public static void hiddenScaleAndTrans(final View firstView, float fHeight) {
/*

        if(true){

            final Animation mScaleAnimation = AnimationUtils.loadAnimation(firstView.getContext(),
                    R.anim.scale_out_animation);
            firstView.clearAnimation();
            firstView.startAnimation(mScaleAnimation);
            return;
        }
*/

        firstView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        ObjectAnimator fViewScaleXAnim = ObjectAnimator.ofFloat(firstView, "scaleX", 0.9f, 1.0f);
        fViewScaleXAnim.setDuration(350);
        ObjectAnimator fViewScaleYAnim = ObjectAnimator.ofFloat(firstView, "scaleY", 0.9f, 1.0f);
        fViewScaleYAnim.setDuration(350);
        ObjectAnimator fViewAlphaAnim = ObjectAnimator.ofFloat(firstView, "alpha", 0.5f, 1.0f);
        fViewAlphaAnim.setDuration(350);
        // ObjectAnimator fViewRotationXAnim = ObjectAnimator.ofFloat(firstView, "rotationX", 0f, 10f);
        // fViewRotationXAnim.setDuration(200);
        // ObjectAnimator fViewResumeAnim = ObjectAnimator.ofFloat(firstView, "rotationX", 10f, 0f);
        //  fViewResumeAnim.setDuration(150);
        //  fViewResumeAnim.setStartDelay(200);
      /*  ObjectAnimator fViewTransYAnim=ObjectAnimator.ofFloat(firstView,"translationY",-0.1f* fHeight,0);
        fViewTransYAnim.setDuration(350);*/


        AnimatorSet showAnim = new AnimatorSet();
        // showAnim.playTogether(fViewScaleXAnim,fViewRotationXAnim,fViewResumeAnim,fViewTransYAnim,fViewAlphaAnim,fViewScaleYAnim);
        showAnim.playTogether(fViewScaleXAnim, fViewAlphaAnim, fViewScaleYAnim);
        showAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                firstView.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        showAnim.start();
    }

    public static void startScaleAndTrans(final View firstView, float fHeight) {

/*
        if(true){

            final Animation mScaleAnimation = AnimationUtils.loadAnimation(firstView.getContext(),
                    R.anim.scale_animation);

            mScaleAnimation.setDuration(800);
            mScaleAnimation.setFillAfter(true);
            firstView.clearAnimation();
            firstView.startAnimation(mScaleAnimation);
            return;
        }*/
        firstView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        ObjectAnimator fViewScaleXAnim = ObjectAnimator.ofFloat(firstView, "scaleX", 1.0f, 0.9f);
        fViewScaleXAnim.setDuration(350);


        ObjectAnimator fViewScaleYAnim = ObjectAnimator.ofFloat(firstView, "scaleY", 1.0f, 0.9f);
        fViewScaleYAnim.setDuration(350);

        ObjectAnimator fViewAlphaAnim = ObjectAnimator.ofFloat(firstView, "alpha", 1.0f, 0.5f);
        fViewAlphaAnim.setDuration(350);


        // ObjectAnimator fViewRotationXAnim = ObjectAnimator.ofFloat(firstView, "rotationX", 0f, 10f);
        // fViewRotationXAnim.setDuration(200);

        //  ObjectAnimator fViewResumeAnim = ObjectAnimator.ofFloat(firstView, "rotationX", 10f, 0f);
        //  fViewResumeAnim.setDuration(150);

        // fViewResumeAnim.setStartDelay(200);
        // ObjectAnimator fViewTransYAnim=ObjectAnimator.ofFloat(firstView,"translationY",0,-0.1f* fHeight);
        // fViewTransYAnim.setDuration(350);

        AnimatorSet showAnim = new AnimatorSet();
        // showAnim.playTogether(fViewScaleXAnim,fViewRotationXAnim,fViewResumeAnim,fViewTransYAnim,fViewAlphaAnim,fViewScaleYAnim);
        showAnim.playTogether(fViewScaleXAnim, fViewAlphaAnim, fViewScaleYAnim);
        showAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                firstView.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        showAnim.start();
    }


    public static void startTagAnimationSet(View v) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
        animationSet.addAnimation(new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        animationSet.setDuration(1000);
        animationSet.setFillAfter(true);
        animationSet.cancel();
        animationSet.reset();
        v.startAnimation(animationSet);
    }


    public static void repeatMoveAnim(View targetView) {


        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -8 * DisplayUtil.getDensity());
        //animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(400);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        targetView.startAnimation(animation);
    }

    public static void animTopShow(final View targetView,boolean isShow) {
        if (targetView == null)
            return;
        targetView.setVisibility(View.VISIBLE);
        TranslateAnimation mAnimation;
        if(isShow)
          mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0f);
        else
            mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,-1.0f);
        mAnimation.setDuration(500);
        mAnimation.setInterpolator(new DecelerateInterpolator());
        targetView.clearAnimation();
        if(!isShow){
            mAnimation.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    targetView.setVisibility(View.GONE);
                }
            });
        }
        targetView.startAnimation(mAnimation);
    }

    public static void animHorShow(final View targetView,boolean isShow) {
        targetView.setVisibility(View.VISIBLE);

        TranslateAnimation mAnimation;
        if(isShow)
           mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,  1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        else
            mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,  0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        mAnimation.setDuration(400);
        mAnimation.setInterpolator(new DecelerateInterpolator());
        //  mAnimation.setFillAfter(true);
        targetView.clearAnimation();
        if(!isShow){
           mAnimation.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    targetView.setVisibility(View.GONE);
                }
            });
        }
        targetView.startAnimation(mAnimation);
    }

    public static void AlphaTimeShow(final View targetView,boolean isShow,int time) {
        targetView.setVisibility(View.VISIBLE);
        AlphaAnimation mAnimation;
        if(isShow)
            mAnimation = new AlphaAnimation(0f, 1f);
        else
            mAnimation = new AlphaAnimation(1f, 0f);
        mAnimation.setDuration(time);
        mAnimation.setInterpolator(new DecelerateInterpolator());
        //  mAnimation.setFillAfter(true);
        targetView.clearAnimation();
        if(!isShow){
            mAnimation.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    targetView.setVisibility(View.INVISIBLE);
                }
            });
        }
        targetView.startAnimation(mAnimation);
    }

    public static void AlphaShow(final View targetView,boolean isShow) {
        targetView.setVisibility(View.VISIBLE);
        AlphaAnimation mAnimation;
        if(isShow)
            mAnimation = new AlphaAnimation(0f, 1f);
        else
            mAnimation = new AlphaAnimation(1f, 0f);
        mAnimation.setDuration(400);
        mAnimation.setInterpolator(new DecelerateInterpolator());
        //  mAnimation.setFillAfter(true);
        targetView.clearAnimation();
        if(!isShow){
            mAnimation.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    targetView.setVisibility(View.GONE);
                }
            });
        }
        targetView.startAnimation(mAnimation);
    }

    public static void AlphaTagShow(final View targetView,final boolean isShow) {
        targetView.setVisibility(View.VISIBLE);
        targetView.setTag("0");
        AlphaAnimation mAnimation;
        if(isShow)
            mAnimation = new AlphaAnimation(0f, 1f);
        else
            mAnimation = new AlphaAnimation(1f, 0f);
        mAnimation.setDuration(400);
        mAnimation.setInterpolator(new DecelerateInterpolator());
        //  mAnimation.setFillAfter(true);
         targetView.clearAnimation();

         mAnimation.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if(!isShow) {
                        targetView.setVisibility(View.GONE);
                        targetView.setTag("1");
                    }else {
                        targetView.setTag("1");
                    }
                }
            });

        targetView.startAnimation(mAnimation);
    }

    public static void animLeftHorShow(final View targetView,boolean isShow) {
        targetView.setVisibility(View.VISIBLE);
        TranslateAnimation mAnimation;
        if(isShow)
            mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, - 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        else
            mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,  0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        mAnimation.setDuration(400);
        mAnimation.setInterpolator(new DecelerateInterpolator());
        //  mAnimation.setFillAfter(true);
        targetView.clearAnimation();
        if(!isShow){
            mAnimation.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                   if(targetView!=null) targetView.setVisibility(View.GONE);
                }
            });
        }
        targetView.startAnimation(mAnimation);
    }

    public static void animTopHide(final View targetView) {
        targetView.setVisibility(View.VISIBLE);
        TranslateAnimation mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.0f);
        // mAnimation.setStartOffset(100);
        mAnimation.setDuration(400);
        //mAnimation.setFillAfter(true);
        mAnimation.setInterpolator(new DecelerateInterpolator());
        targetView.clearAnimation();
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                targetView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        targetView.startAnimation(mAnimation);
    }

    public static void animBottom(View targetView, boolean isShow) {
        float distance = DisplayUtil.getDensity() * 100;
        TranslateAnimation animation;
        if (!isShow) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                    Animation.ABSOLUTE, distance, Animation.ABSOLUTE, 0);

        } else {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                    Animation.ABSOLUTE, 0, Animation.ABSOLUTE, distance);
        }
        animation.setDuration(300);
        Interpolator interpolator = AnimationUtils.loadInterpolator(targetView.getContext(),
                R.interpolator.msf_interpolator);
        animation.setInterpolator(interpolator);
        animation.setFillAfter(true);
        targetView.clearAnimation();
        targetView.startAnimation(animation);
    }

    public static void animBottomShow(final View targetView) {

        targetView.setVisibility(View.VISIBLE);
        TranslateAnimation mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        mAnimation.setDuration(500);

        Interpolator interpolator = AnimationUtils.loadInterpolator(targetView.getContext(),
                R.interpolator.msf_interpolator);
        mAnimation.setInterpolator(interpolator);
        targetView.clearAnimation();
        targetView.startAnimation(mAnimation);
    }

    public static void animBottomHide(final View targetView,boolean isLinear) {

        targetView.setVisibility(View.VISIBLE);
        TranslateAnimation mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        mAnimation.setDuration(600);

        Interpolator interpolator = isLinear? new LinearInterpolator():AnimationUtils.loadInterpolator(targetView.getContext(),
                R.interpolator.msf_interpolator);
        // mAnimation.setFillAfter(true);
        mAnimation.setInterpolator(interpolator);
        targetView.clearAnimation();
        mAnimation.setAnimationListener(new SimpleAnimationListener() {
             @Override
            public void onAnimationEnd(Animation animation) {
                targetView.setVisibility(View.GONE);
            }
        });
        targetView.startAnimation(mAnimation);

    }

    public static void animOnceBottomHide(final View targetView) {

        targetView.setVisibility(View.VISIBLE);
        TranslateAnimation mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        mAnimation.setDuration(500);

        Interpolator interpolator = AnimationUtils.loadInterpolator(targetView.getContext(),
                R.interpolator.msf_interpolator);
        // mAnimation.setFillAfter(true);
        mAnimation.setInterpolator(interpolator);
        mAnimation.setStartOffset(200);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                targetView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        targetView.startAnimation(mAnimation);

    }

    public static ObjectAnimator animateBottomHide(View targetView,int distance) {
         return animationFromTo(targetView, 0, distance);
    }


    public static ObjectAnimator animateBottomHide(View targetView) {
        int distance = DensityUtils.dp2px(targetView.getContext(), 55);
        return animationFromTo(targetView, 0, distance);
    }

    public static ObjectAnimator animateBottomShow(View targetView) {
        return animationFromTo(targetView, targetView.getTranslationY(), 0);
    }

    public static ObjectAnimator animateHide(View targetView) {
        int distance = -targetView.getBottom();
        return animationFromTo(targetView, targetView.getTranslationY(), distance);
    }

    public static ObjectAnimator animateShow(View targetView) {
        return animationFromTo(targetView, targetView.getTranslationY(), 0);
    }

    public static ObjectAnimator animationFromTo(View view, float start, float end) {
        String propertyName = "translationY";
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, propertyName, start, end);
         animator.start();
        return animator;
    }

    public static void animAttention(final View likeView) {

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new AlphaAnimation(1.0f, 0.0f));
        animationSet.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        animationSet.setDuration(1000);
        animationSet.setFillAfter(true);
        animationSet.cancel();
        animationSet.reset();
        likeView.startAnimation(animationSet);
        likeView.setClickable(false);
    }

    public static void AlphaIn(View likeView) {

        AlphaAnimation var11 = new AlphaAnimation(0.0F, 1.0F);
        var11.setDuration(150L);
        //var11.setFillAfter(true);
        var11.setInterpolator(new DecelerateInterpolator());

        likeView.startAnimation(var11);
    }

    public static void animScale(final View likeView) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(200);

        scaleAnimation.setFillAfter(true);
        scaleAnimation.start();
        likeView.startAnimation(scaleAnimation);
        likeView.postDelayed(new Runnable() {

            @Override
            public void run() {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(200);
                scaleAnimation.setFillAfter(true);
                likeView.startAnimation(scaleAnimation);
            }

        }, 200);
    }

    public static void animAlpha(final View likeView) {

        likeView.postDelayed(new Runnable() {

            @Override
            public void run() {
                AlphaAnimation var11 = new AlphaAnimation(0.0F, 1.0F);

                var11.setDuration(100L);
                var11.setFillAfter(true);
                var11.setInterpolator(new DecelerateInterpolator());
                var11.start();
                likeView.startAnimation(var11);
            }

        }, 100);


      /*  //表示动画效果Interpolator共享
        AnimationSet animationSet = new AnimationSet(true);
        //前四个参数表示从原来大小的100%缩小到10%，后四个参数是为确定“中心点”
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.1f, 1,
                0.1f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animationSet.addAnimation(scaleAnimation);
        //动画效果推迟1秒钟后启动
        animationSet.setStartOffset(100);
        //如果值为true，控件则保持动画结束的状态
        animationSet.setFillAfter(true);
        //如果值为false，控件则保持动画结束的状态
        animationSet.setFillBefore(false);
        //动画效果重复3次
        //animationSet.setRepeatCount(3);
        animationSet.setDuration(2000);
        likeView.startAnimation(animationSet);*/
    }

    /**
     * 菜单打开旋转动画
     */
    public static void showOpenRotateAnimation(final View view) {
        final RotateAnimation animation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(350);//设置动画持续时间
         animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态

      /*  animation.setAnimationListener(new SimpleAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setRotation(180);
            }
        });*/
        view.startAnimation(animation);
    }

    /**
     * 菜单关闭旋转动画
     */
    public static void showCloseRotateAnimation(final View view) {
        final RotateAnimation animation = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setDuration(350);//设置动画持续时间
        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
       /* animation.setAnimationListener(new SimpleAnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setRotation(0);
            }
        });*/
        view.startAnimation(animation);
    }

    public static void showOpenRotation(final View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 180f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(350);
        animator.start();
    }

    public static void showCloseRotation(final View view) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 180f, 0f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(350);
        animator.start();
    }



    public static void startRotateAnimation(View view){

        RotateAnimation mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1200);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
        view.startAnimation(mRotateAnimation);
    }

/*
    */

    /**
     * @return
     *//*
    private AnimationSet createAnimation() {
        mAnimationSet = new AnimationSet(true);
        TranslateAnimation translateAnim = new TranslateAnimation(0, 0, mFromY, -mToY);
        AlphaAnimation alphaAnim = new AlphaAnimation(mFromAlpha, mToAlpha);
        mAnimationSet.addAnimation(translateAnim);
        mAnimationSet.addAnimation(alphaAnim);
        mAnimationSet.setDuration(mDuration);
        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShowing()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                        }
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return mAnimationSet;
    }*/


    /*

        android.animation.AnimatorSet bouncer = new android.animation.AnimatorSet();

        AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.0F, 1.0F);
        localAlphaAnimation.setInterpolator(new DecelerateInterpolator());



        TranslateAnimation localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, 80, 0.0F);
        localTranslateAnimation.setInterpolator(new DecelerateInterpolator());


     ///   bouncer.playTogether(localAlphaAnimation);
        bouncer.setDuration(300).start();


        mProductView.setVisibility(mProductView.getVisibility()== View.VISIBLE?View.GONE:View.VISIBLE);*/

    public static void alphaDark(View view) {
        ObjectAnimator fViewAlphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.5f);
        fViewAlphaAnim.setDuration(450);
        fViewAlphaAnim.start();
//        AnimatorSet showAnim = new AnimatorSet();
//        showAnim.play(fViewAlphaAnim);
//        showAnim.start();
    }

    public static void alphaLight(View view) {
        ObjectAnimator fViewAlphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0.5f, 1.0f);
        fViewAlphaAnim.setDuration(450);
        fViewAlphaAnim.start();
//        AnimatorSet showAnim = new AnimatorSet();
//        showAnim.play(fViewAlphaAnim);
//        showAnim.start();
    }

    public static void rotateView(View view){
        RotateAnimation animationRed = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        animationRed.setDuration(600);
        animationRed.setRepeatCount(-1);
        animationRed.setFillAfter(true);
        animationRed.setInterpolator(new LinearInterpolator());
        view.startAnimation(animationRed);
    }

    public static void rotate3View(View view){
        RotateAnimation animationRed = new RotateAnimation(0f, 3*360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        animationRed.setDuration(1800);

        animationRed.setFillAfter(true);
        animationRed.setInterpolator(new LinearInterpolator());
        view.startAnimation(animationRed);

    }

    /**
     *  点赞操作动画

     */
/*    public static void thumbActionAnim(Activity activity, View v) {
        FloatingText scaleFloatingText = new FloatingText.FloatingTextBuilder(activity)
                .textColor(Color.RED)
                .textSize(60)
                .offsetY(-60)
                .floatingAnimatorEffect(new ScaleFloatingAnimator())
                .textContent("+1").build();
        scaleFloatingText.attach2Window();
        scaleFloatingText.startFloating(v);
    }*/

    public static void thumbScaleAnim(final View likeView) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1.0f, 0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(200);

        scaleAnimation.setFillAfter(true);
        scaleAnimation.start();
        likeView.startAnimation(scaleAnimation);
//        likeView.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                ScaleAnimation scaleAnimation = new ScaleAnimation(1.5f, 1.0f, 1.5f, 1.0f,
//                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                scaleAnimation.setDuration(200);
//                scaleAnimation.setFillAfter(true);
//                likeView.startAnimation(scaleAnimation);
//            }
//
//        }, 200);
    }


    public static void startShakeByPropertyAnim(View view,float shakeDegrees, long duration) {
        if (view == null) {
            return;
        }
        //先往左再往右
        PropertyValuesHolder rotateValuesHolder = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(0.05f, -shakeDegrees),
                Keyframe.ofFloat(0.1f, shakeDegrees),
                Keyframe.ofFloat(0.15f, -shakeDegrees),
                Keyframe.ofFloat(0.2f, shakeDegrees),
                Keyframe.ofFloat(0.25f, -shakeDegrees),
                Keyframe.ofFloat(0.3f, shakeDegrees),
                Keyframe.ofFloat(0.35f, -shakeDegrees),
                Keyframe.ofFloat(0.4f, shakeDegrees),
                Keyframe.ofFloat(0.5f, -shakeDegrees),
                Keyframe.ofFloat(0.6f, shakeDegrees),
                Keyframe.ofFloat(0.7f, -shakeDegrees),
                Keyframe.ofFloat(0.8f, shakeDegrees),
                Keyframe.ofFloat(0.9f, 0f),
                Keyframe.ofFloat(1.0f, 0f)
        );

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, rotateValuesHolder);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();
    }

    public static void startTopDownAnim(View view,int transY, long duration) {
        startTopDownAnim(view, 3.0f, 6.0f, 3000);

    }

    public static void startTopDownAnim(View view,float transX, float tranY, long duration) {
        if (view == null) {
            return;
        }
        List<Animator> animators = new ArrayList<>();
        ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(view, "translationX", -transX,transX,-transX);
        translationXAnim.setDuration(duration);
        translationXAnim.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        translationXAnim.setRepeatMode(ValueAnimator.RESTART);//
        translationXAnim.start();
        animators.add(translationXAnim);
        ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(view, "translationY", -tranY,tranY,-tranY);
        translationYAnim.setDuration(duration + 1000);
        translationYAnim.setRepeatCount(ValueAnimator.INFINITE);
        translationYAnim.setRepeatMode(ValueAnimator.REVERSE);
        translationYAnim.start();
        animators.add(translationYAnim);

        AnimatorSet btnSexAnimatorSet = new AnimatorSet();
        btnSexAnimatorSet.playTogether(animators);
        btnSexAnimatorSet.setInterpolator(new LinearInterpolator());
        btnSexAnimatorSet.setStartDelay(duration);
        btnSexAnimatorSet.start();

    }

    public static void startTopDown(View view){
        ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(view, "translationY", -3.0f,3.0f,-3.0f);
        translationXAnim.setDuration(800);
        translationXAnim.setRepeatCount(4);//无限循环
        translationXAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        translationXAnim.start();
    }

    public static void translationSideAnim(final View view, int from, int to, final boolean show){
        if (show && view.getVisibility() == View.VISIBLE)
            return;
        if (!show && view.getVisibility() != View.VISIBLE)
            return;
        int begin = show ? from : to;
        int end = show ? to : from;
        view.clearAnimation();
        ObjectAnimator transLationXAnm = ObjectAnimator.ofFloat(view, "translationX", begin, end);
        transLationXAnm.addListener(new SimpleAnimListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (view.getVisibility() == View.GONE && show)
                    view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (view.getVisibility() == View.VISIBLE && !show)
                    view.setVisibility(View.GONE);
            }
        });
        transLationXAnm.start();
    }

    /**
     * 萝卜摇摆，左边与右边是向上移动，中间在原位置
     * 摇摆规律是从左到中间到右边，然后翻转
     * 背景椭圆阴影缩放动画
     * @param view
     */
    public static AnimatorSet loadingAnim(View view, View bgView) {
        if (view == null) {
            return null;
        }

        PropertyValuesHolder rotateValuesHolder = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0.0f, 60f),
                Keyframe.ofFloat(0.5f, -60f),
                Keyframe.ofFloat(1.0f, -180f)
        );

     /*
       消除屏幕差距
      PropertyValuesHolder transYValuesHolder = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y,
                Keyframe.ofFloat(0f, -80f),
                Keyframe.ofFloat(0.5f, 20f),
                Keyframe.ofFloat(1.0f, -80f)
        );*/

        float offset = DensityUtils.dp2floatpx(UniApplicationContext.getContext(),10);
        PropertyValuesHolder transYValuesHolder = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y,
                Keyframe.ofFloat(0f, -offset*3),
                Keyframe.ofFloat(0.5f, offset*0.75f),
                Keyframe.ofFloat(1.0f, -offset*3)
        );

        PropertyValuesHolder scaleXValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 0.3f),
                Keyframe.ofFloat(0.5f, 1f),
                Keyframe.ofFloat(1.0f, 0.3f)
        );

        PropertyValuesHolder scaleYValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 0.3f),
                Keyframe.ofFloat(0.5f, 1f),
                Keyframe.ofFloat(1.0f, 0.3f)
        );

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator objectAnimatorView2 = ObjectAnimator.ofPropertyValuesHolder(bgView, scaleXValuesHolder, scaleYValuesHolder);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, rotateValuesHolder, transYValuesHolder);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimatorView2.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimatorView2.setRepeatCount(ValueAnimator.INFINITE);
        animatorSet.playTogether(objectAnimator, objectAnimatorView2);
        animatorSet.start();
        return animatorSet;
    }
}
