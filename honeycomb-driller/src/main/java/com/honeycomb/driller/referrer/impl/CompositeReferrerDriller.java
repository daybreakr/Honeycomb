package com.honeycomb.driller.referrer.impl;

import com.honeycomb.driller.referrer.IReferrerDriller;
import com.honeycomb.util.Preconditions;

import java.util.Collection;

public class CompositeReferrerDriller implements IReferrerDriller {
    private final IReferrerDriller[] mDrillers;

    public CompositeReferrerDriller(Collection<IReferrerDriller> drillers) {
        drillers = Preconditions.checkCollectionNotEmpty(drillers, "Driller");
        mDrillers = drillers.toArray(new IReferrerDriller[0]);
    }

    public CompositeReferrerDriller(IReferrerDriller... drillers) {
        mDrillers = Preconditions.checkArrayElementsNotNull(drillers, "Driller");
    }

    @Override
    public void drillReferrer(String packageName, String clickUrl,
                              final DrillReferrerCallback callback) {
        doDrill(packageName, clickUrl, callback, 0);
    }

    private void doDrill(String packageName, String clickUrl, final DrillReferrerCallback callback,
                         final int drillerIndex) {
        if (drillerIndex < 0 || drillerIndex >= mDrillers.length) {
            invokeFail(packageName, clickUrl, ERROR_UNKNOWN, callback);
            return;
        }

        IReferrerDriller driller = mDrillers[drillerIndex];
        driller.drillReferrer(packageName, clickUrl, new DrillReferrerCallback() {
            @Override
            public void onDrillFinished(String packageName, String clickUrl, String referrer) {
                invokeSuccess(packageName, clickUrl, referrer, callback);
            }

            @Override
            public void onDrillFailed(String packageName, String clickUrl, int errorCode) {
                int nextDrillerIndex = drillerIndex + 1;
                if (nextDrillerIndex < mDrillers.length) {
                    doDrill(packageName, clickUrl, callback, nextDrillerIndex);
                } else {
                    invokeFail(packageName, clickUrl, errorCode, callback);
                }
            }
        });
    }

    private static void invokeSuccess(String packageName, String clickUrl, String referrer,
                                      DrillReferrerCallback callback) {
        if (callback != null) {
            callback.onDrillFinished(packageName, clickUrl, referrer);
        }
    }

    private static void invokeFail(String packageName, String clickUrl, int errorCode,
                                   DrillReferrerCallback callback) {
        if (callback != null) {
            callback.onDrillFailed(packageName, clickUrl, errorCode);
        }
    }
}
