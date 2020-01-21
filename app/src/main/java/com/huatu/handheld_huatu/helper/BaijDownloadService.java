package com.huatu.handheld_huatu.helper;

/**
 * Created by Administrator on 2019\5\5 0005.
 */
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baijiayun.download.DownloadManager;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.LogUtils;

import java.util.List;

/**
 * Created by Shubo on 2017/12/8.
 * 自定义下载守护服务, 8.0及以上系统需要自定义前台通知样式
 * android 8.0之后不再支持应用后台创建service，需要用户自行实现后台下载保活
 */

public class BaijDownloadService extends Service {

    private static DownloadManager manager;
    private static final String CHANNEL_ID = "bj_download_channel_id";
    private static Context mContext;
    private static boolean hasStartedService;
    /**
     * start 方式开启服务，保存全局的下载管理对象
     */
    public static DownloadManager getDownloadManager(Context context) {
        mContext = context.getApplicationContext();
        if (BaijDownloadService.manager == null) {
            BaijDownloadService.manager = DownloadManager.getInstance(context);
           // startService();
        }
        return manager;
    }

    public static boolean isServiceRunning(Context context) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = null;
        if (activityManager != null) {
            serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        }
        if (serviceList == null || serviceList.size() == 0) return false;
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(BaijDownloadService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    //真正开启service
    public static void startService(){
        if(mContext != null && !hasStartedService) {
            if (!BaijDownloadService.isServiceRunning(mContext)) {
                try{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mContext.startForegroundService(new Intent(mContext, BaijDownloadService.class));
                    } else {
                        mContext.startService(new Intent(mContext, BaijDownloadService.class));
                    }

                }catch (Exception e){}

            }/*else {//有bug
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//Context.startForegroundService() did not then call Service.startForeground()
                    mContext.startForegroundService(new Intent(mContext, BaijDownloadService.class));
                }
            }*/

            hasStartedService = true;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    BroadcastReceiver myReceiver;
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null!=myReceiver){
            unregisterReceiver(myReceiver);
            myReceiver=null;
        }
     }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"下载", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
            //TODO 业务方自行决定notification的样式
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("华图在线")
                    .setContentText("课件下载中")
                    .setSmallIcon(R.drawable.icon_app)//android.R.drawable.sym_def_app_icon
                    .build();
            startForeground(1001, notification);
            LogUtils.d("bjy", "onCreate startForeground");

            IntentFilter filter = new IntentFilter();
            filter.addAction("huatudownserviceStop");
            //注册
            registerReceiver(new BroadcastReceiver(){

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if("huatudownserviceStop".equals(action)){
                        //屏幕开启和关闭
                        stopForeground(true);
                    }
                }
            }, filter);

        }
    }

    //隐藏通知
    public static void cancelNotification() {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }
        if(null!=mContext&&hasStartedService){
            NotificationManager notificationManager = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));
            if(notificationManager != null){
                notificationManager.cancel(1001);
                hasStartedService=false;
                Intent intent = new Intent("huatudownserviceStop");
                mContext.sendBroadcast(intent);
            }
         }

    }
}
