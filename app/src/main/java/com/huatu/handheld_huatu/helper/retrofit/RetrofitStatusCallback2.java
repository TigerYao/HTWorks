package com.huatu.handheld_huatu.helper.retrofit;



import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.event.SessionTimeOutEvent;
import com.huatu.handheld_huatu.mvpmodel.BaseResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/8/4.
 *
   但是在Retrofit 2.0中，不管 response 是否能被解析。onResponse总是会被调用。但是在结果不能被解析的情况下，response.body()会返回null。别忘了处理这种情况。
   如果response存在什么问题，比如404什么的，onResponse也会被调用。你可以从response.errorBody().string()中获取错误信息的主体。
 *
 *
 */
public abstract class RetrofitStatusCallback2<T extends BaseResponse> implements Callback<T> {


    //200 正常，get请求
    //200 创建成功，post请求
    //204  修改，删除成功，put，delete请求
    @Override
    public void onResponse(Call<T> var1, Response<T> var2){

        if(var2.isSuccessful()){
           if(var2.body()!=null){
                 //添加timeout跳转
                 if(var2.body().code==401){
                     EventBus.getDefault().post(new SessionTimeOutEvent(-1));
                     return;
                 }

                 if(var2.body().code== ApiErrorCode.ERROR_SUCCESS)
                    onSuccess(var2);
                 else
                    onFailure(var2.body().message,3);
            }
            else
                onFailure("数据解析出错",1);
        }else{
            try{
                onFailure(var2.errorBody().string(),2);

            }catch (Exception e){
                onFailure("加载数据出错",2);
            }
        }
    }

    @Override
    public void onFailure(Call<T> var1, Throwable var2){
        onFailure(var2.getMessage(),2);
    }

    protected abstract void onSuccess(Response<T> response);


    //1,数据解析出错，2，抛出的异常
    protected abstract void onFailure(String error,int type);
}
