package com.croquis.crary.app;

import android.app.ListActivity;

public class CraryListActivity extends ListActivity {
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
