package com.honeycomb.nav;

import java.util.List;

public interface INavView {

    void bindNavItems(List<NavCategory> categories);

    void attach(NavItemSelectedListener listener);

    void detach();
}
