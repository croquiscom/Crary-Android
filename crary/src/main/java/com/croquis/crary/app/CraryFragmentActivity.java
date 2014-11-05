package com.croquis.crary.app;

import android.support.v4.app.FragmentActivity;

public class CraryFragmentActivity extends FragmentActivity {
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
