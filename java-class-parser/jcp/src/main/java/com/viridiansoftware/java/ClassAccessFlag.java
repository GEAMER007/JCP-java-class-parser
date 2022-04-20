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

public enum ClassAccessFlag {
	PUBLIC(0x0001),
	PRIVATE(0x0002),
	PROTECTED(0x0004),
	STATIC(0x0008),

	FINAL(0x0010),
	SUPER(0x0020),

	INTERFACE(0x0200),
	ABSTRACT(0x0400),

	SYNTHETIC(0x1000),
	ANNOTATION(0x2000),
	ENUM(0x4000);

	private final int mask;

	ClassAccessFlag(int mask) {
		this.mask = mask;
	}

	public int getMask() {
		return mask;
	}
}
