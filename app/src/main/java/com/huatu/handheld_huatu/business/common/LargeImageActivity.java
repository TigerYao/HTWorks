package com.huatu.handheld_huatu.business.common;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.baijiayun.glide.request.target.SimpleTarget;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.widget.ProgressWheel;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by kaelli on 2016/7/1.
 */
public class LargeImageActivity extends Activity {
    private static final String TAG = "LargeImageActivity";
    private ImageView largeImage;
    PhotoViewAttacher mAttacher;
    String imageUrl;
    private FrameLayout rootView;
    ProgressWheel mProgressBar;
    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        imageUrl = getIntent().getStringExtra("image_url");

        LogUtils.e("LargeImageActivity",imageUrl+"");
        setContentView(R.layout.activity_largeimage);
        rootView = (FrameLayout) findViewById(R.id.rootView);

        mProgressBar = (ProgressWheel) this.findViewById(R.id.loading_icon);

        largeImage = (ImageView) findViewById(R.id.iv_large_image);
        ArenaDataCache.getInstance().inBackgroundTime = System.currentTimeMillis();
        GlideApp.with(this)
                .load(imageUrl)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Drawable>() {

                    //public void onResourceReady(Drawable resource, GlideAnimation<? super Drawable> glideAnimation)
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition){
                        largeImage.setImageDrawable(resource);
                        if (mProgressBar.isSpinning()) {
                            mProgressBar.stopSpinning();
                        }
                        mAttacher = new PhotoViewAttacher(largeImage);
                        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                            @Override
                            public void onPhotoTap(View view, float v, float v1) {
                                startDismissMiss();
                            }

                            @Override
                            public void onOutsidePhotoTap() {
                                startDismissMiss();
                            }
                        });
                    }
                });

        if (!mProgressBar.isSpinning()) {
            mProgressBar.spin();
        }
    }

   /* @Override
    protected void onDestroy() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onDestroy();
    }*/

    @Override
    public void onBackPressed() {
        startDismissMiss();
    }

    public static final int ANIMATE_DURATION = 300;

    private void startDismissMiss() {
        final View view = largeImage;// imagePreviewAdapter.getPrimaryItem();
        final ImageView imageView = largeImage;
        if (imageView == null || imageView.getDrawable() == null) {
            finish();
            overridePendingTransition(0, 0);
            return;
        }
        ValueAnimator missAnimator = ValueAnimator.ofFloat(0, 1f);
        missAnimator.setInterpolator(new AccelerateInterpolator());
        missAnimator.setDuration(ANIMATE_DURATION);
        missAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                float scale = .5f * fraction + 1.f;
                float alpha = 1.f - fraction;

                view.setScaleX(scale);
                view.setScaleY(scale);
                view.setAlpha(alpha);
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.WHITE, Color.TRANSPARENT));
            }
        });
        addOutListener(missAnimator);
        missAnimator.start();
    }

    /**
     * Argb 估值器
     */
    public int evaluateArgb(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return (startA + (int) (fraction * (endA - startA))) << 24//
                | (startR + (int) (fraction * (endR - startR))) << 16//
                | (startG + (int) (fraction * (endG - startG))) << 8//
                | (startB + (int) (fraction * (endB - startB)));
    }

    /**
     * 退场动画过程监听
     */
    private void addOutListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(0xfff);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }


    public static void newIntent(Context context, String imageUrl) {
        Intent intent = new Intent(context, LargeImageActivity.class);
        if (!TextUtils.isEmpty(imageUrl)) {
            intent.putExtra("image_url", imageUrl);
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new ArenaExamMessageEvent(ArenaExamMessageEvent.LARGE_IMG_SHOW_FINISH));
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }
}
