package com.google.honeycomb.log;

import com.google.honeycomb.util.Preconditions;

public class UnifiedLogPrinter implements ILogPrinter {
    private final String mTag;
    private final ILogPrinter mPrinter;

    UnifiedLogPrinter(String tag, ILogPrinter printer) {
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
