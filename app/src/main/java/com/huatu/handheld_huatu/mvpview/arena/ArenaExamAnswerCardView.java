package com.huatu.handheld_huatu.mvpview.arena;

import com.huatu.handheld_huatu.mvpview.BaseView;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;

/**
 * Created by saiyuan on 2016/10/21.
 */
public interface ArenaExamAnswerCardView extends BaseView {
    void onSubmitAnswerResult(RealExamBeans.RealExamBean bean);
    void onSubmitMokaoAnswerResult(boolean isReport, RealExamBeans.RealExamBean bean);
    void onSubmitMokaoAnswerError();

    class SimpleAnswerCardView implements ArenaExamAnswerCardView{

        @Override
        public void onSubmitAnswerResult(RealExamBeans.RealExamBean bean) {

        }

        @Override
        public void onSubmitMokaoAnswerResult(boolean isReport, RealExamBeans.RealExamBean bean) {

        }

        @Override
        public void onSubmitMokaoAnswerError() {

        }

        @Override
        public void showProgressBar() {

        }

        @Override
        public void dismissProgressBar() {

        }

        @Override
        public void onSetData(Object respData) {

        }

        @Override
        public void onLoadDataFailed() {

        }
    }
}
