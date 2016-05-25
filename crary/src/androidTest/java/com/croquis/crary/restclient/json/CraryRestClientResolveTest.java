package com.croquis.crary.restclient.json;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.croquis.crary.TestConfig;
import com.croquis.crary.restclient.CraryRestClient;

import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

public class CraryRestClientResolveTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CraryRestClient.sharedClient(getContext()).clearSession();
    }

    @LargeTest
    public void testParent() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL + "sub/");
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
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL + "sub/");
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
        restClient.get(TestConfig.TEST_BASE_URL + "ping", null, new CraryRestClient.OnRequestComplete<JSONObject>() {
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
