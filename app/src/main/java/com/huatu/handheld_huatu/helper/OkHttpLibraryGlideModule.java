package com.huatu.handheld_huatu.helper;

import android.content.Context;

import com.baijiayun.glide.Registry;
import com.baijiayun.glide.Glide;
import com.baijiayun.glide.GlideBuilder;

import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.model.GlideUrl;
import com.baijiayun.glide.load.resource.bitmap.Downsampler;
import com.baijiayun.glide.request.RequestOptions;


import java.io.InputStream;

/**
 * Created by cjx on 2019\4\28 0028.
 */
//https://blog.csdn.net/niuba123456/article/details/86369744
//https://muyangmin.github.io/glide-docs-cn/doc/configuration.html#%E9%BB%98%E8%AE%A4%E8%AF%B7%E6%B1%82%E9%80%89%E9%A1%B9
public final class OkHttpLibraryGlideModule implements com.baijiayun.glide.module.GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Do nothing.
        //设置图片解码格式
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565).disallowHardwareConfig());


       // builder.setLogLevel(Log.DEBUG);
    }
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlV2Loader.Factory());
    }

   /* @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // Default empty impl.
    }*/
/*
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);
    }
*/

}
