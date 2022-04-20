/**
 * Copyright 2020 Viridian Software Ltd.
 */
package com.viridiansoftware.java.attributes;

import com.viridiansoftware.java.constants.ConstantPool;

import java.io.DataInputStream;
import java.io.IOException;

public class AnnotationDefault {
	private char tag;
	private AnnotationElementValue defaultValue;

	public AnnotationDefault(ConstantPool constantPool, AttributeInfo attributeInfo) throws IOException {
		final DataInputStream dataInputStream = attributeInfo.getDataInputStream();
		tag = (char) dataInputStream.readUnsignedByte();
		defaultValue = AnnotationElementValue.create(tag, constantPool, dataInputStream);
	}

	public char getTag() {
		return tag;
	}

	public AnnotationElementValue getDefaultValue() {
		return defaultValue;
	}
}
