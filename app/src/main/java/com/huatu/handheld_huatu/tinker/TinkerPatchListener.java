package com.huatu.handheld_huatu.tinker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.UpgradePatchRetry;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import java.io.File;
import java.util.Properties;

/**
 * Created by saiyuan on 16-10-10.
 */
public class TinkerPatchListener extends DefaultPatchListener {
    private static final String TAG = "changxin";

    protected static final long NEW_PATCH_RESTRICTION_SPACE_SIZE_MIN = 60 * 1024 * 1024;
    protected static final long OLD_PATCH_RESTRICTION_SPACE_SIZE_MIN = 30 * 1024 * 1024;

    private final int maxMemory;

    public TinkerPatchListener(Context context) {
        super(context);
        maxMemory = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        TinkerLog.i(TAG, "application maxMemory:" + maxMemory);
    }

    /**
     * because we use the defaultCheckPatchReceived method
     * the error code define by myself should after {@code ShareConstants.ERROR_RECOVER_INSERVICE
     *
     * @param path
     * @param newPatch
     * @return
     */
    @Override
    public int patchCheck(String path, String patchMd5) {
        File patchFile = new File(path);
        TinkerLog.i(TAG, "receive a patch file: %s, file size:%d", path, SharePatchFileUtil.getFileOrDirectorySize(patchFile));
        int returnCode = super.patchCheck(path,patchMd5);

        if (returnCode == ShareConstants.ERROR_PATCH_OK) {
            returnCode = TinkerMethods.checkForPatchRecover(NEW_PATCH_RESTRICTION_SPACE_SIZE_MIN, maxMemory);
        }

        if (returnCode == ShareConstants.ERROR_PATCH_OK) {
           // String patchMd5 = SharePatchFileUtil.getMD5(patchFile);
            SharedPreferences sp = context.getSharedPreferences(ShareConstants.TINKER_SHARE_PREFERENCE_CONFIG, Context.MODE_MULTI_PROCESS);
            //optional, only disable this patch file with md5
            int fastCrashCount = sp.getInt(patchMd5, 0);
            if (fastCrashCount >= TinkerUncaughtExceptionHandler.MAX_CRASH_COUNT) {
                returnCode = TinkerMethods.ERROR_PATCH_CRASH_LIMIT;
            } /*else {1.9.11 基类中已有
                //for upgrade patch, version must be not the same
                //for repair patch, we won't has the tinker load flag
                Tinker tinker = Tinker.with(context);

                if (tinker.isTinkerLoaded()) {
                    TinkerLoadResult tinkerLoadResult = tinker.getTinkerLoadResultIfPresent();
                    if (tinkerLoadResult != null && !tinkerLoadResult.useInterpretMode) {
                        String currentVersion = tinkerLoadResult.currentVersion;
                        if (patchMd5.equals(currentVersion)) {
                            returnCode = TinkerMethods.ERROR_PATCH_ALREADY_APPLY;
                        }
                    }
                }
            }
            //check whether retry so many times
            if (returnCode == ShareConstants.ERROR_PATCH_OK) {
                returnCode = UpgradePatchRetry.getInstance(context).onPatchListenerCheck(patchMd5)
                        ? ShareConstants.ERROR_PATCH_OK : TinkerMethods.ERROR_PATCH_RETRY_COUNT_LIMIT;
            }*/
        }
        // Warning, it is just a sample case, you don't need to copy all of these
        // Interception some of the request
        if (returnCode == ShareConstants.ERROR_PATCH_OK) {
            Properties properties = ShareTinkerInternals.fastGetPatchPackageMeta(patchFile);
            if (properties == null) {
                returnCode = TinkerMethods.ERROR_PATCH_CONDITION_NOT_SATISFIED;
            } else {
                String platform = properties.getProperty(TinkerMethods.PLATFORM);
                TinkerLog.i(TAG, "get platform:" + platform);
                // check patch platform require
                if (platform == null || !platform.equalsIgnoreCase("all")) {
                    returnCode = TinkerMethods.ERROR_PATCH_CONDITION_NOT_SATISFIED;
                }
            }
        }

        TinkerReport.onTryApply(returnCode == ShareConstants.ERROR_PATCH_OK);
        return returnCode;
    }
}

