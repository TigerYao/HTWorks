package com.huatu.handheld_huatu.base.fragment;

import android.os.Handler;

import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;


public abstract class ADelayStripTabsFragment<T extends AStripTabsFragment.StripTabItem> extends AStripTabsFragment<T> {


    @Override
    public void onPageSelected(int position) {
       //super.onPageSelected(position);
        mCurrentPosition = position;

        if (configLastPositionKey() != null) {
            PrefStore.putSettingString("PagerLastPosition" + configLastPositionKey(), mItems.get(position).getType());
        }

        LogUtils.e("onPageSelected___?_", position + "");

        mHandler.removeCallbacks(refreshFragmentRunnable);
        mHandler.postDelayed(refreshFragmentRunnable, 500);

       /* if((!isFragmentFinished()&&(getActivity() instanceof BaseFragmentActivity))){
            ((BaseFragmentActivity)getActivity()).setSwipeBackEnable(position == 0);
        }*/
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
            if (fragment instanceof IStripTabInitData) {
                ((IStripTabInitData) fragment).onStripTabRequestData();
            }
        }
    };

    Handler mHandler = new Handler() {

    };

}
