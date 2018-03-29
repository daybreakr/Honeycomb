package com.honeycomb.util;

import com.honeycomb.log.HLog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";

    private static final String DEFAULT_CHARSET = StringUtils.UTF_8;
    private static final String EXTENSION_SEPARATOR = ".";

    //==============================================================================================
    // Read
    //==============================================================================================

    public static String readStringFile(File file) throws IOException {
        return readStringFile(file, DEFAULT_CHARSET);
    }

    public static String readStringFile(File file, String charset) throws IOException {
        byte[] contents = readFile(file);
        if (contents != null) {
            return new String(contents, charset);
        }
        return null;
    }

    public static byte[] readFile(File file) throws IOException {
        InputStream input = null;
        try {
            // wrapping a BufferedInputStream around it because when reading /proc with unbuffered
            // input stream, bytes read not equal to buffer size is not necessarily the correct
            // indication for EOF; but it is true for BufferedInputStream due to its implementation.
            input = new BufferedInputStream(new FileInputStream(file));
            // No need to close a ByteArrayOutputStream.
            ByteArrayOutputStream contents = new ByteArrayOutputStream();
            IoUtils.dump(input, contents);
            return contents.toByteArray();
        } finally {
            IoUtils.closeQuietly(input);
        }
    }

    //==============================================================================================
    // Write
    //==============================================================================================

    public static void stringToFile(File file, String string) throws IOException {
        stringToFile(file.getAbsolutePath(), string);
    }

    /**
     * Writes string to file. Basically same as "echo -n $string > $filename"
     *
     * @param filename Absolute path of target file.
     * @param string   String to write to the file.
     * @throws IOException when IO errors occurred.
     */
    public static void stringToFile(String filename, String string) throws IOException {
        bytesToFile(filename, string.getBytes(StringUtils.UTF_8));
    }

    public static void bytesToFile(File file, byte[] content) throws IOException {
        bytesToFile(file.getAbsolutePath(), content);
    }

    /*
     * Writes the bytes given in {@code content} to the file whose absolute path
     * is {@code filename}.
     */
    public static void bytesToFile(String filename, byte[] content) throws IOException {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(filename);
            output.write(content);
        } finally {
            IoUtils.closeQuietly(output);
        }
    }

    //==============================================================================================
    // Delete
    //==============================================================================================

    public static void deleteFileChecked(String pathname) throws IOException {
        if (pathname == null) {
            throw new IOException("pathname is null.");
        }
        deleteFileChecked(new File(pathname));
    }

    public static void deleteFileChecked(File file) throws IOException {
        boolean success = false;
        Exception error = null;
        try {
            success = deleteFile(file);
        } catch (SecurityException e) {
            error = e;
        }
        if (!success) {
            throw new IOException("Failed to delete " + file, error);
        }
    }

    public static void deleteContentsAndDirChecked(File dir) throws IOException {
        boolean success = false;
        Exception error = null;
        try {
            success = deleteContentsAndDir(dir);
        } catch (SecurityException e) {
            error = e;
        }
        if (!success) {
            throw new IOException("Failed to delete dir " + dir, error);
        }
    }

    public static void deleteContentsChecked(File dir) throws IOException {
        boolean success = false;
        Exception error = null;
        try {
            success = deleteContents(dir);
        } catch (SecurityException e) {
            error = e;
        }
        if (!success) {
            throw new IOException("Failed to delete dir contents " + dir, error);
        }
    }

    public static boolean deleteFile(String pathname) throws SecurityException {
        return pathname != null && deleteFile(new File(pathname));
    }

    public static boolean deleteFile(File file) throws SecurityException {
        return !file.exists() || file.delete();
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
                    HLog.w(TAG, "Failed to delete " + file);
                    success = false;
                }
            }
        }
        return success;
    }

    //==============================================================================================
    // Copy
    //==============================================================================================

    public static void copyFileOrThrow(File srcFile, File destFile) throws IOException {
        InputStream input = null;
        try {
            input = new FileInputStream(srcFile);
            copyToFileOrThrow(input, destFile);
        } finally {
            IoUtils.closeQuietly(input);
        }
    }

    public static void copyToFileOrThrow(InputStream input, File destFile) throws IOException {
        if (destFile.exists() && !destFile.delete()) {
            throw new IOException("Failed to delete exists " + destFile);
        }
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(destFile);
            IoUtils.dump(input, output);
        } finally {
            flushAndClose(output);
        }
    }

    //==============================================================================================
    // Rename & Move
    //==============================================================================================

    public static void renameFileChecked(File srcFile, File destFile) throws IOException {
        boolean success = false;
        Exception error = null;
        try {
            success = renameFile(srcFile, destFile);
        } catch (SecurityException e) {
            error = e;
        }
        if (!success) {
            throw new IOException("Failed to rename from " + srcFile + " to " + destFile, error);
        }
    }

    public static boolean renameFile(File srcFile, File destFile) throws SecurityException {
        return srcFile != null && destFile != null && srcFile.renameTo(destFile);
    }

    public static void moveFile(File srcFile, File destFile) throws IOException {
        // XXX: Just rename file if under the same mount point.

        // Copy to destination and delete source file.
        try {
            copyFileOrThrow(srcFile, destFile);
            deleteFileChecked(srcFile);
        } catch (IOException e) {
            // Delete copied destination file either failed to copy or failed to delete source file.
            deleteFile(destFile);

            throw new IOException("Failed to move from " + srcFile + " to " + destFile, e);
        }
    }

    //==============================================================================================
    // Create
    //==============================================================================================

    public static void mkdirsChecked(File dir) throws IOException {
        if (dir == null) {
            throw new IOException("dir is null.");
        }
        if (dir.mkdirs()) {
            throw new IOException("Failed to create dir " + dir);
        }
        if (!dir.isDirectory()) {
            File parent = dir.getParentFile();
            String errorMsg = "Failed to create dir " + dir;
            if (parent == null) {
                errorMsg += ", parent file is null.";
            } else {
                errorMsg += ", parent file"
                        + ": dir=" + parent.isDirectory()
                        + ", file=" + parent.isFile()
                        + ", exists=" + parent.exists()
                        + ", mod=" + getFileMode(parent);
            }
            throw new IOException(errorMsg);
        }
    }

    //==============================================================================================
    // Permissions
    //==============================================================================================

    public static void setReadableChecked(File path, boolean readable, boolean ownerOnly)
            throws IOException {
        if (!path.setReadable(readable, ownerOnly)) {
            throw new IOException("Failed to change read mode of file " + path);
        }
    }

    public static void setWritableChecked(File path, boolean writable, boolean ownerOnly)
            throws IOException {
        if (!path.setWritable(writable, ownerOnly)) {
            throw new IOException("Failed to change write mode of file " + path);
        }
    }

    public static void setExecutableChecked(File path, boolean executable, boolean ownerOnly)
            throws IOException {
        if (!path.setExecutable(executable, ownerOnly)) {
            throw new IOException("Failed to change execute mode of file " + path);
        }
    }

    public static String getFileMode(File file) {
        if (file != null) {
            String mod = "";
            mod += file.canRead() ? 'r' : '-';
            mod += file.canWrite() ? 'w' : '-';
            mod += file.canExecute() ? 'x' : '-';
            return mod;
        }
        return "---";
    }

    //==============================================================================================
    // Misc
    //==============================================================================================

    public static String getFileName(String path) {
        return path == null ? null : new File(path).getName();
    }

    public static String getFileExtension(String path) {
        String fileName = getFileName(path);
        if (fileName != null) {
            int index = fileName.lastIndexOf(EXTENSION_SEPARATOR);
            if (index > 0 && index < fileName.length() - 1) {
                return path.substring(index + 1);
            }
        }
        return "";
    }

    /**
     * Perform an fsync on the given FileOutputStream.  The stream at this
     * point must be flushed but not yet closed.
     */
    public static boolean sync(FileOutputStream stream) {
        try {
            if (stream != null) {
                stream.getFD().sync();
            }
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }

    private static void flushAndClose(FileOutputStream stream) throws IOException {
        if (stream != null) {
            stream.flush();
            sync(stream);
            stream.close();
        }
    }
}
