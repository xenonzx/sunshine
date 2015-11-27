package com.luxtech_eg.sunshine;

/**
 * Created by ahmed on 27/11/15.
 */
import org.json.JSONException;
import org.json.JSONObject;


public class WeatherDataParser {
    final static String TAG=WeatherDataParser.class.getSimpleName();
    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        // TODO: add parsing code here
        //JSONObject income= new JSONObject(weatherJsonStr);
        //income.getJSONArray("list").getJSONObject(0).getJSONObject("temp").getDouble("max");
        return new JSONObject(weatherJsonStr).getJSONArray("list").getJSONObject(dayIndex).getJSONObject("temp").getDouble("max");
    }

}
