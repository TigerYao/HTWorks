package com.huatu.handheld_huatu.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baijiahulian.common.permission.AppPermissions;
import com.baijiahulian.downloader.download.DownloadInfo;
import com.baijiahulian.livecore.models.LPDataModel;
import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;

import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * 放置一些通用的方法
 * Created by KaelLi on 2016/7/11.
 */
public class CommonUtils {
    public  static boolean hasX5nited=false;
    public static boolean hasX5nited(){
       /* int flag= PrefStore.getSettingInt("htx5webviewhasinit",0);
        if(X5WebView.hasX5nited||(flag==1)) {

            if(flag==0) PrefStore.putSettingInt("htx5webviewhasinit",1);
            return true;
        }
        return false;*/
       return hasX5nited&&(Build.VERSION.SDK_INT <29);
     }
    private static long lastClickTime;

   // public static final long MEMORYSIZE = 200;
    /*public static final String APKFILEPATH = Environment.getExternalStorageDirectory()
            + "/new.apk";*/
    //public static final String MOBILEDATASWITCHER = "mobileDataSwitcher";//是否使用移动数据进行下载或者观看视频;




    /**
     * 显示toast
     * KaelLi 2016/7/11
     *
     * @param s 显示内容
     */
    public static void showToast(String s) {
         ToastUtils.showShortToast(UniApplicationContext.getContext(),s);
    }

    /**
     * 显示toast
     * KaelLi 2016/7/11
     *
     * @param res string资源文件id
     */
    public static void showToast(int res) {
        if (Looper.myLooper() != Looper.getMainLooper()) return;
        ToastUtils.showShortToast(res);

    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     * <p>
     * param context
     *
     * @return 平板返回 True，手机返回 False
     */
/*    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }*/
    public static String getDataTime(long time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        return df.format(time);
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }

        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getFormatData(String format, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(time);
        return currentTime;
    }

/*    public static String getSCDayFormatData(long time){
        return getFormatData("yyyy年MM月dd日",time);
    }

    public static String getSCHourFormatData(long time){
        return getFormatData("HH:mm",time);
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    */

    public static String getFormatTime(String format, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(time);
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long step = time - lastClickTime;
        if (0 < step && step < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isFastDoubleClick(final int duration) {
        long time = System.currentTimeMillis();
        long step = time - lastClickTime;
        if (0 < step && step < duration) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0 || "0".equals(str);
    }
/*    public static boolean getMobileDataSetting() {
        return (boolean) CommonUtils.getSP(CommonUtils.MOBILEDATASWITCHER, false);
    }

    public static void setMobileDataSetting(boolean isOpen) {
        CommonUtils.putSP(CommonUtils.MOBILEDATASWITCHER, isOpen);
    }*/


    public static boolean isMobileDataConnectedForCurrent() {
        ConnectivityManager manager = (ConnectivityManager) UniApplicationContext.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.isConnected()) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    return true;
                }
            } else {
                //WIFI已断开,移动数据已断开
            }
        } else {   // not connected to the internet
            //WIFI已断开,移动数据已断开
        }

