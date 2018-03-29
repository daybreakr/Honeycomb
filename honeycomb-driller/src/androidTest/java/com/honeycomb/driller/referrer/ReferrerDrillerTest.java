package com.honeycomb.driller.referrer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.honeycomb.driller.referrer.IReferrerDriller.DrillReferrerCallback;
import com.honeycomb.log.HLog;
import com.honeycomb.util.AsyncResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ReferrerDrillerTest {
    private static final String TAG = "ReferrerDrillerTest";

    private static final String LANDING_PAGE = "http://192.168.1.155/honeycomb/test/referrer-driller";
    private static final String TARGET_PACKAGE = "com.ted.android";

    @Before
    public void setUp() throws Exception {
        HLog.setUnifiedLogger(TAG, true);
    }

    @Test
    public void testDrillReferrerWithHttpUrlDriller() {
        Context context = InstrumentationRegistry.getTargetContext();
        IReferrerDriller referrerDriller = ReferrerDrillerFactory.createHttpDriller(context);
        testDrillReferrer(referrerDriller, true);
    }

    @Test
    public void testDrillReferrerWithWebViewUrlDriller() {
        Context context = InstrumentationRegistry.getTargetContext();
        IReferrerDriller referrerDriller = ReferrerDrillerFactory.createWebViewDriller(context);
        testDrillReferrer(referrerDriller, true);
    }

    @Test
    public void testDrillReferrerWithMockSuccessfulDriller() {
        Context context = InstrumentationRegistry.getTargetContext();
        IReferrerDriller referrerDriller = ReferrerDrillerFactory.createMockSuccessfulDriller(context, 1000, "referrer");
        testDrillReferrer(referrerDriller, true);
    }

    @Test
    public void testDrillReferrerWithMockFailureDriller() {
        Context context = InstrumentationRegistry.getTargetContext();
        IReferrerDriller referrerDriller = ReferrerDrillerFactory.createMockFailureDriller(context, 1000);
        testDrillReferrer(referrerDriller, false);
    }

    private void testDrillReferrer(IReferrerDriller referrerDriller, final boolean expectedSuccess) {
        final AsyncResult<Void> result = new AsyncResult<>();

        referrerDriller.drillReferrer(TARGET_PACKAGE, LANDING_PAGE, new DrillReferrerCallback() {
            @Override
            public void onDrillFinished(String packageName, String clickUrl, String referrer) {
                if (expectedSuccess) {
                    if (LANDING_PAGE.equals(clickUrl) && TARGET_PACKAGE.equals(packageName)) {
                        result.success();
                    } else {
                        result.fail("Drill finished, but callback data mismatched."
                                + " expect package: " + TARGET_PACKAGE
                                + ", expect url: " + LANDING_PAGE
                                + ", found package: " + packageName
                                + ", found url: " + clickUrl);
                    }
                } else {
                    result.fail("Expected failure but got successful.");
                }
            }

            @Override
            public void onDrillFailed(String packageName, String clickUrl, int errorCode) {
                if (expectedSuccess) {
                    result.fail("Drill failed, reason: " + translateErrorCode(errorCode));
                } else {
                    result.success();
                }
            }
        });

        result.await();

        if (!result.isSuccessful()) {
            fail(result.getErrorString());
        }
    }

    private static String translateErrorCode(int errorCode) {
        switch (errorCode) {
            case IReferrerDriller.ERROR_PACKAGE_MISMATCH:
                return "ERROR_PACKAGE_MISMATCH";
            case IReferrerDriller.ERROR_NO_REFERRER:
                return "ERROR_NO_REFERRER";
            case IReferrerDriller.ERROR_REDIRECT_ERROR:
                return "ERROR_REDIRECT_ERROR";
            case IReferrerDriller.ERROR_UNKNOWN:
            default:
                return "ERROR_UNKNOWN";
        }
    }
}
