package com.huatu.handheld_huatu.business.matches.fragment;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.GiftWebViewActivity;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.adapter.ArenaPointTreeAdapter;
import com.huatu.handheld_huatu.business.arena.customview.ArenaAnswerCardViewNew;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.ArenaExamDataConverts;
import com.huatu.handheld_huatu.business.matches.customview.ShakeImageView;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.event.match.MatchEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeansNew;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.exercise.ScMatchMetaBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PopWindowUtil;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.ListViewForScroll;
import com.huatu.handheld_huatu.view.custom.NewScDesTipGraphView;
import com.huatu.handheld_huatu.view.custom.NewSimulationContestGraphView;
import com.huatu.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * sc_report 新的行测模考报告
 * 行测模考报告
 * 数据是从父类Activity那获取的
 * realExamBean = (RealExamBeans.RealExamBean) mActivity.getDataFromActivity("");
 */
public class ScReportArenaFragmentNew extends BaseFragment {

    @BindView(R.id.sc_answer_report_analysis_layout)
    LinearLayout scAnswerReportAnalysisLayout;
    @BindView(R.id.sc_answer_report_type_tv)
    TextView scAnswerReportTypeTv;
    @BindView(R.id.sc_answer_report_self_score_tv)
    TextView scAnswerReportSelfScoreTv;
    @BindView(R.id.sc_answer_report_self_score_des)
    TextView scAnswerReportSelfScoreDes;
    @BindView(R.id.sc_answer_report_ranking_samejob_des)
    TextView scAnswerReportRankingSamejobDes;
    @BindView(R.id.sc_answer_report_ranking_samejob_self_tv)
    TextView scAnswerReportRankingSamejobSelfTv;
    @BindView(R.id.sc_answer_report_ranking_samejob_allnum_tv)
    TextView scAnswerReportRankingSamejobAllnumTv;
    @BindView(R.id.sc_answer_report_ranking_total_des)
    TextView scAnswerReportRankingTotalDes;
    @BindView(R.id.sc_answer_report_ranking_total)
    TextView scAnswerReportRankingTotal;
    @BindView(R.id.sc_answer_report_total_num)
    TextView scAnswerReportRankingTotalNum;
    @BindView(R.id.sc_answer_report_max_score_des)
    TextView scAnswerReportMaxScoreDes;
    @BindView(R.id.sc_answer_report_max_score_tv)
    TextView scAnswerReportMaxScoreTv;
    @BindView(R.id.sc_answer_report_compare_layout)
    LinearLayout scAnswerReportCompareLayout;

    @BindView(R.id.tv_count)
    TextView tvCount;               // 总题数
    @BindView(R.id.tv_right)
    TextView tvRight;               // 答对题数
    @BindView(R.id.tv_no_ans)
    TextView tvNoAns;               // 未答题数
    @BindView(R.id.tv_time)
    TextView tvTime;                // 用时


    @BindView(R.id.sc_answer_report_knowledge_list_view)
    ListViewForScroll scAnswerReportKnowledgeListView;
    @BindView(R.id.sc_answer_card_report_layout_id)
    ArenaAnswerCardViewNew scAnswerCardView;
    @BindView(R.id.simulation_contest_main_error_layout)
    CommonErrorView errorView;
    @BindView(R.id.sc_report_graphView)
    NewSimulationContestGraphView scReportGraphView;
    @BindView(R.id.sc_report_graphView_des)
    NewScDesTipGraphView scReportGraphViewDes;
    @BindView(R.id.sc_report_scrollv)
    NestedScrollView scReportScrollv;
    @BindView(R.id.sc_report_graphView_scrollView)
    HorizontalScrollView scReportGraphViewScrollView;
    @BindView(R.id.sc_rp_area_rl)
    RelativeLayout sc_rp_area_rl;

    @BindView(R.id.rl_gift)
    RelativeLayout rlGift;                          // 大礼包布局
    @BindView(R.id.iv_gift)
    ShakeImageView ivGift;                          // 礼包图片

    private long paperId;                           // 模考Id

    private boolean isClickCard = false;            // 是否点击答题卡去对应的解析
    private int choiceIndex = -1;                   // 点击答题卡的index

    int typeSc;                                     // 要分享哪个模考报告 1、行测 2、申论 3、全部

