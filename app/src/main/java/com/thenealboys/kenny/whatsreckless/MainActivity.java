package com.thenealboys.kenny.whatsreckless;

import android.location.Location;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    /**
     * Service used for looking up the user's location
     */
    LocationLookup locationLookupService = null;
    StateChangeListener stateChangeListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Loading current location", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                pullState();
            }
        });


        if ( locationLookupService == null ) {
            locationLookupService = new LocationLookup( this );
        }
        if (stateChangeListener == null){
            stateChangeListener = new StateChangeListener(){

                @Override
                public void onStateLocationChanged(String oldState, String newState) {
                    MainActivityFragment frag = (MainActivityFragment)getSupportFragmentManager().findFragmentById (R.id.fragment);
                    frag.renderStateInfo(newState);;
                }
            };
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void pullState( ) {

        MainActivityFragment frag = (MainActivityFragment)getSupportFragmentManager().findFragmentById (R.id.fragment);
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
