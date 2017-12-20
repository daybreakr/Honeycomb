package com.google.honeycomb.remoteconfig;

import com.google.honeycomb.common.util.Preconditions;

import java.util.HashMap;
import java.util.Map;

public class RemoteConfig {
    private final RemoteConfigSource mConfigSource;
    private final IConfigFetcher mConfigFetcher;

    private RemoteConfigSettings mConfigSettings;

    private RemoteConfig(Builder builder) {
        mConfigSource = new RemoteConfigSource(Preconditions.checkNotNull(builder.fetchedConfig));
        mConfigFetcher = Preconditions.checkNotNull(builder.configFetcher);

        mConfigSettings = Preconditions.checkNotNull(builder.configSettings);
    }

    public void setConfigSettings(RemoteConfigSettings configSettings) {
        if (configSettings != null) {
            mConfigSettings = configSettings;
        }
    }

    public void setDefaults(Map<String, Object> defaults) {
        mConfigSource.setDefaults(parseDefaults(defaults));
    }

    public void fetch() {
        fetch(null);
    }

    public void fetch(final IConfigFetcher.FetchConfigCallback callback) {
        mConfigFetcher.fetchConfig(new IConfigFetcher.FetchConfigCallback() {
            @Override
            public void onFetchConfigSuccessful(IFetchedConfig fetchedConfig, boolean updated) {
                if (updated) {
                    mConfigSource.setFetched(fetchedConfig.getConfigMap());

                    if (mConfigSettings.activateFetchedWhenUpdated()) {
                        activateFetched();
                    }
                }

                if (callback != null) {
                    callback.onFetchConfigSuccessful(fetchedConfig, updated);
                }
            }

            @Override
            public void onFetchConfigFailure(int errorCode) {
                if (callback != null) {
                    callback.onFetchConfigFailure(errorCode);
                }
            }
        });
    }

    public boolean activateFetched() {
        return mConfigSource.activateFetched();
    }

    public boolean getBoolean(String key) {
        return mConfigSource.getBoolean(key);
    }

    public int getInt(String key) {
        return mConfigSource.getInt(key);
    }

    public long getLong(String key) {
        return mConfigSource.getLong(key);
    }

    public float getFloat(String key) {
        return mConfigSource.getFloat(key);
    }

    public double getDouble(String key) {
        return mConfigSource.getDouble(key);
    }

    public String getString(String key) {
        return mConfigSource.getString(key);
    }

    private static Map<String, String> parseDefaults(Map<String, Object> defaults) {
        HashMap<String, String> configMap = new HashMap<>();

        if (defaults != null && !defaults.isEmpty()) {
            for (Map.Entry<String, Object> entry : defaults.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key == null || value == null) {
                    continue;
                }
                configMap.put(key, value.toString());
            }
        }

        return configMap;
    }

    public static class Builder {
        private IFetchedConfig fetchedConfig;
        private IConfigFetcher configFetcher;
        private RemoteConfigSettings configSettings;

        public Builder setFetchedConfig(IFetchedConfig fetchedConfig) {
            this.fetchedConfig = fetchedConfig;
            return this;
        }

        public Builder setConfigFetcher(IConfigFetcher configFetcher) {
            this.configFetcher = configFetcher;
            return this;
        }

        public Builder setConfigSettings(RemoteConfigSettings configSettings) {
            this.configSettings = configSettings;
            return this;
        }

        public RemoteConfig build() {
            if (this.configSettings == null) {
                this.configSettings = new RemoteConfigSettings.Builder().build();
            }

            return new RemoteConfig(this);
        }
    }
}
