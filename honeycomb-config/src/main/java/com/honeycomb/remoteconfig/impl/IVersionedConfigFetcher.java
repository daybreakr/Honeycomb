package com.honeycomb.remoteconfig.impl;

public interface IVersionedConfigFetcher {

    interface FetchVersionedConfigCallback {

        void onFetchConfigSuccessful(IVersionedFetchedConfig fetchedConfig, boolean updated);

        void onFetchConfigFailure(int errorCode);
    }

    void fetchVersionedConfig(int version, FetchVersionedConfigCallback callback);
}
