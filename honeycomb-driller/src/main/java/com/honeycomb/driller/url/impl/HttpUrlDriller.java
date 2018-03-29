package com.honeycomb.driller.url.impl;

import com.honeycomb.log.HLog;
import com.honeycomb.util.IoUtils;
import com.honeycomb.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpUrlDriller extends BaseUrlDriller {
    private static final String TAG = "HttpUrlDriller";

    public static class Options {
        // Timeout in milliseconds when making connection.
        public int connectTimeout;

        // Timeout int milliseconds when retrieving response.
        public int readTimeout;

        public Options() {
            this.connectTimeout = 15000; // 15 seconds
            this.readTimeout = 20000; // 20 seconds
        }
    }

    private Options mOptions;

    private ExecutorService mExecutor;

    public HttpUrlDriller() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public HttpUrlDriller setOptions(Options options) {
        mOptions = options;
        return this;
    }

    @Override
    public void onStartDrill(final String url) {
        // Supply default options if not set before.
        if (mOptions == null) {
            mOptions = new Options();
        }

        invokeDrillStart(url);

        drill(url, 0);
    }

    @Override
    protected void onDestroyDriller() {
        super.onDestroyDriller();
        mExecutor.shutdown();
    }

    private void drill(final String url, final int counter) {
        if (isStopped()) {
            HLog.v(TAG, "Drill been stopped.");
            return;
        }

        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                doDrill(url, counter);
            }
        });
    }

    private void doDrill(String url, int counter) {
        if (counter == 0) {
            HLog.v(TAG, "Landing: " + url);
        }

        HttpURLConnection connection = null;
        try {
            // 1. Make a connection to target URL.

            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();

            String userAgent = getUserAgent();
            if (userAgent != null) {
                connection.setRequestProperty("User-Agent", userAgent);
            }

            connection.setConnectTimeout(mOptions.connectTimeout);
            connection.setReadTimeout(mOptions.readTimeout);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);

            // 2. Retrieve response status.

            int status = connection.getResponseCode();
            switch (status) {
                case HttpURLConnection.HTTP_OK: { // 200
                    HLog.v(TAG, " - Page Finished(%d): %s", status, url);
                    finishDrill(url, connection);
                }
                break;

                case HttpURLConnection.HTTP_MOVED_TEMP: // 302
                case HttpURLConnection.HTTP_MOVED_PERM: // 301
                case HttpURLConnection.HTTP_SEE_OTHER: { // 303
                    int drillDepth = getDrillDepth();
                    if (drillDepth < 0 /* no limited */ || counter < drillDepth) { // Redirecting
                        String newUrl = retrieveLocation(connection, urlObj);

                        HLog.v(TAG, " - Redirecting(%d): %s", status, newUrl);
                        invokeDrillRedirect(newUrl);

                        drill(newUrl, counter + 1);
                    } else { // Redirection limited
                        String errorMsg = "Drill depth exceed " + drillDepth;
                        HLog.v(TAG, " - Fail(%d): %s", status, errorMsg);
                        invokeFail(url, new Exception(errorMsg));
                    }
                }
                break;

                default: {
                    HLog.v(TAG, " - Fail(%d)", status);
                    Exception e = new Exception("Drilling error: Invalid URL, Status: " + status);
                    invokeFail(url, e);
                }
                break;
            }

            connection.getInputStream().close();
        } catch (Exception exception) {
            HLog.e(exception, TAG, " - Fail: " + exception);
            invokeFail(url, exception);
        } catch (Error error) {
            HLog.e(error, TAG, " - Fail: " + error);
            invokeFail(url, new Exception(error));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void finishDrill(String url, HttpURLConnection connection) {
        HLog.v(TAG, " - Done.");

        String responseString = null;
        // Load source means retrieve response string in HttpUrlDriller.
        if (isRetrieveResponseString() || isLoadSource()) {
            responseString = retrieveResponseString(connection);
        }
        invokeDrillFinish(url, responseString);
    }

    //==============================================================================================
    // Http Utils
    //==============================================================================================

    private static String retrieveLocation(HttpURLConnection connection, URL urlObj) {
        String location = connection.getHeaderField("Location");
        if (location.startsWith("/")) {
            String protocol = urlObj.getProtocol();
            String host = urlObj.getHost();
            location = protocol + "://" + host + location;
        }
        return location;
    }

    private static String retrieveResponseString(HttpURLConnection connection) {
        if (connection != null) {
            InputStream entityStream = null;
            try {
                entityStream = getEntityFromConnection(connection);
                byte[] entity = dumpStream(entityStream);
                if (entity != null) {
                    return new String(entity, StringUtils.UTF_8);
                }
            } catch (Exception ignored) {
            } finally {
                IoUtils.closeQuietly(entityStream);
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
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            IoUtils.dump(stream, bytes);
            result = bytes.toByteArray();
        }
        return result;
    }
}
