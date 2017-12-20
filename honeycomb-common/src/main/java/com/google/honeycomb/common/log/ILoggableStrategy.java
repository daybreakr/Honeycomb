package com.google.honeycomb.common.log;

/**
 * Determines whether the log should be logged.
 */
public interface ILoggableStrategy {

    boolean isLoggable(LogLevel level, String tag);
}
