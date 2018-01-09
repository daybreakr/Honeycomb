package com.honeycomb;

import android.annotation.SuppressLint;
import android.content.Context;

import com.honeycomb.base.ProcessName;
import com.honeycomb.log.HLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

public class HoneycombApp {
    private static final String TAG = "HoneycombApp";

    private static final String[] SINGLETON_HONEYCOMB_PLUGINS = {
            "com.honeycomb.crash.HoneycombCrash"
    };
    private static final Set<String> REQUIRED_PLUGINS = Collections.emptySet();

    @SuppressLint("StaticFieldLeak")
    private static volatile HoneycombApp sInstance;

    private static final Object sLock = new Object();
    private final Context mApplicationContext;
    private final HoneycombOptions mOptions;

    private HoneycombApp(Context applicationContext, HoneycombOptions options) {
        mApplicationContext = applicationContext;
        mOptions = options;
    }

    public static HoneycombApp getInstance() {
            synchronized (sLock) {
            if (sInstance == null) {
                throw new IllegalStateException("HoneycombApp is not initialized in this process "
                        + ProcessName.get()
                        + ". Make sure to call HoneycombApp.initializeApp(Context) first.");
            }
            return sInstance;
        }
    }

    public static HoneycombApp initializeApp(Context context) {
        // TODO: parse HoneycombOptions
        return initializeApp(context, new HoneycombOptions());
    }

    public static HoneycombApp initializeApp(Context context, HoneycombOptions options) {
        // instantiate HoneycombApp instance
        synchronized (sLock) {
            if (sInstance != null) {
                // HoneycombApp instance already exits!
                return sInstance;
            }
            sInstance = new HoneycombApp(getApplicationContext(context), options);
        }

        // initialize plugins
        sInstance.initializePlugin(HoneycombApp.class, sInstance, SINGLETON_HONEYCOMB_PLUGINS);

        return sInstance;
    }

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    public HoneycombOptions getOptions() {
        return mOptions;
    }

    private static Context getApplicationContext(Context context) {
        Context applicationContext;
        if ((applicationContext = context.getApplicationContext()) == null) {
            applicationContext = context;
        }
        return applicationContext;
    }

    private <T> void initializePlugin(Class<T> argumentClass, T argumentObject,
                                      String... plugins) {
        if (plugins != null) {
            for (String plugin : plugins) {
                try {
                    Method method = Class.forName(plugin).getMethod("getInstance", argumentClass);
                    int modifiers = method.getModifiers();
                    if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
                        method.invoke(null, argumentObject);
                    }
                } catch (ClassNotFoundException e) {
                    if (REQUIRED_PLUGINS.contains(plugin)) {
                        throw new IllegalStateException(plugin + " is missing, but is required. "
                                + "Check if has been removed by Proguard.");
                    }
                    HLog.d(TAG, plugin + " is not linked. Skipping initialization.");
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException(plugin + "#getInstance"
                            + " has been removed by Proguard. Add keep rule to prevent it.");
                } catch (InvocationTargetException e) {
                    HLog.wtf(TAG, "Honeycomb API initialization failure.", e);
                } catch (IllegalAccessException e) {
                    HLog.wtf(TAG, "Failed to initialize " + plugin, e);
                }
            }
        }
    }
}
