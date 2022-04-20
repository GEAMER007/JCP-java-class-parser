/*******************************************************************************
 * Copyright 2019 Volker Berlin (i-net software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.viridiansoftware.java.constants;

import com.viridiansoftware.java.utils.ClassUtils;

public class ConstantClass {
    private final String name;

    public ConstantClass( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isArray() {
        return ClassUtils.isArray(name);
    }

    public int getArrayDimensions() {
        return ClassUtils.getArrayDimensions(name);
    }

    public boolean isArrayOfPrimitives() {
        return ClassUtils.isArrayOfPrimitives(name);
    }

    public boolean isArrayOfObjects() {
        return ClassUtils.isArrayOfObjects(name);
    }

    public String getReferenceClass() {
        return ClassUtils.getReferenceClass(name);
    }
}