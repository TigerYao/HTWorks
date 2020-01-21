package com.huatu.handheld_huatu.mvppresenter.arena;

import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.ArenaExamDataConverts;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.mvpmodel.BaseResponse;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.AnswerCardBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.AnswerCardSaveBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeans;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeansNew;
import com.huatu.handheld_huatu.mvpmodel.exercise.ModuleBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.PaperBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.exercise.TimeBean;
import com.huatu.handheld_huatu.mvpview.arena.ArenaExamMainNewView;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2016/10/14.
 */
public class ArenaExamPresenterNewImpl {

    private ArenaExamMainNewView mView;
    private CompositeSubscription mCompositeSubscription = null;

    public ArenaExamPresenterNewImpl(CompositeSubscription cs, ArenaExamMainNewView view) {
        mCompositeSubscription = cs;
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mView = view;
    }

    private int requestType = -1;                                       // 请求类型
    private long paperId = -1;                                          // 试卷id

    private RealExamBeans.RealExamBean realExamBean;                    // 答题卡
    private List<ArenaExamQuestionBean> questionBeans;                  // 问题内容

    private List<ArenaExamQuestionBean> collectionQuestionListPre;      // 收藏，预加载的题

    private int showIndex = -1;                                         // 显示那一道题
    private boolean isContinueAnswer;                                   // 是否是继续答题

    public void getData(int requestType, Bundle extraArgs) {

        this.requestType = requestType;
        isContinueAnswer = extraArgs.getBoolean("continue_answer", false);
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST) {      // 如果是小模考/阶段性测试，创建答题卡和继续做题走同一个接口，以防万一 所以 isContinueAnswer 手动置为 false
            isContinueAnswer = false;
        }


