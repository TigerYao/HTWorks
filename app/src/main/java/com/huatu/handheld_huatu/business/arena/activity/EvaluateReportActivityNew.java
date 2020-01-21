package com.huatu.handheld_huatu.business.arena.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.business.arena.bean.EvaluateReportBean;
import com.huatu.handheld_huatu.business.arena.customview.ViewHistogram;
import com.huatu.handheld_huatu.business.arena.customview.ViewPieChart;
import com.huatu.handheld_huatu.business.arena.customview.ViewRadarMap;
import com.huatu.handheld_huatu.business.arena.utils.LUtils;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 行测评估报告页新
 */
public class EvaluateReportActivityNew extends BaseActivity {

    private int[] counts = {R.mipmap.eva_count_02, R.mipmap.eva_count_03, R.mipmap.eva_count_04, R.mipmap.eva_count_01};
    private int[] corrects = {R.mipmap.eva_crrect_01, R.mipmap.eva_crrect_02, R.mipmap.eva_crrect_03, R.mipmap.eva_crrect_04};
    private int[] speeds = {R.mipmap.eva_speed_05, R.mipmap.eva_speed_01, R.mipmap.eva_speed_02, R.mipmap.eva_speed_03, R.mipmap.eva_speed_04};
    private int[] ranks = {R.mipmap.eva_rankx_01, R.mipmap.eva_rankx_02, R.mipmap.eva_rankx_03, R.mipmap.eva_rankx_04};
    private int[] times = {R.mipmap.eva_time_03, R.mipmap.eva_time_01, R.mipmap.eva_time_02, R.mipmap.eva_time_04};
    private int[] days = {R.mipmap.eva_day_01, R.mipmap.eva_day_02, R.mipmap.eva_day_03, R.mipmap.eva_day_04};

    @BindView(R.id.view_statue)
    View statueBar;
    @BindView(R.id.action_bar)
    RelativeLayout actionBar;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

    @BindView(R.id.ll_user)
    LinearLayout llUser;                    // 用户有数据布局
    @BindView(R.id.tv_words)
    TextView tvWords;                       // 预测分评价
    @BindView(R.id.tv_score_big)
    TextView tvScoreBig;                    // 我的得分
    @BindView(R.id.tv_score_all)
    TextView tvScoreAll;                    // 总分
    @BindView(R.id.iv_up_down)
    ImageView ivUpDown;                     // 上升下降箭头
    @BindView(R.id.tv_up_down)
    TextView tvUpDown;                      // 上升下降文字
    @BindView(R.id.tv_eva_judge)
    TextView tvEvaJudge;                    // 我的预测分评价

    @BindView(R.id.rl_no_user_data)
    RelativeLayout rlNoUserData;            // 用户无数据布局
    @BindView(R.id.tv_go_exercise)
    TextView tvGoExercise;                  // 去做题

    @BindView(R.id.iv_header)
    ImageView ivHeader;                     // 我的头像
    @BindView(R.id.tv_level)
    TextView tvLevel;                       // 我的等级
    @BindView(R.id.tv_name)
    TextView tvName;                        // 我的昵称
    @BindView(R.id.iv_explain)
    ImageView ivExplain;                    // 介绍
    @BindView(R.id.gl_my_content)
    GridLayout glMyContent;                 // 网格布局 我的六个数据

    @BindView(R.id.tv_time)
    TextView tvTime;                        // 预测时间

    @BindView(R.id.tv_question_count)
    TextView tvQuestionCount;               // 我的做题数量
    @BindView(R.id.iv_count_change)
    ImageView ivCountChange;                // 做题量变化箭头
    @BindView(R.id.tv_count_change)
    TextView tvCountChange;                 // 做题量变化
    @BindView(R.id.tv_count_ave)
    TextView tvCountAve;                    // 全站平均
    @BindView(R.id.view_pie_chart)
    ViewPieChart pieChart;                  // 饼图
    @BindView(R.id.tv_question_count_pie)
    TextView tvQuestionCountPie;            // 饼中的体量
    @BindView(R.id.gl_pie_cutline)
    GridLayout glPieCutline;                // 网格布局 饼图的图例
    @BindView(R.id.tv_count_describe)
    TextView tvCountDescribe;               // 饼图下的黄描述

