package com.huatu.handheld_huatu.business.main;

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
import com.huatu.handheld_huatu.adapter.ExperienceCourseAdapter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.mvpmodel.AllCourseListResponse;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;
import com.netease.hearttouch.router.HTRouter;

import java.util.ArrayList;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2019/2/25.
 * 精品微课（喜闻）
 */
@HTRouter(url = {"ztk://exquisite/small/course{fragment}"}, needLogin = false)
public class ExperienceCourseFragment extends ABaseListFragment<AllCourseListResponse> {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView prv_course_list;
    private TextView tv_title;
    private ImageView iv_back;
    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    private ExperienceCourseAdapter mAdapter;
    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onPrepare() {
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_date);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);
        prv_course_list.getRefreshableView().setPagesize(getLimit());
        prv_course_list.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(getActivity()));
        prv_course_list.getRefreshableView().setRecyclerAdapter(mAdapter);
        prv_course_list.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected RecyclerViewEx getListView() {
        return prv_course_list.getRefreshableView();
    }

    @Override
    protected int getLimit() {
        return 30;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        mListResponse = new AllCourseListResponse();
        mListResponse.mLessionlist = new ArrayList<>();
        mAdapter = new ExperienceCourseAdapter(getContext(), mListResponse.mLessionlist);
    }

    @Override
    public boolean attachTitleBar(LayoutInflater inflater, ViewGroup container) {
        final View topView = inflater.inflate(R.layout.top_bar_more_course, container, false);
        container.addView(topView);
        iv_back = topView.findViewById(R.id.iv_back);
        tv_title = topView.findViewById(R.id.tv_title_bar);
        tv_title.setText("喜闻");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return true;

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

    private void superOnRefresh() {
        super.onRefresh();

    }

    @Override
    public void requestData() {
        super.requestData();
        onFirstLoad();
    }


    @Override
    protected void onLoadData(int pageIndex, int limit) {
        //与课程列表共用一个接口，科目id写死为2001
        CourseApiService.getApi().getAllCourses(2001).enqueue(getCallback());

    }

    @Override
    protected void onRefreshCompleted() {
        if (null != prv_course_list) prv_course_list.onRefreshComplete();
    }

    @Override
    public void onSuccess(AllCourseListResponse response) {
        super.onSuccess(response);

    }

    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            if (mAdapter.getItemCount() <= 0) {
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
            mAdapter.clearAndRefresh();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        } else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }

    public static void launch(Context mContext) {
        UIJumpHelper.jumpFragment(mContext, ExperienceCourseFragment.class, null);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_experience_course;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }
}
