package com.honeycomb.nav;

public interface INavigation {

    void inflateNavItems();

    void setNavItemSelectedListener(NavItemSelectedListener listener);

    void detach();
}
