package com.huatu.handheld_huatu.business.arena.downloaderror;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.downloaderror.bean.ErrExpListBean;
import com.huatu.handheld_huatu.business.arena.downloaderror.inter.IErrorExportManager;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.listener.OnFileDownloadListener;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DownloadBaseInfo;
import com.huatu.handheld_huatu.utils.DownloadManager;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;

public class ErrExportManager implements IErrorExportManager {

    private String FILE_PARENT = "download_err_export";          // 下载文件的父文件夹
    private String SPLIT = "#";

    // 单例模式
    private static volatile ErrExportManager singleton;

    public static ErrExportManager newInstance() {
        if (singleton == null) {
            synchronized (ErrExportManager.class) {
                if (singleton == null) {
                    singleton = new ErrExportManager();
                }
            }
        }
        return singleton;
    }

    private ArrayList<String> downLoadingList;                        // 正在下载中的试卷，防止多次点击下载

    private ErrExportManager() {
        downLoadingList = new ArrayList<>();
    }

    @Override
    public void downloadPaper(CompositeSubscription compositeSubscription, final ErrExpListBean.ErrExpBean expBean, final OnFileDownloadListener onFileDownloadListener) {

        if (compositeSubscription == null) return;

        final String key = expBean.id + "-" + expBean.subject;

        // 如果正在下载就return
        if (downLoadingList.contains(key)) {
            ToastUtil.showToast("文件正在下载中");
            return;
        }

        downLoadingList.add(key);

        ServiceProvider.getErrExpDownloadInfo(compositeSubscription, expBean.id, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                onFileDownloadListener.onCancel(null);
                downLoadingList.remove(expBean.id + "-" + expBean.subject);
                if (e instanceof ApiException) {
                    ToastUtil.showToast(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtil.showToast("获取下载地址失败");
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                ErrExpListBean.ErrExpBean errExpBean = (ErrExpListBean.ErrExpBean) model.data;
                if (errExpBean != null) {
                    if (!StringUtils.isEmpty(errExpBean.fileUrl)) {
                        downLoadFileBean(errExpBean, onFileDownloadListener);
                    } else {
                        onFileDownloadListener.onCancel(null);
                        downLoadingList.remove(expBean.id + "-" + expBean.subject);
                        ToastUtil.showToast("试卷正在导出中，请稍后查看");
                    }
                } else {
                    onFileDownloadListener.onCancel(null);
                    downLoadingList.remove(expBean.id + "-" + expBean.subject);
                    ToastUtil.showToast("获取下载地址失败");
                }
            }
        });
    }

