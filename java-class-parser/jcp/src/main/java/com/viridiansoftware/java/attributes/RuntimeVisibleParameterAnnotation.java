/*******************************************************************************
 * Copyright 2019 Viridian Software Ltd
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
package com.viridiansoftware.java.attributes;

import com.viridiansoftware.java.constants.ConstantPool;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RuntimeVisibleParameterAnnotation {
	private final List<RuntimeVisibleAnnotation> annotations = new ArrayList<RuntimeVisibleAnnotation>();

	public RuntimeVisibleParameterAnnotation(ConstantPool constantPool, DataInputStream input) throws IOException {
		final int totalAnnotations = input.readUnsignedShort();
		for(int i = 0; i < totalAnnotations; i++) {
			annotations.add(new RuntimeVisibleAnnotation(constantPool, input));
		}
	}

	public RuntimeVisibleAnnotation getAnnotationByType(String classInfo) {
		if(!classInfo.startsWith("L")) {
			classInfo = "L" + classInfo + ";";
		}
		for(int i = 0; i < annotations.size(); i++) {
			if(annotations.get(i).getTypeDescriptor().equalsIgnoreCase(classInfo)) {
				return annotations.get(i);
			}
		}
		return null;
	}

	public List<RuntimeVisibleAnnotation> getAnnotations() {
		return annotations;
	}
}
