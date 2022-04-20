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

public class MethodAccessFlagTest {

	@Test
	public void testPublicAbstract() {
		Assert.assertEquals(MethodAccessFlag.ABSTRACT.getMask(), 0x0401 & MethodAccessFlag.ABSTRACT.getMask());
		Assert.assertEquals(MethodAccessFlag.PUBLIC.getMask(), 0x0401 & MethodAccessFlag.PUBLIC.getMask());
	}

	@Test
	public void testDefaultScope() {
		Assert.assertTrue( (0x0400 & MethodAccessFlag.PUBLIC.getMask()) != MethodAccessFlag.PUBLIC.getMask());
		Assert.assertTrue( (0x0400 & MethodAccessFlag.PRIVATE.getMask()) != MethodAccessFlag.PRIVATE.getMask());
		Assert.assertTrue( (0x0400 & MethodAccessFlag.PROTECTED.getMask()) != MethodAccessFlag.PROTECTED.getMask());

		Assert.assertTrue( (0x0401 & MethodAccessFlag.PUBLIC.getMask()) == MethodAccessFlag.PUBLIC.getMask());
		Assert.assertTrue( (0x0401 & MethodAccessFlag.PRIVATE.getMask()) != MethodAccessFlag.PRIVATE.getMask());
		Assert.assertTrue( (0x0401 & MethodAccessFlag.PROTECTED.getMask()) != MethodAccessFlag.PROTECTED.getMask());
	}
}
