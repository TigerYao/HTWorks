/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huatu.handheld_huatu.tinker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.TinkerServiceInternals;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;


/**
 * optional, you can just use DefaultTinkerResultService
 * we can restart process when we are at background or screen off
 * Created by zhangshaowen on 16/4/13.
 */
public class SampleResultService extends DefaultTinkerResultService {
    private static final String TAG = "Tinker.SampleResultService";


    @Override
    public void onPatchResult(final PatchResult result) {
        if (result == null) {
            TinkerLog.e(TAG, "SampleResultService received null result!!!!");
            return;
        }
        TinkerLog.i(TAG, "SampleResultService receive result: %s", result.toString());

        //first, we want to kill the recover process
        TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());


        if(PatchUtils.hasLocalPatchConfig(getApplicationContext())){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (result.isSuccess) {
                        Toast.makeText(getApplicationContext(), "patch success, please restart process", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "patch fail, please check reason", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


        if (result.isSuccess) {
            SpUtils.setTinkerInstalledPatchMd5(SpUtils.getTinkerMd5());
            MobclickAgent.onEvent(UniApplicationContext.getContext(),
                    ArenaConstant.TINKER_INSTALL_SUCCESS,String.valueOf(AppUtils.getVersionCode()));
            LogUtils.i("Tinker Install Success, getTinkerMd5:" + SpUtils.getTinkerMd5()
                    + ", setTinkerInstalledPathMd5:" + SpUtils.getTinkerInstalledPatchMd5());
        } else {
//            SpUtils.clearTinkerInstalledPatchMd5();

            MobclickAgent.onEvent(UniApplicationContext.getContext(),
                    ArenaConstant.TINKER_INSTALL_FAIL,String.valueOf(AppUtils.getVersionCode()));
            LogUtils.i("Tinker Install failed, getTinkerMd5:" + SpUtils.getTinkerMd5()
                    + ", setTinkerInstalledPathMd5:" + SpUtils.getTinkerInstalledPatchMd5());
        }

        // is success and newPatch, it is nice to delete the raw file, and restart at once
        // for old patch, you can't delete the patch file
        if (result.isSuccess) {
            deleteRawPatchFile(new File(result.rawPatchFilePath));

            //not like TinkerResultService, I want to restart just when I am at background!
            //if you have not install tinker this moment, you can use TinkerApplicationHelper api
            if (checkIfNeedKill(result)) {
                if (UniApplicationContext.isAppInBackground) {
                    TinkerLog.i(TAG, "it is in background, just restart process");
                    restartProcess();
                } else {
                    //we can wait process at background, such as onAppBackground
                    //or we can restart when the screen off
                    TinkerLog.i(TAG, "tinker wait screen to restart process");
                   /* new Utils.ScreenState(getApplicationContext(), new Utils.ScreenState.IOnScreenOff() {
                        @Override
                        public void onScreenOff() {
                            restartProcess();
                        }
                    });*/
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
    }

    /**
     * you can restart your process through service or broadcast
     */
    private void restartProcess() {
        TinkerLog.i(TAG, "app is background now, i can kill quietly");
        //you can send service or broadcast intent to restart your process
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
