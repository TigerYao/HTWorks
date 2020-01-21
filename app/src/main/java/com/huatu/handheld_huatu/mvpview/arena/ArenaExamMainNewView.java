package com.huatu.handheld_huatu.mvpview.arena;

import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpview.BaseView;

import java.util.List;

/**
 * Created by saiyuan on 2016/10/17.
 */
public interface ArenaExamMainNewView extends BaseView {

    // 得到试卷信息
    void onGetPractiseData(RealExamBeans.RealExamBean beans, List<ArenaExamQuestionBean> questionBeans);
    // 返回退出，保存答案成功
    void onSavedAnswerCardSuccess(int saveType);
    // 获取数据失败
    void onLoadDataFailed(int flag);
}
