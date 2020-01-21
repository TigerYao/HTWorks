package com.huatu.handheld_huatu.business.play.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.adapter.CourseOutLineExpandAdapter;
import com.huatu.handheld_huatu.business.play.bean.CourseOutlineBean;
import com.huatu.handheld_huatu.business.play.bean.CourseOutlineItemBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.recyclerview.LoadingFooter;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;

import java.util.List;

import butterknife.BindView;

/**
 * Created by zyw,课程大纲页
 */
public class CourseOutlineFragment extends BaseFragment {
    private final String TAG = "httpCourseOutlineFragment";
    private String classId;
    private boolean isBuy = false;
    private CourseOutlineBean mCourseOutlineBean;
    private int page = 1;
    private int childPage = 1;
    private int pageSize = 20;
    //    private int parentId = 0;
    private Bundle args;
    private XiaoNengHomeActivity mActivity;
    private CourseOutLineExpandAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private DividerItemDecoration dividerItemDecoration;
    boolean isAutoLoadMoreEnd = false;//是否加载完成

    @BindView(R.id.course_outline_pullrefresh_view)
    RecyclerView mPullRefreshRecyclerView;
    @BindView(R.id.no_outline)
    RelativeLayout no_outline;
    @BindView(R.id.no_network_layout)
    RelativeLayout no_network_layout;
    @BindView(R.id.no_outlin_text)
    TextView mEmptyTv;
    @BindView(R.id.no_outlin_image)
    ImageView mEmptyImg;

    private List<CourseOutlineItemBean> tryListenerDatas;
    private boolean mIsChanged = true;

