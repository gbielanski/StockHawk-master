package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static com.udacity.stockhawk.ui.ChartActivity.START_SYMBOL_KEY;

public class StockWidgetRemoteViewsService extends RemoteViewsService {
        private static final String[] STOCK_COLUMNS = {
                Contract.Quote.TABLE_NAME + "." + Contract.Quote._ID,
                Contract.Quote.COLUMN_SYMBOL,
                Contract.Quote.COLUMN_PRICE,
                Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                Contract.Quote.COLUMN_PERCENTAGE_CHANGE
        };
        // these indices must match the projection
        private static final int INDEX_SYMBOL = 1;
        private static final int INDEX_PRICE = 2;
        private static final int INDEX_ABSOLUTE_CHANGE = 3;
        private static final int INDEX_PERCENTAGE_CHANGE = 4;

        @Override
        public RemoteViewsFactory onGetViewFactory(Intent intent) {
            final DecimalFormat dollarFormatWithPlus;
            final DecimalFormat dollarFormat;
            final DecimalFormat percentageFormat;
            dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus.setPositivePrefix("+$");
            percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
            percentageFormat.setMaximumFractionDigits(2);
            percentageFormat.setMinimumFractionDigits(2);
            percentageFormat.setPositivePrefix("+");

            return new RemoteViewsFactory() {
                private Cursor data = null;

                @Override
                public void onCreate() {
                    // Nothing to do
                }

                @Override
                public void onDataSetChanged() {
                    if (data != null) {
                        data.close();
                    }
                    // This method is called by the app hosting the widget (e.g., the launcher)
                    // However, our ContentProvider is not exported so it doesn't have access to the
                    // data. Therefore we need to clear (and finally restore) the calling identity so
                    // that calls use our process and permission
                    final long identityToken = Binder.clearCallingIdentity();

                    data = getContentResolver().query(Contract.Quote.URI,
                            STOCK_COLUMNS,
                            null,
                            null,
                            Contract.Quote.COLUMN_SYMBOL);
                    Binder.restoreCallingIdentity(identityToken);
                }

                @Override
                public void onDestroy() {
                    if (data != null) {
                        data.close();
                        data = null;
                    }
                }

                @Override
                public int getCount() {
                    return data == null ? 0 : data.getCount();
                }

                @Override
                public RemoteViews getViewAt(int position) {
                    if (position == AdapterView.INVALID_POSITION ||
                            data == null || !data.moveToPosition(position)) {
                        return null;
                    }
                    RemoteViews views = new RemoteViews(getPackageName(),
                            R.layout.widget_stock_list_item);

                    String stockSymbol = data.getString(INDEX_SYMBOL);
                    String price = dollarFormat.format(data.getFloat(INDEX_PRICE));
                    views.setTextViewText(R.id.widget_symbol, stockSymbol);
                    views.setTextViewText(R.id.widget_price, price);
                    float rawAbsoluteChange = data.getFloat(INDEX_ABSOLUTE_CHANGE);
                    float percentageChange = data.getFloat(INDEX_PERCENTAGE_CHANGE);

                    if (rawAbsoluteChange > 0) {
                        views.setTextColor(R.id.widget_change, Color.parseColor(getString(R.string.widget_color_change_plus)));
                    } else {
                        views.setTextColor(R.id.widget_change, Color.parseColor(getString(R.string.widget_color_change_minus)));
                    }

                    String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                    String percentage = percentageFormat.format(percentageChange / 100);

                    Context context = StockWidgetRemoteViewsService.this;
                    if (PrefUtils.getDisplayMode(context)
                            .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
                        views.setTextViewText(R.id.widget_change, change);
                    } else {
                        views.setTextViewText(R.id.widget_change, percentage);
                    }

                    final Intent fillInIntent = new Intent();
                    fillInIntent.putExtra(START_SYMBOL_KEY, stockSymbol);
                    views.setOnClickFillInIntent(R.id.widget_stock_list_item, fillInIntent);
                    return views;
                }

                @Override
                public RemoteViews getLoadingView() {
                    return new RemoteViews(getPackageName(), R.layout.widget_stock_list_item);
                }

                @Override
                public int getViewTypeCount() {
                    return 1;
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public boolean hasStableIds() {
                    return true;
                }
            };
        }
    }