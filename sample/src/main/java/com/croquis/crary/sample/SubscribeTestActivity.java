package com.croquis.crary.sample;

import android.os.Bundle;

import com.croquis.crary.app.CraryActivity;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class SubscribeTestActivity extends CraryActivity {
    PublishSubject<String> mSubject = PublishSubject.create();
    String mSaved;

    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSubscription = subscribe(mSubject, new Action1<String>() {
            @Override
            public void call(String s) {
                mSaved = s;
            }
        });
    }

    public void unsubscribe() {
        unsubscribe(mSubscription);
    }
}
