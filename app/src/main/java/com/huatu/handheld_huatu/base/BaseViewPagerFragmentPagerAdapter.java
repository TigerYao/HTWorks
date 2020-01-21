package com.huatu.handheld_huatu.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2017/9/20.
 */

public abstract class BaseViewPagerFragmentPagerAdapter extends FragmentPagerAdapter {
    FragmentManager childFragmentManager;
    protected List<BaseFragment> fragmentList = new ArrayList<>();
    protected List<String> nameList = new ArrayList<>();
    private int viewpagerId;

    public BaseViewPagerFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.childFragmentManager = fm;
    }

    public void setFragmentList(List<BaseFragment> frgmList) {
        this.fragmentList = frgmList;
    }

    public void setNameList(List<String> names) {
        nameList = names;
    }

    public void setViewPagerId(int id) {
        viewpagerId = id;
    }

    @Override
    public Fragment getItem(int position) {
        final long itemId = getItemId(position);
        // Do we already have this fragment?
        String name = makeFragmentName(viewpagerId, itemId);
        BaseFragment fragment = (BaseFragment) childFragmentManager.findFragmentByTag(name);
        if (fragment == null) {
            fragment = instanceFrg(position);
        }
        if (position < fragmentList.size()) {
            fragmentList.set(position, fragment);
        } else {
            fragmentList.add(fragment);
        }
        return fragment;
    }

    private String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return nameList.get(position);
    }

    protected abstract BaseFragment instanceFrg(int position);
}
