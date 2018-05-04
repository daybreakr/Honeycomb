package com.honeycomb.nav.impl.presenter;

import com.honeycomb.nav.INavModel;
import com.honeycomb.nav.INavPresenter;
import com.honeycomb.nav.INavView;
import com.honeycomb.nav.NavCategory;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseNavPresenter implements INavPresenter {
    private INavView mView;
    private INavModel mModel;

    private String mCategoryKey;

    @Override
    public void attach(INavView view, INavModel model, String categoryKey) {
        mView = view;
        mModel = model;

        mCategoryKey = categoryKey;
    }

    @Override
    public final void detach() {
        onDetach();

        mView = null;
        mModel = null;
    }

    protected void onDetach() {
    }

    protected void invokeBindNavItems(List<NavCategory> categories) {
        if (mView != null) {
            mView.bindNavItems(categories);
        }
    }

    protected List<NavCategory> invokeLoadNavItems() {
        List<NavCategory> categories;
        if (mCategoryKey != null) {
            categories = new ArrayList<>(1);
            NavCategory category = mModel.getItemsByCategory(mCategoryKey);
            if (category != null) {
                categories.add(category);
            }
        } else {
            categories = mModel.getCategories();
        }

        return categories;
    }

    protected INavView getNavView() {
        return mView;
    }

    protected INavModel getNavModel() {
        return mModel;
    }
}
