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
package com.viridiansoftware.java;

import com.viridiansoftware.java.attributes.*;
import com.viridiansoftware.java.constants.ConstantNameAndType;
import com.viridiansoftware.java.constants.ConstantPool;
import com.viridiansoftware.java.descriptor.MethodDescriptor;
import com.viridiansoftware.java.signature.ClassSignature;
import com.viridiansoftware.java.signature.MethodSignature;
import com.viridiansoftware.java.signature.antlr.SignatureParser;
import com.viridiansoftware.java.utils.ClassUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

public class MethodInfo implements Member, TypeVariableResolver {

    private final int          accessFlags;
    private final List<MethodAccessFlag> methodAccessFlags = new ArrayList<MethodAccessFlag>(4);
    private final String       name;
    private final String       description;
    private final Attributes attributes;
    private final ConstantPool constantPool;
    private Code code;
    private Exceptions         exceptions;
    private ClassFile          classFile;
    private RuntimeVisibleAnnotations runtimeVisibleAnnotations;
    private RuntimeVisibleParameterAnnotations runtimeVisibleParameterAnnotations;
    private AnnotationDefault annotationDefault;

    private String signature;
    private MethodParameters methodParameters;
    private MethodSignature methodSignature;
    private MethodDescriptor methodDescriptor;

    /**
     * Read the method_info structure http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.6
     *
     * @param input
     *            the stream of the class file
     * @param constantPool
     *            the ConstantPool of the class
     * @param classFile
     *            the declaring class file
     * @throws IOException
     *             if an I/O error occurs
     */
    MethodInfo( DataInputStream input, ConstantPool constantPool, ClassFile classFile ) throws IOException {
        this.accessFlags = input.readUnsignedShort();

        for(MethodAccessFlag methodAccessFlag : MethodAccessFlag.values()) {
            if((methodAccessFlag.getMask() & accessFlags) == methodAccessFlag.getMask()) {
                methodAccessFlags.add(methodAccessFlag);
            }
        }

        this.name = (String)constantPool.get( input.readUnsignedShort() );
        this.description = (String)constantPool.get( input.readUnsignedShort() );
        this.constantPool = constantPool;
        this.attributes = new Attributes( input, constantPool );
        this.classFile = classFile;
    }

    /**
     * Get the declaring class file of the method
     * @return the ClassFile
     */
    public ClassFile getDeclaringClassFile() {
        return classFile;
    }

    /**
     * Get the access flags of the method.
     * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.6-200-A
     *
     * @return the flags
     */
    public int getAccessFlags() {
        return accessFlags;
    }

    /**
     * Returns the access flags as a list of enums
     * @return
     */
    public List<MethodAccessFlag> getMethodAccessFlags() {
        return methodAccessFlags;
    }

    /**
     * If the method is a static method.
     * http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.6-200-A
     * http://docs.oracle.com/javase/specs/jvms/se8/html/ClassFile.doc.html#1522
     * @return true, if static
     * @see #getAccessFlags()
     */
    public boolean isStatic() {
        return (accessFlags & MethodAccessFlag.STATIC.getMask()) == MethodAccessFlag.STATIC.getMask();
    }

    /**
     * Returns if the method is package visibility
     * @return True if package visibility
     */
    public boolean isDefaultScope() {
        if(isPrivate()) {
            return false;
        }
        if(isPublic()) {
            return false;
        }
        if(isProtected()) {
            return false;
        }
        return true;
    }

    /**
     * Returns if the method is public visibility
     * @return True if public visibility
     */
    public boolean isPublic() {
        return (accessFlags & MethodAccessFlag.PUBLIC.getMask()) == MethodAccessFlag.PUBLIC.getMask();
    }

    /**
     * Returns if the method is protected visibility
     * @return True if protected visibility
     */
    public boolean isProtected() {
        return (accessFlags & MethodAccessFlag.PROTECTED.getMask()) == MethodAccessFlag.PROTECTED.getMask();
    }

    /**
     * Returns if the method is private visibility
     * @return True if private visibility
     */
    public boolean isPrivate() {
        return (accessFlags & MethodAccessFlag.PRIVATE.getMask()) == MethodAccessFlag.PRIVATE.getMask();
    }

