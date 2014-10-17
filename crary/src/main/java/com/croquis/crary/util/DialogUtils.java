package com.croquis.crary.util;

import android.content.Context;

public class DialogUtils {
	public static String getAppName(Context context) {
		String packageName = context.getPackageName();
		int resId = context.getResources().getIdentifier("app_name", "string", packageName);
		return resId == 0 ? "" : context.getString(resId);
	}
}
