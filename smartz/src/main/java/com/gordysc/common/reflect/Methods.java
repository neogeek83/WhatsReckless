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
package com.gordysc.common.reflect;

import java.beans.Introspector;
import java.lang.reflect.Method;

import com.google.common.base.Strings;

/**
 * A useful collection of methods for performing reflection operations on methods
 * 
 * @author gordysc
 *
 */
public final class Methods {

    /**
     * An "exhaustive" list of well known accessor {@link Method} prefixes & the return types they're allowed to have
     */
    public static enum AccessorType {
        GET( String.class ),
        IS( Boolean.TYPE ),
        SET( Void.TYPE );

        private final Class<?> allowed;

        private AccessorType( Class<?> allowed ) {
            this.allowed = allowed;
        }

        /**
         * @return the Class<?> this method is allowed to return
         */
        public Class<?> getAllowedReturnType() {
            return allowed;
        }
    }

    /**
     * @param method to get a name to associate with
     * @param offset to start with for getting the name from the {@link Method}
     * @return a name to associate with this {@link Method}
     */
    private static String getAccessorName( String method, int offset ) {
        String value = method.substring( offset );
        if ( Strings.isNullOrEmpty( value ) ) {
            throw new IllegalArgumentException( String.format( "%s is an illegal method name!", method ) );
        }
        return Introspector.decapitalize( value );
    }

    /**
     * @param method to get a name to associate with
     * @param types to use for getting an accessor name
     * @return a name to associate with this {@link Method}
     */
    public static String getAccessorName( Method method, AccessorType... types ) {
        String methodName = method.getName();
        for ( AccessorType type : types ) {
            if ( methodName.startsWith( type.toString().toLowerCase() ) ) {
                Class<?> returnType = method.getReturnType();
                if ( !returnType.equals( type.getAllowedReturnType() ) ) {
                    throw new IllegalArgumentException( String.format( "%s is an illegal return type for %s!", returnType.getName(), method.getName() ) );
                }
                return getAccessorName( methodName, type.toString().length() );
            }
        }
        throw new IllegalArgumentException( String.format( "%s is an illegal method name!", method ) );
    }
}
