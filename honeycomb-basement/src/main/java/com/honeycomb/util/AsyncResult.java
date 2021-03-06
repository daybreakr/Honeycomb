package com.honeycomb.util;

import java.util.concurrent.CountDownLatch;

public class AsyncResult<T> {
    private boolean mSuccess;
    private T mResult;
    private Object mErrorCookie;

    private CountDownLatch mLatch;

    public AsyncResult() {
        mLatch = new CountDownLatch(1);
    }

    public void await() {
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            mSuccess = false;
            mErrorCookie = e.getMessage();
        }
    }

    public final void success() {
        success(null);
    }

    public final void success(T result) {
        mSuccess = true;
        mResult = result;
        mErrorCookie = null;

        mLatch.countDown();
    }

    public void fail() {
        fail(null);
    }

    public void fail(Object errorCookie) {
        mSuccess = false;
        mResult = null;
        mErrorCookie = errorCookie;

        mLatch.countDown();
    }

    public boolean isSuccessful() {
        return mSuccess;
    }

    public T getResult() {
        return mResult;
    }

    public Object getErrorCookie() {
        return mErrorCookie;
    }

    public String getErrorString() {
        return mErrorCookie != null ? mErrorCookie.toString() : "";
    }
}
