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
package com.whatsreckless.infoPuller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.whatsreckless.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author gordysc
 * 
 */
public final class InformationAsyncTask extends AsyncTask<Void, Void, Map<String, String>> {
    /**
     * Tag used for logging
     */
    private static final String LOG_TAG = InformationAsyncTask.class.getSimpleName();

    /**
     * The Wikipedia URL we use for collecting the data
     */
    static final String WIKIPEDIA_URL = "http://en.wikipedia.org/wiki/Reckless_driving";

    /**
     * We append this suffix to the end of each location's text when parsing the Wikipedia article
     */
    private static final String SUFFIX = "Penalties";
    /**
     * The document ID for the penalty headers
     */
    private static final String PENALTY_HEADERS_ID = "PenaltyHeaders";

    /**
     * Hard coded strings used for parsing
     */
    private static final String TABLE_HEADER_DELIM = "th";
    private static final String TABLE_COLUMN_DELIM = "td";

    /**
     * The context of this activity
     */
    private final Activity context;

    /**
     * The Wikipedia article we're downloading
     */
    private Document wiki;

    /**
     * A key to use for fetching the names of the columns in the Wikipedia article
     */
    public static final String COLUMN_NAMES = "columns";

    /**
     * The state we want to asynchronously get reckless driving information for
     */
    private final String state;

    /**
     * @param context of this activty
     * @param state we want to asynchronously get reckless driving information for
     */
    public InformationAsyncTask( Activity context, String state ) {
        this.context = context;
        this.state = state;
    }

    /**
     * Helper method to parse an HTML table row and return the data as an array of {@link String} objects
     * 
     * @param e the table row to parse
     * @param delimiter to use for parsing the element
     * 
     * @return an array of {@link String} objects of the column data in the HTML row
     */
    private String[] getDataFromColumnsForRow( Element e, String delimiter ) {
        // Initialize a list
        List<String> result = new ArrayList<String>();

        // Get the columns from the row
        Elements columns = e.select( delimiter );
        if ( !columns.isEmpty() ) {
            for ( Element column : columns ) {
                result.add( column.text() );
            }
        }

        // Return the list as an array
        return result.toArray( new String[result.size()] );
    }

    private Map<String, String> getRecklessInformation( String[] columns ) {
        // Initialize a map that keeps data ordered by insertion
        Map<String, String> information = new LinkedHashMap<String, String>();

        // Append the suffix to the location we want to resolve from the Wikipedia document
        String id = new StringBuilder( state ).append( SUFFIX ).toString();

        // Fetch the appropriate row for the location
        Element row = wiki.getElementById( id );

        // Check to see if we have information to provide, if not, just return the empty map for now
        if ( row == null ) {
            // TODO: DO we want to throw here?
            Log.w( LOG_TAG, context.getString( R.string.no_state_found ) );
            return information;
        }

        // Parse the table row for the column data
        String[] columnData = getDataFromColumnsForRow( row, TABLE_COLUMN_DELIM );

        // Materialize and return the reckless driving data from the Wikipedia document for the specified location
        int count = 0;
        for ( String columName : columns ) {
            information.put( columName, columnData[count] );
            count++;
        }

        // In case we want to debug
        if ( Log.isLoggable( LOG_TAG, Log.DEBUG ) ) {
            Log.d( LOG_TAG, String.format( "Column Names: %s", Arrays.toString( columns ) ) );
            Log.d( LOG_TAG, String.format( "Columns: %s", Arrays.toString( columnData ) ) );
        }
        return information;
    }

    @Override
    protected Map<String, String> doInBackground( Void... params ) {
        try {
            // Get the Wikipedia document
            wiki = Jsoup.connect( WIKIPEDIA_URL ).get();
        } catch ( IOException e ) {
            Log.e( LOG_TAG, e.getMessage() );
            return null;
        }

        // Grab the headers for the penalties table
        Element headerRow = wiki.getElementById( PENALTY_HEADERS_ID );

        // Are the headers defined? Did we find them?
        if ( headerRow == null ) {
            // TODO: Where should we point them here to resolving the issue?
            String msg = context.getString( R.string.invalid_wikipedia_headers );
            // Log the error and return
            Log.e( LOG_TAG, msg );
            return null;
        }

        // Set the defined column names
        String[] columns = getDataFromColumnsForRow( headerRow, TABLE_HEADER_DELIM );

        // Display the reckless driving information
        Map<String, String> result = getRecklessInformation( columns );
        // Add the column names to use for generating the table
        result.put( COLUMN_NAMES, StringUtil.join( Arrays.asList( columns ), "," ) );
        return result;
    }
}
