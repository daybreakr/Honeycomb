package com.honeycomb.crash.impl;

import com.honeycomb.crash.CrashHandler;
import com.honeycomb.log.HLog;

public class LogCrashHandler extends CrashHandler {
    private static final String TAG = "Crash";
    private static final String DEFAULT_MESSAGE = "FATAL ERROR!";

    private final String mTag;
    private final String mMessage;

    public LogCrashHandler() {
        this(TAG, DEFAULT_MESSAGE);
    }

    public LogCrashHandler(String tag, String message) {
        super(BUILD_IN_MAX_PRIORITY);
        mTag = tag;
        mMessage = message;
    }

    @Override
    public void handleCrash(Throwable throwable, long timestamp) {
        HLog.e(throwable, mTag, mMessage);
    }
}
