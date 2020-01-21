package com.huatu.handheld_huatu.business.me.order;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.course.MyCourselistAdapter;

import com.huatu.handheld_huatu.adapter.order.OrderStatusListAdapter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.MyCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.MyOrderListResponse;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Administrator on 2018\7\12 0012.
 */

public class OrderStatusListFragment extends ABaseListFragment<MyOrderListResponse> implements OnRecItemClickListener, AStripTabsFragment.IStripTabInitData {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView myPeopleListView;

    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    OrderStatusListAdapter mListAdapter;

    @Override
    protected int getLimit() {
        return 10;
    }

    @Override
    public int getContentView() {
        return R.layout.comm_ptrlist_layout;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return myPeopleListView.getRefreshableView();
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        mListResponse = new MyOrderListResponse();
        mListResponse.mOrderlist = new ArrayList<>();
        mListAdapter = new OrderStatusListAdapter(getContext(), mListResponse.mOrderlist);
        //mListAdapter.setOnRecyclerViewItemClickListener(this);
    }

    private void superOnRefresh() {
        super.onRefresh();
    }

    @Override
    public void setListener() {

        myPeopleListView.getRefreshableView().setPagesize(getLimit());
        myPeopleListView.getRefreshableView().setOnLoadMoreListener(this);
        myPeopleListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_record_course);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);

        myPeopleListView.getRefreshableView().setRecyclerAdapter(mListAdapter);
        myPeopleListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        // myPeopleListView.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 10)));

    }

    @Override
    public void requestData() {
        super.requestData();
        onFirstLoad();
    }

    @Override
    public void onStripTabRequestData() {
        onFirstLoad();
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        //CourseApiService.getApi().getMyCourseList(0, offset).enqueue(getCallback());
    }

    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            if (mListAdapter.getItemCount() <= 0) {
                super.onError(throwable, type);
                // initNotify("网络加载出错~");
            } else {
                hideEmptyLayout();
                onRefreshCompleted();
                ToastUtils.showShortToast(UniApplicationContext.getContext(), "网络加载出错~");
            }
        }
    }

    @Override
    public void showEmpty() {
        if (isCurrentReMode()) {
            mListAdapter.clear();
            mListAdapter.notifyDataSetChanged();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        } else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }

    @Override
    protected void onRefreshCompleted() {
        if (null != myPeopleListView) myPeopleListView.onRefreshComplete();
    }

    @Override
    public void onItemClick(int position, View view, int type) {

    }

}
