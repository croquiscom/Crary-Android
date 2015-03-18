package com.croquis.crary.restclient.gson;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.croquis.crary.restclient.CraryRestClient;

import java.util.concurrent.CountDownLatch;

public class CraryRestClientResolveTest extends AndroidTestCase {
	// It should be changed to your url
	private final static String TEST_BASE_URL = "http://192.168.56.1:3000/";

	private static class PingResult {
		String response;
	}

	@LargeTest
	public void testParent() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(TEST_BASE_URL + "sub/");
		restClient.get("../ping", null, PingResult.class, new CraryRestClient.OnRequestComplete<PingResult>() {
			@Override
			public void onComplete(CraryRestClient.RestError error, PingResult result) {
				assertNull(error);
				assertNull(result.response);
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
		restClient.get("/ping", null, PingResult.class, new CraryRestClient.OnRequestComplete<PingResult>() {
			@Override
			public void onComplete(CraryRestClient.RestError error, PingResult result) {
				assertNull(error);
				assertNull(result.response);
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
		restClient.get(TEST_BASE_URL + "ping", null, PingResult.class, new CraryRestClient.OnRequestComplete<PingResult>() {
			@Override
			public void onComplete(CraryRestClient.RestError error, PingResult result) {
				assertNull(error);
				assertNull(result.response);
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}
}
