package com.huatu.handheld_huatu.business.arena.adapter;

import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PagerViewAdapter extends PagerAdapter {

    private ArrayList<View> views;
    private ArrayList<String> titles;

    public PagerViewAdapter(ArrayList<View> views, ArrayList<String> titles) {
        this.views = views;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (titles != null && titles.size() > position) ? titles.get(position) : "";
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
