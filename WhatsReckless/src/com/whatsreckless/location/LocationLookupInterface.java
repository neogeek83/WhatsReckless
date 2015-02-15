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
package com.whatsreckless.location;

public interface LocationLookupInterface {

	/**
	 * @return Returns the 2 char abbreviation for the current US state from Google's location services.
	 */
	public String getCurrentState();

	/**
	 * Register for US state change notifications
	 * @param listenerToRegister
	 */
	public void registerStateChangeListener(final StateChangeListener listenerToRegister);

	/**
	 * Unregister from US state change notifications
	 * @param listenerToUnregister 
	 */
	public void unregisterStateChangeListener(final StateChangeListener listenerToUnregister);
}
