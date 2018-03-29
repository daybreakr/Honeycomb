package com.honeycomb.driller.referrer.impl;

import android.content.Context;

import com.honeycomb.driller.referrer.IReferrerDriller;
import com.honeycomb.driller.url.IUrlDriller;
import com.honeycomb.log.HLog;
import com.honeycomb.util.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReferrerDrillerImpl implements IReferrerDriller {
    private static final String TAG = "ReferrerDrillerImpl";

    // Maximum redirect times each drill.
    private static final int MAX_REDIRECTS = 25;
    // Maximum drill times each landing page.
    private static final int MAX_DRILLS = 10;

    private final Context mContext;
    private final IUrlDrillerFactory mUrlDrillerFactory;

    public ReferrerDrillerImpl(Context context, IUrlDrillerFactory urlDrillerFactory) {
        mContext = context.getApplicationContext();
        mUrlDrillerFactory = urlDrillerFactory;
    }

    @Override
    public void drillReferrer(String packageName, String clickUrl, DrillReferrerCallback callback) {
        HLog.d(TAG, "Drill referrer for package: %s, landing page: %s", packageName, clickUrl);
        doDrill(packageName, clickUrl, callback);
    }

    private void doDrill(String packageName, String clickUrl, DrillReferrerCallback callback) {
        new DrillTask(packageName, clickUrl, callback).drill(clickUrl);
    }

    private void doDrill(DrillTask parent, String landingUrl) {
        new DrillTask(parent).drill(landingUrl);
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

    private class DrillTask implements IUrlDriller.Listener {
        private final String mPackageName;
        private final String mClickUrl;
        private final DrillReferrerCallback mCallback;
        private int mCounter;

        private IUrlDriller mUrlDriller;

        private AtomicBoolean mHandled = new AtomicBoolean(false);

        DrillTask(String packageName, String clickUrl, DrillReferrerCallback callback) {
            mPackageName = packageName;
            mClickUrl = clickUrl;
            mCallback = callback;
            mCounter = 1;
        }

        DrillTask(DrillTask parent) {
            mPackageName = parent.mPackageName;
            mClickUrl = parent.mClickUrl;
            mCallback = parent.mCallback;
            mCounter = parent.mCounter + 1;
        }

        void drill(String landingUrl) {
            mUrlDriller = mUrlDrillerFactory.createUrlDriller(mContext);
            if (mUrlDriller == null) {
                invokeDrillFailed(mPackageName, mClickUrl, ERROR_CREATING_URL_DRILLER, mCallback);
                return;
            }

            mUrlDriller.setListener(this);
            mUrlDriller.setDrillDepth(MAX_REDIRECTS);
            mUrlDriller.setRetrieveResponseString(true);
            mUrlDriller.setLoadSource(false);
            mUrlDriller.drill(landingUrl);
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

            HLog.v(TAG, "Drill finished, not a market url: %s, response:\n%s", url, response);

            if (mHandled.compareAndSet(false, true)) {
                // make sure url driller been stopped.
                mUrlDriller.stop();

                String location = ReferrerUtils.retrieveLocation(response);
                if (location != null) {
                    if (mCounter < MAX_DRILLS) {
                        HLog.d(TAG, "Found location in response, drill it: " + location);
                        doDrill(this, location);
                    } else {
                        HLog.d(TAG, "Too many re-drills, drill failed.");
                        invokeDrillFailed(mPackageName, mClickUrl, ERROR_REDIRECT_ERROR, mCallback);
                    }
                } else {
                    HLog.d(TAG, "Location not found in response, drill failed.");
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
