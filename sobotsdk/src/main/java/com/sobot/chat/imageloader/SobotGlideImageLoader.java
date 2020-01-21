package com.sobot.chat.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;


import com.baijiayun.glide.Glide;
import com.baijiayun.glide.RequestBuilder;
import com.baijiayun.glide.load.DataSource;
import com.baijiayun.glide.load.engine.GlideException;
import com.baijiayun.glide.request.RequestListener;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.target.Target;

/**
 * 图片加载器  Glide
 */
public class SobotGlideImageLoader extends SobotImageLoader {

    @Override
    public void displayImage(Context context, final ImageView imageView, final String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final SobotDisplayImageListener listener) {
        RequestOptions options = new RequestOptions().placeholder(loadingResId).error(failResId).centerCrop();
        if (width != 0 || height != 0) {
            options.override(width, height);
        }
        RequestBuilder<Bitmap> builder = Glide.with(context)
                .asBitmap()
                .load(path)
                .apply(options);
        builder.listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                if (listener != null) {
                    listener.onSuccess(imageView, path);
                }
                return false;
            }
        }).into(imageView);
    }

    @Override
    public void displayImage(Context context, final ImageView imageView, @DrawableRes int targetResId, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final SobotDisplayImageListener listener) {
        RequestOptions options = new RequestOptions().placeholder(loadingResId).error(failResId).centerCrop();
        if (width != 0 || height != 0) {
            options.override(width, height);
        }
        RequestBuilder<Bitmap> builder = Glide.with(context)
                .asBitmap()
                .load(targetResId)
                .apply(options);
        builder.listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                if (listener != null) {
                    listener.onSuccess(imageView, "");
                }
                return false;
            }
        }).into(imageView);
    }

   /* @Override
    public void displayImage(Context context, final ImageView imageView, final String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final SobotDisplayImageListener listener) {
        BitmapRequestBuilder<String, Bitmap> builder = Glide.with(context).load(path).asBitmap().placeholder(loadingResId).error(failResId).centerCrop();
        if (width != 0 || height != 0) {
            builder.override(width, height);
        }
        builder.listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (listener != null) {
                    listener.onSuccess(imageView, path);
                }
                return false;
            }
        }).into(imageView);
    }

    @Override
    public void displayImage(Context context, final ImageView imageView, @DrawableRes int targetResId, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final SobotDisplayImageListener listener) {
        BitmapRequestBuilder<Integer, Bitmap> builder = Glide.with(context).load(targetResId).asBitmap().placeholder(loadingResId).error(failResId).centerCrop();
        if (width != 0 || height != 0) {
            builder.override(width, height);
        }
        builder.listener(new RequestListener<Integer, Bitmap>() {
            @Override
            public boolean onException(Exception e, Integer model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Integer model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (listener != null) {
                    listener.onSuccess(imageView, "");
                }
                return false;
            }

        }).into(imageView);
    }
*/
}