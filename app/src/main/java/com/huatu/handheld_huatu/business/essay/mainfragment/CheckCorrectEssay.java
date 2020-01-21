package com.huatu.handheld_huatu.business.essay.mainfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.business.essay.mainfragment.fragment_check.CheckEssayListFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.NoScrollViewPager;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的批改
 */
public class CheckCorrectEssay extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "CheckCorrectEssay";
    private List<String> title = new ArrayList<String>();
    private TopActionBar topActionBar;
    private int topicType = 0;//（0标准答案，1套题，2文章写作）
    private boolean mToHome = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topicType = args.getInt("topicType");
        mToHome = args.getBoolean("from_push", false);
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.check_exam_efragment;
    }

    @Override
    protected void onInitView() {
        refreshTitle();
        initTitleBar();
        initViewPager();
    }

    @Override
    protected void onLoadData() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
//        EssayCheckImpl essayCheck = new EssayCheckImpl(compositeSubscription);
//        essayCheck.checkCountVerify(0);
//        essayCheck.checkCountVerify(1);
//        essayCheck.checkCountVerify(2);
    }

    private void initViewPager() {
        NoScrollViewPager viewPager = (NoScrollViewPager) rootView.findViewById(R.id.check_viewpager);
        viewPager.setScrollEnable(false);
        SmartTabLayout viewPagerTab = (SmartTabLayout) rootView.findViewById(R.id.check_viewpager_tab);
        viewPagerTab.setDividerColors(android.R.color.white);
        FragmentPagerItems pages = new FragmentPagerItems(mActivity);
        for (String titleRes : title) {
            pages.add(FragmentPagerItem.of(titleRes, CheckEssayListFragment.class));
        }
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);

        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(1);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setCurrentItem(topicType);
    }

    public void refreshTitle() {
        title.clear();
        title.add("标准答案");
        title.add("套题");
        title.add("文章写作");

    }

    private void initTitleBar() {
        topActionBar = (TopActionBar) rootView.findViewById(R.id.fragment_title_bar);
        topActionBar.setTitle("批改记录");
//        topActionBar.showButtonText("批改次数", TopActionBar.RIGHT_AREA, R.color.text_color_light);
        topActionBar.setDividerShow(true);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                if (mToHome && !ActivityStack.getInstance().hasRootActivity()) {
                    MainTabActivity.newIntent(getActivity());
                }
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

//                BaseFrgContainerActivity.newInstance(mActivity,
//                        CheckCountFragment.class.getName(),
//                        null);
//                Toast.makeText(mActivity, "批改次数", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        if(EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
//        RxUtils.unsubscribeIfNotNull(compositeSubscription);
//        Ntalker.getInstance().logout();
    }

    @Override
    public boolean onBackPressed() {
        if (mToHome && !ActivityStack.getInstance().hasRootActivity()) {
            MainTabActivity.newIntent(getActivity());
        }
        return super.onBackPressed();
    }

    public static CheckCorrectEssay newInstance() {
        return new CheckCorrectEssay();
    }

}
