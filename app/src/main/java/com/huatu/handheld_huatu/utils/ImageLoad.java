package com.huatu.handheld_huatu.utils;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.RequestManager;
import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.load.engine.bitmap_recycle.BitmapPool;
import com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions;
import com.baijiayun.glide.request.Request;
import com.baijiayun.glide.request.RequestListener;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.target.ImageViewTarget;
import com.baijiayun.glide.request.target.SimpleTarget;
import com.baijiayun.glide.util.Util;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.helper.CircleTransformV2;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.helper.GlideOptions;
import com.huatu.handheld_huatu.helper.image.BlurTransformation;
import com.huatu.handheld_huatu.helper.image.ImageUrlUtils;
import com.huatu.utils.StringUtils;

import java.io.File;

import static com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * desc:ImageLoad
 *
 * @author zhaodongdong
 *         QQ: 676362303
 *         email: androidmdeveloper@163.com
 */
public class ImageLoad {
    private static final int mDefaultResource = R.drawable.icon_default;//R.mipmap.load_default
    private static final int mErrorResource = R.drawable.icon_default;

    public static BitmapPool getBitmapPool() {
        return GlideApp.get(UniApplicationContext.getContext()).getBitmapPool();
    }

    public static void clear(com.baijiayun.glide.request.target.Target<?> target,boolean safeClean) {
        Util.assertMainThread();
        Request request = target.getRequest();
        if (request != null) {
            target.setRequest(null);
            request.clear();
            if(safeClean)
                request.recycle();
        }
    }

    public static void load(Context context, String imageUrl, ImageView imageView,DiskCacheStrategy diskCacheStrategy) {
        GlideApp.with(context)
                .load(imageUrl)
//                .placeholder(mDefaultResource)
//                .error(mErrorResource)
                .diskCacheStrategy(diskCacheStrategy)
                .format(DecodeFormat.PREFER_RGB_565)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void load(Context context, String imageUrl, ImageView imageView) {
        GlideApp.with(context)
                .load(imageUrl)
//                .placeholder(mDefaultResource)
//                .error(mErrorResource)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .format(DecodeFormat.PREFER_RGB_565)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void load(Context context, File file, ImageView imageView) {
        GlideApp.with(context).asBitmap()
                .load(file)
               // .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565)
                .into(imageView);

       // BitmapImageViewTarget
    }

  /*  public static void load(Context context, int resourceId, ImageView imageView) {
        GlideApp.with(context)
                .load(resourceId)
                .placeholder(mDefaultResource)
                .error(mErrorResource)
                .into(imageView);
    }*/

    public static void load(Context context, byte[] bytes, ImageView imageView) {
        GlideApp.with(context)
                .load(bytes)
                .placeholder(mDefaultResource)
                .error(mErrorResource)
                .into(imageView);
    }

    public static void load(Context context, String imageUrl, ImageView imageView,int defResImage) {
        GlideApp.with(context).load(imageUrl).placeholder(defResImage)
                .error(defResImage).skipMemoryCache(true).format(DecodeFormat.PREFER_RGB_565).into(imageView);
    }

    public static void displayMultSource(Context context, String url, ImageView view, @DrawableRes int shouldDrawable) {
        if (StringUtils.isEmpty(url)) return;
        if(!url.equals(view.getTag(R.id.reuse_cachetag))){
            view.setTag(R.id.reuse_cachetag,url);
            switch (ImageUrlUtils.isImageType(url)) {
                case WEB:
                    displaynoCacheImage(context, shouldDrawable,url, view);
                    break;
                case FILE:
                    // displayFileImage(context, url, view, shouldDrawable);
                    displayImage(context, ImageUrlUtils.getImageFile(url), view, shouldDrawable);
                    break;
                case ASSET:
                    displayImageAsset(context, ImageUrlUtils.getImageAsset(url), view, shouldDrawable);
                    break;
                case DRAWAABLE:
                    displayImageResource(context, view, ImageUrlUtils.getImageDrawable(url));
                    break;
            }
        }
    }

 /*   public static void displayAssetGifImage(Context context,String url, ImageView imageView) {
        // Glide.with(context).load(resourceId).placeholder(mDefaultResource).error(mErrorResource).into(imageView);
        GlideApp.with(context)
                .load(url)
                .asGif()
               // .error(errorDrawable)
                .skipMemoryCache(true)
                //.placeholder(shouldDrawable)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)//设置全尺寸缓存
                .into(imageView);
    }
*/
    public static void displaynoCacheImage(Context context,@DrawableRes int placeholderId,String url, ImageView imageView) {
       // Glide.with(context).load(resourceId).placeholder(mDefaultResource).error(mErrorResource).into(imageView);
        if(TextUtils.isEmpty(url)) return;
        if(!url.equals(imageView.getTag(R.id.reuse_cachetag))){
            imageView.setTag(R.id.reuse_cachetag,url);
            displayNormalnoCacheImage(context,url, imageView, placeholderId);
        }
    }

    public static void displayBlurTransImage(Context context, @DrawableRes int placeholderId,String url, ImageView imageView) {
        if(TextUtils.isEmpty(url)) return;
        if(!url.equals(imageView.getTag(R.id.reuse_cachetag))){
            imageView.setTag(R.id.reuse_cachetag,url);
            GlideApp.with(context)
                    .load(url)
                    .error(placeholderId)
                    .placeholder(placeholderId)
                    .skipMemoryCache(true).optionalTransform(new BlurTransformation(16,12))
                    .format(DecodeFormat.PREFER_RGB_565)
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    //.dontAnimate()
                    //.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(imageView);
        }

    }

    public static void displayCacheImage(Context context, @DrawableRes int placeholderId,String url, ImageView imageView) {

        GlideApp.with(context)
                .load(url)
                .error(placeholderId)
                .placeholder(placeholderId)
                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_RGB_565)
                //.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imageView);
    }

