package com.huatu.handheld_huatu.business.play.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.helper.ReplaceViewHelper;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.helper.statistic.SensorStatisticHelper;
import com.huatu.handheld_huatu.mvpmodel.HomeTreeBeanNew;
import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;
import com.huatu.handheld_huatu.mvpmodel.VideoStudyReportBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaExamPresenterNewImpl;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamMainNewView;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.AnswerCardView;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.ShadowDrawable;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.LinearLayoutListView;
import com.qmuiteam.qmui.widget.QMUIProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

public class VideoStudyReportFragment extends MySupportFragment {
    @BindView(R.id.xi_toolbar)
    TitleBar mTitleBar;

    @BindView(R.id.circleProgressBar)
    QMUIProgressBar mCircleProgressBar;

    @BindView(R.id.study_report_des)
    TextView mStudyReportDes;

    @BindView(R.id.study_get_coin)
    TextView mStudyGetCoin;

    @BindView(R.id.after_class_exercises_answer)
    View mAfterExerciseView;

    @BindView(R.id.class_exercises_answer)
    View mExerciseView;

    @BindView(R.id.study_report_info)
    TextView mReportInfo;

    @BindView(R.id.knowspoint_listView)
    LinearLayoutListView mknowspointsView;

    @BindView(R.id.class_coin_tip)
    TextView mCoinTipTv;

    @BindView(R.id.live_time_report)
    View mLiveTimeView;

    @BindView(R.id.class_work_layout)
    View mInClassLayoutView;

    @BindView(R.id.home_work_layout)
    View mHomeWorkLayoutView;

    @BindView(R.id.continue_points)
    View mContinueTitleView;

    private int mLearnTime;

    Map<String, Object> params = new HashMap<>();

    @Override
    protected int getContentView() {
        return R.layout.video_study_report_layout;
    }

    private CompositeSubscription mCompositeSubscription = null;

