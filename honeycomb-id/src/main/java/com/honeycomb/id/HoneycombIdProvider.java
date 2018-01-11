package com.honeycomb.id;

import com.honeycomb.HoneycombApp;

public abstract class HoneycombIdProvider implements Comparable<HoneycombIdProvider> {
    public static final int DEFAULT_PRIORITY = 0;

    public static final int BUILD_IN_MAX_PRIORITY = 100;

    public static final int BUILD_IN_MIN_PRIORITY = -100;

    private int mPriority;

    public HoneycombIdProvider() {
        this(DEFAULT_PRIORITY);
    }

    public HoneycombIdProvider(int priority) {
        mPriority = priority;
    }

    @Override
    public int compareTo(HoneycombIdProvider o) {
        return o != null ? o.mPriority - mPriority : 1;
    }

    public abstract String getDeviceId(HoneycombApp honeycombApp);

    public abstract void setDeviceId(String deviceId, HoneycombApp honeycombApp);
}
