package com.huatu.handheld_huatu.mvppresenter.arena;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.business.arena.utils.ArenaExamDataConverts;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamQuestionMainView;
import com.huatu.handheld_huatu.mvpmodel.exercise.AnswerCardBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2016/10/24.
 */
public class ArenaQuestionMainPresenterImpl {
    ArenaExamQuestionMainView mView;
    CompositeSubscription mCompositeSubscription;

    public ArenaQuestionMainPresenterImpl(CompositeSubscription cs, ArenaExamQuestionMainView view) {
        mCompositeSubscription = cs;
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mView = view;
    }

    /*
    提交答题卡
     */
    public void submitAnswerCard(final long practiceId, final RealExamBeans.RealExamBean realExamBean) {
        mView.showProgressBar();
        if (practiceId <= 0 || realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null) {
            LogUtils.e("submitAnswerCard direct return, params is invalid");
            return;
        }
        if(!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络,请检查网络再重新提交");
            mView.onLoadDataFailed(1);
            return;
        }
        JsonArray jsonArray = ArenaExamDataConverts.createAnswerCardJson(realExamBean.paper.questionBeanList);
        Observable<RealExamBeans> realExamBeansObservable = RetrofitManager.getInstance()
                .getService().submitAnswerCard(practiceId, jsonArray);
        mCompositeSubscription.add(realExamBeansObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RealExamBeans>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dismissProgressBar();
                        CommonUtils.showToast("提交失败，请您重新提交");
                        mView.onLoadDataFailed(3);
                    }

                    @Override
                    public void onNext(RealExamBeans examBean) {
                        mView.dismissProgressBar();
                        if (examBean != null && "1000000".equals(examBean.code) && examBean.data != null) {
                            RealExamBeans.RealExamBean resultBean = examBean.data;
                            realExamBean.corrects = resultBean.corrects;
                            realExamBean.times = resultBean.times;
                            ArenaExamDataConverts.dealExamBeanAnswers(realExamBean);
                            mView.onSubmitArenaAnswerCardResult(realExamBean);
                        } else {
                            CommonUtils.showToast("提交失败，请您重新提交, ErrorMessage: " + examBean.message);
                            mView.onLoadDataFailed(3);
                        }
                    }
                })
        );
    }

    /*
    练习保存答题卡
     */
    public void saveAnswerCard(final RealExamBeans.RealExamBean realExamBean) {
        if (realExamBean == null || realExamBean.paper == null
                || realExamBean.paper.questionBeanList == null) {
            LogUtils.e("submitAnswerCard direct return, params is invalid");
            return;
        }
        if(!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络,请检查网络再重新提交");
            mView.onLoadDataFailed(1);
            return;
        }
        mView.showProgressBar();
        JsonArray jsonArray = ArenaExamDataConverts.createAnswerCardJson(realExamBean.paper.questionBeanList);
        Observable<BaseResponseModel> realExamBeansObservable = RetrofitManager.getInstance()
                .getService().saveAnswerCard(realExamBean.id, jsonArray);
        mCompositeSubscription.add(realExamBeansObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dismissProgressBar();
                        CommonUtils.showToast("保存答案失败，请您重新提交");
                        mView.onLoadDataFailed(4);
                    }

                    @Override
                    public void onNext(BaseResponseModel model) {
                        mView.dismissProgressBar();
                        if (model != null && "1000000".equals(model.code)) {
                            mView.onSaveAnswerCardSucc();
                            CommonUtils.showToast("保存成功");
                        } else {
                            CommonUtils.showToast("保存答案失败，请您重新提交, ErrorMessage: " + model.message);
                            mView.onLoadDataFailed(4);
                        }
                    }
                })
        );
    }

    /*
    竞技答题单次提交答案
     */
    public void saveAnswerCardForArena(final long practiceId, final int position, final RealExamBeans.RealExamBean realExamBean) {
        if (practiceId <= 0 || realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null
                || position >= realExamBean.paper.questionBeanList.size()) {
            LogUtils.e("submitAnswerCard direct return, params is invalid");
            return;
        }
        if(!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络,请检查网络再重新提交");
            mView.onLoadDataFailed(1);
            return;
        }
        mView.showProgressBar();
        ArenaExamQuestionBean questionBean = realExamBean.paper.questionBeanList.get(position);
        List<ArenaExamQuestionBean> list = new ArrayList<>();
        list.add(questionBean);
        JsonArray jsonArray = ArenaExamDataConverts.createAnswerCardJson(list);
        Observable<BaseResponseModel> realExamBeansObservable = RetrofitManager.getInstance()
                .getService().saveAnswerCard(practiceId, jsonArray);
        mCompositeSubscription.add(realExamBeansObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dismissProgressBar();
                        CommonUtils.showToast("提交失败，请您重新提交");
                        mView.onLoadDataFailed(3);
                    }

                    @Override
                    public void onNext(BaseResponseModel examBean) {
                        mView.dismissProgressBar();
                        if (examBean != null && "1000000".equals(examBean.code)) {
                            mView.onSaveArenaAnswerCardSucc(position);
                        } else {
                            CommonUtils.showToast("提交失败，请您重新提交, ErrorMessage: " + examBean.message);
                            mView.onLoadDataFailed(3);
                        }
                    }
                })
        );
    }

    /*
    分步保存答题卡，每5道题自动保存
     */
    public void saveAnswerCardPartly(final RealExamBeans.RealExamBean realExamBean, final List<Integer> indexList) {
        if (realExamBean == null || realExamBean.paper == null
                || realExamBean.paper.questionBeanList == null) {
            LogUtils.e("submitAnswerCard direct return, params is invalid");
            return;
        }
        if(!NetUtil.isConnected()) {
            mView.onSaveAnswerCardPartlyFail(indexList);
            return;
        }
        List<AnswerCardBean> list = new ArrayList<>();
        if(indexList != null && indexList.size() > 0) {
            for (int index : indexList) {
                if(index <= realExamBean.paper.questionBeanList.size()) {
                    ArenaExamQuestionBean questionBean = realExamBean.paper.questionBeanList.get(index);
                    AnswerCardBean answerCardBean = ArenaExamDataConverts.createAnswerCardBean(questionBean);
                    if(answerCardBean != null) {
                        list.add(answerCardBean);
                    }
                }
            }
        }
        final JsonArray jsonArray = new Gson().toJsonTree(list,
                new TypeToken<List<AnswerCardBean>>() { }.getType()).getAsJsonArray();
        Observable<BaseResponseModel> realExamBeansObservable = RetrofitManager.getInstance()
                .getService().saveAnswerCard(realExamBean.id, jsonArray);
        mCompositeSubscription.add(realExamBeansObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.onSaveAnswerCardPartlyFail(indexList);
                    }

                    @Override
                    public void onNext(BaseResponseModel examBean) {
                        if (examBean != null && "1000000".equals(examBean.code)) {
                            mView.onSaveAnswerCardPartlySucc(indexList);
                        } else {
                            mView.onSaveAnswerCardPartlyFail(indexList);
                        }
                    }
                })
        );
    }

    public void submitMoKao(final RealExamBeans.RealExamBean realExamBean) {
        if (realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null) {
            LogUtils.e("getArenaDetailInfo direct return, realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null");
            return;
        }
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("网络错误，请检查您的网络");
        }
        mView.showProgressBar();
        JsonArray jsonArray = ArenaExamDataConverts.createAnswerCardJson(realExamBean.paper.questionBeanList);
        ServiceProvider.submitGufenAnswer(mCompositeSubscription, realExamBean.id, realExamBean.type,jsonArray,
                new NetResponse(){
                    @Override
                    public void onError(final Throwable e) {
                        mView.dismissProgressBar();
                        mView.onLoadDataFailed(3);
                        mView.onSubmitMokaoAnswerError();
                    }

                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        mView.dismissProgressBar();
                        if(model.data == null) {
                            mView.onSubmitMokaoAnswerResult(false, null);
                        } else {
                            RealExamBeans.RealExamBean resultBean = (RealExamBeans.RealExamBean) model.data;
                            resultBean.paper.questionBeanList = realExamBean.paper.questionBeanList;
                            resultBean.paper.wrongQuestionBeanList = realExamBean.paper.wrongQuestionBeanList;
                            ArenaExamDataConverts.dealExamBeanAnswers(resultBean);
                            mView.onSubmitMokaoAnswerResult(true, resultBean);
                        }
                    }
                });
    }


    /*
    答题卡疑问
     */
