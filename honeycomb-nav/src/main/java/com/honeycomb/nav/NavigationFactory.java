package com.honeycomb.nav;

import android.app.Activity;
import android.content.Context;

import com.honeycomb.nav.impl.NavigationImpl;
import com.honeycomb.nav.impl.model.ManifestNavCategoryLoader;
import com.honeycomb.nav.impl.model.ManifestNavItemLoader;
import com.honeycomb.nav.impl.model.NavModelImpl;
import com.honeycomb.nav.impl.presenter.ActivityNavPresenter;

public class NavigationFactory {
    private static INavModel sDefaultModel;

    public static INavPresenter createActivityPresenter(Activity activity) {
        return new ActivityNavPresenter(activity);
    }

    public static INavModel getDefaultModel(Context context) {
        if (sDefaultModel == null) {
            synchronized (NavigationFactory.class) {
                if (sDefaultModel == null) {
                    sDefaultModel = buildModel().build(context);
                }
            }
        }
        return sDefaultModel;
    }

    public static INavigation createDefaultNavigation(INavView view, INavPresenter presenter,
                                                      Context context) {
        return buildNavigation(view, presenter).build(context);
    }

    public static NavigationBuilder buildNavigation(INavView view, INavPresenter presenter) {
        return new NavigationBuilder(view, presenter);
    }

    public static ModelBuilder buildModel() {
        return new ModelBuilder();
    }

    public static class NavigationBuilder {
        private final INavView view;
        private final INavPresenter presenter;

        private INavModel model;
        private ModelBuilder modelBuilder;
        private String categoryKey;

        NavigationBuilder(INavView view, INavPresenter presenter) {
            this.view = view;
            this.presenter = presenter;
        }

        public NavigationBuilder model(INavModel model) {
            this.model = model;
            return this;
        }

        public NavigationBuilder model(ModelBuilder modelBuilder) {
            this.modelBuilder = modelBuilder;
            return this;
        }

        public NavigationBuilder category(String categoryKey) {
            this.categoryKey = categoryKey;
            return this;
        }

        INavigation build(Context context) {
            if (this.view == null) {
                throw new IllegalArgumentException("BaseNavView is null.");
            }
            if (this.presenter == null) {
                throw new IllegalArgumentException("BaseNavPresenter is null.");
            }

            if (this.model == null) {
                if (this.modelBuilder != null) {
                    this.model = this.modelBuilder.build(context);
                } else {
                    this.model = getDefaultModel(context);
                }
            }

            return new NavigationImpl(this.view, this.presenter, this.model, this.categoryKey);
        }
    }

    public static class ModelBuilder {
        private INavCategoryLoader categoryLoader;
        private INavItemLoader itemLoader;

        public ModelBuilder categoryLoader(INavCategoryLoader categoryLoader) {
            this.categoryLoader = categoryLoader;
            return this;
        }

        public ModelBuilder itemLoader(INavItemLoader itemLoader) {
            this.itemLoader = itemLoader;
            return this;
        }

        public INavModel build(Context context) {
            if (context == null) {
                throw new NullPointerException("Context is null.");
            }

            if (this.categoryLoader == null) {
                this.categoryLoader = new ManifestNavCategoryLoader(context, false);
            }
            if (this.itemLoader == null) {
                this.itemLoader = new ManifestNavItemLoader(context);
            }

            return new NavModelImpl(this.categoryLoader, this.itemLoader);
        }
    }
}