        return false;
    }

    /**
     * KaelLi, 2016/2/18
     * 从SharedPreferences中获取值的公共方法 -> config
     *
     * @param key          获取值的key
     * @param defaultValue 获取值的默认值
     */
    public static Object getSP(String key, Object defaultValue) {
        SharedPreferences sp = UniApplicationContext.getApplication().getSharedPreferences("config", Context.MODE_PRIVATE);
        if (defaultValue instanceof String) {
            return sp.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return sp.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Long) {
            return sp.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Float) {
            return sp.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultValue);
        }
        return "";
    }

    /**
     * KaelLi, 2016/2/18
     * 向SharedPreferences中提交值的公共方法 -> config
     *
     * @param key   提交值的key
     * @param value 提交值
     */
    public static void putSP(String key, Object value) {
        SharedPreferences sp = UniApplicationContext.getApplication().getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof String) {
            if (value != null) {
                editor.putString(key, (String) value);
            } else {
                Log.e("CommonUtils", " putSp:" + key + "值为空");
            }
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        editor.apply();
    }

    // 判断sd剩余空间
    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }


    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return "";
        try {
            for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static DecimalFormat df = new DecimalFormat("0.0"); //取所有整数部分

    public final static void setSpaceTip(TextView spaceTip, long downSize) {
        float totalAvailableSpace = ((float) FileUtil.getAvailableSpaceSize()) / 1024;//单位M
        String sizeDes = "";

        if (totalAvailableSpace > 1024) {
            totalAvailableSpace = totalAvailableSpace / 1024;
            sizeDes = df.format(totalAvailableSpace) + "G";
        } else {
            sizeDes = df.format(totalAvailableSpace) + "M";
        }

        String as_detail = "剩余" + StringUtils.fontColor("#5163F1", sizeDes) + "可用";
        if (downSize > 0) {
            as_detail = "预计新添加" + StringUtils.fontColor("#5163F1", df.format(((float) downSize) / 1024 / 1024) + "M") + "，" + as_detail;
        }
        //
        if (spaceTip != null)
            spaceTip.setText(ResourceUtils.getStringForHtml(as_detail));
    }

    public final static String formatSpaceSize(long space) {
        float total = ((float) space / 1024 / 1024);
        String spaceStr = total <= 0.1 ? (int) (space / 1024) + "K" :
                total > 1024 ? df.format(total / 1024) + "G" : df.format(total) + "M";
        return spaceStr;

    }

    public final static void checkPower(final Activity activity, final Action1<Boolean> callback) {
        AppPermissions.newPermissions(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            if (callback != null) callback.call(true);
                        } else {
                            ToastUtils.showMessage("没有获取读写sd卡权限");
                        }
                    }
                });

    }

    public final static void checkPowerAndTraffic(final Activity activity, final Action1<Boolean> callback) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("当前网络不可用~");
            return;
        }
        if (NetUtil.isWifi()) {
            checkPower(activity, callback);
        } else {
            boolean downFlag = PrefStore.canDownloadIn3G();
            if (downFlag) {
                checkPower(activity, callback);
            } else {
                DialogUtils.onShowWarnTraffic(activity, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkPower(activity, callback);
                    }
                });
            }
        }
    }

    public static int checkIntervalDay() {
        String meLookTime = PrefStore.getSettingString(Constant.APPSTORE_INTERVALDAY_FLAG, "");
        if (TextUtils.isEmpty(meLookTime)) {
            PrefStore.putSettingString(Constant.APPSTORE_INTERVALDAY_FLAG, System.currentTimeMillis() + "");
            return 1;
        }

        long time = StringUtils.parseLong(meLookTime);
        int delayDay = (int) ((System.currentTimeMillis() - time) / (1000 * 60 * 60 * 24));
        return delayDay;

    }


    public static boolean checkPlayRewardHasDay(int isFree) {
        String flag = "check_playreward_interval_day" + isFree;
        String meLookTime = PrefStore.getSettingString(flag, "");
        if (TextUtils.isEmpty(meLookTime)) {
            PrefStore.putSettingString(flag, TimeUtils.getCurrentTime("yyyyMMdd"));
            return true;
        }
        String curDate = TimeUtils.getCurrentTime("yyyyMMdd");
        LogUtils.e("checkPlayRewardHasDay", meLookTime + "," + curDate);
        if (curDate.equals(meLookTime)) return false;
        else {
            PrefStore.putSettingString(flag, curDate);
            return true;
        }
    }
    /* public static int getTimeDistance(Date beginDate , Date endDate ) {
     *//* Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(beginDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        long beginTime = beginCalendar.getTime().getTime();
        long endTime = endCalendar.getTime().getTime();
        int betweenDays = (int) ((endTime - beginTime) / (1000 * 60 * 60 * 24));//先算出两时间的毫秒数之差大于一天的天数

        endCalendar.add(Calendar.DAY_OF_MONTH, -betweenDays);//使endCalendar减去这些天数，将问题转换为两时间的毫秒数之差不足一天的情况
        endCalendar.add(Calendar.DAY_OF_MONTH, -1);//再使endCalendar减去1天
        if (beginCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH))//比较两日期的DAY_OF_MONTH是否相等
            return betweenDays + 1;    //相等说明确实跨天了
        else
            return betweenDays + 0;    //不相等说明确实未跨天*//*

        long beginTime = beginDate.getTime();
        long endTime = endDate.getTime();
        long betweenDays = (long)((endTime - beginTime) / (1000 * 60 * 60 *24));

    }*/



    public static String getShortFileName2(DownloadInfo downloadInfo) {

        String taskKey = downloadInfo.getTaskKey();
        if (TextUtils.isEmpty(taskKey) || (!taskKey.contains("_")))
            return downloadInfo.getFileName();//.fileName;
        String realKey = taskKey.split("_")[0];
        if (downloadInfo.getFileName().contains(realKey)) {
            int lastIndex = downloadInfo.getFileName().indexOf(realKey);
            if (lastIndex <= 0) return downloadInfo.getFileName();
            return downloadInfo.getFileName().substring(0, lastIndex - 1);
        }
        return downloadInfo.getFileName();
    }


    public static void sharePdfFile(String filePath, String tilte, Activity activity) {

        if (!FileUtil.isFileExist(filePath)) {
            ToastUtils.showShort("文件不存在");
            return;
        }
        String localPath = filePath;
        String newPath = localPath.replace(localPath.substring(localPath.lastIndexOf("/") + 1, localPath.lastIndexOf(".")), StringUtils.filterFileName(tilte));

//        File data = new File(course.localPath);

        if (!FileUtil.isFileExist(newPath)) {
            FileUtil.copyFile(localPath, newPath);
        }
        File newFile = new File(newPath);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileprovider", newFile);
        } else {
            uri = Uri.fromFile(newFile);
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType(CommonUtils.getMIMEType(newFile));

        try {
            activity.startActivity(Intent.createChooser(intent, "讲义分享到"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //https://blog.csdn.net/liuxu0703/article/details/70145168
    public static Activity getActivityFromView(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }


    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return findActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }

    //查看文件的后缀名，对应的MIME类型
    private static final String[][] MIME_MapTable = {
            //word文档
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            //excel文档
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            //ppt文档
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            //pdf文档
            {".pdf", "application/pdf"},
    };

    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }


    public static int getNaviHeight(Activity activity) {
        if (activity == null) {
            return 0;
        }
        Display display = activity.getWindowManager().getDefaultDisplay();
        int contentHeight = activity.getResources().getDisplayMetrics().heightPixels;
        int realHeight = 0;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final DisplayMetrics metrics = new DisplayMetrics();
            display.getRealMetrics(metrics);
            realHeight = metrics.heightPixels;
        } else {
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return realHeight - contentHeight;
    }


    private static int mIsPad = -1;

    public static boolean isPadv2(Context context) {

        if(mIsPad==-1){
            mIsPad=(context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE?1:0;
            return mIsPad==1;

        }
        return mIsPad==1;
    }

    public static boolean isPad(Context context) {
         if(mIsPad==-1){
            mIsPad=(context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE?1:0;
            return mIsPad==1;

        }
        LogUtils.e("isPad",mIsPad==1?"true":"false");
        return mIsPad==1;
    }
/*    public static boolean requestSlientPower(){
        NotificationManager notificationManager = (NotificationManager)UniApplicationContext.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            UniApplicationContext.getContext().startActivity(intent);

            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
            return false;
        }
        return true;
    }*/


    /**
     * 设置透明/沉浸式状态栏
     * KaelLi, 2016/7/11
     *
     * @ activity
     */
 /*   public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }*/

    public static boolean date_compare(String startTime, String endTime) {

        if (CommonUtils.date_transform(startTime, "MM月dd日").equals(CommonUtils.date_transform(endTime, "MM月dd日"))) {
            return true;
        }

        return false;

    }

    public static String formatScore(double score){

        int shortScore=(int)score;
        if(shortScore==score) return String.valueOf(shortScore);
        return String.valueOf(score);

    }
    /**
     * 日期格式的转换
     *
     * @param date      日期
     * @param transform 想要转换的格式
     * @return
     */
    public static String date_transform(String date, String transform) {
        if(TextUtils.isEmpty(date)) return "";
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            SimpleDateFormat format = new SimpleDateFormat(transform);

            Date transformTime = simpleDateFormat.parse(date);

            return format.format(transformTime).toString();

        } catch (Exception e) {
            return "";
        }
    }

    public static boolean checkLogin(Context context){
        if (!SpUtils.getLoginState()){
            Intent intent = new Intent(Constant.APP_LOGIN_ACTION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ArgConstant.QUICK_LOGIN,true);

            if(null!=context){
               context.startActivity(intent);
            }
            else {
                UniApplicationContext.getContext().startActivity(intent);
            }

            return false;
        }else  return true;
    }

   // private static boolean shouldJudege = false;
    public static void startLiveRoom(CompositeSubscription subscription, final Context ctx,
                                     final String classId, String lessionId,
                                     final String parentId, String joinCode, final String roomId, String sign){

        //userNick, userAvatar, userNumber
        Intent intent = new Intent("com.baijiahulian.live.ui.onliveroomshow");
        if (Utils.isEmptyOrNull(sign) || UserInfoUtil.getLiveUserInfo() == null){
            // LiveSDKWithUI.enterRoom(ctx, joinCode, SpUtils.getUname(), SpUtils.getAvatar(), liveSDKEnterRoomListener);

            if (TextUtils.isEmpty(SpUtils.getUname())||TextUtils.isEmpty(joinCode)) {
                if (ctx instanceof Activity){
                    ((Activity)ctx).finish();
                }
                return;
            }
            intent.putExtra("name",SpUtils.getUname());
            intent.putExtra("code", joinCode);
            if (!TextUtils.isEmpty(SpUtils.getAvatar())) {
                intent.putExtra("avatar", SpUtils.getAvatar());
            }
         }else{
           //  LiveSDKWithUI.enterRoom(ctx, Long.parseLong(roomId), sign, UserInfoUtil.getLiveUserInfo().getUserModel(), liveSDKEnterRoomListener);*/
            long bjRoomId=StringUtils.parseLong(roomId);
            if ((bjRoomId <= 0)||TextUtils.isEmpty(sign)) {
                if (ctx instanceof Activity){
                    ((Activity)ctx).finish();
                }
                return;
            }
            intent.putExtra("roomId", bjRoomId);
            intent.putExtra("sign", sign);
            intent.putExtra("name",UserInfoUtil.getLiveUserInfo().userNick);
            intent.putExtra("avatar",UserInfoUtil.getLiveUserInfo().userAvatar);
            intent.putExtra("userNumber", UserInfoUtil.getLiveUserInfo().userNumber);
         }

        Bundle args=new Bundle();
        args.putString("course_id", classId);
        args.putString("lesson_id", lessionId);
        args.putString("parent_id",parentId);

        intent.putExtra("courseInfo",args);
        try {
            ctx.startActivity(intent);
        }catch (Exception e){

        }

          /*  LiveSDKWithUI.enterRoom(ctx, Long.parseLong(roomId), sign, UserInfoUtil.getLiveUserInfo().getUserModel(), liveSDKEnterRoomListener);*/
      /*  LiveSDKWithUI.LPRoomClassEndListener listener = new LiveSDKWithUI.LPRoomClassEndListener() {

            @Override
            public void onClassEnd(Context context, LiveSDKWithUI.LPRoomExitCallback callback) {
                if(shouldJudege)
                    CourseJudgeActivity.newInstance(((Activity)context),classId, roomId, parentId);//courseId,
            }
        };
        LiveSDKWithUI.setRoomClassEndListener(listener);*/
        final long startTime=  System.currentTimeMillis();
        ServiceExProvider.visit(subscription, CourseApiService.getApi().checkLessionEvalute(classId, lessionId),
                new NetObjResponse<CourseApiService.BooleanResult>() {

                    @Override
                    public void onError(String message, int type) {
                    }

                    @Override
                    public void onSuccess(BaseResponseModel<CourseApiService.BooleanResult> model) {
                        if(!model.data.result){
                           long offtime=System.currentTimeMillis()-startTime;
                            if(offtime>=600)
                               EventBus.getDefault().post(new LPDataModel());
                            else {
                                UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        EventBus.getDefault().post(new LPDataModel());
                                    }
                                },600-offtime);
                            }
                        }
                       // shouldJudege = !model.data.result;
                    }
                });
    }
}
