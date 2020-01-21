package com.huatu.handheld_huatu.mvppresenter.me;


import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.event.me.ExamTypeAreaMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.area.ProvinceBeanList;
import com.huatu.handheld_huatu.mvpmodel.me.ExamTypeAreaConfigBean;
import com.huatu.handheld_huatu.mvppresenter.BasePreMessageEventImpl;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

import rx.subscriptions.CompositeSubscription;

/**
 *
 */

public class ExamTargetAreaImpl extends BasePreMessageEventImpl<ExamTypeAreaMessageEvent> {
    private String TAG = "ExamTargetAreaImpl";

    public ExamTargetAreaImpl(CompositeSubscription cs) {
        super(cs, new ExamTypeAreaMessageEvent());
    }

    public ProvinceBeanList dataList;

    public ProvinceBeanList getDataList() {
        return dataList;
    }

    public void setDataList(Object dataList) {
        if (dataList instanceof ProvinceBeanList) {
            this.dataList = (ProvinceBeanList) dataList;
        }
    }

    public void getTargetAreaList(int catgory) {
        showProgressBar();
        ServiceProvider.getTargetAreaList(compositeSubscription, catgory, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissProgressBar();
                setDataList(model.data);
                postEvent(ExamTypeAreaMessageEvent.ETA_MSG_GET_TARGET_AREA_LIST_SUCCESS);
            }

            @Override
            public void onError(final Throwable e) {
                dismissProgressBar();
                onLoadDataFailed();
            }
        });
    }

    /**
     * @param type 0、登录后应用内切换科目 & 地区
     *             1、点击HomeTitle中的 公基、职测 按钮
     *             3、首次登录后切换科目
     *             5、申论下点击行测
     */
    public void setUserAreaTypeConfig(final int type, final Integer category, Integer area, final Integer subject, Integer qcount, Integer errorQcount) {
        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("网络未连接，请检查您的网络");
            return;
        }
        showProgressBar();
        ServiceProvider.setUserAreaTypeConfig(compositeSubscription, category, area, subject, qcount, errorQcount, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                dismissProgressBar();
                if (category != null) {
                    SpUtils.setUserCatgory(category);
                }
                if (subject != null) {
                    SpUtils.setUserSubject(subject);
                }

                if (subject != null || category != null) {
                    SignUpTypeDataCache.getInstance().reSetData();
                }
                if (type != 5) {            // 申论切到行测，不刷新
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_MSG_TYPE_CHANGE_UPDATE_VIEW));
                }
                UniApplicationContext.updatePushAgentTag();
                if (type == 0) {            // 登录后更改考试类型
                    // 设置成功后通知 SettingExamTargetAreaFragment 保存一下选择的 区域 和 题型信息 到 SharePreference
                    postEvent(ExamTypeAreaMessageEvent.ETA_MSG_SET_AREA_TYPE_CONFIG_SUCCESS);
                } else if (type == 3) {     // 第一次设置category，通知设置页面，启动MainTabActivity
                    postEvent(ExamTypeAreaMessageEvent.ETA_MSG_SET_AREA_TYPE_CONFIG_SUCCESS_from_SettingExamTypeFromFirstFragment);
                }
            }

            @Override
            public void onError(final Throwable e) {
                dismissProgressBar();
                CommonUtils.showToast("网络出错了，请稍后重试");
            }
        });
    }
}
