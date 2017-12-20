package com.google.honeycomb.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link ILogger}
 */
class Logger implements ILogger {
    private final List<ILogAdapter> mLogAdapters = new ArrayList<>();

    @Override
    public void addLogAdapter(ILogAdapter logAdapter) {
        if (logAdapter != null) {
            mLogAdapters.add(logAdapter);
        }
    }

    @Override
    public void clearLogAdapters() {
        mLogAdapters.clear();
    }

    @Override
    public void v(String tag, String message, Object... args) {
        logFormat(LogLevel.VERBOSE, null, tag, message, args);
    }

    @Override
    public void d(String tag, String message, Object... args) {
        logFormat(LogLevel.DEBUG, null, tag, message, args);
    }

    @Override
    public void i(String tag, String message, Object... args) {
        logFormat(LogLevel.INFO, null, tag, message, args);
    }

    @Override
    public void w(String tag, String message, Object... args) {
        logFormat(LogLevel.WARN, null, tag, message, args);
    }

    @Override
    public void e(String tag, String message, Object... args) {
        logFormat(LogLevel.DEBUG, null, tag, message, args);
    }

    @Override
    public void wtf(String tag, String message, Object... args) {
        logFormat(LogLevel.ASSERT, null, tag, message, args);
    }

    @Override
    public void w(Throwable throwable, String tag, String message, Object... args) {
        logFormat(LogLevel.WARN, throwable, tag, message, args);
    }

    @Override
    public void e(Throwable throwable, String tag, String message, Object... args) {
        logFormat(LogLevel.ERROR, throwable, tag, message, args);
    }

    @Override
    public void wtf(Throwable throwable, String tag, String message, Object... args) {
        logFormat(LogLevel.ASSERT, throwable, tag, message, args);
    }

    @Override
    public void w(Throwable throwable) {
        logInner(LogLevel.WARN, null, null, throwable);
    }

    @Override
    public void e(Throwable throwable) {
        logInner(LogLevel.ERROR, null, null, throwable);
    }

    @Override
    public void log(LogLevel level, String tag, String message, Throwable throwable) {
        logInner(level, tag, message, throwable);
    }

    private void logFormat(LogLevel level, Throwable throwable, String tag, String message,
                           Object... args) {
        logInner(level, getTag(tag), formatMessage(message, args), throwable);
    }

    /* This method is synchronized in order to avoid messy of logs' order. */
    private synchronized void logInner(LogLevel level, String tag, String message,
                                       Throwable throwable) {
        if (throwable == null && message == null) {
            // nothing to show
            return;
        }
        if (level == null) {
            // set default log level
            level = LogLevel.DEBUG;
        }

        String stackTraceString = throwable != null ? getStackTraceString(throwable) : null;
        if (message == null) {
            if (stackTraceString == null) {
                wtf(tag, "Failed to get stack trace string.");
                return;
            }
            message = stackTraceString;
        } else if (stackTraceString != null) {
            message += " : " + stackTraceString;
        }

        for (ILogAdapter logAdapter : mLogAdapters) {
            if (logAdapter.isLoggable(level, tag)) {
                logAdapter.printLog(level, tag, message);
            }
        }
    }

    private String getTag(String tag) {
        return tag;
    }

    private String formatMessage(String message, Object... args) {
        if (args != null && args.length > 0) {
            try {
                message = String.format(message, args);
            } catch (Exception e) {
                wtf(e, null, "Log format error. " + message);
                return null;
            }
        }
        return message;
    }

    private static String getStackTraceString(Throwable throwable) {
        try {
            if (throwable == null) {
                return "";
            }

            // This is to reduce the amount of log spew that apps do in the non-error
            // condition of the network being unavailable.
            Throwable t = throwable;
            while (t != null) {
                if (t instanceof UnknownHostException) {
                    return "";
                }
                t = t.getCause();
            }

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        } catch (Exception ignored) {
        }
        return null;
    }
}