    @BindView(R.id.tv_question_correct)
    TextView tvQuestionCorrect;             // 正确率
    @BindView(R.id.iv_correct_change)
    ImageView ivCorrectChange;              // 正确率变化箭头
    @BindView(R.id.tv_correct_change)
    TextView tvCorrectChange;               // 正确率变化
    @BindView(R.id.tv_correct_line)
    TextView tvCorrectLine;                 // 上岸正确率
    @BindView(R.id.view_radar)
    ViewRadarMap radarMap;                  // 正确率雷达图
    @BindView(R.id.tv_correct_describe)
    TextView tvCorrectDescribe;             // 雷达图下的黄描述

    @BindView(R.id.tv_question_speed)
    TextView tvQuestionSpeed;               // 做题速度
    @BindView(R.id.iv_speed_change)
    ImageView ivSpeedChange;                // 做题速率变化箭头
    @BindView(R.id.tv_speed_change)
    TextView tvSpeedChange;                 // 做题速度变化率
    @BindView(R.id.tv_speed_ave)
    TextView tvSpeedAve;                    // 做题速度全站平均值
    @BindView(R.id.tv_speed_line)
    TextView tvSpeedLine;                   // 做题速度上岸率
    @BindView(R.id.view_histogram)
    ViewHistogram histogram;                // 柱状图
    @BindView(R.id.tv_speed_describe)
    TextView tvSpeedDescribe;               // 做题速度评价

    @BindView(R.id.rl_rank_02)
    RelativeLayout rlRank02;                // 排行第二整个布局
    @BindView(R.id.iv_rank_header_02)
    ImageView ivHeader02;                   // 排行第二名头像
    @BindView(R.id.tv_name_02_5)
    TextView tvName02;                      // 第二排名用户名
    @BindView(R.id.tv_score_02)
    TextView tvScore02;                     // 预测分

    @BindView(R.id.rl_rank_01)
    RelativeLayout rlRank01;                // 排行第一整个布局
    @BindView(R.id.iv_rank_header_01)
    ImageView ivHeader01;                   // 排行第一名头像
    @BindView(R.id.tv_name_01_5)
    TextView tvName01;                      // 第一排名用户名
    @BindView(R.id.tv_score_01)
    TextView tvScore01;                     // 预测分

    @BindView(R.id.rl_rank_03)
    RelativeLayout rlRank03;                // 排行第三整个布局
    @BindView(R.id.iv_rank_header_03)
    ImageView ivHeader03;                   // 排行第三名头像
    @BindView(R.id.tv_name_03_5)
    TextView tvName03;                      // 第三排名用户名
    @BindView(R.id.tv_score_03)
    TextView tvScore03;                     // 预测分

    @BindView(R.id.ll_rank)
    LinearLayout llRank;                    // 排名布局，添加其他名次人

    @BindView(R.id.iv_rank_header_me)
    ImageView ivHeaderMe;                   // 排行里我的头像
    @BindView(R.id.tv_rank_name_me)
    TextView tvUserMe;                      // 我的用户名
    @BindView(R.id.iv_rank_1w)
    ImageView ivRank1w;                     // 10000+显示图片
    @BindView(R.id.tv_rank_me)
    TextView tvRankMe;                      // 我的排名
    @BindView(R.id.iv_change_me)
    ImageView ivChangeMe;                   // 我的排名变化箭头
    @BindView(R.id.iv_change_1k)
    ImageView ivChange1k;                   // 排名变化1000+
    @BindView(R.id.tv_rank_change_me)
    TextView tvRankChangeMe;                // 我的排名变化
    @BindView(R.id.tv_score_me)
    TextView tvScoreMe;                     // 我的得分

    @BindView(R.id.eva_create_time)
    TextView tvCreateTime;                  // 底部的报告创建时间

    @BindView(R.id.err_view)
    CommonErrorView errorView;              // 网络错误

