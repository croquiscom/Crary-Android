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

import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class MainActivity extends CraryActionBarActivity {
	PublishSubject<Void> mReactiveError = PublishSubject.create();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		subscribe(mReactiveError, new Action1<Void>() {
			@Override
			public void call(Void aVoid) {
				throw new RuntimeException("Error while handling event");
			}
		});

		ArrayList<String> titleList = new ArrayList<String>();
		titleList.add("Dialog");
		titleList.add("Open Browser");
		titleList.add("Reactive Error");

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
				} else if (i == 2) {
					mReactiveError.onNext(null);
				}
			}
		});
	}
}