    ArenaPointTreeAdapter pointTreeAdapter;
    private RealExamBeans.RealExamBean realExamBean;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_simulation_contest_report_layout;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {
        if (event == null || event.type <= 0) {
            return;
        }
        // 跳转题号
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION) {               // 点击答题卡，跳转题号
            if (event.extraBundle != null) {
                if ("ScReportFragment".equals(event.tag)) {
                    isClickCard = true;
                    choiceIndex = event.extraBundle.getInt("request_index", -1);

                    clickType = ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL;
                    if (realExamBean.paper != null && realExamBean.paper.questionBeanList != null && realExamBean.paper.questionBeanList.size() > 0
                            && realExamBean.paper.questionBeanList.get(0).questionOptions != null &&
                            !StringUtils.isEmpty(realExamBean.paper.questionBeanList.get(0).questionOptions.get(0).optionDes)) {
                        goArenaExamActivityNew(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL);
                    } else {
                        getAllQuestions(realExamBean.id);
                    }
                }
            }
        } else if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_SHARE) {                // 分享
            if (event.extraBundle != null) {
                int type = event.extraBundle.getInt("share_type", 0);
                if (type == 3) {                                                                  // 模考报告页面中的分享
                    typeSc = event.extraBundle.getInt("typeSc");
                }
            }
        } else if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_SHARE_WAY) {            // 分享渠道回传
            if (typeSc == 1) {    // 分享报告
                String shareWay = event.extraBundle.getString("share_way");
                if (realExamBean != null)
                    StudyCourseStatistic.simulatedShareResult("模考大赛", Type.getSubject(realExamBean.subject), Type.getCategory(realExamBean.catgory),
                            String.valueOf(realExamBean.paper.id), realExamBean.paper.name, realExamBean.score, shareWay);
            }
        }
    }

    @Override
    protected void onInitView() {
        super.onInitView();

        EventBus.getDefault().register(this);

        compositeSubscription = new CompositeSubscription();

        if (args != null) {
//            requestType = args.getInt("request_type");
            paperId = args.getLong("practice_id", -1);
        }

        scAnswerCardView.setTitleVisible(View.GONE);

        scReportScrollv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY - oldScrollY > 10) {
                    EventBus.getDefault().post(new MatchEvent(MatchEvent.GIFT_SCROLL_UP));
                } else if (scrollY - oldScrollY < -10) {
                    EventBus.getDefault().post(new MatchEvent(MatchEvent.GIFT_SCROLL_DOWN));
                }
            }
        });
        if (scReportGraphViewDes != null) {
            scReportGraphViewDes.setShowType(1);
        }
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();

        if (paperId < 0) {
            ToastUtils.showShort("Id错误");
            showError(1);
            return;
        }

        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("无网络连接");
            showError(0);
            return;
        }

        mActivity.showProgress();

        Observable<RealExamBeans> realExamBeansObservable = RetrofitManager.getInstance().getService().getScArenaReport(paperId);
        compositeSubscription.add(realExamBeansObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RealExamBeans>() {
                    @Override
                    public void onCompleted() {
                        mActivity.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mActivity.hideProgress();
                        showError(1);

                    }

                    @Override
                    public void onNext(RealExamBeans examBean) {
                        mActivity.hideProgress();
                        realExamBean = examBean.data;
                        ArenaDataCache.getInstance().realExamBean = realExamBean;

                        if (realExamBean != null) {
                            handleRealExamBean(realExamBean);

                            refreshViewStatus1();
                            refreshViewStatus2();
                        } else {
                            showError(1);
                        }
                    }
                })
        );
    }

    /**
     * 这里处理返回的报告信息，把答题情况对应进paper的qestions中去。
     */
    private void handleRealExamBean(RealExamBeans.RealExamBean realExamBean) {
        ArrayList<ArenaExamQuestionBean> questionBeanList = new ArrayList<>();
        realExamBean.paper.questionBeanList = questionBeanList;
        List<Integer> corrects = realExamBean.corrects;
        List<Integer> answers = realExamBean.answers;
        List<Integer> doubts = realExamBean.doubts;
        if (corrects == null) return;
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i) == null) {
                answers.set(i, 0);
            }
        }
        for (int i = 0; i < corrects.size(); i++) {
            ArenaExamQuestionBean bean = new ArenaExamQuestionBean();
            bean.index = i;
            bean.isCorrect = corrects.get(i);
            if (answers != null && i < answers.size()) {
                bean.userAnswer = answers.get(i);
            }
            if (doubts != null && i < doubts.size()) {
                bean.doubt = doubts.get(i);
            }
            questionBeanList.add(bean);
        }
    }

    private void refreshViewStatus1() {
        if (Method.isActivityFinished(mActivity)) {
            return;
        }
        if (realExamBean == null || realExamBean.matchMeta == null || realExamBean.cardUserMeta == null) {
            return;
        }

        StudyCourseStatistic.simulatedReport(Type.getSubject(realExamBean.subject), Type.getCategory(realExamBean.catgory),
                String.valueOf(realExamBean.paperId), realExamBean.name, realExamBean.score, realExamBean.rcount, realExamBean.expendTime);

        scAnswerReportTypeTv.setText(realExamBean.name);
        scAnswerReportSelfScoreTv.setText(realExamBean.scoreStr + " ");

        if (realExamBean.cardUserMeta != null) {
            scAnswerReportRankingTotal.setText(realExamBean.cardUserMeta.rank + " ");
            scAnswerReportRankingTotalNum.setText("/" + realExamBean.cardUserMeta.total + "");
            scAnswerReportMaxScoreTv.setText(realExamBean.cardUserMeta.maxStr + " ");
        }

        if (realExamBean.matchMeta != null) {
            scAnswerReportRankingSamejobSelfTv.setText(realExamBean.matchMeta.getPositionRank() + " ");
            scAnswerReportRankingSamejobAllnumTv.setText("/" + realExamBean.matchMeta.getPositionCount() + "");
        }

        if (realExamBean.matchMeta != null) {
            int id = realExamBean.matchMeta.getPositionId();
            if (sc_rp_area_rl != null) {
                if (id == -9) {
                    sc_rp_area_rl.setVisibility(View.GONE);
                } else {
                    sc_rp_area_rl.setVisibility(View.VISIBLE);
                }
            }
            ScMatchMetaBean.ScoreLineEntity scoreLineEntity = realExamBean.matchMeta.getScoreLine();
            if (scoreLineEntity != null) {
                List<String> categories = scoreLineEntity.getCategories();
                if (categories != null && categories.size() > 1) {
                    scReportGraphViewDes.setTextDes(categories.get(0), categories.get(1));
                }
                List<ScMatchMetaBean.ScoreLineEntity.SeriesEntity> series = scoreLineEntity.getSeries();
                if (series != null) {
                    int size = series.size();
                    float[] scores = new float[size + 1];
                    float[] scoresAverage = new float[size + 1];
                    String[] date = new String[size + 1];
                    scores[0] = 0;
                    scoresAverage[0] = 0;
                    date[0] = "";
                    for (int i = 0; i < series.size(); i++) {
                        ScMatchMetaBean.ScoreLineEntity.SeriesEntity var = series.get(i);
                        if (var != null) {
                            String data = var.getName();
                            if (data != null) {
                                date[i + 1] = data;
                            }
                            List<Float> values = var.getData();
                            if (values != null && values.size() == 2) {
                                scoresAverage[i + 1] = values.get(0);
                                scores[i + 1] = values.get(1);
                            }
                        }
                    }
                    scReportGraphView.setDatas(scores, scoresAverage, date);
                    if (scReportGraphViewScrollView != null) {
                        scReportGraphViewScrollView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (scReportGraphViewScrollView != null)
                                    scReportGraphViewScrollView.fullScroll(View.FOCUS_RIGHT);
                            }
                        }, 500);
                    }
                }
            }
        }

        tvCount.setText(realExamBean.paper.qcount + "");
        tvRight.setText(realExamBean.rcount + "");
        tvNoAns.setText(realExamBean.ucount + "");

        tvTime.setText(TimeUtils.getSecond2HourMinTimeOther(realExamBean.expendTime));

        pointTreeAdapter = new ArenaPointTreeAdapter(mActivity.getApplicationContext(), realExamBean.type, realExamBean.points);
        scAnswerReportKnowledgeListView.setAdapter(pointTreeAdapter);
        //大礼包
        if (realExamBean.hasGift == 1 && realExamBean.rightImgUrl != null) {
            rlGift.setVisibility(View.VISIBLE);
            //  Glide.with(mActivity).load(realExamBean.rightImgUrl).diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivGift);
            ImageLoad.load(mActivity, realExamBean.rightImgUrl, ivGift, DiskCacheStrategy.AUTOMATIC);

            ivGift.startAnim();
        } else {
            rlGift.setVisibility(View.GONE);
        }
    }

    private void refreshViewStatus2() {
        if (Method.isActivityFinished(mActivity)) {
            return;
        }

        if (realExamBean == null || realExamBean.matchMeta == null || realExamBean.cardUserMeta == null) {
            return;
        }
        scAnswerCardView.setData(realExamBean, ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL, realExamBean.type, "ScReportFragment");

        if (scReportScrollv != null) {
            scReportScrollv.scrollTo(0, 0);
        }
        if (scAnswerReportTypeTv != null) {
            scAnswerReportTypeTv.setFocusableInTouchMode(true);
            scAnswerReportTypeTv.requestFocus();
        }
    }

    @OnClick({R.id.sc_answer_report_analysis_wrong,
            R.id.sc_answer_report_analysis_all,
            R.id.rl_gift})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sc_answer_report_analysis_wrong:
                onClickAnalysisWrong();
                break;
            case R.id.sc_answer_report_analysis_all:
                onClickAnalysisAll();
                break;
            case R.id.rl_gift:                                  //领取大礼包
                if (realExamBean.hasGetBigGift == 0) {
                    //没领过大礼包
                    showBigGift();
                } else {
                    // TODO已领过大礼包，跳H5
                    GiftWebViewActivity.newInstance(getContext(), realExamBean.giftHtmlUrl);
                }
                break;
        }
    }

    private void showBigGift() {
        PopWindowUtil.showPopInCenter(mActivity, scReportScrollv, 0, 0, R.layout.layout_biggift_open_pop,
                DisplayUtil.px2dp(DisplayUtil.getScreenWidth()), DisplayUtil.px2dp(DisplayUtil.getScreenHeight()), new PopWindowUtil.PopViewCall() {
                    @Override
                    public void popViewCall(View contentView, final PopupWindow popWindow) {


                        final ImageView iv_bag = (ImageView) contentView.findViewById(R.id.iv_bag);                 // 去发红包

                        ImageView ivClose = (ImageView) contentView.findViewById(R.id.iv_close);                    // 关闭按钮

                        TextView tvOpen = (TextView) contentView.findViewById(R.id.tv_open);
                        if (realExamBean.giftImgUrl != null) {
                            // Glide.with(mActivity).load(realExamBean.giftImgUrl).diskCacheStrategy(DiskCacheStrategy.RESULT).into(iv_bag);
                            ImageLoad.load(mActivity, realExamBean.giftImgUrl, iv_bag, DiskCacheStrategy.AUTOMATIC);
                        }
                        // 规则按钮
                        final ValueAnimator animatorBagIn = ValueAnimator.ofFloat(0.2f, 1f);
                        animatorBagIn.setInterpolator(new AnticipateOvershootInterpolator());
                        animatorBagIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float values = (float) animation.getAnimatedValue();
                                iv_bag.setScaleX(values);
                                iv_bag.setScaleY(values);
                            }
                        });
                        animatorBagIn.setDuration(900);
                        animatorBagIn.start();

                        ivClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popWindow.dismiss();
                            }
                        });

                        tvOpen.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GiftWebViewActivity.newInstance(getContext(), realExamBean.giftHtmlUrl);
                                popWindow.dismiss();
                            }
                        });

                    }

                    @Override
                    public void popViewDismiss() {
                    }
                });
    }

    private int clickType;              // 记录点击的按钮，以便获取数据之后，进行跳转

    /**
     * 错题解析
     */
    public void onClickAnalysisWrong() {
        clickType = ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG;
        if (realExamBean.wcount <= 0) {
            ToastUtils.showShort("恭喜您，您没有错题！");
        } else {
            if (realExamBean.paper != null && realExamBean.paper.wrongQuestionBeanList != null && realExamBean.paper.wrongQuestionBeanList.size() > 0) {
                goArenaExamActivityNew(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG);
            } else {
                getAllQuestions(realExamBean.id);
            }
        }
    }

    /**
     * 全部解析
     */
    public void onClickAnalysisAll() {
        clickType = ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL;
        if (realExamBean.paper != null && realExamBean.paper.questionBeanList != null && realExamBean.paper.questionBeanList.size() > 0
                && realExamBean.paper.questionBeanList.get(0).questionOptions != null && !StringUtils.isEmpty(realExamBean.paper.questionBeanList.get(0).questionOptions.get(0).optionDes)) {
            goArenaExamActivityNew(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL);
        } else {
            getAllQuestions(realExamBean.id);
        }
    }

    /**
     * 错题解析、全部解析
     * 去答题/看题页面
     */
    private void goArenaExamActivityNew(int requestType) {
        Bundle bundle = new Bundle();
        if (isClickCard) {
            isClickCard = false;
            bundle.putInt("showIndex", choiceIndex);
        } else {
            bundle.putInt("showIndex", 0);
        }
        ArenaExamActivityNew.show(mActivity, requestType, bundle);
    }

    /**
     * 获取全部试题
     */
    public void getAllQuestions(long id) {
        if (!NetUtil.isConnected()) {
            showError(0);
            return;
        }
        if (id <= 0) {
            ToastUtils.showShort("试卷Id错误");
            return;
        }
        mActivity.showProgress();
        // 获取试卷信息
        Observable<BaseListResponseModel<ExerciseBeansNew.ExerciseBean>> scWrongQuestion = RetrofitManager.getInstance().getService().getScAllQuestion(id);
        compositeSubscription.add(scWrongQuestion.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseListResponseModel<ExerciseBeansNew.ExerciseBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.hideProgress();
                        ToastUtils.showShort("获取试题失败");
                    }

                    @Override
                    public void onNext(BaseListResponseModel<ExerciseBeansNew.ExerciseBean> data) {
                        if (data != null && data.code == 1000000 && data.data != null && data.data.size() > 0) {
                            realExamBean.paper.questionBeanList = new ArrayList<>();
                            for (ExerciseBeansNew.ExerciseBean datum : data.data) {
                                realExamBean.paper.questionBeanList.add(ArenaExamDataConverts.convertFromExerciseBeanNew(datum));
                            }
                            // 把已经选择的答案，对应到试题详情中
                            ArenaExamDataConverts.dealExamBeanAnswers(realExamBean);
                            getCollectionState();
                        } else {
                            mActivity.hideProgress();
                            ToastUtils.showShort("获取试题失败");
                        }
                    }
                })
        );
    }

    /**
     * 获取收藏信息
     */
    private void getCollectionState() {
        List<ArenaExamQuestionBean> questionBeanList = realExamBean.paper.questionBeanList;
        StringBuilder exerciseIds = new StringBuilder();
        for (ArenaExamQuestionBean arenaExamQuestionBean : questionBeanList) {
            exerciseIds.append(String.valueOf(arenaExamQuestionBean.id)).append(",");
        }
        // 组合试题Id，为了获取是否收藏过
        exerciseIds = new StringBuilder(exerciseIds.substring(0, exerciseIds.length() - 1));
        // 查询试题是否被收藏过
        Observable<JsonObject> collectionObservable = RetrofitManager.getInstance().getService().getExerciseCollectStatus(exerciseIds.toString());
        compositeSubscription.add(collectionObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonObject>() {
                    @Override
                    public void onCompleted() {
                        mActivity.hideProgress();

                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.hideProgress();

                    }

                    @Override
                    public void onNext(JsonObject jsonObject) {
                        mActivity.hideProgress();
                        // 是否收藏的int列表
                        List<Integer> collectList = ArenaExamDataConverts.parsePractiseCollectionList(jsonObject);
                        // 是否收藏等属性添加进试题
                        ArenaExamDataConverts.processExamCollection(realExamBean.paper.questionBeanList, collectList);
                        // 把已经选择的答案，对应到试题详情中
                        ArenaExamDataConverts.dealExamBeanAnswers(realExamBean);

                        goArenaExamActivityNew(clickType);
                    }
                }));
    }

    private void showError(int type) {
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

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        realExamBean = ArenaDataCache.getInstance().realExamBean;
        if (realExamBean != null) {
            refreshViewStatus1();
            refreshViewStatus2();
        } else {
            onLoadData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static ScReportArenaFragmentNew newInstance(Bundle bundle) {
        ScReportArenaFragmentNew fragment = new ScReportArenaFragmentNew();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }
}
