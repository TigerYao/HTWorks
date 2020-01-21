package com.huatu.test.custom;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.RequestManager;
import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions;
import com.baijiayun.glide.request.Request;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.target.ViewTarget;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.test.ArrayUtils;
import com.huatu.test.R;
import com.huatu.test.utils.DisplayUtil;
import com.huatu.test.utils.LogUtils;
import com.huatu.test.utils.Method;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by saiyuan on 2017/12/14.
 *
 * https://www.jianshu.com/p/037ae1dfb442
 * https://www.jianshu.com/p/f1f17f816d1d
 * https://www.cnblogs.com/Jason-Jan/p/7881690.html
 *
 */

public class GlideImageV4Getter implements Html.ImageGetter {
    private String htmlSource;
    private final TextView mTextView;

    private final Set<ImageGetterViewTarget> mTargets;
    private float mContainerWidthdp;

    private  Context mContext;
    private  RequestManager mRequestManager;

    public GlideImageV4Getter get(View view) {
        return (GlideImageV4Getter) view.getTag(R.id.glide_tag_id_exercise);
    }

    private RequestManager  getRequestManager(Context context){
        try{
            if(null!=mRequestManager) return mRequestManager;
            else {
                mRequestManager=Glide.with(context);
                return mRequestManager;
            }
        }catch (Exception e){
            return null;
        }

   }

    public void clear(boolean safeclean) {
        GlideImageV4Getter prev = get(mTextView);
        if (prev == null)
            return;
        if(!ArrayUtils.isEmpty(prev.mTargets)){
            for (ImageGetterViewTarget target : prev.mTargets) {
                RequestManager curRequest=getRequestManager(mContext);
                if(null!=curRequest){
                    curRequest.clear(target);
                }
                 //ImageLoad.clear(target,safeclean);
            }
            prev.mTargets.clear();
        }
    }

    public GlideImageV4Getter(String source, TextView textView, Context context) {
        this.htmlSource = source;
        this.mTextView = textView;
        mContext=context;
        clear(true);
        mTargets = new HashSet<>();
        mTextView.setTag(R.id.glide_tag_id_exercise, this);
    }

    public GlideImageV4Getter(float widthdp, String source, TextView textView, Context context) {
        this.htmlSource = source;
        this.mTextView = textView;
        mContext=context;
        clear(true);
        mTargets = new HashSet<>();
        mTextView.setTag(R.id.glide_tag_id_exercise, this);
        this.mContainerWidthdp = widthdp;
    }

