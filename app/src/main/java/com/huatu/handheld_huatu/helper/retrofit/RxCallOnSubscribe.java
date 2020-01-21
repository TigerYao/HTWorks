package com.huatu.handheld_huatu.helper.retrofit;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2019\4\2 0002.
 */


public class RxCallOnSubscribe<T> implements Observable.OnSubscribe<Response<T>> {
    private final Call<T> originalCall;

    public  RxCallOnSubscribe(Call<T> originalCall) {
        this.originalCall = originalCall;
    }

    public void call(Subscriber<? super Response<T>> subscriber) {
        Call<T> call = this.originalCall.clone();
        RxRequestArbiter<T> requestArbiter = new RxRequestArbiter(call, subscriber);
        subscriber.add(requestArbiter);
        subscriber.setProducer(requestArbiter);


    }


}

