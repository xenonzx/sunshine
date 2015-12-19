package com.luxtech_eg.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luxtech_eg.sunshine.data.WeatherContract;

/**
 * Created by ahmed on 19/12/15.
 */public  class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    TextView dateview;
    TextView maxTempView;
    TextView minTempView;
    TextView humedityView;
    TextView windView;
    TextView pressureView;
    TextView descView;
    TextView forecastTV;
    ImageView weatherIcon;

    private static final int LOADER_ID = 1;
    String TAG=DetailFragment.class.getSimpleName();
    String forecast;
    ShareActionProvider mShareActionProvider;


    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,



    };
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_HUMIDITY = 5;
    private static final int COL_PRESSURE = 6;
    private static final int COL_WIND_SPEED =7;
    private static final int COL_DEGREES = 8;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        forecastTV=(TextView) rootView.findViewById(R.id.tv_forcast);
        dateview=(TextView) rootView.findViewById(R.id.tv_date);
        maxTempView=(TextView) rootView.findViewById(R.id.tv_max_temp);
        minTempView=(TextView) rootView.findViewById(R.id.tv_min_temp);
        humedityView=(TextView) rootView.findViewById(R.id.tv_humidity);
        windView=(TextView) rootView.findViewById(R.id.tv_wind);
        pressureView=(TextView)rootView.findViewById(R.id.tv_pressure);
        descView=(TextView)rootView.findViewById(R.id.tv_desc);
        weatherIcon=(ImageView)rootView.findViewById(R.id.ic_icon);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // register rh loader with loader manager
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    Intent createShareIntent(){
        Intent mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, forecast + "#SUNSHINE");
        //mShareIntent.putExtra(Intent.EXTRA_TEXT, getActivity().getIntent().getStringExtra("forecastString") + "#SUNSHINE");
        return  mShareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment,menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // for compatibility  MenuItemCompat
        mShareActionProvider =(ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (forecast!=null){
            // if we already have a forecast
            mShareActionProvider.setShareIntent(createShareIntent());
        }
        else{
            Log.d(TAG, "share action provider is null");
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //make the query
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // can modify ui
        Log.v(TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        String dateString = Utility.formatDate(
                data.getLong(COL_WEATHER_DATE));

        String weatherDescription =
                data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

        String low = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);


        forecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
        TextView detailTextView = (TextView)getView().findViewById(R.id.tv_forcast);
        detailTextView.setText(forecast);
        dateview.setText(dateString);
        maxTempView.setText(high);
        minTempView.setText(low);
        // TODO: add wind
        // TODO: add humedity
        // TODO: add pressure
        // TODO: add desc
        //ThumedityView;
        TextView windView;
        TextView pressureView;
        TextView descView;

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        // avoiding thread happened before main thread
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // no data to be cleared
    }

}
