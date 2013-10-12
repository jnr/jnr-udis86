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

import java.lang.reflect.Method;

final class FunctionNotFoundInvoker implements Invoker {
    private final Method method;
    private final String functionName;

    FunctionNotFoundInvoker(Method method, String functionName) {
        this.method = method;
        this.functionName = functionName;
    }

    @Override
    public Object invoke(Object self, Object[] parameters) {
        throw new UnsatisfiedLinkError(String.format("native method '%s' not found for method %s", functionName,  method));
    }
}
