package com.huatu.handheld_huatu.business.essay.examfragment;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.essay.bhelper.DlEssayDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.DownLoadEssayHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamTimerHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayViewHelper;
import com.huatu.handheld_huatu.business.essay.cusview.CorrectDialog;
import com.huatu.handheld_huatu.business.essay.cusview.MaterialsOperatorView;
import com.huatu.handheld_huatu.business.essay.cusview.ProvPaperLView;
import com.huatu.handheld_huatu.business.essay.cusview.QuestionDragViewLayout;
import com.huatu.handheld_huatu.business.essay.cusview.RightOperatorTextView;
import com.huatu.handheld_huatu.business.essay.cusview.TextSizeControlImageView;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.essay.DownloadEssayBean;
import com.huatu.handheld_huatu.mvpmodel.essay.ExamMaterialListBean;
import com.huatu.handheld_huatu.mvpmodel.essay.MaterialsFileUrlBean;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleAreaListBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 材料页
 * 使用的 MV-P 结构
 * Presenter有：
 * EssayExamImpl
 * EssayCheckImpl
 */
public class EssayExamMaterials extends BaseFragment {

    private String titleView;                           // ActionBar标题
    private boolean isSingle;                           // 是否是单题
    private boolean isFromArgue = false;                // 是否从文章写作来
    private boolean isFromCollection;                   // 是否是从收藏跳过来
    private RelativeLayout actionBar;                   // 新的ActionBar
    private ImageView ivBack;                           // 返回键，字体大小键
    private TextView tvTitle;                           // 本页标题
    private TextSizeControlImageView ivTextSize;        // 字体控制
    private CommonErrorView errorView;                  // 获取数据失败页面

    private boolean titleIsSingleLine = true;           // 标题是否是单行显示

    private EssayExamActivity parentActivity;           // 父Activity

    private FrameLayout flMaterialsContent;             // 添加材料Fragment内容的位置
    private QuestionDragViewLayout flQuestionContent;   // 添加问题Fragment内容的位置

    private View dragView;                              // 拖拽的View

    private EssayMaterialsFragment materialsFragment;   // 材料Fragment
    private EssayQuestionFragment questionFragment;     // 问题Fragment

    private Bundle extraArgs;                                               // 上层穿过来的Bundle参数
    private int selPos;                                                     // 地区的position

    private boolean showNotAgain;
    private ProvPaperLView province_view;                                   // 选择地区的隐藏View
    private RightOperatorTextView rightBtn;                                 // 右侧可拖动的切换页面按钮
    private MaterialsOperatorView bottom_operator_llayout;                  // 底部的 收藏按钮 自定义View

    private EssayExamImpl mEssayExamImpl;
    private EssayViewHelper mEssayViewHelper;                               // 弹窗的Helper

    private ArrayList<ExamMaterialListBean> cacheSingleMaterialListBeans;   // 单题材料
    private SingleQuestionDetailBean cacheSingleQuestionDetailBean;         // 单题问题对象
    private SingleAreaListBean cacheSingleAreaListBean;                     // 地区对象

    public ArrayList<ExamMaterialListBean> cachePaperMaterialListBeans;     // 套题材料集合
    public PaperQuestionDetailBean cachePaperQuestionDetailBean;            // 多题问题对象
    public MaterialsFileUrlBean cacheMaterialsFileUrlBean;                  // 获取的试题下载路径等信息，为了下载试题

    private long questionBaseId;                // 当前这道题的Id，一个单体组，会有不同地区属性，每个地区的这道题的id
    private long similarId;                     // 根据这个id获取次单题对应的地区信息list。单体组id

    private int areaId;                         // 那个地区的套题，地区Id
    private long paperId;                       // 套题试卷Id

    private long answerId;                      // 答题卡Id，人工批改，退回修改答案需要去请求响应的答题卡，进行修改
    private int bizStatus;                      // 答题卡状态，人工批改，退回修改答案需要去请求相应的答题卡，进行修改

    private long showMaterialId;                // 默认显示第几个材料
    private long showQuestionId;                // 默认显示第几个材料

    public String areaName;                     // 地区名称

    private int mPhoto = -1;
    private String mPhotoMsg;
    private int screenHeight;

    private boolean isThisDown = false;
    private CorrectDialog correctModeDialog;    // 选择批改方式
    private boolean needGoAnswer = false;       // 选择了批改方式，如果当前问题&答题卡不是当前批改方式，就需要重新获取，获取完成之后，直接跳转答题卡

    // 材料页 让问题取消选择
//     EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_QUESTION_CONTENT_CLEAR_VIEW));
    // 材料页 让材料取消选择
//     EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_MATERIAL_CONTENT_CLEAR_VIEW));

