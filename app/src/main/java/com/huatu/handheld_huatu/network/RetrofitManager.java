package com.huatu.handheld_huatu.network;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.TokenConflictActivity;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.business.login.LoginByPasswordActivity;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaPlugins;

/**
 * Created by zdd
 */
public class RetrofitManager {
    private static RetrofitManager mInstance;

//   public static final String BASE_URL = "https://ns.huatu.com/";
//    public static final String BASE_URL = "http://123.103.86.52/";
//    public static final String BASE_URL = "http://123.103.86.59:2280/";

    //  public static final String BASE_MONK_URL = "http://mock.ztk.com/";

    public static final String BASE_MONK_URL = "http://rap.ztk.com/mockjsdata/48/";
    public static final String BASE_URL = "https://ns.huatu.com/";
    public static final String BASE_TEST_URL = "http://123.103.86.52/";
    public static final String BASE_GRAY_URL = "http://123.103.86.58/";
    public static final String BASE_TEST_URL_2280 = "http://123.103.86.59:2280/";

    public static final String BASE_URL_SHOP = "https://shop.huatu.com/";
    public static final String BASE_URL_PHOTO = "http://39.106.163.36:9999/";

    public static final String BASE_URL_APITK = "https://apitk.huatu.com/";
    public static final String BASE_TEST_URL_APITK = "http://123.103.79.69:8022/";

    public static final String EDUCATIONURL_DEBUG = "http://app-alpha.huatu.com"; // https://app.huatu.com
    public static final String EDUCATIONURL = "https://app.huatu.com"; // https://app.huatu.com


    //短缓存有效期为1分钟
    public static final int CACHE_STALE_SHORT = 60;
    //长缓存有效期为7天
    public static final int CACHE_STALE_LONG = 60 * 60 * 24 * 7;

    public static final String CACHE_CONTROL_AGE = "Cache-Control: public, max-age=";

    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    private final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_LONG;
    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
    private final String CACHE_CONTROL_NETWORK = "max-age=0";
    private OkHttpClient mOkHttpClient;
    private HttpService mService;
    private HttpShopService mServiceShop;
    private HttpApiTkService mServiceApiTk;
    private Subscriber<? super String> mSubscriber;
    private File CACHEFILE;
    private String terminal;          // 终端,1:安卓,2:苹果,3:pc,4:安卓pad,5:苹果pad,6:微信
    private String device;            //设备型号
    private String systemInfo;            //系统版本
    private String baseUrl = BASE_URL;
    private String baseApiTkUrl = BASE_URL_APITK;
    private String baseShopUrl = BASE_URL_SHOP;
    private String basePhotoUrl = BASE_URL_PHOTO;
    private final String APP_TYPE_HT_ONLINE = "2";//华图在线传2

