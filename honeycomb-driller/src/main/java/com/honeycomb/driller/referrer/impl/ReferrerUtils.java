package com.honeycomb.driller.referrer.impl;

public class ReferrerUtils {

    static boolean isMarketUrl(String url) {
        return url != null
                && (url.startsWith("https://play.google.com/store/apps")
                || url.startsWith("market://"));
    }

    static String retrievePackageName(String url) {
        return findRawQueryString(url, "id");
    }

    static String retrieveReferrer(String url) {
        return findRawQueryString(url, "referrer");
    }

    static String retrieveLocation(String content) {
        if (content != null) {
            String location;
            for (int tries = 0; ; tries++) {
                switch (tries) {
                    case 0:
                        location = findRefresh(content);
                        break;
                    case 1:
                        location = findWindowLocationHref(content);
                        break;
                    default:
                        return null;
                }

                if (isValidLocation(location)) {
                    return location;
                }
            }
        }
        return null;
    }

    private static String findRefresh(String src) {
        final String lowerCase = src.toLowerCase();

        int startIndex = 0;
        int endIndex = lowerCase.length();
        for (int step = 0; ; step++) {
            switch (step) {
                case 0:
                    startIndex = findKeywordsNextIndex(lowerCase, startIndex,
                            "<meta http-equiv=\"refresh\"",
                            "<meta http-equiv = \"refresh\"");
                    break;
                case 1:
                    int start = startIndex;
                    endIndex = findKeywordsIndex(lowerCase, start, ">");
                    startIndex = findKeywordsNextIndex(lowerCase, start,
                            "url=",
                            "url =");
                    if (startIndex < 0) {
                        startIndex = findKeywordsIndex(lowerCase, start,
                                "http");
                    }
                    break;
                case 2:
                    endIndex = findKeywordsIndex(lowerCase, startIndex, "\"");
                    break;
                case 3:
                    StringBuilder builder = new StringBuilder(
                            src.substring(startIndex, endIndex).trim());
                    int first = 0;
                    if (builder.charAt(first) == '\'') {
                        builder.deleteCharAt(first);
                    }
                    int last = builder.length() - 1;
                    if (builder.charAt(last) == '\'') {
                        builder.deleteCharAt(last);
                    }
                    return builder.toString();

            }

            if (startIndex < 0 || endIndex < 0 || startIndex > endIndex) {
                return null;
            }
        }
    }

    private static String findWindowLocationHref(String src) {
        final String lowerCase = src.toLowerCase();

        String wrapChar = null;
        int startIndex = 0;
        for (int step = 0; ; step++) {
            switch (step) {
                case 0:
                    startIndex = findKeywordsNextIndex(lowerCase, startIndex,
                            "window.location.href",
                            "document.location.href",
                            "window.location");
                    break;
                case 1:
                    wrapChar = "\"";
                    int contentIndex = findKeywordsNextIndex(lowerCase, startIndex, wrapChar);
                    if (contentIndex < 0) {
                        wrapChar = "'";
                        contentIndex = findKeywordsNextIndex(lowerCase, startIndex, wrapChar);
                    }
                    startIndex = contentIndex;
                    break;
                case 2:
                    if (wrapChar != null) {
                        int endIndex = findKeywordsIndex(lowerCase, startIndex, wrapChar);
                        if (endIndex > startIndex) {
                            return src.substring(startIndex, endIndex).trim();
                        }
                    }
                    return null;
            }

            if (startIndex < 0) {
                return null;
            }
        }
    }

    private static boolean isValidLocation(String content) {
        return content != null
                && (content.startsWith("/")
                || content.startsWith("https://")
                || content.startsWith("http://")
                || content.startsWith("market://"));
    }

    private static String findRawQueryString(String url, String key) {
        int startIndex = findKeywordsNextIndex(url, 0, key + "=");
        if (startIndex < 0) {
            return null;
        }
        int endIndex = url.indexOf("&", startIndex);
        if (endIndex < 0) {
            endIndex = url.length();
        }
        return url.substring(startIndex, endIndex);
    }

    private static int findKeywordsIndex(String src, int startIndex, String... keywords) {
        for (String keyword : keywords) {
            int index = src.indexOf(keyword, startIndex);
            if (index >= 0) {
                return index;
            }
        }
        return -1;
    }

    private static int findKeywordsNextIndex(String src, int startIndex, String... keywords) {
        for (String keyword : keywords) {
            int index = src.indexOf(keyword, startIndex);
            if (index >= 0) {
                return index + keyword.length();
            }
        }
        return -1;
    }
}
