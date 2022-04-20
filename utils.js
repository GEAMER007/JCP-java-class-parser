var fs=require('fs');
var typeParsers={
    "String":(value,refpool)=>refpool.push(1,...GetUInt16BE(value.length),...Buffer.from(value)),
    "ConstantMethodRef":(value,refpool,linkpool)=>refpool.push(10,...GetUInt16BE(linkpool.indexOf(value.constClass)),...GetUInt16BE(linkpool.indexOf(value.nameAndType))),
    "ConstantFieldRef":(value,refpool,linkpool)=>refpool.push(9,...GetUInt16BE(linkpool.indexOf(value.constClass)),...GetUInt16BE(linkpool.indexOf(value.nameAndType))),
    "ConstantStringRef":(value,refpool,linkpool)=>refpool.push(8,...GetUInt16BE(linkpool.indexOf(value.pointsAt))),
    "ConstantClass":(value,refpool,linkpool)=>refpool.push(7,...GetUInt16BE(linkpool.indexOf(value.name))),
    "ConstantNameAndType":(value,refpool,linkpool)=>refpool.push(12,...GetUInt16BE(linkpool.indexOf(value.name)),...GetUInt16BE( linkpool.indexOf(value.type))),
    "ConstantMethodHandle":(value,refpool,linkpool)=>refpool.push(15,ReferenceKind.indexOf(value.referenceKind),...GetUInt16BE(linkpool.indexOf(value.reference))),
    "ConstantMethodType":(value,refpool,linkpool)=>refpool.push(16,...GetUInt16BE(linkpool.indexOf(value.descriptor))),
    "ConstantInvokeDynamic":(value,refpool,linkpool)=>refpool.push(18,...GetUInt16BE(value.bootstrapMethodIndex),...GetUInt16BE(linkpool.indexOf(value.nameAndType))),
    "ConstantInterfaceMethodRef":(value,refpool,linkpool)=>refpool.push(11,...GetUInt16BE(linkpool.indexOf(value.constClass)),...GetUInt16BE(linkpool.indexOf(value.nameAndType))),
   }
var ReferenceKind =[
    'REF_NULL',
    'REF_GETFIELD',
    'REF_GETSTATIC',
    'REF_PUTFIELD',
    'REF_PUTSTATIC',
    'REF_INVOKEVIRTUAL',
    'REF_INVOKESTATIC',
    'REF_INVOKESPECIAL',
    'REF_NEWINVOKESPECIAL',
    'REF_INVOKEINTERFACE',
]
class ClassAccessFlag {
	
    static enums={

         PUBLIC:(0x0001),
         PRIVATE:(0x0002),
         PROTECTED:(0x0004),
         STATIC:(0x0008),
        
         FINAL:(0x0010),
         SUPER:(0x0020),

         INTERFACE:(0x0200),
         ABSTRACT:(0x0400),
        
         SYNTHETIC:(0x1000),
         ANNOTATION:(0x2000),
         ENUM:(0x4000),
    }
    
	constructor(mask) {
		this.mask = mask;
	}
}
class FieldsAccessFlag {
	
    static enums={

        PUBLIC:(0x0001),
        PRIVATE:(0x0002),
        PROTECTED:(0x0004),
        STATIC:(0x0008),
    
        FINAL:(0x0010),
        VOLATILE:(0x0040),
        TRANSIENT:(0x0080),
    
        SYNTHETIC:(0x1000),
        ENUM:(0x4000)
    }
    
	constructor(mask) {
		this.mask = mask;
	}
}

class MethodAccessFlag {
	
    static enums={

        PUBLIC:(0x0001),
        PRIVATE:(0x0002),
        PROTECTED:(0x0004),
        STATIC:(0x0008),
    
        FINAL:(0x0010),
        SYNCHRONIZED:(0x0020),
        BRIDGE:(0x0040),
        VARARGS:(0x0080),
        NATIVE:(0x0100),
        ABSTRACT:(0x0400),
        STRICT:(0x0800),
        SYNTHETIC:(0x1000),
    }
    
	constructor(mask) {
		this.mask = mask;
	}
}

class ConstantInvokeDynamic {

    constructor( bootstrapMethodIndex,  nameAndType) {
        this.bootstrapMethodIndex = bootstrapMethodIndex;
        this.nameAndType = nameAndType;
    }
}
class ConstantDynamic {

