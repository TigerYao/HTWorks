package com.huatu.handheld_huatu.business.essay.examfragment;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.MySupportFragment;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.essay.bhelper.DlEssayDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.DownLoadEssayHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.cusview.CorrectDialog;
import com.huatu.handheld_huatu.business.essay.cusview.RightOperatorTextView;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.essay.video.CheckBJPlayerV2View;
import com.huatu.handheld_huatu.business.essay.video.CustomAudioPlayerView;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.DownloadEssayBean;
import com.huatu.handheld_huatu.mvpmodel.essay.ExamMaterialListBean;
import com.huatu.handheld_huatu.mvpmodel.essay.MaterialsFileUrlBean;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.CovertManulView;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomLoadingDialog;
import com.huatu.utils.ArrayUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItems;
import com.ogaclejapan.v4.FragmentStatePagerItemAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 批改详情页
 */
public class EssayExamCheckDetailV2 extends MySupportFragment {

    private static final String TAG = "EssayExamCheckDetail";

    public int requestType;

    @BindView(R.id.iv_download)
    ImageView ivDownload;                                                                   // 下载键

    @BindView(R.id.ex_check_detail_viewpager_tab)
    SmartTabLayout viewPagerTab;

    @BindView(R.id.ex_check_detail_viewpager)
    ViewPager mViewPager;

    @BindView(R.id.back_exam_materials)
    RightOperatorTextView backExamMaterials;


    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    @BindView(R.id.covert_mauel_view)
    CovertManulView mCoverManulView;//转人工

    CheckBJPlayerV2View mBJPlayView;         //共用一个播放器，存在父fragment中
    CustomAudioPlayerView mBjAudioPlayerView;//共用一个

    private String titleView;
    private boolean isSingle;
    private Bundle extraArgs;
    //private EssExCheckContentV2 mEssExCheckContent;
    private FragmentStatePagerItemAdapter adapter;


    private int mCorrectMode = 0;// //批改模式 1 智能批改，2人工批改,3 智能->人工

    private int areaId;
    public long questionBaseId;
    //public long questionDetailId;
    private long answerCardId;                                                              // 答题卡id
    private long paperId;
    private int questionType;

    private long essayAnswerId;                                                             // 答题卡id（前一个页面传过来的）
    private String areaName;

    private SingleQuestionDetailBean mSingleQuestionDetailBean;                             // 单题详情
    private PaperQuestionDetailBean mPaperQuestionDetailBean;                               // 套题详情

    private List<ExamMaterialListBean> mCachePaperMaterialListBeans;                        // 套题材料

    ArrayList<CheckDetailBean> mPaperAnswerCheck = new ArrayList<>();


    private boolean hasPdfFile = false;                                                     // 是否已经下载试题
    private boolean isFromArgue = false;
    private boolean paperReportFlag;                                                        // 是否有报告
    private boolean mPlayingPause = false;
    private boolean mPlayingAudioPause = false;      //音频的暂停

    public boolean mIsFromReport = false;


    public static void lanuch(Context context, Bundle extraArgs) {
        FragmentParameter tmpPar = new FragmentParameter(EssayExamCheckDetailV2.class, extraArgs);
        UIJumpHelper.jumpSupportFragment(context, tmpPar, 1);
    }


    private double mAllPapeScore = 0;

    public double getAllpagerScore() {              //套题总分
        return mAllPapeScore;
    }

    public CheckDetailBean getCurrentInfo(int position) {
        if (position >= 0 && (position < ArrayUtils.size(mPaperAnswerCheck)))
            return mPaperAnswerCheck.get(position);
        return null;
    }

    @Override
    public int getContentView() {
        return R.layout.essay_exam_check_detail2_layout;
    }

    public CheckBJPlayerV2View getPlayView() {
        return mBJPlayView;
    }

    //两种播放器的互斥
    public void setPlayView(CheckBJPlayerV2View currentPlayview) {
        mBJPlayView = currentPlayview;
        mBJPlayView.setOnStartPlayListener(new CheckBJPlayerV2View.onStartPlayListener() {
            @Override
            public void onVideoPlayClick() {
                if (null != mBjAudioPlayerView && (mBjAudioPlayerView.isPlaying())) {
                    mBjAudioPlayerView.pausePlay();
                }
            }
        });
    }

