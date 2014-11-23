package com.whatsreckless;

public interface LocationLookup {

	/**
	 * @return Returns the 2 char abbreviation for the current US state from Google's location services.
	 */
	public String getCurrentState();
}
