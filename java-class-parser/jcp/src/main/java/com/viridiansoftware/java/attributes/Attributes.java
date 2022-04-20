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

public class Attributes {

    private final AttributeInfo[] attributes;

    private final ConstantPool constantPool;

    public Attributes(@NonNull DataInputStream input, @NonNull ConstantPool constantPool ) throws IOException {
        this.constantPool = constantPool;
        this.attributes = readAttributs( input );
    }

    private AttributeInfo[] readAttributs( @NonNull DataInputStream input ) throws IOException {
        AttributeInfo[] attrs = new AttributeInfo[input.readUnsignedShort()];
        for( int i = 0; i < attrs.length; i++ ) {
            attrs[i] = new AttributeInfo( input, constantPool );
        }
        return attrs;
    }

    public AttributeInfo get(String name) {
        if(name == null) {
            return null;
        }
        for( AttributeInfo attr : attributes ) {
            if( attr.getName().equals( name ) ) {
                return attr;
            }
        }
        return null;
    }

    public void printAttributeNames()
    {
        for( AttributeInfo attr : attributes ) {
            System.out.println(attr.getName());
        }
    }

    /**
     * Get value of SourceFile if available.
     * @return the source file name or null.
     * @throws IOException if an I/O error occurs.
     */
    public String getSourceFile() throws IOException{
        AttributeInfo data = get( "SourceFile" );
        if( data == null ) {
            return null;
        }
        return (String)constantPool.get( data.getDataInputStream().readUnsignedShort() );
    }
}
