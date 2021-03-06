/*
 * $Id$
 *
 * Janus platform is an open-source multiagent platform.
 * More details on http://www.janusproject.io
 *
 * Copyright (C) 2014-2015 Sebastian RODRIGUEZ, Nicolas GAUD, Stéphane GALLAND.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.janusproject.tests.testutils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.janusproject.services.logging.LogService;

/**
 * The injection module for unit tests.
 *
 * @author $Author: srodriguez$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ErrorLogTestingModule extends AbstractModule {

	private final Map<UUID, List<Object>> results;

	/**
	 * @param results the results to fill.
	 */
	public ErrorLogTestingModule(Map<UUID, List<Object>> results) {
		this.results = results;
	}

	@Override
	protected void configure() {
		//
	}

	/**
	 * Replies the log service.
	 * 
	 * @return the log service.
	 */
	@Singleton
	@Provides
	public LogService getLogService() {
		return new ExceptionLogService(this.results);
	}

}
