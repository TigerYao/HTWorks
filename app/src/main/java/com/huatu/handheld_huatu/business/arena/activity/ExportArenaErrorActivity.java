package com.huatu.handheld_huatu.business.arena.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.adapter.ArenaErrExportAdapter;
import com.huatu.handheld_huatu.business.arena.adapter.BaseTreeAdapter;
import com.huatu.handheld_huatu.business.arena.bean.ExportDescription;
import com.huatu.handheld_huatu.business.essay.cusview.CorrectDialog;
import com.huatu.handheld_huatu.business.me.account.RechargeNowActivity;
import com.huatu.handheld_huatu.business.me.fragment.EverydayTaskFragment;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.HomeTreeBeanNew;
import com.huatu.handheld_huatu.mvpmodel.arena.ExportErrBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ExportErrBeanPre;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.custom.CatchLinearLayoutManager;
import com.netease.hearttouch.router.HTPageRouterCall;
import com.netease.hearttouch.router.HTRouter;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 错题导出页面
 */
@HTRouter(url = {"ztk://arena/export/error"}, needLogin = true)
public class ExportArenaErrorActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivBack;
    private ImageView ivExplain;            // 问号解释
    private RecyclerView rlTree;            // 列表数据
    private TextView tvNotice;              // 提示条
    private TextView tvSelectAll;           // 全选
    private LinearLayout llConfirm;         // 确认导出
    private TextView tvFree;                // (5道题免费)
    private CommonErrorView errorView;      // 错误页面

    private ArrayList<HomeTreeBeanNew> originTreeList;                                          // 取回的原始数据

    private String description = "1. 可最多免费导出5题；\n2. 5题以上1题消耗1图币。";     // 导出说明
    private int freeSize = 5;                                                       // 免费导出题量

    private int count = 0;                                      // 选择的错题总数
    private StringBuilder points = new StringBuilder();         // 选择的三级知识点id，用逗号隔开
    private StringBuilder allPoints = new StringBuilder();      // 所有三级知识点id，用逗号隔开，与points比对，看选择是否全部，判断“选择全部”按钮显示样式
    private ArenaErrExportAdapter arenaErrExportAdapter;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_export_arena_error;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(ExportArenaErrorActivity.this);
        ivBack = findViewById(R.id.iv_back);
        ivExplain = findViewById(R.id.iv_explain);
        rlTree = findViewById(R.id.rl_tree);
        tvNotice = findViewById(R.id.tv_notice);
        tvSelectAll = findViewById(R.id.tv_select_all);
        llConfirm = findViewById(R.id.ll_confirm);
        tvFree = findViewById(R.id.tv_free);
        errorView = findViewById(R.id.err_view);

        ivBack.setOnClickListener(this);
        ivExplain.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        llConfirm.setOnClickListener(this);
        errorView.setOnClickListener(this);

        initTreeAdapter();
    }

    @Override
    protected void onLoadData() {

        if (!NetUtil.isConnected()) {
            showErr(1);
            return;
        }

        // 获取导出描述和导出体量信息
        ServiceProvider.getExportDescription(compositeSubscription, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                if (model.data != null) {
                    ExportDescription exportDescription = (ExportDescription) model.data;
                    if (exportDescription.description != null && exportDescription.description.length > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String s : exportDescription.description) {
                            stringBuilder.append(s).append("\n");
                        }
                        description = stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length()).toString();
                    }
                    freeSize = exportDescription.freeSize;
                    tvFree.setText("(" + freeSize + "题内免费)");
                }
            }
        });

        showProgress();
        // 获取错题树
        ServiceProvider.getErrorListX(compositeSubscription, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                hideProgress();
                showErr(2);

            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                hideProgress();
                if (model != null && model.data != null) {
                    originTreeList = (ArrayList<HomeTreeBeanNew>) model.data;
                    arenaErrExportAdapter.setData(originTreeList);
                } else {
                    showErr(3);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:          // 返回
                finish();
                break;
            case R.id.iv_explain:       // 解释
                initCommonDialog();
                tvTitle.setText("导出说明");
                tvTip.setText(description);
                tvKnow.setText("知道了");
                tvKnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (commonDialog != null) {
                            commonDialog.dismiss();
                        }
                    }
                });
                commonDialog.show();
                break;
            case R.id.tv_select_all:    // 选择全部
                StudyCourseStatistic.clickStatistic("题库", "错题重练", "选择全部");
                if (tvSelectAll.getText().equals("选择全部")) {     // 全选
                    tvSelectAll.setText("取消选择");
                    for (HomeTreeBeanNew homeTreeBeanNew : originTreeList) {
                        homeTreeBeanNew.setCheckedState(1);
                        changeChildren(homeTreeBeanNew.getChildren(), 1);
                    }
                } else {                                            // 全取消选
                    tvSelectAll.setText("选择全部");
                    for (HomeTreeBeanNew homeTreeBeanNew : originTreeList) {
                        homeTreeBeanNew.setCheckedState(0);
                        changeChildren(homeTreeBeanNew.getChildren(), 0);
                    }
                }
                selectAndNotifyAdapter();
                break;
            case R.id.ll_confirm:       // 确认导出
                if (count == 0) {
                    ToastUtils.showEssayToast("请选择题目");
                    return;
                }
                // 神策埋点
                StudyCourseStatistic.comfirmExportError(count);
                if (count > 5) {
                    preExport();
                } else {
                    exportError();
                }
                break;
            case R.id.err_view:         // 错误页面
                onLoadData();
                break;
        }
    }

    private int coin;   // 要消耗图币

    // 预下载调用接口，返回真实消耗图币
    private void preExport() {

        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("无网络连接");
            return;
        }

        showProgress();

        ServiceProvider.preExportErrorArena(compositeSubscription, points.toString(), count, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                hideProgress();
                if (e instanceof ApiException) {
                    ToastUtils.showEssayToast(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtils.showEssayToast("预导出失败，请重试");
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                hideProgress();

                if (model.data != null) {
                    ExportErrBeanPre errBean = (ExportErrBeanPre) model.data;
                    coin = errBean.coin;
                    initCommonDialog();
                    tvTitle.setText("图币使用");
                    tvTip.setText(errBean.message);
                    tvCancel.setVisibility(View.VISIBLE);
                    tvCancel.setText("取消");
                    tvKnow.setText("使用");
                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (commonDialog != null) {
                                commonDialog.dismiss();
                            }
                            StudyCourseStatistic.clickStatistic("题库", "错题导出", "取消金币");
                        }
                    });
                    tvKnow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (commonDialog != null) {
                                commonDialog.dismiss();
                            }
                            exportError();
                        }
                    });
                    commonDialog.show();
                } else {
                    ToastUtils.showEssayToast("数据获取失败，请重试");
                }
            }
        });
    }

    private void exportError() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("无网络连接");
            return;
        }

        showProgress();

        ServiceProvider.exportErrorArena(compositeSubscription, points.toString(), new NetResponse() {
            @Override
            public void onError(Throwable e) {
                hideProgress();
                if (e instanceof ApiException) {
                    ToastUtils.showEssayToast(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtils.showEssayToast("导出失败，请重试");
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                hideProgress();

                // 图币不足
                if (model != null && model.data instanceof ExportErrBean && ((ExportErrBean) model.data).payStatus == -4) {
                    showBuyCoin();
                    return;
                }

                if (model != null && model.data instanceof ExportErrBean) {
                    ExportErrBean errBean = (ExportErrBean) model.data;
                    if (errBean.list != null && errBean.list.size() > 0) {
                        // 神策埋点
                        StudyCourseStatistic.exportErrorSucceed(errBean.list.get(0).sum, coin);
                    }
                }

                initCommonDialog();
                tvTitle.setVisibility(View.GONE);
                tvTip.setText("题目已加入到“下载”-“错题下载”列表中，请前往查看～");
                tvCancel.setVisibility(View.VISIBLE);
                tvCancel.setText("知道了");
                tvKnow.setText("立即前往");
                tvKnow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (commonDialog != null) {
                            commonDialog.dismiss();
                        }
                        StudyCourseStatistic.clickStatistic("题库", "错题导出", "立即前往");
                        // 去下载页面
                        Intent sourceIntent = new Intent();
                        sourceIntent.putExtra("curSubject", SpUtils.getUserSubject());
                        sourceIntent.putExtra("showIndex", 1);
                        HTPageRouterCall.newBuilderV2("ztk://arena/download")
                                .context(ExportArenaErrorActivity.this)
                                .sourceIntent(sourceIntent)
                                .build()
                                .start();
                    }
                });
                commonDialog.show();
            }
        });
    }

    // 金币不足弹窗，任务页面/购买页面
    private void showBuyCoin() {
        initCommonDialog();
        ivImg.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.INVISIBLE);
        tvTip.setText("抱歉，您的图币不足。您可以去充值，或者做任务也能获得图币哦～");
        tvCancel.setVisibility(View.VISIBLE);
        tvCancel.setText("查看任务");
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commonDialog != null) {
                    commonDialog.dismiss();
                }
                BaseFrgContainerActivity.newInstance(ExportArenaErrorActivity.this,
                        EverydayTaskFragment.class.getName(),
                        EverydayTaskFragment.getArgs());
            }
        });
        tvKnow.setText("去充值");
        tvKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commonDialog != null) {
                    commonDialog.dismiss();
                }
                // 去购买金币
                RechargeNowActivity.newInstance(ExportArenaErrorActivity.this, 110);
            }
        });
        commonDialog.show();
    }

    private CorrectDialog commonDialog;
    private ImageView ivImg;
    private TextView tvTitle;
    private TextView tvTip;
    private TextView tvCancel;
    private TextView tvKnow;

    private void initCommonDialog() {
        if (commonDialog == null) {
            commonDialog = new CorrectDialog(ExportArenaErrorActivity.this, R.layout.err_export_tip);
            View mContentView = commonDialog.mContentView;
            ivImg = mContentView.findViewById(R.id.iv_img);
            tvTitle = mContentView.findViewById(R.id.tv_title);
            tvTip = mContentView.findViewById(R.id.tv_tip);
            tvCancel = mContentView.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commonDialog != null) {
                        commonDialog.dismiss();
                    }
                }
            });
            tvKnow = mContentView.findViewById(R.id.tv_know);
        }
        ivImg.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.GONE);
    }

    // 知识树Adapter
    private void initTreeAdapter() {
        rlTree.setNestedScrollingEnabled(false);
        rlTree.setLayoutManager(new CatchLinearLayoutManager(ExportArenaErrorActivity.this));
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(100);
        animator.setRemoveDuration(100);
        animator.setChangeDuration(200);
        rlTree.setItemAnimator(animator);

        arenaErrExportAdapter = new ArenaErrExportAdapter(ExportArenaErrorActivity.this, new BaseTreeAdapter.OnItemClickListener() {
            @Override
            public void doSomeThing(int type, HomeTreeBeanNew homeTreeBeanNew) {
                if (homeTreeBeanNew == null) return;

                // 被选中状态 0、未选中 1、选中 2、半选中
                int checkedState = homeTreeBeanNew.getCheckedState();
                if (checkedState == 0 || checkedState == 2) {
                    homeTreeBeanNew.setCheckedState(1);
                } else {
                    homeTreeBeanNew.setCheckedState(0);
                }

                // 父亲是什么样，子孙就是什么样
                changeChildren(homeTreeBeanNew.getChildren(), homeTreeBeanNew.getCheckedState());
                // 如果选中，父亲要根据子孙的情况; 如果未选中，父类未选中
                changeParent(homeTreeBeanNew);
                selectAndNotifyAdapter();
            }
        });

        rlTree.setAdapter(arenaErrExportAdapter);
    }

    // 选中状态改变
    private void changeParent(HomeTreeBeanNew homeTreeBeanNew) {
        HomeTreeBeanNew parent = homeTreeBeanNew.getParent();
        while (parent != null) {

            parent.setCheckedState(0);                                                  // 假定子类全没选 自己全未选
            boolean isAllChildChecked = true;                                           // 假定全选了
            for (HomeTreeBeanNew child : parent.getChildren()) {
                if (child.getCheckedState() == 1 || child.getCheckedState() == 2) {     // 只要有子类选上了 || 半选了 自己就是半选
                    parent.setCheckedState(2);
                } else {                                                                // 只要有没选的，就是没全选
                    isAllChildChecked = false;
                }
            }
            if (isAllChildChecked) {                                                    // 如果全选了，自己就全选
                parent.setCheckedState(1);
            }

            parent = parent.getParent();
        }
    }

    private void changeChildren(ArrayList<HomeTreeBeanNew> children, int checkedState) {
        if (children != null && children.size() > 0) {
            for (HomeTreeBeanNew child : children) {
                child.setCheckedState(checkedState);
                changeChildren(child.getChildren(), checkedState);
            }
        }
    }

    private void selectAndNotifyAdapter() {
        arenaErrExportAdapter.notifyDataSetChanged();
        // 判断是否显示底部的提示条
        count = 0;
        points.setLength(0);
        allPoints.setLength(0);
        scaleSelect(originTreeList);
        // 神策埋点
        StudyCourseStatistic.selectExErrQuestion(count);
        if (count > freeSize) {
            tvNotice.setText("导出" + count + "道题，将消耗" + (count - freeSize) + "个图币");
            tvNotice.setVisibility(View.VISIBLE);
        } else {
            tvNotice.setVisibility(View.GONE);
        }
        if (points.length() == 0) {                             // 全没选
            tvSelectAll.setText("选择全部");
        } else if (points.length() == allPoints.length()) {     // 全选
            tvSelectAll.setText("取消选择");
        }
    }

    // 扫描选择的要导出的知识点
    private void scaleSelect(ArrayList<HomeTreeBeanNew> treeList) {
        if (treeList != null && treeList.size() > 0) {
            for (HomeTreeBeanNew homeTreeBeanNew : treeList) {
                if (homeTreeBeanNew.getLevel() == 2 && homeTreeBeanNew.getCheckedState() == 1) {
                    points.append(homeTreeBeanNew.getId()).append(",");
                    count += homeTreeBeanNew.getWnum();
                }
                if (homeTreeBeanNew.getLevel() == 2) {
                    allPoints.append(homeTreeBeanNew.getId()).append(",");
                }
                scaleSelect(homeTreeBeanNew.getChildren());
            }
        }
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onLoadData();
    }

    /**
     * @param flag 1、无网络
     *             2、获取数据失败
     *             3、无数据
     */
    public void showErr(int flag) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setErrorImageVisible(true);
        switch (flag) {
            case 1:
                errorView.setErrorText("网络不太好，点击屏幕，刷新看看");
                errorView.setErrorImage(R.drawable.icon_common_net_unconnected);
                break;
            case 2:
                errorView.setErrorText("获取数据失败，点击刷新");
                errorView.setErrorImage(R.drawable.no_data_bg);
                break;
            case 3:
                errorView.setErrorText("什么都没有");
                errorView.setErrorImage(R.drawable.no_data_bg);
                break;
        }
    }
}
