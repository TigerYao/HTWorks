package com.huatu.handheld_huatu.business.matches.fragment;

import android.os.Bundle;
import android.util.Log;

import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScDetailBean;
import com.huatu.handheld_huatu.mvppresenter.simulation.SimulationContestImpl;
import com.huatu.handheld_huatu.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * 模考大赛
 */
public abstract class AbsSimulationContestFragment extends BaseFragment {

    protected SimulationContestImpl mPresenter;
    public CompositeSubscription compositeSubscription;
    protected Subscription timerSubscription;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(BaseMessageEvent<SimulationContestMessageEvent> event) {
        if (event == null || event.typeExObject == null) {
            return;
        }
        LogUtils.d("AbsSimulationContestFragment", getClass().getSimpleName() + " onEventUIUpdate  event.type " + event.type);
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        compositeSubscription = new CompositeSubscription();
        mPresenter = new SimulationContestImpl(compositeSubscription);
    }

    private boolean isViewInitiated;
    private boolean isVisibleToUser;
    private boolean isDataInitiated;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        prepareFetchData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (!isDataInitiated) {
            prepareFetchData();
        }
    }

    public boolean prepareFetchData() {
        return prepareFetchData(false);
    }

    public boolean prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            getData();
            isDataInitiated = true;
            return true;
        }
        return false;
    }

    // 懒加载调用的加载数据方法
    protected void getData() {

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
            LogUtils.d("AbsSimulationContestFra SCMainChildFragment", "unRegisterTimeTask");
            timerSubscription.unsubscribe();
        }
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        if (mPresenter == null) {
            if (mActivity != null) {
                LogUtils.e("AbsSimulationContestFragment", getClass().getSimpleName() + " mPresenter  null");
                mActivity.finish();
                return;
            }
        }
    }
}
