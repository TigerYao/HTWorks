package com.huatu.music.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;
import android.text.TextUtils;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.target.SimpleTarget;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.music.R;
import com.huatu.music.bean.Music;

/**
 * Created by Administrator on 2019\8\22 0022.
 */

public class CoverLoader {

    public interface Callback<T>{
         void invoke(T t);
    }


    private static final RequestOptions loadDefaultOptions =
            new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.default_cover)
                    .placeholder(R.drawable.default_cover).format(DecodeFormat.PREFER_RGB_565)
                    .skipMemoryCache(true);

    private static String lastUrl;


    public static class  CustomSimpleTarget extends SimpleTarget<Bitmap> {

        Callback<Bitmap> mCallback;

        public CustomSimpleTarget(){

        }

        public CustomSimpleTarget(Callback<Bitmap> callBack){
            mCallback=callBack;
        }

        public void setCallback(Callback<Bitmap> callBack){
            mCallback=callBack;
        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            if(mCallback!=null &&resource!=null){
               mCallback.invoke(resource);
            }
        }
    }

    private static CustomSimpleTarget mLastSmallTarget,mLastBigTarget;

    public static void clearTarget(Context context){
        Glide.with(context).clear(mLastSmallTarget);
        Glide.with(context).clear(mLastBigTarget);
        mLastSmallTarget=null;
        mLastBigTarget=null;
        lastUrl=null;
    }

    public static void loadImageViewByMusic(Context mContext, Music music, final Callback<Bitmap> callBack){
        if (music == null) return;
        if (mContext == null) return;
            //    val url = MusicUtils.getAlbumPic(music.coverUri, music.type, MusicUtils.PIC_SIZE_BIG)
        String url=music.coverUri;
        if(TextUtils.isEmpty(url)) return;
        if(!url.equals(lastUrl)){
            lastUrl=url;
            int width=SizeUtils.dp2px(mContext,100);

            if(null==mLastSmallTarget){
               mLastSmallTarget=new CustomSimpleTarget();
            }
            mLastSmallTarget.setCallback(callBack);
            Glide.with(mContext).asBitmap().load(url).apply(loadDefaultOptions.override(width,width)).into(mLastSmallTarget);
        }

    }

    public static void loadBigImageView(Context mContext, Music music, final Callback<Bitmap> callBack) {
        if (music == null) return;
        if (mContext == null) return;
        //    val url = MusicUtils.getAlbumPic(music.coverUri, music.type, MusicUtils.PIC_SIZE_BIG)
        String url = music.coverUri;
        if (TextUtils.isEmpty(url)) return;
        {
            //lastLockUrl = url;
            int width = SizeUtils.getScreenWidth(mContext) - SizeUtils.dp2px(mContext, 80);
            width = Math.min(width, 800);

            if (null == mLastBigTarget) {
                mLastBigTarget = new CustomSimpleTarget();
            }
            mLastBigTarget.setCallback(callBack);
            Glide.with(mContext).asBitmap().load(url).apply(loadDefaultOptions.override(width, width)).into(mLastBigTarget);
        }
    }

    public static void loadBigImageView(Context mContext, Music music, final CustomSimpleTarget simpleTarget) {
        if (music == null) return;
        if (mContext == null) return;
        //    val url = MusicUtils.getAlbumPic(music.coverUri, music.type, MusicUtils.PIC_SIZE_BIG)
        String url = music.coverUri;
        if (TextUtils.isEmpty(url)) return;
        {
            //lastLockUrl = url;
            int width = SizeUtils.getScreenWidth(mContext) - SizeUtils.dp2px(mContext, 80);
            width = Math.min(width, 800);
/*
            if (null == mLastBigTarget) {
                mLastBigTarget = new CustomSimpleTarget();
            }
            mLastBigTarget.setCallback(callBack);*/
            Glide.with(mContext).asBitmap().load(url).apply(loadDefaultOptions.override(width, width)).into(simpleTarget);
        }
    }
}
