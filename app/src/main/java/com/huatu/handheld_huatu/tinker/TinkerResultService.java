package com.huatu.handheld_huatu.tinker;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.TinkerServiceInternals;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

/**
 * Created by saiyuan on 16-10-10.
 */
public class TinkerResultService extends DefaultTinkerResultService {
    private static final String TAG = "changxin";


    @Override
    public void onPatchResult(final PatchResult result) {
        if (result == null) {
            TinkerLog.e(TAG, "SampleResultService received null result!!!!");
            return;
        }
        TinkerLog.i(TAG, "SampleResultService receive result: %s", result.toString());

        boolean isLoaded = false;
        if (result.isSuccess) {
            File dic = SharePatchFileUtil.getPatchDirectory(UniApplicationContext.getContext());
            if(dic != null && dic.exists()) {
                File infoFile = SharePatchFileUtil.getPatchInfoFile(dic.getAbsolutePath());
                if(infoFile != null && infoFile.exists()) {
                    isLoaded = true;
                }
            }
            TinkerLog.i(TAG, "SampleResultService: Tinker.isTinkerLoaded: " + isLoaded);
        }

        //first, we want to kill the recover process
        TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());
        final boolean isLoadSucc = isLoaded;
        if (isLoadSucc) {
            SpUtils.setTinkerInstalledPatchMd5(SpUtils.getTinkerMd5());
            MobclickAgent.onEvent(UniApplicationContext.getContext(),
                    ArenaConstant.TINKER_INSTALL_SUCCESS);
            LogUtils.i("Tinker Install Success, getTinkerMd5:" + SpUtils.getTinkerMd5()
                    + ", setTinkerInstalledPathMd5:" + SpUtils.getTinkerInstalledPatchMd5());
        } else {
//            SpUtils.clearTinkerInstalledPatchMd5();
            LogUtils.i("Tinker Install failed, getTinkerMd5:" + SpUtils.getTinkerMd5()
                    + ", setTinkerInstalledPathMd5:" + SpUtils.getTinkerInstalledPatchMd5());
        }

//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isLoadSucc) {
//                    Toast.makeText(getApplicationContext(), "补丁加载成功，请锁屏或杀掉应用重启", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "补丁加载失败，检查原因", Toast.LENGTH_LONG).show();
//                }
//            }
//        }, 2000);
        // is success and newPatch, it is nice to delete the raw file, and restart at once
        // for old patch, you can't delete the patch file
        if (isLoadSucc) {
            deleteRawPatchFile(new File(result.rawPatchFilePath));

            //not like TinkerResultService, I want to restart just when I am at background!
            //if you have not install tinker this moment, you can use TinkerApplicationHelper api
            if (checkIfNeedKill(result)) {
                if (UniApplicationContext.isAppInBackground) {
                    TinkerLog.i(TAG, "it is in background, just restart process");
                    AppUtils.killProgress();
                } else {
                    //we can wait process at background, such as onAppBackground
                    //or we can restart when the screen off
                    TinkerLog.i(TAG, "tinker wait screen to restart process");
                    new TinkerMethods.ScreenState(getApplicationContext(), new TinkerMethods.ScreenState.IOnScreenOff() {
                        @Override
                        public void onScreenOff() {
                            AppUtils.killProgress();
                        }
                    });
                }
            } else {
                TinkerLog.i(TAG, "I have already install the newly patch version!");
            }
        }

        //repair current patch fail, just clean!
        if (!result.isSuccess && !result.isSuccess) {
            //if you have not install tinker this moment, you can use TinkerApplicationHelper api
            Tinker.with(getApplicationContext()).cleanPatch();
        }
    }
}
