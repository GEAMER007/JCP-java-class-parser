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

public class ConstElementValue extends AnnotationElementValue {
	private final char tag;
	private final Object value;

	public ConstElementValue(char tag, ConstantPool constantPool, DataInputStream input) throws IOException {
		this.tag = tag;
		this.value = (Object) constantPool.get(input.readUnsignedShort());
	}

	public Object getValue() {
		return value;
	}

	public boolean isByte() {
		return tag == 'B';
	}

	public boolean isChar() {
		return tag == 'C';
	}

	public boolean isDouble() {
		return tag == 'D';
	}

	public boolean isFloat() {
		return tag == 'F';
	}

	public boolean isInt() {
		return tag == 'I';
	}

	public boolean isLong() {
		return tag == 'J';
	}

	public boolean isShort() {
		return tag == 'S';
	}

	public boolean isBoolean() {
		return tag == 'Z';
	}

	public boolean isString() {
		return tag == 's';
	}
}
