package com.honeycomb.nav;

import android.support.design.widget.NavigationView;

import com.honeycomb.nav.impl.view.NavigationViewNavView;

public class NavigationImplFactory {

    public static INavView createNavigationViewNavView(NavigationView navigationView) {
        return new NavigationViewNavView(navigationView);
    }
}