    /**
     * Returns if the method is a native method
     * @return True if the method is a native method
     */
    public boolean isNative() {
        return (accessFlags & MethodAccessFlag.NATIVE.getMask()) == MethodAccessFlag.NATIVE.getMask();
    }

    /**
     * Returns if the method is an abstract method
     * @return True if the method is an abstract method
     */
    public boolean isAbstract() {
        return (accessFlags & MethodAccessFlag.ABSTRACT.getMask()) == MethodAccessFlag.ABSTRACT.getMask();
    }

    /**
     * Returns if the method is a final method
     * @return True if the method is a final method
     */
    public boolean isFinal() {
        return (accessFlags & MethodAccessFlag.FINAL.getMask()) == MethodAccessFlag.FINAL.getMask();
    }

    /**
     * Returns if the method is a synchronized method
     * @return True if the method is a synchronized method
     */
    public boolean isSynchronized() {
        return (accessFlags & MethodAccessFlag.SYNCHRONIZED.getMask()) == MethodAccessFlag.SYNCHRONIZED.getMask();
    }

    /**
     * Returns if the method is a synthetic method
     * @return True if the method is a synthetic method
     */
    public boolean isSynthetic() {
        return (accessFlags & MethodAccessFlag.SYNTHETIC.getMask()) == MethodAccessFlag.SYNTHETIC.getMask();
    }

    /**
     * Returns if the method is a bridge method
     * @return True if the method is a bridge method
     */
    public boolean isBridge() {
        return (accessFlags & MethodAccessFlag.BRIDGE.getMask()) == MethodAccessFlag.BRIDGE.getMask();
    }

