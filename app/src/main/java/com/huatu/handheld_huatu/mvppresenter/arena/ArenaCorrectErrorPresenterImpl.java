package com.huatu.handheld_huatu.mvppresenter.arena;

import com.google.gson.JsonObject;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.mvpview.BaseView;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2016/10/25.
 */
public class  ArenaCorrectErrorPresenterImpl {
    BaseView mView;
    CompositeSubscription compositeSubscription;

    public ArenaCorrectErrorPresenterImpl(CompositeSubscription cs, BaseView view) {
        compositeSubscription = cs;
        if(compositeSubscription == null) {
            compositeSubscription = new CompositeSubscription();
        }
        mView = view;
    }

    public void correctError(int currentExerciseId, String contact, String content) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("qid", currentExerciseId);
        jsonObject.addProperty("contacts", contact);
        jsonObject.addProperty("content", content);
        mView.showProgressBar();
        ServiceProvider.correctError(
                compositeSubscription, currentExerciseId, jsonObject, new NetResponse(){
                    @Override
                    public void onError(final Throwable e) {
                        mView.dismissProgressBar();
                        mView.onLoadDataFailed();
                    }

                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        super.onSuccess(model);
                        mView.dismissProgressBar();
                        CommonUtils.showToast("您的纠错建议提交成功");
                        mView.onSetData(null);
                    }
                });
    }
}