	constructor( bootstrapMethodIndex,  nameAndType) {
		this.bootstrapMethodIndex = bootstrapMethodIndex;
		this.nameAndType = nameAndType;
	}
}
class ConstantMethodType {

    constructor(descriptor) {
		this.descriptor = descriptor;
}
}
class ConstantNameAndType{
    constructor( name,  type){
        this.name = name;
        this.type = type;
    }
}
class ConstantMethodHandle {
//	private final ReferenceKind referenceKind;
//	private final ConstantRef reference;

	constructor( referenceKind,  reference) {
		this.reference = reference;

		for(var i=0;i<ReferenceKind.length;i++) {
            var refKind=ReferenceKind[i]
			if(i== referenceKind) {
				this.referenceKind = refKind;
				return  this;
			}
		}
		this.referenceKind = ReferenceKind[1];
	}
}
class ConstantRef {
    constructor(  constClass,  nameAndType ) {
        this.constClass = constClass;
        this.nameAndType = nameAndType;
    }
}
class ConstantFieldRef extends ConstantRef {

    constructor(constClass,nameAndType ) {
        super( constClass, nameAndType );
    }

}
class ConstantStringRef {

    constructor( str ) {
        this.pointsAt=str
    }

}
class ConstantClass {

    constructor(name ) {
        this.name = name;
    }
}

class ConstantInterfaceMethodRef extends ConstantRef {

     constructor( constClass,  nameAndType ) {
        super( constClass, nameAndType );
    }
}

class ConstantMethodRef extends ConstantRef {

    constructor( constClass,  nameAndType ) {
       super( constClass, nameAndType );
   }
}
class AttributeInfo {
    constructor(input, constantPool,offset ) {
		this.name = String(constantPool[input.readUInt16BE(offset)]);
        offset+=2
        var atrlen=input.readUInt32BE(offset)
        offset+=4
		this.info = bufCopy(input,offset,offset+atrlen) //new byte[input.readInt()];
        offset+=atrlen
        this.newOffset=offset
		//input.readFully( this.info );
	}
}
class Attributes {

