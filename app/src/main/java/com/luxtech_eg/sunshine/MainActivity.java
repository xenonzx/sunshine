package com.luxtech_eg.sunshine;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView lv= (ListView)rootView.findViewById(R.id.listview_forcast);
            ArrayList<String> tempInfo= new ArrayList<String>();
            tempInfo.add("today-23");
            tempInfo.add("sun-23");
            tempInfo.add("mon-23");
            tempInfo.add("tuey-23");

            ArrayAdapter<String>  mArrayAdapter= new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,tempInfo);
            lv.setAdapter(mArrayAdapter);
            return rootView;
        }
    }
}
