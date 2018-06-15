package com.honeycomb.provider;

public class ReflectiveProvider<T> implements IProvider<T> {
    private ClassLoader mClassLoader;
    private String mClassName;
    private Class<T> mClass;

    public ReflectiveProvider(String className) {
        this(className, null);
    }

    public ReflectiveProvider(String className, ClassLoader classLoader) {
        if (className == null) {
            throw new NullPointerException("Provides a null class name");
        }
        mClassName = className;

        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        mClassLoader = classLoader;
    }

    public ReflectiveProvider(Class<T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Provides a null class");
        }
        mClass = clazz;
    }

    @Override
    public T get() {
        final String name = getClassName();
        try {
            Class<T> targetClass = getTargetClass();
            return targetClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to create " + name
                    + ": class not found.", e);
        } catch (ClassCastException e) {
            throw new RuntimeException("Failed to create " + name
                    + ": cannot cast to target class.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create " + name
                    + ": must have a public constructor with an empty argument list.", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Failed to create " + name
                    + ": constructor threw an exception.", e);
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    private String getClassName() {
        return mClass != null ? mClass.getName() : mClassName;
    }

    private Class<T> getTargetClass() throws ClassNotFoundException, ClassCastException {
        if (mClass == null) {
            @SuppressWarnings("unchecked")
            Class<T> clazz = (Class<T>) mClassLoader.loadClass(mClassName);
            mClass = clazz;
        }
        return mClass;
    }
}
