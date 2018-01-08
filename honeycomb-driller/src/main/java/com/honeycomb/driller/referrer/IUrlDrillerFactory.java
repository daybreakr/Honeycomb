package com.honeycomb.driller.referrer;

import android.content.Context;

import com.honeycomb.driller.url.IUrlDriller;

public interface IUrlDrillerFactory {

    IUrlDriller createUrlDriller(Context context);
}
