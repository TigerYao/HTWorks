package com.huatu.handheld_huatu.helper;

/**
 * Created by Administrator on 2019\12\2 0002.
 */

public abstract class Singleton<T> {
    private T mInstance;

    protected abstract T create();

    public final T get() {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create();
            }
            return mInstance;
        }
    }
}
