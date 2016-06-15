package com.croquis.crary.sample;

import android.test.ActivityInstrumentationTestCase2;

public class SubscribeTest extends ActivityInstrumentationTestCase2<SubscribeTestActivity> {
    private SubscribeTestActivity mActivity;

    public SubscribeTest() {
        super(SubscribeTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mActivity = getActivity();
    }

    public void testInitial() {
        assertNull(mActivity.mSaved);
    }

    public void testCall() {
        mActivity.mSubject.onNext("hello");
        assertEquals("hello", mActivity.mSaved);
        mActivity.unsubscribe();
        mActivity.mSubject.onNext("hello2");
        assertEquals("hello", mActivity.mSaved);
    }
}
