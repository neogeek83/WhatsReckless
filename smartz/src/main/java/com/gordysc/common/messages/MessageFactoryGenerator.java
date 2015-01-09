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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.reflect.AbstractInvocationHandler;
import com.gordysc.common.reflect.Methods;
import com.gordysc.common.reflect.Methods.AccessorType;
import com.gordysc.common.reflect.ReflectionValidator;

/**
 * Generates {@link MessageFactory} objects
 * 
 * @author gordysc
 *
 */
public final class MessageFactoryGenerator {
    /**
     * An {@link InvocationHandler} for mapping method invocations to generated text
     */
    private static final class MessageInvocationHandler extends AbstractInvocationHandler {

        /**
         * A {@link Map} of a {@link Method} to a suffix associated with it
         */
        private final Map<Method, String> map;

        /**
         * A {@link Map} of a suffix associated with a {@link Method} to formatted text generated from a {@link ResourceBundle}
         */
        private final Map<String, String> data;

        /**
         * @param map
         */
        MessageInvocationHandler( BiMap<Method, String> map, Map<String, String> data ) {
            this.map = map;
            this.data = data;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.google.common.reflect.AbstractInvocationHandler#handleInvocation(java.lang.Object, java.lang.reflect.Method,
         * java.lang.Object[])
         */
        @Override
        protected String handleInvocation( Object proxy, Method method, Object[] args ) throws Throwable {
            // Get the suffix associated with this method
            String suffix = map.get( method );
            // Return the formatted text associated with this suffix
            return data.get( suffix );
        }
    }

    /**
     * A factory for generating an instance of a message from a {@link MessageKey}
     */
    private static final class MessageFactoryImpl<T extends Message> implements MessageFactory<T> {

        /**
         * The class being proxied
         */
        private final Class<?> proxy;

        /**
         * A bi-directional map for methods and suffixes
         */
        private final BiMap<Method, String> map;

        /**
         * Name of the {@link ResourceBundle}
         */
        private final String bundleName;

        /**
         * Used for separating a message key from it's suffix
         */
        private final String separator;

        /**
         * @param klass we'll create a proxy for
         * @param msgBundle to use for generating messages
         * @param suffixes to append to a {@link MessageKey} when fetching text from the specified {@link ResourceBundle}
         */
        MessageFactoryImpl( Class<?> klass, BiMap<Method, String> map, MessageBundle msgBundle ) {
            // Set the class we're creating a proxy for
            this.proxy = klass;
            // Set the method-to-suffix map
            this.map = map;
            // Get the resource bundle name to use
            bundleName = msgBundle.value();
            // Get the separator to use for separating a message key from it's suffix
            separator = msgBundle.separator();
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.gordysc.message.MessageFactory#get(com.gordysc.message.MessageKey)
         */
        @Override
        public T get( MessageKey msgKey ) {
            return this.get( msgKey, Locale.getDefault() );
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.gordysc.message.MessageFactory#get(com.gordysc.message.MessageKey, java.util.Locale)
         */
        @Override
        @SuppressWarnings( "unchecked" )
        public T get( MessageKey msgKey, Locale locale ) {
            // Fetch the resource bundle for the specified locale
            ResourceBundle bundle = ResourceBundle.getBundle( bundleName, locale );
            // Get the list of suffixes to use for this message
            Set<String> suffixes = map.inverse().keySet();
            // Initialize a map for suffix-to-text
            Map<String, String> data = new HashMap<String, String>();
            // Are we dealing with an "enhanced" message key?
            Object[] context = ( msgKey instanceof EnhancedMessageKey ) ? ( (EnhancedMessageKey) msgKey ).getContext() : null;
            // Iterate through the list of suffixes and generate their respective text
            for ( String suffix : suffixes ) {
                // Create the key to use
                String key = String.format( "%s%s%s", msgKey.getKey(), separator, suffix );
                try {
                    // Fetch the text (unformatted) from the bundle
                    String unformatted = bundle.getString( key );
                    // Format the text and add it to the map
                    data.put( suffix, MessageFormat.format( unformatted, context ) );
                } catch ( MissingResourceException e ) {
                    // If we couldn't find the text, check to see if the suffix is required before throwing
                    Method method = map.inverse().get( suffix );
                    if ( method.isAnnotationPresent( Suffix.class ) ) {
                        Suffix annotation = method.getAnnotation( Suffix.class );
                        if ( annotation.required() ) {
                            throw e;
                        }
                    } else {
                        throw e;
                    }
                }
            }
            // Generate a proxy for this message
            return (T) Proxy.newProxyInstance( proxy.getClassLoader(), new Class[] { proxy }, new MessageInvocationHandler( map, data ) );
        }
    }

    /**
     * @param methods to map to a suffix
     * @return a bi-directional {@link Map} for mapping a {@link Method} name to a suffix, and vice versa
     */
    private static BiMap<Method, String> getSuffixMap( Method[] methods ) {
        BiMap<Method, String> map = HashBiMap.create();
        for ( Method method : methods ) {
            String suffix = null;
            // If no annotation is present, parse the method name itself
            if ( !method.isAnnotationPresent( Suffix.class ) ) {
                suffix = Methods.getAccessorName( method, AccessorType.GET );
            } else {
                // Get the name to use for the suffix for this method
                suffix = method.getAnnotation( Suffix.class ).value();
                if ( Strings.isNullOrEmpty( suffix ) ) {
                    suffix = Methods.getAccessorName( method, AccessorType.GET );
                }
            }
            map.put( method, suffix );
        }
        // Make sure at least 1 accessor method was defined for this message
        if ( map.isEmpty() ) {
            throw new IllegalArgumentException( "No accessor methods were defined!" );
        }
        return Maps.unmodifiableBiMap( map );
    }

    /**
     * @param klass extension of a {@link Message} to create a {@link MessageFactory} for
     * @return a {@link MessageFactory} for generating a {@link Message} from a specific {@link MessageKey}
     */
    public static <T extends Message> MessageFactory<T> generate( Class<T> klass ) {
        // Check the MessageBundle annotation is present
        ReflectionValidator.hasRequiredAnnotation( klass, MessageBundle.class );
        // Get the MessageBundle annotation
        MessageBundle msgBundle = klass.getAnnotation( MessageBundle.class );
        // Check the class is an interface
        ReflectionValidator.isInterface( klass );
        // Get all the methods for the message
        Method[] methods = klass.getMethods();
        // Map each method to a suffix, and vice versa
        BiMap<Method, String> map = getSuffixMap( methods );
        // Create the factory
        return new MessageFactoryImpl<T>( klass, map, msgBundle );
    }
}
