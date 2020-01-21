package com.huatu.handheld_huatu.network;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.huatu.handheld_huatu.network.RetrofitManager.CACHE_STALE_LONG;

/**
 * Created by xing on 2018/4/4.
 */

public class OkHttpClientBuilder {

    private static Interceptor getReWriteCacheInterceptor(){
        final   String terminal;          // 终端,1:安卓,2:苹果,3:pc,4:安卓pad,5:苹果pad,6:微信
        final   String device    = Build.MANUFACTURER + android.os.Build.MODEL;;            //设备型号
        final   String systemInfo = android.os.Build.VERSION.RELEASE;;       //系统版本
        final   String APP_TYPE_HT_ONLINE="2";//华图在线传2
        TelephonyManager tm = (TelephonyManager) UniApplicationContext.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            terminal = "4";
        } else {
            terminal = "1";
        }

        // 云端响应头拦截器，用来配置缓存策略
          Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                if (!NetUtil.isConnected()) {
                    original = original.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                }
                //可以在此配置统一的头信息
                Request request = original.newBuilder()
                        .header("token", UserInfoUtil.token)
//                    .header("token", "test_token")
                        .header("uid", String.valueOf(SpUtils.getUid()))
                        .header("cv", AppUtils.getVersionName())
                        .header("pixel", DisplayUtil.getScreenHeight() + "_" + DisplayUtil.getScreenWidth())
                        .header("terminal", terminal)
                        .header("device", device)
                        .header("system", systemInfo)
                        .header("channelId", AppUtils.getChannelId())
                        .header("appType", APP_TYPE_HT_ONLINE)
                        .build();
                Response originalResponse = chain.proceed(request);
                if (401 == originalResponse.code()) {
                    //认证失败不需要缓存处理

                    return originalResponse;
                }

                if (NetUtil.isConnected()) {
                    //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                    String cacheControl = request.cacheControl().toString();
                    return originalResponse.newBuilder().header("Cache-Control", cacheControl)
                            .removeHeader("Pragma").build();
                } else {
                    return originalResponse.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_LONG)
                            .removeHeader("Pragma").build();
                }
            }
        };
          return mRewriteCacheControlInterceptor;
    }

    public static OkHttpClient build(Authenticator authenticator){
        // 指定缓存路径,缓存大小100Mb
        Cache cache = new Cache(new File(UniApplicationContext.getContext().getCacheDir(), "HttpCache"),
                1024 * 1024 * 100);

        Interceptor rewriteCacheControlInterceptor=getReWriteCacheInterceptor();
        if(BuildConfig.DEBUG){
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Platform.get().log(Platform.WARN, message, null);
                }
            });
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            return new OkHttpClient.Builder()
                    .cache(cache)
                    .authenticator(authenticator)
                    .addInterceptor(rewriteCacheControlInterceptor)
                    .addNetworkInterceptor(rewriteCacheControlInterceptor)
                    .addInterceptor(logInterceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

        }
         return new OkHttpClient.Builder()
                 .cache(cache)
                 .authenticator(authenticator)
                 .addInterceptor(rewriteCacheControlInterceptor)
                 .addNetworkInterceptor(rewriteCacheControlInterceptor)
                 .retryOnConnectionFailure(true)
                 .connectTimeout(15, TimeUnit.SECONDS)
                 .readTimeout(30, TimeUnit.SECONDS)
                 .build();

    }
}

