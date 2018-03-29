package com.honeycomb.util;

import java.util.ArrayList;
import java.util.List;

public class PropertiesBuilder {
    private static final String HEADER_DELIMITER = "{";
    private static final String TAIL_DELIMITER = "}";
    private static final String PROPERTY_SEPARATOR = ",";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final String mObjectName;
    private final List<String> mProperties;

    private PropertiesBuilder(String objectName) {
        mObjectName = objectName;
        mProperties = new ArrayList<>();
    }

    public static PropertiesBuilder create(Object object) {
        String name = object != null ? object.getClass().getSimpleName() : "null";
        return create(name);
    }

    public static PropertiesBuilder create(String name) {
        return new PropertiesBuilder(name);
    }

    public final PropertiesBuilder append(String key, Object value) {
        String keyStr = String.valueOf(key);
        String valueStr = String.valueOf(value);
        mProperties.add(keyStr + KEY_VALUE_SEPARATOR + valueStr);
        return this;
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder(mObjectName);
        builder.append(HEADER_DELIMITER);
        for (int i = 0, count = mProperties.size(); i < count; i++) {
            builder.append(mProperties.get(i));
            // append separator if not the last one.
            if (i < count - 1) {
                builder.append(PROPERTY_SEPARATOR);
            }
        }
        builder.append(TAIL_DELIMITER);
        return builder.toString();
    }
}
