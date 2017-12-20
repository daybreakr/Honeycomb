package com.google.honeycomb.driller.referrer;

public class CompositeReferrerDriller implements IReferrerDriller {
    private final IReferrerDriller[] mDrillers;
    private int mDrillerIndex;

    private int mLastErrorCode;

    public CompositeReferrerDriller(IReferrerDriller... drillers) {
        if (drillers == null || drillers.length < 1) {
            throw new IllegalArgumentException("Must at least supply one driller.");
        }
        mDrillers = drillers;

        mDrillerIndex = -1;
    }

    @Override
    public void drillReferrer(String packageName, String clickUrl,
                              final DrillReferrerCallback callback) {
        mDrillerIndex++;
        if (mDrillerIndex < mDrillers.length) {
            IReferrerDriller driller = mDrillers[mDrillerIndex];
            driller.drillReferrer(packageName, clickUrl, new DrillReferrerCallback() {
                @Override
                public void onDrillFinished(String packageName, String clickUrl,
                                            String referrer) {
                    callback.onDrillFinished(packageName, clickUrl, referrer);
                }

                @Override
                public void onDrillFailed(String packageName, String clickUrl, int errorCode) {
                    mLastErrorCode = errorCode;
                    drillReferrer(packageName, clickUrl, callback);
                }
            });
        } else {
            callback.onDrillFailed(packageName, clickUrl, mLastErrorCode);
        }
    }
}
