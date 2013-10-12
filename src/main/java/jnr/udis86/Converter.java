/*
 * Copyright (C) 2012-2013 Wayne Meissner
 *
 * This file is part of the JNR project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jnr.udis86;

import com.kenai.jffi.MemoryIO;

import java.nio.charset.Charset;

abstract class Converter {
    static Converter parameterConverter(Class javaType) {
        if (X86Disassembler.class == javaType) {
            return X86DISASSEMBLER;

        } else {
            return IDENTITY;
        }
    }

    static Converter resultConverter(Class javaType) {
        if (byte.class == javaType) {
            return byteValue;

        } else if (short.class == javaType) {
            return shortValue;

        } else if (int.class == javaType) {
            return intValue;

        } else if (long.class == javaType) {
            return longValue;

        } else if (String.class == javaType) {
            return stringValue;

        } else {
            return IDENTITY;
        }
    }

    abstract Object convert(Object value);

    static final Converter IDENTITY = new Converter() {
        @Override
        public Object convert(Object value) {
            return value;
        }
    };

    static final Converter X86DISASSEMBLER = new Converter() {
        @Override
        public Object convert(Object value) {
            return ((X86Disassembler) value).ud;
        }
    };
    static final Converter byteValue = new Converter() {
        @Override
        public Object convert(Object value) {
            return ((Number) value).byteValue();
        }
    };

    static final Converter shortValue = new Converter() {
        @Override
        public Object convert(Object value) {
            return ((Number) value).shortValue();
        }
    };

    static final Converter intValue = new Converter() {
        @Override
        public Object convert(Object value) {
            return ((Number) value).intValue();
        }
    };

    static final Converter longValue = new Converter() {
        @Override
        public Object convert(Object value) {
            return ((Number) value).longValue();
        }
    };

    static final Converter stringValue = new Converter() {
        @Override
        public Object convert(Object value) {
            long ptr = ((Number) value).longValue();
            return ptr != 0L
                    ? new String(MemoryIO.getInstance().getZeroTerminatedByteArray(ptr), Charset.defaultCharset())
                    : null;
        }
    };
}
