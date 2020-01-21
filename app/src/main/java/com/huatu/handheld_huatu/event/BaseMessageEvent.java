package com.huatu.handheld_huatu.event;

import android.os.Bundle;

/**
 */
public class BaseMessageEvent<T> {
    public String tag;
    public int type;
    public Bundle extraBundle;
    public T typeExObject;
    // index 5

    private static final int BASE_EVENT_TYPE = 1;
    public static final int BASE_EVENT_TYPE_SHOW_PROGRESS_BAR = BASE_EVENT_TYPE + 1;
    public static final int BASE_EVENT_TYPE_DISMISS_PROGRESS_BAR = BASE_EVENT_TYPE_SHOW_PROGRESS_BAR + 1;
    public static final int BASE_EVENT_TYPE_ONLOAD_DATA_FAILED = BASE_EVENT_TYPE_DISMISS_PROGRESS_BAR + 1;
    public static final int BASE_EVENT_TYPE_ON_BACKFINISH = BASE_EVENT_TYPE_ONLOAD_DATA_FAILED + 1;
    public static final int BASE_EVENT_TYPE_ON_BACKPRESS = BASE_EVENT_TYPE_ON_BACKFINISH + 1;
    public static final int BASE_EVENT_TYPE_ON_PAYED_ALL = BASE_EVENT_TYPE_ON_BACKPRESS + BASE_EVENT_TYPE;
    public BaseMessageEvent(int type, T typeeo) {
        this.type = type;
        this.typeExObject = typeeo;
    }

    public BaseMessageEvent() {

    }

    public BaseMessageEvent(int type) {
        this.type = type;
    }
}
