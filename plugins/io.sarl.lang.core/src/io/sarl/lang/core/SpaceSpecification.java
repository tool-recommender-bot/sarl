/*
 * Copyright 2014 Sebastian RODRIGUEZ, Nicolas GAUD, Stéphane GALLAND.
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
package io.sarl.lang.core;


/**
 * The Specification of an Interaction Space.
 * 
 * The {@link SpaceSpecification} defines the rules (including action and
 * perception) for interacting within a given set of Spaces respecting this
 * specification.
 * 
 * @param <S> type of the spaces that may be created by this specification.
 * @author $Author: srodriguez$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface SpaceSpecification<S extends Space> {

	/**
	 * Creates a {@link Space} that respects this specification
	 * 
	 * @param id
	 *            the {@link SpaceID} for the newly created {@link Space}
	 * @param params
	 *            initialization parameters for the {@link Space}
	 * @return an instance of {@link Space}
	 */
	public S create(SpaceID id, Object... params);

}