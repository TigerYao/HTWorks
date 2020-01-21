package com.huatu.handheld_huatu.view.photo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;


import com.baijiayun.glide.Glide;
import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.resource.gif.GifDrawable;
import com.baijiayun.glide.request.target.SimpleTarget;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ResourceUtils;

/**
 * HPhotoView
 * Created by hanks on 17-11-8.
 */

public class TransitionImageView extends AppCompatImageView {
    private final static Property<View, Rect> BOUNDS =
            new Property<View, Rect>(Rect.class, "bounds") {
                @Override
                public void set(View object, Rect value) {
                    //object.layout(value.left, value.top, value.right, value.bottom);
                    object.setTranslationX(value.left);
                    object.setTranslationY(value.top);
                    object.getLayoutParams().width = value.width();
                    object.getLayoutParams().height = value.height();
                    object.requestLayout();
                }

                @Override
                public Rect get(View object) {
                    Rect rect = new Rect();
                    rect.left = (int) object.getTranslationX();
                    rect.top = (int) object.getTranslationY();
                    rect.right = rect.left + object.getWidth();
                    rect.bottom = rect.top + object.getHeight();
                    return rect;
                }
            };
    boolean beginLoad = false;
    private int statusBarHeight;
    private int imageW, imageH;   // 原图大小
    private int targetW, targetH; // 屏幕上 imageView 的大小
    private boolean isEnterAnim;
    private Rect thumbnailBounds, fullBounds;
    private Matrix thumbnailMatrix, fullMatrix;
    private int startColor, endColor;
    private Animator.AnimatorListener enterAnimatorListener;
    private Drawable thumbnailDrawable;
    private PictureData pictureData;

    public TransitionImageView(Context context) {
        this(context, null);
    }

