package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.content.Context;

import com.baijiahulian.common.cache.sp.SharePreferenceUtil;
import com.baijiahulian.downloader.download.DownloadInfo;
import com.baijiahulian.downloader.download.db.DownloadInfoDao;
import com.baijiahulian.player.utils.b;
import com.baijiayun.download.DownloadModel;
import com.baijiayun.download.IRecoveryCallback;
import com.baijiayun.download.a;
import com.baijiayun.download.constant.FileType;
import com.baijiayun.download.constant.TaskStatus;
import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018\6\22 0022.
 */




public class RecoverDbHelperEx {
   /* private String recoveryPath;
    private static RecoverDbHelperEx instance;
    private SharePreferenceUtil sharePreferenceUtil;
    private static final String IS_RECOVERED = "isRecovered";
    private IRecoveryCallback recoveryCallback;

    private RecoverDbHelperEx() {
    }

    public static RecoverDbHelperEx getInstance() {
        if(instance == null) {
            Class var0 = RecoverDbHelperEx.class;
            synchronized(RecoverDbHelperEx.class) {
                if(instance == null) {
                    instance = new RecoverDbHelperEx();
                }
            }
        }

        return instance;
    }

    public void init(Context var1, String var2, IRecoveryCallback var3) {
        this.sharePreferenceUtil = new SharePreferenceUtil(var1, "download_recover_file");
        this.recoveryPath = var2;
        this.recoveryCallback = var3;
    }

    public String getRecoveryPath() {
        return this.recoveryPath;
    }

    public void setRecoveryPath(String var1) {
        this.recoveryPath = var1;
    }

    public void resetRecoveryMark() {
        this.sharePreferenceUtil.putBoolean("isRecovered", false);
    }

    public boolean getRecoveryMark() {
        return this.sharePreferenceUtil.getBooleanValue("isRecovered", false);
    }

    public void setRecoveryCallback(IRecoveryCallback var1) {
        this.recoveryCallback = var1;
    }

    public IRecoveryCallback getRecoveryCallback() {
        return this.recoveryCallback;
    }

    public void recoveryDbData() {
        if(!this.sharePreferenceUtil.getBooleanValue("isRecovered", false)) {
            final IRecoveryCallback var1 = getInstance().getRecoveryCallback();
            if(var1 != null) {
                b.a(new com.baijiahulian.player.utils.b.b() {
                    public void A() {
                        doRecoveryData();
                    }

                    public void b() {
                        var1.recoverySuccess();
                    }
                });
            }

        }
    }

    private void doRecoveryData() {
        List var1 = a.Q().getModelList("default", new TypeToken<List<DownloadModel>>() {
        });
        DownloadInfoDao var2 = new DownloadInfoDao();
        ArrayList var3 = new ArrayList();
        Iterator var4 = var2.getAll().iterator();

        while(var4.hasNext()) {
            DownloadInfo var5 = (DownloadInfo)var4.next();
            DownloadModel var6 = this.convertToDownloadModel(var5);
            if(var6 != null && !var6.targetName.startsWith("s_")) {
                DownloadInfo var7 = var2.get("s_" + var5.getVideoId());
                if(var7 != null) {
                    DownloadModel var8 = this.convertToDownloadModel(var7);
                    if(var8 != null && var8.totalLength == 0L) {
                        var8.totalLength = var6.totalLength;
                    }

                    var6.nextModel = var8;
                }

                if(!var1.contains(var6)) {
                    var3.add(var6);
                }
            }
        }

        var1.addAll(var3);
        a.Q().putModelList("default", var1);
        this.sharePreferenceUtil.putBoolean("isRecovered", true);
    }

    private DownloadModel convertToDownloadModel(DownloadInfo var1) {
        if(!var1.getTargetFolder().equals(this.recoveryPath)) {
            return null;
        } else {
            DownloadModel var2 = new DownloadModel();
            var2.targetName = var1.getFileName();
            if(var2.targetName.startsWith("s_")) {
                var2.fileType = FileType.Signal;
            } else {
                var2.fileType = FileType.Video;
            }

            var2.targetFolder = var1.getTargetFolder();
            var2.url = var1.getUrl();
            var2.downloadLength = var1.getDownloadLength();
            var2.totalLength = var1.getTotalLength();
            switch(var1.getState()) {
                case 1:
                case 3:
                    var2.status = TaskStatus.Pause;
                    break;
                case 2:
                    var2.status = TaskStatus.Downloading;
                    break;
                case 4:
                    var2.status = TaskStatus.Finish;
                    break;
                case 5:
                    var2.status = TaskStatus.Error;
                    break;
                default:
                    var2.status = TaskStatus.New;
            }

            var2.data = var1.getData();
            var2.encryptType = var1.getEncryptType();
            var2.extraInfo = String.valueOf(UserInfoUtil.userId)+String.valueOf(var1.getVideoId());// var1.getExtraInfo();
            var2.videoId = (long)var1.getVideoId();
            var2.speed = var1.getNetworkSpeed();
            return var2;
        }
    }*/
}
