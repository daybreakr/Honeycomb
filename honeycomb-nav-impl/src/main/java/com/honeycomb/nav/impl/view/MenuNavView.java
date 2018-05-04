package com.honeycomb.nav.impl.view;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import com.honeycomb.nav.NavCategory;
import com.honeycomb.nav.NavItem;

import java.util.List;

public class MenuNavView extends BaseNavView {
    private static final int CATEGORY_ORDER_WEIGHT = 100;

    private Menu mMenu;

    private SparseArray<NavItem> mNavItems = new SparseArray<>();

    public MenuNavView(Menu menu) {
        mMenu = menu;
    }

    public boolean selectMenuItem(MenuItem menuItem) {
        NavItem navItem = mNavItems.get(menuItem.getItemId());
        if (navItem != null) {
            invokeNavItemSelected(navItem);
        }
        return false;
    }

    @Override
    protected void onDetach() {
        mMenu = null;
    }

    @Override
    public void bindNavItems(List<NavCategory> categories) {
        if (mMenu == null) {
            return;
        }

        mNavItems.clear();

        int itemId = 0;
        for (int categoryId = 0, cCount = categories.size(); categoryId < cCount; categoryId++) {
            NavCategory category = categories.get(categoryId);

            int categoryOrder = category.order * CATEGORY_ORDER_WEIGHT;
            Menu categoryMenu = bindCategoryMenu(mMenu, category, categoryId,
                    categoryOrder);

            for (int iIndex = 0, iCount = category.getItemsCount(); iIndex < iCount; iIndex++) {
                NavItem item = category.getItem(iIndex);

                bindMenuItem(categoryMenu, item, categoryId, itemId, categoryOrder + item.order);

                mNavItems.put(itemId, item);
                itemId++;
            }
        }
    }

    private Menu bindCategoryMenu(Menu parentMenu, NavCategory category, int categoryId,
                                  int order) {
        Menu categoryMenu;
        if (TextUtils.isEmpty(category.title)) {
            categoryMenu = parentMenu;
        } else {
            categoryMenu = parentMenu.addSubMenu(Menu.NONE, categoryId, order, category.title);
        }
        return categoryMenu;
    }

    private void bindMenuItem(Menu categoryMenu, NavItem item, int categoryId, int itemId,
                              int order) {
        MenuItem menuItem = categoryMenu.add(categoryId, itemId, order, item.title);
        if (item.iconRes > 0) {
            menuItem.setIcon(item.iconRes);
        }
    }
}
