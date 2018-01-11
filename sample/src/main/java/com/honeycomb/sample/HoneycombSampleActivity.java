package com.honeycomb.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.honeycomb.id.HoneycombId;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HoneycombSampleActivity extends AppCompatActivity {
    @BindView(R.id.device_id)
    TextView mDeviceId;

    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_honeycomb_sample);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDeviceId();
    }

    //==============================================================================================
    // Presenter
    //==============================================================================================

    private void refreshDeviceId() {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                String deviceId = HoneycombId.getInstance().getDeviceId();

                updateDeviceId(deviceId);
            }
        });
    }

    //==============================================================================================
    // View
    //==============================================================================================

    private void updateDeviceId(final String deviceId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDeviceId.setText(deviceId);
            }
        });
    }
}
