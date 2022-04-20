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

import com.viridiansoftware.java.descriptor.MethodDescriptor;
import com.viridiansoftware.java.signature.MethodSignature;
import com.viridiansoftware.java.utils.ClassUtils;
import org.junit.Assert;
import org.junit.Test;

public class ClassUtilsTest {

	@Test
	public void testGetReferenceClass() {
		Assert.assertEquals("java/lang/Object", ClassUtils.getReferenceClass("java/lang/Object"));
		Assert.assertEquals("java/lang/Object", ClassUtils.getReferenceClass("Ljava/lang/Object;"));
		Assert.assertEquals("java/lang/Object", ClassUtils.getReferenceClass("[Ljava/lang/Object;"));
		Assert.assertEquals("java/lang/Object", ClassUtils.getReferenceClass("[[Ljava/lang/Object;"));
		Assert.assertNull(ClassUtils.getReferenceClass("[[I"));
		Assert.assertNull(ClassUtils.getReferenceClass("[I"));
		Assert.assertNull(ClassUtils.getReferenceClass("I"));
	}

	@Test
	public void testIsSameType() {
		Assert.assertTrue(ClassUtils.isSameType(new MethodDescriptor("()V"), new MethodSignature("()V")));
		Assert.assertTrue(ClassUtils.isSameType(new MethodDescriptor("(IBZ)V"), new MethodSignature("(IBZ)V")));
		Assert.assertTrue(ClassUtils.isSameType(new MethodDescriptor("()Ljava/lang/Object;"), new MethodSignature("()Ljava/lang/Object;")));
		Assert.assertTrue(ClassUtils.isSameType(new MethodDescriptor("()[Ljava/lang/Object;"), new MethodSignature("()[Ljava/lang/Object;")));
		Assert.assertTrue(ClassUtils.isSameType(new MethodDescriptor("([I)V"), new MethodSignature("([I)V")));
		Assert.assertTrue(ClassUtils.isSameType(new MethodDescriptor("([[C)V"), new MethodSignature("([[C)V")));
		Assert.assertTrue(ClassUtils.isSameType(new MethodDescriptor("([[C[I)[[Z"), new MethodSignature("([[C[I)[[Z")));
		Assert.assertTrue(ClassUtils.isSameType(new MethodDescriptor("([[Ljava/lang/Object;)V"), new MethodSignature("([[Ljava/lang/Object;)V")));
		Assert.assertTrue(ClassUtils.isSameType(new MethodDescriptor("(Ljava/lang/Object;Ljava/lang/Object;)V"), new MethodSignature("(Ljava/lang/Object;Ljava/lang/Object;)V")));
		Assert.assertTrue(ClassUtils.isSameType(new MethodDescriptor("(Ljava/lang/Object;Ljava/lang/List;)V"), new MethodSignature("(Ljava/lang/Object;Ljava/lang/List<TT;>;)V")));

		Assert.assertFalse(ClassUtils.isSameType(new MethodDescriptor("(IBZ)V"), new MethodSignature("()V")));
		Assert.assertFalse(ClassUtils.isSameType(new MethodDescriptor("(IBZ)V"), new MethodSignature("([I)V")));
		Assert.assertFalse(ClassUtils.isSameType(new MethodDescriptor("([[C[Z)[[Z"), new MethodSignature("([[C[I)[[Z")));
		Assert.assertFalse(ClassUtils.isSameType(new MethodDescriptor("()Ljava/lang/Object;"), new MethodSignature("()V")));
		Assert.assertFalse(ClassUtils.isSameType(new MethodDescriptor("(Ljava/lang/Object;)V"), new MethodSignature("()V")));
	}
}
