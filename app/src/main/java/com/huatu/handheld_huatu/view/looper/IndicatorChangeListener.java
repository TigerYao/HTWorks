package com.huatu.handheld_huatu.view.looper;

import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * desc:indicator change 监听
 *
 * @author zhaodongdong
 *         QQ: 676362303
 *         email: androidmdeveloper@163.com
 */
public class IndicatorChangeListener implements ViewPager.OnPageChangeListener {
    private ArrayList<ImageView> mPointViews;
    private int[] mIndicatorIds;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    public IndicatorChangeListener(ArrayList<ImageView> pointViews, int[] indicatorIds) {
        mPointViews = pointViews;
        mIndicatorIds = indicatorIds;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mPointViews.size(); i++) {
            if (position != i) {
                mPointViews.get(i).setImageResource(mIndicatorIds[0]);
            }
            mPointViews.get(position).setImageResource(mIndicatorIds[1]);
        }
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    /**
     * 设置viewpager监听
     *
     * @param onPageChangeListener listener
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }
}
