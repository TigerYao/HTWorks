package com.huatu.handheld_huatu.business.essay.examfragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.essay.adapter.AwardedMarkAdapter;
import com.huatu.handheld_huatu.business.essay.adapter.StyleScoreListAdapter;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.SelectableMultTextHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.SelectableTextHelper;
import com.huatu.handheld_huatu.business.essay.cusview.AnswerExercisePasteTextView;
import com.huatu.handheld_huatu.business.essay.cusview.ExplandArrowLayout;
import com.huatu.handheld_huatu.business.essay.cusview.ScoreLayout;
import com.huatu.handheld_huatu.business.essay.video.CheckBJPlayerV2View;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckDetailBean;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.view.custom.EssayExerciseTextView;
import com.huatu.handheld_huatu.view.custom.ExercisePasteTextView;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.AutoTextSizeView;
import com.huatu.widget.LinearLayoutListView;
import com.huatu.widget.StickyScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

/**
 * Created by cjx on 2019\6\21 0021.
 * 智能批改
 */

public class EssExCheckRobotContent extends AbsSettingFragment implements ExercisePasteTextView.OnLongClickListener, ExplandArrowLayout.OnExplandStatusListener {

    @BindView(R.id.scroll_layout)
    StickyScrollView mStickyScrollView;

    @BindView(R.id.ess_ex_materials_ques_title)
    ExerciseTextView mQuesTitle;               // 问题内容

    @BindView(R.id.standard_answer_txt)
    AnswerExercisePasteTextView mStandardAnswerView;

    @BindView(R.id.answer_analysis_txt)
    EssayExerciseTextView mAnsAnalysisView;

    @BindView(R.id.rl_paper_all_score)
    ExplandArrowLayout mScoreTitleView;//本题得分

/*    @BindView(R.id.subScore_txt)
    TextView mSubScoreTxt;               // 扣分项*/

    @BindView(R.id.standanswer_layout)
    ExplandArrowLayout mStandardAnswTitle;

    @BindView(R.id.video_title_layout)
    ExplandArrowLayout mVideoTitlelayout;

    @BindView(R.id.rl_video_content)
    RelativeLayout mVideoParentContent;                            // 视频内容布局


    @BindView(R.id.whole_content)
    LinearLayout mWholecontentView;


    @BindView(R.id.btn_play_center)
    Button mPlayerBtn;                              // 百家云视频播放器

    @BindView(R.id.video_cover_layout)
    FrameLayout mPlayeCoverLayout;


    @BindView(R.id.analyzequestion_title)
    ExplandArrowLayout mAnalyzeQuesTitle;               // 试题分析

    @BindView(R.id.analyzequestion_txt)
    ExercisePasteTextView mAnalyzeQuesContent;               // 试题分析

    @BindView(R.id.authorityeviews_title)
    ExplandArrowLayout mAuthorityeViewTitle;                   // 经验小结

    @BindView(R.id.authorityeviews_txt)
    ExercisePasteTextView mAuthorityeViewContent;            // 经验小结

  /*  @BindView(R.id.myanswer_title)
    FrameLayout mMyAnswerTitle;                      // 我的做答

    @BindView(R.id.myanswer_txt)
    TextView mMyAnswerContent;                      // 我的做答*/

    @BindView(R.id.tv_paper_score)
    TextView mcurPaperScore;                       // 本题得分

    @BindView(R.id.rl_multpaper_title)
    ScoreLayout mMultpaperTitle;                      // 套题得分

    @BindView(R.id.tv_content_score)
    TextView mContentScoreTxt;                            // 内容得分

    @BindView(R.id.tv_style_score)
    TextView mStyleScoreTxt;                             // 形式的分数

    @BindView(R.id.tv_use_time)
    AutoTextSizeView mUserTimetxt;                              // 用时


    @BindView(R.id.rl_style_score)
    LinearLayoutListView mStyleScorelistView;


    @BindView(R.id.markpaper_txt)
    TextView mMarkpaperTxt;               // 综合阅卷

    @BindView(R.id.markpaper_title)
    FrameLayout mMarkpaperTitle;


    @BindView(R.id.knowspoint_listView)
    LinearLayoutListView mKnowspointView;


    ExerciseTextView mLastSelectTextView;  //记录最后一次选中的TextView

    String mVideoToken, mVideoId;

    private int mIndex;
    private boolean mIsSingle;
    Typeface mTypeface;
    private int[] mViewLocaion = new int[2];

