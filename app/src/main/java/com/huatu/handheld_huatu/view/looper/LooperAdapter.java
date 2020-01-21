package com.huatu.handheld_huatu.view.looper;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.view.looper.holder.LooperHolder;
import com.huatu.handheld_huatu.view.looper.holder.LooperViewHolderCreator;

import java.util.List;

/**
 * desc:
 *
 * @author zhaodongdong
 *         QQ: 676362303
 *         email: androidmdeveloper@163.com
 */
public class LooperAdapter<T> extends PagerAdapter {
    private boolean mCanLoop = true;
    private LooperViewPager mViewPager;
    protected List<T> mDatas;
    protected LooperViewHolderCreator mHolderCreator;
    private static final int MULTIPLE_COUNT = 300;


    public LooperAdapter(LooperViewHolderCreator holderCreator, List<T> datas) {
        mHolderCreator = holderCreator;
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mCanLoop ? getRealCount() * MULTIPLE_COUNT : getRealCount();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = toRealPosition(position);
        View view = getView(realPosition, null, container);
        container.addView(view);
        return view;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        int position = mViewPager.getCurrentItem();
        if (position == 0) {
            position = mViewPager.getFirstItem();
        } else if (position == getCount() - 1) {
            position = mViewPager.getLastItem();
        }
        mViewPager.setCurrentItem(position, false);
    }

    public View getView(int position, View view, ViewGroup container) {
        LooperHolder holder = null;
        if (view == null) {
            holder = (LooperHolder) mHolderCreator.createHolder();
            view = holder.createView(container.getContext());
            view.setTag(R.id.looper_item_tag, holder);
        } else {
            holder = (LooperHolder<T>) view.getTag(R.id.looper_item_tag);
        }
        if (mDatas != null && !mDatas.isEmpty()) {
            holder.upDataUI(container.getContext(), position, mDatas.get(position));
        }
        return view;
    }


    public int getRealCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /**
     * @param position 传入position
     * @return 实际的position
     */
    public int toRealPosition(int position) {
        int realPosition = getRealCount();
        if (realPosition == 0) {
            return 0;
        }
        return position % realPosition;
    }

    /**
     * 设置viewpager
     *
     * @param viewPager viewpager
     */
    public void setViewPager(LooperViewPager viewPager) {
        mViewPager = viewPager;
    }

    /**
     * 设置是否可以loop
     *
     * @param canLoop boolean
     */
    public void setCanLoop(boolean canLoop) {
        mCanLoop = canLoop;
    }
}
