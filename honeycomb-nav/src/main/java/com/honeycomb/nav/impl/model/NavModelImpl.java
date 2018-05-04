package com.honeycomb.nav.impl.model;

import com.honeycomb.nav.INavCategoryLoader;
import com.honeycomb.nav.INavItemLoader;
import com.honeycomb.nav.INavModel;
import com.honeycomb.nav.NavCategory;
import com.honeycomb.nav.NavItem;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavModelImpl implements INavModel {
    private final INavCategoryLoader mCategoryLoader;
    private final INavItemLoader mItemLoader;

    // Nav categories cache.
    private List<NavCategory> mCategories;
    // Nav categories index (key: category key, value: category)
    private final Map<String, NavCategory> mCategoryByKeyMap = new HashMap<>();

    public NavModelImpl(INavCategoryLoader categoryLoader, INavItemLoader itemLoader) {
        mCategoryLoader = categoryLoader;
        mItemLoader = itemLoader;
    }

    @Override
    public List<NavCategory> getCategories() {
        tryInitCategories();
        return mCategories;
    }

    @Override
    public NavCategory getItemsByCategory(String categoryKey) {
        tryInitCategories();
        return mCategoryByKeyMap.get(categoryKey);
    }

    private void tryInitCategories() {
        if (mCategories == null) {
            synchronized (this) {
                if (mCategories == null) {
                    initCategoriesLocked();
                }
            }
        }
    }

    private void initCategoriesLocked() {
        // clear caches
        mCategoryByKeyMap.clear();

        // load nav items and categories
        List<NavItem> items = mItemLoader.loadNavItems();
        mCategories = mCategoryLoader.loadNavCategories(items);

        // make category index by category key
        for (NavCategory category : mCategories) {
            mCategoryByKeyMap.put(category.key, category);
        }

        // sort nav items and categories
        for (NavCategory category : mCategories) {
            Collections.sort(category.items, ITEM_COMPARATOR);
        }
        Collections.sort(mCategories, CATEGORY_COMPARATOR);
    }

    private static final Comparator<NavItem> ITEM_COMPARATOR =
            new Comparator<NavItem>() {
                @Override
                public int compare(NavItem o1, NavItem o2) {
                    return o1.order - o2.order;
                }
            };

    private static final Comparator<NavCategory> CATEGORY_COMPARATOR =
            new Comparator<NavCategory>() {
                @Override
                public int compare(NavCategory o1, NavCategory o2) {
                    return o1.order - o2.order;
                }
            };
}
