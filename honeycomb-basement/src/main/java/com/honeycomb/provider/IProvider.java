package com.honeycomb.provider;

public interface IProvider<T> {

    T get();

    boolean isSingleton();
}
