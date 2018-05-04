package com.honeycomb.nav.utils;

import android.content.res.Resources;
import android.os.Bundle;

public class MetaDataUtils {

    public static boolean getBoolean(Bundle metaData, String name, boolean defaultValue) {
        boolean value = defaultValue;
        if (metaData != null && metaData.containsKey(name)) {
            value = metaData.getBoolean(name);
        }
        return value;
    }

    public static int getInt(Bundle metaData, String name, int defaultValue) {
        int value = defaultValue;
        if (metaData != null && metaData.containsKey(name)) {
            value = metaData.getInt(name);
        }
        return value;
    }

    public static String getString(Bundle metaData, String name, String defaultValue) {
        String value = defaultValue;
        if (metaData != null && metaData.containsKey(name)) {
            value = metaData.getString(name);
        }
        return value;
    }

    public static String getString(Bundle metaData, Resources res, String name, String defaultValue) {
        String value = defaultValue;
        if (metaData != null && metaData.containsKey(name)) {
            if (metaData.get(name) instanceof Integer) {
                value = res.getString(metaData.getInt(name));
            } else {
                value = metaData.getString(name);
            }
        }
        return value;
    }
}
