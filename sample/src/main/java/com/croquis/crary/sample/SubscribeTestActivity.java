package com.croquis.crary.sample;

import android.os.Bundle;

import com.croquis.crary.app.CraryActivity;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class SubscribeTestActivity extends CraryActivity {
	PublishSubject<String> mSubject = PublishSubject.create();
	String mSaved;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		subscribe(mSubject, new Action1<String>() {
			@Override
			public void call(String s) {
				mSaved = s;
			}
		});
	}
}
