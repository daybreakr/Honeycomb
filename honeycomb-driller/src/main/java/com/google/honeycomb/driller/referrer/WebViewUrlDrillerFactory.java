package com.google.honeycomb.driller.referrer;

import android.content.Context;

import com.google.honeycomb.driller.url.IUrlDriller;
import com.google.honeycomb.driller.url.WebViewUrlDriller;

public class WebViewUrlDrillerFactory implements IUrlDrillerFactory {

    @Override
    public IUrlDriller createUrlDriller(Context context) {
        return new WebViewUrlDriller(context);
    }
}
