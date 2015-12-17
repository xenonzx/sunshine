package com.luxtech_eg.sunshine;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.luxtech_eg.sunshine.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by ahmed on 26/11/15.
 */
public  class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static final int FORECAST_LOADER_ID=0;
    String TAG=ForecastFragment.class.getSimpleName();
    final static String appid="2de143494c0b295cca9337e1e96b00e0";
    private static final String[] FORECAST_COLUMNS = {
        WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                    WeatherContract.WeatherEntry.COLUMN_DATE,
                    WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                    WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                    WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                    WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                    WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                    WeatherContract.LocationEntry.COLUMN_LATITUDEE,
                    WeatherContract.LocationEntry.COLUMN_LONGITUDE
            };
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;


    ArrayList<String> tempInfo;
    ForecastAdapter mForecastAdapter;
    SharedPreferences sp;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //should say that this fragment has options menu
        setHasOptionsMenu(true);
        tempInfo= new ArrayList<String>();

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }
    public void onActivityCreated(Bundle savedInstanceState) {
               getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
               super.onActivityCreated(savedInstanceState);
           }
    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_refresh){
            updateWeather();
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            Intent i = new Intent( getActivity(),SettingsActivity.class );
            startActivity(i);
            return true;
        }
        if (item.getItemId() == R.id.action_map) {
            openMaps();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    void openMaps(){
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q="+sp.getString(getString(R.string.prefs_location_key),"def"));
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        if (i.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(i);
        }
        else{
            Log.e(TAG,"no maps package was found");
            Toast.makeText(getActivity(),"no maps package was found",Toast.LENGTH_LONG).show();
        }
    }
    public void updateWeather(){
        Log.v(TAG, "refresh clicked");
        String key=getString(R.string.prefs_location_key);
        String defVal = getString(R.string.prefs_location_default_value);

        String mountainViewPostCode=sp.getString(key,defVal);
        //Log.v(TAG, sp.getString(key, defVal));
        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
        String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String locationSetting = Utility.getPreferredLocation(getActivity());
        ListView lv= (ListView)rootView.findViewById(R.id.listview_forcast);

        tempInfo.add("today-23");
        tempInfo.add("sun-23");
        tempInfo.add("mon-23");
        tempInfo.add("tuey-23");


        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
        locationSetting, System.currentTimeMillis());
        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri, null, null, null, sortOrder);
        mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);
        lv.setAdapter(mForecastAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                    startActivity(intent);
                }
            }
        });


        return rootView;
    }
    /* The date/time conversion code is going to be moved outside the asynctask later,
        * so for convenience we're breaking it out into its own method now.
        */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        Log.v(TAG, sp.getString(getString(R.string.pref_temp_setting_key),"def"));
        if (sp.getString(getString(R.string.pref_temp_setting_key),"def")==getString(R.string.pref_temp_setting_fahrenheit)){
            //converting from cels to f
            high=high*1.8+32;
            low=low*1.8+32;
        }
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        if (sp.getString(getString(R.string.pref_temp_setting_key),"def")==getString(R.string.pref_temp_setting_fahrenheit)){
            highLowStr=highLowStr+"F";
        }
        else{
            highLowStr=highLowStr+"C";
        }
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        for (String s : resultStrs) {
            Log.v(TAG, "Forecast entry: " + s);
        }
        return resultStrs;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mForecastAdapter.swapCursor(null);
    }
}