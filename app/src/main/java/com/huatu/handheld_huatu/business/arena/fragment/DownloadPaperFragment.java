package com.huatu.handheld_huatu.business.arena.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.DownloadArenaPaperActivity;
import com.huatu.handheld_huatu.business.arena.adapter.DownloadedArenaPaperAdapter;
import com.huatu.handheld_huatu.business.arena.downloadpaper.ArenaPaperLocalFileManager;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaPaperFileBean;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaPaperInfoNet;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class DownloadPaperFragment extends DownloadChildBaseFragment {

    private RecyclerView rlOne;             // 试卷下载列表
    private CommonErrorView errOne;

    private List<ArenaPaperFileBean> localArenaListData;        // 本地下载的数据
    private DownloadedArenaPaperAdapter oneAdapter;             // 试卷下载adapter

    private int curSubject;

    @Override
    public int onSetRootViewId() {
        return R.layout.arena_download_layout;
    }

    @Override
    protected void onInitView() {
        compositeSubscription = new CompositeSubscription();

        curSubject = args.getInt("curSubject");

        rlOne = rootView.findViewById(R.id.rv_view);
        errOne = rootView.findViewById(R.id.err_view);

        errOne.setErrorImageVisible(true);
        errOne.setErrorImage(R.drawable.no_data_bg);
        errOne.setErrorText("您还没有下载试卷，赶快去下载吧！");

        localArenaListData = new ArrayList<>();
        // 侧滑实现
        rlOne.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(mActivity));
        rlOne.setLayoutManager(new LinearLayoutManager(mActivity));
        oneAdapter = new DownloadedArenaPaperAdapter(mActivity, localArenaListData, compositeSubscription, curSubject);
        rlOne.setAdapter(oneAdapter);
    }

    @Override
    protected void onLoadData() {
        loadPaperData();
    }

    // 获取本地试卷数据
    private void loadPaperData() {
        // 读取本地数据
        List<ArenaPaperFileBean> localArenaList = ArenaPaperLocalFileManager.newInstance().getLocalArenaList(curSubject);
        localArenaListData.clear();
        if (localArenaList != null && localArenaList.size() > 0) {
            localArenaListData.addAll(localArenaList);
        }
        if (localArenaListData.size() > 0) {
            if (SpUtils.getSharePaperTipShow()) {
                ((DownloadArenaPaperActivity)mActivity).rl_tip.setVisibility(View.VISIBLE);
                SpUtils.setSharePaperTipShow(false);
            }
            errOne.setVisibility(View.GONE);
            // 初始化是否选中和是否有更新
            for (ArenaPaperFileBean localArenaListDatum : localArenaListData) {
                localArenaListDatum.setChecked(false);
                localArenaListDatum.setHasNew(false);
            }
            // 检查试卷更新
            checkPaper();
        } else {
            errOne.setVisibility(View.VISIBLE);
        }
        oneAdapter.notifyDataSetChanged();
    }

    // 试卷检查更新
    private void checkPaper() {

        if (!NetUtil.isConnected()) return;

        StringBuilder ids = new StringBuilder();

        for (ArenaPaperFileBean localArenaListDatum : localArenaListData) {
            ids.append(",").append(localArenaListDatum.getPaperId());
        }

        ids = new StringBuilder(ids.substring(1));

        ServiceProvider.getArenaPaperInfoList(compositeSubscription, ids.toString(), new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                if (model.data.size() > 0) {
                    List<ArenaPaperInfoNet> paperInfoNets = model.data;
                    for (ArenaPaperInfoNet paperInfoNet : paperInfoNets) {
                        for (ArenaPaperFileBean localArenaListDatum : localArenaListData) {
                            if (paperInfoNet.getId() == localArenaListDatum.getPaperId()) {
                                if (!paperInfoNet.getGmtModify().equals(localArenaListDatum.getUpdateTime())) { // 创建时间不同，就是需要更新
                                    localArenaListDatum.setHasNew(true);
                                }
                                break;
                            }
                        }
                    }
                    oneAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void editItem(boolean editOrComplete) {
        if (!editOrComplete) {
            for (ArenaPaperFileBean localArenaListDatum : localArenaListData) {
                localArenaListDatum.setChecked(false);
            }
        }
        oneAdapter.notifyDataSetChanged();
    }

    @Override
    public void selectAll(boolean select) {
        if (localArenaListData.size() > 0) {
            for (ArenaPaperFileBean localArenaListDatum : localArenaListData) {
                localArenaListDatum.setChecked(select);
            }
            oneAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<Long> deleteList = new ArrayList<>();
    private CustomConfirmDialog exitConfirmDialog;

    @Override
    public void delete() {
        deleteList.clear();
        for (ArenaPaperFileBean localArenaListDatum : localArenaListData) {
            if (localArenaListDatum.isChecked()) {
                deleteList.add(localArenaListDatum.getPaperId());
            }
        }
        if (deleteList.size() == 0) {
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
                    doDeleteOne(deleteList);
                }
            });
            exitConfirmDialog.setCancelBtnVisibility(true);
        }
        exitConfirmDialog.show();
    }

    // 删除真题演练等试卷
    private void doDeleteOne(ArrayList<Long> deleteList) {
        ArenaPaperLocalFileManager.newInstance().deletePaper(deleteList);
        ((DownloadArenaPaperActivity)mActivity).resetTab();
        onLoadData();
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        onLoadData();
    }
}
