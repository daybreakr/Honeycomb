package com.google.honeycomb.log;

import android.util.Log;

/**
 * Print log message with logcat.
 */
public class LogcatLogPrinter implements ILogPrinter {

    @Override
    public void printLog(LogLevel level, String tag, String message) {
        Log.println(level.getPriority(), tag, message);
    }
}
