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

public abstract class AnnotationElementValue {

	public static AnnotationElementValue create(char tag, ConstantPool constantPool, DataInputStream input) throws IOException {
		switch(tag) {
		default:
		case 'B':
		case 'C':
		case 'D':
		case 'F':
		case 'I':
		case 'J':
		case 'S':
		case 'Z':
		case 's':
			return new ConstElementValue(tag, constantPool, input);
		case 'e':
			return new EnumConstElementValue(constantPool, input);
		case 'c':
			return new ClassElementValue(constantPool, input);
		case '@':
			return new AnnotationValue(constantPool, input);
		case '[':
			return new ArrayElementValue(constantPool, input);
		}
	}
}
