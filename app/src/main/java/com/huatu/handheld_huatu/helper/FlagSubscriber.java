package com.huatu.handheld_huatu.helper;

import rx.Subscriber;

/**
 * Created by Administrator on 2019\4\23 0023.
 */

public abstract class FlagSubscriber<T> extends Subscriber<T> {

    public boolean mFlag=false;

    protected FlagSubscriber(boolean flag) {
        super();
        mFlag=false;
    }
}


