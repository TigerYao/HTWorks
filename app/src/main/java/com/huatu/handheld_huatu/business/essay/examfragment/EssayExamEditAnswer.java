package com.huatu.handheld_huatu.business.essay.examfragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamTimerHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayViewHelper;
import com.huatu.handheld_huatu.business.essay.cusview.CorrectDialog;
import com.huatu.handheld_huatu.business.essay.cusview.EssayExamBottomView;
import com.huatu.handheld_huatu.business.essay.cusview.MaterialsCardView;
import com.huatu.handheld_huatu.business.essay.cusview.ObArrayList;
import com.huatu.handheld_huatu.business.essay.cusview.RightOperatorTextView;
import com.huatu.handheld_huatu.business.essay.cusview.SoftInputLayout;
import com.huatu.handheld_huatu.business.essay.cusview.TextSizeControlImageView;
import com.huatu.handheld_huatu.business.essay.cusview.imgdrag.AnswerImage;
import com.huatu.handheld_huatu.business.essay.essayroute.EssayRoute;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.essay.CreateAnswerCardIdBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CreateAnswerCardPostBean;
import com.huatu.handheld_huatu.mvpmodel.essay.EssayAnswerImageSortBean;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.TeacherBusyStateInfo;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItems;
import com.ogaclejapan.v4.FragmentStatePagerItemAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作答页面
 * 申论模考大赛，用户进入后一直停留在材料页的处理方案：
 * 1、此情况下最终不产生批改报告，和报名了但是未进入答题等同处理；
 * 2、若该用户一直停留在材料页直至考试结束，则考试结束时toast提示“考试已结束，您仍未作答”后返回报名页。
 */
public class EssayExamEditAnswer extends BaseFragment implements SoftInputLayout.OnSoftInputChangeListener {

    private static final String TAG = "EssayExamEditAnswer";

    @BindView(R.id.root_layout)
    RelativeLayout rootView;                            // 跟布局

    //页面ui调整,顶部以ID为准
    @BindView(R.id.action_bar_title)
    TextView tvTime;                                    // 中间的计时title
    @BindView(R.id.iv_back)
    ImageView ivBack;                                   // 返回键
    @BindView(R.id.tv_change_check)
    TextView tvChangeCheck;                             // 改变人工批改，智能批改的按钮

    @BindView(R.id.iv_text_size)
    TextSizeControlImageView ivTextSize;                // 改变字体大小按钮

    @BindView(R.id.ex_materials_ques_viewpager_tab)
    SmartTabLayout viewPagerTab;                        // TabLayout
    @BindView(R.id.ex_materials_ques_viewpager)
    ViewPager viewPager;                                // ViewPager
    @BindView(R.id.back_exam_materials)
    RightOperatorTextView rightButton;                  // 右边可拖拽的按钮

    @BindView(R.id.rl_controller)
    RelativeLayout rlController;                        // 底部的控制布局，在人工批改，上传图片的时候需要
    @BindView(R.id.bottom_left_text)
    TextView bottomLeftText;                            // xxx
    @BindView(R.id.tv_total)
    TextView tvTotal;                                   // 总字数

    @BindView(R.id.ess_ex_input_camera_iv)
    ImageView essExInputCameraIv;                       // 拍照输入
    @BindView(R.id.ess_ex_input_voice_iv)
    ImageView essExInputVoiceIv;                        // 声音输入
    @BindView(R.id.ess_ex_input_soft_iv)
    ImageView essExInputSoftIv;                         // 打字输入
    @BindView(R.id.ll_post)
    LinearLayout llPost;                                // 交卷按钮
    @BindView(R.id.iv_post)
    ImageView ivPost;
    @BindView(R.id.tv_post)
    TextView tvPost;
    @BindView(R.id.essay_exam_bottomView)
    EssayExamBottomView edit_bottom_view;               // 整个底部布局
    @BindView(R.id.softinput_layout)
    SoftInputLayout mSoftInputLayout;                   // 整个布局

    @BindView(R.id.materials_card_view)
    MaterialsCardView materialsCardView;                // 材料卡片

    private EssayExamImpl mEssayExamImpl;

    private Bundle extraArgs;                                           // 上个页面过来携带的各种信息
    private boolean isAutoSubmit = false;                               // 是否自动提交
    private String titleView;                                           // 试卷名称
    private boolean isSingle;                                           // 单题
    public long questionDetailId;                                       // 问题Id
    private long areaId;                                                // 那个地区的套题，地区Id
    private int curPos;                                                 // 当前是哪个答题页
    private boolean isFromArgue;                                        // 是不是文章写作
    private int commitLimitTime;                                        // 可提前交卷时间 30 考试结束前30分钟之内可以交卷

    // 课后作业需要
    private int courseType;                     // 1、录播 2、直播 3、直播回放
    private long courseId;                      // 课程Id
    private long courseWareId;                  // 课件ID
    private long syllabusId;                    // 大纲ID

    private int staticCorrectMode = 0;                                  // 指定的批改方式，不可切换 0、不限 1、智能 2、人工

    private List<String> titleMatrQues = new ArrayList<String>();            // 问题1 问题2 问题3 Tab

    private String photoData;                                           // 图片识别的内容
    private String fileName;                                            // （可能是图片识别拍照的图片名称）

    private EssExEditAnswerContent mEssExEditAnswerContent;             // 当前的答题页

    private FragmentStatePagerItemAdapter adapter;                      // 答题Fragment Adapter

    public long questionBaseId;                                         // 当前这道题的Id，一个单体组，会有不同地区属性，每个地区的这道题的id
    public long answerCardId;                                           // 答题卡Id（网络创建）
    private long paperId;                                               // 试卷Id

    private SingleQuestionDetailBean mSingleQuestionDetailBean;         // 单题详情，从缓存数据中获取，或者套题的当前题目
    private PaperQuestionDetailBean mPaperQuestionDetailBean;           // 套题详情

    private EssayViewHelper mEssayViewHelper;                           // 一些工具类

    private int limitMaxLength = 30;                                    // 提交限制最大字数
    private int inputLimitMaxLength = 30;                               // 输入限制最大字数

    public boolean isClickSubmit = false;                               // 是否点击了提交

    private ViewTreeObserver.OnGlobalLayoutListener softInputLayoutListener;                // 为了键盘吧底部栏顶起来而做的Global监听

    private SparseArray<ObArrayList<AnswerImage>> essayImgMap = new SparseArray<>();        // 保存答题页图片列表
    private boolean isShowCheck = false;                                                    // 是否显示校对页

