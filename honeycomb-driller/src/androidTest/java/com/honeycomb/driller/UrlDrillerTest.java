package com.honeycomb.driller;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.honeycomb.log.HLog;
import com.honeycomb.base.AsyncResult;
import com.honeycomb.driller.url.HttpUrlDriller;
import com.honeycomb.driller.url.IUrlDriller;
import com.honeycomb.driller.url.WebViewUrlDriller;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class UrlDrillerTest {
    private static final String TAG = "UrlDrillerTest";

    private static final String LANDING_PAGE = "http://192.168.1.155";

    @Test
    public void testHttpUrlDriller() {
        HLog.setUnifiedLogger(TAG, false);

        final AsyncResult<Void> result = new AsyncResult<>();

        HttpUrlDriller driller = new HttpUrlDriller();
        driller.setRetrieveResponseString(true);
        driller.setListener(new IUrlDriller.Listener() {
            @Override
            public void onDrillerStart(String url) {
            }

            @Override
            public void onDrillerRedirect(String url) {
            }

            @Override
            public void onDrillerFinish(String url, String responseString) {
                result.success();
            }

            @Override
            public void onDrillerFail(String url, Exception exception) {
                result.fail(exception.getMessage());
            }
        });
        driller.drill(LANDING_PAGE);

        result.await();

        if (!result.isSuccessful()) {
            fail(result.getErrorString());
        }
    }

    @Test
    public void testWebViewUrlDriller() {
        HLog.setUnifiedLogger(TAG, false);

        final AsyncResult<Void> result = new AsyncResult<>();

        Context context = InstrumentationRegistry.getTargetContext();
        WebViewUrlDriller driller = new WebViewUrlDriller(context);
        driller.setRetrieveResponseString(false);
        driller.setListener(new IUrlDriller.Listener() {
            @Override
            public void onDrillerStart(String url) {
            }

            @Override
            public void onDrillerRedirect(String url) {
            }

            @Override
            public void onDrillerFinish(String url, String responseString) {
                result.success();
            }

            @Override
            public void onDrillerFail(String url, Exception exception) {
                result.fail(exception.getMessage());
            }
        });
        driller.drill(LANDING_PAGE);

        result.await();

        if (!result.isSuccessful()) {
            fail(result.getErrorString());
        }
    }
}
