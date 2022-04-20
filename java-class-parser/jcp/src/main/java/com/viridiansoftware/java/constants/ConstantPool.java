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
package com.viridiansoftware.java.constants;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConstantPool {

	private final Object[] constantPool;

	/**
	 * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4
	 * http://docs.oracle.com/javase/specs/jvms/se8/html/ClassFile.doc.html#20080
	 *
	 * @param input the stream of the class
	 * @throws IOException if any IO error occur
	 */
	public ConstantPool(int majorVersion, int minorVersion, DataInputStream input) throws IOException {
		int count = input.readUnsignedShort();
		Object[] pool = constantPool = new Object[count];
		for (int i = 1; i < count; i++) {
			byte type = input.readByte();
			switch (type) {
			case 1: //CONSTANT_Utf8
				pool[i] = input.readUTF();
				break;
			case 3: //CONSTANT_Integer
				pool[i] = new Integer(input.readInt());
				break;
			case 4: //CONSTANT_Float
				pool[i] = new Float(input.readFloat());
				break;
			case 5: //CONSTANT_Long
				pool[i] = new Long(input.readLong());
				i++;
				break;
			case 6: //CONSTANT_Double
				pool[i] = new Double(input.readDouble());
				i++;
				break;
			case 7: //CONSTANT_Class
			case 8: //CONSTANT_String
			case 16: //CONSTANT_MethodType
			case 19: //CONSTANT_Module_info
			case 20: //CONSTANT_Package_info
				pool[i] = new int[]{type, input.readUnsignedShort()};
				break;
			case 9: //CONSTANT_Fieldref
			case 10: //CONSTANT_Methodref
			case 11: //CONSTANT_InterfaceMethodref
			case 12: //CONSTANT_NameAndType
			case 17: //CONSTANT_Dynamic
			case 18: //CONSTANT_InvokeDynamic
				pool[i] = new int[]{type, input.readUnsignedShort(), input.readUnsignedShort()};
				break;
			case 15: //CONSTANT_MethodHandle
				pool[i] = new int[]{type, input.readByte(), input.readUnsignedShort()};
				break;
			default:
				throw new IOException("Unknown constant pool type: " + type);
			}
		}

		boolean repeat;
		do {
			repeat = false;
			for (int i = 0; i < count; i++) {
				if (pool[i] instanceof int[]) {
					int[] data = (int[]) pool[i];
					switch (data[0]) {
					case 7: //CONSTANT_Class
						pool[i] = new ConstantClass((String) pool[data[1]]);
						break;
					case 8: //CONSTANT_String
						pool[i] = pool[data[1]];
						break;
					case 9: //CONSTANT_Fieldref
						if (pool[data[1]] instanceof int[] || pool[data[2]] instanceof int[]) {
							repeat = true;
						} else {
							pool[i] = new ConstantFieldRef((ConstantClass) pool[data[1]], (ConstantNameAndType) pool[data[2]]);
						}
						break;
					case 10: //CONSTANT_Methodref
						if (pool[data[1]] instanceof int[] || pool[data[2]] instanceof int[]) {
							repeat = true;
						} else {
							pool[i] = new ConstantMethodRef((ConstantClass) pool[data[1]], (ConstantNameAndType) pool[data[2]]);
						}
						break;
					case 11: //CONSTANT_InterfaceMethodref
						if (pool[data[1]] instanceof int[] || pool[data[2]] instanceof int[]) {
							repeat = true;
						} else {
							pool[i] = new ConstantInterfaceMethodRef((ConstantClass) pool[data[1]], (ConstantNameAndType) pool[data[2]]);
						}
						break;
					case 12: //CONSTANT_NameAndType
						pool[i] = new ConstantNameAndType((String) pool[data[1]], (String) pool[data[2]]);
						break;
					case 15: //CONSTANT_MethodHandle
						if (pool[data[2]] instanceof int[]) {
							repeat = true;
						} else {
							switch (data[1]) {
							case 1: //REF_getField
							case 2: //REF_getStatic
							case 3: //REF_putField
							case 4: //REF_putStatic
								if (pool[data[2]] instanceof ConstantFieldRef) {
									pool[i] = new ConstantMethodHandle(data[1], (ConstantFieldRef) pool[data[2]]);
								} else {
									throw new IOException("Expected " + ConstantFieldRef.class.getSimpleName() +
											" in constant pool index " + data[2] + " but found " + pool[data[2]].getClass().getSimpleName());
								}
								break;
							case 5: //REF_invokeVirtual
							case 8: //REF_newInvokeSpecial
								if (pool[data[2]] instanceof ConstantMethodRef) {
									pool[i] = new ConstantMethodHandle(data[1], (ConstantMethodRef) pool[data[2]]);
								} else {
									throw new IOException("Expected " + ConstantFieldRef.class.getSimpleName() +
											" in constant pool index " + data[2] + " but found " + pool[data[2]].getClass().getSimpleName());
								}
								break;
							case 6: //REF_invokeStatic
							case 7: //REF_invokeSpecial
								if(majorVersion < 52) {
									if (pool[data[2]] instanceof ConstantMethodRef) {
										pool[i] = new ConstantMethodHandle(data[1], (ConstantMethodRef) pool[data[2]]);
									} else {
										throw new IOException("Expected " + ConstantMethodRef.class.getSimpleName() +
												" in constant pool index " + data[2] + " but found " + pool[data[2]].getClass().getSimpleName());
									}
								} else {
									if (pool[data[2]] instanceof ConstantMethodRef) {
										pool[i] = new ConstantMethodHandle(data[1], (ConstantMethodRef) pool[data[2]]);
									} else if (pool[data[2]] instanceof ConstantInterfaceMethodRef) {
										pool[i] = new ConstantMethodHandle(data[1], (ConstantInterfaceMethodRef) pool[data[2]]);
									} else {
										throw new IOException("Expected " + ConstantMethodRef.class.getSimpleName() +
												" or " + ConstantInterfaceMethodRef.class.getSimpleName() +
												" in constant pool index " + data[2] + " but found " + pool[data[2]].getClass().getSimpleName());
									}
								}
								break;
							case 9: //REF_invokeInterface
								if (pool[data[2]] instanceof ConstantInterfaceMethodRef) {
									pool[i] = new ConstantMethodHandle(data[1], (ConstantInterfaceMethodRef) pool[data[2]]);
								} else {
									throw new IOException("Expected " + ConstantInterfaceMethodRef.class.getSimpleName() +
											" in constant pool index " + data[2] + " but found " + pool[data[2]].getClass().getSimpleName());
								}
								break;
							default:
								throw new IOException("Unknown method handle reference kind: " + data[1]);
							}
						}
						break;
					case 16: //CONSTANT_MethodType
						pool[i] = new ConstantMethodType((String) pool[data[1]]);
						break;
					case 17: //CONSTANT_Dynamic
						if (pool[data[2]] instanceof int[]) {
							repeat = true;
						} else {
							pool[i] = new ConstantDynamic(data[1], (ConstantNameAndType) pool[data[2]]);
						}
						break;
					case 18: //CONSTANT_InvokeDynamic
						if (pool[data[2]] instanceof int[]) {
							repeat = true;
						} else {
							pool[i] = new ConstantInvokeDynamic(data[1], (ConstantNameAndType) pool[data[2]]);
						}
						break;
					case 19: //CONSTANT_Module_info
						pool[i] = pool[data[1]];
						break;
					case 20: //CONSTANT_Package_info
						pool[i] = pool[data[1]];
						break;
					default:
						throw new IOException("Unknown constant pool type: " + data[0]);
					}
				}
			}
		} while (repeat);
	}

	public ConstantPool(int size) {
		constantPool = new Object[size];
	}

	public Object get(int index) {
		return constantPool[index];
	}

	public void set(int index, Object obj) {
		constantPool[index] = obj;
	}

	public int length() {
		return constantPool.length;
	}

	public List<ConstantClass> getConstantClasses() {
		final List<ConstantClass> result = new ArrayList<ConstantClass>();
		for (int i = 0; i < constantPool.length; i++) {
			if (constantPool[i] instanceof ConstantClass) {
				result.add((ConstantClass) constantPool[i]);
			}
		}
		return result;
	}

	public List<String> getConstantFieldDescriptors() {
		final List<String> result = new ArrayList<String>();
		for (int i = 0; i < constantPool.length; i++) {
			if (constantPool[i] instanceof ConstantFieldRef) {
				result.add(((ConstantFieldRef) constantPool[i]).getType());
			}
		}
		return result;
	}

	public List<String> getConstantMethodDescriptors() {
		final List<String> result = new ArrayList<String>();
		for (int i = 0; i < constantPool.length; i++) {
			if (constantPool[i] instanceof ConstantMethodRef) {
				result.add(((ConstantMethodRef) constantPool[i]).getType());
			} else if (constantPool[i] instanceof ConstantInterfaceMethodRef) {
				result.add(((ConstantInterfaceMethodRef) constantPool[i]).getType());
			}
		}
		return result;
	}

	public List<ConstantRef> getConstantMethodRefs() {
		final List<ConstantRef> result = new ArrayList<ConstantRef>();
		for (int i = 0; i < constantPool.length; i++) {
			if (constantPool[i] instanceof ConstantMethodRef) {
				result.add(((ConstantMethodRef) constantPool[i]));
			} else if (constantPool[i] instanceof ConstantInterfaceMethodRef) {
				result.add(((ConstantInterfaceMethodRef) constantPool[i]));
			}
		}
		return result;
	}
}