    /**
     * Returns if the method has a variable number of arguments
     * @return True if VARARGS flag is set
     */
    public boolean isVarArgs() {
        return (accessFlags & MethodAccessFlag.VARARGS.getMask()) == MethodAccessFlag.VARARGS.getMask();
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClassName() {
        return getDeclaringClassFile().getThisClass().getName();
    }

    /**
     * @return the attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }

    public AnnotationDefault getAnnotationDefault() throws IOException {
        if(annotationDefault == null) {
            AttributeInfo info = getAttributes().get("AnnotationDefault");
            if(info != null) {
                annotationDefault = new AnnotationDefault(constantPool, info);
            }
        }
        return annotationDefault;
    }

    public RuntimeVisibleAnnotations getRuntimeVisibleAnnotations() throws IOException {
        if(runtimeVisibleAnnotations == null) {
            AttributeInfo info = getAttributes().get( "RuntimeVisibleAnnotations" );
            if(info != null) {
                runtimeVisibleAnnotations = new RuntimeVisibleAnnotations(constantPool, info.getDataInputStream());
            }
        }
        return runtimeVisibleAnnotations;
    }

    public RuntimeVisibleParameterAnnotations getRuntimeVisibleParameterAnnotations() throws IOException {
        if(runtimeVisibleParameterAnnotations == null) {
            AttributeInfo info = getAttributes().get( "RuntimeVisibleParameterAnnotations" );
            if(info != null) {
                runtimeVisibleParameterAnnotations = new RuntimeVisibleParameterAnnotations(constantPool, info.getDataInputStream());
            }
        }
        return runtimeVisibleParameterAnnotations;
    }

    public Code getCode() throws IOException {
        if( code != null ){
            return code;
        }
        AttributeInfo data = attributes.get( "Code" );
        if( data != null ) {
            code = new Code( data.getDataInputStream(), constantPool, classFile.getBootstrapMethods());
        }
        return code;
    }

    /**
     * Get the signature of the method without generic types.
     */
    @Override
    public String getType() {
        return description;
    }

    /**
     * Get the signature of the method with generic types.
     * 
     * @return the signature
     * @throws IOException
     *             if an I/O error occurs
     */
    public String getSignature() throws IOException {
        if(signature != null) {
            return signature;
        }
        AttributeInfo info = getAttributes().get( "Signature" );
        if( info != null ) {
            int idx = info.getDataInputStream().readShort();
            signature = (String)constantPool.get( idx );
        }
        return signature;
    }

    public MethodParameters getMethodParameters() throws IOException {
        if(methodParameters != null) {
            return methodParameters;
        }
        AttributeInfo info = getAttributes().get( "MethodParameters" );
        if( info != null ) {
            methodParameters = new MethodParameters(info.getDataInputStream(), constantPool);
        } else {
            methodParameters = new MethodParameters(new String [0]);
        }
        return methodParameters;
    }

    public MethodDescriptor getMethodDescriptor() {
        if(methodDescriptor == null) {
            methodDescriptor = new MethodDescriptor(getType());
        }
        return methodDescriptor;
    }

    /**
     * Returns the parsed {@link MethodSignature}
     */
    public MethodSignature getMethodSignature() throws IOException {
        if(methodSignature == null) {
            if(getSignature() != null) {
                methodSignature = new MethodSignature(getSignature());
            } else {
                methodSignature = new MethodSignature(getType());
            }
        }
        return methodSignature;
    }

    /**
     * Returns if this method is a Void method
     * @return True if void
     * @throws IOException
     */
    public boolean isVoidMethod() throws IOException {
        return getMethodSignature().isVoidMethod();
    }

    public int getTotalTypeParameters() throws IOException {
        return getMethodSignature().getTotalTypeParameters();
    }

    public SignatureParser.TypeParameterContext getTypeParameter(int i) throws IOException {
        return getMethodSignature().getTypeParameter(i);
    }

    public int getTotalMethodArguments() throws IOException {
        if(getMethodDescriptor() != null)
        {
            return getMethodDescriptor().getTotalMethodParameters();
        }
        if(getMethodSignature() != null)
        {
            return getMethodSignature().getTotalMethodArguments();
        }
        return getMethodParameters().getParameterNames().length;
    }

    public String getMethodArgumentName(int i) throws IOException {
        try {
            return getMethodParameters().getParameterNames()[i];
        } catch (Exception e) {}
        return "arg" + i;
    }

    public SignatureParser.JavaTypeSignatureContext getMethodArgumentType(int i) throws IOException {
        return getMethodSignature().getMethodArgument(i);
    }

    public int getTotalThrowsSignatures() throws IOException {
        return getMethodSignature().getTotalThrowsSignatures();
    }

    public SignatureParser.ThrowsSignatureContext getThrowsSignature(int i) throws IOException {
        return getMethodSignature().getThrowsSignature(i);
    }

    public ResolvedTypeVariable resolveTypeVariable(String variableName) throws UnresolvedTypeVariableException, IOException {
        if(variableName.startsWith("T")) {
            variableName = variableName.substring(1);
        }

        for(int i = 0; i < methodSignature.getTotalTypeParameters(); i++) {
            final SignatureParser.TypeParameterContext typeParameterContext = methodSignature.getTypeParameter(i);
            if(!typeParameterContext.identifier().getText().equals(variableName)) {
                continue;
            }
            if(typeParameterContext.classBound().referenceTypeSignature() == null
                && typeParameterContext.interfaceBounds() == null) {
                continue;
            }
            return new ResolvedTypeVariable(variableName, typeParameterContext);
        }
        return classFile.resolveTypeVariable(variableName);
    }

    public SignatureParser.JavaTypeSignatureContext getResultSignature() throws IOException {
        return getMethodSignature().getReturnType();
    }

    public boolean matches(ConstantNameAndType constantNameAndType) throws IOException {
        if(!getName().equals(constantNameAndType.getName())) {
            return false;
        }
        if(description != null) {
            return description.equals(constantNameAndType.getType());
        }
        return ClassUtils.isSameType(constantNameAndType.asMethodDescriptor(), getMethodSignature());
    }

    public boolean isImplementationOf(MethodInfo methodInfo) throws IOException {
        return isImplementationOf(methodInfo, true,false);
    }

    public boolean isImplementationOf(MethodInfo methodInfo, boolean allowErasedMatch, boolean allowSyntheicImplementations) throws IOException {
        if(!getName().equals(methodInfo.getName())) {
            return false;
        }
        if(!allowSyntheicImplementations && isSynthetic()) {
            return false;
        }
        if((methodInfo.getSignature() != null && allowErasedMatch) || methodInfo.getSignature() == null)
        {
            if(description != null && methodInfo.getType() != null) {
                if(description.equals(methodInfo.getType())) {
                    return true;
                }
            }
        }
        if(methodInfo.getSignature() != null) {
            return isImplementationOf(methodInfo.getDeclaringClassFile(), methodInfo.getMethodSignature());
        }
        return false;
    }

    public boolean isImplementationOf(MethodInfo methodInfo, boolean allowSyntheicImplementations) throws IOException {
        if(!getName().equals(methodInfo.getName())) {
            return false;
        }
        if(!allowSyntheicImplementations && isSynthetic()) {
            return false;
        }
        if(description != null && methodInfo.getType() != null) {
            if(description.equals(methodInfo.getType())) {
                return true;
            }
        }
        if(methodInfo.getSignature() != null) {
            return isImplementationOf(methodInfo.getDeclaringClassFile(), methodInfo.getMethodSignature());
        }
        return false;
    }

    public boolean isImplementationOf(ClassFile declaringFile, MethodSignature methodSignature) throws IOException {
        if(declaringFile.getClassSignature() == null || getDeclaringClassFile().getClassSignature() == null) {
            if(getMethodSignature() != null) {
                return getMethodSignature().getSignatureContext().getText().equals(methodSignature.getSignatureContext().getText());
            }
            return false;
        }
        if(!methodSignature.isVoidMethod() && isVoidMethod()) {
            return false;
        } else if(methodSignature.isVoidMethod() && !isVoidMethod()) {
            return false;
        }
        if(methodSignature.getTotalMethodArguments() != getTotalMethodArguments()) {
            return false;
        }

        final ClassSignature methodClassSignature = declaringFile.getClassSignature();
        final String methodClassWithoutGenerics = declaringFile.getThisSignature();

        final Map<String, String> resolvedTypes = new HashMap<String, String>();

        final ClassSignature thisClassSignature = getDeclaringClassFile().getClassSignature();

        if(thisClassSignature.getSuperclass() != null && thisClassSignature.getSuperclass().classTypeSignature() != null) {
            String superClassWithoutGenerics = thisClassSignature.getSuperclass().classTypeSignature().getText();
            if(superClassWithoutGenerics.contains("<")) {
                superClassWithoutGenerics = superClassWithoutGenerics.substring(1, superClassWithoutGenerics.indexOf('<'));
            } else {
                superClassWithoutGenerics = superClassWithoutGenerics.substring(1, superClassWithoutGenerics.indexOf(';'));
            }

            if(superClassWithoutGenerics.equals(methodClassWithoutGenerics)) {
                for(int i = 0; i < methodClassSignature.getTotalTypeParameters(); i++) {
                    final String identifier = methodClassSignature.getTypeParameter(i).identifier().getText();
                    if(thisClassSignature.getSuperclass().classTypeSignature().simpleClassTypeSignature().typeArguments() != null &&
                            thisClassSignature.getSuperclass().classTypeSignature().simpleClassTypeSignature().typeArguments().typeArgument() != null) {
                        resolvedTypes.put(identifier, thisClassSignature.getSuperclass().classTypeSignature().
                                simpleClassTypeSignature().typeArguments().typeArgument(i).getText());
                    } else {
                        resolvedTypes.put(identifier, "Ljava/lang/Object;");
                    }
                }
            }
        }
        for(int i = 0; i < thisClassSignature.getTotalSuperinterfaces(); i++) {
            final SignatureParser.SuperinterfaceSignatureContext superinterfaceSignatureContext = thisClassSignature.getSuperinterface(i);
            String superInterfaceWithoutGenerics = thisClassSignature.getSuperclass().classTypeSignature().getText();
            if(superInterfaceWithoutGenerics.contains("<")) {
                superInterfaceWithoutGenerics = superInterfaceWithoutGenerics.substring(1, superInterfaceWithoutGenerics.indexOf('<'));
            } else {
                superInterfaceWithoutGenerics = superInterfaceWithoutGenerics.substring(1, superInterfaceWithoutGenerics.indexOf(';'));
            }

            if(!superInterfaceWithoutGenerics.equals(methodClassWithoutGenerics)) {
                continue;
            }
            for(int j = 0; j < methodClassSignature.getTotalTypeParameters(); j++) {
                final String identifier = methodClassSignature.getTypeParameter(j).identifier().getText();
                if(superinterfaceSignatureContext.classTypeSignature().simpleClassTypeSignature().typeArguments() != null &&
                        superinterfaceSignatureContext.classTypeSignature().simpleClassTypeSignature().typeArguments().typeArgument() != null) {
                    resolvedTypes.put(identifier, superinterfaceSignatureContext.classTypeSignature().
                            simpleClassTypeSignature().typeArguments().typeArgument(j).getText());
                } else {
                    resolvedTypes.put(identifier, "Ljava/lang/Object;");
                }
            }
        }

        if(!methodSignature.isVoidMethod() && !isVoidMethod()) {
            if(!isSameType(resolvedTypes, methodSignature.getReturnType(), getResultSignature())) {
                return false;
            }
        }

        for(int i = 0; i < methodSignature.getTotalMethodArguments(); i++) {
            if(!isSameType(resolvedTypes, methodSignature.getMethodArgument(i), getMethodArgumentType(i))) {
                return false;
            }
        }

        return true;
    }



    /**
     * Get the constant pool of the the current class.
     * @return the constant pool
     */
    public ConstantPool getConstantPool() {
        return constantPool;
    }

    private boolean isSameType(Map<String, String> resolvedType, List<SignatureParser.ClassTypeSignatureSuffixContext> returnType1,
                               List<SignatureParser.ClassTypeSignatureSuffixContext> returnType2) {
        if(returnType1.size() != returnType2.size()) {
            return false;
        }
        for(int i = 0; i < returnType1.size(); i++) {
            if(!isSameType(resolvedType, returnType1.get(i).simpleClassTypeSignature(), returnType2.get(i).simpleClassTypeSignature())) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameType(Map<String, String> resolvedType, SignatureParser.SimpleClassTypeSignatureContext returnType1,
                               SignatureParser.SimpleClassTypeSignatureContext returnType2) {
        if(returnType1.typeArguments() != null && returnType2.typeArguments() == null) {
            return false;
        }
        if(returnType1.typeArguments() == null && returnType2.typeArguments() != null) {
            return false;
        }
        if(returnType1.typeArguments() != null && returnType2.typeArguments() != null) {
            if(returnType1.typeArguments().typeArgument() != null && returnType2.typeArguments().typeArgument() == null) {
                return false;
            }
            if(returnType1.typeArguments().typeArgument() == null && returnType2.typeArguments().typeArgument() != null) {
                return false;
            }
            if(returnType1.typeArguments().typeArgument() != null && returnType2.typeArguments().typeArgument() != null) {
                if(returnType1.typeArguments().typeArgument().size() != returnType2.typeArguments().typeArgument().size()) {
                    return false;
                }
                for(int i = 0; i < returnType1.typeArguments().typeArgument().size(); i++) {
                    if(returnType1.typeArguments().typeArgument(i).ASTERISK() != null ||
                            returnType2.typeArguments().typeArgument(i).ASTERISK() != null) {
                        continue;
                    }

                    if(returnType1.typeArguments().typeArgument(i).referenceTypeSignature() != null &&
                            returnType2.typeArguments().typeArgument(i).referenceTypeSignature() != null) {
                        if(!isSameType(resolvedType, returnType1.typeArguments().typeArgument(i).referenceTypeSignature(),
                                returnType2.typeArguments().typeArgument(i).referenceTypeSignature())) {
                            return false;
                        }
                    }
                }
            }
        }
        return returnType1.identifier().getText().equals(returnType2.identifier().getText());
    }

    private boolean isSameType(Map<String, String> resolvedTypes, SignatureParser.ClassTypeSignatureContext returnType1,
                               SignatureParser.ClassTypeSignatureContext returnType2) {
        if(returnType1.packageSpecifier() != null && returnType2.packageSpecifier() == null) {
            return false;
        }
        if(returnType1.packageSpecifier() == null && returnType2.packageSpecifier() != null) {
            return false;
        }
        if(returnType1.packageSpecifier() != null && returnType2.packageSpecifier() != null) {
            SignatureParser.PackageSpecifierContext specifierContext1 = returnType1.packageSpecifier();
            SignatureParser.PackageSpecifierContext specifierContext2 = returnType2.packageSpecifier();

            while(true) {
                if(specifierContext1 == null && specifierContext2 == null) {
                    break;
                }
                if(specifierContext1 != null && specifierContext2 == null) {
                    return false;
                }
                if(specifierContext1 == null && specifierContext2 != null) {
                    return false;
                }
                if(!specifierContext1.identifier().getText().equals(specifierContext2.identifier().getText())) {
                    return false;
                }
                specifierContext1 = specifierContext1.packageSpecifier() != null ?
                        specifierContext1.packageSpecifier(0) : null;
                specifierContext2 = specifierContext2.packageSpecifier() != null ?
                        specifierContext2.packageSpecifier(0) : null;
            }
        }
        if(!isSameType(resolvedTypes, returnType1.simpleClassTypeSignature(), returnType2.simpleClassTypeSignature())) {
            return false;
        }
        if(returnType1.classTypeSignatureSuffix() != null && returnType2.classTypeSignatureSuffix() == null) {
            return false;
        }
        if(returnType1.classTypeSignatureSuffix() == null && returnType2.classTypeSignatureSuffix() != null) {
            return false;
        }
        if(returnType1.classTypeSignatureSuffix() != null && returnType2.classTypeSignatureSuffix() != null) {
            return isSameType(resolvedTypes, returnType1.classTypeSignatureSuffix(), returnType2.classTypeSignatureSuffix());
        }
        return true;
    }

    private boolean isSameType(Map<String, String> resolvedTypes, SignatureParser.ReferenceTypeSignatureContext returnType1,
                               SignatureParser.ReferenceTypeSignatureContext returnType2) {
        if(returnType1.classTypeSignature() != null && returnType2.classTypeSignature() != null) {
            return isSameType(resolvedTypes, returnType1.classTypeSignature(), returnType2.classTypeSignature());
        }
        if(returnType1.arrayTypeSignature() != null && returnType2.arrayTypeSignature() != null) {
            return isSameType(resolvedTypes, returnType1.arrayTypeSignature().javaTypeSignature(),
                    returnType2.arrayTypeSignature().javaTypeSignature());
        }
        if(returnType1.typeVariableSignature() != null || returnType2.typeVariableSignature() != null) {
            String class1 = "";
            String class2 = "";
            if(returnType1.typeVariableSignature() != null &&
					resolvedTypes.containsKey(returnType1.typeVariableSignature().identifier().getText())) {
                class1 = resolvedTypes.get(returnType1.typeVariableSignature().identifier().getText());
            } else if(returnType1.classTypeSignature() != null) {
                class1 = returnType1.classTypeSignature().getText();
                if(class1.contains("<")) {
                    class1 = class1.substring(0, class1.indexOf('<')) + ";";
                }
            }
            if(returnType2.typeVariableSignature() != null &&
					resolvedTypes.containsKey(returnType2.typeVariableSignature().identifier().getText())) {
                class2 = resolvedTypes.get(returnType2.typeVariableSignature().identifier().getText());
            } else if(returnType2.classTypeSignature() != null) {
                class2 = returnType2.classTypeSignature().getText();
                if(class2.contains("<")) {
                    class2 = class2.substring(0, class2.indexOf('<')) + ";";
                }
            }
            return class1.equals(class2);
        }
        return false;
    }

    private boolean isSameType(Map<String, String> resolvedTypes, SignatureParser.JavaTypeSignatureContext returnType1,
                               SignatureParser.JavaTypeSignatureContext returnType2) {
        if(returnType1.getText().equals(returnType2.getText())) {
            return true;
        }

        if(returnType1.BaseType() != null && returnType2.BaseType() != null) {
            return returnType1.BaseType().getText().equals(returnType2.BaseType().getText());
        }
        if(returnType1.referenceTypeSignature() != null && returnType2.referenceTypeSignature() != null) {
            return isSameType(resolvedTypes, returnType1.referenceTypeSignature(), returnType2.referenceTypeSignature());
        }
        return false;
    }
}
