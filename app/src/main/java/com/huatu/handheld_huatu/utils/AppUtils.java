package com.huatu.handheld_huatu.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.ImageView;


import com.huatu.utils.StringUtils;
import com.meituan.android.walle.WalleChannelReader;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by ht on 2016/7/15.
 */
public class AppUtils {
    private static int versionCode = -1;
    private static String versionName = "";
    private static String channelId = "";

    /**
     * 获取本地应用APK的版本号
     *
     * @return 版本号，如：1
     */
    public static int getVersionCode() {
        if(versionCode < 0) {
            try {
                Context context = UniApplicationContext.getContext();
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                versionCode = info.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return versionCode;
    }

    /**
     * 获取app的版本名
     *
     * @return
     */
    public static String getVersionName() {
        if(TextUtils.isEmpty(versionName)) {
            try {
                Context context = UniApplicationContext.getContext();
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                versionName = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                versionName = "";
            }
        }
        return versionName;
    }

    /**
     * 获取程序版本号
     */
    public static String getAppVersion() {
        try {
            PackageManager manager = UniApplicationContext.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(UniApplicationContext.getContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isVersionLower(String curVersion,String updateVersion){
        if(TextUtils.isEmpty(curVersion)||TextUtils.isEmpty(updateVersion)) return false;

        if(curVersion.length()==updateVersion.length()){
            int curVersionInt= StringUtils.parseInt(curVersion.trim().replace(".",""));
            int updateVersionInt=StringUtils.parseInt(updateVersion.trim().replace(".",""));
            return curVersionInt<updateVersionInt;
        }else {
            String[] oldArr=curVersion.split("\\.");  //“.”和“|”都是转义字符,必须得加"\\";
            String[] newArr=updateVersion.split("\\.");

            if(oldArr.length==newArr.length){
                for(int i=0;i<oldArr.length;i++){
                    int oldValue=StringUtils.parseInt(oldArr[i]);
                    int newValue=StringUtils.parseInt(newArr[i]);
                    if(oldValue< newValue)
                        return true;
                    else if(oldValue>newValue)
                        return false;
                }
                return false;
            }
            return false;
        }
    }

   /* public static void setImage(Context context, ImageView image) {
        String avatar = SpUtils.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(context)
                    .load(avatar)
                    .transform(new CircleTransform(context))
                    .placeholder(R.drawable.icon_default)
                    .error(R.drawable.icon_default)
                    .skipMemoryCache(false)
                    .placeholder(R.mipmap.image11)
                    .crossFade()
                    .error(R.mipmap.image11)
                    .into(image);
        }
    }*/

    public static void killProgress() {
        LogUtils.i("current thread PID: " + android.os.Process.myPid());
        UniApplicationLike.getApplicationHandler().post(new Runnable() {
            @Override
            public void run() {
                LogUtils.i("system thread PID: " + android.os.Process.myPid());
                MobclickAgent.onKillProcess(UniApplicationContext.getContext());
                android.os.Process.killProcess(android.os.Process.myPid());
                ShareTinkerInternals.killAllOtherProcess(UniApplicationContext.getContext());
            }
        });
    }

    public static String getChannelId() {
//        String channel = getAppMetaData("UMENG_CHANNEL");
        if(TextUtils.isEmpty(channelId)) {
            try {
                channelId = WalleChannelReader.getChannel(
                        UniApplicationContext.getContext());
                LogUtils.i("channelId is " + channelId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(TextUtils.isEmpty(channelId)) {
            channelId = "ht00000020";
        }
        return channelId;
    }
    private static String channelIdBaidu = "";
    /**
     * create by guangju
     * 为了保证后台还能使用以前的渠道id
     *
     * @return 渠道id
     */
    public static String getChannelIdBaiDu() {
//        String channel = getAppMetaData("UMENG_CHANNEL");
        if (TextUtils.isEmpty(channelIdBaidu)) {
            try {
                channelIdBaidu = WalleChannelReader.getChannel(
                        UniApplicationContext.getContext());

                if (TextUtils.isEmpty(channelIdBaidu)) {
                    channelIdBaidu = "20";
                }
               // LogUtils.i("channelId is " + channelId);
                switch (channelIdBaidu) {
                    case "vhuatu":
                        channelIdBaidu = "180001";
                        break;
                    case "mvhuatu":
                        channelIdBaidu = "180002";
                        break;
                    case "upgrade":
                        channelIdBaidu = "180003";
                        break;
                    case "baidu":
                        channelIdBaidu = "180004";
                        break;
                    case "huawei":
                        channelIdBaidu = "180005";
                        break;
                    case "vivo":
                        channelIdBaidu = "180006";
                        break;
                    case "oppo":
                        channelIdBaidu = "180007";
                        break;
                    case "samsung":
                        channelIdBaidu = "180008";
                        break;
                    case "uc":
                        channelIdBaidu = "180009";
                        break;
                    case "qq":
                        channelIdBaidu = "180010";
                        break;
                    case "xiaomi":
                        channelIdBaidu = "180011";
                        break;
                    case "meizu":
                        channelIdBaidu = "180012";
                        break;
                    case "360":
                        channelIdBaidu = "180013";
                        break;
                    case "smartisan":
                        channelIdBaidu = "180014";
                        break;
                    case "sougouzhushou":
                        channelIdBaidu = "180015";
                        break;
                    case "gionee":
                        channelIdBaidu = "180016";
                        break;
                    case "lenovo":
                        channelIdBaidu = "180017";
                        break;
                    case "mumayi":
                        channelIdBaidu = "180018";
                        break;
                    case "web":
                        channelIdBaidu = "180019";
                        break;
                    case "anzhi":
                        channelIdBaidu = "180020";
                        break;
                    case "toutiao":
                        channelIdBaidu = "180050";
                        break;
                    case "baidufeed":
                        channelIdBaidu = "180051";
                        break;
                    case "openqq":
                        channelIdBaidu = "180052";
                        break;
                 /*   case "baitongsousuo":
                        channelIdBaidu = "180053";
                        break;
                    case "baitongfenfa":
                        channelIdBaidu = "180054";
                        break;
                    case "baitongjingjia":
                        channelIdBaidu = "180055";
                        break;*/
                    case "360sem":
                        channelIdBaidu = "180053";
                        break;
                    case "baidufeed1":
                        channelIdBaidu = "180054";
                    case "baidufeed2":
                        channelIdBaidu = "180055";
                    case "baidufeed3":
                        channelIdBaidu = "180056";
                    case "baidufeed4":
                        channelIdBaidu = "180057";
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(channelIdBaidu)) {
            channelIdBaidu = "20";
        }
        return channelIdBaidu;
    }


    public static String getAppMetaData(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = UniApplicationContext.getContext().getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                        UniApplicationContext.getContext().getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null && applicationInfo.metaData != null) {
                    resultData = applicationInfo.metaData.getString(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultData;
    }

    /**
     * 获取本地应用APK的包名
     *
     * @return
     */
    public static String getPackageName() {
        try {
            Context context = UniApplicationContext.getContext();
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.packageName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
