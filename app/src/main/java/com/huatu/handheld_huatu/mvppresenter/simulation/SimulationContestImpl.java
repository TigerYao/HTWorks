package com.huatu.handheld_huatu.mvppresenter.simulation;


import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.event.arena.SimulationContestMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.matchs.EssayScReportBean;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScDetailBean;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScHistory;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScHistoryTag;
import com.huatu.handheld_huatu.mvppresenter.BasePreMessageEventImpl;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 *
 */

public class SimulationContestImpl extends BasePreMessageEventImpl<SimulationContestMessageEvent> {

    public SimulationContestImpl(CompositeSubscription cs) {
        super(cs, new SimulationContestMessageEvent());
    }

    @Override
    public void showErrorMsg(int type) {
        super.showErrorMsg(type);
    }

    //data area
    public ArrayList<SimulationScDetailBean> mSimulationContestEssayDetailBeans;

    // 获取申论模考大赛详情
    public void getSimulationEssayScDetail() {
        showProgressBar();
        ServiceProvider.getSimulationEssayScDetail(compositeSubscription, new NetResponse() {

            @Override
            public void onError(final Throwable e) {
                postEvent(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_DETAIL_DATA_FAIL_ESSAY);
                dismissProgressBar();
                onLoadDataFailed();
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                dismissProgressBar();
                if (model.data != null) {
                    mSimulationContestEssayDetailBeans = (ArrayList<SimulationScDetailBean>) model.data;
                    LogUtils.e("getSimulationScDetail", model.data.toString());
                    getAgainSc(0, model.data);
                }
                if (model.data != null && mSimulationContestEssayDetailBeans != null && mSimulationContestEssayDetailBeans.size() > 0) {
                    postEvent(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_DETAIL_DATA_ESSAY);
                } else {
                    postEvent(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_DETAIL_DATA_FAIL_ESSAY);
                }
            }
        });
    }

    private SimulationScDetailBean getAgainSc(int type, List<SimulationScDetailBean> var) {
        SimulationScDetailBean varRetr = null;
        if (var != null) {
            for (SimulationScDetailBean simulationScDetailBean : var) {
                if (simulationScDetailBean != null) {
                    if (simulationScDetailBean.status == 8) {
                        if (type == 0) {
                            postEvent(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_8_AGAIN_DATA);
                        } else {
                            varRetr = simulationScDetailBean;
                            if (simulationScDetailBean != null) {
                                String ids = simulationScDetailBean.paperId + "_" + simulationScDetailBean.essayPaperId;
                                String idsSp = SpUtils.getSimulationContestId();
                                if (idsSp != null) {
                                    if (idsSp.equals(ids)) {
                                        return simulationScDetailBean;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return varRetr;
    }

    private SimulationScHistory mNewSimulationScHistory;

    public SimulationScHistory getNewSimulationHistoryData() {
        return mNewSimulationScHistory;
    }

    private void setNewSimulationScHistory(Object var) {
        if (var != null && var instanceof SimulationScHistory) {
            this.mNewSimulationScHistory = (SimulationScHistory) var;
        }
    }

    public void getNewScReport(int tag, int subjectId) {
        showProgressBar();
        ServiceProvider.getNewScReport(compositeSubscription, tag, subjectId, new NetResponse() {

            @Override
            public void onError(final Throwable e) {
                dismissProgressBar();
                onLoadDataFailed();
                showErrorMsg(1);
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissProgressBar();
                LogUtils.d(model.data);
                setNewSimulationScHistory(model.data);
                postEvent(SimulationContestMessageEvent.NEW_BASE_EVENT_TYPE_SC_EXAM_REPORT_DATA);
            }
        });
    }

    //新模考tags 339--383
    public List<SimulationScHistoryTag> mNewScHistoryTags;

    public void setNewScHistoryTags(Object var) {
        if (var != null) {
            this.mNewScHistoryTags = (List<SimulationScHistoryTag>) var;
        }
    }

    public void getNewScHistoryTag(int subjectId) {
        showProgressBar();
        ServiceProvider.getNewScHistoryTag(compositeSubscription, subjectId, subjectId, new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                dismissProgressBar();
                LogUtils.d(model.data);
                setNewScHistoryTags(model.data);
                postEvent(SimulationContestMessageEvent.NEW_BASE_EVENT_TYPE_SC_EXAM_HISTORY_TAG_DATA);
            }

            @Override
            public void onError(final Throwable e) {
                dismissProgressBar();
                onLoadDataFailed();
            }
        });
    }

    public EssayScReportBean essayScReportBean;

    // 申论模考大赛报告
    public void getEssayReport(long paperId) {
        showProgressBar();
        ServiceProvider.getEssayReport(compositeSubscription, paperId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissProgressBar();
                if (model.data != null && model.data instanceof EssayScReportBean) {
                    essayScReportBean = (EssayScReportBean) model.data;
                }
                if (model.data != null) {
                    postEvent(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_essay_getEssayReport);
                }
            }

            @Override
            public void onError(final Throwable e) {
                dismissProgressBar();
                onLoadDataFailed();
                showErrorMsg(1);
            }
        });
    }

    // 我的申论报告
    public SimulationScHistory simulationScHistory;

    public void getEssayScHistoryList(int tag) {
        showProgressBar();
        ServiceProvider.getEssayScHistoryList(compositeSubscription, tag, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissProgressBar();
                simulationScHistory = (SimulationScHistory) model.data;
                postEvent(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_essay_getEssayScHistoryList);
            }

            @Override
            public void onError(final Throwable e) {
                dismissProgressBar();
                if (e instanceof ApiException) {
                    if (((ApiException) e).getErrorCode() == ApiErrorCode.ERROR_INVALID_DATA) {
                        postEvent(SimulationContestMessageEvent.BASE_EVENT_TYPE_SC_essay_getEssayScHistoryList);
                    }
                } else {
                    onLoadDataFailed();
                }
            }
        });
    }

    public RealExamBeans.RealExamBean realExamBean;
}