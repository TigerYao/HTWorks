package com.huatu.handheld_huatu.business.arena.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.business.arena.utils.LUtils;
import com.huatu.handheld_huatu.business.essay.bean.EssayPaperReport;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayExamCheckDetailV2;
import com.huatu.handheld_huatu.business.essay.examfragment.FeedbackDialogFragment;
import com.huatu.handheld_huatu.business.essay.video.CustomAudioPlayerView;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomTTFTypeFaceSpan;
import com.huatu.handheld_huatu.view.ListViewForScroll;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

public class ManualCheckReportActivity extends BaseActivity {
    @BindView(R.id.root_view)
    View rootView;
    @BindView(R.id.xi_toolbar)
    TitleBar mTitleBar;
    @BindView(R.id.score_tv)
    TextView mMyScore;

    @BindView(R.id.tv_check_time)
    TextView tvCheckTime;                   // 批改时间

    @BindView(R.id.my_position)
    TextView mMyPosition;//我的排名
    @BindView(R.id.total_rang)
    TextView mAllRange;//总人数
    @BindView(R.id.ll_rank_up)
    LinearLayout llRankUpDown;// 上升下降布局
    @BindView(R.id.iv_rank_up)
    ImageView ivRankUpDown; // 上升下降箭头
    @BindView(R.id.tv_rank_up)
    TextView tvRankUpDown;   // 排名上升下降数量

    @BindView(R.id.hight_score)
    TextView mHeightScore; //最高分
    @BindView(R.id.ll_score_up)
    LinearLayout llScoreUpDown; // 上升下降布局
    @BindView(R.id.iv_score_up)
    ImageView ivScoreUpDown; // 上升下降箭头
    @BindView(R.id.tv_score_up)
    TextView tvScoreUpDown;    // 排名上升下降数量

    @BindView(R.id.average_score)
    TextView mAvarageScore;//平均分
    @BindView(R.id.exam_info_tv)
    TextView mExamInfoView;
    @BindView(R.id.feedback_tv)
    TextView mEvaluateTv1;
    @BindView(R.id.feedback_tv1)
    TextView mEvaluateTv2;
    @BindView(R.id.teacher_voice_layout)
    LinearLayout teacherVoidcLayout;
    @BindView(R.id.teacher_voice)
    CustomAudioPlayerView mediaPlayerView;
    @BindView(R.id.iv_up_down)
    ImageView mScoreUpDownIv;
    @BindView(R.id.tv_up_down)
    TextView mScoreUpDowTv;
    @BindView(R.id.paper_name)
    TextView mPaperNameTv;

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

    @BindView(R.id.score_change)
    View mScoreChangeLayout;

    TextView mEvaluateTv;

    private EssayPaperReport mReportInfo;
    private long mAnswerId;
    private int type; //类型 0单题 （包括文章写作）1套题


    public static void startForMsgCheckReport(Context ctx, int topicType, long answerId, long questionBaseId, long paperId) {
        Bundle bundle = new Bundle();
        bundle.putLong("answerId", answerId);
        bundle.putBoolean("isSingle", topicType != 1);
        bundle.putLong("questionBaseId", questionBaseId);
        bundle.putLong("paperId", paperId);
        startManualCheckReport(ctx, bundle);
    }

    public static void startManualCheckReport(Context ctx, Bundle bundle) {
        Intent intent = new Intent(ctx, ManualCheckReportActivity.class);
        intent.putExtra("params", bundle);
        ctx.startActivity(intent);
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_manual_report_layout;
    }

