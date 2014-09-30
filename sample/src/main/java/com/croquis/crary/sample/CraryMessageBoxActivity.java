package com.croquis.crary.sample;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.croquis.crary.dialog.CraryMessageBox;

public class CraryMessageBoxActivity extends ActionBarActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crary_message_box);
	}

	public void onShowAlert(View view) {
		CraryMessageBox.alert(this, "message", "title", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				CraryMessageBox.alert(CraryMessageBoxActivity.this, "done");
			}
		});
	}

	public void onShowConfirm(View view) {
		CraryMessageBox.confirmYesNo(this, "Really?", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int which) {
				CraryMessageBox.alert(CraryMessageBoxActivity.this, which == DialogInterface.BUTTON_POSITIVE ? "You confirmed" : "You cancelled");
			}
		});
	}
}
