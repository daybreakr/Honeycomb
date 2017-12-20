package com.google.honeycomb.driller.referrer;

import android.content.Context;

import com.google.honeycomb.driller.url.IUrlDriller;

public interface IUrlDrillerFactory {

    IUrlDriller createUrlDriller(Context context);
}
