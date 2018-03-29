package com.honeycomb.driller.referrer;

import android.content.Context;

import com.honeycomb.driller.referrer.impl.CompositeReferrerDriller;
import com.honeycomb.driller.referrer.impl.HttpUrlDrillerFactory;
import com.honeycomb.driller.referrer.impl.MockReferrerDriller;
import com.honeycomb.driller.referrer.impl.ReferrerDrillerImpl;
import com.honeycomb.driller.referrer.impl.WebViewUrlDrillerFactory;

import java.util.LinkedList;
import java.util.List;

public class ReferrerDrillerFactory {

    public static IReferrerDriller createHttpDriller(Context context) {
        return buildUpon(context).addHttpDriller().build();
    }

    public static IReferrerDriller createWebViewDriller(Context context) {
        return buildUpon(context).addWebViewDriller().build();
    }

    public static IReferrerDriller createMockSuccessfulDriller(Context context,
                                                               long delay, String referrer) {
        return buildUpon(context).addMockSuccessfulDriller(delay, referrer).build();
    }

    public static IReferrerDriller createMockFailureDriller(Context context, long delay) {
        return buildUpon(context).addMockFailureDriller(delay).build();
    }

    public static IReferrerDriller createDriller(Context context, IReferrerDriller driller) {
        return buildUpon(context).addDriller(driller).build();
    }

    public static Builder buildUpon(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private final Context mContext;
        private final List<IReferrerDriller> mDrillers;

        Builder(Context context) {
            mContext = context.getApplicationContext();
            mDrillers = new LinkedList<>();
        }

        public Builder addHttpDriller() {
            addDriller(new ReferrerDrillerImpl(mContext, new HttpUrlDrillerFactory()));
            return this;
        }

        public Builder addWebViewDriller() {
            addDriller(new ReferrerDrillerImpl(mContext, new WebViewUrlDrillerFactory()));
            return this;
        }

        public Builder addMockSuccessfulDriller(long delay, String referrer) {
            addDriller(new MockReferrerDriller(true, delay, referrer));
            return this;
        }

        public Builder addMockFailureDriller(long delay) {
            addDriller(new MockReferrerDriller(false, delay, null));
            return this;
        }

        public Builder addDriller(IReferrerDriller driller) {
            mDrillers.add(driller);
            return this;
        }

        public IReferrerDriller build() {
            final IReferrerDriller driller;

            int count = mDrillers.size();
            if (count <= 0) {
                throw new IllegalArgumentException("Must specific at least one driller.");
            } else if (count == 1) {
                driller = mDrillers.get(0);
            } else {
                driller = new CompositeReferrerDriller(mDrillers);
            }

            return driller;
        }
    }
}
