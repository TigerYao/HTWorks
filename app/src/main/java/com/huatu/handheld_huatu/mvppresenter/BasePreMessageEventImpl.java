package com.huatu.handheld_huatu.mvppresenter;


import android.os.Bundle;

import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2016/12/16.
 */

public abstract class BasePreMessageEventImpl<T> {
    protected CompositeSubscription compositeSubscription;
    public T typeExObject;
    String toast="网络不稳定，请检查网络";
    public BasePreMessageEventImpl(CompositeSubscription cs,T typeexo) {
        compositeSubscription = cs;
        typeExObject=typeexo;
    }

    public CompositeSubscription getCompositeSubscription() {
        return compositeSubscription;
    }

    public void postEvent(int eventType) {
        BaseMessageEvent<T> event = new BaseMessageEvent<T>(eventType,typeExObject);
        EventBus.getDefault().post(event);
    }

    public void postEvent(int eventType, Object... objects) {
        BaseMessageEvent<T> event = new BaseMessageEvent<T>(
                eventType,typeExObject);
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

    public void showErrorMsg(int type) {
        CommonUtils.showToast(toast);
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

    public void onBack(){
        postEvent(BaseMessageEvent.BASE_EVENT_TYPE_ON_BACKPRESS);
    }

    public void finish(){
        postEvent(BaseMessageEvent.BASE_EVENT_TYPE_ON_BACKFINISH);
    }
}
