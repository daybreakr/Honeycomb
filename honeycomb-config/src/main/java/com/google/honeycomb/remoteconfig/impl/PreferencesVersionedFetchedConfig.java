package com.google.honeycomb.remoteconfig.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.honeycomb.common.util.Preconditions;

import java.util.HashMap;
import java.util.Map;

public class PreferencesVersionedFetchedConfig implements IVersionedFetchedConfig {
    private final SharedPreferences mPreferences;

    private static final String DEFAULT_PREFS_NAME = "remote_config";
    private static final String KEY_VERSION = "_meta:version";

    public PreferencesVersionedFetchedConfig(Context context) {
        mPreferences = context.getSharedPreferences(DEFAULT_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public PreferencesVersionedFetchedConfig(SharedPreferences preferences) {
        mPreferences = Preconditions.checkNotNull(preferences);
    }

    @Override
    public Map<String, String> getConfigMap() {
        Map<String, String> configMap = new HashMap<>();

        for (String key : mPreferences.getAll().keySet()) {
            if (KEY_VERSION.equals(key)) {
                continue;
            }
            try {
                String value = mPreferences.getString(key, null);
                if (value != null) {
                    configMap.put(key, value);
                }
            } catch (Exception ignored) {
                // Remove invalid config entry
                mPreferences.edit().remove(key).apply();
            }
        }

        return configMap;
    }

    @Override
    public int getVersion() {
        return mPreferences.getInt(KEY_VERSION, 0);
    }

    @Override
    public boolean setFetchedConfig(Map<String, String> fetchedConfig) {
        // clear old config first
        if (!mPreferences.edit().clear().commit()) {
            return false;
        }

        if (fetchedConfig != null && !fetchedConfig.isEmpty()) {
            final SharedPreferences.Editor editor = mPreferences.edit();
            for (Map.Entry<String, String> configEntry : fetchedConfig.entrySet()) {
                editor.putString(configEntry.getKey(), configEntry.getValue());
            }
            return editor.commit();
        }

        return true;
    }

    @Override
    public boolean setFetchedConfig(Map<String, String> fetchedConfig, int version) {
        // clear old config first
        if (!mPreferences.edit().clear().commit()) {
            return false;
        }

        final SharedPreferences.Editor editor = mPreferences.edit();
        if (fetchedConfig != null && !fetchedConfig.isEmpty()) {
            for (Map.Entry<String, String> configEntry : fetchedConfig.entrySet()) {
                editor.putString(configEntry.getKey(), configEntry.getValue());
            }
        }
        editor.putInt(KEY_VERSION, version);

        return editor.commit();
    }
}
