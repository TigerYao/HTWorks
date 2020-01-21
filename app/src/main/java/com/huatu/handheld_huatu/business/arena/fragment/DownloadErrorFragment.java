package com.huatu.handheld_huatu.business.arena.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.DownloadArenaPaperActivity;
import com.huatu.handheld_huatu.business.arena.adapter.DownloadedErrExpAdapter;
import com.huatu.handheld_huatu.business.arena.downloaderror.ErrExportManager;
import com.huatu.handheld_huatu.business.arena.downloaderror.bean.ErrExpListBean;
import com.huatu.handheld_huatu.business.arena.downloaderror.inter.IErrorExportManager;
import com.huatu.handheld_huatu.business.arena.downloadpaper.listener.ArenaDownloadListener;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DownloadBaseInfo;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.library.PullToRefreshBase;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class DownloadErrorFragment extends DownloadChildBaseFragment {

    private PullRefreshRecyclerView rlTwo;  // 错题导出下载列表
    private CommonErrorView errTwo;

    private List<ErrExpListBean.ErrExpBean> errExpListData;     // 错题导出数据
    private DownloadedErrExpAdapter twoAdapter;                 // 错题导出adapter

    public boolean isEdit = false;                              // 是否正在编辑

    @Override
    public int onSetRootViewId() {
        return R.layout.arena_download_err_exp_layout;
    }

    @Override
    protected void onInitView() {
        compositeSubscription = new CompositeSubscription();

        rlTwo = rootView.findViewById(R.id.rv_view);
        errTwo = rootView.findViewById(R.id.err_view);
        errTwo.setErrorImageVisible(true);
        errTwo.setErrorImage(R.drawable.no_data_bg);
        errTwo.setErrorText("您还没有下载试卷，赶快去下载吧！");
        errTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadErrExpData(true);
            }
        });

        errExpListData = new ArrayList<>();
        rlTwo.getRefreshableView().setPagesize(size);
        rlTwo.getRefreshableView().addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(mActivity));
        rlTwo.getRefreshableView().setLayoutManager(new LinearLayoutManager(mActivity));
        twoAdapter = new DownloadedErrExpAdapter(mActivity, errExpListData, new DownloadedErrExpAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                ErrExpListBean.ErrExpBean bean = errExpListData.get(position);
                if (bean == null) return;
                if (isEdit) {
                    bean.isChecked = !bean.isChecked;
                    twoAdapter.notifyDataSetChanged();
                } else {
                    if (bean.isDownLoad) {  // 已经下载了
                        bean.isNew = 0;
                        twoAdapter.notifyDataSetChanged();

                        File file = new File(bean.path);
                        if (file.exists()) {
                            ErrExportManager.newInstance().openPaper(mActivity, bean);
                        } else {
                            bean.isNew = 1;
                            bean.isDownLoad = false;
                            bean.path = "";
                            downLoadExportErr(0, bean);
                        }
                    } else {                // 未下载
                        downLoadExportErr(0, bean);
                    }
                }
            }

            @Override
            public void onShare(int position) {
                ErrExpListBean.ErrExpBean bean = errExpListData.get(position);
                if (bean == null) return;
                if (bean.isDownLoad) {  // 已经下载了
                    File file = new File(bean.path);
                    if (file.exists()) {
                        ErrExportManager.newInstance().sharePaper(mActivity, bean);
                    } else {
                        bean.isNew = 1;
                        bean.isDownLoad = false;
                        bean.path = "";
                        downLoadExportErr(1, bean);
                    }
                } else {                // 未下载
                    downLoadExportErr(1, bean);
                }

            }
        });

        rlTwo.getRefreshableView().setOnLoadMoreListener(new IonLoadMoreListener() {
            @Override
            public void OnLoadMoreEvent(boolean isRetry) {
                loadErrExpData(false);
            }
        });
        rlTwo.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                loadErrExpData(true);
            }
        });
        rlTwo.getRefreshableView().setRecyclerAdapter(twoAdapter);
    }

    @Override
    protected void onLoadData() {
        loadErrExpData(true);
    }


    private int page = 1;
    private int size = 20;

    // 获取错题导出数据
    private void loadErrExpData(boolean isRefresh) {

        if (!NetUtil.isConnected()) {
            ToastUtil.showToast("无网络连接");
            showErr(0);
            return;
        }

        if (isRefresh) {
            page = 1;
            errExpListData.clear();
            rlTwo.getRefreshableView().resetAll();
            twoAdapter.notifyDataSetChanged();
        }

        mActivity.showProgress();
        ServiceProvider.getErrExpList(compositeSubscription, page, size, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
                rlTwo.onRefreshComplete();
                showErr(1);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                mActivity.hideProgress();
                rlTwo.onRefreshComplete();
                if (model.data != null) {
                    ErrExpListBean listBean = (ErrExpListBean) model.data;
                    if (listBean.result != null && listBean.result.size() > 0) {
                        ErrExportManager.newInstance().checkLocalFile(listBean.result);
                        errExpListData.addAll(listBean.result);
                        twoAdapter.notifyDataSetChanged();
                    }
                    if (listBean.next == 0) {
                        rlTwo.getRefreshableView().checkloadMore(0);
                        rlTwo.getRefreshableView().hideloading();
                    }
                }
                if (page == 1 && errExpListData.size() == 0) {
                    showErr(2);
                } else {
                    errTwo.setVisibility(View.GONE);
                }
                page++;
            }
        });

