package com.honeycomb.crash;

import com.honeycomb.common.HoneycombApp;
import com.honeycomb.crash.impl.LogCrashHandler;
import com.honeycomb.crash.impl.RebootCrashHandler;
import com.honeycomb.crash.impl.ToastCrashHandler;
import com.honeycomb.log.HLog;
import com.honeycomb.util.Preconditions;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HoneycombCrash {
    private static final String TAG = "HoneycombCrash";

    private static volatile HoneycombCrash sInstance;

    private HoneycombCrashOptions mOptions;
    private ExecutorService mExecutor;

    private final List<CrashHandler> mCrashHandlers;

    private HoneycombCrash(HoneycombCrashOptions options, ExecutorService executor) {
        mOptions = Preconditions.checkNotNull(options);
        mExecutor = Preconditions.checkNotNull(executor);

        mCrashHandlers = new LinkedList<>();
    }

    public static HoneycombCrash getInstance() {
        return getInstance(HoneycombApp.getInstance());
    }

    public static HoneycombCrash getInstance(HoneycombApp honeycombApp) {
        if (sInstance == null) {
            synchronized (HoneycombCrash.class) {
                if (sInstance == null) {
                    HoneycombCrashOptions options = HoneycombCrashOptions.buildUpon().build();
                    ExecutorService executor = createExecutor();
                    HoneycombCrash honeycombCrash = new HoneycombCrash(options, executor);

                    setupBuildInCrashHandlers(honeycombApp, honeycombCrash);
                    setupUncaughtExceptionHandler(honeycombCrash);

                    sInstance = honeycombCrash;
                }
            }
        }
        return sInstance;
    }

    public HoneycombCrashOptions getOptions() {
        return mOptions;
    }

    public void setOptions(HoneycombCrashOptions options) {
        mOptions = Preconditions.checkNotNull(options);
    }

    public void setCrashHandlers(CrashHandler... crashHandlers) {
        mCrashHandlers.clear();
        addCrashHandler(crashHandlers);
    }

    public void addCrashHandler(CrashHandler... crashHandlers) {
        if (crashHandlers != null) {
            for (CrashHandler crashHandler : crashHandlers) {
                if (crashHandler != null) {
                    mCrashHandlers.add(crashHandler);
                }
            }

            Collections.sort(mCrashHandlers);
        }
    }

    private static ThreadPoolExecutor createExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                10000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private static void setupBuildInCrashHandlers(HoneycombApp honeycombApp,
                                                  HoneycombCrash honeycombCrash) {
        List<CrashHandler> crashHandlers = new LinkedList<>();
        crashHandlers.add(new LogCrashHandler());
        crashHandlers.add(new ToastCrashHandler(honeycombCrash, honeycombApp));
        crashHandlers.add(new RebootCrashHandler(honeycombCrash));
        honeycombCrash.setCrashHandlers(crashHandlers.toArray(new CrashHandler[0]));
    }

    private static void setupUncaughtExceptionHandler(HoneycombCrash honeycombCrash) {
        UncaughtExceptionHandler defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        UncaughtExceptionHandler crashHandler =
                honeycombCrash.new CrashDispatcher(defaultCrashHandler);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }

    private class CrashDispatcher implements UncaughtExceptionHandler {
        private final UncaughtExceptionHandler mDefaultCrashHandler;

        CrashDispatcher(UncaughtExceptionHandler defaultCrashHandler) {
            mDefaultCrashHandler = defaultCrashHandler;
        }

        @Override
        public void uncaughtException(Thread t, final Throwable e) {
            if (t == null || e == null) {
                return;
            }

            try {
                // Make sure not processing in main thread.
                Future<?> task = dispatchHandleCrash(e, System.currentTimeMillis());

                // Would block until processing finished.
                task.get();
            } catch (Exception ignored) {
            }

            if (!mOptions.isSuppressCrashed()) {
                if (mDefaultCrashHandler != null) {
                    mDefaultCrashHandler.uncaughtException(t, e);
                }
            }
        }
    }

    private Future<?> dispatchHandleCrash(final Throwable throwable, final long timestamp) {
        return mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                for (CrashHandler crashHandler : mCrashHandlers) {
                    try {
                        crashHandler.handleCrash(throwable, timestamp);
                    } catch (Exception ex) {
                        HLog.e(ex, TAG, "Ouch! My own exception handler threw an exception.");
                    }
                }
            }
        });
    }
}