    public CustomAudioPlayerView getAudioPlayerView() {
        return mBjAudioPlayerView;
    }

    public void setAudioPlayerView(CustomAudioPlayerView audioPlayview) {
        mBjAudioPlayerView = audioPlayview;
        //内部按钮点击
        mBjAudioPlayerView.setOnStartPlayListener(new CustomAudioPlayerView.onStartPlayListener() {
            @Override
            public void onAudioPlayClick() {
                if (null != mBJPlayView && (mBJPlayView.isPlaying())) {
                    mBJPlayView.pauseVideo();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBJPlayView != null) {
            mBJPlayView.onDestroy();
        }
        if (null != mBjAudioPlayerView) {
            mBjAudioPlayerView.releasePlayer();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (backExamMaterials != null) {
            backExamMaterials.resetPosition();
        }
        // 各处不判断横竖屏，以是否全屏为指示
        if (null != mBJPlayView)
            mBJPlayView.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBJPlayView != null) {
            if (mPlayingPause) {
                mBJPlayView.onResume();
                mPlayingPause = false;
            }
        }
        if (null != mBjAudioPlayerView) {
            if (mPlayingAudioPause) {
                mBjAudioPlayerView.resumePlay();
                mPlayingAudioPause = false;
            }
        }
    }

    public void stopVideoPlay() {
        if (mBJPlayView != null) {
            if (mBJPlayView.isPlaying()) {
                mBJPlayView.pauseVideo();
            }
        }
    }

    public void stopAudioPlay() {
        if (mBjAudioPlayerView != null) {
            if (mBjAudioPlayerView.isPlaying()) {
                mBjAudioPlayerView.pausePlay();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBJPlayView != null) {
            if (mBJPlayView.isPlaying()) {
                mPlayingPause = true;
                mBJPlayView.onPause();
            }
        }
        if (null != mBjAudioPlayerView) {
            if (mBjAudioPlayerView.isPlaying()) {
                mPlayingAudioPause = true;
                mBjAudioPlayerView.pausePlay();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event.type == EssayExamMessageEvent.EssayExam_net_download_essay_success) {                   // 下载完成
            ToastUtils.showEssayToast("下载成功");
            refreshTitleRight();
        }
        return true;
    }


    public static void lanuchForMsg(Context context, int topiceType, long answerId, long questionBaseId, long paperId, String areaName, String paperName) {

        Bundle tmpArg = new Bundle();
        if (topiceType == 0 || topiceType == 2) {
            tmpArg.putBoolean("isSingle", true);
        }
        if (topiceType == 2) {
            tmpArg.putBoolean("isFromArgue", true);
        }
        tmpArg.putLong("answerId", answerId);
        tmpArg.putLong("questionBaseId", questionBaseId);
        tmpArg.putLong("paperId", paperId);
        tmpArg.putString("areaName", areaName);
        tmpArg.putString("titleView", paperName);
        tmpArg.putInt("staticCorrectMode", 1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (getArguments() != null) {
            requestType = getArguments().getInt("request_type", 1);             // 显示类型
            titleView = getArguments().getString("titleView");
            isSingle = getArguments().getBoolean("isSingle", false);
            isFromArgue = getArguments().getBoolean("isFromArgue", false);
            essayAnswerId = getArguments().getLong("answerId");
            questionBaseId = getArguments().getLong("questionBaseId");
            // questionDetailId = getArguments().getLong("questionDetailId");
            paperId = getArguments().getLong("paperId", -1);
            areaName = getArguments().getString("areaName", "");
            paperReportFlag = getArguments().getBoolean("paperReportFlag", false);
            mCorrectMode = getArguments().getInt("correctMode", 0);
            questionType = getArguments().getInt("questionType", 0);
        }
    }


    @Override
    protected void setListener() {
        mCommloadingView.setOnRtyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        // materialsCardView.setChildFragmentManager(getChildFragmentManager());
        if (backExamMaterials != null) {
            backExamMaterials.resetPosition();
            if (backExamMaterials != null) {
                backExamMaterials.setOnCusViewCallBack(new RightOperatorTextView.OnCusViewCallBack() {
                    @Override
                    public boolean isActionUp(boolean isOpen) {
                        showBottomMaterial();
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
        ;
    }


    private void sortQuesBean(PaperQuestionDetailBean data) {
        if (data != null) {
            if (data.essayQuestions != null) {
                Collections.sort(data.essayQuestions, new Comparator<SingleQuestionDetailBean>() {
                    @Override
                    public int compare(SingleQuestionDetailBean o1, SingleQuestionDetailBean o2) {
                        if (o1 != null && o2 != null) {
                            return o1.sort - o2.sort;
                        }
                        return 0;
                    }
                });
            }
        }
    }

    private void loadData() {
        mCommloadingView.showLoadingStatus();
        if (isSingle) {
            if (questionBaseId > 0) {
                // 增加请求字段 mCorrectMode：1、智能批改 2、人工批改    modeType 需要知道是否是课后作业，默认传 1、表示非课后作业 2、表示课后作业
                ServiceExProvider.visit(getSubscription(), RetrofitManager.getInstance().getService().getSingleQuestionDetail(questionBaseId, mCorrectMode, 1, 0, 0),
                        new NetObjResponse<SingleQuestionDetailBean>() {
                            @Override
                            public void onSuccess(BaseResponseModel<SingleQuestionDetailBean> model) {
                                if(null!=mCommloadingView){
                                    mCommloadingView.hide();
                                }
                                if (model != null && model.data != null) {
                                    mSingleQuestionDetailBean = model.data;
                                    if (mSingleQuestionDetailBean.answerCardId > 0) {
                                        answerCardId = mSingleQuestionDetailBean.answerCardId;
                                        if (essayAnswerId <= 0) {
                                            essayAnswerId = answerCardId;
                                        }
                                        if (mCorrectMode <= 0) {//从消息列表点击过来
                                            mCorrectMode = mSingleQuestionDetailBean.correctMode;//
                                        }
                                    }
                                    questionBaseId = mSingleQuestionDetailBean.questionBaseId;
                                    // questionDetailId = mSingleQuestionDetailBean.questionDetailId;
                                    bindViewPager();
                                    refreshDetail(0);
                                }
                            }

                            @Override
                            public void onError(String message, int type) {
                                if(null!=mCommloadingView){
                                    if (type == 3)
                                        mCommloadingView.showNetworkTip();
                                    else
                                        mCommloadingView.showServerError();
                                }
                             }
                        });
            }

        } else if (!isSingle) {
            if (paperId > 0) {
                // 增加请求字段 mCorrectMode：1、智能批改 2、人工批改    modeType 需要知道是否是课后作业，默认传 1、表示非课后作业 2、表示课后作业
                ServiceExProvider.visit(getSubscription(), RetrofitManager.getInstance().getService().getPaperQuestionDetail(paperId, mCorrectMode, 1, 0, 0),
                        new NetObjResponse<PaperQuestionDetailBean>() {
                            @Override
                            public void onSuccess(BaseResponseModel<PaperQuestionDetailBean> model) {
                                if(null!=mCommloadingView){
                                    mCommloadingView.hide();
                                }
                                if (model != null && model.data != null) {
                                    mPaperQuestionDetailBean = model.data;
                                    sortQuesBean(mPaperQuestionDetailBean);

                                    if (mPaperQuestionDetailBean != null) {
                                        if (mPaperQuestionDetailBean.essayPaper != null) {
                                            paperId = mPaperQuestionDetailBean.essayPaper.paperId;
                                            answerCardId = mPaperQuestionDetailBean.essayPaper.answerCardId;
                                            if (essayAnswerId <= 0) {
                                                essayAnswerId = answerCardId;
                                            }
                                            if (mCorrectMode <= 0) {//从消息列表点击过来
                                                mCorrectMode = mPaperQuestionDetailBean.essayPaper.correctMode;//
                                            }
                                        }
                                        if (mPaperQuestionDetailBean.essayQuestions != null && mPaperQuestionDetailBean.essayQuestions.size() > 0) {
                                            mSingleQuestionDetailBean = mPaperQuestionDetailBean.essayQuestions.get(0);
                                        }
                                    }
                                    bindViewPager();
                                    refreshDetail(1);
                                }
                            }

                            @Override
                            public void onError(String message, int type) {
                                if(null!=mCommloadingView){
                                    if (type == 3)
                                        mCommloadingView.showNetworkTip();
                                    else
                                        mCommloadingView.showServerError();
                                }
                            }
                        });
            }
        }
    }

    private void sortCheckDetail(ArrayList<CheckDetailBean> data) {
        if (data != null) {
            if (data != null) {
                Collections.sort(data, new Comparator<CheckDetailBean>() {
                    @Override
                    public int compare(CheckDetailBean o1, CheckDetailBean o2) {
                        if (o1 != null && o2 != null) {
                            return o1.sort - o2.sort;
                        }
                        return 0;
                    }
                });
            }
        }
    }

    private void refreshDetail(final int type) {
        ServiceProvider.getCheckDetail(getSubscription(), type, essayAnswerId, mCorrectMode, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                // dismissProgressBar();
                if (e instanceof ApiException) {
                    String errorMsg = ((ApiException) e).getErrorMsg();
                    CommonUtils.showToast(errorMsg);
                } else {
                    CommonUtils.showToast("获取数据失败，请重试");
                }
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                // dismissProgressBar();
                if (model != null && model.data != null) {
                    ArrayList<CheckDetailBean> mList = (ArrayList<CheckDetailBean>) model.data;
                    if (type == 0) {
                        mPaperAnswerCheck.clear();
                        mPaperAnswerCheck.addAll(mList);
                        if (mList != null && mList.size() > 0) {
                            CheckDetailBean cacheCheckDetailBean = mList.get(0);
                            if (cacheCheckDetailBean != null) {

                                setCorrectConvert(cacheCheckDetailBean.convertCount);
                                refreshCurrentFragment(0, cacheCheckDetailBean);
                            }
                        }
                    } else {
                        mPaperAnswerCheck.clear();
                        mPaperAnswerCheck.addAll(mList);
                        sortCheckDetail(mPaperAnswerCheck);
                        mAllPapeScore = 0;
                        for (CheckDetailBean checkDetailBean : mPaperAnswerCheck) {
                            mAllPapeScore += checkDetailBean.score;
                        }
                        for (int i = 0; i < mList.size(); i++) {
                            boolean isSucc = refreshCurrentFragment(i, mList.get(i));
                            LogUtils.e("refreshDetail", isSucc + "_" + i);
                            if (!isSucc) break;
                        }
                    }
                }
            }
        });
    }

    private void bindViewPager() {
        refreshTitleRight();
        if (isSingle) {
            if (mSingleQuestionDetailBean != null) {
                refreshViewPager(mSingleQuestionDetailBean, null);
            }
        } else {
            if (mPaperQuestionDetailBean != null) {
                refreshViewPager(mSingleQuestionDetailBean, mPaperQuestionDetailBean);
            }
        }
    }

    public void refreshViewPager(SingleQuestionDetailBean singleQuestionDetailBean, final PaperQuestionDetailBean paperQuestionDetailBean) {

        if (mViewPager == null || viewPagerTab == null || getActivity() == null) return;
        viewPagerTab.setDividerColors(android.R.color.white);
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        int i = 0;
        // mCorrectMode=3;
        boolean isRobotCorrect = mCorrectMode == 1;//批改模式 1 智能批改，2人工批改 ,3智能转人工
        if (isSingle) {

            Bundle arg = new Bundle();
            arg.putInt("title_question", 0);
            //  arg.putString("content", singleQuestionDetailBean.content);
            arg.putInt("request_type", 1);
            arg.putBoolean("isSingle", true);
            arg.putLong("answerId", essayAnswerId);
            arg.putInt("correctMode", mCorrectMode);
            pages.add(FragmentPagerItem.of("问题1", 1.f, isRobotCorrect ? EssExCheckRobotContent.class : EssExCheckSingleContent.class, arg));
        } else {
            if (paperQuestionDetailBean != null && paperQuestionDetailBean.essayQuestions != null) {
                for (SingleQuestionDetailBean var : paperQuestionDetailBean.essayQuestions) {
                    i++;
                    Bundle arg = new Bundle();
                    arg.putInt("title_question", i - 1);
                    //arg.putSerializable("singleQuestionDetailBean", var);
                    arg.putInt("request_type", 2);
                    arg.putBoolean("isSingle", false);
                    arg.putLong("answerId", essayAnswerId);
                    arg.putInt("number", i);//增加批改详情序号问题序号，在相应展示时去取相应的内容
                    arg.putInt("correctMode", mCorrectMode);
                    pages.add(FragmentPagerItem.of("问题" + i, 1.f, isRobotCorrect ? EssExCheckRobotContent.class : EssExCheckSingleContent.class, arg));
                }
            }
        }
        mViewPager.setOffscreenPageLimit(3);
        adapter = new FragmentStatePagerItemAdapter(getChildFragmentManager(), pages);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (null != mBJPlayView && (mBJPlayView.isPlaying())) {
                    mPlayingPause = false;
                    mBJPlayView.pauseVideo();
                }

                if (null != mBjAudioPlayerView) {
                    mBjAudioPlayerView.pausePlay();

                }
            }
        });
        viewPagerTab.setViewPager(mViewPager);
        if (isSingle) {
            viewPagerTab.setVisibility(View.GONE);
        } else {
            viewPagerTab.setVisibility(View.VISIBLE);
        }
    }

    private void setCorrectConvert(int convertCount) {
        if (isSingle && mCorrectMode == 1) {
            mCoverManulView.setVisibility(View.VISIBLE);
            mCoverManulView.setData(questionType, paperId <= 0 ? questionBaseId : paperId, essayAnswerId, isSingle, convertCount);
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        if (adapter != null && (mViewPager != null)) {
            if (null != mBJPlayView && (mBJPlayView.isFullScreen())) {
                mBJPlayView.quitWindowFullscreen();
                return true;
            } else if (!ActivityStack.getInstance().hasRootActivity()) {
                MainTabActivity.newIntent(getActivity());
            }
            return false;
        }
        return false;
    }

    private boolean refreshCurrentFragment(int position, CheckDetailBean bean) {
        if (adapter != null) {
            Fragment curFragment = adapter.getPage(position);
            if (null == curFragment)
                return false;
            if (curFragment.isAdded() && (!((AbsSettingFragment) curFragment).isFragmentFinished())) {
                //||(!(curFragment instanceof EssExCheckSingleContent||curFragment instanceof EssExCheckRobotContent))
                if (curFragment instanceof EssExCheckSingleContent)
                    ((EssExCheckSingleContent) curFragment).refreshSingleView(bean);
                else if (curFragment instanceof EssExCheckRobotContent) {
                    ((EssExCheckRobotContent) curFragment).refreshSingleView(bean);
                }
            }

            return true;
        }
        return false;
    }

    /**
     * 显示材料卡片
     */
    private void showBottomMaterial() {
        if ((isSingle && mCachePaperMaterialListBeans != null) || (!isSingle && mCachePaperMaterialListBeans != null)) {

            // 埋点 申论查看资料
            String on_module;
            String exam_title = "";
            String exam_id;
            if (isFromArgue) {
                on_module = "文章写作";
                exam_id = Long.toString(questionBaseId);
            } else if (isSingle) {
                on_module = "标准答案";
                exam_id = Long.toString(questionBaseId);
            } else {
                on_module = "套题";
                exam_title = titleView;
                exam_id = Long.toString(paperId);
            }
            StudyCourseStatistic.viewEssayMaterial(on_module, exam_title, exam_id);

            MaterialsCardDialogFragment ratefragment = new MaterialsCardDialogFragment();
            ratefragment.setCacheExamMaterialList(mCachePaperMaterialListBeans);
            ratefragment.show(getActivity().getSupportFragmentManager(), "materialsCardFragment");
            //materialsCardView.show();
        } else {
            //ToastUtils.showMessage("材料还没准备好，请稍等！");
            getMaterialList(isSingle);
        }
    }

    CustomLoadingDialog mLoadingDialog;

    private void getMaterialList(boolean isSingle) {
        mLoadingDialog = DialogUtils.showLoading(getActivity(), mLoadingDialog);
        ServiceExProvider.visitList(getSubscription(), isSingle ? RetrofitManager.getInstance().getService().getSingleMaterialList(questionBaseId)
                : RetrofitManager.getInstance().getService().getPaperMaterials(paperId), new NetListResponse<ExamMaterialListBean>() {
            @Override
            public void onSuccess(BaseListResponseModel<ExamMaterialListBean> model) {
                DialogUtils.dismissLoading(mLoadingDialog);
                mCachePaperMaterialListBeans = model.data;
                showBottomMaterial();
            }

            @Override
            public void onError(String message, int type) {
                DialogUtils.dismissLoading(mLoadingDialog);
                CommonUtils.showToast(message);
            }
        });
    }


    private void downLoadPdf() {
        // 下载pdf相关

        String filePath = refreshTitleRight();
        if (filePath != null && FileUtil.isFileExist(filePath)) {
            FileUtil.readPDF(getActivity(), filePath);
            return;
        }

        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        }
        if (isSingle) {
            getMaterialsDownloadUrl(0, 0, essayAnswerId, 0);
        } else {
            //套题批改下载，传paperAnswerId,第一个
            getMaterialsDownloadUrl(essayAnswerId, 0, 0, 0);
        }
    }

    private void getMaterialsDownloadUrl(long paperAnswerId, long paperBaseId, long questionAnswerId, long questionBaseId) {
        mLoadingDialog = DialogUtils.showLoading(getActivity(), mLoadingDialog);
        ServiceProvider.getMaterialsDownloadUrl(getSubscription(), paperAnswerId, paperBaseId, questionAnswerId, questionBaseId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showEssayToast("下载失败，请稍后重试");
                DialogUtils.dismissLoading(mLoadingDialog);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                DialogUtils.dismissLoading(mLoadingDialog);
                if (model != null && model.data != null) {
                    MaterialsFileUrlBean cacheMaterialsFileUrlBean = (MaterialsFileUrlBean) model.data;
                    downloadMaterPaper(cacheMaterialsFileUrlBean);

                } else {
                    ToastUtils.showEssayToast("下载失败，请稍后重试");
                }
            }
        });
    }

    /**
     * 刷新当前题目pdf是否已经下载到本地
     */
    private String refreshTitleRight() {
        DlEssayDataCache essayDataCache = DlEssayDataCache.getInstance();
        essayDataCache.readDownloadFilePathFromFile();
        String filePath = essayDataCache.fileCachePath(1, isSingle, true, essayAnswerId, mSingleQuestionDetailBean, mPaperQuestionDetailBean, null);
        String size = "";
        if (filePath != null) {
            try {
                size = FileUtil.getFileSize(filePath);
            } catch (Throwable t) {
            }
        }
        if (filePath != null && !"".equals(size) && FileUtil.isFileExist(filePath)) {
            hasPdfFile = true;
            // 已经下载，刷新显示状态
            ivDownload.setImageResource(R.mipmap.download_essay);

            // 已下载，如果是课后作业，要把课后作业的对应下载类型改成课后作业
            // 因为此课后作业可能是在不是课后作业的时候下载的，就没有是课后作业的信息
            if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {
                long id;
                ArrayList<DownloadEssayBean> essayBeansList;
                if (isSingle) {
                    if (isFromArgue) {
                        essayBeansList = essayDataCache.argue_dl_successList;
                    } else {
                        essayBeansList = essayDataCache.dl_successList;
                    }
                    id = questionBaseId;
                } else {
                    essayBeansList = essayDataCache.multi_dl_successList;
                    id = paperId;
                }
                if (essayBeansList != null && essayBeansList.size() > 0) {
                    for (DownloadEssayBean downloadEssayBean : essayBeansList) {
                        if (downloadEssayBean.downLoadId == id) {
                            downloadEssayBean.type = 1;
                            break;
                        }
                    }
                }
                essayDataCache.writeToFileCache();
            }

        } else {
            // 未下载
            hasPdfFile = false;
            ivDownload.setImageResource(R.mipmap.download_paper_icon);
        }
        return filePath;
    }

    private CorrectDialog homeworkDownloadTip;
    private CheckBox checkBox;

    /**
     * 下载试题
     */
    private void downloadMaterPaper(MaterialsFileUrlBean cacheMaterialsFileUrlBean) {
        if (cacheMaterialsFileUrlBean != null) {
            if (cacheMaterialsFileUrlBean.pdfPath != null && cacheMaterialsFileUrlBean.pdfPath.startsWith("http")) {
                DownloadEssayBean downloadEssayBean = new DownloadEssayBean();
                downloadEssayBean.downloadUrl = cacheMaterialsFileUrlBean.pdfPath;
                if (isSingle) {
                    if (!isFromArgue) {
                        downloadEssayBean.downtype = 0;
                    } else {
                        downloadEssayBean.downtype = 2;
                    }
                    if (!TextUtils.isEmpty(areaName)) {
                        downloadEssayBean.title = titleView + "（" + areaName + "）";
                    } else {
                        downloadEssayBean.title = titleView;
                    }
                } else {
                    downloadEssayBean.downtype = 1;
                    String var2 = EssayExamDataCache.getInstance().titleArea;
                    if (var2 != null) {
                        downloadEssayBean.title = titleView + "（" + var2 + "）";
                    } else {
                        if (!TextUtils.isEmpty(areaName)) {
                            downloadEssayBean.title = titleView + "（" + areaName + "）";
                        } else {
                            downloadEssayBean.title = titleView;
                        }
                    }
                }
                downloadEssayBean.fileSize = cacheMaterialsFileUrlBean.fileSize;
                downloadEssayBean.time = DateUtils.getCurrentTime2();
                downloadEssayBean.check = 1;
                downloadEssayBean.isSingle = isSingle;
                downloadEssayBean.isStartToCheckDetail = true;
                downloadEssayBean.downLoadId = essayAnswerId;
                downloadEssayBean.filepath = DownLoadEssayHelper.getInstance().getDownloadFilePath(downloadEssayBean);
                if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {     // 课后作业
                    downloadEssayBean.type = 1;
                }

                DlEssayDataCache var2 = DlEssayDataCache.getInstance();
                var2.fileCachePath(0, isSingle, true, essayAnswerId, mSingleQuestionDetailBean, mPaperQuestionDetailBean, downloadEssayBean.filepath);
                var2.writeDownloadFilePathToFile();

                if (DlEssayDataCache.getInstance().isContains(downloadEssayBean)) {
//                    ToastUtils.showEssayToast("已添加至下载列表");
                } else {
                    DownLoadEssayHelper.getInstance().startDowningEssay(downloadEssayBean);
//                    ToastUtils.showEssayToast("已添加至下载列表");
                }

                if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {     // 课后作业下载开始要弹dialog提示
                    if (SpUtils.getHomeworkDownloadTipsShow()) {
                        homeworkDownloadTip = new CorrectDialog(getActivity(), R.layout.homework_download_tip);
                        View mContentView = homeworkDownloadTip.mContentView;
                        TextView tvTip = mContentView.findViewById(R.id.tv_tip);
                        checkBox = mContentView.findViewById(R.id.check_box);
                        TextView tvKnow = mContentView.findViewById(R.id.tv_know);
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append("完成后请前往题库-申论-下载列表中根据题目类型查看");
                        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#FF3F47")), 6, 14, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        tvTip.setText(builder);
                        tvKnow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (homeworkDownloadTip != null) {
                                    homeworkDownloadTip.dismiss();
                                }
                                if (checkBox != null && checkBox.isChecked()) {
                                    SpUtils.setHomeworkDownloadTipsShow();
                                }
                            }
                        });
                        homeworkDownloadTip.show();
                    }
                }
            } else {
                ToastUtils.showEssayToast("此资料暂不支持下载~");
                LogUtils.e(TAG, "!pdfPath.startsWith(\"http\")");
            }
        }
    }

/*    @Override
    public boolean onBackPressed() {
        if (super.onBackPressed()) return true;
        if (materialsCardView.isShowMaterialCard()) {
            materialsCardView.hide();
            return true;
        }
        if (isSingle || !paperReportFlag) {
            mActivity.finish();
        } else {
            EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_start_CheckToReport));
        }
        return true;
    }*/

    @OnClick({R.id.iv_back, R.id.iv_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (mIsFromReport) {
                    pop();

                } else {
                    if (!ActivityStack.getInstance().hasRootActivity()) {
                        MainTabActivity.newIntent(getActivity());
                    }
                    getActivity().finish();
                }

                break;
            case R.id.iv_download:
                if (!NetUtil.isConnected()) {
                    ToastUtils.showMessage("无网络连接！");
                    return;
                }
                downLoadPdf();
                break;
        }
    }


}