    public static synchronized RetrofitManager getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitManager.class) {
                mInstance = new RetrofitManager();
                mInstance.init();
            }
        }
        return mInstance;
    }

    private static String USERAGENT = "";

    private String getUserAgent() {
        try {
            if (!TextUtils.isEmpty(USERAGENT)) return USERAGENT;
            if (TextUtils.isEmpty(systemInfo)) {
                USERAGENT = "okhttp_" + AppUtils.getVersionName() + "_android";
            } else {
                USERAGENT = "okhttp_" + AppUtils.getVersionName() + "_android" + URLEncoder.encode(systemInfo);
            }
        } catch (Exception e) {

        }
        return USERAGENT;
    }

    public String getBaseUrl() {
        ApplicationInfo applicationInfo = UniApplicationContext.getContext().getApplicationInfo();
        if (applicationInfo != null && (applicationInfo.flags & 2) != 0) {
            if (BuildConfig.isDebug) {
                this.baseUrl = BASE_TEST_URL;
            }
            if (!TextUtils.isEmpty(SpUtils.getHostUrl())) {
                this.baseUrl = SpUtils.getHostUrl();
            }
        } else {
            this.baseUrl = BASE_URL;
        }
//        this.baseUrl = BASE_URL;
        return this.baseUrl;
    }

    public String getBaseApiTkUrl() {
//        ApplicationInfo applicationInfo = UniApplicationContext.getContext().getApplicationInfo();
//        if (applicationInfo != null && (applicationInfo.flags & 2) != 0) {
//            if (BuildConfig.isDebug) {
//                this.baseUrl = BASE_TEST_URL;
//            }
//            if (!TextUtils.isEmpty(SpUtils.getHostUrl())) {
//                this.baseUrl = SpUtils.getHostUrl();
//            }
//        } else {
//            this.baseUrl = BASE_URL;
//        }
        this.baseApiTkUrl = BASE_URL_APITK;
//        this.baseUrl = BASE_MONK_URL;
        return this.baseApiTkUrl;
    }

    public String getBaseShopUrl() {

        this.baseShopUrl = BASE_URL_SHOP;
//        this.baseUrl = BASE_MONK_URL;
        return this.baseShopUrl;
    }

    public String getBasePhotoUrl() {
//        ApplicationInfo applicationInfo = UniApplicationContext.getContext().getApplicationInfo();
//        if (applicationInfo != null && (applicationInfo.flags & 2) != 0) {
//            if (BuildConfig.isDebug) {
//                this.baseUrl = BASE_TEST_URL;
//            }
//            if (!TextUtils.isEmpty(SpUtils.getHostUrl())) {
//                this.baseUrl = SpUtils.getHostUrl();
//            }
//        } else {
//            this.baseUrl = BASE_URL;
//        }
        this.basePhotoUrl = BASE_URL_PHOTO;
//        this.baseUrl = BASE_MONK_URL;
        return this.basePhotoUrl;
    }

    public void setBaseUrl(String url) {
        ApplicationInfo applicationInfo = UniApplicationContext.getContext().getApplicationInfo();
        if (applicationInfo != null && (applicationInfo.flags & 2) != 0) {
            SpUtils.setHostUrl(url);
            this.baseUrl = url;
        }
    }

    public void init() {
        TelephonyManager tm = (TelephonyManager) UniApplicationContext.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            terminal = "4";
        } else {
            terminal = "1";
        }
        device = Build.MANUFACTURER + android.os.Build.MODEL;
        systemInfo = android.os.Build.VERSION.RELEASE;
        CACHEFILE = new File(UniApplicationContext.getContext().getCacheDir(), "HttpCache");
        initOkHttpClient();
        initRetrofit();
        initObservables();
    }

    private void initObservables() {
        RxJavaPlugins.getInstance().reset();
        // RxJavaPlugins.getInstance().registerObservableExecutionHook(new RxJavaStackTracer());
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                mSubscriber = subscriber;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .throttleFirst(5000, TimeUnit.MILLISECONDS)
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(String str) {
                        // LogUtils.e("test","401_33");
                        String code = "", message = "";
                        try {
                           // Gson gson = new Gson();
                            JsonObject jsonObject = GsonUtil.GsonToBean(str, JsonObject.class);//；、、.fromJson(str, JsonObject.class);
                            message = jsonObject.get("message").getAsString();
                            code = jsonObject.get("code").getAsString();
                        } catch (Exception e) {
                        }
                        //清除缓存
                        CACHEFILE.delete();
                        //清除个人信息
                        UserInfoUtil.clearUserInfo();
                        LogUtils.i("test", "code:" + code + ",msg:" + message);
                        if ("1110002".equals(code)) {
                            ActivityStack.getInstance().finishAllActivity();
                            LoginByPasswordActivity.newIntent(UniApplicationContext.getContext());
                        } else {
                            TokenConflictActivity.newIntent(UniApplicationContext.getContext(), message);
                        }
                    }
                });
    }

    public HttpService getService() {
        return mService;
    }

