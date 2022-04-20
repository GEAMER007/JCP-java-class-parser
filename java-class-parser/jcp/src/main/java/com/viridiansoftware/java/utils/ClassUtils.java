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
package com.viridiansoftware.java.utils;

import com.viridiansoftware.java.PrimitiveType;
import com.viridiansoftware.java.descriptor.FieldDescriptor;
import com.viridiansoftware.java.descriptor.MethodDescriptor;
import com.viridiansoftware.java.descriptor.antlr.DescriptorParser;
import com.viridiansoftware.java.signature.MethodSignature;
import com.viridiansoftware.java.signature.antlr.SignatureParser;

public class ClassUtils {
	private static final String [] EMPTY_GENERICS = new String[0];

	public static boolean hasGenerics(String className) {
		if(!className.contains("<")) {
			return false;
		}
		if(!className.contains(">")) {
			return false;
		}
		return true;
	}

	public static String [] getGenericTypes(String className) {
		if(!hasGenerics(className)) {
			return EMPTY_GENERICS;
		}
		final String genericClasses = className.substring(className.indexOf('<') + 1, className.lastIndexOf('>'));
		return genericClasses.split(";");
	}

	private static void appendPackageSpecifier(StringBuilder result, SignatureParser.PackageSpecifierContext context) {
		if(context.identifier() == null) {
			return;
		}
		result.append(context.identifier().getText());
		result.append('/');

		if(context.packageSpecifier() == null) {
			return;
		}
		for(int i = 0; i < context.packageSpecifier().size(); i++) {
			appendPackageSpecifier(result, context.packageSpecifier(i));
		}
	}

	public static String getQualifiedSimpleClassName(SignatureParser.ClassTypeSignatureContext context) {
		final StringBuilder result = new StringBuilder();
		appendPackageSpecifier(result, context.packageSpecifier());
		result.append(context.simpleClassTypeSignature().identifier().getText());
		return result.toString();
	}

	public static boolean isSameType(final MethodDescriptor expectedType, final MethodSignature actualType) {
		if(actualType.getTotalMethodArguments() != expectedType.getTotalMethodParameters()) {
			return false;
		}

		boolean match = true;
		for(int i = 0; i < actualType.getTotalMethodArguments(); i++) {
			final DescriptorParser.FieldTypeContext expectedTypeSignature = expectedType.getMethodParameter(i);
			final SignatureParser.JavaTypeSignatureContext actualTypeSignature = actualType.getMethodArgument(i);

			if(!ClassUtils.isSameType(expectedTypeSignature, actualTypeSignature)) {
				match = false;
				break;
			}
		}

		if(!match) {
			return false;
		}
		if(expectedType.isVoidMethod() && actualType.isVoidMethod()) {
			return true;
		}
		if(expectedType.isVoidMethod() && !actualType.isVoidMethod()) {
			return false;
		}
		if(!expectedType.isVoidMethod() && actualType.isVoidMethod()) {
			return false;
		}
		final DescriptorParser.ReturnDescriptorContext expectedReturnTypeSignature = expectedType.getReturnDescriptor();
		final SignatureParser.JavaTypeSignatureContext actualReturnTypeSignature = actualType.getReturnType();

		return isSameType(expectedReturnTypeSignature.fieldType(), actualReturnTypeSignature);
	}

