package com.huatu.music.player;

import android.content.Context;

/**
 * Created by Administrator on 2019\11\28 0028.
 */

public class BgPlayContract {



    public interface BasePresenter<T extends BgPlayContract.BaseView> {

        void attachView(T view);

        void detachView();


    }

    public interface BaseView {

        long getCurrentPosition();

        boolean isPlaying();

        //暂停
        void doPauseAction();

        //继续播放
        void doResumeAction();



        Context getContext();

      /*  *//**//**
         * 绑定生命周期
         *
         * @param <T>
         * @return
         *//**//*
        <T> LifecycleTransformer<T> bindToLife();*/
    }
}
