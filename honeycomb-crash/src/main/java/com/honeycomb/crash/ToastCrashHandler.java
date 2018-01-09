package com.honeycomb.crash;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.honeycomb.HoneycombApp;
import com.honeycomb.util.Preconditions;

public class ToastCrashHandler extends CrashHandler {
    private static final String DEFAULT_MESSAGE = "FATAL ERROR!\n";

    private HoneycombCrash mHoneycombCrash;
    private Context mContext;
    private String mMessage;

    private Handler mHandler;

    public ToastCrashHandler(HoneycombCrash honeycombCrash, HoneycombApp honeycombApp) {
        this(honeycombCrash, honeycombApp.getApplicationContext(), DEFAULT_MESSAGE);
    }

    public ToastCrashHandler(HoneycombCrash honeycombCrash, Context context, String message) {
        super(BUILD_IN_MAX_PRIORITY);
        mHoneycombCrash = Preconditions.checkNotNull(honeycombCrash);
        mContext = Preconditions.checkNotNull(context);
        mMessage = message;

        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void handleCrash(final Throwable throwable, long timestamp) {
        if (mHoneycombCrash.getOptions().isDeveloperModeEnabled()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    String message = mMessage + throwable.toString();
                    Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }
    }
}
