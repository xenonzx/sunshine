package com.luxtech_eg.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by ahmed on 16/12/15.
 */
public class ForecastAdapter extends CursorAdapter {
    private static final String TAG="ForecastAdapter";
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    Context mContext;
    public ForecastAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
        mContext=context;
    }
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * Prepare the weather high/lows for presentation.
     */

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(TAG,"newView was called");
        // Choose the layout type

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        // TODO: Determine layoutId from viewType
        if(viewType==VIEW_TYPE_TODAY)
        {
            layoutId= R.layout.list_item_forecast_today;
        }
        else if(viewType==VIEW_TYPE_FUTURE_DAY)  {
            layoutId= R.layout.list_item_forecast;
        }
        //saving in view holder and setting tag
        View view= LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder vh=new ViewHolder(view);
        view.setTag(vh);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(TAG,"bindView was called");
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        // bind data to view using tag using Tag
        //the get tag should return the tag object passed which was of class ViewHolder
        ViewHolder vh = (ViewHolder)view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        // Use placeholder image for now
        vh.iconView.setImageResource(R.drawable.ic_launcher);

        // TODO Read date from cursor
        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        String friendlysDate=Utility.getFriendlyDayString(context, date);
        vh.dateView.setText(friendlysDate);

        // TODO Read weather forecast from cursor
        String forecastDesc=cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        TextView forecastDescView= (TextView) view.findViewById(R.id.list_item_forecast_textview);
        forecastDescView.setText(forecastDesc);
        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        vh.highTempView.setText(Utility.formatTemperature(context,high, isMetric));

        // TODO Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        vh.lowTempView.setText(Utility.formatTemperature(context,low, isMetric));
    }
}
