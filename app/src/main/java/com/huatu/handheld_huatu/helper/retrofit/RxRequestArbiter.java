package com.huatu.handheld_huatu.helper.retrofit;

import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Response;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;

/**
 * Created by Administrator on 2019\4\2 0002.
 */


public class RxRequestArbiter<T> extends AtomicBoolean implements Subscription, Producer {
    private final Call<T> call;
    private final Subscriber<? super Response<T>> subscriber;

    RxRequestArbiter(Call<T> call, Subscriber<? super Response<T>> subscriber) {
        this.call = call;
        this.subscriber = subscriber;
    }

    public void request(long n) {
        if (n < 0L) {
            throw new IllegalArgumentException("n < 0: " + n);
        } else if (n != 0L) {
            if (this.compareAndSet(false, true)) {
                try {
                    Response<T> response = this.call.execute();
                    if (!this.subscriber.isUnsubscribed()) {
                        this.subscriber.onNext(response);
                    }
                } catch (Throwable var4) {
                    Exceptions.throwIfFatal(var4);
                    if (!this.subscriber.isUnsubscribed()) {
                        this.subscriber.onError(var4);
                    }

                    return;
                }

                if (!this.subscriber.isUnsubscribed()) {
                    this.subscriber.onCompleted();
                }

            }
        }
    }

    public void unsubscribe() {
        this.call.cancel();
    }

    public boolean isUnsubscribed() {
        return this.call.isCanceled();
    }
}