    /*private final AttributeInfo[]*/

    
    constructor(input,  constantPool,offset ){
        this.constantPool = constantPool;
        this.readAttributs=( input )=> {
            var attrs =[]// new AttributeInfo[];
            var len=input.readUInt16BE(offset);
            offset+=2
            for( var i = 0; i < len; i++ ) {
                attrs[i] = new AttributeInfo( input, constantPool,offset );
                offset=attrs[i].newOffset
            }
            return attrs;
        }
        this.attributes = this.readAttributs( input,offset );
        this.newOffset=offset
        this.get=( name) =>{
            if(name == null) {
                return null;
            }
            for( var attr in this.attributes ) {
                if( this.attributes[attr].name == name) {
                    return this.attributes[attr];
                }
            }
            return null;
        }
    }
}
class TryCatchFinally{
    constructor(input, constantPool,offset){
        this.start = input.readUInt16BE(offset);
        this.end = input.readUInt16BE(offset+2);
        this.handler = input.readUInt16BE(offset+4);
        this.type = constantPool[input.readUInt16BE(offset+6)];
    }
}
class FieldInfo{
    constructor( input=Buffer.from([]),offset,  constantPool)  {
        this.accessFlags = input.readUInt16BE(offset);
        this.faf= [new FieldsAccessFlag()]
        this.faf.pop()
        offset+=2
        this.constantPool = constantPool;

        for(var Enum in FieldsAccessFlag.enums) {
            var fieldAccessFlag=FieldsAccessFlag.enums[Enum]
            if((fieldAccessFlag & this.accessFlags) == fieldAccessFlag) {
                this.faf.push(fieldAccessFlag);
            }
        }

        this.name = String(constantPool[input.readUInt16BE(offset)]);
        this.description = String(constantPool[input.readUInt16BE(offset+2)]);
        offset+=4
        this.attributes = new Attributes( input, constantPool,offset );
        //this.description = String(constantPool.get( input.readUnsignedShort() ));
        this.newOffset=this.attributes.newOffset
    }
}
class MethodInfo{
    constructor( input, constantPool, offset) {
        this.accessFlags = input.readUInt16BE(offset)
        this.methodAccessFlags=[]
        offset+=2
        for(var Enum in MethodAccessFlag.enums) {
            var methodAccessFlag=MethodAccessFlag.enums[Enum]
            if((methodAccessFlag & this.accessFlags) == methodAccessFlag) {
                this.methodAccessFlags.push(methodAccessFlag);
            }
        }

        this.name = String(constantPool[input.readUInt16BE(offset)]);
        offset+=2
        this.description = String(constantPool[input.readUInt16BE(offset)]);
        offset+=2
        this.constantPool = constantPool;
        this.attributes = new Attributes( input, constantPool,offset );
        this.CodeAttr=this.attributes.get("Code").info
        this.getAdvanced=()=>{
            var CodeAttrBuf=this.CodeAttr
            var loffset=0
            
            var adv={}
            adv.maxStack=CodeAttrBuf.readUint16BE(loffset)
            adv.max_locals=CodeAttrBuf.readUint16BE(loffset+2)
            loffset+=4
            var bytecodelen=CodeAttrBuf.readUInt32BE(loffset)
            loffset+=4
            adv.bytecode=bufCopy(CodeAttrBuf,loffset,loffset+bytecodelen)
            loffset+=bytecodelen
            var extablelen=CodeAttrBuf.readUInt16BE(loffset)
            loffset+=2
            adv.extable=[]
            for (let i = 0; i < extablelen; i++) {
                adv.extable[i]=new TryCatchFinally(CodeAttrBuf,constantPool,loffset)
                loffset+=8
                
            }
            adv.attributes=new Attributes(CodeAttrBuf,constantPool,loffset)
            var localtblattr=adv.attributes.get("LocalVariableTable")
            if(localtblattr!=null){
                var localtblbuf=localtblattr.info
                var localtable=[]
                var sloffset=0
                var count=localtblbuf.readUInt16BE(sloffset)
                offset+=2
                for(var i=0;i<count;i++){
                    localtable.push({
                        spc:localtblbuf.readUInt16BE(sloffset),
                        len:localtblbuf.readUInt16BE(sloffset+2),
                        desc:constantPool[localtblbuf.readUInt16BE(sloffset+4)],
                        name:constantPool[localtblbuf.readUInt16BE(sloffset+6)],
                        ind:localtblbuf.readUInt16BE(sloffset+8)
                    })
                    sloffset+=10
                }

                adv.localtable={locals:localtable,raw:localtblbuf}
                //adv.locals=localtblbuf
            }
            loffset=adv.attributes.newOffset

            return adv

        }
        this.adv=this.getAdvanced()
        this.newOffset=this.attributes.newOffset
       // this.classFile = classFile;
    }
}
//#region hide
function n32tonum(bytes){
    var bits="0b"
    bytes.forEach(b=>{
      b= b.toString(2)
      while (b.length<8)b=0+b
      bits+=b})
    return bits-0
  }
  function bufCopy(buf,start,end){
    var buffarray=[]
    for(var i=start;i<end;i++)
      if(buf[i]!==undefined)
         buffarray.push(buf[i])
    return Buffer.from(buffarray)
  }

