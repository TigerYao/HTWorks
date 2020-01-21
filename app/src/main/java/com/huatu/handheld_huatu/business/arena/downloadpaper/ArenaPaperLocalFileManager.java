package com.huatu.handheld_huatu.business.arena.downloadpaper;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaDownLoadUrlBean;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaPaperFileBean;
import com.huatu.handheld_huatu.business.arena.downloadpaper.inter.IArenaPaperManager;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.listener.OnFileDownloadListener;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DownloadBaseInfo;
import com.huatu.handheld_huatu.utils.DownloadManager;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class ArenaPaperLocalFileManager implements IArenaPaperManager {

    private String FILE_PARENT = "download_arena";          // 下载文件的父文件夹

    private Gson gson;

    // 单例模式
    private static volatile ArenaPaperLocalFileManager singleton;

    public static ArenaPaperLocalFileManager newInstance() {
        if (singleton == null) {
            synchronized (ArenaPaperLocalFileManager.class) {
                if (singleton == null) {
                    singleton = new ArenaPaperLocalFileManager();
                }
            }
        }
        return singleton;
    }

    // 读取本地sp中存储的已下载试卷信息，根据科目、小分类过滤
    // 为了区分科目，所以key中添加了科目信息。不同科目的文件，放到了不同文件夹里
    // String key = paperId + "-" + SpUtils.getUserCatgory();
    private HashMap<String, ArenaPaperFileBean> localArenaMap;
    private ArrayList<String> downLoadingList;                        // 正在下载中的试卷，防止多次点击下载

    private ArenaPaperLocalFileManager() {
        gson = new Gson();
        localArenaMap = new HashMap<>();
        downLoadingList = new ArrayList<>();
        getLocalFile();                                 // 读取本地文件信息
    }

    @Override
    public List<ArenaPaperFileBean> getLocalArenaList(int littleType) {
        if (localArenaMap.size() == 0) {
            return null;
        }

        HashMap<String, ArenaPaperFileBean> fileMap = new HashMap<>(localArenaMap);

        int catgory = SpUtils.getUserCatgory();

        // 一次过滤，是否是当前科目，不是的remove掉
        List<String> wrongKeys = new ArrayList<>();
        if (fileMap.size() > 0) {
            for (String key : fileMap.keySet()) {
                ArenaPaperFileBean fileBean = fileMap.get(key);
                if (fileBean.getCatgory() != catgory) {
                    wrongKeys.add(key);
                }
            }
            if (wrongKeys.size() > 0) {
                for (String wrongKey : wrongKeys) {
                    fileMap.remove(wrongKey);
                }
            }
        }

        ArrayList<ArenaPaperFileBean> arenaPaperFileBeans = new ArrayList<>(fileMap.values());
        ArrayList<ArenaPaperFileBean> filterData = new ArrayList<>();
        // 二次过滤，为了过滤考试科目下的小科目
        for (ArenaPaperFileBean arenaPaperFileBean : arenaPaperFileBeans) {
            if (arenaPaperFileBean.getLittleType() == littleType) {
                filterData.add(arenaPaperFileBean);
            }
        }
        Collections.sort(filterData, new Comparator<ArenaPaperFileBean>() {
            @Override
            public int compare(ArenaPaperFileBean o1, ArenaPaperFileBean o2) {
                return o1.getDownloadTime() - o2.getDownloadTime() > 0 ? -1 : 1;
            }
        });
        Log.i("ArenaPaperLocalFile", gson.toJson(arenaPaperFileBeans));
        return filterData;
    }

    @Override
    public boolean isDownLoadedPaper(long paperId) {
        String key = paperId + "-" + SpUtils.getUserCatgory();
        return localArenaMap.containsKey(key);
    }

    @Override
    public boolean deletePaper(ArrayList<Long> paperIds) {
        for (Long paperId : paperIds) {
            String key = paperId + "-" + SpUtils.getUserCatgory();
            if (localArenaMap.containsKey(key)) {
                ArenaPaperFileBean fileBean = localArenaMap.get(key);
                if (FileUtil.deleteFile(fileBean.getFilePath())) {
                    localArenaMap.remove(key);
                }
            }
        }
        saveMapToLocal(localArenaMap);
        return true;
    }

    // 重新下载     ArenaPaperLocalFileManager.newInstance().openPaper(mActivity, paperId);
    // 第一次下载    ToastUtil.showToast("试卷下载成功。请在“题库”页面，向左滑动图标，然后点击“试卷下载”图标就可以查看下载好的试卷了哦。", 2500);
    @Override
    public void downloadPaper(CompositeSubscription compositeSubscription, final long paperId, final String name, final int littleType, final OnFileDownloadListener onFileDownloadListener) {

        // final ArenaPaperFileBean fileBean, String downloadUrl

        if (compositeSubscription == null) return;

        final String key = paperId + "-" + SpUtils.getUserCatgory();

        // 如果正在下载就return
        if (downLoadingList.contains(key)) {
            return;
        }

        downLoadingList.add(key);

        ToastUtil.showToast("开始下载");

        ServiceProvider.getArenaPaperDownloadUrl(compositeSubscription, paperId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                if (model.data instanceof ArenaDownLoadUrlBean) {

                    ArenaDownLoadUrlBean urlBean = (ArenaDownLoadUrlBean) model.data;

                    if (urlBean.getUrl().startsWith("http")) {
                        urlBean.setLittleType(littleType);
                        downLoadFileBean(urlBean, name, onFileDownloadListener);
                    } else {
                        ToastUtil.showToast("下载路径出错");
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                downLoadingList.remove(key);
                if (e instanceof ApiException) {
                    String errorMsg = ((ApiException) e).getErrorMsg();
                    ToastUtil.showToast(errorMsg);
                } else {
                    ToastUtil.showToast("服务器错误");
                }
            }
        });
    }

    private void downLoadFileBean(ArenaDownLoadUrlBean urlBean, String name, final OnFileDownloadListener onFileDownloadListener) {

        final ArenaPaperFileBean bean = new ArenaPaperFileBean();

        int userCatgory = SpUtils.getUserCatgory();

        bean.setPaperId(urlBean.getPaperId());
        bean.setCatgory(userCatgory);
        bean.setLittleType(urlBean.getLittleType());
        bean.setName(name);
        bean.setUpdateTime(urlBean.getUpdateTime());        // 把当前的试卷创建时间存储下来
        bean.setDownloadTime(System.currentTimeMillis());

        // 文件名称，创建文件路径
        String fileName = bean.getName() + ".pdf";
        String catgoryPath = FILE_PARENT + "/" + userCatgory;              // 拼接科目路径
        String filePath = FileUtil.getFilePath(catgoryPath, fileName);

        bean.setFilePath(filePath);

        // 添加到下载任务
        DownloadBaseInfo downloadBaseInfo = new DownloadBaseInfo(urlBean.getUrl());
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
                downLoadingList.remove(bean.getPaperId() + "-" + bean.getCatgory());
                onFileDownloadListener.onCancel(baseInfo);
            }

            @Override
            public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                // 下载成功，得到文件大小，放入本地变量，存储进sp
                downLoadingList.remove(bean.getPaperId() + "-" + bean.getCatgory());
                String fileSize = FileUtil.getFileSize(mFileSavePath);
                bean.setSize(fileSize);
                localArenaMap.put(bean.getPaperId() + "-" + bean.getCatgory(), bean);
                // 下载成功，添加进本地存储
                saveToLocal(bean);
                onFileDownloadListener.onSuccess(baseInfo, mFileSavePath);
            }

            @Override
            public void onFailed(DownloadBaseInfo baseInfo) {
                downLoadingList.remove(bean.getPaperId() + "-" + bean.getCatgory());
                onFileDownloadListener.onFailed(baseInfo);
            }
        }, false, true);

    }

    @Override
    public void openPaper(BaseActivity activity, long paperId) {
        if (Method.isActivityFinished(activity)) {
            return;
        }
        String key = paperId + "-" + SpUtils.getUserCatgory();
        if (!localArenaMap.containsKey(key)) {
            return;
        }
        ArenaPaperFileBean arenaPaperFileBean = localArenaMap.get(key);
        FileUtil.readPDF(activity, arenaPaperFileBean.getFilePath());
    }

    @Override
    public void sharePaper(BaseActivity activity, long paperId) {
        String key = paperId + "-" + SpUtils.getUserCatgory();
        if (!localArenaMap.containsKey(key)) {
            return;
        }
        ArenaPaperFileBean arenaPaperFileBean = localArenaMap.get(key);
        File data = new File(arenaPaperFileBean.getFilePath());

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

    @Override
    public boolean hasNewPaper(long paperId, String gmtModify) {
        String key = paperId + "-" + SpUtils.getUserCatgory();
        if (!localArenaMap.containsKey(key)) {
            return false;
        }
        ArenaPaperFileBean bean = localArenaMap.get(key);
        return !gmtModify.equals(bean.getUpdateTime());
    }

    /**
     * 读取本地存储的文件信息
     */
    private void getLocalFile() {
        localArenaMap.clear();
        String arenaPaperLocalFile = SpUtils.getArenaPaperLocalFile();
        if (arenaPaperLocalFile != null) {
            try {
                HashMap<String, ArenaPaperFileBean> localArenaMapX = gson.fromJson(arenaPaperLocalFile, new TypeToken<HashMap<String, ArenaPaperFileBean>>() {
                }.getType());
                localArenaMap.putAll(localArenaMapX);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        // 遍历存储，本地文件存在不，不存在就删除map，并保存到sp
        List<String> deleteKeys = new ArrayList<>();
        if (localArenaMap.size() > 0) {
            for (String key : localArenaMap.keySet()) {
                ArenaPaperFileBean fileBean = localArenaMap.get(key);
                if (!FileUtil.isFileExist(fileBean.getFilePath())) {
                    deleteKeys.add(key);
                }
            }
            if (deleteKeys.size() > 0) {
                for (String deleteKey : deleteKeys) {
                    localArenaMap.remove(deleteKey);
                }
            }
            saveMapToLocal(localArenaMap);
        }
    }

    // 全部存储
    private void saveMapToLocal(HashMap<String, ArenaPaperFileBean> map) {
        String localArenaMapString = gson.toJson(map);
        SpUtils.setArenaPaperLocalFile(localArenaMapString);
    }

    // 下载完成添加到本地
    private void saveToLocal(ArenaPaperFileBean fileBean) {
        String arenaPaperLocalFile = SpUtils.getArenaPaperLocalFile();
        HashMap<String, ArenaPaperFileBean> localArenaMapNow = new HashMap<>();
        if (arenaPaperLocalFile != null) {
            try {
                localArenaMapNow = gson.fromJson(arenaPaperLocalFile, new TypeToken<HashMap<String, ArenaPaperFileBean>>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        String key = fileBean.getPaperId() + "-" + fileBean.getCatgory();
        localArenaMapNow.put(key, fileBean);
        saveMapToLocal(localArenaMapNow);
    }
}