        // 分类请求、得到对应的数据----------------------------------------------------------------

        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST) {                     // 新模考大赛
            paperId = extraArgs.getLong("practice_id");
            getAllData(paperId);
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {                       // 全部解析
            realExamBean = ArenaDataCache.getInstance().realExamBean;
            showIndex = extraArgs.getInt("showIndex", -1);
            // 这种情况下是查看答题记录里已交卷的 专项练习 错题练习 的背题模式
            if (realExamBean != null && realExamBean.paper != null && (realExamBean.paper.questionBeanList == null || realExamBean.paper.questionBeanList.size() == 0)) {
                getPractice(realExamBean);
                return;
            }
            if (realExamBean == null || realExamBean.paper == null) {
                ToastUtils.showShort("试题信息错误");
                mView.onLoadDataFailed(2);
                return;
            }
            setDataToView();
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG) {                     // 错题解析
            realExamBean = ArenaDataCache.getInstance().realExamBean;
            showIndex = extraArgs.getInt("showIndex", -1);
            if (realExamBean == null || realExamBean.paper == null || realExamBean.paper.wrongQuestionBeanList == null || realExamBean.paper.wrongQuestionBeanList.size() == 0) {
                ToastUtils.showShort("试题信息错误");
                mView.onLoadDataFailed(2);
                return;
            }
            setDataToView();
        } else if (requestType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {                        // 各种情况的获取试卷信息，(背题模式已经在之前处理，且一定要先处理了，所以这里不用排除了)
            if (!isContinueAnswer) {
                getPaperData(requestType, extraArgs);                                                       // 做新题
            } else {
                getPractiseDetails(extraArgs);                                                              // 继续做题
            }
        } else if (extraArgs.containsKey("exerciseIdList")) {                                           // 根据试题 ids 用逗号隔开的id，获取试题（查看收藏（分页加载），单题解析，老师预览）
            String ids = extraArgs.getString("exerciseIdList");
            getPracticeInfoList(ids, requestType);
        }
    }

    /**
     * 答题卡 & 试卷 信息处理过之后，返给做题页面
     */
    private void setDataToView() {
        if (realExamBean == null) {
            return;
        }
        if (requestType != -1) {
            realExamBean.type = requestType;                // 请求类型
        }
        if (realExamBean.paper != null) {
            questionBeans = getQuestionsFromExamBean(requestType, realExamBean);
        }

        // ***********************针对做题类型、错题、背题等...进行的数据处理

        // 阶段考试给试卷添加类型（因为接口没有返回试题类型）
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST) {
            if (realExamBean.paper != null) {                               // 没有返回考试类型
                realExamBean.paper.type = ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST;
            }
            realExamBean.id = realExamBean.practiceId;                      // 答题卡Id字符不同
            realExamBean.remainingTime = realExamBean.remainTime;           // 剩余时间字段不同
        }

        // 每道题添加类型
        for (int i = 0; i < questionBeans.size(); i++) {
            ArenaExamQuestionBean arenaExamQuestionBean = questionBeans.get(i);

            // 把试卷的名称赋给单题内
            arenaExamQuestionBean.name = realExamBean.name;
            if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {
                if (arenaExamQuestionBean.userAnswer == 0) {        // 未做的把单题类型设置成 ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE
                    arenaExamQuestionBean.seeType = ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE;
                } else {                                            // 未做的把单题类型设置成 ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_AFTER
                    arenaExamQuestionBean.seeType = ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_AFTER;
                }
            } else {                                        // 任何模式都要把类型赋给单个题
                arenaExamQuestionBean.seeType = requestType;
            }
        }

        if (realExamBean.paper != null && realExamBean.paper.questionBeanList != null
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL                      // 全部解析不需要
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG                    // 错题解析不需要
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE                 // 收藏解析不需要
        ) {
            // 找到没做的那道题，定位
            List<ArenaExamQuestionBean> questionBeanList = realExamBean.paper.questionBeanList;
            for (int i = 0; i < questionBeanList.size(); i++) {
                if (questionBeanList.get(i).type != 105 && questionBeanList.get(i).userAnswer == 0) {
                    realExamBean.lastIndex = i;
                    break;
                }
            }
        } else if (showIndex > -1) {                               // 要显示哪个题
            realExamBean.lastIndex = showIndex;
            showIndex = -1;
        }

        mView.onGetPractiseData(realExamBean, questionBeans);
        questionBeans = null;
    }

    /**
     * 如果是收藏解析模式，检查是否有预加载的试题，进行加载更多
     */
    public void checkAndRefreshCollectionPre() {
        if (collectionQuestionListPre != null) {
            if (realExamBean != null && requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE
                    && realExamBean.paper != null && realExamBean.paper.questionBeanList != null) {
                realExamBean.paper.questionBeanList.addAll(collectionQuestionListPre);
                collectionQuestionListPre = null;
                setDataToView();
            }
        }
    }

    /**
     * 根据做题类型，从paper中获取对应的试卷信息
     *
     * @param requestType  做题类型
     * @param realExamBean 答题卡 & 试卷
     * @return 试题列表
     */
    public static List<ArenaExamQuestionBean> getQuestionsFromExamBean(int requestType, RealExamBeans.RealExamBean realExamBean) {
        if (realExamBean == null || realExamBean.paper == null) {
            return null;
        }
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG) {            // 错题解析
            return realExamBean.paper.wrongQuestionBeanList;
        }
        return realExamBean.paper.questionBeanList;                 // 其他
    }

    /**
     * 根据试卷Id，获取试题信息，新做题逻辑（现在只有新模考大赛）
     * 然后再获取 答题卡、收藏信息
     * 101、获取试卷
     */
    private void getAllData(final long paperId) {
        if (checkNet()) return;
        mView.showProgressBar();
        // 获取试卷信息
        Observable<ExerciseBeansNew> exerciseBeansNewObservable = RetrofitManager.getInstance().getService().getPaper(paperId);
        mCompositeSubscription.add(exerciseBeansNewObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ExerciseBeansNew>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dismissProgressBar();
                        mView.onLoadDataFailed(1);
                    }

                    @Override
                    public void onNext(ExerciseBeansNew examBean) {
                        if (examBean != null && examBean.data != null && examBean.data.size() > 0) {
                            getAnswerCardAndCollection(paperId, examBean.data);
                        } else {
                            mView.dismissProgressBar();
                            mView.onLoadDataFailed(2);
                        }
                    }
                })
        );
    }

    /**
     * 获取收藏信息、答题卡
     * 102、获取答题卡，收藏信息 组合数据返回
     */
    private void getAnswerCardAndCollection(long paperId, final List<ExerciseBeansNew.ExerciseBean> exerciseBeans) {
        StringBuilder exerciseIds = new StringBuilder();
        for (ExerciseBeansNew.ExerciseBean datum : exerciseBeans) {
            if (datum == null) continue;
            exerciseIds.append(String.valueOf(datum.id)).append(",");
        }
        // 组合试题Id，为了获取是否收藏过
        exerciseIds = new StringBuilder(exerciseIds.substring(0, exerciseIds.length() - 1));
        // 查询试题是否被收藏过
        Observable<JsonObject> collectionObservable = RetrofitManager.getInstance().getService().getExerciseCollectStatus(exerciseIds.toString());
        // 获取答题卡
        Observable<RealExamBeans> realExamBeansObservable = RetrofitManager.getInstance().getService().getAnswerCard(paperId);
        // 两个网络访问，然后组合数据
        Observable<RealExamBeans.RealExamBean> combineObv = Observable.zip(
                collectionObservable, realExamBeansObservable,
                new Func2<JsonObject, RealExamBeans, RealExamBeans.RealExamBean>() {

                    @Override
                    public RealExamBeans.RealExamBean call(JsonObject jsonObject, RealExamBeans realExamBeans) {

                        // ***********************初步组合，把返回来的收藏、答题卡和试题进行组合成 RealExamBeans.RealExamBean

                        // 组合成最后的试题
                        RealExamBeans.RealExamBean resultBean = realExamBeans.data;

                        //--------------------处理返回数据错误
                        if (resultBean.answers != null && resultBean.answers.size() > 0) {
                            for (int i = 0; i < resultBean.answers.size(); i++) {
                                if (resultBean.answers.get(i) == null) {
                                    resultBean.answers.set(i, 0);
                                }
                            }
                        }

                        //--------------------处理null题
                        for (int i = exerciseBeans.size() - 1; i >= 0; i--) {
                            if (exerciseBeans.get(i) == null) {
                                exerciseBeans.remove(i);
                                resultBean.answers.remove(i);
                                resultBean.corrects.remove(i);
                                resultBean.times.remove(i);
                                resultBean.doubts.remove(i);
                            }
                        }

                        //--------------------处理返回数据错误

                        resultBean.paper = new PaperBean();

                        resultBean.paper.name = resultBean.name;
                        resultBean.paper.id = (int) resultBean.paperId;
                        resultBean.paper.startTime = resultBean.startTime;
                        resultBean.paper.endTime = resultBean.endTime;
                        resultBean.paper.modules = resultBean.modules;
                        resultBean.paper.subject = resultBean.subject;
                        resultBean.paper.type = resultBean.type;

                        // 开始组合
                        resultBean.paper.questionBeanList = new ArrayList<>();

                        if (resultBean.times == null || resultBean.answers == null) {
                            resultBean.times = new ArrayList<>();                               // 我的答题时间列表
                            resultBean.answers = new ArrayList<>();                             // 我的答案列表
                        }
                        // 遍历试题，开始组合
                        for (int i = 0; i < exerciseBeans.size(); i++) {
                            // 单个试题
                            ArenaExamQuestionBean bean = ArenaExamDataConverts.convertFromExerciseBeanNew(exerciseBeans.get(i));
                            // 什么试题
                            bean.name = realExamBeans.data.name;
                            // 答题卡疑问  1:用户对该试题有疑问,0:没有疑问
                            if (resultBean.doubts != null && i < resultBean.doubts.size()) {
                                bean.doubt = resultBean.doubts.get(i);
                            }
                            resultBean.paper.questionBeanList.add(bean);
                        }

                        // ***********************组合是否收藏过
                        // 是否收藏的int列表
                        List<Integer> collectList = ArenaExamDataConverts.parsePractiseCollectionList(jsonObject);
                        // 是否收藏等属性添加进试题
                        ArenaExamDataConverts.processExamCollection(resultBean.paper.questionBeanList, collectList);
                        // ***********************把试题记录对应到每一道题中
                        // 把已经选择的答案，对应到试题详情中
                        ArenaExamDataConverts.dealExamBeanAnswers(resultBean);
                        return resultBean;
                    }
                });
        dealObserve(combineObv);
    }

    private void dealObserve(Observable<RealExamBeans.RealExamBean> combineObv) {
        mCompositeSubscription.add(combineObv.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RealExamBeans.RealExamBean>() {
                    @Override
                    public void onCompleted() {
                        mView.dismissProgressBar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dismissProgressBar();
                        mView.onLoadDataFailed(2);
                        CommonUtils.showToast("试题失败，请您重试");
                    }

                    @Override
                    public void onNext(RealExamBeans.RealExamBean examBean) {
                        mView.dismissProgressBar();
                        if (examBean == null || examBean.paper == null || examBean.paper.questionBeanList == null) {
                            CommonUtils.showToast("获取练习信息失败，请您重试");
                            mView.onLoadDataFailed(2);
                            return;
                        }
                        realExamBean = examBean;
                        setDataToView();
                    }
                })
        );
    }

    /**
     * 旧的试题，自动保存，答题卡每五道题就保存一下 || 退出时全量保存。
     * saveType 0、全部保存退出 1、部分保存，不退出
     */
    public void saveAnswerCardOld(final int saveType, int requestType, long id, Collection<ArenaExamQuestionBean> saveQuestionBeans) {

        if (!NetUtil.isConnected()) return;

        if (saveType == 0) {
            mView.showProgressBar();
        }

        ArrayList<AnswerCardBean> list = new ArrayList<>();
        if (saveQuestionBeans == null || saveQuestionBeans.size() == 0) return;

        for (ArenaExamQuestionBean saveQuestionBean : saveQuestionBeans) {
            AnswerCardBean bean = new AnswerCardBean();
            bean.answer = saveQuestionBean.userAnswer;
            bean.doubt = saveQuestionBean.doubt;
            bean.questionId = saveQuestionBean.id;
            bean.time = saveQuestionBean.usedTime == 0 ? 1 : saveQuestionBean.usedTime;
            bean.correct = saveQuestionBean.isCorrect;
            list.add(bean);
        }
        final JsonArray jsonArray = new Gson().toJsonTree(list,
                new TypeToken<List<AnswerCardBean>>() {
                }.getType()).getAsJsonArray();

        Observable<BaseResponseModel> realExamBeansObservable = null;
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH) {            // 小模考保存
            realExamBeansObservable = RetrofitManager.getInstance().getService().saveSMatchAnswerCard(id, jsonArray);
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST) {      // 阶段性测试保存
            realExamBeansObservable = RetrofitManager.getInstance().getService().saveStageAnswerCard(id, jsonArray);
        } else {                                                                        // 其他保存
            realExamBeansObservable = RetrofitManager.getInstance().getService().saveAnswerCard(id, jsonArray);
        }
        mCompositeSubscription.add(realExamBeansObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (saveType == 0) {
                            mView.dismissProgressBar();
                            ToastUtils.showShort("保存失败，请重试");
                        }
                    }

                    @Override
                    public void onNext(BaseResponseModel model) {
                        if (saveType == 0) {
                            mView.dismissProgressBar();
                            mView.onSavedAnswerCardSuccess(saveType);
                        }
                    }
                })
        );
    }

    /**
     * 新模考，自动保存，答题卡每五道题就保存一下 || 退出时全量保存。
     * saveType 0、全部保存退出 1、部分保存，不退出
     */
    public void saveAnswerCard(final int saveType, long id, Collection<ArenaExamQuestionBean> saveQuestionBeans) {

        if (!NetUtil.isConnected()) return;

        if (saveType == 0) {
            mView.showProgressBar();
        }

        ArrayList<AnswerCardSaveBean> list = new ArrayList<>();
        if (saveQuestionBeans == null || saveQuestionBeans.size() == 0) return;

        for (ArenaExamQuestionBean saveQuestionBean : saveQuestionBeans) {
            AnswerCardSaveBean bean = new AnswerCardSaveBean();
            bean.answer = saveQuestionBean.userAnswer;
            bean.doubt = saveQuestionBean.doubt;
            bean.questionId = saveQuestionBean.id;
            bean.expireTime = saveQuestionBean.usedTime == 0 ? 1 : saveQuestionBean.usedTime;
            list.add(bean);
        }
        JsonArray jsonArray = new Gson().toJsonTree(list, new TypeToken<List<AnswerCardSaveBean>>() {
        }.getType()).getAsJsonArray();
        Observable<BaseResponse> baseResponseObservable = RetrofitManager.getInstance()
                .getService().saveScAnswerCardNew(id, jsonArray);
        mCompositeSubscription.add(baseResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onCompleted() {
                        mView.dismissProgressBar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (saveType == 0) {
                            mView.dismissProgressBar();
                        }
                        if (saveType == 0) {
                            ToastUtils.showShort("保存失败，请重试");
                        }
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        if (saveType == 0) {
                            mView.dismissProgressBar();
                        }
                        if (baseResponse.code == 1000000) {
                            if (saveType == 0) {
                                mView.onSavedAnswerCardSuccess(saveType);
                            }
                        } else {
                            if (saveType == 0) {
                                ToastUtils.showShort(baseResponse.message);
                            }
                        }
                    }
                })
        );
    }

    /**
     * 旧的 创建答题卡（做新题）
     * 201、获取答题卡
     */
    private void getPaperData(int type, Bundle extraArgs) {
        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络,请检查网络");
            mView.onLoadDataFailed(1);
            return;
        }
        if (extraArgs == null) {
            return;
        }
        Observable<RealExamBeans> examBeansObservable = null;
        long pointIds = extraArgs.getLong("point_ids");

        if (!(type == ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI || type == ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE)) {
            if (pointIds <= 0) {
                return;
            }
        }
        mView.showProgressBar();
        switch (type) {
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE:                    // 智能练习 随手练 智能刷题
                examBeansObservable = RetrofitManager.getInstance().getService().getArtificialIntelligencePaper();
                mCompositeSubscription.add(createObservable(examBeansObservable));
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI:              // 专项练习
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI:       // 专项练习背题模式
                // 做题 和 背题 通过flag区别，返回数据都一样
                examBeansObservable = RetrofitManager.getInstance().getService().getHomeQuestionPaper(pointIds, SpUtils.getHomeQuestionSize(), SpUtils.getHomeQuestionMode() + 1);
                mCompositeSubscription.add(createObservable(examBeansObservable));
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN:                 // 真题演练
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_PAST_MOKAO:                     // 往期模考
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN:                     // 专项模考
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN:                 // 精准估分
                examBeansObservable = RetrofitManager.getInstance().getService().getRealOrMockPaper(pointIds);
                mCompositeSubscription.add(createObservable(examBeansObservable));
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI:                   // 错题重练
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI:            // 错题重练背题模式
//                examBeansObservable = RetrofitManager.getInstance().getService().getErrorTrainingPaper(pointIds, pageSize);
                examBeansObservable = RetrofitManager.getInstance().getService().getErrorQuestionPaper(pointIds, SpUtils.getErrorQuestionSize(), SpUtils.getErrorQuestionMode() + 1);
                mCompositeSubscription.add(createObservable(examBeansObservable));
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN:                    // 每日特训
                examBeansObservable = RetrofitManager.getInstance().getService().getDailyTrainingPaper(pointIds);
                mCompositeSubscription.add(createObservable(examBeansObservable));
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST:             // 答题卡类型	模考大赛
                examBeansObservable = RetrofitManager.getInstance().getService().getScMockPaper(pointIds);
                mCompositeSubscription.add(createObservable(examBeansObservable));
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH:                    // 答题卡类型	小模考
                examBeansObservable = RetrofitManager.getInstance().getService().getSmallMatchPaper(pointIds);
                mCompositeSubscription.add(createObservable(examBeansObservable));
                break;
            case ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST:                     // 答题卡类型	阶段性测试
                long syllabusId = extraArgs.getLong("syllabusId");
                examBeansObservable = RetrofitManager.getInstance().getService().getStagePaper(pointIds, syllabusId);
                mCompositeSubscription.add(createObservable(examBeansObservable));
                break;
            default:
                mView.dismissProgressBar();
                break;
        }
    }

    private Subscription createObservable(Observable<RealExamBeans> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RealExamBeans>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.dismissProgressBar();
                        e.printStackTrace();
                        mView.onLoadDataFailed(2);
                    }

                    @Override
                    public void onNext(RealExamBeans bean) {
                        if (bean == null) {
                            mView.dismissProgressBar();
                            return;
                        }
                        if (bean.data == null) {
                            mView.dismissProgressBar();
                            ToastUtils.showShort(bean.message);
                            return;
                        }
                        getPractice(bean.data);
                    }
                });
    }

    /**
     * 根据考卷信息获取
     * 202、根据答题卡中问题ids，获取试题信息，收藏信息，组合到paper里
     * 302、
     */
    private void getPractice(final RealExamBeans.RealExamBean realExamBean) {
        final List<Integer> questions = realExamBean.paper.questions;
        if (questions == null || questions.size() == 0) {
            mView.dismissProgressBar();
            mView.onLoadDataFailed(2);
            return;
        }
        mView.showProgressBar();
        // 把List的题号，改成逗号隔开的题号
        String exerciseIds = getExerciseIds(questions);
        // 根据逗号分隔开的id，获取试题
        Observable<ExerciseBeans> questionsObservable = RetrofitManager.getInstance().getService().getExercises(exerciseIds);
        // 查询试题是否被收藏过
        Observable<JsonObject> collectionObservable = RetrofitManager.getInstance().getService().getExerciseCollectStatus(exerciseIds);
        Observable<RealExamBeans.RealExamBean> combineObv = Observable.zip(questionsObservable, collectionObservable,
                new Func2<ExerciseBeans, JsonObject, RealExamBeans.RealExamBean>() {
                    @Override
                    public RealExamBeans.RealExamBean call(ExerciseBeans exerciseBeans, JsonObject jsonObject) {

                        // ***********************处理错误数据 & 初步组合，把返回来的收藏、试题和答题卡进行组合成 RealExamBeans.RealExamBean

                        List<ModuleBean> modules = null;
                        if (realExamBean.paper != null && realExamBean.paper.modules != null && realExamBean.paper.modules.size() > 0) {
                            modules = realExamBean.paper.modules;
                        }

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
                                List<Integer> exercises = new ArrayList<>();                // 返回问题id
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
                                        // 检查知识点
                                        if (realExamBean.points != null && realExamBean.points.size() > realQuestionSize && realExamBean.points.size() > i) {
                                            realExamBean.points.remove(i);
                                        }
                                        // 答题卡疑问
                                        if (realExamBean.doubts != null && realExamBean.doubts.size() > realQuestionSize && realExamBean.doubts.size() > i) {
                                            realExamBean.doubts.remove(i);
                                        }
                                    }
                                }
                            }
                            // ---------- 到这里做的处理，都是以防服务端返回丢题

                            realExamBean.paper.questionBeanList = new ArrayList<>();
                            for (int i = 0; i < exerciseBeans.data.size(); i++) {
                                ArenaExamQuestionBean bean = ArenaExamDataConverts.convertFromExerciseBean(exerciseBeans.data.get(i));
                                bean.name = realExamBean.name;

                                // 答题卡疑问  1:用户对该试题有疑问,0:没有疑问
                                if (realExamBean.doubts != null && i < realExamBean.doubts.size()) {
                                    bean.doubt = realExamBean.doubts.get(i);
                                }
                                realExamBean.paper.questionBeanList.add(bean);
                            }
                        }

                        // ***********************处理收藏信息
                        List<Integer> collectList = ArenaExamDataConverts.parsePractiseCollectionList(jsonObject);
                        // 处理收藏信息
                        ArenaExamDataConverts.processExamCollection(realExamBean.paper.questionBeanList, collectList);
                        // ***********************处理答题卡内部信息，把对错做题时长对应到单个题内部
                        // 把答案、正确与否、是否有疑问赋值给paper.questionList。错题列表，分类信息，等信息的处理
                        ArenaExamDataConverts.dealExamBeanAnswers(realExamBean);
                        return realExamBean;
                    }
                });
        dealPractices(combineObv);
    }

    private void dealPractices(Observable<RealExamBeans.RealExamBean> combineObv) {
        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络,请检查网络");
            mView.dismissProgressBar();
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
                            mView.onLoadDataFailed(2);
                            return;
                        }
                        if (realExamBean != null && requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE
                                && realExamBean.paper != null && realExamBean.paper.questionBeanList != null) {             // 收藏模式下添加分页添加更多的题
                            collectionQuestionListPre = examBean.paper.questionBeanList;
                        } else {
                            realExamBean = examBean;
                            setDataToView();
                        }
                    }
                })
        );
    }

    /**
     * 旧的获取试题数据（继续做题）
     * 301、根据答题卡id（practice_id）获取答题卡
     */
    private void getPractiseDetails(Bundle extraArgs) {
        long practiceId = extraArgs.getLong("practice_id");
        if (practiceId <= 0) {
            return;
        }
        getPractiseDetails(practiceId);
    }

    public void getPractiseDetails(long practiceId) {
        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络,请检查网络");
            mView.onLoadDataFailed(1);
            return;
        }
        if (practiceId <= 0) {
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
                        if (practiceDetailsBeans != null && !"1000000".equals(practiceDetailsBeans.code)) {
                            mView.dismissProgressBar();
                            CommonUtils.showToast("获取练习信息失败，" + practiceDetailsBeans.message);
                            mView.onLoadDataFailed(2);
                            return;
                        }
                        if (practiceDetailsBeans == null || practiceDetailsBeans.data == null || practiceDetailsBeans.data.paper == null) {
                            mView.dismissProgressBar();
                            CommonUtils.showToast("获取练习信息失败，请您重试");
                            mView.onLoadDataFailed(2);
                            return;
                        }
                        RealExamBeans.RealExamBean resultBean = practiceDetailsBeans.data;
                        getPractice(resultBean);
                    }
                }));
    }

    /**
     * 根据题号获取问题，组成答题卡类型显示。我的收藏、单题解析。
     *
     * @param exerciseIds
     * @param reqType
     */
    private void getPracticeInfoList(String exerciseIds, final int reqType) {
        if (TextUtils.isEmpty(exerciseIds)) {
            mView.onLoadDataFailed(2);
            return;
        }
        if (ArenaHelper.isFirstLoad(reqType)) {
            mView.showProgressBar();
        }
        // 查看是否是收藏分页加载，这里返回需要分页的试题ids
        exerciseIds = ArenaHelper.getPartExerciseIds(exerciseIds, reqType);
        // 根据试题id，获取试题
        Observable<ExerciseBeans> questionsObservable = RetrofitManager.getInstance().getService().getExercises(exerciseIds);
        // 根据试题id,批量查询哪些试题被收藏过
        Observable<JsonObject> collectionObservable = RetrofitManager.getInstance().getService().getExerciseCollectStatus(exerciseIds);
        // 获取我的答题记录
        Observable<TimeBean> timeBeanObservable = RetrofitManager.getInstance().getService().getQuestionsMyRecords(exerciseIds);
        // 三个网络访问，然后组合数据
        Observable<RealExamBeans.RealExamBean> combineObv = Observable.zip(
                questionsObservable, collectionObservable, timeBeanObservable,
                new Func3<ExerciseBeans, JsonObject, TimeBean, RealExamBeans.RealExamBean>() {
                    @Override
                    public RealExamBeans.RealExamBean call(ExerciseBeans exerciseBeans, JsonObject jsonObject, TimeBean timeBean) {

                        // 组合成最后的试题
                        RealExamBeans.RealExamBean resultBean = new RealExamBeans.RealExamBean();
                        resultBean.paper = new PaperBean();

                        // 什么试题
                        if (reqType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE) {
                            resultBean.name = resultBean.paper.name = "收藏解析";
                        } else if (reqType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG) {
                            resultBean.name = resultBean.paper.name = "错题解析";
                        } else if (reqType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_SINGLE) {
                            resultBean.name = resultBean.paper.name = "单题解析";
                        } else if (reqType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_PREVIEW) {
                            resultBean.name = resultBean.paper.name = "预览模式";
                        }

                        // 是否收藏的int列表
                        List<Integer> collectList = ArenaExamDataConverts.parsePractiseCollectionList(jsonObject);

                        // 开始组合
                        if (exerciseBeans != null && exerciseBeans.data != null) {
                            resultBean.paper.questionBeanList = new ArrayList<>();
                            boolean isUserRecord = false;                                           // 是否用户答过题
                            if (resultBean.times == null || resultBean.answers == null || resultBean.corrects == null) {
                                isUserRecord = true;
                                resultBean.times = new ArrayList<>();                               // 我的答题时间列表
                                resultBean.answers = new ArrayList<>();                             // 我的答案列表
                                resultBean.corrects = new ArrayList<>();
                            }
                            // 遍历试题，开始组合
                            for (int i = 0; i < exerciseBeans.data.size(); i++) {
                                // 单个试题
                                ArenaExamQuestionBean bean = ArenaExamDataConverts.convertFromExerciseBean(exerciseBeans.data.get(i));
                                // 组合答题记录
                                if (isUserRecord && timeBean != null && timeBean.data != null) {
                                    TimeBean.Data timeRecord = timeBean.data.get(i);
                                    if (timeRecord != null && timeRecord.answers != null && timeRecord.times != null
                                            && timeRecord.answers.size() > 0 && timeRecord.times.size() > 0) {
                                        int sizeTime = timeRecord.times.size();
                                        int sizeAnswer = timeRecord.answers.size();
                                        resultBean.times.add(timeRecord.times.get(sizeTime - 1));
                                        resultBean.answers.add(timeRecord.answers.get(sizeAnswer - 1));
                                    } else {
                                        resultBean.times.add(0);
                                        resultBean.answers.add(0);
                                    }
                                }
                                // 答题卡疑问  1:用户对该试题有疑问,0:没有疑问
                                if (resultBean.doubts != null && i < resultBean.doubts.size()) {
                                    bean.doubt = resultBean.doubts.get(i);
                                }
                                if (resultBean.answers.get(i) != 0) {
                                    if (resultBean.answers.get(i) == bean.answer) {
                                        resultBean.corrects.add(1);
                                    } else {
                                        resultBean.corrects.add(2);
                                    }
                                } else {
                                    resultBean.corrects.add(0);
                                }
                                resultBean.paper.questionBeanList.add(bean);
                            }
                        }

                        // 是否收藏添加进试题
                        ArenaExamDataConverts.processExamCollection(resultBean.paper.questionBeanList, collectList);
                        // 把已经选择的答案，对应到试题详情中
                        ArenaExamDataConverts.dealExamBeanAnswers(resultBean);
                        return resultBean;
                    }
                });
        dealPractices(combineObv);
    }

    /**
     * 把List的题号，改成逗号隔开的题号
     */
    private String getExerciseIds(List<Integer> ids) {
        if (ids == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Integer id : ids) {
            sb.append(id).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private boolean checkNet() {
        if (!NetUtil.isConnected()) {
            mView.onLoadDataFailed(0);
            ToastUtils.showShort("无网络连接");
            return true;
        }
        return false;
    }
}
