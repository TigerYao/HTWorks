package com.huatu.handheld_huatu.business.ztk_vod.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.huatu.handheld_huatu.base.adapter.MyCustomFragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2019\12\30 0030.
 */

public class MyAddFragmentPagerAdapter extends MyCustomFragmentPagerAdapter {

    private Fragment mFirstFragment;

    public MyAddFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
        super(fm,fragmentList,titleList);
        mFirstFragment=fragmentList.get(0);
    }

    public int getItemPosition(@NonNull Object object) {
        return    object==mFirstFragment? POSITION_UNCHANGED:POSITION_NONE;
    }

    public void insertTabAt(int index,Fragment fragment,String title){
        fragmentList.add(index,fragment);
        titleList.add(index,title);

    }

    public void addList(List<Fragment> fragments,List<String> titles){
        fragmentList.addAll(fragments);
        titleList.addAll(titles);
        this.notifyDataSetChanged();

    }
}
