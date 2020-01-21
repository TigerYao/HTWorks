package com.huatu.handheld_huatu.business.essay.examfragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.cusview.MaterialsCardView;
import com.huatu.handheld_huatu.business.essay.cusview.RightOperatorTextView;
import com.huatu.handheld_huatu.business.essay.cusview.TextSizeControlImageView;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
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
 * 答案详情页
 */
public class EssayExamRightAns extends BaseFragment {

    private static final String TAG = "EssayExamRightAns";

    @BindView(R.id.iv_text_size)
    TextSizeControlImageView ivTextSize;                    // 字体大小控制按钮

    @BindView(R.id.view_divider_01)
    View viewDivider01;                                     // 上分割线
    @BindView(R.id.ex_answer_detail_viewpager_tab)
    SmartTabLayout viewPagerTab;
    @BindView(R.id.view_divider_02)
    View viewDivider02;                                     // 下分割线
    @BindView(R.id.ex_answer_detail_viewpager)
    ViewPager viewPager;

    @BindView(R.id.back_exam_materials)
    RightOperatorTextView backExamMaterials;                // 悬浮按钮
    @BindView(R.id.materials_card_view)
    MaterialsCardView materialsCardView;                    // 材料卡片

    private int curPos;
    private String titleView;
    private boolean isSingle;
    private boolean isFromArgue;
    private Bundle extraArgs;
    private EssExRightContent mEssExAnswerContent;
    private FragmentStatePagerItemAdapter adapter;

    private int areaId;
    public long questionBaseId;
    public long questionDetailId;
    private long answerCardId;
    private long paperId;
    private SingleQuestionDetailBean mSingleQuestionDetailBean;
    private PaperQuestionDetailBean mPaperQuestionDetailBean;
    private List<String> titleMatrQues = new ArrayList<String>();
    private long essayAnswerId;//答题卡id

    public int getCurrentPosition() {
        return curPos;
    }

