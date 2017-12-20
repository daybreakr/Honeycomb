package com.google.honeycomb.log;

import com.google.honeycomb.util.Preconditions;
import com.google.honeycomb.util.SystemProperties;

public class SystemPropertyLoggableStrategy implements ILoggableStrategy {
    private static final String PROPERTY_PREFIX = "log.tag.";

    private final String mKey;
    private Integer mLowestLogLevelOrdinal;

    public SystemPropertyLoggableStrategy(String tag) {
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
        try {
            return Integer.valueOf(SystemProperties.get(mKey));
        } catch (Exception ignored) {
        }
        return LogLevel.SUPPRESS.ordinal();
    }
}
