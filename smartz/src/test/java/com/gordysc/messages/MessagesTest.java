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
package com.gordysc.messages;

import java.util.Locale;
import java.util.MissingResourceException;

import org.junit.Assert;
import org.junit.Test;

import com.gordysc.common.messages.EnhancedMessageKey;
import com.gordysc.common.messages.Message;
import com.gordysc.common.messages.MessageBundle;
import com.gordysc.common.messages.MessageFactory;
import com.gordysc.common.messages.MessageFactoryGenerator;
import com.gordysc.common.messages.MessageKey;
import com.gordysc.common.messages.Suffix;

public class MessagesTest {

    // ------------------------------------------------------------------------
    // Happy path tests
    // ------------------------------------------------------------------------

    private static enum TestKeys implements MessageKey {
        TEST_KEY1,
        TEST_NOT_REQUIRED_1,
        TEST_NOT_REQUIRED_2,
        TEST_NOT_REQUIRED_3,
        TEST_NOT_REQUIRED_4;

        @Override
        public String getKey() {
            return this.toString();
        }
    }

    private static enum EnhancedTestKeys implements EnhancedMessageKey {
        ENHANCED_TEST_KEY1( "9000" ),
        ENHANCED_TEST_KEY2( "are", "strings", "this" ),
        THIS_KEY_DOES_NOT_EXIST( "fishsticks" );

        private final Object[] context;

        private EnhancedTestKeys( Object... context ) {
            this.context = context;
        }

        @Override
        public String getKey() {
            return this.toString();
        }

        @Override
        public Object[] getContext() {
            return context;
        }
    }

    @MessageBundle( "TestMessages" )
    private static interface TestMessage1 extends Message {
        String getCode();

        String getDescription();
    }

    @Test
    public void testMessageKey() {
        MessageFactory<TestMessage1> factory = MessageFactoryGenerator.generate( TestMessage1.class );
        TestMessage1 msg = factory.get( TestKeys.TEST_KEY1 );
        Assert.assertEquals( "Invalid code returned!", "SMRTST00000I", msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "This is a test!", msg.getDescription() );
    }

    @MessageBundle( "TestMessages" )
    private static interface TestMessage2 extends Message {
        @Suffix( "state" )
        String getCode();

        @Suffix( "city" )
        String getDescription();
    }

    @Test
    public void testMessageKeyWithAnnotations() {
        MessageFactory<TestMessage2> factory = MessageFactoryGenerator.generate( TestMessage2.class );
        TestMessage2 msg = factory.get( TestKeys.TEST_KEY1 );
        Assert.assertEquals( "Invalid code returned!", "Texas", msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "Dallas", msg.getDescription() );
    }

    @Test
    public void testEnhancedMessageKey() {
        MessageFactory<TestMessage1> factory = MessageFactoryGenerator.generate( TestMessage1.class );
        TestMessage1 msg = factory.get( EnhancedTestKeys.ENHANCED_TEST_KEY1 );
        Assert.assertEquals( "Invalid code returned!", "SMRTST00001I", msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "Over 9000!", msg.getDescription() );
    }

    @Test
    public void testEnhancedMessageKey2() {
        MessageFactory<TestMessage1> factory = MessageFactoryGenerator.generate( TestMessage1.class );
        TestMessage1 msg = factory.get( EnhancedTestKeys.ENHANCED_TEST_KEY2 );
        Assert.assertEquals( "Invalid code returned!", "SMRTST00002I", msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "There are multiple strings in this text.", msg.getDescription() );
    }

    @Test
    public void testEnhancedMessageKeyWithAnnotations() {
        MessageFactory<TestMessage2> factory = MessageFactoryGenerator.generate( TestMessage2.class );
        TestMessage2 msg = factory.get( EnhancedTestKeys.ENHANCED_TEST_KEY1 );
        Assert.assertEquals( "Invalid code returned!", "South Carolina", msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "9000", msg.getDescription() );
    }

    @MessageBundle( value = "TestMessages", separator = "-" )
    private static interface TestMessage3 extends Message {
        String getCode();

        String getDescription();
    }

    @Test
    public void testSeparator() {
        MessageFactory<TestMessage3> factory = MessageFactoryGenerator.generate( TestMessage3.class );
        TestMessage3 msg = factory.get( TestKeys.TEST_KEY1 );
        Assert.assertEquals( "Invalid code returned!", "SMRTST00003I", msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "Successfully used a different separator!", msg.getDescription() );
    }

    @MessageBundle( value = "TestMessages" )
    private static interface TestMessage4 extends Message {
        String getCode();

        @Suffix( required = false )
        String getDescription();
    }

    @Test
    public void testNotRequired1() {
        MessageFactory<TestMessage4> factory = MessageFactoryGenerator.generate( TestMessage4.class );
        TestMessage4 msg = factory.get( TestKeys.TEST_NOT_REQUIRED_1 );
        Assert.assertEquals( "Invalid code returned!", "SMRTST00004I", msg.getCode() );
        Assert.assertNull( msg.getDescription() );
    }

    @MessageBundle( value = "TestMessages" )
    private static interface TestMessage5 extends Message {
        @Suffix( required = false )
        String getCode();

        @Suffix( "suffix" )
        String getDescription();
    }

    @Test
    public void testNotRequired2() {
        MessageFactory<TestMessage5> factory = MessageFactoryGenerator.generate( TestMessage5.class );
        TestMessage5 msg = factory.get( TestKeys.TEST_NOT_REQUIRED_2 );
        Assert.assertNull( msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "Success!", msg.getDescription() );
    }

