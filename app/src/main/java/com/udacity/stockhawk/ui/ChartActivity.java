package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class ChartActivity extends AppCompatActivity {
    TextView mSymbolTextView;
    
    public static String START_SYMBOL_KEY = "SYMBOL";
    private LineChart mChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        mSymbolTextView = (TextView)findViewById(R.id.chart_symbol);
        String symbol = getIntent().getStringExtra(START_SYMBOL_KEY);
        mSymbolTextView.setText(symbol);

        mChart = (LineChart) findViewById(R.id.chart_stock);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        mChart.setDrawGridBackground(false);
        mChart.setExtraTopOffset(24f);

        XAxis x = mChart.getXAxis();
        x.setValueFormatter(new MyCustomXAxisValueFormatter());
        x.setLabelCount(6, false);
        x.setTextColor(Color.WHITE);
        x.setTextSize(18f);
        x.setPosition(XAxis.XAxisPosition.TOP);
        //x.setDrawGridLines(false);
        x.setAxisLineColor(Color.WHITE);
        YAxis y = mChart.getAxisLeft();
        //y.setTypeface(mTfLight);
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setTextSize(24f);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        //y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.invalidate();
        Uri stackSymbolURI = Contract.Quote.URI.buildUpon().appendPath(symbol).build();
        String [] projection = {Contract.Quote.COLUMN_HISTORY};
        Cursor cursor = getContentResolver().query(stackSymbolURI, projection, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            setChartData(cursor);
            refreshChartDisplay();
        }
    }

    private void refreshChartDisplay(){
        mChart.invalidate();

    }

    
    private String[] cursorToHistory(Cursor cursor){
        String historyString = cursor.getString(0);
        String[] historyStringArray = historyString.split("\n");
        Collections.reverse(Arrays.asList(historyStringArray));
        
        return historyStringArray;
    }
    
    private void setChartData(Cursor cursor) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        String[] historyStringArray = cursorToHistory(cursor);
        
        for (int i = 0; i < historyStringArray.length; i++) {
            String [] particularStockValueAndDate = historyStringArray[i].split(",");
            yVals.add(new Entry(Float.valueOf(particularStockValueAndDate[0]), Float.valueOf(particularStockValueAndDate[1])));
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
            data.setValueTextSize(24f);
            data.setDrawValues(false);
            // set data
            mChart.setData(data);
        }
    }

    private class MyCustomXAxisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/yy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) value);
            return formatter.format(calendar.getTime());
        }
    }

}
