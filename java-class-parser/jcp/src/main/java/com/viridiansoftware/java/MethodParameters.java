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
package com.viridiansoftware.java;

import com.viridiansoftware.java.constants.ConstantPool;

import java.io.DataInputStream;
import java.io.IOException;

public class MethodParameters {
	private final String [] parameterNames;
	private final int [] parameterAccessFlags;

	public MethodParameters(DataInputStream dataInputStream, ConstantPool constantPool) throws IOException {
		final int parameterCount = dataInputStream.readUnsignedByte();
		parameterNames = new String[parameterCount];
		parameterAccessFlags = new int[parameterCount];

		for(int i = 0; i < parameterCount; i++) {
			parameterNames[i] = (String) constantPool.get(dataInputStream.readUnsignedShort());
			parameterAccessFlags[i] = dataInputStream.readUnsignedShort();
		}
	}

	public MethodParameters(String [] parameterNames) {
		this.parameterNames = parameterNames;
		this.parameterAccessFlags = new int[parameterNames.length];
	}

	public String[] getParameterNames() {
		return parameterNames;
	}

	public int[] getParameterAccessFlags() {
		return parameterAccessFlags;
	}
}
