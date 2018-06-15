package com.honeycomb.provider;

public abstract class FactoryProvider<T> implements IProvider<T> {

    @Override
    public T get() {
        return createInstance();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    protected abstract T createInstance();
}
