package com.croquis.crary.dialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.croquis.crary.R;

public class CraryMessageBox {
	public static void alert(Context context, String message) {
		alert(context, message, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	public static void alert(Context context, String message, OnClickListener done) {
		alert(context, message, getAppName(context), done);
	}

	public static void alert(Context context, String message, String title) {
		alert(context, message, title, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	public static void alert(Context context, String message, String title, OnClickListener done) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(title);
		dialogBuilder.setMessage(message);
		dialogBuilder.setCancelable(false);
		dialogBuilder.setPositiveButton(context.getString(R.string.OK), done);

		dialogBuilder.show();
	}

	public static void confirm(Context context, String message, String yes, String no, OnClickListener done) {
		String title = getAppName(context);
		confirm(context, message, title, yes, no, done);
	}

	public static void confirm(Context context, String message, String title, String yes, String no, OnClickListener done) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(title);
		dialogBuilder.setMessage(message);
		dialogBuilder.setCancelable(false);
		dialogBuilder.setPositiveButton(yes, done);
		dialogBuilder.setNegativeButton(no, done);

		dialogBuilder.show();
	}

	public static void confirmOkCancel(Context context, String message, OnClickListener done) {
		confirm(context, message, context.getString(R.string.OK), context.getString(R.string.Cancel), done);
	}

	public static void confirmOkCancel(Context context, String message, String title, OnClickListener done) {
		confirm(context, message, title, context.getString(R.string.OK), context.getString(R.string.Cancel), done);
	}

	public static void confirmYesNo(Context context, String message, OnClickListener done) {
		confirm(context, message, context.getString(R.string.Yes), context.getString(R.string.No), done);
	}

	public static void confirmYesNo(Context context, String message, String title, OnClickListener done) {
		confirm(context, message, title, context.getString(R.string.Yes), context.getString(R.string.No), done);
	}

	public static ProgressDialog progress(Context context) {
		return progress(context, context.getString(R.string.Wait));
	}
	public static ProgressDialog progress(Context context, String message) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.show();
		return progressDialog;
	}

	public static void selectItem(Context context, String[] items, OnClickListener listener) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(getAppName(context));
		dialogBuilder.setItems(items, listener);
		dialogBuilder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogBuilder.show();
	}

	private static String getAppName(Context context) {
		String packageName = context.getPackageName();
		int resId = context.getResources().getIdentifier("app_name", "string", packageName);
		return resId == 0 ? "" : context.getString(resId);
	}
}
