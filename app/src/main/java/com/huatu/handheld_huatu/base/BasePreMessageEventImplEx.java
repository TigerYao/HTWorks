package com.huatu.handheld_huatu.base;


import android.os.Bundle;

import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import rx.subscriptions.CompositeSubscription;

public abstract class BasePreMessageEventImplEx {

    String toast = "网络不稳定，请检查网络";
    protected CompositeSubscription compositeSubscription;
    public BaseMessageEvent typeExObject;

    public BasePreMessageEventImplEx(CompositeSubscription cs, BaseMessageEvent typeexo) {
        compositeSubscription = cs;
        typeExObject = typeexo;
    }

    public void setCompositeSubscription(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;
    }

    public CompositeSubscription getCompositeSubscription() {
        return compositeSubscription;
    }

    public void showErrorMsg(int type) {
        CommonUtils.showToast(toast);
    }

    public void postEvent(BaseMessageEvent event) {
        EventBus.getDefault().post(event);
    }

    public void postEvent(int eventType) {
        if (typeExObject != null) {
            typeExObject.type = eventType;
            EventBus.getDefault().post(typeExObject);
        }
    }

    public void postEvent(BaseMessageEvent event, Object... objects) {
        if (objects != null) {
            Object[] mAryObjects = objects;
            Bundle bundle = new Bundle();
            for (int i = 0; i < mAryObjects.length; i++) {
                if (mAryObjects[i] != null) {
                    bundle.putSerializable("obj" + i, (Serializable) mAryObjects[i]);
                }
            }
            event.extraBundle = bundle;
        }
        EventBus.getDefault().post(event);
    }

    public void showProgressBar() {
        postEvent(BaseMessageEvent.BASE_EVENT_TYPE_SHOW_PROGRESS_BAR);
    }

    public void dismissProgressBar() {
        postEvent(BaseMessageEvent.BASE_EVENT_TYPE_DISMISS_PROGRESS_BAR);
    }

    public void onLoadDataFailed() {
        postEvent(BaseMessageEvent.BASE_EVENT_TYPE_ONLOAD_DATA_FAILED);
    }

    public void onBack() {
        postEvent(BaseMessageEvent.BASE_EVENT_TYPE_ON_BACKPRESS);
    }

    public void finish() {
        postEvent(BaseMessageEvent.BASE_EVENT_TYPE_ON_BACKFINISH);
    }
}
