package com.honeycomb.crash.impl;

import android.os.Process;

import com.honeycomb.crash.CrashHandler;
import com.honeycomb.crash.HoneycombCrash;
import com.honeycomb.crash.HoneycombCrashOptions;
import com.honeycomb.util.Preconditions;

public class RebootCrashHandler extends CrashHandler {
    private HoneycombCrash mHoneycombCrash;

    public RebootCrashHandler(HoneycombCrash honeycombCrash) {
        super(BUILD_IN_MIN_PRIORITY);
        mHoneycombCrash = Preconditions.checkNotNull(honeycombCrash);
    }

    @Override
    public void handleCrash(Throwable throwable, long timestamp) {
        HoneycombCrashOptions options = mHoneycombCrash.getOptions();
        if (options.isRebootOnCrash()) {
            try {
                Thread.sleep(options.getRebootDelay());
            } catch (InterruptedException ignored) {
            }

            Process.killProcess(Process.myPid());
            System.exit(10);
        }
    }
}
