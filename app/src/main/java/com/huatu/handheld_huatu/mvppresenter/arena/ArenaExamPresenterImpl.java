package com.huatu.handheld_huatu.mvppresenter.arena;

import android.os.Bundle;

import com.google.gson.JsonObject;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.ArenaExamDataConverts;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeans;
import com.huatu.handheld_huatu.mvpmodel.exercise.ModuleBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.me.ShareInfoBean;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamMainView;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2016/10/14.
 */
public class ArenaExamPresenterImpl {
    private ArenaExamMainView mView;
    private CompositeSubscription mCompositeSubscription = null;

    public ArenaExamPresenterImpl(CompositeSubscription cs, ArenaExamMainView view) {
        mCompositeSubscription = cs;
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mView = view;
    }

    /**
     * 把List的题号，改成逗号隔开的题号
     */
    private String getExerciseIds(List<Integer> ids) {
        if (ids == null) {
            LogUtils.e("getExerciseIds ids is null");
        }
        StringBuilder sb = new StringBuilder();
        for (Integer id : ids) {
            sb.append(id).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public void getPractiseDetails(Bundle extraArgs) {
        long practiceId = extraArgs.getLong("practice_id");
        if (practiceId <= 0) {
            LogUtils.e("ArenaExamPresenterImpl.getPractiseDetails:  practiceId <= 0");
            return;
        } else {
            LogUtils.d("ArenaExamPresenterImpl.getPractiseDetails:  practiceId " + practiceId);
        }
        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络,请检查网络");
            mView.onLoadDataFailed(1);
            return;
        }
        mView.showProgressBar();
        mCompositeSubscription.add(RetrofitManager.getInstance().getService().getPractiseDetails(practiceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RealExamBeans>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dismissProgressBar();
                        mView.onLoadDataFailed(2);
                    }

                    @Override
                    public void onNext(RealExamBeans practiceDetailsBeans) {
                        mView.dismissProgressBar();
                        if (practiceDetailsBeans != null && !"1000000".equals(practiceDetailsBeans.code)) {
                            CommonUtils.showToast("获取练习信息失败，" + practiceDetailsBeans.message);
                            LogUtils.e("getPractiseDetails: equals(practiceDetailsBeans.code is not 1000000");
                            mView.onLoadDataFailed(2);
                            return;
                        }
                        if (practiceDetailsBeans == null || practiceDetailsBeans.data == null
                                || practiceDetailsBeans.data.paper == null) {
                            CommonUtils.showToast("获取练习信息失败，请您重试");
                            LogUtils.e("getPractiseDetails: practiceDetailsBeans is invalid");
                            mView.onLoadDataFailed(2);
                            return;
                        }
                        RealExamBeans.RealExamBean resultBean = practiceDetailsBeans.data;
                        LogUtils.i("getPractiseDetails success, continue to getPractice");
                        getPractice(resultBean);
                    }
                }));
    }

    /**
     * 根据考卷信息获取
     */
    public void getPractice(final RealExamBeans.RealExamBean realExamBean) {
        final List<Integer> questions = realExamBean.paper.questions;
        if (questions == null || questions.size() == 0) {
            mView.onLoadDataFailed(4);
            return;
        }
        mView.showProgressBar();
        String exerciseIds = getExerciseIds(questions);                                                                  // 把List的题号，改成逗号隔开的题号
        Observable<ExerciseBeans> questionsObservable = RetrofitManager.getInstance().getService().getExercises(exerciseIds);               // 根据逗号分隔开的id，获取试题
        Observable<JsonObject> collectionObservable = RetrofitManager.getInstance().getService().getExerciseCollectStatus(exerciseIds);     // 查询试题是否被收藏过
        Observable<RealExamBeans.RealExamBean> combineObv = Observable.zip(questionsObservable, collectionObservable,
                new Func2<ExerciseBeans, JsonObject, RealExamBeans.RealExamBean>() {
                    @Override
                    public RealExamBeans.RealExamBean call(ExerciseBeans exerciseBeans, JsonObject jsonObject) {
                        RealExamBeans.RealExamBean resultBean = realExamBean;

                        // 给每一道题添加类型
                        List<ModuleBean> modules = null;
                        if (resultBean.paper != null && resultBean.paper.modules != null && resultBean.paper.modules.size() > 0) {
                            modules = resultBean.paper.modules;
                        }

                        List<Integer> collectList = ArenaExamDataConverts.parsePractiseCollectionList(jsonObject);
                        if (exerciseBeans != null && exerciseBeans.data != null) {
                            // 因为有题目会不存在，所以realExamBean.paper.questions.size()可能会大于返回的试题数
                            // 所以要计算模块module中量的问题
                            int realQuestionSize = exerciseBeans.data.size();
                            if (questions.size() > realQuestionSize) {
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
                                for (ExerciseBeans.ExerciseBean datum : exerciseBeans.data) {
                                    exercises.add(datum.id);
                                }
                                for (int i = questions.size() - 1; i >= 0; i--) {
                                    if (!exercises.contains(questions.get(i))) {
                                        // 修改module的数量
                                        for (int j = 0; j < modulesE.size(); j++) {
                                            if ((j - 1 < 0 ? 0 : modulesE.get(j - 1)) <= i && i < modulesE.get(j)) {
                                                modules.get(j).qcount--;
                                                break;
                                            }
                                        }
                                        // 检查答案的情况
                                        if (realExamBean.answers != null && realExamBean.answers.size() > realQuestionSize && realExamBean.answers.size() > i) {
                                            realExamBean.answers.remove(i);
                                        }
                                        // 检查是否答对
                                        if (realExamBean.corrects != null && realExamBean.corrects.size() > realQuestionSize && realExamBean.corrects.size() > i) {
                                            realExamBean.corrects.remove(i);
                                        }
                                        // 检查作答时间
                                        if (realExamBean.times != null && realExamBean.times.size() > realQuestionSize && realExamBean.times.size() > i) {
                                            realExamBean.times.remove(i);
                                        }
                                        // 答题卡疑问
                                        if (realExamBean.doubts != null && realExamBean.doubts.size() > realQuestionSize && realExamBean.doubts.size() > i) {
                                            realExamBean.doubts.remove(i);
                                        }
                                    }
                                }
                            }
                            // ---------- 到这里做的处理，都是以防服务端返回丢题
                            resultBean.paper.questionBeanList = new ArrayList<>();
                            for (int i = 0; i < realQuestionSize; i++) {
                                ArenaExamQuestionBean bean = ArenaExamDataConverts.convertFromExerciseBean(exerciseBeans.data.get(i));
                                bean.name = realExamBean.name;

                                // 答题卡疑问  1:用户对该试题有疑问,0:没有疑问
                                if (resultBean.doubts != null && i < resultBean.doubts.size()) {
                                    bean.doubt = resultBean.doubts.get(i);
                                }
                                resultBean.paper.questionBeanList.add(bean);
                            }
                        }
                        ArenaExamDataConverts.processExamCollection(resultBean.paper.questionBeanList, collectList);
                        ArenaExamDataConverts.dealExamBeanAnswers(resultBean);
                        LogUtils.i("getPractice finish zip");
                        return resultBean;
                    }
                });
        dealPractices(combineObv);
    }

    private void dealPractices(Observable<RealExamBeans.RealExamBean> combineObv) {
        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络,请检查网络");
            mView.onLoadDataFailed(1);
            return;
        }
        mCompositeSubscription.add(combineObv.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RealExamBeans.RealExamBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dismissProgressBar();
                        mView.onLoadDataFailed(2);
                        CommonUtils.showToast("获取练习信息失败，请您重试");
                    }

                    @Override
                    public void onNext(RealExamBeans.RealExamBean examBean) {
                        mView.dismissProgressBar();
                        if (examBean == null || examBean.paper == null || examBean.paper.questionBeanList == null) {
                            CommonUtils.showToast("获取练习信息失败，请您重试");
                            LogUtils.e("getPractice: examBean is invalid");
                            mView.onLoadDataFailed(2);
                            return;
                        }
                        LogUtils.i("getPractice success, refresh UI");
                        mView.onGetPractiseData(examBean);
                    }
                })
        );
    }

    /*
    type: 0:练习 1:题目 2:报告
     */
    public void getShareContents(long practiceId, int type, int practiceType) {
        if (practiceId == 0) {
            return;
        } else {
            LogUtils.d("ArenaExamPresenterImpl.getShareContents:  practiceId " + practiceId);
        }
        mView.showProgressBar();
        ServiceProvider.getShareInfo(mCompositeSubscription, type, practiceId, practiceType, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                mView.dismissProgressBar();
                CommonUtils.showToast("获取分享数据失败");
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mView.dismissProgressBar();
                mView.onGetShareContent((ShareInfoBean) model.data);
            }
        });
    }

}
