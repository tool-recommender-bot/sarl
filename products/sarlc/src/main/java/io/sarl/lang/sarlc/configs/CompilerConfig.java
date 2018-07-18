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

package io.sarl.lang.sarlc.configs;

import java.nio.charset.Charset;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.config.ConfigurationFactory;

import io.sarl.lang.SARLVersion;
import io.sarl.lang.compiler.GeneratorConfig2;
import io.sarl.maven.bqextension.configs.Config;
import io.sarl.maven.bqextension.configs.Configs;

/**
 * Configuration for the compiler.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.8
 */
@BQConfig("Configuration of the SARL compiler")
public class CompilerConfig implements Config {

	/**
	 * Prefix for the configuration entries of the path modules.
	 */
	public static final String PREFIX = Configs.SARL_ROOT_PROPERTY_PREFIX + "compiler"; //$NON-NLS-1$

	/**
	 * Name of the property that contains the file encoding.
	 */
	public static final String FILE_ENCODING_NAME = PREFIX + ".fileEncoding"; //$NON-NLS-1$

	/**
	 * Name of the property that contains the version of java.
	 */
	public static final String JAVA_VERSION_NAME = PREFIX + ".javaVersion"; //$NON-NLS-1$

	/**
	 * Name of the property that contains the enabling flag for the Java compiler.
	 */
	public static final String JAVA_COMPILER_NAME = PREFIX + ".javaCompiler"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if the trace files should be generated.
	 */
	public static final String OUTPUT_TRACES_NAME = PREFIX + ".outputTraceFiles"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if the storage files should be generated.
	 */
	public static final String OUTPUT_STORAGES_NAME = PREFIX + ".outputStorageFiles"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if inline annotations are generated.
	 */
	public static final String GENERATE_INLINES_NAME = PREFIX + ".generateInlines"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if pure annotations are generated.
	 */
	public static final String GENERATE_PURES_NAME = PREFIX + ".generatePures"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if equality tests are generated.
	 */
	public static final String GENERATE_EQUALITY_TESTS_NAME = PREFIX + ".generateEqualityTests"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if toString functions are generated.
	 */
	public static final String GENERATE_TOSTRING_NAME = PREFIX + ".generateToString"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if clone functions are generated.
	 */
	public static final String GENERATE_CLONE_NAME = PREFIX + ".generateClone"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if serial ids are generated.
	 */
	public static final String GENERATE_SERIAL_IDS_NAME = PREFIX + ".generateSerialIds"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if the expression interpreter should be used for
	 * compiling the inline expressions.
	 */
	public static final String COMPRESS_INLINE_EXPRESSIONS_NAME = PREFIX + ".compressInlineExpressions"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if warnings are ignored.
	 */
	public static final String IGNORE_WARNINGS_NAME = PREFIX + ".ignoreWarnings"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if all warnings are printed out.
	 */
	public static final String ALL_WARNINGS_NAME = PREFIX + ".allWarnings"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates if warnings are errors.
	 */
	public static final String ALL_ERRORS_NAME = PREFIX + ".allErrors"; //$NON-NLS-1$

	/**
	 * Name of the property that indicates the levels of specific warnings.
	 */
	public static final String WARNING_LEVELS_NAME = PREFIX + ".warningLevels"; //$NON-NLS-1$

	private String fileEcoding = Charset.defaultCharset().displayName();

	private String javaVersion = SARLVersion.MINIMAL_JDK_VERSION;

	private boolean javaCompiler = true;

	private boolean traceFiles = true;

	private boolean storageFiles = true;

	private boolean generateInlines = GeneratorConfig2.DEFAULT_GENERATE_INLINE_ANNOTATION;

	private boolean generatePures = GeneratorConfig2.DEFAULT_GENERATE_PURE_ANNOTATION;

	private boolean generateEqualityTests = GeneratorConfig2.DEFAULT_GENERATE_EQUALITY_TEST_FUNCTIONS;

	private boolean generateToString = GeneratorConfig2.DEFAULT_GENERATE_TOSTRING_FUNCTION;

	private boolean generateClone = GeneratorConfig2.DEFAULT_GENERATE_CLONE_FUNCTION;

	private boolean generateSerialIds = GeneratorConfig2.DEFAULT_GENERATE_SERIAL_NUMBER_FIELD;

	private boolean compressInlineExpressions = GeneratorConfig2.DEFAULT_USE_EXPRESSION_INTERPRETER_FOR_INLINE_ANNOTATION;

	/** Replies if the inline expressions should be compressed on the fly.
	 *
	 * @return {@code true} if the expressions are compressed.
	 */
	public boolean getCompressInlineExpressions() {
		return this.compressInlineExpressions;
	}

	/** Change the flag that indicates if the inline expressions should be compressed on the fly.
	 *
	 * @param enable {@code true} if expressions are compressed.
	 */
	@BQConfigProperty("Enable or disable the generation of the serial numbers")
	public void setCompressInlineExpressions(boolean enable) {
		this.compressInlineExpressions = enable;
	}

	/** Replies if the serial ids are generated.
	 *
	 * @return {@code true} if the serial ids are generated.
	 */
	public boolean getGenerateSerialIds() {
		return this.generateSerialIds;
	}

