/**
 * Copyright 2014 Luke Gordon and Kenny Neal
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
package com.whatsreckless.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

import com.whatsreckless.R;

/**
 * A service for returning reckless driving information for a specified location.
 * <p>
 * Note: We fetch the Wikipedia document on the first call and cache the data. This saves bandwidth from repeated requests.
 * 
 * @author gordysc
 * 
 */
final class RecklessServiceImpl implements RecklessService {
    /**
     * Tag used for logging
     */
    private static final String LOG_TAG = RecklessServiceImpl.class.getSimpleName();

    /**
     * The Wikipedia URL we use for collecting the data
     */
    private static final String WIKIPEDIA_URL = "https://en.wikipedia.org/wiki/Reckless_driving";

    /**
     * We append this suffix to the end of each location's text when parsing the Wikipedia article
     */
    private static final String SUFFIX = "Penalties";

    /**
     * Context for the application
     */
    private Context context;

    /**
     * An {@link AtomicBoolean} to detect if we've initialized the singleton
     */
    private final AtomicBoolean initialized;

    /**
     * A cached version of the Wikipedia document to eliminate bandwidth consumption
     */
    private Document wiki;

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
     * The headers of the table for penalties
     */
    private String[] headers;

    /**
     * Bill Pugh initialization on demand singleton pattern for lazy-loaded initialization
     * 
     * @see http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
     * 
     */
    private static final class Singleton {
        public static final RecklessServiceImpl INSTANCE = new RecklessServiceImpl();
    }

    /**
     * Never initialize this class externally
     */
    private RecklessServiceImpl() {
        // Initialize the atomic reference to see if we have to warm the cache
        initialized = new AtomicBoolean( false );
    }

    /**
     * @return this class as a singleton
     */
    public static RecklessServiceImpl getInstance() {
        return Singleton.INSTANCE;
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

    /**
     * Helper method to check if we've fetched the Wikipedia document for reckless driving
     * 
     * @throws IOException If there's an issue making a connection/fetching the HTML from Wikipedia
     */
    void initialize( Context context ) throws IOException {
        // Check if we need to fetch the wikipedia document
        synchronized ( initialized ) {
            // Do we need to do anything?
            if ( !initialized.compareAndSet( false, true ) ) {
                return;
            }

            // Set the context
            this.context = context;

            // Get the Wikipedia document
            wiki = Jsoup.connect( WIKIPEDIA_URL ).get();

            // Grab the headers for the penalties table
            Element headerRow = wiki.getElementById( PENALTY_HEADERS_ID );

            // Are the headers defined? Did we find them?
            if ( headerRow == null ) {
                // TODO: Where should we point them here to resolving the issue?
                String msg = context.getString( R.string.invalid_wikipedia_headers );
                // Log the error and throw
                Log.e( LOG_TAG, msg );
                throw new IOException( msg );
            }

            // Save the defined headers
            headers = getDataFromColumnsForRow( headerRow, TABLE_HEADER_DELIM );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see RecklessServiceInterface#getRecklessInfoByLocation(java.lang.String)
     */
    @Override
    public Map<String, String> getRecklessInfoByLocation( String location ) {
        // Initialize a map that keeps data ordered by insertion
        Map<String, String> result = new LinkedHashMap<String, String>();

        // Append the suffix to the location we want to resolve from the Wikipedia document
        String id = new StringBuilder( location ).append( SUFFIX ).toString();

        // Fetch the appropriate row for the location
        Element row = wiki.getElementById( id );

        // Check to see if we have information to provide, if not, just return the empty map for now
        if ( row == null ) {
            // TODO: DO we want to throw here?
            Log.w( LOG_TAG, context.getString( R.string.no_state_found ) );
            return result;
        }

        // Parse the table row for the column data
        String[] columnData = getDataFromColumnsForRow( row, TABLE_COLUMN_DELIM );

        // Materialize and return the reckless driving data from the Wikipedia document for the specified location
        int count = 0;
        for ( String header : headers ) {
            result.put( header, columnData[count] );
            count++;
        }

        // In case we want to debug
        if ( Log.isLoggable( LOG_TAG, Log.DEBUG ) ) {
            Log.d( LOG_TAG, String.format( "Headers: %s", Arrays.toString( headers ) ) );
            Log.d( LOG_TAG, String.format( "Columns: %s", Arrays.toString( columnData ) ) );
        }

        return result;
    }
}
