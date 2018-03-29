package com.honeycomb.driller.url.impl;

import com.honeycomb.driller.url.IUrlDriller;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseUrlDriller implements IUrlDriller {
    private Listener mListener;
    private String mUserAgent = null;
    private int mDrillDepth = -1; // no limited
    private boolean mRetrieveResponseString = false;
    private boolean mLoadSource = false;

    private String mDrillingUrl;

    private AtomicBoolean mIsStopped = new AtomicBoolean(true);
    private boolean mIsDestroyed = false;

    @Override
    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void setUserAgent(String userAgent) {
        mUserAgent = userAgent;
    }

    @Override
    public void setDrillDepth(int drillDepth) {
        mDrillDepth = drillDepth;
    }

    @Override
    public void setRetrieveResponseString(boolean retrieveResponseString) {
        mRetrieveResponseString = retrieveResponseString;
    }

    @Override
    public void setLoadSource(boolean loadSource) {
        mLoadSource = loadSource;
    }

    @Override
    public void drill(String url) {
        if (url == null || url.trim().length() <= 0) {
            invokeFail(url, new IllegalArgumentException("URL is null or empty"));
            return;
        }

        // Would always fail if driller had already been destroyed.
        if (mIsDestroyed) {
            invokeFail(url, new IllegalStateException("Driller been destroyed."));
            return;
        }

        // Uses the atomic boolean to make sure that only one drilling task was running.
        if (mIsStopped.compareAndSet(true, false)) {
            mDrillingUrl = url;
            onStartDrill(url);
        } else if (!url.equals(mDrillingUrl)) {
            invokeFail(url, new IllegalStateException("Another drilling task was in progress."));
        }
    }

    @Override
    public void stop() {
        if (isDestroyed()) {
            return;
        }

        mDrillingUrl = null;

        if (!isStopped()) {
            onStopDrill();
            mIsStopped.set(true);
        }
    }

    @Override
    public void destroy() {
        if (isDestroyed()) {
            return;
        }

        mDrillingUrl = null;

        onDestroyDriller();
        mIsDestroyed = true;
    }

    protected String getUserAgent() {
        return mUserAgent;
    }

    protected int getDrillDepth() {
        return mDrillDepth;
    }

    protected boolean isRetrieveResponseString() {
        return mRetrieveResponseString;
    }

    protected boolean isLoadSource() {
        return mLoadSource;
    }

    protected abstract void onStartDrill(String url);

    protected void onStopDrill() {
    }

    protected void onDestroyDriller() {
    }

    protected boolean isStopped() {
        return mIsStopped.get();
    }

    protected boolean isDestroyed() {
        return mIsDestroyed;
    }

    //==============================================================================================
    //region // Callback helpers

    protected void invokeDrillStart(String url) {
        if (mListener != null) {
            mListener.onDrillerStart(url);
        }
    }

    protected void invokeDrillRedirect(String url) {
        if (mListener != null) {
            mListener.onDrillerRedirect(url);
        }
    }

    protected void invokeDrillFinish(String url, String responseString) {
        mIsStopped.set(true);
        mDrillingUrl = null;

        if (mListener != null) {
            mListener.onDrillerFinish(url, responseString);
        }
    }

    protected void invokeFail(String url, Exception exception) {
        mIsStopped.set(true);
        mDrillingUrl = null;

        if (mListener != null) {
            mListener.onDrillerFail(url, exception);
        }
    }

    //endregion
    //==============================================================================================
}
