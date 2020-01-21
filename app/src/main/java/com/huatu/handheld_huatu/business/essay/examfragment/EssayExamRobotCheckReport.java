package com.huatu.handheld_huatu.business.essay.examfragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.business.arena.utils.LUtils;
import com.huatu.handheld_huatu.business.essay.bean.EssayPaperReport;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.mvpmodel.me.DeleteResponseBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.CovertManulView;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.ListViewForScroll;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * 申论智能批改 报告页
 */
public class EssayExamRobotCheckReport extends MySupportFragment {

    @BindView(R.id.tv_paper_name)
    TextView tvPaperName;                   // 试卷名称
    @BindView(R.id.tv_score_big)
    TextView tvScoreBig;                    // 我的得分
    @BindView(R.id.tv_score_all)
    TextView tvScoreAll;                    // 总分
    @BindView(R.id.ll_up_down)
    LinearLayout llUpDown;                  // 同比变化
    @BindView(R.id.iv_up_down)
    ImageView ivUpDown;                     // 提高或降低箭头
    @BindView(R.id.tv_up_down)
    TextView tvUpDown;                      // 提高或降低数字
    @BindView(R.id.tv_check_time)
    TextView tvCheckTime;                   // 批改时间

    @BindView(R.id.tv_rank_all_area)
    TextView tvRankAllArea;                 // 我的总排名
    @BindView(R.id.tv_rank_all_area_all)
    TextView tvRankAllAreaAll;              // 总人数
    @BindView(R.id.ll_rank_up)
    LinearLayout llRankUpDown;              // 上升下降布局
    @BindView(R.id.iv_rank_up)
    ImageView ivRankUpDown;                 // 上升下降箭头
    @BindView(R.id.tv_rank_up)
    TextView tvRankUpDown;                  // 排名上升下降数量

    @BindView(R.id.tv_high_score)
    TextView tvHighScore;                   // 全站最高分
    @BindView(R.id.ll_score_up)
    LinearLayout llScoreUpDown;             // 上升下降布局
    @BindView(R.id.iv_score_up)
    ImageView ivScoreUpDown;                // 上升下降箭头
    @BindView(R.id.tv_score_up)
    TextView tvScoreUpDown;                 // 排名上升下降数量

    @BindView(R.id.tv_ave_score)
    TextView tvAveScore;                    // 同地区我的排名

    @BindView(R.id.tv_count)
    TextView tvCount;                       // 共多少道题
    @BindView(R.id.tv_no_ans)
    TextView tvNoAns;                       // 几个题没有答
    @BindView(R.id.tv_time)
    TextView tvTime;                        // 答题时间

    @BindView(R.id.list_view)
    ListViewForScroll listView;             // 做题内容分类统计

    @BindView(R.id.err_view)
    CommonErrorView errorView;              // 无数据

    @BindView(R.id.covert_mauel_view)
    CovertManulView trymanualView;

    public long answerCardId;               // 答题卡Id

    private EssayPaperReport paperReport;   // 返回的数据
    private boolean isSingle;
    private boolean isFromArgue;
    private long similarId;                 // 单题组Id
    private long paperId;                   // 套题Id
    private int questionType;               // 问题类型


    public static void lanuch(Context context, Bundle arg) {
        FragmentParameter tmpPar = new FragmentParameter(EssayExamRobotCheckReport.class, arg);
        UIJumpHelper.jumpSupportFragment(context, tmpPar, 1);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_essay_exam_check_report;
    }

    private CompositeSubscription mCompositeSubscription = null;

    protected CompositeSubscription getSubscription() {
        if(null==mCompositeSubscription){
            mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        }
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        LogUtils.v(this.getClass().getName() + " onDestroy()");
    }

    @Override
    protected void setListener() {
        errorView.setErrorImageVisible(true);
        Bundle extraArgs = getArguments();
        if (extraArgs == null) {
            extraArgs = new Bundle();
        }
        answerCardId = extraArgs.getLong("answerId");
        isSingle = extraArgs.getBoolean("isSingle");
        isFromArgue = extraArgs.getBoolean("isFromArgue");
        similarId = extraArgs.getLong("similarId");
        paperId = extraArgs.getLong("paperId");
        questionType = extraArgs.getInt("questionType");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onLoadData();
        // mCircleProgressBar.setProgress(85,true);
    }

    CustomLoadingDialog mLoadingDialog;

