/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huatu.handheld_huatu.business.essay.cusview.imgdrag;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;


import com.baijiayun.glide.Glide;
import com.baijiayun.glide.Priority;
import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions;
import com.baijiayun.glide.request.RequestOptions;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.zhihu.matisse.engine.ImageEngine;

public class MyGlideEngine implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        RequestOptions options = new RequestOptions()
                .placeholder(placeholder)
                .override(resize, resize)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE);

        if(TextUtils.isEmpty(uri.getPath())) return;
        if(!uri.getPath().equals(imageView.getTag(R.id.reuse_cachetag))){
            imageView.setTag(R.id.reuse_cachetag,uri.getPath());
           // displayNormalnoCacheImage(context,url, imageView, placeholderId);
            GlideApp.with(context).load(uri)
                    .apply(options).transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(imageView);

         /*   Glide.with(context).load(url).apply(curOptions)
                    .transition(DrawableTransitionOptions.withCrossFade(250)).into(imageView);*/
        }



    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        RequestOptions options = new RequestOptions()
                .placeholder(placeholder)
                .override(resize, resize)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        if(TextUtils.isEmpty(uri.getPath())) return;
        if(!uri.getPath().equals(imageView.getTag(R.id.reuse_cachetag))){
            imageView.setTag(R.id.reuse_cachetag,uri.getPath());
            // displayNormalnoCacheImage(context,url, imageView, placeholderId);
            GlideApp.with(context).load(uri)
                    .apply(options).transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(imageView);
        }
 /*       Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(options)
                .into(imageView);*/
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        RequestOptions options = new RequestOptions()
                .fitCenter()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL);


        if(TextUtils.isEmpty(uri.getPath())) return;
        if(!uri.getPath().equals(imageView.getTag(R.id.reuse_cachetag))){
            imageView.setTag(R.id.reuse_cachetag,uri.getPath());
            // displayNormalnoCacheImage(context,url, imageView, placeholderId);
            GlideApp.with(context).load(uri)
                    .apply(options).transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(imageView);
        }
   /*     Glide.with(context)
                .load(uri)
                .apply(options)
                .into(imageView);*/
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        RequestOptions options = new RequestOptions()
                .fitCenter()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.NONE);

        if(TextUtils.isEmpty(uri.getPath())) return;
        if(!uri.getPath().equals(imageView.getTag(R.id.reuse_cachetag))){
            imageView.setTag(R.id.reuse_cachetag,uri.getPath());
            // displayNormalnoCacheImage(context,url, imageView, placeholderId);
            GlideApp.with(context).asGif().load(uri)
                    .apply(options).transition(DrawableTransitionOptions.withCrossFade(250))
                    .into(imageView);
        }
       /* Glide.with(context)
                .asGif()
                .load(uri)
                .apply(options)
                .into(imageView);*/
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }

}