class ByteCode{
    static parseRef(bcr,asMnemonics=false){
        if(!asMnemonics){
        var allinstructions={}
        for(var i=0;i<bcr.length;i++){
            var el=bcr[i]
            allinstructions[el[1]]={
                mnemonic:el[0],
                code:el[1],
                ob:el[2],
                sba:el[3],
                takesargs:el[2].substring(0,el[2].indexOf(':'))-0,
                format:el[2].substring(el[2].indexOf(':')+1,el[2].length)
            }
        }
        return allinstructions
    }
        else{
        var allinstructions={}
        for(var el in ByteCode.instructions){
            allinstructions[ByteCode.instructions[el].mnemonic]=ByteCode.instructions[el].code
        }
        return allinstructions
        }

    }
    static instructions=ByteCode.parseRef(JSON.parse(fs.readFileSync('./bytecodes.json','utf-8')))
    static mnemonicmap=ByteCode.parseRef(null,true)
    static types={
        "byte":[1,b=>b[0],b=>[b]],
        "short":[2,b=>b.readUInt16BE(0),b=>{var bf=Buffer.from([0,0]);bf.writeUint16BE(b);return bf}],
        "int":[4,b=>b.readUInt32BE(0),b=>{var bf=Buffer.from([0,0,0,0]);bf.writeUint32BE(b);return bf}],
        "cps":[2,(b,cpool)=>cpool[b.readUInt16BE(0)],b=>{var bf=Buffer.from([0,0]);bf.writeUint16BE(b.replace('#','')-0);return bf;}],
        "cpb":[1,(b,cpool)=>
            cpool[b[0]],b=>[b.replace('#','')-0]]
        
    }
    static parseArgsByTypes(buffer=Buffer.from([]),instruction,cpool){
        
        if(instruction.takesargs>0){
        var argtypes=instruction.format.split(',')
        var allargs=[]
        var offset=0
        argtypes.forEach(t=>{
            var ct=ByteCode.types[t]
            if(!ct)return
            allargs.push({value:ct[1](bufCopy(buffer,offset,offset+ct[0]),cpool),fromcpool:t.startsWith('cp')})
            offset+=ct[0]


        })
        return allargs
    }
        else return [] 
    }
    static compileArgsByTypes(args=[],instruction){
        
        if(instruction.takesargs>0){
        var argtypes=instruction.format.split(',')
        var argbuffer=[]
        argtypes.forEach((t,i)=>{
            var ct=ByteCode.types[t]
            if(!ct)return
            argbuffer.push(...ct[2](args[i]))
            //allargs.push({value:ct[1](bufCopy(buffer,offset,offset+ct[0]),cpool),fromcpool:t=='cps'})
        


        })
        return Buffer.from(argbuffer)
    }
        else return Buffer.from([]) 
    }
    static parse(rawbytecode=Buffer.from([]),cpool){
        var allinstructions=[]
        var offset=0
        while (offset<rawbytecode.length){
            var curinstruction= ByteCode.instructions[rawbytecode[offset]]
            var instructionAddress=offset
            offset++
            var argbuffer=bufCopy(rawbytecode,offset,offset+curinstruction.takesargs)
            offset+=curinstruction.takesargs
            var args=ByteCode.parseArgsByTypes(argbuffer,curinstruction,cpool)
            allinstructions.push({mnemonic:curinstruction.mnemonic,code:curinstruction.code,args:args,instructionAddress:instructionAddress})

        }
        return new ByteCode(allinstructions,cpool)
    }
    static compile (sourcecode=''){
        var strinstructions=sourcecode.split('\n')
        var instructions=[]
        var codebuffer=[]
        for(let i=0;i<strinstructions.length;i++){
            if(strinstructions[i]==''||strinstructions[i].startsWith('//'))continue
            var splitted=strinstructions[i].split(' ')
            var [instructionAddress,instruction]=[];
            var args=[]
            for(var kw=0;kw<splitted.length;kw++){
                if(splitted[kw].startsWith("//"))break
                if(kw==0)
                [instructionAddress,instruction]= splitted[0].split(':\t')
                else{
                    if(splitted[kw]=='')continue
                    args.push(splitted[kw])
                }
            }
            instructions.push({instructionAddress,instruction,args})
        }
        for(var ins in instructions){
            var inst=instructions[ins]
            var insref= ByteCode.instructions[ByteCode.mnemonicmap[inst.instruction]]
            if(!insref){throw new Error("instruction"+inst.instruction+" does not exist")}
            var argbuf= ByteCode.compileArgsByTypes(inst.args,insref)
            codebuffer.push(insref.code,...argbuf)
        }
        return Buffer.from(codebuffer)

    }
    constructor(instructionarray,cpool){
        this.disassemble=()=>{
            var jasmstr=''
            // jasmstr+="ConstantPool:\n"
            // cpool.forEach((v,i)=>{jasmstr+=`\t${i}:\t${typeof v=='object'?JSON.stringify(v):v}\n`})
            // jasmstr+='Code:\n'
            instructionarray.forEach(el=>{
                var argstr=''
                var comment=''
                el.args.forEach((arg,i)=>{
                    argstr+=`${arg.fromcpool?`#${cpool.indexOf(arg.value)}`:arg.value} `
                    arg.fromcpool?comment+=`\n// arg №${i+1}: type: ${arg.value.constructor.name} ${arg.value instanceof ConstantStringRef?`value: ${arg.value.pointsAt}`:('name: '+ arg.value.nameAndType?arg.value.nameAndType.name+"::"+arg.value.nameAndType.type:arg.value.name)}  `:1
                })
                argstr+=comment
                jasmstr+=`${el.instructionAddress}:\t${el.mnemonic} ${argstr}\n`
            })
            return jasmstr
        }
        this.AsArray=()=>instructionarray
    }
}
function GetUInt16BE(num){
    var buf=Buffer.from([0,0])
    buf.writeUInt16BE(num)
    return buf
}
function GetUInt32BE(num){
    var buf=Buffer.from([0,0,0,0])
    buf.writeUInt32BE(num)
    return buf
}
function serializeConstantPool(linked_cpool){
    var reference_cpool=[]
for(var i in linked_cpool){
	var value=linked_cpool[i]
        var type=value.constructor.name
		try{
			typeParsers[type](value,reference_cpool,linked_cpool)
		}catch(er){
            console.log(type);
			console.log(reference_cpool)
            console.log(`constant_pool[${i}]`)
			throw er
		}
		
	

}
return reference_cpool
}
function generateFlagMask(wantedFlags,prop){
    if(prop!==false)
        for(var wf=0;wf< wantedFlags.length;wf++)wantedFlags[wf]=wantedFlags[wf][prop]
        
    
        wantedFlags=wantedFlags.join(';')
    var mask=0
    while(true){
        var classAccessFlags=[]
        for(var Enum in ClassAccessFlag.enums) {
            var classAccessFlag=ClassAccessFlag.enums[Enum]
             if((classAccessFlag & mask) == classAccessFlag) {
             classAccessFlags.push(classAccessFlag);
            }
        }
        if(classAccessFlags.join(';')==wantedFlags){
            break
        } else mask++
    }
    return mask
}
//#endregion hide
class ClassFile{
    constructor(methods=[new MethodInfo()],fields=[new FieldInfo()],cpool=[],interfaces=[],majorv=60,minorv=0,super_class='',this_class='',accessFlags=[1],attributes=new Attributes()){
        this.minor_version=minorv
        this.major_version=majorv
        this.constant_pool=cpool
        this.access_flags=accessFlags
        this.this_class=this_class
        this.super_class=super_class
        this.interfaces=interfaces
        this.fields=fields
        this.methods=methods
        this.attributes=attributes
    }
    static AddMethodRef(classInstance=new ClassFile(),className,methodName,returntype){
        var cc=new ConstantClass(className)
        var cnat=new ConstantNameAndType(methodName,returntype)
        var cmr=new ConstantMethodRef(cc,cnat)
        var cpoolImportsWithDependencies=[
            className,
            methodName,
            returntype,
            cc,
            cnat,
            
        ]
        for(var cpoolEntry in cpoolImportsWithDependencies)
            if(!classInstance.constant_pool.includes(cpoolImportsWithDependencies[cpoolEntry]))classInstance.constant_pool.push(cpoolImportsWithDependencies[cpoolEntry])
        return classInstance.constant_pool.push(cmr)-1
     }
    static GetMethodCode(classInstance=new ClassFile(),methodID=0){
        return `// Class:${classInstance.this_class.name}\n// Method:${classInstance.methods[methodID].name}\n// (...argument types;)Return type: ${classInstance.methods[methodID].description}\n// Code:\n`+ByteCode.parse(classInstance.methods[methodID].adv.bytecode,classInstance.constant_pool).disassemble()}
    static SetMethodCode(classInstance=new ClassFile(),methodID=0,uncompiledCode=''){
        var compiled=ByteCode.compile(uncompiledCode)
        var mth=classInstance.methods[methodID]
        var catrID=mth.attributes.attributes.indexOf(classInstance.methods[methodID].attributes.get("Code"))
        var cainfo=[]
        var aextjnd=[]
        for(var i=0;i<mth.adv.extable.length;i++){
            var ex=mth.adv.extable[i]
            aextjnd.push(...GetUInt16BE(ex.start),...GetUInt16BE(ex.end),...GetUInt16BE(ex.handler),...GetUInt16BE(classInstance.constant_pool.indexOf(ex.type)))
        }
        cainfo.push(...GetUInt16BE(mth.adv.maxStack),...GetUInt16BE(mth.adv.max_locals),...GetUInt32BE(compiled.length),...compiled,...GetUInt16BE(mth.adv.extable.length),...aextjnd,...GetUInt16BE(mth.attributes.attributes.length))
        for(var i = 0;i<mth.attributes.attributes.length;i++){
            cainfo.push(...GetUInt16BE(classInstance.constant_pool.indexOf(mth.attributes.attributes[i].name)))
            cainfo.push(...GetUInt32BE(mth.attributes.attributes[i].info.length))
            cainfo.push(...mth.attributes.attributes[i].info)
            
        }
        classInstance.methods[methodID].attributes.attributes[catrID].info=Buffer.from(cainfo)
        
        classInstance.methods[methodID].adv.bytecode=compiled

    }
    
