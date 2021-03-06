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

import static java.lang.Integer.highestOneBit;

import java.util.function.Consumer;

import org.kaazing.nuklei.Flyweight;
import org.kaazing.nuklei.FlyweightBE;

import uk.co.real_logic.agrona.BitUtil;
import uk.co.real_logic.agrona.MutableDirectBuffer;

/*
 * See AMQP 1.0 specification, section 1.6.24 "array"
 */
public final class ArrayType extends Type {

    private final Header header;
    private final DynamicType elementType;
    
    public ArrayType() {
        this.elementType = new DynamicType();
        this.header = new Header().watch((owner) -> { elementType().wrap(buffer(), owner.limit()); notifyChanged(); });
    }

    @Override
    public Kind kind() {
        return Kind.ARRAY;
    }
    
    public Kind elementKind() {
        return elementType().kind();
    }

    @Override
    public ArrayType watch(Consumer<Flyweight> observer) {
        super.watch(observer);
        return this;
    }

    @Override
    public ArrayType wrap(MutableDirectBuffer buffer, int offset) {
        super.wrap(buffer, offset);
        header.wrap(buffer, offset);
        return this;
    }

    public ArrayType set(ArrayType value) {
        buffer().putBytes(offset(), value.buffer(), value.offset(), value.limit() - value.offset());
        notifyChanged();
        return this;
    }

    public int length() {
        return header.length();
    }

    public ArrayType maxLength(int value) {
        header.max(value);
        return this;
    }
    
    public int count() {
        return header.count();
    }
    
    public ArrayType maxCount(int value) {
        header.max(value);
        return this;
    }
    
    @Override
    public int limit() {
        return header.lengthLimit() + header.length();
    }
    
    public final void limit(int count, int limit) {
        header.count(count);
        header.length(limit - header.lengthLimit());
    }

    protected final int offsetBody() {
        return header.limit();
    }

    private DynamicType elementType() {
        elementType.wrap(buffer(), header.limit());
        return elementType;
    }

    private static final class Header extends FlyweightBE {

        private static final int OFFSET_LENGTH_KIND = 0;
        private static final int SIZEOF_LENGTH_KIND = BitUtil.SIZE_OF_BYTE;
        private static final int OFFSET_LENGTH = OFFSET_LENGTH_KIND + SIZEOF_LENGTH_KIND;

        private static final short WIDTH_KIND_1 = 0xe0;
        private static final short WIDTH_KIND_4 = 0xf0;

        @Override
        public Header wrap(MutableDirectBuffer buffer, int offset) {
            super.wrap(buffer, offset);
            return this;
        }

        @Override
        public Header watch(Consumer<Flyweight> observer) {
            super.watch(observer);
            return this;
        }

        public void count(int value) {
            switch (kind()) {
            case WIDTH_KIND_1:
                switch (highestOneBit(value)) {
                case 0:
                case 1:
                case 2:
                case 4:
                case 8:
                case 16:
                case 32:
                case 64:
                case 128:
                    uint8Put(buffer(), offset() + OFFSET_LENGTH + 1, (short) value);
                    break;
                default:
                    throw new IllegalStateException();
                }
                break;
            case WIDTH_KIND_4:
                int32Put(buffer(), offset() + OFFSET_LENGTH + 4, value);
                break;
            default:
                throw new IllegalStateException();
            }
        }

        public int count() {
            switch (kind()) {
            case WIDTH_KIND_1:
                return uint8Get(buffer(), offset() + OFFSET_LENGTH + 1);
            case WIDTH_KIND_4:
                return int32Get(buffer(), offset() + OFFSET_LENGTH + 4);
            default:
                throw new IllegalStateException();
            }
        }

        public int length() {
            switch (kind()) {
            case WIDTH_KIND_1:
                return uint8Get(buffer(), offset() + OFFSET_LENGTH);
            case WIDTH_KIND_4:
                return int32Get(buffer(), offset() + OFFSET_LENGTH);
            default:
                throw new IllegalStateException();
            }
        }
        
        public int lengthLimit() {
            switch (kind()) {
            case WIDTH_KIND_1:
                return offset() + OFFSET_LENGTH + 1;
            case WIDTH_KIND_4:
                return offset() + OFFSET_LENGTH + 4;
            default:
                throw new IllegalStateException();
            }
        }
        
        public Header length(int value) {
            switch (kind()) {
            case WIDTH_KIND_1:
                switch (highestOneBit(value)) {
                case 0:
                case 1:
                case 2:
                case 4:
                case 8:
                case 16:
                case 32:
                case 64:
                case 128:
                    uint8Put(buffer(), offset() + OFFSET_LENGTH, (short) value);
                    break;
                default:
                    throw new IllegalStateException();
                }
                break;
            case WIDTH_KIND_4:
                int32Put(buffer(), offset() + OFFSET_LENGTH, value);
                break;
            default:
                throw new IllegalStateException();
            }
            
            notifyChanged();
            return this;
        }

        public void max(int value) {
            switch (highestOneBit(value)) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 8:
            case 16:
            case 32:
            case 64:
            case 128:
                kind(WIDTH_KIND_1);
                break;
            default:
                kind(WIDTH_KIND_4);
                break;
            }
            
        }
        
        public int limit() {
            switch (kind()) {
            case WIDTH_KIND_1:
                return offset() + OFFSET_LENGTH + 2;
            case WIDTH_KIND_4:
                return offset() + OFFSET_LENGTH + 8;
            default:
                throw new IllegalStateException();
            }
        }

        private void kind(short kind) {
            uint8Put(buffer(), offset() + OFFSET_LENGTH_KIND, kind);
        }

        private short kind() {
            return uint8Get(buffer(), offset() + OFFSET_LENGTH_KIND);
        }
    }

}