    private EvaluateReportBean evaluateReportBean;              // 网络获取数据

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_evaluate_report_new;
    }

    @Override
    protected void onInitView() {

        errorView.setErrorImageVisible(true);
        // 雷达图显示文字百分比，不显示星星
        radarMap.setIsShowStars(false);

        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @Override
    protected void onLoadData() {

        if (!NetUtil.isConnected()) {
            showError(0);
            return;
        }

        showProgress();

        Observable<BaseResponseModel<EvaluateReportBean>> evaluatorDetail = RetrofitManager.getInstance().getService().getEvaluatorDetailNew();
        Subscription subscription = evaluatorDetail.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel<EvaluateReportBean>>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        hideProgress();
                        ToastUtils.showMessage("获取数据失败");
                        showError(1);
                    }

                    @Override
                    public void onNext(BaseResponseModel<EvaluateReportBean> data) {
                        if (data != null) {
                            if (data.code == ApiErrorCode.ERROR_SUCCESS) {
                                evaluateReportBean = data.data;
                                handleData(evaluateReportBean);
                                showData(evaluateReportBean);
                            } else {
                                ToastUtils.showMessage(data.message);
                                showError(1);
                            }
                        } else {
                            ToastUtils.showMessage("获取数据失败");
                            showError(1);
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }

    // 处理返回信息，更好用
    private void handleData(EvaluateReportBean evaluateReportBean) {
        if (!evaluateReportBean.isFlag()) {
            evaluateReportBean.setPredictedScore(0);
            evaluateReportBean.setDif_predictedScore(0);
            evaluateReportBean.setHasBeat(0);
            evaluateReportBean.setDoExerciseNum(0);
            evaluateReportBean.setAccuracy(0);
            evaluateReportBean.setDoExerciseSpead(0);
            evaluateReportBean.setRank(0);
            evaluateReportBean.setDoExerciseTime(0);
            evaluateReportBean.setDoExerciseDay(0);
            EvaluateReportBean.WeekReport weekReport = evaluateReportBean.getWeekReport();
            weekReport.setDoExerciseNum(0);
            if (weekReport.getPointName() == null) {
                weekReport.setPointName(new ArrayList<String>());
                weekReport.setExerciseNum(new ArrayList<Integer>());
                weekReport.setAccuracies(new ArrayList<Double>());
                weekReport.setSpeeds(new ArrayList<Double>());
                for (int i = 0; i < 5; i++) {
                    weekReport.getPointName().add("知识点" + (i + 1));
                    weekReport.getExerciseNum().add(0);
                    weekReport.getAccuracies().add(0d);
                    weekReport.getSpeeds().add(0d);
                }
            }
        }
        // 把用户信息对应到map中
        evaluateReportBean.setUserInfoMap(new HashMap<Long, EvaluateReportBean.UserInfo>());
        HashMap<Long, EvaluateReportBean.UserInfo> userInfoMap = evaluateReportBean.getUserInfoMap();
        ArrayList<EvaluateReportBean.UserInfo> userInfo = evaluateReportBean.getUserInfo();
        for (int i = 0; i < userInfo.size(); i++) {
            EvaluateReportBean.UserInfo userInfo1 = userInfo.get(i);
            userInfoMap.put(userInfo1.getId(), userInfo1);
        }
        // 处理雷达图和柱状图的上线率
        evaluateReportBean.getWeekReport().setLineAccuracies(new ArrayList<Double>());
        ArrayList<Double> lineAccuracies = evaluateReportBean.getWeekReport().getLineAccuracies();
        String s = evaluateReportBean.getShangAn()[0];          // 上岸正确率 75%
        if (s.contains("%")) {
            s = s.substring(0, s.indexOf("%"));
        }
        double lineAccurecies = Double.valueOf(s) / 100;
        ArrayList<Double> accuracies = evaluateReportBean.getWeekReport().getAccuracies();
        for (Double ignored : accuracies) {
            lineAccuracies.add(lineAccurecies);
        }

        evaluateReportBean.getWeekReport().setLineSpeeds(new ArrayList<Double>());
        ArrayList<Double> lineSpeeds = evaluateReportBean.getWeekReport().getLineSpeeds();
        String sk = evaluateReportBean.getShangAn()[1];          // 上岸做题速度
        double lineSpeed = Double.valueOf(sk);
        ArrayList<Double> speeds = evaluateReportBean.getWeekReport().getSpeeds();
        for (Double ignored : speeds) {
            lineSpeeds.add(lineSpeed);
        }
    }

    @OnClick({R.id.err_view, R.id.iv_back, R.id.tv_go_exercise, R.id.iv_share, R.id.iv_explain})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.err_view:     // 网络错误点击重试
                onLoadData();
                break;
            case R.id.iv_back:
            case R.id.tv_go_exercise:                           // 去做题
                EvaluateReportActivityNew.this.finish();
                break;
            case R.id.iv_share:

                break;
            case R.id.iv_explain:                               // 弹窗介绍
                String content = "您的估分成绩是由各个模块准确率、答题时间、做题量、学习状态、模考成绩等各因素综合计算得出。该分数为百分制，非百分制的成绩需自行换算得出。坚持每天学习，听课，多参加模考训练，提升答题速度，距离岸上就会更近一步哦。" +
                        "\n\n" +
                        "注：报告中累计数据一小时更新一次，周报表数据是每周更新一次";
                final CustomConfirmDialog dialog = DialogUtils.createDialog(EvaluateReportActivityNew.this, "预估分说明", content);
                dialog.setBtnDividerVisibility(false);
                dialog.setCancelBtnVisibility(false);
                dialog.setMessage(content, 13);
                dialog.setOkBtnConfig(200, 50, R.drawable.eva_explain_btn_bg);
                dialog.setContentGravity(Gravity.START);
                dialog.setPositiveColor(Color.parseColor("#FFFFFF"));
                dialog.setPositiveButton("我知道啦", null);
                dialog.setTitleBold();
                View contentView = dialog.getContentView();
                LinearLayout llBtn = contentView.findViewById(R.id.ll_btn);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llBtn.getLayoutParams();
                layoutParams.height = DisplayUtil.dp2px(66);
                contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }

    private void showData(EvaluateReportBean data) {
        if (!data.isFlag()) {
            llUser.setVisibility(View.INVISIBLE);
            rlNoUserData.setVisibility(View.VISIBLE);
        } else {
            llUser.setVisibility(View.VISIBLE);
            rlNoUserData.setVisibility(View.GONE);
        }
        tvWords.setText(data.getScoreText());
        tvScoreBig.setText(LUtils.formatPoint(data.getPredictedScore()));
        tvScoreAll.setText("100");
        if (data.getDif_predictedScore() < 0) {
            ivUpDown.setRotation(180);
        } else if (data.getDif_predictedScore() == 0) {
            ivUpDown.setVisibility(View.GONE);
        }
        tvUpDown.setText(LUtils.doubleToPercent(Math.abs(data.getDif_predictedScore())) + "%");
        tvEvaJudge.setText("我的预估分已击败了" + LUtils.doubleToPercent(Math.abs(data.getHasBeat())) + "%的学员");
        //  加载原型图片
        EvaluateReportBean.UserInfo userInfo = data.getUserInfoMap().get(data.getUserId());
        if (userInfo != null) {
            ImageLoad.displayUserAvater(EvaluateReportActivityNew.this, userInfo.getAvatar(), ivHeader, R.mipmap.user_default_avater);
            tvName.setText(userInfo.getNick());
        }
        showMyData();

        tvTime.setText(data.getWeekReport().getWeek());

        setPieData();
        setRadarData();
        setHistogram();
        setUserRank();

        if (userInfo != null) {
            // Glide加载圆形图片
            ImageLoad.displayUserAvater(EvaluateReportActivityNew.this, userInfo.getAvatar(), ivHeaderMe, R.mipmap.user_default_avater);
            tvUserMe.setText(userInfo.getNick());
        }
        if (data.getWeekReport().getRank() >= 10000) {
            ivRank1w.setImageResource(R.mipmap.eva_1w_white);
            tvRankMe.setText("");
        } else {
            String s = String.valueOf(data.getWeekReport().getRank());
            tvRankMe.setText(s.equals("0") ? "-" : s);
        }

        int rankChange;
        if (data.getWeekReport().getLastWeekRank() == 0) {
            rankChange = 0;
        } else {
            rankChange = data.getWeekReport().getLastWeekRank() - data.getWeekReport().getRank();
        }
        if (rankChange > 0) {
            ivChangeMe.setRotation(180);
        } else if (rankChange == 0) {
            ivChangeMe.setVisibility(View.GONE);
        }

        if (Math.abs(rankChange) > 1000) {
            ivChange1k.setImageResource(R.mipmap.eva_1k);
            tvRankChangeMe.setText("");
        } else {
            tvRankChangeMe.setText(String.valueOf(Math.abs(rankChange)));
        }
        tvScoreMe.setText(LUtils.formatPoint(data.getWeekReport().getPredictedScore()));

        tvCreateTime.setText("周报数据生成于" + TimeUtils.getFormatData("yyyy.MM.dd HH:mm", data.getUpdateTime()));
    }

    /**
     * 添加我的数据View到GridLayout
     * 注：inflater merge 的标签，必须指定 parent 和 attachToRoot true
     */
    private void showMyData() {
        for (int i = 0; i < 6; i++) {
            View view = LayoutInflater.from(EvaluateReportActivityNew.this).inflate(R.layout.eva_mine_data, glMyContent, false);
            ImageView ivDescribe = view.findViewById(R.id.iv_describe);
            ImageView iv1W = view.findViewById(R.id.iv_1w);
            TextView tvData = view.findViewById(R.id.tv_data);
            TextView tvUnit = view.findViewById(R.id.tv_unit);
            TextView tvTitle = view.findViewById(R.id.tv_title);

            switch (i) {
                case 0:
                    String num = evaluateReportBean.getTextArea().get(0);
                    int img = 0;
                    switch (num) {
                        case "优秀":
                            img = counts[0];
                            break;
                        case "良好":
                            img = counts[1];
                            break;
                        case "偏低":
                            img = counts[2];
                            break;
                        case "裸考战将":
                            img = counts[3];
                            break;
                    }
                    if (img != 0) {
                        ivDescribe.setImageResource(img);
                    }
                    int doExerciseNum = evaluateReportBean.getDoExerciseNum();
                    tvData.setText(String.valueOf(doExerciseNum));
                    tvUnit.setText("题");
                    tvTitle.setText("做题数量");
                    break;
                case 1:
                    String acc = evaluateReportBean.getTextArea().get(1);
                    int accuracyImg = 0;
                    switch (acc) {
                        case "优秀":
                            accuracyImg = corrects[0];
                            break;
                        case "良好":
                            accuracyImg = corrects[1];
                            break;
                        case "偏低":
                            accuracyImg = corrects[2];
                            break;
                        case "认真答题哦":
                            accuracyImg = corrects[3];
                            break;
                    }
                    if (accuracyImg != 0) {
                        ivDescribe.setImageResource(accuracyImg);
                    }
                    double accuracy = evaluateReportBean.getAccuracy();
                    tvData.setText(LUtils.doubleToPercent(accuracy));
                    tvUnit.setText("%");
                    tvTitle.setText("正确率");
                    break;
                case 2:
                    String speed = evaluateReportBean.getTextArea().get(2);
                    int speedImg = 0;
                    switch (speed) {
                        case "认真答题哦":
                            speedImg = speeds[0];
                            break;
                        case "秒杀王子":
                            speedImg = speeds[1];
                            break;
                        case "良好":
                            speedImg = speeds[2];
                            break;
                        case "偏低":
                            speedImg = speeds[3];
                            break;
                        case "蜗牛的速度":
                            speedImg = speeds[4];
                            break;
                    }
                    double doExerciseSpead = evaluateReportBean.getDoExerciseSpead();
                    if (speedImg != 0) {
                        ivDescribe.setImageResource(speedImg);
                    }
                    tvData.setText(LUtils.formatPoint(doExerciseSpead));
                    tvUnit.setText("秒/题");
                    tvTitle.setText("做题速度");
                    break;
                case 3:
                    String rank = evaluateReportBean.getTextArea().get(3);
                    int rankImg = 0;
                    switch (rank) {
                        case "引领风骚":
                            rankImg = ranks[0];
                            break;
                        case "中等偏上":
                            rankImg = ranks[1];
                            break;
                        case "中等偏下":
                            rankImg = ranks[2];
                            break;
                        case "垫底小霸王":
                            rankImg = ranks[3];
                            break;
                    }
                    int rankX = evaluateReportBean.getRank();
                    if (rankImg != 0) {
                        ivDescribe.setImageResource(rankImg);
                    }
                    if (rankX >= 10000) {
                        iv1W.setImageResource(R.mipmap.eva_1w_red);
                        tvData.setText("");
                    } else {
                        tvData.setText(String.valueOf(rankX));
                    }
                    tvUnit.setText("名");
                    tvTitle.setText("全站排名");
                    break;
                case 4:
                    String time = evaluateReportBean.getTextArea().get(4);
                    int timeImg = 0;
                    switch (time) {
                        case "刷题王者":
                            timeImg = times[0];
                            break;
                        case "良好":
                            timeImg = times[1];
                            break;
                        case "偏低":
                            timeImg = times[2];
                            break;
                        case "刷题太少了":
                            timeImg = times[3];
                            break;
                    }
                    long doExerciseTime = evaluateReportBean.getDoExerciseTime();
                    if (timeImg != 0) {
                        ivDescribe.setImageResource(timeImg);
                    }
                    tvData.setText(millitotime(doExerciseTime));
                    tvUnit.setText("");
                    tvTitle.setText("累计做题时长");
                    break;
                case 5:
                    String day = evaluateReportBean.getTextArea().get(5);
                    int dayImg = 0;
                    switch (day) {
                        case "就是这么优秀":
                            dayImg = days[0];
                            break;
                        case "良好":
                            dayImg = days[1];
                            break;
                        case "偏低":
                            dayImg = days[2];
                            break;
                        case "需要加油哦":
                            dayImg = days[3];
                            break;
                    }
                    int doExerciseDay = evaluateReportBean.getDoExerciseDay();
                    if (dayImg != 0) {
                        ivDescribe.setImageResource(dayImg);
                    }
                    tvData.setText(String.valueOf(doExerciseDay));
                    tvUnit.setText("天");
                    tvTitle.setText("练习天数");
                    break;
            }
            glMyContent.addView(view);
        }
    }

    /**
     * 给饼图添加数据
     */
    private void setPieData() {

        EvaluateReportBean.WeekReport weekReport = evaluateReportBean.getWeekReport();

        tvQuestionCount.setText(String.valueOf(weekReport.getDoExerciseNum()));
        if (weekReport.getDif_doExerciseNum() < 0) {
            ivCountChange.setImageResource(R.mipmap.essay_report_down);
        } else if (weekReport.getDif_doExerciseNum() == 0) {
            ivCountChange.setVisibility(View.GONE);
        }
        tvCountChange.setText(String.valueOf(Math.abs(weekReport.getDif_doExerciseNum())));
        tvCountAve.setText(LUtils.removePoint(weekReport.getAve_doExerciseNum()));
        pieChart.setData(weekReport.getExerciseNum());
        tvQuestionCountPie.setText(String.valueOf(weekReport.getDoExerciseNum()) + "道");
        for (int i = 0; i < weekReport.getExerciseNum().size(); i++) {
            View view = LayoutInflater.from(EvaluateReportActivityNew.this).inflate(R.layout.ave_pie_item, glPieCutline, false);
            View viewColor = view.findViewById(R.id.view_color);
            TextView tvTitle = view.findViewById(R.id.tv_title);
            TextView tvCount = view.findViewById(R.id.tv_count);
            viewColor.setBackgroundColor(pieChart.getColor(i));
            if (i < weekReport.getPointName().size()) {
                tvTitle.setText(weekReport.getPointName().get(i));
            } else {
                tvTitle.setText("知识点" + (i + 1));
            }
            tvCount.setText(String.valueOf(weekReport.getExerciseNum().get(i)) + "题");
            glPieCutline.addView(view);
        }
        tvCountDescribe.setText(evaluateReportBean.getWeekTextArea().get(0));
    }

    /**
     * 给雷达图添加数据
     */
    private void setRadarData() {
        EvaluateReportBean.WeekReport weekReport = evaluateReportBean.getWeekReport();

        tvQuestionCorrect.setText(LUtils.doubleToPercent(weekReport.getAccuracy()) + "%");
        if (weekReport.getDif_accuracy() < 0) {
            ivCorrectChange.setImageResource(R.mipmap.essay_report_down);
        } else if (weekReport.getDif_accuracy() == 0) {
            ivCorrectChange.setVisibility(View.GONE);
        }
        tvCorrectChange.setText(LUtils.doubleToPercent(Math.abs(weekReport.getDif_accuracy())) + "%");
        tvCorrectLine.setText(evaluateReportBean.getShangAn()[0]);

        ArrayList<Double[]> datas = new ArrayList<>();
        datas.add(LUtils.dobuleListToArray(weekReport.getAccuracies()));
        datas.add(LUtils.dobuleListToArray(weekReport.getLineAccuracies()));
        String[] names = LUtils.stringListToArray(weekReport.getPointName());
        radarMap.setData(datas, names, false);

        tvCorrectDescribe.setText(evaluateReportBean.getWeekTextArea().get(1));
    }

    /**
     * 设置柱状图数据
     */
    private void setHistogram() {

        EvaluateReportBean.WeekReport weekReport = evaluateReportBean.getWeekReport();

        tvQuestionSpeed.setText(LUtils.formatPoint(weekReport.getSpeed()));
        if (weekReport.getDif_speed() < 0) {
            ivSpeedChange.setImageResource(R.mipmap.essay_report_down);
        } else if (weekReport.getDif_speed() == 0) {
            ivSpeedChange.setVisibility(View.GONE);
        }
        tvSpeedChange.setText(LUtils.formatPoint(Math.abs(weekReport.getDif_speed())));
        tvSpeedAve.setText(LUtils.formatPoint(weekReport.getAve_speed()));
        tvSpeedLine.setText(evaluateReportBean.getShangAn()[1]);
        String[] names = LUtils.stringListToArray(weekReport.getPointName());
        ArrayList<Double> speeds = weekReport.getSpeeds();
        ArrayList<Double> lineSpeeds = weekReport.getLineSpeeds();
        ArrayList<PointF> datas = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            PointF p = new PointF();
            p.x = speeds.get(i).floatValue();
            p.y = lineSpeeds.get(i).floatValue();
            datas.add(p);
        }
        histogram.setData(datas, names);
        tvSpeedDescribe.setText(evaluateReportBean.getWeekTextArea().get(2));
    }

    /**
     * 设置排行榜数据
     */
    private void setUserRank() {
        ArrayList<EvaluateReportBean.TopUser> top10User = evaluateReportBean.getTop10User();
        int count = top10User.size() >= 10 ? 10 : top10User.size();
        for (int i = 0; i < count; i++) {
            final EvaluateReportBean.TopUser user = top10User.get(i);
            EvaluateReportBean.UserInfo userInfo = evaluateReportBean.getUserInfoMap().get(user.getUserId());
            if (userInfo == null) {
                userInfo = new EvaluateReportBean.UserInfo();
            }
            switch (i) {
                case 0:
                    // Glide加载圆形图片
                    ImageLoad.displayUserAvater(EvaluateReportActivityNew.this, userInfo.getAvatar(), ivHeader01, R.mipmap.user_default_avater);
                    tvScore01.setText(LUtils.formatPoint(user.getPrediceScore()));
                    tvName01.setText(userInfo.getNick());
                    rlRank01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(EvaluateReportActivityNew.this, EvaluateReportForUserActivity.class);
                            intent.putExtra("rank", 0);
                            intent.putExtra("userId", user.getUserId());
                            startActivity(intent);
                        }
                    });
                    break;
                case 1:
                    // Glide加载圆形图片
                    ImageLoad.displayUserAvater(EvaluateReportActivityNew.this, userInfo.getAvatar(), ivHeader02, R.mipmap.user_default_avater);
                    tvScore02.setText(LUtils.formatPoint(user.getPrediceScore()));
                    tvName02.setText(userInfo.getNick());
                    rlRank02.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(EvaluateReportActivityNew.this, EvaluateReportForUserActivity.class);
                            intent.putExtra("rank", 1);
                            intent.putExtra("userId", user.getUserId());
                            startActivity(intent);
                        }
                    });
                    break;
                case 2:
                    // Glide加载圆形图片
                    ImageLoad.displayUserAvater(EvaluateReportActivityNew.this, userInfo.getAvatar(), ivHeader03, R.mipmap.user_default_avater);
                    tvScore03.setText(LUtils.formatPoint(user.getPrediceScore()));
                    tvName03.setText(userInfo.getNick());
                    rlRank03.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(EvaluateReportActivityNew.this, EvaluateReportForUserActivity.class);
                            intent.putExtra("rank", 2);
                            intent.putExtra("userId", user.getUserId());
                            startActivity(intent);
                        }
                    });
                    break;
                default:
                    View view = LayoutInflater.from(EvaluateReportActivityNew.this).inflate(R.layout.eva_rank_item, llRank, false);
                    TextView tvRank = view.findViewById(R.id.tv_rank);
                    ImageView ivHeader = view.findViewById(R.id.iv_header);
                    TextView tvName = view.findViewById(R.id.tv_name);
                    TextView tvScore = view.findViewById(R.id.tv_score);
                    tvRank.setText(String.valueOf(user.getNowWeekRank()));
                    tvName.setText(userInfo.getNick());
                    tvScore.setText(LUtils.formatPoint(user.getPrediceScore()));

                    // Glide加载原型图片
                    ImageLoad.displayUserAvater(EvaluateReportActivityNew.this, userInfo.getAvatar(), ivHeader, R.mipmap.user_default_avater);

                    final int finalI = i;
                    view.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(EvaluateReportActivityNew.this, EvaluateReportForUserActivity.class);
                            intent.putExtra("rank", finalI);
                            intent.putExtra("userId", user.getUserId());
                            startActivity(intent);
                        }
                    });
                    llRank.addView(view);
            }
        }
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
                errorView.setErrorText("获取数据失败，点击重试");
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (evaluateReportBean != null) {
            outState.putSerializable("evaluateReportBean", evaluateReportBean);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        evaluateReportBean = (EvaluateReportBean) savedInstanceState.getSerializable("evaluateReportBean");
        if (evaluateReportBean != null) {
            showData(evaluateReportBean);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    public String millitotime(long second) {
        if (second <= 0) {
            return "00:00′00″";
        }
        String sec = String.valueOf(second % 60);       // 秒
        second /= 60;       // 分
        String min = String.valueOf(second % 60);       // 分
        String hour = String.valueOf(second / 60);      // 小时

        return getZero(hour) + ":" + getZero(min) + "′" + getZero(sec) + "″";
    }

    private String getZero(String a) {
        if (a.length() == 1) {
            return "0" + a;
        }
        return a;
    }

    // 字符串"#000000"转换成int Color
    private int myGetColor(String s) {
        return Color.parseColor(s);
    }

    public String getMiddleColor(String color1, String color2, float percent) {
        if (color1.contains("#") && color2.contains("#") && color1.length() == color2.length() && color2.length() == 7) {
            color1 = color1.replace("#", "");
            color2 = color2.replace("#", "");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("#");
            for (int i = 0; i < color1.length() / 2; i++) {
                String s = Integer.toHexString((int) (getColorX(color1, i) + (getColorX(color2, i) - getColorX(color1, i)) * percent));
                String tempResult = s.length() == 1 ? "0" + s : s;
                stringBuilder.append(tempResult);
            }
            return stringBuilder.toString();
        }
        return "";
    }

    private float getColorX(String color, int i) {
        return (float) Integer.parseInt(color.substring(i * 2, i * 2 + 2), 16);
    }
}
