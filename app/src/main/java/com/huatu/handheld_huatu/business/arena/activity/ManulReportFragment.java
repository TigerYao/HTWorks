package com.huatu.handheld_huatu.business.arena.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.ExplandListAdapter;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.business.arena.utils.LUtils;
import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamCheckDetailV2;
import com.huatu.handheld_huatu.business.essay.examfragment.FeedbackDialogFragment;
import com.huatu.handheld_huatu.business.essay.video.CustomAudioPlayerView;
import com.huatu.handheld_huatu.business.matchsmall.adapter.ExescisRankAdapter;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseReportBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.handheld_huatu.view.CustomTTFTypeFaceSpan;
import com.huatu.handheld_huatu.view.ListViewForScroll;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

public class ManulReportFragment extends MySupportFragment implements View.OnClickListener {
    @BindView(R.id.score_tv)
    TextView mMyScore;

    @BindView(R.id.nestedScrollingView)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.tv_check_time)
    TextView tvCheckTime;                   // 批改时间

    @BindView(R.id.feedback_tv)
    TextView mEvaluateTv1;
    @BindView(R.id.feedback_tv1)
    TextView mEvaluateTv2;

    @BindView(R.id.my_position)
    TextView mMyPosition;//我的排名
    @BindView(R.id.hight_score)
    TextView mHeightScore; //最高分

    @BindView(R.id.average_score)
    TextView mAvarageScore;//平均分
    @BindView(R.id.exam_info_tv)
    TextView mExamInfoView;
    @BindView(R.id.teacher_voice_layout)
    LinearLayout teacherVoidcLayout;


    @BindView(R.id.teacher_voice)
    CustomAudioPlayerView mediaPlayerView;
    @BindView(R.id.tv_submit_time)
    TextView mSubmitTime;

    @BindView(R.id.exam_info_layout)
    View mExamInfolayout;//做题情况信息
    @BindView(R.id.read_paper_layout)
    View mReadPaperlayout;//综合阅卷布局
    @BindView(R.id.exam_info_listview)
    ListViewForScroll mExamInfoList;             // 做题内容分类统计
    @BindView(R.id.read_info_listview)
    ListViewForScroll mReadInfoList;             // 综合阅卷内容分类统计
    @BindView(R.id.go_check_detail_tv)
    View mGoToCheckDetialView;        //跳转到批改详情页
    @BindView(R.id.title_read)
    TextView mReadTitle;

    @BindView(R.id.err_view)
    CommonErrorView errorView;

    //优秀排名榜
    @BindView(R.id.class_info_layout)
    View mClassListLayout;
    @BindView(R.id.study_listview)
    ListView mRangeListView;
    @BindView(R.id.range_view)
    View mRangeView;
    @BindView(R.id.user_range_layout)
    View mUserRangeLayout;
    @BindView(R.id.tv_my_rank)
    TextView mMyRankTv;
    @BindView(R.id.list_expland_img)
    TextView mExpandMoreView;

    @BindView(R.id.go_second_report)
    TextView mReAnswerView;

    TextView mEvaluateTv;

    private ExerciseReportBean mReportInfo;
    private long mAnswerId;
    private boolean isSingle; //类型 单题 （包括文章写作）1套题
    private int courseType;
    private long courseId;
    private long courseWareId;
    private long syllabusId;
    private String titleView;


    private CustomLoadingDialog mLoadingDialog;
    private Toast mToastTip;
    private ExescisRankAdapter mAdapter;
    private Bundle arguments;

    public static void lanuch(Context context, Bundle arg) {
        FragmentParameter tmpPar = new FragmentParameter(ManulReportFragment.class, arg);
        UIJumpHelper.jumpSupportFragment(context, tmpPar, 1);
    }

    /**
     * @param context
     * @param answerId
     * @param isSingle
     * @param courseType   // 1、点播 2、直播 3、回放
     * @param courseId     // 课程Id
     * @param courseWareId // 课件ID
     * @param syllabusId   // 大纲ID
     * @param titleView    // title可不传
     * @param areaName     // areaName
     * @param correctNum   // correctNum批改次数
     */
    public static void lanuch(Context context, long answerId, boolean isSingle, int courseType, long courseId, long courseWareId, long syllabusId
            , String titleView, String areaName, int correctNum) {
        Bundle bundle = new Bundle();
        bundle.putLong("answerId", answerId);
        bundle.putBoolean("isSingle", isSingle);
        bundle.putInt("courseType", courseType);
        bundle.putLong("courseId", courseId);
        bundle.putLong("courseWareId", courseWareId);
        bundle.putLong("syllabusId", syllabusId);
        bundle.putString("titleView", titleView);
        bundle.putString("areaName", areaName);
        bundle.putInt("correctNum", correctNum);
        lanuch(context, bundle);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_afterclass_exam_report;
    }

    private CompositeSubscription mCompositeSubscription = null;

    protected CompositeSubscription getSubscription() {
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    private boolean mPlayingAudioPause = false;      //音频的暂停
    @Override
    public void onDestroyView() {
        if (mediaPlayerView != null)
            mediaPlayerView.releasePlayer();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mediaPlayerView) {
            if (mPlayingAudioPause) {
                mediaPlayerView.resumePlay();
                mPlayingAudioPause = false;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mediaPlayerView) {
            if (mediaPlayerView.isPlaying()) {
                mPlayingAudioPause = true;
                mediaPlayerView.pausePlay();
            }
        }
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
        arguments = getArguments();
        if (arguments != null) {
            mAnswerId = arguments.getLong("answerId", 0);
            isSingle = arguments.getBoolean("isSingle");
            courseType = arguments.getInt("courseType");
            courseId = arguments.getLong("courseId");
            courseWareId = arguments.getLong("courseWareId");
            syllabusId = arguments.getLong("syllabusId");
            titleView = arguments.getString("titleView");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onLoadData();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.iv_des).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonUtils.isFastDoubleClick(3000)) return;
                if (mToastTip == null)
                    mToastTip = ToastUtils.makeText(getContext(), null, Gravity.TOP | Gravity.RIGHT, R.layout.toast_manul_report_tip_layout);
                mToastTip.show();
            }
        });
        ((ViewGroup.MarginLayoutParams) mRangeView.getLayoutParams()).leftMargin = DisplayUtil.dp2px(10);
        errorView.setErrorImageVisible(true);
        errorView.setOnReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoadData();
            }
        });
        mNestedScrollView.setNestedScrollingEnabled(false);
        view.findViewById(R.id.iv_back).setOnClickListener(this);
        view.findViewById(R.id.go_check_detail_tv).setOnClickListener(this);
        mReadTitle.setText(isSingle ? "本题阅卷" : "综合阅卷");
        mEvaluateTv = !isSingle ? mEvaluateTv2 : mEvaluateTv1;
        mEvaluateTv.setVisibility(View.VISIBLE);
        view.findViewById(R.id.go_second_report).setOnClickListener(this);
        mEvaluateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReportInfo != null) {
                    final FeedbackDialogFragment ratefragment = FeedbackDialogFragment.getInstance(mAnswerId, isSingle ? 0 : 1, mReportInfo.feedBackContent, mReportInfo.feedBackStar);
                    ratefragment.show(getFragmentManager(), getClass().getSimpleName());
                    ratefragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (mReportInfo.feedBackStatus != 1 && ratefragment.mIsSuccess) {
                                mReportInfo.feedBackStatus = 1;
                                mEvaluateTv.setText("已评价");
                                mReportInfo.feedBackStar = ratefragment.feedStar;
                                mReportInfo.feedBackContent = ratefragment.feedbackContent;
                            }
                        }
                    });
                }
            }
        });
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(DisplayUtil.dp2px(8));
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
        gradientDrawable.setColors(new int[]{Color.parseColor("#fffff797"), Color.parseColor("#ffffe43e")});
        mEvaluateTv.setBackground(gradientDrawable);
    }

    protected void onLoadData() {
        if (!NetUtil.isConnected()) {
            showError(0);
            return;
        }
        if (mAnswerId == 0) {
            showError(1);
            return;
        }
        showProgress();
        NetResponse response = new NetResponse() {

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mReportInfo = (ExerciseReportBean) model.data;
                fillData();
                if (errorView.getVisibility() == View.VISIBLE)
                    errorView.setVisibility(View.GONE);
                hideProgress();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                showError(1);
                hideProgress();
            }
        };
        ServiceProvider.exerciseReport(getSubscription(), isSingle, mAnswerId, syllabusId, response);
    }

    private void showProgress() {
        mLoadingDialog = DialogUtils.showLoading(getActivity(), mLoadingDialog);
    }

    private void hideProgress() {
        DialogUtils.dismissLoading(mLoadingDialog);
    }

    private void fillData() {
        if (mReportInfo.feedBackStatus == 1) {
            mEvaluateTv.setText("已评价");
        }
        if (!Utils.isEmptyOrNull(mReportInfo.submitTime)) {
            mSubmitTime.setVisibility(View.VISIBLE);
            mSubmitTime.setText(mReportInfo.submitTime);
        } else
            mSubmitTime.setVisibility(View.INVISIBLE);
        mMyRankTv.setText("我的排名 " + mReportInfo.totalRank + "名");
        tvCheckTime.setText("批改时间：" + mReportInfo.correctDate);
        if (mReportInfo.correctNum >= 1 && mReportInfo.otherAnswerCardId > 0 && mReportInfo.otherAnswerBizStatus == 3) {
            mReAnswerView.setText(mReportInfo.correctNum == 1 ? "作答报告二" : "作答报告一");
        }
        setMyScore(LUtils.formatPoint(mReportInfo.examScore), LUtils.formatPoint(mReportInfo.score));
        setMyPosition(TimeUtils.getSecond2OnlyMinTime(mReportInfo.avgSpendTime), LUtils.formatPoint(mReportInfo.avgScore) + "", LUtils.formatPoint(mReportInfo.maxScore));
        setExamInfo(mReportInfo.questionCount + "", mReportInfo.unfinishedCount + "", TimeUtils.getSecond2MinTime(mReportInfo.spendTime));
        if (mReportInfo.questionVOList == null || mReportInfo.questionVOList.isEmpty()) {
            mExamInfolayout.setVisibility(View.GONE);
        } else {
            mExamInfoList.setAdapter(new CommonAdapter<ExerciseReportBean.QuestionVO>(
                    getContext(), mReportInfo.questionVOList, R.layout.exam_maunal_report_item_layout) {
                @Override
                public void convert(ViewHolder holder, ExerciseReportBean.QuestionVO item, int position) {
                    holder.setText(R.id.type_name, "问题" + item.sort + "-" + item.typeName);
                    holder.setText(R.id.sc_essay_content, "得分" + ArenaHelper.setNoZero(item.examScore + "")
                            + "/" + ArenaHelper.setNoZero(item.score + ""));// + "，" + item.getInputWordNum() + "字");
//                    TextView detailTv = holder.getView(R.id.type_detail);
//                    detailTv.setVisibility(View.VISIBLE);
//                    detailTv.setText(item.typeName);
                }
            });
        }

        if (mReportInfo.userScoreRankList == null || mReportInfo.userScoreRankList.isEmpty()) {
            mUserRangeLayout.setVisibility(View.GONE);
        } else {
            mAdapter = new ExescisRankAdapter(getActivity(), mReportInfo.userScoreRankList);
            mRangeListView.setAdapter(mAdapter);
            if (mAdapter.getRealCount() <= 3) {
                mExpandMoreView.setText("没有更多了");
                mExpandMoreView.setClickable(false);
            } else {
                mExpandMoreView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAdapter.getCount() == ExplandListAdapter.ExplandCount) {
                            mAdapter.setItemNum(mAdapter.getRealCount());
                            ((TextView) v).setText("收起");
                            mAdapter.notifyDataSetChanged();
                        } else {
                            mAdapter.setItemNum(ExplandListAdapter.ExplandCount);
                            ((TextView) v).setText("查看更多");
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }

        if (mReportInfo.remarkList != null && !mReportInfo.remarkList.isEmpty()) {
            mReadInfoList.setAdapter(new CommonAdapter<ExerciseReportBean.RemarkList>(getContext(), mReportInfo.remarkList, android.R.layout.simple_list_item_1) {
                @Override
                public void convert(ViewHolder holder, ExerciseReportBean.RemarkList item, int position) {
                    holder.setText(android.R.id.text1, (position + 1) + "." + item.content);
                }
            });
        } else {
            if (isSingle) {
                mReadPaperlayout.setVisibility(View.GONE);
            } else
                mReadInfoList.setVisibility(View.GONE);
        }
        if (mReportInfo.audioId > 0 && !Utils.isEmptyOrNull(mReportInfo.audioToken)) {
            teacherVoidcLayout.setVisibility(View.VISIBLE);
            mediaPlayerView.releasePlayer();
            mediaPlayerView.setAudioData(null, mReportInfo.audioId, mReportInfo.audioToken);
        } else {
            teacherVoidcLayout.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.iv_back, R.id.ll_go_check, R.id.err_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:              // 点击返回，finish页面
                getActivity().finish();
                break;
            case R.id.go_check_detail_tv:          // 去查看批改详情
                EssayExamCheckDetailV2 curFragment = new EssayExamCheckDetailV2();
                curFragment.mIsFromReport = true;

                if (arguments == null) {
                    arguments = new Bundle();
                }
                arguments.putLong("answerId", mAnswerId);
                arguments.putLong("questionBaseId", mReportInfo.questionBaseId);
                arguments.putLong("paperId", mReportInfo.paperId);
                arguments.putInt("correctMode", mReportInfo.correctMode);
                arguments.putInt("request_type", EssayExamActivity.ESSAY_EXAM_HOMEWORK);

                curFragment.setArguments(arguments);
                start(curFragment);

                if ((null!=mediaPlayerView)&&(mediaPlayerView.isPlaying())) {
                    mediaPlayerView.forcePausePlay();
                }
                //  EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_SHOW_ESSAY_REPORT));
                break;
            case R.id.err_view:             // 点击重试
                onLoadData();
                break;
            case R.id.go_second_report:
                if (mReportInfo.correctNum == 0 || mReportInfo.otherAnswerCardId == 0 || mReportInfo.otherAnswerBizStatus != 3) {
                    Bundle arguments = getArguments();
                    Bundle bundle = new Bundle();
                    bundle.putString("areaName", arguments != null ? arguments.getString("areaName") : "");
                    if (mReportInfo.otherAnswerBizStatus == 5){
                        bundle.putLong("answerId", mReportInfo.otherAnswerCardId);    //人工批改，被退回，修改答案的时候，需要答题卡Id
                        bundle.putInt("bizStatus", mReportInfo.otherAnswerBizStatus);
                    }
                    EssayRoute.goEssayHomeworkAnswer(getContext(),
                            isSingle,
                            titleView,
                            mReportInfo.questionBaseId,
                            mReportInfo.paperId,
                            courseType,
                            courseId,
                            courseWareId,
                            syllabusId,
                            bundle);
                    getActivity().finish();
                } else {
                    long temp = mReportInfo.otherAnswerCardId;
                    mReportInfo.otherAnswerCardId = mAnswerId;
                    mAnswerId = temp;
                    onLoadData();
                }
                break;
        }
    }

    private void setMyScore(String myScore, String totalScore) {
        CustomTTFTypeFaceSpan ttfTypeFaceSpan = new CustomTTFTypeFaceSpan("font/851-CAI978.ttf", getContext());
        String score = String.format(getResources().getString(R.string.manual_check_myscore), myScore, totalScore);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(score);
        int myScoreIndex = score.indexOf(myScore);
        int myScoreEnd = myScoreIndex + myScore.length();
        int totalScoreIndex = score.indexOf(totalScore);
        int totalScoreEnd = totalScoreIndex + totalScore.length();
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(DisplayUtil.sp2px(40));
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(ttfTypeFaceSpan), myScoreIndex, myScoreEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(absoluteSizeSpan), myScoreIndex, myScoreEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(styleSpan), totalScoreIndex, totalScoreEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMyScore.setText(spannableStringBuilder);
    }

    private void setMyPosition(String position, String hightScore, String avarageScore) {
        setTTFType(position, position, mMyPosition);
        setTTFType(hightScore, hightScore, mHeightScore);
        setTTFType(avarageScore, avarageScore, mAvarageScore);
        mMyPosition.append("分钟");
        mHeightScore.append("分");
        mAvarageScore.append("分");
    }

    private void setExamInfo(String totalQuestion, String rightQuestion, String time) {
        String examInfo = String.format(getResources().getString(R.string.manual__exam_info), totalQuestion, rightQuestion, time);
        CustomTTFTypeFaceSpan ttfTypeFaceSpan = new CustomTTFTypeFaceSpan("font/851-CAI978.ttf", getContext());
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FF6D73"));
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(DisplayUtil.dp2px(14));

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(examInfo);
        int startIndex = examInfo.indexOf(totalQuestion);
        int endIndex = startIndex + totalQuestion.length();

        spannableStringBuilder.setSpan(CharacterStyle.wrap(ttfTypeFaceSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(foregroundColorSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(absoluteSizeSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String examInfo1 = examInfo.substring(endIndex);
        startIndex = endIndex + examInfo1.indexOf(rightQuestion);
        endIndex = startIndex + rightQuestion.length();

        spannableStringBuilder.setSpan(CharacterStyle.wrap(ttfTypeFaceSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(foregroundColorSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(absoluteSizeSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String examInfo2 = examInfo.substring(endIndex);
        startIndex = endIndex + examInfo2.indexOf(time);
        endIndex = startIndex + time.length();

        spannableStringBuilder.setSpan(CharacterStyle.wrap(ttfTypeFaceSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(foregroundColorSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(absoluteSizeSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mExamInfoView.setText(spannableStringBuilder);
    }

    private void setTTFType(String content, String changeText, TextView textView) {
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(DisplayUtil.dp2px(18));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FF5F5C"));
        CustomTTFTypeFaceSpan ttfTypeFaceSpan = new CustomTTFTypeFaceSpan("font/851-CAI978.ttf", getContext());
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        int startIndex = TextUtils.equals(content, changeText) ? 0 : content.indexOf(changeText);
        int endIndex = TextUtils.equals(content, changeText) ? content.length() : startIndex + changeText.length();
        spannableStringBuilder.setSpan(CharacterStyle.wrap(ttfTypeFaceSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(absoluteSizeSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(foregroundColorSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableStringBuilder);
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

    @Override
    public void onDetach() {
        super.onDetach();
        if (mToastTip != null) {
            mToastTip.cancel();
            mToastTip = null;
        }
    }
}
