package com.croquis.crary.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.croquis.crary.RestClient.CraryRestClient;
import com.croquis.crary.RestClient.CraryRestClientImplApache;
import com.croquis.crary.dialog.CraryMessageBox;

import org.json.JSONObject;

import java.io.IOException;

public class CraryRestClientImplApacheActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crary_rest_client_impl_apache);

		setTitle("CraryRestClientIMplApache");
	}

	public void onGetClick(View view) throws IOException {
		CraryRestClientImplApache implApache = new CraryRestClientImplApache(this);
		implApache.get("http://192.168.23.7:3000/echo/", null, new CraryRestClient.OnRequestComplete<JSONObject>(){
			@Override
			public void onComplete(CraryRestClient.RestError restError, JSONObject jsonObject) {
				if (restError != null) {
					CraryMessageBox.alert(CraryRestClientImplApacheActivity.this, restError.getMessage());
					return;
				}

				CraryMessageBox.alert(CraryRestClientImplApacheActivity.this, "success");
			}
		}, JSONObject.class);
	}

}
