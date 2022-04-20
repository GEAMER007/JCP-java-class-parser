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

import com.viridiansoftware.java.constants.ConstantClass;
import com.viridiansoftware.java.constants.ConstantPool;

import java.io.DataInputStream;
import java.io.IOException;

public class NestMembers {
	private final ConstantClass [] classes;

	public NestMembers(DataInputStream dataInputStream, ConstantPool constantPool) throws IOException {
		this.classes = new ConstantClass[dataInputStream.readUnsignedShort()];
		for(int i = 0; i < this.classes.length; i++) {
			classes[i] = (ConstantClass) constantPool.get(dataInputStream.readUnsignedShort());
		}
	}

	public ConstantClass[] getClasses() {
		return classes;
	}
}
