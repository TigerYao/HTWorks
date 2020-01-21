package com.huatu.handheld_huatu.helper.retrofit;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2019\4\2 0002.
 */


public class RxOperatorMapResponseToBodyOrError<T> implements Observable.Operator<T, Response<T>> {
    private static final RxOperatorMapResponseToBodyOrError<Object> INSTANCE =
            new RxOperatorMapResponseToBodyOrError<>();

    @SuppressWarnings("unchecked") // Safe because of erasure.
    public static <R> RxOperatorMapResponseToBodyOrError<R> instance() {
        return (RxOperatorMapResponseToBodyOrError<R>) INSTANCE;
    }

    @Override public Subscriber<? super Response<T>> call(final Subscriber<? super T> child) {
        return new Subscriber<Response<T>>(child) {
            @Override public void onNext(Response<T> response) {
                if (response.isSuccessful()) {
                    child.onNext(response.body());
                } else {
                    child.onError(new HttpException(response));
                }
            }

            @Override public void onCompleted() {
                child.onCompleted();
            }

            @Override public void onError(Throwable e) {
                child.onError(e);
            }
        };
    }
}



