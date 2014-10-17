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

import io.wcm.sling.models.annotations.AemObject;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory;
import org.osgi.framework.Constants;

import com.adobe.granite.xss.XSSAPI;
import com.day.cq.i18n.I18n;
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
 * Documentation see {@link AemObject}.
 */
@Component
@Service
/*
 * SERVICE_RANKING of this service should be lower than the ranking of the OsgiServiceInjector (5000),
 * otherwise the generic XSSAPI service would be injected from the OSGi Service Registry instead of the
 * pre-configured from the current request.
 */
@Property(name = Constants.SERVICE_RANKING, intValue = 4500)
public final class AemObjectInjector implements Injector, InjectAnnotationProcessorFactory, AcceptsNullName {

  /**
   * Injector name
   */
  public static final String NAME = "wcm-io-aem-object";

  static final String RESOURCE_PAGE = "resourcePage";
  static final String USER_I18N = "userI18n";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object getValue(final Object adaptable, final String name, final Type type, final AnnotatedElement element,
      final DisposalCallbackRegistry callbackRegistry) {

    // only class types are supported
    if (!(type instanceof Class<?>)) {
      return null;
    }
    Class<?> requestedClass = (Class<?>)type;

    if (adaptable instanceof SlingHttpServletRequest) {
      SlingHttpServletRequest request = (SlingHttpServletRequest)adaptable;
      if (requestedClass.equals(WCMMode.class)) {
        return getWcmMode(request);
      }
      if (requestedClass.equals(AuthoringUIMode.class)) {
        return getAuthoringUiMode(request);
      }
      if (requestedClass.equals(ComponentContext.class)) {
        return getComponentContext(request);
      }
      if (requestedClass.equals(Style.class)) {
        return getStyle(request);
      }
      if (requestedClass.equals(XSSAPI.class)) {
        return getXssApi(request);
      }
      if (requestedClass.equals(I18n.class)) {
        if (StringUtils.equals(name, USER_I18N)) {
          return getUserI18n(request);
        }
        else {
          return getResourceI18n(request);
        }
      }
    }

    if (requestedClass.equals(PageManager.class)) {
      return getPageManager(adaptable);
    }
    else if (requestedClass.equals(Page.class)) {
      if (StringUtils.equals(name, RESOURCE_PAGE)) {
        return getResourcePage(adaptable);
      }
      else {
        return getCurrentPage(adaptable);
      }
    }
    else if (requestedClass.equals(Designer.class)) {
      return getDesigner(adaptable);
    }
    else if (requestedClass.equals(Design.class)) {
      return getCurrentDesign(adaptable);
    }

    return null;
  }

  private ResourceResolver getResourceResolver(final Object adaptable) {
    if (adaptable instanceof SlingHttpServletRequest) {
      return ((SlingHttpServletRequest)adaptable).getResourceResolver();
    }
    if (adaptable instanceof ResourceResolver) {
      return (ResourceResolver)adaptable;
    }
    if (adaptable instanceof Resource) {
      return ((Resource)adaptable).getResourceResolver();
    }
    return null;
  }

  private Resource getResource(final Object adaptable) {
    if (adaptable instanceof SlingHttpServletRequest) {
      return ((SlingHttpServletRequest)adaptable).getResource();
    }
    if (adaptable instanceof Resource) {
      return (Resource)adaptable;
    }
    return null;
  }

  private PageManager getPageManager(final Object adaptable) {
    ResourceResolver resolver = getResourceResolver(adaptable);
    if (resolver != null) {
      return resolver.adaptTo(PageManager.class);
    }
    return null;
  }

  private Designer getDesigner(final Object adaptable) {
    ResourceResolver resolver = getResourceResolver(adaptable);
    if (resolver != null) {
      return resolver.adaptTo(Designer.class);
    }
    return null;
  }

  private Page getCurrentPage(final Object adaptable) {
    if (adaptable instanceof SlingHttpServletRequest) {
      ComponentContext context = getComponentContext((SlingHttpServletRequest)adaptable);
      if (context != null) {
        return context.getPage();
      }
    }
    return getResourcePage(adaptable);
  }

  private Page getResourcePage(final Object adaptable) {
    PageManager pageManager = getPageManager(adaptable);
    Resource resource = getResource(adaptable);
    if (pageManager != null && resource != null) {
      return pageManager.getContainingPage(resource);
    }
    return null;
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

  private Design getCurrentDesign(final Object adaptable) {
    Page currentPage = getCurrentPage(adaptable);
    Designer designer = getDesigner(adaptable);
    if (currentPage != null && designer != null) {
      return designer.getDesign(currentPage);
    }
    return null;
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

  private I18n getResourceI18n(final SlingHttpServletRequest request) {
    Page currentPage = getCurrentPage(request);
    if (currentPage != null) {
      Locale currentLocale = currentPage.getLanguage(false);
      return new I18n(getRequestFromSlingBindings(request).getResourceBundle(currentLocale));
    }
    return null;
  }

  private I18n getUserI18n(final SlingHttpServletRequest request) {
    return new I18n(getRequestFromSlingBindings(request));
  }

  /**
   * Returns the {@link SlingHttpServletRequest} from SlingBindings. This is a request wrapped
   * in another way than passed to this sling model object, any only with this resource bundle
   * resolution against JCR-based resource bundles is working.
   * @param request Original request
   * @return Request from sling bindings
   */
  private SlingHttpServletRequest getRequestFromSlingBindings(SlingHttpServletRequest request) {
    SlingBindings bindings = (SlingBindings)request.getAttribute(SlingBindings.class.getName());
    if (bindings != null) {
      return bindings.getRequest();
    }
    else {
      return request;
    }
  }

  @Override
  public InjectAnnotationProcessor createAnnotationProcessor(final Object adaptable, final AnnotatedElement element) {
    // check if the element has the expected annotation
    AemObject annotation = element.getAnnotation(AemObject.class);
    if (annotation != null) {
      return new AemObjectAnnotationProcessor(annotation);
    }
    return null;
  }

  private static class AemObjectAnnotationProcessor extends AbstractInjectAnnotationProcessor {

    private final AemObject annotation;

    public AemObjectAnnotationProcessor(final AemObject annotation) {
      this.annotation = annotation;
    }

    @Override
    public Boolean isOptional() {
      return this.annotation.optional();
    }
  }

}
