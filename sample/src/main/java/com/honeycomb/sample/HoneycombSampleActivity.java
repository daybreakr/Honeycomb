package com.honeycomb.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.honeycomb.crash.HoneycombCrash;

public class HoneycombSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_honeycomb_sample);

        HoneycombCrash.getInstance();
    }
}
