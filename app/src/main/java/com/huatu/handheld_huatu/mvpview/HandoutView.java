package com.huatu.handheld_huatu.mvpview;

import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;

import java.util.List;

public interface HandoutView {
    void showProgressBar();
    void dismissProgressBar();
    void onSetData(List<HandoutBean.Course> handoutBeanList);
    void onLoadDataFailed(int type);
}
