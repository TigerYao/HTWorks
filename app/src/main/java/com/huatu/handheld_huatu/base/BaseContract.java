package com.huatu.handheld_huatu.base;

import android.content.Context;

/**
 * Created by Administrator on 2019\8\23 0023.
 */

public class BaseContract {
    public interface BasePresenter<T extends BaseContract.BaseView> {

        void attachView(T view);

        void detachView();
    }

    public interface BaseView {
  /*      Context getContext();

        //显示进度中
        void showLoading();

        //隐藏进度
        void hideLoading();



        *//**
         * 绑定生命周期
         *
         * @param <T>
         * @return
         *//*
        <T> LifecycleTransformer<T> bindToLife();*/
    }
}
