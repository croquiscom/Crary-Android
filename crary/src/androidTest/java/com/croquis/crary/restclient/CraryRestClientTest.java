package com.croquis.crary.restclient;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.croquis.crary.restclient.CraryRestClient.OnRequestComplete;
import com.croquis.crary.restclient.CraryRestClient.RestError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

public class CraryRestClientTest extends AndroidTestCase {
	private String mBaseUrl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// It should be changed to your url
		mBaseUrl = "http://192.168.23.7:3000/";
	}

	@LargeTest
	public void testGet() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		restClient.get("ping", null, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(0, result.length());
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testGetWithParameters() throws JSONException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		JSONObject parameters = new JSONObject();
		parameters.put("message", "hello");
		restClient.get("ping", parameters, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(1, result.length());
				assertEquals("hello", result.optString("response"));
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testPost() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		restClient.post("ping", (JSONObject) null, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(0, result.length());
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testPostWithParameters() throws JSONException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		JSONObject parameters = new JSONObject();
		parameters.put("message", "hello");
		restClient.post("ping", parameters, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(1, result.length());
				assertEquals("hello", result.optString("response"));
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testSession() throws JSONException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		final CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		JSONObject parameters = new JSONObject();
		parameters.put("data", "croquis");
		restClient.post("setData", parameters, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				restClient.setBaseUrl(mBaseUrl);
				restClient.get("getData", null, new OnRequestComplete<JSONObject>() {
					@Override
					public void onComplete(RestError error, JSONObject result) {
						assertNull(error);
						assertEquals(1, result.length());
						assertEquals("croquis", result.optString("data"));
						countDownLatch.countDown();
					}
				});
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testPostWithGZippedParameters() throws JSONException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		final CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		JSONObject parameters = new JSONObject();
		parameters.put("message", "hello");
		restClient.postGzip("ping", parameters, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(1, result.length());
				assertEquals("hello", result.optString("response"));
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}
}
