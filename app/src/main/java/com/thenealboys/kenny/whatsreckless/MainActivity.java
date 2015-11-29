package com.thenealboys.kenny.whatsreckless;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.thenealboys.kenny.whatsreckless.location.LocationLookup;
import com.thenealboys.kenny.whatsreckless.location.StateChangeListener;
import com.thenealboys.kenny.whatsreckless.setttings.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    /**
     * Service used for looking up the user's location
     */
    LocationLookup locationLookupService = null;
    StateChangeListener stateChangeListener = null;
    private static final int RESULT_SETTINGS = 1;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(prefs.getBoolean("current_location_switch", false)){
                    Snackbar.make(view, "Loading current location", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    pullState();
                } else {
                    Snackbar.make(view, "Current location disabled, enable in settings", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });


        if ( locationLookupService == null ) {
            if (prefs.getBoolean("current_location_switch", true)){
                locationLookupService = new LocationLookup( this );
            }
        }
        if (locationLookupService != null && stateChangeListener == null && prefs.getBoolean("state_tracking_switch", false)){
            stateChangeListener = new StateChangeListener(){

                @Override
                public void onStateLocationChanged(String oldState, String newState) {
                    MainActivityFragment frag = (MainActivityFragment)getSupportFragmentManager().findFragmentById (R.id.fragment);
                    frag.renderStateInfo(newState);;
                }
            };
            locationLookupService.registerStateChangeListener(stateChangeListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {

            case R.id.title_activity_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                return true;
            case R.id.title_read_state_info:
                MainActivityFragment frag = (MainActivityFragment)getSupportFragmentManager().findFragmentById (R.id.fragment);
                frag.readInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void pullState( ) {

        MainActivityFragment frag = (MainActivityFragment)getSupportFragmentManager().findFragmentById (R.id.fragment);
        if ( locationLookupService == null ) {
            locationLookupService = new LocationLookup( this );
        }
        Location loc = locationLookupService.getCurrentLocation();

        if ( loc != null ) {
            // Set the state we're fetching information for
            String state = locationLookupService.getCurrentState();
            if ( state != null ) {
                frag.select(state);
            } else {
                Toast.makeText(this,getString(R.string.unable_to_find_state_for_location, loc.getLatitude(), loc.getLongitude()), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.no_gps_fix , Toast.LENGTH_LONG).show();
        }
    }
}
