package com.huatu.handheld_huatu.mvpview;

import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipNewBean;
import com.huatu.handheld_huatu.datacache.model.HomeIconBean;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.mvpmodel.HomeAdvBean;
import com.huatu.handheld_huatu.mvpmodel.HomeTreeBeanNew;
import com.huatu.handheld_huatu.mvpmodel.area.ExamAreaVersion;
import com.huatu.handheld_huatu.mvpmodel.special.DailySpecialBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2017/1/17.
 */

public interface HomeView extends BaseView {
    void updateAdvertise(BaseListResponseModel<AdvertiseConfig> list);

    void updateTreePoint(BaseListResponseModel<HomeTreeBeanNew> list);

    void updateNewTips(BaseListResponseModel<TipNewBean> data);

    void updateTreePointById(int parentId, BaseListResponseModel<HomeTreeBeanNew> list);

    void getTreePointFail();

    void dispatchDaily(DailySpecialBean dailySpecialBean);

    void updateHomeAdvList(HomeAdvBean homeAdvBean);

    void refreshRealExamVersion(BaseResponseModel<ExamAreaVersion> realExamVersion);

    void updateHomeIcons(ArrayList<HomeIconBean> icons);
}
