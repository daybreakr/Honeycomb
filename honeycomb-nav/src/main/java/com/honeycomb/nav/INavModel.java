package com.honeycomb.nav;

import java.util.List;

public interface INavModel {

    List<NavCategory> getCategories();

    NavCategory getItemsByCategory(String categoryKey);
}
