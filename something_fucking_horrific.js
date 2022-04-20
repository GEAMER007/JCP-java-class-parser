var { ConstantClass, ConstantFieldRef, ConstantMethodRef, ConstantInterfaceMethodRef, ConstantNameAndType, ConstantMethodHandle, ConstantMethodType, ConstantDynamic, ConstantInvokeDynamic,GetUInt16BE, ConstantStringRef } =require("./utils");


module.exports=
	(pool,count,majorVersion)=>{
var repeat;
		do {
			repeat = false;
			for (var i = 0; i < count; i++) {
				if (pool[i] instanceof Array) {
					var data =  pool[i];
					switch (data[0]) {
					case 7: //CONSTANT_Class
						pool[i] =new ConstantClass(pool[data[1]]) //new ConstantClass( pool[data[1]]);
						break;
					case 8: //CONSTANT_String
						pool[i] =new ConstantStringRef(pool[data[1]]) //pool[data[1]];
						break;
					case 9: //CONSTANT_Fieldref
						if (pool[data[1]] instanceof Array || pool[data[2]] instanceof Array) {
							repeat = true;
						} else {
							pool[i] =new ConstantFieldRef(pool[data[1]], pool[data[2]]); //{constClass:pool[data[1]],nameAndType:pool[data[2]]}//new ConstantFieldRef(pool[data[1]], pool[data[2]]);
						}
						break;
					case 10: //CONSTANT_Methodref
						if (pool[data[1]] instanceof Array || pool[data[2]] instanceof Array) {
							repeat = true;
						} else {
							pool[i] = new ConstantMethodRef(pool[data[1]],  pool[data[2]]);//{constClass:pool[data[1]],nameAndType:pool[data[2]]}//new ConstantMethodRef(pool[data[1]],  pool[data[2]]);
						}
						break;
					case 11: //CONSTANT_InterfaceMethodref
						if (pool[data[1]] instanceof Array || pool[data[2]] instanceof Array) {
							repeat = true;
						} else {
							pool[i] =new ConstantInterfaceMethodRef( pool[data[1]], pool[data[2]]);//{constClass:pool[data[1]],nameAndType:pool[data[2]]}// new ConstantInterfaceMethodRef( pool[data[1]], pool[data[2]]);
						}
						break;
					case 12: //CONSTANT_NameAndType
						pool[i] =new ConstantNameAndType(pool[data[1]],  pool[data[2]]);//{name:''+pool[data[1]],type:''+pool[data[2]]} //new ConstantNameAndType(pool[data[1]],  pool[data[2]]);
						break;
					case 15: //CONSTANT_MethodHandle
						if (pool[data[2]] instanceof Array) {
							repeat = true;
						} else {
							switch (data[1]) {
							case 1: //REF_getField
							case 2: //REF_getStatic
							case 3: //REF_putField
							case 4: //REF_putStatic
								if (pool[data[2]] instanceof ConstantFieldRef) {
									pool[i] = new ConstantMethodHandle(data[1],  pool[data[2]]);
								} else {
									throw new Error("Expected " + ConstantFieldRef.class.getSimpleName() +
											" in constant pool index " + data[2] + " but found " + pool[data[2]].getClass().getSimpleName());
								}
								break;
							case 5: //REF_invokeVirtual
							case 8: //REF_newInvokeSpecial
								if (pool[data[2]] instanceof ConstantMethodRef) {
									pool[i] = new ConstantMethodHandle(data[1],  pool[data[2]]);
								} else {
									throw new IOException("Expected " + ConstantFieldRef.class.getSimpleName() +
											" in constant pool index " + data[2] + " but found " + pool[data[2]].getClass().getSimpleName());
								}
								break;
							case 6: //REF_invokeStatic
							case 7: //REF_invokeSpecial
								if(majorVersion < 52) {
									if (pool[data[2]] instanceof ConstantMethodRef) {
										pool[i] = new ConstantMethodHandle(data[1],  pool[data[2]]);
									} else {
										throw new IOException("Expected " + ConstantMethodRef.class.getSimpleName() +
												" in constant pool index " + data[2] + " but found " + pool[data[2]].getClass().getSimpleName());
									}
								} else {
									if (pool[data[2]] instanceof ConstantMethodRef) {
										pool[i] = new ConstantMethodHandle(data[1],  pool[data[2]]);
									} else if (pool[data[2]] instanceof ConstantInterfaceMethodRef) {
										pool[i] = new ConstantMethodHandle(data[1],  pool[data[2]]);
									} else {
										throw new IOException("Expected " + ConstantMethodRef.class.getSimpleName() +
												" or " + ConstantInterfaceMethodRef.class.getSimpleName() +
												" in constant pool index " + data[2] + " but found " + pool[data[2]].getClass().getSimpleName());
									}
								}
								break;
							case 9: //REF_invokeInterface
								if (pool[data[2]] instanceof ConstantInterfaceMethodRef) {
									pool[i] = new ConstantMethodHandle(data[1], pool[data[2]]);
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
						pool[i] = new ConstantMethodType( pool[data[1]]);
						break;
					case 17: //CONSTANT_Dynamic
						if (pool[data[2]] instanceof Array) {
							repeat = true;
						} else {
							pool[i] = new ConstantDynamic(data[1],  pool[data[2]]);
						}
						break;
					case 18: //CONSTANT_InvokeDynamic
						if (pool[data[2]] instanceof Array) {
							repeat = true;
						} else {
							pool[i] = new ConstantInvokeDynamic(data[1],  pool[data[2]]);
						}
						break;
					case 19: //CONSTANT_Module_info
						pool[i] = pool[data[1]];
						break;
					case 20: //CONSTANT_Package_info
						pool[i] = pool[data[1]];
						break;
					default:
						throw new Error("Unknown constant pool type: " + data[0]);
					}
				}
			}
		} while (repeat);
return pool
}
