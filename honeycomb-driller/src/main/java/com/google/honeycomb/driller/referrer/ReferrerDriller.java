package com.google.honeycomb.driller.referrer;

import android.content.Context;

import com.google.honeycomb.log.HLog;
import com.google.honeycomb.util.StringUtils;
import com.google.honeycomb.driller.url.IUrlDriller;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReferrerDriller implements IReferrerDriller {
    private static final String TAG = "ReferrerDriller";

    // Maximum redirect times each drill.
    private static final int MAX_REDIRECTS = 25;
    // Maximum drill times each landing page.
    private static final int MAX_DRILLS = 10;

    private final Context mContext;
    private final IUrlDrillerFactory mUrlDrillerFactory;

    public ReferrerDriller(Context context, IUrlDrillerFactory factory) {
        mContext = context.getApplicationContext();
        mUrlDrillerFactory = factory;
    }

    @Override
    public void drillReferrer(String packageName, String clickUrl, DrillReferrerCallback callback) {
        HLog.d(TAG, "Drill referrer for package: %s, landing page: %s", packageName, clickUrl);
        doDrill(packageName, clickUrl, callback, clickUrl, 0);
    }

    private void doDrill(String packageName, String clickUrl, DrillReferrerCallback callback,
                         String drillUrl, int counter) {
        new UrlDrillTask(packageName, clickUrl, callback).start(drillUrl, counter);
    }

    private void invokeDrillFinished(String packageName, String clickUrl, String referrer,
                                     DrillReferrerCallback callback) {
        if (callback != null) {
            callback.onDrillFinished(packageName, clickUrl, referrer);
        }
    }

    private void invokeDrillFailed(String packageName, String clickUrl, int errorCode,
                                   DrillReferrerCallback callback) {
        if (callback != null) {
            callback.onDrillFailed(packageName, clickUrl, errorCode);
        }
    }

    private class UrlDrillTask implements IUrlDriller.Listener {
        private final String mPackageName;
        private final String mClickUrl;
        private final DrillReferrerCallback mCallback;

        private int mCounter;

        private IUrlDriller mUrlDriller;

        private AtomicBoolean mHandled = new AtomicBoolean(false);

        UrlDrillTask(String packageName, String clickUrl, DrillReferrerCallback callback) {
            mPackageName = packageName;
            mClickUrl = clickUrl;
            mCallback = callback;
        }

        void start(String drillUrl, int counter) {
            mCounter = counter;

            mUrlDriller = mUrlDrillerFactory.createUrlDriller(mContext);
            if (mUrlDriller == null) {
                invokeDrillFailed(mPackageName, mClickUrl, ERROR_UNKNOWN, mCallback);
                return;
            }

            mUrlDriller.setListener(this);
            mUrlDriller.setDrillDepth(MAX_REDIRECTS);
            mUrlDriller.setRetrieveResponseString(true);
            mUrlDriller.drill(drillUrl);
        }

        @Override
        public void onDrillerStart(String url) {
            handleUrl(url);
        }

        @Override
        public void onDrillerRedirect(String url) {
            handleUrl(url);
        }

        @Override
        public void onDrillerFinish(String url, String response) {
            if (handleUrl(url)) {
                return;
            }

            HLog.d(TAG, "Drill finished, not a market url: %s, response: %s", url, response);

            if (mHandled.compareAndSet(false, true)) {
                // make sure url driller been stopped.
                mUrlDriller.stop();

                String location = ReferrerUtils.retrieveLocation(response);
                if (location != null) {
                    int counter = mCounter + 1;
                    if (counter < MAX_DRILLS) {
                        HLog.v(TAG, "Found location in response, drill it: " + location);
                        doDrill(mPackageName, mClickUrl, mCallback, location, counter);
                    } else {
                        HLog.v(TAG, "Too many re-drills, drill failed.");
                        invokeDrillFailed(mPackageName, mClickUrl, ERROR_REDIRECT_ERROR, mCallback);
                    }
                } else {
                    HLog.v(TAG, "Location not found in response, drill failed.");
                    invokeDrillFailed(mPackageName, mClickUrl, ERROR_REDIRECT_ERROR, mCallback);
                }
            }
        }

        @Override
        public void onDrillerFail(String url, Exception exception) {
            if (mHandled.compareAndSet(false, true)) {
                invokeDrillFailed(mPackageName, mClickUrl, ERROR_REDIRECT_ERROR, mCallback);
            }
        }

        private boolean handleUrl(String url) {
            if (mHandled.get()) {
                // Already been handled before.
                return true;
            }

            if (ReferrerUtils.isMarketUrl(url)) {
                HLog.d(TAG, "Market url detected: " + url);

                if (mHandled.compareAndSet(false, true)) {
                    // Found market url, stop redirecting.
                    mUrlDriller.stop();

                    if (StringUtils.equals(mPackageName, ReferrerUtils.retrievePackageName(url))) {
                        String referrer = ReferrerUtils.retrieveReferrer(url);
                        if (!StringUtils.isEmpty(referrer)) {
                            invokeDrillFinished(mPackageName, mClickUrl, referrer, mCallback);
                        } else {
                            invokeDrillFailed(mPackageName, mClickUrl, ERROR_NO_REFERRER, mCallback);
                        }
                    } else {
                        invokeDrillFailed(mPackageName, mClickUrl, ERROR_PACKAGE_MISMATCH, mCallback);
                    }
                }

                // Url been handled.
                return true;
            }

            return false;
        }
    }
}