    @MessageBundle( value = "TestMessages" )
    private static interface TestMessage6 extends Message {
        String getCode();

        @Suffix( value = "suffix", required = false )
        String getDescription();
    }

    @Test
    public void testNotRequired3() {
        MessageFactory<TestMessage6> factory = MessageFactoryGenerator.generate( TestMessage6.class );
        TestMessage6 msg = factory.get( TestKeys.TEST_NOT_REQUIRED_3 );
        Assert.assertEquals( "Invalid code returned!", "SMRTST00005I", msg.getCode() );
        Assert.assertNull( msg.getDescription() );
    }

    @Test
    public void testNotRequired4() {
        MessageFactory<TestMessage6> factory = MessageFactoryGenerator.generate( TestMessage6.class );
        TestMessage6 msg = factory.get( TestKeys.TEST_NOT_REQUIRED_4 );
        Assert.assertEquals( "Invalid code returned!", "SMRTST00006I", msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "Wahoo!", msg.getDescription() );
    }

    private static enum ChineseTestKeys implements MessageKey {
        TEST_KEY1;

        @Override
        public String getKey() {
            return this.toString();
        }
    }

    private static enum ChineseEnhancedTestKeys implements EnhancedMessageKey {
        ENHANCED_TEST_KEY1( "America" ),
        ENHANCED_TEST_KEY2( "7", "8" );

        private final Object[] context;

        private ChineseEnhancedTestKeys( Object... context ) {
            this.context = context;
        }

        @Override
        public String getKey() {
            return this.toString();
        }

        @Override
        public Object[] getContext() {
            return context;
        }
    }

    @Test
    public void testMessageKeyChinese() {
        MessageFactory<TestMessage1> factory = MessageFactoryGenerator.generate( TestMessage1.class );
        TestMessage1 msg = factory.get( ChineseTestKeys.TEST_KEY1, Locale.CHINESE );
        Assert.assertEquals( "Invalid code returned!", "SMRCHN00000I", msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "We successfully used the Chinese locale!", msg.getDescription() );
    }

    @Test
    public void testEnhancedMessageKeyChinese() {
        MessageFactory<TestMessage1> factory = MessageFactoryGenerator.generate( TestMessage1.class );
        TestMessage1 msg = factory.get( ChineseEnhancedTestKeys.ENHANCED_TEST_KEY1, Locale.CHINESE );
        Assert.assertEquals( "Invalid code returned!", "SMRCHN00001I", msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "Hello America!", msg.getDescription() );
    }

    @Test
    public void testEnhancedMessageKey2Chinese() {
        MessageFactory<TestMessage1> factory = MessageFactoryGenerator.generate( TestMessage1.class );
        TestMessage1 msg = factory.get( ChineseEnhancedTestKeys.ENHANCED_TEST_KEY2, Locale.CHINESE );
        Assert.assertEquals( "Invalid code returned!", "SMRCHN00002I", msg.getCode() );
        Assert.assertEquals( "Invalid description returned!", "Fall down 7 times, get up 8.", msg.getDescription() );
    }

    // ------------------------------------------------------------------------
    // Negative tests
    // ------------------------------------------------------------------------

    private static interface InvalidInterface1 extends Message {

    }

    @Test( expected = IllegalArgumentException.class )
    public void testInvalidInterface1() {
        MessageFactoryGenerator.generate( InvalidInterface1.class );
    }

    @MessageBundle( "TestMessages" )
    private static interface InvalidInterface2 extends Message {

    }

    @Test( expected = IllegalArgumentException.class )
    public void testInvalidInterface2() {
        MessageFactoryGenerator.generate( InvalidInterface2.class );
    }

    @Test( expected = MissingResourceException.class )
    public void testMissingRequiredSuffix() {
        MessageFactory<TestMessage2> factory = MessageFactoryGenerator.generate( TestMessage2.class );
        factory.get( EnhancedTestKeys.THIS_KEY_DOES_NOT_EXIST );
    }

    @Test( expected = MissingResourceException.class )
    public void testMissingRequiredSuffixWithAnnotation() {
        MessageFactory<TestMessage1> factory = MessageFactoryGenerator.generate( TestMessage1.class );
        factory.get( EnhancedTestKeys.THIS_KEY_DOES_NOT_EXIST );
    }

    @MessageBundle( "TestMessages" )
    private static final class DummyMessage implements Message {

    }

    @Test( expected = IllegalArgumentException.class )
    public void testIllegalClass() {
        MessageFactoryGenerator.generate( DummyMessage.class );
    }

    @MessageBundle( "TestMessages" )
    private static interface IllegalMessage extends Message {
        void getIllegalReturnType();
    }

    @Test( expected = IllegalArgumentException.class )
    public void testIllegalMessage() {
        MessageFactoryGenerator.generate( IllegalMessage.class );
    }

    @MessageBundle( "TestMessages" )
    private static interface IllegalMessage2 extends Message {
        String get();
    }

    @Test( expected = IllegalArgumentException.class )
    public void testIllegalMessage2() {
        MessageFactoryGenerator.generate( IllegalMessage2.class );
    }

    @MessageBundle( "TestMessages" )
    private static interface IllegalMessage3 extends Message {
        String fishsticks();
    }

    @Test( expected = IllegalArgumentException.class )
    public void testIllegalMessage3() {
        MessageFactoryGenerator.generate( IllegalMessage3.class );
    }
}