//        if (NetUtil.isConnected()) {    // 有网，获取网络列表
//        } else {                        // 无网，获取本地文件数据
//            rlTwo.onRefreshComplete();
//            ArrayList<ErrExpListBean.ErrExpBean> localArenaList = ErrExportManager.newInstance().getLocalArenaList(curSubject);
//            if (localArenaList != null && localArenaList.size() > 0) {
//                errExpListData.addAll(localArenaList);
//                twoAdapter.notifyDataSetChanged();
//                rlTwo.getRefreshableView().checkloadMore(0);
//                rlTwo.getRefreshableView().hideloading();
//            } else {
//                showErr(2);
//            }
//        }
//        twoAdapter.notifyDataSetChanged();
    }

    // 去下载错题导出试卷
    // type 0、打开 1、分享
    private void downLoadExportErr(final int type, final ErrExpListBean.ErrExpBean errExpBean) {
        if (!NetUtil.isConnected()) {
            ToastUtil.showToast("网络异常，请连接网络后查看");
            return;
        }

        // 神策埋点，pdf点击下载
        StudyCourseStatistic.downloadExErr(errExpBean.sum, String.valueOf(errExpBean.id), "错题");

        mActivity.showProgress();
        ErrExportManager.newInstance().downloadPaper(compositeSubscription, errExpBean, new ArenaDownloadListener() {
            @Override
            public void onCancel(DownloadBaseInfo baseInfo) {
                mActivity.hideProgress();
            }

            @Override
            public void onFailed(DownloadBaseInfo baseInfo) {
                mActivity.hideProgress();
                ToastUtil.showToast("下载失败，请稍后重试");
            }

            @Override
            public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                mActivity.hideProgress();
                if (!Method.isActivityFinished(mActivity)) {

                    // 神策埋点，下载成功
                    StudyCourseStatistic.downloadExErrSucceed(errExpBean.sum, String.valueOf(errExpBean.id), "错题");
                    errExpBean.isDownLoad = true;
                    errExpBean.isNew = 0;
                    errExpBean.path = baseInfo.filePath;
                    errExpBean.fileSize = baseInfo.fileSize;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (type == 0) {
                                ErrExportManager.newInstance().openPaper(mActivity, errExpBean);
                            } else {
                                ErrExportManager.newInstance().sharePaper(mActivity, errExpBean);
                            }
                            twoAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void editItem(boolean editOrComplete) {
        isEdit = editOrComplete;
        if (!editOrComplete) {
            for (ErrExpListBean.ErrExpBean errExpListDatum : errExpListData) {
                errExpListDatum.isChecked = false;
            }
        }
        twoAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectAll(boolean select) {
        if (errExpListData.size() > 0) {
            for (ErrExpListBean.ErrExpBean errExpListDatum : errExpListData) {
                errExpListDatum.isChecked = select;
            }
            twoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 删除本地数据
     */
    private CustomConfirmDialog exitConfirmDialog;
    private ArrayList<ErrExpListBean.ErrExpBean> deleteErrList = new ArrayList<>();

    @Override
    public void delete() {
        deleteErrList.clear();
        for (ErrExpListBean.ErrExpBean errExpListDatum : errExpListData) {
            if (errExpListDatum.isChecked) {
                deleteErrList.add(errExpListDatum);
            }
        }
        if (deleteErrList.size() == 0) {
            ToastUtil.showToast("您还没有选中要删除的试卷");
            return;
        }
        if (exitConfirmDialog == null) {
            exitConfirmDialog = DialogUtils.createDialog(mActivity, "", "您确定要删除试卷吗？");
            exitConfirmDialog.setPositiveButton("取消", null);
            exitConfirmDialog.setNegativeButton("删除", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.showProgress();
                    doDeleteTwo(deleteErrList);
                }
            });
            exitConfirmDialog.setCancelBtnVisibility(true);
        }
        exitConfirmDialog.show();
    }

    // 删除错题导出试卷
    private void doDeleteTwo(ArrayList<ErrExpListBean.ErrExpBean> deleteErrList) {
        ErrExportManager.newInstance().deletePaper(compositeSubscription, deleteErrList, new IErrorExportManager.DeletePaperListener() {
            @Override
            public void onCall(int type) {
                mActivity.hideProgress();
                if (type == 0) {
                    ((DownloadArenaPaperActivity) mActivity).resetTab();
                    onLoadData();
                } else {
                    ToastUtil.showToast("删除失败，请重试");
                }
            }
        });
    }

    /**
     * @param flag 1、获取数据失败
     *             2、无数据
     */
    public void showErr(int flag) {
        errTwo.setVisibility(View.VISIBLE);
        errTwo.setErrorImageVisible(true);
        switch (flag) {
            case 0:
                errTwo.setErrorText("网络不太好，点击屏幕，刷新看看");
                errTwo.setErrorImage(R.drawable.icon_common_net_unconnected);
                break;
            case 1:
                errTwo.setErrorText("获取数据失败，点击刷新");
                errTwo.setErrorImage(R.drawable.no_data_bg);
                break;
            case 2:
                errTwo.setErrorText("您还没有导出错题，赶快去导出吧！");
                errTwo.setErrorImage(R.drawable.no_data_bg);
                break;
        }
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        onLoadData();
    }
}
