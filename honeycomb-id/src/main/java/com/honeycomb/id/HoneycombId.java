package com.honeycomb.id;

import com.honeycomb.HoneycombApp;
import com.honeycomb.log.HLog;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HoneycombId {
    private static final String TAG = "HoneycombId";

    private static volatile HoneycombId sInstance;

    private HoneycombApp mHoneycombApp;

    private final List<HoneycombIdProvider> mHoneycombIdProviders;

    private String mDeviceId;

    private HoneycombId(HoneycombApp honeycombApp) {
        mHoneycombApp = honeycombApp;

        mHoneycombIdProviders = new LinkedList<>();
    }

    public static HoneycombId getInstance() {
        return getInstance(HoneycombApp.getInstance());
    }

    public static HoneycombId getInstance(HoneycombApp honeycombApp) {
        if (sInstance == null) {
            synchronized (HoneycombId.class) {
                if (sInstance == null) {
                    HoneycombId honeycombId = new HoneycombId(honeycombApp);
                    setupBuildInIdProviders(honeycombId, honeycombApp);
                    sInstance = honeycombId;
                }
            }
        }
        return sInstance;
    }

    public void addHoneycombIdProvider(HoneycombIdProvider... providers) {
        if (providers != null) {
            for (HoneycombIdProvider provider : providers) {
                if (provider != null) {
                    mHoneycombIdProviders.add(provider);
                }
            }

            Collections.sort(mHoneycombIdProviders);
        }
    }

    public String getDeviceId() {
        if (!isValidDeviceId(mDeviceId)) {
            String deviceId = null;
            boolean success = false;
            for (HoneycombIdProvider provider : mHoneycombIdProviders) {
                try {
                    deviceId = provider.getDeviceId(mHoneycombApp);
                } catch (Exception e) {
                    HLog.w(e, TAG, "Errors occurred while get device ID from provider "
                            + provider.getClass().getSimpleName());
                    continue;
                }
                if (isValidDeviceId(deviceId)) {
                    success = true;
                    break;
                }
            }

            if (success) {
                mDeviceId = deviceId;

                // Sync device ID to all providers.
                setDeviceId(deviceId);
            }
        }

        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        for (HoneycombIdProvider provider : mHoneycombIdProviders) {
            boolean success = false;
            try {
                provider.setDeviceId(deviceId, mHoneycombApp);
                success = true;
            } catch (Exception e) {
                HLog.w(e, TAG, "Errors occurred while set device ID to provider "
                        + provider.getClass().getSimpleName());
            }

            if (success) {
                mDeviceId = deviceId;
            }
        }
    }

    private static void setupBuildInIdProviders(HoneycombId honeycombId, HoneycombApp honeycombApp) {
        List<HoneycombIdProvider> providers = new LinkedList<>();
        providers.add(new SystemPropertiesIdProvider());
        providers.add(new ExternalStorageIdProvider());
        honeycombId.addHoneycombIdProvider(providers.toArray(new HoneycombIdProvider[0]));
    }

    private boolean isValidDeviceId(String deviceId) {
        return deviceId != null && deviceId.trim().length() > 8;
    }
}
