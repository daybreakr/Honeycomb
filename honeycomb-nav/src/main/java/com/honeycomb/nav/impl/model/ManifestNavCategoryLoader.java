package com.honeycomb.nav.impl.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;

import com.honeycomb.nav.INavCategoryLoader;
import com.honeycomb.nav.NavCategory;
import com.honeycomb.nav.NavItem;
import com.honeycomb.nav.utils.MetaDataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.honeycomb.nav.impl.model.ManifestKeys.META_DATA_ORDER;
import static com.honeycomb.nav.impl.model.ManifestKeys.META_DATA_TITLE;

public class ManifestNavCategoryLoader implements INavCategoryLoader {
    private final Context mContext;
    private final boolean mUseMetaDataCategory;

    public ManifestNavCategoryLoader(Context context, boolean useMetaDataCategory) {
        mContext = context.getApplicationContext();
        mUseMetaDataCategory = useMetaDataCategory;
    }

    @Override
    public List<NavCategory> loadNavCategories(List<NavItem> navItems) {
        Map<String, NavCategory> categories = new HashMap<>();

        for (NavItem item : navItems) {
            NavCategory category = categories.get(item.category);
            if (category == null) {
                // Use category key as category's action.
                category = getNavCategoryForAction(item.category, item.category);
                if (category == null && mUseMetaDataCategory) {
                    category = getNavCategoryForMetaData(item.category);
                }
                if (category == null) {
                    throw new IllegalArgumentException("Couldn't find category " + item.category);
                }

                categories.put(item.category, category);
            }
            category.addItem(item);
        }

        return new ArrayList<>(categories.values());
    }

    private NavCategory getNavCategoryForAction(String action, String categoryKey) {
        Intent intent = new Intent(action);
        intent.setPackage(mContext.getPackageName());
        return getNavCategoryForIntent(intent, categoryKey);
    }

    private NavCategory getNavCategoryForIntent(Intent intent, String categoryKey) {
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> results = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);
        ResolveInfo resolved = results.isEmpty() ? null : results.get(0);
        if (resolved != null) {
            Bundle metaData = resolved.activityInfo.metaData;

            NavCategory category = new NavCategory();
            category.key = categoryKey;
            category.order = MetaDataUtils.getInt(metaData, META_DATA_ORDER, 0);
            updateCategoryFromActivityInfo(category, resolved.activityInfo);

            return category;
        }
        return null;
    }

    private void updateCategoryFromActivityInfo(NavCategory category, ActivityInfo activityInfo) {
        Resources res = mContext.getResources();
        Bundle metaData = activityInfo.metaData;

        CharSequence title = MetaDataUtils.getString(metaData, res, META_DATA_TITLE, null);

        if (TextUtils.isEmpty(title)) {
            title = activityInfo.nonLocalizedLabel;
        }
        if (TextUtils.isEmpty(title) && activityInfo.labelRes > 0) {
            title = res.getString(activityInfo.labelRes);
        }

        category.title = title;
    }

    private NavCategory getNavCategoryForMetaData(String categoryKey) {
        String packageName = mContext.getPackageName();
        try {
            PackageManager pm = mContext.getPackageManager();
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
            return getNavCategoryForMetaData(categoryKey, applicationInfo.metaData);
        } catch (PackageManager.NameNotFoundException ignored) {
            return null;
        }
    }

    private NavCategory getNavCategoryForMetaData(String categoryKey, Bundle metaData) {
        if (metaData != null && metaData.containsKey(categoryKey)) {
            NavCategory category = new NavCategory();
            category.key = categoryKey;
            category.order = 0;
            category.title = MetaDataUtils.getString(metaData, mContext.getResources(),
                    category.key, null);

            return category;
        }
        return null;
    }
}
