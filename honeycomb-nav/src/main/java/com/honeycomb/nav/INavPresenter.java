package com.honeycomb.nav;

public interface INavPresenter {

    void inflateNavItems();

    void selectNavItem(NavItem navItem);

    void attach(INavView navView, INavModel navModel, String categoryKey);

    void detach();
}
