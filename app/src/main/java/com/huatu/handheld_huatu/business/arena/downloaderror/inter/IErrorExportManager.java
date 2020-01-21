package com.huatu.handheld_huatu.business.arena.downloaderror.inter;

import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.downloaderror.ErrExportManager;
import com.huatu.handheld_huatu.business.arena.downloaderror.bean.ErrExpListBean;
import com.huatu.handheld_huatu.listener.OnFileDownloadListener;

import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;

public interface IErrorExportManager {

    // 下载试卷
    void downloadPaper(CompositeSubscription compositeSubscription, ErrExpListBean.ErrExpBean errExpBean, OnFileDownloadListener onFileDownloadListener);

    // 打开试卷
    void openPaper(BaseActivity activity, ErrExpListBean.ErrExpBean bean);

    // 本地分享试卷文件
    void sharePaper(BaseActivity activity, ErrExpListBean.ErrExpBean bean);

//    // 获得本地下载的试卷信息List
//    ArrayList<ErrExpListBean.ErrExpBean> getLocalArenaList(int littleType);

    // 删除（批量）试卷
    boolean deletePaper(CompositeSubscription compositeSubscription, ArrayList<ErrExpListBean.ErrExpBean> deleteErrList, DeletePaperListener delListener);

    // 根据网络获取的文件信息，检查本地数据，进行合并
    void checkLocalFile(ArrayList<ErrExpListBean.ErrExpBean> result);

    interface DeletePaperListener{
        // type 0、删除成功 1、删除失败
        void onCall(int type);
    }
}
