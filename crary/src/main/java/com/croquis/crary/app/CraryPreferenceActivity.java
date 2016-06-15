package com.croquis.crary.app;

import android.preference.PreferenceActivity;
import android.util.Log;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class CraryPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onStart() {
        super.onStart();
        ((CraryApplication) getApplication()).activityStarted(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((CraryApplication) getApplication()).activityStopped(this);
    }

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    protected <T> Subscription subscribe(Observable<T> observable, Action1<? super T> onNext) {
        Subscription subscription = observable.subscribe(onNext, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("Crary", "Reactive Error", throwable);
            }
        });
        mSubscriptions.add(subscription);
        return subscription;
    }

    protected void unsubscribe(Subscription subscription) {
        mSubscriptions.remove(subscription);
    }
}
