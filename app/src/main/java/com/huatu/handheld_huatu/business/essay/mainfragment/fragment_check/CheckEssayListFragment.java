package com.huatu.handheld_huatu.business.essay.mainfragment.fragment_check;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.adapter.CheckListAdapter;
import com.huatu.handheld_huatu.business.essay.bean.MyCheckData;
import com.huatu.handheld_huatu.business.essay.bean.MyCheckResult;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.swiperecyclerview.XRecyclerView;
import com.huatu.handheld_huatu.view.swiperecyclerview.swipemenu.SwipeMenuRecyclerView;
import com.ogaclejapan.v4.FragmentPagerItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by ht-ldc on 2018/7/10.
 * 申论批改列表奥
 */

public class CheckEssayListFragment extends BaseFragment implements XRecyclerView.LoadingListener, View.OnClickListener {
    @BindView(R.id.layout_nodata)
    RelativeLayout layout_nodata;
    @BindView(R.id.layout_net_error)
    RelativeLayout layout_net_error;
    @BindView(R.id.layout_net_unconnected)
    RelativeLayout layout_net_unconnected;
    @BindView(R.id.lv_my_check)
    SwipeMenuRecyclerView lv_my_check;

    private CheckListAdapter adapter;

    private CustomLoadingDialog mDailyDialog;
    private ArrayList<MyCheckResult> dataList = new ArrayList<>();
    int type; //0:单题   1：试卷 2:文章写作
    private int mPage = 1;
    private boolean alreadyShow = false;
    private EssayExamImpl mEssayExamImpl;
    private int position;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit) {
            dataList.clear();
            mPage = 1;
            loadData(false);
        }else if (event.type == EssayExamMessageEvent.EssayExam_CONVERT_CORRECT_MODE) {
            //智能转人工后刷新页面
            dataList.clear();
            mPage = 1;
            loadData(false);
        }
        return true;

    }

    @Override
    public int onSetRootViewId() {
        return R.layout.check_exam_list_efragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        position = FragmentPagerItem.getPosition(getArguments());
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        mEssayExamImpl = new EssayExamImpl(compositeSubscription);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initListener();
        initView();
        dataList.clear();
        mPage = 1;
        loadData(true);
    }

    private void initListener() {
        layout_nodata.setOnClickListener(this);
        layout_net_unconnected.setOnClickListener(this);
        layout_net_error.setOnClickListener(this);
    }

    private void loadData(boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            lv_my_check.refreshComplete();
            lv_my_check.loadMoreComplete();
            layout_net_unconnected.setVisibility(View.VISIBLE);
            lv_my_check.setVisibility(View.GONE);
            layout_net_error.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.GONE);
            ToastUtils.showEssayToast("网络未连接，请连网后点击屏幕重试");
            return;
        } else {
            layout_net_unconnected.setVisibility(View.GONE);
            lv_my_check.setVisibility(View.VISIBLE);

        }
        type = position;
        if (isRefresh && position == 0 && !alreadyShow) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLoadingDialog();
                }
            });
            alreadyShow = true;
        }
        if (position == 1) {
            loadMultiData();
        } else {
            loadSingleData();
        }
    }

    private void loadSingleData() {
        ServiceProvider.getMySingleCheck(compositeSubscription, type, mPage, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissLoadingDialog();
                layout_net_error.setVisibility(View.GONE);
                lv_my_check.setVisibility(View.VISIBLE);
                MyCheckData mData = (MyCheckData) model.data;
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                lv_my_check.refreshComplete();
                lv_my_check.loadMoreComplete();
                if (mData.result != null) {
                    dataList.addAll(mData.result);
                }
                if (dataList.isEmpty()) {
                    layout_nodata.setVisibility(View.VISIBLE);
//                    lv_my_check.setVisibility(View.GONE);
                } else {
                    lv_my_check.setVisibility(View.VISIBLE);
                    layout_nodata.setVisibility(View.GONE);
                }
                adapter.setData(dataList);
                if (mData.next == 1) {
                    mPage++;
                    lv_my_check.setLoadingMoreEnabled(true);
                } else {
                    lv_my_check.setLoadingMoreEnabled(false);
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                dismissLoadingDialog();
                lv_my_check.refreshComplete();
                lv_my_check.loadMoreComplete();
                lv_my_check.setVisibility(View.GONE);
                layout_net_error.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
                lv_my_check.setVisibility(View.GONE);
                layout_net_unconnected.setVisibility(View.GONE);

            }
        });
    }

    private void loadMultiData() {
        ServiceProvider.getMyCheck(compositeSubscription, type, mPage, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissLoadingDialog();
                lv_my_check.setVisibility(View.VISIBLE);
                layout_net_error.setVisibility(View.GONE);
                MyCheckData mData = (MyCheckData) model.data;
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                lv_my_check.refreshComplete();
                lv_my_check.loadMoreComplete();
                if (mData.result != null) {
                    dataList.addAll(mData.result);
                }
                if (dataList.isEmpty()) {
                    layout_nodata.setVisibility(View.VISIBLE);
                    lv_my_check.setVisibility(View.GONE);
                } else {
                    lv_my_check.setVisibility(View.VISIBLE);
                    layout_nodata.setVisibility(View.GONE);
                }
                adapter.setData(dataList);
                if (mData.next == 1) {
                    mPage++;
                    lv_my_check.setLoadingMoreEnabled(true);
                } else {
                    lv_my_check.setLoadingMoreEnabled(false);
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (Method.isActivityFinished(mActivity) || !isAdded() || isDetached()) {
                    return;
                }
                dismissLoadingDialog();
                lv_my_check.refreshComplete();
                lv_my_check.loadMoreComplete();
                lv_my_check.setVisibility(View.GONE);
                layout_net_error.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
                layout_net_unconnected.setVisibility(View.GONE);
                ToastUtils.showEssayToast("网络出错了，请稍后点击屏幕重试");
            }
        });
    }

    private void initView() {
        lv_my_check = (SwipeMenuRecyclerView) rootView.findViewById(R.id.lv_my_check);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_my_check.setLayoutManager(layoutManager);
        lv_my_check.setPullRefreshEnabled(true);
        lv_my_check.setLoadingMoreEnabled(true);
        lv_my_check.setLoadingListener(this);
        lv_my_check.setSwipeDirection(SwipeMenuRecyclerView.DIRECTION_LEFT);//左滑（默认）
        adapter = new CheckListAdapter(mActivity, compositeSubscription, mEssayExamImpl, position);
        lv_my_check.setAdapter(adapter);
    }


    @Override
    public void onRefresh() {
        layout_nodata.setVisibility(View.GONE);
        dataList.clear();
        mPage = 1;
        loadData(false);

    }

    @Override
    public void onLoadMore() {
        loadData(false);
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
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
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
}
