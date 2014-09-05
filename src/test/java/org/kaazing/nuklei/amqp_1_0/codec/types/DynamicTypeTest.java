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

import static java.math.MathContext.DECIMAL128;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.UUID.fromString;
import static org.junit.Assert.assertEquals;
import static org.kaazing.nuklei.amqp_1_0.codec.util.FieldMutators.newMutator;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.kaazing.nuklei.Flyweight;
import org.kaazing.nuklei.concurrent.AtomicBuffer;
import org.kaazing.nuklei.function.AtomicBufferMutator;

@RunWith(Theories.class)
public class DynamicTypeTest {

    private static final int BUFFER_CAPACITY = 64;
    private static final AtomicBufferMutator<String> WRITE_UTF_8 = newMutator(UTF_8);
    
    @DataPoint
    public static final int ZERO_OFFSET = 0;
    
    @DataPoint
    public static final int NON_ZERO_OFFSET = new Random().nextInt(BUFFER_CAPACITY - 1) + 1;

    private final AtomicBuffer buffer = new AtomicBuffer(new byte[BUFFER_CAPACITY]);
    
    @Theory
    public void shouldDecodeBinary1Limit(int offset) {
        BinaryType binaryType = new BinaryType();
        binaryType.wrap(buffer, offset);
        binaryType.set(WRITE_UTF_8, "Hello, world");
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(binaryType.limit(), dynamicType.limit());
    }
    
    @Theory
    public void shouldDecodeBinary4Limit(int offset) {
        char[] chars = new char[256];
        Arrays.fill(chars, 'a');

        BinaryType binaryType = new BinaryType();
        binaryType.wrap(buffer, offset);
        binaryType.set(WRITE_UTF_8, new String(chars));
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(binaryType.limit(), dynamicType.limit());
    }
    
    @Theory
    public void shouldDecodeBooleanTrueLimit(int offset) {
        BooleanType booleanType = new BooleanType();
        booleanType.wrap(buffer, offset);
        booleanType.set(true);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(booleanType.limit(), dynamicType.limit());
    }
    
