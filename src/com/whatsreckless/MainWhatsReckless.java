/**
 * Copyright 2014-2015 Luke Gordon and Kenny Neal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whatsreckless;

import java.util.Map;

import com.whatsreckless.infoPuller.InformationAsyncTask;
import com.whatsreckless.location.LocationLookup;
import com.whatsreckless.location.StateChangeListener;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainWhatsReckless extends Activity {

    /**
     * A tag used for logging purposes
     */
    private static final String LOG_TAG = MainWhatsReckless.class.getSimpleName();

    /**
     * Service used for looking up the user's location
     */
    LocationLookup locationLookupService = null;
    StateChangeListener stateChangeListener = null;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main_whats_reckless );
        if ( savedInstanceState == null ) {
            getFragmentManager().beginTransaction().add( R.id.container, new PlaceholderFragment() ).commit();
        }
        if ( locationLookupService == null ) {
            locationLookupService = new LocationLookup( this );
        }
        if (stateChangeListener == null){
        	stateChangeListener = new StateChangeListener(){

				@Override
				public void onStateLocationChanged(String oldState, String newState) {
					renderStateInfo(newState);
				}
        	};
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main_whats_reckless, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if ( id == R.id.action_settings ) {
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
            View rootView = inflater.inflate( R.layout.fragment_main_whats_reckless, container, false );
            return rootView;
        }
    }

    private void renderStateInfo(String state){
    	// Fetch the table we're going to insert rows into
    	TableLayout recklessInfoTable = (TableLayout) findViewById( R.id.recklessInfoTable );
    	// Clear the table
    	recklessInfoTable.removeAllViews();

    	TextView stateTextView = (TextView) findViewById( R.id.txtCurrentState );
    	if ( Log.isLoggable( LOG_TAG, Log.DEBUG ) ) {
    		Log.d( LOG_TAG, String.format( "Fetching reckless information for %s", state ) );
    	}
    	stateTextView.setText( state );

    	try {
    		// Get the reckless driving information from Wikipedia
    		Map<String, String> information = new InformationAsyncTask( this, state ).execute().get();

    		// Iteratively add each row to the table header to the row
    		String[] columnNames = information.get( InformationAsyncTask.COLUMN_NAMES ).split( "," );
    		for ( String columnName : columnNames ) {
    			TableRow row = new TableRow( this );
    			// Add the column name
    			TextView cName = new TextView( this );
    			cName.setText( columnName );
    			row.addView( cName );
    			// Add the data for that column name
    			TextView cInfo = new TextView( this );
    			String info = information.get( columnName );
    			cInfo.setText( info );
    			row.addView( cInfo );
    			// Is debugging enabled?
    			if ( Log.isLoggable( LOG_TAG, Log.DEBUG ) ) {
    				Log.d( LOG_TAG, String.format( "%s => %s", columnName, info ) );
    			}
    			// Add the row
    			recklessInfoTable.addView( row );
    		}
    	} catch ( Exception e ) {
    		Log.e( LOG_TAG, e.getMessage() );
    	}

    }
    
    public void pullState( View v ) {
        // Fetch the table we're going to insert rows into
        TableLayout recklessInfoTable = (TableLayout) findViewById( R.id.recklessInfoTable );
        // Clear the table
        recklessInfoTable.removeAllViews();

        Location loc = locationLookupService.getCurrentLocation();

        if ( loc != null ) {
            // Set the state we're fetching information for
            String state = locationLookupService.getCurrentState();
            if ( state != null ) {
            	renderStateInfo(state);
            } else {

            	TableRow row = new TableRow( this );
                // Add the column name
                TextView cName = new TextView( this );
                cName.setText( "Unable to find current state for Location:" + loc.getLatitude() + "," + loc.getLongitude() );
                row.addView( cName );
                recklessInfoTable.addView( row );
            }
        } else {
        	TableRow row = new TableRow( this );
            // Add the column name
            TextView cName = new TextView( this );
            cName.setText( "No GPS Fix, go to window or outside" );
            row.addView( cName );
            recklessInfoTable.addView( row );
        }
    }
}
