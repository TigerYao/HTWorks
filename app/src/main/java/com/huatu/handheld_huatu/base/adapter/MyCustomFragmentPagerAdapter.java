package com.huatu.handheld_huatu.base.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

/**
 * Created by cpoopc on 2015-02-10.
 */
public class MyCustomFragmentPagerAdapter extends MyFragmentPagerAdapter {

    protected List<Fragment> fragmentList;
    protected List<String> titleList;

    public MyCustomFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    protected   String makeFragmentName(int position){
        return titleList.get(position);
    }
}
