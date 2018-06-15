package com.honeycomb.provider;

public abstract class SingletonProvider<T> implements IProvider<T> {
    private T mInstance;

    @Override
    public T get() {
        if (mInstance == null) {
            synchronized (this) {
                if (mInstance == null) {
                    mInstance = createInstance();
                }
            }
        }
        return mInstance;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    protected abstract T createInstance();
}