    @Override
    public int getContentView() {
        return R.layout.ess_ex_check_robotdetail_layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIndex = getArguments().getInt("title_question");
            mIsSingle = getArguments().getBoolean("isSingle");
        }
        mTypeface =Typeface.DEFAULT;// Typeface.createFromAsset(getContext().getAssets(), "font/Heavy.ttf");
    }

    @Override
    public void onDestroyView() {
        if (null != mVideoParentContent) {//视图移除时，移除播放器
            if (mVideoParentContent.getChildAt(0) instanceof CheckBJPlayerV2View) {
                mVideoParentContent.removeViewAt(0);
            }
        }
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            if (!isVisibleToUser) {
                if (null != mLastSelectTextView) {
                    mLastSelectTextView.clearView();
                    mLastSelectTextView = null;
                }
                if (null != mSelectableMultTextHelper) {
                    mSelectableMultTextHelper.clearView();
                }
              /*  if (null != mVideoParentContent && (!TextUtils.isEmpty(mVideoToken))) {
                    mVideoParentContent.setVisibility(View.VISIBLE);
                }*/
              /*  if(null!=mVoicePlayView){
                    mVoicePlayView.pausePlay();
                }*/
            }
        }
    }


    SelectableMultTextHelper mSelectableMultTextHelper;

    @Override
    public void onLongClick(ExercisePasteTextView v, int x, int y) {

        if (null != mLastSelectTextView) {//答题分析暂时没有共用
            mLastSelectTextView.clearView();
        }
        if (null == mSelectableMultTextHelper) {
            mSelectableMultTextHelper = new SelectableMultTextHelper.Builder(v)
                    .setSelectedColor(ContextCompat.getColor(getContext(), R.color.essay_sel_color))
                    .setCursorHandleSizeInDp(10).setShowType(SelectableTextHelper.SHOW_TYPE_ONLY_COPY)
                    .setCursorHandleColor(ContextCompat.getColor(getContext(), R.color.main_color))
                    .buildV2();
        }
        mSelectableMultTextHelper.setCurrentTextView(v);
        mSelectableMultTextHelper.showSelectView(x, y);

    }

    @Override
    public void onExplandClick(boolean isExpland, View explandLayout) {
        if (isExpland) {
            explandLayout.getLocationOnScreen(mViewLocaion);
            int yof;
            if (mIsSingle) {
                yof = 180;// 180;
            } else {
                yof = 220;// 220;
            }
            final int dp = mViewLocaion[1] - DisplayUtil.dp2px(yof);
            if (mStickyScrollView != null && dp != 0) {
                mStickyScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mStickyScrollView.smoothScrollBy(0, dp);
                    }
                }, 100);
            }
        }
    }

    @Override
    protected void setListener() {
        if (null != mVideoParentContent) {
            ViewGroup.LayoutParams tmpPara = mVideoParentContent.getLayoutParams();
            int screeenWitdh = DensityUtils.getScreenWidth(getContext());
            final int distanceHeight = (int) ((screeenWitdh - DensityUtils.dp2px(getContext(), 40)) * 0.56);//0.563

            LogUtils.e("mVideoParentContent", distanceHeight + "");
            tmpPara.height = distanceHeight;
            mVideoParentContent.setLayoutParams(tmpPara);

        }
        mPlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getParentFragment() instanceof EssayExamCheckDetailV2) {
                    CheckBJPlayerV2View curPlayerView = ((EssayExamCheckDetailV2) getParentFragment()).getPlayView();
                    if (null == curPlayerView) {
                        ViewStub tmpViewStub = mVideoParentContent.findViewById(R.id.viewStub_videoView);
                        curPlayerView = (CheckBJPlayerV2View) tmpViewStub.inflate();

                        curPlayerView.disableAutoScrollBottom();
                        ((EssayExamCheckDetailV2) getParentFragment()).setPlayView(curPlayerView);
                    } else {//viewpager中共用一个playerview

                        if (curPlayerView.getParent() != null && (curPlayerView.getParent() instanceof ViewGroup)) {
                            ((View) curPlayerView.getParent()).findViewById(R.id.video_cover_layout).setVisibility(View.VISIBLE);
                            ((ViewGroup) curPlayerView.getParent()).removeView(curPlayerView);

                        }
                        mVideoParentContent.addView(curPlayerView, 0, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                                , RelativeLayout.LayoutParams.WRAP_CONTENT));
                    }
                    // mVideoId="19583695";
                    // mVideoToken="Cwj3bH1kvsI7H_Jmfnz5Ai83ENSdXvQnIROJY47N6zOF0QnBP6-tNjTSEzZrILF4";
                    curPlayerView.setVideoId(StringUtils.parseLong(mVideoId), mVideoToken);
                    curPlayerView.rePlayVideo();
                    ((View) v.getParent()).setVisibility(View.GONE);
                }
            }
        });
        mVideoTitlelayout.setOnExplandStatusListener(new ExplandArrowLayout.OnExplandStatusListener() {
            @Override
            public void onExplandClick(boolean isExpland, View explandView) {
                if (null == mVideoParentContent) return;
                if (isExpland) {
                    mVideoParentContent.setVisibility(View.VISIBLE);
                    EssExCheckRobotContent.this.onExplandClick(true, explandView);
                } else {
                    View firstView = mVideoParentContent.getChildAt(0);
                    if (firstView instanceof CheckBJPlayerV2View) {
                        boolean isPlaying = ((CheckBJPlayerV2View) firstView).isPlaying();
                        if (isPlaying) {
                            ((CheckBJPlayerV2View) firstView).pauseVideo();
                        }
                    }
                    mVideoParentContent.setVisibility(View.GONE);
                }
            }
        });
        mScoreTitleView.setOnExplandStatusListener(new ExplandArrowLayout.OnExplandStatusListener() {
            @Override
            public void onExplandClick(boolean isExpland, View explandView) {
                boolean hasValue = "1".equals(mStyleScoreTxt.getTag(R.id.reuse_tag2));
                boolean hasRemark="1".equals(mMarkpaperTxt.getTag(R.id.reuse_tag2));
                boolean hasAddScore="1".equals(mKnowspointView.getTag(R.id.reuse_tag2));
                if (isExpland) {
                    mAnsAnalysisView.setVisibility(View.VISIBLE);
                    if (hasValue) {
                        ((View) mStyleScoreTxt.getParent()).setVisibility(View.VISIBLE);
                        ((View) mContentScoreTxt.getParent()).setVisibility(View.VISIBLE);
                        mStyleScorelistView.setVisibility(View.VISIBLE);
                    }
                    if(hasRemark){
                        mMarkpaperTxt.setVisibility(View.VISIBLE);
                        mMarkpaperTitle.setVisibility(View.VISIBLE);
                    }
                    if(hasAddScore){
                        mKnowspointView.setVisibility(View.VISIBLE);
                    }
                    EssExCheckRobotContent.this.onExplandClick(true, explandView);
                } else {
                    mAnsAnalysisView.setVisibility(View.GONE);
                    if (hasValue) {
                        ((View) mStyleScoreTxt.getParent()).setVisibility(View.GONE);
                        ((View) mContentScoreTxt.getParent()).setVisibility(View.GONE);
                        mStyleScorelistView.setVisibility(View.GONE);
                    }
                    if(hasRemark){
                        mMarkpaperTxt.setVisibility(View.GONE);
                        mMarkpaperTitle.setVisibility(View.GONE);
                    }
                    if(hasAddScore){
                        mKnowspointView.setVisibility(View.GONE);
                    }
                }
            }
        });
        mStandardAnswTitle.setCanExplandLayout(mStandardAnswerView)
                .setOnExplandStatusListener(this);
        mStandardAnswerView.setOnLongClickListener(this);
    }


    @Override
    public void requestData() {
/*
        if(null!=mSubScoreTxt){
            mSubScoreTxt.setTypeface(mTypeface);
        }*/
        if (this.getParentFragment() instanceof EssayExamCheckDetailV2) {
            CheckDetailBean info = ((EssayExamCheckDetailV2) this.getParentFragment()).getCurrentInfo(mIndex);
            if (null != info) {
                // 显示问题内容
                // mQuesTitle.setHtmlSource(EssayHelper.getFilterTxt(info.answerRequire));
                refreshSingleView(info);
            }
        }
    }


    // 过滤形式得分和内容得分
    private void getContentStyleScore(CheckDetailBean singleBean, List<CheckDetailBean.ScoreListEntity> contentScore
            , List<CheckDetailBean.ScoreListEntity> styleScore) {
        contentScore.clear();
        styleScore.clear();
        if (singleBean == null) {
            return;
        }
        if (singleBean.addScoreList != null && singleBean.addScoreList.size() > 0) {
            // ScoreListEntity.type   1、内容得分 2、格式得分 3、减分 4、其他得分
            for (CheckDetailBean.ScoreListEntity scoreListEntity : singleBean.addScoreList) {
                if (scoreListEntity.type == 1) {
                    contentScore.add(scoreListEntity);
                } else {
                    styleScore.add(scoreListEntity);
                }
            }
        }
        if (singleBean.subScoreList != null && singleBean.subScoreList.size() > 0) {
            styleScore.addAll(singleBean.subScoreList);
        }

    }

    // 显示形式得分
    private void showStyleScoreList(ArrayList<CheckDetailBean.ScoreListEntity> styleScore) {
        double score = 0;
        for (CheckDetailBean.ScoreListEntity scoreListEntity : styleScore) {
            if (scoreListEntity.type == 3) {
                score -= scoreListEntity.score;
            } else {
                score += scoreListEntity.score;
            }
        }
        if (score > 0) {
            mStyleScoreTxt.setText(("+" + score + "分").replace(".0", ""));
            mStyleScoreTxt.setBackgroundResource(R.drawable.ess_check_score_content_bg);
        } else {
            mStyleScoreTxt.setText((score + "分").replace(".0", ""));
            mStyleScoreTxt.setBackgroundResource(R.drawable.ess_check_score_style_bg);
        }
        mStyleScorelistView.setAdapter(new StyleScoreListAdapter(getContext(), styleScore));

    }

    // 显示内容得分
    private void showContentScore(ArrayList<CheckDetailBean.ScoreListEntity> contentScore) {
        double score = 0;
        for (CheckDetailBean.ScoreListEntity scoreListEntity : contentScore) {
            score += scoreListEntity.score;
        }
        if (score > 0) {
            mContentScoreTxt.setText(("+" + score + "分").replace(".0", ""));
            mContentScoreTxt.setBackgroundResource(R.drawable.ess_check_score_content_bg);
        } else {
            mContentScoreTxt.setText((score + "分").replace(".0", ""));
            mContentScoreTxt.setBackgroundResource(R.drawable.ess_check_score_style_bg);
        }
    }



    // 单批改材料展示
    // singleBean.type --> 1：概括归纳 2：综合分析 3：解决问题 4：应用文 5：文章写作
    public void refreshSingleView(CheckDetailBean singleBean) {
        if (singleBean != null) {
            //  SingleQuestionInfoBean tmpInfo= refresh();
            CheckDetailBean tmpInfo = singleBean;

            // 批改完成显示 本题得分，套题得分
            if (singleBean.bizStatus == 3) {
                // 设置本题得分
                mcurPaperScore.setText(ScoreLayout.getScoreSpan(singleBean.examScore, singleBean.score));
                mUserTimetxt.setGravity(Gravity.RIGHT);
                mUserTimetxt.setAutoSizeImmediatelyText(ScoreLayout.getUseTimeSpan(singleBean.spendTime,singleBean.inputWordNum));

               // mUserTimetxt.setAutoSizeImmediatelyText(ScoreLayout.getUseTimeSpan(7240,22222));
                // 套题要显示总得分，总时长
                if (!mIsSingle) {
                    mMultpaperTitle.setVisibility(View.VISIBLE);
                    mMultpaperTitle.setScoreTxt(singleBean, ((EssayExamCheckDetailV2) getParentFragment()).getAllpagerScore());
                }

                if (null != mAnsAnalysisView)
                    //5：文章写作  新标签解析
                    if (singleBean.type == 5) {
                        mAnsAnalysisView.initContent(singleBean.correctedContent);
                        //加分点
                        if (null != mKnowspointView) {
                            mKnowspointView.setAdapter(new AwardedMarkAdapter(getContext(), EssayExerciseTextView.getAddScoreList(tmpInfo.correctedContent), mTypeface));
                            mKnowspointView.setTag(R.id.reuse_tag2,"1");
                        }
                        //综合阅卷
                        if (null != mMarkpaperTxt) {
                            if (!ArrayUtils.isEmpty(tmpInfo.remarkList)) {
                                StringBuilder sb = new StringBuilder(200);//1.语言不够得体，-2分
                                for (int i = 0; i < tmpInfo.remarkList.size(); i++) {
                                    sb.append((i + 1) + "." + tmpInfo.remarkList.get(i).content + "\n");
                                }
                                mMarkpaperTxt.setText(sb.toString());
                                mMarkpaperTitle.setVisibility(View.VISIBLE);
                                mMarkpaperTxt.setVisibility(View.VISIBLE);
                                mMarkpaperTxt.setTag(R.id.reuse_tag2,"1");
                            }
                        }
                    } else {//老标签解析
                        mAnsAnalysisView.initContent(singleBean);
                    }
                mAnsAnalysisView.setOnTextSelectListener(new EssayExerciseTextView.OnTextSelectListener() {
                    @Override
                    public void clearView() {
                        if (null != mSelectableMultTextHelper) {
                            mSelectableMultTextHelper.clearView();
                        } else {
                            mLastSelectTextView = mAnsAnalysisView;
                        }
                    }
                });

                // 计算并显示  内容分、形式分
                ArrayList<CheckDetailBean.ScoreListEntity> contentScore = new ArrayList<>();     // 内容得分
                ArrayList<CheckDetailBean.ScoreListEntity> styleScore = new ArrayList<>();       // 形式得分

                // 过滤 内容得分 和 形式得分
                getContentStyleScore(singleBean, contentScore, styleScore);
                if (styleScore.size() > 0) {
                    mContentScoreTxt.setTag(R.id.reuse_tag2, "1");
                    mStyleScoreTxt.setTag(R.id.reuse_tag2, "1");
                    ((View) mContentScoreTxt.getParent()).setVisibility(View.VISIBLE);
                    ((View) mStyleScoreTxt.getParent()).setVisibility(View.VISIBLE);
                    mStyleScorelistView.setVisibility(View.VISIBLE);
                    showStyleScoreList(styleScore);
                    showContentScore(contentScore);
                }

            } else {
                // 没有批改完
                mAnsAnalysisView.setText("正在批改中，请稍后查看");
            }
            // 显示问题内容
            mQuesTitle.setHtmlSource(EssayHelper.getFilterTxt(tmpInfo.answerRequire));
            mStandardAnswerView.refreshStandAnswerView(tmpInfo.answerList, mStandardAnswTitle, mWholecontentView, this, this);
            if (ArrayUtils.size(tmpInfo.answerList) > 1) {
                if (null != mStickyScrollView) mStickyScrollView.notifyStickyAttributeChanged();
            }

            if (singleBean.videoAnalyzeFlag && (singleBean.videoId != 0 && !StringUtils.isEmpty(singleBean.token))) {
                mVideoTitlelayout.setVisibility(View.VISIBLE);
                mVideoParentContent.setVisibility(View.VISIBLE);
                mVideoId = String.valueOf(singleBean.videoId);
                mVideoToken = singleBean.token;
                // mPlayerView.setVideoId(singleBean.videoId, singleBean.token);
            } else {
                mVideoTitlelayout.setVisibility(View.GONE);
                mVideoTitlelayout.setTag("");//sticky
                mVideoParentContent.setVisibility(View.GONE);
            }

            //singleBean.correctedContent=errorMsg;
            // 试题分析
            if (StringUtils.isEmptyOrNull(singleBean.difficultGrade)
                    || TextUtils.isEmpty(singleBean.analyzeQuestion) || TextUtils.isEmpty(EssayHelper.getFilterTxt(singleBean.analyzeQuestion)) || singleBean.analyzeQuestion.equals("null")) {
                mAnalyzeQuesTitle.setVisibility(View.GONE);
                mAnalyzeQuesContent.setVisibility(View.GONE);
                mAnalyzeQuesTitle.setTag("");
            } else {

                mAnalyzeQuesContent.setHtmlSource("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + singleBean.difficultGrade + "<br/>" + EssayHelper.getFilterTxt(singleBean.analyzeQuestion));
                mAnalyzeQuesContent.setOnLongClickListener(this);
                mAnalyzeQuesTitle.setCanExplandLayout(mAnalyzeQuesContent)
                        .setOnExplandStatusListener(this);
            }

            // 经验小结
            if (!TextUtils.isEmpty(singleBean.authorityReviews) && !singleBean.authorityReviews.equals("null")) {

                mAuthorityeViewContent.setHtmlSource(EssayHelper.getFilterTxt(singleBean.authorityReviews));
                mAuthorityeViewContent.setOnLongClickListener(this);
                mAuthorityeViewTitle.setCanExplandLayout(mAuthorityeViewContent)
                        .setOnExplandStatusListener(this);
            } else {
                mAuthorityeViewContent.setVisibility(View.GONE);
                mAuthorityeViewTitle.setVisibility(View.GONE);
                mAuthorityeViewTitle.setTag("");
            }
        }
    }
}
