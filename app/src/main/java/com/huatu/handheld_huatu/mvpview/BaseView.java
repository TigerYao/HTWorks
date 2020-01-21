package com.huatu.handheld_huatu.mvpview;

/**
 * Created by saiyuan on 2016/10/13.
 */
public interface BaseView {
    void showProgressBar();
    void dismissProgressBar();
    void onSetData(Object respData);
    void onLoadDataFailed();
}
