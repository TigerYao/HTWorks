package com.huatu.handheld_huatu.mvpview.arena;

import com.huatu.handheld_huatu.mvpmodel.arena.ArenaDetailBean;
import com.huatu.handheld_huatu.mvpview.BaseView;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.me.ShareInfoBean;

/**
 * Created by saiyuan on 2016/10/17.
 */
public interface ArenaExamMainView extends BaseView {
    void onSetPagerDatas(RealExamBeans beans);
    void onGetPractiseData(RealExamBeans.RealExamBean beans);
    void onCollectionCanceled(int questionId);
    void onCollectionSuccess(int questionId);
    void onGetShareContent(ShareInfoBean shareInfoBean);
    void onArenaInfoSuccess(ArenaDetailBean bean);
    void onGetPaperDataFailed(long errorCode);
    void onLoadDataFailed(int flag);
}
