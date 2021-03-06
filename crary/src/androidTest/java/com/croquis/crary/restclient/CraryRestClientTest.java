package com.croquis.crary.restclient;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.croquis.crary.TestConfig;

import java.util.concurrent.CountDownLatch;

public class CraryRestClientTest extends AndroidTestCase {
    @LargeTest
    public void testBinary() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        CraryRestClient restClient = CraryRestClient.sharedClient(getContext());
        restClient.setBaseUrl(TestConfig.TEST_BASE_URL);
        byte[] data = new byte[]{
                1, 6, 9, 2, 4
        };
        restClient.post("binary", data, new CraryRestClient.OnRequestComplete<byte[]>() {
            @Override
            public void onComplete(CraryRestClient.RestError error, byte[] result) {
                assertNull(error);
                assertEquals(5, result.length);
                assertEquals(1, result[0]);
                assertEquals(7, result[1]);
                assertEquals(11, result[2]);
                assertEquals(5, result[3]);
                assertEquals(8, result[4]);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }
}
