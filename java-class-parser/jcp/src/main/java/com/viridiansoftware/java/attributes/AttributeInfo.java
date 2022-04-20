package com.viridiansoftware.java.attributes;

import com.viridiansoftware.java.constants.ConstantPool;
import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class AttributeInfo {
	private final String name;
	private final byte[] info;

	public AttributeInfo(@NonNull DataInputStream input, @NonNull ConstantPool constantPool ) throws IOException {
		this.name = (String)constantPool.get( input.readUnsignedShort() );
		this.info = new byte[input.readInt()];
		input.readFully( this.info );
	}

	public String getName() {
		return name;
	}

	public byte[] getData() {
		return info;
	}

	public DataInputStream getDataInputStream(){
		return new DataInputStream( new ByteArrayInputStream( info ) );
	}
}
