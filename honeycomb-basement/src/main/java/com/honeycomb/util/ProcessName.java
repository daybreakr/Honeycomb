package com.honeycomb.util;

import android.os.Process;
import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcessName {
    private static final int PID = Process.myPid();
    private static String sName;

    public static String get() {
        if (sName == null) {
            synchronized (ProcessName.class) {
                if (sName == null) {
                    sName = readProcessName(PID);
                }
            }
        }
        return sName;
    }

    private static String readProcessName(int pid) {
        if (pid <= 0) {
            return null;
        }
        String processName = null;

        BufferedReader reader = null;
        try {
            StrictMode.ThreadPolicy policy = StrictMode.allowThreadDiskReads();
            try {
                reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            } finally {
                StrictMode.setThreadPolicy(policy);
            }
            processName = reader.readLine().trim();
        } catch (IOException ignored) {
        } finally {
            IoUtils.closeQuietly(reader);
        }
        return processName;
    }
}
