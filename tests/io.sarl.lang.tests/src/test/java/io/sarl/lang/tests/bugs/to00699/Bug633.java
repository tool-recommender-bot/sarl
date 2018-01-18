/*
 * Copyright (C) 2014-2018 the original authors or authors.
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

package io.sarl.lang.tests.bugs.to00699;

import com.google.inject.Inject;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.testing.CompilationTestHelper;
import org.junit.Test;

import io.sarl.lang.SARLVersion;
import io.sarl.lang.sarl.SarlPackage;
import io.sarl.lang.sarl.SarlScript;
import io.sarl.tests.api.AbstractSarlTest;

/** Testing class for issue: Invalid call to static function with default parameter.
 *
 * <p>https://github.com/sarl/sarl/issues/633
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class Bug633 extends AbstractSarlTest {

	private static final String SNIPSET1 = multilineString(
			"package io.sarl.lang.tests.bug633",
			"class XXX {",
			"	public static def f1() : void {",
			"		f2",
			"	}",
			"	public static def f2(p : Object = null) {",
			"	}",
			"}");

	@Inject
	private CompilationTestHelper compiler;

	@Test
	public void parsing_01() throws Exception {
		SarlScript mas = file(SNIPSET1);
		final Validator validator = validate(mas);
		validator.assertNoErrors();
	}

	@Test
	public void compiling_01() throws Exception {
		this.compiler.assertCompilesTo(SNIPSET1, multilineString(
				"package io.sarl.lang.tests.bug633;",
				"",
				"import io.sarl.lang.annotation.DefaultValue;",
				"import io.sarl.lang.annotation.DefaultValueSource;",
				"import io.sarl.lang.annotation.DefaultValueUse;",
				"import io.sarl.lang.annotation.SarlElementType;",
				"import io.sarl.lang.annotation.SarlSourceCode;",
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import org.eclipse.xtext.xbase.lib.Pure;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SarlElementType(" + SarlPackage.SARL_CLASS + ")",
				"@SuppressWarnings(\"all\")",
				"public class XXX {",
				"  @Pure",
				"  public static void f1() {",
				"    XXX.f2();",
				"  }",
				"  ",
				"  @DefaultValueSource",
				"  @Pure",
				"  public static void f2(@DefaultValue(\"io.sarl.lang.tests.bug633.XXX#F2_0\") final Object p) {",
				"  }",
				"  ",
				"  /**",
				"   * Default value for the parameter p",
				"   */",
				"  @SyntheticMember",
				"  @SarlSourceCode(\"null\")",
				"  private final static Object $DEFAULT_VALUE$F2_0 = null;",
				"  ",
				"  @DefaultValueUse(\"java.lang.Object\")",
				"  @SyntheticMember",
				"  @Pure",
				"  public static void f2() {",
				"    f2($DEFAULT_VALUE$F2_0);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public XXX() {",
				"    super();",
				"  }",
				"}",
				""));
	}

}
