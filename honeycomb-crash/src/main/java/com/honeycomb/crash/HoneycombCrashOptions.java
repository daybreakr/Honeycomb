package com.honeycomb.crash;

public class HoneycombCrashOptions {
    private boolean mIsDeveloperModeEnabled;
    private boolean mIsSuppressCrashed;
    private boolean mRebootOnCrash;
    private long mRebootDelay;

    private HoneycombCrashOptions(Builder builder) {
        mIsDeveloperModeEnabled = builder.isDeveloperModeEnabled;
        mIsSuppressCrashed = builder.isSuppressCrashed;
        mRebootOnCrash = builder.rebootOnCrash;
        mRebootDelay = builder.rebootDelay;
    }

    public boolean isDeveloperModeEnabled() {
        return mIsDeveloperModeEnabled;
    }

    public boolean isSuppressCrashed() {
        return mIsSuppressCrashed;
    }

    public boolean isRebootOnCrash() {
        return mRebootOnCrash;
    }

    public long getRebootDelay() {
        return mRebootDelay;
    }

    public static Builder buildUpon() {
        return new Builder();
    }

    public static class Builder {
        boolean isDeveloperModeEnabled = false;
        boolean isSuppressCrashed = true;
        boolean rebootOnCrash = true;
        long rebootDelay = 5000;

        public Builder setDeveloperModeEnabled(boolean enabled) {
            this.isDeveloperModeEnabled = enabled;
            return this;
        }

        public Builder setSuppressCrashed(boolean suppressCrashed) {
            this.isSuppressCrashed = suppressCrashed;
            return this;
        }

        public Builder setRebootOnCrash(boolean rebootOnCrash) {
            this.rebootOnCrash = rebootOnCrash;
            return this;
        }

        public Builder setRebootDelay(long rebootDelayMillis) {
            this.rebootDelay = rebootDelayMillis;
            return this;
        }

        public HoneycombCrashOptions build() {
            return new HoneycombCrashOptions(this);
        }
    }
}
