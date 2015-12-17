package com.luxtech_eg.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(DetailActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.menu_item_share) {



        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        String TAG=PlaceholderFragment.class.getSimpleName();
        TextView forecastTV;
        String forecast;
        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            forecastTV=(TextView) rootView.findViewById(R.id.tv_forcast);

            Intent intent = getActivity().getIntent();
            if (intent != null) {
                forecast = intent.getDataString();
            }

            //String forecast = getActivity().getIntent().getStringExtra("forecastString");
            if(null!= forecast) {
                forecastTV.setText(forecast);
            }
            return rootView;
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
            ShareActionProvider mShareActionProvider =(ShareActionProvider) MenuItemCompat.getActionProvider(item);
            if (mShareActionProvider!=null){
                mShareActionProvider.setShareIntent(createShareIntent());
            }
            else{
                Log.d(TAG,"share action provider is null");
            }

        }
    }
}
