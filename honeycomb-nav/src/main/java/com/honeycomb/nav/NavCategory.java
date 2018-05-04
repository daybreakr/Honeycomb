package com.honeycomb.nav;

import java.util.ArrayList;
import java.util.List;

public class NavCategory {

    /**
     * Key used for placing external items.
     */
    public String key;

    /**
     * Used to control display order.
     */
    public int order;

    /**
     * Title of the category that is shown to the user.
     */
    public CharSequence title;

    /**
     * List of the category's children
     */
    public List<NavItem> items = new ArrayList<>();

    public NavCategory() {
        // Empty
    }

    public void addItem(NavItem item) {
        this.items.add(item);
    }

    public void addItem(int index, NavItem item) {
        this.items.add(index, item);
    }

    public void removeItem(NavItem item) {
        this.items.remove(item);
    }

    public void removeItem(int index) {
        this.items.remove(index);
    }

    public int getItemsCount() {
        return this.items.size();
    }

    public NavItem getItem(int index) {
        return this.items.get(index);
    }
}
