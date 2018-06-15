package com.honeycomb.common.initprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.honeycomb.common.HoneycombApp;
import com.honeycomb.log.HLog;

public class HoneycombInitProvider extends ContentProvider {
    private static final String TAG = "HoneycombInitProvider";

    @Override
    public boolean onCreate() {
        if (HoneycombApp.initializeApp(getContext()) == null) {
            HLog.w(TAG, "HoneycombApp initialization unsuccessful");
        } else {
            HLog.i(TAG, "HoneycombApp initialization successful");
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
