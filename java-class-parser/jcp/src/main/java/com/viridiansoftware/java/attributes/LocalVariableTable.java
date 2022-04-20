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

import com.viridiansoftware.java.constants.ConstantPool;
import lombok.NonNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalVariableTable {
    private final int maxLocals;
    private final ConstantPool constantPool;
    private final List<LocalVariable> table;
    private int count;

    /**
     * Create a new instance of the code attribute "LocalVariableTable".
     * 
     * @param maxLocals
     *            the count of local variables in the memory
     * @param constantPool
     *            Reference to the current ConstantPool
     */
    public LocalVariableTable( int maxLocals, ConstantPool constantPool ) {
        this.maxLocals = maxLocals;
        table = new ArrayList(maxLocals + 1);
        this.constantPool = constantPool;
    }

    /**
     * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.13
     * http://docs.oracle.com/javase/specs/jvms/se8/html/ClassFile.doc.html#5956
     *
     * @param input
     *            the stream of the class
     * @throws IOException
     *             if any I/O error occurs.
     */
    void read( DataInputStream input) throws IOException {
        count = input.readUnsignedShort();
        for( int i = 0; i < count; i++ ) {
            LocalVariable var = new LocalVariable( input, i, constantPool );
            int idx = var.getIndex();
            table.add(var);
        }
    }

    /**
     * Get the LocalVariable with its memory location (slot). The index has empty places with double and long variables.
     *
     * @param idx
     *            the index in the memory
     * @return the LocalVariable
     */
    @NonNull
    public LocalVariable getByEntryIndex( int idx ) {
        return table.get(idx);
    }

    public int getTotalEntires() {
        return table.size();
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    /**
     * Find a LocalVariable with a given name.
     *
     * @param name
     *            needed for evaluate the name.
     * @return the LocalVariable or null
     */
    public LocalVariable get( String name ) {
        if(name == null) {
            return null;
        }
        for( int i=0; i< table.size(); i++ ){
            if( name.equals( table.get(i).getName() )) {
                return table.get(i);
            }
        }
        return null;
    }
}
