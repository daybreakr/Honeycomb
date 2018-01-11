package com.honeycomb.util;

import android.annotation.SuppressLint;

import com.honeycomb.log.HLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SystemProperties {
    private static final String TAG = "SystemProperties";

    private static SystemPropertiesProxy sProxy;

    private SystemProperties() {
    }

    public static String get(String key) {
        SystemPropertiesProxy proxy = getProxy();
        if (proxy != null) {
            return proxy.get(key);
        }
        return null;
    }

    public static String get(String key, String def) {
        SystemPropertiesProxy proxy = getProxy();
        if (proxy != null) {
            return proxy.get(key, def);
        }
        return def;
    }

    public static int getInt(String key, int def) {
        SystemPropertiesProxy proxy = getProxy();
        if (proxy != null) {
            return proxy.getInt(key, def);
        }
        return def;
    }

    public static long getLong(String key, long def) {
        SystemPropertiesProxy proxy = getProxy();
        if (proxy != null) {
            return proxy.getLong(key, def);
        }
        return def;
    }

    public static boolean getBoolean(String key, boolean def) {
        SystemPropertiesProxy proxy = getProxy();
        if (proxy != null) {
            return proxy.getBoolean(key, def);
        }
        return def;
    }

    public static void set(String key, String val) {
        SystemPropertiesProxy proxy = getProxy();
        if (proxy != null) {
            proxy.set(key, val);
        }
    }

    private static SystemPropertiesProxy getProxy() {
        if (sProxy == null) {
            try {
                sProxy = new SystemPropertiesProxy();
            } catch (ClassNotFoundException e) {
                HLog.wtf(e, TAG, "SystemProperties class not found.");
            }
        }
        return sProxy;
    }

    private static class SystemPropertiesProxy {
        private Class<?> mSystemPropertiesClass;

        @SuppressLint("PrivateApi")
        SystemPropertiesProxy() throws ClassNotFoundException {
            mSystemPropertiesClass = Class.forName("android.os.SystemProperties");
            if (mSystemPropertiesClass == null) {
                throw new ClassNotFoundException("SystemProperties class is null.");
            }
        }

        public String get(String key) {
            Method method = getMethod("get", String.class);
            return (String) invoke(method, key);
        }

        public String get(String key, String def) {
            Method method = getMethod("get", String.class, String.class);
            return (String) invoke(method, key, def);
        }

        public int getInt(String key, int def) {
            Method method = getMethod("getInt", String.class, int.class);
            Object result = invoke(method, key, def);
            if (result != null) {
                return (int) result;
            }
            return def;
        }

        public long getLong(String key, long def) {
            Method method = getMethod("getLong", String.class, long.class);
            Object result = invoke(method, key, def);
            if (result != null) {
                return (long) result;
            }
            return def;
        }

        public boolean getBoolean(String key, boolean def) {
            Method method = getMethod("getBoolean", String.class, boolean.class);
            Object result = invoke(method, key, def);
            if (result != null) {
                return (boolean) result;
            }
            return def;
        }

        public void set(String key, String val) {
            Method method = getMethod("set", String.class, String.class);
            invoke(method, key, val);
        }

        private Method getMethod(String name, Class<?>... parameterTypes) {
            try {
                return mSystemPropertiesClass.getMethod(name, parameterTypes);
            } catch (NoSuchMethodException e) {
                HLog.wtf(TAG, "Method not found, SystemProperties." + name);
                return null;
            }
        }

        private Object invoke(Method method, Object... args) {
            if (method != null) {
                try {
                    return method.invoke(null, args);
                } catch (IllegalAccessException e) {
                    HLog.wtf(e, TAG, "Failed to access " + method);
                } catch (InvocationTargetException e) {
                    HLog.wtf(e, TAG, "SystemProperties invocation failure.");
                }
            }
            return null;
        }
    }
}
