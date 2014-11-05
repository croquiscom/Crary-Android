package com.croquis.crary.app;

import android.app.Activity;
import android.app.Application;

import rx.subjects.PublishSubject;

public class CraryApplication extends Application {
	public PublishSubject<Activity> didEnterForeground = PublishSubject.create();
	public PublishSubject<Activity> didEnterBackground = PublishSubject.create();

	private Activity mActiveActivity;

	void activityStarted(Activity activity) {
		if (mActiveActivity == null) {
			onDidEnterForeground(activity);
		}
		mActiveActivity = activity;
	}

	void activityStopped(Activity activity) {
		if (mActiveActivity == activity) {
			mActiveActivity = null;
			onDidEnterBackground(activity);
		}
	}

	protected void onDidEnterForeground(Activity activity) {
		didEnterForeground.onNext(activity);
	}

	protected void onDidEnterBackground(Activity activity) {
		didEnterBackground.onNext(activity);
	}

	public boolean isForeground() {
		return mActiveActivity != null;
	}

	public Activity getActiveActivity() {
		return mActiveActivity;
	}
}
