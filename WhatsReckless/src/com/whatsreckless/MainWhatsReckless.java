package com.whatsreckless;

import java.util.Map;

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

    public void pullState( View v ) {
        // Fetch the table we're going to insert rows into
        TableLayout recklessInfoTable = (TableLayout) findViewById( R.id.recklessInfoTable );
        // Clear the table
        recklessInfoTable.removeAllViews();

        String displayString = "No GPS Fix, go to window or outside";
        Location loc = locationLookupService.getCurrentLocation();

        if ( loc != null ) {
            // Set the state we're fetching information for
            String state = locationLookupService.getCurrentState();
            TextView stateTextView = (TextView) findViewById( R.id.txtCurrentState );
            if ( Log.isLoggable( LOG_TAG, Log.DEBUG ) ) {
                Log.d( LOG_TAG, String.format( "Fetching reckless information for %s", state ) );
            }
            stateTextView.setText( state );

            if ( state != null ) {
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
            } else {
                displayString = "No State for Location:" + loc.getLatitude() + "," + loc.getLongitude();
            }
        }
    }
}
