package com.google.honeycomb.driller;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.honeycomb.common.log.HLog;
import com.google.honeycomb.common.util.AsyncResult;
import com.google.honeycomb.driller.referrer.HttpUrlDrillerFactory;
import com.google.honeycomb.driller.referrer.IReferrerDriller;
import com.google.honeycomb.driller.referrer.IReferrerDriller.DrillReferrerCallback;
import com.google.honeycomb.driller.referrer.IUrlDrillerFactory;
import com.google.honeycomb.driller.referrer.ReferrerDriller;
import com.google.honeycomb.driller.referrer.WebViewUrlDrillerFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ReferrerDrillerTest {
    private static final String TAG = "ReferrerDrillerTest";

    private static final String LANDING_PAGE = "http://192.168.1.155";
    private static final String TARGET_PACKAGE = "com.ted.android";

    @Test
    public void testDrillReferrerWithWebViewUrlDriller() {
        testDrillReferrer(new WebViewUrlDrillerFactory());
    }

    @Test
    public void testDrillReferrerWithHttpUrlDriller() {
        testDrillReferrer(new HttpUrlDrillerFactory());
    }

    private void testDrillReferrer(IUrlDrillerFactory urlDrillerFactory) {
        HLog.setUnifiedLogger(TAG, false);

        final AsyncResult<Void> result = new AsyncResult<>();

        Context context = InstrumentationRegistry.getTargetContext();
        ReferrerDriller driller = new ReferrerDriller(context, urlDrillerFactory);
        driller.drillReferrer(TARGET_PACKAGE, LANDING_PAGE, new DrillReferrerCallback() {
            @Override
            public void onDrillFinished(String packageName, String clickUrl, String referrer) {
                if (LANDING_PAGE.equals(clickUrl) && TARGET_PACKAGE.equals(packageName)) {
                    result.success();
                } else {
                    result.fail("Drill finished, but callback data mismatched."
                            + " expect package: " + TARGET_PACKAGE
                            + ", expect url: " + LANDING_PAGE
                            + ", found package: " + packageName
                            + ", found url: " + clickUrl);
                }
            }

            @Override
            public void onDrillFailed(String packageName, String clickUrl, int errorCode) {
                result.fail("Drill failed, reason: " + translateErrorCode(errorCode));
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
