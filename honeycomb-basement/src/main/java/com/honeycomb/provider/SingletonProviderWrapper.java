package com.honeycomb.provider;

public class SingletonProviderWrapper<T> extends SingletonProvider<T> {
    private final IProvider<T> mProvider;

    public SingletonProviderWrapper(IProvider<T> provider) {
        if (provider == null) {
            throw new NullPointerException("Wrapped a null provider");
        } else if (provider.isSingleton()) {
            throw new IllegalArgumentException("Wrapped a singleton provider");
        }
        mProvider = provider;
    }

    @Override
    protected T createInstance() {
        return mProvider.get();
    }
}
