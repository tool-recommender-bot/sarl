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
package io.sarl.lang.codebuilder.builders;

import io.sarl.lang.sarl.SarlClass;
import io.sarl.lang.sarl.SarlFactory;
import io.sarl.lang.sarl.SarlScript;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Provider;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.core.xtend.XtendFactory;
import org.eclipse.xtend.core.xtend.XtendTypeDeclaration;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.util.EmfFormatter;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.compiler.DocumentationAdapter;
import org.eclipse.xtext.xbase.lib.Pure;

/** Builder of a Sarl SarlClass.
 */
@SuppressWarnings("all")
public class SarlClassBuilderImpl extends AbstractBuilder implements ISarlClassBuilder {

	private SarlClass sarlClass;

	private EObject container;

	@Override
	@Pure
	public String toString() {
		return EmfFormatter.objToStr(getSarlClass());
	}

	/** Initialize the Ecore element when inside a script.
	 */
	public void eInit(SarlScript script, String name, IJvmTypeProvider context) {
		setTypeResolutionContext(context);
		if (this.sarlClass == null) {
			this.container = script;
			this.sarlClass = SarlFactory.eINSTANCE.createSarlClass();
			script.getXtendTypes().add(this.sarlClass);
			this.sarlClass.setAnnotationInfo(XtendFactory.eINSTANCE.createXtendTypeDeclaration());
			if (!Strings.isEmpty(name)) {
				this.sarlClass.setName(name);
			}
		}
	}

	/** Initialize the Ecore element when inner type declaration.
	 */
	public void eInit(XtendTypeDeclaration container, String name, IJvmTypeProvider context) {
		if (this.sarlClass == null) {
			this.container = container;
			this.sarlClass = SarlFactory.eINSTANCE.createSarlClass();
			container.getMembers().add(this.sarlClass);
			if (!Strings.isEmpty(name)) {
				this.sarlClass.setName(name);
			}
		}
	}

	/** Replies the generated SarlClass.
	 */
	@Pure
	public SarlClass getSarlClass() {
		return this.sarlClass;
	}

	/** Replies the resource to which the SarlClass is attached.
	 */
	@Pure
	public Resource eResource() {
		return getSarlClass().eResource();
	}

	/** Change the documentation of the element.
	 *
	 * <p>The documentation will be displayed just before the element.
	 *
	 * @param doc the documentation.
	 */
	public void setDocumentation(String doc) {
		if (Strings.isEmpty(doc)) {
			getSarlClass().eAdapters().removeIf(new Predicate<Adapter>() {
				public boolean test(Adapter adapter) {
					return adapter.isAdapterForType(DocumentationAdapter.class);
				}
			});
		} else {
			DocumentationAdapter adapter = (DocumentationAdapter) EcoreUtil.getExistingAdapter(
					getSarlClass(), DocumentationAdapter.class);
			if (adapter == null) {
				adapter = new DocumentationAdapter();
				getSarlClass().eAdapters().add(adapter);
			}
			adapter.setDocumentation(doc);
		}
	}

	/** Change the super type.
	 * @param superType the qualified name of the super type,
	 *     or <code>null</code> if the default type.
	 */
	public void setExtends(String superType) {
		if (!Strings.isEmpty(superType)) {
			JvmParameterizedTypeReference superTypeRef = newTypeRef(this.container, superType);
			this.sarlClass.setExtends(superTypeRef);
		}
		this.sarlClass.setExtends(null);
	}

	/** Add an implemented type.
	 * @param type the qualified name of the implemented type.
	 */
	public void addImplements(String type) {
		if (!Strings.isEmpty(type)) {
			this.sarlClass.getImplements().add(newTypeRef(this.container, type));
		}
	}

	/** Add a modifier.
	 * @param modifier the modifier to add.
	 */
	public void addModifier(String modifier) {
		if (!Strings.isEmpty(modifier)) {
			this.sarlClass.getModifiers().add(modifier);
		}
	}

	@Inject
	private Provider<ITypeParameterBuilder> iTypeParameterBuilderProvider;

	/** Add a type parameter.
	 * @param name the simple name of the type parameter.
	 * @return the builder of type parameter.
	 */
	public ITypeParameterBuilder addTypeParameter(String name) {
		ITypeParameterBuilder builder = this.iTypeParameterBuilderProvider.get();
		final SarlClass object = getSarlClass();
		builder.eInit(object, name, getTypeResolutionContext());
		object.getTypeParameters().add(builder.getJvmTypeParameter());
		return builder;
	}

	@Inject
	private Provider<ISarlConstructorBuilder> iSarlConstructorBuilderProvider;

