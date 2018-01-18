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
package io.sarl.lang.validation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtend.core.validation.AnnotationValidation;
import org.eclipse.xtend.core.validation.XtendValidator;
import org.eclipse.xtext.validation.ComposedChecks;

@ComposedChecks(validators = {AnnotationValidation.class})
public abstract class AbstractSARLValidator extends XtendValidator {
	
	@Override
	protected List<EPackage> getEPackages() {
		List<EPackage> result = new ArrayList<EPackage>(super.getEPackages());
		result.add(io.sarl.lang.sarl.SarlPackage.eINSTANCE);
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/xtend"));
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/xtext/xbase/Xbase"));
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/xtext/common/JavaVMTypes"));
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/xtext/xbase/Xtype"));
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/Xtext/Xbase/XAnnotations"));
		return result;
	}
	
}
