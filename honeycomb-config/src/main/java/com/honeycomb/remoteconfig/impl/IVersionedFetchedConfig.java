package com.honeycomb.remoteconfig.impl;

import com.honeycomb.remoteconfig.IFetchedConfig;

import java.util.Map;

public interface IVersionedFetchedConfig extends IFetchedConfig {

    int getVersion();

    boolean setFetchedConfig(Map<String, String> configMap, int version);
}
