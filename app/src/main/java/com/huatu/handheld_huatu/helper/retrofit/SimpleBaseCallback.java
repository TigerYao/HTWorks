package com.huatu.handheld_huatu.helper.retrofit;


import com.huatu.handheld_huatu.mvpmodel.BaseResponse;

import retrofit2.Response;

/**
 * Created by Administrator on 2016/8/26.
 */
public class SimpleBaseCallback extends RetrofitStatusCallback<BaseResponse> {

    @Override
    protected void onSuccess(Response<BaseResponse> response) {
    }

    @Override
    protected void onFailure(String error, int type) {
    }
}
