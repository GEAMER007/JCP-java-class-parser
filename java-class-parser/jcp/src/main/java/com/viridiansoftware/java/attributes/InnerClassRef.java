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

import com.viridiansoftware.java.ClassAccessFlag;
import com.viridiansoftware.java.constants.ConstantClass;
import com.viridiansoftware.java.constants.ConstantPool;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InnerClassRef {
	private final ConstantClass innerClass;
	private final ConstantClass outerClass;
	private final String innerName;
	private final int accessFlags;

	private List<ClassAccessFlag> classAccessFlags;

	public InnerClassRef(DataInputStream dataInputStream, ConstantPool constantPool) throws IOException {
		super();

		innerClass = (ConstantClass) constantPool.get(dataInputStream.readUnsignedShort());

		final int outerClassIndex = dataInputStream.readUnsignedShort();
		if(outerClassIndex > 0) {
			outerClass = (ConstantClass) constantPool.get(outerClassIndex);
		} else {
			outerClass = null;
		}

		innerName = (String) constantPool.get(dataInputStream.readUnsignedShort());
		accessFlags = dataInputStream.readUnsignedShort();
	}

	public List<ClassAccessFlag> getClassAccessFlags() {
		if(classAccessFlags == null) {
			classAccessFlags = new ArrayList<ClassAccessFlag>(1);
			for(ClassAccessFlag accessFlag : ClassAccessFlag.values()) {
				if((accessFlag.getMask() & accessFlags) == accessFlag.getMask()) {
					classAccessFlags.add(accessFlag);
				}
			}
		}
		return classAccessFlags;
	}

	public ConstantClass getInnerClass() {
		return innerClass;
	}

	public ConstantClass getOuterClass() {
		return outerClass;
	}

	public String getInnerName() {
		return innerName;
	}

	public int getAccessFlags() {
		return accessFlags;
	}
}
