package com.honeycomb.invisible;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;

public class InvisibleUtils {

    public static void invisibleNow(Context context) {
        final ComponentName launchActivity = PackageUtils.getLaunchActivity(context);
        if (launchActivity == null) {
            // Already invisible.
            return;
        }

        PackageUtils.disableComponent(context, launchActivity);
    }

    public static void invisibleAfterLaunched(Context context) {
        final ComponentName launchActivity = PackageUtils.getLaunchActivity(context);
        if (launchActivity == null) {
            // Already invisible.
            return;
        }

        final Application application = getApplication(context);
        if (application == null) {
            // Unable to get application from context.
            return;
        }

        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                disableIfLaunchActivity(application, launchActivity, activity, this);
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    private static Application getApplication(Context context) {
        Context applicationContext = context != null ? context.getApplicationContext() : null;
        if (applicationContext != null && applicationContext instanceof Application) {
            return (Application) applicationContext;
        }
        return null;
    }

    private static void disableIfLaunchActivity(Application application,
                                                ComponentName launchActivity,
                                                Activity activity,
                                                ActivityLifecycleCallbacks callbacks) {
        if (launchActivity.equals(activity.getComponentName())) {
            PackageUtils.disableComponent(application, launchActivity);
            application.unregisterActivityLifecycleCallbacks(callbacks);
        }
    }
}
