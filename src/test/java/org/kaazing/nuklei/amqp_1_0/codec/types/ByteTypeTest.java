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

import static org.junit.Assert.assertEquals;
import static org.kaazing.nuklei.Flyweight.uint8Get;
import static org.kaazing.nuklei.amqp_1_0.codec.types.ByteType.SIZEOF_BYTE;

import java.util.Random;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.kaazing.nuklei.concurrent.AtomicBuffer;

@RunWith(Theories.class)
public class ByteTypeTest {

    public static final int BUFFER_CAPACITY = 64;
    
    @DataPoint
    public static final int ZERO_OFFSET = 0;
    
    @DataPoint
    public static final int NON_ZERO_OFFSET = new Random().nextInt(BUFFER_CAPACITY - SIZEOF_BYTE - 1) + 1;

    private final AtomicBuffer buffer = new AtomicBuffer(new byte[BUFFER_CAPACITY]);
    
    @Theory
    public void shouldEncode(int offset) {
        ByteType byteType = new ByteType();
        byteType.wrap(buffer, offset);
        byteType.set((byte) 0x12);
        
        assertEquals(0x51, uint8Get(buffer, offset));
        assertEquals(0x12, uint8Get(buffer, offset + 1));
    }
    
    @Theory
    public void shouldDecode(int offset) {
        buffer.putByte(offset, (byte) 0x51);
        buffer.putByte(offset + 1, (byte) 0x12);
        
        ByteType byteType = new ByteType();
        byteType.wrap(buffer, offset);
        
        assertEquals(0x12, byteType.get());
        assertEquals(offset + 2, byteType.limit());
    }
    
    @Theory
    public void shouldEncodeThenDecode(int offset) {
        ByteType byteType = new ByteType();
        byteType.wrap(buffer, offset);
        byteType.set((byte) 0x12);
        
        assertEquals(0x12, byteType.get());
        assertEquals(offset + 2, byteType.limit());
    }
    
    @Theory
    @Test(expected = Exception.class)
    public void shouldNotDecode(int offset) {
        buffer.putByte(offset, (byte) 0x00);
        
        ByteType byteType = new ByteType();
        byteType.wrap(buffer, offset);

        assertEquals(0, byteType.get());
    }
    
}