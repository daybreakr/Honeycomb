package com.google.honeycomb.remoteconfig;

public interface IConfigFetcher {

    interface FetchConfigCallback {

        void onFetchConfigSuccessful(IFetchedConfig fetchedConfig, boolean updated);

        void onFetchConfigFailure(int errorCode);
    }

    void fetchConfig(FetchConfigCallback callback);
}
