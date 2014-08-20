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
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.osgi.framework.Constants;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;

/**
 * Injects common AEM objects that can be derived from either a Resource object.
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
 * <td>{@link PageManager}</td>
 * <td>(any)</td>
 * <td>AEM Page manager</td>
 * </tr>
 * <tr>
 * <td>{@link Page}</td>
 * <td>currentPage</td>
 * <td>AEM page addressed by the request.</td>
 * </tr>
 * <tr>
 * <td>{@link Designer}</td>
 * <td>(any)</td>
 * <td>AEM designer</td>
 * </tr>
 * <tr>
 * <td>{@link Design}</td>
 * <td>(any)</td>
 * <td>AEM design of the current page.</td>
 * </tr>
 * </table>
 */
@Component
@Service(Injector.class)
@Property(name = Constants.SERVICE_RANKING, intValue = 4510)
public final class AemObjectResourceInjector extends AbstractAemObjectInjector {

  @Override
  public String getName() {
    return "aem-object-resource";
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
    if (!(adaptable instanceof Resource)) {
      return null;
    }
    Resource resource = (Resource)adaptable;

    if (requestedClass.equals(PageManager.class)) {
      return getPageManager(resource);
    }
    else if (requestedClass.equals(Page.class)) {
      return getCurrentPage(resource);
    }
    else if (requestedClass.equals(Designer.class)) {
      return getDesigner(resource);
    }
    else if (requestedClass.equals(Design.class)) {
      return getCurrentDesign(resource);
    }

    return null;
  }

}
