package com.zhy.http.okhttp.imagehttp;

import android.content.Context;

import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.ImageDownLoader;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.SSLSocketBuild;
import com.huatu.handheld_huatu.network.SSLSocketFactoryCompat;

import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Dns;
import okhttp3.OkHttpClient;

public class OkHttpUrlLoader{/* implements ModelLoader<GlideUrl, InputStream> {

    *//**
     * The default factory for {@link OkHttpUrlLoader}s.
     *//*
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private static volatile OkHttpClient internalClient;
        private OkHttpClient client;

        private static OkHttpClient getInternalClient() {
            if (internalClient == null) {
                synchronized (Factory.class) {
                    if (internalClient == null) {
                        internalClient = getSOkHttpClient();
                    }
                }
            }
            return internalClient;
        }

        *//**
         * Constructor for a new Factory that runs requests using a static singleton client.
         *//*
        public Factory() {
            this(getInternalClient());
        }

        *//**
         * Constructor for a new Factory that runs requests using given client.
         *//*
        public Factory(OkHttpClient client) {
            this.client = client;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new OkHttpUrlLoader(client);
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }

    private  OkHttpClient client;

    public OkHttpUrlLoader(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(GlideUrl model, int width, int height) {
        return new OkHttpStreamFetcher(client, model);
    }

    private static OkHttpClient getSOkHttpClient() {
        //浅克隆
        OkHttpClient.Builder okHttpClientBuilder =   RetrofitManager.getInstance().getOkHttpClient().newBuilder();
        okHttpClientBuilder.interceptors().clear();
        okHttpClientBuilder.networkInterceptors().clear();
        okHttpClientBuilder.dns(Dns.SYSTEM);
        SSLSocketBuild.TrustAllManager trustAllCert=new SSLSocketBuild.TrustAllManager();


        OkHttpClient client = okHttpClientBuilder.readTimeout(20000, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(20000, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(20000, TimeUnit.SECONDS)//设置连接超时时间
                    .sslSocketFactory(new SSLSocketFactoryCompat(trustAllCert), trustAllCert)
                   // .sslSocketFactory(SSLSocketBuild.createSSLSocketFactory())
                    // .sslSocketFactory(createSSLSocketFactory())    //添加信任所有证书
                    .hostnameVerifier(new HostnameVerifier() {     //信任规则全部信任
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();
        return client;

    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new X509TrustManager(){
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }*/
}
