package com.huatu.handheld_huatu.mvppresenter.arena;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.ArenaExamDataConverts;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.AnswerCardBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.AnswerCardSaveBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.ModuleBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamAnswerCardView;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2016/10/21.
 * 答题卡页面Presenter
 */
public class ArenaExamAnswerCardPresenterImpl {
    private ArenaExamAnswerCardView mView;
    private CompositeSubscription mCompositeSubscription = null;

    public ArenaExamAnswerCardPresenterImpl(CompositeSubscription cs, ArenaExamAnswerCardView view) {
        mCompositeSubscription = cs;
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mView = view;
    }

    // 专项模考，精准估分交卷
    public void submitMoKao(final RealExamBeans.RealExamBean realExamBean) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("网络错误，请检查您的网络");
        }
        mView.showProgressBar();
        if (realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null) {
            mView.dismissProgressBar();
            LogUtils.e("getArenaDetailInfo direct return, realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null");
            return;
        }
        JsonArray jsonArray = ArenaExamDataConverts.createAnswerCardJson(realExamBean.paper.questionBeanList);
        ServiceProvider.submitGufenAnswer(mCompositeSubscription, realExamBean.id, realExamBean.type, jsonArray,
                new NetResponse() {
                    @Override
                    public void onError(final Throwable e) {
                        if (e instanceof ApiException) {
                            Log.i("TAG", "------模考答题卡提交失败---- " + ((ApiException) e).getErrorMsg());
                        }
                        mView.dismissProgressBar();
                        mView.onLoadDataFailed();
                        mView.onSubmitMokaoAnswerError();
                        if (e instanceof ApiException) {
                            ToastUtils.showMessage(((ApiException) e).getErrorMsg());
                        }
                    }

                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        mView.dismissProgressBar();
                        if (model.data == null) {
                            mView.onSubmitMokaoAnswerResult(false, null);
                        } else {
                            RealExamBeans.RealExamBean resultBean = (RealExamBeans.RealExamBean) model.data;
                            resultBean.paper.questionBeanList = realExamBean.paper.questionBeanList;
                            resultBean.paper.wrongQuestionBeanList = realExamBean.paper.wrongQuestionBeanList;

                            // 过滤缺的题-------------------------------
                            // 因为有题目会不存在，所以realExamBean.paper.questions.size()可能会大于返回的试题数
                            // 所以要计算模块module中量的问题
                            List<ModuleBean> modules = resultBean.paper.modules;
                            List<ArenaExamQuestionBean> questionBeanList = resultBean.paper.questionBeanList;

                            List<Integer> questionsIds = resultBean.paper.questions;
                            int realQuestionSize = questionBeanList.size();

                            if (questionsIds.size() > realQuestionSize) {
                                // 遍历请求试题的id，如果那个不被返回的试题包含，就需要处理
                                List<Integer> modulesE = new ArrayList<>();                 // 累加各个模块
                                for (int i = 0; i < modules.size(); i++) {
                                    int x = 0;
                                    for (int j = 0; j <= i; j++) {
                                        x += modules.get(j).qcount;
                                    }
                                    modulesE.add(x);
                                }
                                HashSet<Integer> exercises = new HashSet<>();                // 返回问题id
                                for (ArenaExamQuestionBean arenaExamQuestionBean : questionBeanList) {
                                    exercises.add(arenaExamQuestionBean.id);
                                }
                                for (int i = questionsIds.size() - 1; i >= 0; i--) {
                                    if (!exercises.contains(questionsIds.get(i))) {
                                        // 修改module的数量
                                        for (int j = 0; j < modulesE.size(); j++) {
                                            if ((j - 1 < 0 ? 0 : modulesE.get(j - 1)) <= i && i < modulesE.get(j)) {
                                                modules.get(j).qcount--;
                                                break;
                                            }
                                        }
                                        // 检查答案的情况
                                        if (resultBean.answers != null && resultBean.answers.size() > realQuestionSize && resultBean.answers.size() > i) {
                                            resultBean.answers.remove(i);
                                        }
                                        // 检查是否答对
                                        if (resultBean.corrects != null && resultBean.corrects.size() > realQuestionSize && resultBean.corrects.size() > i) {
                                            resultBean.corrects.remove(i);
                                        }
                                        // 检查作答时间
                                        if (resultBean.times != null && resultBean.times.size() > realQuestionSize && resultBean.times.size() > i) {
                                            resultBean.times.remove(i);
                                        }
                                        // 答题卡疑问
                                        if (resultBean.doubts != null && resultBean.doubts.size() > realQuestionSize && resultBean.doubts.size() > i) {
                                            resultBean.doubts.remove(i);
                                        }
                                    }
                                }
                            }
                            // 过滤缺的题-------------------------------

                            ArenaExamDataConverts.dealExamBeanAnswers(resultBean);
                            mView.onSubmitMokaoAnswerResult(true, resultBean);
                        }
                    }
                });
    }

    // 小模考的交卷、其他交卷
    public void submitAnswerCard(final RealExamBeans.RealExamBean realExamBean) {
        mView.showProgressBar();
        if (realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null) {
            mView.dismissProgressBar();
            LogUtils.e("getArenaDetailInfo direct return, realExamBean == null " +
                    "|| realExamBean.paper == null || realExamBean.paper.questionBeanList == null");
            return;
        }
        JsonArray jsonArray = ArenaExamDataConverts.createAnswerCardJson(realExamBean.paper.questionBeanList);

        Observable<RealExamBeans> realExamBeansObservable = null;
        if (realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH) {                  // 小模考的交卷
            realExamBeansObservable = RetrofitManager.getInstance().getService().submitSMatchAnswerCard(realExamBean.id, jsonArray);
        } else if (realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_COURSE_EXERICE) {        // 课后作业 - 提交答题卡（已完成）add by cjx
            realExamBeansObservable = RetrofitManager.getInstance().getService().submitAfterClassAnswerCard(realExamBean.id, jsonArray);
        } else {                                                                                    // 其他交卷
            realExamBeansObservable = RetrofitManager.getInstance().getService().submitAnswerCard(realExamBean.id, jsonArray);
        }
        mCompositeSubscription.add(realExamBeansObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RealExamBeans>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof ApiException) {
                            Log.i("TAG", "------答题卡提交失败---- " + ((ApiException) e).getErrorMsg());
                        }
                        mView.dismissProgressBar();
                        mView.onLoadDataFailed();
                        CommonUtils.showToast("提交失败，请您重新提交");
                        EventBusUtil.sendMessage(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_pastPaper, new SimulationContestMessageEvent());
                    }

                    @Override
                    public void onNext(RealExamBeans examBean) {
                        mView.dismissProgressBar();
                        if (examBean != null && "1000000".equals(examBean.code) && examBean.data != null && examBean.data.paper != null) {
                            RealExamBeans.RealExamBean resultBean = examBean.data;
                            resultBean.paper.questionBeanList = realExamBean.paper.questionBeanList;
                            resultBean.paper.wrongQuestionBeanList = realExamBean.paper.wrongQuestionBeanList;

                            // 过滤缺的题-------------------------------
                            // 因为有题目会不存在，所以realExamBean.paper.questions.size()可能会大于返回的试题数
                            // 所以要计算模块module中量的问题
                            List<ModuleBean> modules = resultBean.paper.modules;
                            List<ArenaExamQuestionBean> questionBeanList = resultBean.paper.questionBeanList;

                            List<Integer> questionsIds = resultBean.paper.questions;
                            int realQuestionSize = questionBeanList.size();

                            if (questionsIds.size() > realQuestionSize) {
                                // 遍历请求试题的id，如果那个不被返回的试题包含，就需要处理
                                List<Integer> modulesE = new ArrayList<>();                 // 累加各个模块
                                for (int i = 0; i < modules.size(); i++) {
                                    int x = 0;
                                    for (int j = 0; j <= i; j++) {
                                        x += modules.get(j).qcount;
                                    }
                                    modulesE.add(x);
                                }
                                HashSet<Integer> exercises = new HashSet<>();                // 返回问题id
                                for (ArenaExamQuestionBean arenaExamQuestionBean : questionBeanList) {
                                    exercises.add(arenaExamQuestionBean.id);
                                }
                                for (int i = questionsIds.size() - 1; i >= 0; i--) {
                                    if (!exercises.contains(questionsIds.get(i))) {
                                        // 修改module的数量
                                        for (int j = 0; j < modulesE.size(); j++) {
                                            if ((j - 1 < 0 ? 0 : modulesE.get(j - 1)) <= i && i < modulesE.get(j)) {
                                                modules.get(j).qcount--;
                                                break;
                                            }
                                        }
                                        // 检查答案的情况
                                        if (resultBean.answers != null && resultBean.answers.size() > realQuestionSize && resultBean.answers.size() > i) {
                                            resultBean.answers.remove(i);
                                        }
                                        // 检查是否答对
                                        if (resultBean.corrects != null && resultBean.corrects.size() > realQuestionSize && resultBean.corrects.size() > i) {
                                            resultBean.corrects.remove(i);
                                        }
                                        // 检查作答时间
                                        if (resultBean.times != null && resultBean.times.size() > realQuestionSize && resultBean.times.size() > i) {
                                            resultBean.times.remove(i);
                                        }
                                        // 答题卡疑问
                                        if (resultBean.doubts != null && resultBean.doubts.size() > realQuestionSize && resultBean.doubts.size() > i) {
                                            resultBean.doubts.remove(i);
                                        }
                                    }
                                }
                            }
                            // 过滤缺的题-------------------------------

                            ArenaExamDataConverts.dealExamBeanAnswers(resultBean);
                            mView.onSubmitAnswerResult(resultBean);
                        } else {
                            mView.onLoadDataFailed();
                            CommonUtils.showToast("提交失败，" + examBean.message);
                        }
                        EventBusUtil.sendMessage(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_pastPaper, new SimulationContestMessageEvent());
                    }
                })
        );
    }

    /**
     * 阶段性测试提交答案
     */
    public void submitStageAnswerCard(long syllabusId, final RealExamBeans.RealExamBean realExamBean) {
        mView.showProgressBar();
        if (realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null) {
            mView.dismissProgressBar();
            return;
        }
        JsonArray jsonArray = ArenaExamDataConverts.createAnswerCardJson(realExamBean.paper.questionBeanList);

        Observable<BaseResponseModel> realExamBeansObservable = RetrofitManager.getInstance().getService().submitStageAnswerCard(realExamBean.id, syllabusId, jsonArray);
        mCompositeSubscription.add(realExamBeansObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof ApiException) {
                            Log.i("TAG", "------答题卡提交失败---- " + ((ApiException) e).getErrorMsg());
                        }
                        mView.dismissProgressBar();
                        mView.onLoadDataFailed();
                        CommonUtils.showToast("提交失败，请您重新提交");
                    }

                    @Override
                    public void onNext(BaseResponseModel model) {
                        mView.dismissProgressBar();
                        if (model != null && 1000000 == model.code) {
                            mView.onSubmitAnswerResult(null);
                        } else {
                            mView.onLoadDataFailed();
                            CommonUtils.showToast("提交失败，" + (model != null ? model.message : ""));
                        }
                        EventBusUtil.sendMessage(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_pastPaper, new SimulationContestMessageEvent());
                    }
                })
        );
    }

    /**
     * 新模考大赛交卷
     */
    public void submitScCard(final RealExamBeans.RealExamBean realExamBean) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("网络错误，请检查您的网络");
        }
        if (realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null) {
            return;
        }
        mView.showProgressBar();
        List<AnswerCardSaveBean> list = new ArrayList<>();
        for (ArenaExamQuestionBean bean : realExamBean.paper.questionBeanList) {
            AnswerCardSaveBean commitBean = new AnswerCardSaveBean();
            if (bean.usedTime == 0) {
                bean.usedTime = 1;
            }
            commitBean.expireTime = bean.usedTime;
            commitBean.answer = bean.userAnswer;
            commitBean.questionId = bean.id;
            commitBean.doubt = bean.doubt;
            list.add(commitBean);
        }
        JsonArray jsonArray = new Gson().toJsonTree(list, new TypeToken<List<AnswerCardBean>>() {
        }.getType()).getAsJsonArray();

        // 调用接口，服务器打印模考内容日志matchErrorPath