    protected void onLoadData() {
        if (!NetUtil.isConnected()) {
            showError(0);
            ToastUtils.showMessage("请检查网络");
            return;
        }
        mLoadingDialog = DialogUtils.showLoading(getActivity(), mLoadingDialog);
        ServiceProvider.getEssayPaperReport(getSubscription(), answerCardId, new NetResponse() {

            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    ToastUtils.showMessage(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtils.showMessage("获取数据失败");
                }
                showError(1);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                if (model.data instanceof EssayPaperReport) {
                    paperReport = (EssayPaperReport) model.data;
                    showData(paperReport);
                } else {
                    showError(1);
                    ToastUtils.showMessage("获取数据失败");
                }
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.ll_go_check, R.id.err_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:              // 点击返回，finish页面
                getActivity().finish();
                break;
            case R.id.ll_go_check:          // 去查看批改详情
                EssayExamCheckDetailV2 curFragment = new EssayExamCheckDetailV2();
                curFragment.mIsFromReport = true;
                Bundle args = getArguments();
                curFragment.setArguments(getArguments());
                start(curFragment);
                //  EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_SHOW_ESSAY_REPORT));
                break;
            case R.id.err_view:             // 点击重试
                onLoadData();
                break;
        }
    }

    private void showData(EssayPaperReport report) {

        DialogUtils.dismissLoading(mLoadingDialog);
        errorView.setVisibility(View.GONE);

        tvPaperName.setText(report.getPaperName());
        tvScoreBig.setText(LUtils.formatPoint(report.getExamScore()) + " ");
        tvScoreAll.setText(String.valueOf(report.getScore()).replace(".0", "") + " ");
        if (report.getExamScoreChange() == 0) {
            llUpDown.setVisibility(View.GONE);
        } else if (report.getExamScoreChange() > 0) {
            ivUpDown.setRotation(180);
        }
        tvUpDown.setText(LUtils.formatPoint(Math.abs(report.getExamScoreChange())) + " ");
        tvCheckTime.setText("批改时间：" + report.getCorrectDate());
        tvRankAllArea.setText(String.valueOf(report.getTotalRank()) + " ");
        tvRankAllAreaAll.setText("/" + String.valueOf(report.getTotalCount()) + " ");
        if (report.getTotalRankChange() == 0) {
            llRankUpDown.setVisibility(View.GONE);
        } else if (report.getTotalRankChange() < 0) {
            ivRankUpDown.setImageResource(R.mipmap.essay_report_down);
            tvRankUpDown.setTextColor(Color.parseColor("#1DD86C"));
        }
        tvRankUpDown.setText(String.valueOf(Math.abs(report.getTotalRankChange())));
        tvHighScore.setText(LUtils.formatPoint(report.getMaxScore()) + " ");
        if (report.getMaxScoreChange() == 0) {
            llScoreUpDown.setVisibility(View.GONE);
        } else if (report.getMaxScoreChange() < 0) {
            ivScoreUpDown.setImageResource(R.mipmap.essay_report_down);
            tvScoreUpDown.setTextColor(Color.parseColor("#1DD86C"));
        }
        tvScoreUpDown.setText(LUtils.formatPoint(Math.abs(report.getMaxScoreChange())));
        tvAveScore.setText(LUtils.formatPoint(report.getAvgScore()) + " ");
        tvCount.setText(String.valueOf(report.getQuestionCount()) + " ");
        tvNoAns.setText(String.valueOf(report.getUnfinishedCount()) + " ");
        tvTime.setText(TimeUtils.getSecond2MinTime(report.getSpendTime()) + " ");

        listView.setAdapter(new CommonAdapter<EssayPaperReport.QuestionVO>(
                getActivity(), report.getQuestionVOList(), R.layout.essay_report_item_layout) {
            @Override
            public void convert(ViewHolder holder, EssayPaperReport.QuestionVO item, int position) {
                holder.setText(R.id.type_name, "问题" + (position + 1) + "-" + item.getTypeName());
                holder.setText(R.id.sc_essay_content, "得分" + ArenaHelper.setNoZero(item.getExamScore() + "")
                        + "/" + ArenaHelper.setNoZero(item.getScore() + "") + "，用时"
                        + TimeUtils.getSecond2MinTime(item.getSpendTime()) + "，" + item.getInputWordNum() + "字");
            }
        });
        if (report.type != 0) {
            trymanualView.setVisibility(View.VISIBLE);
            trymanualView.setData(questionType, paperId, answerCardId, isSingle, report.convertCount);
        }else
            trymanualView.setVisibility(View.GONE);
    }

    private void showError(int type) {
        DialogUtils.dismissLoading(mLoadingDialog);
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
        }
    }



  /*  @Override
    protected void onSaveState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("paperReport", paperReport);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        paperReport = (EssayPaperReport) savedInstanceState.getSerializable("paperReport");
        if (paperReport != null) {
            showData(paperReport);
        }
    }
*/

}
