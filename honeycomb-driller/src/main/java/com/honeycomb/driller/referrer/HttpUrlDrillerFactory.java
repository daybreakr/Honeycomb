package com.honeycomb.driller.referrer;

import android.content.Context;
import android.os.Build;

import com.honeycomb.driller.url.HttpUrlDriller;
import com.honeycomb.driller.url.IUrlDriller;

public class HttpUrlDrillerFactory implements IUrlDrillerFactory {
    private static final String UA = buildUserAgent();

    @Override
    public IUrlDriller createUrlDriller(Context context) {
        IUrlDriller driller = new HttpUrlDriller();
        driller.setUserAgent(UA);
        return driller;
    }

    // XXX: Read user-agent dynamically.
    private static String buildUserAgent() {
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
