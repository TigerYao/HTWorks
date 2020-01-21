package com.huatu.handheld_huatu.base;

import android.content.Intent;
import android.os.Bundle;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * 
 */
public abstract class AbsHtEventFragment extends BaseFragment {

    protected CompositeSubscription compositeSubscription;
    protected Subscription timerSubscription;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(Object event) {
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    protected void onSaveState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        super.onSaveState(outState);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
        unRegisterTimeTask();
    }

    protected void unRegisterTimeTask() {
        if (timerSubscription != null && !timerSubscription.isUnsubscribed()) {
            LogUtils.d(TAG, "unRegisterTimeTask");
            timerSubscription.unsubscribe();
        }
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
            if (Method.isActivityFinished(mActivity)) {
                LogUtils.e(TAG, getClass().getSimpleName()+" isActivityFinished  null");
                mActivity.finish();
                return;
            }
    }
}
