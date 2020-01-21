package com.huatu.handheld_huatu.mvppresenter.impl;

import com.huatu.handheld_huatu.mvppresenter.BasePresenter;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * desc:BasePresenterImpl
 *
 * @author zhaodongdong
 *         QQ: 676362303
 *         email: androidmdeveloper@163.com
 */
public abstract class BasePresenterImpl implements BasePresenter {
    private CompositeSubscription mCompositeSubscription;

    protected void addSubscription(Subscription s) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(s);
    }

    @Override
    public void unsubcrible() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
        onDestroyView();
    }

    protected abstract void onDestroyView();
}
