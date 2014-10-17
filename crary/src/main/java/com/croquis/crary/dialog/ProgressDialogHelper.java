package com.croquis.crary.dialog;

import android.app.ProgressDialog;
import android.content.Context;

import com.croquis.crary.R;

public class ProgressDialogHelper {
	public static ProgressDialog show(Context context) {
		return show(context, context.getString(R.string.Wait));
	}
	public static ProgressDialog show(Context context, String message) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.show();
		return progressDialog;
	}
}
