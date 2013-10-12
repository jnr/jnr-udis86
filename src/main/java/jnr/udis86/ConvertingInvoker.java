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

class ConvertingInvoker implements Invoker {
    private final Invoker invoker;
    private final Converter resultConverter;
    private final Converter[] parameterConverters;

    ConvertingInvoker(Invoker invoker, Converter resultConverter, Converter... parameterConverters) {
        this.invoker = invoker;
        this.resultConverter = resultConverter;
        this.parameterConverters = parameterConverters.clone();
    }

    @Override
    public Object invoke(Object self, Object[] parameters) {
        Object[] convertedParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            convertedParameters[i] = parameterConverters[i].convert(parameters[i]);
        }

        return resultConverter.convert(invoker.invoke(self, convertedParameters));
    }
}