//        if (!StringUtils.isEmpty(realExamBean.matchErrorPath)) {
//            String terminal;
//            TelephonyManager tm = (TelephonyManager) UniApplicationContext.getContext().getSystemService(Context.TELEPHONY_SERVICE);
//            if (tm != null && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
//                terminal = "4";
//            } else {
//                terminal = "1";
//            }
//            try {
//                OkHttpUtils.get()
//                        .url(realExamBean.matchErrorPath)
//                        .addParams("id", realExamBean.id + "")
//                        .addParams("token", SpUtils.getToken())
//                        .addParams("cv", AppUtils.getVersionName())
//                        .addParams("terminal", terminal)
//                        .addParams("answers", jsonArray.toString())
//                        .build()
//                        .execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        ServiceProvider.submitScCard(mCompositeSubscription, realExamBean.id, jsonArray,
                new NetResponse() {
                    @Override
                    public void onError(final Throwable e) {
                        mView.dismissProgressBar();
                        if (e instanceof ApiException) {
                            if (((ApiException) e).getErrorCode() == 5000000) {
                                mView.onSubmitMokaoAnswerResult(false, null);
                                return;
                            }
                        }
                        mView.onLoadDataFailed();
                        mView.onSubmitMokaoAnswerError();
                    }

                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        // 新模考大赛，走onSuccess就是报名成功，直接返回就行了
                        mView.onSubmitMokaoAnswerResult(false, null);
                        // code == 5000000 是交过卷了
                    }
                });
    }
}
