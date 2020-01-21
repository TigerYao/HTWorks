package com.huatu.handheld_huatu.tinker;

import android.content.Context;
import android.os.Looper;
import android.os.MessageQueue;

import com.huatu.handheld_huatu.utils.LogUtils;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.util.UpgradePatchRetry;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import java.io.File;

/**
 * Created by saiyuan on 16-10-10.
 */
public class TinkerLoadReporter extends DefaultLoadReporter {

    public TinkerLoadReporter(Context context) {
        super(context);
    }

    @Override
    public void onLoadPatchListenerReceiveFail(final File patchFile, int errorCode) {
        super.onLoadPatchListenerReceiveFail(patchFile, errorCode);
        switch (errorCode) {
            case ShareConstants.ERROR_PATCH_NOTEXIST:
                LogUtils.i("ShareConstants.ERROR_PATCH_NOTEXIST: patch file is not exist");
                break;
            case ShareConstants.ERROR_PATCH_RUNNING:
                LogUtils.i("ShareConstants.ERROR_PATCH_RUNNING: only retry for upgrade patch");
                break;
            case TinkerMethods.ERROR_PATCH_ROM_SPACE:
                LogUtils.i("TinkerMethods.ERROR_PATCH_ROM_SPACE: rom space is not enough");
                break;
        }
        TinkerReport.onTryApplyFail(errorCode);
    }

    @Override
    public void onLoadResult(File patchDirectory, int loadCode, long cost) {
        super.onLoadResult(patchDirectory, loadCode, cost);
        switch (loadCode) {
            case ShareConstants.ERROR_LOAD_OK:
                TinkerReport.onLoaded(cost);
                break;
        }
        Looper.getMainLooper().myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override public boolean queueIdle() {
                if (UpgradePatchRetry.getInstance(context).onPatchRetryLoad()) {
                    TinkerReport.onReportRetryPatch();
                }
                return false;
            }
        });
    }
    @Override
    public void onLoadException(Throwable e, int errorCode) {
        super.onLoadException(e, errorCode);
        TinkerReport.onLoadException(e, errorCode);
    }

    @Override
    public void onLoadFileMd5Mismatch(File file, int fileType) {
        super.onLoadFileMd5Mismatch(file, fileType);
        TinkerReport.onLoadFileMisMatch(fileType);
    }

    /**
     * try to recover patch oat file
     *
     * @param file
     * @param fileType
     * @param isDirectory
     */
    @Override
    public void onLoadFileNotFound(File file, int fileType, boolean isDirectory) {
        super.onLoadFileNotFound(file, fileType, isDirectory);
        TinkerReport.onLoadFileNotFound(fileType);
    }

    @Override
    public void onLoadPackageCheckFail(File patchFile, int errorCode) {
        super.onLoadPackageCheckFail(patchFile, errorCode);
        TinkerReport.onLoadPackageCheckFail(errorCode);
    }

    @Override
    public void onLoadPatchInfoCorrupted(String oldVersion, String newVersion, File patchInfoFile) {
        super.onLoadPatchInfoCorrupted(oldVersion, newVersion, patchInfoFile);
        TinkerReport.onLoadInfoCorrupted();
    }

    @Override
    public void onLoadInterpret(int type, Throwable e) {
        super.onLoadInterpret(type, e);
        TinkerReport.onLoadInterpretReport(type, e);
    }

    @Override
    public void onLoadPatchVersionChanged(String oldVersion, String newVersion, File patchDirectoryFile, String currentPatchName) {
        super.onLoadPatchVersionChanged(oldVersion, newVersion, patchDirectoryFile, currentPatchName);
    }

}
