package com.huatu.handheld_huatu.business.essay.bhelper;

import com.huatu.handheld_huatu.mvpmodel.essay.DownloadEssayBean;
import com.huatu.handheld_huatu.utils.FileUtil;

/**
 * 下载帮助类，开始下载，下载进度回调，单个顺序下载
 */
public class DownLoadEssayHelper {

    DownLoadUpdate mDownLoadUpdate;
    DlEssayDataCache varCache = DlEssayDataCache.getInstance();

    public interface DownLoadUpdate {
        void updateView(int progress);
    }

    public void setmDownLoadUpdate(DownLoadUpdate mDownLoadUpdate) {
        this.mDownLoadUpdate = mDownLoadUpdate;
    }

    public static volatile DownLoadEssayHelper instance = null;

    public static DownLoadEssayHelper getInstance() {
        synchronized (DownLoadEssayHelper.class) {
            if (instance == null) {
                instance = new DownLoadEssayHelper();
            }
        }
        return instance;
    }

    private DownLoadEssayHelper() {
    }

    public void startDowningEssay(DownloadEssayBean var) {
        varCache.fromNetToWait(var);
        updateView(-1);
        if (varCache.canDownload()) {
            DownLoadEssayNet.getInstance().startDowningEssay();
        }
    }

    public void againDowningFailEssay(DownloadEssayBean var) {
        varCache.fromFailToWait(var);
        updateView(-1);
        if (varCache.canDownload()) {
            DownLoadEssayNet.getInstance().startDowningEssay();
        }
    }

    public void deleteDowningSuccessEssay(DownloadEssayBean item) {
        varCache.removeSuccessBean(item);
        varCache.deletFilePath(item.isSingle, item.isStartToCheckDetail, item.downLoadId);
        updateView(-1);
    }

    public void nextDowningEssay() {
        if (varCache.canDownload()) {
            DownLoadEssayNet.getInstance().startDowningEssay();
        }
    }

    public void updateView(int progress) {
        if (progress < 0) {
            DlEssayDataCache.getInstance().writeToFileCache();
        }
        if (mDownLoadUpdate != null) {
            mDownLoadUpdate.updateView(progress);
        }
    }

    public String getDownloadFilePath(DownloadEssayBean var) {
        if (var != null && var.downloadUrl != null) {
            String substring = var.downloadUrl.substring(var.downloadUrl.lastIndexOf("/") + 1);
            String newName = var.title + "_" + var.check + "_" + substring;
            final String download_essay = FileUtil.getFilePath("download_essay", newName);
            return download_essay;
        }
        return "";
    }

    public void clearData() {
        mDownLoadUpdate = null;
    }
}