	/** Create a SarlConstructor.
	 * @return the builder.
	 */
	public ISarlConstructorBuilder addSarlConstructor() {
		ISarlConstructorBuilder builder = this.iSarlConstructorBuilderProvider.get();
		builder.eInit(getSarlClass(), getTypeResolutionContext());
		return builder;
	}

	@Inject
	private Provider<ISarlFieldBuilder> iSarlFieldBuilderProvider;

	/** Create a SarlField.
	 * @param name the name of the SarlField.
	 * @return the builder.
	 */
	public ISarlFieldBuilder addVarSarlField(String name) {
		ISarlFieldBuilder builder = this.iSarlFieldBuilderProvider.get();
		builder.eInit(getSarlClass(), name, "var", getTypeResolutionContext());
		return builder;
	}

	/** Create a SarlField.
	 * @param name the name of the SarlField.
	 * @return the builder.
	 */
	public ISarlFieldBuilder addValSarlField(String name) {
		ISarlFieldBuilder builder = this.iSarlFieldBuilderProvider.get();
		builder.eInit(getSarlClass(), name, "val", getTypeResolutionContext());
		return builder;
	}

	/** Create a SarlField.	 *
	 * <p>This function is equivalent to {@link #addVarSarlField}.
	 * @param name the name of the SarlField.
	 * @return the builder.
	 */
	public ISarlFieldBuilder addSarlField(String name) {
		return this.addVarSarlField(name);
	}

	@Inject
	private Provider<ISarlActionBuilder> iSarlActionBuilderProvider;

	/** Create a SarlAction.
	 * @param name the name of the SarlAction.
	 * @return the builder.
	 */
	public ISarlActionBuilder addDefSarlAction(String name) {
		ISarlActionBuilder builder = this.iSarlActionBuilderProvider.get();
		builder.eInit(getSarlClass(), name, "def", getTypeResolutionContext());
		return builder;
	}

	/** Create a SarlAction.
	 * @param name the name of the SarlAction.
	 * @return the builder.
	 */
	public ISarlActionBuilder addOverrideSarlAction(String name) {
		ISarlActionBuilder builder = this.iSarlActionBuilderProvider.get();
		builder.eInit(getSarlClass(), name, "override", getTypeResolutionContext());
		return builder;
	}

	/** Create a SarlAction.	 *
	 * <p>This function is equivalent to {@link #addDefSarlAction}.
	 * @param name the name of the SarlAction.
	 * @return the builder.
	 */
	public ISarlActionBuilder addSarlAction(String name) {
		return this.addDefSarlAction(name);
	}

	@Inject
	private Provider<ISarlClassBuilder> iSarlClassBuilderProvider;

	/** Create a SarlClass.
	 * @param name the name of the SarlClass.
	 * @return the builder.
	 */
	public ISarlClassBuilder addSarlClass(String name) {
		ISarlClassBuilder builder = this.iSarlClassBuilderProvider.get();
		builder.eInit(getSarlClass(), name, getTypeResolutionContext());
		return builder;
	}

	@Inject
	private Provider<ISarlInterfaceBuilder> iSarlInterfaceBuilderProvider;

	/** Create a SarlInterface.
	 * @param name the name of the SarlInterface.
	 * @return the builder.
	 */
	public ISarlInterfaceBuilder addSarlInterface(String name) {
		ISarlInterfaceBuilder builder = this.iSarlInterfaceBuilderProvider.get();
		builder.eInit(getSarlClass(), name, getTypeResolutionContext());
		return builder;
	}

	@Inject
	private Provider<ISarlEnumerationBuilder> iSarlEnumerationBuilderProvider;

	/** Create a SarlEnumeration.
	 * @param name the name of the SarlEnumeration.
	 * @return the builder.
	 */
	public ISarlEnumerationBuilder addSarlEnumeration(String name) {
		ISarlEnumerationBuilder builder = this.iSarlEnumerationBuilderProvider.get();
		builder.eInit(getSarlClass(), name, getTypeResolutionContext());
		return builder;
	}

	@Inject
	private Provider<ISarlAnnotationTypeBuilder> iSarlAnnotationTypeBuilderProvider;

	/** Create a SarlAnnotationType.
	 * @param name the name of the SarlAnnotationType.
	 * @return the builder.
	 */
	public ISarlAnnotationTypeBuilder addSarlAnnotationType(String name) {
		ISarlAnnotationTypeBuilder builder = this.iSarlAnnotationTypeBuilderProvider.get();
		builder.eInit(getSarlClass(), name, getTypeResolutionContext());
		return builder;
	}

}

