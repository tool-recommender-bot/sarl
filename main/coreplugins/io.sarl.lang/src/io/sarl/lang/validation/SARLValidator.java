/*
 * $Id$
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

package io.sarl.lang.validation;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Lists.newArrayList;
import static io.sarl.lang.sarl.SarlPackage.Literals.SARL_AGENT__EXTENDS;
import static io.sarl.lang.sarl.SarlPackage.Literals.SARL_BEHAVIOR__EXTENDS;
import static io.sarl.lang.sarl.SarlPackage.Literals.SARL_CAPACITY_USES__CAPACITIES;
import static io.sarl.lang.sarl.SarlPackage.Literals.SARL_CAPACITY__EXTENDS;
import static io.sarl.lang.sarl.SarlPackage.Literals.SARL_EVENT__EXTENDS;
import static io.sarl.lang.sarl.SarlPackage.Literals.SARL_FORMAL_PARAMETER__DEFAULT_VALUE;
import static io.sarl.lang.sarl.SarlPackage.Literals.SARL_SKILL__EXTENDS;
import static io.sarl.lang.sarl.SarlPackage.Literals.SARL_SKILL__IMPLEMENTS;
import static io.sarl.lang.validation.IssueCodes.DISCOURAGED_BOOLEAN_EXPRESSION;
import static io.sarl.lang.validation.IssueCodes.DISCOURAGED_CAPACITY_DEFINITION;
import static io.sarl.lang.validation.IssueCodes.DISCOURAGED_FUNCTION_NAME;
import static io.sarl.lang.validation.IssueCodes.INVALID_CAPACITY_TYPE;
import static io.sarl.lang.validation.IssueCodes.INVALID_EXTENDED_TYPE;
import static io.sarl.lang.validation.IssueCodes.INVALID_FIRING_EVENT_TYPE;
import static io.sarl.lang.validation.IssueCodes.INVALID_IMPLEMENTED_TYPE;
import static io.sarl.lang.validation.IssueCodes.INVALID_NESTED_DEFINITION;
import static io.sarl.lang.validation.IssueCodes.REDUNDANT_CAPACITY_USE;
import static io.sarl.lang.validation.IssueCodes.REDUNDANT_INTERFACE_IMPLEMENTATION;
import static io.sarl.lang.validation.IssueCodes.RETURN_TYPE_SPECIFICATION_IS_RECOMMENDED;
import static io.sarl.lang.validation.IssueCodes.UNREACHABLE_BEHAVIOR_UNIT;
import static io.sarl.lang.validation.IssueCodes.UNUSED_AGENT_CAPACITY;
import static org.eclipse.xtend.core.validation.IssueCodes.ABSTRACT_METHOD_WITH_BODY;
import static org.eclipse.xtend.core.validation.IssueCodes.CLASS_EXPECTED;
import static org.eclipse.xtend.core.validation.IssueCodes.CREATE_FUNCTIONS_MUST_NOT_BE_ABSTRACT;
import static org.eclipse.xtend.core.validation.IssueCodes.CYCLIC_INHERITANCE;
import static org.eclipse.xtend.core.validation.IssueCodes.DISPATCH_FUNCTIONS_MUST_NOT_BE_ABSTRACT;
import static org.eclipse.xtend.core.validation.IssueCodes.DUPLICATE_TYPE_NAME;
import static org.eclipse.xtend.core.validation.IssueCodes.INTERFACE_EXPECTED;
import static org.eclipse.xtend.core.validation.IssueCodes.INVALID_MEMBER_NAME;
import static org.eclipse.xtend.core.validation.IssueCodes.JDK_NOT_ON_CLASSPATH;
import static org.eclipse.xtend.core.validation.IssueCodes.MISSING_ABSTRACT;
import static org.eclipse.xtend.core.validation.IssueCodes.MISSING_ABSTRACT_IN_ANONYMOUS;
import static org.eclipse.xtend.core.validation.IssueCodes.MISSING_OVERRIDE;
import static org.eclipse.xtend.core.validation.IssueCodes.MISSING_STATIC_MODIFIER;
import static org.eclipse.xtend.core.validation.IssueCodes.MUST_INVOKE_SUPER_CONSTRUCTOR;
import static org.eclipse.xtend.core.validation.IssueCodes.OBSOLETE_OVERRIDE;
import static org.eclipse.xtend.core.validation.IssueCodes.OVERRIDDEN_FINAL;
import static org.eclipse.xtend.core.validation.IssueCodes.OVERRIDE_REDUCES_VISIBILITY;
import static org.eclipse.xtend.core.validation.IssueCodes.XBASE_LIB_NOT_ON_CLASSPATH;
import static org.eclipse.xtend.core.xtend.XtendPackage.Literals.XTEND_CLASS__IMPLEMENTS;
import static org.eclipse.xtend.core.xtend.XtendPackage.Literals.XTEND_FIELD__NAME;
import static org.eclipse.xtend.core.xtend.XtendPackage.Literals.XTEND_FUNCTION__NAME;
import static org.eclipse.xtend.core.xtend.XtendPackage.Literals.XTEND_INTERFACE__EXTENDS;
import static org.eclipse.xtend.core.xtend.XtendPackage.Literals.XTEND_TYPE_DECLARATION__NAME;
import static org.eclipse.xtext.util.JavaVersion.JAVA8;
import static org.eclipse.xtext.xbase.validation.IssueCodes.DISCOURAGED_REFERENCE;
import static org.eclipse.xtext.xbase.validation.IssueCodes.FORBIDDEN_REFERENCE;
import static org.eclipse.xtext.xbase.validation.IssueCodes.INCOMPATIBLE_RETURN_TYPE;
import static org.eclipse.xtext.xbase.validation.IssueCodes.INCOMPATIBLE_TYPES;
import static org.eclipse.xtext.xbase.validation.IssueCodes.MISSING_TYPE;
import static org.eclipse.xtext.xbase.validation.IssueCodes.TYPE_BOUNDS_MISMATCH;
import static org.eclipse.xtext.xbase.validation.IssueCodes.VARIABLE_NAME_DISALLOWED;
import static org.eclipse.xtext.xbase.validation.IssueCodes.VARIABLE_NAME_SHADOWING;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtend.core.typesystem.LocalClassAwareTypeNames;
import org.eclipse.xtend.core.validation.ModifierValidator;
import org.eclipse.xtend.core.validation.XtendValidator;
import org.eclipse.xtend.core.xtend.XtendAnnotationTarget;
import org.eclipse.xtend.core.xtend.XtendAnnotationType;
import org.eclipse.xtend.core.xtend.XtendClass;
import org.eclipse.xtend.core.xtend.XtendConstructor;
import org.eclipse.xtend.core.xtend.XtendEnum;
import org.eclipse.xtend.core.xtend.XtendField;
import org.eclipse.xtend.core.xtend.XtendFile;
import org.eclipse.xtend.core.xtend.XtendFunction;
import org.eclipse.xtend.core.xtend.XtendInterface;
import org.eclipse.xtend.core.xtend.XtendMember;
import org.eclipse.xtend.core.xtend.XtendPackage;
import org.eclipse.xtend.core.xtend.XtendTypeDeclaration;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtend.lib.annotations.Delegate;
import org.eclipse.xtend.lib.annotations.EqualsHashCode;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtend.lib.annotations.ToString;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.JvmConstructor;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmExecutable;
import org.eclipse.xtext.common.types.JvmField;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmGenericType;
import org.eclipse.xtext.common.types.JvmIdentifiableElement;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.JvmVisibility;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.JavaVersion;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.util.XtextVersion;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.ComposedChecks;
import org.eclipse.xtext.validation.IssueSeverities;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XAbstractWhileExpression;
import org.eclipse.xtext.xbase.XAssignment;
import org.eclipse.xtext.xbase.XBasicForLoopExpression;
import org.eclipse.xtext.xbase.XBlockExpression;
import org.eclipse.xtext.xbase.XBooleanLiteral;
import org.eclipse.xtext.xbase.XConstructorCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XFeatureCall;
import org.eclipse.xtext.xbase.XForLoopExpression;
import org.eclipse.xtext.xbase.XMemberFeatureCall;
import org.eclipse.xtext.xbase.XPostfixOperation;
import org.eclipse.xtext.xbase.XSynchronizedExpression;
import org.eclipse.xtext.xbase.XTypeLiteral;
import org.eclipse.xtext.xbase.XVariableDeclaration;
import org.eclipse.xtext.xbase.XbasePackage;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotation;
import org.eclipse.xtext.xbase.compiler.GeneratorConfig;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Inline;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;
import org.eclipse.xtext.xbase.typesystem.override.IOverrideCheckResult.OverrideCheckDetails;
import org.eclipse.xtext.xbase.typesystem.override.IResolvedOperation;
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReference;
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReferenceFactory;
import org.eclipse.xtext.xbase.typesystem.references.StandardTypeReferenceOwner;
import org.eclipse.xtext.xbase.util.XbaseUsageCrossReferencer;
import org.eclipse.xtext.xbase.validation.FeatureNameValidator;

import io.sarl.lang.SARLVersion;
import io.sarl.lang.annotation.EarlyExit;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.Behavior;
import io.sarl.lang.core.Capacity;
import io.sarl.lang.core.DefaultSkill;
import io.sarl.lang.core.Event;
import io.sarl.lang.core.Skill;
import io.sarl.lang.extralanguage.validator.ExtraLanguageValidatorSupport;
import io.sarl.lang.jvmmodel.IDefaultVisibilityProvider;
import io.sarl.lang.jvmmodel.SARLReadAndWriteTracking;
import io.sarl.lang.jvmmodel.SarlJvmModelAssociations;
import io.sarl.lang.sarl.SarlAction;
import io.sarl.lang.sarl.SarlAgent;
import io.sarl.lang.sarl.SarlAnnotationType;
import io.sarl.lang.sarl.SarlArtifact;
import io.sarl.lang.sarl.SarlAssertExpression;
import io.sarl.lang.sarl.SarlBehavior;
import io.sarl.lang.sarl.SarlBehaviorUnit;
import io.sarl.lang.sarl.SarlBreakExpression;
import io.sarl.lang.sarl.SarlCapacity;
import io.sarl.lang.sarl.SarlCapacityUses;
import io.sarl.lang.sarl.SarlClass;
import io.sarl.lang.sarl.SarlConstructor;
import io.sarl.lang.sarl.SarlContinueExpression;
import io.sarl.lang.sarl.SarlEnumeration;
import io.sarl.lang.sarl.SarlEvent;
import io.sarl.lang.sarl.SarlField;
import io.sarl.lang.sarl.SarlFormalParameter;
import io.sarl.lang.sarl.SarlInterface;
import io.sarl.lang.sarl.SarlPackage;
import io.sarl.lang.sarl.SarlRequiredCapacity;
import io.sarl.lang.sarl.SarlScript;
import io.sarl.lang.sarl.SarlSkill;
import io.sarl.lang.sarl.SarlSpace;
import io.sarl.lang.sarl.actionprototype.ActionParameterTypes;
import io.sarl.lang.sarl.actionprototype.IActionPrototypeProvider;
import io.sarl.lang.services.SARLGrammarKeywordAccess;
import io.sarl.lang.typesystem.IImmutableTypeValidator;
import io.sarl.lang.typesystem.IOperationHelper;
import io.sarl.lang.typesystem.InheritanceHelper;
import io.sarl.lang.typesystem.SARLExpressionHelper;
import io.sarl.lang.util.OutParameter;
import io.sarl.lang.util.Utils;
import io.sarl.lang.util.Utils.SarlLibraryErrorCode;

/**
 * Validator for the SARL elements.
 *
 * <p>The check type may be one of:<ul>
 * <li>{@link CheckType#FAST}: is executed after a delay of 500ms after ANY editing action (type, enter, delete);</li>
 * <li>{@link CheckType#NORMAL}: is executed after a build (manual, or automatic);</li>
 * <li>{@link CheckType#EXPENSIVE}: is executed by right clicking ANYWHERE in the editor window and chooseing "Validate".</li>
 * </ul>
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation"
 */
