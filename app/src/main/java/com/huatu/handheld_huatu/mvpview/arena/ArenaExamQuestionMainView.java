package com.huatu.handheld_huatu.mvpview.arena;

import com.huatu.handheld_huatu.mvpview.BaseView;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;

import java.util.List;

/**
 * Created by saiyuan on 2016/10/25.
 */
public interface ArenaExamQuestionMainView extends BaseView {
    void onSaveAnswerCardSucc();
    void onSaveArenaAnswerCardSucc(int index);
    void onSubmitArenaAnswerCardResult(RealExamBeans.RealExamBean realExamBean);
    void onSaveAnswerCardPartlySucc(List<Integer> list);
    void onSaveAnswerCardPartlyFail(List<Integer> list);
    void onSubmitMokaoAnswerResult(boolean isReport, RealExamBeans.RealExamBean bean);
    void onSubmitMokaoAnswerError();
    void onLoadDataFailed(int flag);
//    void onUpdatePracticeDoubtsSucc(int position,long practiceId, int questionId, int doubt);
}
