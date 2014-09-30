package com.croquis.crary.sample;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.croquis.crary.dialog.CraryMessageBox;

public class CraryMessageBoxActivity extends ActionBarActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crary_message_box);

		findViewById(R.id.alert).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CraryMessageBox.alert(CraryMessageBoxActivity.this, "alert");
			}
		});

		findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CraryMessageBox.confirm(CraryMessageBoxActivity.this, "confirm", "Yes", "No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							// do something
						}

						dialogInterface.dismiss();
					}
				});
			}
		});

		findViewById(R.id.confirmOkCancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CraryMessageBox.confirmOkCancel(CraryMessageBoxActivity.this, "confirmOkCancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							// do something
						}

						dialogInterface.dismiss();
					}
				});
			}
		});

		findViewById(R.id.confirmYesNo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CraryMessageBox.confirmYesNo(CraryMessageBoxActivity.this, "confirmYesNo", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							// do something
						}

						dialogInterface.dismiss();
					}
				});

			}
		});
	}
}
