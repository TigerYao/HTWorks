package com.huatu.handheld_huatu.business.play.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.adapter.CourseJudgeAdapter;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeItemBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.XListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subscriptions.CompositeSubscription;

/**
 * 单个老师评价页
 */

public class TecherCommentActivity extends Activity implements XListView.IXListViewListener, View.OnClickListener {
    private final String TAG = "httpTecherCommentActivity";
    @BindView(R.id.techer_comment_back)
    ImageView backImage;
    @BindView(R.id.techer_comment_title)
    TextView title;
    @BindView(R.id.comment_count)
    TextView comment_count;
    @BindView(R.id.comment_listview)
    XListView mListView;
    @BindView(R.id.no_outline)
    View mEmptyView;
    @BindView(R.id.no_network_layout)
    View mNoNetWorkView;
    @BindView(R.id.no_content_name)
    TextView mEmptyTipView;
    private CourseJudgeAdapter judgeAdapter;
    protected CompositeSubscription compositeSubscription;
    private Unbinder unbinder;
    private String teacherName;
    private String nickName;
    private String teacherId;
    private String classId;
    private String lessonId;
    private int count;
    private CustomLoadingDialog progressDlg;
    private int commentPage = 1;
    private int pageSize = 20;
    private List<CourseTeacherJudgeItemBean> judgeList = new ArrayList<>();
    private CourseTeacherJudgeBean judgeBean;

