package com.huatu.handheld_huatu.tinker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.StorageUtils;
import com.tencent.tinker.lib.library.TinkerLoadLibrary;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import okhttp3.Call;

/**
 * Created by saiyuan on 16-10-10.
 */
public class TinkerMethods {

    /**
     * the error code define by myself
     * should after {@code ShareConstants.ERROR_PATCH_INSERVICE
     */
    public static final int ERROR_PATCH_GOOGLEPLAY_CHANNEL      = -6;
    public static final int ERROR_PATCH_ROM_SPACE               = -7;
    public static final int ERROR_PATCH_MEMORY_LIMIT            = -8;
    public static final int ERROR_PATCH_CRASH_LIMIT             = -9;
    public static final int ERROR_PATCH_CONDITION_NOT_SATISFIED = -10;
    public static final int ERROR_PATCH_ALREADY_APPLY           = -11;
    public static final int ERROR_PATCH_RETRY_COUNT_LIMIT       = -12;

    public static final String PLATFORM = "platform";

    public static final int MIN_MEMORY_HEAP_SIZE = 45;

    public static boolean isGooglePlay() {
        return false;
    }

    public static int checkForPatchRecover(long roomSize, int maxMemory) {
        if (isGooglePlay()) {
            return ERROR_PATCH_GOOGLEPLAY_CHANNEL;
        }
        if (maxMemory < MIN_MEMORY_HEAP_SIZE) {
            return ERROR_PATCH_MEMORY_LIMIT;
        }
        //or you can mention user to clean their rom space!
        if (!checkRomSpaceEnough(roomSize)) {
            return ERROR_PATCH_ROM_SPACE;
        }

        return ShareConstants.ERROR_PATCH_OK;
    }