//    public PhotoHttp getPhotoService() {
//        return mPhotoService;
//    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    //只用了一次  可优化
    public HttpShopService getShopService() {
        if (mServiceShop == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseShopUrl())
                    .client(mOkHttpClient)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mServiceShop = retrofit.create(HttpShopService.class);
        }
        return mServiceShop;
    }

    //只用了一次可优化
    public HttpApiTkService getApiTkService() {
        if (mServiceApiTk == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseApiTkUrl())
                    .client(mOkHttpClient)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mServiceApiTk = retrofit.create(HttpApiTkService.class);
        }
        return mServiceApiTk;
    }


    Retrofit mRestAdapter;

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.getBaseUrl())
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRestAdapter = retrofit;
        mService = retrofit.create(HttpService.class);

    }


    public <T> T buildService(Class<T> service) {
       /* if(mRestAdapter == null){
            synchronized (RetrofitManager.class){
                if(mRestAdapter == null){




                }
            }
        }*/
        return mRestAdapter.create(service);
    }
     //https://blog.csdn.net/guoxiaolongonly/article/details/82589069
    //com.baijiahulian.common.networkv2.dns
    private void initOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (RetrofitManager.class) {
                if (mOkHttpClient == null) {

                    // 指定缓存路径,缓存大小100Mb
                    Cache cache = new Cache(new File(UniApplicationContext.getContext().getCacheDir(), "HttpCache"),
                            1024 * 1024 * 100);

                    SSLSocketBuild.TrustAllManager trustAllCert = new SSLSocketBuild.TrustAllManager();
                    okhttp3.OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder()
                            .cache(cache)
                            .authenticator(mAuthenticator)
                            .addInterceptor(mRewriteCacheControlInterceptor)
                            .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                            .sslSocketFactory(new SSLSocketFactoryCompat(trustAllCert), trustAllCert)
                            //.sslSocketFactory(SSLSocketBuild.createSSLSocketFactory())
                            .hostnameVerifier(new SSLSocketBuild.TrustAllHostnameVerifier());
                    if (BuildConfig.DEBUG) {
                        HttpExLoggingInterceptopr interceptor = new HttpExLoggingInterceptopr(new HttpExLoggingInterceptopr.Logger() {
                            @Override
                            public void log(String message) {
                                Platform.get().log(Platform.WARN, message, null);
                            }
                        });
                        interceptor.setLevel(HttpExLoggingInterceptopr.Level.BODY);
                        okhttpBuilder.addInterceptor(interceptor);
                    }
                    mOkHttpClient = okhttpBuilder
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }

    /**
     * 当 headstatus返回401时,调用该函数才会进行重新尝试请求; return null 则取消重试
     * 参考链接 https://github.com/square/okhttp/wiki/Recipes#handling-authentication
     * https://www.jianshu.com/p/62ab11ddacc8
     */
    private Authenticator mAuthenticator = new Authenticator() {
        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            ResponseBody body = response.body();
            String res = body.string();
            mSubscriber.onNext(res);
            return null;
        }
    };


    // 云端响应头拦截器，用来配置缓存策略
    private Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();  //获取request
            // boolean hasChangeRequest=false;
            List<String> headerValues = original.headers("url_name"); //从request中获取headers，通过给定的键url_ame
            if (headerValues != null && headerValues.size() > 0) {

                Request.Builder builder = original.newBuilder(); //获取request的创建者builder
                //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
                builder.removeHeader("url_name");//HttpConfig.HEADER_KEY
                //匹配获得新的BaseUrl
                String headerValue = headerValues.get(0);
                HttpUrl newBaseUrl = null;

                if ("education".equals(headerValue)) {
                    newBaseUrl = HttpUrl.parse(getBaseUrl().equals(BASE_URL) ? EDUCATIONURL : EDUCATIONURL_DEBUG);
                }
                //从request中获取原有的HttpUrl实例oldHttpUrl
                if (null != newBaseUrl) {
                    HttpUrl oldHttpUrl = original.url();
                    HttpUrl newFullUrl = oldHttpUrl.newBuilder().scheme(newBaseUrl.scheme()).host(newBaseUrl.host())
                            .port(newBaseUrl.port()).build();

                    original = builder.url(newFullUrl).build();
                    // hasChangeRequest=true;
                }
            }

            if (!NetUtil.isConnected()) {
                original = original.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }


            //可以在此配置统一的头信息
            // addHeader() 不会覆盖已经添加过的相同Header字段     header() 会覆盖
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
                    .header("subject", String.valueOf(SpUtils.getUserSubject() == -1 ? Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST : SpUtils.getUserSubject()))    // subject 科目（eg行测  申论 ）
                    .header("catgory", String.valueOf(SpUtils.getUserCatgory() == -1 ? Type.SignUpType.CIVIL_SERVANT : SpUtils.getUserCatgory()))                       // catgory 考试类型 (eg公务员)
                    .header("User-Agent", getUserAgent())
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

}