    @Override
    public int onSetRootViewId() {
        return R.layout.essay_exam_edit_answer_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null || mEssayExamImpl == null) {
            return false;
        }

        if (event.type == EssayExamMessageEvent.EssayExam_time_heartbeat) {                         // 每秒计时
            setTime();
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_createAnswerCard) {            // 创建答题卡成功
            if (mEssayExamImpl.answerCardIdBean != null) {
                answerCardId = mEssayExamImpl.answerCardIdBean.answerCardId;
            }
            if (isSingle) {
                if (mSingleQuestionDetailBean != null) {
                    mSingleQuestionDetailBean.answerCardId = answerCardId;
                }
            } else {
                if (mPaperQuestionDetailBean != null && mPaperQuestionDetailBean.essayPaper != null && mEssayExamImpl.answerCardIdBean != null) {
                    mPaperQuestionDetailBean.essayPaper.answerCardId = answerCardId;
                    // 问题列表
                    List<SingleQuestionDetailBean> essayQuestions = mPaperQuestionDetailBean.essayQuestions;
                    // 答题卡id列表
                    ArrayList<CreateAnswerCardIdBean.QuestionAnswerCard> questionAnswerCardList = mEssayExamImpl.answerCardIdBean.questionAnswerCardList;
                    // 把每道题的答题卡id赋给每道问题
                    for (SingleQuestionDetailBean essayQuestion : essayQuestions) {
                        for (CreateAnswerCardIdBean.QuestionAnswerCard questionAnswerCard : questionAnswerCardList) {
                            if (essayQuestion.questionBaseId == questionAnswerCard.questionBaseId) {
                                essayQuestion.answerCardId = questionAnswerCard.id;
                                break;
                            }
                        }
                    }
                }
            }
            if (isClickSubmit) {
                commitVerify();
            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_paperCommit_fail) {            // 交卷失败
            isClickSubmit = false;
        } else if (event.type == EssayExamMessageEvent.EssayExam_start_EssayExamEditAnswer1) {
            onPageSelectPaper(curPos);
        } else if (event.type == EssayExamMessageEvent.EssayExam_start_EssayExamEditAnswer) {       // 材料点击 右边按钮，跳Edit页
            // 套题只走到对应的问题curPos页面
            if (!isSingle) {
                curPos = event.extraBundle.getInt("curPos", 0);
                if (curPos < adapter.getCount()) {
                    viewPager.setCurrentItem(curPos);
                }
            }
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_AUTO_COMMIT_PAPER) {               // 自动交卷
            hideSoft();
            stopRec();
            refreshAllAnswerCache();
            onClickCommit2();
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_MOCK_AUTO_SAVE_PAPER) {            // 自动保存
            saveAnswer();
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_essExMaterialsContent_setTextSize) {
            ivTextSize.initStyle();
        } else if (event.type == EssayExamMessageEvent.EssayExam_CHANGE_CORRECT_TYPE_GETED) {       // 改变批改方式/切换地区，材料获取数据成功，通知这里刷新内容，并重新开始计时
            curPos = 0;
            onLoadData();
        } else if (event.type == EssayExamMessageEvent.EssayExam_SINGLE_CHANGE_ARED) {              // 单题切换地区，清楚答题页存储的图片信息
            saveAnswer();
            essayImgMap.clear();
        }
        return true;
    }

    @Override
    protected void onInitView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (rightButton != null) {
            rightButton.resetPosition();
        }

        ivTextSize.setType(1, "作答页");

        initImpl();

        Typeface fontStyle = Typeface.createFromAsset(mActivity.getAssets(), "font/851-CAI978.ttf");

        tvTime.setTypeface(fontStyle);

        materialsCardView.setChildFragmentManager(getChildFragmentManager());

        initData();
        setBottomView();
        initRightButton();

