package com.honeycomb.driller.referrer.impl;

import android.content.Context;
import android.os.Build;

import com.honeycomb.driller.url.IUrlDriller;
import com.honeycomb.driller.url.impl.HttpUrlDriller;

public class HttpUrlDrillerFactory implements IUrlDrillerFactory {
    private static final String DEFAULT_USER_AGENT = buildDefaultUserAgent();

    private final String mUserAgent;

    public HttpUrlDrillerFactory() {
        this(null);
    }

    public HttpUrlDrillerFactory(String userAgent) {
        if (userAgent == null) {
            mUserAgent = DEFAULT_USER_AGENT;
        } else {
            mUserAgent = userAgent;
        }
    }

    @Override
    public IUrlDriller createUrlDriller(Context context) {
        IUrlDriller driller = new HttpUrlDriller();
        driller.setUserAgent(mUserAgent);
        return driller;
    }

    // XXX: Read user-agent dynamically.
    private static String buildDefaultUserAgent() {
        final String androidVersion = Build.VERSION.RELEASE;
        final String model = Build.MODEL;
        final String buildId = Build.ID;
        final String webkitVersion = "537.36";
        final String ua = "Mozilla/5.0"
                + " (Linux; Android %s; %s Build/%s)"
                + " AppleWebKit/%s"
                + " (KHTML, like Gecko)"
                + " Version/4.0"
                + " Chrome/56.0.2924.87"
                + " Mobile"
                + " Safari/%s";
        return String.format(ua, androidVersion, model, buildId, webkitVersion, webkitVersion);
    }
}
