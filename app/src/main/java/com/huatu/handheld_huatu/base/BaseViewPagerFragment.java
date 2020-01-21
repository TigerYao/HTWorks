package com.huatu.handheld_huatu.base;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2017/9/6.
 */

public abstract class BaseViewPagerFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    public LinearLayout layoutTitle;
    public TextView tvLeft;
    protected TabLayout tabLayout;
    public TextView tvRight;
    public ViewPager viewPager;
    protected BaseViewPagerFragmentPagerAdapter mAdapter;

    protected List<BaseFragment> fragmentList = new ArrayList<>();
    protected List<String> nameList = new ArrayList<>();

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_base_view_pager_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        layoutTitle = (LinearLayout) rootView.findViewById(R.id.base_view_pager_title_layout);
        tvLeft = (TextView) rootView.findViewById(R.id.base_view_pager_title_left_tv);
        tabLayout = (TabLayout) rootView.findViewById(R.id.base_view_pager_title_tab_id);
        tvRight = (TextView) rootView.findViewById(R.id.base_view_pager_title_right_tv);
        viewPager = (ViewPager) rootView.findViewById(R.id.base_view_pager_id);
        initFrgList();
        if(Method.isListEmpty(fragmentList)) {
            LogUtils.e("fragmentList is empty");
            mActivity.finish();
            return;
        }
        initNameList();
        if(Method.isListEmpty(nameList)) {
            LogUtils.e("fragment nameList is empty");
            mActivity.finish();
            return;
        }
        if(nameList.size() < fragmentList.size()) {
            for(int i = nameList.size(); i < fragmentList.size(); i++) {
                nameList.add("");
            }
        }
        for(int i = 0; i < fragmentList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(nameList.get(i)));
        }
        mAdapter = new BaseViewPagerFragmentPagerAdapter(getChildFragmentManager()){
            @Override
            protected BaseFragment instanceFrg(int position) {
                return instanceFragment(position);
            }
        };
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);
    }

    protected abstract void initFrgList();
    protected abstract void initNameList();

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected abstract BaseFragment instanceFragment(int position);
}
