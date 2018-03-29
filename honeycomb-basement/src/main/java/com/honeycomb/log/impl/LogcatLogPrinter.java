package com.honeycomb.log.impl;

import android.util.Log;

import com.honeycomb.log.ILogPrinter;
import com.honeycomb.log.LogLevel;

/**
 * Print log message with logcat.
 */
public class LogcatLogPrinter implements ILogPrinter {

    @Override
    public void printLog(LogLevel level, String tag, String message) {
        Log.println(level.getPriority(), tag, message);
    }
}
