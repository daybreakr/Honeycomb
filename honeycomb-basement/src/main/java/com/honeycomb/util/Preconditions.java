package com.honeycomb.util;

public class Preconditions {

    public static <T> T checkNotNull(final T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(final T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static <T extends String> T checkStringNotEmpty(final T string) {
        if (StringUtils.isEmpty(string)) {
            throw new IllegalArgumentException();
        }
        return string;
    }

    public static <T extends String> T checkStringNotEmpty(final T string, Object errorMessage) {
        if (StringUtils.isEmpty(string)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return string;
    }
}
