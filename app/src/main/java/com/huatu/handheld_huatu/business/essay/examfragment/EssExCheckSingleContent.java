package com.huatu.handheld_huatu.business.essay.examfragment;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiayun.glide.request.target.ImageViewTarget;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.common.PhotoInfo;
import com.huatu.handheld_huatu.business.common.PictureViewActivity;
import com.huatu.handheld_huatu.business.common.PictureViewDialog;
import com.huatu.handheld_huatu.business.essay.adapter.AwardedMarkAdapter;
import com.huatu.handheld_huatu.business.essay.adapter.CheckPicListAdapter;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.SelectableMultTextHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.SelectableTextHelper;
import com.huatu.handheld_huatu.business.essay.cusview.AnswerExercisePasteTextView;
import com.huatu.handheld_huatu.business.essay.cusview.ExplandArrowLayout;
import com.huatu.handheld_huatu.business.essay.cusview.ScoreLayout;
import com.huatu.handheld_huatu.business.essay.video.CheckBJPlayerV2View;
import com.huatu.handheld_huatu.business.essay.video.CustomAudioPlayerView;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionInfoBean;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.view.custom.EssayExerciseTextView;
import com.huatu.handheld_huatu.view.custom.ExercisePasteTextView;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;
import com.huatu.handheld_huatu.view.photo.PictureData;
import com.huatu.music.utils.LogUtil;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.GallerySnapHelper;
import com.huatu.widget.LinearLayoutListView;
import com.huatu.widget.StickyScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

/**
 * Created by cjx on 2019\6\21 0021.
 * 人工批改或者智能转人工
 */

public class EssExCheckSingleContent extends AbsSettingFragment implements ExercisePasteTextView.OnLongClickListener, ExplandArrowLayout.OnExplandStatusListener {

    @BindView(R.id.scroll_layout)
    StickyScrollView mStickyScrollView;

    @BindView(R.id.ess_ex_materials_ques_title)
    ExerciseTextView mQuesTitle;               // 问题内容

    @BindView(R.id.pic_list)
    RecyclerView mPicListView;

    @BindView(R.id.big_pic_view)
    ImageView mBigPicView;

    @BindView(R.id.standard_answer_txt)
    AnswerExercisePasteTextView mStandardAnswerView;

    @BindView(R.id.answer_analysis_txt)
    EssayExerciseTextView mAnsAnalysisView;

    @BindView(R.id.rl_paper_all_score)
    ExplandArrowLayout mScoreTitleView;//本题得分

    @BindView(R.id.subScore_title)
    FrameLayout mSubScoreTitle;               // 扣分项

    @BindView(R.id.subScore_txt)
    TextView mSubScoreTxt;               // 扣分项

    @BindView(R.id.markpaper_txt)
    TextView mMarkpaperTxt;               // 综合阅卷

    @BindView(R.id.markpaper_title)
    FrameLayout mMarkpaperTitle;

    @BindView(R.id.standanswer_layout)
    ExplandArrowLayout mStandardAnswTitle;

    @BindView(R.id.video_title_layout)
    ExplandArrowLayout mVideoTitlelayout;

    @BindView(R.id.rl_video_content)
    RelativeLayout mVideoParentContent;                            // 视频内容布局

    @BindView(R.id.knowspoint_listView)
    LinearLayoutListView mKnowspointView;

    @BindView(R.id.whole_content)
    LinearLayout mWholecontentView;

    @BindView(R.id.voice_split_line)
    View mVoiceSplitView;

    @BindView(R.id.teacher_voice_container)
    FrameLayout mVoicePlayContainer;

    @BindView(R.id.teacher_voice_title)
    FrameLayout mVoicePlayTitle;

    @BindView(R.id.voice_play_btn)
    ImageView mVoicePlayBtn;

    @BindView(R.id.btn_play_center)
    Button mPlayerBtn;                              // 百家云视频播放器

    @BindView(R.id.video_cover_layout)
    FrameLayout mPlayeCoverLayout;


    @BindView(R.id.tv_paper_score)
    TextView mcurPaperScore;                       // 本题得分


    @BindView(R.id.rl_multpaper_title)
    ScoreLayout mMultpaperTitle;                   // 套题得分



    ExerciseTextView mLastSelectTextView;  //记录最后一次选中的TextView