    /**
     * 单题：
     * 先根据similarId获取单题所属地区列表并显示
     * 返回之后
     * 根据questionBaseId获取单题材料列表并显示
     * 返回只有
     * 根据questionBaseId获取单题问题并显示
     * <p>
     * 切换地区之后，显示地区信息
     * 获取目标地区的questionBaseId，依次获取材料、问题并显示
     * <p>
     * 套题：
     * 顺序获取套题材料和套题问题并显示
     */
    @Override
    public int onSetRootViewId() {
        return R.layout.essay_exam_materials_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null || mEssayExamImpl == null || rootView == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.EssayExam_time_heartbeat) {                         // 每秒计时
            setTime();
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_getSingleAreaListDetail) {     // 根据单题组Id获取它所属的地区列表

            if (mEssayExamImpl.cacheSingleAreaListBeans == null) {
                return true;
            }
            for (int i = 0; i < mEssayExamImpl.cacheSingleAreaListBeans.size(); i++) {
                SingleAreaListBean singleAreaListBean = mEssayExamImpl.cacheSingleAreaListBeans.get(i);
                if (singleAreaListBean.questionBaseId == questionBaseId) {
                    selPos = i;
                    cacheSingleAreaListBean = singleAreaListBean;
                    areaName = cacheSingleAreaListBean.areaName;
                    break;
                }
            }
            refreshAreaTitle(mEssayExamImpl.cacheSingleAreaListBeans);

        } else if (event.type == EssayExamMessageEvent.EssayExam_net_getSingleDataSuccess) {        // 获取到 单材料 和 问题详情

            // 获取数据成功，隐藏错误View
            errorView.setVisibility(View.GONE);

            // 设置材料列表显示
            cacheSingleMaterialListBeans = mEssayExamImpl.cacheSingleMaterialListBeans;
            refreshMaterialViewPager(cacheSingleMaterialListBeans);

            // 获取到单题问题内容，显示
            cacheSingleQuestionDetailBean = mEssayExamImpl.cacheSingleQuestionDetailBean;
            if (cacheSingleQuestionDetailBean != null) {
                isFromArgue = cacheSingleQuestionDetailBean.questionType == 5;      // 是否是文章写作
            }
            if (parentActivity != null) {
                parentActivity.setIsArgue(isFromArgue);
            }
            addQuestionViewData(cacheSingleQuestionDetailBean, null);
            if (!isEssaySc() && StringUtils.isEmpty(titleView)) {
                titleView = cacheSingleQuestionDetailBean.stem;
                tvTitle.setText(cacheSingleQuestionDetailBean.stem);
            }

            // 切换答题方式/切换地区 成功，通知答题页，如果没有答题页，发送Event也没问题
            EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_CHANGE_CORRECT_TYPE_GETED));
            refreshDownloadBtn();

            // 选择了批改方式，如果当前问题&答题卡不是当前批改方式，就需要重新获取，获取完成之后，直接跳转答题卡
            if (needGoAnswer) {
                needGoAnswer = false;
                goAnswer(EssayExamDataCache.getInstance().correctMode);
            }

        } else if (event.type == EssayExamMessageEvent.EssayExam_net_getPaperDataSuccess) {         // 获取套题 材料 和 问题

            // 获取数据成功，隐藏错误View
            errorView.setVisibility(View.GONE);

            // 套题材料
            cachePaperMaterialListBeans = mEssayExamImpl.cachePaperMaterialListBeans;
            refreshMaterialViewPager(cachePaperMaterialListBeans);

            // 套题问题
            cachePaperQuestionDetailBean = mEssayExamImpl.cachePaperQuestionDetailBean;
            addQuestionViewData(null, cachePaperQuestionDetailBean);
            if (!isEssaySc() && StringUtils.isEmpty(titleView)) {
                tvTitle.setText(cachePaperQuestionDetailBean.essayPaper.paperName);
            }
            startTimer();
            // 切换答题方式/切换地区 成功，通知答题页，如果没有答题页，发送Event也没问题
            EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_CHANGE_CORRECT_TYPE_GETED));
            refreshDownloadBtn();

            // 选择了批改方式，如果当前问题&答题卡不是当前批改方式，就需要重新获取，获取完成之后，直接跳转答题卡
            if (needGoAnswer) {
                needGoAnswer = false;
                goAnswer(EssayExamDataCache.getInstance().correctMode);
            }
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_getDataFailed) {               // 获取数据失败
            showError(1);

        } else if (event.type == EssayExamMessageEvent.EssayExam_CHANGE_CORRECT_TYPE_GET) {         // 答题页切换答题方式，这里获取新的答题卡
            // 这里是为了人工批改被退回的时候，切换答题方式，需要把 答题卡id 和 批改状态 初始化
            if (EssayExamDataCache.getInstance().correctMode == 1) {          // 智能清空
                answerId = 0;
                bizStatus = 0;
            } else {                                        // 人工可能是修改
                if (extraArgs != null) {
                    answerId = extraArgs.getLong("answerId", 0);
                    bizStatus = extraArgs.getInt("bizStatus", 0);
                }
            }
            getMaterialQuestionData();
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_getMaterialsDownloadUrl) {     // 获取下载路径
            cacheMaterialsFileUrlBean = mEssayExamImpl.cacheMaterialsFileUrlBean;
            downloadMaterPaper(cacheMaterialsFileUrlBean);
        } else if (event.type == EssayExamMessageEvent.EssayExam_net_download_essay_success) {      // 下载完成
            if (isThisDown) {
                isThisDown = false;
                refreshDownloadBtn();
                ToastUtils.showEssayToast("下载完成");
            }
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_essExMaterialsContent_setTextSize) {       // 修改字体大小
            ivTextSize.initStyle();
        }

        if (bottom_operator_llayout != null) {
            bottom_operator_llayout.onEventUpdate(event);
        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImpl(savedInstanceState);
        screenHeight = DisplayUtil.getScreenHeight();
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        parentActivity = (EssayExamActivity) mActivity;

        rightBtn = rootView.findViewById(R.id.right_btn);

        rightBtn.resetPosition();

        province_view = rootView.findViewById(R.id.province_view);
        flMaterialsContent = rootView.findViewById(R.id.ess_ex_materials_content_fl);
        flQuestionContent = rootView.findViewById(R.id.ess_ex_question_content_fl);
        dragView = rootView.findViewById(R.id.drag_view);

        flQuestionContent.setChangeListener(new QuestionDragViewLayout.ChangeChildListener() {
            @Override
            public void changeChild(int dy) {
                setQuestionCardHeight(dy);
            }
        });

        dragView.setOnTouchListener(new MaterialTouch());

        initData();
        initTitleBar();
        initRightButton();

        initMaterialsFragment();
        initQuestionFragment();
        setBottomView();
        refreshSingleAreaSelView();
        mPhoto = SpUtils.getUpdatePhotoAnswer();
        mPhotoMsg = SpUtils.getUpdatePhotoAnswerMsg();
        if (mPhoto == 0 && !TextUtils.isEmpty(mPhotoMsg)) {
            showTipDialog();
        }
    }

    @Override
    protected void onLoadData() {
        if (!NetUtil.isConnected()) {
            ToastUtil.showToast("无网络连接");
            showError(0);
            return;
        }
        errorView.setVisibility(View.GONE);
        if (mEssayExamImpl != null) {
            if (isSingle && requestType != EssayExamActivity.ESSAY_EXAM_HOMEWORK) {     // 课后作业不需要地区
                getSingleAreaListDetail(similarId);                     // 获取区域位置信息
            }
            getMaterialQuestionData();
        }
    }

    /**
     * 获取材料和问题内容
     */
    private void getMaterialQuestionData() {
        if (!NetUtil.isConnected()) {
            ToastUtil.showToast("无网络连接");
            showError(0);
            return;
        }
        errorView.setVisibility(View.GONE);
        // 请求的类型，默认传1、表示非课后作业 2、表示课后作业
        int modeType = requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK ? 2 : 1;
        if (isSingle) {
            getSingleData(questionBaseId, EssayExamDataCache.getInstance().correctMode, modeType, answerId, bizStatus);   // 获取单题 问题（或许包含答题卡） & 材料列表
        } else {
            if (isEssaySc()) {                              // 是否是模考
                getMockPaperData(paperId);
            } else {
                getPaperData(paperId, EssayExamDataCache.getInstance().correctMode, modeType, answerId, bizStatus);                 // 获取套题 问题 & 材料列表 correctMode 1、智能批改 2、人工批改
            }
        }
    }

    /**
     * 初始化ActionBar
     */
    private void initTitleBar() {

        actionBar = rootView.findViewById(R.id.action_bar);
        ivBack = rootView.findViewById(R.id.iv_back);
        tvTitle = rootView.findViewById(R.id.tv_title);
        ivTextSize = rootView.findViewById(R.id.iv_text_size);
        errorView = rootView.findViewById(R.id.error_view);

        ivTextSize.setType(0, "资料页");          // 设置黑字图片

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadData();
            }
        });

        if (!isEssaySc()) {
            tvTitle.setText(titleView);
        }

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleIsSingleLine) {
                    titleIsSingleLine = false;
                    // 点击标题，如果标题有多行，显示多行
                    tvTitle.setSingleLine(false);
                } else {
                    titleIsSingleLine = true;
                    // 点击标题，如果标题有多行，显示多行
                    tvTitle.setSingleLine(true);
                }

            }
        });
    }

    /**
     * 获取前页面传过来的数据，
     */
    private void initData() {

        if (args != null) {
            requestType = args.getInt("request_type");                                  // 请求类型
            extraArgs = args.getBundle("extra_args");                                        // Bundle数据
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            titleView = extraArgs.getString("titleView");                               // title

            // 单题过来
            isSingle = extraArgs.getBoolean("isSingle");                                // 是否是单题
            questionBaseId = extraArgs.getLong("questionBaseId");                       // 当前这道题的Id，一个单体组，会有不同地区属性，每个地区的这道题的id
            similarId = extraArgs.getLong("similarId");                                 // 根据这个id获取次单题对应的地区信息list。单体组id

            // 套题过来
            paperId = extraArgs.getLong("paperId");                                     // 套题Id
            areaId = extraArgs.getInt("areaId");                                        // 那个地区的套题，地区Id

            isFromCollection = extraArgs.getBoolean("isFromCollection");                // 是否是从收藏过来

            areaName = extraArgs.getString("areaName");                                 // 地区名称(收藏，搜索会传这个字段)

            answerId = extraArgs.getLong("answerId", 0);                     // 答题卡Id（人工批改被退回，再请求答题卡，需要答题卡Id）
            bizStatus = extraArgs.getInt("bizStatus", 0);                    // 答题卡状态（人工批改被退回，再请求答题卡，需要状态）

            showMaterialId = extraArgs.getLong("showMaterialId", 0);         // 从搜索跳过来，可能要默认显示哪个材料
            showQuestionId = extraArgs.getLong("showQuestionId", 0);         // 从搜索跳过来，可能要默认显示哪个材料
        }
    }

    // 初始化材料Fragment
    private void initMaterialsFragment() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag("materialsFragment");
        if (fragment != null) {
            materialsFragment = (EssayMaterialsFragment) fragment;
        } else {
            materialsFragment = new EssayMaterialsFragment();
            materialsFragment.setArguments(args);
            transaction.add(R.id.ess_ex_materials_content_fl, materialsFragment, "materialsFragment");
            transaction.commit();
        }

        materialsFragment.setProvinceChoiceListener(new EssayMaterialsFragment.ProvinceChoiceListener() {
            @Override
            public void onClickProvince() {
                if (mEssayExamImpl != null) {
                    // 显示选择省
                    refreshOnclickAreaTitle(mEssayExamImpl.cacheSingleAreaListBeans);
                }
            }

            @Override
            public void setProvinceVisibility(int visibility) {
                if (province_view != null) {
                    province_view.setVisibility(visibility);
                }
            }
        });
    }

    /**
     * 显示材料数据
     */
    public void refreshMaterialViewPager(List<ExamMaterialListBean> cacheExamMaterialListBean) {
        materialsFragment.setData(cacheExamMaterialListBean, showMaterialId);
    }

    // 初始化问题Fragment
    private void initQuestionFragment() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag("questionFragment");
        if (fragment != null) {
            questionFragment = (EssayQuestionFragment) fragment;
        } else {
            questionFragment = EssayQuestionFragment.newInstance();
            transaction.add(R.id.ess_ex_question_content_fl, questionFragment, "questionFragment");
            transaction.commit();
        }

    }

    /**
     * 显示问题数据
     */
    private void addQuestionViewData(SingleQuestionDetailBean cacheSingleQuestionDetailBean, PaperQuestionDetailBean cachePaperQuestionDetailBean) {
        questionFragment.setData(isSingle, cacheSingleQuestionDetailBean, cachePaperQuestionDetailBean, showQuestionId);
    }

    public boolean isCollect;

    /**
     * 初始化底部 收藏、下载资料栏
     */
    private void setBottomView() {
        if (rootView != null) {
            if (bottom_operator_llayout == null) {
                bottom_operator_llayout = rootView.findViewById(R.id.bottom_operator_llayout);
            }
            bottom_operator_llayout.setData(isCollect, isSingle, similarId, questionBaseId, paperId, mEssayExamImpl, requestType, new MaterialsOperatorView.OnCusViewCallBack() {
                @Override
                public boolean isCollect(boolean iscollect) {
                    isCollect = iscollect;
                    return false;
                }
            });
            bottom_operator_llayout.setParentFragment(this);
        }
    }

    /**
     * 刷新区域显示
     */
    private void refreshSingleAreaSelView() {
        if (materialsFragment != null) {
            materialsFragment.setSingleArea(areaName, isSingle, false, isFromCollection);
        }
    }

    /**
     * 初始化Presenter
     */
    private void initImpl(@Nullable Bundle savedInstanceState) {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        if (savedInstanceState != null && EssayExamDataCache.getInstance().mEssayExamImpl_m != null) {
            mEssayExamImpl = EssayExamDataCache.getInstance().mEssayExamImpl_m;
            EssayExamDataCache.getInstance().mEssayExamImpl_m = null;
            mEssayExamImpl.setCompositeSubscription(compositeSubscription);
        } else {
            mEssayExamImpl = new EssayExamImpl(compositeSubscription);
        }
        mEssayViewHelper = new EssayViewHelper();
    }

    /**
     * 底部 字体、收藏 条的隐现动画
     */
    public void startAnim(boolean open) {
//        if (bottom_operator_llayout != null) {
//            bottom_operator_llayout.startAnim(open);
//        }
    }

    // 拍照答题是测试阶段的提示
    private void showTipDialog() {
        showNotAgain = SpUtils.getEssayMaterialShow();
//        if (!showNotAgain && !isStartToCheckDetail) {
        if (!showNotAgain) {
            if (mEssayViewHelper != null) {
                mEssayViewHelper.showDialog_m1(mActivity, mPhotoMsg, new EssayViewHelper.OnHelperCallBack() {
                    @Override
                    public boolean doSomething(Object isOpen) {
                        showNotAgain = true;
                        return false;
                    }
                });
            }
        }
    }

    /**
     * 刷新当前题目pdf是否已经下载到本地
     */
    public String refreshDownloadBtn() {
        DlEssayDataCache essayDataCache = DlEssayDataCache.getInstance();
        essayDataCache.readDownloadFilePathFromFile();
        String filePath = essayDataCache.fileCachePath(1, isSingle, false, answerId, cacheSingleQuestionDetailBean, cachePaperQuestionDetailBean, null);
        String size = "";
        if (filePath != null) {
            try {
                size = FileUtil.getFileSize(filePath);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (filePath != null && !"".equals(size) && FileUtil.isFileExist(filePath)) {   // 已经下载
            bottom_operator_llayout.showDownloadButtonImage(true);

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

        } else {                                                                        // 未下载
            bottom_operator_llayout.showDownloadButtonImage(false);
        }
        return filePath;
    }

    /**
     * 点击下载按钮，打开pdf 或 获取下载地址
     */
    public void downLoadPdf() {
        isThisDown = true;
        // 下载pdf相关
        String filePath = refreshDownloadBtn();
        if (filePath != null && FileUtil.isFileExist(filePath)) {   // 如果下载了，就去打开pdf，否则，就去下载
            FileUtil.readPDF(mActivity, filePath);
            return;
        }

        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        }

        if (mEssayExamImpl != null) {
            if (isSingle) {     // 单题下载
                if (cacheSingleQuestionDetailBean != null) {
                    mEssayExamImpl.getMaterialsDownloadUrl(0, 0, 0, cacheSingleQuestionDetailBean.questionBaseId);
                } else {
                    CommonUtils.showToast("试卷信息错误，无法下载");
                }
            } else {            // 套题下载
                if (cachePaperQuestionDetailBean != null) {
                    if (cachePaperQuestionDetailBean.essayPaper != null) {
                        //套题下载，传paperId,第二个
                        mEssayExamImpl.getMaterialsDownloadUrl(0, cachePaperQuestionDetailBean.essayPaper.paperId, 0, 0);
                    } else {
                        CommonUtils.showToast("试卷信息错误，无法下载");
                    }
                } else {
                    CommonUtils.showToast("试卷信息错误，无法下载");
                }
            }
        }
    }

    private CorrectDialog homeworkDownloadTip;
    private CheckBox checkBox;

    // 开始下载
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
                    if (!StringUtils.isEmpty(areaName)) {
                        downloadEssayBean.title = titleView + "（" + areaName + "）";
                    } else {
                        downloadEssayBean.title = titleView;
                    }
                    downloadEssayBean.downLoadId = cacheSingleQuestionDetailBean.questionBaseId;
                } else {
                    downloadEssayBean.downtype = 1;
                    downloadEssayBean.downLoadId = cachePaperQuestionDetailBean.essayPaper.paperId;
                    String var2 = EssayExamDataCache.getInstance().titleArea;
                    if (!StringUtils.isEmpty(areaName)) {
                        downloadEssayBean.title = titleView + "（" + areaName + "）";
                    } else if (!StringUtils.isEmpty(var2)) {
                        downloadEssayBean.title = titleView + "（" + var2 + "）";
                    } else {
                        downloadEssayBean.title = titleView;
                    }
                }
                downloadEssayBean.fileSize = cacheMaterialsFileUrlBean.fileSize;
                downloadEssayBean.time = DateUtils.getCurrentTime2();
                downloadEssayBean.isSingle = isSingle;
                downloadEssayBean.isStartToCheckDetail = false;
                downloadEssayBean.check = 0;
                downloadEssayBean.filepath = DownLoadEssayHelper.getInstance().getDownloadFilePath(downloadEssayBean);
                if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {     // 课后作业
                    downloadEssayBean.type = 1;
                }

                DlEssayDataCache var2 = DlEssayDataCache.getInstance();
                var2.fileCachePath(0, isSingle, false, answerId, cacheSingleQuestionDetailBean, cachePaperQuestionDetailBean, downloadEssayBean.filepath);
                var2.writeDownloadFilePathToFile();

                if (!DlEssayDataCache.getInstance().isContains(downloadEssayBean)) {
                    DownLoadEssayHelper.getInstance().startDowningEssay(downloadEssayBean);
                }

                if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {     // 课后作业下载开始要弹dialog提示
                    if (SpUtils.getHomeworkDownloadTipsShow()) {
                        homeworkDownloadTip = new CorrectDialog(mActivity, R.layout.homework_download_tip);
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
            }
        }
    }

    // 是否是模考
    private boolean isEssaySc() {
        return requestType == EssayExamActivity.ESSAY_EXAM_SC;
    }

    // 刷新显示地区
    public void refreshAreaTitle(final List<SingleAreaListBean> var) {
        if (var != null && selPos < var.size()) {
            SingleAreaListBean var2 = var.get(selPos);
            if (var2 == null || materialsFragment == null) {
                return;
            }
            areaId = var2.areaId;
            materialsFragment.setAreaTitle(var2.areaName);
        }
    }

    /**
     * 单题选择地区
     */
    public void refreshOnclickAreaTitle(final List<SingleAreaListBean> var) {
        if (mEssayExamImpl != null && province_view != null) {
            province_view.setVisibility(View.VISIBLE);
            if (selPos >= var.size()) {
                return;
            }
            final SingleAreaListBean va = var.get(selPos);
            if (va == null) {
                return;
            }
            province_view.refreshView(var, selPos, "请选择试卷", va.areaName, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    province_view.setVisibility(View.GONE);
                    if (selPos == position) return;
                    // 取消计时，然后获取到问题详情，再开始计时
                    EssayExamTimerHelper.getInstance().onDestroy();
                    // 单题切换地区，清除答题页存储的图片信息
                    EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_SINGLE_CHANGE_ARED));
                    selPos = position;
                    cacheSingleAreaListBean = var.get(position);
                    if (cacheSingleAreaListBean != null) {

                        // 这里为了人工批改被退回，把 答题卡Id 和 批改状态 初始化
                        if (cacheSingleAreaListBean.questionBaseId == extraArgs.getLong("questionBaseId") && EssayExamDataCache.getInstance().correctMode == 2) {
                            // 人工批改对应的questionBaseId下 需要取一下 答题卡Id 和 批改状态
                            answerId = extraArgs.getLong("answerId", 0);
                            bizStatus = extraArgs.getInt("bizStatus", 0);
                        } else {
                            // 否则，置为0
                            answerId = 0;
                            bizStatus = 0;
                        }

                        areaId = cacheSingleAreaListBean.areaId;
                        areaName = cacheSingleAreaListBean.areaName;
                        questionBaseId = cacheSingleAreaListBean.questionBaseId;
                        materialsFragment.setAreaTitle(cacheSingleAreaListBean.areaName);
                        getMaterialQuestionData();
                        setBottomView();
                    }
                }
            });
        }
    }

    /**
     * 初始化右侧可拖拽按钮的事件
     */
    private void initRightButton() {
        if (rootView != null) {
            if (rightBtn != null) {
                rightBtn.setOnCusViewCallBack(new RightOperatorTextView.OnCusViewCallBack() {
                    @Override
                    public boolean isActionUp(boolean isOpen) {

                        if (areaId == 9999) {
                            ToastUtils.showEssayToast("估分试卷，暂不可答题");
                            return true;
                        }

                        if (mEssayExamImpl == null) {
                            return true;
                        }
                        if (isSingle) {
                            if (cacheSingleMaterialListBeans == null || cacheSingleMaterialListBeans.size() == 0) {
                                ToastUtils.showEssayToast("资料缺少");
                                return true;
                            }
                            if (cacheSingleQuestionDetailBean == null) {
                                ToastUtils.showEssayToast("试题数据加载中，请稍后再试！");
                                return true;
                            }
                        } else {
                            if (cachePaperMaterialListBeans == null || cachePaperMaterialListBeans.size() == 0) {
                                ToastUtils.showEssayToast("资料缺少");
                                return true;
                            }
                            if (cachePaperQuestionDetailBean == null) {
                                ToastUtils.showEssayToast("试题数据加载中，请稍后再试！");
                                return true;
                            }
                        }
                        // 埋点 点击申论答题按钮
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
                        StudyCourseStatistic.clickEssayAnswer(on_module, exam_title, exam_id);

                        if (EssayExamDataCache.getInstance().correctMode != 0) {
                            goAnswer(EssayExamDataCache.getInstance().correctMode);       // 此时批改方式传几都没问题，不会被答题卡页面接收
                        } else {
                            if (correctModeDialog == null) {
                                correctModeDialog = new CorrectDialog(mActivity, R.layout.chose_correct_way);
                                View contentView = correctModeDialog.mContentView;
                                ImageView ivPersonal = contentView.findViewById(R.id.iv_personal);
                                ImageView ivAi = contentView.findViewById(R.id.iv_ai);
                                // 了解人工批改没有用，按钮隐藏了
                                TextView tvKnow = contentView.findViewById(R.id.tv_known);
                                tvKnow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        correctModeDialog.dismiss();
                                        BaseWebViewFragment.lanuch(mActivity, "http://www.baidu.com", "人工批改说明");
                                    }
                                });
                                ivPersonal.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        correctModeDialog.dismiss();
                                        goAnswer(2);
                                    }
                                });
                                ivAi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        correctModeDialog.dismiss();
                                        goAnswer(1);
                                    }
                                });
                            }
                            correctModeDialog.show();
                        }

                        return true;
                    }
                });
            }
        }
    }

    /**
     * 1、智能批改 2、人工批改
     */
    private void goAnswer(int correctMode) {
        // 如果当前答题卡类型和correct不一样，就重新请求问题答题卡
        if (isSingle) {
            if (cacheSingleQuestionDetailBean.correctMode != correctMode) {
                EssayExamDataCache.getInstance().correctMode = correctMode;
                needGoAnswer = true;
                getMaterialQuestionData();    // 获取单题问题
                return;
            }
        } else {
            if (cachePaperQuestionDetailBean.essayPaper.correctMode != correctMode) {
                EssayExamDataCache.getInstance().correctMode = correctMode;
                needGoAnswer = true;
                getMaterialQuestionData();    // 获取套题问题correctMode 1、智能批改 2、人工批改
                return;
            }

        }
        EssayExamMessageEvent var = new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_start_EssayExamEditAnswer);
        var.extraBundle = new Bundle();
        var.extraBundle.putInt("areaId", areaId);
        var.extraBundle.putInt("curPos", getCurrentQuestion());     // 当前查看的问题页面发过去
        EventBus.getDefault().post(var);
        // 点击悬浮按钮，清除材料页，问题页内容的选中等状态
        EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_MATERIAL_CONTENT_CLEAR_VIEW));
        EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_QUESTION_CONTENT_CLEAR_VIEW));
    }

    /**
     * 用于显示答案，获取当前是第几个问题
     */
    public int getCurrentQuestion() {
        if (questionFragment != null) {
            return questionFragment.getCurrentQuestion();
        }
        return 0;
    }

    private void startTimer() {         // 开始模考倒计时
        if (isEssaySc() && cachePaperQuestionDetailBean != null && cachePaperQuestionDetailBean.essayPaper != null && cachePaperQuestionDetailBean.essayPaper.startTime > 0) {
            tvTitle.setTextAppearance(getContext(), R.style.text_italic);
            EssayExamTimerHelper.getInstance().resumeExamTime(requestType, false, null, cachePaperQuestionDetailBean);
            setTime();
            EssayExamTimerHelper.getInstance().startScExam(requestType, cachePaperQuestionDetailBean);
        }
    }

    private void setTime() {
        if (tvTitle != null) {
            if (requestType == EssayExamActivity.ESSAY_EXAM_SC) {      // 模考大赛
                if (cachePaperQuestionDetailBean != null && cachePaperQuestionDetailBean.essayPaper != null) {
                    tvTitle.setText(TimeUtils.getSecond22HourMinTime(cachePaperQuestionDetailBean.essayPaper.remainTime));
                }
            }
        }
    }

    // **************************************************************   网络访问

    // 根据单题组Id，获取地区列表（一道题可以属于多个地区）
    public void getSingleAreaListDetail(long similarId) {
        if (mEssayExamImpl != null) {
            mEssayExamImpl.getSingleAreaListDetail(similarId);
        }
    }

    // 获取单题 问题（如果有未完成的答题卡，会包含答题卡信息） 和 材料
    public void getSingleData(long questionBaseId, int correctMode, int modeType, long answerId, int bizStatus) {
        if (mEssayExamImpl != null) {
            mEssayExamImpl.getSingleData(questionBaseId, correctMode, modeType, answerId, bizStatus);
        }
    }

    // 获取套题 问题（如果有未完成的答题卡，会包含答题卡信息） 和 材料
    public void getPaperData(long paperId, int correctMode, int modeType, long answerId, int bizStatus) {
        if (mEssayExamImpl != null) {
            mEssayExamImpl.getPaperData(paperId, correctMode, modeType, answerId, bizStatus);
        }
    }

    // 获取模考 问题（申论模考答题卡是第一次获取问题的时候，后台自动创建的） 和 材料
    public void getMockPaperData(long paperId) {
        if (mEssayExamImpl != null) {
            mEssayExamImpl.getMockPaperData(paperId);
        }
    }
    // **************************************************************   网络访问

    @Override
    public boolean onBackPressed() {
        if (areaId == 9999) {                                                                       // 估分试卷，不可作答
            mActivity.finish();
        } else if ((isSingle && cacheSingleQuestionDetailBean == null)
                || (!isSingle && (cachePaperQuestionDetailBean == null
                || cachePaperQuestionDetailBean.essayPaper == null))) {                             // 如果题为空，就直接finish
            mActivity.finish();
        } else if ((isSingle && cacheSingleQuestionDetailBean.answerCardId <= 0)
                || (!isSingle && cachePaperQuestionDetailBean.essayPaper.answerCardId <= 0 && requestType != EssayExamActivity.ESSAY_EXAM_SC)) {      // 如果答题卡Id为0
            mActivity.finish();
        } else if (mEssayExamImpl != null) {                               // 去掉保存弹窗，直接保存
            // 神策埋点，保存退出
            if (isEssaySc()) {
                StudyCourseStatistic.simulatedOperation("退出", Type.getSubject(SpUtils.getUserSubject()), Type.getCategory(SpUtils.getUserCatgory()), String.valueOf(paperId), titleView);
            }
            // 如果是模考大赛，并且还没开始考试，就直接finish。否则就保存
            if (isEssaySc() && !EssayExamTimerHelper.getInstance().isEnableExam()) {
                EventBus.getDefault().post(new BaseMessageEvent<>(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_ESSAY_SAVE_SUCCESS, new SimulationContestMessageEvent()));
                mActivity.finish();
            } else {
                mEssayExamImpl.paperCommit(requestType, false, 0, isSingle, 0, cacheSingleQuestionDetailBean, cachePaperQuestionDetailBean);
            }
        } else {
            mActivity.finish();
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        // 模考在这里暂停
        if (isEssaySc() && cachePaperQuestionDetailBean != null) {
            EssayExamTimerHelper.getInstance().pauseExamTime();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isEssaySc() && cachePaperQuestionDetailBean != null) {
            startTimer();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (rightBtn != null) {
            rightBtn.resetPosition();
        }
    }

    @Override
    protected void onSaveState(Bundle outState) {
        EssayExamDataCache.getInstance().mEssayExamImpl_m = mEssayExamImpl;
        outState.putInt("selPos", selPos);
        outState.putLong("questionBaseId", questionBaseId);
        outState.putInt("areaId", areaId);
        outState.putLong("answerId", answerId);
        outState.putInt("bizStatus", bizStatus);
        outState.putString("areaName", areaName);
        outState.putBoolean("isThisDown", isThisDown);
    }

    @Override
    protected void onRestoreState(Bundle outState) {
        selPos = outState.getInt("selPos", selPos);
        questionBaseId = outState.getLong("questionBaseId", questionBaseId);
        areaId = outState.getInt("areaId", areaId);
        answerId = outState.getLong("answerId", answerId);
        bizStatus = outState.getInt("bizStatus", bizStatus);
        areaName = outState.getString("areaName", areaName);
        isThisDown = outState.getBoolean("isThisDown", isThisDown);
        if (mEssayExamImpl != null) {
            if (isSingle && mEssayExamImpl.cacheSingleAreaListBeans != null && mEssayExamImpl.cacheSingleMaterialListBeans != null && mEssayExamImpl.cacheSingleQuestionDetailBean != null) {
                EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_net_getSingleAreaListDetail));
                EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_net_getSingleDataSuccess));
            } else if (mEssayExamImpl.cachePaperMaterialListBeans != null && mEssayExamImpl.cachePaperQuestionDetailBean != null) {
                EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_net_getPaperDataSuccess));
            } else {
                onLoadData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
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

    public static EssayExamMaterials newInstance(Bundle extra) {
        EssayExamMaterials fragment = new EssayExamMaterials();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }

    /**
     * 拖拽Bar的Touch事件
     */
    private class MaterialTouch implements View.OnTouchListener {

        boolean isClear = false;
        int lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClear = true;
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (isClear) {
                        isClear = false;
                        EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_QUESTION_CONTENT_CLEAR_VIEW));
                        EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_MATERIAL_CONTENT_CLEAR_VIEW));
                    }

                    int dy = (int) event.getRawY() - lastY;

                    setQuestionCardHeight(dy);

                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    }

    int lastHeight;

    // 设置问题卡片的高度，通过拖拽 卡片 或者 拖拽拖拽条
    private void setQuestionCardHeight(int dy) {
        lastHeight = flQuestionContent.getHeight();
        int i = lastHeight - dy;
        if (i > screenHeight - DisplayUtil.dp2px(170) || i < DisplayUtil.dp2px(41)) {
            return;
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) flQuestionContent.getLayoutParams();
        layoutParams.height = i;
        flQuestionContent.setLayoutParams(layoutParams);
    }
}
