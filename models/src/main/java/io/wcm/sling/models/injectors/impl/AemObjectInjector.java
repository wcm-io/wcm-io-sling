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
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.xss.XSSAPI;
import com.day.cq.i18n.I18n;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.AuthoringUIMode;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.commons.WCMUtils;

import io.wcm.sling.commons.request.RequestContext;
import io.wcm.sling.models.annotations.AemObject;

/**
 * Injects common AEM objects that can be derived from a SlingHttpServletRequest.
 * Documentation see {@link AemObject}.
 */
@Component(service = { Injector.class, StaticInjectAnnotationProcessorFactory.class }, property = {
    /*
     * SERVICE_RANKING of this service should be lower than the ranking of the OsgiServiceInjector (5000),
     * otherwise the generic XSSAPI service would be injected from the OSGi Service Registry instead of the
     * pre-configured from the current request.
     * Additionally it should be lower than the ACS commons AemObjectInjector (4500).
     */
    Constants.SERVICE_RANKING + ":Integer=" + 4400
})
@SuppressWarnings("deprecation")
public final class AemObjectInjector implements Injector, StaticInjectAnnotationProcessorFactory, AcceptsNullName {

  /**
   * Injector name
   */
  public static final @NotNull String NAME = "wcm-io-aem-object";

  static final String RESOURCE_PAGE = "resourcePage";
  static final String USER_I18N = "userI18n";

  @Reference
  private RequestContext requestContext;
  @Reference
  private ModelsImplConfiguration modelsImplConfiguration;

  @Override
  public @NotNull String getName() {
    return NAME;
  }

