package com.croquis.crary.app;

import android.app.Activity;
import android.app.Application;

import rx.subjects.PublishSubject;

/**
 * The base Application class for Croquis Apps.
 * <br>
 * This provides whether the app is on foreground or on background.
 */
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

	/**
	 * Called when the app is going foreground
	 *
	 * @param activity the Activity that makes the app on foreground
	 */
	protected void onDidEnterForeground(Activity activity) {
		didEnterForeground.onNext(activity);
	}

	/**
	 * Called when the app is going background
	 *
	 * @param activity the last Activity when the app goes on background
	 */
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
