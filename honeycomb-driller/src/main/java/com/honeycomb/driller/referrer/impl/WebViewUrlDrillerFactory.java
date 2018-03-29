package com.honeycomb.driller.referrer.impl;

import android.content.Context;

import com.honeycomb.driller.url.IUrlDriller;
import com.honeycomb.driller.url.impl.WebViewUrlDriller;

public class WebViewUrlDrillerFactory implements IUrlDrillerFactory {

    @Override
    public IUrlDriller createUrlDriller(Context context) {
        return new WebViewUrlDriller(context);
    }
}
