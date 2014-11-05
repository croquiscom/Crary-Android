package com.croquis.crary.sample;

import android.app.Activity;
import android.widget.Toast;

import com.croquis.crary.app.CraryApplication;

public class Sample extends CraryApplication {
	@Override
	protected void onDidEnterForeground(Activity activity) {
		super.onDidEnterForeground(activity);

		Toast.makeText(this, "The app goes to the foreground", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDidEnterBackground(Activity activity) {
		super.onDidEnterBackground(activity);

		Toast.makeText(this, "The app goes to the background", Toast.LENGTH_LONG).show();
	}
}
