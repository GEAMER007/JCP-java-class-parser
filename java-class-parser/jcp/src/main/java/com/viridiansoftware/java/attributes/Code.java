/*******************************************************************************
 * Copyright 2019 Volker Berlin (i-net software)
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

import com.viridiansoftware.java.CodeInputStream;
import com.viridiansoftware.java.constants.ConstantClass;
import com.viridiansoftware.java.constants.ConstantMethodRef;
import com.viridiansoftware.java.constants.ConstantPool;
import lombok.NonNull;

import java.io.DataInputStream;
import java.io.IOException;

public class Code {

    private final ConstantPool constantPool;
    private final BootstrapMethods bootstrapMethods;
    private final int maxStack;
    private final int maxLocals;
    private final byte[] codeData;
    private final TryCatchFinally[] exceptionTable;
    private final Attributes attributes;
    private LineNumberTable lineNumberTable;
    private LocalVariableTable localVariableTable;

    /**
     * The code of a method attribute.
     * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.3
     * http://docs.oracle.com/javase/specs/jvms/se8/html/ClassFile.doc.html#1546
     *
     * @param input
     * @param constantPool
     * @throws IOException
     */
    public Code( DataInputStream input, @NonNull ConstantPool constantPool, BootstrapMethods bootstrapMethods) throws IOException {
        this.constantPool = constantPool;
        this.bootstrapMethods = bootstrapMethods;
        maxStack = input.readUnsignedShort(); //max_stack
        maxLocals = input.readUnsignedShort(); //max_locals;
        codeData = new byte[input.readInt()];
        input.readFully( codeData );

        exceptionTable = new TryCatchFinally[input.readUnsignedShort()];
        for( int i = 0; i < exceptionTable.length; i++ ) {
            exceptionTable[i] = new TryCatchFinally( input, constantPool );
        }
        attributes = new Attributes( input, constantPool );
    }

    public Code() {
        constantPool = null;
        bootstrapMethods = null;
        maxStack = 0;
        maxLocals = 0;
        codeData = new byte[0];
        exceptionTable = new TryCatchFinally[0];
        attributes = null;
        lineNumberTable = null;
        localVariableTable = null;
    }

    @NonNull
    public ConstantPool getConstantPool(){
        return constantPool;
    }

    @NonNull
    public TryCatchFinally[] getExceptionTable() {
        return exceptionTable;
    }

	public BootstrapMethods getBootstrapMethods() {
		return bootstrapMethods;
	}

	public LineNumberTable getLineNumberTable() throws IOException {
        if( lineNumberTable != null ){
            return lineNumberTable;
        }
        AttributeInfo data = attributes.get( "LineNumberTable" );
        if( data != null ) {
            lineNumberTable = new LineNumberTable( data.getDataInputStream() );
        }
        return lineNumberTable;
    }

    public LocalVariableTable getLocalVariableTable() throws IOException{
        if( localVariableTable != null ){
            return localVariableTable;
        }
        AttributeInfo data = attributes.get( "LocalVariableTable" );
        if( data != null ) {
            localVariableTable = new LocalVariableTable( maxLocals, constantPool );
            localVariableTable.read( data.getDataInputStream() );
        }
        return localVariableTable;
    }

    public int getFirstLineNr() throws IOException {
        LineNumberTable table = getLineNumberTable();
        if( table == null ){
            return -1;
        } else {
            return table.getMinLineNr();
        }
    }

    public int getLastLineNr() throws IOException {
        LineNumberTable table = getLineNumberTable();
        if( table == null ){
            return -1;
        } else {
            return table.getMaxLineNr();
        }
    }

	/**
     * Get the stream of Java Byte code instruction of this method.  
     * 
     * @return the stream
     */
    public CodeInputStream getByteCode() {
        return new CodeInputStream( codeData, 0, codeData.length, this );
    }

    public int getCodeSize(){
        return codeData.length;
    }

    public boolean startWithSuperInit( ConstantClass superClass ){
        if( codeData.length >= 4 && codeData[0] == 0x2a && codeData[1] == (byte)0xb7) {
            int idx = ((codeData[2] & 0xFF << 8)) + (codeData[3] & 0xFF);
            ConstantMethodRef method = (ConstantMethodRef)constantPool.get( idx );
            return method.getConstantClass() == superClass;
        }
        return false;
    }

    /**
     * Check if the code only contains the default constructor code:
     * <pre><code>
     * super.&lt;init&gt;;
     * return;
     * </code></pre>
     * In this case the constructor will not be printed
     * @param superClass
     * @return
     */
    public boolean isSuperInitReturn( ConstantClass superClass ) {
        if( codeData.length == 5 && codeData[0] == 0x2a && codeData[1] == (byte)0xb7 && codeData[4] == (byte)0xb1 ) {
            int idx = ((codeData[2] & 0xFF << 8)) + (codeData[3] & 0xFF);
            ConstantMethodRef method = (ConstantMethodRef)constantPool.get( idx );
            return method.getConstantClass() == superClass;
        }
        return false;
    }

    public byte[] getCodeData() {
        return codeData;
    }

	public int getMaxStack() {
		return maxStack;
	}

	public int getMaxLocals() {
		return maxLocals;
	}
}