        if (isEssaySc() || staticCorrectMode != 0) {                            // 如果是模考，只能是智能答题。或者指定答题方式
            ivTextSize.setVisibility(View.VISIBLE);
            tvChangeCheck.setVisibility(View.GONE);
        } else if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {      // 如果是课后作业，就只显示人工答题，不能改变答题方式
            ivTextSize.setVisibility(View.GONE);
            tvChangeCheck.setVisibility(View.VISIBLE);
            tvChangeCheck.setCompoundDrawables(null, null, null, null);
        } else {
            ivTextSize.setVisibility(View.GONE);
            tvChangeCheck.setVisibility(View.VISIBLE);
            tvChangeCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCorrectModeDialog();
                }
            });
        }

        if (EssayExamDataCache.getInstance().correctMode == 2) {
            tvChangeCheck.setText("人工批改");
        } else {
            tvChangeCheck.setText("智能批改");
        }
    }

    @Override
    protected void onInitListener() {
        softInputLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            int visitHeight = 0;

            // 当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                // 获取当前界面可视部分
                mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

                if (visitHeight != r.bottom) {
                    visitHeight = r.bottom;
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSoftInputLayout.getLayoutParams();
                    layoutParams.height = visitHeight;
                    mSoftInputLayout.setLayoutParams(layoutParams);
                    if (rightButton != null) {
                        rightButton.resetPosition();
                    }
                }
            }
        };
        // 监听屏幕高度变化，适配键盘弹出
        mSoftInputLayout.getViewTreeObserver().addOnGlobalLayoutListener(softInputLayoutListener);
    }

    @Override
    protected void onLoadData() {

        refreshData();
        createAnswerId();
        showData();
        setMaterialData();

        startTimer();

        if (isAutoSubmit) {
            TimeUtils.delayTask(new Runnable() {
                @Override
                public void run() {
                    hideSoft();
                    stopRec();
                    refreshAllAnswerCache();
                    onClickCommit2();

                }
            }, 2000);
        }
    }

    @OnClick({R.id.iv_back, R.id.ess_ex_input_camera_iv, R.id.ess_ex_input_voice_iv, R.id.ess_ex_input_soft_iv, R.id.ll_post})
    public void onClick(View view) {
        if (!EssayExamTimerHelper.getInstance().isEnableExam()) {
            switch (view.getId()) {
                case R.id.ess_ex_input_camera_iv:
                case R.id.ess_ex_input_voice_iv:
                case R.id.ess_ex_input_soft_iv:
                case R.id.ll_post:
                    Toast.makeText(mActivity, "考试等待中...", Toast.LENGTH_SHORT).show();
                    return;
            }
        }
        switch (view.getId()) {
            case R.id.iv_back:              // 返回按钮
                // 埋点 申论作答页返回键
                StudyCourseStatistic.clickStatistic("题库->申论", "作答页", "返回");
                onBackPressed();
                break;
            case R.id.ll_post:              // 交卷
                clickCommit();
                break;
            case R.id.ess_ex_input_camera_iv:                           // 去拍照识别
                if (curViewVerify()) return;
                if (mEssExEditAnswerContent != null) {
                    if (mEssayViewHelper != null) {
                        mEssayViewHelper.vCameraPer(mActivity);
                    }
                    edit_bottom_view.onStopRecognizer();
                    mSoftInputLayout.hideEmojiLayout();
                }
                edit_bottom_view.onViewClicked(2);
                break;
            case R.id.ess_ex_input_voice_iv:                            // 声音识别
                if (curViewVerify()) return;
                if (mEssayViewHelper != null) {
                    mEssayViewHelper.vAudioPer(mActivity, new EssayViewHelper.OnHelperCallBack() {
                        @Override
                        public boolean doSomething(Object isOpen) {
                            if (!mSoftInputLayout.isEmojiLayoutShow) {
                                if (mEssExEditAnswerContent != null) {
                                    edit_bottom_view.setEditText(mEssExEditAnswerContent.getEditV(), limitMaxLength, inputLimitMaxLength);
                                }
                                mSoftInputLayout.showEmojiLayout();
                                edit_bottom_view.onViewClicked(1);
                            } else {
                                stopRec();
                            }
                            return false;
                        }
                    });
                }
                break;
            case R.id.ess_ex_input_soft_iv:                             // 键盘输入
                if (mEssExEditAnswerContent != null) {
                    mEssExEditAnswerContent.showHideSoftInput(EssayExamMessageEvent.EssayExam_show_soft);
                    edit_bottom_view.onStopRecognizer();
                    mSoftInputLayout.hideEmojiLayout();
                }
                edit_bottom_view.onViewClicked(0);
                break;
        }
    }

    /**
     * 点击提交
     */
    private void clickCommit() {

        if (!NetUtil.isConnected()) {
            ToastUtils.showMessage("无网络连接！");
            return;
        }

        // 如果是模考并且有交卷时间限制
        if (requestType == EssayExamActivity.ESSAY_EXAM_SC && commitLimitTime > 0) {
            if (mPaperQuestionDetailBean != null && mPaperQuestionDetailBean.essayPaper != null) {
                if (mPaperQuestionDetailBean.essayPaper.remainTime > commitLimitTime * 60) {
                    // 如果剩余时间大于可提交的提前时间，就不提交并且试题
                    ToastUtils.showShort("考试结束前" + commitLimitTime + "分钟才可以交卷");
                    return;
                }
            }
        }

        // 埋点 申论点击交卷
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
        StudyCourseStatistic.submitEssayAnswer(on_module, exam_title, exam_id);

        if (isClickSubmit) return;
        isClickSubmit = true;
        onClickCommit();
    }

    private void initImpl() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        mEssayExamImpl = new EssayExamImpl(compositeSubscription);
        mEssayViewHelper = new EssayViewHelper();
    }

    /**
     * 初始化数据
     * 读取缓存中的数据
     * 设置材料卡片数据
     */
    private void initData() {
        if (args != null) {
            requestType = args.getInt("request_type");
            extraArgs = args.getBundle("extra_args");
            isAutoSubmit = args.getBoolean("auto_submit", false);
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            titleView = extraArgs.getString("titleView");
            isSingle = extraArgs.getBoolean("isSingle");
            questionDetailId = extraArgs.getLong("questionDetailId");
            areaId = extraArgs.getInt("areaId");
            isFromArgue = extraArgs.getBoolean("isFromArgue");
            curPos = extraArgs.getInt("curPos", 0);

            commitLimitTime = extraArgs.getInt("commitLimitTime", -1);

            staticCorrectMode = extraArgs.getInt("staticCorrectMode", 0);

            courseType = extraArgs.getInt("courseType", 0);
            courseId = extraArgs.getLong("courseId", 0);
            courseWareId = extraArgs.getLong("courseWareId", 0);
            syllabusId = extraArgs.getLong("syllabusId", 0);
        }
    }

    /**
     * 从缓存类中获取题目数据
     */
    private void refreshData() {
        if (isSingle) {
            SingleQuestionDetailBean var = EssayExamDataCache.getInstance().cacheSingleQuestionDetailBean;
            if (var != null) {
                mSingleQuestionDetailBean = var;
            }
            if (mSingleQuestionDetailBean != null) {
                answerCardId = mSingleQuestionDetailBean.answerCardId;
                questionBaseId = mSingleQuestionDetailBean.questionBaseId;
                questionDetailId = mSingleQuestionDetailBean.questionDetailId;
                limitMaxLength = mSingleQuestionDetailBean.commitWordNumMax;
                inputLimitMaxLength = mSingleQuestionDetailBean.inputWordNumMax;
            }
        } else {
            PaperQuestionDetailBean var = EssayExamDataCache.getInstance().cachePaperQuestionDetailBean;
            if (var != null) {
                mPaperQuestionDetailBean = var;
            }
            if (mPaperQuestionDetailBean != null) {
                if (mPaperQuestionDetailBean.essayPaper != null) {
                    answerCardId = mPaperQuestionDetailBean.essayPaper.answerCardId;
                }
                if (mPaperQuestionDetailBean.essayPaper != null) {
                    paperId = mPaperQuestionDetailBean.essayPaper.paperId;
                }
                if (mPaperQuestionDetailBean.essayQuestions != null && mPaperQuestionDetailBean.essayQuestions.size() > curPos) {
                    mSingleQuestionDetailBean = mPaperQuestionDetailBean.essayQuestions.get(curPos);
                    if (mSingleQuestionDetailBean != null) {
                        limitMaxLength = mSingleQuestionDetailBean.commitWordNumMax;
                        inputLimitMaxLength = mSingleQuestionDetailBean.inputWordNumMax;
                    }
                }
            }
        }
        // 答题页的，拍照按钮。人工答题不显示
        if (EssayExamDataCache.getInstance().correctMode == 2) {
            essExInputCameraIv.setVisibility(View.GONE);
        } else {
            essExInputCameraIv.setVisibility(View.VISIBLE);
        }
    }

    // 设置材料卡片数据
    private void setMaterialData() {
        if (isSingle) {
            materialsCardView.setData(EssayExamDataCache.getInstance().cacheSingleMaterialListBeans);
        } else {
            materialsCardView.setData(EssayExamDataCache.getInstance().cachePaperMaterialListBeans);
        }
    }

    /**
     * 初始化底部操作栏
     */
    private void setBottomView() {
        mSoftInputLayout.setOnSoftInputChangeListener(this);
        edit_bottom_view.setInitView(EssayExamEditAnswer.this, bottomLeftText, tvTotal, essExInputCameraIv, essExInputVoiceIv, essExInputSoftIv, llPost, ivPost, tvPost);
        edit_bottom_view.setNormal();

        int mPhoto = SpUtils.getUpdatePhotoAnswer();
        if (mPhoto == 0) {
            essExInputCameraIv.setVisibility(View.VISIBLE);
        } else {
            essExInputCameraIv.setVisibility(View.GONE);
        }

        int mVoice = SpUtils.getUpdateVoiceAnswer();
        if (mVoice == 0) {
            essExInputVoiceIv.setVisibility(View.VISIBLE);
        } else {
            essExInputVoiceIv.setVisibility(View.GONE);
        }
        if (EssayExamDataCache.getInstance().correctMode == 2) {
            rlController.setVisibility(View.GONE);
        } else {
            rlController.setVisibility(View.VISIBLE);
        }
    }

    private void startTimer() {
        setTime();
        if (!isEssaySc()) {
            EssayExamTimerHelper.getInstance().initTimeSubscription(isSingle, requestType, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
        }
    }

    private void setTime() {
        if (tvTime != null) {
            int time = 0;
            if (requestType == EssayExamActivity.ESSAY_EXAM_SC) {      // 模考大赛
                if (mPaperQuestionDetailBean != null && mPaperQuestionDetailBean.essayPaper != null) {
                    time = mPaperQuestionDetailBean.essayPaper.remainTime;
                }
            } else {                                                    // 非模考大赛
                if (isSingle) {
                    if (mSingleQuestionDetailBean != null) {
                        time = mSingleQuestionDetailBean.spendTime;
                    }
                } else {
                    if (mPaperQuestionDetailBean != null) {
                        time = mPaperQuestionDetailBean.spendTime;
                    }
                }
            }
            tvTime.setText(TimeUtils.getSecond22HourMinTime(time));
        }
    }

    /**
     * 初始化右边的显示材料卡片按钮
     */
    private void initRightButton() {
        if (rightButton != null) {
            rightButton.setOnCusViewCallBack(new RightOperatorTextView.OnCusViewCallBack() {
                @Override
                public boolean isActionUp(boolean isOpen) {
                    hideSoft();

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

                    materialsCardView.show();
                    return false;
                }
            });
        }
    }

    /**
     * 显示数据
     */
    private void showData() {
        if (mEssayExamImpl != null) {
            if (isSingle) {                                 // 单题
                if (mSingleQuestionDetailBean != null) {    // 缓存中读取的
                    refreshView(mSingleQuestionDetailBean, null);       // 刷新答题卡
                }
            } else {
                if (mPaperQuestionDetailBean != null) {
                    refreshView(mSingleQuestionDetailBean, mPaperQuestionDetailBean);
                }
            }
        }
    }

    /**
     * 创建答题卡
     */
    private void createAnswerId() {
        if (mEssayExamImpl != null) {
            CreateAnswerCardPostBean createAnswerCardPostBean = new CreateAnswerCardPostBean();
            createAnswerCardPostBean.correctMode = EssayExamDataCache.getInstance().correctMode;
            createAnswerCardPostBean.terminal = 1;
            if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {         // 申论课后作业创建答题卡
                createAnswerCardPostBean.courseType = courseType;
                createAnswerCardPostBean.courseId = courseId;
                createAnswerCardPostBean.courseWareId = courseWareId;
                createAnswerCardPostBean.syllabusId = syllabusId;

                if (isSingle) {
                    if (mSingleQuestionDetailBean != null) {
                        if (answerCardId <= 0) {                // 答题卡信息，如果答题卡数字小于0，就重新请求网络
                            createAnswerCardPostBean.questionBaseId = mSingleQuestionDetailBean.questionBaseId;
                            createAnswerCardPostBean.type = 0;
                        }
                    }
                } else {
                    if (mPaperQuestionDetailBean != null) {
                        if (answerCardId <= 0) {
                            if (mPaperQuestionDetailBean.essayPaper != null) {
                                paperId = mPaperQuestionDetailBean.essayPaper.paperId;
                            }
                            createAnswerCardPostBean.paperBaseId = paperId;
                            createAnswerCardPostBean.type = 1;
                        }
                    }
                }

            } else if (isSingle) {                                              // 其他的单题创建答题卡
                if (mSingleQuestionDetailBean != null) {
                    if (answerCardId <= 0) {                // 答题卡信息，如果答题卡数字小于0，就重新请求网络
                        createAnswerCardPostBean.questionBaseId = mSingleQuestionDetailBean.questionBaseId;
                        createAnswerCardPostBean.type = 0;
                    }
                }
            } else {                                                            // 其他的套题创建答题卡
                if (mPaperQuestionDetailBean != null) {
                    if (answerCardId <= 0) {
                        if (!isEssaySc()) {
                            if (mPaperQuestionDetailBean.essayPaper != null) {
                                paperId = mPaperQuestionDetailBean.essayPaper.paperId;
                            }
                            createAnswerCardPostBean.paperBaseId = paperId;
                            createAnswerCardPostBean.type = 1;
                        }
                    }
                }
            }

            if (createAnswerCardPostBean.paperBaseId != 0 || createAnswerCardPostBean.questionBaseId != 0) {       // 赋值了，那就创建
                mEssayExamImpl.createAnswerCardNew(requestType, createAnswerCardPostBean);
            }
        }
    }

    /**
     * 获取完数据，刷新UI
     */
    private void refreshView(final SingleQuestionDetailBean singleQuestionDetailBean, final PaperQuestionDetailBean paperQuestionDetailBean) {
        if (singleQuestionDetailBean != null) {
            refreshViewPager(singleQuestionDetailBean, paperQuestionDetailBean);                            // 初始化ViewPager
            TimeUtils.delayTask(new Runnable() {
                @Override
                public void run() {
                    if (viewPager != null) {
                        viewPager.setCurrentItem(curPos, false);
                    }
                    onPageSelectPaper(curPos);
                }
            }, 100);
        }
    }

    /**
     * 添加答题Fragment
     */
    public void refreshViewPager(SingleQuestionDetailBean singleQuestionDetailBean, final PaperQuestionDetailBean paperQuestionDetailBean) {

        if (viewPager == null || viewPagerTab == null || mActivity == null) return;

        viewPagerTab.setDividerColors(android.R.color.white);

        FragmentPagerItems pages = new FragmentPagerItems(mActivity);
        int i = 0;
        if (isSingle) {
            titleMatrQues.clear();
            titleMatrQues.add("问题");
            Bundle arg = new Bundle();
            arg.putString("title_question", "1");
            arg.putSerializable("singleQuestionDetailBean", singleQuestionDetailBean);
            arg.putInt("request_type", requestType);
            arg.putBoolean("isSingle", true);
            arg.putInt("correctMode", EssayExamDataCache.getInstance().correctMode);
            pages.add(FragmentPagerItem.of("1", 1.f, EssExEditAnswerContent.class, arg));
        } else {
            if (paperQuestionDetailBean != null && paperQuestionDetailBean.essayQuestions != null) {
                titleMatrQues.clear();
                for (SingleQuestionDetailBean var : paperQuestionDetailBean.essayQuestions) {
                    Bundle arg = new Bundle();
                    arg.putInt("index", i);
                    i++;
                    titleMatrQues.add("问题" + i);
                    arg.putString("title_question", "问题" + i);
                    arg.putSerializable("singleQuestionDetailBean", var);
                    arg.putInt("request_type", requestType);
                    arg.putBoolean("isSingle", false);
                    arg.putInt("correctMode", EssayExamDataCache.getInstance().correctMode);
                    pages.add(FragmentPagerItem.of("问题" + i, 1.f, EssExEditAnswerContent.class, arg));
                }
            }
        }
        if (i < 15) {
            viewPager.setOffscreenPageLimit(i);
        }
        adapter = new FragmentStatePagerItemAdapter(getChildFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                onPageSelectPaper(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPagerTab.setViewPager(viewPager);

        if (titleMatrQues.size() > 1 || !isSingle) {
            viewPagerTab.setVisibility(View.VISIBLE);
        } else {
            viewPagerTab.setVisibility(View.GONE);
        }
    }

    private CorrectDialog correctModeDialog;
    private ImageView ivPersonal;
    private ImageView ivAi;

    /**
     * 显示修改批改方式弹窗
     */
    private void showCorrectModeDialog() {
        if (correctModeDialog == null) {
            correctModeDialog = new CorrectDialog(mActivity, R.layout.chose_correct_way);
            View contentView = correctModeDialog.mContentView;
            ivPersonal = contentView.findViewById(R.id.iv_personal);
            ivAi = contentView.findViewById(R.id.iv_ai);
            TextView tvIntroduce = contentView.findViewById(R.id.tv_introduce);
            TextView tvKnow = contentView.findViewById(R.id.tv_known);
            tvIntroduce.setVisibility(View.GONE);
            tvKnow.setVisibility(View.GONE);
            ivPersonal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    correctModeDialog.dismiss();
                    isShowCheck = false;
                    if (EssayExamDataCache.getInstance().correctMode == 2) {
                        return;
                    }
                    // 取消计时
                    EssayExamTimerHelper.getInstance().onDestroy();
                    tvChangeCheck.setText("人工批改");
                    changeAnswerShow(false);
                    EssayExamDataCache.getInstance().correctMode = 2;
                    hideController();
                    // 保存一下当前答题卡
                    saveAnswer();
                    // 清理数据
                    essayImgMap.clear();
                    curPos = 0;
                    TimeUtils.delayTask(new Runnable() {
                        @Override
                        public void run() {
                            // 切换批改方式，通知材料也重新获取问题
                            EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_CHANGE_CORRECT_TYPE_GET));
                        }
                    }, 400);
                }
            });
            ivAi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    correctModeDialog.dismiss();
                    showChangeAiConfirm();
                }
            });
        }
        if (EssayExamDataCache.getInstance().correctMode == 2) {     // 人工批改
            ivPersonal.setImageResource(R.mipmap.correct_personal_ok);
            ivAi.setImageResource(R.mipmap.correct_ai);
        } else {                                // 智能批改
            ivPersonal.setImageResource(R.mipmap.correct_personal);
            ivAi.setImageResource(R.mipmap.correct_ai_ok);
        }

        correctModeDialog.show();
    }

    private CorrectDialog aiConfirmDialog;

    /**
     * 确认改为智能批改的弹窗
     */
    private void showChangeAiConfirm() {
        if (aiConfirmDialog == null) {
            aiConfirmDialog = new CorrectDialog(mActivity, R.layout.essay_rabbit_dialog);
            View contentView = aiConfirmDialog.mContentView;
            contentView.findViewById(R.id.tv_left).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aiConfirmDialog.dismiss();
                    if (EssayExamDataCache.getInstance().correctMode == 1) {
                        return;
                    }
                    // 取消计时
                    EssayExamTimerHelper.getInstance().onDestroy();
                    tvChangeCheck.setText("智能批改");
                    changeAnswerShow(true);
                    EssayExamDataCache.getInstance().correctMode = 1;
                    showController();
                    // 保存一下当前答题卡
                    saveAnswer();
                    // 答题页切换答题方式，这里把EditText作答内容清除
                    EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_CHANGE_CORRECT_CLEAR_CONTENT));
                    // 清理数据
                    essayImgMap.clear();
                    curPos = 0;
                    TimeUtils.delayTask(new Runnable() {
                        @Override
                        public void run() {
                            // 切换批改方式，通知材料也重新获取问题
                            EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_CHANGE_CORRECT_TYPE_GET));
                        }
                    }, 400);
                }
            });
            contentView.findViewById(R.id.tv_right).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aiConfirmDialog.dismiss();
                }
            });
        }
        aiConfirmDialog.show();
    }

    // 以下是人工批改的某些操作
    // ****************************艹，把校对答案改成了交卷

    /**
     * 点击校对答案，检查每道题是否已经识别成功，每道题显示校对页
     */
    public void checkReadAnswer() {

        // 点击校验的时候刷一下当前的问题内容，保存一下上传的图片信息
        refreshAllAnswerCache();

        boolean isOk = true;

        if (essayImgMap.size() > 0) {
            for (int i = 0; i < essayImgMap.size(); i++) {
                ObArrayList<AnswerImage> answerImages = essayImgMap.get(i);
                int j = 0;
                for (; j < answerImages.size(); j++) {
                    AnswerImage answerImage = answerImages.get(j);
                    if (answerImage.upState != 4) {
                        isOk = false;
                    }
                }
                if (j != answerImages.size()) {
                    break;
                }
            }
        }

        if (isOk) {         // 识别好了
            saveImg();
            clickCommit();
        } else {            // 没识别号，就再次调用压缩识别
            ToastUtils.showEssayToast("图片上传完成才能交卷哦~");
        }
    }

    private ObjectAnimator animationBottomIn;
    private ObjectAnimator animationBottomOut;

    private void showController() {
        if (rlController.getVisibility() == View.VISIBLE) return;
        if (animationBottomIn == null) {
            animationBottomIn = ObjectAnimator.ofFloat(rlController, "translationY", DisplayUtil.dp2px(75), 0);
            animationBottomIn.setDuration(200);
            animationBottomIn.setStartDelay(210);
            animationBottomIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    rlController.setVisibility(View.VISIBLE);
                }
            });
        }
        animationBottomIn.start();
    }

    private void hideController() {
        if (rlController.getVisibility() == View.GONE) return;
        if (animationBottomOut == null) {
            animationBottomOut = ObjectAnimator.ofFloat(rlController, "translationY", 0, DisplayUtil.dp2px(75));
            animationBottomOut.setDuration(200);
            animationBottomOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rlController.setVisibility(View.GONE);
                }
            });
        }
        animationBottomOut.start();
    }

    /**
     * 改变选择图片的个数，修改校对按钮
     */
    public void changeTvProofreadState() {
        if (adapter == null) return;
        boolean canProofread = false;
        for (int i = 0; i < adapter.getCount(); i++) {
            Fragment fragment = adapter.getPage(i);
            if (fragment instanceof EssExEditAnswerContent) {
                EssExEditAnswerContent answerFragment = (EssExEditAnswerContent) fragment;
                ObArrayList<AnswerImage> originImages = answerFragment.getOriginImages();
                if (originImages != null && originImages.size() > 1) {
                    canProofread = true;
                    break;
                }
            }
        }
        for (int i = 0; i < adapter.getCount(); i++) {
            Fragment fragment = adapter.getPage(i);
            if (fragment instanceof EssExEditAnswerContent) {
                EssExEditAnswerContent answerFragment = (EssExEditAnswerContent) fragment;
                answerFragment.changeTvProofreadState(canProofread);
            }
        }
    }

    /**
     * 选中ViewPager
     */
    private void onPageSelectPaper(int position) {
        if (adapter != null) {
            refreshCurrentCache();

            curPos = position;

            // 得到当前正在回答的问题
            if (!isSingle) {
                if (mPaperQuestionDetailBean != null && mPaperQuestionDetailBean.essayQuestions != null && mPaperQuestionDetailBean.essayQuestions.size() > curPos) {
                    mSingleQuestionDetailBean = mPaperQuestionDetailBean.essayQuestions.get(curPos);
                    if (mSingleQuestionDetailBean != null) {
                        limitMaxLength = mSingleQuestionDetailBean.commitWordNumMax;
                        inputLimitMaxLength = mSingleQuestionDetailBean.inputWordNumMax;
                    }
                }
            }

            if (curViewVerify()) return;
            // 关联当前单题也与底部栏
            if (mEssExEditAnswerContent != null) {
                edit_bottom_view.setEditText(mEssExEditAnswerContent.getEditV(), limitMaxLength, inputLimitMaxLength);
            }
//            refreshContentView(mSingleQuestionDetailBean.isExp);
            // 为了记录单题的时间，把此单题放入TimerHelper中
            EssayExamTimerHelper.getInstance().setCurSingleQuestionDetailBean(mSingleQuestionDetailBean);
        }
    }

    /**
     * 把当前页的答案内容取出来，放进mSingleQuestionDetailBean里
     */
    private void refreshCurrentCache() {
        curViewVerify();
        if (mSingleQuestionDetailBean != null) {
            if (mEssExEditAnswerContent != null && mEssExEditAnswerContent.getEditV() != null) {
                String var = mEssExEditAnswerContent.getEditV().getText().toString();
                if (var.length() > 0 && !var.equals(mSingleQuestionDetailBean.content)) {
                    mSingleQuestionDetailBean.content = var;
                }
            }
        }
    }

    /**
     * 刷新全部问题内容
     */
    private void refreshAllAnswerCache() {
        if (adapter != null && adapter.getCount() > 0) {
            essayImgMap.clear();
            for (int i = 0; i < adapter.getCount(); i++) {
                Fragment fragment = adapter.getPage(i);
                if (fragment instanceof EssExEditAnswerContent) {
                    EssExEditAnswerContent answerFragment = (EssExEditAnswerContent) fragment;
                    // 取编辑框的内容
                    String editAnswer = answerFragment.getEditV().getText().toString();
                    if (isSingle) {
                        if (mSingleQuestionDetailBean != null) {
                            mSingleQuestionDetailBean.content = editAnswer;
                        }
                    } else {
                        if (mPaperQuestionDetailBean != null && mPaperQuestionDetailBean.essayQuestions != null && mPaperQuestionDetailBean.essayQuestions.size() > i) {
                            SingleQuestionDetailBean singleQuestionDetailBean = mPaperQuestionDetailBean.essayQuestions.get(i);
                            if (singleQuestionDetailBean != null) {
                                singleQuestionDetailBean.content = editAnswer;
                            }
                        }
                    }
                    // 取图片内容
                    ObArrayList<AnswerImage> originImages = answerFragment.getOriginImages();
                    if (originImages != null) {
                        essayImgMap.put(i, originImages);
                    }
                }
            }
        }
    }

    @Override
    public void onSoftInputChange(boolean show, int layoutHeight, int contentHeight) {
        if (show) {
            TimeUtils.delayTask(new Runnable() {
                @Override
                public void run() {
                    rightButton.resetPosition();
                }
            }, 1);
            edit_bottom_view.onViewClicked(0);
            edit_bottom_view.onStopRecognizer();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10010 && resultCode == 100861) {
            photoData = data.getStringExtra("mPhotoData");
            fileName = data.getStringExtra("FILENAME");
            if (mSingleQuestionDetailBean != null) {
                mSingleQuestionDetailBean.fileName = fileName;
            }
            if (photoData != null) {
                String str = EssayExamImpl.filterPhotoResultString(photoData);
                if (!curViewVerify() && mEssExEditAnswerContent != null) {
                    mEssExEditAnswerContent.appendInputAnsStr(str);
                }
            }
        }
    }

    private void stopRec() {
        edit_bottom_view.onStopRecognizer();
        mSoftInputLayout.hideEmojiLayout();
        edit_bottom_view.onViewClicked(3);

        if (mEssExEditAnswerContent != null) {
            mEssExEditAnswerContent.clearView();
        }
    }

    private void saveAnswer() {
        refreshAllAnswerCache();
        saveImg();
        if (mEssayExamImpl != null && answerCardId != 0) {
            mEssayExamImpl.paperCommit(requestType, true, 0, isSingle, 0, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
        }
    }

    // 保存一下图片信息
    public void saveImg() {

        // 智能批改不需要保存图片
        if (isSingle) {
            if (mSingleQuestionDetailBean.correctMode == 1 || answerCardId == 0) {
                return;
            }
        } else {
            if (mPaperQuestionDetailBean.essayPaper.correctMode == 1 || answerCardId == 0) {
                return;
            }
        }

        ArrayList<EssayAnswerImageSortBean> answerImageList = new ArrayList<>();

        if (essayImgMap.size() > 0) {
            // 遍历所有缓存的问题
            for (int i = 0; i < essayImgMap.size(); i++) {
                ArrayList<AnswerImage> answerImages = essayImgMap.get(i);
                if (answerImages != null && answerImages.size() > 0) {
                    EssayAnswerImageSortBean bean = new EssayAnswerImageSortBean();
                    bean.imageList = new ArrayList<>();
                    // 遍历每一个问题的图片，最后一张是那个加号
                    for (int j = 0; j < answerImages.size() - 1; j++) {
                        AnswerImage originImage = answerImages.get(j);
                        EssayAnswerImageSortBean.ImageSort imgSort = new EssayAnswerImageSortBean.ImageSort();
                        imgSort.imageId = originImage.id;
                        imgSort.sort = originImage.sort;
                        bean.imageList.add(imgSort);
                    }
                    if (isSingle) {
                        bean.answerId = mSingleQuestionDetailBean.answerCardId;
                    } else {
                        bean.answerId = mPaperQuestionDetailBean.essayQuestions.get(i).answerCardId;
                    }
                    answerImageList.add(bean);
                }
            }
            ServiceProvider.changePictureSort(compositeSubscription, answerImageList, new NetResponse());
        }
    }

    /**
     * 点击交卷
     */
    private void onClickCommit() {
        stopRec();
        refreshAllAnswerCache();
        commitVerify();
    }

    /**
     * 交卷
     */
    public void commitVerify() {

        if ((!isEssaySc()) && answerCardId <= 0) {
            createAnswerId();
            return;
        }
        boolean isPersonCorrect = EssayExamDataCache.getInstance().correctMode == 2;
        if (isSingle) {
            DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickCommit2();
                }
            }, "确认交卷？", isPersonCorrect ? "(请检查作答图片是否已全部上传)" : "(请注意调整答案格式，如首行缩进、分条、分段等)", "取消", "确认");
        } else {
            if (mPaperQuestionDetailBean != null && mPaperQuestionDetailBean.essayQuestions != null && mPaperQuestionDetailBean.essayPaper != null) {
                int unDoneCount = 0;
                SpannableStringBuilder builder = new SpannableStringBuilder();
                String tip = "(请注意调整答案格式，如首行缩进、分条、分段等)";

                if (mEssayExamImpl != null) {
                    if (EssayExamDataCache.getInstance().correctMode == 2) {      // 人工计算未添加图片
                        // tip 人工批改对作答图片进行批改，你还有x题未上传作答图片，可能影响批改结果，确认交卷？
                        int i = 0;
                        for (int j = 0; j < essayImgMap.size(); j++) {
                            ObArrayList<AnswerImage> answerImages = essayImgMap.get(j);
                            // 最后一张是那个加号
                            if (answerImages != null && answerImages.size() > 1) {
                                i++;
                            }
                        }
                        unDoneCount = mPaperQuestionDetailBean.essayQuestions.size() - i;
                        builder.append("人工批改对作答图片进行批改，你还有").append(String.valueOf(unDoneCount));
                        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.red120)), 17, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        builder.append("题未上传作答图片，可能影响批改结果，确认交卷？");
                        tip = "";
                    } else {                                    // 智能计算未做题
                        unDoneCount = EssayExamImpl.getUnfinishedCount(isSingle, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
                        builder.append("还有").append(String.valueOf(unDoneCount));
                        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.red120)), 2, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        builder.append("题未作答，确认交卷？");
                        tip = "(请注意调整答案格式，如首行缩进、分条、分段等)";
                    }
                }
                if (unDoneCount != 0) {
                    DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickCommit2();
                        }
                    }, null, builder, null, tip, "取消", "确认");
                } else {
                    DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickCommit2();
                        }
                    }, "确认交卷？", isPersonCorrect ? "(请检查作答图片是否已全部上传)" : "(请注意调整答案格式，如首行缩进、分条、分段等)", "取消", "确认");
                }
            }
        }

        isClickSubmit = false;
    }

    private void onClickCommit2() {
        if (isEssaySc()) {                                           // 模考走模考的交卷接口，不需要检查批改次数
            if (mEssayExamImpl != null) {
                mEssayExamImpl.paperCommit(requestType, false, 1, isSingle, 0, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
            }
        } else {                                                            // 其他都需要检查批改次数

            int questionType;   // 问题Type 0、套题
            long id;            // questionId | paperId
            if (isSingle) {
                questionType = extraArgs.getInt("questionType");
                id = questionBaseId;
            } else {
                questionType = 0;
                id = paperId;
            }

            if (EssayExamDataCache.getInstance().correctMode == 2) {

                // 检查老师工作是否饱和 & 批改次数，并交卷
                mActivity.showProgress();

                ServiceProvider.getTeacherBusyState(compositeSubscription, answerCardId, 2, questionType, id, new NetResponse() {
                    @Override
                    public void onError(Throwable e) {
                        mActivity.hideProgress();
                        if (e instanceof ApiException) {
                            ToastUtils.showEssayToast(((ApiException) e).getErrorMsg());
                        } else {
                            ToastUtils.showEssayToast("获取数据失败，请重试");
                        }
                    }

                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        mActivity.hideProgress();
                        if (model.data != null) {
                            TeacherBusyStateInfo teacherState = (TeacherBusyStateInfo) model.data;
                            teacherState.manual = teacherState.correct;     // 人工批改次数
                            if (teacherState.canCorrect) {  // 能批改 -> 老师工作不饱和，去检查批改次数
                                if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {             // 课后作业不需要检查批改次数，是人工答题，需要查看人工是否饱和
                                    mEssayExamImpl.paperCommit(requestType, false, 1, isSingle, 0, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
                                } else if (EssayRoute.checkCount(2, teacherState)) {        // 检查批改次数，足够，直接交卷
                                    mEssayExamImpl.paperCommit(requestType, false, 1, isSingle, 0, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
                                } else {                                                                // 批改次数不足，购买弹窗
                                    mEssayViewHelper.showBuyDialog(mActivity, new EssayViewHelper.OnHelperCallBack() {
                                        @Override
                                        public boolean doSomething(Object isOpen) {
                                            // 保存答案
                                            saveAnswer();
                                            return false;
                                        }
                                    });
                                }
                            } else {                        // 不能批改 -> 老师工作饱和，弹窗
                                showTeacherBusyDialog(teacherState);
                            }
                        } else {
                            ToastUtils.showEssayToast("获取数据失败，请重试");
                        }
                    }
                });
            } else {        // 智能批改，去检查批改次数
                EssayRoute.checkCanCorrectNet(mActivity, compositeSubscription, 1, questionType, id,
                        new Runnable() {
                            @Override
                            public void run() {
                                mEssayExamImpl.paperCommit(requestType, false, 1, isSingle, 0, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
                            }
                        }, 1, "取消");
            }
        }
    }


    /**
     * 老师工作饱和弹窗
     */
    private void showTeacherBusyDialog(final TeacherBusyStateInfo teacherState) {
        final CorrectDialog teacherBusyDialog = new CorrectDialog(mActivity, R.layout.essay_rabbit_dialog);
        View contentView = teacherBusyDialog.mContentView;

        ImageView ivRabbit = contentView.findViewById(R.id.iv_rabbit);
        TextView tvTips = contentView.findViewById(R.id.tv_tips);
        TextView tvLeft = contentView.findViewById(R.id.tv_left);
        TextView tvRight = contentView.findViewById(R.id.tv_right);

        ivRabbit.setImageResource(R.mipmap.rabbit_sad);
        tvTips.setText(teacherState.correctDesc);
        tvLeft.setText("我再想想");
        tvRight.setText("确认批改");

        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherBusyDialog.dismiss();
            }
        });
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherBusyDialog.dismiss();
                if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {             // 课后作业不需要检查批改次数，是人工答题，需要查看人工是否饱和
                    mEssayExamImpl.paperCommit(requestType, false, 1, isSingle, 1, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
                } else if (EssayRoute.checkCount(2, teacherState)) {        // 检查批改次数，足够
                    mEssayExamImpl.paperCommit(requestType, false, 1, isSingle, 1, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
                } else {                                                                // 批改次数不足，弹窗
                    mEssayViewHelper.showBuyDialog(mActivity, new EssayViewHelper.OnHelperCallBack() {
                        @Override
                        public boolean doSomething(Object isOpen) {
                            // 保存答案
                            saveAnswer();
                            return false;
                        }
                    });
                }
            }
        });
        teacherBusyDialog.show();
    }

    // 获取当前编辑页EditAnswerContent
    private boolean curViewVerify() {
        if (adapter == null || mSingleQuestionDetailBean == null) {
            return true;
        }
        mEssExEditAnswerContent = (EssExEditAnswerContent) adapter.getPage(curPos);
        return false;
    }

    private void hideSoft() {
        if (mEssExEditAnswerContent != null) {
            mEssExEditAnswerContent.showHideSoftInput(EssayExamMessageEvent.EssayExam_hide_soft);
        }
    }

    // 是否是申论模考
    private boolean isEssaySc() {
        return requestType == EssayExamActivity.ESSAY_EXAM_SC;
    }

    // 是否答题了，拥有显示交卷按钮状态
    public boolean isAlreadyAns() {
        refreshAllAnswerCache();
        int unfinishedCount = EssayExamImpl.getUnfinishedCount(isSingle, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
        return isSingle ? unfinishedCount < 1 : unfinishedCount < mPaperQuestionDetailBean.essayQuestions.size();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 不是模考，就暂停
        if (!isEssaySc()) {
            EssayExamTimerHelper.getInstance().pauseExamTime();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isEssaySc()) {
            EssayExamTimerHelper.getInstance().resumeExamTime(requestType, isSingle, mSingleQuestionDetailBean, mPaperQuestionDetailBean);
            startTimer();
        }
    }

    @Override
    public boolean onBackPressed() {
        // 如果显示着材料卡，就只隐藏材料卡
        if (materialsCardView.isShowMaterialCard()) {
            materialsCardView.hide();
            return true;
        }
        if (isSoftShowing() && ivBack != null) {
            hideKeyboard(ivBack);
            return true;
        }
        if (mSoftInputLayout.handleBack()) {
            return true;
        }
        // 现在正在显示校对答案页，就提示并返回上传图片页
        if (EssayExamDataCache.getInstance().correctMode == 2 && isShowCheck) {      // 人工批改并且当前显示的是校对页
            isShowCheck = false;
            hideController();
            changeAnswerShow(false);
            return true;
        }
        stopRec();
        hideSoft();
        refreshAllAnswerCache();
        saveImg();
        EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_start_EssayExamMaterials1));
        return true;
    }

    /**
     * 修改编辑页显示情况
     *
     * @param showEdit true、显示编辑框 false、显示选择图片
     */
    private void changeAnswerShow(boolean showEdit) {
        for (int i = 0; i < adapter.getCount(); i++) {
            Fragment fragment = adapter.getPage(i);
            if (fragment instanceof EssExEditAnswerContent) {
                EssExEditAnswerContent answerFragment = (EssExEditAnswerContent) fragment;
                if (showEdit) {
                    answerFragment.showEdit();
                } else {
                    answerFragment.hideEdit();
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (rightButton != null) {
            rightButton.resetPosition();
        }
    }

    @Override
    protected void onSaveState(Bundle outState) {
        outState.putInt("curPos", curPos);
    }


    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        curPos = savedInstanceState.getInt("curPos");
        onLoadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (softInputLayoutListener != null && mSoftInputLayout != null) {
            mSoftInputLayout.getViewTreeObserver().removeOnGlobalLayoutListener(softInputLayoutListener);
        }
    }

    private boolean isSoftShowing() {
        if (mActivity == null) {
            return false;
        }
        //获取当屏幕内容的高度
        int screenHeight = mActivity.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        //DecorView即为activity的顶级view
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom;
    }

    private void hideKeyboard(View v) {
        // 隐藏键盘
        InputMethodManager immX = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        immX.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (edit_bottom_view != null) {
            edit_bottom_view.onStopRecognizer();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }


    public static EssayExamEditAnswer newInstance(Bundle extra) {
        EssayExamEditAnswer fragment = new EssayExamEditAnswer();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }
}
