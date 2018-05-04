package com.honeycomb.nav.impl;

import com.honeycomb.nav.INavModel;
import com.honeycomb.nav.INavPresenter;
import com.honeycomb.nav.INavView;
import com.honeycomb.nav.INavigation;
import com.honeycomb.nav.NavItem;
import com.honeycomb.nav.NavItemSelectedListener;

public class NavigationImpl implements INavigation, NavItemSelectedListener {
    private final INavView mView;
    private final INavPresenter mPresenter;

    private NavItemSelectedListener mNavItemSelectedListener;

    public NavigationImpl(INavView view, INavPresenter presenter,
                          INavModel model, String categoryKey) {
        mView = view;
        mPresenter = presenter;

        mView.attach(this);
        mPresenter.attach(mView, model, categoryKey);
    }

    @Override
    public void inflateNavItems() {
        mPresenter.inflateNavItems();
    }

    @Override
    public void setNavItemSelectedListener(NavItemSelectedListener listener) {
        mNavItemSelectedListener = listener;
    }

    @Override
    public void detach() {
        mView.detach();
        mPresenter.detach();

        mNavItemSelectedListener = null;
    }

    @Override
    public void onNavItemSelected(NavItem navItem) {
        mPresenter.selectNavItem(navItem);

        invokeNavItemSelected(navItem);
    }

    private void invokeNavItemSelected(NavItem navItem) {
        if (mNavItemSelectedListener != null) {
            mNavItemSelectedListener.onNavItemSelected(navItem);
        }
    }
}
