package com.huatu.handheld_huatu;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mobstat.StatService;
import com.baijiahulian.BJVideoPlayerSDK;
import com.baijiahulian.player.utils.BJLog;
import com.baijiayun.log.BJFileLog;
import com.huatu.AppContextProvider;
import com.huatu.handheld_huatu.helper.GlobalRouterInterceptor;
import com.huatu.handheld_huatu.mvpmodel.UpdateInfoBean;
import com.huatu.handheld_huatu.receiver.ConnectionBroadcastReceiver;
import com.huatu.handheld_huatu.router.HTRouterTable;
import com.huatu.handheld_huatu.tinker.TinkerManager;
import com.huatu.handheld_huatu.tinker.TinkerMethods;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.iflytek.cloud.SpeechUtility;
import com.netease.hearttouch.router.HTRouterCall;
import com.netease.hearttouch.router.HTRouterManager;
import com.networkbench.agent.impl.NBSAppAgent;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.loader.TinkerRuntimeException;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.socialize.PlatformConfig;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static com.umeng.commonsdk.utils.UMUtils.getAppName;



/**
 * Created by saiyuan on 2016/10/31.
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = "com.huatu.handheld_huatu.UniApplication",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)
public class UniApplicationLike extends DefaultApplicationLike {
    private static final String TAG = "UniApplicationLike";
    //CC加密 server
    //private DRMServer drmServer;
    private int drmServerPort;
    // 需要在接口header中添加的公共参数
    private static Handler applicationHandler;

    public UniApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                              long applicationStartElapsedTime, long applicationStartMillisTime,
                              Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime,
                applicationStartMillisTime, tinkerResultIntent);

    }

    private static UniApplicationLike instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LogUtils.i("UniApplicationLike", "UniApplicationLike onCreate start");
        UniApplicationContext.setContext(getApplication().getBaseContext());
        applicationHandler = new Handler(Looper.getMainLooper());
        UserInfoUtil.init();
        JPushInterface.init(getApplication().getBaseContext());     		// 初始化 JPush

        //科大讯飞
        SpeechUtility.createUtility(getApplication().getBaseContext(), "appid=5a3b4b1b");

        //com.baijiahulian.BJVideoPlayerSDK
        //百家云点播播放器SDK
        BJVideoPlayerSDK.getInstance().init(getApplication());

 //        UMAnalyticsConfig umAnalyticsConfig = new MobclickAgent.UMAnalyticsConfig(
//                UniApplicationContext.getContext(), "54645278fd98c565730006bc",
//                AppUtils.getChannelId(), MobclickAgent.EScenarioType.E_UM_NORMAL
//        );
//        MobclickAgent.startWithConfigure(umAnalyticsConfig);
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
        UMConfigure.init(UniApplicationContext.getContext(), "54645278fd98c565730006bc", AppUtils.getChannelId(), UMConfigure.DEVICE_TYPE_PHONE,
                "");
        MobclickAgent.setCatchUncaughtExceptions(true);
        //UMConfigure.setProcessEvent(true);
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        PushAgent mPushAgent = PushAgent.getInstance(getApplication().getApplicationContext());
        //注册推送服务 每次调用register都会回调该接口
        try{
            mPushAgent.register(new IUmengRegisterCallback() {
                @Override
                public void onSuccess(final String deviceToken) {
                    LogUtils.i(TAG, "device token: " + deviceToken + "..."+UserInfoUtil.userId + "。。。");

                    String oldToken=PrefStore.getSettingString(UserInfoUtil.userId+"_ht_umeng_devicetoken","");
                    int hasSet=!oldToken.equals(deviceToken)? 0:1;

                    if(hasSet==1||(UserInfoUtil.userId<=0)) return;
                    PushAgent.getInstance(UniApplicationContext.getContext())
                            .addAlias(String.valueOf(UserInfoUtil.userId), "personID", new UTrack.ICallBack() {
                                @Override
                                public void onMessage(boolean isSuccess, String message) {
                                    LogUtils.i(TAG, "device token: " + isSuccess+""+message);
                                    if(isSuccess)
                                        PrefStore.putSettingString(UserInfoUtil.userId+"_ht_umeng_devicetoken",deviceToken);
                                }
                            });
                }

                @Override
                public void onFailure(String s, String s1) {
                    LogUtils.i(TAG, "register failed: " + s + " " + s1);
                }
            });

        }catch (Exception e){  }

        mPushAgent.setNotificationClickHandler(new UmengNotificationClickHandlerImpl());
        mPushAgent.setMessageHandler(new UmengMessageHandlerImpl());
        mPushAgent.onAppStart();

//      听云 b9633becc59b45439b7d2383ff90bbd1
        NBSAppAgent.setLicenseKey(Constant.TING_YUN_APP_KEY).withLocationServiceEnabled(true).enableLogging(true).startInApplication(getApplication());
        // Log last 100messages
        NBSAppAgent.setLogging(100);
        NBSAppAgent.setUserIdentifier(String.format("ht_%s", SpUtils.getUid() + ""));//听云添加用户标记

        initInMainProcess();
        if (Tinker.with(getApplication()).isTinkerLoaded()) {
            LogUtils.i("UniApplicationLike", "Tinker is installed");
        } else {
            LogUtils.i("UniApplicationLike", "Tinker is not installed");
        }
        if (SpUtils.getTinkerClearFlag() && !TextUtils.isEmpty(SpUtils.getTinkerInstalledPatchMd5())
                && Tinker.with(getApplication()).isTinkerLoaded()) {
            LogUtils.i("UniApplicationLike", "Tinker is installed, but we want to uninstall it");
            Tinker.with(getApplication()).cleanPatch();
            MobclickAgent.onEvent(UniApplicationContext.getContext(), "TinkerUninstall",String.valueOf(AppUtils.getVersionCode()));
            SpUtils.setTinkerClearFlag(false);
            SpUtils.clearTinkerInstalledPatchMd5();
            AppUtils.killProgress();
        }
        LogUtils.i("UniApplicationLike", "UniApplicationLike onCreate end");

        BJFileLog.setMaxRecordDay(7);
        BJLog.LOG_OPEN=BuildConfig.DEBUG;
    }


    ConnectionBroadcastReceiver mNetBroadcastReceiver;
    private void initPushChannel(){
        //此段是参考推送注册内部逻辑
 /*       if(!UtilityImpl.isMainProcess(getApplication())) {
            Log.i("UtilityImpl", "not in main process, return");
            return;
        }*/

        //设备放在外面减少判断
        if(TextUtils.equals("Xiaomi".toLowerCase(), Build.BRAND.toLowerCase())){
            MiPushRegistar.register(UniApplicationContext.getContext(),"2882303761517184205", "5151718431205");
        }
        else if(Build.BRAND.equalsIgnoreCase("huawei") || Build.BRAND.equalsIgnoreCase("honor")) {
           // HuaWeiHackRegister.register(UniApplicationContext.getApplication());
            HuaWeiRegister.register(UniApplicationContext.getApplication());
        }else {

            com.huatu.message.OppoRegister.register(UniApplicationContext.getContext(), "61CL4ilb2eo8c0k0gwWgsos40", "4Bd90bf76F5034193dcFcea733bab343");
            //vivo 通道
            VivoRegister.register(UniApplicationContext.getContext());
        }

        /*else if(com.huatu.message.OppoRegister.isSupportPush(UniApplicationContext.getApplication())){
           // OppoRegister.register(UniApplicationContext.getContext(), "61CL4ilb2eo8c0k0gwWgsos40", "4Bd90bf76F5034193dcFcea733bab343");
            com.huatu.message.OppoRegister.register(UniApplicationContext.getContext(), "61CL4ilb2eo8c0k0gwWgsos40", "4Bd90bf76F5034193dcFcea733bab343");

        }*/
    }

    private void initInMainProcess() {
        //todo 只在主进程进行初始化!!!
        boolean flag = (getApplication().getApplicationInfo().packageName.equals(CommonUtils.getCurProcessName(getApplication().getApplicationContext())));
        // LogUtils.e("initInMainProcess_"+flag);
        if (flag) {
            LogUtils.e("initInMainProcess_" + 1);
            initPushChannel();
            // getApplication().getApplicationContext().getApplicationInfo()..setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            UniApplicationContext.setPushAgent(PushAgent.getInstance(getApplication().getApplicationContext()));

            StatService.autoTrace(getApplication().getApplicationContext(), true, false);
            StatService.setAppChannel(getApplication().getApplicationContext(), AppUtils.getChannelIdBaiDu(), true);

            initSensorsDataSDK(getApplication().getApplicationContext());
            initTinkerInfo();

            // 设置第三方平台appkey 华图在线
            //PlatformConfig.setWeixin("wxd0611111b31aa452", "a89fe2312272d5c555ed9a2a6eb7b4f8");
            // PlatformConfig.setWeixin("wxd0611111b31aa452", "a89fe2312272d5c555ed9a2a6eb7b4f8");
            PlatformConfig.setWeixin("wxd0611111b31aa452", "fee78ac9f3e5fc2d452affb548c88d2d");
            PlatformConfig.setSinaWeibo("3727108781", "c8ab0ba6c295af7ce3fcc756f34b7837", "http://v.huatu.com");
            PlatformConfig.setQQZone("1103583915", "xhcEZi7btQteqUOC");


            //x5内核初始化接口  方法数太多，暂时注掉
            // QbSdk.initX5Environment(getApplication(), null);
            // QbSdk.setDownloadWithoutWifi(true);

            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            filter.addAction("com.huatu.start_download_course");

            // filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            // filter.addAction("android.net.wifi.STATE_CHANGE");

            //https://blog.csdn.net/LeongAndroid/article/details/78276572
            mNetBroadcastReceiver = new ConnectionBroadcastReceiver();
            getApplication().getApplicationContext().registerReceiver(mNetBroadcastReceiver, filter);
            AppContextProvider.INSTANCE=getApplication().getApplicationContext();

            // 初始化路由表
            HTRouterCall.init();
            new HTRouterTable().init(null);

            //初始化智齿sdk
            com.huatu.handheld_huatu.helper.SobotChatServer.init();

           //处理每次跳转监听 用户打点统计等
            HTRouterCall.addGlobalInterceptors(new GlobalRouterInterceptor());

             //注册绑定默认的降级页面
           // String customUrlKey = "customUrlKey"; //HTWebActivity接收参数key
            //HTRouterManager.registerWebActivity(ActionDetailActivity.class, customUrlKey);
            //开启Debug模式，输出相应日志
            HTRouterManager.setDebugMode(BuildConfig.DEBUG);
            if (LeakCanary.isInAnalyzerProcess( getApplication().getApplicationContext())) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }

            LeakCanary.install(getApplication());
            // LogUtils.e("initInMainProcess_"+3);
        }
    }

    private final String SA_SERVER_URL_DEBUG="https://datax-api.huatu.com/sa?project=default";
    private final String SA_SERVER_URL_RELEASE="https://datax-api.huatu.com/sa?project=production";
    /**
     * 初始化 SDK 、设置公共属性、开启自动采集
     */
    private void initSensorsDataSDK(Context context) {
        try {
            // 初始化 SDK
            SensorsDataAPI.sharedInstance(
                    context,                                                                                  // 传入 Context
                    (BuildConfig.isDebug) ? SA_SERVER_URL_DEBUG : SA_SERVER_URL_RELEASE,       // 数据接收的 URL
                    BuildConfig.isDebug ? SensorsDataAPI.DebugMode.DEBUG_AND_TRACK : SensorsDataAPI.DebugMode.DEBUG_OFF); // Debug 模式选项
            SensorsDataAPI.sharedInstance().enableLog(BuildConfig.isDebug);


            // 将'平台类型'作为事件公共属性，后续所有 track() 追踪的事件都将设置 "PlatformType" 属性，且属性值为 "Android"
            try {
                JSONObject properties = new JSONObject();
                properties.put("business_line", "华图在线");
                properties.put("platform", "AndroidApp");
                properties.put("product_name", "华图在线");
                properties.put("domain_first_classification", "v");

                // 初始化SDK后，获取应用名称设置为公共属性
                properties.put("app_name", getAppName(context));

                SensorsDataAPI.sharedInstance().registerSuperProperties(properties);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 初始化SDK后，获取应用名称设置为公共属性
         /*   JSONObject properties = new JSONObject();
            properties.put("app_name", getAppName(context));
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties);*/

            // 打开自动采集, 并指定追踪哪些 AutoTrack 事件
            List<SensorsDataAPI.AutoTrackEventType> eventTypeList = new ArrayList<>();
            // $AppStart
            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_START);
            // $AppEnd
            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_END);
            // $AppViewScreen
            eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_VIEW_SCREEN);
            // $AppClick
           // eventTypeList.add(SensorsDataAPI.AutoTrackEventType.APP_CLICK);
            SensorsDataAPI.sharedInstance().enableAutoTrack(eventTypeList);

            try {
                JSONObject properties2 = new JSONObject();
                properties2.put("DownloadChannel", AppUtils.getChannelId());
                SensorsDataAPI.sharedInstance().trackInstallation("AppInstall",properties2);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(!TextUtils.isEmpty(UserInfoUtil.ucId)){
                SensorsDataAPI.sharedInstance().login(String.valueOf(UserInfoUtil.ucId));

                // 注册成功/登录成功（调用 login 方法）后 保存 jgId 到用户表
                String registrationId=JPushInterface.getRegistrationID(context);
                if(!TextUtils.isEmpty(registrationId)){
                    SensorsDataAPI.sharedInstance().profilePushId("jgId",registrationId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 启动DRMServer
  /*  public void startDRMServer() {
        if (drmServer == null) {
            drmServer = new DRMServer();
            drmServer.setRequestRetryCount(10);
            Log.e("drmServer","启动成功");
        }

        try {
            drmServer.start();
            setDrmServerPort(drmServer.getPort());
        } catch (Exception e) {
        }
    }
    public int getDrmServerPort() {
        return drmServerPort;
    }

    public void setDrmServerPort(int drmServerPort) {
        this.drmServerPort = drmServerPort;
    }

    public DRMServer getDRMServer() {
        return drmServer;
    }*/

    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        try {
            MultiDex.install(base);
            UniApplicationContext.setApplication(getApplication());
            TinkerManager.setTinkerApplicationLike(this);
            TinkerManager.initFastCrashProtect();
            //should set before tinker is installed
            TinkerManager.setUpgradeRetryEnable(true);
            //optional set logIml, or you can use default debug log
            //        TinkerInstaller.setLogIml(new MyLogImp());
            //installTinker after load multiDex
            //or you can put com.tencent.tinker.** to main dex
            TinkerManager.installTinker(this);
            Tinker.with(getApplication());
            Log.i("Netschool", "start load MultiDex end");
        } catch (TinkerRuntimeException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

    public static Handler getApplicationHandler() {
        return applicationHandler;
    }

    private void initTinkerInfo() {

        //*  Security.insertProviderAt(  new org.conscrypt.OpenSSLProvider(), 1);*//*

        String updateBean = SpUtils.getUpdateBean();
        if (null != updateBean && !TextUtils.isEmpty(updateBean)) {
            UpdateInfoBean data = GsonUtil.GsonToBean(updateBean, UpdateInfoBean.class);
            if (data != null && data.appVersionBean != null) {
                UpdateInfoBean.AppVersion updateInfo = data.appVersionBean;
                if (!updateInfo.isPatch) {  return;

                } else if (!TextUtils.isEmpty(updateInfo.patchUrl) && !TextUtils.isEmpty(updateInfo.patchMd5)) {
                    SpUtils.setTinkerUrl(updateInfo.patchUrl);
                    SpUtils.setTinkerMd5(updateInfo.patchMd5);
                    SpUtils.setTinkerClearFlag(false);
                    Log.i("UniApplicationLike", "Get Tinker Info, getTinkerMd5:" + SpUtils.getTinkerMd5()
                            + ", setTinkerInstalledPathMd5:" + SpUtils.getTinkerInstalledPatchMd5());
                    if ((!updateInfo.patchMd5.equals(SpUtils.getTinkerInstalledPatchMd5()))
                            &&(updateInfo.patchUrl.endsWith(String.valueOf(AppUtils.getVersionCode())+".patch"))) {//
//                            ToastUtils.showShort("开始加载patch");
                        TinkerMethods.loadPatch();
                    } else {
                        //onError();
                    }
                } else if ((!TextUtils.isEmpty(updateInfo.patchUrl))
                        &&(updateInfo.patchUrl.equals("clear"+AppUtils.getVersionCode()))) {
                    SpUtils.setTinkerClearFlag(true);
                   // onError();
                } else {
                   // onError();
                }
            } else {
               // onError();
            }
        }
    }

    public void onError() {
        SpUtils.clearUpdateFlag();
        SpUtils.clearUpdateLevelFlag();
        SpUtils.clearUpdateLatestVersion();
        SpUtils.clearUpdateMessage();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        if (null != mNetBroadcastReceiver)
            getApplication().getApplicationContext().unregisterReceiver(mNetBroadcastReceiver);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.i("UniApplicationLike", "application is in background");
            UniApplicationContext.isAppInBackground = true;

        }

    }

    public static UniApplicationLike getInstance() {
        return instance;
    }


   /* private void initPicasso() {
        //Picasso.setSingletonInstance(new Picasso.Builder(this.getApplication()).downloader(new ImageDownLoader()).loggingEnabled(true).build());
    }*/
}
