package com.whatsreckless;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationLookup implements LocationLookupInterface {

    private LocationListener locListener = null;
    private Location currentLocation = null;

    private LocationManager manager = null;
    private Context context = null;

    public LocationLookup( Activity context ) {
        this.context = context;

        manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        locListener = new LocationListener() {
            @Override
            public void onLocationChanged( Location location ) {
                currentLocation = location;
            }

            @Override
            public void onStatusChanged( String provider, int status, Bundle extras ) {
            }

            @Override
            public void onProviderEnabled( String provider ) {
            }

            @Override
            public void onProviderDisabled( String provider ) {
            }
        };
        enableLocationUpdates();
    }

    @Override
    public String getCurrentState() {
        if ( currentLocation != null ) {
            Address addr = getAddress( currentLocation );
            if ( addr != null ) {
                if ( addr.getAdminArea() == null ) {
                    Log.e( this.getClass().getSimpleName(), context.getString( R.string.no_state_found ) );
                }
                return addr.getAdminArea();
            } else {
                Log.e( this.getClass().getSimpleName(), context.getString( R.string.no_address_found ) );
                return null;
            }
        } else {

            Log.e( this.getClass().getSimpleName(), context.getString( R.string.location_unassigned ) );
            return null;
        }
    }

    @SuppressWarnings( "unused" )
    private String getFormattedAddress( final Location location ) {
        /*
         * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform to address standards can also be used.
         */
        Geocoder geocoder = new Geocoder( context, Locale.getDefault() );

        // Create a list to contain the result address
        List<Address> addresses = null;

        // Try to get an address for the current location. Catch IO or network problems.
        try {

            /*
             * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
             */
            addresses = geocoder.getFromLocation( currentLocation.getLatitude(), location.getLongitude(), 1 );

            // Catch network or other I/O problems.
        } catch ( IOException exception1 ) {

            // Log an error and return an error message
            Log.e( this.getClass().getSimpleName(), context.getString( R.string.IO_Exception_getFromLocation ) );

            // print the stack trace
            exception1.printStackTrace();

            // Return an error message
            return ( context.getString( R.string.IO_Exception_getFromLocation ) );

            // Catch incorrect latitude or longitude values
        } catch ( IllegalArgumentException exception2 ) {

            // Construct a message containing the invalid arguments
            String errorString = context.getString( R.string.illegal_argument_exception, location.getLatitude(), location.getLongitude() );
            // Log the error and print the stack trace
            Log.e( this.getClass().getSimpleName(), errorString );
            exception2.printStackTrace();

            //
            return errorString;
        }
        // If the reverse geocode returned an address
        if ( addresses != null && addresses.size() > 0 ) {

            // Get the first address
            Address address = addresses.get( 0 );

            // Format the first line of address
            String addressText = context.getString( R.string.address_output_string,

            // If there's a street address, add it
                            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine( 0 ) : "",

                            // Locality is usually a city
                            address.getLocality(),

                            // The country of the address
                            address.getCountryName() );

            // Return the text
            return addressText;

            // If there aren't any addresses, post a message
        } else {
            return context.getString( R.string.no_address_found );
        }
    }

    private Address getAddress( final Location location ) {
        /*
         * Get a new geocoding service instance, set for localized addresses. This example uses android.location.Geocoder, but other geocoders that conform to address standards can also be used.
         */
        Geocoder geocoder = new Geocoder( context, Locale.getDefault() );

        // Create a list to contain the result address
        List<Address> addresses = null;

        // Try to get an address for the current location. Catch IO or network problems.
        try {

            /*
             * Call the synchronous getFromLocation() method with the latitude and longitude of the current location. Return at most 1 address.
             */
            addresses = geocoder.getFromLocation( currentLocation.getLatitude(), location.getLongitude(), 1 );

            // Catch network or other I/O problems.
        } catch ( IOException exception1 ) {

            // Log an error and return an error message
            Log.e( this.getClass().getSimpleName(), context.getString( R.string.IO_Exception_getFromLocation ) );

            // print the stack trace
            exception1.printStackTrace();

            // Return an error message
            return null;

            // Catch incorrect latitude or longitude values
        } catch ( IllegalArgumentException exception2 ) {

            // Construct a message containing the invalid arguments
            String errorString = context.getString( R.string.illegal_argument_exception, location.getLatitude(), location.getLongitude() );
            // Log the error and print the stack trace
            Log.e( this.getClass().getSimpleName(), errorString );
            exception2.printStackTrace();

            //
            return null;
        }
        // If the reverse geocode returned an address
        if ( addresses != null && addresses.size() > 0 ) {

            // Get the first address
            Address address = addresses.get( 0 );

            // Return the text
            return address;

            // If there aren't any addresses, post a message
        } else {

            Log.e( this.getClass().getSimpleName(), context.getString( R.string.no_address_found ) );
            return null;
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void disableLocationUpdates() {
        manager.removeUpdates( locListener );
    }

    /**
     * Not needed for first start, but if disabled, allows for restarting.
     */
    public void enableLocationUpdates() {
        manager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 0, locListener );
    }
}