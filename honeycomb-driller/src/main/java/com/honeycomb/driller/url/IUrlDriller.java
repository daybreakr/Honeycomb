package com.honeycomb.driller.url;

public interface IUrlDriller {

    interface Listener {

        /**
         * Called when the drilling process start
         *
         * @param url url where drilling process started
         */
        void onDrillerStart(String url);

        /**
         * Called when the drilling process detects a redirection
         *
         * @param url url where redirection is pointing to
         */
        void onDrillerRedirect(String url);

        /**
         * Called whenever the drilling process finishes
         *
         * @param url url where the drilling process ends
         */
        void onDrillerFinish(String url, String responseString);

        /**
         * Called when the drilling process fails, it will interrupt the drilling process.
         *
         * @param url       url where the drilling process stopped
         * @param exception exception with extended message of the error.
         */
        void onDrillerFail(String url, Exception exception);
    }

    /**
     * This method will set up a listener in this drill
     *
     * @param listener valid Listener or null
     */
    void setListener(Listener listener);

    /**
     * This method will set user agent in request
     *
     * @param userAgent User-Agent string
     */
    void setUserAgent(String userAgent);

    /**
     * Set the steps for URL drilling.
     *
     * @param drillDepth how deep we must drill root URL
     */
    void setDrillDepth(int drillDepth);

    /**
     * Set whether retrieving the response string or not while drill finished.
     *
     * @param retrieveResponseString whether retrieving response string or not.
     */
    void setRetrieveResponseString(boolean retrieveResponseString);

    /**
     * Start drilling from target URL
     *
     * @param url target URL used to drill with.
     */
    void drill(String url);

    /**
     * Stop current drilling progress.
     */
    void stop();

    /**
     * Destroy driller, releases resources if needed, once a driller been destroyed,
     * it could never start drilling again, all calls to method drill would fail immediately.
     */
    void destroy();
}
