package com.honeycomb.id;

import com.honeycomb.HoneycombApp;
import com.honeycomb.util.SystemProperties;

public class SystemPropertiesIdProvider extends HoneycombIdProvider {
    private static final String PROP_DEVICE_ID = "persist.sys.nest.token";

    public SystemPropertiesIdProvider() {
        super(BUILD_IN_MAX_PRIORITY);
    }

    @Override
    public String getDeviceId(HoneycombApp honeycombApp) {
        return SystemProperties.get(PROP_DEVICE_ID);
    }

    @Override
    public void setDeviceId(String deviceId, HoneycombApp honeycombApp) {
        SystemProperties.set(PROP_DEVICE_ID, deviceId);
    }
}
