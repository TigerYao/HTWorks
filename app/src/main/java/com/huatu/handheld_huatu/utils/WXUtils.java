package com.huatu.handheld_huatu.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.huatu.handheld_huatu.business.ztk_zhibo.play.Utils;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class WXUtils {
    // 跳转到详情页
    public static boolean appToWxApp(Context ctx, String activityId, String classId, int videoType){
        String path = "/pages/activityInfo/components/activityDetail?activityId=" +
                activityId+"&classId=" +
                classId+"&isLive="+videoType;
        return sendWXRe(ctx, path);
    }

    private static boolean sendWXRe(Context ctx, String path) {
        IWXAPI api = WXAPIFactory.createWXAPI(ctx, "wxd0611111b31aa452");
        boolean isInstalled = api.isWXAppInstalled();
        if (!isInstalled) {
            ToastUtils.showShort("没有安装微信");
            return false;
        }
        if (!api.isWXAppSupportAPI()){
            ToastUtils.showShort("微信版本过低");
            return false;
        }
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = "gh_e39a7a3a4f3d"; // 填小程序原始id
        req.path = path + "&from=app";                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = Utils.isApkInDebug(ctx) && SpUtils.getTestUrlPosition() > 0 ? WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW : WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        return api.sendReq(req);
    }

    //从订单跳转到小程序
    public static boolean appOrderToWxApp(Context ctx, String orderId){
        String path = "/pages/userInfo/order/coupeOrderDetail?id="+orderId;
        return sendWXRe(ctx, path);
    }

    /**
     * 打开微信
     * @param context context
     */
    public static boolean openWeXin(Context context){
        try {
            Uri uri = Uri.parse("weixin://");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception ex){
            ToastUtils.showShort(context, "请安装最新版微信后重试！");
            return false;
        }
    }

    /**
     * 打开微信扫一扫
     * @param context context
     */
    public static boolean openWeXinQr(Context context){
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
            return true;
        } catch (Exception ex){
            return openWeXin(context);
        }
    }
}
