package com.honeycomb.invisible;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

class PackageUtils {

    static ComponentName getLaunchActivity(Context context) {
        return getLaunchActivityForPackage(context, context.getPackageName());
    }

    static ComponentName getLaunchActivityForPackage(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageName);
        List<ResolveInfo> ris = pm.queryIntentActivities(intent, 0);

        if (ris == null || ris.isEmpty()) {
            return null;
        }

        ActivityInfo activityInfo = ris.get(0).activityInfo;
        return new ComponentName(activityInfo.packageName, activityInfo.name);
    }

    static void disableComponent(Context context, ComponentName componentName) {
        setComponentEnabledSetting(context, componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }

    static void enableComponent(Context context, ComponentName componentName) {
        setComponentEnabledSetting(context, componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    private static void setComponentEnabledSetting(Context context, ComponentName componentName,
                                                   int setting) {
        PackageManager pm = context.getPackageManager();

        if (pm.getComponentEnabledSetting(componentName) != setting) {
            pm.setComponentEnabledSetting(componentName, setting, PackageManager.DONT_KILL_APP);
        }
    }
}
