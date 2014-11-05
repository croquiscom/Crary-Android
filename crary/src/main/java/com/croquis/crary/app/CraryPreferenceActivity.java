package com.croquis.crary.app;

import android.preference.PreferenceActivity;

public class CraryPreferenceActivity extends PreferenceActivity {
	@Override
	public void onStart() {
		super.onStart();
		((CraryApplication) getApplication()).activityStarted(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		((CraryApplication) getApplication()).activityStopped(this);
	}
}
