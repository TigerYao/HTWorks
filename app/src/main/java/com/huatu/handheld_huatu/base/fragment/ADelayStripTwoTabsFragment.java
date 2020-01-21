package com.huatu.handheld_huatu.base.fragment;

import android.os.Handler;

import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;


public abstract class ADelayStripTwoTabsFragment<T extends AStripTwoTabsFragment.StripTabItem> extends AStripTwoTabsFragment<T> {


    @Override
    public void onPageSelected(int position) {
       //super.onPageSelected(position);
        mCurrentPosition = position;

        if (configLastPositionKey() != null) {
            PrefStore.putSettingString("PagerLastPosition" + configLastPositionKey(), mItems.get(position).getType());
        }

        LogUtils.e("onPageSelected___?_", position + "");

        mHandler.removeCallbacks(refreshFragmentRunnable);
        mHandler.postDelayed(refreshFragmentRunnable, 300);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacks(refreshFragmentRunnable);

    }

    Runnable refreshFragmentRunnable = new Runnable() {

        @Override
        public void run() {
            android.support.v4.app.Fragment fragment = getCurrentFragment();
            if (fragment instanceof AStripTabsFragment.IStripTabInitData) {
                ((AStripTabsFragment.IStripTabInitData) fragment).onStripTabRequestData();
            }
        }
    };

    Handler mHandler = new Handler() {

    };

}
