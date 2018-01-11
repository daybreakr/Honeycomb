package com.honeycomb.id;

import android.os.Environment;
import android.util.Base64;

import com.honeycomb.HoneycombApp;
import com.honeycomb.log.HLog;
import com.honeycomb.util.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class ExternalStorageIdProvider extends HoneycombIdProvider {
    private static final String DEVICE_ID_FILENAME = ".sys.EF07CD.token";

    private static final String CHARSET_UTF_8 = "UTF-8";
    private static final String MAGIC_NUMBER = "EF07CD";

    private static final String KEY_MAGIC = "magic";
    private static final String KEY_DEVICE_ID = "token";

    private String mCachedDeviceId = null;
    private long mCachedFileSize = -1;

    public ExternalStorageIdProvider() {
        super(BUILD_IN_MIN_PRIORITY);
    }

    @Override
    public String getDeviceId(HoneycombApp honeycombApp) {
        if (mCachedDeviceId == null || mCachedFileSize < 0) {
            File deviceIdFile = getDeviceIdFile();
            try {
                if (isValidDeviceIdFile(deviceIdFile)) {
                    byte[] contents = FileUtils.readFile(deviceIdFile);
                    if (contents != null && contents.length > 0) {
                        mCachedDeviceId = decodeDeviceId(contents);
                        mCachedFileSize = deviceIdFile.length();
                    }
                }
            } catch (Exception e) {
                HLog.w(e);
                mCachedDeviceId = null;
                mCachedFileSize = -1;
            }
        }
        return mCachedDeviceId;
    }

    @Override
    public void setDeviceId(String deviceId, HoneycombApp honeycombApp) {
        File deviceIdFile = getDeviceIdFile();
        try {
            // Dump cached device ID first.
            getDeviceId(honeycombApp);

            if (isDirty(deviceId, deviceIdFile)) {
                prepareOverrideDeviceIdFile(deviceIdFile);
                byte[] encoded = encodeDeviceId(deviceId);
                if (encoded != null) {
                    FileUtils.bytesToFile(deviceIdFile, encoded);
                    mCachedFileSize = deviceIdFile.length();
                    mCachedDeviceId = deviceId;
                    return;
                }
            }
        } catch (Exception e) {
            HLog.w(e);
        }

        // Failed to write device ID.
        mCachedDeviceId = null;
        mCachedFileSize = -1;
    }

    private boolean isValidDeviceIdFile(File deviceIdFile) {
        return deviceIdFile != null && deviceIdFile.exists() && deviceIdFile.canRead()
                && deviceIdFile.length() < 32;
    }

    private boolean isDirty(String deviceId, File deviceIdFile) {
        return mCachedDeviceId == null || !mCachedDeviceId.equals(deviceId)
                || mCachedFileSize < 0 || mCachedFileSize != deviceIdFile.length();
    }

    private void prepareOverrideDeviceIdFile(File deviceIdFile) throws Exception {
        if (deviceIdFile == null) {
            throw new NullPointerException("Device ID file is null.");
        }

        if (deviceIdFile.exists() && !FileUtils.deleteFile(deviceIdFile)) {
            throw new IllegalStateException("Failed to delete old device ID file: "
                    + deviceIdFile);
        }
    }

    private File getDeviceIdFile() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File external = Environment.getExternalStorageDirectory();
            return new File(external, DEVICE_ID_FILENAME);
        }
        return null;
    }

    private byte[] encodeDeviceId(String deviceId) throws JSONException,
            UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_MAGIC, MAGIC_NUMBER);
        jsonObject.put(KEY_DEVICE_ID, deviceId);
        String json = jsonObject.toString();
        return Base64.encode(json.getBytes(CHARSET_UTF_8), Base64.DEFAULT);
    }

    private String decodeDeviceId(byte[] encoded) throws IllegalArgumentException,
            UnsupportedEncodingException, JSONException {
        byte[] decoded = Base64.decode(encoded, Base64.DEFAULT);
        String json = new String(decoded, CHARSET_UTF_8);
        JSONObject jsonObject = new JSONObject(json);
        String magic = jsonObject.getString(KEY_MAGIC);
        String deviceId = jsonObject.getString(KEY_DEVICE_ID);

        if (!MAGIC_NUMBER.equals(magic)) {
            return null;
        }
        return deviceId;
    }
}
