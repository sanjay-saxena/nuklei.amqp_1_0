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

import java.util.function.Consumer;

import org.kaazing.nuklei.Flyweight;

import uk.co.real_logic.agrona.BitUtil;
import uk.co.real_logic.agrona.MutableDirectBuffer;

/*
 * See AMQP 1.0 specification, section 1.6.11 "float"
 */
public final class FloatType extends Type {

    private static final int OFFSET_KIND = 0;
    private static final int SIZEOF_KIND = BitUtil.SIZE_OF_BYTE;

    private static final int OFFSET_VALUE = OFFSET_KIND + SIZEOF_KIND;
    private static final int SIZEOF_VALUE = BitUtil.SIZE_OF_FLOAT;

    static final int SIZEOF_FLOAT = SIZEOF_KIND + SIZEOF_VALUE;
    
    private static final short WIDTH_KIND_4 = 0x72;

    @Override
    public Kind kind() {
        return Kind.FLOAT;
    }

    @Override
    public FloatType watch(Consumer<Flyweight> observer) {
        super.watch(observer);
        return this;
    }

    @Override
    public FloatType wrap(MutableDirectBuffer buffer, int offset) {
        super.wrap(buffer, offset);
        return this;
    }

    public FloatType set(float value) {
        widthKind(WIDTH_KIND_4);
        floatPut(buffer(), offset() + OFFSET_VALUE, value);
        notifyChanged();
        return this;
    }
    
    public float get() {
        switch (widthKind()) {
        case WIDTH_KIND_4:
            return floatGet(buffer(), offset() + OFFSET_VALUE);
        default:
            throw new IllegalStateException();
        }
    }

    public int limit() {
        return offset() + SIZEOF_FLOAT;
    }

    private void widthKind(short value) {
        uint8Put(buffer(), offset() + OFFSET_KIND, value);
    }
    
    private short widthKind() {
        return uint8Get(buffer(), offset() + OFFSET_KIND);
    }
}
