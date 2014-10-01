package com.croquis.crary.RestClient;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.croquis.crary.RestClient.CraryRestClient.OnRequestComplete;
import com.croquis.crary.RestClient.CraryRestClient.RestError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

public class CraryRestClientImplApacheTest extends AndroidTestCase {
	private CraryRestClientImplApache mImplApache;
	private String mBaseUrl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mImplApache = new CraryRestClientImplApache(getContext());
		assertNotNull(mImplApache.mContext);
		assertNotNull(mImplApache.mHandler);
		assertNotNull(mImplApache.mClient);

		// It should be changed to your url
		mBaseUrl = "http://192.168.23.7:3000/";
	}

	@LargeTest
	public void testGet() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		mImplApache.get(mBaseUrl + "echo", null, new CraryRestClient.OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(0, result.length());
				countDownLatch.countDown();
			}
		}, JSONObject.class);

		countDownLatch.await();
	}

	@LargeTest
	public void testGetWithParameters() throws JSONException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		JSONObject params = new JSONObject();
		params.put("message", "hello");
		mImplApache.get(mBaseUrl + "echo", params, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(CraryRestClient.RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(1, result.length());
				assertEquals("hello", result.optString("response"));
				countDownLatch.countDown();
			}
		}, JSONObject.class);

		countDownLatch.await();
	}

	@LargeTest
	public void testPost() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		JSONObject parameters = null;
		mImplApache.post(mBaseUrl + "echo", parameters, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(0, result.length());
				countDownLatch.countDown();
			}
		}, JSONObject.class);

		countDownLatch.await();
	}

	@LargeTest
	public void testWithParameters() throws JSONException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		JSONObject parameters = new JSONObject();
		parameters.put("message", "hello");
		mImplApache.post(mBaseUrl + "echo", parameters, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(1, result.length());
				assertEquals("hello", result.optString("response"));
				countDownLatch.countDown();
			}
		}, JSONObject.class);

		countDownLatch.await();
	}

	@LargeTest
	public void testSession() throws JSONException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		final JSONObject parameters = new JSONObject();
		parameters.put("data", "croquis");
		mImplApache.post(mBaseUrl + "setData", parameters, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				mImplApache.get(mBaseUrl + "getData", parameters, new OnRequestComplete<JSONObject>() {
					@Override
					public void onComplete(CraryRestClient.RestError error, JSONObject result) {
						assertNull(error);
						assertEquals(1, result.length());
						assertEquals("croquis", result.optString("data"));
						countDownLatch.countDown();
					}
				}, JSONObject.class);
			}
		}, JSONObject.class);

		countDownLatch.await();
	}

	@LargeTest
	public void testPostWithGZippedParameters() throws JSONException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		JSONObject parameters = new JSONObject();
		parameters.put("message", "hello");
		mImplApache.postGzip(mBaseUrl + "echo", parameters, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(1, result.length());
				assertEquals("hello", result.optString("response"));
				countDownLatch.countDown();
			}
		}, JSONObject.class);

		countDownLatch.await();
	}
}
