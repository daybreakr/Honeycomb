package com.honeycomb.log;

import com.honeycomb.log.impl.LogAdapter;
import com.honeycomb.log.impl.LogcatLogPrinter;
import com.honeycomb.log.impl.Logger;
import com.honeycomb.log.impl.SimpleLoggable;
import com.honeycomb.log.impl.SystemPropertyLoggable;
import com.honeycomb.log.impl.UnifiedLogPrinter;

/**
 * Gets the raw log data and delegates it to {@link ILogger}
 */
public class HLog {
    private static ILogger sLogger = createDefaultLogger();

    private HLog() {
        // no instance
    }

    public static void setUnifiedLogger(String tag, boolean allLoggable) {
        setLogger(createUnifiedLogger(tag, allLoggable));
    }

    public static void setLogger(ILogger logger) {
        if (logger != null) {
            sLogger = logger;
        }
    }

    public static void setLogAdapter(ILogAdapter logAdapter) {
        clearLogAdapters();
        addLogAdapter(logAdapter);
    }

    public static void addLogAdapter(ILogAdapter logAppender) {
        sLogger.addLogAdapter(logAppender);
    }

    public static void clearLogAdapters() {
        sLogger.clearLogAdapters();
    }

    public static void v(String tag, String message, Object... args) {
        sLogger.v(tag, message, args);
    }

    public static void d(String tag, String message, Object... args) {
        sLogger.d(tag, message, args);
    }

    public static void i(String tag, String message, Object... args) {
        sLogger.i(tag, message, args);
    }

    public static void w(String tag, String message, Object... args) {
        sLogger.w(tag, message, args);
    }

    public static void e(String tag, String message, Object... args) {
        sLogger.e(tag, message, args);
    }

    public static void wtf(String tag, String message, Object... args) {
        sLogger.wtf(tag, message, args);
    }

    public static void w(Throwable throwable, String tag, String message, Object... args) {
        sLogger.w(throwable, tag, message, args);
    }

    public static void e(Throwable throwable, String tag, String message, Object... args) {
        sLogger.e(throwable, tag, message, args);
    }

    public static void wtf(Throwable throwable, String tag, String message, Object... args) {
        sLogger.wtf(throwable, tag, message, args);
    }

    public static void w(Throwable throwable) {
        sLogger.w(throwable);
    }

    public static void e(Throwable throwable) {
        sLogger.e(throwable);
    }

    public static void log(LogLevel level, String tag, String message, Throwable throwable) {
        sLogger.log(level, tag, message, throwable);
    }

    private static ILogger createDefaultLogger() {
        ILogger logger = new Logger();

        ILogAdapter logAdapter = LogAdapter.buildUpon().build();

        logger.addLogAdapter(logAdapter);

        return logger;
    }

    private static ILogger createUnifiedLogger(String tag, boolean allLoggable) {
        ILogger logger = new Logger();

        ILogAdapter logAdapter = LogAdapter.buildUpon()
                .addLoggable(new SimpleLoggable(allLoggable))
                .addLoggable(new SystemPropertyLoggable(tag))
                .logPrinter(new UnifiedLogPrinter(tag, new LogcatLogPrinter()))
                .build();

        logger.addLogAdapter(logAdapter);

        return logger;
    }
}