    private static final RequestOptions loadDefaultOptions =
            new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).error(R.drawable.icon_default)
                    .placeholder(R.drawable.icon_default).format(DecodeFormat.PREFER_ARGB_8888)
                    .skipMemoryCache(true);

    @Override
    public Drawable getDrawable(String url) {
        final UrlDrawableV4 urlDrawable = new UrlDrawableV4(url);
        int widthFromServer = 0;
        int heightFromServer = 0;
        if (!TextUtils.isEmpty(htmlSource) && !TextUtils.isEmpty(urlDrawable.imgUrl)) {
            try {
                int index = htmlSource.indexOf(urlDrawable.imgUrl);
                String subStringStyle = "";
                if (index >= 0) {
                    subStringStyle = htmlSource.substring(index + urlDrawable.imgUrl.length());
                }
              /*  String endStr=subStringStyle.substring(0,3);
                if(endStr.contains(">")){//

                    htmlSource.lastIndexOf("width=",index)

                    htmlSource.lastIndexOf("height=",index);
                }*/

                index = subStringStyle.indexOf("width=");
                if (index >= 0) {
                    widthFromServer = Method.parseInt(subStringStyle.substring(index + "width=".length()));
                }
                index = subStringStyle.indexOf("height=");
                if (index >= 0) {
                    heightFromServer = Method.parseInt(subStringStyle.substring(index + "height=".length()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 如果 widthdp != -2 就把服务器传过来的长度从 dp 转为 px
//            if (widthdp != -2) {     //统一放大处理，选项中的图片可能很小
               widthFromServer = DisplayUtil.dp2px(widthFromServer);
               heightFromServer = DisplayUtil.dp2px(heightFromServer);


//            }
        }

        // 如果宽度超过了 屏幕宽度 - 80dp 就按比例缩小图片尺寸

        int maxScreenWidth=Math.min(DisplayUtil.getScreenWidth(),DisplayUtil.getScreenHeight())- DisplayUtil.dp2px(40);
        if (widthFromServer >= maxScreenWidth) {
            float downScale = ((float) widthFromServer) / maxScreenWidth;
            widthFromServer = (int) (((float)widthFromServer) / downScale);
            heightFromServer = (int) (((float)heightFromServer )/ downScale);
       }


  /*      final UrlDrawable urlDrawable = new UrlDrawable();
        final GenericRequestBuilder load;
        final Target target;
        if(isGif(url)){
            load = Glide.with(mContext).load(url).asGif();
            target = new GifTarget(urlDrawable);
        }else {
            load = Glide.with(mContext).load(url).asBitmap();
            target = new BitmapTarget(urlDrawable);
        }
        targets.add(target);
        load.into(target);*/
        RequestManager curRequestManager=getRequestManager(mContext);
        if(null==curRequestManager) return urlDrawable;
        if (widthFromServer <= 0 || heightFromServer <= 0) {
           /* Glide.with(UniApplicationContext.getContext())
                    .load(url)
                    .crossFade(30)
                    .animate(android.R.anim.fade_in)
                    .placeholder(R.drawable.icon_default)
                    .error(R.drawable.icon_default)
                    .skipMemoryCache(false)
                    .into(new ImageGetterViewTarget(mTextView, widthdp, widthFromServer, heightFromServer, urlDrawable));*/

            curRequestManager
                    .load(url).apply(loadDefaultOptions)
                   // .transition(DrawableTransitionOptions.withCrossFade(30))
                    .into(new ImageGetterViewTarget(mTextView, mContainerWidthdp, widthFromServer, heightFromServer, urlDrawable));

        } else {
            /*Glide.with(UniApplicationContext.getContext())
                    .load(url)
                    .crossFade(30)
                    .override(widthFromServer, heightFromServer)
                    .animate(android.R.anim.fade_in)
                    .placeholder(R.drawable.icon_default)
                    .error(R.drawable.icon_default)
                    .skipMemoryCache(false)
                    .into(new ImageGetterViewTarget(mTextView, widthdp, widthFromServer, heightFromServer, urlDrawable));*/
            curRequestManager
                    .load(url).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).error(R.drawable.icon_default)
                    .placeholder(R.drawable.icon_default).format(DecodeFormat.PREFER_ARGB_8888)
                    .skipMemoryCache(true).override(widthFromServer, heightFromServer))
                    .transition(DrawableTransitionOptions.withCrossFade(30))
                    .into(new ImageGetterViewTarget(mTextView, mContainerWidthdp, widthFromServer, heightFromServer, urlDrawable));
        }
        return urlDrawable;
    }

    public class ImageGetterViewTarget extends ViewTarget<TextView, Drawable> {
        private final UrlDrawableV4 mDrawable;
        private float widthFromServer = 0;
        private float heightFromServer = 0;
        private float mViewwidthdp;
        private int maxScreenWidth=Math.min(DisplayUtil.getScreenWidth(),DisplayUtil.getScreenHeight())- DisplayUtil.dp2px(40);

        public ImageGetterViewTarget(TextView view, float widthdp, int width, int height, UrlDrawableV4 drawable) {
            super(view);
            mTargets.add(this);
            this.mDrawable = drawable;
            this.widthFromServer = (float) width;
            this.heightFromServer =(float) height;
            this.mViewwidthdp = (float)widthdp;
        }
        //public void onResourceReady(Drawable resource, GlideAnimation<? super Drawable> glideAnimation)

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition){
            Rect rect;
            float width = widthFromServer;
            float height = heightFromServer;
            // 如果服务器没有指定宽高
            if (width <= 0 || height <= 0) {
                // 如果图片大小本身宽度大于 屏幕宽度 - 80dp 按比例缩小
//                if (resource.getIntrinsicWidth() >= (DisplayUtil.getScreenWidth() - DisplayUtil.dp2px(80))) {
//                    float downScale = (float) resource.getIntrinsicWidth() / (DisplayUtil.getScreenWidth() - DisplayUtil.dp2px(80));
                width = (float)resource.getIntrinsicWidth();
                height = (float)resource.getIntrinsicHeight();


                if (maxScreenWidth > 0 && width > maxScreenWidth) {
                    float downScale = ((float) width) / maxScreenWidth;
                    if (downScale != 0) {
                        width =   (width / downScale);
                        height =   (height / downScale);
                    }
                }
//                } else {
//                    // 如果图片宽度小于 30dp 就放大图片
//                    if (resource.getIntrinsicWidth() <= DisplayUtil.dp2px(30)) {
//                        float downScale = (float) resource.getIntrinsicWidth() / DisplayUtil.dp2px(30);
//                        width = (int) (resource.getIntrinsicWidth() / downScale);
//                        height = (int) (resource.getIntrinsicHeight() / downScale);
//                    } else {
//                        // 否则图片大小就是原始大小
//                        width = resource.getIntrinsicWidth();
//                        height = resource.getIntrinsicHeight();
//                    }
//                }
            }

//            if (width <= DisplayUtil.dp2px(30)) {
//                float downScale = (float) width / DisplayUtil.dp2px(30);
//                if (downScale != 0) {
//                    width = (int) (width / downScale);
//                    height = (int) (height / downScale);
//                }
//            }

            if(this.mViewwidthdp<=0){//
               this.mViewwidthdp=Math.min(getView().getWidth(),maxScreenWidth);
                // 控制图片不要超过 指定的大小宽度
                if (this.mViewwidthdp > 0 && width > mViewwidthdp) {
                    float downScale = ((float) width) / mViewwidthdp;
                    if (downScale != 0) {
                        width =   (width / downScale);
                        height =   (height / downScale);
                    }
                }else if(this.mViewwidthdp>0&&(width<mViewwidthdp)){//放大的到view宽度
                    float multiplier = (float) mViewwidthdp / width;
                    width = (float) width * (float) multiplier;
                    height = (float) height * (float) multiplier;
                }

            }else {  //
                this.mViewwidthdp=Math.min(mViewwidthdp,maxScreenWidth);
                if (this.mViewwidthdp > 0 && width > mViewwidthdp) {
                    float downScale = ((float) width) / mViewwidthdp;
                    if (downScale != 0) {
                        width =   (width / downScale);
                        height =   (height / downScale);
                    }
                }
            }


            // 控制图片不要超过 屏幕宽度 - 80
           // int screenWidth = DisplayUtil.getScreenWidth() - DisplayUtil.dp2px(80);


            LogUtils.d("ImageGetterViewTarget", "widthdp:" + mViewwidthdp);
            LogUtils.d("ImageGetterViewTarget", "width:" + width);
            LogUtils.d("ImageGetterViewTarget", "height:" + height);
            LogUtils.d("ImageGetterViewTarget", "=======================");

            rect = new Rect(0, 0, Math.round(width), Math.round(height));
            resource.setBounds(rect);
            mDrawable.setBounds(rect);
            mDrawable.setDrawable(resource);

            getView().setText(getView().getText());
            getView().invalidate();
        }

        private Request request;

        @Override
        public Request getRequest() {
            return request;
        }

        @Override
        public void setRequest(Request request) {
            this.request = request;
        }
    }
}
