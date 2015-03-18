package com.croquis.crary.restclient.json;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.croquis.crary.restclient.CraryRestClient;

import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

public class CraryRestClientResolveTest extends AndroidTestCase {
	// It should be changed to your url
	private final static String TEST_BASE_URL = "http://192.168.56.1:3000/";

	@LargeTest
	public void testParent() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(TEST_BASE_URL + "sub/");
		restClient.get("../ping", null, new CraryRestClient.OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(CraryRestClient.RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(0, result.length());
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testAbsolute() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(TEST_BASE_URL + "sub/");
		restClient.get("/ping", null, new CraryRestClient.OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(CraryRestClient.RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(0, result.length());
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testFull() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl("http://google.com/");
		restClient.get(TEST_BASE_URL + "ping", null, new CraryRestClient.OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(CraryRestClient.RestError error, JSONObject result) {
				assertNull(error);
				assertEquals(0, result.length());
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}
}
