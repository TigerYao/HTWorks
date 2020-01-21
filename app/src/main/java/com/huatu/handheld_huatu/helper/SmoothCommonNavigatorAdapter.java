package com.huatu.handheld_huatu.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.huatu.utils.DensityUtils;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * Created by Administrator on 2019\9\9 0009.
 */

public class SmoothCommonNavigatorAdapter extends CommonNavigatorAdapter {
    public   String[] mDataList;

    public void setDataRefresh(String[] titleList){
        mDataList=titleList;
        this.notifyDataSetChanged();
    }

    ViewPager mViewPapger;
    public SmoothCommonNavigatorAdapter(String[] datalist,ViewPager viewPager) {
        mDataList=datalist;
        mViewPapger=viewPager;
    }

    @Override
    public int getCount() {
        return mDataList.length;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
        simplePagerTitleView.setText(mDataList[index]);
        simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
        simplePagerTitleView.setNormalColor(Color.parseColor("#4A4A4A"));
        simplePagerTitleView.setSelectedColor(Color.parseColor("#000000"));
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPapger.setCurrentItem(index);
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        indicator.setStartInterpolator(new AccelerateInterpolator());
        indicator.setEndInterpolator(new DecelerateInterpolator(1.6f));
        indicator.setLineHeight(UIUtil.dip2px(context, 4));
        indicator.setYOffset(DensityUtils.dp2px(context,10));
        indicator.setColors(Color.parseColor("#FF6D73"));
        return indicator;
    }
}
