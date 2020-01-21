package com.huatu.handheld_huatu.base;

import com.huatu.handheld_huatu.mvpview.BaseView;

import java.util.List;

/**
 * Created by saiyuan on 2016/11/24.
 */

public interface BaseListResponseView<T> extends BaseView {
    public void onSuccess(List<T> list, boolean isRefresh);
}
