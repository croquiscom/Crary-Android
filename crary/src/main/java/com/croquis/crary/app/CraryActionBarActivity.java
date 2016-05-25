package com.croquis.crary.app;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class CraryActionBarActivity extends ActionBarActivity {
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

    protected <T> void subscribe(Observable<T> observable, Action1<? super T> onNext) {
        mSubscriptions.add(observable.subscribe(onNext, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("Crary", "Reactive Error", throwable);
            }
        }));
    }
}
