package com.honeycomb.nav.impl.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;

import com.honeycomb.nav.INavItemLoader;
import com.honeycomb.nav.NavItem;
import com.honeycomb.nav.utils.MetaDataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.honeycomb.nav.impl.model.ManifestKeys.ACTION_NAV_ITEM;
import static com.honeycomb.nav.impl.model.ManifestKeys.META_DATA_CATEGORY;
import static com.honeycomb.nav.impl.model.ManifestKeys.META_DATA_ICON;
import static com.honeycomb.nav.impl.model.ManifestKeys.META_DATA_ICON_TINTABLE;
import static com.honeycomb.nav.impl.model.ManifestKeys.META_DATA_ORDER;
import static com.honeycomb.nav.impl.model.ManifestKeys.META_DATA_TITLE;

public class ManifestNavItemLoader implements INavItemLoader {
    private static final String DEFAULT_CATEGORY = "default";

    private final Context mContext;

    public ManifestNavItemLoader(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public List<NavItem> loadNavItems() {
        Map<Pair<String, String>, NavItem> items = new HashMap<>();

        getNavItemsForAction(ACTION_NAV_ITEM, DEFAULT_CATEGORY, items);

        return new ArrayList<>(items.values());
    }

    private void getNavItemsForAction(String action, String defaultCategory,
                                      Map<Pair<String, String>, NavItem> outItems) {
        Intent intent = new Intent(action);
        intent.setPackage(mContext.getPackageName());
        getNavItemsForIntent(intent, defaultCategory, outItems);
    }

    private void getNavItemsForIntent(Intent intent, String defaultCategory,
                                      Map<Pair<String, String>, NavItem> outItems) {
        PackageManager pm = mContext.getPackageManager();
        List<ResolveInfo> results = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA | PackageManager.GET_RESOLVED_FILTER);
        for (ResolveInfo resolved : results) {
            ActivityInfo activityInfo = resolved.activityInfo;
            Pair<String, String> key = new Pair<>(activityInfo.packageName, activityInfo.name);
            NavItem item = outItems.get(key);
            if (item == null) {
                item = new NavItem();

                Bundle metaData = activityInfo.metaData;

                String category = MetaDataUtils.getString(metaData, META_DATA_CATEGORY,
                        defaultCategory);
                if (category == null) {
                    throw new IllegalArgumentException("Found " + activityInfo.name
                            + " for intent " + intent
                            + " missing meta-data " + (metaData == null ? "" : META_DATA_CATEGORY));
                }
                item.category = category;
                item.order = MetaDataUtils.getInt(metaData, META_DATA_ORDER, 0);
                item.metaData = metaData;
                item.intent = new Intent().setClassName(activityInfo.packageName, activityInfo.name);
                updateItemFromActivityInfo(item, activityInfo);

                outItems.put(key, item);
            }
        }
    }

    private void updateItemFromActivityInfo(NavItem item, ActivityInfo activityInfo) {
        PackageManager pm = mContext.getPackageManager();
        Resources res = mContext.getResources();
        Bundle metaData = activityInfo.metaData;

        CharSequence title = MetaDataUtils.getString(metaData, res, META_DATA_TITLE, null);
        int iconRes = MetaDataUtils.getInt(metaData, META_DATA_ICON, 0);
        boolean isIconTintable = MetaDataUtils.getBoolean(metaData, META_DATA_ICON_TINTABLE, false);

        // Set the preference title to the activity's label if no
        // meta-data is found
        if (TextUtils.isEmpty(title)) {
            title = activityInfo.loadLabel(pm);
        }

        // Set the preference icon resource ID to the activity's icon if no
        // meta-data is found
        if (iconRes == 0) {
            iconRes = activityInfo.icon;
        }

        item.title = title;
        item.iconRes = iconRes;
        item.isIconTintable = isIconTintable;
    }
}
