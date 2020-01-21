package com.huatu.handheld_huatu.mvppresenter.essay;


import android.text.TextUtils;

import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BasePreMessageEventImplEx;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.essay.bean.IsCheckData;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamTimerHelper;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.essay.CreateAnswerCardIdBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CreateAnswerCardPostBean;
import com.huatu.handheld_huatu.mvpmodel.essay.EssayCommitResponse;
import com.huatu.handheld_huatu.mvpmodel.essay.ExamMaterialListBean;
import com.huatu.handheld_huatu.mvpmodel.essay.MaterialsFileUrlBean;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperCommitBean;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleAreaListBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class EssayExamImpl extends BasePreMessageEventImplEx {

    public EssayExamImpl(CompositeSubscription cs) {
        super(cs, new EssayExamMessageEvent());
    }

    @Override
    public void showErrorMsg(int type) {
        super.showErrorMsg(type);
    }

    // 根据单体组Id，获取地区列表
    public ArrayList<SingleAreaListBean> cacheSingleAreaListBeans;

    public void getSingleAreaListDetail(long similarId) {
        ServiceProvider.getSingleAreaListDetail(compositeSubscription, similarId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (e instanceof ApiException) {
                    String errorMsg = ((ApiException) e).getErrorMsg();
                    CommonUtils.showToast(errorMsg);
                } else {
                    CommonUtils.showToast("获取数据失败，请重试");
                }
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                if (model != null && model.data != null) {
                    cacheSingleAreaListBeans = (ArrayList<SingleAreaListBean>) model.data;
                    postEvent(EssayExamMessageEvent.EssayExam_net_getSingleAreaListDetail);
                }
            }

        });
    }

    // 单题材料
    public ArrayList<ExamMaterialListBean> cacheSingleMaterialListBeans;
    // 单题问题
    public SingleQuestionDetailBean cacheSingleQuestionDetailBean;

    // 获取单题材料和问题
    public void getSingleData(final long questionBaseId, int correctMode, int modeType, long answerId, int bizStatus) {

        showProgressBar();

        // 获取材料
        cacheSingleMaterialListBeans = EssayExamDataCache.getInstance().getCacheSingleMaterialListBeans(questionBaseId);
        Observable<BaseListResponseModel<ExamMaterialListBean>> materialObservable;
        if (cacheSingleMaterialListBeans == null) {
            materialObservable = RetrofitManager.getInstance().getService().getSingleMaterialList(questionBaseId);
        } else {
            BaseListResponseModel<ExamMaterialListBean> materialListBeanBaseListResponseModel = new BaseListResponseModel<>();
            materialListBeanBaseListResponseModel.data = cacheSingleMaterialListBeans;
            materialObservable = Observable.just(materialListBeanBaseListResponseModel);
        }

        // 获取问题
        Observable<BaseResponseModel<SingleQuestionDetailBean>> questionObservable = RetrofitManager.getInstance().getService().getSingleQuestionDetail(questionBaseId, correctMode, modeType, answerId, bizStatus);

        Observable<Object> observable = Observable.zip(materialObservable, questionObservable,
                new Func2<BaseListResponseModel<ExamMaterialListBean>, BaseResponseModel<SingleQuestionDetailBean>, Object>() {
                    @Override
                    public Object call(BaseListResponseModel<ExamMaterialListBean> materialListResponse, BaseResponseModel<SingleQuestionDetailBean> singleQuestionResponse) {

                        if (materialListResponse != null && materialListResponse.data != null) {
                            cacheSingleMaterialListBeans = (ArrayList<ExamMaterialListBean>) materialListResponse.data;
                            EssayExamDataCache.getInstance().setCacheSingleMaterialListBeans(questionBaseId, cacheSingleMaterialListBeans);
                        }
                        if (singleQuestionResponse != null && singleQuestionResponse.data != null) {
                            cacheSingleQuestionDetailBean = singleQuestionResponse.data;
                            EssayExamDataCache.getInstance().cacheSingleQuestionDetailBean = cacheSingleQuestionDetailBean;
                        }
                        return "OK";
                    }
                });

        compositeSubscription.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        dismissProgressBar();
                        postEvent(EssayExamMessageEvent.EssayExam_net_getSingleDataSuccess);
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressBar();
                        e.printStackTrace();
                        postEvent(EssayExamMessageEvent.EssayExam_net_getDataFailed);
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                }));
    }

    // 多题 －－－－－－－－－－－－－－－－－－－－－－－－paperId－－－－－－－－－－－－－－－－－－－－－－－

    // 套题材料
    public ArrayList<ExamMaterialListBean> cachePaperMaterialListBeans;
    // 套题问题
    public PaperQuestionDetailBean cachePaperQuestionDetailBean;

    // 获取套题 问题 和 材料
    public void getPaperData(final long paperId, int correctMode, int modeType, long answerId, int bizStatus) {

        showProgressBar();

        Observable<BaseListResponseModel<ExamMaterialListBean>> materialObservable;
        cachePaperMaterialListBeans = EssayExamDataCache.getInstance().getCachePaperMaterialListBeans(paperId);
        if (cachePaperMaterialListBeans == null) {
            materialObservable = RetrofitManager.getInstance().getService().getPaperMaterials(paperId);
        } else {
            BaseListResponseModel<ExamMaterialListBean> materialListBeanBaseListResponseModel = new BaseListResponseModel<>();
            materialListBeanBaseListResponseModel.data = cachePaperMaterialListBeans;
            materialObservable = Observable.just(materialListBeanBaseListResponseModel);
        }
        Observable<BaseResponseModel<PaperQuestionDetailBean>> questionObservable = RetrofitManager.getInstance().getService().getPaperQuestionDetail(paperId, correctMode, modeType, answerId, bizStatus);

        Observable<Object> observable = Observable.zip(materialObservable, questionObservable,
                new Func2<BaseListResponseModel<ExamMaterialListBean>, BaseResponseModel<PaperQuestionDetailBean>, Object>() {
                    @Override
                    public Object call(BaseListResponseModel<ExamMaterialListBean> materialListResponse, BaseResponseModel<PaperQuestionDetailBean> paperQuestionResponse) {
                        if (materialListResponse != null && materialListResponse.data != null) {
                            cachePaperMaterialListBeans = (ArrayList<ExamMaterialListBean>) materialListResponse.data;
                            EssayExamDataCache.getInstance().setCachePaperMaterialListBeans(paperId, cachePaperMaterialListBeans);
                        }
                        if (paperQuestionResponse != null && paperQuestionResponse.data != null) {
                            cachePaperQuestionDetailBean = paperQuestionResponse.data;
                            sortQuesBean(cachePaperQuestionDetailBean);
                            if (cachePaperQuestionDetailBean != null && cachePaperQuestionDetailBean.essayPaper != null) {
                                cachePaperQuestionDetailBean.spendTime = cachePaperQuestionDetailBean.essayPaper.spendTime;
                            }
                            EssayExamDataCache.getInstance().cachePaperQuestionDetailBean = cachePaperQuestionDetailBean;
                        }
                        return "OK";
                    }
                });

        compositeSubscription.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        dismissProgressBar();
                        postEvent(EssayExamMessageEvent.EssayExam_net_getPaperDataSuccess);
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressBar();
                        e.printStackTrace();
                        postEvent(EssayExamMessageEvent.EssayExam_net_getDataFailed);
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                }));
    }

    // 获取模考 问题 和 材料
    public void getMockPaperData(final long paperId) {

        showProgressBar();

        ArenaDataCache.getInstance().isMockAutoSubmit = false;

        // 模考材料
        Observable<BaseListResponseModel<ExamMaterialListBean>> materialObservable;
        cachePaperMaterialListBeans = EssayExamDataCache.getInstance().getCachePaperMaterialListBeans(paperId);
        if (cachePaperMaterialListBeans == null) {
            materialObservable = RetrofitManager.getInstance().getService().getMockPaperMaterials(paperId);
        } else {
            BaseListResponseModel<ExamMaterialListBean> materialListBeanBaseListResponseModel = new BaseListResponseModel<>();
            materialListBeanBaseListResponseModel.data = cachePaperMaterialListBeans;
            materialObservable = Observable.just(materialListBeanBaseListResponseModel);
        }
        // 模考问题
        Observable<BaseResponseModel<PaperQuestionDetailBean>> questionObservable = RetrofitManager.getInstance().getService().getMockPaperQuestionDetail(paperId);
        // 模考剩余时间
//        Observable<BaseResponseModel<EssyMockRemindTime>> remainTimeObservable = RetrofitManager.getInstance().getService().getMockRemainTime(paperId);

        Observable<PaperQuestionDetailBean> observable = Observable.zip(materialObservable, questionObservable,
                new Func2<BaseListResponseModel<ExamMaterialListBean>, BaseResponseModel<PaperQuestionDetailBean>, PaperQuestionDetailBean>() {
                    @Override
                    public PaperQuestionDetailBean call(BaseListResponseModel<ExamMaterialListBean> materialListResponse, BaseResponseModel<PaperQuestionDetailBean> paperQuestionResponse) {
                        if (materialListResponse != null && materialListResponse.data != null) {
                            cachePaperMaterialListBeans = (ArrayList<ExamMaterialListBean>) materialListResponse.data;
                            EssayExamDataCache.getInstance().setCachePaperMaterialListBeans(paperId, cachePaperMaterialListBeans);
                        }
                        if (paperQuestionResponse != null && paperQuestionResponse.data != null) {
                            sortQuesBean(paperQuestionResponse.data);
                            EssayExamTimerHelper.getInstance().lastSaveTime = System.currentTimeMillis();
                            cachePaperQuestionDetailBean = paperQuestionResponse.data;

                            EssayExamDataCache.getInstance().cachePaperQuestionDetailBean = cachePaperQuestionDetailBean;

                            return cachePaperQuestionDetailBean;
                        }
                        return null;
                    }
                });

        compositeSubscription.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PaperQuestionDetailBean>() {
                    @Override
                    public void onCompleted() {
                        dismissProgressBar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressBar();
                        e.printStackTrace();
                        postEvent(EssayExamMessageEvent.EssayExam_net_getDataFailed);
                    }

                    @Override
                    public void onNext(PaperQuestionDetailBean questionDetailBean) {
                        if (questionDetailBean == null) {
                            postEvent(EssayExamMessageEvent.EssayExam_net_getDataFailed);
                        } else {
                            if (questionDetailBean.essayPaper != null) {
                                cachePaperQuestionDetailBean.essayPaper.correctMode = 1;            // 模考一定是智能批改
                            }
                            postEvent(EssayExamMessageEvent.EssayExam_net_getPaperDataSuccess);
                        }
                    }
                }));

    }

    private void sortQuesBean(PaperQuestionDetailBean data) {
        if (data != null) {
            if (data.essayQuestions != null) {
                Collections.sort(data.essayQuestions, new Comparator<SingleQuestionDetailBean>() {
                    @Override
                    public int compare(SingleQuestionDetailBean o1, SingleQuestionDetailBean o2) {
                        if (o1 != null && o2 != null) {
                            return o1.sort - o2.sort;
                        }
                        return 0;
                    }
                });
            }
        }
    }

    // 试卷下载
    public MaterialsFileUrlBean cacheMaterialsFileUrlBean;

    public void getMaterialsDownloadUrl(long paperAnswerId, long paperId, long questionAnswerId, long questionBaseId) {
        showProgressBar();
        ServiceProvider.getMaterialsDownloadUrl(compositeSubscription, paperAnswerId, paperId, questionAnswerId, questionBaseId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                ToastUtils.showEssayToast("下载失败，请稍后重试");
                dismissProgressBar();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissProgressBar();
                if (model != null && model.data != null) {
                    cacheMaterialsFileUrlBean = (MaterialsFileUrlBean) model.data;
                    postEvent(EssayExamMessageEvent.EssayExam_net_getMaterialsDownloadUrl);
                } else {
                    ToastUtils.showEssayToast("下载失败，请稍后重试");
                }
            }
        });
    }

    // 创建的答题卡
    public CreateAnswerCardIdBean answerCardIdBean;
    private boolean again = false;

    // 创建答题卡
    public void createAnswerCardNew(final int requestType, final CreateAnswerCardPostBean createAnswerCardPostBean) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("无网络连接");
        }
        showProgressBar();
        Observable<BaseResponseModel<CreateAnswerCardIdBean>> observable;
        if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {     // 课后作业创建答题卡
            observable = RetrofitManager.getInstance().getService().createHomeworkAnswerCardNew(createAnswerCardPostBean);
        } else {                                                        // 一般创建答题卡
            observable = RetrofitManager.getInstance().getService().createAnswerCardNew(createAnswerCardPostBean);
        }

        compositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel<CreateAnswerCardIdBean>>() {
                    @Override
                    public void onCompleted() {
                        dismissProgressBar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressBar();
                        e.printStackTrace();
                        createFail(null, requestType, createAnswerCardPostBean);
                    }

                    @Override
                    public void onNext(BaseResponseModel<CreateAnswerCardIdBean> model) {
                        dismissProgressBar();
                        if (model != null) {
                            if (model.code == ApiErrorCode.ERROR_SUCCESS) {    // 华图教育，面授相关接口，返回 200 或 0 为成功
                                if (model.data != null) {
                                    answerCardIdBean = model.data;
                                    postEvent(EssayExamMessageEvent.EssayExam_net_createAnswerCard);
                                } else {
                                    createFail(model, requestType, createAnswerCardPostBean);
                                }
                            } else {
                                createFail(model, requestType, createAnswerCardPostBean);
                            }
                        } else {
                            createFail(model, requestType, createAnswerCardPostBean);
                        }
                    }
                }));
    }

    // 创建答题卡失败 重试/发送失败
    private void createFail(BaseResponseModel<CreateAnswerCardIdBean> model, int requestType, CreateAnswerCardPostBean createAnswerCardPostBean) {
        if (!again) {
            again = true;
            createAnswerCardNew(requestType, createAnswerCardPostBean);
        } else if (model != null) {
            postEvent(EssayExamMessageEvent.EssayExam_net_createAnswerCard_error);
            if (!StringUtils.isEmpty(model.message)) {
                ToastUtils.showEssayToast(model.message);
            } else {
                ToastUtils.showEssayToast("答题卡创建失败");
            }
        } else {
            postEvent(EssayExamMessageEvent.EssayExam_net_createAnswerCard_error);
            ToastUtils.showEssayToast("答题卡创建失败");
        }
    }

    // 计算未完成题数量
    public static int getUnfinishedCount(boolean isSingle, SingleQuestionDetailBean singleQuestionDetailBean, PaperQuestionDetailBean paperQuestionDetailBean) {
        if (isSingle) {
            if (singleQuestionDetailBean == null) {
                return 1;
            }
            if (!TextUtils.isEmpty(singleQuestionDetailBean.content)) {
                return 0;
            }
            return 1;
        } else {
            if (paperQuestionDetailBean == null || paperQuestionDetailBean.essayQuestions == null || paperQuestionDetailBean.essayPaper == null) {
                return 5;
            }
            PaperQuestionDetailBean var = paperQuestionDetailBean;
            int j = 0;
            for (SingleQuestionDetailBean var2 : var.essayQuestions) {
                if (var2 == null) {
                    continue;
                }
                if (!TextUtils.isEmpty(var2.content)) {
                    j++;
                }
            }
            return var.essayQuestions.size() - j;
        }
    }

    /**
     * 保存/交卷
     *
     * @param requestType              提交类型
     * @param isAutoSave               是否是自动保存
     * @param saveType                 保存类型 0、保存 1、提交
     * @param isSingle                 是否是单题
     * @param delayStatus              是否顺延 0、不顺延 1、顺延 人工答题，如果老师工作饱和，就顺延。不饱和、智能答题不顺延。
     * @param singleQuestionDetailBean 单题内容
     * @param paperQuestionDetailBean  套题内容
     */
    public void paperCommit(int requestType, final boolean isAutoSave, final int saveType, final boolean isSingle, int delayStatus,
                            SingleQuestionDetailBean singleQuestionDetailBean, PaperQuestionDetailBean paperQuestionDetailBean) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("无网络连接");
            return;
        }
        if (!isAutoSave) {
            showProgressBar();
        }

        final PaperCommitBean paperCommitBean = assembleAnswer(saveType, isSingle, delayStatus, singleQuestionDetailBean, paperQuestionDetailBean);
        if (paperCommitBean == null) return;

        if (requestType == EssayExamActivity.ESSAY_EXAM_SC) {       // 模考大赛交卷/保存
            scCommit(isAutoSave, saveType, paperCommitBean);
        } else {                                                    // 其他交卷/保存
            otherCommit(requestType, isAutoSave, saveType, paperCommitBean);
        }
    }

    /**
     * 组装要提交的答案对象
     */
    private PaperCommitBean assembleAnswer(int saveType, boolean isSingle, int delayStatus, SingleQuestionDetailBean singleQuestionDetailBean, PaperQuestionDetailBean paperQuestionDetailBean) {
        final PaperCommitBean paperCommitBean = new PaperCommitBean();
        paperCommitBean.terminal = 1;
        if (isSingle) {
            if (singleQuestionDetailBean == null) {
                postEvent(EssayExamMessageEvent.EssayExam_net_paperSave);
                return null;
            }
            paperCommitBean.answerCardId = singleQuestionDetailBean.answerCardId;
            paperCommitBean.lastIndex = 0;
            paperCommitBean.correctMode = singleQuestionDetailBean.correctMode;
            paperCommitBean.paperBaseId = 0;
            paperCommitBean.delayStatus = delayStatus;
            paperCommitBean.saveType = saveType;
            paperCommitBean.type = 0;
            paperCommitBean.unfinishedCount = 0;
            paperCommitBean.spendTime = singleQuestionDetailBean.spendTime;
            paperCommitBean.answerList = new ArrayList<>();

            PaperCommitBean.PaperAnsContentBean ansContentBean = new PaperCommitBean.PaperAnsContentBean();
            ansContentBean.content = singleQuestionDetailBean.content;
            if (singleQuestionDetailBean.fileName != null) {
                ansContentBean.fileName = singleQuestionDetailBean.fileName;

            }
            if (singleQuestionDetailBean.content != null) {
                StringUtils.WordsCount wordsCount = StringUtils.getStringLength(singleQuestionDetailBean.content, 0);
                if (wordsCount != null) {
                    ansContentBean.inputWordNum = wordsCount.length;
                } else {
                    ansContentBean.inputWordNum = 0;
                }
            }
            ansContentBean.questionBaseId = singleQuestionDetailBean.questionBaseId;
            ansContentBean.questionDetailId = singleQuestionDetailBean.questionDetailId;
            ansContentBean.answerId = singleQuestionDetailBean.answerCardId;

            paperCommitBean.answerList.add(ansContentBean);
        } else {
            if (paperQuestionDetailBean == null || paperQuestionDetailBean.essayQuestions == null || paperQuestionDetailBean.essayPaper == null) {
                postEvent(EssayExamMessageEvent.EssayExam_net_paperSave);
                return null;
            }
            paperCommitBean.correctMode = paperQuestionDetailBean.essayPaper.correctMode;
            paperCommitBean.answerCardId = paperQuestionDetailBean.essayPaper.answerCardId;
            paperCommitBean.paperBaseId = paperQuestionDetailBean.essayPaper.paperId;
            paperCommitBean.saveType = saveType;
            paperCommitBean.delayStatus = delayStatus;
            paperCommitBean.type = 1;
            paperCommitBean.spendTime = paperQuestionDetailBean.spendTime;
            paperCommitBean.answerList = new ArrayList<>();

            int j = 0;          // 为了计算未完成的数量
            int index = 0;      // 记录最后做到第几题
            for (SingleQuestionDetailBean detailBean : paperQuestionDetailBean.essayQuestions) {
                if (detailBean == null) {
                    continue;
                }
                PaperCommitBean.PaperAnsContentBean ansContentBean = new PaperCommitBean.PaperAnsContentBean();
                ansContentBean.content = detailBean.content;
                if (detailBean.fileName != null) {
                    ansContentBean.fileName = detailBean.fileName;
                }
                if (!TextUtils.isEmpty(detailBean.content)) {
                    if (detailBean.content != null) {
                        StringUtils.WordsCount wordsCount = StringUtils.getStringLength(detailBean.content, 0);
                        if (wordsCount != null) {
                            ansContentBean.inputWordNum = wordsCount.length;
                        } else {
                            ansContentBean.inputWordNum = 0;
                        }
                    }
                    j++;
                    paperCommitBean.lastIndex = index;
                }
                index++;
                ansContentBean.questionBaseId = detailBean.questionBaseId;
                ansContentBean.questionDetailId = detailBean.questionDetailId;
                ansContentBean.spendTime = detailBean.spendTime;
                ansContentBean.answerId = detailBean.answerCardId;
                paperCommitBean.answerList.add(ansContentBean);
            }
            paperCommitBean.unfinishedCount = paperQuestionDetailBean.essayQuestions.size() - j;
        }

        if (paperCommitBean.answerList != null && paperCommitBean.answerList.size() > 0) {
            // 校验 各个题用时 和 总用时
            int allSpendTime = 0;
            for (PaperCommitBean.PaperAnsContentBean paperAnsContentBean : paperCommitBean.answerList) {
                allSpendTime += paperAnsContentBean.spendTime;
            }
            if (paperCommitBean.spendTime < allSpendTime) {
                paperCommitBean.spendTime = allSpendTime;
            }
        }
        return paperCommitBean;
    }

    /**
     * 一般的申论交卷
     *
     * @param requestType 提交类型
     * @param isAutoSave  是否是自动保存
     * @param saveType    保存类型 0、保存 1、提交
     */
    private void otherCommit(int requestType, final boolean isAutoSave, final int saveType, PaperCommitBean paperCommitBean) {

        Observable<BaseResponseModel<EssayCommitResponse>> observable;
        if (requestType == EssayExamActivity.ESSAY_EXAM_HOMEWORK) {     // 课后作业
            observable = RetrofitManager.getInstance().getService().paperHomeworkCommit(paperCommitBean);
        } else {                                                        // 其他交卷
            observable = RetrofitManager.getInstance().getService().paperCommit(paperCommitBean);
        }

        compositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel<EssayCommitResponse>>() {
                    @Override
                    public void onCompleted() {
                        dismissProgressBar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressBar();
                        e.printStackTrace();
                        if (!isAutoSave) {      // 不是自动保存，自动保存什么都不做
                            if (saveType == 0) {                        // 保存失败
                                postEvent(EssayExamMessageEvent.EssayExam_net_paperSave_fail);
                            } else {                                    // 提交失败
                                postEvent(EssayExamMessageEvent.EssayExam_net_paperCommit_fail);
                            }
                        }
                    }

                    @Override
                    public void onNext(BaseResponseModel<EssayCommitResponse> model) {
                        dismissProgressBar();
                        if (!isAutoSave) {
                            if (model != null) {
                                if (model.code == ApiErrorCode.ERROR_SUCCESS) {     // 成功
                                    if (saveType == 0) {    // 保存成功
                                        postEvent(EssayExamMessageEvent.EssayExam_net_paperSave);
                                    } else {                // 交卷成功
                                        String commitSuccess = "交卷成功";
                                        if (model.data != null && !StringUtils.isEmpty(model.data.msg)) {
                                            commitSuccess = model.data.msg;
                                        }
                                        ToastUtils.showEssayToast(commitSuccess);
                                        postEvent(EssayExamMessageEvent.EssayExam_net_paperCommit);
                                    }
                                } else if (model.code == 1000543) {                 // 重复提交答题卡的问题
                                    if (saveType == 0) {    // 保存成功
                                        postEvent(EssayExamMessageEvent.EssayExam_net_paperSave);
                                    } else {                // 交卷成功
                                        ToastUtils.showEssayToast("交卷成功");
                                        postEvent(EssayExamMessageEvent.EssayExam_net_paperCommit);
                                    }
                                } else {
                                    if (saveType == 0) {    // 保存失败
                                        postEvent(EssayExamMessageEvent.EssayExam_net_paperSave_fail);
                                    } else {                // 提交失败
                                        postEvent(EssayExamMessageEvent.EssayExam_net_paperCommit_fail);
                                    }
                                    ToastUtil.showToast(model.message);
                                }
                            } else {
                                if (saveType == 0) {        // 保存失败
                                    postEvent(EssayExamMessageEvent.EssayExam_net_paperSave_fail);
                                } else {                    // 提交失败
                                    postEvent(EssayExamMessageEvent.EssayExam_net_paperCommit_fail);
                                }
                            }
                        }
                    }
                }));
    }

    // 模考大赛交卷
    private void scCommit(final boolean isAutoSave, final int saveType, PaperCommitBean paperCommitBean) {
        ServiceProvider.paperCommit_sc(compositeSubscription, paperCommitBean, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissProgressBar();
                if (isAutoSave) {

                } else if (e instanceof ApiException && ((ApiException) e).getErrorCode() == 1000543) { // 重复提交答题卡的问题
                    if (saveType == 0) {
                        postEvent(EssayExamMessageEvent.EssayExam_net_paperSave);
                    } else {
                        ToastUtils.showEssayToast("交卷成功");
                        postEvent(EssayExamMessageEvent.EssayExam_net_paperCommit);
                    }
                } else if (e instanceof ApiException) {     // 提示错误信息
                    String errorMsg = ((ApiException) e).getErrorMsg();
                    if (!StringUtils.isEmpty(errorMsg)) {
                        ToastUtils.showEssayToast(errorMsg);
                    }
                    if (saveType == 0) {                        // 保存失败
                        postEvent(EssayExamMessageEvent.EssayExam_net_paperSave_fail);
                    } else {                                    // 提交失败
                        postEvent(EssayExamMessageEvent.EssayExam_net_paperCommit_fail);
                    }
                } else if (saveType == 0) {     // 保存失败
                    postEvent(EssayExamMessageEvent.EssayExam_net_paperSave_fail);
                } else {                        // 提交失败
                    postEvent(EssayExamMessageEvent.EssayExam_net_paperCommit_fail);
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissProgressBar();
                if (model != null && model.code == 1000000) {
                    if (isAutoSave) {

                    } else if (saveType == 0) {
                        postEvent(EssayExamMessageEvent.EssayExam_net_paperSave);
                        // 模考大赛保存通知模考列表刷新
                        EventBus.getDefault().post(new BaseMessageEvent<>(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_ESSAY_SAVE_SUCCESS, new SimulationContestMessageEvent()));
                    } else {
                        SpUtils.setIsSimulationContest(false);
                        ToastUtils.showEssayToast("交卷成功");
                        postEvent(EssayExamMessageEvent.EssayExam_net_paperCommit);
                        // 模考大赛交卷后通知模考列表刷新
                        EventBus.getDefault().post(new BaseMessageEvent<>(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_EXAM_COMP_ESSAY, new SimulationContestMessageEvent()));
                    }
                } else {
                    if (isAutoSave) {

                    } else if (saveType == 0) {
                        postEvent(EssayExamMessageEvent.EssayExam_net_paperSave_fail);
                    } else {
                        postEvent(EssayExamMessageEvent.EssayExam_net_paperCommit_fail);
                    }
                }
            }
        });
    }

    public void setCollectEssay(boolean isSingle, long similarId, long baseId) {
        if (!NetUtil.isConnected()) {
            return;
        }
        int type;
        if (isSingle) {
            type = 0;
        } else {
            type = 1;
        }
        Long similarIdObj = null;
        if (similarId > 0) {
            similarIdObj = similarId;
        }
        ServiceProvider.setCollectEssay(compositeSubscription, type, similarIdObj, baseId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (e instanceof ApiException) {
                    ToastUtils.showEssayToast(e.getMessage());
                }
                postEvent(EssayExamMessageEvent.EssayExam_net_setCollectEssay_fail);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (model != null && model.data != null) {
                    postEvent(EssayExamMessageEvent.EssayExam_net_setCollectEssay_success);
                } else {
                    postEvent(EssayExamMessageEvent.EssayExam_net_setCollectEssay_fail);
                }
            }
        });
    }

    public IsCheckData isCollect;

    public void checkCollectEssay(boolean isSingle, long similarId, long baseId) {
        int type;
        if (isSingle) {
            type = 0;
        } else {
            type = 1;
        }
        Long similarIdObj = null;
        if (similarId > 0) {
            similarIdObj = similarId;
        }
        ServiceProvider.checkCollectEssay(compositeSubscription, type, similarIdObj, baseId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                postEvent(EssayExamMessageEvent.EssayExam_net_checkCollectEssay_fail);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (model != null && model.data != null) {
                    isCollect = (IsCheckData) model.data;
                    postEvent(EssayExamMessageEvent.EssayExam_net_checkCollectEssay_success);
                } else {
                    postEvent(EssayExamMessageEvent.EssayExam_net_checkCollectEssay_fail);
                }
            }
        });
    }

    public void deleteCheckEssay(int type, long answerId) {
        ServiceProvider.deleteCheckEssay(compositeSubscription, type, answerId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
            }
        });
    }

    public void deleteMyOrder(String orderId, int type) {
        ServiceProvider.deleteMyOrder(compositeSubscription, orderId, type, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                postEvent(EssayExamMessageEvent.EssayExam_net_delete_my_course_order);
            }
        });
    }

    public void deleteMyEssayOrder(long orderId) {
        ServiceProvider.deleteMyEssayOrder(compositeSubscription, orderId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
//                postEvent(EssayExamMessageEvent.EssayExam_net_delete_my_essay_order);

            }
        });
    }

    public void deleteCollectEssay(boolean isSingle, long similarId, long baseId) {
        int type;
        if (isSingle) {
            type = 0;
        } else {
            type = 1;
        }
        Long similarIdObj = null;
        if (similarId > 0) {
            similarIdObj = similarId;
        }
        ServiceProvider.deleteCollectEssay(compositeSubscription, type, similarIdObj, baseId, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                postEvent(EssayExamMessageEvent.EssayExam_net_deleteCollectEssay_fail);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (model != null && model.data != null) {
                    postEvent(EssayExamMessageEvent.EssayExam_net_deleteCollectEssay_success);
                } else {
                    postEvent(EssayExamMessageEvent.EssayExam_net_deleteCollectEssay_fail);
                }
            }
        });
    }

    /**
     * 处理照片识别的返回文字
     *
     * @param photoData 识别回来的结果
     */
    public static String filterPhotoResultString(String photoData) {
        String[] all = photoData.split("[$][$][$]");
        String mData;
        final StringBuffer sb = new StringBuffer();
        if (all[0].length() < 2) {
            for (int i = 1; i < all.length; i++) {
                if (i == all.length - 1) {
                    mData = "        " + all[i];
                } else {
                    mData = "        " + all[i] + "\n";
                }
                sb.append(mData);
            }
        } else {
            for (int i = 0; i < all.length; i++) {
                if (i == all.length - 1) {
                    mData = "        " + all[i];
                } else {
                    mData = "        " + all[i] + "\n";
                }
                sb.append(mData);
            }
        }
        return sb.toString();
    }
}