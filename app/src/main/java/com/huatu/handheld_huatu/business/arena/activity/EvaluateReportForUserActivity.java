package com.huatu.handheld_huatu.business.arena.activity;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;

import java.io.Serializable;
import java.lang.reflect.Field;
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
 * 行测评估报告，个人信息页
 */
public class EvaluateReportForUserActivity extends BaseActivity {

    /**
     * 三个View
     * eva_pie_layout
     * eva_histogram_layout
     * eva_radar_layout
     */

    // 四种展示颜色
    private String[] colorsStart = {"#FFF3A1", "#E1E1E1", "#D9973E", "#00CDF2"};
    private String[] colorsEnd = {"#D49112", "#AAAAAA", "#7A3D00", "#6688FF"};

    // 渐变色bg
    private int[] headerBg = {R.drawable.eva_user_header_bg_01, R.drawable.eva_user_header_bg_02, R.drawable.eva_user_header_bg_03, R.drawable.eva_user_header_bg_04};

    private int[] medals = {R.mipmap.eva_medal_01, R.mipmap.eva_medal_02, R.mipmap.eva_medal_03};
    private int[] diademas = {R.mipmap.eva_rank_001, R.mipmap.eva_rank_002, R.mipmap.eva_rank_003};

    @BindView(R.id.view_statue)
    View statueBar;
    @BindView(R.id.action_bar)
    RelativeLayout actionBar;
    @BindView(R.id.ll_header)
    LinearLayout llHeader;                  // 头像布局
    @BindView(R.id.iv_header)
    ImageView ivHeader;                     // 头像
    @BindView(R.id.iv_diadema)
    ImageView ivDiadema;                    // 王冠
    @BindView(R.id.tv_name)
    TextView tvName;                        // 用户名
    @BindView(R.id.tv_score)
    TextView tvScore;                       // 预测分
    @BindView(R.id.view_half_bg)
    View viewHalfBg;
    @BindView(R.id.tv_count)
    TextView tvCount;                       // 做题量
    @BindView(R.id.tv_correct)
    TextView tvCorrect;                     // 正确率
    @BindView(R.id.tv_speed)
    TextView tvSpeed;                       // 做题速度
    @BindView(R.id.iv_medal)
    ImageView ivMedal;                      // 奖牌

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.err_view)
    CommonErrorView errorView;

    // 三个View
    private View viewPie;
    private View viewRadar;
    private View viewHistogram;

    private int rank;                           // 第几名跳过来，显示的样子不一样
    private long userId;                        // 用户Id

    private EvaluateReportBean evaluateReportBean;              // 网络获取数据

    private TextView tvQuestionCount;           // 总做题量
    private ImageView ivCountChange;            // 做题量变化箭头
    private TextView tvCountChange;             // 变化数量
    private TextView tvCountAve;                // 全站平均
    private ViewPieChart pieChart;              // 饼图
    private TextView tvQuestionCountPie;        // 饼图中做题总量
    private GridLayout glPieCutline;            // 饼图图例Layout

    private TextView tvQuestionCorrect;         // 正确率
    private ImageView ivCorrectChange;          // 正确率变化箭头
    private TextView tvCorrectChange;           // 正确变化
    private TextView tvCorrectLine;             // 上岸正确率
    private ViewRadarMap radarMap;              // 雷达图

    private TextView tvQuestionSpeed;           // 我的速度
    private ImageView ivSpeedChange;            // 做题速率变化箭头
    private TextView tvSpeedChange;             // 速度变化
    private TextView tvSpeedAve;                // 全站平均
    private TextView tvSpeedLine;               // 上岸速率
    private ViewHistogram histogram;            // 柱形图


    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_evaluate_report_for_user;
    }

    @Override
    protected void onInitView() {

        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);

        rank = originIntent.getIntExtra("rank", 0);
        userId = originIntent.getLongExtra("userId", 0);

        errorView.setErrorImageVisible(true);

        initViewPager();

        initColorBg();

        findView();

        radarMap.setIsShowStars(false);     // 周围不显示星星，显示数字
    }

    @Override
    protected void onLoadData() {

        if (!NetUtil.isConnected()) {
            showError(0);
            return;
        }

        showProgress();

        Observable<BaseResponseModel<EvaluateReportBean>> evaluatorDetail = RetrofitManager.getInstance().getService().getEvaluatorDetailByUserId(userId, SpUtils.getUserSubject());
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

    @OnClick({R.id.iv_back, R.id.iv_share, R.id.err_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                EvaluateReportForUserActivity.this.finish();
                break;
            case R.id.iv_share:

                break;
            case R.id.err_view:
                onLoadData();
                break;
        }
    }

    private void showData(EvaluateReportBean data) {
        // Glide加载原型图片
        EvaluateReportBean.UserInfo userInfo = data.getUserInfoMap().get(data.getUserId());
        if (userInfo != null) {
            ImageLoad.displayUserAvater(EvaluateReportForUserActivity.this, userInfo.getAvatar(), ivHeader, R.mipmap.user_default_avater);
            tvName.setText(userInfo.getNick());
        }
        tvScore.setText("预测分" + LUtils.formatPoint(data.getWeekReport().getPredictedScore()));
        tvCount.setText(String.valueOf(data.getDoExerciseNum()));
        tvCorrect.setText(LUtils.doubleToPercent(data.getAccuracy()));
        tvSpeed.setText(LUtils.formatPoint(data.getDoExerciseSpead()));

        setPieData();
        setRadarData();
        setHistogram();

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
            View view = LayoutInflater.from(EvaluateReportForUserActivity.this).inflate(R.layout.ave_pie_item, glPieCutline, false);
            View viewColor = view.findViewById(R.id.view_color);
            TextView tvTitle = view.findViewById(R.id.tv_title);
            TextView tvCount = view.findViewById(R.id.tv_count);
            viewColor.setBackgroundColor(pieChart.getColor(i));
            tvTitle.setText(weekReport.getPointName().get(i));
            tvCount.setText(String.valueOf(weekReport.getExerciseNum().get(i)) + "题");
            glPieCutline.addView(view);
        }
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

    private void initViewPager() {
        ArrayList<View> viewList = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        View viewPieContainer = LayoutInflater.from(EvaluateReportForUserActivity.this).inflate(R.layout.scroll_container_layout, null);
        View viewRadarContainer = LayoutInflater.from(EvaluateReportForUserActivity.this).inflate(R.layout.scroll_container_layout, null);
        View viewHistogramContainer = LayoutInflater.from(EvaluateReportForUserActivity.this).inflate(R.layout.scroll_container_layout, null);

        viewPie = LayoutInflater.from(EvaluateReportForUserActivity.this).inflate(R.layout.eva_pie_layout, null);
        viewRadar = LayoutInflater.from(EvaluateReportForUserActivity.this).inflate(R.layout.eva_radar_layout, null);
        viewHistogram = LayoutInflater.from(EvaluateReportForUserActivity.this).inflate(R.layout.eva_histogram_layout, null);

        ((LinearLayout) viewPieContainer.findViewById(R.id.linearlayout)).addView(viewPie);
        ((LinearLayout) viewRadarContainer.findViewById(R.id.linearlayout)).addView(viewRadar);
        ((LinearLayout) viewHistogramContainer.findViewById(R.id.linearlayout)).addView(viewHistogram);

        viewList.add(viewPieContainer);
        viewList.add(viewRadarContainer);
        viewList.add(viewHistogramContainer);

        names.add("做题数量");
        names.add("正确率");
        names.add("做题速度");

        LinearLayout llPie = viewPie.findViewById(R.id.ll_pie_num);
        LinearLayout llRadar = viewRadar.findViewById(R.id.ll_radar_num);
        LinearLayout llHistogram = viewHistogram.findViewById(R.id.ll_histogram_num);

        llPie.setBackground(null);
        llRadar.setBackground(null);
        llHistogram.setBackground(null);

        PagerAdapter pagerAdapter = new PagerAdapter(viewList, names);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        setTabWidth(tabLayout, 30);
    }

    private void initColorBg() {
        int r = rank > 2 ? 3 : rank;
        statueBar.setBackgroundResource(headerBg[r]);
        statueBar.setBackgroundResource(headerBg[r]);
        actionBar.setBackgroundResource(headerBg[r]);
        llHeader.setBackgroundResource(headerBg[r]);
        viewHalfBg.setBackgroundResource(headerBg[r]);
        if (rank > 2) {
            ivMedal.setVisibility(View.GONE);
            ivDiadema.setVisibility(View.GONE);
        } else {
            ivMedal.setImageResource(medals[rank]);
            ivDiadema.setImageResource(diademas[rank]);
        }
        tabLayout.setTabTextColors(Color.parseColor("#9B9B9B"), Color.parseColor(colorsEnd[r]));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(colorsEnd[r]));
    }

    private void findView() {
        tvQuestionCount = viewPie.findViewById(R.id.tv_question_count);
        ivCountChange = viewPie.findViewById(R.id.iv_count_change);
        tvCountChange = viewPie.findViewById(R.id.tv_count_change);
        tvCountAve = viewPie.findViewById(R.id.tv_count_ave);
        pieChart = viewPie.findViewById(R.id.view_pie_chart);
        tvQuestionCountPie = viewPie.findViewById(R.id.tv_question_count_pie);
        glPieCutline = viewPie.findViewById(R.id.gl_pie_cutline);

        tvQuestionCorrect = viewRadar.findViewById(R.id.tv_question_correct);
        ivCorrectChange = viewRadar.findViewById(R.id.iv_correct_change);
        tvCorrectChange = viewRadar.findViewById(R.id.tv_correct_change);
        tvCorrectLine = viewRadar.findViewById(R.id.tv_correct_line);
        radarMap = viewRadar.findViewById(R.id.view_radar);

        tvQuestionSpeed = viewHistogram.findViewById(R.id.tv_question_speed);
        ivSpeedChange = viewHistogram.findViewById(R.id.iv_speed_change);
        tvSpeedChange = viewHistogram.findViewById(R.id.tv_speed_change);
        tvSpeedAve = viewHistogram.findViewById(R.id.tv_speed_ave);
        tvSpeedLine = viewHistogram.findViewById(R.id.tv_speed_line);
        histogram = viewHistogram.findViewById(R.id.view_histogram);
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
        }
    }

    private String formatDouble(Double d) {
        String s = String.valueOf(d);
        if (s.contains(".")) {
            return s.substring(0, s.indexOf(".") + 2);
        }
        return s;
    }

    // 为了改变TabLayout下的横线长度
    public void setTabWidth(final TabLayout tabLayout, final int padding) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);


                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距 注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = padding;
                        params.rightMargin = padding;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    static class PagerAdapter extends android.support.v4.view.PagerAdapter {

        private ArrayList<View> viewList;
        private ArrayList<String> names;

        public PagerAdapter(ArrayList<View> viewList, ArrayList<String> names) {
            this.viewList = viewList;
            this.names = names;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return names.get(position);
        }
    }
}
