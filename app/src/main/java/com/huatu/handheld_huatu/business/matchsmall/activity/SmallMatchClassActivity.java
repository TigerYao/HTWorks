package com.huatu.handheld_huatu.business.matchsmall.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CourseListAdapter;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.business.lessons.bean.CourseListData;
import com.huatu.handheld_huatu.business.lessons.bean.SmallMatchClassBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.library.PullToRefreshBase;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

public class SmallMatchClassActivity extends BaseActivity {

    @BindView(R.id.mPullRefreshRecyclerView)
    PullRefreshRecyclerView mPullRefreshRecyclerView;       // 下拉刷新

    @BindView(R.id.view_err)
    CommonErrorView errorView;              // 错误、空白页面

    private CourseListAdapter adapter;



    //分页数据都会加到这个集合内
    private List<CourseListData> courseListData;

    //请求的页数
    private int page = 1;
    //每页的最大请求数量
    private final int pageSize = 20;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_small_match_class;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(SmallMatchClassActivity.this);

        initRecyclerView();

    }

    /**
     * 列表控件初始化
     */
    private void initRecyclerView() {
        // 设置每页加载的条数，判断是否是最后一页
        mPullRefreshRecyclerView.getRefreshableView().setPagesize(pageSize);
        mPullRefreshRecyclerView.getRefreshableView().setLayoutManager(new LinearLayoutManager(this));

        // 加载过程中是否可以滑动
        mPullRefreshRecyclerView.setPullToRefreshOverScrollEnabled(true);
        // 下拉刷新的回调
        mPullRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                //恢复页码数为1
                page = 1;
                //刷新的时候，清空所有数据
                courseListData.clear();
                onLoadData();
            }
        });
        // 自动加载更多的回调
        mPullRefreshRecyclerView.getRefreshableView().setOnLoadMoreListener(new IonLoadMoreListener() {
            @Override
            public void OnLoadMoreEvent(boolean isRetry) {
                onLoadData();
            }
        });

    }

    protected CompositeSubscription getSubscription() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        return compositeSubscription;
    }

    @Override
    protected void onLoadData() {

        showProgress();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getAnalysisClassList(page, pageSize),
                new NetObjResponse<SmallMatchClassBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<SmallMatchClassBean> model) {

                        hideProgress();
                        showList(model.data.courseListData);
                        mPullRefreshRecyclerView.onRefreshComplete();

                        // 隐藏加载动画
                        mPullRefreshRecyclerView.getRefreshableView().hideloading();
                    }

                    @Override
                    public void onError(String message, int type) {

                        hideProgress();
                        showError(type==0?2:(type==3?0:1));
                    }
                });

    }

    /**
     * 列表展示数据
     *
     * @param courseListData
     */
    private void showList(List<CourseListData> courseListData) {

        if (courseListData != null && courseListData.size() != 0) {

            page++;  //当请求的数据不为空时：页码数+1

            if (this.courseListData == null){
                this.courseListData = new ArrayList<>();
            }

            //所有请求回来的数据都会加到this.courseListData里
            this.courseListData.addAll(courseListData);

            if (adapter == null) {
                adapter = new CourseListAdapter(this, this.courseListData);
                mPullRefreshRecyclerView.getRefreshableView().setRecyclerAdapter(adapter);
                adapter.setPageSource("小模考往期解析课列表");
            }else{
                adapter.setAdapterData(this.courseListData);
                adapter.notifyDataSetChanged();
            }

        }else{
            if (page == 1){
                showError(2);
            }
        }

    }

    @OnClick({R.id.iv_back, R.id.view_err})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:              // 返回键
                finish();
                break;
            case R.id.view_err:             // 错误页面
                onLoadData();
                break;
            default:
                break;
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

    private void hideError() {
        hideProgress();
        errorView.setVisibility(View.GONE);
    }

    private void showError(int type) {
        hideProgress();
        mPullRefreshRecyclerView.onRefreshComplete();
        // 隐藏加载动画
        mPullRefreshRecyclerView.getRefreshableView().hideloading();
        errorView.setVisibility(View.VISIBLE);
        errorView.setErrorImageVisible(true);
        switch (type) {
            case 0:                         // 无网络
                errorView.setErrorImage(R.drawable.no_server_service);
                errorView.setErrorText("无网络，点击重试");
                break;
            case 1:                         // 无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("获取数据是失败，点击重试");
                break;
            case 2:                         // 无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("无数据");
                break;
        }
    }
}
