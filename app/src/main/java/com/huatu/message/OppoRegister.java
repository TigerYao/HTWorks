package com.huatu.message;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.heytap.mcssdk.PushManager;
import com.heytap.mcssdk.callback.PushAdapter;
import com.heytap.mcssdk.callback.PushCallback;
import com.heytap.mcssdk.mode.SubscribeResult;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.taobao.accs.utl.ALog;
import com.taobao.accs.utl.UtilityImpl;
import com.taobao.agoo.BaseNotifyClickActivity;

import org.android.agoo.control.NotifManager;
import org.android.agoo.oppo.OppoMsgParseImpl;

import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Administrator on 2019\12\13 0013.
 */


public class OppoRegister {
    public static final String TAG = "OppoPush";
    private static final String OPPO_TOKEN = "OPPO_TOKEN";
    private static Context mContext;
    private static PushCallback mPushCallback = new PushAdapter() {
        public void onRegister(int code, String regid) {
            if(code == 0) {
                ALog.i("OppoPush", "onRegister regid=" + regid, new Object[0]);
               // org.android.agoo.oppo.OppoRegister.reportToken(mContext, regid);

                if(!TextUtils.isEmpty(regid) ) {
                    NotifManager notifyManager = new NotifManager();
                    notifyManager.init(UniApplicationContext.getContext());
                    notifyManager.reportThirdPushToken(regid, "OPPO_TOKEN", false);
                }

            } else {
                ALog.e("OppoPush", "onRegister code=" + code + ",regid=" + regid, new Object[0]);
            }

        }

        public void onUnRegister(int code) {
            Log.i("OppoPush", "onUnRegister code=" + code);
        }

        public void onGetAliases(int code, List<SubscribeResult> list) {
        }

        public void onSetAliases(int code, List<SubscribeResult> list) {
        }

        public void onUnsetAliases(int code, List<SubscribeResult> list) {
        }

        public void onSetUserAccounts(int code, List<SubscribeResult> list) {
        }

        public void onUnsetUserAccounts(int code, List<SubscribeResult> list) {
        }

        public void onGetUserAccounts(int code, List<SubscribeResult> list) {
        }

        public void onSetTags(int code, List<SubscribeResult> list) {
        }

        public void onUnsetTags(int code, List<SubscribeResult> list) {
        }

        public void onGetTags(int code, List<SubscribeResult> list) {
        }

        public void onGetPushStatus(int code, int status) {
        }

        public void onGetNotificationStatus(int code, int status) {
        }

        public void onSetPushTime(int code, String s) {
        }
    };

    public OppoRegister() {
    }

  /*  public static boolean isSupportPush(Context context){
         return PushManager.isSupportPush(mContext);
    }*/

    /**
     * 创建Notification ChannelID
     *
     * @return 频道id
     */
    private static String initChannelId(Context context) {
        // 通知渠道的id
        String id = "huatu_message_1";
        // 用户可以看到的通知渠道的名字.
        CharSequence name = "华图消息推送";
        // 用户可以看到的通知渠道的描述
        String description = "消息推送";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            try{
               NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
               if(null==mNotificationManager.getNotificationChannel(id)) {
                   int importance = NotificationManager.IMPORTANCE_LOW;
                   NotificationChannel mChannel;
                   mChannel = new NotificationChannel(id, name, importance);
                   mChannel.setDescription(description);
                   mChannel.enableLights(false);
                   mChannel.enableVibration(false);
                   //最后在notificationmanager中创建该通知渠道
                   mNotificationManager.createNotificationChannel(mChannel);
                 }

            }catch (Exception e){
                e.printStackTrace();
            }
         }
        return id;
    }

    public static void register(Context context, String appKey, String appSecret) {
        try {
            mContext = context.getApplicationContext();
            if(!UtilityImpl.isMainProcess(mContext)) {
                ALog.i("OppoPush", "not in main process, return", new Object[0]);
                return;
            }

            if(PushManager.isSupportPush(mContext)) {

                initChannelId(mContext);
                BaseNotifyClickActivity.addNotifyListener(new OppoMsgParseImpl());
                PushCallback pushCallback = mPushCallback;
                ALog.i("OppoPush", "register oppo begin ", new Object[0]);
                PushManager.getInstance().register(mContext, appKey, appSecret, pushCallback);
            } else {
                ALog.i("OppoPush", "not support oppo push", new Object[0]);
            }
        } catch (Throwable var4) {
            ALog.e("OppoPush", "register error", var4, new Object[0]);
        }

    }

    public static void unregister() {
        ALog.i("OppoPush", "unregister", new Object[0]);
        PushManager.getInstance().unRegister();
    }

    public static void setPushCallback(PushCallback pushCallback) {
        ALog.i("OppoPush", "setPushCallback", new Object[0]);
        PushManager.getInstance().setPushCallback(pushCallback);
    }

    public static void pausePush() {
        ALog.w("OppoPush", "pausePush", new Object[0]);
        PushManager.getInstance().pausePush();
    }

    public static void resumePush() {
        ALog.w("OppoPush", "resumePush", new Object[0]);
        PushManager.getInstance().resumePush();
    }

    private static void reportToken(Context context, String token) {
        if(!TextUtils.isEmpty(token) && context != null) {
            NotifManager notifyManager = new NotifManager();
            notifyManager.init(context.getApplicationContext());
            notifyManager.reportThirdPushToken(token, "OPPO_TOKEN", false);
        }

    }
}
