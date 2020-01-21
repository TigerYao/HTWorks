package com.huatu.handheld_huatu.business.arena.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.adapter.ArenaAnswerCardActAdapter;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.ArenaExamDataConverts;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.business.matches.activity.ScReportActivity;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeans;
import com.huatu.handheld_huatu.mvpmodel.exercise.ModuleBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.PaperBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaExamAnswerCardPresenterImpl;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamAnswerCardView;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.TopActionBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 答题卡页面，扫描试卷上的二维码，进行答题，只有选项。提交
 */
public class ArenaAnswerCardActivity extends BaseActivity implements ArenaExamAnswerCardView {

    private ArenaExamAnswerCardPresenterImpl mPresenter;        // 使用原交卷接口

    @BindView(R.id.action_bar)
    TopActionBar topActionBar;
    @BindView(R.id.ex_list)
    ExpandableListView exList;              // 二级展开ListView

    @BindView(R.id.ll_tip)
    LinearLayout llTip;                     // 文本提示条
    @BindView(R.id.tv_tip)
    TextView tvTip;                         // 文本提示内容
    @BindView(R.id.iv_tip)
    ImageView ivTip;                        // 提示条关闭按钮

    private RealExamBeans.RealExamBean cardBean;     // 扫描paperId，然后访问网络，获取答题卡内容