//    public void updatePracticeDoubts(final int position,final long practiceId, final int questionId, final int doubt) {
//        if (practiceId < 0 || questionId < 0) {
//            LogUtils.e("practiceId or questionId return, params is invalid");
//            return;
//        }
//        if(!NetUtil.isConnected()) {
//            CommonUtils.showToast("无网络,请检查网络再重新提交");
//            mView.onLoadDataFailed();
//            return;
//        }
//        Observable<BaseResponseModel<String>> practiceDoubtsObservable =null;
//        if(doubt==1){
//            practiceDoubtsObservable= RetrofitManager.getInstance()
//                    .getService().addPracticeDoubts(practiceId,questionId);
//        }else if(doubt==0){
//            practiceDoubtsObservable= RetrofitManager.getInstance()
//                    .getService().cancelPracticeDoubts(practiceId,questionId);
//        }else {
//            mView.onLoadDataFailed();
//            LogUtils.e("type  return, params is invalid");
//            return;
//        }
//        mView.showProgressBar();
//        mCompositeSubscription.add(practiceDoubtsObservable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<BaseResponseModel<String>>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                        mView.dismissProgressBar();
//                        CommonUtils.showToast("答题卡疑问操作失败，请您重新提交");
//                        mView.onLoadDataFailed();
//                    }
//
//                    @Override
//                    public void onNext(BaseResponseModel<String> brmodel) {
//                        mView.dismissProgressBar();
//                        if (brmodel != null &&  brmodel.code>0 && "1000000".equals(String.valueOf(brmodel.code))) {
//                            mView.onUpdatePracticeDoubtsSucc(position,practiceId,questionId,doubt);
//                        } else {
//                            CommonUtils.showToast(brmodel.message);
//                            mView.onLoadDataFailed();
//                        }
//                    }
//                })
//        );
//    }
}
