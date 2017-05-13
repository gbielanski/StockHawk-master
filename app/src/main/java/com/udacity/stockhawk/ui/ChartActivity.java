package com.udacity.stockhawk.ui;

import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.udacity.stockhawk.R;

public class ChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        String symbol = getIntent().getStringExtra("SYMBOL");
        Log.v("SYMBOL", symbol);
    }
}
