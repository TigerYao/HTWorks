package com.huatu.handheld_huatu.business.lessons;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.course.CourseListAdapter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.business.lessons.bean.CourseListData;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.mvpmodel.CourseListResponse;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.countdown.CountDownTask;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ServerTimeUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.ArrayUtils;
import com.netease.hearttouch.router.HTRouter;

import java.util.ArrayList;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2018/2/23.
 * https://blog.csdn.net/harvic880925/article/details/44679239
 * Uri详解之——Uri结构与代码提取
 * 合集列表
 */
@HTRouter(url = {"ztk://course/collection{fragment}"}, needLogin = false)
public class CourseCollectSubsetFragment extends ABaseListFragment<CourseListResponse> {
    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView prv_course_list;

    private TextView tv_title;
    private ImageView iv_back;
    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    private int mCategoryId = 0;// 最终考试Id
    private CompositeSubscription mCompositeSubscription;
    private CourseListAdapter mListAdapter;
    private String classID;
    private String title;
    private String pageSource;

    private boolean mTohome = false;

    public static void show(Context context, String rid, String shortTitle, String title, boolean toHome, String... pageSource) {
        Bundle arg = new Bundle();
        arg.putString("classId", rid);
        arg.putString("shortTitle", shortTitle);
        arg.putString("title", title);
        arg.putBoolean("toHome", toHome);
        if (pageSource != null && pageSource.length > 0)
            arg.putString("pageSource", pageSource[0]);
        UIJumpHelper.jumpFragment(context, CourseCollectSubsetFragment.class, arg);
    }

    public static void show(Context context, String rid, String shortTitle, String title, int courseType, String... pageSource) {
        Bundle arg = new Bundle();
        arg.putString("classId", rid);
        arg.putString("shortTitle", shortTitle);
        arg.putString("title", title);
        arg.putBoolean("toHome", false);
        if (pageSource != null && pageSource.length > 0)
            arg.putString("pageSource", pageSource[0]);
        arg.putInt(ArgConstant.TYPE, courseType);
        UIJumpHelper.jumpFragment(context, CourseCollectSubsetFragment.class, arg);
    }

    @Override
    protected RecyclerViewEx getListView() {
        return prv_course_list.getRefreshableView();
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        mListResponse = new CourseListResponse();
        mListResponse.mCourseList = new ArrayList<>();
        mListAdapter = new CourseListAdapter(getContext(), mListResponse.mCourseList);
        mListAdapter.setPageSource(pageSource);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mTohome) {
                MainTabActivity.newIntent(getActivity());
            }
            getActivity().finish();
            return true;
        }
        return false;
    }

    @Override
    public boolean attachTitleBar(LayoutInflater inflater, ViewGroup container) {
        if (getArguments() != null) {
            String uriPath = getArguments().getString(ArgConstant.URI_PATH);
            if (!TextUtils.isEmpty(uriPath)) {
                Uri uriInfo = Uri.parse(uriPath);
                classID = uriInfo.getQueryParameter("classId");
                if (TextUtils.isEmpty(classID)) {
                    classID = uriInfo.getQueryParameter("rid");
                }
                title = uriInfo.getQueryParameter("shortTitle");
                if (TextUtils.isEmpty(title)) {
                    title = uriInfo.getQueryParameter("title");
                }
                pageSource = uriInfo.getQueryParameter("from");
                mTohome = "1".equals(uriInfo.getQueryParameter("toHome"));
            } else {
                classID = getArguments().getString("classId");
                title = getArguments().getString("shortTitle");
                pageSource = getArguments().getString("pageSource");
                mTohome = getArguments().getBoolean("toHome");
            }

        }
        final View topView = inflater.inflate(R.layout.top_bar_more_course, container, false);
        container.addView(topView);
        iv_back = topView.findViewById(R.id.iv_back);
        tv_title = topView.findViewById(R.id.tv_title_bar);
        if (title != null) {
            tv_title.setText(title);
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTohome) {
                    MainTabActivity.newIntent(getActivity());
                }
                getActivity().finish();
            }
        });
        return true;

    }

    @Override
    protected int getLimit() {
        return 10;
    }

    @Override
    protected void onRefreshCompleted() {
        if (null != prv_course_list) prv_course_list.onRefreshComplete();
    }


    @Override
    protected void onLoadData(int currentPage, int limit) {
        CourseApiService.getApi().getCollectCourseList(classID, currentPage, limit).enqueue(getCallback());
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
    public void onSuccess(CourseListResponse response) {
        if (isCurrentReMode()) {
            if (null != mListAdapter)
                mListAdapter.clearCountDownTask();
        }
        if (!ArrayUtils.isEmpty(response.getListResponse())) {
            long beginTime = CountDownTask.elapsedRealtime();//毫秒级
            for (CourseListData curLesson : response.getListResponse()) {
                curLesson.isCollect = false;

                // 根据服务器时间，计算剩余时间（这里服务器上cdn后，改成用开始时间，而不是剩余时间了）
                if (curLesson.startTimeStamp != 0) {
                    long startR = (curLesson.startTimeStamp * 1000 - ServerTimeUtil.newInstance().getServerTime()) / 1000;
                    curLesson.lSaleStart = startR > 0 ? startR : 0;
                } else {
                    curLesson.lSaleStart = Method.parseLong(curLesson.saleStart);
                }
                if (curLesson.stopTimeStamp != 0) {
                    long endR = (curLesson.stopTimeStamp * 1000 - ServerTimeUtil.newInstance().getServerTime()) / 1000;
                    curLesson.lSaleEnd = endR > 0 ? endR : 0;
                } else {
                    curLesson.lSaleEnd = Method.parseLong(curLesson.saleEnd);
                }

                if (curLesson.lSaleStart > 0) {
                    curLesson.lSaleStart = beginTime + curLesson.lSaleStart * 1000;
                }
                if (curLesson.lSaleEnd > 0) {
                    curLesson.lSaleEnd = beginTime + curLesson.lSaleEnd * 1000;
                }
            }
        }
        super.onSuccess(response);
    }

    @Override
    public void showEmpty() {
        if (isCurrentReMode()) {
            mListAdapter.clearAndRefresh();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        } else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_course_list_layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mListAdapter)
            mListAdapter.clearCountDownTask();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }
}
