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

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestParsing {

	@Test
	public void testParsingByteClass() throws IOException {
		final ClassFile classFile = new ClassFile(getClass().getResourceAsStream("/Byte.class"));
		Assert.assertEquals("Byte.java", classFile.getSourceFile());
		Assert.assertEquals("java/lang/Byte", classFile.getThisClass().getName());
		Assert.assertEquals(ClassFile.Type.Class, classFile.getType());
		Assert.assertEquals(2, classFile.getMethodCount("toString"));
	}

	@Test
	public void testParsingStringClass() throws IOException {
		final ClassFile classFile = new ClassFile(getClass().getResourceAsStream("/String.class"));
		Assert.assertEquals("String.java", classFile.getSourceFile());
		Assert.assertEquals(ClassFile.Type.Class, classFile.getType());
		Assert.assertEquals(1, classFile.getMethodCount("toString"));
		Assert.assertTrue(classFile.getMethod("intern").get(0).getMethodAccessFlags().contains(MethodAccessFlag.NATIVE));
	}
}
