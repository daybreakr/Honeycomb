package com.google.honeycomb.common.log;

/**
 * Prints or saves the log message
 */
public interface ILogPrinter {

    void printLog(LogLevel level, String tag, String message);
}
