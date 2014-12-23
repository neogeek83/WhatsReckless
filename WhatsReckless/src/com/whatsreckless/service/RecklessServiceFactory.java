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

import android.content.Context;

/**
 * A factory for generating a {@link RecklessService}
 * 
 * @author gordysc
 * 
 */
public final class RecklessServiceFactory {

    /**
     * @return a service for returning reckless driving information for a specified location
     * 
     * @throws IOException If there's an issue making a connection/fetching the HTML from Wikipedia
     */
    public static RecklessService getRecklessService( Context context ) throws IOException {
        // Fetch the service
        RecklessServiceImpl service = RecklessServiceImpl.getInstance();

        // Initialize it with the application's context
        service.initialize( context );

        return service;
    }
}
