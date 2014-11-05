package com.croquis.crary.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.croquis.crary.app.CraryActionBarActivity;

import java.util.ArrayList;

public class MainActivity extends CraryActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ArrayList<String> titleList = new ArrayList<String>();
		titleList.add("Dialog");
		titleList.add("Open Browser");

		ListView listView = (ListView) findViewById(R.id.listView);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titleList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if (i == 0) {
					Intent intent = new Intent(MainActivity.this, DialogActivity.class);
					startActivity(intent);
				} else if (i == 1) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
					startActivity(intent);
				}
			}
		});
	}
}