    public void setSelectPosition(int selectPosition) {
        if (!isSingle) {
            curPos = selectPosition;
            if (viewPager != null) {
                viewPager.setCurrentItem(curPos, false);
            }
            onPageSelectPaper(curPos);
        }
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.essay_exam_answer_detail_layout;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.ESSAYEXAM_essExMaterialsContent_setTextSize) {      // 改变字体大小
            ivTextSize.initStyle();
        } else if (event.type == EssayExamMessageEvent.EssayExam_CHANGE_CORRECT_TYPE_GETED) {       // 改变批改方式/切换地区，材料获取数据成功，通知这里刷新内容，并重新开始计时
            curPos = 0;
            onLoadData();
        }
        return true;
    }

    @Override
    protected void onInitView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);

        ivTextSize.setType(0, "查看答案页");

        materialsCardView.setChildFragmentManager(getChildFragmentManager());

        if (backExamMaterials != null) {
            backExamMaterials.resetPosition();
        }

        initData();
        initbackExamMaterials();
    }

    @Override
    protected void onInitListener() {

    }

    private void initbackExamMaterials() {
        if (backExamMaterials != null) {
            backExamMaterials.setOnCusViewCallBack(new RightOperatorTextView.OnCusViewCallBack() {
                @Override
                public boolean isActionUp(boolean isOpen) {

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


    private void initData() {
        if (args != null) {
            requestType = args.getInt("request_type");
            curPos = args.getInt("curPos");
            extraArgs = args.getBundle("extra_args");
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            titleView = extraArgs.getString("titleView");
            isSingle = extraArgs.getBoolean("isSingle");
            isFromArgue = extraArgs.getBoolean("isFromArgue");
            essayAnswerId = extraArgs.getLong("answerId");
        }
    }

    @Override
    protected void onLoadData() {
        refreshView();
    }

    private void refreshView() {

        if (isSingle) {
            SingleQuestionDetailBean var = EssayExamDataCache.getInstance().cacheSingleQuestionDetailBean;
            if (var != null) {
                mSingleQuestionDetailBean = var;
            }
            if (mSingleQuestionDetailBean != null) {
                if (mSingleQuestionDetailBean.answerCardId > 0) {
                    answerCardId = mSingleQuestionDetailBean.answerCardId;
                    if (essayAnswerId <= 0) {
                        essayAnswerId = answerCardId;
                    }
                }
                questionBaseId = mSingleQuestionDetailBean.questionBaseId;
                questionDetailId = mSingleQuestionDetailBean.questionDetailId;
            }
        } else {
            PaperQuestionDetailBean var = EssayExamDataCache.getInstance().cachePaperQuestionDetailBean;
            if (var != null) {
                mPaperQuestionDetailBean = var;
            }
            if (mPaperQuestionDetailBean != null) {
                if (mPaperQuestionDetailBean.essayPaper != null) {
                    paperId = mPaperQuestionDetailBean.essayPaper.paperId;
                    answerCardId = mPaperQuestionDetailBean.essayPaper.answerCardId;
                    if (essayAnswerId <= 0) {
                        essayAnswerId = answerCardId;
                    }
                }
                if (mPaperQuestionDetailBean.essayQuestions != null && mPaperQuestionDetailBean.essayQuestions.size() > 0) {
                    mSingleQuestionDetailBean = mPaperQuestionDetailBean.essayQuestions.get(0);
                }
            }
        }

        if (isSingle) {
            materialsCardView.setData(EssayExamDataCache.getInstance().cacheSingleMaterialListBeans);
        } else {
            materialsCardView.setData(EssayExamDataCache.getInstance().cachePaperMaterialListBeans);
        }

        if (isSingle) {
            if (mSingleQuestionDetailBean != null) {
                refreshView(mSingleQuestionDetailBean, null);
            }
        } else {
            if (mPaperQuestionDetailBean != null) {
                refreshView(mSingleQuestionDetailBean, mPaperQuestionDetailBean);
            }
        }
    }

    private void refreshView(final SingleQuestionDetailBean singleQuestionDetailBean, final PaperQuestionDetailBean paperQuestionDetailBean) {
        if (singleQuestionDetailBean != null) {
            refreshViewPager(singleQuestionDetailBean, paperQuestionDetailBean);
            TimeUtils.delayTask(new Runnable() {
                @Override
                public void run() {
                    if (viewPager != null) {
                        viewPager.setCurrentItem(curPos);
                    }
                    onPageSelectPaper(curPos);
                }
            }, 100);
        }
    }


    public void refreshViewPager(SingleQuestionDetailBean singleQuestionDetailBean, final PaperQuestionDetailBean paperQuestionDetailBean) {

        if (viewPager == null || viewPagerTab == null || mActivity == null) return;

        viewPagerTab.setDividerColors(android.R.color.white);

        FragmentManager fragmentManager = getChildFragmentManager();
        List<Fragment> fragmentList = fragmentManager.getFragments();
        if (fragmentList != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            for (Fragment fragment : fragmentList) {
                if (fragment != null && fragment.getClass().getSimpleName().equals("EssExRightContent")) {
                    ft.remove(fragment);
                }
            }
            ft.commit();
            fragmentManager.executePendingTransactions();
        }

        FragmentPagerItems pages = new FragmentPagerItems(mActivity);
        int i = 0;
        if (isSingle) {
            titleMatrQues.clear();
            titleMatrQues.add("问题");
            Bundle arg = new Bundle();
            arg.putString("title_question", "1");
            arg.putSerializable("singleQuestionDetailBean", singleQuestionDetailBean);
            arg.putInt("request_type", 1);
            arg.putBoolean("isSingle", true);
            arg.putLong("answerId", essayAnswerId);
            pages.add(FragmentPagerItem.of("1", 1.f, EssExRightContent.class, arg));

        } else {
            if (paperQuestionDetailBean != null && paperQuestionDetailBean.essayQuestions != null) {
                titleMatrQues.clear();
                for (SingleQuestionDetailBean var : paperQuestionDetailBean.essayQuestions) {
                    i++;
                    titleMatrQues.add("问题" + i);
                    Bundle arg = new Bundle();
                    arg.putString("title_question", "问题" + i);
                    arg.putSerializable("singleQuestionDetailBean", var);
                    arg.putInt("request_type", 2);
                    arg.putBoolean("isSingle", false);
                    arg.putLong("answerId", essayAnswerId);
                    arg.putInt("number", i);//增加批改详情序号问题序号，在相应展示时去取相应的内容
                    pages.add(FragmentPagerItem.of("问题" + i, 1.f, EssExRightContent.class, arg));
                }
            }
        }
        if (i < 15) {
            viewPager.setOffscreenPageLimit(i);
        }
        adapter = new FragmentStatePagerItemAdapter(
                mActivity.getSupportFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
            viewDivider01.setVisibility(View.VISIBLE);
        } else {
            viewPagerTab.setVisibility(View.GONE);
            viewDivider01.setVisibility(View.GONE);
        }
    }

    private void onPageSelectPaper(int position) {
        if (adapter != null) {
            curPos = position;
            mEssExAnswerContent = (EssExRightContent) adapter.getPage(position);
            if (!isSingle) {
                if (mPaperQuestionDetailBean != null) {
                    if (mPaperQuestionDetailBean.essayQuestions != null) {
                        mSingleQuestionDetailBean = mPaperQuestionDetailBean.essayQuestions.get(position);
                    }
                }
            }
            if (mSingleQuestionDetailBean != null) {
                refreshContentView(mSingleQuestionDetailBean.isExp);
            }
        }
    }

    private void refreshContentView(boolean isExp) {
        curViewVerify();
        if (isExp) {
            if (mEssExAnswerContent != null) {
                mEssExAnswerContent.hideTogShowQuesTV(View.VISIBLE);
            }
        } else {
            if (mEssExAnswerContent != null) {
                mEssExAnswerContent.hideTogShowQuesTV(View.GONE);
            }
        }
    }

    @OnClick({
            R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPress();
                break;
        }
    }

    private void onBackPress() {
        if (materialsCardView.isShowMaterialCard()) {
            materialsCardView.hide();
            return;
        }
        if (mEssExAnswerContent != null) {
            mEssExAnswerContent.clearView();
        }
        EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_start_EssayExamEditAnswer1));
    }

    private boolean curViewVerify() {
        if (adapter == null) {
            return true;
        }
        if (mEssExAnswerContent == null) {
            mEssExAnswerContent = (EssExRightContent) adapter.getPage(curPos);
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        onBackPress();
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (backExamMaterials != null) {
            backExamMaterials.resetPosition();
        }
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        refreshView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    public static EssayExamRightAns newInstance(Bundle extra) {
        EssayExamRightAns fragment = new EssayExamRightAns();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }
}
