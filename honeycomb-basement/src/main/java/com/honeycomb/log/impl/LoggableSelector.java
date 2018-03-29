package com.honeycomb.log.impl;

import com.honeycomb.log.ILoggable;
import com.honeycomb.log.LogLevel;

import java.util.ArrayList;
import java.util.List;

public class LoggableSelector implements ILoggable {
    private List<ILoggable> mLoggableList = new ArrayList<>();

    void addLoggable(ILoggable loggable) {
        if (!mLoggableList.contains(loggable)) {
            mLoggableList.add(loggable);
        }
    }

    @Override
    public boolean isLoggable(LogLevel level, String tag) {
        for (ILoggable loggable : mLoggableList) {
            if (loggable.isLoggable(level, tag)) {
                return true;
            }
        }
        return false;
    }
}
