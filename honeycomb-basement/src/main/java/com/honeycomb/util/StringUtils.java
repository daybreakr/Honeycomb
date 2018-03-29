package com.honeycomb.util;

import android.text.TextUtils;

public class StringUtils {
    public static final String UTF_8 = "UTF-8";

    private static final String EMPTY_STRING = "";

    public static boolean isEmpty(CharSequence str) {
        return TextUtils.isEmpty(str);
    }

    public static String nullIfEmpty(String str) {
        return isEmpty(str) ? null : str;
    }

    public static String emptyIfNull(String str) {
        return str == null ? EMPTY_STRING : str;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        return TextUtils.equals(a, b);
    }
}
