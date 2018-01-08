package com.honeycomb.remoteconfig;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class RemoteConfigSource {
    // Fetched configs, would be updated and persisted every time configs fetched from server.
    private ConfigSource mFetchedConfig;

    // Active configs, clients would get config values from here first.
    // Use 'activateFetched()' to make fetched configs been activated.
    // Would be initialized with last fetched configs while initializing.
    private ConfigSource mActiveConfig;

    // Default configs, clients would get config values from here if not found in
    // activated configs.
    // Use 'setDefaults' to set defaults every time initializing.
    private ConfigSource mDefaultConfig;

    private final Lock mLock;

    RemoteConfigSource(IFetchedConfig fetchedConfig) {
        mLock = new ReentrantLock(true);

        if (fetchedConfig != null) {
            setFetched(fetchedConfig.getConfigMap());
            activateFetched();
        }
    }

    void setDefaults(Map<String, String> defaults) {
        mLock.lock();
        try {
            mDefaultConfig = new ConfigSource(defaults);
        } finally {
            mLock.unlock();
        }
    }

    void setFetched(Map<String, String> fetched) {
        mLock.lock();
        try {
            mFetchedConfig = new ConfigSource(fetched);
        } finally {
            mLock.unlock();
        }
    }

    boolean activateFetched() {
        mLock.lock();
        try {
            if (mFetchedConfig != null) {
                mActiveConfig = mFetchedConfig;
                mFetchedConfig = null;
                return true;
            }
        } finally {
            mLock.unlock();
        }

        return false;
    }

    boolean getBoolean(String key) {
        if (key != null) {
            mLock.lock();
            try {
                Boolean value = getBooleanLocked(key, mActiveConfig, mDefaultConfig);
                if (value != null) {
                    return value;
                }
            } finally {
                mLock.unlock();
            }
        }
        return RemoteConfigValue.STATIC_DEFAULT_BOOLEAN;
    }

    int getInt(String key) {
        if (key != null) {
            mLock.lock();
            try {
                Integer value = getIntLocked(key, mActiveConfig, mDefaultConfig);
                if (value != null) {
                    return value;
                }
            } finally {
                mLock.unlock();
            }
        }
        return RemoteConfigValue.STATIC_DEFAULT_INT;
    }

    long getLong(String key) {
        if (key != null) {
            mLock.lock();
            try {
                Long value = getLongLocked(key, mActiveConfig, mDefaultConfig);
                if (value != null) {
                    return value;
                }
            } finally {
                mLock.unlock();
            }
        }
        return RemoteConfigValue.STATIC_DEFAULT_LONG;
    }

    float getFloat(String key) {
        if (key != null) {
            mLock.lock();
            try {
                Float value = getFloatLocked(key, mActiveConfig, mDefaultConfig);
                if (value != null) {
                    return value;
                }
            } finally {
                mLock.unlock();
            }
        }
        return RemoteConfigValue.STATIC_DEFAULT_FLOAT;
    }

    double getDouble(String key) {
        if (key != null) {
            mLock.lock();
            try {
                Double value = getDoubleLocked(key, mActiveConfig, mDefaultConfig);
                if (value != null) {
                    return value;
                }
            } finally {
                mLock.unlock();
            }
        }
        return RemoteConfigValue.STATIC_DEFAULT_DOUBLE;
    }

    String getString(String key) {
        if (key != null) {
            mLock.lock();
            try {
                String value = getStringLocked(key, mActiveConfig, mDefaultConfig);
                if (value != null) {
                    return value;
                }
            } finally {
                mLock.unlock();
            }
        }
        return RemoteConfigValue.STATIC_DEFAULT_STRING;
    }

    private Boolean getBooleanLocked(String key, ConfigSource... configSources) {
        if (configSources != null) {
            for (ConfigSource configSource : configSources) {
                if (configSource != null && configSource.hasConfig(key)) {
                    String value = configSource.getConfig(key);

                    if (RemoteConfigValue.TRUE_PATTERN.matcher(value).matches()) {
                        return true;
                    }

                    if (RemoteConfigValue.FALSE_PATTERN.matcher(value).matches()) {
                        return false;
                    }
                }
            }
        }
        return null;
    }

    private Integer getIntLocked(String key, ConfigSource... configSources) {
        if (configSources != null) {
            for (ConfigSource configSource : configSources) {
                if (configSource != null && configSource.hasConfig(key)) {
                    try {
                        return Integer.valueOf(configSource.getConfig(key));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return null;
    }

    private Long getLongLocked(String key, ConfigSource... configSources) {
        if (configSources != null) {
            for (ConfigSource configSource : configSources) {
                if (configSource != null && configSource.hasConfig(key)) {
                    try {
                        return Long.valueOf(configSource.getConfig(key));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return null;
    }

    private Float getFloatLocked(String key, ConfigSource... configSources) {
        if (configSources != null) {
            for (ConfigSource configSource : configSources) {
                if (configSource != null && configSource.hasConfig(key)) {
                    try {
                        return Float.valueOf(configSource.getConfig(key));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return null;
    }

    private Double getDoubleLocked(String key, ConfigSource... configSources) {
        if (configSources != null) {
            for (ConfigSource configSource : configSources) {
                if (configSource != null && configSource.hasConfig(key)) {
                    try {
                        return Double.valueOf(configSource.getConfig(key));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return null;
    }

    private String getStringLocked(String key, ConfigSource... configSources) {
        if (configSources != null) {
            for (ConfigSource configSource : configSources) {
                if (configSource != null && configSource.hasConfig(key)) {
                    String value = configSource.getConfig(key);
                    if (value != null) {
                        return value;
                    }
                }
            }
        }
        return null;
    }
}
