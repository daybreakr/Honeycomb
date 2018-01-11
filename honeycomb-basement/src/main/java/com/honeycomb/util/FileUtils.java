package com.honeycomb.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";

    private static final String CHARSET_UTF_8 = "UTF-8";

    public static byte[] readFile(File file) throws IOException {
        // wrapping a BufferedInputStream around it because when reading /proc with unbuffered
        // input stream, bytes read not equal to buffer size is not necessarily the correct
        // indication for EOF; but it is true for BufferedInputStream due to its implementation.
        InputStream input = new BufferedInputStream(new FileInputStream(file));
        try {
            ByteArrayOutputStream contents = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = input.read(buffer)) != -1) {
                contents.write(buffer, 0, len);
            }
            return contents.toByteArray();
        } finally {
            closeQuietly(input);
        }
    }

    public static void stringToFile(File file, String string) throws IOException {
        stringToFile(file.getAbsolutePath(), string);
    }

    /**
     * Writes string to file. Basically same as "echo -n $string > $filename"
     *
     * @param filename
     * @param string
     * @throws IOException
     */
    public static void stringToFile(String filename, String string) throws IOException {
        bytesToFile(filename, string.getBytes(CHARSET_UTF_8));
    }

    public static void bytesToFile(File file, byte[] content) throws IOException {
        bytesToFile(file.getAbsoluteFile(), content);
    }

    /*
     * Writes the bytes given in {@code content} to the file whose absolute path
     * is {@code filename}.
     */
    public static void bytesToFile(String filename, byte[] content) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            fos.write(content);
        } finally {
            closeQuietly(fos);
        }
    }

    public static boolean deleteFile(File file) throws SecurityException {
        return file.delete();
    }

    public static boolean deleteContentsAndDir(File dir) throws SecurityException {
        return deleteContents(dir) && deleteFile(dir);
    }

    public static boolean deleteContents(File dir) throws SecurityException {
        File[] files = dir.listFiles();
        boolean success = true;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    success &= deleteContents(file);
                }
                if (!deleteFile(file)) {
                    Log.w(TAG, "Failed to delete " + file);
                    success = false;
                }
            }
        }
        return success;
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}
