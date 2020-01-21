package com.huatu.handheld_huatu.mvppresenter.me;

import com.huatu.handheld_huatu.event.me.ErrorPapersMessageEvent;
import com.huatu.handheld_huatu.mvppresenter.BasePreMessageEventImpl;

import rx.subscriptions.CompositeSubscription;

public class ErrorPapersImpl extends BasePreMessageEventImpl<ErrorPapersMessageEvent> {
    public ErrorPapersImpl(CompositeSubscription cs) {
        super(cs, new ErrorPapersMessageEvent());
    }

    public Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
