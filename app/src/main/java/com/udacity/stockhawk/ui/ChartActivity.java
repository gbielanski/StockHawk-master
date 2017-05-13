package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    protected Typeface mTfLight;
    public static String START_SYMBOL_KEY = "SYMBOL";
    private static final int HISTORY_STOCK_LOADER = 123;
    private LineChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        String symbol = getIntent().getStringExtra(START_SYMBOL_KEY);
        Log.v(START_SYMBOL_KEY, "onCreate " + symbol);

        Bundle bundle = new Bundle();
        bundle.putString(START_SYMBOL_KEY, symbol);
        getSupportLoaderManager().initLoader(HISTORY_STOCK_LOADER, bundle, this);

        //mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        mChart = (LineChart) findViewById(R.id.chart_stock);
        mChart.setViewPortOffsets(0, 0, 0, 0);
        mChart.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        mChart.invalidate();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {Contract.Quote.COLUMN_HISTORY};
        String symbol = args.getString(START_SYMBOL_KEY);
        Log.v(START_SYMBOL_KEY, "onCreateLoader " + symbol);

        Uri stackSymbolURI = Contract.Quote.URI.buildUpon().appendPath(symbol).build();
        return new CursorLoader(this,
                stackSymbolURI,
                projection,
                null, null, null);
    }

    private void refreshChartDisplay(){
        mChart.invalidate();

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();

            setChartData(cursor);
            refreshChartDisplay();

            Log.v(START_SYMBOL_KEY, "onLoadFinished \n" + cursor.getString(0));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void setChartData(int count, float range) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 20;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "DataSet 1");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.WHITE);
            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData(set1);
            //data.setValueTypeface(mTfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            mChart.setData(data);
        }
    }

    private void setChartData(Cursor cursor) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        String historyString = cursor.getString(0);
        String[] historyStringArray = historyString.split("\n");

//        for (int i = historyStringArray.length-1; i > 0; i--) {
//        for (int i = 0; i < historyStringArray.length; i++) {
            for (int i = 0; i < historyStringArray.length; i++) {
            String [] particularStockValueAndDate = historyStringArray[i].split(",");
            yVals.add(new Entry(i, Float.valueOf(particularStockValueAndDate[1])));
            Log.v("Value", Float.valueOf(particularStockValueAndDate[1]).toString());
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "DataSet 1");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.WHITE);
            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData(set1);
            //data.setValueTypeface(mTfLight);
            data.setValueTextSize(9f);
            data.setDrawValues(true);

            // set data
            mChart.setData(data);
        }
    }

}
