package com.huatu.handheld_huatu.business.matchsmall.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.newtips.NewTipsManager;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matchsmall.adapter.MatchAdapter;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.event.SectionalExaminationEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.SmallMatchBean;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.StageBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 小模考/阶段性测试 首页
 */
public class SmallMatchActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_report)
    TextView tvReport;
    @BindView(R.id.tv_class_past)
    TextView tvClassPast;

    @BindView(R.id.sl_layout)
    SwipeRefreshLayout refreshLayout;       // 下拉刷新
    @BindView(R.id.rv_content)
    RecyclerView rvContent;                 // 列表

    @BindView(R.id.view_err)
    CommonErrorView errorView;              // 空白页面

    private int requestType;                // 请求类型

    private MatchAdapter adapter;

    private ArrayList<SmallMatchBean> datas;

    private long paperId;                   // 阶段性测试，需要paperId
    private long syllabusId;                // 阶段性测试，需要大纲Id

    private int position;                   // 为了刷新阶段测试列表
    private boolean isRefresh = false;      // 是否发送了消息刷新阶段测试列表
    private Bundle extraArgs;

    private boolean toHome;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_small_match;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(SmallMatchActivity.this);

        requestType = originIntent.getIntExtra("request_type", 0);
        extraArgs = originIntent.getBundleExtra("extra_args");
        if (extraArgs == null) {
            extraArgs = new Bundle();
        }
        toHome = extraArgs.getBoolean("toHome", false);
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST) {      // 阶段性测试，需要使用paperId请求
            tvTitle.setText("阶段测评");
            tvReport.setVisibility(View.GONE);
            tvClassPast.setVisibility(View.GONE);
            extraArgs = originIntent.getBundleExtra("extra_args");
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            paperId = extraArgs.getLong("paperId");
            syllabusId = extraArgs.getLong("syllabusId");
            position = extraArgs.getInt("position", -1);
        }

        datas = new ArrayList<>();
        adapter = new MatchAdapter(SmallMatchActivity.this, datas, requestType, new MatchAdapter.OnItemClickListener() {
            @Override
            public void onClick(int type, int position) {
                switch (type) {
                    case MatchAdapter.OnItemClickListener.GO_ANSWER:        // 去答题、继续答题
                        if (datas == null || position >= datas.size()) return;
                        SmallMatchBean smallMatchBean = datas.get(position);
                        if (smallMatchBean.status == 6) {            // 查看报告
                            extraArgs.putLong("practice_id", smallMatchBean.practiceId);          // 把答题卡id传过去，到那边请求报告
                            if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH) {
                                // 小模考报告
                                SmallMatchReportActivity.show(SmallMatchActivity.this, requestType, extraArgs);
                            } else {
                                // 阶段测试报告需要大纲Id
                                extraArgs.putLong("syllabusId", syllabusId);
                                // 阶段测试报告页面
                                StageReportActivity.show(SmallMatchActivity.this, requestType, extraArgs);
                            }
                        } else {                                     // 去答题
                            extraArgs.putLong("point_ids", smallMatchBean.paperId);
                            if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST) {             // 阶段考试开始考试
                                // 阶段测试开始考试需要大纲Id
                                extraArgs.putLong("syllabusId", syllabusId);
                                // 埋点 阶段测试开始考试
                                String course_id = extraArgs.getString("course_id");
                                if (!StringUtils.isEmpty(course_id)) {
                                    String course_title = extraArgs.getString("course_title");
                                    String class_id = extraArgs.getString("class_id");
                                    String class_title = extraArgs.getString("class_title");
                                    StudyCourseStatistic.stageStart(course_id, course_title, class_id, class_title, class_id, class_title);
                                }

                            } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH) {     // 小模考去考试
                                StudyCourseStatistic.startSmallMatch(Long.toString(smallMatchBean.paperId), smallMatchBean.name);
                            }
                            ArenaExamActivityNew.show(SmallMatchActivity.this, requestType, extraArgs);
                        }
                        break;
                    case MatchAdapter.OnItemClickListener.CLASS:            // 查看课程
                        goClass(position);
                        break;
                }
            }
        });
        rvContent.setLayoutManager(new LinearLayoutManager(SmallMatchActivity.this));
        rvContent.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                loadData();
            }
        });
    }

    @Override
    protected void onLoadData() {

    }

    private void loadData() {
        if (!NetUtil.isConnected()) {
            showError(0);
            ToastUtils.showMessage("网络未连接，请检查您的网络设置");
            return;
        }
        showProgress();
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH) {        // 小模考
            getSMatchData();
        } else {                                                                    // 阶段性测试
            getStageData();
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_report, R.id.tv_class_past, R.id.view_err})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_report:
                Intent intent = new Intent(SmallMatchActivity.this, SmallReportListActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_class_past:
                startActivity(new Intent(SmallMatchActivity.this, SmallMatchClassActivity.class));
                break;
            case R.id.view_err:
                loadData();
                break;
        }
    }

    /**
     * 小模考获取数据
     */
    private void getSMatchData() {
        ServiceProvider.getSmallMatch(compositeSubscription, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                showError(1);
                if (e instanceof ApiException) {
                    ToastUtils.showMessage(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtils.showMessage(e.getMessage());
                }
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                if (model.data != null && model.data.size() > 0) {
                    hideError();
                    datas.clear();
                    datas.addAll(model.data);
                    adapter.notifyDataSetChanged();
                    // 修改首页角标
                    NewTipsManager.newInstance().setTipsGone(SpUtils.getUserCatgory(), SpUtils.getUserSubject(), ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH);
                } else {
                    showError(3);
                }
            }
        });
    }

    /**
     * 阶段性测试获取数据
     */
    private void getStageData() {
        ServiceProvider.getStageTest(compositeSubscription, paperId, syllabusId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                showError(1);
                if (e instanceof ApiException) {
                    ToastUtils.showMessage(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtils.showMessage(e.getMessage());
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                if (model.data != null) {
                    if (model.data instanceof StageBean) {
                        hideError();
                        StageBean bean = (StageBean) model.data;
                        bean.startTime = bean.onlineTime;
                        bean.endTime = bean.offlineTime;
                        bean.limitTime = bean.time;
                        bean.joinCount = bean.submitCount;

                        datas.clear();
                        datas.add(bean);
                        adapter.notifyDataSetChanged();

                        // 1未开始2开始考试5继续考试6查看报告
                        PrefStore.putSettingInt(ArgConstant.KEY_ID + syllabusId, bean.status);

                        // 5、继续考试 6、查看报告
                        if ((bean.status == 5 || bean.status == 6) && !isRefresh && position >= 0) {
                            if (bean.status == 6) {         // 继续作答可以发多次event，作答完成只能发送一次进行删除item
                                isRefresh = true;
                            }
                            // status 5: 继续作答,  6: 作答完成
                            // position: 传过去的值，传回来
                            Bundle bundle = new Bundle();

                            bundle.putInt("position", position);
                            bundle.putInt("status", bean.status);

                            EventBus.getDefault().post(new SectionalExaminationEvent(bundle));
                        }

                    } else {
                        showError(2);
                    }
                } else {
                    showError(2);
                }
            }
        });
    }


    /**
     * 去查看课程
     */
    private void goClass(int position) {
        if (datas == null || position > datas.size() - 1) {
            ToastUtils.showMessage("还没准备好数据");
            return;
        }
        final Intent intent = new Intent(SmallMatchActivity.this, BaseIntroActivity.class);
        intent.putExtra("from", requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST ? "app阶段测试" : "小模考当期解析课");
        intent.putExtra("NetClassId", datas.get(position).courseId + "");
        startActivity(intent);
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("datas", datas);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<SmallMatchBean> saveDatas = (ArrayList<SmallMatchBean>) savedInstanceState.getSerializable("datas");
        if (saveDatas == null || saveDatas.size() == 0) {
            loadData();
        } else {
            if (datas != null) {
                datas.addAll(saveDatas);
                adapter.notifyDataSetChanged();
            } else {
                loadData();
            }
        }
    }

    private void hideError() {
        hideProgress();
        errorView.setVisibility(View.GONE);
    }

    private void showError(int type) {
        hideProgress();
        errorView.setVisibility(View.VISIBLE);
        errorView.setErrorImageVisible(true);
        switch (type) {
            case 0:                         // 无网络
                errorView.setErrorImage(R.drawable.no_server_service);
                errorView.setErrorText("无网络，点击重试");
                break;
            case 1:                         // 无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("获取数据是失败，点击重试");
                break;
            case 2:                         // 阶段性测试无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("无试题数据");
                break;
            case 3:                         // 小模考无数据
                errorView.setErrorImage(R.mipmap.s_match_rest);
                errorView.setErrorText("");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (toHome) {
            MainTabActivity.newIntent(SmallMatchActivity.this);
        }
        super.onBackPressed();
    }

    /**
     * 小模考跳转
     * SmallMatchActivity.show(this, ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH, new Bundle());
     * <p>
     * 阶段性测试跳转
     * Bundle bundle = new Bundle();
     * bundle.putLong("paperId", paperId);
     * SmallMatchActivity.show(this, ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST, bundle);
     */
    public static void show(Context context, int requestType, Bundle args) {
        Intent intent = new Intent(context, SmallMatchActivity.class);
        intent.putExtra("request_type", requestType);
        intent.putExtra("extra_args", args);
        context.startActivity(intent);
    }

    public static void showForResult(Activity context, int requestType, Bundle args) {
        Intent intent = new Intent(context, SmallMatchActivity.class);
        intent.putExtra("request_type", requestType);
        intent.putExtra("extra_args", args);
        context.startActivityForResult(intent, 2003);
    }
}
