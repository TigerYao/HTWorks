package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;

import java.util.List;

/**
 * Created by cjx on 2018\8\1 0001.
 */

public interface OnPlaySelectListener {

    void onSelectChange(int id,int hasPlayTime);

    List<PurchasedCourseBean.Data> getCurrentCourseList();
}