@SuppressWarnings({"checkstyle:classfanoutcomplexity", "checkstyle:methodcount"})
@ComposedChecks(validators = {ExtraLanguageValidatorSupport.class})
public class SARLValidator extends AbstractSARLValidator {

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator constructorModifierValidatorForSpecialContainer = new SARLModifierValidator(
			newArrayList(SARLValidator.this.visibilityModifers));

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator staticConstructorModifierValidator = new SARLModifierValidator(
			newArrayList("static")); //$NON-NLS-1$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator agentModifierValidator = new SARLModifierValidator(
			newArrayList("public", "package", "abstract", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					"final")); //$NON-NLS-1$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator methodInAgentModifierValidator = new SARLModifierValidator(
			newArrayList(
					"package",  //$NON-NLS-1$
					"protected", "private", "static", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					"abstract", "dispatch", "final", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					"def", "override", "synchronized")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator fieldInAgentModifierValidator = new SARLModifierValidator(
			newArrayList(
					"package",  //$NON-NLS-1$
					"protected", "private", //$NON-NLS-1$//$NON-NLS-2$
					"final", "val", "var")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator behaviorModifierValidator = new SARLModifierValidator(
			newArrayList("public", "package", "abstract", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					"final")); //$NON-NLS-1$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator methodInBehaviorModifierValidator = new SARLModifierValidator(
			newArrayList(
					"public", "package", //$NON-NLS-1$ //$NON-NLS-2$
					"protected", "private", //$NON-NLS-1$//$NON-NLS-2$
					"abstract", "dispatch", "final", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					"def", "override", "synchronized")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator fieldInBehaviorModifierValidator = new SARLModifierValidator(
			newArrayList(
					"package",  //$NON-NLS-1$
					"protected", "private", //$NON-NLS-1$//$NON-NLS-2$
					"final", "val", "var")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator capacityModifierValidator = new SARLModifierValidator(
			newArrayList("public", "package")); //$NON-NLS-1$//$NON-NLS-2$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator methodInCapacityModifierValidator = new SARLModifierValidator(
			newArrayList(
					"public", "def", "override")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator eventModifierValidator = new SARLModifierValidator(
			newArrayList("public", "package", "final")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator fieldInEventModifierValidator = new SARLModifierValidator(
			newArrayList(
					"public", //$NON-NLS-1$
					"final", "val", "var")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator skillModifierValidator = new SARLModifierValidator(
			newArrayList("public", "package", "abstract", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					"final")); //$NON-NLS-1$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator methodInSkillModifierValidator = new SARLModifierValidator(
			newArrayList(
					"public", "package", //$NON-NLS-1$ //$NON-NLS-2$
					"protected", "private", //$NON-NLS-1$//$NON-NLS-2$
					"abstract", "dispatch", "final", //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
					"def", "override", "synchronized")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator fieldInSkillModifierValidator = new SARLModifierValidator(
			newArrayList(
					"public", "package", //$NON-NLS-1$ //$NON-NLS-2$
					"protected", "private", //$NON-NLS-1$//$NON-NLS-2$
					"final", "val", "var")); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator nestedClassInAgentModifierValidator = new SARLModifierValidator(
			newArrayList(
					"package", "protected", //$NON-NLS-1$ //$NON-NLS-2$
					"private", "static", "final", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					"abstract")); //$NON-NLS-1$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator nestedInterfaceInAgentModifierValidator = new SARLModifierValidator(
			newArrayList(
					"package", "protected", "private", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					"static", "abstract")); //$NON-NLS-1$ //$NON-NLS-2$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator nestedEnumerationInAgentModifierValidator = new SARLModifierValidator(
			newArrayList(
					"package", "protected", "private", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					"static")); //$NON-NLS-1$

	@SuppressWarnings("synthetic-access")
	private final SARLModifierValidator nestedAnnotationTypeInAgentModifierValidator = new SARLModifierValidator(
			newArrayList(
					"package", "protected", "private", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					"static", "abstract")); //$NON-NLS-1$ //$NON-NLS-2$

	@Inject
	private SarlJvmModelAssociations associations;

	@Inject
	private FeatureNameValidator featureNames;

	@Inject
	private IActionPrototypeProvider sarlActionSignatures;

	@Inject
	private IFeatureCallValidator featureCallValidator;

	@Inject
	private SARLGrammarKeywordAccess grammarAccess;

	@Inject
	private TypeReferences typeReferences;

	@Inject
	private LocalClassAwareTypeNames localClassAwareTypeNames;

	@Inject
	private IOperationHelper operationHelper;

	@Inject
	private IProgrammaticWarningSuppressor warningSuppressor;

	@Inject
	private IQualifiedNameConverter qualifiedNameConverter;

	@Inject
	private SARLReadAndWriteTracking readAndWriteTracking;

	@Inject
	private InheritanceHelper inheritanceHelper;

	@Inject
	private IDefaultVisibilityProvider defaultVisibilityProvider;

	@Inject
	private IImmutableTypeValidator immutableTypeValidator;

	// Update the annotation target information
	{
		final ImmutableMultimap.Builder<Class<?>, ElementType> result = ImmutableMultimap.builder();
		result.putAll(this.targetInfos);
		result.put(SarlAgent.class, ElementType.TYPE);
		result.put(SarlCapacity.class, ElementType.TYPE);
		result.put(SarlSkill.class, ElementType.TYPE);
		result.put(SarlEvent.class, ElementType.TYPE);
		result.put(SarlBehavior.class, ElementType.TYPE);
		result.put(SarlSpace.class, ElementType.TYPE);
		result.put(SarlArtifact.class, ElementType.TYPE);
		result.put(SarlClass.class, ElementType.TYPE);
		result.put(SarlInterface.class, ElementType.TYPE);
		result.put(SarlEnumeration.class, ElementType.TYPE);
		result.putAll(SarlAnnotationType.class, ElementType.ANNOTATION_TYPE, ElementType.TYPE);
		result.put(SarlField.class, ElementType.FIELD);
		result.put(SarlAction.class, ElementType.METHOD);
		result.put(SarlFormalParameter.class, ElementType.PARAMETER);
		// Override the target informations
		try {
			final Field field = XtendValidator.class.getDeclaredField("targetInfos"); //$NON-NLS-1$
			field.setAccessible(true);
			field.set(this, result.build());
		} catch (Exception exception) {
			throw new Error(exception);
		}
	}

	/** Create a lightweight type reference from the given type.
	 *
	 * @param type the type to point to.
	 * @param context the context in which the reference is located.
	 * @return the reference.
	 */
	protected LightweightTypeReference toLightweightTypeReference(JvmType type, EObject context) {
		final StandardTypeReferenceOwner owner = new StandardTypeReferenceOwner(getServices(), context);
		final LightweightTypeReferenceFactory factory = new LightweightTypeReferenceFactory(owner, false);
		return factory.toLightweightReference(type);
	}

	@Override
	protected boolean isValueExpectedRecursive(XExpression expr) {
		final EObject container = expr.eContainer();
		if (container instanceof SarlBreakExpression) {
			return false;
		}
		if (container instanceof SarlContinueExpression) {
			return false;
		}
		if (container instanceof SarlAssertExpression) {
			return true;
		}
		return super.isValueExpectedRecursive(expr);
	}

	@Override
	protected IssueSeverities getIssueSeverities(Map<Object, Object> context, EObject eObject) {
		final IssueSeverities severities = super.getIssueSeverities(context, eObject);
		return this.warningSuppressor.getIssueSeverities(context, eObject, severities);
	}

	/** Replies if the given issue is ignored for the given object.
	 *
	 * @param issueCode the code if the issue.
	 * @param currentObject the current object.
	 * @return <code>true</code> if the issue is ignored.
	 * @see #isIgnored(String)
	 */
	protected boolean isIgnored(String issueCode, EObject currentObject) {
		final IssueSeverities severities = getIssueSeverities(getContext(), currentObject);
		return severities.isIgnored(issueCode);
	}

	/** Replies the canonical name of the given object.
	 *
	 * @param object the object.
	 * @return the canonical name or <code>null</code> if it cannot be computed.
	 */
	protected String canonicalName(EObject object) {
		if (object instanceof JvmIdentifiableElement) {
			return ((JvmIdentifiableElement) object).getQualifiedName();
		}
		final EObject jvmElement = this.associations.getPrimaryJvmElement(object);
		if (jvmElement instanceof JvmIdentifiableElement) {
			return ((JvmIdentifiableElement) jvmElement).getQualifiedName();
		}
		return null;
	}

	/** Space keyword is reserved.
	 *
	 * @param space the space to check.
	 */
	@Check
	public void checkSpaceUse(SarlSpace space) {
		error(MessageFormat.format(
				Messages.SARLValidator_0,
				this.grammarAccess.getSpaceKeyword()),
				space,
				null);
	}

	/** Artifact keyword is reserved.
	 *
	 * @param artifact the artifact to check.
	 */
	@Check
	public void checkArtifactUse(SarlArtifact artifact) {
		error(MessageFormat.format(
				Messages.SARLValidator_0,
				this.grammarAccess.getSpaceKeyword()),
				artifact,
				null);
	}

	/** Emit a warning when the "fires" keyword is used.
	 *
	 * @param action the action to check.
	 */
	@Check
	public void checkFiresKeywordUse(SarlAction action) {
		if (!action.getFiredEvents().isEmpty()) {
			warning(MessageFormat.format(
					Messages.SARLValidator_1,
					this.grammarAccess.getFiresKeyword()),
					action,
					SarlPackage.eINSTANCE.getSarlAction_FiredEvents());
		}
	}

	/** Emit a warning when the "requires" keyword is used.
	 *
	 * @param statement the statement to check.
	 */
	@Check
	public void checkRequiredCapacityUse(SarlRequiredCapacity statement) {
		warning(MessageFormat.format(
				Messages.SARLValidator_0,
				this.grammarAccess.getRequiresKeyword()),
				statement,
				null);
	}

	/** Check if the correct SARL libraries are on the classpath.
	 *
	 * <p>This function is overriding the function given by the Xtend validator
	 * for having finer tests, and firing a warning in place of an error.
	 *
	 * @param sarlScript the SARL script.
	 */
	@Check(CheckType.NORMAL)
	@Override
	@SuppressWarnings("checkstyle:npathcomplexity")
	public void checkClassPath(XtendFile sarlScript) {
		final TypeReferences typeReferences = getServices().getTypeReferences();

		if (!Utils.isCompatibleJREVersion()) {
			error(
					MessageFormat.format(
							Messages.SARLValidator_3,
							System.getProperty("java.specification.version"), //$NON-NLS-1$
							SARLVersion.MINIMAL_JDK_VERSION),
					sarlScript,
					XtendPackage.Literals.XTEND_FILE__PACKAGE,
					JDK_NOT_ON_CLASSPATH);
		} else {
			final GeneratorConfig generatorConfiguration = getGeneratorConfig(sarlScript);
			final JavaVersion javaVersion = JavaVersion.fromQualifier(SARLVersion.MINIMAL_JDK_VERSION);
			final JavaVersion generatorVersion = generatorConfiguration.getJavaSourceVersion();
			if (generatorVersion == null
					|| javaVersion == null
					|| !generatorVersion.isAtLeast(javaVersion)) {
				error(
						MessageFormat.format(
								Messages.SARLValidator_4,
								generatorVersion,
								SARLVersion.MINIMAL_JDK_VERSION),
						sarlScript,
						XtendPackage.Literals.XTEND_FILE__PACKAGE,
						JDK_NOT_ON_CLASSPATH);
			}
		}

		if (!Utils.isCompatibleXtextVersion()) {
			error(
					MessageFormat.format(
							Messages.SARLValidator_5,
							XtextVersion.getCurrent(),
							SARLVersion.MINIMAL_XTEXT_VERSION),
					sarlScript,
					XtendPackage.Literals.XTEND_FILE__PACKAGE,
					XBASE_LIB_NOT_ON_CLASSPATH);
		} else {
			final JvmType type = typeReferences.findDeclaredType(ToStringBuilder.class.getName(), sarlScript);
			if (type == null) {
				error(
						MessageFormat.format(
								Messages.SARLValidator_6,
								SARLVersion.MINIMAL_XTEXT_VERSION),
						sarlScript,
						XtendPackage.Literals.XTEND_FILE__PACKAGE,
						XBASE_LIB_NOT_ON_CLASSPATH);
			}
		}

		final OutParameter<String> sarlLibraryVersion = new OutParameter<>();
		final SarlLibraryErrorCode errorCode = Utils.getSARLLibraryVersionOnClasspath(typeReferences, sarlScript, sarlLibraryVersion);
		if (errorCode != SarlLibraryErrorCode.SARL_FOUND) {
			final ResourceSet resourceSet = EcoreUtil2.getResourceSet(sarlScript);
			final StringBuilder classPath = new StringBuilder();
			for (final Resource resource : resourceSet.getResources()) {
				classPath.append(resource.getURI().toString());
				classPath.append("\n"); //$NON-NLS-1$
			}
			final StringBuilder fields = new StringBuilder();
			try {
				final JvmDeclaredType type = (JvmDeclaredType) typeReferences.findDeclaredType(SARLVersion.class, sarlScript);
				for (final JvmField field : type.getDeclaredFields()) {
					fields.append(field.getIdentifier());
					fields.append(" / "); //$NON-NLS-1$
					fields.append(field.getSimpleName());
					fields.append("\n"); //$NON-NLS-1$
				}
			} catch (Exception e) {
				//
			}
			if (fields.length() == 0) {
				for (final Field field : SARLVersion.class.getDeclaredFields()) {
					fields.append(field.getName());
					fields.append("\n"); //$NON-NLS-1$
				}
			}
			error(
					MessageFormat.format(Messages.SARLValidator_7, errorCode.name(), classPath.toString(), fields.toString()),
					sarlScript,
					XtendPackage.Literals.XTEND_FILE__PACKAGE,
					io.sarl.lang.validation.IssueCodes.SARL_LIB_NOT_ON_CLASSPATH);
		} else if (!Utils.isCompatibleSARLLibraryVersion(sarlLibraryVersion.get())) {
			error(
					MessageFormat.format(Messages.SARLValidator_8,
							sarlLibraryVersion.get(), SARLVersion.SPECIFICATION_RELEASE_VERSION),
					sarlScript,
					XtendPackage.Literals.XTEND_FILE__PACKAGE,
					io.sarl.lang.validation.IssueCodes.INVALID_SARL_LIB_ON_CLASSPATH);
		}
	}

	@Check
	@Override
	protected void checkModifiers(XtendConstructor constructor) {
		final XtendTypeDeclaration declaringType = constructor.getDeclaringType();
		if (declaringType != null) {
			final String typeName = declaringType.getName();
			final String msg = MessageFormat.format(Messages.SARLValidator_61, typeName);
			if (declaringType instanceof SarlEvent
					|| declaringType instanceof SarlAgent
					|| declaringType instanceof SarlSkill
					|| declaringType instanceof SarlBehavior) {
				this.constructorModifierValidatorForSpecialContainer.checkModifiers(constructor, msg);
			} else if (constructor.isStatic()) {
				this.staticConstructorModifierValidator.checkModifiers(constructor, msg);
			} else {
				super.checkModifiers(constructor);
			}
		}
	}

	@Check
	@Override
	protected void checkModifiers(XtendFunction function) {
		final XtendTypeDeclaration declaringType = function.getDeclaringType();
		if (declaringType != null) {
			if (declaringType instanceof SarlAgent) {
				final String typeName = ((XtendTypeDeclaration) function.eContainer()).getName();
				this.methodInAgentModifierValidator.checkModifiers(function,
						MessageFormat.format(Messages.SARLValidator_10, function.getName(), typeName));
			} else if (declaringType instanceof SarlCapacity) {
				final String typeName = ((XtendTypeDeclaration) function.eContainer()).getName();
				this.methodInCapacityModifierValidator.checkModifiers(function,
						MessageFormat.format(Messages.SARLValidator_10, function.getName(), typeName));
			} else if (declaringType instanceof SarlSkill) {
				final String typeName = ((XtendTypeDeclaration) function.eContainer()).getName();
				this.methodInSkillModifierValidator.checkModifiers(function,
						MessageFormat.format(Messages.SARLValidator_10, function.getName(), typeName));
			} else if (declaringType instanceof SarlBehavior) {
				final String typeName = ((XtendTypeDeclaration) function.eContainer()).getName();
				this.methodInBehaviorModifierValidator.checkModifiers(function,
						MessageFormat.format(Messages.SARLValidator_10, function.getName(), typeName));
			} else {
				super.checkModifiers(function);
			}
		}
	}

	@Check
	@Override
	protected void checkModifiers(XtendField field) {
		final XtendTypeDeclaration declaringType = field.getDeclaringType();
		if (declaringType != null) {
			if (declaringType instanceof SarlEvent) {
				final String typeName = ((XtendTypeDeclaration) field.eContainer()).getName();
				this.fieldInEventModifierValidator.checkModifiers(field,
						MessageFormat.format(Messages.SARLValidator_10, field.getName(), typeName));
			} else if (declaringType instanceof SarlAgent) {
				final String typeName = ((XtendTypeDeclaration) field.eContainer()).getName();
				this.fieldInAgentModifierValidator.checkModifiers(field,
						MessageFormat.format(Messages.SARLValidator_10, field.getName(), typeName));
			} else if (declaringType instanceof SarlSkill) {
				final String typeName = ((XtendTypeDeclaration) field.eContainer()).getName();
				this.fieldInSkillModifierValidator.checkModifiers(field,
						MessageFormat.format(Messages.SARLValidator_10, field.getName(), typeName));
			} else if (declaringType instanceof SarlBehavior) {
				final String typeName = ((XtendTypeDeclaration) field.eContainer()).getName();
				this.fieldInBehaviorModifierValidator.checkModifiers(field,
						MessageFormat.format(Messages.SARLValidator_10, field.getName(), typeName));
			} else {
				super.checkModifiers(field);
			}
		}
	}

	/** Check if the modifiers for the SARL events.
	 *
	 * @param event the event.
	 */
	@Check
	protected void checkModifiers(SarlEvent event) {
		this.eventModifierValidator.checkModifiers(event,
				MessageFormat.format(Messages.SARLValidator_9, event.getName()));
	}

	/** Check the modifiers for the SARL agents.
	 *
	 * @param agent the agent.
	 */
	@Check
	protected void checkModifiers(SarlAgent agent) {
		this.agentModifierValidator.checkModifiers(agent,
				MessageFormat.format(Messages.SARLValidator_9, agent.getName()));
	}

	/** Check the modifiers for the SARL behaviors.
	 *
	 * @param behavior the behavior.
	 */
	@Check
	protected void checkModifiers(SarlBehavior behavior) {
		this.behaviorModifierValidator.checkModifiers(behavior,
				MessageFormat.format(Messages.SARLValidator_9, behavior.getName()));
	}

	/** Check the modifiers for the SARL capacities.
	 *
	 * @param capacity the capacity.
	 */
	@Check
	protected void checkModifiers(SarlCapacity capacity) {
		this.capacityModifierValidator.checkModifiers(capacity,
				MessageFormat.format(Messages.SARLValidator_9, capacity.getName()));
	}

	/** Check the modifiers for the SARL skills.
	 *
	 * @param skill the skill.
	 */
	@Check
	protected void checkModifiers(SarlSkill skill) {
		this.skillModifierValidator.checkModifiers(skill,
				MessageFormat.format(Messages.SARLValidator_9, skill.getName()));
	}

	@Check
	@Override
	protected void checkModifiers(XtendInterface oopInterface) {
		final EObject econtainer = oopInterface.eContainer();
		if (econtainer instanceof SarlAgent) {
			this.nestedInterfaceInAgentModifierValidator.checkModifiers(oopInterface,
					MessageFormat.format(Messages.SARLValidator_9, oopInterface.getName()));
		} else {
			super.checkModifiers(oopInterface);
		}
	}

	@Check
	@Override
	protected void checkModifiers(XtendClass oopClass) {
		final EObject econtainer = oopClass.eContainer();
		if (econtainer instanceof SarlAgent) {
			this.nestedClassInAgentModifierValidator.checkModifiers(oopClass,
					MessageFormat.format(Messages.SARLValidator_9, oopClass.getName()));
		} else {
			super.checkModifiers(oopClass);
		}
		// This constraint should be never removed from Xtend: https://www.eclipse.org/forums/index.php/m/1774946/
		if (!oopClass.isStatic()
				&& ((econtainer instanceof SarlAgent)
						|| (econtainer instanceof SarlBehavior)
						|| (econtainer instanceof SarlSkill))) {
			error(Messages.SARLValidator_25, XTEND_TYPE_DECLARATION__NAME, -1, MISSING_STATIC_MODIFIER);
		}
	}

	@Check
	@Override
	protected void checkModifiers(XtendEnum oopEnum) {
		final EObject econtainer = oopEnum.eContainer();
		if (econtainer instanceof SarlAgent) {
			this.nestedEnumerationInAgentModifierValidator.checkModifiers(oopEnum,
					MessageFormat.format(Messages.SARLValidator_9, oopEnum.getName()));
		} else {
			super.checkModifiers(oopEnum);
		}
	}

	@Check
	@Override
	protected void checkModifiers(XtendAnnotationType oopAnnotationType) {
		final EObject econtainer = oopAnnotationType.eContainer();
		if (econtainer instanceof SarlAgent) {
			this.nestedAnnotationTypeInAgentModifierValidator.checkModifiers(oopAnnotationType,
					MessageFormat.format(Messages.SARLValidator_9, oopAnnotationType.getName()));
		} else {
			super.checkModifiers(oopAnnotationType);
		}
	}

	/** Check the container for the SARL agents.
	 *
	 * @param agent the agent.
	 */
	@Check
	public void checkContainerType(SarlAgent agent) {
		final XtendTypeDeclaration declaringType = agent.getDeclaringType();
		if (declaringType != null) {
			final String name = canonicalName(declaringType);
			assert name != null;
			error(MessageFormat.format(Messages.SARLValidator_28, name),
					agent,
					null,
					INVALID_NESTED_DEFINITION);
		}
	}

	/** Check the container for the SARL behaviors.
	 *
	 * @param behavior the behavior.
	 */
	@Check
	public void checkContainerType(SarlBehavior behavior) {
		final XtendTypeDeclaration declaringType = behavior.getDeclaringType();
		if (declaringType != null) {
			final String name = canonicalName(declaringType);
			assert name != null;
			error(MessageFormat.format(Messages.SARLValidator_29, name),
					behavior,
					null,
					INVALID_NESTED_DEFINITION);
		}
	}

	/** Check the container for the SARL capacities.
	 *
	 * @param capacity the capacity.
	 */
	@Check
	public void checkContainerType(SarlCapacity capacity) {
		final XtendTypeDeclaration declaringType = capacity.getDeclaringType();
		if (declaringType != null) {
			final String name = canonicalName(declaringType);
			assert name != null;
			error(MessageFormat.format(Messages.SARLValidator_30, name),
					capacity,
					null,
					INVALID_NESTED_DEFINITION);
		}
	}

	/** Check the container for the SARL skills.
	 *
	 * @param skill the skill.
	 */
	@Check
	public void checkContainerType(SarlSkill skill) {
		final XtendTypeDeclaration declaringType = skill.getDeclaringType();
		if (declaringType != null) {
			final String name = canonicalName(declaringType);
			assert name != null;
			error(MessageFormat.format(Messages.SARLValidator_31, name),
					skill,
					null,
					INVALID_NESTED_DEFINITION);
		}
	}

	/** Check if the modifiers for the SARL events.
	 *
	 * @param event the event.
	 */
	@Check
	public void checkContainerType(SarlEvent event) {
		final XtendTypeDeclaration declaringType = event.getDeclaringType();
		if (declaringType != null) {
			final String name = canonicalName(declaringType);
			assert name != null;
			error(MessageFormat.format(Messages.SARLValidator_32, name),
					event,
					null,
					INVALID_NESTED_DEFINITION);
		}
	}

	/** Check if all the fields are initialized in a SARL event.
	 *
	 * @param event the event.
	 */
	@Check
	public void checkFinalFieldInitialization(SarlEvent event) {
		final JvmGenericType inferredType = this.associations.getInferredType(event);
		if (inferredType != null) {
			checkFinalFieldInitialization(inferredType);
		}
	}

	/** Check if all the fields are initialized in a SARL behavior.
	 *
	 * @param behavior the behavior.
	 */
	@Check
	public void checkFinalFieldInitialization(SarlBehavior behavior) {
		final JvmGenericType inferredType = this.associations.getInferredType(behavior);
		if (inferredType != null) {
			checkFinalFieldInitialization(inferredType);
		}
	}

	/** Check if all the fields are initialized in a SARL skill.
	 *
	 * @param skill the skill.
	 */
	@Check
	public void checkFinalFieldInitialization(SarlSkill skill) {
		final JvmGenericType inferredType = this.associations.getInferredType(skill);
		if (inferredType != null) {
			checkFinalFieldInitialization(inferredType);
		}
	}

	/** Check if all the fields are initialized in a SARL agent.
	 *
	 * @param agent the agent.
	 */
	@Check
	public void checkFinalFieldInitialization(SarlAgent agent) {
		final JvmGenericType inferredType = this.associations.getInferredType(agent);
		if (inferredType != null) {
			checkFinalFieldInitialization(inferredType);
		}
	}

	/** Check the super constructors.
	 *
	 * @param container the container.
	 * @param feature the syntactic feature related to the supertypes.
	 * @param defaultSignatures the signatures of the default constructors for the given container.
	 */
	@SuppressWarnings({"checkstyle:cyclomaticcomplexity", "checkstyle:npathcomplexity",
		"checkstyle:nestedifdepth"})
	protected void checkSuperConstructor(
			XtendTypeDeclaration container,
			EStructuralFeature feature,
			Collection<ActionParameterTypes> defaultSignatures) {
		final JvmDeclaredType jvmElement = this.associations.getInferredType(container);
		if (jvmElement != null) {
			final Map<ActionParameterTypes, JvmConstructor> superConstructors =
					CollectionLiterals.newTreeMap((Comparator<ActionParameterTypes>) null);
			final JvmTypeReference typeRef = jvmElement.getExtendedClass();
			final JvmType supertype = (typeRef == null) ? null : typeRef.getType();
			if (supertype instanceof JvmGenericType) {
				final JvmGenericType jvmSuperElement = (JvmGenericType) supertype;
				for (final JvmConstructor superConstructor : jvmSuperElement.getDeclaredConstructors()) {
					final ActionParameterTypes sig = this.sarlActionSignatures.createParameterTypesFromJvmModel(
							superConstructor.isVarArgs(), superConstructor.getParameters());
					superConstructors.put(sig, superConstructor);
				}
			}

			final ActionParameterTypes voidKey = this.sarlActionSignatures.createParameterTypesForVoid();
			//boolean hasDeclaredConstructor = false;

			for (final XtendMember member : container.getMembers()) {
				if (member instanceof SarlConstructor) {
					final SarlConstructor constructor = (SarlConstructor) member;
					//hasDeclaredConstructor = true;
					boolean invokeDefaultConstructor = true;
					final XExpression body = constructor.getExpression();
					if (body instanceof XBlockExpression) {
						final XBlockExpression block = (XBlockExpression) body;
						if (!block.getExpressions().isEmpty()) {
							final XExpression firstStatement = block.getExpressions().get(0);
							if (firstStatement instanceof XConstructorCall || isDelegateConstructorCall(firstStatement)) {
								invokeDefaultConstructor = false;
							}
						}
					} else if (body instanceof XConstructorCall || isDelegateConstructorCall(body)) {
						invokeDefaultConstructor = false;
					}
					if (invokeDefaultConstructor && !superConstructors.containsKey(voidKey)) {
						final List<String> issueData = new ArrayList<>();
						for (final ActionParameterTypes defaultSignature : defaultSignatures) {
							issueData.add(defaultSignature.toString());
						}
						error(Messages.SARLValidator_33,
								member,
								null,
								ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
								MUST_INVOKE_SUPER_CONSTRUCTOR,
								toArray(issueData, String.class));
					}
				}
			}

			// The following code is no more needed because of the constructor inheritance mechanism which
			// is implemented into the JVM model inferrer.
			/*if (!hasDeclaredConstructor) {
				for (final ActionParameterTypes defaultSignature : defaultSignatures) {
					if (!superConstructors.containsKey(defaultSignature)) {
						final List<String> issueData = new ArrayList<>();
						for (final JvmConstructor superConstructor : superConstructors.values()) {
							issueData.add(EcoreUtil.getURI(superConstructor).toString());
							issueData.add(doGetReadableSignature(container.getName(), superConstructor.getParameters()));
						}
						error(Messages.SARLValidator_33,
								container, feature, MISSING_CONSTRUCTOR, toArray(issueData, String.class));
					}
				}
			}*/
		}
	}

	/** Check if the super default constructor is correctly invoked.
	 *
	 * @param agent the SARL element.
	 */
	@Check
	public void checkSuperConstructor(SarlAgent agent) {
		checkSuperConstructor(
				agent,
				XTEND_TYPE_DECLARATION__NAME,
				doGetConstructorParameterTypes(Agent.class, agent));
	}

	/** Check if the super default constructor is correctly invoked.
	 *
	 * @param behavior the SARL element.
	 */
	@Check
	public void checkSuperConstructor(SarlBehavior behavior) {
		checkSuperConstructor(
				behavior,
				XTEND_TYPE_DECLARATION__NAME,
				doGetConstructorParameterTypes(Behavior.class, behavior));
	}

	/** Check if the super default constructor is correctly invoked.
	 *
	 * @param skill the SARL element.
	 */
	@Check
	public void checkSuperConstructor(SarlSkill skill) {
		checkSuperConstructor(
				skill,
				XTEND_TYPE_DECLARATION__NAME,
				doGetConstructorParameterTypes(Skill.class, skill));
	}

	/** Check if the super default constructor is correctly invoked.
	 *
	 * @param event the SARL element.
	 */
	@Check
	public void checkSuperConstructor(SarlEvent event) {
		checkSuperConstructor(
				event,
				XTEND_TYPE_DECLARATION__NAME,
				doGetConstructorParameterTypes(Event.class, event));
	}

	private Collection<ActionParameterTypes> doGetConstructorParameterTypes(Class<?> type, Notifier context) {
		final Collection<ActionParameterTypes> parameters = new ArrayList<>();
		final JvmTypeReference typeReference = this.typeReferences.getTypeForName(type, context);
		final JvmType jvmType = typeReference.getType();
		if (jvmType instanceof JvmDeclaredType) {
			final JvmDeclaredType declaredType = (JvmDeclaredType) jvmType;
			for (final JvmConstructor constructor : declaredType.getDeclaredConstructors()) {
				final ActionParameterTypes types = this.sarlActionSignatures.createParameterTypesFromJvmModel(
						constructor.isVarArgs(), constructor.getParameters());
				if (types != null) {
					parameters.add(types);
				}
			}
		}
		if (parameters.isEmpty()) {
			parameters.add(this.sarlActionSignatures.createParameterTypesForVoid());
		}
		return parameters;
	}

	/** Check if the super default constructor is correctly invoked.
	 *
	 * @param xtendClass the Xtend element.
	 */
	@Check
	@Override
	public void checkDefaultSuperConstructor(XtendClass xtendClass) {
		checkSuperConstructor(
				xtendClass,
				XTEND_TYPE_DECLARATION__NAME,
				doGetConstructorParameterTypes(Object.class, xtendClass));
	}

	/** Check if the call is forbidden.
	 *
	 * <p>One example of a forbidden feature is {@link System#exit(int)}.
	 *
	 * @param expression the expression.
	 */
	@Check(CheckType.FAST)
	public void checkForbiddenCalls(XAbstractFeatureCall expression) {
		if (this.featureCallValidator.isDisallowedCall(expression)) {
			error(
					MessageFormat.format(
							Messages.SARLValidator_36,
							expression.getFeature().getIdentifier()),
					expression,
					null,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					FORBIDDEN_REFERENCE);
		}
	}

	/** Check if the call is discouraged.
	 *
	 * <p>One example of a discouraged feature is {@link System#err}.
	 *
	 * @param expression the expression.
	 */
	@Check(CheckType.FAST)
	public void checkDiscouragedCalls(XAbstractFeatureCall expression) {
		if (!isIgnored(DISCOURAGED_REFERENCE)
				&& this.featureCallValidator.isDiscouragedCall(expression)) {
			addIssue(
					MessageFormat.format(Messages.SARLValidator_37,
							expression.getConcreteSyntaxFeatureName()),
					expression,
					DISCOURAGED_REFERENCE);
		}
	}

	/** Check if the default values of the formal parameters have a compatible type with the formal parameter.
	 *
	 * @param param the formal parameter to check.
	 */
	@Check
	public void checkDefaultValueTypeCompatibleWithParameterType(SarlFormalParameter param) {
		final XExpression defaultValue = param.getDefaultValue();
		if (defaultValue != null) {
			final JvmTypeReference rawType = param.getParameterType();
			assert rawType != null;
			final LightweightTypeReference toType = toLightweightTypeReference(rawType, true);
			if (toType == null) {
				error(MessageFormat.format(
						Messages.SARLValidator_20,
						param.getName()),
						param,
						XtendPackage.Literals.XTEND_PARAMETER__PARAMETER_TYPE,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						org.eclipse.xtext.xbase.validation.IssueCodes.INVALID_TYPE);
				return;
			}
			LightweightTypeReference fromType = getExpectedType(defaultValue);
			if (fromType == null) {
				fromType = getActualType(defaultValue);
				if (fromType == null) {
					error(MessageFormat.format(
							Messages.SARLValidator_21,
							param.getName()),
							param,
							SARL_FORMAL_PARAMETER__DEFAULT_VALUE,
							ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
							org.eclipse.xtext.xbase.validation.IssueCodes.INVALID_TYPE);
					return;
				}
			}
			if (!Utils.canCast(fromType, toType, true, false, true)) {
				error(MessageFormat.format(
						Messages.SARLValidator_38,
						getNameOfTypes(fromType), canonicalName(toType)),
						param,
						SARL_FORMAL_PARAMETER__DEFAULT_VALUE,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						INCOMPATIBLE_TYPES,
						canonicalName(fromType),
						canonicalName(toType));
			}
		}
	}

	/** Check if the default values has not a reference to the not final fields.
	 *
	 * @param param the formal parameter to check.
	 */
	@Check
	public void checkDefaultValueFieldReference(SarlFormalParameter param) {
		final XExpression defaultValue = param.getDefaultValue();
		if (defaultValue != null) {
			final Iterator<XFeatureCall> iter;
			if (defaultValue instanceof XFeatureCall) {
				iter = Iterators.singletonIterator((XFeatureCall) defaultValue);
			} else {
				iter = Iterators.filter(defaultValue.eAllContents(), XFeatureCall.class);
			}
			while (iter.hasNext()) {
				final XFeatureCall call = iter.next();
				final JvmIdentifiableElement feature = call.getFeature();
				String invalidFieldName = null;
				if (feature instanceof XtendField) {
					final XtendField field = (XtendField) feature;
					if (!field.isFinal()) {
						invalidFieldName = field.getName();
					}
				} else if (feature instanceof JvmField) {
					final JvmField field = (JvmField) feature;
					if (!field.isFinal()) {
						invalidFieldName = field.getSimpleName();
					}
				}
				if (invalidFieldName != null) {
					error(MessageFormat.format(
							Messages.SARLValidator_19,
							invalidFieldName),
							call,
							XbasePackage.Literals.XABSTRACT_FEATURE_CALL__FEATURE,
							ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
							FORBIDDEN_REFERENCE);
				}
			}
		}
	}

	/** Check if the given action has a valid name.
	 *
	 * @param action the action to test.
	 * @see SARLFeatureNameValidator
	 */
	@Check(CheckType.FAST)
	public void checkActionName(SarlAction action) {
		final JvmOperation inferredType = this.associations.getDirectlyInferredOperation(action);
		final QualifiedName name = QualifiedName.create(inferredType.getQualifiedName('.').split("\\.")); //$NON-NLS-1$
		if (this.featureNames.isDisallowedName(name)) {
			final String validName = Utils.fixHiddenMember(action.getName());
			error(MessageFormat.format(
					Messages.SARLValidator_39,
					action.getName()),
					action,
					XTEND_FUNCTION__NAME,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					INVALID_MEMBER_NAME,
					validName);
		} else if (!isIgnored(DISCOURAGED_FUNCTION_NAME)
				&& this.featureNames.isDiscouragedName(name)) {
			warning(MessageFormat.format(
					Messages.SARLValidator_39,
					action.getName()),
					action,
					XTEND_FUNCTION__NAME,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					DISCOURAGED_FUNCTION_NAME);
		}
	}

	/** Check if the given field has a valid name.
	 *
	 * @param field the field to test.
	 * @see SARLFeatureNameValidator
	 */
	@Check(CheckType.FAST)
	public void checkFieldName(SarlField field) {
		final JvmField inferredType = this.associations.getJvmField(field);
		final QualifiedName name = Utils.getQualifiedName(inferredType);
		if (this.featureNames.isDisallowedName(name)) {
			final String validName = Utils.fixHiddenMember(field.getName());
			error(MessageFormat.format(
					Messages.SARLValidator_41,
					field.getName()),
					field,
					XTEND_FIELD__NAME,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					VARIABLE_NAME_DISALLOWED,
					validName);
		} else if (this.grammarAccess.getOccurrenceKeyword().equals(field.getName())) {
			error(MessageFormat.format(
					Messages.SARLValidator_41,
					this.grammarAccess.getOccurrenceKeyword()),
					field,
					XTEND_FIELD__NAME,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					VARIABLE_NAME_DISALLOWED);
		}
	}

	/** Check if the given field has a name that is shadowing an inherited field.
	 *
	 * @param field the field to test.
	 */
	@Check
	public void checkFieldNameShadowing(SarlField field) {
		if (!isIgnored(VARIABLE_NAME_SHADOWING)
				&& !Utils.isHiddenMember(field.getName())) {
			final JvmField inferredField = this.associations.getJvmField(field);
			final Map<String, JvmField> inheritedFields = new TreeMap<>();
			Utils.populateInheritanceContext(
					inferredField.getDeclaringType(),
					null, null,
					inheritedFields,
					null, null,
					this.sarlActionSignatures);

			final JvmField inheritedField = inheritedFields.get(field.getName());
			if (inheritedField != null) {
				int nameIndex = 0;
				String newName = field.getName() + nameIndex;
				while (inheritedFields.containsKey(newName)) {
					++nameIndex;
					newName = field.getName() + nameIndex;
				}
				addIssue(MessageFormat.format(
						Messages.SARLValidator_42,
						field.getName(),
						inferredField.getDeclaringType().getQualifiedName(),
						inheritedField.getQualifiedName()),
						field,
						XTEND_FIELD__NAME,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						VARIABLE_NAME_SHADOWING,
						newName);
			}
		}
	}

	/** Check if the given parameter has a valid name.
	 *
	 * @param parameter the parameter to test.
	 * @see SARLFeatureNameValidator
	 */
	@Check(CheckType.FAST)
	public void checkParameterName(SarlFormalParameter parameter) {
		if (this.grammarAccess.getOccurrenceKeyword().equals(parameter.getName())) {
			error(MessageFormat.format(
					Messages.SARLValidator_14,
					this.grammarAccess.getOccurrenceKeyword()),
					parameter,
					XtendPackage.Literals.XTEND_PARAMETER__NAME,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					VARIABLE_NAME_DISALLOWED);
		}
	}

	/** Check if the given local variable has a valid name.
	 *
	 * @param variable the variable to test.
	 * @see SARLFeatureNameValidator
	 */
	@Check(CheckType.FAST)
	public void checkParameterName(XVariableDeclaration variable) {
		if (this.grammarAccess.getOccurrenceKeyword().equals(variable.getName())) {
			error(MessageFormat.format(
					Messages.SARLValidator_15,
					this.grammarAccess.getOccurrenceKeyword()),
					variable,
					XbasePackage.Literals.XVARIABLE_DECLARATION__NAME,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					VARIABLE_NAME_DISALLOWED);
		}
	}

	private String returnType(EObject source) {
		if (source instanceof XtendFunction) {
			final XtendFunction fct = (XtendFunction) source;
			final JvmTypeReference type = fct.getReturnType();
			if (type != null) {
				return type.getIdentifier();
			}
		}
		return this.grammarAccess.getVoidKeyword();
	}

	/** Caution: This function is overridden for translating the MISSING_OVERRIDE error into a warning,
	 * and emit a warning when a return type should be specified.
	 *
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings({"checkstyle:cyclomaticcomplexity", "checkstyle:npathcomplexity"})
	protected void doCheckFunctionOverrides(EObject sourceElement, IResolvedOperation resolved,
			List<IResolvedOperation> allInherited) {
		boolean overrideProblems = false;
		List<IResolvedOperation> exceptionMismatch = null;
		for (final IResolvedOperation inherited: allInherited) {
			if (inherited.getOverrideCheckResult().hasProblems()) {
				overrideProblems = true;
				final EnumSet<OverrideCheckDetails> details = inherited.getOverrideCheckResult().getDetails();
				if (details.contains(OverrideCheckDetails.IS_FINAL)) {
					error(MessageFormat.format(Messages.SARLValidator_43, inherited.getSimpleSignature()),
							sourceElement,
							nameFeature(sourceElement), OVERRIDDEN_FINAL);
				} else if (details.contains(OverrideCheckDetails.REDUCED_VISIBILITY)) {
					error(MessageFormat.format(Messages.SARLValidator_44,
							inherited.getSimpleSignature()),
							sourceElement, nameFeature(sourceElement), OVERRIDE_REDUCES_VISIBILITY);
				} else if (details.contains(OverrideCheckDetails.EXCEPTION_MISMATCH)) {
					if (exceptionMismatch == null) {
						exceptionMismatch = Lists.newArrayListWithCapacity(allInherited.size());
					}
					exceptionMismatch.add(inherited);
				} else if (details.contains(OverrideCheckDetails.RETURN_MISMATCH)) {
					error(MessageFormat.format(Messages.SARLValidator_45,
							inherited.getSimpleSignature(),
							inherited.getResolvedReturnType().getIdentifier(),
							returnType(sourceElement)),
							sourceElement,
							returnTypeFeature(sourceElement), INCOMPATIBLE_RETURN_TYPE,
							inherited.getResolvedReturnType().getIdentifier());
				}
			} else if (!isIgnored(RETURN_TYPE_SPECIFICATION_IS_RECOMMENDED, sourceElement)
					&& sourceElement instanceof SarlAction) {
				final SarlAction function = (SarlAction) sourceElement;
				if (function.getReturnType() == null && !inherited.getResolvedReturnType().isPrimitiveVoid()) {
					warning(MessageFormat.format(Messages.SARLValidator_46,
							resolved.getResolvedReturnType().getHumanReadableName()),
							sourceElement,
							returnTypeFeature(sourceElement), RETURN_TYPE_SPECIFICATION_IS_RECOMMENDED,
							inherited.getResolvedReturnType().getIdentifier());
				}
			}
		}
		if (exceptionMismatch != null) {
			createExceptionMismatchError(resolved, sourceElement, exceptionMismatch);
		}
		if (sourceElement instanceof SarlAction) {
			final SarlAction function = (SarlAction) sourceElement;
			if (!overrideProblems && !function.isOverride() && !function.isStatic()
					&& !isIgnored(MISSING_OVERRIDE, sourceElement)) {
				warning(MessageFormat.format(Messages.SARLValidator_47,
						resolved.getSimpleSignature(),
						getDeclaratorName(resolved)),
						function,
						XTEND_FUNCTION__NAME, MISSING_OVERRIDE);
			}
			if (!overrideProblems && function.isOverride() && function.isStatic()) {
				for (final IResolvedOperation inherited: allInherited) {
					error(MessageFormat.format(Messages.SARLValidator_48,
							resolved.getSimpleSignature(),
							getDeclaratorName(resolved),
							resolved.getSimpleSignature(),
							getDeclaratorName(inherited)),
							function, XTEND_FUNCTION__NAME,
							function.getModifiers().indexOf(Messages.SARLValidator_49),
							OBSOLETE_OVERRIDE);
				}
			}
		}
	}

	private boolean checkRedundantInterfaceInSameType(
			XtendTypeDeclaration element,
			EReference structuralElement,
			LightweightTypeReference lightweightInterfaceReference,
			List<LightweightTypeReference> knownInterfaces) {
		final String iid = lightweightInterfaceReference.getUniqueIdentifier();
		int index = 1;
		for (final LightweightTypeReference previousInterface : knownInterfaces) {
			final String pid = previousInterface.getUniqueIdentifier();
			if (Objects.equal(iid, pid)) {
				error(MessageFormat.format(
						Messages.SARLValidator_50,
						canonicalName(lightweightInterfaceReference)),
						element,
						structuralElement,
						// The index of the element to highlight in the super-types
						index,
						REDUNDANT_INTERFACE_IMPLEMENTATION,
						canonicalName(lightweightInterfaceReference),
						"pre"); //$NON-NLS-1$
				return true;
			}
			if (!isIgnored(REDUNDANT_INTERFACE_IMPLEMENTATION, element)) {
				if (memberOfTypeHierarchy(previousInterface, lightweightInterfaceReference)) {
					addIssue(MessageFormat.format(
							Messages.SARLValidator_52,
							canonicalName(lightweightInterfaceReference),
							canonicalName(previousInterface)),
							element,
							structuralElement,
							// The index of the element to highlight in the super-types
							index,
							REDUNDANT_INTERFACE_IMPLEMENTATION,
							canonicalName(lightweightInterfaceReference),
							"pre"); //$NON-NLS-1$
					return true;
				}
			}
			++index;
		}
		return false;
	}

	private void checkRedundantInterfaces(
			XtendTypeDeclaration element,
			EReference structuralElement,
			Iterable<? extends JvmTypeReference> interfaces,
			Iterable<? extends JvmTypeReference> superTypes) {
		final List<LightweightTypeReference> knownInterfaces = CollectionLiterals.newArrayList();
		for (final JvmTypeReference interfaceRef : interfaces) {
			final LightweightTypeReference lightweightInterfaceReference = toLightweightTypeReference(interfaceRef);
			// Detect if an interface is specified two types for the same type.
			if (!checkRedundantInterfaceInSameType(
					element, structuralElement,
					lightweightInterfaceReference,
					knownInterfaces)) {
				// Check the interface against the super-types
				if (superTypes != null && !isIgnored(REDUNDANT_INTERFACE_IMPLEMENTATION, element)) {
					for (final JvmTypeReference superType : superTypes) {
						final LightweightTypeReference lightweightSuperType = toLightweightTypeReference(superType);
						if (memberOfTypeHierarchy(lightweightSuperType, lightweightInterfaceReference)) {
							addIssue(MessageFormat.format(
									Messages.SARLValidator_52,
									canonicalName(lightweightInterfaceReference),
									canonicalName(lightweightSuperType)),
									element,
									structuralElement,
									// The index of the element to highlight in the super-types
									knownInterfaces.size(),
									REDUNDANT_INTERFACE_IMPLEMENTATION,
									canonicalName(lightweightInterfaceReference),
									"unknow"); //$NON-NLS-1$
						}
					}
				}
			}
			// Prepare next loop
			knownInterfaces.add(lightweightInterfaceReference);
		}
	}

	/** Check if implemented interfaces of a skill are redundant.
	 *
	 * @param skill the skill.
	 */
	@Check
	public void checkRedundantImplementedInterfaces(SarlSkill skill) {
		checkRedundantInterfaces(
				skill,
				SARL_SKILL__IMPLEMENTS,
				skill.getImplements(),
				Utils.singletonList(skill.getExtends()));
	}

	/** Check if implemented interfaces of a Xtend Class are redundant.
	 *
	 * @param xtendClass the class.
	 */
	@Check
	public void checkRedundantImplementedInterfaces(SarlClass xtendClass) {
		checkRedundantInterfaces(
				xtendClass,
				XTEND_CLASS__IMPLEMENTS,
				xtendClass.getImplements(),
				Utils.singletonList(xtendClass.getExtends()));
	}

	/** Check if implemented interfaces of a Xtend Interface are redundant.
	 *
	 * @param xtendInterface the interface.
	 */
	@Check
	public void checkRedundantImplementedInterfaces(SarlInterface xtendInterface) {
		checkRedundantInterfaces(
				xtendInterface,
				XTEND_INTERFACE__EXTENDS,
				xtendInterface.getExtends(),
				Collections.<JvmTypeReference>emptyList());
	}

	/** Check the type of the behavior unit's guard.
	 *
	 * @param behaviorUnit the behavior unit.
	 */
	@Check(CheckType.FAST)
	public void checkBehaviorUnitGuardType(SarlBehaviorUnit behaviorUnit) {
		final XExpression guard = behaviorUnit.getGuard();
		if (guard != null) {
			if (this.operationHelper.hasSideEffects(null, guard)) {
				error(Messages.SARLValidator_53,
						guard,
						null,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						org.eclipse.xtext.xbase.validation.IssueCodes.INVALID_INNER_EXPRESSION);
				return;
			}
			if (guard instanceof XBooleanLiteral) {
				final XBooleanLiteral booleanLiteral = (XBooleanLiteral) guard;
				if (booleanLiteral.isIsTrue()) {
					if (!isIgnored(DISCOURAGED_BOOLEAN_EXPRESSION)) {
						addIssue(Messages.SARLValidator_54,
								booleanLiteral,
								null,
								ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
								DISCOURAGED_BOOLEAN_EXPRESSION);
					}
				} else if (!isIgnored(UNREACHABLE_BEHAVIOR_UNIT)) {
					addIssue(Messages.SARLValidator_55,
							behaviorUnit,
							null,
							ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
							UNREACHABLE_BEHAVIOR_UNIT,
							behaviorUnit.getName().getSimpleName());
				}
				return;
			}

			final LightweightTypeReference fromType = getActualType(guard);
			if (!fromType.isAssignableFrom(Boolean.TYPE)) {
				error(MessageFormat.format(
						Messages.SARLValidator_38,
						getNameOfTypes(fromType), boolean.class.getName()),
						guard,
						null,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						INCOMPATIBLE_TYPES);
			}
		}
	}

	/** Check the type of the capacity uses.
	 *
	 * @param uses the capacity uses.
	 */
	@Check(CheckType.FAST)
	public void checkCapacityTypeForUses(SarlCapacityUses uses) {
		for (final JvmParameterizedTypeReference usedType : uses.getCapacities()) {
			final LightweightTypeReference ref = toLightweightTypeReference(usedType);
			if (ref != null && !this.inheritanceHelper.isSarlCapacity(ref)) {
				error(MessageFormat.format(
						Messages.SARLValidator_57,
						usedType.getQualifiedName(),
						Messages.SARLValidator_58,
						this.grammarAccess.getUsesKeyword()),
						usedType,
						null,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						INVALID_CAPACITY_TYPE,
						usedType.getSimpleName());
			}
		}
	}

	/** Check the types of the parameters of the "fires" statement.
	 *
	 * @param action the signature that contains the "fires" statement.
	 */
	@Check(CheckType.FAST)
	public void checkActionFires(SarlAction action) {
		for (final JvmTypeReference event : action.getFiredEvents()) {
			final LightweightTypeReference ref = toLightweightTypeReference(event);
			if (ref != null && !this.inheritanceHelper.isSarlEvent(ref)) {
				error(MessageFormat.format(
						Messages.SARLValidator_57,
						event.getQualifiedName(),
						Messages.SARLValidator_62,
						this.grammarAccess.getFiresKeyword()),
						event,
						null,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						INVALID_FIRING_EVENT_TYPE,
						event.getSimpleName());
			}
		}
	}

	/** Check the super type.
	 *
	 * @param element the child type.
	 * @param feature the syntactic feature related to the supertypes.
	 * @param superTypes the current super types.
	 * @param expectedType the expected root type.
	 * @param onlySubTypes if <code>true</code> only the subtype of the <code>expectedType</code> are valid;
	 * <code>false</code> if the <code>expectedType</code> is allowed.
	 * @return the count of supertypes.
	 */
	@SuppressWarnings({"checkstyle:cyclomaticcomplexity"})
	protected int checkSuperTypes(
			XtendTypeDeclaration element,
			EReference feature,
			List<? extends JvmTypeReference> superTypes,
			Class<?> expectedType,
			boolean onlySubTypes) {
		int nbSuperTypes = 0;
		final JvmDeclaredType inferredType = this.associations.getInferredType(element);
		if (inferredType instanceof JvmGenericType) {
			final LinkedList<JvmTypeReference> inferredSuperTypes = CollectionLiterals.newLinkedList();
			inferredSuperTypes.addAll(inferredType.getSuperTypes());
			final boolean isExpectingInterface = expectedType.isInterface();
			int superTypeIndex = 0;
			for (final JvmTypeReference superType : superTypes) {
				boolean success = true;
				final JvmType jvmSuperType = (superType == null) ? null : superType.getType();
				if (jvmSuperType != null) {
					final JvmTypeReference inferredSuperType =
							(inferredSuperTypes.isEmpty()) ? null : inferredSuperTypes.removeFirst();
					final LightweightTypeReference lighweightSuperType = toLightweightTypeReference(superType);
					if (!(jvmSuperType instanceof JvmGenericType)
							|| (isExpectingInterface != ((JvmGenericType) jvmSuperType).isInterface())) {
						if (isExpectingInterface) {
							error(
									MessageFormat.format(Messages.SARLValidator_63, Messages.SARLValidator_64),
									feature,
									superTypeIndex,
									INTERFACE_EXPECTED,
									jvmSuperType.getIdentifier());
						} else {
							error(
									MessageFormat.format(Messages.SARLValidator_63, Messages.SARLValidator_66),
									feature,
									superTypeIndex,
									CLASS_EXPECTED,
									jvmSuperType.getIdentifier());
						}
						success = false;
					} else if (isFinal(lighweightSuperType)) {
						error(Messages.SARLValidator_67,
								feature,
								superTypeIndex,
								OVERRIDDEN_FINAL,
								inferredType.getIdentifier(),
								jvmSuperType.getIdentifier());
						success = false;
					} else if (!lighweightSuperType.isSubtypeOf(expectedType)
							|| (onlySubTypes && lighweightSuperType.isType(expectedType))) {
						if (onlySubTypes) {
							error(MessageFormat.format(Messages.SARLValidator_68, expectedType.getName()),
									feature,
									superTypeIndex,
									INVALID_EXTENDED_TYPE,
									jvmSuperType.getIdentifier());
						} else {
							error(MessageFormat.format(Messages.SARLValidator_69, expectedType.getName()),
									feature,
									superTypeIndex,
									INVALID_EXTENDED_TYPE,
									jvmSuperType.getIdentifier());
						}
						success = false;
					} else if (inferredSuperType == null
							|| !Objects.equal(inferredSuperType.getIdentifier(), jvmSuperType.getIdentifier())
							|| Objects.equal(inferredType.getIdentifier(), jvmSuperType.getIdentifier())
							|| hasCycleInHierarchy((JvmGenericType) inferredType, Sets.<JvmGenericType>newHashSet())) {
						error(MessageFormat.format(Messages.SARLValidator_70,
								inferredType.getQualifiedName()),
								feature,
								superTypeIndex,
								CYCLIC_INHERITANCE,
								jvmSuperType.getIdentifier());
						success = false;
					}
				} else if (superType != null) {
					error(MessageFormat.format(Messages.SARLValidator_70,
							inferredType.getQualifiedName()),
							feature,
							superTypeIndex,
							CYCLIC_INHERITANCE,
							superType.getIdentifier());
					success = false;
				}
				checkWildcardSupertype(element, superType, feature, superTypeIndex);
				++superTypeIndex;
				if (success) {
					++nbSuperTypes;
				}
			}
		}
		return nbSuperTypes;
	}

	/** Check if the supertype of the given capacity is a subtype of Capacity.
	 *
	 * @param capacity the type to test.
	 */
	@Check(CheckType.FAST)
	public void checkSuperTypes(SarlCapacity capacity) {
		checkSuperTypes(
				capacity,
				SARL_CAPACITY__EXTENDS,
				capacity.getExtends(),
				Capacity.class,
				false);
	}

	/** Check if the supertype of the given skill is a subtype of Skill.
	 *
	 * @param skill the type to test.
	 */
	@Check(CheckType.FAST)
	public void checkSuperType(SarlSkill skill) {
		final int nbSuperTypes = checkSuperTypes(
				skill,
				SARL_SKILL__EXTENDS,
				Utils.singletonList(skill.getExtends()),
				Skill.class,
				false);
		checkImplementedTypes(
				skill,
				SARL_SKILL__IMPLEMENTS,
				skill.getImplements(),
				Capacity.class,
				nbSuperTypes > 0 ? 0 : 1,
						true);
	}

	/** Check if the supertype of the given event is a subtype of Event.
	 *
	 * @param event the type to test.
	 */
	@Check(CheckType.FAST)
	public void checkSuperType(SarlEvent event) {
		checkSuperTypes(
				event,
				SARL_EVENT__EXTENDS,
				Utils.singletonList(event.getExtends()),
				Event.class,
				false);
	}

	/** Check if the supertype of the given behavior is a subtype of Behavior.
	 *
	 * @param behavior the type to test.
	 */
	@Check(CheckType.FAST)
	public void checkSuperType(SarlBehavior behavior) {
		checkSuperTypes(
				behavior,
				SARL_BEHAVIOR__EXTENDS,
				Utils.singletonList(behavior.getExtends()),
				Behavior.class,
				false);
	}

	/** Check if the supertype of the given agent is a subtype of Agent.
	 *
	 * @param agent the type to test.
	 */
	@Check(CheckType.FAST)
	public void checkSuperType(SarlAgent agent) {
		checkSuperTypes(
				agent,
				SARL_AGENT__EXTENDS,
				Utils.singletonList(agent.getExtends()),
				Agent.class,
				false);
	}

	/** Check the implemeted type.
	 *
	 * @param element the child type.
	 * @param feature the syntactic feature related to the supertypes.
	 * @param implementedTypes the current super types.
	 * @param expectedType the expected root type.
	 * @param mandatoryNumberOfTypes the minimal number of implemented types.
	 * @param onlySubTypes if <code>true</code> only the subtype of the <code>expectedType</code> are valid;
	 * <code>false</code> if the <code>expectedType</code> is allowed.
	 * @return the count of supertypes.
	 */
	protected boolean checkImplementedTypes(
			XtendTypeDeclaration element,
			EReference feature,
			List<? extends JvmTypeReference> implementedTypes,
			Class<?> expectedType,
			int mandatoryNumberOfTypes,
			boolean onlySubTypes) {
		boolean success = true;
		int nb = 0;
		int index = 0;
		for (final JvmTypeReference superType : implementedTypes) {
			final LightweightTypeReference  ref = toLightweightTypeReference(superType);
			if (ref != null
					&& (!ref.isInterfaceType() || !ref.isSubtypeOf(expectedType)
							|| (onlySubTypes && ref.isType(expectedType)))) {
				final String msg;
				if (onlySubTypes) {
					msg = Messages.SARLValidator_72;
				} else {
					msg = Messages.SARLValidator_73;
				}
				error(MessageFormat.format(
						msg,
						superType.getQualifiedName(),
						expectedType.getName(),
						element.getName()),
						element,
						feature,
						index,
						INVALID_IMPLEMENTED_TYPE,
						superType.getSimpleName());
				success = false;
			} else {
				++nb;
			}
			++index;
		}
		if (nb < mandatoryNumberOfTypes) {
			error(MessageFormat.format(
					Messages.SARLValidator_74,
					expectedType.getName(),
					element.getName()),
					element,
					feature,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					MISSING_TYPE);
			success = false;
		}
		return success;
	}

	/** Check if the parameter of the bahavior unit is an event.
	 *
	 * @param behaviorUnit the behavior unit to test.
	 */
	@Check(CheckType.FAST)
	public void checkBehaviorUnitEventType(SarlBehaviorUnit behaviorUnit) {
		final JvmTypeReference event = behaviorUnit.getName();
		final LightweightTypeReference ref = toLightweightTypeReference(event);
		if (ref == null || !this.inheritanceHelper.isSarlEvent(ref)) {
			error(MessageFormat.format(
					Messages.SARLValidator_75,
					event.getQualifiedName(),
					Messages.SARLValidator_62,
					this.grammarAccess.getOnKeyword()),
					event,
					null,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					TYPE_BOUNDS_MISMATCH);
		}
	}

	/** Check if a capacity has a feature defined inside.
	 *
	 * @param capacity the capacity to test.
	 */
	@Check(CheckType.FAST)
	public void checkCapacityFeatures(SarlCapacity capacity) {
		if (capacity.getMembers().isEmpty()) {
			if (!isIgnored(DISCOURAGED_CAPACITY_DEFINITION)) {
				addIssue(Messages.SARLValidator_77,
						capacity,
						null,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						DISCOURAGED_CAPACITY_DEFINITION,
						capacity.getName(),
						"aFunction"); //$NON-NLS-1$
			}
		}
	}

	/** Check for unused capacities.
	 *
	 * @param uses the capacity use declaration.
	 */
	@Check(CheckType.NORMAL)
	public void checkUnusedCapacities(SarlCapacityUses uses) {
		if (!isIgnored(UNUSED_AGENT_CAPACITY)) {
			final XtendTypeDeclaration container = uses.getDeclaringType();
			final JvmDeclaredType jvmContainer = (JvmDeclaredType) this.associations.getPrimaryJvmElement(container);
			final Map<String, JvmOperation> importedFeatures = CollectionLiterals.newHashMap();
			for (final JvmOperation operation : jvmContainer.getDeclaredOperations()) {
				if (Utils.isNameForHiddenCapacityImplementationCallingMethod(operation.getSimpleName())) {
					importedFeatures.put(operation.getSimpleName(), operation);
				}
			}

			final boolean isSkill = container instanceof SarlSkill;
			int index = 0;
			for (final JvmTypeReference capacity : uses.getCapacities()) {
				final LightweightTypeReference lreference = toLightweightTypeReference(capacity);
				if (isSkill && lreference.isAssignableFrom(jvmContainer)) {
					addIssue(MessageFormat.format(
							Messages.SARLValidator_22,
							capacity.getSimpleName()),
							uses,
							SARL_CAPACITY_USES__CAPACITIES,
							index, UNUSED_AGENT_CAPACITY,
							capacity.getSimpleName());
				} else {
					final String fieldName = Utils.createNameForHiddenCapacityImplementationAttribute(capacity.getIdentifier());
					final String operationName = Utils.createNameForHiddenCapacityImplementationCallingMethodFromFieldName(fieldName);
					final JvmOperation operation = importedFeatures.get(operationName);
					if (operation != null && !isLocallyUsed(operation, container)) {
						addIssue(MessageFormat.format(
								Messages.SARLValidator_78,
								capacity.getSimpleName()),
								uses,
								SARL_CAPACITY_USES__CAPACITIES,
								index, UNUSED_AGENT_CAPACITY,
								capacity.getSimpleName());
					}
				}
				++index;
			}
		}
	}

	private static Set<String> doGetPreviousCapacities(SarlCapacityUses uses, Iterator<XtendMember> iterator) {
		boolean continueToFill = true;
		final Set<String> capacityUses = CollectionLiterals.newTreeSet((Comparator<String>) null);
		while (continueToFill && iterator.hasNext()) {
			final XtendMember elt = iterator.next();
			if (elt instanceof SarlCapacityUses) {
				final SarlCapacityUses usesElt = (SarlCapacityUses) elt;
				if (usesElt == uses) {
					continueToFill = false;
				} else {
					for (final JvmTypeReference use : usesElt.getCapacities()) {
						capacityUses.add(use.getIdentifier());
					}
				}
			}
		}
		return capacityUses;
	}

	/** Check for multiple capacity use declaration.
	 *
	 * @param uses the capacity use declaration.
	 */
	@Check(CheckType.NORMAL)
	public void checkMultipleCapacityUses(SarlCapacityUses uses) {
		if (!isIgnored(REDUNDANT_CAPACITY_USE)) {
			final XtendTypeDeclaration declaringType = uses.getDeclaringType();
			if (declaringType != null) {
				final Set<String> previousCapacityUses = doGetPreviousCapacities(uses,
						declaringType.getMembers().iterator());
				int index = 0;
				for (final JvmTypeReference capacity : uses.getCapacities()) {
					if (previousCapacityUses.contains(capacity.getIdentifier())) {
						addIssue(MessageFormat.format(
								Messages.SARLValidator_79,
								capacity.getSimpleName()),
								uses,
								SARL_CAPACITY_USES__CAPACITIES,
								index,
								REDUNDANT_CAPACITY_USE,
								capacity.getSimpleName());
					} else {
						previousCapacityUses.add(capacity.getIdentifier());
					}
					++index;
				}
			}
		}
	}

	/** Check for abstract methods.
	 *
	 * <p>Override the Xtend behavior for: <ul>
	 * <li>not generating an error when a return type is missed. Indeed, the return type if "void" by default.</li>
	 * <li>generating a warning when "abstract" is missed.</li>
	 * </ul>
	 */
	@Check
	@Override
	@SuppressWarnings("checkstyle:cyclomaticcomplexity")
	public void checkAbstract(XtendFunction function) {
		final XtendTypeDeclaration declarator = function.getDeclaringType();
		if (function.getExpression() == null || function.isAbstract()) {
			if (declarator instanceof XtendClass || declarator.isAnonymous()
					|| declarator instanceof SarlAgent || declarator instanceof SarlBehavior
					|| declarator instanceof SarlSkill) {
				if (function.isDispatch()) {
					error(MessageFormat.format(
							Messages.SARLValidator_80,
							function.getName(), this.localClassAwareTypeNames.getReadableName(declarator)),
							XTEND_FUNCTION__NAME, -1, DISPATCH_FUNCTIONS_MUST_NOT_BE_ABSTRACT);
					return;
				}
				if (function.getCreateExtensionInfo() != null) {
					error(MessageFormat.format(
							Messages.SARLValidator_81,
							function.getName(), this.localClassAwareTypeNames.getReadableName(declarator)),
							XTEND_FUNCTION__NAME, -1, CREATE_FUNCTIONS_MUST_NOT_BE_ABSTRACT);
					return;
				}
				if (declarator.isAnonymous()) {
					error(MessageFormat.format(
							Messages.SARLValidator_82,
							function.getName(), this.localClassAwareTypeNames.getReadableName(declarator)),
							XTEND_FUNCTION__NAME, -1, MISSING_ABSTRACT_IN_ANONYMOUS);
				} else {
					final boolean isAbstract;
					if (declarator instanceof XtendClass) {
						isAbstract = ((XtendClass) declarator).isAbstract();
					} else if (declarator instanceof SarlAgent) {
						isAbstract = ((SarlAgent) declarator).isAbstract();
					} else if (declarator instanceof SarlBehavior) {
						isAbstract = ((SarlBehavior) declarator).isAbstract();
					} else if (declarator instanceof SarlSkill) {
						isAbstract = ((SarlSkill) declarator).isAbstract();
					} else {
						return;
					}
					if (!isAbstract && !function.isNative()) {
						error(MessageFormat.format(
								Messages.SARLValidator_82,
								function.getName(), this.localClassAwareTypeNames.getReadableName(declarator)),
								XTEND_FUNCTION__NAME, -1, MISSING_ABSTRACT);
						return;
					}
				}

				if (!function.getModifiers().contains("abstract")) { //$NON-NLS-1$
					warning(MessageFormat.format(
							Messages.SARLValidator_84,
							function.getName(), this.localClassAwareTypeNames.getReadableName(declarator)),
							XTEND_FUNCTION__NAME, -1, MISSING_ABSTRACT,
							function.getName(),
							this.localClassAwareTypeNames.getReadableName(declarator));
				}

			} else if (declarator instanceof XtendInterface || declarator instanceof SarlCapacity) {
				if (function.getCreateExtensionInfo() != null) {
					error(MessageFormat.format(
							Messages.SARLValidator_85,
							function.getName()),
							XTEND_FUNCTION__NAME, -1, CREATE_FUNCTIONS_MUST_NOT_BE_ABSTRACT);
					return;
				}
			}
		} else if (declarator instanceof XtendInterface || declarator instanceof SarlCapacity) {
			if (!getGeneratorConfig(function).getJavaSourceVersion().isAtLeast(JAVA8)) {
				error(Messages.SARLValidator_86, XTEND_FUNCTION__NAME, -1, ABSTRACT_METHOD_WITH_BODY);
				return;
			}
		}
	}

	/** Check for reserved annotations.
	 *
	 * @param annotationTarget thee target to test.
	 */
	@Check
	public void checkReservedAnnotation(XtendAnnotationTarget annotationTarget) {
		if (!isIgnored(IssueCodes.USED_RESERVED_SARL_ANNOTATION)) {
			if (annotationTarget.getAnnotations().isEmpty() || !isRelevantAnnotationTarget(annotationTarget)) {
				return;
			}
			final QualifiedName reservedPackage = this.qualifiedNameConverter.toQualifiedName(
					EarlyExit.class.getPackage().getName());
			final String earlyExitAnnotation = EarlyExit.class.getName();
			for (final XAnnotation annotation : annotationTarget.getAnnotations()) {
				final JvmType type = annotation.getAnnotationType();
				if (type != null && !type.eIsProxy()) {
					if (Objects.equal(type.getIdentifier(), earlyExitAnnotation)) {
						// Special case: EarlyExit is allowed on events for declaring early-exit events
						if (!(annotationTarget instanceof SarlEvent)) {
							addIssue(
									MessageFormat.format(Messages.SARLValidator_87, type.getSimpleName()),
									annotation,
									IssueCodes.USED_RESERVED_SARL_ANNOTATION);
						}
					} else {
						final QualifiedName annotationName = this.qualifiedNameConverter.toQualifiedName(
								type.getIdentifier());
						if (annotationName.startsWith(reservedPackage)) {
							addIssue(
									MessageFormat.format(Messages.SARLValidator_87, type.getSimpleName()),
									annotation,
									IssueCodes.USED_RESERVED_SARL_ANNOTATION);
						}
					}
				}
			}
		}
	}

	/** Check for {@code @Inline} annotation usage.
	 *
	 * @param annotationTarget thee target to test.
	 */
	@Check
	public void checkManualInlineDefinition(XtendAnnotationTarget annotationTarget) {
		if (!isIgnored(IssueCodes.MANUAL_INLINE_DEFINITION)) {
			if (annotationTarget.getAnnotations().isEmpty() || !isRelevantAnnotationTarget(annotationTarget)) {
				return;
			}
			final String inlineAnnotation = Inline.class.getName();
			for (final XAnnotation annotation : annotationTarget.getAnnotations()) {
				final JvmType type = annotation.getAnnotationType();
				if (type != null && !type.eIsProxy()) {
					if (Objects.equal(type.getIdentifier(), inlineAnnotation)) {
						addIssue(
								Messages.SARLValidator_16,
								annotation,
								IssueCodes.MANUAL_INLINE_DEFINITION);
					}
				}
			}
		}
	}

	/** Replies the member feature call that is the root of a sequence of member feature calls.
	 *
	 * <p>While the current feature call is the actual receiver of a member feature call, the sequence is still active.
	 * Otherwise, the sequence is stopped.
	 *
	 * @param leaf the expression at the leaf of the feature call.
	 * @param container the top most container that cannot be part of the sequence. Could be {@code null}.
	 * @param feedback the function that is invoked on each discovered member feature call within the sequence. Could be {@code null}.
	 * @return the root of a member feature call sequence.
	 */
	protected static XMemberFeatureCall getRootOfMemberFeatureCallSequence(EObject leaf, EObject container,
			Procedure1<XMemberFeatureCall> feedback) {
		EObject call = leaf;
		EObject obj = EcoreUtil2.getContainerOfType(leaf.eContainer(), XExpression.class);
		while (obj != null && (container == null || obj != container)) {
			if (!(obj instanceof XMemberFeatureCall)) {
				obj = null;
			} else {
				final EObject previous = call;
				final XMemberFeatureCall fcall = (XMemberFeatureCall) obj;
				call = fcall;
				if (fcall.getActualReceiver() == previous) {
					if (feedback != null) {
						feedback.apply(fcall);
					}
					obj = EcoreUtil2.getContainerOfType(call.eContainer(), XExpression.class);
				} else {
					obj = null;
				}
			}
		}
		return call instanceof XMemberFeatureCall ? (XMemberFeatureCall) call : null;
	}

	@SuppressWarnings({"checkstyle:nestedifdepth", "checkstyle:npathcomplexity", "checkstyle:cyclomaticcomplexity"})
	private void checkUnmodifiableFeatureAccess(boolean enableWarning, EObject readOnlyKeyword, String keywordName) {
		final OutParameter<EObject> container = new OutParameter<>();
		final OutParameter<EObject> directContainerChild = new OutParameter<>();

		final OutParameter<Boolean> failure = new OutParameter<>(false);
		final XMemberFeatureCall sequence = getRootOfMemberFeatureCallSequence(readOnlyKeyword, null, it -> {
			// Function call: if one of the functions called on the read-only keyword is not pure => WARNING
			if (getExpressionHelper().hasSideEffects(it)) {
				addIssue(MessageFormat.format(Messages.SARLValidator_11, keywordName),
						it,
						IssueCodes.DISCOURAGED_OCCURRENCE_READONLY_USE);
				failure.set(true);
			}
		});
		if (failure.get().booleanValue()) {
			return;
		}
		final EObject expression = sequence == null ? readOnlyKeyword : sequence;

		if (Utils.getContainerOfType(expression, container, directContainerChild,
				XAssignment.class, XVariableDeclaration.class)) {
			if (container.get() instanceof XAssignment) {
				// Assignment: occurrence in left side => ERROR
				final XAssignment assignment = (XAssignment) container.get();
				if (directContainerChild.get() == assignment.getActualReceiver()) {
					error(MessageFormat.format(Messages.SARLValidator_2, keywordName),
							readOnlyKeyword,
							null,
							ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
							IssueCodes.INVALID_OCCURRENCE_READONLY_USE);
					return;
				}
			} else if (enableWarning && container.get() instanceof XVariableDeclaration) {
				final XVariableDeclaration declaration = (XVariableDeclaration) container.get();
				if (directContainerChild.get() == declaration.getRight()) {
					// Inside the initial value of a variable.
					// If the variable has a primitive type => No problem.
					// If the keyword is used in member feature calls => Warning
					final LightweightTypeReference variableType = getActualType(sequence);
					if (!this.immutableTypeValidator.isImmutable(variableType)) {
						if (expression == null || expression == declaration.getRight()) {
							addIssue(MessageFormat.format(Messages.SARLValidator_12, keywordName),
									sequence,
									IssueCodes.DISCOURAGED_OCCURRENCE_READONLY_USE);
							return;
						}
					}
				}
			}
		}

		if (enableWarning) {
			if (Utils.getContainerOfType(expression, container, directContainerChild,
					XFeatureCall.class, XMemberFeatureCall.class, XConstructorCall.class,
					XPostfixOperation.class)) {
				// Side effect Operator: occurrence in one of the operands => WARNING
				if (container.get() instanceof XPostfixOperation) {
					error(MessageFormat.format(Messages.SARLValidator_13, keywordName),
							readOnlyKeyword,
							null,
							ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
							IssueCodes.INVALID_OCCURRENCE_READONLY_USE);
					return;
				}
				// Function argument: direct passing, if function has side effect => WARNING
				final List<XExpression> arguments;
				final List<LightweightTypeReference> parameters;
				final boolean hasSideEffects;
				final boolean isVariadic;
				if (container.get() instanceof XConstructorCall) {
					final XConstructorCall cons = (XConstructorCall) container.get();
					arguments = cons.getArguments();
					final JvmConstructor constructor = cons.getConstructor();
					parameters = getParamTypeReferences(constructor, false, true);
					hasSideEffects = false;
					isVariadic = constructor.isVarArgs();
				} else {
					final XAbstractFeatureCall call = (XAbstractFeatureCall) container.get();
					if (call.getFeature() instanceof JvmOperation) {
						arguments = call.getActualArguments();
						final JvmOperation operation = (JvmOperation) call.getFeature();
						parameters = getParamTypeReferences(operation, false, true);
						hasSideEffects = getExpressionHelper().hasSideEffects(call);
						isVariadic = operation.isVarArgs();
					} else {
						arguments = null;
						parameters = null;
						hasSideEffects = false;
						isVariadic = false;
					}
				}
				if (arguments != null && hasSideEffects) {
					assert parameters != null;
					final int index = arguments.indexOf(directContainerChild.get());
					if (index >= 0 && !parameters.isEmpty()) {
						final boolean isPrimitive;
						final int endIndex = parameters.size() - 1;
						if (index < endIndex || (!isVariadic && index == endIndex)) {
							isPrimitive = this.immutableTypeValidator.isImmutable(parameters.get(index));
						} else if (isVariadic && index == endIndex) {
							// Assume argument for the variadic parameter.
							LightweightTypeReference parameter = parameters.get(endIndex);
							assert parameter.isArray();
							parameter = parameter.getComponentType().getWrapperTypeIfPrimitive();
							isPrimitive = this.immutableTypeValidator.isImmutable(parameter);
						} else {
							// Problem in the calling syntax: invalid number of arguments.
							// Avoid to output the warning.
							isPrimitive = true;
						}
						if (!isPrimitive) {
							addIssue(MessageFormat.format(Messages.SARLValidator_92, keywordName),
									sequence,
									IssueCodes.DISCOURAGED_OCCURRENCE_READONLY_USE);
							return;
						}
					}
				}
			}
		}
	}

	/** Retrieve the types of the formal parameters of the given JVM executable object.
	 *
	 * @param jvmExecutable the JVM executable.
	 * @param wrapFromPrimitives indicates if the primitive types must be wrapped to object type equivalent.
	 * @param wrapToPrimitives indicates if the object types must be wrapped to primitive type equivalent.
	 * @return the list of types.
	 * @since 0.8
	 * @see #getParamTypes(JvmOperation, boolean)
	 */
	protected List<LightweightTypeReference> getParamTypeReferences(JvmExecutable jvmExecutable,
			boolean wrapFromPrimitives, boolean wrapToPrimitives) {
		assert (wrapFromPrimitives && !wrapToPrimitives) || (wrapToPrimitives && !wrapFromPrimitives);
		final List<LightweightTypeReference> types = newArrayList();
		for (final JvmFormalParameter parameter : jvmExecutable.getParameters()) {
			LightweightTypeReference typeReference = toLightweightTypeReference(parameter.getParameterType());
			if (wrapFromPrimitives) {
				typeReference = typeReference.getWrapperTypeIfPrimitive();
			} else if (wrapToPrimitives) {
				typeReference = typeReference.getPrimitiveIfWrapperType();
			}
			types.add(typeReference);
		}
		return types;
	}

	/** Check for usage of the event functions in the behavior units.
	 *
	 * @param unit the unit to analyze.
	 */
	@Check(CheckType.EXPENSIVE)
	public void checkUnmodifiableEventAccess(SarlBehaviorUnit unit) {
		final boolean enable1 = !isIgnored(IssueCodes.DISCOURAGED_OCCURRENCE_READONLY_USE);
		final XExpression root = unit.getExpression();
		final String occurrenceKw = this.grammarAccess.getOccurrenceKeyword();
		for (final XFeatureCall child : EcoreUtil2.getAllContentsOfType(root, XFeatureCall.class)) {
			if (occurrenceKw.equals(child.getFeature().getIdentifier())) {
				checkUnmodifiableFeatureAccess(enable1, child, occurrenceKw);
			}
		}
	}

	/** Check for usage of break inside loops.
	 *
	 * @param expression the expression to analyze.
	 */
	@Check
	public void checkBreakKeywordUse(SarlBreakExpression expression) {
		final EObject container = Utils.getFirstContainerForPredicate(expression,
				it -> !(it instanceof XExpression) || it instanceof XAbstractWhileExpression
				|| it instanceof XBasicForLoopExpression || it instanceof XForLoopExpression);
		if (container instanceof XExpression) {
			if (!isIgnored(IssueCodes.DISCOURAGED_LOOP_BREAKING_KEYWORD_USE)
					&& container instanceof XBasicForLoopExpression) {
				addIssue(
						MessageFormat.format(Messages.SARLValidator_17, this.grammarAccess.getBreakKeyword()),
						expression,
						null,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						IssueCodes.DISCOURAGED_LOOP_BREAKING_KEYWORD_USE);
			}
		} else {
			error(MessageFormat.format(Messages.SARLValidator_18, this.grammarAccess.getBreakKeyword()),
					expression,
					null,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					IssueCodes.INVALID_USE_OF_LOOP_BREAKING_KEYWORD);
		}
	}

	/** Check for usage of continue inside loops.
	 *
	 * @param expression the expression to analyze.
	 * @since 0.7
	 */
	@Check
	public void checkContinueKeywordUse(SarlContinueExpression expression) {
		final EObject container = Utils.getFirstContainerForPredicate(expression,
				it -> !(it instanceof XExpression) || it instanceof XAbstractWhileExpression
				|| it instanceof XBasicForLoopExpression || it instanceof XForLoopExpression);
		if (container instanceof XExpression) {
			if (!isIgnored(IssueCodes.DISCOURAGED_LOOP_BREAKING_KEYWORD_USE)
					&& container instanceof XBasicForLoopExpression) {
				addIssue(
						MessageFormat.format(Messages.SARLValidator_17, this.grammarAccess.getContinueKeyword()),
						expression,
						null,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						IssueCodes.DISCOURAGED_LOOP_BREAKING_KEYWORD_USE);
			}
		} else {
			error(MessageFormat.format(Messages.SARLValidator_18, this.grammarAccess.getContinueKeyword()),
					expression,
					null,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					IssueCodes.INVALID_USE_OF_LOOP_BREAKING_KEYWORD);
		}
	}

	/** Check for usage of assert keyword.
	 *
	 * @param expression the expression to analyze.
	 */
	@Check
	public void checkAssertKeywordUse(SarlAssertExpression expression) {
		final XExpression condition = expression.getCondition();
		if (condition != null) {
			final LightweightTypeReference fromType = getActualType(condition);
			if (!fromType.isAssignableFrom(Boolean.TYPE)) {
				error(MessageFormat.format(
						Messages.SARLValidator_38,
						getNameOfTypes(fromType), boolean.class.getName()),
						condition,
						null,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						INCOMPATIBLE_TYPES);
				return;
			}
			if (getExpressionHelper() instanceof SARLExpressionHelper) {
				final SARLExpressionHelper helper = (SARLExpressionHelper) getExpressionHelper();
				final Boolean constant = helper.toBooleanPrimitiveWrapperConstant(condition);
				if (constant == Boolean.TRUE && !isIgnored(DISCOURAGED_BOOLEAN_EXPRESSION)) {
					addIssue(Messages.SARLValidator_51,
							condition,
							null,
							ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
							DISCOURAGED_BOOLEAN_EXPRESSION);
					return;
				}
			}
		}
	}

	/** Check for usage of assert keyword.
	 *
	 * @param expression the expression to analyze.
	 */
	@Check
	public void checkAssumeKeywordUse(SarlAssertExpression expression) {
		if (expression.isIsStatic()) {
			error(MessageFormat.format(
					Messages.SARLValidator_0,
					this.grammarAccess.getIsStaticAssumeKeyword()),
					expression,
					null);
		}
	}


	/** Check for a valid prototype of a static constructor.
	 *
	 * @param constructor the constructor to analyze.
	 */
	@Check
	public void checkStaticConstructorPrototype(XtendConstructor constructor) {
		if (constructor.isStatic()) {
			if (!constructor.getAnnotations().isEmpty()) {
				error(Messages.SARLValidator_23,
						constructor,
						XtendPackage.eINSTANCE.getXtendAnnotationTarget_Annotations(),
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						org.eclipse.xtend.core.validation.IssueCodes.ANNOTATION_WRONG_TARGET);
			}
			if (!constructor.getTypeParameters().isEmpty()) {
				error(Messages.SARLValidator_24,
						constructor,
						XtendPackage.eINSTANCE.getXtendExecutable_TypeParameters(),
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						org.eclipse.xtend.core.validation.IssueCodes.CONSTRUCTOR_TYPE_PARAMS_NOT_SUPPORTED);
			}
			if (!constructor.getParameters().isEmpty()) {
				error(Messages.SARLValidator_26,
						constructor,
						XtendPackage.eINSTANCE.getXtendExecutable_Parameters(),
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						IssueCodes.UNEXPECTED_FORMAL_PARAMETER);
			}
			if (!constructor.getExceptions().isEmpty()) {
				error(Messages.SARLValidator_27,
						constructor,
						XtendPackage.eINSTANCE.getXtendExecutable_Parameters(),
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						IssueCodes.UNEXPECTED_EXCEPTION_THROW);
			}
			if (constructor.getExpression() == null) {
				error(Messages.SARLValidator_83,
						constructor,
						XtendPackage.eINSTANCE.getXtendExecutable_Expression(),
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						IssueCodes.MISSING_BODY);
			}
		}
	}

	/** Check if a field needs to be synchronized.
	 *
	 * @param field the field.
	 * @since 0.7
	 */
	@Check
	@SuppressWarnings({"checkstyle:nestedifdepth", "checkstyle:npathcomplexity", "checkstyle:cyclomaticcomplexity"})
	public void checkUnsynchronizedField(XtendField field) {
		if (doCheckValidMemberName(field) && !isIgnored(IssueCodes.POTENTIAL_FIELD_SYNCHRONIZATION_PROBLEM)) {
			final JvmField jvmField = this.associations.getJvmField(field);
			if (jvmField == null || jvmField.eContainer() == null || jvmField.isConstant() || jvmField.isFinal()) {
				return;
			}
			final EObject scope = getOutermostType(field);
			if ((scope instanceof SarlAgent || scope instanceof SarlBehavior
					|| scope instanceof SarlSkill) && isLocallyAssigned(jvmField, scope)) {
				final Collection<Setting> usages = XbaseUsageCrossReferencer.find(jvmField, scope);
				final Set<XtendMember> blocks = new HashSet<>();
				boolean isAccessibleFromOutside = jvmField.getVisibility() != JvmVisibility.PRIVATE;
				final Collection<Setting> pbUsages = new ArrayList<>();
				for (final Setting usage : usages) {
					final XtendMember member = EcoreUtil2.getContainerOfType(usage.getEObject(), XtendMember.class);
					if (member instanceof XtendFunction) {
						final XtendFunction fct = (XtendFunction) member;
						blocks.add(member);
						if (member.getVisibility() != JvmVisibility.PRIVATE) {
							isAccessibleFromOutside = true;
						}
						if (!fct.isSynchonized()) {
							pbUsages.add(usage);
						}
					} else if (member instanceof SarlBehaviorUnit) {
						blocks.add(member);
						isAccessibleFromOutside = true;
						pbUsages.add(usage);
					}
				}
				for (final Setting usage : pbUsages) {
					boolean synchronizationIssue = false;
					if (isAccessibleFromOutside || blocks.size() > 1) {
						synchronizationIssue = true;
					} else {
						// TODO: Refine the function call detection
						synchronizationIssue = true;
					}
					// Check if the field is already locally synchronized
					if (synchronizationIssue) {
						final XSynchronizedExpression syncExpr = EcoreUtil2.getContainerOfType(
								usage.getEObject(), XSynchronizedExpression.class);
						if (syncExpr != null) {
							synchronizationIssue = false;
						}
					}
					if (synchronizationIssue
							&& !isIgnored(IssueCodes.POTENTIAL_FIELD_SYNCHRONIZATION_PROBLEM,
									usage.getEObject())) {
						addIssue(
								MessageFormat.format(Messages.SARLValidator_91, field.getName()),
								usage.getEObject(),
								usage.getEStructuralFeature(),
								IssueCodes.POTENTIAL_FIELD_SYNCHRONIZATION_PROBLEM);
					}
				}
			}
		}
	}

	@Override
	protected boolean isInitialized(JvmField input) {
		if (super.isInitialized(input)) {
			return true;
		}
		// Check initialization into a static constructor.
		final XtendField sarlField = (XtendField) this.associations.getPrimarySourceElement(input);
		if (sarlField == null) {
			return false;
		}
		final XtendTypeDeclaration declaringType = sarlField.getDeclaringType();
		if (declaringType == null) {
			return false;
		}
		for (final XtendConstructor staticConstructor : Iterables.filter(Iterables.filter(
				declaringType.getMembers(), XtendConstructor.class), it -> it.isStatic())) {
			if (staticConstructor.getExpression() != null) {
				for (final XAssignment assign : EcoreUtil2.getAllContentsOfType(staticConstructor.getExpression(), XAssignment.class)) {
					if (assign.isStatic() && Strings.equal(input.getIdentifier(), assign.getFeature().getIdentifier())) {
						// Mark the field as initialized in order to be faster during the next initialization test.
						this.readAndWriteTracking.markInitialized(input, null);
						return true;
					}
				}
			}
		}
		return false;
	}

	/** Replies if the given object is locally assigned.
	 *
	 * <p>An object is locally assigned when it is the left operand of an assignment operation.
	 *
	 * @param target the object to test.
	 * @param containerToFindUsage the container in which the usages should be find.
	 * @return {@code true} if the given object is assigned.
	 * @since 0.7
	 */
	protected boolean isLocallyAssigned(EObject target, EObject containerToFindUsage) {
		if (this.readAndWriteTracking.isAssigned(target)) {
			return true;
		}
		final Collection<Setting> usages = XbaseUsageCrossReferencer.find(target, containerToFindUsage);
		// field are assigned when they are not used as the left operand of an assignment operator.
		for (final Setting usage : usages) {
			final EObject object = usage.getEObject();
			if (object instanceof XAssignment) {
				final XAssignment assignment = (XAssignment) object;
				if (assignment.getFeature() == target) {
					// Mark the field as assigned in order to be faster during the next assignment test.
					this.readAndWriteTracking.markAssignmentAccess(target);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void checkAssignment(XExpression expression, EStructuralFeature feature, boolean simpleAssignment) {
		if (simpleAssignment && expression instanceof XAbstractFeatureCall) {
			final JvmIdentifiableElement assignmentFeature = ((XAbstractFeatureCall) expression).getFeature();
			if (assignmentFeature instanceof JvmField) {
				final JvmField field = (JvmField) assignmentFeature;
				if (!field.isFinal()) {
					return;
				}
				final JvmIdentifiableElement container = getLogicalContainerProvider().getNearestLogicalContainer(expression);
				if (container != null && container instanceof JvmOperation) {
					final JvmOperation operation = (JvmOperation) container;
					if (operation.isStatic() && field.getDeclaringType() == operation.getDeclaringType()
							&& Utils.STATIC_CONSTRUCTOR_NAME.equals(operation.getSimpleName())) {
						return;
					}
				}
			}
		}
		super.checkAssignment(expression, feature, simpleAssignment);
	}

	/** Check the correct usage of the {@link DefaultSkill} annotation.
	 *
	 * @param capacity the associated capacity to check.
	 */
	@Check
	@SuppressWarnings("checkstyle:nestedifdepth")
	public void checkDefaultSkillAnnotation(SarlCapacity capacity) {
		final String annotationId = DefaultSkill.class.getName();
		final XAnnotation annotation = IterableExtensions.findFirst(capacity.getAnnotations(), it -> {
			return Strings.equal(annotationId, it.getAnnotationType().getIdentifier());
		});
		if (annotation != null) {
			final XExpression expr = annotation.getValue();
			if (expr instanceof XTypeLiteral) {
				final XTypeLiteral typeLiteral = (XTypeLiteral) expr;
				final JvmType type = typeLiteral.getType();
				if (type != null && !type.eIsProxy()) {
					final LightweightTypeReference reference = toLightweightTypeReference(type, capacity);
					// Validating by the annotation value's type.
					if (this.inheritanceHelper.isSarlSkill(reference)) {
						final EObject element = this.associations.getPrimaryJvmElement(capacity);
						assert element instanceof JvmType;
						if (!reference.isSubtypeOf((JvmType) element)) {
							error(MessageFormat.format(
									Messages.SARLValidator_71,
									capacity.getName(), type.getSimpleName()),
									expr,
									null,
									ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
									IssueCodes.INVALID_DEFAULT_SKILL_ANNOTATION);
						}
						return;
					}
				}
			}
			error(Messages.SARLValidator_88,
					expr,
					null,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					IssueCodes.INVALID_DEFAULT_SKILL_ANNOTATION);
		}
	}

	@Override
	public void checkAnnotationTarget(XAnnotation annotation) {
		super.checkAnnotationTarget(annotation);
		if (isForbiddenActiveAnnotation(annotation)) {
			error(Messages.SARLValidator_89,
					annotation,
					null,
					ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
					org.eclipse.xtext.xbase.validation.IssueCodes.FORBIDDEN_REFERENCE);
		} else if (isOOActiveAnnotation(annotation)) {
			XtendTypeDeclaration container = EcoreUtil2.getContainerOfType(annotation.eContainer(), XtendTypeDeclaration.class);
			while (container != null && (container.isAnonymous() || container.getName() == null)) {
				container = EcoreUtil2.getContainerOfType(container.eContainer(), XtendTypeDeclaration.class);
			}
			if (!isOOType(container)) {
				error(Messages.SARLValidator_90,
						annotation,
						null,
						ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
						org.eclipse.xtext.xbase.validation.IssueCodes.FORBIDDEN_REFERENCE);
			}
		}
	}

	/** Replies if the given element is an object oriented type.
	 *
	 * @param type the type to test.
	 * @return {@code true} if the type is an object oriented type.
	 */
	@SuppressWarnings("static-method")
	protected boolean isOOType(XtendTypeDeclaration type) {
		return type instanceof XtendClass
				|| type instanceof XtendInterface
				|| type instanceof XtendEnum
				|| type instanceof XtendAnnotationType;
	}

	/** Replies if the given annotation is an active annotation for object-oriented elements.
	 *
	 * @param annotation the annotation.
	 * @return {@code true} if the annotation should be used only for OO elements.
	 */
	@SuppressWarnings("static-method")
	protected boolean isOOActiveAnnotation(XAnnotation annotation) {
		final String name = annotation.getAnnotationType().getQualifiedName();
		return Strings.equal(Accessors.class.getName(), name)
				|| Strings.equal(Data.class.getName(), name)
				|| Strings.equal(Delegate.class.getName(), name)
				|| Strings.equal(ToString.class.getName(), name);
	}

	/** Replies if the given annotation is a forbidden active annotation.
	 *
	 * @param annotation the annotation.
	 * @return {@code true} if the annotation is forbidden.
	 */
	@SuppressWarnings("static-method")
	protected boolean isForbiddenActiveAnnotation(XAnnotation annotation) {
		final String name = annotation.getAnnotationType().getQualifiedName();
		return Strings.equal(EqualsHashCode.class.getName(), name)
				|| Strings.equal(FinalFieldsConstructor.class.getName(), name);
	}

	/** Check the top elements within a script are not duplicated.
	 *
	 * @param script the SARL script
	 */
	@Check
	public void checkTopElementsAreUnique(SarlScript script) {
		final Multimap<String, XtendTypeDeclaration> name2type = HashMultimap.create();
		for (final XtendTypeDeclaration declaration : script.getXtendTypes()) {
			final String name = declaration.getName();
			if (!Strings.isEmpty(name)) {
				name2type.put(name, declaration);
			}
		}
		for (final String name: name2type.keySet()) {
			final Collection<XtendTypeDeclaration> types = name2type.get(name);
			if (types.size() > 1) {
				for (final XtendTypeDeclaration type: types) {
					error(
							MessageFormat.format(Messages.SARLValidator_93, name),
							type,
							XtendPackage.Literals.XTEND_TYPE_DECLARATION__NAME,
							DUPLICATE_TYPE_NAME);
				}
			}
		}
	}

	/** The modifier validator for constructors.
	 *
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected final class SARLModifierValidator extends ModifierValidator {

		/** Constructor.
		 * @param modifiers the list of the supported modifiers.
		 */
		private SARLModifierValidator(List<String> modifiers) {
			super(modifiers, SARLValidator.this);
		}

		@Check
		@Override
		public void checkModifiers(XtendMember member, String memberName) {
			super.checkModifiers(member, memberName);
		}

		@Override
		@SuppressWarnings("synthetic-access")
		protected boolean isPrivateByDefault(XtendMember member) {
			final JvmVisibility defaultVisibility = SARLValidator.this.defaultVisibilityProvider.getDefaultJvmVisibility(member);
			return defaultVisibility == JvmVisibility.PRIVATE;
		}

		@Override
		@SuppressWarnings("synthetic-access")
		protected boolean isProtectedByDefault(XtendMember member) {
			final JvmVisibility defaultVisibility = SARLValidator.this.defaultVisibilityProvider.getDefaultJvmVisibility(member);
			return defaultVisibility == JvmVisibility.PROTECTED;
		}

		@Override
		@SuppressWarnings("synthetic-access")
		protected boolean isPackageByDefault(XtendMember member) {
			final JvmVisibility defaultVisibility = SARLValidator.this.defaultVisibilityProvider.getDefaultJvmVisibility(member);
			return defaultVisibility == JvmVisibility.DEFAULT;
		}

		@Override
		@SuppressWarnings("synthetic-access")
		protected boolean isPublicByDefault(XtendMember member) {
			final JvmVisibility defaultVisibility = SARLValidator.this.defaultVisibilityProvider.getDefaultJvmVisibility(member);
			return defaultVisibility == JvmVisibility.PUBLIC;
		}

	}

}
