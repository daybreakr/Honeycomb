package com.honeycomb.driller.url;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.honeycomb.driller.url.impl.HttpUrlDriller;
import com.honeycomb.driller.url.impl.WebViewUrlDriller;
import com.honeycomb.log.HLog;
import com.honeycomb.util.AsyncResult;
import com.honeycomb.util.StringUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class UrlDrillerTest {
    private static final String TAG = "UrlDrillerTest";

    private static final String LANDING_PAGE = "http://192.168.1.155/honeycomb/test/url-driller";
    private static final String ENDING = "bing.com";

    @Before
    public void setUp() throws Exception {
        HLog.setUnifiedLogger(TAG, true);
    }

    @Test
    public void testHttpUrlDriller() {
        HttpUrlDriller driller = new HttpUrlDriller();
        driller.setRetrieveResponseString(true);

        // HttpUrlDriller can only drill onto man machine check page.
        testDriller(driller, true, "man_machine_check.html");
    }

    @Test
    public void testWebViewUrlDriller() {
        Context context = InstrumentationRegistry.getTargetContext();
        WebViewUrlDriller driller = new WebViewUrlDriller(context);
        driller.setRetrieveResponseString(false);

        // WebViewUrlDriller should drill onto the final page.
        testDriller(driller, false, ENDING);
    }

    @Test
    public void testWebViewUrlDriller_LoadSource() {
        Context context = InstrumentationRegistry.getTargetContext();
        WebViewUrlDriller driller = new WebViewUrlDriller(context);
        driller.setLoadSource(true);

        // WebViewUrlDriller should drill onto the final page.
        testDriller(driller, true, ENDING);
    }

    private void testDriller(IUrlDriller driller, final boolean retrieveResponse,
                             final String expectedEnding) {
        final AsyncResult<Void> result = new AsyncResult<>();

        driller.setListener(new IUrlDriller.Listener() {
            @Override
            public void onDrillerStart(String url) {
            }

            @Override
            public void onDrillerRedirect(String url) {
            }

            @Override
            public void onDrillerFinish(String url, String responseString) {
                if (retrieveResponse && StringUtils.isEmpty(responseString)) {
                    result.fail("Failed to retrieve response of " + url);
                    return;
                }

                if (expectedEnding != null && !url.endsWith(expectedEnding)) {
                    result.fail("Wrong redirection"
                            + ", expected ending: " + expectedEnding
                            + ", found: " + url);
                }

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
