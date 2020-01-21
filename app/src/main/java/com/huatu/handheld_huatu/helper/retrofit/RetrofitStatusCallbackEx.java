package com.huatu.handheld_huatu.helper.retrofit;

import android.app.Activity;


import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.mvpmodel.BaseResponse;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Response;


public abstract class RetrofitStatusCallbackEx<T extends BaseResponse> extends RetrofitStatusCallback2<T> {


    int type=1;
    WeakReference<AbsFragment> tmpFragment;
    WeakReference<Activity> tmpActivity;

    public Activity getRefActivity(){
        if(null==tmpActivity||null==tmpActivity.get()) return null;
        return tmpActivity.get();
    }
    public RetrofitStatusCallbackEx(AbsFragment curFragment) {
        tmpFragment =new WeakReference<AbsFragment>(curFragment);
        type=1;
    }


    public RetrofitStatusCallbackEx(Activity curActivity) {
        tmpActivity =new WeakReference<Activity>(curActivity);
        type=2;
    }

    //200 正常，get请求
    //200 创建成功，post请求
    //204  修改，删除成功，put，delete请求
    @Override
    public void onResponse(Call<T> var1, Response<T> var2){

        if(type==1){
            if (tmpFragment == null || tmpFragment.get()==null||tmpFragment.get().isFragmentFinished()) return;
        }
        else {
            if(tmpActivity==null||tmpActivity.get()==null||tmpActivity.get().isFinishing())  return;
        }

        super.onResponse(var1, var2);
    }

    @Override
    public void onFailure(Call<T> var1, Throwable var2){
        if(type==1){
            if (tmpFragment == null || tmpFragment.get()==null||tmpFragment.get().isFragmentFinished()) return;
        }
        else {
            if(tmpActivity==null||tmpActivity.get()==null||tmpActivity.get().isFinishing())  return;
        }
        super.onFailure(var1, var2);

    }


}
