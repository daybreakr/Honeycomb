package com.honeycomb.driller.referrer.impl;

import com.honeycomb.driller.referrer.IReferrerDriller;

public class MockReferrerDriller implements IReferrerDriller {
    private final boolean mMockSuccessful;
    private final long mDelay;
    private final String mReferrer;

    public MockReferrerDriller(boolean mockSuccessful, long delay, String referrer) {
        mMockSuccessful = mockSuccessful;
        mDelay = Math.max(delay, 0);
        mReferrer = referrer;
    }

    @Override
    public void drillReferrer(String packageName, String clickUrl, DrillReferrerCallback callback) {
        try {
            Thread.sleep(mDelay);
        } catch (InterruptedException ignored) {
        }

        if (callback != null) {
            if (mMockSuccessful) {
                callback.onDrillFinished(packageName, clickUrl, mReferrer);
            } else {
                callback.onDrillFailed(packageName, clickUrl, ERROR_UNKNOWN);
            }
        }
    }
}
