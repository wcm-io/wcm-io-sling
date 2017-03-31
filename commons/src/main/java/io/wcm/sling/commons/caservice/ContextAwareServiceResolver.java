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

import java.util.stream.Stream;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Resolves the best-matching context-aware service implementation.
 */
@ProviderType
public interface ContextAwareServiceResolver {

  /**
   * Resolves the best-matching service implementation for the given resource context.
   * Only implementation which accepts the given context resource path (via the service properties defined in
   * {@link ContextAwareService}) are considered as candidates.
   * If multiple candidates exist the implementation with the highest service ranking is returned.
   * @param serviceClass Service interface or class
   * @param adaptable Adaptable which is either a {@link Resource} or {@link SlingHttpServletRequest}.
   *          A resource instances is used directly for matching, in case of request the associated resource is used.
   *          May be null if no context is available.
   * @param <T> Service interface or class
   * @return Service implementation or null if no match found.
   */
  <T extends ContextAwareService> T resolve(Class<T> serviceClass, Adaptable adaptable);

  /**
   * Resolves all matching service implementations for the given resource context.
   * Only implementation which accepts the given context resource path (via the service properties defined in
   * {@link ContextAwareService}) are considered as candidates.
   * The candidates are returned ordered descending by their service ranking.
   * @param serviceClass Service interface or class
   * @param adaptable Adaptable which is either a {@link Resource} or {@link SlingHttpServletRequest}.
   *          A resource instances is used directly for matching, in case of request the associated resource is used.
   *          May be null if no context is available.
   * @param <T> Service interface or class
   * @return Stream with all matching service implementations (may be empty)
   */
  <T extends ContextAwareService> Stream<T> resolveAll(Class<T> serviceClass, Adaptable adaptable);

}