    public static boolean isXposedExists(Throwable thr) {
        StackTraceElement[] stackTraces = thr.getStackTrace();
        for (StackTraceElement stackTrace : stackTraces) {
            final String clazzName = stackTrace.getClassName();
            if (clazzName != null && clazzName.contains("de.robv.android.xposed.XposedBridge")) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public static boolean checkRomSpaceEnough(long limitSize) {
        long allSize;
        long availableSize = 0;
        try {
            File data = Environment.getDataDirectory();
            StatFs sf = new StatFs(data.getPath());
            availableSize = (long) sf.getAvailableBlocks() * (long) sf.getBlockSize();
            allSize = (long) sf.getBlockCount() * (long) sf.getBlockSize();
        } catch (Exception e) {
            allSize = 0;
        }

        if (allSize != 0 && availableSize > limitSize) {
            return true;
        }
        return false;
    }

    public static String getExceptionCauseString(final Throwable ex) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(bos);

        try {
            // print directly
            Throwable t = ex;
            while (t.getCause() != null) {
                t = t.getCause();
            }
            t.printStackTrace(ps);
            return toVisualString(bos.toString());
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String toVisualString(String src) {
        boolean cutFlg = false;

        if (null == src) {
            return null;
        }

        char[] chr = src.toCharArray();
        if (null == chr) {
            return null;
        }

        int i = 0;
        for (; i < chr.length; i++) {
            if (chr[i] > 127) {
                chr[i] = 0;
                cutFlg = true;
                break;
            }
        }

        if (cutFlg) {
            return new String(chr, 0, i);
        } else {
            return src;
        }
    }

    private static String getPrefixName(String md5){
        if(TextUtils.isEmpty(md5)) return "prefix";
        else {
            if(md5.length()>8) return md5.substring(0,8);
            return "prefix";
        }
    }

    public static void loadPatch() {
        String url = SpUtils.getTinkerUrl();
        String md5 = SpUtils.getTinkerMd5();
        if(TextUtils.isEmpty(url) || TextUtils.isEmpty(md5)
                || md5.equals(SpUtils.getTinkerInstalledPatchMd5()) || !NetUtil.isConnected()) {
            return;
        }

        String patchFileName=String.format("netschool%s.patch",getPrefixName(md5)+String.valueOf(AppUtils.getVersionCode()));

        final String patchFilePath = StorageUtils.getHuatuTempDirectory(UniApplicationContext.getContext()).getAbsolutePath() + File.separator + patchFileName;
        File oldFile = new File(patchFilePath);
        if (oldFile != null && oldFile.exists()) {

            String fileMd5 = Md5Util.getFileMD5String(oldFile);
            if (!TextUtils.isEmpty(md5) && md5.equals(fileMd5)) {
                TinkerInstaller.onReceiveUpgradePatch(UniApplicationContext.getContext(), patchFilePath);
                return;
            }
        }

        MobclickAgent.onEvent(UniApplicationContext.getContext(), ArenaConstant.TINKER_DOWNLOAD_START,String.valueOf(AppUtils.getVersionCode()));
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(StorageUtils.getHuatuTempDirectory(UniApplicationContext.getContext()).getAbsolutePath(), patchFileName) {
                    @Override
                    public void inProgress(float progress, long total) {

                        if (progress == 1) {
                            LogUtils.i("TinkerMethods.loadPatch start");
                            UniApplicationLike.getApplicationHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    MobclickAgent.onEvent(UniApplicationContext.getContext(),
                                            ArenaConstant.TINKER_DOWNLOAD_SUCCESS,String.valueOf(AppUtils.getVersionCode()));
                                    MobclickAgent.onEvent(UniApplicationContext.getContext(),
                                            ArenaConstant.TINKER_INSTALL_START,String.valueOf(AppUtils.getVersionCode()));

                                    TinkerInstaller.onReceiveUpgradePatch(UniApplicationContext.getContext(), patchFilePath);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Call request, Exception e) {
                        MobclickAgent.onEvent(UniApplicationContext.getContext(),
                                ArenaConstant.TINKER_DOWNLOAD_FAIL,String.valueOf(AppUtils.getVersionCode()));
                    }

                    @Override
                    public void onResponse(File file) {
                        LogUtils.e("onResponse :", file.getAbsolutePath());
                    }
                });

     /*   boolean isDownload = DownloadManager.getInstance().addDownloadTask(info, filePath, new OnFileDownloadListener() {
            @Override
            public void onStart(DownloadBaseInfo baseInfo) {
                MobclickAgent.onEvent(UniApplicationContext.getContext(),
                        ArenaConstant.TINKER_DOWNLOAD_START);
            }

            @Override
            public void onProgress(DownloadBaseInfo baseInfo, int percent, int byteCount, int total) {

            }

            @Override
            public void onCancel(DownloadBaseInfo baseInfo) {

            }

            @Override
            public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                LogUtils.i("TinkerMethods.loadPatch start");
                UniApplicationLike.getApplicationHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        MobclickAgent.onEvent(UniApplicationContext.getContext(),
                                ArenaConstant.TINKER_DOWNLOAD_SUCCESS);
                        MobclickAgent.onEvent(UniApplicationContext.getContext(),
                                ArenaConstant.TINKER_INSTALL_START);
                        TinkerInstaller.onReceiveUpgradePatch(UniApplicationContext.getContext(), filePath);
                    }
                });
            }

            @Override
            public void onFailed(DownloadBaseInfo baseInfo) {
                MobclickAgent.onEvent(UniApplicationContext.getContext(),
                        ArenaConstant.TINKER_DOWNLOAD_FAIL);
            }
        }, true, false);*/
    }

    public static void loadLibrary() {
        // #method 1, hack classloader library path
        TinkerLoadLibrary.installNavitveLibraryABI(UniApplicationContext.getContext(), "armeabi");
        System.loadLibrary("stlport_shared");

        // #method 2, for lib/armeabi, just use TinkerInstaller.loadLibrary
//                TinkerLoadLibrary.loadArmLibrary(getApplicationContext(), "stlport_shared");

        // #method 3, load tinker patch library directly
//                TinkerInstaller.loadLibraryFromTinker(getApplicationContext(), "assets/x86", "stlport_shared");
    }

    public static void cleanPatch() {
        Tinker.with(UniApplicationContext.getContext()).cleanPatch();
    }

    public static void killSelf() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static class ScreenState {
        public interface IOnScreenOff {
            void onScreenOff();
        }

        public ScreenState(final Context context, final IOnScreenOff onScreenOffInterface) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);

            context.registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent in) {
                    String action = in == null ? "" : in.getAction();
                    TinkerLog.i("changxin", "ScreenReceiver action [%s] ", action);
                    if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                        if (onScreenOffInterface != null) {
                            onScreenOffInterface.onScreenOff();
                        }
                    }
                    context.unregisterReceiver(this);
                }
            }, filter);
        }
    }
}
