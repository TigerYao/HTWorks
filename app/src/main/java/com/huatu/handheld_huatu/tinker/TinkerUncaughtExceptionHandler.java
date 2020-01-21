package com.huatu.handheld_huatu.tinker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.tinker.TinkerApplicationHelper;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * Created by saiyuan on 16-10-10.
 */
public class TinkerUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "Tinker.TinkerUncaughtExceptionHandler";

    private final Thread.UncaughtExceptionHandler ueh;
    private static final long   QUICK_CRASH_ELAPSE  = 10 * 1000;
    public static final  int    MAX_CRASH_COUNT     = 3;
    private static final String DALVIK_XPOSED_CRASH = "Class ref in pre-verified class resolved to unexpected implementation";

    public TinkerUncaughtExceptionHandler() {
        ueh = Thread.getDefaultUncaughtExceptionHandler();
    }



    /**
     * 判断是否缺少权限
     */
/*    private static boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;*/



    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        TinkerLog.e(TAG, "uncaughtException:" + ex.getMessage());
        StringBuilder sb = new StringBuilder(ex.getMessage());
        StackTraceElement[] steArray = ex.getStackTrace();
        if(steArray != null) {
            for(int i = 0; i < steArray.length; i++) {
                sb.append(steArray[i].toString() + "\n");
            }
        }
        String crashInfo=saveCrashInfoToInfo(ex);
        if(crashInfo!=null) {
            sb.append("\n crash: ===================================ht online=========================================== crash");
            sb.append("\n " + crashInfo);
        }

        sb.append("\n platform: android");
        sb.append("\n model:" +  Build.MODEL);
        sb.append("\n os_version:" + Build.VERSION.RELEASE);
        if(UniApplicationContext.getApplication()!=null) {
            TelephonyManager tm = (TelephonyManager) UniApplicationContext.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
            if(tm!=null) {
              //   sb.append("\n deviceid: " + tm.getDeviceId());
                sb.append("\n telephony_mcc+mnc_type: " + tm.getNetworkOperator());
                sb.append("\n telephony_net_type: " + tm.getNetworkType());
                sb.append("\n net_isconnected: " + NetUtil.isConnected());
                sb.append("\n net_connected_iswifi: " + NetUtil.isWifi());
            }
        }
        sb.append("\n uid: " + UserInfoUtil.userId);
        sb.append("\n userName: " + UserInfoUtil.userName);
        sb.append("\n APP_version: " + AppUtils.getVersionName());
        sb.append("\n timestamp: " + System.currentTimeMillis());
        LogUtils.logCrashOnFile(sb.toString());
        tinkerFastCrashProtect();
        tinkerPreVerifiedCrashHandler(ex);
        ueh.uncaughtException(thread, ex);
    }

    private String saveCrashInfoToInfo(Throwable ex) {
        try {
            Writer info = new StringWriter();
            PrintWriter printWriter = new PrintWriter(info);
            ex.printStackTrace(printWriter);

            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }

            String result = info.toString();
            printWriter.close();
            return  result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return  null;
    }

    /**
     * Such as Xposed, if it try to load some class before we load from patch files.
     * With dalvik, it will crash with "Class ref in pre-verified class resolved to unexpected implementation".
     * With art, it may crash at some times. But we can't know the actual crash type.
     * If it use Xposed, we can just clean patch or mention user to uninstall it.
     */
    private void tinkerPreVerifiedCrashHandler(Throwable ex) {
        ApplicationLike applicationLike = TinkerManager.getTinkerApplicationLike();
        if (applicationLike == null || applicationLike.getApplication() == null) {
            LogUtils.w("applicationlike is null");
            return;
        }
        if (!TinkerApplicationHelper.isTinkerLoadSuccess(applicationLike)) {
            LogUtils.w("tinker is not loaded");
            return;
        }
        Throwable throwable = ex;
        boolean isXposed = false;
        while (throwable != null) {
            if (!isXposed) {
                isXposed = TinkerMethods.isXposedExists(throwable);
            }
            // xposed?
            if (isXposed) {
                boolean isCausedByXposed = false;
                //for art, we can't know the actually crash type
                //just ignore art
                if (throwable instanceof IllegalAccessError && throwable.getMessage().contains(DALVIK_XPOSED_CRASH)) {
                    //for dalvik, we know the actual crash type
                    isCausedByXposed = true;
                }

                if (isCausedByXposed) {
                    TinkerReport.onXposedCrash();
                    TinkerLog.e(TAG, "have xposed: just clean tinker");
                    //kill all other process to ensure that all process's code is the same.
                    ShareTinkerInternals.killAllOtherProcess(applicationLike.getApplication());

                    TinkerApplicationHelper.cleanPatch(applicationLike);
                    ShareTinkerInternals.setTinkerDisableWithSharedPreferences(applicationLike.getApplication());
                    return;
                }
            }
            throwable = throwable.getCause();
        }
    }

    /**
     * if tinker is load, and it crash more than MAX_CRASH_COUNT, then we just clean patch.
     */
    private boolean tinkerFastCrashProtect() {
        ApplicationLike applicationLike = TinkerManager.getTinkerApplicationLike();

        if (applicationLike == null || applicationLike.getApplication() == null) {
            LogUtils.w("applicationlike is null");
            return false;
        }
        if (!TinkerApplicationHelper.isTinkerLoadSuccess(applicationLike)) {
            LogUtils.w("tinker is not loaded");
            return false;
        }

        final long elapsedTime = SystemClock.elapsedRealtime() - applicationLike.getApplicationStartElapsedTime();
        //this process may not install tinker, so we use TinkerApplicationHelper api
        if (elapsedTime < QUICK_CRASH_ELAPSE) {
            String currentVersion = TinkerApplicationHelper.getCurrentVersion(applicationLike);
            if (ShareTinkerInternals.isNullOrNil(currentVersion)) {
                return false;
            }

            SharedPreferences sp = applicationLike.getApplication().getSharedPreferences(ShareConstants.TINKER_SHARE_PREFERENCE_CONFIG, Context.MODE_MULTI_PROCESS);
            int fastCrashCount = sp.getInt(currentVersion, 0) + 1;
            if (fastCrashCount >= MAX_CRASH_COUNT) {
                TinkerReport.onFastCrashProtect();
                TinkerApplicationHelper.cleanPatch(applicationLike);
                TinkerLog.e(TAG, "tinker has fast crash more than %d, we just clean patch!", fastCrashCount);
                return true;
            } else {
                sp.edit().putInt(currentVersion, fastCrashCount).commit();
                TinkerLog.e(TAG, "tinker has fast crash %d times", fastCrashCount);
            }
        }

        return false;
    }
}
