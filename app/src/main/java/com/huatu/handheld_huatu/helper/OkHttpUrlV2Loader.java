package com.huatu.handheld_huatu.helper;



import com.baijiayun.glide.load.Options;
import com.baijiayun.glide.load.model.GlideUrl;
import com.baijiayun.glide.load.model.ModelLoader;
import com.baijiayun.glide.load.model.ModelLoaderFactory;
import com.baijiayun.glide.load.model.MultiModelLoaderFactory;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.SSLSocketBuild;
import com.huatu.handheld_huatu.network.SSLSocketFactoryCompat;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Dns;
import okhttp3.OkHttpClient;

/**
 * A simple model loader for fetching media over http/https using OkHttp.
 */
public class OkHttpUrlV2Loader implements ModelLoader<GlideUrl, InputStream> {

    private final Call.Factory client;

    public OkHttpUrlV2Loader(Call.Factory client) {
        this.client = client;
    }

    @Override
    public boolean handles(GlideUrl url) {
        return true;
    }

    @Override
    public LoadData<InputStream> buildLoadData(GlideUrl model, int width, int height,
                                               Options options) {
        return new LoadData<>(model, new OkHttpStreamV2Fetcher(client, model));
    }

    private static OkHttpClient getSOkHttpClient() {
        //浅克隆
        OkHttpClient.Builder okHttpClientBuilder =   RetrofitManager.getInstance().getOkHttpClient().newBuilder();
        okHttpClientBuilder.interceptors().clear();
        okHttpClientBuilder.networkInterceptors().clear();
        okHttpClientBuilder.dns(Dns.SYSTEM);
       // okHttpClientBuilder.cache(null);//禁用缓存http缓存
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

    /**
     * The default factory for {@link OkHttpUrlV2Loader}s.
     */
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private static volatile Call.Factory internalClient;
        private Call.Factory client;

        private static Call.Factory getInternalClient() {
            if (internalClient == null) {
                synchronized (Factory.class) {
                    if (internalClient == null) {
                        internalClient = getSOkHttpClient();
                    }
                }
            }
            return internalClient;
        }

        /**
         * Constructor for a new Factory that runs requests using a static singleton client.
         */
        public Factory() {
            this(getInternalClient());
        }

        /**
         * Constructor for a new Factory that runs requests using given client.
         *
         * @param client this is typically an instance of {@code OkHttpClient}.
         */
        public Factory(Call.Factory client) {
            this.client = client;
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new OkHttpUrlV2Loader(client);
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}
