package com.huatu.handheld_huatu.business.essay.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ManulReportFragment;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.essay.adapter.HomeworkSingleListAdapter;
import com.huatu.handheld_huatu.business.essay.bean.HomeworkSingleListBean;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 申论课后作业多道单题列表
 */
public class HomeworkSingleListActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_undo)
    TextView tvUndo;
    @BindView(R.id.err_view)
    CommonErrorView errorView;

    private ArrayList<HomeworkSingleListBean.HomeworkSingleBean> data;
    private HomeworkSingleListAdapter adapter;

    private int courseType;         // 课程类型 1、点播 2、直播 3、回放
    private long courseId;          // 课程Id
    private long courseWareId;      // 课件Id
    private long syllabusId;        // 大纲ID

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_single_homework_list;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEvent(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_net_paperSave                 // 保存试卷
                || event.type == EssayExamMessageEvent.EssayExam_net_paperCommit) {     // 提交成功
            onLoadData();
        }
        return true;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(HomeworkSingleListActivity.this);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        Bundle bundle = originIntent.getBundleExtra("extra_args");
        if (bundle == null) {
            bundle = new Bundle();
        }
        courseType = bundle.getInt("courseType", 0);
        courseId = bundle.getLong("courseId", 0);
        courseWareId = bundle.getLong("courseWareId", 0);
        syllabusId = bundle.getLong("syllabusId", 0);

        data = new ArrayList<>();
        adapter = new HomeworkSingleListAdapter(HomeworkSingleListActivity.this, data, new HomeworkSingleListAdapter.OnItemClickListener() {
            @Override
            public void onClick(final int position) {
                if (data == null || position >= data.size()) {
                    return;
                }
                final HomeworkSingleListBean.HomeworkSingleBean bean = data.get(position);
                switch (bean.bizStatus) {
                    case 0:     // 未开始（可做题）
                    case 1:     // 未提交
                        Bundle bundle01 = new Bundle();
                        bundle01.putString("areaName", bean.areaName);
                        bundle01.putLong("answerId", bean.questionAnswerId);
                        EssayRoute.goEssayHomeworkAnswer(HomeworkSingleListActivity.this,
                                true,
                                null,
                                bean.questionBaseId,
                                0,
                                courseType,
                                courseId,
                                courseWareId,
                                syllabusId,
                                bundle01);
                        break;
                    case 3:     // 已批改，跳转报告
                        Bundle bundle02 = new Bundle();

                        bundle02.putString("titleView", "");
                        bundle02.putLong("answerId", bean.questionAnswerId);
                        bundle02.putBoolean("isSingle", true);

                        bundle02.putLong("questionBaseId", bean.questionBaseId);

                        bundle02.putString("areaName", bean.areaName);
                        bundle02.putInt("correctNum", bean.correctNum);

                        bundle02.putInt("courseType", courseType);
                        bundle02.putLong("courseId", courseId);
                        bundle02.putLong("courseWareId", courseWareId);
                        bundle02.putLong("syllabusId", syllabusId);

                        ManulReportFragment.lanuch(HomeworkSingleListActivity.this, bundle02);
                        break;
                    case 2:     // 未批改（批改中），什么都不做
                    case 4:
                        ToastUtil.showToast(bean.clickContent);
                        break;
                    case 5:     // 被退回
                        DialogUtils.showEssaySendBackDialog(HomeworkSingleListActivity.this, bean.correctMemo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle extraBundle = new Bundle();

                                extraBundle.putString("areaName", bean.areaName);

                                extraBundle.putLong("answerId", bean.questionAnswerId);     // 退回修改答案的时候，用答题卡id请求问题
                                extraBundle.putInt("bizStatus", bean.bizStatus);            // 退回修改答案的时候，需要用批改状态

                                EssayRoute.goEssayHomeworkAnswer(HomeworkSingleListActivity.this,
                                        true,
                                        null,
                                        bean.questionBaseId,
                                        0,
                                        courseType,
                                        courseId,
                                        courseWareId,
                                        syllabusId,
                                        extraBundle);
                            }
                        });
                        break;
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeworkSingleListActivity.this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onLoadData() {

        if (!NetUtil.isConnected()) {
            showError(0);
            ToastUtils.showEssayToast("无网络连接！");
            return;
        }

        showProgress();

        ServiceProvider.getHomeworkSingleList(compositeSubscription, courseType, courseWareId, syllabusId, new NetResponse() {

            @Override
            public void onError(Throwable e) {
                hideProgress();
                showError(1);
                if (e instanceof ApiException) {
                    ToastUtils.showEssayToast(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtils.showEssayToast("获取数据失败，点击重试");
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                hideProgress();
                if (model != null && model.data != null) {
                    errorView.setVisibility(View.GONE);

                    HomeworkSingleListBean singleListBean = (HomeworkSingleListBean) model.data;

                    tvCount.setText(String.valueOf(singleListBean.total));
                    tvUndo.setText(String.valueOf(singleListBean.finishedCount));

                    ArrayList<HomeworkSingleListBean.HomeworkSingleBean> exercisesItemList = singleListBean.exercisesItemList;

                    if (exercisesItemList != null && exercisesItemList.size() > 0) {
                        data.clear();
                        data.addAll(exercisesItemList);
                        adapter.notifyDataSetChanged();
                    } else {
                        showError(2);
                    }
                } else {
                    showError(2);
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.err_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.err_view:
                onLoadData();
                break;
        }
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

    private void showError(int type) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setErrorImageVisible(true);
        switch (type) {
            case 0:                         // 无网络
                errorView.setErrorImage(R.drawable.no_server_service);
                errorView.setErrorText("无网络，点击重试");
                break;
            case 1:                         // 获取数据失败
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("获取数据是失败，点击重试");
                break;
            case 2:                         // 无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("无数据");
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onLoadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public static void showListHomework(final Activity context, String courseId, long courseWareId, String syllabusId, int videoType) {
        Bundle args = new Bundle();
        args.putLong("courseId", StringUtils.parseLong(courseId));
        args.putLong("courseWareId", courseWareId);
        args.putLong("syllabusId", StringUtils.parseLong(syllabusId));
        args.putInt("courseType", videoType);

        Intent intent = new Intent(context, HomeworkSingleListActivity.class);
        intent.putExtra("extra_args", args);
        context.startActivity(intent);
    }

    // courseType = bundle.getInt("courseType", 0);         课程类型 1、点播 2、直播 3、回放
    // courseId = bundle.getLong("courseId", 0);            课程Id
    // courseWareId = bundle.getLong("courseWareId", 0);    课件Id
    // syllabusId = bundle.getLong("syllabusId", 0);        大纲ID
    public static void show(Context context, Bundle bundle) {
        Intent intent = new Intent(context, HomeworkSingleListActivity.class);
        intent.putExtra("extra_args", bundle);
        context.startActivity(intent);
    }
}