  @Override
  public Object getValue(@NotNull final Object adaptable, final String name, @NotNull final Type type,
      @NotNull final AnnotatedElement element, @NotNull final DisposalCallbackRegistry callbackRegistry) {

    // only class types are supported
    if (!(type instanceof Class<?>)) {
      return null;
    }
    Class<?> requestedClass = (Class<?>)type;

    SlingHttpServletRequest request = getRequest(adaptable);
    if (request != null) {
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
    else if (requestedClass.equals(TagManager.class)) {
      return getTagManager(adaptable);
    }
    else if (requestedClass.equals(WorkflowSession.class)) {
      return getWorkflowSession(adaptable);
    }

    return null;
  }

  private @Nullable SlingHttpServletRequest getRequest(@NotNull final Object adaptable) {
    if (adaptable instanceof SlingHttpServletRequest) {
      return (SlingHttpServletRequest)adaptable;
    }
    else if (modelsImplConfiguration.isRequestThreadLocal()) {
      return requestContext.getThreadRequest();
    }
    else {
      return null;
    }
  }

  @SuppressWarnings("null")
  private @Nullable ResourceResolver getResourceResolver(@NotNull final Object adaptable) {
    if (adaptable instanceof ResourceResolver) {
      return (ResourceResolver)adaptable;
    }
    if (adaptable instanceof Resource) {
      return ((Resource)adaptable).getResourceResolver();
    }
    if (adaptable instanceof Page) {
      return ((Page)adaptable).adaptTo(Resource.class).getResourceResolver();
    }
    SlingHttpServletRequest request = getRequest(adaptable);
    if (request != null) {
      return request.getResourceResolver();
    }
    return null;
  }

  private @Nullable Resource getResource(@NotNull final Object adaptable) {
    if (adaptable instanceof Resource) {
      return (Resource)adaptable;
    }
    if (adaptable instanceof Page) {
      return ((Page)adaptable).adaptTo(Resource.class);
    }
    SlingHttpServletRequest request = getRequest(adaptable);
    if (request != null) {
      return request.getResource();
    }
    return null;
  }

  private @Nullable PageManager getPageManager(@NotNull final Object adaptable) {
    ResourceResolver resolver = getResourceResolver(adaptable);
    if (resolver != null) {
      return resolver.adaptTo(PageManager.class);
    }
    return null;
  }

  private @Nullable Designer getDesigner(@NotNull final Object adaptable) {
    ResourceResolver resolver = getResourceResolver(adaptable);
    if (resolver != null) {
      return resolver.adaptTo(Designer.class);
    }
    return null;
  }

  private @Nullable Page getCurrentPage(@NotNull final Object adaptable) {
    SlingHttpServletRequest request = getRequest(adaptable);
    if (request != null) {
      ComponentContext context = getComponentContext(request);
      if (context != null) {
        return context.getPage();
      }
    }
    return getResourcePage(adaptable);
  }

  private @Nullable Page getResourcePage(@NotNull final Object adaptable) {
    PageManager pageManager = getPageManager(adaptable);
    Resource resource = getResource(adaptable);
    if (pageManager != null && resource != null) {
      return pageManager.getContainingPage(resource);
    }
    return null;
  }

  private @NotNull WCMMode getWcmMode(@NotNull final SlingHttpServletRequest request) {
    return WCMMode.fromRequest(request);
  }

  private @NotNull AuthoringUIMode getAuthoringUiMode(@NotNull final SlingHttpServletRequest request) {
    AuthoringUIMode mode = AuthoringUIMode.fromRequest(request);
    if (mode == null) {
      // if no mode is set (e.g. if WCMMode is disabled) default to Touch UI
      mode = AuthoringUIMode.TOUCH;
    }
    return mode;
  }

  private @Nullable ComponentContext getComponentContext(@NotNull final SlingHttpServletRequest request) {
    return WCMUtils.getComponentContext(request);
  }

  private @Nullable Design getCurrentDesign(final Object adaptable) {
    Page currentPage = getCurrentPage(adaptable);
    Designer designer = getDesigner(adaptable);
    if (currentPage != null && designer != null) {
      return designer.getDesign(currentPage);
    }
    return null;
  }

  private @Nullable Style getStyle(@NotNull final SlingHttpServletRequest request) {
    Style style = null;
    // first try to get from sling bindings
    SlingBindings slingBindings = getSlingBindings(request);
    if (slingBindings != null) {
      style = (Style)slingBindings.get(WCMBindings.CURRENT_STYLE);
    }
    if (style == null) {
      // fallback to current design
      Design currentDesign = getCurrentDesign(request);
      ComponentContext componentContext = getComponentContext(request);
      if (currentDesign != null && componentContext != null) {
        style = currentDesign.getStyle(componentContext.getCell());
      }
    }
    return style;
  }

  private @Nullable XSSAPI getXssApi(@NotNull final SlingHttpServletRequest request) {
    return request.adaptTo(XSSAPI.class);
  }

  private @Nullable I18n getResourceI18n(@NotNull final SlingHttpServletRequest request) {
    Page currentPage = getCurrentPage(request);
    if (currentPage != null) {
      Locale currentLocale = currentPage.getLanguage(false);
      return new I18n(getI18nEnabledRequest(request).getResourceBundle(currentLocale));
    }
    return null;
  }

  private @NotNull I18n getUserI18n(@NotNull final SlingHttpServletRequest request) {
    return new I18n(getI18nEnabledRequest(request));
  }

  private @Nullable TagManager getTagManager(@NotNull final Object adaptable) {
    ResourceResolver resolver = getResourceResolver(adaptable);
    if (resolver != null) {
      return resolver.adaptTo(TagManager.class);
    }
    return null;
  }

  private @Nullable WorkflowSession getWorkflowSession(@NotNull final Object adaptable) {
    ResourceResolver resolver = getResourceResolver(adaptable);
    if (resolver != null) {
      return resolver.adaptTo(WorkflowSession.class);
    }
    return null;
  }

  /**
   * Returns a sling request that has a resource bundle set. Due to several wrappings inside Sling
   * this is not always the request that is available in the script or java code initiating the injection.
   * If a SlingBindings object is available the request from this is returned.
   * If not it is checked if the current request that was recorded in a ThreadLocal can be used.
   * As a last resort a fallback to the request that was used for the adaption is returned, but this
   * is likely to not be i18n-enabled.
   * @param request Original request
   * @return Request from sling bindings
   */
  private @NotNull SlingHttpServletRequest getI18nEnabledRequest(@NotNull SlingHttpServletRequest request) {
    SlingBindings bindings = getSlingBindings(request);
    if (bindings != null) {
      SlingHttpServletRequest bindingsRequest = bindings.getRequest();
      if (bindingsRequest != null) {
        return bindingsRequest;
      }
    }
    if (modelsImplConfiguration.isRequestThreadLocal()) {
      SlingHttpServletRequest threadLocalRequest = requestContext.getThreadRequest();
      if (threadLocalRequest != null) {
        return threadLocalRequest;
      }
    }
    return request;
  }

  private @Nullable SlingBindings getSlingBindings(@NotNull SlingHttpServletRequest request) {
    return (SlingBindings)request.getAttribute(SlingBindings.class.getName());
  }

  @SuppressWarnings({ "null", "unused" })
  @Override
  public InjectAnnotationProcessor2 createAnnotationProcessor(final AnnotatedElement element) {
    // check if the element has the expected annotation
    AemObject annotation = element.getAnnotation(AemObject.class);
    if (annotation != null) {
      return new AemObjectAnnotationProcessor(annotation);
    }
    return null;
  }

  private static class AemObjectAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

    private final AemObject annotation;

    AemObjectAnnotationProcessor(final AemObject annotation) {
      this.annotation = annotation;
    }

    @Override
    public InjectionStrategy getInjectionStrategy() {
      return annotation.injectionStrategy();
    }

    @Override
    public Boolean isOptional() {
      return annotation.optional();
    }
  }

}
