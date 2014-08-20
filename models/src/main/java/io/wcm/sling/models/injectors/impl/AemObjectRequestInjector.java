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

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.osgi.framework.Constants;

import com.adobe.granite.xss.XSSAPI;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.commons.WCMUtils;

/**
 * Injects common AEM objects that can be derived from a SlingHttpServletRequest.
 * The injection is class-based, but may be supported by name hints when multiple targets are available for the same
 * class.
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
 * <td>AEM page addressed by the request. If no or another name is specified always the current page is injected for a
 * {@link Page} class.</td>
 * </tr>
 * <tr>
 * <td>{@link Page}</td>
 * <td>resourcePage</td>
 * <td>AEM page containing the current resource.</td>
 * </tr>
 * <tr>
 * <td>{@link WCMMode}</td>
 * <td>(any)</td>
 * <td>Current AEM WCM mode</td>
 * </tr>
 * <tr>
 * <td>{@link AuthoringUIMode}</td>
 * <td>(any)</td>
 * <td>Current AEM Authoring UI mode</td>
 * </tr>
 * <tr>
 * <td>{@link ComponentContext}</td>
 * <td>(any)</td>
 * <td>AEM component context of current request</td>
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
 * <tr>
 * <td>{@link Style}</td>
 * <td>(any)</td>
 * <td>AEM design style of the current component</td>
 * </tr>
 * <tr>
 * <td>{@link XSSAPI}</td>
 * <td>(any)</td>
 * <td>AEM XSS API object for the current request</td>
 * </tr>
 * </table>
 */
@Component
@Service(Injector.class)
/*
 * SERVICE_RANKING of this service should be lower than the ranking of the OsgiServiceInjector (5000),
 * otherwise the generic XSSAPI service would be injected from the OSGi Service Registry instead of the
 * pre-configured from the current request.
 */
@Property(name = Constants.SERVICE_RANKING, intValue = 4500)
public final class AemObjectRequestInjector extends AbstractAemObjectInjector {

  @Override
  public String getName() {
    return "aem-object-request";
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

    if (requestedClass.equals(PageManager.class)) {
      return getPageManager(request);
    }
    else if (requestedClass.equals(Page.class)) {
      if (StringUtils.equals(name, RESOURCE_PAGE)) {
        return getResourcePage(request);
      }
      else {
        return getCurrentPage(request);
      }
    }
    else if (requestedClass.equals(WCMMode.class)) {
      return getWcmMode(request);
    }
    else if (requestedClass.equals(AuthoringUIMode.class)) {
      return getAuthoringUiMode(request);
    }
    else if (requestedClass.equals(ComponentContext.class)) {
      return getComponentContext(request);
    }
    else if (requestedClass.equals(Designer.class)) {
      return getDesigner(request);
    }
    else if (requestedClass.equals(Design.class)) {
      return getCurrentDesign(request);
    }
    else if (requestedClass.equals(Style.class)) {
      return getStyle(request);
    }
    else if (requestedClass.equals(XSSAPI.class)) {
      return getXssApi(request);
    }

    return null;
  }

  @Override
  protected Page getCurrentPage(final Object adaptable) {
    ComponentContext context = getComponentContext((SlingHttpServletRequest)adaptable);
    if (context != null) {
      return context.getPage();
    }
    else {
      return getResourcePage(adaptable);
    }
  }

  private WCMMode getWcmMode(final SlingHttpServletRequest request) {
    return WCMMode.fromRequest(request);
  }

  private AuthoringUIMode getAuthoringUiMode(final SlingHttpServletRequest request) {
    return AuthoringUIMode.fromRequest(request);
  }

  private ComponentContext getComponentContext(final SlingHttpServletRequest request) {
    return WCMUtils.getComponentContext(request);
  }

  private Style getStyle(final SlingHttpServletRequest request) {
    Design currentDesign = getCurrentDesign(request);
    ComponentContext componentContext = getComponentContext(request);
    if (currentDesign != null && componentContext != null) {
      return currentDesign.getStyle(componentContext.getCell());
    }
    return null;
  }

  private XSSAPI getXssApi(final SlingHttpServletRequest request) {
    return request.adaptTo(XSSAPI.class);
  }

}
