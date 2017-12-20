package com.google.honeycomb.remoteconfig;

import java.util.HashMap;
import java.util.Map;

class ConfigSource {
    private Map<String, String> mConfigMap;

    ConfigSource(Map<String, String> configMap) {
        setConfigMap(configMap);
    }

    boolean isEmpty() {
        return mConfigMap == null || mConfigMap.isEmpty();
    }

    boolean hasConfig(String key) {
        return !isEmpty() && mConfigMap.containsKey(key);
    }

    String getConfig(String key) {
        return (isEmpty() || key == null) ? null : mConfigMap.get(key);
    }

    void setConfigMap(Map<String, String> configMap) {
        if (configMap == null) {
            mConfigMap = null;
        } else {
            mConfigMap = new HashMap<>(configMap);
        }
    }
}
