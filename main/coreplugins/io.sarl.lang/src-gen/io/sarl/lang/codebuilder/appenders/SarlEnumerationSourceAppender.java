/*
 * $Id$
 *
 * File is automatically generated by the Xtext language generator.
 * Do not change it.
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2018 the original authors or authors.
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
package io.sarl.lang.codebuilder.appenders;

import io.sarl.lang.codebuilder.builders.ISarlEnumLiteralBuilder;
import io.sarl.lang.codebuilder.builders.ISarlEnumerationBuilder;
import io.sarl.lang.sarl.SarlEnumeration;
import io.sarl.lang.sarl.SarlScript;
import java.io.IOException;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend.core.xtend.XtendTypeDeclaration;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.xbase.compiler.ISourceAppender;
import org.eclipse.xtext.xbase.lib.Pure;

/** Source adapter of a Sarl SarlEnumeration.
 */
@SuppressWarnings("all")
public class SarlEnumerationSourceAppender extends AbstractSourceAppender implements ISarlEnumerationBuilder {

	private final ISarlEnumerationBuilder builder;

	public SarlEnumerationSourceAppender(ISarlEnumerationBuilder builder) {
		this.builder = builder;
	}

	public void build(ISourceAppender appender) throws IOException {
		build(this.builder.getSarlEnumeration(), appender);
	}

	/** Find the reference to the type with the given name.
	 * @param typeName the fully qualified name of the type
	 * @return the type reference.
	 */
	public JvmParameterizedTypeReference newTypeRef(String typeName) {
		return this.builder.newTypeRef(typeName);
	}

	/** Find the reference to the type with the given name.
	 * @param context the context for the type reference use
	 * @param typeName the fully qualified name of the type
	 * @return the type reference.
	 */
	public JvmParameterizedTypeReference newTypeRef(Notifier context, String typeName) {
		return this.builder.newTypeRef(context, typeName);
	}

	public IJvmTypeProvider getTypeResolutionContext() {
		return this.builder.getTypeResolutionContext();
	}

	/** Dispose the resource.
	 */
	public void dispose() {
		this.builder.dispose();
	}

	@Override
	@Pure
	public String toString() {
		return this.builder.toString();
	}

	/** Initialize the Ecore element when inside a script.
	 */
	public void eInit(SarlScript script, String name, IJvmTypeProvider context) {
		this.builder.eInit(script, name, context);
	}

	/** Initialize the Ecore element when inner type declaration.
	 */
	public void eInit(XtendTypeDeclaration container, String name, IJvmTypeProvider context) {
		this.builder.eInit(container, name, context);
	}

	/** Replies the generated SarlEnumeration.
	 */
	@Pure
	public SarlEnumeration getSarlEnumeration() {
		return this.builder.getSarlEnumeration();
	}

	/** Replies the resource to which the SarlEnumeration is attached.
	 */
	@Pure
	public Resource eResource() {
		return getSarlEnumeration().eResource();
	}

	/** Change the documentation of the element.
	 *
	 * <p>The documentation will be displayed just before the element.
	 *
	 * @param doc the documentation.
	 */
	public void setDocumentation(String doc) {
		this.builder.setDocumentation(doc);
	}

	/** Add a modifier.
	 * @param modifier the modifier to add.
	 */
	public void addModifier(String modifier) {
		this.builder.addModifier(modifier);
	}

	/** Create a SarlEnumLiteral.
	 * @param name the name of the SarlEnumLiteral.
	 * @return the builder.
	 */
	public ISarlEnumLiteralBuilder addSarlEnumLiteral(String name) {
		return this.builder.addSarlEnumLiteral(name);
	}

}

