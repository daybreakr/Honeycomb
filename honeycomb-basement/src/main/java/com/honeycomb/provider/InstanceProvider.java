package com.honeycomb.provider;

public class InstanceProvider<T> implements IProvider<T> {
    private final T mInstance;

    public InstanceProvider(T instance) {
        if (instance == null) {
            throw new NullPointerException("Provides a null instance.");
        }
        mInstance = instance;
    }

    @Override
    public T get() {
        return mInstance;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
