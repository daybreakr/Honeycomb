package com.honeycomb.nav;

import java.util.List;

public interface INavCategoryLoader {

    List<NavCategory> loadNavCategories(List<NavItem> navItems);
}
