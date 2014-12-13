package com.whatsreckless;

import java.util.HashMap;
import java.util.Map;

/**
 * A service for returning reckless driving information for the specified state
 * 
 * @author gordysc
 *
 */
final class RecklessService implements RecklessServiceInterface {

	/**
	 * Hard coded key that's always defined for a location
	 */
	public static final String SOURCE = "source";
	
	@Override
	public Map<String, String> getRecklessInfoByLocation(String location) {
		// TODO flush this information out... for now, hard code this
		Map<String, String> data = new HashMap<String, String>();
		data.put( "Max Highway Speed", "70" );
		data.put("Reckless Driving Threshold", "80 mph, 20 mph over a 30-mph-or-higher limit, 60 mph in a 35-mph zone" );
		data.put("Reckless Driving Mandatory Penalty", "none; class 1 misdemeanor" );
		data.put("Reckless Driving Maximum Penalty", "12 months imprisonment, $2500 fine, 6-month license suspension" );
		data.put(SOURCE, "http://en.wikipedia.org/wiki/Code_of_Virginia");
		
		return data;
	}
}
