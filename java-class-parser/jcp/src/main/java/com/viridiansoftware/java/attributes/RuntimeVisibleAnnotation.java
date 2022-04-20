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

public class RuntimeVisibleAnnotation {
	private final String typeDescriptor;
	private final List<AnnotationElementValuePair> elementValuePairs = new ArrayList<AnnotationElementValuePair>();

	public RuntimeVisibleAnnotation(ConstantPool constantPool, DataInputStream input) throws IOException {
		typeDescriptor = (String) constantPool.get(input.readUnsignedShort());

		final int totalElementValuePairs = input.readUnsignedShort();
		for(int i = 0; i < totalElementValuePairs; i++) {
			elementValuePairs.add(new AnnotationElementValuePair(constantPool, input));
		}
	}

	public String getTypeDescriptor() {
		return typeDescriptor;
	}

	public AnnotationElementValuePair getElementValuePair(String name) {
		for(int i = 0; i < elementValuePairs.size(); i++) {
			if(elementValuePairs.get(i).getElementName().equalsIgnoreCase(name)) {
				return elementValuePairs.get(i);
			}
		}
		return null;
	}

	public List<AnnotationElementValuePair> getElementValuePairs() {
		return elementValuePairs;
	}
}
