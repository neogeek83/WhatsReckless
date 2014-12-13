package com.whatsreckless;

import java.util.Map;

/**
 * A service for returning reckless driving information for the specified state
 * @author gordysc
 *
 */
public interface RecklessServiceInterface {
	/**
	 * @param location to fetch reckless driving information for
	 * @return a key-value store of reckless driving information
	 */
	Map<String, String> getRecklessInfoByLocation( String location );
}
