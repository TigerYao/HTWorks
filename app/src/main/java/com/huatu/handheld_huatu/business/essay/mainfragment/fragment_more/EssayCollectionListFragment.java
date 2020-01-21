package com.huatu.handheld_huatu.business.essay.mainfragment.fragment_more;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.adapter.EssayCollectMultiAdapter;
import com.huatu.handheld_huatu.business.essay.adapter.EssayCollectSingleAdapter;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseData;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseResult;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseData;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseResult;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.XListView;
import com.ogaclejapan.v4.FragmentPagerItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 收藏列表
 */

public class EssayCollectionListFragment extends BaseFragment implements XListView.IXListViewListener, View.OnClickListener {

    private static final String TAG = "EssayCollectionListFragment";

    @BindView(R.id.layout_nodata)
    RelativeLayout layout_nodata;
    @BindView(R.id.layout_net_error)
    RelativeLayout layout_net_error;
    @BindView(R.id.layout_net_unconnected)
    RelativeLayout layout_net_unconnected;
    @BindView(R.id.lv_collection)
    XListView lv_collection;

    private CustomLoadingDialog mDailyDialog;
    private boolean alreadyShow = false;
//    private EssayCheckImpl mEssayCheckImpl;
    private EssayCollectSingleAdapter mSingleAdapter;
    private EssayCollectMultiAdapter mMultiAdapter;
    protected ArrayList<SingleExerciseResult> mSingleResult = new ArrayList<>();
    protected ArrayList<MultiExerciseResult> mMultiResult = new ArrayList<>();
    private int page = 1;
    private int type;
    private EssayExamImpl mEssayExamImpl;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_essay_collection_list_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit) {
//            ToastUtils.showEssayToast("交卷成功");
            prepareForRefresh();
            loadData(false);
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit_fail) {
//            ToastUtils.showEssayToast("交卷失败");
            prepareForRefresh();
            loadData(false);

        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave) {
//            ToastUtils.showEssayToast("保存成功");
            prepareForRefresh();
            loadData(false);
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave_fail) {
//            ToastUtils.showEssayToast("保存失败");
            prepareForRefresh();
            loadData(false);
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_deleteCollectEssay_success) {
//           ToastUtils.showEssayToast("移除成功");
            prepareForRefresh();
            loadData(false);
        }

        return true;
    }

    private void prepareForRefresh() {
        int position = FragmentPagerItem.getPosition(getArguments());
//        if (mEssayCheckImpl != null) {
//            mEssayCheckImpl.checkCountVerify(position);
//        }
        if (position == 1) {
            mMultiResult.clear();
        } else {
            mSingleResult.clear();
        }
        page = 1;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
//        int position = FragmentPagerItem.getPosition(getArguments());
        mEssayExamImpl = new EssayExamImpl(compositeSubscription);
//        mEssayCheckImpl = new EssayCheckImpl(compositeSubscription);
//        mEssayCheckImpl.checkCountVerify(position);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initListener();
        prepareForRefresh();
        loadData(true);
    }

    private void initListener() {
        layout_net_error.setOnClickListener(this);
        layout_net_unconnected.setOnClickListener(this);
        layout_net_error.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_net_error:
                layout_net_error.setVisibility(View.GONE);
                loadData(false);
                break;
            case R.id.layout_net_unconnected:
                layout_net_unconnected.setVisibility(View.GONE);
                loadData(false);
                break;
            case R.id.layout_nodata:
//                layout_nodata.setVisibility(View.GONE);
//                loadData(false);
                break;
        }
    }

    private void loadData(boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            lv_collection.stopRefresh();
            lv_collection.stopLoadMore();
            layout_net_unconnected.setVisibility(View.VISIBLE);
            lv_collection.setVisibility(View.GONE);
            layout_net_error.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.GONE);
            ToastUtils.showEssayToast("网络未连接，请连网后点击屏幕重试");
            return;
        } else {
            layout_net_unconnected.setVisibility(View.GONE);
            lv_collection.setVisibility(View.VISIBLE);

        }
        int position = FragmentPagerItem.getPosition(getArguments());
        type = position;
        if (isRefresh && position == 0 && !alreadyShow) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLoadingDialog();
                    alreadyShow = true;
                }
            });
        }
        if (position == 0) {
            lv_collection.setAdapter(mSingleAdapter);
            loadSingleData(0);
        } else if (position == 2) {
            lv_collection.setAdapter(mSingleAdapter);
            loadSingleData(2);
        } else if (position == 1) {
            lv_collection.setAdapter(mMultiAdapter);
            loadMultiData();
        }
    }

    private void loadMultiData() {
        ServiceProvider.getEssayCollectMultiList(compositeSubscription, 1, page, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissLoadingDialog();
                MultiExerciseData mData = (MultiExerciseData) model.data;
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                lv_collection.setVisibility(View.VISIBLE);
                lv_collection.stopLoadMore();
                lv_collection.stopRefresh();
                if (mData != null) {
                    mMultiResult.addAll(mData.result);
                }
                if (mMultiResult.isEmpty()) {
                    layout_nodata.setVisibility(View.VISIBLE);
                } else {
                    layout_nodata.setVisibility(View.GONE);
                    lv_collection.setVisibility(View.VISIBLE);
                }

                if (mData.next == 1) {
                    page++;
                    lv_collection.setPullLoadEnable(true);
                } else {
                    lv_collection.setPullLoadEnable(false);
                }
                mMultiAdapter.setDataAndNotify(mMultiResult);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissLoadingDialog();
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                lv_collection.stopLoadMore();
                lv_collection.stopRefresh();
                lv_collection.setVisibility(View.GONE);
                layout_net_error.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
                layout_net_unconnected.setVisibility(View.GONE);
            }
        });
    }

    private void loadSingleData(final int type) {
        ServiceProvider.getEssayCollectSingleList(compositeSubscription, type, page, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissLoadingDialog();
                if (model.code == 1000000 && model != null) {
                    lv_collection.setVisibility(View.VISIBLE);
                    SingleExerciseData mData = (SingleExerciseData) model.data;
                    if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                        return;
                    }
                    lv_collection.stopLoadMore();
                    lv_collection.stopRefresh();
                    if (mData != null) {
                        mSingleResult.addAll(mData.result);
                    }
                    if (mSingleResult.isEmpty()) {
                        layout_nodata.setVisibility(View.VISIBLE);
                    } else {
                        layout_nodata.setVisibility(View.GONE);
                        lv_collection.setVisibility(View.VISIBLE);
                    }

                    if (mData.next == 1) {
                        page++;
                        lv_collection.setPullLoadEnable(true);
                    } else {
                        lv_collection.setPullLoadEnable(false);
                    }
                    mSingleAdapter.setDataAndNotify(mSingleResult, type);
                } else {
                    layout_net_error.setVisibility(View.VISIBLE);
                    layout_nodata.setVisibility(View.GONE);
                    layout_net_unconnected.setVisibility(View.GONE);
                    ToastUtils.showEssayToast("网络出错了，请稍后点击屏幕重试");
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissLoadingDialog();
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                lv_collection.stopLoadMore();
                lv_collection.stopRefresh();
                lv_collection.setVisibility(View.GONE);
                layout_net_error.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
                layout_net_unconnected.setVisibility(View.GONE);
                ToastUtils.showEssayToast("网络出错了，请稍后点击屏幕重试");
            }
        });
    }


    private void initView() {
        mSingleAdapter = new EssayCollectSingleAdapter(mActivity, mEssayExamImpl);
        mMultiAdapter = new EssayCollectMultiAdapter(mActivity, mEssayExamImpl);
        lv_collection.setHeaderDividersEnabled(false);
        lv_collection.setFooterViewVisible(false);
        lv_collection.setPullLoadEnable(false);
        lv_collection.setPullRefreshEnable(true);
        lv_collection.setXListViewListener(this);
    }

    private void showLoadingDialog() {
        if (mDailyDialog == null) {
            mDailyDialog = new CustomLoadingDialog(mActivity);

        }
        mDailyDialog.show();

    }

    public void dismissLoadingDialog() {
        try {
            if (mDailyDialog != null) {
                mDailyDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AlertDialog  Exception:", e.getMessage() + "");
        }

    }

    @Override
    public void onRefresh() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            lv_collection.stopRefresh();
            return;
        }
        prepareForRefresh();
        layout_nodata.setVisibility(View.GONE);
        loadData(false);
    }

    @Override
    public void onLoadMore() {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            lv_collection.stopLoadMore();
            return;
        }
        loadData(false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
