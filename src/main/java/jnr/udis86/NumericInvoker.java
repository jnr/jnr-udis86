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

abstract class NumericInvoker implements Invoker {
    protected final CallContext callContext;
    protected final long address;

    protected NumericInvoker(CallContext callContext, long address) {
        this.callContext = callContext;
        this.address = address;
    }

    static Invoker create(CallContext callContext, long address) {
        switch (callContext.getParameterCount()) {
            case 0:
                return new N0(callContext, address);
            case 1:
                return new N1(callContext, address);
            case 2:
                return new N2(callContext, address);
            case 3:
                return new N3(callContext, address);

            default:
                throw new UnsatisfiedLinkError("unsupported arity: " + callContext.getParameterCount());
        }
    }

    private static final class N0 extends NumericInvoker {
        N0(CallContext callContext, long address) {
            super(callContext, address);
        }

        @Override
        public Object invoke(Object self, Object[] parameters) {
            return com.kenai.jffi.Invoker.getInstance().invokeN0(callContext, address);
        }
    }

    private static final class N1 extends NumericInvoker {
        N1(CallContext callContext, long address) {
            super(callContext, address);
        }

        @Override
        public Object invoke(Object self, Object[] parameters) {
            return com.kenai.jffi.Invoker.getInstance().invokeN1(callContext, address, ((Number) parameters[0]).longValue());
        }
    }

    private static final class N2 extends NumericInvoker {
        N2(CallContext callContext, long address) {
            super(callContext, address);
        }

        @Override
        public Object invoke(Object self, Object[] parameters) {
            return com.kenai.jffi.Invoker.getInstance().invokeN2(callContext, address,
                    ((Number) parameters[0]).longValue(), ((Number) parameters[1]).longValue());
        }
    }

    private static final class N3 extends NumericInvoker {
        N3(CallContext callContext, long address) {
            super(callContext, address);
        }

        @Override
        public Object invoke(Object self, Object[] parameters) {
            return com.kenai.jffi.Invoker.getInstance().invokeN3(callContext, address,
                    ((Number) parameters[0]).longValue(),
                    ((Number) parameters[1]).longValue(),
                    ((Number) parameters[2]).longValue());
        }
    }
}