    public static void displayEmojiCacheImage(Context context, @DrawableRes int placeholderId,String url, ImageView imageView) {

        GlideApp.with(context)
                .load(url)
                .error(placeholderId)
                .placeholder(placeholderId)
                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_RGB_565)
                //.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imageView);
    }

    private static void displayImage(Context context, String url, ImageView imageView, @DrawableRes int shouldDrawable) {

        GlideApp.with(context)
                .load(url)
                .error(shouldDrawable)
                .format(DecodeFormat.PREFER_RGB_565)
                //.dontAnimate()
                .placeholder(shouldDrawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//设置全尺寸缓存
                .into(imageView);
    }

    private static final GlideOptions transbgOptions =
            new GlideOptions().diskCacheStrategy(DiskCacheStrategy.DATA).error(R.drawable.trans_bg)
                    .placeholder(R.drawable.trans_bg).format(DecodeFormat.PREFER_RGB_565)
                    .skipMemoryCache(true);

    private static final GlideOptions loadDefaultOptions =
            new GlideOptions().diskCacheStrategy(DiskCacheStrategy.DATA).error(R.mipmap.load_default)
                    .placeholder(R.mipmap.load_default).format(DecodeFormat.PREFER_RGB_565)
                    .skipMemoryCache(true);

    private static final GlideOptions interactColorOptions =
            new GlideOptions().diskCacheStrategy(DiskCacheStrategy.DATA).error(R.drawable.interact_color)
                    .placeholder(R.drawable.interact_color).format(DecodeFormat.PREFER_RGB_565)
                    .skipMemoryCache(true);

    private static final GlideOptions userDefaultAvaterOptions =
            new GlideOptions().diskCacheStrategy(DiskCacheStrategy.DATA).error(R.mipmap.user_default_avater)
                    .placeholder(R.mipmap.user_default_avater).format(DecodeFormat.PREFER_RGB_565).transform(new CircleTransformV2())
                    .skipMemoryCache(true);


    private static GlideOptions getDisplayOption(@DrawableRes int shouldDrawable){
        if(shouldDrawable==R.drawable.trans_bg){
            return transbgOptions;
        }else if(shouldDrawable==R.mipmap.load_default){
            return loadDefaultOptions;
        }else if(shouldDrawable==R.drawable.interact_color){
            return interactColorOptions;
        }
        return null;
    }

    public static void displayNormalnoCacheImage(Context context, String url, ImageView imageView, @DrawableRes int shouldDrawable) {

        GlideOptions curOptions=getDisplayOption(shouldDrawable);
        if(curOptions!=null){
            Glide.with(context).load(url).apply(curOptions)
                 .transition(DrawableTransitionOptions.withCrossFade(250)).into(imageView);
            return;
        }

        GlideApp.with(context)
                .load(url)
                .error(shouldDrawable)
                .placeholder(shouldDrawable)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565)
                //.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(imageView);
    }

    public static void displayNormalnoCacheImage(Context context, String url, ImageViewTarget<Drawable> target, @DrawableRes int shouldDrawable) {

        GlideApp.with(context).asDrawable()
                .load(url)
                .error(shouldDrawable)
                .placeholder(shouldDrawable)
                .skipMemoryCache(true)
                .transition(withCrossFade())
                .format(DecodeFormat.PREFER_RGB_565)
                //.dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)

                .into(target);
    }

    private static void displayImageAsset(Context context, String url, ImageView imageView, @DrawableRes int drawableId) {
        GlideApp.with(context)
                .load("file:///android_asset/" + url)
                .error(drawableId)
                //.dontAnimate()
                .placeholder(drawableId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//设置全尺寸缓存
                .into(imageView);
    }


    public static void displayImageDrawableResource(Context context, ImageView view, @DrawableRes int drawableId,int shouldDrawable) {

        if (drawableId<=0) return;
        String tag="image_tag"+drawableId;
        if(!tag.equals(view.getTag(R.id.reuse_cachetag))) {
            view.setTag(R.id.reuse_cachetag, tag);

            GlideApp.with(context)
                    .load(drawableId)
                    //.load(drawableId)
                    .error(shouldDrawable)
                    .dontAnimate()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .skipMemoryCache(true)
                    .placeholder(shouldDrawable)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//设置全尺寸缓存
                    .into(view);
        }
    }

    //好像有问题
    private static void displayImageResource(Context context, ImageView imageView, @DrawableRes int drawableId) {
        GlideApp.with(context)
                .load("android.resource://com.huatu.handheld_huatu/drawable/" + drawableId)
                //.load(drawableId)
                .error(drawableId)
                .dontAnimate()
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(drawableId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//设置全尺寸缓存
                .into(imageView);
    }

    public static void displayOriginCircleLogo(Context context, String url, ImageView imageView, @DrawableRes int shouldDrawable) {

        if(shouldDrawable==R.mipmap.user_default_avater){
            Glide.with(context).load(url).apply(userDefaultAvaterOptions).into(imageView);
            return;
        }

        GlideApp.with(context)
                .load(url)
                .error(shouldDrawable)
                .placeholder(shouldDrawable)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);


    }

    public static void downloadPhotoCover(String url,  SimpleTarget<File> simpleTarget) {
        GlideApp.with(UniApplicationContext.getContext())
                .load(url).downloadOnly(simpleTarget);

     }

    public static void displayWorkImageListener(Context context, String url, ImageView imageView, @DrawableRes int shouldDrawable,RequestListener<Drawable> listener) {
        if(TextUtils.isEmpty(url)) return;
        if(!url.equals(imageView.getTag(R.id.reuse_cachetag))) {
            imageView.setTag(R.id.reuse_cachetag, url);
            Glide.with(context).asDrawable()
                    .load(url)
                    //.dontAnimate()
                    .apply(RequestOptions.placeholderOf(shouldDrawable)
                            .skipMemoryCache(true)
                            .format(DecodeFormat.PREFER_RGB_565)
                            .placeholder(shouldDrawable)
                            .diskCacheStrategy(DiskCacheStrategy.DATA))
                    .addListener(listener)
                    .into(imageView);
        }
    }


    public static void displayUserAvater(Context context, String url, ImageView imageView, @DrawableRes int shouldDrawable){
        if(TextUtils.isEmpty(url)) return;
        if(!url.equals(imageView.getTag(R.id.reuse_cachetag))){
            imageView.setTag(R.id.reuse_cachetag,url);
            GlideApp.with(context)
                    .load(url)
                    .transform(new CircleTransformV2())
                    .placeholder(shouldDrawable)
                    .error(shouldDrawable)
                    .skipMemoryCache(false)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .placeholder(shouldDrawable)
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                   // .crossFade()
                     .into(imageView);
        }
    }

    public static void displaynoCacheUserAvater(Context context, String url, ImageView imageView, @DrawableRes int shouldDrawable){
        if(TextUtils.isEmpty(url)) return;
        if(!url.equals(imageView.getTag(R.id.reuse_cachetag))){
            imageView.setTag(R.id.reuse_cachetag,url);

            if(shouldDrawable==R.mipmap.user_default_avater){
                Glide.with(context).load(url).apply(userDefaultAvaterOptions)
                        .transition(DrawableTransitionOptions.withCrossFade(200)).into(imageView);
                return;
            }
            GlideApp.with(context)
                    .load(url)
                    .transform(new CircleTransformV2())
                    .placeholder(shouldDrawable)
                    .error(shouldDrawable)
                    .skipMemoryCache(true)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .placeholder(shouldDrawable)
                   // .crossFade()
                    .into(imageView);
        }
    }

    public static RequestManager getRequestManager(FragmentActivity activity){
        return GlideApp.with(activity);
    }
}
