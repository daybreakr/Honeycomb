package com.honeycomb.log;

import android.util.Log;

public class LogcatLoggableStrategy implements ILoggableStrategy {

    @Override
    public boolean isLoggable(LogLevel level, String tag) {
        return Log.isLoggable(tag, level.getPriority());
    }
}
