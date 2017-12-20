package com.google.honeycomb.log;

/**
 * Determines whether the log should be logged.
 */
public interface ILoggableStrategy {

    boolean isLoggable(LogLevel level, String tag);
}
