package com.huatu.handheld_huatu.view;

/**
 * Created by saiyuan on 2016/10/14.
 */
public interface MultiItemTypeSupport<T> {
    int getLayoutId(int position, T t);

    int getViewTypeCount();

    int getItemViewType(int position, T t);
}