    static compile(classInstance=new ClassFile()){
        // #region main
        var classbuffer=[1]
        classbuffer.pop()
        classbuffer.push(0xCA,0xFE,0xBA,0xBE)
        
        classbuffer.push(...GetUInt16BE(classInstance.minor_version))
        classbuffer.push(...GetUInt16BE(classInstance.major_version))
        
        classbuffer.push(...GetUInt16BE(classInstance.constant_pool.length))
        classbuffer.push(...serializeConstantPool(classInstance.constant_pool))

        classbuffer.push(...GetUInt16BE(generateFlagMask(classInstance.access_flags,false)))
        
        classbuffer.push(...GetUInt16BE(classInstance.constant_pool.indexOf(classInstance.this_class)))
        classbuffer.push(...GetUInt16BE(classInstance.constant_pool.indexOf(classInstance.super_class)))

        classbuffer.push(...GetUInt16BE(classInstance.interfaces.length))
        // #endregion main
        for(var i=0;i<classInstance.interfaces.length;i++)
        classbuffer.push(...GetUInt16BE(classInstance.constant_pool.indexOf(classInstance.interfaces[i])))
        
        classbuffer.push(...GetUInt16BE(classInstance.fields.length))
        for(var i=0;i<classInstance.fields.length;i++){
        classbuffer.push(...GetUInt16BE(generateFlagMask(classInstance.fields[i].faf,false)))
        classbuffer.push(...GetUInt16BE(classInstance.constant_pool.indexOf(classInstance.fields[i].name)))
        classbuffer.push(...GetUInt16BE(classInstance.constant_pool.indexOf(classInstance.fields[i].description)))
        classbuffer.push(...GetUInt16BE(classInstance.fields[i].attributes.attributes.length))
        for(var ia =0;ia<classInstance.fields[i].attributes.attributes.length;ia++){
            classbuffer.push(...GetUInt16BE(classInstance.constant_pool.indexOf(classInstance.fields[i].attributes.attributes[ia].name)))
            classbuffer.push(...GetUInt16BE(classInstance.fields[i].attributes.attributes[ia].info.length))
            classbuffer.push(...classInstance.fields[i].attributes.attributes[ia].info)
            
        }
    }
        classbuffer.push(...GetUInt16BE(classInstance.methods.length))
        for(var i=0;i<classInstance.methods.length;i++){
        classbuffer.push(...GetUInt16BE(generateFlagMask(classInstance.methods[i].methodAccessFlags,false)))
        classbuffer.push(...GetUInt16BE(classInstance.constant_pool.indexOf(classInstance.methods[i].name)))
        classbuffer.push(...GetUInt16BE(classInstance.constant_pool.indexOf(classInstance.methods[i].description)))
        classbuffer.push(...GetUInt16BE(classInstance.methods[i].attributes.attributes.length))
        for(var ia =0;ia<classInstance.methods[i].attributes.attributes.length;ia++){
            classbuffer.push(...GetUInt16BE(classInstance.constant_pool.indexOf(classInstance.methods[i].attributes.attributes[ia].name)))
            classbuffer.push(...GetUInt32BE(classInstance.methods[i].attributes.attributes[ia].info.length))
            classbuffer.push(...classInstance.methods[i].attributes.attributes[ia].info)
            
        }
        }

        classbuffer.push(...GetUInt16BE(classInstance.attributes.attributes.length))
        for(var i = 0;i<classInstance.attributes.attributes.length;i++){
            classbuffer.push(...GetUInt16BE(classInstance.constant_pool.indexOf(classInstance.attributes.attributes[i].name)))
            classbuffer.push(...GetUInt32BE(classInstance.attributes.attributes[i].info.length))
            classbuffer.push(...classInstance.attributes.attributes[i].info)
            
        }

    
    
        //classbuffer.push()
        return Buffer.from(classbuffer)
        
    }
    static importMethod(classInstance,exporterClass,methodName){

        var exporter=exporterClass
        var importedMethod
        var cpoolImports=[]
        var cpoolImportsWithDependencies=[]
        
        exporter.methods.forEach(m=>m.name==methodName?importedMethod=m:1)
        importedMethod.attributes.attributes.forEach(attr=>cpoolImportsWithDependencies.push(attr.name))
        var methodCodeDissasembly=ByteCode.parse(importedMethod.adv.bytecode,exporter.constant_pool).AsArray()
        methodCodeDissasembly.forEach(line=>line.args.forEach(arg=>arg.fromcpool?cpoolImports.push(arg.value):1))
        for(var cpi in cpoolImports)
        cpoolImportsWithDependencies.push(...ClassFile.GetConstantDependencies(exporter.constant_pool,cpoolImports[cpi]).reverse(),cpoolImports[cpi])
        cpoolImports=null
        for(var cpoolEntry in cpoolImportsWithDependencies)
            if(!classInstance.constant_pool.includes(cpoolImportsWithDependencies[cpoolEntry]))classInstance.constant_pool.push(cpoolImportsWithDependencies[cpoolEntry])
            for(var tcf in importedMethod.adv.extable){
                if(!classInstance.constant_pool.includes(importedMethod.adv.extable[tcf].type))classInstance.constant_pool.push(importedMethod.adv.extable[tcf].type)
                if(!classInstance.constant_pool.includes(importedMethod.adv.extable[tcf].type.name))classInstance.constant_pool.push(importedMethod.adv.extable[tcf].type.name)
        }
        
        methodCodeDissasembly=new ByteCode(methodCodeDissasembly,classInstance.constant_pool)
        var methodIndex=classInstance.methods.push(importedMethod)-1
        if(!classInstance.constant_pool.includes(importedMethod.description))classInstance.constant_pool.push(importedMethod.description)
        if(!classInstance.constant_pool.includes(importedMethod.name))classInstance.constant_pool.push(importedMethod.name)
        ClassFile.SetMethodCode(classInstance,methodIndex,methodCodeDissasembly.disassemble())
        return ClassFile.AddMethodRef(classInstance,classInstance.this_class,importedMethod.name,importedMethod.description)

        //console.log(methodCode)
    }
    static #depRec (curobj,cpool){
        var localpool=[]
        if(typeof curobj=='object')
        for(var p in curobj){
            localpool.push(curobj[p],...this.#depRec(curobj[p],cpool))
            
        }
        return localpool
    }
    
