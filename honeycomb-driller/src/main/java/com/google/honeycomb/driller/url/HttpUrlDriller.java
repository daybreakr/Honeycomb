package com.google.honeycomb.driller.url;

import com.google.honeycomb.common.log.HLog;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlDriller extends BaseUrlDriller {
    private static final String TAG = "HttpUrlDriller";

    public static class Settings {
        int connectTimeout = 15000; // 15 seconds
        int readTimeout = 20000; // 20 seconds
    }

    private final Settings mSettings;

    public HttpUrlDriller() {
        this(null);
    }

    public HttpUrlDriller(Settings settings) {
        if (settings == null) {
            settings = new Settings();
        }
        mSettings = settings;
    }

    @Override
    public void onStartDrill(final String url) {
        invokeDrillStart(url);

        new Thread() {
            @Override
            public void run() {
                doDrill(url);
            }
        }.start();
    }

    /**
     * Method do request for the URL and depends from the response status return last used URL
     * or made new request with new URL.
     *
     * @param url URL for request
     */
    private void doDrill(String url) {
        doDrill(url, 0);
    }

    /**
     * Method do request for the URL and depends from the response status return last used URL
     * or made new request with new URL.
     *
     * @param url     URL for request
     * @param counter number of request from startMonitor.
     */
    private void doDrill(String url, int counter) {
        if (isStopped()) {
            HLog.v(TAG, "Drill been stopped.");
            return;
        }
        HLog.v(TAG, "doDrill: " + url);

        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();

            String userAgent = getUserAgent();
            if (userAgent != null) {
                connection.setRequestProperty("User-Agent", userAgent);
            }

            connection.setConnectTimeout(mSettings.connectTimeout);
            connection.setReadTimeout(mSettings.readTimeout);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);

            int status = connection.getResponseCode();
            HLog.v(TAG, " - Status: " + status);

            switch (status) {
                case HttpURLConnection.HTTP_OK: {
                    HLog.v(TAG, " - Done: " + url);
                    finishDrill(url, connection);
                }
                break;

                case HttpURLConnection.HTTP_MOVED_TEMP:
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_SEE_OTHER: {
                    String newUrl = connection.getHeaderField("Location");
                    HLog.v(TAG, " - Redirecting: " + newUrl);
                    if (newUrl.startsWith("/")) {
                        String protocol = urlObj.getProtocol();
                        String host = urlObj.getHost();
                        newUrl = protocol + "://" + host + newUrl;
                    }
                    invokeDrillRedirect(newUrl);

                    int drillDepth = getDrillDepth();
                    if (drillDepth == 0) { // No limited
                        doDrill(newUrl);
                    } else if (drillDepth > 0 && counter < drillDepth) { // Not limited
                        doDrill(newUrl, counter + 1);
                    } else { // Limited
                        finishDrill(url, connection);
                    }
                }
                break;

                default: {
                    Exception statusException = new Exception("Drilling error: Invalid URL, Status: " + status);
                    HLog.e(TAG, statusException.toString());
                    invokeFail(url, statusException);
                }
                break;
            }

            connection.getInputStream().close();
        } catch (Exception exception) {
            HLog.e(exception, TAG, "Drilling error: " + exception);
            invokeFail(url, exception);
        } catch (Error error) {
            HLog.e(error, TAG, "Drilling error: " + error);
            invokeFail(url, new Exception(error));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void finishDrill(String url, HttpURLConnection connection) {
        if (isStopped()) {
            return;
        }

        String responseString = null;
        if (isRetrieveResponseString()) {
            responseString = retrieveResponseString(connection);
        }
        invokeDrillFinish(url, responseString);
    }

    //==============================================================================================
    // Http Utils
    //==============================================================================================

    private static String retrieveResponseString(HttpURLConnection connection) {
        if (connection != null) {
            InputStream entityStream = null;
            try {
                entityStream = getEntityFromConnection(connection);
                byte[] entity = dumpStream(entityStream);
                if (entity != null) {
                    return new String(entity, "Utf-8");
                }
            } catch (Exception ignored) {
            } finally {
                closeQuietly(entityStream);
            }
        }
        return null;
    }

    private static InputStream getEntityFromConnection(HttpURLConnection connection) {
        InputStream entity = null;
        if (connection != null) {
            try {
                entity = connection.getInputStream();
            } catch (IOException ioe) {
                entity = connection.getErrorStream();
            }
        }
        return entity;
    }

    private static byte[] dumpStream(InputStream stream) throws IOException {
        byte[] result = null;
        if (stream != null) {
            ByteArrayOutputStream bytes = null;
            try {
                bytes = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = stream.read(buffer)) != -1) {
                    bytes.write(buffer, 0, len);
                }
                result = bytes.toByteArray();
            } finally {
                closeQuietly(bytes);
            }
        }
        return result;
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
