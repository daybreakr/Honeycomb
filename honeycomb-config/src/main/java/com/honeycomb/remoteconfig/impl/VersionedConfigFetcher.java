package com.honeycomb.remoteconfig.impl;

import com.honeycomb.util.Preconditions;
import com.honeycomb.remoteconfig.IConfigFetcher;
import com.honeycomb.remoteconfig.impl.IVersionedConfigFetcher.FetchVersionedConfigCallback;

import java.util.Map;

public class VersionedConfigFetcher implements IConfigFetcher {
    public static final int ERROR_FETCHED_CONFIG_ERROR = 1;
    public static final int ERROR_UPDATE_FETCHED_CONFIG_FAILED = 2;

    private final IVersionedFetchedConfig mFetchedConfig;
    private final IVersionedConfigFetcher mConfigFetcher;

    public VersionedConfigFetcher(IVersionedFetchedConfig fetchedConfig,
                                  IVersionedConfigFetcher configFetcher) {
        mFetchedConfig = Preconditions.checkNotNull(fetchedConfig);
        mConfigFetcher = Preconditions.checkNotNull(configFetcher);
    }

    @Override
    public void fetchConfig(final FetchConfigCallback callback) {
        int version = mFetchedConfig.getVersion();
        mConfigFetcher.fetchVersionedConfig(version, new FetchVersionedConfigCallback() {
            @Override
            public void onFetchConfigSuccessful(IVersionedFetchedConfig fetchedConfig,
                                                boolean updated) {
                if (updated) {
                    // Fetched config must not be null if updated.
                    if (fetchedConfig == null) {
                        invokeFetchConfigFailure(ERROR_FETCHED_CONFIG_ERROR, callback);
                        return;
                    }

                    // Update persisted fetched config if updated.
                    Map<String, String> configMap = fetchedConfig.getConfigMap();
                    int version = fetchedConfig.getVersion();
                    if (!mFetchedConfig.setFetchedConfig(configMap, version)) {
                        invokeFetchConfigFailure(ERROR_UPDATE_FETCHED_CONFIG_FAILED, callback);
                        return;
                    }
                }

                invokeFetchConfigSuccessful(fetchedConfig, updated, callback);
            }

            @Override
            public void onFetchConfigFailure(int errorCode) {
                invokeFetchConfigFailure(errorCode, callback);
            }
        });
    }

    private void invokeFetchConfigSuccessful(IVersionedFetchedConfig fetchedConfig, boolean updated,
                                             FetchConfigCallback callback) {
        if (callback != null) {
            callback.onFetchConfigSuccessful(fetchedConfig, updated);
        }
    }

    private void invokeFetchConfigFailure(int errorCode, FetchConfigCallback callback) {
        if (callback != null) {
            callback.onFetchConfigFailure(errorCode);
        }
    }
}
