package com.honeycomb.nav.impl.presenter;

import android.app.Activity;
import android.content.Intent;

import com.honeycomb.nav.NavCategory;
import com.honeycomb.nav.NavItem;

import java.util.List;

public class ActivityNavPresenter extends BaseNavPresenter {
    private Activity mActivity;

    public ActivityNavPresenter(Activity activity) {
        mActivity = activity;
    }

    @Override
    protected void onDetach() {
        mActivity = null;
    }

    @Override
    public void inflateNavItems() {
        List<NavCategory> categories = invokeLoadNavItems();

        invokeBindNavItems(categories);
    }

    @Override
    public void selectNavItem(NavItem navItem) {
        if (navItem.intent != null) {
            startActivity(navItem.intent);
        }
    }

    private void startActivity(Intent intent) {
        Activity activity = mActivity;
        if (activity != null) {
            activity.startActivity(intent);
        }
    }
}
