package com.google.honeycomb.driller.url;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.honeycomb.common.log.HLog;
import com.google.honeycomb.common.util.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class WebViewUrlDriller extends BaseUrlDriller {
    private static final String TAG = "WebViewUrlDriller";

    // Timeout milliseconds each redirecting request.
    private static final int REDIRECTING_TIMEOUT = 30000; // 30 seconds
    // Wait for a moment when page loading finished because of the refresh delay.
    private static final int DRILL_FINISHED_DELAY = 10000; // 10 seconds

    private Context mContext;

    private DrillWebView mDrillWebView;
    private DrillWebViewClient mDrillWebViewClient;

    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private boolean mLandingPageStarted = false;

    public WebViewUrlDriller(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onStartDrill(final String url) {
        invokeDrillStart(url);

        // Check if drilling been cancelled before startup.
        if (isStopped()) {
            HLog.v(TAG, "Drill been stopped.");
            return;
        }

        // Web view must be created in main thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doDrill(url);
            }
        });

        // Initialize timeout thread and timeout handler.
        if (mHandlerThread == null || mHandler == null) {
            mHandlerThread = new TimeoutThread();
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }

        // Schedule landing page starts timeout timer.
        scheduleTimeout(REDIRECTING_TIMEOUT);
    }

    @Override
    public void onStopDrill() {
        // Stop web view
        if (mDrillWebView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDrillWebView.stopLoading();
                }
            });
        }

        removeAllCallbacks();
    }

    @Override
    protected void onDestroyDriller() {
        // destroy web view
        if (mDrillWebView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDrillWebView.destroy();
                    mDrillWebView = null;
                }
            });
        }
        mDrillWebViewClient = null;

        // destroy timeout timer
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        mHandler = null;

        mContext = null;
    }

    private void doDrill(String url) {
        // Initialize web view and web view client if needed.
        if (mDrillWebView == null) {
            mDrillWebView = new DrillWebView(mContext);
            // XXX: set user agent
        }
        if (mDrillWebViewClient == null) {
            mDrillWebViewClient = new DrillWebViewClient();
            mDrillWebView.setWebViewClient(mDrillWebViewClient);
        }

        mLandingPageStarted = false;

        // Start drilling.
        mDrillWebView.loadUrl(url);
    }

    private class DrillWebViewClient extends WebViewClient {
        private String mDrillingUrl;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mDrillingUrl = url;

            if (!mLandingPageStarted) {
                mLandingPageStarted = true;
                HLog.v(TAG, "Landing: " + url);
            } else {
                HLog.v(TAG, " - Redirecting: " + url);
                invokeDrillRedirect(url);
            }

            // Reset timeout timer when new page started.
            scheduleTimeout(REDIRECTING_TIMEOUT);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (isStopped()) {
                return;
            }
            HLog.v(TAG, " - Finished: " + url);

            scheduleOnFinish(DRILL_FINISHED_DELAY);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (isStopped()) {
                return;
            }
            HLog.v(TAG, " - Fail: " + description);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mDrillWebView != null) {
                        mDrillWebView.stopLoading();
                    }
                }
            });

            invokeFail(failingUrl, new Exception(description));
        }

        String getDrillingUrl() {
            return mDrillingUrl;
        }
    }

    private void scheduleTimeout(long timeout) {
        removeAllCallbacks();
        if (mHandler != null) {
            mHandler.postDelayed(mOnTimeout, timeout);
        }
    }

    private void scheduleOnFinish(long delay) {
        removeAllCallbacks();
        if (mHandler != null) {
            mHandler.postDelayed(mOnFinished, delay);
        }
    }

    private void removeAllCallbacks() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mOnTimeout);
            mHandler.removeCallbacks(mOnFinished);
        }
    }

    private Runnable mOnTimeout = new Runnable() {
        @Override
        public void run() {
            HLog.v(TAG, " - Timeout");

            stop();

            String url = mDrillWebViewClient != null ? mDrillWebViewClient.getDrillingUrl() : null;
            invokeFail(url, new Exception("timeout loading " + url));
        }
    };

    private Runnable mOnFinished = new Runnable() {
        @Override
        public void run() {
            HLog.v(TAG, " - Done");

            stop();

            String url = mDrillWebViewClient != null ? mDrillWebViewClient.getDrillingUrl() : null;
            if (StringUtils.isEmpty(url)) {
                invokeDrillFinish(url, null);
            } else {
                invokeFail(url, new Exception("url is null"));
            }
        }
    };

    private static void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    private static class DrillWebView extends WebView {
        private static boolean sIsShadowWebViewOpened = false;
        private boolean mIsDestroyed = false;

        public DrillWebView(Context context) {
            super(context.getApplicationContext());

            setPluginState(false);
            setupAccessibility();
            setupJavascript();

            if (!sIsShadowWebViewOpened) {
                showShadowWebView(context);
                sIsShadowWebViewOpened = true;
            }
        }

        @Override
        public void destroy() {
            mIsDestroyed = true;

            detachFromParent(this);
            removeAllViews();

            super.destroy();
        }

        private void setPluginState(boolean on) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (on) {
                    getSettings().setPluginState(WebSettings.PluginState.ON);
                } else {
                    getSettings().setPluginState(WebSettings.PluginState.OFF);
                }
            }
        }

        @SuppressLint("SetJavaScriptEnabled")
        private void setupJavascript() {
            getSettings().setJavaScriptEnabled(true);
            getSettings().setDomStorageEnabled(true);
        }

        private void setupAccessibility() {
            getSettings().setAllowFileAccess(false);
            getSettings().setDatabaseEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getSettings().setAllowContentAccess(false);
            }
        }

        private void showShadowWebView(Context context) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                try {
                    WebView webView = new WebView(context.getApplicationContext());
                    webView.setBackgroundColor(0);

                    webView.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.width = 1;
                    params.height = 1;

                    params.type = WindowManager.LayoutParams.TYPE_TOAST;
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;

                    params.format = PixelFormat.TRANSPARENT;
                    params.gravity = Gravity.START | Gravity.TOP;

                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    if (wm != null) {
                        wm.addView(webView, params);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private static void detachFromParent(View view) {
            if (view == null || view.getParent() == null) {
                return;
            }
            if ((view.getParent()) instanceof ViewGroup) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }
    }

    private static class TimeoutThread extends HandlerThread {
        private final static AtomicInteger sNextSerialNumber = new AtomicInteger(0);

        private static int serialNumber() {
            return sNextSerialNumber.getAndIncrement();
        }

        TimeoutThread() {
            super("TimeoutThread-" + serialNumber());
        }
    }
}
