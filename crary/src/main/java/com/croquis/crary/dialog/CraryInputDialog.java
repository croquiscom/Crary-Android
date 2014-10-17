package com.croquis.crary.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class CraryInputDialog {
	public static void selectSingle(Context context, int itemsId, DialogInterface.OnClickListener listener) {
		selectSingle(context, context.getResources().getStringArray(itemsId), listener);
	}

	public static void selectSingle(Context context, String[] items, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(Utils.getAppName(context));
		dialogBuilder.setItems(items, listener);
		dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogBuilder.show();
	}
}