    public static void startCommentActivity(Context ctx, String teacherId, String classId, String lessionId) {
        Intent intent = new Intent(ctx, TecherCommentActivity.class);
        intent.putExtra("teacherid", teacherId);
        intent.putExtra("classId", classId);
        intent.putExtra("lessionId", lessionId);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_techer_comment);
        unbinder = ButterKnife.bind(this);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        teacherName = getIntent().getStringExtra("teachername");
        nickName = getIntent().getStringExtra("nickname");
        teacherId = getIntent().getStringExtra("teacherid");
        classId = getIntent().getStringExtra("classId");
        lessonId = getIntent().getStringExtra("lessionId");
        count = getIntent().getIntExtra("count", 0);
        LogUtils.d(TAG, "teacher name is :" + teacherName);
        LogUtils.d(TAG, "teacher id is : " + teacherId);
        LogUtils.d(TAG, "commnent number is : " + count);
        initView();
        initData();
    }

    private void initView() {
//        if(TextUtils.isEmpty(nickName)) {
//            title.setText(teacherName+"的学员评价");
//        } else {
//            title.setText("“"+nickName+"”的学员评价");
//        }
        comment_count.setText("(" + count + ")");
        mListView.setXListViewListener(this);
        backImage.setClickable(true);
        backImage.setOnClickListener(this);
        mListView.setVisibility(View.VISIBLE);
        mEmptyView.setOnClickListener(this);
        mNoNetWorkView.setOnClickListener(this);
    }

    private void initData() {
        showProgress();
        NetResponse response = new NetResponse() {
            @Override
            public void onError(Throwable e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgess();
                        LogUtils.d(TAG, "getCourseTeacherJudgeList onError");
                        mListView.stopRefresh();
                        mListView.stopLoadMore();
                        if (judgeAdapter == null || judgeAdapter.getCount() == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                            mEmptyTipView.setText("列表加载失败，点击重试");
                        }
                    }
                });
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                judgeBean = (CourseTeacherJudgeBean) model.data;
                hideProgess();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.stopRefresh();
                        mListView.stopLoadMore();
                        refreshJudgeView();
                        if (judgeBean.total < pageSize)
                            mListView.setLoadEndInfo();
                        LogUtils.d(TAG, "getCourseTeacherJudgeList onSuccess");
                        if (judgeAdapter == null || judgeAdapter.getCount() == 0 && judgeBean.total == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                            mEmptyTipView.setText("暂时没有评价内容，稍后重试");
                        }else if(mEmptyTipView.getVisibility() == View.VISIBLE)
                            mEmptyTipView.setVisibility(View.GONE);
                    }
                });

            }
        };
        if (CommonUtils.isEmpty(classId) || CommonUtils.isEmpty(lessonId))
            ServiceProvider.getCourseTeacherJudgeList(compositeSubscription, teacherId, commentPage, pageSize, response);
        else
            ServiceProvider.getLessonEvaluates(compositeSubscription, teacherId, classId, lessonId, commentPage, pageSize, response);

    }

    private void refreshJudgeView() {
        LogUtils.d(TAG, "refreshJudgeView,judgeBean.data.size() is :" + judgeBean.data.size());
        commentPage++;
        LogUtils.d(TAG, "commentPage is : " + commentPage + " ;last page is : " + judgeBean.last_page);
        for (int j = 0; j < judgeBean.data.size(); j++) {
            judgeList.add(judgeBean.data.get(j));
        }
        judgeAdapter = new CourseJudgeAdapter();
        mListView.setAdapter(judgeAdapter);
        judgeAdapter.setDataAndNotify(judgeList);
//        mListView.setHeaderDividersEnabled(false);
//        mListView.setFooterViewVisible(false);
        if (commentPage > judgeBean.last_page) {
            mListView.setLoadEndInfo();
        } else {
            mListView.setPullLoadEnable(true);
            mListView.setPullRefreshEnable(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    public void showProgress() {
        if (!Method.isActivityFinished(this)) {
            if (progressDlg == null) {
                progressDlg = new CustomLoadingDialog(this);
            }
            progressDlg.show();
        }
    }

    public void hideProgess() {
        if (!Method.isActivityFinished(this)) {
            progressDlg.dismiss();
        }
    }

    @Override
    public void onRefresh() {
        LogUtils.d(TAG, "xlistview onRefresh...");
        loadData();
    }

    @Override
    public void onLoadMore() {
        LogUtils.d(TAG, "xlistview onLoadMore...");
        loadData();
    }

    private void loadData() {
        LogUtils.d(TAG, "loadData, commentPage is :" + commentPage + " ; last page is :" + judgeBean.last_page);
        showProgress();
        if (judgeBean == null || judgeAdapter.getCount() < judgeBean.total) {
            NetResponse response = new NetResponse() {
                @Override
                public void onError(Throwable e) {
                    hideProgess();
                    LogUtils.d(TAG, "getCourseTeacherJudgeList onError");
                    mListView.stopRefresh();
                    mListView.stopLoadMore();
                    if (judgeAdapter == null || judgeAdapter.getCount() == 0) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mEmptyTipView.setText("列表加载失败，点击重试");
                    }
                }

                @Override
                public void onSuccess(BaseResponseModel model) {
                    hideProgess();
                    judgeBean = (CourseTeacherJudgeBean) model.data;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListView.stopRefresh();
                            mListView.stopLoadMore();
                            refreshAllJudgeView();
                            if(mEmptyTipView.getVisibility() == View.VISIBLE)
                                mEmptyTipView.setVisibility(View.GONE);

                        }
                    });
                    LogUtils.d(TAG, "getCourseTeacherJudgeList onSuccess");
                }
            };
            if (CommonUtils.isEmpty(classId) || CommonUtils.isEmpty(lessonId))
                ServiceProvider.getCourseTeacherJudgeList(compositeSubscription, teacherId, commentPage, pageSize, response);
            else
                ServiceProvider.getLessonEvaluates(compositeSubscription, teacherId, classId, lessonId, commentPage, pageSize, response);
        } else {
            hideProgess();
            mListView.stopLoadMore();
            mListView.stopRefresh();
            mListView.setLoadEndInfo();
        }
    }

    private void refreshAllJudgeView() {
        commentPage++;
        judgeList.addAll(judgeBean.data);
        judgeAdapter.setDataAndNotify(judgeList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.techer_comment_back:
                finish();
                break;
            case R.id.no_outline:
                v.setVisibility(View.GONE);
                initData();
                break;
            case R.id.no_network_layout:
                v.setVisibility(View.GONE);
                initData();
                break;
        }
    }
}
