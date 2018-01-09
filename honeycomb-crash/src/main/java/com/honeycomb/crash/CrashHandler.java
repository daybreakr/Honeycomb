package com.honeycomb.crash;

public abstract class CrashHandler implements Comparable<CrashHandler> {
    public static final int DEFAULT_PRIORITY = 0;

    public static final int BUILD_IN_MAX_PRIORITY = 100;

    public static final int BUILD_IN_MIN_PRIORITY = -100;

    private int mPriority;

    public CrashHandler() {
        this(DEFAULT_PRIORITY);
    }

    public CrashHandler(int priority) {
        mPriority = priority;
    }

    @Override
    public int compareTo(CrashHandler o) {
        return o != null ? o.mPriority - mPriority : 1;
    }

    public void handleCrash(Throwable throwable, long timestamp) {
    }
}
