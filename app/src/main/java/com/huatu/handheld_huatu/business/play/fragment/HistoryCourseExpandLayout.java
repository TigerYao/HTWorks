package com.huatu.handheld_huatu.business.play.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.adapter.HistroyExpandAdapter;
import com.huatu.handheld_huatu.business.play.bean.HistoryCourseBean;
import com.huatu.handheld_huatu.business.play.bean.HistoryCourseInfo;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.swiperecyclerview.XRecyclerView;

import rx.subscriptions.CompositeSubscription;

public class HistoryCourseExpandLayout extends RelativeLayout {
    private XRecyclerView mRecyclerview;
    private View noContentView;
    private View noNetWorkView;
    private LinearLayoutManager linearLayoutManager;
    private HistroyExpandAdapter mHistoryCouseAdapter;
    private String teacherId;
    private CompositeSubscription compositeSubscription;
    private int coursePage = 1;
    private int pageSize = 4;
    private int lessonPage = 1;
    private HistoryCourseBean allCourses;
    public CustomLoadingDialog progressDlg;

    public HistoryCourseExpandLayout(@NonNull Context context) {
        this(context, null);
    }

    public HistoryCourseExpandLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryCourseExpandLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setRefreshAndLoad(boolean can) {
        mRecyclerview.setLoadingMoreEnabled(can);
        mRecyclerview.setRefreshing(can);
        mRecyclerview.setEnabledScroll(can);
        if (can) {
            mRecyclerview.setLoadingListener(loadingListener);
            mRecyclerview.setFootViewText("正在加载", "糟糕，看到了底线");
        }
    }

    public void loadData(CompositeSubscription compositeSubscription, String teacherId, int pageSize) {
        this.compositeSubscription = compositeSubscription;
        this.teacherId = teacherId;
        this.pageSize = pageSize;
        getCourseList();
    }

    public void displayLayout(){
        if (mHistoryCouseAdapter == null || mHistoryCouseAdapter.getItemCount() == 0)
            getCourseList();
    }

    public void showProgress() {
        if (!Method.isActivityFinished((Activity) getContext())) {
            if (progressDlg == null) {
                progressDlg = new CustomLoadingDialog((Activity) getContext());
            }
            progressDlg.show();
        }
    }

    public void hideProgess() {
        if (!Method.isActivityFinished((Activity) getContext()) && progressDlg != null) {
            progressDlg.dismiss();
        }
    }

    private void initView(Context ctx) {
        View.inflate(ctx, R.layout.layout_judge_course_list, this);
        mRecyclerview = findViewById(R.id.recyclerview);
        linearLayoutManager = new LinearLayoutManager(ctx);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        noContentView = findViewById(R.id.no_outline);
        noNetWorkView = findViewById(R.id.no_network_layout);
        noNetWorkView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getCourseList();
                view.setVisibility(GONE);
            }
        });
        noContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getCourseList();
                view.setVisibility(GONE);
            }
        });
    }

    private void initAdapter() {
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (noContentView.getVisibility() == View.VISIBLE)
                    noContentView.setVisibility(GONE);
                if (noNetWorkView.getVisibility() == View.VISIBLE)
                    noNetWorkView.setVisibility(GONE);
                hideProgess();
                if (mHistoryCouseAdapter == null) {
                    mHistoryCouseAdapter = new HistroyExpandAdapter(getContext(), allCourses);
                    mHistoryCouseAdapter.setOnItemClickListener(onItemClickListener);
                    mRecyclerview.setAdapter(mHistoryCouseAdapter);
                    mHistoryCouseAdapter.notifyDataSetChanged();
                }else
                    mHistoryCouseAdapter.notifyRecyclerViewData();
                if (mRecyclerview.isLoadingMoreEnabled()) {
                    mRecyclerview.refreshComplete();
                    mRecyclerview.loadMoreComplete();
                    mRecyclerview.setLoadingMoreEnabled(allCourses.current_page != allCourses.last_page);
                }
            }
        });

    }

    boolean isLoading;
    private void getCourseList() {
        if (isLoading) return;
        if (compositeSubscription == null && !NetUtil.isConnected()) {
            noContentView.setVisibility(GONE);
            noNetWorkView.setVisibility(VISIBLE);
            return;
        }
        isLoading = true;
        showProgress();
        ServiceProvider.getHistoryCourses(compositeSubscription, teacherId, coursePage, pageSize, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            hideProgess();
                            if (mRecyclerview.isLoadingMoreEnabled()) {
                                mRecyclerview.refreshComplete();
                                mRecyclerview.loadMoreComplete();
                            }
                            if (coursePage == 1) {
                                noContentView.setVisibility(VISIBLE);
                                noNetWorkView.setVisibility(GONE);
                            }
                            isLoading = false;
                        }catch (Exception e){}
                    }
                });

            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                HistoryCourseBean courses = (HistoryCourseBean) model.data;
                if (allCourses == null) {
                    allCourses = courses;
                } else if (coursePage > 1) {
                    allCourses.current_page = courses.current_page;
                    allCourses.data.addAll(courses.data);
                }
                initAdapter();
                isLoading = false;
            }
        });
    }

    private void getLessonList(final HistoryCourseInfo info, final int position) {
        showProgress();
        ServiceProvider.getHistoryLessons(compositeSubscription, teacherId, info.classId, lessonPage, pageSize, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                HistoryCourseBean bean = (HistoryCourseBean) model.data;
                if (bean.current_page == 1)
                    info.childDatas = bean.data;
                else
                    info.childDatas.addAll(bean.data);
                if (bean.current_page != bean.last_page){
                    lessonPage += 1;
                    getLessonList(info, position);
                    return;
                }
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mHistoryCouseAdapter.expandGroup(position);
                        mHistoryCouseAdapter.notifyRecyclerViewData();
                        hideProgess();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgess();
            }
        });
    }

    private String classId, lessionId;
    HistroyExpandAdapter.OnItemClickListener onItemClickListener = new HistroyExpandAdapter.OnItemClickListener() {

        @Override
        public void onGroupItemClick(HistoryCourseInfo info, int position, int groupPosition, View view) {
            classId = info.classId;
            if (info.hasChilds())
                return;
            getLessonList(allCourses.data.get(groupPosition), position);
        }

        @Override
        public void onChildItemClick(HistoryCourseInfo info, int position, View view) {
            lessionId = info.lessonId;
            TecherCommentActivity.startCommentActivity(getContext(), teacherId, classId, lessionId);

        }
    };

    XRecyclerView.LoadingListener loadingListener = new XRecyclerView.LoadingListener() {
        @Override
        public void onRefresh() {
            if (mRecyclerview != null) {
                mRecyclerview.refreshComplete();
                mRecyclerview.loadMoreComplete();
            }
        }

        @Override
        public void onLoadMore() {
            if (allCourses != null && allCourses.current_page != allCourses.last_page) {
                coursePage = allCourses.current_page + 1;
                getCourseList();
            } else {
                mRecyclerview.loadMoreComplete();
                mRecyclerview.refreshComplete();
                mRecyclerview.setLoadingMoreEnabled(false);
            }
        }
    };
}
