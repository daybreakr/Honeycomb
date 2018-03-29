package com.honeycomb.log;

/**
 * Determines whether the log should be logged.
 */
public interface ILoggable {

    boolean isLoggable(LogLevel level, String tag);
}
