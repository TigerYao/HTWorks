package com.huatu.handheld_huatu.helper;

import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseResponseModel;

import rx.Subscriber;

/**
 * Created by Administrator on 2018\5\15 0015.
 */

public abstract class SimpleSubscriber<T> extends Subscriber<BaseResponseModel<T>> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(BaseResponseModel<T> responseModel) {
        if (responseModel.code == ApiErrorCode.ERROR_SUCCESS) {
            if (responseModel.data == null) {
                //if (responseModel != null) response.onError("数据解析出错", 0);
                onError(new Throwable("数据解析出错"));
                return;
            }
            onSuccess(responseModel.data);
       } else {
             onError(new Throwable(responseModel.message));
       }
    }

    public abstract void onSuccess(T response);
}
