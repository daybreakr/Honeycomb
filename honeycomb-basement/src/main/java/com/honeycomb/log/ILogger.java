package com.honeycomb.log;

/**
 * Prepares the log message and delegates it to {@link ILogAdapter} attached to it.
 */
public interface ILogger {

    void addLogAdapter(ILogAdapter logAdapter);

    void clearLogAdapters();

    void v(String moduleTag, String message, Object... args);

    void d(String moduleTag, String message, Object... args);

    void i(String moduleTag, String message, Object... args);

    void w(String moduleTag, String message, Object... args);

    void e(String moduleTag, String message, Object... args);

    void wtf(String moduleTag, String message, Object... args);

    void w(Throwable throwable, String moduleTag, String message, Object... args);

    void e(Throwable throwable, String moduleTag, String message, Object... args);

    void wtf(Throwable throwable, String moduleTag, String message, Object... args);

    void w(Throwable throwable);

    void e(Throwable throwable);

    void log(LogLevel level, String tag, String message, Throwable throwable);
}
