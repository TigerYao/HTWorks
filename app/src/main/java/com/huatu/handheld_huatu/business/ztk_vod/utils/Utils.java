package com.huatu.handheld_huatu.business.ztk_vod.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by djd on 2016/1/18.
 */
public class Utils {

    public static final String SITE_ID_ERROR = "CCEROR";
    public static final String VIDEO_ID_ERROR = "-11111";

  /*  private static String mName = "ht_wangxiao";
    public static final String KEFU_BEFOR = "kf_9846_1456968676369";//售前
    public static final String KEFU_AFTER = "kf_9846_1456972068951";//售后*/
    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();








    /**
     * 判断字符串是否为空
     */
    public static boolean isEmptyOrNull(String returnData) {
        if (returnData == null || "".equals(returnData.trim())) {
            return true;
        }
        return false;
    }



    //判断今天方法
    public static boolean IsToday(String day) throws ParseException, java.text.ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

    public static void copySoFile(final Context context) {
        //是否copy
        if ((Build.VERSION.SDK_INT >= 21
                && Build.SUPPORTED_ABIS != null
                && Build.SUPPORTED_ABIS[0].contains("x86")) ||
                (Build.CPU_ABI != null && Build.CPU_ABI.contains("x86"))) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    //复制so文件
                    boolean needCopy = false;
                    File file = context.getFilesDir();
                    String path = file.toString() + "/x86/";

                    try {
                        String[] fileNames = context.getAssets().list("x86");
                        for (int i = 0; i < fileNames.length; i++) {
                            if (!FileUtil.isFileExist(path + fileNames[i])) {
                                needCopy = true;
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (needCopy)
                        copyFile2Assets(context, "x86", path);
                }
            }.start();
        }
    }

    private static void copyFile2Assets(Context context, String categtary, String path) {
        try {

            // 获取assets目录下的所有文件及目录名
            String fileNames[] = context.getAssets().list(categtary);

            // 如果是目录名，则将重复调用方法递归地将所有文件
            if (fileNames.length > 0) {
                File file = new File(path);
                file.mkdirs();
                for (String fileName : fileNames) {
                    copyFile2Assets(context, categtary + "/" + fileName, path + "/" + fileName);
                }
            }
            // 如果是文件，则循环从输入流读取字节写入
            else {
                InputStream is = context.getAssets().open(categtary);
                FileOutputStream fos = new FileOutputStream(new File(path));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
