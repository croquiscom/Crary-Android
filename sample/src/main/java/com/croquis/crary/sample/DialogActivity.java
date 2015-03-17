package com.croquis.crary.sample;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.croquis.crary.app.CraryActionBarActivity;
import com.croquis.crary.dialog.CraryInputDialog;
import com.croquis.crary.dialog.CraryMessageBox;
import com.croquis.crary.dialog.ProgressDialogHelper;

public class DialogActivity extends CraryActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);

		setTitle("Crary/Dialog");
	}

	public void onShowAlert(View view) {
		CraryMessageBox.alert(this, "message", "title", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				CraryMessageBox.alert(DialogActivity.this, "done");
			}
		});
	}

	public void onShowConfirm(View view) {
		CraryMessageBox.confirmYesNo(this, "Really?", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				CraryMessageBox.alert(DialogActivity.this, which == DialogInterface.BUTTON_POSITIVE ? "You confirmed" : "You cancelled");
			}
		});
	}

	public void onShowProgress(View view) {
		ProgressDialog progressDialog = ProgressDialogHelper.show(this);
		progressDialog.setCancelable(true);
	}

	public void onShowSelectItem(View view) {
		final String[] items = {"foo", "bar", "baz"};

		CraryInputDialog.selectSingle(this, items, "Select One", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				CraryMessageBox.alert(DialogActivity.this, items[which]);
			}
		});
	}
}
