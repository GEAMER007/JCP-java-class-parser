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

public class AnnotationElementValuePair {
	private final String elementName;
	private final char elementTag;
	private final AnnotationElementValue elementValue;

	public AnnotationElementValuePair(ConstantPool constantPool, DataInputStream input) throws IOException {
		elementName = (String) constantPool.get(input.readUnsignedShort());

		elementTag = (char) input.readUnsignedByte();
		elementValue = AnnotationElementValue.create(elementTag, constantPool, input);
	}

	public String getElementName() {
		return elementName;
	}

	public AnnotationElementValue getElementValue() {
		return elementValue;
	}

	public boolean isByte() {
		return elementTag == 'B';
	}

	public boolean isChar() {
		return elementTag == 'C';
	}

	public boolean isDouble() {
		return elementTag == 'D';
	}

	public boolean isFloat() {
		return elementTag == 'F';
	}

	public boolean isInt() {
		return elementTag == 'I';
	}

	public boolean isLong() {
		return elementTag == 'J';
	}

	public boolean isShort() {
		return elementTag == 'S';
	}

	public boolean isBoolean() {
		return elementTag == 'Z';
	}

	public boolean isString() {
		return elementTag == 's';
	}

	public boolean isEnum() {
		return elementTag == 'e';
	}

	public boolean isClass() {
		return elementTag == 'c';
	}

	public boolean isAnnotation() {
		return elementTag == '@';
	}

	public boolean isArray() {
		return elementTag == '[';
	}
}
