package com.google.honeycomb.remoteconfig;

import java.util.Map;

public interface IFetchedConfig {

    Map<String, String> getConfigMap();

    boolean setFetchedConfig(Map<String, String> configMap);
}
