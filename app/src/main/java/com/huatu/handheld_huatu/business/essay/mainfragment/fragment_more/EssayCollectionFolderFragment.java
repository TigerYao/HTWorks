package com.huatu.handheld_huatu.business.essay.mainfragment.fragment_more;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.v4.FragmentPagerItem;
import com.ogaclejapan.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.v4.FragmentPagerItems;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 申论收藏
 */
public class EssayCollectionFolderFragment extends BaseFragment {

    private static final String TAG = "EssayCollectionFolderFragment";
    private List<String> title = new ArrayList<String>();
    private TopActionBar topActionBar;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_essay_collection_folder_layout;
    }

    @Override
    protected void onInitView() {
        refreshTitle();
        initTitleBar();
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.collection_viewpager);
        SmartTabLayout viewPagerTab = (SmartTabLayout) rootView.findViewById(R.id.collection_viewpager_tab);
        viewPagerTab.setDividerColors(android.R.color.white);
        FragmentPagerItems pages = new FragmentPagerItems(mActivity);
        for (String titleRes : title) {
            pages.add(FragmentPagerItem.of(titleRes, EssayCollectionListFragment.class));
        }
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        viewPagerTab.setViewPager(viewPager);
    }

    public void refreshTitle() {
        title.clear();
        title.add("标准答案");
        title.add("套题");
        title.add("文章写作");
    }

    private void initTitleBar() {
        topActionBar = (TopActionBar) rootView.findViewById(R.id.fragment_title_bar);
        topActionBar.setTitle("收藏");
        topActionBar.setDividerShow(true);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    public static EssayCollectionFolderFragment newInstance() {
        return new EssayCollectionFolderFragment();
    }
}