    @Theory
    public void shouldDecodeBooleanFalseLimit(int offset) {
        BooleanType booleanType = new BooleanType();
        booleanType.wrap(buffer, offset);
        booleanType.set(false);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(booleanType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeByteLimit(int offset) {
        ByteType byteType = new ByteType();
        byteType.wrap(buffer, offset);
        byteType.set((byte) 0x12);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(byteType.limit(), dynamicType.limit());
    }
    
    @Theory
    public void shouldDecodeCharLimit(int offset) {
        CharType charType = new CharType();
        charType.wrap(buffer, offset);
        charType.set(0x12);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(charType.limit(), dynamicType.limit());
    }
    
    @Theory
    @Ignore("until decimal128 format implemented")
    public void shouldDecodeDecimal128Limit(int offset) {
        Decimal128Type decimal128Type = new Decimal128Type();
        decimal128Type.wrap(buffer, offset);
        decimal128Type.set(new BigDecimal(1.23456, DECIMAL128));
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(decimal128Type.limit(), dynamicType.limit());
    }
    
    @Theory
    @Ignore("until decimal32 format implemented")
    public void shouldDecodeDecimal32Limit(int offset) {
        Decimal32Type decimal32Type = new Decimal32Type();
        decimal32Type.wrap(buffer, offset);
        decimal32Type.set(new BigDecimal(1.23456, DECIMAL128));
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(decimal32Type.limit(), dynamicType.limit());
    }
    
    @Theory
    @Ignore("until decimal64 format implemented")
    public void shouldDecodeDecimal64Limit(int offset) {
        Decimal64Type decimal64Type = new Decimal64Type();
        decimal64Type.wrap(buffer, offset);
        decimal64Type.set(new BigDecimal(1.23456, DECIMAL128));
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(decimal64Type.limit(), dynamicType.limit());
    }
    
    @Theory
    public void shouldDecodeDoubleLimit(int offset) {
        DoubleType doubleType = new DoubleType();
        doubleType.wrap(buffer, offset);
        doubleType.set(12345678d);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(doubleType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeFloatLimit(int offset) {
        FloatType floatType = new FloatType();
        floatType.wrap(buffer, offset);
        floatType.set(12345678f);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(floatType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeInt1Limit(int offset) {
        IntType intType = new IntType();
        intType.wrap(buffer, offset);
        intType.set(1);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(intType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeInt4Limit(int offset) {
        IntType intType = new IntType();
        intType.wrap(buffer, offset);
        intType.set(0x12345678);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(intType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeList0Limit(int offset) {
        ListType listType = new ListType();
        listType.wrap(buffer, offset);
        listType.maxLength(0x00);
        listType.clear();
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(listType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeList1Limit(int offset) {
        ListType listType = new ListType();
        listType.wrap(buffer, offset);
        listType.maxLength(0xff);
        listType.clear();
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(listType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeList8Limit(int offset) {
        ListType listType = new ListType();
        listType.wrap(buffer, offset);
        listType.maxLength(0x100);
        listType.clear();
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(listType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeLong1Limit(int offset) {
        LongType longType = new LongType();
        longType.wrap(buffer, offset);
        longType.set(1L);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(longType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeLong8Limit(int offset) {
        LongType longType = new LongType();
        longType.wrap(buffer, offset);
        longType.set(12345678L);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(longType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeMap0Limit(int offset) {
        MapType mapType = new MapType();
        mapType.wrap(buffer, offset);
        mapType.maxLength(0x00);
        mapType.clear();
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(mapType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeMap1Limit(int offset) {
        MapType mapType = new MapType();
        mapType.wrap(buffer, offset);
        mapType.maxLength(0xff);
        mapType.clear();
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(mapType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeMap8Limit(int offset) {
        MapType mapType = new MapType();
        mapType.wrap(buffer, offset);
        mapType.maxLength(0x100);
        mapType.clear();
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(mapType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeNullLimit(int offset) {
        NullType nullType = new NullType();
        nullType.wrap(buffer, offset);
        nullType.set(null);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(nullType.limit(), dynamicType.limit());
    }
    
    @Theory
    public void shouldDecodeShortLimit(int offset) {
        ShortType shortType = new ShortType();
        shortType.wrap(buffer, offset);
        shortType.set((short) 0x1234);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(shortType.limit(), dynamicType.limit());
    }
    
    @Theory
    public void shouldDecodeString1Limit(int offset) {
        StringType stringType = new StringType();
        stringType.wrap(buffer, offset);
        stringType.set(WRITE_UTF_8, "a");
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(stringType.limit(), dynamicType.limit());
    }
    
    @Theory
    public void shouldDecodeString4Limit(int offset) {
        char[] chars = new char[256];
        Arrays.fill(chars, 'a');

        StringType stringType = new StringType();
        stringType.wrap(buffer, offset);
        stringType.set(WRITE_UTF_8, new String(chars));
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(stringType.limit(), dynamicType.limit());
    }
    
    @Theory
    public void shouldDecodeSymbol1Limit(int offset) {
        SymbolType symbolType = new SymbolType();
        symbolType.wrap(buffer, offset);
        symbolType.set(WRITE_UTF_8, "a");
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(symbolType.limit(), dynamicType.limit());
    }
    
    @Theory
    public void shouldDecodeSymbol4Limit(int offset) {
        char[] chars = new char[256];
        Arrays.fill(chars, 'a');

        SymbolType symbolType = new SymbolType();
        symbolType.wrap(buffer, offset);
        symbolType.set(WRITE_UTF_8, new String(chars));
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(symbolType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeTimestampLimit(int offset) {
        TimestampType timestampType = new TimestampType();
        timestampType.wrap(buffer, offset);
        timestampType.set(0x12345678L);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(timestampType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeUByteLimit(int offset) {
        UByteType ubyteType = new UByteType();
        ubyteType.wrap(buffer, offset);
        ubyteType.set(0x12);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(ubyteType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeUInt0Limit(int offset) {
        UIntType uintType = new UIntType();
        uintType.wrap(buffer, offset);
        uintType.set(0L);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(uintType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeUInt1Limit(int offset) {
        UIntType uintType = new UIntType();
        uintType.wrap(buffer, offset);
        uintType.set(1L);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(uintType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeUInt4Limit(int offset) {
        UIntType uintType = new UIntType();
        uintType.wrap(buffer, offset);
        uintType.set(0x12345678L);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(uintType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeULong0Limit(int offset) {
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        ulongType.set(0L);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(ulongType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeULong1Limit(int offset) {
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        ulongType.set(1L);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(ulongType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeULong8Limit(int offset) {
        ULongType ulongType = new ULongType();
        ulongType.wrap(buffer, offset);
        ulongType.set(12345678L);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(ulongType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeUShortLimit(int offset) {
        UShortType ushortType = new UShortType();
        ushortType.wrap(buffer, offset);
        ushortType.set(0x1234);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(ushortType.limit(), dynamicType.limit());
    }

    @Theory
    public void shouldDecodeUuidLimit(int offset) {
        UuidType uuidType = new UuidType();
        uuidType.wrap(buffer, offset);
        uuidType.set(fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6"));
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);

        assertEquals(uuidType.limit(), dynamicType.limit());
    }

    @Theory
    @Test(expected = Exception.class)
    public void shouldNotDecode(int offset) {
        buffer.putByte(offset, (byte) 0x00);
        
        DynamicType dynamicType = new DynamicType();
        dynamicType.wrap(buffer, offset);
        dynamicType.limit();
    }

    @Theory
    @SuppressWarnings("unchecked")
    public void shouldNotifyChanged(int offset) {
        final Consumer<Flyweight> observer = mock(Consumer.class);
        
        NullType nullType = new NullType();
        nullType.watch(observer);
        nullType.wrap(buffer, offset);
        nullType.set(null);
        
        verify(observer).accept(nullType);
    }
    
}