    String mVideoToken, mVideoId, mAudioId, mAudioToken;
    boolean mHasCheckFinish = true;//是否批改完成

    CheckPicListAdapter mListAdapter;
    private int mIndex;
    private boolean mIsSingle;
    private int mCorrectMode;  // 1智能批改 2人工批改 3智能转人工
    Typeface mTypeface;
    private int[] mViewLocaion = new int[2];

    @Override
    public int getContentView() {
        return R.layout.ess_ex_check_singledetail_layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIndex = getArguments().getInt("title_question");
            mIsSingle = getArguments().getBoolean("isSingle");
            mCorrectMode = getArguments().getInt("correctMode");
        }
        mTypeface = Typeface.DEFAULT;//Typeface.createFromAsset(getContext().getAssets(), "font/Heavy.ttf");
    }

    @Override
    public void onDestroyView() {
        if (null != mVoicePlayContainer) {
            if (mVoicePlayContainer.getChildAt(0) instanceof CustomAudioPlayerView) {
                mVoicePlayContainer.removeViewAt(0);
            }
        }
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
             /*   if (null != mVideoParentContent && (!TextUtils.isEmpty(mVideoToken))) {
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
        if (null != mVoicePlayBtn) {
            mVoicePlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getParentFragment() instanceof EssayExamCheckDetailV2) {
                        ((EssayExamCheckDetailV2) getParentFragment()).stopVideoPlay();
                        CustomAudioPlayerView audioPlayerView = ((EssayExamCheckDetailV2) getParentFragment()).getAudioPlayerView();
                        if (null == audioPlayerView) {
                            audioPlayerView = new CustomAudioPlayerView(getContext());
                            ((EssayExamCheckDetailV2) getParentFragment()).setAudioPlayerView(audioPlayerView);
                            mVoicePlayContainer.addView(audioPlayerView, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                                    , FrameLayout.LayoutParams.WRAP_CONTENT));
                        } else {//viewpager中共用一个Audioplayerview

                            if (audioPlayerView.getParent() != null && (audioPlayerView.getParent() instanceof ViewGroup)) {
                                ((View) audioPlayerView.getParent()).findViewById(R.id.voice_play_btn).setVisibility(View.VISIBLE);
                                ((ViewGroup) audioPlayerView.getParent()).removeView(audioPlayerView);

                            }
                            mVoicePlayContainer.addView(audioPlayerView, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                                    , FrameLayout.LayoutParams.WRAP_CONTENT));
                        }

                        if (null != audioPlayerView) {
                            audioPlayerView.reSetBjAudioData(StringUtils.parseLong(mAudioId), mAudioToken, 0, 240);
                            // audioPlayerView.reSetBjAudioData(23472300,"n3ivNZmUrUQ7H_Jmfnz5AhxFu6YI2olCxYgkDrJy8m522pqNcwVSWjTSEzZrILF4",0,240);
                        }
                        ((View) v).setVisibility(View.GONE);
                    }
                }
            });

        }
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
                    ((EssayExamCheckDetailV2) getParentFragment()).stopAudioPlay();
                    if (null == curPlayerView) {
                        ViewStub tmpViewStub = mVideoParentContent.findViewById(R.id.viewStub_videoView);
                        curPlayerView = (CheckBJPlayerV2View) tmpViewStub.inflate();
                        ((EssayExamCheckDetailV2) getParentFragment()).setPlayView(curPlayerView);
                    } else {//viewpager中共用一个playerview

                        if (curPlayerView.getParent() != null && (curPlayerView.getParent() instanceof ViewGroup)) {
                            ((View) curPlayerView.getParent()).findViewById(R.id.video_cover_layout).setVisibility(View.VISIBLE);
                            ((ViewGroup) curPlayerView.getParent()).removeView(curPlayerView);

                        }
                        mVideoParentContent.addView(curPlayerView, 0, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                                , RelativeLayout.LayoutParams.WRAP_CONTENT));
                    }

                    //mVideoToken="dcQ6trW0MIU7H_Jmfnz5Ai83ENSdXvQnQEg1eRQXACTmR_a5TCfvqTTSEzZrILF4";
                    //"videoId":"19583695","token":"UwERjWDL7r47H_Jmfnz5Ai83ENSdXvQnk5oi4PurLLnQa-L_XIPLPDTSEzZrILF4"
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
                    EssExCheckSingleContent.this.onExplandClick(isExpland, explandView);

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

                boolean hasVoice = !TextUtils.isEmpty(mAudioToken) && (!TextUtils.isEmpty(mAudioId));
                boolean hasSubScore = "1".equals(mSubScoreTxt.getTag(R.id.reuse_tag2));
                if (isExpland) {
                    //3智能转人工
                    if (mCorrectMode == 3) {
                        mAnsAnalysisView.setVisibility(View.VISIBLE);
                    } else if (mCorrectMode == 2) {
                        if (!mHasCheckFinish) {
                            mAnsAnalysisView.setVisibility(View.VISIBLE);
                        } else {
                            mPicListView.setVisibility(View.VISIBLE);
                            mBigPicView.setVisibility(View.VISIBLE);
                        }
                    }

                    mKnowspointView.setVisibility(View.VISIBLE);
                    mMarkpaperTitle.setVisibility(View.VISIBLE);
                    mMarkpaperTxt.setVisibility(View.VISIBLE);
                    if (hasSubScore) {
                        mSubScoreTitle.setVisibility(View.VISIBLE);
                        mSubScoreTxt.setVisibility(View.VISIBLE);
                    }
                    if (hasVoice) {
                        mVoiceSplitView.setVisibility(View.VISIBLE);
                        mVoicePlayTitle.setVisibility(View.VISIBLE);
                        mVoicePlayContainer.setVisibility(View.VISIBLE);
                    }
                    EssExCheckSingleContent.this.onExplandClick(isExpland, explandView);
                } else {

                    if (mCorrectMode == 3) {
                        mAnsAnalysisView.setVisibility(View.GONE);
                    } else if (mCorrectMode == 2) {
                        if (!mHasCheckFinish) {
                            mAnsAnalysisView.setVisibility(View.GONE);
                        } else {
                            mPicListView.setVisibility(View.GONE);
                            mBigPicView.setVisibility(View.GONE);
                        }
                    }
                    mKnowspointView.setVisibility(View.GONE);
                    mMarkpaperTitle.setVisibility(View.GONE);
                    mMarkpaperTxt.setVisibility(View.GONE);
                    if (hasSubScore) {
                        mSubScoreTitle.setVisibility(View.GONE);
                        mSubScoreTxt.setVisibility(View.GONE);
                    }
                    if (hasVoice) {
                        mVoiceSplitView.setVisibility(View.GONE);
                        mVoicePlayTitle.setVisibility(View.GONE);
                        mVoicePlayContainer.setVisibility(View.GONE);
                    }
                }

            }
        });
        mStandardAnswTitle.setCanExplandLayout(mStandardAnswerView).setOnExplandStatusListener(this);
        mStandardAnswerView.setOnLongClickListener(this);
        mKnowspointView.setFooterView(R.layout.space_line);
        mBigPicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"1".equals( v.getTag(R.id.reuse_tag2))){//第一张图加载失败
                    return;
                }
                int selectIndex = 0;
                if (null != mListAdapter) {
                    selectIndex = mListAdapter.getSelectIndex();
                    List<SingleQuestionInfoBean.UserMetaInfo> pictureUrls = mListAdapter.getAllItems();


                     ArrayList<PhotoInfo> piclist=new ArrayList<>();
                    ArrayList<PictureData> list = new ArrayList<>();
                    for (int i = 0; i < pictureUrls.size(); i++) {
                        SingleQuestionInfoBean.UserMetaInfo pictureUrl = pictureUrls.get(i);
                        ImageView view = (ImageView) v;
                        PictureData e = new PictureData();
                        e.location = new int[2];
                        view.getLocationOnScreen(e.location);
                        e.matrixValue = new float[9];
                        view.getImageMatrix().getValues(e.matrixValue);
                        e.size = new int[]{view.getWidth(), view.getHeight()};

                        // String url="http://img.redocn.com/sheji/20141219/zhongguofengdaodeliyizhanbanzhijing_3744115.jpg";
                        e.url = pictureUrl.finalUrl;// pictureUrl.thumbnailUrl;
                        e.originalUrl = pictureUrl.finalUrl;// pictureUrl.picUrl;
                        e.imageSize = new int[]{600, 1024};
                        list.add(e);

                         if(!TextUtils.isEmpty(pictureUrl.finalUrl)){
                            PhotoInfo tmpinfo=new PhotoInfo();
                            tmpinfo.path=pictureUrl.finalUrl;
                            piclist.add(tmpinfo);
                        }
                    }
                    //
                    PictureViewActivity.show(getActivity(), piclist, selectIndex);

                   /* if(null==((ImageView)v).getDrawable()) return;
                    final PictureViewDialog ratefragment = new PictureViewDialog();
                    ratefragment.setPictureData(list,selectIndex,((ImageView)v).getDrawable());
                    ratefragment.show(getActivity().getSupportFragmentManager(), ratefragment.getClass().getSimpleName());*/
                   //  PictureViewActivity.startRemotePic(getActivity(), list, selectIndex);
                }

            }
        });
        //  videoParentView = (RelativeLayout) mPlayerView.getParent();
    }


    private  class CustomViewTarget extends ImageViewTarget<Drawable>  {

        ImageView mImgView;
        public CustomViewTarget(ImageView view) {
            super(view);
            mImgView=view;
        }
        @Override
        protected void setResource(@Nullable Drawable bitmap) {
            getView().setImageDrawable(bitmap);
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            if(null!=resource){

                boolean hasOver=(resource.getIntrinsicHeight()*1f/resource.getIntrinsicWidth())>2.4f;
                { //过高
                    if (mImgView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                        LinearLayout.LayoutParams tmpParm = (LinearLayout.LayoutParams) mImgView.getLayoutParams();
                        tmpParm.width = LinearLayout.LayoutParams.MATCH_PARENT;
                        tmpParm.height =hasOver? DisplayUtil.getScreenHeight():LinearLayout.LayoutParams.WRAP_CONTENT;
                        mImgView.setLayoutParams(tmpParm);
                    }
                }
                mImgView.setAdjustViewBounds(hasOver?false:true);
                LogUtil.e("setResource",resource.getIntrinsicHeight()+","+resource.getIntrinsicWidth()+","+DisplayUtil.getScreenWidth());
            }
            super.onResourceReady(resource,transition);
            if(null!=resource){
                getView().setTag(R.id.reuse_tag2,"1");
            }
        }
    }

    @Override
    public void requestData() {

        if (null != mSubScoreTxt) {
            mSubScoreTxt.setTypeface(mTypeface);
        }
        mListAdapter = new CheckPicListAdapter(getContext());
        mPicListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false));
        mPicListView.setAdapter(mListAdapter);
        mListAdapter.setOnViewItemClickListener(new OnRecItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int type) {

                if (mListAdapter.getSelectIndex() == position) return;
                int oldSelectIndex = mListAdapter.getSelectIndex();
                mListAdapter.setSelectIndex(position);

                mListAdapter.notifyItemChanged(oldSelectIndex, "partRefresh");
                mListAdapter.notifyItemChanged(position, "partRefresh");

                mBigPicView.setTag(R.id.reuse_tag2,"0");
               ImageLoad.displayNormalnoCacheImage(getContext(), mListAdapter.getItem(position).finalUrl, new CustomViewTarget(mBigPicView) ,R.drawable.trans_bg);
            }
        });


        GallerySnapHelper mGallerySnapHelper = new GallerySnapHelper();
        mGallerySnapHelper.attachToRecyclerView(mPicListView);
        if (this.getParentFragment() instanceof EssayExamCheckDetailV2) {
            CheckDetailBean info = ((EssayExamCheckDetailV2) this.getParentFragment()).getCurrentInfo(mIndex);
            if (null != info) {
                // 显示问题内容
                // mQuesTitle.setHtmlSource(EssayHelper.getFilterTxt(info.answerRequire));
                refreshSingleView(info);
            }
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

               // mUserTimetxt.setText(ScoreLayout.getUseTimeSpan(singleBean.spendTime,singleBean.inputWordNum));
                // 套题要显示总得分，总时长
                if (!mIsSingle) {
                    mMultpaperTitle.setVisibility(View.VISIBLE);
                    mMultpaperTitle.setScoreTxt(singleBean, ((EssayExamCheckDetailV2) getParentFragment()).getAllpagerScore());
                }
            } else {
                // 没有批改完
                mHasCheckFinish = false;
                mAnsAnalysisView.setVisibility(View.VISIBLE);
                mPicListView.setVisibility(View.GONE);
                mBigPicView.setVisibility(View.GONE);

                mAnsAnalysisView.setText("正在批改中，请稍后查看");
            }

            if (null != mAnsAnalysisView) {
                if (mCorrectMode == 3) {//智能转人工
                    mPicListView.setVisibility(View.GONE);
                    mBigPicView.setVisibility(View.GONE);
                    mAnsAnalysisView.setVisibility(View.VISIBLE);
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
                    // 智能转人工，小题和应用文都是新的
                    mAnsAnalysisView.initContent(tmpInfo.correctedContent);
                }
            }

            mListAdapter.refresh(tmpInfo.userMeta);
            if (!ArrayUtils.isEmpty(tmpInfo.userMeta))
                ImageLoad.displayNormalnoCacheImage(getContext(), mListAdapter.getItem(0).finalUrl, new CustomViewTarget(mBigPicView), R.drawable.trans_bg);

            //加分点
            if (null != mKnowspointView) {
                mKnowspointView.setAdapter(new AwardedMarkAdapter(getContext(), EssayExerciseTextView.getAddScoreList(tmpInfo.correctedContent), mTypeface));
            }

            //扣分项
            if (null != mSubScoreTxt) {
                if (!ArrayUtils.isEmpty(tmpInfo.deRemarkList)) {
                    StringBuilder sb = new StringBuilder(200);//1.语言不够得体，-2分
                    for (int i = 0; i < tmpInfo.deRemarkList.size(); i++) {
                        sb.append((i + 1) + "." + tmpInfo.deRemarkList.get(i).content + "，" +  CommonUtils.formatScore(tmpInfo.deRemarkList.get(i).score) + "分\n");
                    }
                    mSubScoreTxt.setText(sb.toString());
                    mSubScoreTxt.setTag(R.id.reuse_tag2, "1");
                } else {
                    mSubScoreTxt.setVisibility(View.GONE);
                    mSubScoreTitle.setVisibility(View.GONE);
                }
            }

            //综合阅卷
            if (null != mMarkpaperTxt) {
                StringBuilder sb = new StringBuilder(200);//1.语言不够得体，-2分
                if (!ArrayUtils.isEmpty(tmpInfo.remarkList)) {
                    for (int i = 0; i < tmpInfo.remarkList.size(); i++) {
                        sb.append((i + 1) + "." + tmpInfo.remarkList.get(i).content + "\n");
                    }
                }
                mMarkpaperTxt.setText(sb.toString());
            }

            //名师之声
            if ((singleBean.audioId != 0 && !StringUtils.isEmpty(singleBean.audioToken))) {
                mAudioId = String.valueOf(singleBean.audioId);
                mAudioToken = singleBean.audioToken;
            } else {
                mVoicePlayTitle.setVisibility(View.GONE);
                mVoicePlayTitle.setTag("");//sticky
                mVoicePlayContainer.setVisibility(View.GONE);
                mVoiceSplitView.setVisibility(View.GONE);

            }

            // 显示问题内容
            mQuesTitle.setHtmlSource(EssayHelper.getFilterTxt(tmpInfo.answerRequire));
            mStandardAnswerView.refreshStandAnswerView(tmpInfo.answerList, mStandardAnswTitle, mWholecontentView, this, this);
            if (ArrayUtils.size(tmpInfo.answerList) > 1) {
                if (null != mStickyScrollView) mStickyScrollView.notifyStickyAttributeChanged();
            }

            if (singleBean.videoAnalyzeFlag && (singleBean.videoId != 0 && !StringUtils.isEmpty(singleBean.token))) {
                mVideoTitlelayout.setVisibility(View.VISIBLE);
               // mVideoParentContent.setVisibility(View.VISIBLE);
                mVideoId = String.valueOf(singleBean.videoId);
                mVideoToken = singleBean.token;
                // mPlayerView.setVideoId(singleBean.videoId, singleBean.token);
            } else {
                mVideoTitlelayout.setVisibility(View.GONE);
                mVideoTitlelayout.setTag("");//sticky
                mVideoParentContent.setVisibility(View.GONE);
            }
        }
    }

}
