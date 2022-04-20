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
package com.viridiansoftware.java.constants;

import java.sql.Ref;

public class ConstantMethodHandle {
	private final ReferenceKind referenceKind;
	private final ConstantRef reference;

	public ConstantMethodHandle(int referenceKind, ConstantRef reference) {
		this.reference = reference;

		for(ReferenceKind refKind : ReferenceKind.values()) {
			if(refKind.kind == referenceKind) {
				this.referenceKind = refKind;
				return;
			}
		}
		this.referenceKind = ReferenceKind.GET_FIELD;
	}

	/**
	 * Returns the field or method name depending on the reference kind
	 * @return
	 */
	public String getName() {
		switch(referenceKind) {
		default:
		case GET_FIELD:
		case GET_STATIC:
		case PUT_FIELD:
		case PUT_STATIC: {
			ConstantFieldRef fieldRef = (ConstantFieldRef) reference;
			return fieldRef.getName();
		}
		case INVOKE_VIRTUAL: {
			ConstantMethodRef methodRef = (ConstantMethodRef) reference;
			return methodRef.getName();
		}
		case INVOKE_STATIC:
		case INVOKE_SPECIAL: {
			if(reference instanceof ConstantInterfaceMethodRef) {
				ConstantInterfaceMethodRef methodRef = (ConstantInterfaceMethodRef) reference;
				return methodRef.getName();
			} else {
				ConstantMethodRef methodRef = (ConstantMethodRef) reference;
				return methodRef.getName();
			}
		}
		case NEW_INVOKE_SPECIAL: {
			ConstantMethodRef methodRef = (ConstantMethodRef) reference;
			return methodRef.getName();
		}
		case INVOKE_INTERFACE: {
			ConstantInterfaceMethodRef methodRef = (ConstantInterfaceMethodRef) reference;
			return methodRef.getName();
		}
		}
	}

	public ReferenceKind getReferenceKind() {
		return referenceKind;
	}

	public int getKind() {
		return referenceKind.kind;
	}

	public enum ReferenceKind {
		GET_FIELD(1),
		GET_STATIC(2),
		PUT_FIELD(3),
		PUT_STATIC(4),
		INVOKE_VIRTUAL(5),
		INVOKE_STATIC(6),
		INVOKE_SPECIAL(7),
		NEW_INVOKE_SPECIAL(8),
		INVOKE_INTERFACE(9);

		public int kind;

		ReferenceKind(int kind) {
			this.kind = kind;
		}
	}
}
