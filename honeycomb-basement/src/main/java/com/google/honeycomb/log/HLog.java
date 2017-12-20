package com.google.honeycomb.log;

/**
 * Gets the raw log data and delegates it to {@link ILogger}
 */
public class HLog {
    private static ILogger sLogger = new DefaultLogger();

    private HLog() {
        // no instance
    }

    public static void setLogger(ILogger logger) {
        if (logger != null) {
            sLogger = logger;
        }
    }

    public static void setUnifiedLogger(String tag, boolean production) {
        LogAdapter.Builder builder = LogAdapter.buildUpon()
                .logPrinter(new UnifiedLogPrinter(tag, new LogcatLogPrinter()))
                .addLoggableStrategy(new SimpleLoggableStrategy(!production))
                .addLoggableStrategy(new SystemPropertyLoggableStrategy(tag));
        setLogAdapter(builder.build());
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

    private static class DefaultLogger extends Logger {

        DefaultLogger() {
            // All use defaults.
            addLogAdapter(LogAdapter.buildUpon().build());
        }
    }
}
