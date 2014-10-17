package com.croquis.crary.dialog;

import android.content.Context;

public class Utils {
	public static String getAppName(Context context) {
		String packageName = context.getPackageName();
		int resId = context.getResources().getIdentifier("app_name", "string", packageName);
		return resId == 0 ? "" : context.getString(resId);
	}
}