    protected CompositeSubscription getSubscription() {
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    public static void lanuch(Context context, String netClassId, PurchasedCourseBean.Data item, String netClassName) {
        FragmentParameter tmpPar = new FragmentParameter(VideoStudyReportFragment.class);
        tmpPar.mParams = new Bundle();
        tmpPar.mParams.putString("netClassId", netClassId);
        tmpPar.mParams.putSerializable("courseData", item);
        tmpPar.mParams.putString("netClassName", netClassName);
        UIJumpHelper.jumpSupportFragment(context, tmpPar, true);
    }

    @Override
    protected void setListener() {
        mTitleBar.setBackgroundColor(0X00000000);
        mTitleBar.setTitle("学习报告");
        mTitleBar.setShadowVisibility(View.GONE);
        mTitleBar.setTitleTextColor(Color.BLACK);
        mTitleBar.setDisplayHomeAsUpEnabled(true, R.drawable.icon_arrow_left);
        mTitleBar.setOnTitleBarMenuClickListener(new TitleBar.OnTitleBarMenuClickListener() {
            @Override
            public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
                if (menuItem.getId() == R.id.xi_title_bar_home) {
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // mCircleProgressBar.setFontType("font/851-CAI978.ttf");
        Bundle bundle = getArguments();
        if (bundle != null) {
            PurchasedCourseBean.Data item = (PurchasedCourseBean.Data) bundle.getSerializable("courseData");
            int videoType = item.videoType;
            String bjyRoomId = item.bjyRoomId;
            int classCardId = item.classCardId;
            int classId = item.classId;
            long exerciseCardId = item.answerCard.id;
            int lessonId = item.coursewareId;
            String netClassId = bundle.getString("netClassId");
            int reportStatus = item.reportStatus;
            String syllabusId = item.id;
            loadData(bjyRoomId, classCardId, classId, exerciseCardId, lessonId, netClassId, videoType, reportStatus, syllabusId);
            mReportInfo.setText(R.string.no_report);
            if (videoType == 1) {
                mCircleProgressBar.setVisibility(View.GONE);

            } else {
                mCircleProgressBar.setShadowLayer(3, 0, 4, Color.parseColor("#ffefbd51"));
                mCircleProgressBar.setXoffset(0);
                mCircleProgressBar.setQMUIProgressBarTextGenerator(new QMUIProgressBar.QMUIProgressBarTextGenerator() {
                    @Override
                    public String generateText(QMUIProgressBar progressBar, int value, int maxValue) {
                        return mLearnTime + "";
                    }
                });
            }
            String courseName = bundle.getString("netClassName");
            params.put("report_type", "学习报告");
            params.put("class_id", item.coursewareId + "");
            params.put("class_title", item.title);
            if (!TextUtils.isEmpty(courseName))
                params.put("course_title", courseName);
            params.put("course_id", netClassId);
        }
    }

    private SpannableString getSpannableString(String content, int startlen, String key, int middlen) {
        SpannableString msp = new SpannableString(content);
        msp.setSpan(new ForegroundColorSpan(0XFFFF6D73), 0, startlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色

        msp.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(getContext(), 18)), 0, startlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, startlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体

        int index = content.indexOf(key);
        if (index > 0) {
            msp.setSpan(new ForegroundColorSpan(0XFF4A4A4A), index, index + middlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
            msp.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(getContext(), 12)), index, index + middlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体
        }
        return msp;
    }

    private SpannableString getSpannableStringBlack(String content, int startlen, String key, int middlen) {
        SpannableString msp = new SpannableString(content);
        msp.setSpan(new ForegroundColorSpan(Color.parseColor("#4A4A4A")), 0, startlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色

        msp.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(getContext(), 18)), 0, startlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, startlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体

        int index = content.indexOf(key);
        if (index > 0) {
            msp.setSpan(new ForegroundColorSpan(0XFF4A4A4A), index, index + middlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
            msp.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(getContext(), 12)), index, index + middlen, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体
        }
        return msp;
    }

    private void setStudyDes(int coin, String completePercent, String avaragePercent, String teacherCommon) {
        if (coin > 0) {
            String getCoinInfo = "听课获得" + coin + "图币";
            SpannableString msp = new SpannableString(getCoinInfo);
            int startIndex = getCoinInfo.indexOf(coin);
            msp.setSpan(new ForegroundColorSpan(Color.parseColor("#A45611")), startIndex, startIndex + (coin + "").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
            mStudyGetCoin.setText(msp);
        } else
            mStudyGetCoin.setVisibility(View.GONE);
        String des = TextUtils.isEmpty(teacherCommon) ? "听取了课程" + completePercent + "的内容，完成率高于全班" + avaragePercent + "的同学" : teacherCommon;
        SpannableString msp2 = new SpannableString(des);
        if (des.contains(completePercent)) {
            int desStartIndex = des.indexOf(completePercent);
            msp2.setSpan(new ForegroundColorSpan(Color.parseColor("#A45611")), desStartIndex, desStartIndex + completePercent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
        }
        if (des.contains(avaragePercent)) {
            int avarageStartIndex = des.lastIndexOf(avaragePercent);
            msp2.setSpan(new ForegroundColorSpan(Color.parseColor("#A45611")), avarageStartIndex, avarageStartIndex + avaragePercent.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
        }
        mStudyReportDes.setText(msp2);
    }

    private void bindAnswerView(AnswerCardView cardView, VideoStudyReportBean.CourseWorkPractice courseWorkPractice, final String id) {
        List<AnswerCardView.CardAnswer> pointList = new ArrayList<>();
        for (int i = 0; i < courseWorkPractice.corrects.size(); i++) {
            AnswerCardView.CardAnswer tmpData = new AnswerCardView.CardAnswer();
            tmpData.status = courseWorkPractice.corrects.get(i);
            tmpData.index = (i + 1) + "";
            tmpData.day = "";
            pointList.add(tmpData);
        }
        cardView.setDateList(pointList);

//        cardView.setOnClickCardViewListener(new AnswerCardView.OnClickCardViewListener() {
//
//             public void onClickCurrentCard(AnswerCardView.CardAnswer data) {
//                 int index=StringUtils.parseInt(data.index)-1;
//                 showAnswerAnalysis(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL,StringUtils.parseLong(id), index<0?0:index);
//            }
//         });
    }

    CustomLoadingDialog mLoadingDialog;

    /**
     * 获得报告信息
     */
    private void loadData(String bjyRoomId, int classCardId, final int classId, long exerciseCardId, int lessonId, final String netClassId, final int videoType, int reportStatus, String syllId) {
        mLoadingDialog = DialogUtils.showLoading(getActivity(), mLoadingDialog);
        NetObjResponse callback = new NetObjResponse<VideoStudyReportBean>() {
            @Override
            public void onSuccess(BaseResponseModel<VideoStudyReportBean> model) {
                DialogUtils.dismissLoading(mLoadingDialog);
                VideoStudyReportBean reportBean = model.data;
                //随堂练习
                if (reportBean.classPractice == null || reportBean.classPractice.practiceStatus == 0)//没有随堂练
                    mInClassLayoutView.setVisibility(View.GONE);
                else if (reportBean.classPractice.practiceStatus == 2)//没有做随堂练
                    replaceNoAnwserView(mExerciseView, true, reportBean.classPractice.id, videoType);
                else if (reportBean.classPractice.practiceStatus == 1) {
                    replaceAnwserView(mExerciseView, reportBean.classPractice, reportBean.classPractice.id, true);
                    if (reportBean.classPractice.coin > 0) {
                        mCoinTipTv.setVisibility(View.VISIBLE);
                        mCoinTipTv.setText("获得" + reportBean.classPractice.coin + "图币");
                    }
                }
                //课后作业
                if (reportBean.courseWorkPractice.practiceStatus == 0 && TextUtils.equals(reportBean.courseWorkPractice.id, "0"))//没有课后作业
                    mHomeWorkLayoutView.setVisibility(View.GONE);
                else if (reportBean.courseWorkPractice.practiceStatus == 1)
                    replaceAnwserView(mAfterExerciseView, reportBean.courseWorkPractice, reportBean.courseWorkPractice.id, false);
                else if (reportBean.courseWorkPractice.practiceStatus == 2 || !TextUtils.equals(reportBean.courseWorkPractice.id, "0"))//没有做课后作业
                    replaceNoAnwserView(mAfterExerciseView, false, reportBean.courseWorkPractice.id, videoType);

                if (!Utils.isEmptyOrNull(reportBean.teacherComment))
                    mReportInfo.setText(reportBean.teacherComment);
                else
                    mReportInfo.setVisibility(View.GONE);

                // 直播学习
                if (videoType == 1 || reportBean.liveReport == null)
                    mLiveTimeView.setVisibility(View.GONE);
                else if (reportBean.liveReport != null) {
                    mLearnTime = reportBean.liveReport.learnTime;
                    mCircleProgressBar.setMaxValue(100);
                    mCircleProgressBar.setProgress(reportBean.liveReport.learnPercent, true);
                    setStudyDes(reportBean.liveReport.gold, reportBean.liveReport.learnPercent + "%", reportBean.liveReport.abovePercent + "%", reportBean.liveReport.teacherComment);
                }
                if (reportBean.points == null || reportBean.points.size() == 0)
                    mContinueTitleView.setVisibility(View.GONE);
                else {
                    mContinueTitleView.setVisibility(View.VISIBLE);
                    mknowspointsView.setAdapter(new KnowsPointlistAdapter(reportBean.points));
                }

                params.put("correct_number", reportBean.getTotalRightCount());
                params.put("exercise_done", reportBean.getWriteCount());
                params.put("exercise_number", reportBean.getAllQuestionCount());
                params.put("exercise_duration", reportBean.getTotalTime());
                SensorStatisticHelper.sendTrack("HuaTuOnline_app_pc_HuaTuOnline_ViewReport", params);
//                StudyCourseStatistic.sendTrackWithCourseInfo("HuaTuOnline_app_pc_HuaTuOnline_ViewReport", netClassId, params);
            }

            @Override
            public void onError(String message, int type) {
                DialogUtils.dismissLoading(mLoadingDialog);
            }
        };
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getVideoStudyReport(bjyRoomId, classCardId, classId, exerciseCardId, lessonId, netClassId, videoType, reportStatus, syllId), callback);
    }

    /**
     * 做题情况页面
     *
     * @param targView
     * @param courseWorkPractice
     * @param id
     */
    private void replaceAnwserView(View targView, VideoStudyReportBean.CourseWorkPractice courseWorkPractice, final String id, final boolean isInClass) {
        ReplaceViewHelper viewHelper = new ReplaceViewHelper(getContext());
        View answerView = View.inflate(getContext(), R.layout.video_report_answer_layout, null);
        viewHelper.toReplaceView(targView, answerView);
        AnswerCardView classListView = answerView.findViewById(R.id.answerCard_class);
        TextView costTimeTxt = answerView.findViewById(R.id.right_answer_time_in);
        TextView scoreMaxTxt = answerView.findViewById(R.id.one_answer_time_in);
        TextView averageScoreTxt = answerView.findViewById(R.id.all_right_answer_time_in);
        TextView averageClassScoreTxt = answerView.findViewById(R.id.all_answer_time_class);
        bindAnswerView(classListView, courseWorkPractice, id);
        String correntCount = isInClass ? courseWorkPractice.classAverageRcount + "" : courseWorkPractice.avgCorrect + "";
        costTimeTxt.setText(getSpannableString(courseWorkPractice.rcount + " 题" + "\n" + "答对", (courseWorkPractice.rcount + "").length(), "秒", 1));
        scoreMaxTxt.setText(getSpannableString(courseWorkPractice.getMyCostTime() + " 秒/题" + "\n" + "平均用时", (courseWorkPractice.getMyCostTime() + "").length(), "秒", 1));
        averageScoreTxt.setText(getSpannableStringBlack(correntCount + " 题" + "\n" + "平均答对", (correntCount).length(), "题", 1));
        averageClassScoreTxt.setText(getSpannableStringBlack(courseWorkPractice.getClassCostTime() + " 秒" + "\n" + "平均用时", (courseWorkPractice.getClassCostTime() + "").length(), "秒", 1));
        answerView.findViewById(R.id.all_parse_btn_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAnswer(id, true);
            }
        });
    }

    /**
     * 作业没有做页面
     *
     * @param targView
     * @param isExercNoAfter
     * @param id
     */
    private void replaceNoAnwserView(View targView, final boolean isExercNoAfter, final String id, int videoType) {
        ReplaceViewHelper viewHelper = new ReplaceViewHelper(getContext());
        View answerView = View.inflate(getContext(), R.layout.video_report_noanswer_layout, null);
        TextView continueDes = answerView.findViewById(R.id.continue_study_des);
        TextView continueBtn = answerView.findViewById(R.id.continue_study_btn);
        if (isExercNoAfter) {
            continueDes.setText(videoType == 1 ? R.string.report_no_exerc_des : R.string.report_no_exerc_live_des);
            continueBtn.setText(R.string.report_no_exerc_btn);
        } else {
            continueDes.setText(R.string.report_no_after_exerc_des);
            continueBtn.setText(R.string.report_no_after_exerc_btn);
        }
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExercNoAfter)
                    getActivity().finish();
                else
                    gotoAnswer(id, false);
            }
        });
        viewHelper.toReplaceView(targView, answerView);
        ShadowDrawable.setShadowDrawable(answerView, Color.parseColor("#00ffffff"), DensityUtils.dp2px(getContext(), 8),
                Color.parseColor("#66000000"), DensityUtils.dp2px(getContext(), 8), 0, 0, ShadowDrawable.SHAPE_ROUND);
    }

