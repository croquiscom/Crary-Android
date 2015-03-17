package com.croquis.crary.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class CraryInputDialog {
	public static void selectSingle(Context context, int itemIds, DialogInterface.OnClickListener listener) {
		selectSingle(context, context.getResources().getStringArray(itemIds), listener);
	}

	public static void selectSingle(Context context, String[] items, DialogInterface.OnClickListener listener) {
		new AlertDialog.Builder(context)
				.setTitle(Utils.getAppName(context))
				.setItems(items, listener)
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
	}
}
