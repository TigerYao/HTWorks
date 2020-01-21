package com.huatu.handheld_huatu.business.arena.downloadpaper.inter;

import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaPaperFileBean;
import com.huatu.handheld_huatu.listener.OnFileDownloadListener;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * 行测试卷下载管理类接口
 */
public interface IArenaPaperManager {

    // 获得本地下载的试卷信息List
    List<ArenaPaperFileBean> getLocalArenaList(int littleType);

    // 是否已经下载了此试卷
    boolean isDownLoadedPaper(long paperId);

    // 删除（批量）试卷
    boolean deletePaper(ArrayList<Long> paperIds);

    // 下载试卷
    void downloadPaper(CompositeSubscription compositeSubscription, long paperId, String name, int littleType, OnFileDownloadListener onFileDownloadListener);

    // 打开试卷
    void openPaper(BaseActivity activity, long paperId);

    // 本地分享试卷文件
    void sharePaper(BaseActivity activity, long paperId);

    // 检查是不是最新的试卷
    boolean hasNewPaper(long paperId, String gmtModify);
}
