package com.croquis.crary.app;

import android.support.v4.app.Fragment;
import android.util.Log;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class CrarySupportFragment extends Fragment {
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    @Override
    public void onDestroy() {
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