    public TransitionImageView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public TransitionImageView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        statusBarHeight = DisplayUtil.getStatuBarHeight(getContext());
    }



    public void setEndColor(int endColor) {
        this.endColor = endColor;
    }

    public Matrix getFullMatrix() {
        return fullMatrix;
    }

    public void setFullMatrix(float[] values) {
        fullMatrix = new Matrix();
        fullMatrix.setValues(values);
    }

    private void createAnimator(final boolean in, Animator.AnimatorListener listener) {
        if (in && !isEnterAnim) {
            ((ViewGroup) getParent()).setBackgroundColor(endColor);
            setTranslationX(fullBounds.left);
            setTranslationY(fullBounds.top);
            getLayoutParams().width = fullBounds.width();
            getLayoutParams().height = fullBounds.height();
            setImageMatrix(new Matrix(fullMatrix));
            requestLayout();
            if (listener != null) {
                listener.onAnimationEnd(null);
            }
            if (enterAnimatorListener != null) {
                enterAnimatorListener.onAnimationEnd(null);
            }
            return;
        }

        Animator bgAnimator = ObjectAnimator.ofObject((ViewGroup) getParent(), "BackgroundColor",
                new ArgbEvaluator(), isEnterAnim && in ? startColor : endColor, in ? endColor : startColor);
        Animator boundsAnimator = ObjectAnimator.ofObject(this, BOUNDS,
                new RectEvaluator(), isEnterAnim && in ? thumbnailBounds : fullBounds, in ? fullBounds : thumbnailBounds);
        Animator matrixAnimator = ObjectAnimator.ofObject(this, "imageMatrix",
                new MatrixEvaluator(), isEnterAnim && in ? thumbnailMatrix : fullMatrix, in ? fullMatrix : thumbnailMatrix);
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(
                boundsAnimator,
                matrixAnimator,
                bgAnimator);
        animator.setDuration(300);
        animator.start();
        if (listener != null) {
            animator.addListener(listener);
        }
        if (in && enterAnimatorListener != null) {
            animator.addListener(enterAnimatorListener);
        }
    }

    public void setEnterAnimationListener(Animator.AnimatorListener listener) {
        this.enterAnimatorListener = listener;
    }

    public void runFinishAnimation(final Animator.AnimatorListener listener) {
        createAnimator(false, listener);
    }


    private Drawable mFirstDrawable;
    public void setInitData(PictureData pictureData,Drawable firstDrawable) {
        this.pictureData = pictureData;
        mFirstDrawable=firstDrawable;
        imageW = pictureData.imageSize[0];
        imageH = pictureData.imageSize[1];
        targetW = DisplayUtil.getScreenWidth();
        targetH = DisplayUtil.getScreenHeight();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            targetH -= statusBarHeight;
        }

        int oldW = pictureData.size[0];
        int oldH = pictureData.size[1];

        int oldX = pictureData.location[0];
        int oldY = pictureData.location[1];


        thumbnailBounds = new Rect(oldX, oldY, oldX + oldW, oldY + oldH);
        fullBounds = new Rect(0, 0, targetW, targetH);
    }

    public void reset(){
        beginLoad=false;
        this.setVisibility(VISIBLE);
    }

    public void setEnableInAnima(boolean inAnima) {
        this.isEnterAnim = inAnima;
    }

    public void setRemoteDrawable(Drawable resource,boolean hasAnim){
        thumbnailDrawable = resource;
        imageH = resource.getIntrinsicHeight();
        imageW = resource.getIntrinsicWidth();
        int oldW = pictureData.size[0];
        int oldH = pictureData.size[1];

        int oldX = pictureData.location[0];
        int oldY = pictureData.location[1];

       // Context context = getContext();
      //  thumbnailBounds = new Rect(oldX, oldY, oldX + oldW, oldY + oldH);
        thumbnailBounds.set(oldX, oldY, oldX + oldW, oldY + oldH);
        //fullBounds = new Rect(0, 0, targetW, targetH);
        fullBounds.set(0, 0, targetW, targetH);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            thumbnailBounds.top -= statusBarHeight;
            thumbnailBounds.bottom -= statusBarHeight;
            fullBounds.top -= statusBarHeight;
            fullBounds.bottom -= statusBarHeight;
        }
        thumbnailMatrix = new Matrix();
        thumbnailMatrix.setValues(pictureData.matrixValue);
        float sc = targetW * 1f / imageW;
        float showDrawableHeight = imageH * sc;
        int screenHeight = DisplayUtil.getScreenHeight();
        if (showDrawableHeight > screenHeight) {
            showDrawableHeight = screenHeight;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if ((int)showDrawableHeight == screenHeight){
                showDrawableHeight -= statusBarHeight;
            }
            showDrawableHeight -= statusBarHeight;
        }
        float targetY = (screenHeight - showDrawableHeight) * 0.5f;
        if (sc < 1) sc = 1;
        fullMatrix = new Matrix();
        fullMatrix.setValues(new float[]{
                sc, 0, 0,
                0f, sc, targetY,
                0, 0, 1f,
        });
        startColor = Color.argb(0, 0, 0, 0);
        endColor = Color.argb(255, 0, 0, 0);

        setTranslationX(thumbnailBounds.left);
        setTranslationY(thumbnailBounds.top);
        getLayoutParams().width = thumbnailBounds.width();
        getLayoutParams().height = thumbnailBounds.height();

        setImageDrawable(resource);

        if(hasAnim){
            createAnimator(true, null);
        }
     }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (beginLoad) {
            return;
        }
        beginLoad = true;
        if(null!=mFirstDrawable){

            setRemoteDrawable(mFirstDrawable,true);
        }

        if(true){
            return;
        }
        String url = pictureData.url;
        GlideApp.with(getContext())
                .asDrawable()
                .load(url)
                .thumbnail(.2f).skipMemoryCache(true).format(DecodeFormat.PREFER_RGB_565)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        setRemoteDrawable(resource,false);
                        if (resource instanceof GifDrawable) {
                            ((GifDrawable) resource).start();
                        }
                        createAnimator(true, null);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        setRemoteDrawable(ResourceUtils.getDrawable(R.drawable.ic_preview_split_graph),false);
                        createAnimator(true, null);
                    }
                });
    }

    public static class RectEvaluator implements TypeEvaluator<Rect> {
        private Rect mTmpRect = new Rect();

        @Override
        public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
            mTmpRect.left =
                    (int) (startValue.left + (endValue.left - startValue.left) * fraction);
            mTmpRect.top =
                    (int) (startValue.top + (endValue.top - startValue.top) * fraction);
            mTmpRect.right =
                    (int) (startValue.right + (endValue.right - startValue.right) * fraction);
            mTmpRect.bottom =
                    (int) (startValue.bottom + (endValue.bottom - startValue.bottom) * fraction);

            return mTmpRect;
        }
    }

    private static class MatrixEvaluator implements TypeEvaluator<Matrix> {
        private float[] mTmpStartValues = new float[9];
        private float[] mTmpEndValues = new float[9];
        private Matrix mTmpMatrix = new Matrix();

        @Override
        public Matrix evaluate(float fraction, Matrix startValue, Matrix endValue) {
            startValue.getValues(mTmpStartValues);
            endValue.getValues(mTmpEndValues);
            for (int i = 0; i < 9; i++) {
                float diff = mTmpEndValues[i] - mTmpStartValues[i];
                mTmpEndValues[i] = mTmpStartValues[i] + (fraction * diff);
            }
            mTmpMatrix.setValues(mTmpEndValues);

            return mTmpMatrix;
        }
    }

}