	/** Change the flag that indicates if the serial ids should be generated.
	 *
	 * @param enable {@code true} if the serial ids are generated.
	 */
	@BQConfigProperty("Enable or disable the generation of the serial numbers")
	public void setGenerateSerialIds(boolean enable) {
		this.generateSerialIds = enable;
	}

	/** Replies if the clone functions are generated.
	 *
	 * @return {@code true} if the clone functions are generated.
	 */
	public boolean getGenerateClone() {
		return this.generateClone;
	}

	/** Change the flag that indicates if the clone functions should be generated.
	 *
	 * @param enable {@code true} if the clone functions are generated.
	 */
	@BQConfigProperty("Enable or disable the generation of the clone functions")
	public void setGenerateClone(boolean enable) {
		this.generateClone = enable;
	}

	/** Replies if the toString functions are generated.
	 *
	 * @return {@code true} if the toString functions are generated.
	 */
	public boolean getGenerateToString() {
		return this.generateToString;
	}

	/** Change the flag that indicates if the toString functions should be generated.
	 *
	 * @param enable {@code true} if the toString functions are generated.
	 */
	@BQConfigProperty("Enable or disable the generation of the toString functions")
	public void setGenerateToString(boolean enable) {
		this.generateToString = enable;
	}

	/** Replies if the equality tests are generated.
	 *
	 * @return {@code true} if the equality tests are generated.
	 */
	public boolean getGenerateEqualityTests() {
		return this.generateEqualityTests;
	}

	/** Change the flag that indicates if the equality tests should be generated.
	 *
	 * @param enable {@code true} if the equality tests are generated.
	 */
	@BQConfigProperty("Enable or disable the generation of the equality tests")
	public void setGenerateEqualityTests(boolean enable) {
		this.generateEqualityTests = enable;
	}

	/** Replies if the pure annotations are generated.
	 *
	 * @return {@code true} if the pure annotations are generated.
	 */
	public boolean getGeneratePures() {
		return this.generatePures;
	}

	/** Change the flag that indicates if the pure annotations should be generated.
	 *
	 * @param enable {@code true} if the pure annotations are generated.
	 */
	@BQConfigProperty("Enable or disable the generation of the pure annotations")
	public void setGeneratePures(boolean enable) {
		this.generatePures = enable;
	}

	/** Replies if the inline annotations are generated.
	 *
	 * @return {@code true} if the inline annotations are generated.
	 */
	public boolean getGenerateInlines() {
		return this.generateInlines;
	}

	/** Change the flag that indicates if the inline annotations should be generated.
	 *
	 * @param enable {@code true} if the inline annotations are generated.
	 */
	@BQConfigProperty("Enable or disable the generation of the inline annotations")
	public void setGenerateInlines(boolean enable) {
		this.generateInlines = enable;
	}

	/** Replies if the trace files are generated.
	 *
	 * @return {@code true} if the trace files are generated.
	 */
	public boolean getOutputTraceFiles() {
		return this.traceFiles;
	}

	/** Change the flag that indicates if the trace files should be generated.
	 *
	 * @param enable {@code true} if the trace files are generated.
	 */
	@BQConfigProperty("Enable or disable the creation of the trace files")
	public void setOutputTraceFiles(boolean enable) {
		this.traceFiles = enable;
	}

	/** Replies if the storage files are generated.
	 *
	 * @return {@code true} if the storage files are generated.
	 */
	public boolean getOutputStorageFiles() {
		return this.storageFiles;
	}

	/** Change the flag that indicates if the storage files should be generated.
	 *
	 * @param enable {@code true} if the storage files are generated.
	 */
	@BQConfigProperty("Enable or disable the creation of the storage files")
	public void setOutputStorageFiles(boolean enable) {
		this.storageFiles = enable;
	}

	/** Replies if the Java compiler is run after the Java code is generated..
	 *
	 * @return {@code true} if the Java compiler is run.
	 */
	public boolean getJavaCompiler() {
		return this.javaCompiler;
	}

	/** Change the flag that indicates if the Java compiler is run after the Java code is generated..
	 *
	 * @param enable {@code true} if the Java compiler is run.
	 */
	@BQConfigProperty("Enable or disable the Java compiler")
	public void setJavaCompiler(boolean enable) {
		this.javaCompiler = enable;
	}

	/** Replies the file encoding.
	 *
	 * @return the file encoding
	 */
	public String getFileEncoding() {
		return this.fileEcoding;
	}

	/** Change the file encoding.
	 *
	 * @param encoding the file encoding.
	 */
	@BQConfigProperty("Encoding of the files")
	public void setFileEncoding(String encoding) {
		this.fileEcoding = encoding;
	}

	/** Replies the java version.
	 *
	 * @return the java version.
	 */
	public String getJavaVersion() {
		return this.javaVersion;
	}

	/** Change the java version.
	 *
	 * @param version the java version.
	 */
	@BQConfigProperty("Version of Java to be used (1.8 or higher)")
	public void setJavaVersion(String version) {
		this.javaVersion = version;
	}

	/** Replies the configuration factory for the logging.
	 *
	 * @param configFactory the general configuration factory.
	 * @return the logging configuration factory.
	 */
	public static CompilerConfig getConfiguration(ConfigurationFactory configFactory) {
		assert configFactory != null;
		return configFactory.config(CompilerConfig.class, PREFIX);
	}

}
