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
package com.viridiansoftware.java;

import com.viridiansoftware.java.signature.antlr.SignatureParser;

import java.util.ArrayList;
import java.util.List;

public class ResolvedTypeVariable {
	private final String variableName;
	private final SignatureParser.ReferenceTypeSignatureContext classBound;
	private final List<SignatureParser.ReferenceTypeSignatureContext> interfaceBounds = new ArrayList<SignatureParser.ReferenceTypeSignatureContext>(1);

	public ResolvedTypeVariable(String variableName, SignatureParser.TypeParameterContext typeParameterContext) {
		this.variableName = variableName;

		if(typeParameterContext.classBound().referenceTypeSignature() != null) {
			classBound = typeParameterContext.classBound().referenceTypeSignature();
		} else {
			classBound = null;
		}
		if(typeParameterContext.interfaceBounds() == null) {
			return;
		}
		if(typeParameterContext.interfaceBounds().interfaceBound() == null) {
			return;
		}
		for(int i = 0; i < typeParameterContext.interfaceBounds().interfaceBound().size(); i++) {
			interfaceBounds.add(typeParameterContext.interfaceBounds().interfaceBound(i).referenceTypeSignature());
		}
	}

	public String getVariableName() {
		return variableName;
	}

	public SignatureParser.ReferenceTypeSignatureContext getClassBound() {
		return classBound;
	}

	public List<SignatureParser.ReferenceTypeSignatureContext> getInterfaceBounds() {
		return interfaceBounds;
	}
}