	public static boolean isSameType(final DescriptorParser.FieldTypeContext expectedType, final SignatureParser.JavaTypeSignatureContext actualType) {
		if(expectedType.BaseType() != null) {
			if(actualType.BaseType() != null) {
				return expectedType.BaseType().getText().equals(actualType.BaseType().getText());
			}
			return false;
		}
		if(expectedType.objectType() != null) {
			if(actualType.referenceTypeSignature() != null && actualType.referenceTypeSignature().classTypeSignature() != null) {
				String actualObjectType = actualType.referenceTypeSignature().classTypeSignature().getText();
				if(actualObjectType.contains("<")) {
					actualObjectType = actualObjectType.substring(0, actualObjectType.indexOf('<'));
				} else {
					actualObjectType = actualObjectType.substring(0, actualObjectType.indexOf(';'));
				}
				return actualObjectType.equals("L" + expectedType.objectType().identifier().getText());
			}
			return false;
		}
		if(expectedType.arrayType() != null) {
			if(actualType.referenceTypeSignature() != null && actualType.referenceTypeSignature().arrayTypeSignature() != null) {
				DescriptorParser.ArrayTypeContext expectedArrayType = expectedType.arrayType();
				SignatureParser.ArrayTypeSignatureContext actualArrayType = actualType.referenceTypeSignature().arrayTypeSignature();
				while(expectedArrayType != null) {
					if(expectedArrayType.fieldType().arrayType() != null) {
						if(actualArrayType.javaTypeSignature().referenceTypeSignature() == null) {
							return false;
						}
						if(actualArrayType.javaTypeSignature().referenceTypeSignature().arrayTypeSignature() == null) {
							return false;
						}
						expectedArrayType = expectedArrayType.fieldType().arrayType();
						actualArrayType = actualArrayType.javaTypeSignature().referenceTypeSignature().arrayTypeSignature();
						continue;
					} else if(expectedArrayType.fieldType().objectType() != null) {
						if(actualArrayType.javaTypeSignature().referenceTypeSignature() == null) {
							return false;
						}
						if(actualArrayType.javaTypeSignature().referenceTypeSignature().classTypeSignature() == null) {
							return false;
						}
						String actualObjectType = actualArrayType.javaTypeSignature().referenceTypeSignature().classTypeSignature().getText();
						if(actualObjectType.contains("<")) {
							actualObjectType = actualObjectType.substring(0, actualObjectType.indexOf('<'));
						} else {
							actualObjectType = actualObjectType.substring(0, actualObjectType.indexOf(';'));
						}
						return actualObjectType.equals("L" + expectedArrayType.fieldType().objectType().identifier().getText());
					} else {
						if(actualArrayType.javaTypeSignature().BaseType() == null) {
							return false;
						}
						return expectedArrayType.fieldType().BaseType().getText().equals(actualArrayType.javaTypeSignature().BaseType().getText());
					}
				}
			}
			return false;
		}
		return false;
	}

	public static boolean isArray(String name) {
		return name.startsWith("[");
	}

	public static int getArrayDimensions(String name) {
		int totalDimensions = 0;
		for(int i = 0; i < name.length(); i++) {
			if(name.charAt(i) == '[') {
				totalDimensions++;
			}
		}
		return totalDimensions;
	}

	public static boolean isPrimitive(String name) {
		return !name.startsWith("L") && !name.startsWith("[");
	}

	public static boolean isObject(String name) {
		return name.startsWith("L");
	}

	public static boolean isArrayOfPrimitives(String name) {
		if(!name.startsWith("[")) {
			return false;
		}
		for(int i = 1; i < name.length(); i++) {
			if(name.charAt(i) == '[') {
				continue;
			}
			return name.charAt(i) != 'L';
		}
		return false;
	}

	public static boolean isArrayOfObjects(String name) {
		if(!name.startsWith("[")) {
			return false;
		}
		for(int i = 1; i < name.length(); i++) {
			if(name.charAt(i) == '[') {
				continue;
			}
			return name.charAt(i) == 'L';
		}
		return false;
	}

	public static PrimitiveType getPrimitiveType(String name) {
		while(name.charAt(0) == '[') {
			name = name.substring(1);
		}
		return getPrimitiveType(name.charAt(0));
	}

	public static PrimitiveType getPrimitiveType(char c) {
		for(PrimitiveType primitiveType : PrimitiveType.values()) {
			if(c == primitiveType.getTerm()) {
				return primitiveType;
			}
		}
		return null;
	}

	static PrimitiveOrReferenceType getPrimitiveOrReferenceType(char c) {
		for(PrimitiveOrReferenceType primitiveOrRefType : PrimitiveOrReferenceType.values()) {
			if(c == primitiveOrRefType.getTerm()) {
				return primitiveOrRefType;
			}
		}
		return null;
	}

	public static String getReferenceClass(String name) {
		int offset = 0;

		switch(name.charAt(0)) {
		case '[':
			offset = 1;
			for(int i = 1; i < name.length(); i++) {
				if(name.charAt(i) == '[') {
					offset++;
					continue;
				}
				if(name.charAt(i) == 'L') {
					offset++;
					break;
				}
				return null;
			}
			break;
		case 'L':
			offset = 1;
			break;
		default:
			for(PrimitiveOrReferenceType typeOrRef : PrimitiveOrReferenceType.values()) {
				if(typeOrRef.getTerm() == name.charAt(0)) {
					return null;
				}
			}
			break;
		}

		if(name.endsWith(";")) {
			name = name.substring(offset);
			return name.substring(0, name.length() - 1);
		}
		return name.substring(offset);
	}
}