    /**
     * 知识点适配器
     */
    class KnowsPointlistAdapter extends BaseAdapter {
        private List<HomeTreeBeanNew> mKonwsPoints;

        public KnowsPointlistAdapter(List<HomeTreeBeanNew> namelist) {
            mKonwsPoints = namelist;
        }

        @Override
        public int getCount() {
            return ArrayUtils.size(mKonwsPoints);
        }

        @Override
        public Object getItem(int position) {
            return mKonwsPoints.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.item_point_layout, null);
            final HomeTreeBeanNew point = (HomeTreeBeanNew) getItem(position);
            TextView courseName = convertView.findViewById(R.id.class_name);
            convertView.findViewById(R.id.class_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPointsInfo(point);
                }
            });
            courseName.setText(point.getName());
            return convertView;
        }

    }

    /**
     * 错题解析、全部解析
     * 去答题/看题页面
     * 全部解析type ：ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
     * 全部解析type ：ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG
     */
    private ArenaExamPresenterNewImpl mPresenter;                       // 网络访问
    private RealExamBeans.RealExamBean mCurRealExamBean;
    private int mShowIndex = 0;

    private void showAnswerAnalysis(final int requestType, long practId, int showIndex) {
//        if(null!=mCurRealExamBean){
//            goArenaExamActivityNew(requestType, mCurRealExamBean);
//            return;
//        }
        mShowIndex = showIndex;
        if (null == mPresenter) {
            mPresenter = new ArenaExamPresenterNewImpl(getSubscription(), new ArenaExamMainNewView() {
                // 得到试卷信息
                public void onGetPractiseData(RealExamBeans.RealExamBean beans, List<ArenaExamQuestionBean> questionBeans) {
                    mCurRealExamBean = beans;
                    goArenaExamActivityNew(requestType, beans, mShowIndex);
                    mShowIndex = 0;
                }

                // 返回退出，保存答案成功
                public void onSavedAnswerCardSuccess(int saveType) {
                }

                // 获取数据失败
                public void onLoadDataFailed(int flag) {
                    CommonUtils.showToast(flag <= 1 ? "无网络,请检查网络" : "获取练习信息失败，请您重试");
                }

                public void showProgressBar() {
                    mLoadingDialog = DialogUtils.showLoading(getActivity(), mLoadingDialog);
                }

                public void dismissProgressBar() {
                    DialogUtils.dismissLoading(mLoadingDialog);
                }

                public void onSetData(Object respData) {
                }

                public void onLoadDataFailed() {
                }
            });
        }
        mPresenter.getPractiseDetails(practId);
    }

    /**
     * 错题解析、全部解析 ArenaExamPresenterNewImpl 做题获取答题卡&题的类，576行就是获取到报告数据之后的处理。
     * 去答题/看题页面
     * 全部解析type ：ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
     * 全部解析type ：ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG
     */
    private final void goArenaExamActivityNew(int requestType, RealExamBeans.RealExamBean realExamBean, int showIndex) {
        if (realExamBean == null) return;
        Bundle bundle = new Bundle();
        ArenaDataCache.getInstance().realExamBean = realExamBean;
        bundle.putInt("showIndex", showIndex);
        ArenaExamActivityNew.show(getActivity(), requestType, bundle);
    }


    //知识点考点  参考HomeFragment下的goAnswer(
    private void showPointsInfo(HomeTreeBeanNew tree) {

        if (tree.getQnum() > 0) {
            Bundle bundle = new Bundle();
            if (tree.getUnfinishedPracticeId() > 0) {
                bundle.putBoolean("continue_answer", true);
                bundle.putLong("practice_id", tree.getUnfinishedPracticeId());
                bundle.putLong("point_ids", tree.getId());
            } else {
                bundle.putLong("point_ids", tree.getId());
            }
            ArenaExamActivityNew.show(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI, bundle);
        } else {
            Toast.makeText(getActivity(), "暂无题目", Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoAnswer(String id, boolean hasFinish) {
        if (hasFinish) {
            showAnswerAnalysis(ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL, StringUtils.parseLong(id), 0);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("practice_id", StringUtils.parseLong(id));
        bundle.putBoolean("continue_answer", true);
        ArenaExamActivityNew.show(getActivity(), ArenaConstant.EXAM_ENTER_FORM_TYPE_COURSE_EXERICE, bundle);
        getActivity().finish();
    }
}
