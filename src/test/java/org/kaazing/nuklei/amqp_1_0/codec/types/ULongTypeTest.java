/*
 * Copyright 2014 Kaazing Corporation, All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.nuklei.amqp_1_0.codec.types;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static org.junit.Assert.assertEquals;
import static org.kaazing.nuklei.Flyweight.uint8Get;
import static org.kaazing.nuklei.amqp_1_0.codec.types.ULongType.SIZEOF_ULONG_MAX;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Random;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.kaazing.nuklei.Flyweight;

import uk.co.real_logic.agrona.MutableDirectBuffer;
import uk.co.real_logic.agrona.concurrent.UnsafeBuffer;

@RunWith(Theories.class)
public class ULongTypeTest {

    private static final int BUFFER_CAPACITY = 64;
    
    @DataPoint
    public static final int ZERO_OFFSET = 0;
    
    @DataPoint
    public static final int NON_ZERO_OFFSET = new Random().nextInt(BUFFER_CAPACITY - SIZEOF_ULONG_MAX - 1) + 1;

    private final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[BUFFER_CAPACITY]);

    @Theory
    public void shouldEncode0(int offset) {
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        ulongType.set(0L);
        
        assertEquals(0x44, uint8Get(buffer, offset));
        assertEquals(offset + 1, ulongType.limit());
    }
    
    @Theory
    public void shouldEncode1(int offset) {
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        ulongType.set(1L);
        
        assertEquals(0x53, uint8Get(buffer, offset));
        assertEquals(offset + 2, ulongType.limit());
    }
    
    @Theory
    public void shouldEncode8(int offset) {
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        ulongType.set(12345678L);
        
        assertEquals(0x80, uint8Get(buffer, offset));
        assertEquals(offset + 9, ulongType.limit());
    }
    
    @Theory
    public void shouldDecode0(int offset) {
        buffer.putByte(offset, (byte) 0x44);
        
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        
        assertEquals(0x00L, ulongType.get());
        assertEquals(offset + 1, ulongType.limit());
    }
    
    @Theory
    public void shouldDecode1(int offset) {
        buffer.putByte(offset, (byte) 0x53);
        buffer.putByte(offset + 1, (byte) 0x01);
        
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        
        assertEquals(0x01L, ulongType.get());
        assertEquals(offset + 2, ulongType.limit());
    }
    
    @Theory
    public void shouldDecode8(int offset) {
        buffer.putByte(offset, (byte) 0x80);
        buffer.putLong(offset + 1, 0x12345678L, BIG_ENDIAN);
        
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        
        assertEquals(0x12345678L, ulongType.get());
        assertEquals(offset + 9, ulongType.limit());
    }
    
    @Theory
    public void shouldEncodeThenDecode0(int offset) {
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        ulongType.set(0L);
        
        assertEquals(0L, ulongType.get());
    }
    
    @Theory
    public void shouldEncodeThenDecode1(int offset) {
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        ulongType.set(1L);
        
        assertEquals(1L, ulongType.get());
    }
    
    @Theory
    public void shouldEncodeThenDecode8(int offset) {
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        ulongType.set(12345678L);
        
        assertEquals(12345678L, ulongType.get());
    }
    
    @Theory
    @Test(expected = Exception.class)
    public void shouldNotDecode(int offset) {
        buffer.putByte(offset, (byte) 0x00);
        
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);

        assertEquals(0L, ulongType.get());
    }

    @Theory
    @SuppressWarnings("unchecked")
    public void shouldNotifyChanged(int offset) {
        final Consumer<Flyweight> observer = mock(Consumer.class);
        
        ULongType ulongType = new ULongType();
        ulongType.watch(observer);
        ulongType.wrap(buffer, offset);
        ulongType.set(12345678L);
        
        verify(observer).accept(ulongType);
    }
}
