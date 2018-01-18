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

import io.sarl.lang.codebuilder.builders.IBlockExpressionBuilder;
import io.sarl.lang.codebuilder.builders.IExpressionBuilder;
import io.sarl.lang.documentation.InnerBlockDocumentationAdapter;
import java.io.IOException;
import java.util.function.Predicate;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.XBlockExpression;
import org.eclipse.xtext.xbase.compiler.ISourceAppender;
import org.eclipse.xtext.xbase.lib.Pure;

/** Appender of a Sarl XBlockExpression.
 */
@SuppressWarnings("all")
public class BlockExpressionSourceAppender extends AbstractSourceAppender implements IBlockExpressionBuilder {

	private final IBlockExpressionBuilder builder;

	public BlockExpressionSourceAppender(IBlockExpressionBuilder builder) {
		this.builder = builder;
	}

	public void build(ISourceAppender appender) throws IOException {
		build(this.builder.getXBlockExpression(), appender);
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

	/** Create the XBlockExpression.
	 */
	public void eInit(IJvmTypeProvider context) {
		this.builder.eInit(context);
	}

	/** Replies the string for "auto-generated" comments.
	 * @return the comment text.
	 */
	@Pure
	public String getAutoGeneratedActionString() {
		return this.builder.getAutoGeneratedActionString();
	}

	/** Replies the string for "auto-generated" comments.
	 * @param resource the resource for which the comment must be determined.
	 * @return the comment text.
	 */
	@Pure
	public String getAutoGeneratedActionString(Resource resource) {
		return this.builder.getAutoGeneratedActionString(resource);
	}

	/** An empty block expression.
	 * @return the block expression.
	 */
	@Pure
	public XBlockExpression getXBlockExpression() {
		return this.builder.getXBlockExpression();
	}

	/** Replies the resource to which the XBlockExpression is attached.
	 */
	@Pure
	public Resource eResource() {
		return getXBlockExpression().eResource();
	}

	/** Change the documentation of the element.
	 *
	 * <p>getXBlockExpression()
	 *
	 * @param doc the documentation.
	 */
	public void setInnerDocumentation(String doc) {
		if (Strings.isEmpty(doc)) {
			getXBlockExpression().eAdapters().removeIf(new Predicate<Adapter>() {
				public boolean test(Adapter adapter) {
					return adapter.isAdapterForType(InnerBlockDocumentationAdapter.class);
				}
			});
		} else {
			InnerBlockDocumentationAdapter adapter = (InnerBlockDocumentationAdapter) EcoreUtil.getExistingAdapter(
					getXBlockExpression(), InnerBlockDocumentationAdapter.class);
			if (adapter == null) {
				adapter = new InnerBlockDocumentationAdapter();
				getXBlockExpression().eAdapters().add(adapter);
			}
			adapter.setDocumentation(doc);
		}
	}

	/** Add an expression inside the block.
	 * @return the expression builder.
	 */
	public IExpressionBuilder addExpression() {
		return this.builder.addExpression();
	}

	/** Fill the block with the standard "auto-generated" content.
	 * <p>Any previously added content is removed.
	 * @param type the expected type of the block (the last instruction), or
	    <code>null</code> for no type.
	 */
	public void setDefaultAutoGeneratedContent(String type) {
		this.builder.setDefaultAutoGeneratedContent(type);
	}

	@Override
	@Pure
	public String toString() {
		return this.builder.toString();
	}

	/** Dispose the resource.
	 */
	public void dispose() {
		this.builder.dispose();
	}

}

