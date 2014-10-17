package com.croquis.crary.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.croquis.crary.R;
import com.croquis.crary.util.DialogUtils;

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
		alert(context, message, DialogUtils.getAppName(context), done);
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

	public static void alert(Context context, int messageId) {
		alert(context, context.getString(messageId));
	}

	public static void alert(Context context, int messageId, OnClickListener done) {
		alert(context, context.getString(messageId), done);
	}

	public static void alert(Context context, int messageId, int titleId) {
		alert(context, context.getString(messageId), context.getString(titleId));
	}

	public static void alert(Context context, int messageId, int titleId, OnClickListener done) {
		alert(context, context.getString(messageId), context.getString(titleId), done);
	}

	public static void confirm(Context context, String message, String yes, String no, OnClickListener done) {
		String title = DialogUtils.getAppName(context);
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

	public static void confirmOkCancel(Context context, int messageId, OnClickListener done) {
		confirmOkCancel(context, context.getString(messageId), done);
	}

	public static void confirmYesNo(Context context, String message, OnClickListener done) {
		confirm(context, message, context.getString(R.string.Yes), context.getString(R.string.No), done);
	}

	public static void confirmYesNo(Context context, int messageId, OnClickListener done) {
		confirmYesNo(context, messageId, done);
	}

	public static void confirmYesNo(Context context, String message, String title, OnClickListener done) {
		confirm(context, message, title, context.getString(R.string.Yes), context.getString(R.string.No), done);
	}
}
