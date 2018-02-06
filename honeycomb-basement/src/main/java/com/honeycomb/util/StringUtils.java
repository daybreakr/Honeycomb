package com.honeycomb.util;

import com.honeycomb.log.HLog;

public class StringUtils {
    private static final String EMPTY_STRING = "";

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        } else if (str instanceof String) {
            return ((String) str).trim().length() == 0;
        }
        return false;
    }

    public static String nullIfEmpty(String str) {
        return isEmpty(str) ? null : str;
    }

    public static String emptyIfNull(String str) {
        return str == null ? EMPTY_STRING : str;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) {
            return true;
        }
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    public static void replaceAll(StringBuilder builder, String placeHolder, String replacement) {
        if (isEmpty(placeHolder)) {
            return;
        }

        replacement = emptyIfNull(replacement);

        try {
            final int holderLen = placeHolder.length();
            final int replacementLen = replacement.length();
            int start = 0;
            while ((start = builder.indexOf(placeHolder, start)) >= 0) {
                int end = start + holderLen;
                if (end > builder.length()) {
                    break;
                }

                builder.replace(start, end, replacement);
                start += replacementLen;
                if (start >= builder.length()) {
                    break;
                }
            }
        } catch (Exception e) {
            HLog.e(e);
        }
    }
}
