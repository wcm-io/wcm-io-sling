/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
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
package io.wcm.sling.models.injectors.impl;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.osgi.framework.Constants;

/**
 * Injects common Sling objects that can be derived from either a SlingHttpServletRequest.
 * The injection is class-based.
 * <p>
 * Supports the following objects:
 * <table>
 * <tr>
 * <th>Class</th>
 * <th>Name</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@link SlingScriptHelper}</td>
 * <td>(any)</td>
 * <td>Sling script helper</td>
 * </tr>
 * <tr>
 * <td>{@link SlingHttpServletResponse}</td>
 * <td>(any)</td>
 * <td>Sling response</td>
 * </tr>
 * </table>
 */
@Component
@Service(Injector.class)
@Property(name = Constants.SERVICE_RANKING, intValue = 4400)
public final class SlingObjectInjector implements Injector, AcceptsNullName {

  @Override
  public String getName() {
    return "sling-object";
  }

  @Override
  public Object getValue(final Object adaptable, final String name, final Type type, final AnnotatedElement element,
      final DisposalCallbackRegistry callbackRegistry) {

    // only class types are supported
    if (!(type instanceof Class<?>)) {
      return null;
    }
    Class<?> requestedClass = (Class<?>)type;

    // validate input
    if (!(adaptable instanceof SlingHttpServletRequest)) {
      return null;
    }
    SlingHttpServletRequest request = (SlingHttpServletRequest)adaptable;

    if (requestedClass.equals(SlingScriptHelper.class)) {
      return getSlingScriptHelper(request);
    }
    else if (requestedClass.equals(SlingHttpServletResponse.class)) {
      return getSlingHttpServletResponse(request);
    }

    return null;
  }

  private SlingScriptHelper getSlingScriptHelper(final SlingHttpServletRequest request) {
    SlingBindings bindings = (SlingBindings)request.getAttribute(SlingBindings.class.getName());
    if (bindings != null) {
      return bindings.getSling();
    }
    return null;
  }

  private SlingHttpServletResponse getSlingHttpServletResponse(final SlingHttpServletRequest request) {
    SlingScriptHelper scriptHelper = getSlingScriptHelper(request);
    if (scriptHelper != null) {
      return scriptHelper.getResponse();
    }
    return null;
  }

}
