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

import static java.util.UUID.fromString;
import static org.junit.Assert.assertEquals;
import static org.kaazing.nuklei.BitUtil.fromHex;
import static org.kaazing.nuklei.BitUtil.toHex;
import static org.kaazing.nuklei.Flyweight.uint8Get;
import static org.kaazing.nuklei.amqp_1_0.codec.types.UuidType.SIZEOF_UUID;

import java.util.Random;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.kaazing.nuklei.concurrent.AtomicBuffer;

@RunWith(Theories.class)
public class UuidTypeTest {

    public static final int BUFFER_CAPACITY = 64;
    
    @DataPoint
    public static final int ZERO_OFFSET = 0;
    
    @DataPoint
    public static final int NON_ZERO_OFFSET = new Random().nextInt(BUFFER_CAPACITY - SIZEOF_UUID - 1) + 1;

    private final AtomicBuffer buffer = new AtomicBuffer(new byte[BUFFER_CAPACITY]);
    
    @Theory
    public void shouldEncode(int offset) {
        UuidType uuidType = new UuidType();
        uuidType.wrap(buffer, offset);
        uuidType.set(fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"));
        
        assertEquals(0x98, uint8Get(buffer, offset));
        assertEquals("f81d4fae7dec11d0a76500a0c91e6bf6", toHex(buffer.array(), offset + 1, 16));
    }
    
    @Theory
    public void shouldDecode(int offset) {
        buffer.putByte(offset, (byte) 0x98);
        buffer.putBytes(offset + 1, fromHex("f81d4fae7dec11d0a76500a0c91e6bf6"));
        
        UuidType uuidType = new UuidType();
        uuidType.wrap(buffer, offset);
        
        assertEquals(fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"), uuidType.get());
        assertEquals(offset + 17, uuidType.limit());
    }
    
    @Theory
    public void shouldEncodeThenDecode(int offset) {
        UuidType uuidType = new UuidType();
        uuidType.wrap(buffer, offset);
        uuidType.set(fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"));
        
        assertEquals(fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"), uuidType.get());
        assertEquals(offset + 17, uuidType.limit());
    }
    
    @Theory
    @Test(expected = Exception.class)
    public void shouldNotDecode(int offset) {
        buffer.putByte(offset, (byte) 0x00);
        
        UuidType uuidType = new UuidType();
        uuidType.wrap(buffer, offset);

        assertEquals(fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"), uuidType.get());
    }
    
}