package com.huatu.handheld_huatu.utils;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;


import com.huatu.handheld_huatu.helper.retrofit.RxCallOnSubscribe;
import com.huatu.handheld_huatu.helper.retrofit.RxOperatorMapResponseToBodyOrError;
import com.huatu.handheld_huatu.view.ViewClickOnSubscribe;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;

import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.huatu.utils.Preconditions.checkNotNull;

public class RxUtils {




    public static void unsubscribeIfNotNull(Subscription subscription) {

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) {
            return new CompositeSubscription();
        }

        return subscription;
    }


    @CheckResult
    @NonNull
    public static Observable<Void> clicks(@NonNull View view) {
        checkNotNull(view, "view == null");
        return Observable.create(new ViewClickOnSubscribe(view));
    }

    public static <R> Observable<R> adapt(Call<R> call) {

        Observable<R> observable = Observable.create(new RxCallOnSubscribe(call)).lift(RxOperatorMapResponseToBodyOrError.instance());
        return  observable;
    }
}
