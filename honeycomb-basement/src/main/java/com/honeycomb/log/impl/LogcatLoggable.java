package com.honeycomb.log.impl;

import android.util.Log;

import com.honeycomb.log.ILoggable;
import com.honeycomb.log.LogLevel;

public class LogcatLoggable implements ILoggable {

    @Override
    public boolean isLoggable(LogLevel level, String tag) {
        return Log.isLoggable(tag, level.getPriority());
    }
}
