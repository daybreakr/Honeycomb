package com.honeycomb.crash;

import android.os.Process;

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
        if (options.needRebootOnCrash()) {
            try {
                Thread.sleep(options.getRebootDelay());
            } catch (InterruptedException ignored) {
            }

            Process.killProcess(Process.myPid());
            System.exit(10);
        }
    }
}
