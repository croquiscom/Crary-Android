package com.croquis.crary.sample;

import android.os.Bundle;

import com.croquis.crary.app.CraryActivity;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;


public class SubscribeTestActivity extends CraryActivity {
    PublishSubject<String> mSubject = PublishSubject.create();
    String mSaved;

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDisposable = subscribe(mSubject, new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                mSaved = s;
            }
        });
    }

    public void unsubscribe() {
        unsubscribe(mDisposable);
    }
}
