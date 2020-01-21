package com.huatu.handheld_huatu.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.handheld_huatu.view.XListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by saiyuan on 2016/11/24.
 */

public abstract class BaseListViewActivity<T extends Serializable> extends BaseActivity
        implements BaseListResponseView, XListView.IXListViewListener {
    @BindView(R.id.common_list_view_toolbar_id)
    protected TopActionBar topActionBar;
    @BindView(R.id.common_list_view_bottom_button_layout_id)
    protected LinearLayout layoutButtons;
    @BindView(R.id.base_list_view_id)
    protected XListView listView;
    protected CommonAdapter<T> mAdapter;
//    @BindView(R.id.common_list_view_error_layout)
    protected CommonErrorView layoutErrorView;
    protected RelativeLayout layoutEmptyError;

    protected final ArrayList<T> dataList = new ArrayList<>();
    protected int pageSize = 10;
    protected boolean isPageDivided = false;//是否分页

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_base_list_view_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        initAdapter();
        if(mAdapter != null) {
            listView.setAdapter(mAdapter);
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
        listView.setHeaderDividersEnabled(true);
        if(isBottomButtons()) {
            layoutButtons.addView(getBottomLayout());
            layoutButtons.setVisibility(View.VISIBLE);
        } else {
            layoutButtons.setVisibility(View.GONE);
        }
        listView.setPullRefreshEnable(true);
        listView.setPullLoadEnable(false);
        listView.setXListViewListener(this);
        if(hasToolbar()) {
            initToolBar();
            topActionBar.setVisibility(View.VISIBLE);
        } else {
            topActionBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(outState == null) {
            outState = new Bundle();
        }
        outState.putSerializable("list_data", dataList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            ArrayList<T> tmpList = (ArrayList<T>) savedInstanceState.getSerializable("list_data");
            if(tmpList != null) {
                dataList.clear();
                dataList.addAll(tmpList);
            }
            mAdapter.setDataAndNotify(dataList);
        }
    }

    public abstract void initAdapter();
    public abstract boolean isBottomButtons();
    public abstract View getBottomLayout();
    public abstract boolean hasToolbar();
    public abstract void initToolBar();

    @UiThread
    @Override
    public void onSuccess(List list, boolean isRefresh) {
        if(Method.isActivityFinished(this)) {
            return;
        }
        dismissProgressBar();
        listView.stopLoadMore();
        listView.stopRefresh();
        if(isRefresh) {
            dataList.clear();
            listView.setSelection(0);
        }
        dataList.addAll(list);
        if(dataList.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
        if(!isPageDivided || list.isEmpty() || list.size() < pageSize) {
            listView.setPullLoadEnable(false);
        } else {
            listView.setPullLoadEnable(true);
        }
        mAdapter.setDataAndNotify(dataList);
    }

    @UiThread
    @Override
    public void onLoadDataFailed() {
        if(Method.isActivityFinished(this)) {
            return;
        }
        dismissProgressBar();
        listView.stopLoadMore();
        listView.stopRefresh();
        if(dataList.isEmpty()) {
            showErrorView();
        } else {
            hideEmptyView();
        }
        mAdapter.setDataAndNotify(dataList);
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
        setEmptyViewLp();
        listView.addHeaderView(layoutEmptyError);
        listView.setPullRefreshEnable(false);
    }

    public void showEmptyView() {
        layoutErrorView.setErrorText("什么都没有");
        layoutErrorView.setVisibility(View.VISIBLE);
        layoutErrorView.setErrorImage(R.drawable.no_data_bg);
        layoutErrorView.setErrorImageVisible(true);
        layoutErrorView.setOnReloadButtonListener(null);
        setEmptyViewLp();
        listView.addHeaderView(layoutEmptyError);
        listView.setPullRefreshEnable(true);
    }

    public void hideEmptyView() {
        listView.removeHeaderView(layoutEmptyError);
        listView.setPullRefreshEnable(true);
    }

    private void setEmptyViewLp() {
        int listHeight = listView.getHeight();
        int headerHeight = 0;
        for(int i = 0; i < listView.getHeaderViewsCount(); i++) {
            View view = listView.getChildAt(i);
            if(view != null && (view.getId() != layoutEmptyError.getId())) {
                headerHeight += view.getHeight();
            }
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
                layoutErrorView.getLayoutParams();
        lp.height = listHeight - headerHeight;
        layoutErrorView.setLayoutParams(lp);
    }

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
