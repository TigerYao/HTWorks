package com.huatu.handheld_huatu.business.essay.bhelper;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.listener.OnFileDownloadListener;
import com.huatu.handheld_huatu.mvpmodel.essay.DownloadEssayBean;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DownloadBaseInfo;
import com.huatu.handheld_huatu.utils.DownloadManager;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;

/**
 * 真正的下载工具
 */
public class DownLoadEssayNet {

    DownloadBaseInfo info;
    DlEssayDataCache varCache = DlEssayDataCache.getInstance();
    public static volatile DownLoadEssayNet instance = null;

    public static DownLoadEssayNet getInstance() {
        synchronized (DownLoadEssayNet.class) {
            if (instance == null) {
                instance = new DownLoadEssayNet();
            }
        }
        return instance;
    }

    private DownLoadEssayNet() {
    }

    public void startDowningEssay() {
        if (varCache != null) {
            if (varCache.getmDownloadingEssayBean() == null) {
                LogUtils.e("DownLoadEssayNet", "varCache.getmDownloadingEssayBean()");
                return;
            }
            info = new DownloadBaseInfo(varCache.getmDownloadingEssayBean().downloadUrl);
            addDownloadTask(info, "filepath", new OnFileDownloadListener() {
                @Override
                public void onStart(DownloadBaseInfo baseInfo) {
                    TimeUtils.delayTask(new Runnable() {
                        //            @Override
                        public void run() {
                            isDowningStatus(DownloadEssayBean.DOWNLOAD_ING, 0);
                        }
                    }, 1);
                }

                @Override
                public void onProgress(DownloadBaseInfo baseInfo, final int percent, final int byteCount, final int totalCount) {
                    TimeUtils.delayTask(new Runnable() {
                        //            @Override
                        public void run() {
                            isDowningStatus(DownloadEssayBean.DOWNLOAD_ING, percent);
                        }
                    }, 1);
                }

                @Override
                public void onCancel(DownloadBaseInfo baseInfo) {
                    TimeUtils.delayTask(new Runnable() {
                        //            @Override
                        public void run() {
                            isDowningStatus(DownloadEssayBean.DOWNLOAD_FAIL, 0);
                        }
                    }, 1);
                }

                @Override
                public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                    TimeUtils.delayTask(new Runnable() {
                        //            @Override
                        public void run() {
                            isDowningStatus(DownloadEssayBean.DOWNLOAD_SUCCESS, 0);
                        }
                    }, 1);
                }

                @Override
                public void onFailed(DownloadBaseInfo baseInfo) {
                    TimeUtils.delayTask(new Runnable() {
                        //            @Override
                        public void run() {
                            isDowningStatus(DownloadEssayBean.DOWNLOAD_FAIL, 0);
                        }
                    }, 1);

                }
            });
        } else {

            LogUtils.e("DownLoadEssayNet", "varCache.getmDownloadingEssayBean()");
        }

    }

    private void addDownloadTask(DownloadBaseInfo info, String filepath, final OnFileDownloadListener onFileDownloadListener) {
        readDownloadEssay(info, onFileDownloadListener);
    }

    private void readDownloadEssay(DownloadBaseInfo info, final OnFileDownloadListener onFileDownloadListener) {
        if (varCache.getmDownloadingEssayBean() == null) {
            LogUtils.e("DownLoadEssayNet", "varCache.getmDownloadingEssayBean()");
            return;
        }
        String substring = info.downloadUrl.substring(info.downloadUrl.lastIndexOf("/") + 1);
        String newName = varCache.getmDownloadingEssayBean().title + "_" + varCache.getmDownloadingEssayBean().check + "_" + substring;
        final String download_essay = FileUtil.getFilePath("download_essay", newName);
        LogUtils.d("DownLoadEssayNet", "download_essay  " + download_essay);
        if (FileUtil.isFileExist(download_essay) && FileUtil.getFileSizeInt(download_essay) > 0) {
            varCache.getmDownloadingEssayBean().filepath = download_essay;
            onFileDownloadListener.onSuccess(null, null);
        } else {
            DownloadManager.getInstance().addDownloadTask(info, download_essay, new OnFileDownloadListener() {
                @Override
                public void onStart(DownloadBaseInfo baseInfo) {

                }

                @Override
                public void onProgress(DownloadBaseInfo baseInfo, final int percent, final int byteCount, final int totalCount) {
                    onFileDownloadListener.onProgress(baseInfo, percent, byteCount, totalCount);
                }

                @Override
                public void onCancel(DownloadBaseInfo baseInfo) {
                    CommonUtils.showToast(R.string.download_error);
                    onFileDownloadListener.onFailed(null);
                }

                @Override
                public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                    varCache.getmDownloadingEssayBean().filepath = download_essay;
                    onFileDownloadListener.onSuccess(null, null);
                }

                @Override
                public void onFailed(DownloadBaseInfo baseInfo) {
                    CommonUtils.showToast(R.string.download_error);
                    onFileDownloadListener.onFailed(null);
                }
            }, false, true);
        }
    }

    public void isDowningStatus(int staus, int progress) {
        LogUtils.d("DownLoadEssayNet", "staus: " + staus);
        LogUtils.d("DownLoadEssayNet", "progress: " + progress);
        if (staus == DownloadEssayBean.DOWNLOAD_ING) {
            DlEssayDataCache.getInstance().isDowningStatus(DownloadEssayBean.DOWNLOAD_ING, progress);
            DownLoadEssayHelper.getInstance().updateView(progress);
        } else if (staus == DownloadEssayBean.DOWNLOAD_SUCCESS) {
            DlEssayDataCache.getInstance().isDowningStatus(DownloadEssayBean.DOWNLOAD_SUCCESS, 0);
            DownLoadEssayHelper.getInstance().nextDowningEssay();
            DownLoadEssayHelper.getInstance().updateView(-1);
            EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_net_download_essay_success));
        } else if (staus == DownloadEssayBean.DOWNLOAD_FAIL) {
            DlEssayDataCache.getInstance().isDowningStatus(DownloadEssayBean.DOWNLOAD_FAIL, 0);
            DownLoadEssayHelper.getInstance().nextDowningEssay();
            DownLoadEssayHelper.getInstance().updateView(-2);
        }
    }
}
