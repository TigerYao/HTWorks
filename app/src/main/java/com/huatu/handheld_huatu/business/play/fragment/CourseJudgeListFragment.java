package com.huatu.handheld_huatu.business.play.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.adapter.CourseJudgeAdapter;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeItemBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.XListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * 课程评价页
 */

public class CourseJudgeListFragment extends Fragment implements XListView.IXListViewListener{
    private final String TAG = "httpCourseJudgeListFragment";
    private String courseId;
    private int isLive;
    private int curPage;
    @BindView(R.id.course_judge_top)
    LinearLayout topShow;
    @BindView(R.id.course_judge_count)
    TextView judgeCount;
    @BindView(R.id.course_judge_list)
    XListView mListView;
    @BindView(R.id.course_judge_zero)
    protected RelativeLayout noCommnetLayout;
    private CourseTeacherJudgeBean judgeBean;
    private List<CourseTeacherJudgeItemBean> mList = new ArrayList<CourseTeacherJudgeItemBean>();
    private boolean hasSetNumber = false;
    private CompositeSubscription compositeSubscription;
    private View rootView;
    private LayoutInflater mLayoutInflater;
    private XiaoNengHomeActivity mActivity;
    private Bundle args;
    private CourseJudgeAdapter mAdapter;

    public static CourseJudgeListFragment getInstance(String classID,int courseTYPE) {
        Bundle ids=new Bundle();
        ids.putString("course_id",classID);
        ids.putInt("course_type",courseTYPE);
        CourseJudgeListFragment tempCourseJudgeListFragment = new CourseJudgeListFragment();
        tempCourseJudgeListFragment.setArguments(ids);
        return tempCourseJudgeListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG,"onCreate...");
        this.mActivity = (XiaoNengHomeActivity) getActivity();
        args = getArguments();
        if (args != null) {
            courseId = args.getString("course_id");
            isLive = args.getInt("course_type");
        }
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.v(this.getClass().getName() + " onCreateView()");
        mLayoutInflater = inflater;
        rootView = mLayoutInflater.inflate(R.layout.fragment_course_judge_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();
        getData(true);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
        LogUtils.v(this.getClass().getName() + " onDestroy()");
        LogUtils.d(TAG,"onDestroy...");
    }

    public void initAdapter() {
        mAdapter = new CourseJudgeAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setXListViewListener(this);
    }



    @Override
    public void onRefresh() {
        getData(false);
        LogUtils.d(TAG,"onrefresh...");
    }

    @Override
    public void onLoadMore() {
        LogUtils.d(TAG,"onLoadMore...");
        getData(false);
    }


    protected void onLoadData() {
        onRefresh();
    }

    private void getData(final boolean isRefresh) {
//        mActivity.showProgress();
        if(isRefresh) {
            curPage = 1;
        } else {
            ++curPage;
        }
        LogUtils.d(TAG,"curPage is : "+curPage);
        if (curPage == 1 || curPage <= judgeBean.last_page) {
            ServiceProvider.getCourseJudgeList(compositeSubscription,
                courseId, isLive, curPage, 20, new NetResponse() {
                    @Override
                    public void onError(Throwable e) {
//                        mActivity.hideProgress();
                        LogUtils.d(TAG,"onError...");
                        mListView.stopLoadMore();
                        mListView.stopRefresh();
                        mListView.setPullLoadEnable(false);
                        if (curPage == 1) {
                            topShow.setVisibility(View.GONE);
//                            judgeCount.setText("("+0+")");
                            mListView.setVisibility(View.GONE);
                            noCommnetLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onSuccess(BaseResponseModel model) {
//                        mActivity.hideProgress();
                        judgeBean = (CourseTeacherJudgeBean) model.data;
                        LogUtils.d(TAG,"onSuccess");
                        if (!hasSetNumber) {
                            judgeCount.setText("("+judgeBean.total+")");
                            hasSetNumber = true;
                        }
                        if (judgeBean.data.size() == 0 && curPage == 1) {
                            topShow.setVisibility(View.GONE);
//                            judgeCount.setText("("+0+")");
                            mListView.setVisibility(View.GONE);
                            noCommnetLayout.setVisibility(View.VISIBLE);
                        } else {
                            topShow.setVisibility(View.VISIBLE);
                            noCommnetLayout.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                            mListView.stopLoadMore();
                            mListView.stopRefresh();
                            if(isRefresh) {
                                mList.clear();
                                mList.addAll(judgeBean.data);
//                                mListView.setSelection(0);
                            } else {
                                mList.addAll(judgeBean.data);
                            }
                            mAdapter.setDataAndNotify(mList);
                            if (judgeBean.next == 1) {
                                mListView.setPullLoadEnable(true);
                            } else {
                                mListView.setPullLoadEnable(false);
                            }
                        }
                    }
            });
        } else {
//            mActivity.hideProgress();
            mListView.stopLoadMore();
            mListView.stopRefresh();
        }
    }
}
