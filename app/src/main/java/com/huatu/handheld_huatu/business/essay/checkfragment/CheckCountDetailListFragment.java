package com.huatu.handheld_huatu.business.essay.checkfragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.adapter.CheckCountDetailListAdapter;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountDetail;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountDetailBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorViewExsc;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public class CheckCountDetailListFragment extends BaseFragment implements IonLoadMoreListener {
    @BindView(R.id.top_bar)
    TopActionBar top_bar;
    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView prv_count_detail_list;

    @BindView(R.id.view_no_data)
    CommonErrorViewExsc viewNoData;                         // 没有数据

    private List<CheckCountDetailBean> countDetailData=new ArrayList<>();
    private int page=1;
    private int type;
    private CheckCountDetailListAdapter mAdapter;
    private CompositeSubscription mCompositeSubscription;

    public enum RefreshMode {
        /**
         * 重设数据
         */
        reset,
        /**
         * 拉取更多
         */
        loadmore,
        /**
         * 刷新最新
         */
        refresh
    }
    private RefreshMode loadMode = RefreshMode.reset;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_check_count_detail_list;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        mCompositeSubscription= RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        initTopBar();
        if (args!=null){
            type=args.getInt("goodsType");
        }
        initRecyclerView();
        setListener();
    }
    private void initTopBar() {
        top_bar.setTitle("申论批改次数详情");
        top_bar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    private void initRecyclerView() {
        mAdapter=new CheckCountDetailListAdapter(mActivity);
        prv_count_detail_list.getRefreshableView().setPagesize(20);
        prv_count_detail_list.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(getActivity()));
        prv_count_detail_list.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        prv_count_detail_list.getRefreshableView().setRecyclerAdapter(mAdapter);

    }
    protected void setListener() {
        prv_count_detail_list.getRefreshableView().setOnLoadMoreListener(this);
        prv_count_detail_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
        viewNoData.setOnReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击刷新数据
                superOnRefresh();
            }
        });

    }

    private void superOnRefresh() {
        loadMode=RefreshMode.refresh;
        try {
            if( prv_count_detail_list.getRefreshableView()!=null){
                prv_count_detail_list.getRefreshableView().reset();
            }
            else {
                return;
            }
        }catch (Exception e){
            return;
        }
        page=1;
        countDetailData.clear();
        loadCountDetail(true);
    }

    @Override
    protected void onLoadData() {
        loadCountDetail(false);
    }

    private void loadCountDetail(boolean isRefresh) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请连接网络后点击屏幕重试");
            showEmptyView(1);
            return;
        }
        if (!isRefresh){
            mActivity.showProgress();
        }
        ServiceProvider.getCheckCountDetailList(mCompositeSubscription,type,page,new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                prv_count_detail_list.onRefreshComplete();
                if (model!=null&&model.data!=null){
                    CheckCountDetail data= (CheckCountDetail) model.data;
                    if (data.result!=null){
                    countDetailData.addAll(data.result);
                    if (loadMode == RefreshMode.loadmore && prv_count_detail_list.getRefreshableView() != null) {
                        prv_count_detail_list.getRefreshableView().checkloadMore(data.result.size());
                        prv_count_detail_list.getRefreshableView().hideloading();
                    }
                }
                }
                if (countDetailData!=null&&countDetailData.size()!=0){
                    mAdapter.setData(countDetailData);
                }else {
                    showEmptyView(3);
                }
                prv_count_detail_list.getRefreshableView().hideloading();
                if (page == 1) {
                    // 下拉刷新后显示第一行
                    prv_count_detail_list.getRefreshableView().scrollToPosition(0);
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
                prv_count_detail_list.onRefreshComplete();
                prv_count_detail_list.getRefreshableView().hideloading();
                showEmptyView(2);
            }
        });
    }
    private void showEmptyView(int type) {
        viewNoData.setVisibility(View.VISIBLE);
        switch (type) {
            case 1:
                viewNoData.setErrorText("网络未连接，联网后重试！");
                break;
            case 2:
                viewNoData.setErrorText("网络错误，请点击重试！");
                break;
            case 3:
                viewNoData.setErrorText("暂无相关数据！");
                break;
        }
    }

    @Override
    public void OnLoadMoreEvent(boolean isRetry) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            prv_count_detail_list.onRefreshComplete();
            return;
        }
        page++;
        loadMode =RefreshMode.loadmore;
        loadCountDetail(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }
}
