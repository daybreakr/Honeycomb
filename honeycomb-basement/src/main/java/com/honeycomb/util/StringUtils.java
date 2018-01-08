package com.honeycomb.util;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean equals(String a, String b) {
        return (a != null && a.equals(b)) || (a == null && b == null);
    }
}
