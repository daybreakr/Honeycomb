package com.google.honeycomb.log;

/**
 * Accept/Deny all log request depends a simple boolean value.
 */
public class SimpleLoggableStrategy implements ILoggableStrategy {
    private final boolean mIsLoggable;

    public SimpleLoggableStrategy(boolean isLoggable) {
        mIsLoggable = isLoggable;
    }

    @Override
    public boolean isLoggable(LogLevel level, String tag) {
        return mIsLoggable;
    }
}
