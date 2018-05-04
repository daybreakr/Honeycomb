package com.honeycomb.nav.impl.view;

import com.honeycomb.nav.INavView;
import com.honeycomb.nav.NavItem;
import com.honeycomb.nav.NavItemSelectedListener;

public abstract class BaseNavView implements INavView {
    private NavItemSelectedListener mNavItemSelectedListener;

    @Override
    public void attach(NavItemSelectedListener listener) {
        mNavItemSelectedListener = listener;
    }

    @Override
    public final void detach() {
        onDetach();

        mNavItemSelectedListener = null;
    }

    protected void onDetach() {
    }

    protected void invokeNavItemSelected(NavItem navItem) {
        if (mNavItemSelectedListener != null) {
            mNavItemSelectedListener.onNavItemSelected(navItem);
        }
    }
}
