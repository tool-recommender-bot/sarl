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
package io.sarl.lang.serializer;

import io.sarl.lang.documentation.IEcoreDocumentationBuilder;
import io.sarl.lang.documentation.InnerBlockDocumentationAdapter;
import io.sarl.lang.services.SARLGrammarKeywordAccess;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.acceptor.ISequenceAcceptor;
import org.eclipse.xtext.serializer.acceptor.ISyntacticSequenceAcceptor;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynState;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.ISynTransition;
import org.eclipse.xtext.serializer.analysis.ISyntacticSequencerPDAProvider.SynStateType;
import org.eclipse.xtext.serializer.diagnostic.ISerializationDiagnostic;
import org.eclipse.xtext.serializer.sequencer.HiddenTokenSequencer;
import org.eclipse.xtext.serializer.sequencer.RuleCallStack;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.XBlockExpression;
import org.eclipse.xtext.xbase.compiler.DocumentationAdapter;

/** Syntactic sequencer which supports documentations of Ecore elements.
 */
public class SARLEcoreDocumentationSyntacticSequencer extends SARLSyntacticSequencer {

	private final Set<EObject> documentedSemanticObjects = new HashSet<>();

	private final Set<EObject> indocumentedSemanticObjects = new HashSet<>();

	private InnerBlockDocumentationAdapter lastInnerBlock;

	@Inject
	private IEcoreDocumentationBuilder documentationBuilder;

	@Inject
	private SARLGrammarKeywordAccess keywords;

	private ISequenceAcceptor trailingSequenceAcceptor;

	public void init(ISerializationContext context, EObject semanticObject,
				ISyntacticSequenceAcceptor sequenceAcceptor, ISerializationDiagnostic.Acceptor errorAcceptor) {
		super.init(context, semanticObject, sequenceAcceptor, errorAcceptor);
		if (sequenceAcceptor instanceof ISequenceAcceptor) {
			this.trailingSequenceAcceptor = (ISequenceAcceptor) sequenceAcceptor;
		}
		this.documentedSemanticObjects.clear();
		this.indocumentedSemanticObjects.clear();
		this.lastInnerBlock = null;
	}

	protected ISequenceAcceptor getTrailingSequenceAcceptor() {
		if (this.trailingSequenceAcceptor == null) {
			try {
				Field delegateField = HiddenTokenSequencer.class.getDeclaredField("delegate");
				delegateField.setAccessible(true);
				this.trailingSequenceAcceptor = (ISequenceAcceptor) delegateField.get(this.delegate);
			} catch (Throwable exception) {
				throw new RuntimeException(exception);
			}
		}
		return this.trailingSequenceAcceptor;
	}

	protected void emitDocumentation(Class<?> semanticObjectType, String comment) {
		final String fmtcomment = this.documentationBuilder.build(comment, semanticObjectType);
		if (!Strings.isEmpty(fmtcomment)) {
			final AbstractRule rule = this.documentationBuilder.isMultilineCommentFor(semanticObjectType) ? this.documentationBuilder.getMLCommentRule() : this.documentationBuilder.getSLCommentRule();
			getTrailingSequenceAcceptor().acceptComment(rule, fmtcomment, null);
		}
	}

	protected void emitDocumentation(EObject semanticObject) {
		if (this.documentedSemanticObjects.add(semanticObject)) {
			DocumentationAdapter documentationAdapter = (DocumentationAdapter) EcoreUtil.getAdapter(semanticObject.eAdapters(), DocumentationAdapter.class);
			if (documentationAdapter != null) {
				emitDocumentation(semanticObject.getClass(), documentationAdapter.getDocumentation());
			}
		}
	}

	protected void emitInnerDocumentation(EObject semanticObject) {
		if (this.indocumentedSemanticObjects.add(semanticObject)) {
			InnerBlockDocumentationAdapter documentationAdapter = (InnerBlockDocumentationAdapter) EcoreUtil.getAdapter(semanticObject.eAdapters(), InnerBlockDocumentationAdapter.class);
			if (documentationAdapter != null) {
				emitDocumentation(semanticObject.getClass(), documentationAdapter.getDocumentation());
			}
		}
	}

	private InnerBlockDocumentationAdapter getInnerDocumentation(EObject semanticObject) {
		if (this.indocumentedSemanticObjects.add(semanticObject)) {
			return (InnerBlockDocumentationAdapter) EcoreUtil.getAdapter(semanticObject.eAdapters(), InnerBlockDocumentationAdapter.class);
		}
		return null;
	}

	protected void emitUnassignedTokens(EObject semanticObject, ISynTransition transition,
				INode fromNode, INode toNode) {
		super.emitUnassignedTokens(semanticObject, transition, fromNode, toNode);
		emitDocumentation(semanticObject);
		if (semanticObject instanceof XBlockExpression) {
			this.lastInnerBlock = getInnerDocumentation(semanticObject);
		}
	}

	protected void accept(ISynState emitter, INode node, RuleCallStack stack) {
		super.accept(emitter, node, stack);
		final InnerBlockDocumentationAdapter documentation = this.lastInnerBlock;
		if (documentation != null && emitter.getType() == SynStateType.UNASSIGEND_KEYWORD) {
			Keyword keyword = (Keyword) emitter.getGrammarElement();
			String token = node != null ? node.getText() : keyword.getValue();
			if (Strings.equal(token, this.keywords.getLeftCurlyBracketKeyword())) {
				this.lastInnerBlock = null;
				emitDocumentation(documentation.getTarget().getClass(), documentation.getDocumentation());
			}
		}
	}

}

