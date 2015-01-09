/**
 * Copyright 2015 Luke Gordon
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
package com.gordysc.common.messages;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A factory for generating a {@link Message} from a {@link MessageKey}
 * 
 * @author gordysc
 *
 * @param <T> - interface type to generate
 */
public interface MessageFactory<T extends Message> {
    /**
     * @param msgKey to use for fetching text from a {@link ResourceBundle}
     * @return a materialized instance of <T>
     */
    T get( MessageKey msgKey );

    /**
     * @param msgKey to use for fetching text from a {@link ResourceBundle}
     * @param locale for which a {@link ResourceBundle} is desired
     * @return a materialized instance of <T>
     */
    T get( MessageKey msgKey, Locale locale );
}
