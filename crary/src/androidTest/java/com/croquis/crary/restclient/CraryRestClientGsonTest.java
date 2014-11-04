package com.croquis.crary.restclient;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.croquis.crary.restclient.CraryRestClient.OnRequestComplete;
import com.croquis.crary.restclient.CraryRestClient.RestError;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.concurrent.CountDownLatch;

public class CraryRestClientGsonTest extends AndroidTestCase {
	private String mBaseUrl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// It should be changed to your url
		mBaseUrl = "http://192.168.23.7:3000/";
	}

	private static class PingResult {
		String response;
	}

	private static class DataResult {
		String data;
	}

	@LargeTest
	public void testGet() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		restClient.get("ping", null, PingResult.class, new OnRequestComplete<PingResult>() {
			@Override
			public void onComplete(RestError error, PingResult result) {
				assertNull(error);
				assertNull(result.response);
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testGetWithParameters() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		JsonObject parameters = new JsonObject();
		parameters.add("message", new JsonPrimitive("hello"));
		restClient.get("ping", parameters, PingResult.class, new OnRequestComplete<PingResult>() {
			@Override
			public void onComplete(RestError error, PingResult result) {
				assertNull(error);
				assertEquals("hello", result.response);
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
		restClient.post("ping", null, PingResult.class, new OnRequestComplete<PingResult>() {
			@Override
			public void onComplete(RestError error, PingResult result) {
				assertNull(error);
				assertNull(result.response);
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testPostWithParameters() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		JsonObject parameters = new JsonObject();
		parameters.add("message", new JsonPrimitive("hello"));
		restClient.post("ping", parameters, PingResult.class, new OnRequestComplete<PingResult>() {
			@Override
			public void onComplete(RestError error, PingResult result) {
				assertNull(error);
				assertEquals("hello", result.response);
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testSession() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		final CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		JsonObject parameters = new JsonObject();
		parameters.add("data", new JsonPrimitive("croquis"));
		restClient.post("setData", parameters, DataResult.class, new OnRequestComplete<DataResult>() {
			@Override
			public void onComplete(RestError error, DataResult result) {
				restClient.setBaseUrl(mBaseUrl);
				restClient.get("getData", null, DataResult.class, new OnRequestComplete<DataResult>() {
					@Override
					public void onComplete(RestError error, DataResult result) {
						assertNull(error);
						assertEquals("croquis", result.data);
						countDownLatch.countDown();
					}
				});
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testPostWithGZippedParameters() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		final CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		JsonObject parameters = new JsonObject();
		parameters.add("message", new JsonPrimitive("hello"));
		restClient.postGzip("ping", parameters, PingResult.class, new OnRequestComplete<PingResult>() {
			@Override
			public void onComplete(RestError error, PingResult result) {
				assertNull(error);
				assertEquals("hello", result.response);
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}
}
