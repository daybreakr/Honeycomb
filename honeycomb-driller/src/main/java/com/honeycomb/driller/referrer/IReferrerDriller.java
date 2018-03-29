package com.honeycomb.driller.referrer;

public interface IReferrerDriller {
    int ERROR_UNKNOWN = 0;
    int ERROR_PACKAGE_MISMATCH = 1;
    int ERROR_NO_REFERRER = 2;
    int ERROR_REDIRECT_ERROR = 3;
    int ERROR_CREATING_URL_DRILLER = 4;

    interface DrillReferrerCallback {

        void onDrillFinished(String packageName, String clickUrl, String referrer);

        void onDrillFailed(String packageName, String clickUrl, int errorCode);
    }

    void drillReferrer(String packageName, String clickUrl, DrillReferrerCallback callback);
}
