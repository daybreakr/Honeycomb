package com.honeycomb.remoteconfig;

import java.util.regex.Pattern;

public class RemoteConfigValue {
    static final boolean STATIC_DEFAULT_BOOLEAN = false;
    static final int STATIC_DEFAULT_INT = 0;
    static final long STATIC_DEFAULT_LONG = 0L;
    static final float STATIC_DEFAULT_FLOAT = 0F;
    static final double STATIC_DEFAULT_DOUBLE = 0D;
    static final String STATIC_DEFAULT_STRING = "";

    static final Pattern TRUE_PATTERN = Pattern.compile("^(1|true|t|yes|y|on)$", Pattern.CASE_INSENSITIVE);
    static final Pattern FALSE_PATTERN = Pattern.compile("^(0|false|f|no|n|off|)$", Pattern.CASE_INSENSITIVE);
}
