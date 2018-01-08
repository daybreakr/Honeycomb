package com.google.honeycomb.base;

import com.google.honeycomb.util.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class PropertiesStringBuilder {
    private static final String HEADER_DELIMITER = "{";
    private static final String TAIL_DELIMITER = "}";
    private static final String PROPERTY_SEPARATOR = ", ";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final String mObjectName;
    private final List<String> mProperties;

    private PropertiesStringBuilder(String objectName) {
        mObjectName = objectName;
        mProperties = new ArrayList<>();
    }

    public static PropertiesStringBuilder create(Object object) {
        String objectName = Preconditions.checkNotNull(object).getClass().getSimpleName();
        return new PropertiesStringBuilder(objectName);
    }

    public static PropertiesStringBuilder create(String objectName) {
        return new PropertiesStringBuilder(objectName);
    }

    public final PropertiesStringBuilder append(String key, Object value) {
        String keyStr = Preconditions.checkNotNull(key);
        String valueStr = String.valueOf(value);
        mProperties.add(keyStr + KEY_VALUE_SEPARATOR + valueStr);
        return this;
    }

    @Override
    public final String toString() {
        StringBuilder string = new StringBuilder().append(mObjectName).append(HEADER_DELIMITER);
        for (int i = 0, count = mProperties.size(); i < count; i++) {
            string.append(mProperties.get(i));
            if (i < count - 1) {
                string.append(PROPERTY_SEPARATOR);
            }
        }
        return string.append(TAIL_DELIMITER).toString();
    }
}
