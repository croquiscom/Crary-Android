package com.croquis.crary.restclient;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.croquis.crary.restclient.CraryRestClient.OnRequestComplete;
import com.croquis.crary.restclient.CraryRestClient.RestError;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class CraryRestClientGsonTest extends AndroidTestCase {
	private String mBaseUrl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// It should be changed to your url
		mBaseUrl = "http://192.168.56.1:3000/";
	}

	private static class PingResult {
		String response;
	}

	private static class DataResult {
		String data;
	}

	private static class TestObject {
		String a;
		int b;
		boolean c;
		TestObject d;

		TestObject(String a, int b, boolean c, TestObject d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		void test(String a, int b, boolean c, TestObject d) {
			assertEquals(a, this.a);
			assertEquals(b, this.b);
			assertEquals(c, this.c);
			if (d == null) {
				assertNull(this.d);
			} else {
				assertNotNull(this.d);
				this.d.test(d.a, d.b, d.c, d.d);
			}
		}
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

	@LargeTest
	public void testObject() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(2);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);

		TestObject parameters = new TestObject("message", 5, true, new TestObject("sub", 0, false, null));
		OnRequestComplete<TestObject> check = new OnRequestComplete<TestObject>() {
			@Override
			public void onComplete(RestError error, TestObject result) {
				assertNull(error);
				assertNotNull(result);
				result.test("message", 5, true, new TestObject("sub", 0, false, null));
				countDownLatch.countDown();
			}
		};
		restClient.get("echo", parameters, TestObject.class, check);
		restClient.post("echo", parameters, TestObject.class, check);

		countDownLatch.await();
	}

	@LargeTest
	public void testList() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);

		ArrayList<TestObject> parameters = new ArrayList<TestObject>();
		parameters.add(new TestObject("obj1", 11, false, null));
		parameters.add(new TestObject("obj2", 22, true, new TestObject("sub", 0, false, null)));
		parameters.add(new TestObject("obj3", 33, false, null));
		OnRequestComplete<ArrayList<TestObject>> check = new OnRequestComplete<ArrayList<TestObject>>() {
			@Override
			public void onComplete(RestError error, ArrayList<TestObject> result) {
				assertNull(error);
				assertNotNull(result);
				assertEquals(3, result.size());
				result.get(0).test("obj1", 11, false, null);
				result.get(1).test("obj2", 22, true, new TestObject("sub", 0, false, null));
				result.get(2).test("obj3", 33, false, null);
				countDownLatch.countDown();
			}
		};
		restClient.post("echo", parameters, new TypeToken<ArrayList<TestObject>>() {
		}.getType(), check);

		countDownLatch.await();
	}

	@LargeTest
	public void testListWithGet() throws InterruptedException {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
		restClient.setBaseUrl(mBaseUrl);

		JsonObject parameters = new JsonObject();
		JsonArray list = new JsonArray();
		list.add(new JsonPrimitive("message"));
		list.add(new JsonPrimitive(5));
		list.add(new JsonPrimitive(true));
		parameters.add("data", list);
		restClient.get("echo", parameters, JsonObject.class, new OnRequestComplete<JsonObject>() {
			@Override
			public void onComplete(RestError error, JsonObject result) {
				JsonArray data = result.getAsJsonArray("data");
				assertEquals("message", data.get(0).getAsString());
				assertEquals(5, data.get(1).getAsInt());
				assertEquals(true, data.get(2).getAsBoolean());
				countDownLatch.countDown();
			}
		});

		countDownLatch.await();
	}
}
