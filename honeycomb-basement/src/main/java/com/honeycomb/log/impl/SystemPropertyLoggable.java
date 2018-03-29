package com.honeycomb.log.impl;

import com.honeycomb.log.ILoggable;
import com.honeycomb.log.LogLevel;
import com.honeycomb.util.Preconditions;
import com.honeycomb.util.SystemProperties;

public class SystemPropertyLoggable implements ILoggable {
    private static final String PROPERTY_PREFIX = "log.tag.H";

    private final String mKey;
    private Integer mLowestLogLevelOrdinal;

    public SystemPropertyLoggable(String tag) {
        mKey = PROPERTY_PREFIX + Preconditions.checkStringNotEmpty(tag);
    }

    @Override
    public boolean isLoggable(LogLevel level, String tag) {
        if (mLowestLogLevelOrdinal == null) {
            mLowestLogLevelOrdinal = getLowestLogLevelOrdinal();
        }
        return level.ordinal() >= mLowestLogLevelOrdinal;
    }

    private int getLowestLogLevelOrdinal() {
        int ordinal = LogLevel.SUPPRESS.ordinal();
        try {
            ordinal = Integer.valueOf(SystemProperties.get(mKey));
        } catch (Exception ignored) {
        }
        if (ordinal <= LogLevel.NONE.ordinal() || ordinal > LogLevel.SUPPRESS.ordinal()) {
            ordinal = LogLevel.SUPPRESS.ordinal();
        }
        return ordinal;
    }
}
