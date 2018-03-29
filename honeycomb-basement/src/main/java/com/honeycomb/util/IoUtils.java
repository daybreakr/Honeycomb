package com.honeycomb.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IoUtils {
    private static final int BUFFER_SIZE = 1024;

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void dump(InputStream input, OutputStream output) throws IOException {
        int len;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((len = input.read(buffer)) != -1) {
            output.write(buffer, 0 , len);
        }
    }
}
