package com.luxtech_eg.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
    TextView humidityView;
    TextView windView;
    TextView pressureView;
    TextView descView;
    TextView dayTV;
    ImageView weatherIcon;

    private static final int LOADER_ID = 1;
    String TAG=DetailFragment.class.getSimpleName();
    String forecast;
    ShareActionProvider mShareActionProvider;
    Uri mUri;
    static final String DETAIL_URI = "URI";

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
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,



    };
    private static final int COL_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_HUMIDITY = 5;
    private static final int COL_PRESSURE = 6;
    private static final int COL_WIND_SPEED =7;
    private static final int COL_DEGREES = 8;
    private static final int COL_WEATHER_ID = 9;

    public DetailFragment() {
        Log.d(TAG, "DetailFragment");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        dayTV =(TextView) rootView.findViewById(R.id.tv_day);
        dateview=(TextView) rootView.findViewById(R.id.tv_date);
        maxTempView=(TextView) rootView.findViewById(R.id.tv_max_temp);
        minTempView=(TextView) rootView.findViewById(R.id.tv_min_temp);
        humidityView =(TextView) rootView.findViewById(R.id.tv_humidity);
        windView=(TextView) rootView.findViewById(R.id.tv_wind);
        pressureView=(TextView)rootView.findViewById(R.id.tv_pressure);
        descView=(TextView)rootView.findViewById(R.id.tv_desc);
        weatherIcon=(ImageView)rootView.findViewById(R.id.ic_icon);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        // register rh loader with loader manager
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    Intent createShareIntent(){
        Log.d(TAG, "createShareIntent");
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
        Log.d(TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.detailfragment, menu);
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
        Log.v(TAG, "onCreateLoader");
        //make the query
        Intent intent = getActivity().getIntent();
        if ( null != mUri ) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return  null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // can modify ui
        Log.v(TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        String dateString = Utility.getFormattedMonthDay(getActivity(), data.getLong(COL_WEATHER_DATE));

        String weatherDescription =
                data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

        String low = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        String wind= Utility.getFormattedWind(getActivity(), data.getFloat(COL_WIND_SPEED), data.getFloat(COL_DEGREES));

        String day = Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE));

        String desc = data.getString(COL_WEATHER_DESC);

        String humidity = Utility.getFormattedHumidity(getActivity(), data.getDouble(COL_HUMIDITY));

        String pressure = Utility.getFormattedPressure(getActivity(), data.getDouble(COL_PRESSURE));

        int iconID = Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_ID));
        dayTV.setText(day);
        dateview.setText(dateString);
        maxTempView.setText(high);
        minTempView.setText(low);
        windView.setText(wind);
        descView.setText(desc);
        pressureView.setText(pressure);
        humidityView.setText(humidity);
        weatherIcon.setImageResource(iconID);


        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        // avoiding thread happened before main thread
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(TAG, "onLoaderReset");
        // no data to be cleared
    }
    void onLocationChanged( String newLocation ) {
        Log.v(TAG, "onLocationChanged");
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }
}
