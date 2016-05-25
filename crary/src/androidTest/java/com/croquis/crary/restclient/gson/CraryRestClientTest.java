package com.croquis.crary.restclient.gson;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.croquis.crary.TestConfig;
import com.croquis.crary.restclient.CraryRestClient;
import com.croquis.crary.restclient.CraryRestClient.OnRequestComplete;
import com.croquis.crary.restclient.CraryRestClient.RestError;
import com.croquis.crary.restclient.CraryRestClientAttachment;
import com.croquis.crary.util.CraryIso9601DateFormat;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class CraryRestClientTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CraryRestClient.sharedClient(getContext()).clearSession();
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

    private static class UnderlineConvertObject {
        int userId;
        String fullName;
        String mPhoneNumber;
    }

    private static class PostAttachmentsResult {
        String a;
        int b;
        Sub c;
        File f1;
        File f2;

        private static class Sub {
            String d;
            int e;
        }

        private static class File {
            String fileName;
            int size;
            String type;
        }
    }

    private static class DateRequest {
        Date message;
    }

    private static class DateResult {
        Date d;
    }

    @LargeTest
    public void testGet() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
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
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
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
    public void testGetEscapeParameters() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
        JsonObject parameters = new JsonObject();
        parameters.add("message", new JsonPrimitive("M%<>?="));
        restClient.get("ping", parameters, PingResult.class, new OnRequestComplete<PingResult>() {
            @Override
            public void onComplete(RestError error, PingResult result) {
                assertNull(error);
                assertEquals("M%<>?=", result.response);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    @LargeTest
    public void testPost() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
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
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
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
    public void testPostEscapeParameters() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
        JsonObject parameters = new JsonObject();
        parameters.add("message", new JsonPrimitive("M%<>?="));
        restClient.post("ping", parameters, PingResult.class, new OnRequestComplete<PingResult>() {
            @Override
            public void onComplete(RestError error, PingResult result) {
                assertNull(error);
                assertEquals("M%<>?=", result.response);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    @LargeTest
    public void testSession() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
        JsonObject parameters = new JsonObject();
        parameters.add("data", new JsonPrimitive("croquis"));
        restClient.post("setData", parameters, DataResult.class, new OnRequestComplete<DataResult>() {
            @Override
            public void onComplete(RestError error, DataResult result) {
                restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
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
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
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
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);

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
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);

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
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);

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

    @LargeTest
    public void testUnderlineConvert() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);

        // check request
        UnderlineConvertObject req = new UnderlineConvertObject();
        req.userId = 123;
        req.fullName = "Crary";
        req.mPhoneNumber = "1-234-5678";
        restClient.post("echo", req, JsonObject.class, new OnRequestComplete<JsonObject>() {
            @Override
            public void onComplete(RestError error, JsonObject result) {
                assertNull(error);
                assertNotNull(result);
                assertEquals(123, result.get("user_id").getAsInt());
                assertEquals("Crary", result.get("full_name").getAsString());
                assertEquals("1-234-5678", result.get("phone_number").getAsString());
                countDownLatch.countDown();
            }
        });

        // check response
        JsonObject parameters = new JsonObject();
        parameters.addProperty("user_id", 123);
        parameters.addProperty("full_name", "Crary");
        parameters.addProperty("phone_number", "1-234-5678");
        restClient.post("echo", parameters, UnderlineConvertObject.class, new OnRequestComplete<UnderlineConvertObject>() {
            @Override
            public void onComplete(RestError error, UnderlineConvertObject result) {
                assertNull(error);
                assertNotNull(result);
                assertEquals(123, result.userId);
                assertEquals("Crary", result.fullName);
                assertEquals("1-234-5678", result.mPhoneNumber);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    @LargeTest
    public void testPostAttachments() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        final CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);

        JsonObject parameters = new JsonObjectBuilder()
                .add("a", "message")
                .add("b", 5)
                .add("c", new JsonObjectBuilder()
                        .add("d", "hello")
                        .add("e", 9)
                        .build())
                .build();
        CraryRestClientAttachment[] attachments = {
                CraryRestClientAttachment.byteArray("f1", new byte[]{1, 2, 3}, "image/jpeg", "photo.jpg"),
                CraryRestClientAttachment.byteArray("f2", new byte[]{4, 5, 6, 7, 8, 9, 10}, "audio/mpeg", "sound.mp3"),
        };

        restClient.post("echo", parameters, Arrays.asList(attachments), PostAttachmentsResult.class, new OnRequestComplete<PostAttachmentsResult>() {
            @Override
            public void onComplete(RestError error, PostAttachmentsResult result) {
                assertNull(error);

                assertEquals("message", result.a);
                assertEquals(5, result.b);

                assertNotNull(result.c);
                assertEquals("hello", result.c.d);
                assertEquals(9, result.c.e);

                assertNotNull(result.f1);
                assertEquals("photo.jpg", result.f1.fileName);
                assertEquals(3, result.f1.size);
                assertEquals("image/jpeg", result.f1.type);

                assertNotNull(result.f2);
                assertEquals("sound.mp3", result.f2.fileName);
                assertEquals(7, result.f2.size);
                assertEquals("audio/mpeg", result.f2.type);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    @LargeTest
    public void testDate() throws InterruptedException, ParseException {
        final CountDownLatch countDownLatch = new CountDownLatch(4);

        CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);

        subTestDate(restClient, countDownLatch, "2014-11-25T10:30:05.010Z", "2014-11-25T10:30:05.010Z");
        subTestDate(restClient, countDownLatch, "2014/11/25 10:30:05", "2014-11-25T10:30:05.000Z");
        subTestDate(restClient, countDownLatch, "2014/11/25 10:30:05:010", "2014-11-25T10:30:05.010Z");

        DateRequest parameters = new DateRequest();
        parameters.message = new Date(Date.UTC(114, 10, 25, 10, 30, 05) + 10);
        restClient.post("ping", parameters, PingResult.class, new OnRequestComplete<PingResult>() {
            @Override
            public void onComplete(RestError error, PingResult result) {
                assertNull(error);
                assertEquals("2014-11-25T10:30:05.010Z", result.response);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    private void subTestDate(CraryRestClient restClient, final CountDownLatch countDownLatch, String send, final String expected) {
        restClient.post("echo", JsonObjectBuilder.build("d", send), DateResult.class, new OnRequestComplete<DateResult>() {
            @Override
            public void onComplete(RestError error, DateResult result) {
                assertNull(error);
                assertEquals(expected, CraryIso9601DateFormat.format(result.d));
                countDownLatch.countDown();
            }
        });
    }

    @LargeTest
    public void testError() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(3);

        CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
        subTestError(restClient, countDownLatch,
                new JsonObjectBuilder().add("error", "MyError").build(),
                new RestError(400, "MyError", null));
        subTestError(restClient, countDownLatch,
                new JsonObjectBuilder().add("status", 405).add("error", "MyError").build(),
                new RestError(405, "MyError", null));
        subTestError(restClient, countDownLatch,
                new JsonObjectBuilder().add("status", 405).add("error", "MyError").add("description", "Something wrong").build(),
                new RestError(405, "MyError", "Something wrong"));

        countDownLatch.await();
    }

    private void subTestError(CraryRestClient restClient, final CountDownLatch countDownLatch, JsonObject send, final RestError expected) {
        restClient.get("error", send, JsonObject.class, new OnRequestComplete<JsonObject>() {
            @Override
            public void onComplete(RestError error, JsonObject result) {
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
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
        restClient.get("plain", null, JsonObject.class, new OnRequestComplete<JsonObject>() {
            @Override
            public void onComplete(RestError error, JsonObject result) {
                assertNotNull(error);
                assertEquals(RestError.UNRECOGNIZABLE_RESULT, error);
                countDownLatch.countDown();
            }
        });
        restClient.get("plain", JsonObjectBuilder.build("status", 500), JsonObject.class, new OnRequestComplete<JsonObject>() {
            @Override
            public void onComplete(RestError error, JsonObject result) {
                assertNotNull(error);
                assertEquals(RestError.UNRECOGNIZABLE_RESULT, error);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }
}
