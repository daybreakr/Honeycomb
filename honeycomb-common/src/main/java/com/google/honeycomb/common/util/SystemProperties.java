package com.google.honeycomb.common.util;

import android.annotation.SuppressLint;

import java.lang.reflect.Method;

public class SystemProperties {

    public static String get(String key) {
        try {
            Method getMethod = getSystemPropertiesClass().getMethod("get", String.class);
            return (String) getMethod.invoke(null, key);
        } catch (Exception e) {
            return null;
        }
    }

    // TODO: Hidden API, should be tested.
    @SuppressLint("PrivateApi")
    private static Class<?> getSystemPropertiesClass() throws ClassNotFoundException {
        return Class.forName("android.os.SystemProperties");
    }
}
