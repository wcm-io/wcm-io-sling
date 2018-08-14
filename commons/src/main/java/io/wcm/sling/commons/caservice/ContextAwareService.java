/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
 * %%
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
 * #L%
 */
package io.wcm.sling.commons.caservice;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Marker interface for Context-Aware services.
 * <p>
 * If multiple implementations of an OSGi service interface or class exists the {@link ContextAwareServiceResolver} can
 * be used to picks the implementation with the best match for the current resource context/path in the resource
 * hierarchy.
 * </p>
 * <p>
 * This interface has no methods, the metadata for detecting the matching paths is published via the properties
 * declared by the constants of this interface. The properties can be set as service properties or as bundle header.
 * Service property is checked first, if it is not set there the bundle header of the bundle which registered the
 * service is checked as fallback.
 * </p>
 * <p>
 * If multiple service implementations exist which match for a given resource path the one with the highest service
 * ranking is picked.
 * </p>
 */
//CHECKSTYLE:OFF
@ConsumerType
public interface ContextAwareService {
//CHECKSTYLE:ON

  /**
   * Context path whitelist expression.
   * Service property or bundle header defining a regular expression the context resource path is matched against.
   * Only if the path is matching the service is considered as candidate.
   * If the property is not set all resource paths are accepted.
   */
  @NotNull
  String PROPERTY_CONTEXT_PATH_PATTERN = "Wcmio-CAService-ContextPathRegex";

  /**
   * Context path blacklist expression.
   * Service property or bundle header defining a regular expression the context resource path is matched against.
   * If the path is matching the service is not considered as candidate.
   * If the property is not set no resource paths are blacklisted.
   */
  @NotNull
  String PROPERTY_CONTEXT_PATH_BLACKLIST_PATTERN = "Wcmio-CAService-ContextPathBlacklistRegex";

  /**
   * Accepts empty context paths.
   * Service property or bundle header that can be set to "true" to signal that this service implementation also should
   * be considered as candidate if no context resource exists and thus the context path is empty (null).
   * If the property is not set the implementation is not considered as candidate for empty resource paths.
   */
  @NotNull
  String PROPERTY_ACCEPTS_CONTEXT_PATH_EMPTY = "Wcmio-CASService-AcceptsContextPathEmpty";

}
