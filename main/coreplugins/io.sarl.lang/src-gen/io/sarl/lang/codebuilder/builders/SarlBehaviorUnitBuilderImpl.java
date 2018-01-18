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

import io.sarl.lang.sarl.SarlBehaviorUnit;
import io.sarl.lang.sarl.SarlFactory;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Provider;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.core.xtend.XtendFactory;
import org.eclipse.xtend.core.xtend.XtendTypeDeclaration;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.util.EmfFormatter;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.XBlockExpression;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.compiler.DocumentationAdapter;
import org.eclipse.xtext.xbase.lib.Procedures;
import org.eclipse.xtext.xbase.lib.Pure;

/** Builder of a Sarl SarlBehaviorUnit.
 */
@SuppressWarnings("all")
public class SarlBehaviorUnitBuilderImpl extends AbstractBuilder implements ISarlBehaviorUnitBuilder {

	@Inject
	private Provider<IFormalParameterBuilder> parameterProvider;
	@Inject
	private Provider<IBlockExpressionBuilder> blockExpressionProvider;
	@Inject
	private Provider<IExpressionBuilder> expressionProvider;
	private EObject container;

	private SarlBehaviorUnit sarlBehaviorUnit;

	/** Initialize the Ecore element.
	 * @param container the container of the SarlBehaviorUnit.
	 * @param name the type of the SarlBehaviorUnit.
	 */
	public void eInit(XtendTypeDeclaration container, String name, IJvmTypeProvider context) {
		setTypeResolutionContext(context);
		if (this.sarlBehaviorUnit == null) {
			this.container = container;
			this.sarlBehaviorUnit = SarlFactory.eINSTANCE.createSarlBehaviorUnit();
			this.sarlBehaviorUnit.setAnnotationInfo(XtendFactory.eINSTANCE.createXtendMember());
			this.sarlBehaviorUnit.setName(newTypeRef(container, name));
			container.getMembers().add(this.sarlBehaviorUnit);
		}
	}

	/** Replies the generated element.
	 */
	@Pure
	public SarlBehaviorUnit getSarlBehaviorUnit() {
		return this.sarlBehaviorUnit;
	}

	/** Replies the resource.
	 */
	@Pure
	public Resource eResource() {
		return getSarlBehaviorUnit().eResource();
	}

	/** Change the documentation of the element.
	 *
	 * <p>The documentation will be displayed just before the element.
	 *
	 * @param doc the documentation.
	 */
	public void setDocumentation(String doc) {
		if (Strings.isEmpty(doc)) {
			getSarlBehaviorUnit().eAdapters().removeIf(new Predicate<Adapter>() {
				public boolean test(Adapter adapter) {
					return adapter.isAdapterForType(DocumentationAdapter.class);
				}
			});
		} else {
			DocumentationAdapter adapter = (DocumentationAdapter) EcoreUtil.getExistingAdapter(
					getSarlBehaviorUnit(), DocumentationAdapter.class);
			if (adapter == null) {
				adapter = new DocumentationAdapter();
				getSarlBehaviorUnit().eAdapters().add(adapter);
			}
			adapter.setDocumentation(doc);
		}
	}

	/** Change the guard.
	 * @param value the value of the guard. It may be <code>null</code>.
	 */
	@Pure
	public IExpressionBuilder getGuard() {
		IExpressionBuilder exprBuilder = this.expressionProvider.get();
		exprBuilder.eInit(getSarlBehaviorUnit(), new Procedures.Procedure1<XExpression>() {
				public void apply(XExpression expr) {
					getSarlBehaviorUnit().setGuard(expr);
				}
			}, getTypeResolutionContext());
		return exprBuilder;
	}

	/** Create the block of code.
	 * @return the block builder.
	 */
	public IBlockExpressionBuilder getExpression() {
		IBlockExpressionBuilder block = this.blockExpressionProvider.get();
		block.eInit(getTypeResolutionContext());
		XBlockExpression expr = block.getXBlockExpression();
		this.sarlBehaviorUnit.setExpression(expr);
		return block;
	}

	@Override
	@Pure
	public String toString() {
		return EmfFormatter.objToStr(getSarlBehaviorUnit());
	}

}