    public static CourseOutlineFragment getInstance(String classID, boolean isBuy) {
        Bundle ids = new Bundle();
        ids.putString("course_id", classID);
        ids.putBoolean("isBuy", isBuy);
        CourseOutlineFragment tempCourseOutlineFragment = new CourseOutlineFragment();
        tempCourseOutlineFragment.setArguments(ids);
        return tempCourseOutlineFragment;
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_course_outline;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate...");
        mActivity = (XiaoNengHomeActivity) getActivity();
        args = getArguments();
        if (args != null) {
            classId = args.getString("course_id");
            LogUtils.d(TAG, "classid is : " + classId);
            isBuy = args.getBoolean("isBuy", false);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.d(TAG, "onViewCreated...classId == " + classId);
        mPullRefreshRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mAdapter != null && mCourseOutlineBean.next == 1 && isAutoLoadMoreEnd && dy > 0) {
                    int lastPosition = ((LinearLayoutManager) mPullRefreshRecyclerView.getLayoutManager()).findLastVisibleItemPosition();

                    if (lastPosition == mAdapter.getItemCount() - 1) {
                        isAutoLoadMoreEnd = false;
                        page += 1;
                        getData();
                        mAdapter.setShowFootView(true, LoadingFooter.State.Loading);
                    }
                }
            }
        });
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (isResumed() && isVisibleToUser && mIsChanged)
            initLoadData();
    }

    public void reLoadData(String classId) {
        mAdapter = null;
        page = 1;
        mCourseOutlineBean = null;
        mIsChanged = true;
        this.classId = classId;
    }

    public void initLoadData() {
        if (NetUtil.isConnected()) {
            getData();
        } else {
            showErrorView(0);
        }
    }

    public void addTryListenerItem(List<CourseOutlineItemBean> tryListenerDatas) {
        this.tryListenerDatas = tryListenerDatas;
        if (mCourseOutlineBean == null || mCourseOutlineBean.list == null || page > 1)
            return;
        if (mCourseOutlineBean.list.get(0).type == 2)
            return;
        if (tryListenerDatas != null && !tryListenerDatas.isEmpty() && !mCourseOutlineBean.list.get(0).title.equals("试听单元")) {
            CourseOutlineItemBean bean = new CourseOutlineItemBean();
            bean.type = 1;
            bean.hasChildren = 1;
            bean.title = "试听单元";
            bean.isExpand = true;
            bean.childList = tryListenerDatas;
            mCourseOutlineBean.list.add(0, bean);
            if (mAdapter != null && mAdapter.getItemCount() > 0) {
                bean.isExpand = false;
                mAdapter.getList().add(0, bean);
                mAdapter.expandGroup(0);
            }
        }
    }


    private void getData() {
        isAutoLoadMoreEnd = false;
        if (page == 1)
            showLoadingView(true);
        NetResponse netResponse = new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showLoadingView(false);
                LogUtils.d("httpCourseOutlineFragment", "onerror : " + e.toString());
                if (mAdapter != null && mAdapter.getItemCount() > 0)
                    return;
                showErrorView(1);
                e.printStackTrace();
                isAutoLoadMoreEnd = true;
                if (mAdapter != null)
                    mAdapter.setShowFootView(false, LoadingFooter.State.NetWorkError);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                showLoadingView(false);
                if (mAdapter != null)
                    mAdapter.setShowFootView(false, LoadingFooter.State.Normal);
                CourseOutlineBean tempBean = (CourseOutlineBean) model.data;
                if (tempBean == null || tempBean.list == null || tempBean.list.isEmpty()) {
                    showErrorView(2);
                } else {
                    no_network_layout.setVisibility(View.GONE);
                    no_outline.setVisibility(View.GONE);
                    mPullRefreshRecyclerView.setVisibility(View.VISIBLE);
                    if (page == 1) {
                        mCourseOutlineBean = tempBean;
                        addTryListenerItem(tryListenerDatas);
                    } else {
                        mCourseOutlineBean.next = tempBean.next;
                        mCourseOutlineBean.list.addAll(tempBean.list);
                        mAdapter.AddItems(tempBean.list);
                    }
                    LogUtils.d("httpCourseOutlineFragment", "onSuccess : " + mCourseOutlineBean.next);
                    refreshView();
                }
                mIsChanged = false;
            }
        };
        ServiceProvider.getOutlineDetail(getCompositeSubscription(), classId, page, pageSize, 0, 0, netResponse);
    }

    private void refreshView() {
        LogUtils.d("httpCourseOutlineFragment", "refreshView==" + (mAdapter == null));
        if (mCourseOutlineBean.list.isEmpty()) {
            mPullRefreshRecyclerView.setVisibility(View.GONE);
            no_outline.setVisibility(View.VISIBLE);
        } else if (mAdapter == null) {
            layoutManager = new LinearLayoutManager(mActivity);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            //设置RecyclerView 布局
            mPullRefreshRecyclerView.setLayoutManager(layoutManager);
            mAdapter = new CourseOutLineExpandAdapter(mActivity.getApplicationContext(), mCourseOutlineBean.list);
            mAdapter.setHasBuy(isBuy);
            mPullRefreshRecyclerView.setAdapter(mAdapter);
            if (dividerItemDecoration == null) {
                dividerItemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
                dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.onelayer_reclyview_divider, null));
            }
            mPullRefreshRecyclerView.removeItemDecoration(dividerItemDecoration);
            mPullRefreshRecyclerView.addItemDecoration(dividerItemDecoration);
            mAdapter.setExpandListener(new CourseOutLineExpandAdapter.OnExpandLayoutListener() {
                @Override
                public void onRequstChild(int position, CourseOutlineItemBean parentBean) {
                    childPage = 1;
                    getChildDatas(position, parentBean);
                }
            });
        }

        isAutoLoadMoreEnd = mCourseOutlineBean.next == 1;
        if (mCourseOutlineBean.next == 0) {
            mAdapter.setShowFootView(true, LoadingFooter.State.TheEnd);
        }
    }

    private void getChildDatas(final int postion, final CourseOutlineItemBean parentBean) {
        showLoadingView(true);
        NetResponse netResponse = new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showLoadingView(false);
                LogUtils.d("httpCourseOutlineFragment", "onerror : " + e.toString());
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                showLoadingView(false);
                CourseOutlineBean courseOutlineBean = (CourseOutlineBean) model.data;
                if (!parentBean.hasChilds())
                    parentBean.childList = courseOutlineBean.list;
                else
                    parentBean.childList.addAll(courseOutlineBean.list);
                if (courseOutlineBean.next == 1) {
                    childPage += 1;
                    getChildDatas(postion, parentBean);
                    return;
                }
                childPage = 1;
                mAdapter.expandGroup(postion);
                LogUtils.d(TAG, "adapter2 onSuccess mCourseOutlineBean ");
            }
        };
        ServiceProvider.getOutlineDetail(getCompositeSubscription(), classId, childPage, pageSize, parentBean.id, 0, netResponse);
    }

    private void showErrorView(int type) {
        if (type != 0 && !NetUtil.isConnected())
            type = 0;
        switch (type) {
            case 0://无网络
                no_outline.setVisibility(View.GONE);
                no_network_layout.setVisibility(View.VISIBLE);
                mPullRefreshRecyclerView.setVisibility(View.GONE);
                no_network_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetUtil.isConnected()) {
                            getData();
                        }
                    }
                });
                break;
            case 1: //加载失败
                mPullRefreshRecyclerView.setVisibility(View.GONE);
                no_outline.setVisibility(View.VISIBLE);
                no_outline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initLoadData();
                    }
                });
                mEmptyImg.setImageResource(R.drawable.img_network_error);
                mEmptyTv.setText("数据加载失败, 点击重试");
                break;
            case 2://空页面
                mPullRefreshRecyclerView.setVisibility(View.GONE);
                no_network_layout.setVisibility(View.GONE);
                no_outline.setVisibility(View.VISIBLE);
                mEmptyImg.setImageResource(R.drawable.nonet);
                mEmptyTv.setText("课程大纲还在维护中....");
                no_outline.setOnClickListener(null);
                break;

        }
    }

    private void showLoadingView(boolean show){
        if(mActivity == null)
            return;
        if(show && isResumed() && getUserVisibleHint())
            mActivity.showProgress();
        else if(!show)
            mActivity.hideProgess();
    }
}
