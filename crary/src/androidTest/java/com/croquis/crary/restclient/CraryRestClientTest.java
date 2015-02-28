package com.croquis.crary.restclient;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.croquis.crary.restclient.CraryRestClient.OnRequestComplete;
import com.croquis.crary.restclient.CraryRestClient.RestError;
import com.croquis.crary.restclient.json.JSONObjectBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class CraryRestClientTest extends AndroidTestCase {
	private String mBaseUrl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// It should be changed to your url
		mBaseUrl = "http://192.168.56.1:3000/";
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

	@LargeTest
	public void testPostAttachments() throws JSONException, InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		final CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);

		JSONObject parameters = new JSONObjectBuilder()
				.add("a", "message")
				.add("b", 5)
				.add("c", new JSONObjectBuilder()
						.add("d", "hello")
						.add("e", 9)
						.build())
				.build();
		CraryRestClientAttachment[] attachments = {
				CraryRestClientAttachment.byteArray("f1", new byte[]{1, 2, 3}, "image/jpeg", "photo.jpg"),
				CraryRestClientAttachment.byteArray("f2", new byte[]{4, 5, 6, 7, 8, 9, 10}, "audio/mpeg", "sound.mp3"),
		};

		restClient.post("echo", parameters, Arrays.asList(attachments), new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNull(error);

				assertEquals(5, result.length());
				assertEquals("message", result.optString("a"));
				assertEquals(5, result.optInt("b"));

				assertNotNull(result.optJSONObject("c"));
				assertEquals("hello", result.optJSONObject("c").optString("d"));
				assertEquals(9, result.optJSONObject("c").optInt("e"));

				assertNotNull(result.optJSONObject("f1"));
				assertEquals("photo.jpg", result.optJSONObject("f1").optString("file_name"));
				assertEquals(3, result.optJSONObject("f1").optInt("size"));
				assertEquals("image/jpeg", result.optJSONObject("f1").optString("type"));

				assertNotNull(result.optJSONObject("f2"));
				assertEquals("sound.mp3", result.optJSONObject("f2").optString("file_name"));
				assertEquals(7, result.optJSONObject("f2").optInt("size"));
				assertEquals("audio/mpeg", result.optJSONObject("f2").optString("type"));
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}

	@LargeTest
	public void testError() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(3);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		subTestError(restClient, countDownLatch,
				new JSONObjectBuilder().add("error", "MyError").build(),
				new RestError(400, "MyError", null));
		subTestError(restClient, countDownLatch,
				new JSONObjectBuilder().add("status", 405).add("error", "MyError").build(),
				new RestError(405, "MyError", null));
		subTestError(restClient, countDownLatch,
				new JSONObjectBuilder().add("status", 405).add("error", "MyError").add("description", "Something wrong").build(),
				new RestError(405, "MyError", "Something wrong"));

		countDownLatch.await();
	}

	private void subTestError(CraryRestClient restClient, final CountDownLatch countDownLatch, JSONObject send, final RestError expected) {
		restClient.get("error", send, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNotNull(error);
				assertEquals(expected.code, error.code);
				assertEquals(expected.error, error.error);
				assertEquals(expected.description, error.description);
				countDownLatch.countDown();
			}
		});
	}

	@LargeTest
	public void testUnrecognizableResult() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(2);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);
		restClient.get("plain", null, new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNotNull(error);
				assertEquals(RestError.UNRECOGNIZABLE_RESULT, error);
				countDownLatch.countDown();
			}
		});
		restClient.get("plain", JSONObjectBuilder.build("status", 500), new OnRequestComplete<JSONObject>() {
			@Override
			public void onComplete(RestError error, JSONObject result) {
				assertNotNull(error);
				assertEquals(RestError.UNRECOGNIZABLE_RESULT, error);
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}
}