    static GetConstantDependencies(constantPool,instance){
        
        if(constantPool.includes(instance)==-1)throw new Error("Constant pool does not contain the specified instance")
        return ClassFile.#depRec(instance,constantPool)
        
    }
}
var parseClass=(bytez=Buffer.from(''))=>{
    var SIGNATURE=0xCAFEBABE
    var offset=4
    if(bytez.readUInt32BE() ==SIGNATURE){
  
      var minorv= bytez.readUInt16BE(offset)
  
      var majorv= bytez.readUInt16BE(offset+2)
      var cpoolcount= bytez.readUInt16BE(offset+4)
      offset+=6
      var cpoolbuf=bytez
      var cpool=[]
      console.log(cpoolcount)
      for (let i = 1; i < cpoolcount; i++) {
              
              var type = cpoolbuf.readUInt8(offset);
              offset++
              switch (type) {
              case 1: //CONSTANT_Utf8
              var len=cpoolbuf.readUInt16BE(offset)
              cpool[i]=String(bufCopy(cpoolbuf,offset+2,offset+len+2))  
              //cpool[i] = String.fromCharCode()
                  offset+=2+len
                  
                  break;
              case 3: //CONSTANT_Integer
                  cpool[i] = cpoolbuf.readInt32BE(offset)
                  offset+=4
                  break;
              case 4: //CONSTANT_Float
                  cpool[i] =cpoolbuf.readFloatBE(offset)
                  offset+=4
                  break;
              case 5: //CONSTANT_Long
                  cpool[i] = cpoolbuf.readUIntBE(offset)
                  offset+=8
                  i++
                  break;
              case 6: //CONSTANT_Double
                  cpool[i] =cpoolbuf.readDoubleBE(offset)
                  offset+=8
                  i++;
                  break;
              case 7: //CONSTANT_Class
              case 8: //CONSTANT_String
              case 16: //CONSTANT_MethodType
              case 19: //CONSTANT_Module_info
              case 20: //CONSTANT_Package_info
                  cpool[i] = [type, cpoolbuf.readUInt16BE(offset)]
                  offset+=2
                  break;
              case 9: //CONSTANT_Fieldref
              case 10: //CONSTANT_Methodref
              case 11: //CONSTANT_InterfaceMethodref
              case 12: //CONSTANT_NameAndType
              case 17: //CONSTANT_Dynamic
              case 18: //CONSTANT_InvokeDynamic
                  cpool[i] = [type,cpoolbuf.readUInt16BE(offset),cpoolbuf.readUInt16BE(offset+2)];
                  offset+=4
                  break;
              case 15: //CONSTANT_MethodHandle
                  cpool[i] = [type, cpoolbuf.readInt8(offset), cpoolbuf.readInt16BE(offset+1)];
                  offset+=3
                  break;
              default:
                  throw new Error("Unknown constant pool type: " + type);
              }
      }
      cpool=require('./something_fucking_horrific')(cpool,cpoolcount,majorv)
      var accessflags= bytez.readUInt16BE(offset)
      var classAccessFlags=[]
      //
      //console.log(offset)
      offset+=2
      for(var Enum in ClassAccessFlag.enums) {
        var classAccessFlag=ClassAccessFlag.enums[Enum]
         if((classAccessFlag & accessflags) == classAccessFlag) {
         classAccessFlags.push(classAccessFlag);
        }
    }
    var this_class=cpool[bytez.readUInt16BE(offset)]
    var super_class=cpool[bytez.readUInt16BE(offset+2)]
    //console.log(" 0x"+offset.toString(16))
    var ipoolcount= bytez.readUInt16BE(offset+4)
    offset+=6
    var interfaces = [new ConstantClass()];
    interfaces.pop()
    for( var i = 0; i < ipoolcount; i++ ) {
          interfaces[i] = cpool[bytez.readUInt16BE(offset)];
          offset+=2
    }
    var fieldcount=bytez.readUInt16BE(offset)
    offset+=2
    var fields=[]
    for( let i = 0; i < fieldcount; i++ ) {
      fields[i] = new FieldInfo( cpoolbuf,offset, cpool );
      offset=fields[i].newOffset
  }
  var methodscount=bytez.readUInt16BE(offset)
  offset+=2
  var methods = []//new MethodInfo[input.readUnsignedShort()];
  for( var i = 0; i < methodscount; i++ ) {
      methods[i] = new MethodInfo( bytez, cpool, offset );
      offset=methods[i].newOffset
  }
  
    var attributes = new Attributes(bytez,cpool,offset)
    offset=attributes.newOffset
      //METHODS=methods
      ///console.log(offset,ipoolcount,methods,cpool)
      return new ClassFile(methods,fields,cpool,interfaces,majorv,minorv,super_class,this_class,classAccessFlags,attributes)
    }
    console.error("not a class")
   
  }
  global.parseClass=parseClass
module.exports={

    ReferenceKind,
    ConstantClass,
    ConstantFieldRef,
    ConstantRef,
    ConstantInterfaceMethodRef,
    ConstantMethodRef,
    ConstantStringRef,
    ConstantNameAndType,
    ConstantMethodHandle,
    ConstantMethodType,
    ConstantDynamic,
    ConstantInvokeDynamic,
    ClassAccessFlag,
    FieldsAccessFlag,
    FieldInfo,
    MethodInfo,
    ByteCode,
    ClassFile,
    Attributes,
    fs,
    n32tonum,
    GetUInt16BE,
    bufCopy,
    parseClass
    
}
UTILS=module.exports
for(var util in UTILS)global[util]=UTILS[util]