package com.honeycomb.log.impl;

import com.honeycomb.log.ILogPrinter;
import com.honeycomb.log.LogLevel;
import com.honeycomb.util.Preconditions;

public class UnifiedLogPrinter implements ILogPrinter {
    private final String mTag;
    private final ILogPrinter mPrinter;

    public UnifiedLogPrinter(String tag, ILogPrinter printer) {
        mTag = Preconditions.checkStringNotEmpty(tag, "Tag is empty.");
        mPrinter = Preconditions.checkNotNull(printer);
    }

    @Override
    public void printLog(LogLevel level, String tag, String message) {
        mPrinter.printLog(level, mTag, createMessage(tag, message));
    }

    private static String createMessage(String tag, String message) {
        if (tag != null) {
            message = "[" + tag + "] " + message;
        }
        return message;
    }
}
