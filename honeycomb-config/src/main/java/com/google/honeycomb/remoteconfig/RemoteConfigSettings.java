package com.google.honeycomb.remoteconfig;

public class RemoteConfigSettings {
    private final boolean mActivateFetchedWhenUpdated;

    private RemoteConfigSettings(Builder builder) {
        mActivateFetchedWhenUpdated = builder.activateFetchedWhenUpdated;
    }

    public boolean activateFetchedWhenUpdated() {
        return mActivateFetchedWhenUpdated;
    }

    public static class Builder {
        private boolean activateFetchedWhenUpdated = true;

        public Builder setActivateFetchedWhenUpdated(boolean activateFetchedWhenUpdated) {
            this.activateFetchedWhenUpdated = activateFetchedWhenUpdated;
            return this;
        }

        public RemoteConfigSettings build() {
            return new RemoteConfigSettings(this);
        }
    }
}
