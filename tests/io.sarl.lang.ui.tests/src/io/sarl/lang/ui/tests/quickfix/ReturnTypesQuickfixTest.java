/*
 * $Id$
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2019 the original authors or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sarl.lang.ui.tests.quickfix;

import org.junit.Test;

@SuppressWarnings("all")
public class ReturnTypesQuickfixTest extends AbstractSARLQuickfixTest {

	/**
	 */
	@Test
	public void fixIncompatibleReturnType() {
		assertQuickFix(
				org.eclipse.xtext.xbase.validation.IssueCodes.INCOMPATIBLE_RETURN_TYPE,
				//
				// Code to fix:
				//
				multilineString(
						"class A1 {",
						"	def fct1 : int {",
						"		123",
						"	}",
						"}",
						"class A2 extends A1 {",
						"	def fct1 : boolean {",
						"		true",
						"	}",
						"}"),
				//
				// Label and description:
				//
				"Replace by int",
				//
				// Expected fixed code:
				//
				multilineString(
						"class A1 {",
						"	def fct1 : int {",
						"		123",
						"	}",
						"}",
						"class A2 extends A1 {",
						"	def fct1 : int {",
						"		true",
						"	}",
						"}"));
	}

	/**
	 */
	@Test
	public void fixReturnTypeRecommended() {
		assertQuickFix(
				io.sarl.lang.validation.IssueCodes.RETURN_TYPE_SPECIFICATION_IS_RECOMMENDED,
				//
				// Code to fix:
				//
				multilineString(
						"class A1 {",
						"	def fct1 : int {",
						"		123",
						"	}",
						"}",
						"class A2 extends A1 {",
						"	def fct1 {",
						"		456",
						"	}",
						"}"),
				//
				// Label and description:
				//
				"Add the return type int",
				//
				// Expected fixed code:
				//
				multilineString(
						"class A1 {",
						"	def fct1 : int {",
						"		123",
						"	}",
						"}",
						"class A2 extends A1 {",
						"	def fct1 : int {",
						"		456",
						"	}",
						"}"));
	}

}
