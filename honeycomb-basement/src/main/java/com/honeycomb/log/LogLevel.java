package com.honeycomb.log;

import android.util.Log;

public enum LogLevel {
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR),
    ASSERT(Log.ASSERT),

    // Un-expected priority which equals to ASSERT.
    SUPPRESS(Log.ASSERT); // Largest level, suppress all logs.

    // Priority value matching logcat's priority.
    private final int mPriority;

    LogLevel(int priority) {
        mPriority = priority;
    }

    public int getPriority() {
        return mPriority;
    }
}
