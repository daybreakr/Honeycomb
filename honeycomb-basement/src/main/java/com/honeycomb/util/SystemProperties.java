package com.honeycomb.util;

import android.annotation.SuppressLint;

import java.lang.reflect.Method;

// TODO: Hidden API, should be tested.

public class SystemProperties {
    private static Method sGetMethod;

    public static String get(String key) {
        try {
            Method getMethod = getSystemPropertiesGetMethod();
            return (String) getMethod.invoke(null, key);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint("PrivateApi")
    private static Method getSystemPropertiesGetMethod() {
        if (sGetMethod == null) {
            try {
                Class<?> clazz = getSystemPropertiesClass();
                sGetMethod = clazz.getMethod("get", String.class);
            } catch (Exception ignored) {
            }
        }
        return sGetMethod;
    }

    @SuppressLint("PrivateApi")
    private static Class<?> getSystemPropertiesClass() throws ClassNotFoundException {
        return Class.forName("android.os.SystemProperties");
    }
}
