package com.croquis.crary.app;

import android.support.v7.app.ActionBarActivity;

public class CraryActionBarActivity extends ActionBarActivity {
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