    private void downLoadFileBean(final ErrExpListBean.ErrExpBean errExpBean, final OnFileDownloadListener onFileDownloadListener) {

        // 文件名称，创建文件路径 文件名称意义： 名称-文件大小-下载时间戳-任务id-科目-是否完成（0、否 1、是）
        String fileName = errExpBean.name
                + SPLIT + "0"
                + SPLIT + errExpBean.subject
                + SPLIT + errExpBean.id
                + SPLIT + errExpBean.fileSize
                + SPLIT + errExpBean.gmtCreate
                + ".pdf";
        String filePath = FileUtil.getFilePath(FILE_PARENT, fileName);

        // 添加到下载任务
        DownloadBaseInfo downloadBaseInfo = new DownloadBaseInfo(errExpBean.fileUrl);
        downloadBaseInfo.fileSize = errExpBean.fileSize;
        downloadBaseInfo.filePath = filePath;
        DownloadManager.getInstance().addDownloadTask(downloadBaseInfo, filePath, new OnFileDownloadListener() {
            @Override
            public void onStart(DownloadBaseInfo baseInfo) {
                onFileDownloadListener.onStart(baseInfo);
            }

            @Override
            public void onProgress(DownloadBaseInfo baseInfo, int percent, int byteCount, int totalCount) {
                onFileDownloadListener.onProgress(baseInfo, percent, byteCount, totalCount);
            }

            @Override
            public void onCancel(DownloadBaseInfo baseInfo) {
                downLoadingList.remove(errExpBean.id + "-" + errExpBean.subject);
                onFileDownloadListener.onCancel(baseInfo);
            }

            @Override
            public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                downLoadingList.remove(errExpBean.id + "-" + errExpBean.subject);
                // 下载完成要改名字
                File file = new File(baseInfo.filePath);
                if (file.exists()) {
                    // 名称-文件大小-下载时间戳-任务id-科目-是否完成（0、否 1、是）
                    if (file.renameTo(new File(baseInfo.filePath.replace(errExpBean.name + SPLIT + "0", errExpBean.name + SPLIT + "1")))) {
                        baseInfo.filePath = mFileSavePath = baseInfo.filePath.replace(errExpBean.name + SPLIT + "0", errExpBean.name + SPLIT + "1");
                        onFileDownloadListener.onSuccess(baseInfo, mFileSavePath);
                    }
                }
            }

            @Override
            public void onFailed(DownloadBaseInfo baseInfo) {
                downLoadingList.remove(errExpBean.id + "-" + errExpBean.subject);
                onFileDownloadListener.onFailed(baseInfo);
            }
        }, false, true);

    }

    @Override
    public void openPaper(BaseActivity activity, ErrExpListBean.ErrExpBean bean) {
        FileUtil.readPDF(activity, bean.path);
    }

    @Override
    public void sharePaper(BaseActivity activity, ErrExpListBean.ErrExpBean bean) {
        File data = new File(bean.path);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileprovider", data);
        } else {
            uri = Uri.fromFile(data);
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("application/pdf");

        try {
            activity.startActivity(Intent.createChooser(intent, "试卷分享到"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 文件名称做了一些修改，如果启用这里，需要改变一下文件名称信息解析
    // 这是获取本地数据方法（没网的情况下改成不显示）
//    @Override
//    public ArrayList<ErrExpListBean.ErrExpBean> getLocalArenaList(int littleType) {
//        String filePath = FileUtil.getFilePath(FILE_PARENT, "1");
//        if (!StringUtils.isEmpty(filePath)) {
//            File f = new File(filePath);
//            File parentFile = f.getParentFile();
//            if (parentFile.exists()) {
//                File[] files = parentFile.listFiles();
//                if (files.length > 0) {
//                    ArrayList<ErrExpListBean.ErrExpBean> result = new ArrayList<>();
//                    for (File file : files) {
//                        String name = file.getName();
//                        String[] split = name.split(SPLIT);
//                        // 文件名称，创建文件路径 文件名称意义： 科目-任务id-名称-文件大小-下载时间戳-是否是新下载的（0、否 1、是）
//                        ErrExpListBean.ErrExpBean bean = new ErrExpListBean.ErrExpBean();
//                        bean.isDownLoad = true;
//                        bean.path = file.getAbsolutePath();
//                        if (split.length >= 1) {
//                            try {
//                                bean.subject = Integer.parseInt(split[0]);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (split.length >= 2) {
//                            try {
//                                bean.id = Long.parseLong(split[1]);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (split.length >= 3) {
//                            bean.name = split[2];
//                        }
//                        if (split.length >= 4) {
//                            try {
//                                bean.fileSize = Long.parseLong(split[3]);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (split.length >= 5) {
//                            try {
//                                bean.gmt_create = Long.parseLong(split[4]);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (bean.subject == littleType) {
//                            result.add(bean);
//                        }
//                    }
//                    return result;
//                }
//            }
//        }
//        return null;
//    }

    @Override
    public boolean deletePaper(CompositeSubscription compositeSubscription, final ArrayList<ErrExpListBean.ErrExpBean> deleteErrList, final DeletePaperListener delListener) {
        if (NetUtil.isConnected()) {    // 有网就删除网络上的
            StringBuilder stringBuilder = new StringBuilder();
            for (ErrExpListBean.ErrExpBean errExpBean : deleteErrList) {
                stringBuilder.append(errExpBean.id).append(",");
            }
            ServiceProvider.delErrExport(compositeSubscription, stringBuilder.toString(), new NetResponse() {
                @Override
                public void onError(Throwable e) {
                    delListener.onCall(1);
                }

                @Override
                public void onSuccess(BaseResponseModel model) {
                    delLocalFile(deleteErrList);
                    delListener.onCall(0);
                }
            });
        } else {                        // 没网就删除本地的
            delLocalFile(deleteErrList);
            delListener.onCall(0);
        }
        return false;
    }

    private void delLocalFile(ArrayList<ErrExpListBean.ErrExpBean> deleteErrList) {
        for (ErrExpListBean.ErrExpBean errExpBean : deleteErrList) {
            if (!StringUtils.isEmpty(errExpBean.path)) {
                FileUtil.deleteFile(errExpBean.path);
            }
        }
    }

    @Override
    public void checkLocalFile(ArrayList<ErrExpListBean.ErrExpBean> result) {
        // 检查本地是否有文件
        String filePath = FileUtil.getFilePath(FILE_PARENT, "1");
        if (!StringUtils.isEmpty(filePath)) {
            File f = new File(filePath);
            File parentFile = f.getParentFile();
            if (parentFile.exists()) {
                File[] files = parentFile.listFiles();      // 读取到本地的文件list
                if (files != null && files.length > 0) {
                    for (ErrExpListBean.ErrExpBean errExpBean : result) {   // 遍历，是否有此名字
                        // 名称-文件大小-下载时间戳-任务id-科目-是否完成（0、否 1、是）
                        String fileName = errExpBean.name
                                + SPLIT + "1"
                                + SPLIT + errExpBean.subject
                                + SPLIT + errExpBean.id;               // 下载完成的文件名称
                        String unDownFileName = errExpBean.name
                                + SPLIT + "0"
                                + SPLIT + errExpBean.subject
                                + SPLIT + errExpBean.id;               // 未下载完成的文件名称
                        for (File file : files) {
                            String name = file.getName();
                            if (name.startsWith(fileName)) {                                                                                        // 如果是已经完成，就表明完成
                                errExpBean.path = file.getAbsolutePath();
                                errExpBean.isDownLoad = true;
                                errExpBean.isNew = 0;
                                break;
                            } else if (name.startsWith(unDownFileName) && !downLoadingList.contains(errExpBean.id + "-" + errExpBean.subject)) {    // 如果是未完成并且不在正在下载列表里就删除文件
                                FileUtil.deleteFile(file.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
    }

}
