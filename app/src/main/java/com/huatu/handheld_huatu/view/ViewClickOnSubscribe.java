package com.huatu.handheld_huatu.view;

import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static com.huatu.utils.Preconditions.checkUiThread;


/**
 * Created by cjx on 2018\10\26 0026.
 */

public final class ViewClickOnSubscribe implements Observable.OnSubscribe<Void> {
    final View view;

    public ViewClickOnSubscribe(View view) {
        this.view = view;
    }

    @Override public void call(final Subscriber<? super Void> subscriber) {
        checkUiThread();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                }
            }
        };
        view.setOnClickListener(listener);

        subscriber.add(new MainThreadSubscription() {
            @Override protected void onUnsubscribe() {
                view.setOnClickListener(null);
            }
        });
    }
}