    private ModuleBean[] groups;                    // 分类数据modules
    private ArenaExamQuestionBean[][] children;     // 试题数据

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_arena_answer_card;
    }

    @Override
    protected void onInitView() {
        mPresenter = new ArenaExamAnswerCardPresenterImpl(compositeSubscription, this);

        topActionBar.setTitle("答题卡");
        topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
        topActionBar.setDividerShow(true);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                onBackPressed();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });

        cardBean = (RealExamBeans.RealExamBean) originIntent.getSerializableExtra("cardBean");

    }

    @OnClick({R.id.tv_commit, R.id.iv_tip})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_commit:
                commit();
                break;
            case R.id.iv_tip:
                llTip.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onLoadData() {
        showProgress();
        getPracticeInfoList(ArenaHelper.getExerciseIds(cardBean.paper.questions), cardBean.paper.type);
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

    /**
     * 显示答题卡信息
     */
    public void showData(PaperBean paper) {
        // 处理数据
        // 组合分类数据
        List<ModuleBean> modules = paper.modules;
        for (int i = modules.size() - 1; i >= 0; i--) {
            if (modules.get(i).qcount == 0) {
                modules.remove(i);
            }
        }
        groups = new ModuleBean[modules.size()];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = modules.get(i);
        }
        // 组合试题数据
        List<ArenaExamQuestionBean> questionBeanList = paper.questionBeanList;
        children = new ArenaExamQuestionBean[modules.size()][];
        int current = 0;
        for (int i = 0; i < modules.size(); i++) {
            children[i] = new ArenaExamQuestionBean[modules.get(i).qcount];
            for (int j = 0; j < children[i].length; j++) {
                children[i][j] = questionBeanList.get(current);
                current++;
            }
        }

        // 设置Adapter
        ArenaAnswerCardActAdapter adapter = new ArenaAnswerCardActAdapter(ArenaAnswerCardActivity.this, groups, children);
        exList.setAdapter(adapter);
        exList.expandGroup(0);      // 默认展开第一项
        exList.setDivider(null);
    }

    /**
     * 检查答题量，提交答案
     */
    private void commit() {
        List<ArenaExamQuestionBean> questionBeanList = cardBean.paper.questionBeanList;
        int doneCount = ArenaHelper.getDoneCount(cardBean);
        if (doneCount == 0) {                  // 一道没做
            DialogUtils.onShowConfirmDialog(ArenaAnswerCardActivity.this, null, null, "您还没有答题，\n请答题后交卷。", null, "确定");
        } else if (doneCount == questionBeanList.size()) {          // 做完
            commitCard();
        } else {          // 做一部分
            doneCount = questionBeanList.size() - doneCount;
            String tipContent = null;
            if (questionBeanList.size() < 11) {
                tipContent = "您还有题目未作答，\n确定交卷吗?";
            } else {
                tipContent = String.format("您还有%d题未作答，\n确定交卷吗?", doneCount);
            }
            DialogUtils.onShowConfirmDialog(ArenaAnswerCardActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.d(TAG, "onClick: submit();");
                    commitCard();
                }
            }, null, tipContent, "取消", "确定");
        }
    }

    /**
     * 提交答案
     */
    private void commitCard() {
        if (cardBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {       // 模考大赛提交答案，这里要判断是新模考还是旧模考
            mPresenter.submitScCard(cardBean);                                  // 新模考交卷
        } else {                                                                            // 真题演练提交答案
            mPresenter.submitAnswerCard(cardBean);
        }
    }


    /**********借用提交答案的View**********/
    // 真题演练提交答案回调
    @Override
    public void onSubmitAnswerResult(RealExamBeans.RealExamBean bean) {
        ToastUtils.showShort("提交成功");
        Bundle bundle = new Bundle();
        bundle.putLong("practice_id", cardBean.id);
        if (bean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_ERROR_EXPORT) {
            ArenaExamReportExActivity.show(ArenaAnswerCardActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_ERROR_EXPORT, bundle);
        } else {
            ArenaExamReportActivity.show(ArenaAnswerCardActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, bundle);
        }
        ArenaAnswerCardActivity.this.finish();
    }

    // 模考大赛提交模考答案回调
    @Override
    public void onSubmitMokaoAnswerResult(boolean isReport, RealExamBeans.RealExamBean bean) {
        if (isReport && bean != null) {
            ToastUtils.showShort("提交成功");
            final Bundle bundle = new Bundle();
            bundle.putLong("practice_id", cardBean.id);
            bundle.putInt("tag", 1);
            Intent intent = new Intent(ArenaAnswerCardActivity.this, ScReportActivity.class);
            intent.putExtra("tag", 1);
            intent.putExtra("arg", bundle);
            startActivity(intent);
        } else {
            ToastUtils.showShort("请在本次模考活动结束后查看报告");
        }
        ArenaAnswerCardActivity.this.finish();
    }

    @Override
    public void onSubmitMokaoAnswerError() {
        ToastUtils.showShort("提交失败");
    }

    @Override
    public void showProgressBar() {
        showProgress();
    }

    @Override
    public void dismissProgressBar() {
        hideProgress();
    }

    @Override
    public void onSetData(Object respData) {

    }

    @Override
    public void onLoadDataFailed() {

    }
    /**********借用提交答案的View**********/

    /**
     * 根据问题ids（逗号分隔开的问题id），获取问题详情。
     * 过滤无用数据（多此一举，本来不显示问题内容，过滤有毛用）
     */
    public void getPracticeInfoList(String exerciseIds, final int reqType) {
        if (TextUtils.isEmpty(exerciseIds)) {
            LogUtils.e("getPractice direct return, exerciseIds is empty");
            return;
        }
        showProgressBar();

        compositeSubscription.add(RetrofitManager.getInstance().getService().getExercises(exerciseIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExerciseBeans>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ExerciseBeans exerciseBeans) {

                        // 给每一道题添加类型
                        PaperBean paper = cardBean.paper;
                        List<ModuleBean> modules = null;
                        if (paper != null && paper.modules != null && paper.modules.size() > 0) {
                            modules = paper.modules;
                        }

                        if (exerciseBeans != null && exerciseBeans.data != null) {
                            // 因为有题目会不存在，所以realExamBean.paper.questions.size()可能会大于返回的试题数
                            // 所以要计算模块module中量的问题
                            int realQuestionSize = exerciseBeans.data.size();
                            if (paper.questions.size() > realQuestionSize) {
                                // 遍历请求试题的id，如果那个不被返回的试题包含，就需要处理
                                List<Integer> modulesE = new ArrayList<>();                 // 累加各个模块
                                for (int i = 0; i < modules.size(); i++) {
                                    int x = 0;
                                    for (int j = 0; j <= i; j++) {
                                        x += modules.get(j).qcount;
                                    }
                                    modulesE.add(x);
                                }
                                List<Integer> questions = paper.questions;     // 请求问题id
                                List<Integer> exercises = new ArrayList<>();                // 返回问题id
                                for (ExerciseBeans.ExerciseBean datum : exerciseBeans.data) {
                                    exercises.add(datum.id);
                                }
                                for (int i = questions.size() - 1; i >= 0; i--) {
                                    if (!exercises.contains(questions.get(i))) {
                                        // 修改module的数量
                                        for (int j = 0; j < modulesE.size(); j++) {
                                            if ((j - 1 < 0 ? 0 : modulesE.get(j - 1)) <= i && i < modulesE.get(j)) {
                                                modules.get(j).qcount--;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            // ---------- 到这里做的处理，都是以防服务端返回丢题

                            paper.questionBeanList = new ArrayList<>();
                            for (int i = 0; i < realQuestionSize; i++) {
                                ArenaExamQuestionBean bean = ArenaExamDataConverts.convertFromExerciseBean(exerciseBeans.data.get(i));
                                bean.index = i;

                                paper.questionBeanList.add(bean);
                            }
                        }
                        ArenaExamDataConverts.dealExamBeanAnswers(cardBean);
                        showData(paper);
                        hideProgress();
                    }
                }));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("cardBean", cardBean);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cardBean = (RealExamBeans.RealExamBean) savedInstanceState.getSerializable("cardBean");
        showData(cardBean.paper);
    }

    private CustomConfirmDialog exitConfirmDialog;

    @Override
    public void onBackPressed() {
        if (exitConfirmDialog == null) {
            exitConfirmDialog = DialogUtils.createDialog(ArenaAnswerCardActivity.this, "", "不保存已录入的答案？");
            exitConfirmDialog.setPositiveButton("不保存", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArenaAnswerCardActivity.this.finish();
                }
            });
            exitConfirmDialog.setNegativeButton("取消", null);
            exitConfirmDialog.setCancelBtnVisibility(true);
        }
        exitConfirmDialog.show();
    }
}
