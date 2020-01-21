package com.huatu.handheld_huatu.business.essay.examfragment;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItems;
import com.ogaclejapan.v4.FragmentStatePagerItemAdapter;

/**
 * 问题Tab页
 */
public class EssayQuestionFragment extends BaseFragment {

    private boolean isSingle;
    private SingleQuestionDetailBean cacheSingleQuestionDetailBean;
    private PaperQuestionDetailBean cachePaperQuestionDetailBean;

    private long showQuestionId;                         // 默认显示的Id

    private SmartTabLayout tabLayout;
    private ViewPager viewPager;

    private TextView tvNoData;

    private int current = 0;

    private boolean isSetData = false;                                      // 是否传进来数据了
    private FragmentStatePagerItemAdapter pagerAdapter;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_essay_question;
    }

    @Override
    protected void onInitView() {
        tabLayout = rootView.findViewById(R.id.ex_materials_ques_viewpager_tab);
        viewPager = rootView.findViewById(R.id.ex_materials_ques_viewpager);
        tvNoData = rootView.findViewById(R.id.tv_no_data);
        tabLayout.setDividerColors(android.R.color.white);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (isSetData) {
            reFreshView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void reFreshView() {
        if ((isSingle && (cacheSingleQuestionDetailBean == null || TextUtils.isEmpty(cacheSingleQuestionDetailBean.answerRequire)))
                || (!isSingle && (cachePaperQuestionDetailBean == null || cachePaperQuestionDetailBean.essayQuestions == null || cachePaperQuestionDetailBean.essayQuestions.size() == 0))) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
        }

        FragmentPagerItems pages = new FragmentPagerItems(mActivity);
        int i = 0;
        if (isSingle) {
            Bundle arg = new Bundle();
            arg.putString("title_question", "1");
            arg.putSerializable("singleQuestionDetailBean", cacheSingleQuestionDetailBean);
            arg.putInt("request_type", requestType);
            arg.putBoolean("isSingle", true);
            pages.add(FragmentPagerItem.of("问题", 1.f, EssExQuestionContent.class, arg));
        } else {
            if (cachePaperQuestionDetailBean != null && cachePaperQuestionDetailBean.essayQuestions != null) {
                for (SingleQuestionDetailBean var : cachePaperQuestionDetailBean.essayQuestions) {
                    i++;
                    Bundle arg = new Bundle();
                    arg.putString("title_question", "问题" + i);
                    arg.putSerializable("singleQuestionDetailBean", var);
                    arg.putInt("request_type", requestType);
                    arg.putBoolean("isSingle", false);
                    pages.add(FragmentPagerItem.of("问题" + i, 1.f, EssExQuestionContent.class, arg));
                }
            }
        }
        if (i < 15) {
            viewPager.setOffscreenPageLimit(i);
        }
        pagerAdapter = new FragmentStatePagerItemAdapter(getChildFragmentManager(), pages);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setViewPager(viewPager);

        if (showQuestionId != 0) {
            if (cachePaperQuestionDetailBean != null && cachePaperQuestionDetailBean.essayQuestions != null) {
                int di = 0;
                for (int j = 0; j < cachePaperQuestionDetailBean.essayQuestions.size(); j++) {
                    SingleQuestionDetailBean singleQuestionDetailBean = cachePaperQuestionDetailBean.essayQuestions.get(j);
                    if (singleQuestionDetailBean.questionBaseId == showQuestionId) {
                        di = j;
                        break;
                    }
                }
                if (di != 0) {
                    viewPager.setCurrentItem(di);
                }
            }
        }
    }

    public void setData(boolean isSingle, SingleQuestionDetailBean cacheSingleQuestionDetailBean, PaperQuestionDetailBean cachePaperQuestionDetailBean) {
        this.isSingle = isSingle;
        this.cacheSingleQuestionDetailBean = cacheSingleQuestionDetailBean;
        this.cachePaperQuestionDetailBean = cachePaperQuestionDetailBean;
        this.isSetData = true;
        if (isViewCreated) {
            reFreshView();
        }
    }

    public void setData(boolean isSingle, SingleQuestionDetailBean cacheSingleQuestionDetailBean, PaperQuestionDetailBean cachePaperQuestionDetailBean, long showQuestionId) {
        this.showQuestionId = showQuestionId;
        setData(isSingle, cacheSingleQuestionDetailBean, cachePaperQuestionDetailBean);
    }

    public static EssayQuestionFragment newInstance() {
        EssayQuestionFragment questionFragment = new EssayQuestionFragment();
        return questionFragment;
    }

    public int getCurrentQuestion() {
        return current;
    }
}
