/**
 * <pre>
 * Copyright 2015 Soulwolf Ching
 * Copyright 2015 The Android Open Source Project for XDW-Android-Client
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </pre>
 */
package com.huatu.handheld_huatu.helper.retrofit;



import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.event.SessionTimeOutEvent;
import com.huatu.handheld_huatu.mvpmodel.BaseResponse;
import com.huatu.handheld_huatu.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 异步访问 回调
 * <p>
 * author: Soulwolf Created on 2015/9/11 22:44.
 * email : Ching.Soulwolf@gmail.com
 */
public abstract class RetrofitCallback<RESPONSE extends BaseResponse> implements retrofit2.Callback<RESPONSE> {

   /* public static final String UNLAWFUL_URL = "unlawful_url";
    public static final int UNLAWFUL_STATUS = -1;

    *//**
     * 开始回调，由于Retrofit不支持该回调方法，所以需要自行处理
     *//*
    public void onStart() {

    }

    @Override
    public final void success(RESPONSE response, Response response2) {
        try {
            //  检查结果
            Preconditions.checkResponse(response);
            // Success
            onSuccess(response);
        } catch (Exception e) {
            String url = response2 != null ? response2.getUrl() : UNLAWFUL_URL;
            int status = response2 != null ? response2.getStatus() : UNLAWFUL_STATUS;
            onFailure(RetrofitError.unexpectedError(url, e), status);
        }
    }

    *//**
     * 这里做错误的分发
     *//*
    @Override
    public final void failure(RetrofitError error) {
        if (error.getResponse() != null && error.getResponse().getStatus() == 403) {
            //T下线
            EventBus.getDefault().post(FinishEvent.EVENT_MAIN_FINISH());
            return;
        }
        if (error.getKind() == RetrofitError.Kind.HTTP
                || error.getKind() == RetrofitError.Kind.NETWORK) {
            // 网络或者服务器错误
            if (!onNetworkError(error)) {
                onFailure(error, error.getResponse() == null ? UNLAWFUL_STATUS :
                        error.getResponse().getStatus());
            }
        } else if(error.getKind() == RetrofitError.Kind.CONVERSION) {
            conversion(error);
        }
        else {
            onFailure(error, error.getResponse() == null ? UNLAWFUL_STATUS :
                    error.getResponse().getStatus());
        }
    }

    public final void conversion(RetrofitError error) {
        onConvertFailed(error);
    }

    *//**
     * 网络或服务器错误
     *//*
    protected boolean onNetworkError(RetrofitError error) {
        return false;
    }

    *//**
     * 成功回调，如果该方法中抛出未知的错误，则回调到onFailure()中
     *//*
    protected abstract void onSuccess(RESPONSE response) throws Exception;

    *//**
     * 错误回调
     *//*
    protected void onFailure(RetrofitError error, int status) {
        onFailure(error);
    }

    *//**
     * 错误回调 //1 业务出错，2 网络出错  3 其它出错
     *//*
    protected void onFailure(RetrofitError error) {
        //MaterialToast.makeText(AppStructure.getInstance().getContext(), AppStructure.getInstance().getContext().getString(R.string.xs_no_network)).show();
    }

    *//**
     * 数据解析错误
     * @param
     *//*
    protected void onConvertFailed(RetrofitError error) {

    }*/
    public void onStart() {

    }



    //200 正常，get请求
    //200 创建成功，post请求
    //204  修改，删除成功，put，delete请求
    @Override
    public void onResponse(Call<RESPONSE> var1, Response<RESPONSE> var2){

        if(var2.isSuccessful()){

            if(var2.body()!=null){
                //添加timeout跳转
                if(var2.body().code==401){
                    LogUtils.e("test","401");
                    EventBus.getDefault().post(new SessionTimeOutEvent(-1));
                    return;
                }

                if(var2.body().getCode()== ApiErrorCode.ERROR_SUCCESS)
                    onSuccess(var2.body());
                else
                    onFailure(var2.body().message,1);
               // onSuccess(var2.body());
            }

            else
                onFailure("数据解析出错",1);
        }else{
            try{
                onFailure(var2.errorBody().string(),var2.code()==504? 3:2);

            }catch (Exception e){
                onFailure("加载数据出错",2);
            }
        }
    }

    @Override
    public void onFailure(Call<RESPONSE> var1, Throwable var2){
        onFailure(var2.getMessage(),3);
    }

    protected abstract void onSuccess(RESPONSE response);

    //1 业务出错，2 http出错   3 网络出错

    protected abstract void onFailure(String error,int type);
}
