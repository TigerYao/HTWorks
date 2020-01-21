package com.huatu.handheld_huatu.business.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.CreamArticleListAdapter;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleListData;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleListResponse;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorViewExsc;
import com.huatu.library.PullToRefreshBase;

import java.util.ArrayList;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2019/2/19.
 */

public class CreamArticleListFragment extends BaseFragment implements IonLoadMoreListener {
    private static final String TAG = "CreamArticleListFragmen";
    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView prv_cream_article_list;

    @BindView(R.id.view_no_data)
    CommonErrorViewExsc viewNoData;                         // 没有数据
    private CreamArticleListAdapter mAdapter;
    public ArrayList<CreamArticleListData> mData=new ArrayList<>();
    private int type=1;
    private int page=1;
    private CreamArticleListResponse creamArticleListResponse;
    private CompositeSubscription mCompositeSubscription;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onPrepare();
        setListener();
        loadData(false);
    }



    protected void onPrepare() {
        if (args!=null){
            type=args.getInt("ID");
            LogUtils.i(TAG,"type is "+type);
        }
        page=1;
        if (mData!=null){
            mData.clear();
        }
        mAdapter=new CreamArticleListAdapter(mActivity);
        mCompositeSubscription=RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        prv_cream_article_list.getRefreshableView().setPagesize(10);
        prv_cream_article_list.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(getActivity()));
        prv_cream_article_list.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        prv_cream_article_list.getRefreshableView().setRecyclerAdapter(mAdapter);

    }


    protected void setListener() {
        prv_cream_article_list.getRefreshableView().setOnLoadMoreListener(this);
        prv_cream_article_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
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
        try {
            if( prv_cream_article_list.getRefreshableView()!=null){
                prv_cream_article_list.getRefreshableView().reset();
            }
            else {
                return;
            }
        }catch (Exception e){
            return;
        }
        page=1;
        mData.clear();
        loadData(true);
    }



    private void loadData(boolean isRefresh) {
        if (!isRefresh&&type==1){
            mActivity.showProgress();
        }
        ServiceProvider.getCreamArticleData(mCompositeSubscription,type,page,10,new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                onRefreshCompleted();
                mActivity.hideProgress();

                if (model!=null){
                    creamArticleListResponse= (CreamArticleListResponse) model.data;
                }
                if (creamArticleListResponse!=null){
                    mData.addAll(creamArticleListResponse.data);
                }
                if (mData!=null&&mData.size()!=0){
                    mAdapter.setData(mData);
                    viewNoData.setVisibility(View.GONE);
                }else {
                    showEmptyView(3);
                }
                if (creamArticleListResponse.current_page==creamArticleListResponse.last_page){
                    //没有下一页
                    prv_cream_article_list.getRefreshableView().checkloadMore(0);
                }
                prv_cream_article_list.getRefreshableView().hideloading();
                if (page == 1) {
                    // 下拉刷新后显示第一行
                    prv_cream_article_list.getRefreshableView().scrollToPosition(0);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                onRefreshCompleted();
                mActivity.hideProgress();
                showEmptyView(2);

            }
        });
    }


    protected void onRefreshCompleted(){
        if(null!=prv_cream_article_list) prv_cream_article_list.onRefreshComplete();
    }


    /**
     * 无数据
     * 1、网络不好，点击重试！
     * 3、往期模考试卷：暂无试卷
     */
    private void showEmptyView(int type) {
        if (viewNoData!=null){
            viewNoData.setVisibility(View.VISIBLE);
        }
        switch (type) {
            case 1:
                if (viewNoData!=null) {
                    viewNoData.setErrorText("网络不好，点击重试！");
                }
                break;
            case 2:
                if (viewNoData!=null){
                    viewNoData.setErrorText("网络错误，请点击重试！");
                }
                break;
            case 3:
                if (viewNoData!=null){
                    viewNoData.setErrorText("暂无数据！");
                }
                break;
        }
    }

    @Override
    public void OnLoadMoreEvent(boolean isRetry) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            prv_cream_article_list.onRefreshComplete();
            return;
        }
        page++;
        loadData(false);
    }


    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_cream_article_list_layout;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }


}