    @OnClick({R.id.go_check_detail_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.go_check_detail_tv:
                Bundle bundle = getIntent().getBundleExtra("params");
                EssayExamCheckDetailV2.lanuch(this, bundle);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getBundleExtra("params");
        mAnswerId = bundle.getLong("answerId", 0);
        if (bundle.containsKey("isSingle")) {
            type = (bundle.getBoolean("isSingle") ? 0 : 1);
        }
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Bundle bundle = getIntent().getBundleExtra("params");
        mAnswerId = bundle.getLong("answerId", 0);
        if (bundle.containsKey("isSingle")) {
            type = (bundle.getBoolean("isSingle") ? 0 : 1);
        }
        onLoadData();
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        mTitleBar.setBackgroundColor(0X00000000);
        mTitleBar.setTitle("批改报告");
        mTitleBar.setShadowVisibility(View.GONE);
        mTitleBar.setTitleTextColor(Color.WHITE);
        mTitleBar.setDisplayHomeAsUpEnabled(true, R.drawable.video_play_title_back);

        mTitleBar.setDisplayHomeAsUpEnabled(true);
        mTitleBar.setOnTitleBarMenuClickListener(new TitleBar.OnTitleBarMenuClickListener() {
            @Override
            public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
                if (menuItem.getId() == R.id.xi_title_bar_home) {
                    if (!ActivityStack.getInstance().hasRootActivity()) {
                        MainTabActivity.newIntent(ManualCheckReportActivity.this);

                    }
                    finish();
                }
            }
        });
        mEvaluateTv = type == 1 ? mEvaluateTv2 : mEvaluateTv1;
        mEvaluateTv.setVisibility(View.VISIBLE);
        mEvaluateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReportInfo != null) {
                    final FeedbackDialogFragment ratefragment = FeedbackDialogFragment.getInstance(mAnswerId, type, mReportInfo.feedBackContent, mReportInfo.feedBackStar);
                    ratefragment.show(getSupportFragmentManager(), getClass().getSimpleName());
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
        mGoToCheckDetialView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 跳转批改详情
                EssayExamCheckDetailV2.lanuch(ManualCheckReportActivity.this, getIntent().getBundleExtra("params"));
            }
        });
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(DisplayUtil.dp2px(8));
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT);
        gradientDrawable.setColors(new int[]{Color.parseColor("#fffff797"), Color.parseColor("#ffffe43e")});
        mEvaluateTv.setBackground(gradientDrawable);

        GradientDrawable gradientDrawable1 = new GradientDrawable();
        gradientDrawable1.setColors(new int[]{Color.parseColor("#FFC243"), Color.parseColor("#FFA638")});
        gradientDrawable1.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        rootView.setBackground(gradientDrawable1);
        errorView.setErrorImageVisible(true);
        errorView.setOnReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoadData();
            }
        });
        mReadTitle.setText(type == 0 ? "本题阅卷" : "综合阅卷");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayerView.resumePlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayerView.pausePlay();
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    private void setMyScore(String myScore, String totalScore) {
        CustomTTFTypeFaceSpan ttfTypeFaceSpan = new CustomTTFTypeFaceSpan("font/851-CAI978.ttf", this);
        String score = String.format(getResources().getString(R.string.manual_check_myscore), myScore, totalScore);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(score);
        int myScoreIndex = score.indexOf(myScore);
        int myScoreEnd = myScoreIndex + myScore.length();
        int totalScoreIndex = score.indexOf(totalScore);
        int totalScoreEnd = totalScoreIndex + totalScore.length();
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(DisplayUtil.dp2px(40));
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(ttfTypeFaceSpan), myScoreIndex, myScoreEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(absoluteSizeSpan), myScoreIndex, myScoreEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(CharacterStyle.wrap(styleSpan), totalScoreIndex, totalScoreEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMyScore.setText(spannableStringBuilder);
    }

    private void setMyPosition(String position, String allRang, String hightScore, String avarageScore) {
        setTTFType(position, position, mMyPosition);
        setTTFType(allRang, allRang, mAllRange);
        setTTFType(hightScore, hightScore, mHeightScore);
        setTTFType(avarageScore, avarageScore, mAvarageScore);
    }

    private void setExamInfo(String totalQuestion, String rightQuestion, String time) {
        String examInfo = String.format(getResources().getString(R.string.manual__exam_info), totalQuestion, rightQuestion, time);
        CustomTTFTypeFaceSpan ttfTypeFaceSpan = new CustomTTFTypeFaceSpan("font/851-CAI978.ttf", this);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#E4A03F"));
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
        CustomTTFTypeFaceSpan ttfTypeFaceSpan = new CustomTTFTypeFaceSpan("font/851-CAI978.ttf", this);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        int startIndex = TextUtils.equals(content, changeText) ? 0 : content.indexOf(changeText);
        int endIndex = TextUtils.equals(content, changeText) ? content.length() : startIndex + changeText.length();
        spannableStringBuilder.setSpan(CharacterStyle.wrap(ttfTypeFaceSpan), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableStringBuilder);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayerView != null)
            mediaPlayerView.releasePlayer();
    }

    @Override
    public boolean canScreenshot() {
        return false;
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
                mReportInfo = (EssayPaperReport) model.data;
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
        if (type == 0)
            ServiceProvider.getEssaySinglePaperReport(compositeSubscription, mAnswerId, response);
        else
            ServiceProvider.getEssayPaperReport(compositeSubscription, mAnswerId, response);
    }

    private void fillData() {
        if (mReportInfo.feedBackStatus == 1) {
            mEvaluateTv.setText("已评价");
//            int color = ContextCompat.getColor(this, R.color.gray002);
//            GradientDrawable gradientDrawable = (GradientDrawable) mEvaluateTv.getBackground();
//            gradientDrawable.setColors(new int[color]);
//            mEvaluateTv.setOnClickListener(null);
        }
        tvCheckTime.setText("批改时间：" + mReportInfo.getCorrectDate());
        setMyScore(LUtils.formatPoint(mReportInfo.getExamScore()), LUtils.formatPoint(mReportInfo.getScore()));
        setMyPosition(mReportInfo.getTotalRank() + "", "/" + mReportInfo.getTotalCount(), LUtils.formatPoint(mReportInfo.getMaxScore()), LUtils.formatPoint(mReportInfo.getAvgScore()));
        setExamInfo(mReportInfo.getQuestionCount() + "", mReportInfo.getUnfinishedCount() + "", TimeUtils.getSecond2MinTime(mReportInfo.getSpendTime()));
        if (mReportInfo.getExamScoreChange() == 0) {
            mScoreChangeLayout.setVisibility(View.INVISIBLE);
            mScoreUpDownIv.setVisibility(View.GONE);
        } else if (mReportInfo.getExamScoreChange() > 0) {
            mScoreChangeLayout.setVisibility(View.VISIBLE);
            mScoreUpDownIv.setRotation(180);
            mScoreUpDowTv.setText(LUtils.formatPoint(Math.abs(mReportInfo.getExamScoreChange())));
        }
        if (!Utils.isEmptyOrNull(mReportInfo.getPaperName()))
            mPaperNameTv.setText(mReportInfo.getPaperName());

        if (mReportInfo.getTotalRankChange() == 0) {
            llRankUpDown.setVisibility(View.GONE);
        } else if (mReportInfo.getTotalRankChange() < 0) {
            ivRankUpDown.setImageResource(R.mipmap.essay_report_down);
            tvRankUpDown.setTextColor(Color.parseColor("#1DD86C"));
        }
        tvRankUpDown.setText(String.valueOf(Math.abs(mReportInfo.getTotalRankChange())));

        if (mReportInfo.getMaxScoreChange() == 0) {
            llScoreUpDown.setVisibility(View.GONE);
        } else if (mReportInfo.getMaxScoreChange() < 0) {
            ivScoreUpDown.setImageResource(R.mipmap.essay_report_down);
            tvScoreUpDown.setTextColor(Color.parseColor("#1DD86C"));
        }
        tvScoreUpDown.setText(LUtils.formatPoint(Math.abs(mReportInfo.getMaxScoreChange())));
        if (mReportInfo.getQuestionVOList() == null || mReportInfo.getQuestionVOList().isEmpty()) {
            mExamInfolayout.setVisibility(View.GONE);
        } else {
            mExamInfoList.setAdapter(new CommonAdapter<EssayPaperReport.QuestionVO>(
                    ManualCheckReportActivity.this, mReportInfo.getQuestionVOList(), R.layout.exam_maunal_report_item_layout) {
                @Override
                public void convert(ViewHolder holder, EssayPaperReport.QuestionVO item, int position) {
                    holder.setText(R.id.type_name, "问题" + item.getSort() + "-" + item.getTypeName());
                    holder.setText(R.id.sc_essay_content, "得分" + ArenaHelper.setNoZero(item.getExamScore() + "")
                            + "/" + ArenaHelper.setNoZero(item.getScore() + ""));// + "，" + item.getInputWordNum() + "字");
                }
            });
        }
        if (mReportInfo.remarkList != null && !mReportInfo.remarkList.isEmpty()) {
            mReadInfoList.setAdapter(new CommonAdapter<EssayPaperReport.ReadPaperInfo>(this, mReportInfo.remarkList, android.R.layout.simple_list_item_1) {
                @Override
                public void convert(ViewHolder holder, EssayPaperReport.ReadPaperInfo item, int position) {
                    holder.setText(android.R.id.text1, (position + 1) + "." + item.content);
                }
            });
        } else {
            if (type == 1) {
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

    private void showError(int type) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setErrorImageVisible(true);
        switch (type) {
            case 0:                         // 无网络
                errorView.setErrorImage(R.drawable.no_server_service);
                errorView.setErrorText("网络未连接，连网后点击重试");
                break;
            case 1:                         // 无数据
                errorView.setErrorImage(R.drawable.no_data_bg);
                errorView.setErrorText("获取数据失败，点击重试");
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!ActivityStack.getInstance().hasRootActivity()) {
                MainTabActivity.newIntent(this);
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
