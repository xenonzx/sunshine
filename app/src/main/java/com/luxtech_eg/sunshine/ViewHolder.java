package com.luxtech_eg.sunshine;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ahmed on 19/12/15.
 */
public class ViewHolder {
    public final ImageView iconView;
    public final TextView dateView;
    public final TextView descriptionView;
    public final TextView highTempView;
    public final TextView lowTempView;

    ViewHolder(View view){
        iconView=(ImageView)view.findViewById(R.id.list_item_icon);
        dateView=(TextView)view.findViewById(R.id.list_item_date_textview);
        descriptionView=(TextView)view.findViewById(R.id.list_item_forecast_textview);
        highTempView=(TextView)view.findViewById(R.id.list_item_high_textview);
        lowTempView=(TextView)view.findViewById(R.id.list_item_low_textview);
    }
}
