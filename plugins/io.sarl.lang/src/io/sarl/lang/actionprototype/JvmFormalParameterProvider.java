/*
 * $Id$
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2015 Sebastian RODRIGUEZ, Nicolas GAUD, Stéphane GALLAND.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sarl.lang.actionprototype;

import io.sarl.lang.annotation.DefaultValue;
import io.sarl.lang.util.Utils;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.XExpression;


/** An object able to provide the name and the type of a formal parameter.
*
* @author $Author: sgalland$
* @version $FullVersion$
* @mavengroupid $GroupId$
* @mavenartifactid $ArtifactId$
*/
public class JvmFormalParameterProvider implements FormalParameterProvider {

	private final List<JvmFormalParameter> parameters;

	/**
	 * @param parameters the list of the formal parameters.
	 */
	public JvmFormalParameterProvider(List<JvmFormalParameter> parameters) {
		this.parameters = parameters;
	}

	@Override
	public int getFormalParameterCount() {
		return this.parameters.size();
	}

	@Override
	public String getFormalParameterName(int position) {
		return this.parameters.get(position).getName();
	}

	@Override
	public String getFormalParameterType(int position, boolean isVarargs) {
		return getFormalParameterTypeReference(position, isVarargs).getQualifiedName();
	}

	@Override
	public JvmTypeReference getFormalParameterTypeReference(int position, boolean isVarargs) {
		return this.parameters.get(position).getParameterType();
	}

	@Override
	public boolean hasFormalParameterDefaultValue(int position) {
		JvmFormalParameter parameter = this.parameters.get(position);
		return Utils.hasAnnotation(parameter, DefaultValue.class);
	}

	@Override
	public XExpression getFormalParameterDefaultValue(int position) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EObject getFormalParameter(int position) {
		return this.parameters.get(position);
	}

}
