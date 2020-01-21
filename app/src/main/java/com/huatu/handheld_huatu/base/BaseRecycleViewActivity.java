package com.huatu.handheld_huatu.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.handheld_huatu.view.XListView;
import com.huatu.library.PullToRefreshBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by saiyuan on 2016/11/24.
 */

public abstract class BaseRecycleViewActivity<T extends Serializable> extends BaseActivity implements BaseListResponseView, XListView.IXListViewListener {

    @BindView(R.id.common_list_view_toolbar_id)
    protected TopActionBar topActionBar;
    @BindView(R.id.common_list_view_bottom_button_layout_id)
    protected LinearLayout layoutButtons;
    @BindView(R.id.rl_tip)
    protected RelativeLayout rl_tip;

    // 一个可以刷新自动加载的RecycleView
    // 还可以判断是不是到最后，根据每页加载的条数，和此次加载的条数对比，来判断是不是到最后一页了
    @BindView(R.id.xi_comm_page_list)
    protected PullRefreshRecyclerView listView;
    protected RecyclerView.Adapter mAdapter;
    //    @BindView(R.id.common_list_view_error_layout)
    protected CommonErrorView layoutErrorView;
    protected RelativeLayout layoutEmptyError;

    protected final ArrayList<T> dataList = new ArrayList<>();
    protected int pageSize = 10;
    protected boolean isPageDivided = false;//是否分页

    @Override
    public int onSetRootViewId() {
        return R.layout.base_recycleview_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        if (hasToolbar()) {
            initToolBar();
            topActionBar.setVisibility(View.VISIBLE);
        } else {
            topActionBar.setVisibility(View.GONE);
        }
        initAdapter();
        showTips();
        if (rl_tip != null) {
            rl_tip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rl_tip.setVisibility(View.GONE);
                }
            });
        }
        if (mAdapter != null) {
            // 设置Adapter
            listView.getRefreshableView().setRecyclerAdapter(mAdapter);
        } else {
            LogUtils.e("You must set adapter");
            finish();
            return;
        }

        layoutErrorView = new CommonErrorView(UniApplicationContext.getContext());
        layoutEmptyError = new RelativeLayout(UniApplicationContext.getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutEmptyError.addView(layoutErrorView, lp);
        if (isBottomButtons()) {
            layoutButtons.addView(getBottomLayout());
            layoutButtons.setVisibility(View.VISIBLE);
        } else {
            layoutButtons.setVisibility(View.GONE);
        }
//        listView.setPullRefreshEnable(true);
//        listView.setPullLoadEnable(false);
//        listView.setXListViewListener(this);

        // 设置每页加载的条数，判断是否是最后一页
        listView.getRefreshableView().setPagesize(pageSize);
        // 分割线
//        listView.getRefreshableView().addItemDecoration(new DividerItemDecoration(BaseRecycleViewActivity.this, LinearLayoutManager.VERTICAL));
        listView.getRefreshableView().setLayoutManager(new LinearLayoutManager(BaseRecycleViewActivity.this));
        // 自动加载更多的回调
        listView.getRefreshableView().setOnLoadMoreListener(new IonLoadMoreListener() {
            @Override
            public void OnLoadMoreEvent(boolean isRetry) {
                BaseRecycleViewActivity.this.onLoadMore();
            }
        });
        // 加载过程中是否可以滑动
        listView.setPullToRefreshOverScrollEnabled(true);
        // 下拉刷新的回调
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                BaseRecycleViewActivity.this.onRefresh();

            }
        });

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        outState.putSerializable("list_data", dataList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<T> tmpList = (ArrayList<T>) savedInstanceState.getSerializable("list_data");
            if (tmpList != null) {
                dataList.clear();
                dataList.addAll(tmpList);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    public abstract void initAdapter();

    public void showTips() {
    }

    public boolean isBottomButtons() {
        return false;
    }

    public View getBottomLayout() {
        return null;
    }

    public abstract boolean hasToolbar();

    public abstract void initToolBar();

    @UiThread
    @Override
    public void onSuccess(List list, boolean isRefresh) {
        if (Method.isActivityFinished(this)) {
            return;
        }
        dismissProgressBar();
        // 完成加载
        listView.onRefreshComplete();
        if (isRefresh) {
            // 重设底线
            listView.getRefreshableView().reset();
            dataList.clear();
        }
        dataList.addAll(list);
        if (dataList.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
        // 判断是不是最后一页
        listView.getRefreshableView().checkloadMore(list != null ? list.size() : 0);
        // 隐藏加载动画
        listView.getRefreshableView().hideloading();
        mAdapter.notifyDataSetChanged();
        if (isRefresh) {
            // 下拉刷新后显示第一行
            listView.getRefreshableView().scrollToPosition(0);
        }
    }

    @UiThread
    @Override
    public void onLoadDataFailed() {
        if (Method.isActivityFinished(this)) {
            return;
        }
        dismissProgressBar();
        listView.onRefreshComplete();
        if (dataList.isEmpty()) {
            if (rl_tip != null) {
                rl_tip.setVisibility(View.GONE);
            }
            showErrorView();
        } else {
            hideEmptyView();
        }
        mAdapter.notifyDataSetChanged();
    }

    public void showErrorView() {
        layoutErrorView.updateUI();
        layoutErrorView.setVisibility(View.VISIBLE);
        layoutErrorView.setOnReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadData();
            }
        });
//        setEmptyViewLp();
//        listView.addHeaderView(layoutEmptyError);
//        listView.setPullRefreshEnable(false);
    }

    public void showEmptyView() {
        layoutErrorView.setErrorText("什么都没有");
        layoutErrorView.setVisibility(View.VISIBLE);
        layoutErrorView.setErrorImage(R.drawable.no_data_bg);
        layoutErrorView.setErrorImageVisible(true);
        layoutErrorView.setOnReloadButtonListener(null);
//        setEmptyViewLp();
//        listView.addHeaderView(layoutEmptyError);
//        listView.setPullRefreshEnable(true);
    }

    public void hideEmptyView() {
//        listView.removeHeaderView(layoutEmptyError);
//        listView.setPullRefreshEnable(true);
    }

//    private void setEmptyViewLp() {
//        int listHeight = listView.getHeight();
//        int headerHeight = 0;
//        for (int i = 0; i < listView.getHeaderViewsCount(); i++) {
//            View view = listView.getChildAt(i);
//            if (view != null && (view.getId() != layoutEmptyError.getId())) {
//                headerHeight += view.getHeight();
//            }
//        }
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
//                layoutErrorView.getLayoutParams();
//        lp.height = listHeight - headerHeight;
//        layoutErrorView.setLayoutParams(lp);
//    }

    @Override
    public void showProgressBar() {
        showProgress();
    }

    @Override
    public void dismissProgressBar() {
        hideProgress();
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
    public void onSetData(Object respData) {

    }
}


