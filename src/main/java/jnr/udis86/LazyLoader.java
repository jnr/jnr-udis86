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

import com.kenai.jffi.CallContext;
import com.kenai.jffi.CallingConvention;
import com.kenai.jffi.NativeType;
import com.kenai.jffi.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

final class LazyLoader<T> extends AbstractMap<Method, Invoker> {

    @SuppressWarnings("unused")
    private final NativeLibrary library;
    @SuppressWarnings("unused")
    private final Class<T> interfaceClass;
    @SuppressWarnings("unused")
    private final Map<?, ?> libraryOptions;

    LazyLoader(NativeLibrary library, Class<T> interfaceClass, Map<?, ?> libraryOptions) {
        this.library = library;
        this.interfaceClass = interfaceClass;
        this.libraryOptions = libraryOptions;
    }

    @Override
    public Set<Entry<Method, Invoker>> entrySet() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public synchronized Invoker get(Object key) {

        if (!(key instanceof Method)) {
            throw new IllegalArgumentException("key not instance of Method");
        }
        Method m = (Method) key;
        long address = library.getSymbolAddress(m.getName());
        if (address == 0) {
            return new FunctionNotFoundInvoker(m, m.getName());
        }

        Type[] parameterTypes = new Type[m.getParameterTypes().length];
        Converter[] parameterConverters = new Converter[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class javaType = m.getParameterTypes()[i];
            parameterTypes[i] = jffiType(javaType, m.getParameterAnnotations()[i]);
            parameterConverters[i] = Converter.parameterConverter(javaType);
        }

        return new ConvertingInvoker(NumericInvoker.create(CallContext.getCallContext(jffiType(m.getReturnType(), m.getAnnotations()), parameterTypes, CallingConvention.DEFAULT, true), address),
                Converter.resultConverter(m.getReturnType()), parameterConverters);
    }


    static NativeType nativeType(Class javaType, Annotation[] annotations) {
        for (Annotation a : annotations) {
            Annotation typedef = a.annotationType().getAnnotation(typedef.class);
            if (typedef != null) {
                return ((typedef) typedef).nativeType();
            }
        }
        if (int.class == javaType) {
            return NativeType.SINT32;

        } else if (long.class == javaType) {
            return NativeType.SLONG;

        } else if (X86Disassembler.class == javaType || String.class == javaType) {
            return NativeType.POINTER;

        } else if (void.class == javaType) {
            return NativeType.VOID;

        } else {
            throw new UnsatisfiedLinkError("cannot resolve type: " + javaType);
        }
    }

    static Type jffiType(Class javaType, Annotation[] annotations) {
        return jffiType(nativeType(javaType, annotations));
    }

    static Type jffiType(NativeType nativeType) {
        switch (nativeType) {
            case SINT8: return Type.SINT8;
            case UINT8: return Type.UINT8;
            case SINT16: return Type.SINT16;
            case UINT16: return Type.UINT16;
            case SINT32: return Type.SINT32;
            case UINT32: return Type.UINT32;
            case SINT64: return Type.SINT64;
            case UINT64: return Type.UINT64;
            case SLONG: return Type.SLONG;
            case ULONG: return Type.ULONG;
            case FLOAT: return Type.FLOAT;
            case DOUBLE: return Type.DOUBLE;
            case POINTER: return Type.POINTER;
            case VOID: return Type.VOID;

            default:
                throw new UnsatisfiedLinkError("cannot resolve type: " + nativeType);
        }
    }

}
