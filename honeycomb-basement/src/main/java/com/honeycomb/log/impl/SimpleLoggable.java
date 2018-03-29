package com.honeycomb.log.impl;

import com.honeycomb.log.ILoggable;
import com.honeycomb.log.LogLevel;

/**
 * Accept/Deny all log request depends a simple boolean value.
 */
public class SimpleLoggable implements ILoggable {
    private final boolean mIsLoggable;

    public SimpleLoggable(boolean isLoggable) {
        mIsLoggable = isLoggable;
    }

    @Override
    public boolean isLoggable(LogLevel level, String tag) {
        return mIsLoggable;
    }
}
