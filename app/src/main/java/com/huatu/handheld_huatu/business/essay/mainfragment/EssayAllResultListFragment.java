package com.huatu.handheld_huatu.business.essay.mainfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.essay.adapter.EssaySearchContentAdapter;
import com.huatu.handheld_huatu.business.essay.bean.EssaySearchListResponse;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;

import java.util.ArrayList;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2018/12/15.
 */

public class EssayAllResultListFragment extends ABaseListFragment<EssaySearchListResponse> {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView prv_course_list;

    private TextView tv_title;
    private ImageView iv_back;
    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    private int mCategoryId = 0;
    private CompositeSubscription mCompositeSubscription;
    private EssaySearchContentAdapter mListAdapter;
    private int typeId;
    private String title;
    private String keyWords;

    @Override
    protected int getContentView() {
        return R.layout.fragment_course_list_layout;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return prv_course_list.getRefreshableView();
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        mListResponse = new EssaySearchListResponse();
        mListResponse.mCourseList = new ArrayList<>();
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        mListAdapter = new EssaySearchContentAdapter(getContext(), mCompositeSubscription, mListResponse.mCourseList, typeId);
    }

    @Override
    protected void setListener() {
        super.setListener();
        prv_course_list.getRefreshableView().setOnLoadMoreListener(this);
        prv_course_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });

    }

    @Override
    public void requestData() {
        super.requestData();
        onFirstLoad();
    }


    private void superOnRefresh() {
        super.onRefresh();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        mCategoryId = SpUtils.getSelectedLiveCategory();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_date);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);
        prv_course_list.getRefreshableView().setPagesize(getLimit());
        prv_course_list.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(getActivity()));
        prv_course_list.getRefreshableView().setRecyclerAdapter(mListAdapter);

        prv_course_list.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
//        prv_course_list.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 0.6f)));

    }

    @Override
    public boolean attachTitleBar(LayoutInflater inflater, ViewGroup container) {
        if (getArguments() != null) {
            typeId = getArguments().getInt(ArgConstant.TYPE_ID);
            title = getArguments().getString(ArgConstant.TITLE);
            keyWords = getArguments().getString(ArgConstant.KEYWORDS);
        }
        final View topView = inflater.inflate(R.layout.top_bar_more_course, container, false);
        container.addView(topView);
        iv_back = topView.findViewById(R.id.iv_back);
        tv_title = topView.findViewById(R.id.tv_title_bar);
        if (title != null) {
            tv_title.setText(title + "全部搜索结果");
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return true;

    }

    @Override
    protected int getLimit() {
        return 20;
    }

    @Override
    protected void onRefreshCompleted() {
        if (null != prv_course_list) prv_course_list.onRefreshComplete();
    }


    @Override
    protected void onLoadData(int currentPage, int limit) {
        CourseApiService.getApi().getEssaySearchList(keyWords, typeId, currentPage, limit).enqueue(getCallback());
        mListAdapter.setCurrentPage(currentPage);
    }

    @Override
    public void onSuccess(EssaySearchListResponse response) {
        super.onSuccess(response);
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
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        } else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }

    public static void launch(Context mContext, int typeId, String title, String keyWords) {
        Bundle arg = new Bundle();
        arg.putInt(ArgConstant.TYPE_ID, typeId);
        arg.putString(ArgConstant.TITLE, title);
        arg.putString(ArgConstant.KEYWORDS, keyWords);
        UIJumpHelper.jumpFragment(mContext, EssayAllResultListFragment.class, arg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }
}
