package com.croquis.crary.app;

import android.app.Activity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * The base Activity class for Croquis Apps.
 * <br>
 * You must use this class for CraryApplication to work correctly.
 */
public class CraryActivity extends Activity {
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

    private CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    protected <T> Disposable subscribe(Observable<T> observable, Consumer<? super T> onNext) {
        Disposable disposable = observable.subscribe(onNext, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e("Crary", "Reactive Error", throwable);
            }
        });

        mDisposable.add(disposable);
        return disposable;
    }

    protected void unsubscribe(Disposable disposable) {
        mDisposable.delete(disposable);
    }
}
