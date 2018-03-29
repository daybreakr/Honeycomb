package com.honeycomb.log.impl;

import com.honeycomb.log.ILogAdapter;
import com.honeycomb.log.ILogPrinter;
import com.honeycomb.log.ILoggable;
import com.honeycomb.log.LogLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link ILogAdapter}, just delegates loggable logic to
 * {@link ILoggable} and delegates log printing logic to {@link ILogPrinter}.
 */
public class LogAdapter implements ILogAdapter {
    private final ILoggable mLoggable;
    private final ILogPrinter mLogPrinter;

    private LogAdapter(ILoggable loggable, ILogPrinter logPrinter) {
        mLoggable = loggable;
        mLogPrinter = logPrinter;
    }

    @Override
    public boolean isLoggable(LogLevel level, String tag) {
        return mLoggable.isLoggable(level, tag);
    }

    @Override
    public void printLog(LogLevel level, String tag, String message) {
        mLogPrinter.printLog(level, tag, message);
    }

    public static Builder buildUpon() {
        return new Builder();
    }

    public static class Builder {
        private List<ILoggable> mLoggableList;
        private ILogPrinter mLogPrinter;

        private Builder() {
        }

        public Builder addLoggable(ILoggable loggable) {
            if (mLoggableList == null) {
                mLoggableList = new ArrayList<>();
            }
            mLoggableList.add(loggable);
            return this;
        }

        public Builder logPrinter(ILogPrinter logPrinter) {
            mLogPrinter = logPrinter;
            return this;
        }

        public LogAdapter build() {
            final ILoggable loggable;
            if (mLoggableList == null || mLoggableList.isEmpty()) {
                loggable = new LogcatLoggable();
            } else if (mLoggableList.size() == 1) {
                loggable = mLoggableList.get(0);
            } else {
                LoggableSelector selector = new LoggableSelector();
                for (ILoggable l : mLoggableList) {
                    selector.addLoggable(l);
                }
                loggable = selector;
            }

            if (mLogPrinter == null) {
                mLogPrinter = new LogcatLogPrinter();
            }

            return new LogAdapter(loggable, mLogPrinter);
        }
    }
}
